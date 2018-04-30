/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.dung;

import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.arg.dung.semantics.*;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.BeliefBaseReasoner;

/**
 * This class implements a stratified labeling reasoner.
 * @author Matthias Thimm
 */
public class StratifiedLabelingReasoner implements BeliefBaseReasoner<DungTheory> {

	/**
	 * The type of inference for this reasoner, either sceptical or
	 * credulous.
	 */
	private int inferenceType;
	
	/** The semantics used for this reasoner. */
	private Semantics semantics;
	
	/**
	 * Creates a new reasoner for the given Dung theory, semantics, and inference type.
	 * @param beliefBase a Dung theory
	 * @param semantics a semantics
	 * @param inferenceType and inference type
	 */
	public StratifiedLabelingReasoner(Semantics semantics, int inferenceType) {
		if(inferenceType != Semantics.CREDULOUS_INFERENCE && inferenceType != Semantics.SCEPTICAL_INFERENCE)
			throw new IllegalArgumentException("Inference type must be either sceptical or credulous.");
		this.inferenceType = inferenceType;
		this.semantics = semantics;
	}

	/**
	 * Creates a new reasoner using sceptical inference and grounded semantics.
	 * @param beliefBase The knowledge base for this reasoner.
	 */
	public StratifiedLabelingReasoner(){
		this(Semantics.GROUNDED_SEMANTICS, Semantics.SCEPTICAL_INFERENCE);		
	}	
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.BeliefBaseReasoner#query(net.sf.tweety.commons.BeliefBase, net.sf.tweety.commons.Formula)
	 */
	@Override
	public Answer query(DungTheory theory, Formula query) {
		if(!(query instanceof Argument))
			throw new IllegalArgumentException("Formula of class argument expected");
		Argument arg = (Argument) query;
		if(this.inferenceType == Semantics.SCEPTICAL_INFERENCE){
			Answer answer = new Answer(theory,arg);
			for(StratifiedLabeling e: this.getLabelings(theory)){
				if(!e.satisfies(arg)){
					answer.setAnswer(false);
					answer.appendText("The answer is: false");
					return answer;
				}
			}			
			answer.setAnswer(true);
			answer.appendText("The answer is: true");
			return answer;
		}
		// so its credulous semantics
		Answer answer = new Answer(theory,arg);
		for(StratifiedLabeling e: this.getLabelings(theory)){
			if(e.satisfies(arg)){
				answer.setAnswer(true);
				answer.appendText("The answer is: true");
				return answer;
			}
		}			
		answer.setAnswer(false);
		answer.appendText("The answer is: false");
		return answer;
	}
	
	/**
	 * Returns the inference type of this reasoner.
	 * @return the inference type of this reasoner.
	 */
	public int getInferenceType(){
		return this.inferenceType;
	}
	
	/**
	 * Computes the labelings.
	 * @return A set of labelings.
	 */
	public Set<StratifiedLabeling> getLabelings(DungTheory theory){
		Set<StratifiedLabeling> labelings = new HashSet<StratifiedLabeling>();
		AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getReasonerForSemantics(this.semantics, Semantics.CREDULOUS_INFERENCE);
		Set<Extension> extensions = reasoner.getExtensions(theory);
		for(Extension extension: extensions){
			StratifiedLabeling labeling = new StratifiedLabeling();
			if(extension.isEmpty()){
				for(Argument arg: theory)
					labeling.put(arg, Integer.MAX_VALUE);
				labelings.add(labeling);
			}else{
				for(Argument arg: extension)
					labeling.put(arg, 0);
				Extension remainingArguments = new Extension(theory);
				remainingArguments.removeAll(extension);
				DungTheory remainingTheory = new DungTheory(theory.getRestriction(remainingArguments));
				StratifiedLabelingReasoner sReasoner = new StratifiedLabelingReasoner(this.semantics, this.inferenceType);
				for(StratifiedLabeling labeling2: sReasoner.getLabelings(remainingTheory)){
					for(Argument arg: labeling2.keySet())
						labeling2.put(arg, labeling2.get(arg) + 1);
					labeling2.putAll(labeling);
					labelings.add(labeling2);
				}
			}
		}		
		return labelings;
	}
}
