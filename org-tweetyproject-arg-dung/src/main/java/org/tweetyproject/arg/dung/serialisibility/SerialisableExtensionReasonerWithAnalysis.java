/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.serialisibility;

import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisableExtensionReasoner;
import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisedAdmissibleReasoner;
import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisedCompleteReasoner;
import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisedGroundedReasoner;
import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisedPreferredReasoner;
import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisedStableReasoner;
import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisedUnchallengedReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisibility.plotter.TransitionStateNode;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.syntax.TransitionState;
import org.tweetyproject.graphs.SimpleGraph;

/**
 * {@inheritDoc}
 *
 * This specialization of the reasoner gives access to all interim results of
 * the computation.
 *
 * @see Matthias Thimm. Revisiting initial sets in abstract argumentation.
 *      Argument & Computation 13 (2022) 325–360 DOI 10.3233/AAC-210018
 * @see Lars Bengel and Matthias Thimm. Serialisable Semantics for Abstract
 *      Argumentation. Computational Models of Argument (2022) DOI:
 *      10.3233/FAIA220143
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public abstract class SerialisableExtensionReasonerWithAnalysis extends SerialisableExtensionReasoner {

	/**
	 * Creates a reasoner for the given semantic.
	 * @param semantics Semantics, according to which the reasoner shall derive the extensions
	 * @return a reasoner for the given Dung theory, inference type, and semantics
	 */
	public static SerialisableExtensionReasonerWithAnalysis getSerialisableReasonerForSemantics(Semantics semantics){
		return switch (semantics) {
		case CO -> new SerialisedCompleteReasoner();
		case GR -> new SerialisedGroundedReasoner();
		case PR -> new SerialisedPreferredReasoner();
		case ST -> new SerialisedStableReasoner();
		case ADM -> new SerialisedAdmissibleReasoner();
		case UC -> new SerialisedUnchallengedReasoner();
		default -> throw new IllegalArgumentException("Unknown semantics.");
		};
	}

	protected Semantics usedSemantics;

	/**
	 * @param usedSemantics Semantics used to generate the extensions found during the examination.
	 */
	public SerialisableExtensionReasonerWithAnalysis(Semantics usedSemantics) {
		super();
		this.usedSemantics = usedSemantics;
	}

	/**
	 * Examines the specified framework and computes all extensions, according to this object's semantic.
	 *
	 * @param framework Argumentation framework, for which the extensions shall be computed.
	 * @return Analysis of the framework, containing all found extensions and further information e.g. derivation graph.
	 */
	public TransitionStateAnalysis getModelsWithAnalysis(DungTheory framework) {

		Extension<DungTheory> initExtension = new Extension<>();
		TransitionState initState = new TransitionState(framework, initExtension);

		return this.getModelsRecursiveWithAnalysis(
				new HashSet<TransitionState>(),
				new HashSet<TransitionStateAnalysis>(),
				initState);
	}

	/**
	 * @return Semantics used to generate the extensions found during the examination.
	 */
	public Semantics getSemantic() {
		return this.usedSemantics;
	}

	/**
	 * Examines recursively the specified state and all states that can be reducted from this one,
	 * until the termination function is satisfied.
	 *
	 * @param consistencyCheckSet Set of all nodes, visited during the process
	 * @param state Current transition state of the serialising process.
	 * @return Analysis of the current state.
	 */
	@SuppressWarnings("unchecked")
	private TransitionStateAnalysis getModelsRecursiveWithAnalysis(
			HashSet<TransitionState> consistencyCheckSet,
			HashSet<TransitionStateAnalysis> computedAnalyses,
			TransitionState state) {

		SimpleGraph<TransitionStateNode> currentGraph = new SimpleGraph<>();
		TransitionStateNode root;
		HashSet<Extension<DungTheory>> foundExtensions = new HashSet<>();
		HashSet<TransitionStateAnalysis> subAnalyses = new HashSet<>();

		// if this state (and all of subordinates) has already been examined abort function
		if(consistencyCheckSet.contains(state)) {
			return TransitionStateAnalysis.getAnalysisByState(computedAnalyses, state); //return NULL, if state was already visited, but analysis was not yet computed
		}

		root = new TransitionStateNode(state);
		currentGraph.add(root);
		consistencyCheckSet.add(state);

		// check whether a construction of an extension is finished
		if (this.checkTerminationFunction(state)) {
			// found final extension
			foundExtensions.add(state.getExtension());
		}

		Collection<Extension<DungTheory>> newExtensions = this.selectInitialSetsForReduction(state);

		// [TERMINATION CONDITION] - terminates if newExtensions empty -  iterate depth-first through all reductions
		for (Extension<DungTheory> newExt : newExtensions) {
			TransitionState newState = state.transitToNewState(newExt);

			// [RECURSIVE CALL] examine reduced framework
			TransitionStateAnalysis subAnalysis = this.getModelsRecursiveWithAnalysis(consistencyCheckSet, computedAnalyses, newState);

			// integrate findings of sub-level in this level's analysis
			subAnalyses.add(subAnalysis);
			foundExtensions.addAll(subAnalysis.getExtensions());
			SimpleGraph<TransitionStateNode> subGraph = subAnalysis.getGraph();
			TransitionStateNode subRoot = subAnalysis.getRoot();
			currentGraph = currentGraph.addSubGraph(currentGraph, root, subGraph, subRoot, newExt.toString());
		}

		TransitionStateAnalysis currentAnalysis = new TransitionStateAnalysis(
				state.getTheory(), state.getExtension(), this.usedSemantics, currentGraph, root, foundExtensions, subAnalyses);
		computedAnalyses.add(currentAnalysis);
		return currentAnalysis;
	}


}
