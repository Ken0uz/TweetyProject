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
package org.tweetyproject.arg.dung.serialisibility.syntax;

import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.DefaultGraph;
import org.tweetyproject.graphs.Edge;
import org.tweetyproject.graphs.SimpleGraph;


/**
 * This class represents a graph representing the different {@link SerialisationSequence SerialisationSequences} wrt. some {@link Semantics} for a given {@link DungTheory Argumentation Framework}.
 *
 * @see SimpleGraph
 * Reference: Matthias Thimm. Revisiting initial sets in abstract argumentation. Argument and Computation 13 (2022) 325–360 DOI 10.3233/AAC-210018
 * Reference: Lars Bengel and Matthias Thimm. Serialisable Semantics for Abstract Argumentation. Computational Models of Argument (2022) DOI: 10.3233/FAIA220143
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 */
public class SerialisationGraph extends DefaultGraph<Extension<DungTheory>> {
	/** The set of arguments at the root of the graph, typically the empty set */
	private Extension<DungTheory> root;

	/** Extensions found during the serialisation process represented by this graph */
	private Collection<Extension<DungTheory>> extensions;

	/** Semantics used to generate the graph */
	private Semantics semantics;

	/**
	 * Creates a graph containing all transition states during the generation process of the serialisable extensions.
	 *
	 * @param root Extension with whom the processing of the examined framework started
	 * @param semantics Semantics used for the serialisation process
	 * @param extensions Extensions generated by the serialisation process, associated with this graph
	 */
	public SerialisationGraph(Extension<DungTheory> root, Semantics semantics, Collection<Extension<DungTheory>> extensions) {
		this.init(root, semantics, extensions);
	}

	/**
	 * Creates a graph containing all transition states during the generation process of the serialisable extensions.
	 *
	 * @param semantics Semantics used for the serialisation process.
	 * @param extensions Extensions generated by the serialisation process, associated with this graph.
	 */
	public SerialisationGraph(Semantics semantics, Collection<Extension<DungTheory>> extensions) {
		this.init(semantics, extensions);
	}

	/**
	 * Creates a graph containing all transition states during the generation process of the serialisable extensions.
	 *
	 * @param graph Graph, showing a serialisation process.
	 * @param root Extension with whom the processing of the examined framework started
	 * @param semantics Semantics used for the serialisation process.
	 * @param extensions Extensions generated by the serialisation process, associated with this graph.
	 */
	public SerialisationGraph(SerialisationGraph graph, Extension<DungTheory> root, Semantics semantics, Collection<Extension<DungTheory>> extensions) {
		this.init(graph);
		this.init(root, semantics, extensions);
	}

	@Override
	public boolean add(Extension<DungTheory> node) {
		if(this.root == null) {
			this.root = node;
		}
		return this.nodes.add(node);
	}
	
	/**
	 * Method to add all specified nodes to the graph
	 * @param nodes Nodes to add to the graph
	 * @return TRUE iff nodes have been successfully added to the graph
	 */
	public boolean addAll(HashSet<Extension<DungTheory>> nodes) {
		boolean result = true;
		for(Extension<DungTheory> t: nodes){
			if(this.root == null) {
				this.root = t;
			}
			boolean sub = this.add(t);
			result = result && sub;
		}
		return result;
	}

	/**
	 * Method to add all specified edges to the graph
	 * @param edges Edges to add to the graph
	 * @return TRUE iff the edges have been added successfully to the graph
	 */
	public boolean addAllEdges(HashSet<Edge<Extension<DungTheory>>> edges) {
		boolean result = false;
		for(Edge<Extension<DungTheory>> e: edges) {
			result |= this.add(e);
		}
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof SerialisationGraph)) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		SerialisationGraph other = (SerialisationGraph) obj;

		return  this.root.equals(other.root) &&
				super.equals(other);
	}

	/**
	 * @return The set of extensions that have been found during the process shown by this graph.
	 */
	public Collection<Extension<DungTheory>> getExtensions(){
		return new HashSet<>(this.extensions);
	}

	/**
	 * @return All leaf-nodes of this graph.
	 */
	public Collection<Extension<DungTheory>> getLeaves() {
		return this.getLeavesRecursive(this.root);
	}

	/**
	 * Method to gain access to a node, who is equal to the one specified
	 * @param searchedNode Node, to whom the returned node has to be equal
	 * @return Node, which is part of this graph and which is equal to the specified node
	 * @throws IllegalArgumentException Thrown if there is no node in this graph equal to the one specified
	 */
	public Extension<DungTheory> getNode(Extension<DungTheory> searchedNode) throws IllegalArgumentException {
		for (Extension<DungTheory> node : this.nodes) {
			if(node.equals(searchedNode)) {
				return node;
			}
		}
		throw new IllegalArgumentException();
	}

	/**
	 * @return Extension with whom the processing of the examined framework started
	 */
	public Extension<DungTheory> getRoot() {
		return this.root;
	}


	/**
	 * @return Semantics used for the serialisation process
	 */
	public Semantics getSemantics() {
		return this.semantics;
	}

	/**
	 * Returns all sequences of sets of arguments from the root to the specified node
	 * @param node Destination of the path
	 * @return Sequences of sets, used to construct the extension, represented by the node
	 */
	public Collection<SerialisationSequence> getSerialisationSequences(Extension<DungTheory> node) {
		if(!this.getNodes().contains(node)) {
			throw new IllegalArgumentException("node is not part of this graph");
		}
		// we perform a BFS.
		return this.getSequencesBFSRecursive(node, new SerialisationSequence(this.root));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		String printedResult = "Extensions: " + this.getExtensions().toString() + "\n"
				+ "Root: " + this.root.toString() + "\n"
				+ "Graph: " + super.toString();
		return printedResult;
	}

	/**
	 * Sets the root of this graph.
	 * @param root Extension with whom the processing of the examined framework started
	 */
	protected void initRoot(Extension<DungTheory> root) {
		if(root == null) {
			throw new NullPointerException("root");
		}
		if(this.root != null) {
			throw new IllegalArgumentException("Root of Graph was already set and can only be set once.");
		}
		this.root = root;
		this.add(root);
	}

	private HashSet<Extension<DungTheory>> getLeavesRecursive(Extension<DungTheory> node){
		var output = new HashSet<Extension<DungTheory>>();
		Collection<Extension<DungTheory>> children = this.getChildren(node);

		if(children.isEmpty()) {
			output.add(node); // leaf found
		}
		else {
			for (Extension<DungTheory> child : children) {
				output.addAll(this.getLeavesRecursive(child));
			}
		}

		return output;
	}



	private Collection<SerialisationSequence> getSequencesBFSRecursive(
			Extension<DungTheory> targetNode,
			SerialisationSequence currentSequence) {

		var output = new HashSet<SerialisationSequence>();
		var currentNode = this.getNode(currentSequence.getExtension());

		//[TERMINATION CONDITION]
		if(currentNode.equals(targetNode)) {
			output.add(currentSequence);
			return output;
		}

		for (Extension<DungTheory> child : this.getChildren(currentNode)) {

			var childSequence = new SerialisationSequence(currentSequence);
			childSequence.add(this.subtract(child, currentNode));

			//[RECURSIVE CALL]
			output.addAll(this.getSequencesBFSRecursive(targetNode, childSequence));
		}


		return output;
	}

	private void init(Extension<DungTheory> root, Semantics semanticsUsed, Collection<Extension<DungTheory>> extensionsFound) {
		this.initRoot(root);
		this.init(semanticsUsed, extensionsFound);
	}

	private void init(Semantics semanticsUsed, Collection<Extension<DungTheory>> extensionsFound) {
		this.semantics = semanticsUsed;
		this.extensions = extensionsFound;
	}

	private void init(SerialisationGraph graph) {
		this.addAll(new HashSet<>(graph.getNodes()));
		this.addAllEdges(new HashSet<>(graph.getEdges()));
	}

	/**
	 * @param superSet Node of the graph.
	 * @param subSet Node of the graph.
	 * @return All elements of nodeA, which are not contained in nodeB.
	 */
	private Extension<DungTheory> subtract(Extension<DungTheory> superSet, Extension<DungTheory> subSet){
		var output = new Extension<DungTheory>();
		output.addAll(superSet);
		output.removeAll(subSet);
		return output;
	}
}
