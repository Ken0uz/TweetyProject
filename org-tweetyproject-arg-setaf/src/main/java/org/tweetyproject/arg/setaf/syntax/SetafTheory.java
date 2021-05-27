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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.setaf.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.tweetyproject.arg.dung.reasoner.SimpleGroundedReasoner;
import org.tweetyproject.arg.dung.reasoner.SimplePreferredReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleStableReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungSignature;
import org.tweetyproject.arg.setaf.semantics.SetafExtension;
import org.tweetyproject.commons.BeliefSet;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.graphs.DefaultGraph;
import org.tweetyproject.graphs.Edge;
import org.tweetyproject.graphs.Graph;
import org.tweetyproject.graphs.Node;
import org.tweetyproject.math.matrix.Matrix;
import org.tweetyproject.math.term.IntegerConstant;


/**
 * This class implements an abstract argumentation theory in the sense of Dung.
 * <br>
 * <br>See
 * <br>
 * <br>Phan Minh Dung. On the Acceptability of Arguments and its Fundamental Role in Nonmonotonic Reasoning, Logic Programming and n-Person Games.
 * In Artificial Intelligence, Volume 77(2):321-358. 1995
 *
 *
 * @author Matthias Thimm, Tjitze Rienstra
 *
 */
public class SetafTheory extends BeliefSet<Argument,SetafSignature> implements Graph<Argument>, Comparable<SetafTheory> {

	/**
	 * For archiving sub graphs 
	 */
	private static Map<SetafTheory, Collection<Graph<Argument>>> archivedSubgraphs = new HashMap<SetafTheory, Collection<Graph<Argument>>>();

	/**
	 * explicit listing of direct attackers and attackees (for efficiency reasons) 
	 */
	private Map<Set<Argument>,Set<Argument>> parents = new HashMap<Set<Argument>,Set<Argument>>();
	private Map<Argument,Set<Argument>> children= new HashMap<Argument,Set<Argument>>();
	
	/**
	 * Default constructor; initializes empty sets of arguments and attacks
	 */
	public SetafTheory(){
		super();
	}
	
	/**
	 * Creates a new theory from the given graph.
	 * @param graph some graph
	 */
	public SetafTheory(Graph<Argument> graph){
		super(graph.getNodes());
		for(Edge<? extends Argument> e: graph.getEdges()) {
			if(!parents.containsKey(e.getNodeB()))
				parents.put((Set<Argument>) e.getNodeB(), new HashSet<Argument>());
			parents.get(e.getNodeB()).add(e.getNodeA());
			if(!children.containsKey(e.getNodeA()))
				children.put(e.getNodeA(), new HashSet<Argument>());
			children.get(e.getNodeA()).add(e.getNodeB());
		}		
	}
	
	public SetafTheory clone() {
		SetafTheory result = new SetafTheory(this);
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.kr.BeliefBase#getSignature()
	 */
	public Signature getMinimalSignature(){
		return new SetafSignature(this);
	}

	/**
	 * returns true if <code>arguments</code> attack all other arguments in the theory
	 * @param ext An extension contains a set of arguments.
	 * @return true if <code>arguments</code> attack all other arguments in the theory
	 */
	public boolean isAttackingAllOtherArguments(SetafExtension ext){
		for(Argument a: this) {
			if(ext.contains(a))
				continue;
			if(!this.isAttacked(a, ext))
				return false;
		}		
		return true;
	}

	/**
	 * returns true iff the theory is well-founded, i.e., there is no infinite sequence A1,A2,... of arguments with Ai attacking Ai+1
	 * @return true iff the theory is well-founded
	 */
	public boolean isWellFounded(){
		List<Argument> arguments = new ArrayList<Argument>();		
		for(Formula f: this)
			arguments.add((Argument) f);
		boolean[] dfn = new boolean[arguments.size()];
		boolean[] inProgress = new boolean[arguments.size()];
		for(int i = 0; i < arguments.size(); i++){
			dfn[i] = false;
			inProgress[i] = false;			
		}
		for(int i = 0; i < arguments.size(); i++)
			if(!dfn[i])
				if(dfs(i,arguments,dfn,inProgress))
					return false;		
		return true;
	}

	/**
	 * Depth-First-Search to find a cycle in the theory. Auxiliary method to determine if the theory is well-founded
	 * @param i current node
	 * @param arguments list of all nodes (arguments)
	 * @param dfn array which keeps track whether a node has been visited
	 * @param inProgress array which keeps track which nodes are currently being processed
	 * @return true iff the theory contains a cycle
	 */
	private boolean dfs(int i, List<Argument> arguments, boolean[] dfn, boolean[] inProgress){
		dfn[i] = true;
		inProgress[i] = true;
		Set<Set<Argument>> attackers = getAttackers(arguments.get(i));
		Iterator<Set<Argument>> it = attackers.iterator();
		while(it.hasNext()){
			Argument argument = (Argument) it.next();
			if(inProgress[arguments.indexOf(argument)])
				return true;
			else if(!dfn[arguments.indexOf(argument)])
				if(dfs(arguments.indexOf(argument),arguments,dfn,inProgress))
					return true;
		}
		inProgress[i] = false;
		return false;
	}



	/**
	 * Computes the set {A | (A,argument) in attacks}.
	 * @param argument an argument
	 * @return the set of all arguments that attack <code>argument</code>.
	 */
	public Set<Set<Argument>> getAttackers(Argument argument){
		if(!this.parents.containsKey(argument))
			return new HashSet<Set<Argument>>();
		return new HashSet<Set<Argument>>((Collection<? extends Set<Argument>>) this.parents.get(argument));		
	}
	
	/**
	 * Computes the set {A | (argument,A) in attacks}.
	 * @param node an argument
	 * @return the set of all arguments that are attacked by <code>argument</code>.
	 */
	public Set<Argument> getAttacked(Argument node){
		if(!this.children.containsKey(node))
			return new HashSet<Argument>();
		return new HashSet<Argument>(this.children.get(node));	
	}

	/**
	 * returns true if some argument of <code>ext</code> attacks argument.
	 * @param a an argument
	 * @param setafExtension an extension, ie. a set of arguments
	 * @return true if some argument of <code>ext</code> attacks argument.
	 */
	public boolean isAttacked(Argument a, SetafExtension setafExtension){
		if(!this.parents.containsKey(a))
			return false;
		for(Argument attacker: this.parents.get(a))
			if(setafExtension.contains(attacker))
				return true;
		return false;
	}
	
	/**
	 * returns true if some argument of <code>ext</code> is attacked by argument.
	 * @param arg2 an argument
	 * @param ext an extension, ie. a set of arguments
	 * @return true if some argument of <code>ext</code> is attacked by argument.
	 */
	public boolean isAttackedBy(Set<Argument> arg2, Collection<Argument> ext){
		if(!this.children.containsKey(arg2))
			return false;
		for(Argument attacked: this.children.get(arg2))
			if(ext.contains(attacked))
				return true;
		return false;
	}
	
	/**
	 * returns true if some argument of <code>ext2</code> attacks some argument
	 * in <code>ext1</code>
	 * @param ext1 an extension, ie. a set of arguments
	 * @param ext2 an extension, ie. a set of arguments
	 * @return true if some argument of <code>ext2</code> attacks some argument
	 * in <code>ext1</code>
	 */
	public boolean isAttacked(SetafExtension ext1, SetafExtension ext2){
		for(Argument a: ext1)
			if(this.isAttacked(a, ext2)) return true;
		return false;
	}

	/**
	 * Checks whether the given extension is stable wrt. this theory.
	 * @param e some extension
	 * @return "true" iff the extension is stable.
	 */
	public boolean isStable(SetafExtension e) {
		for(Argument a: this) {
			if(e.contains(a)) { 
				if(this.isAttacked(a, e))
					return false;
			}else {
				if(!this.isAttacked(a, e))
					return false;
			}
		}
		return true;
	}
	
	/**
	 * The characteristic function of an abstract argumentation framework: F_AF(S) = {A|A is acceptable wrt. S}.
	 * @param extension an extension (a set of arguments).
	 * @return an extension (a set of arguments).
	 */
	public SetafExtension faf(SetafExtension extension){
		SetafExtension newExtension = new SetafExtension();
		Iterator<Argument> it = this.iterator();
		while(it.hasNext()){
			Argument argument = it.next();
			if(extension.isAcceptable(argument, this))
				newExtension.add(argument);
		}
		return newExtension;
	}
	
	/**
	 * Checks whether arg1 is attacked by arg2.
	 * @param arg1 an argument.
	 * @param arg2 an argument.
	 * @return "true" if arg1 is attacked by arg2
	 */
	public boolean isAttackedBy(Argument arg1, Argument arg2){
		if(!this.parents.containsKey(arg1))
			return false;
		return this.parents.get(arg1).contains(arg2);
	}
	

	

	

	
	// Misc methods

	
	/** Pretty print of the theory.
	 * @return the pretty print of the theory.
	 */
	public String prettyPrint(){
		String output = new String();
		Iterator<Argument> it = this.iterator();
		while(it.hasNext())
			output += "argument("+it.next().toString()+").\n";
		output += "\n";
		Iterator<? extends Edge<? extends Argument>> it2 = this.getAttacks().iterator();
		while(it2.hasNext())
			output += "attack"+it2.next().toString()+".\n";
		return output;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){		
		return "<" + super.toString() + "," + this.getAttacks() + ">";
	}
	
	/**
	 * Adds the given attack to this dung theory.
	 * @param attack an attack
	 * @return "true" if the set of attacks has been modified.
	 */
	public boolean add(SetafAttack attack){
		return this.addAttack(attack.getAttackers(), attack.getAttacked()); 
	}
	
	/**
	 * Adds the given attacks to this dung theory.
	 * @param attacks some attacks
	 * @return "true" if the set of attacks has been modified.
	 */
	public boolean add(SetafAttack... attacks){
		boolean result = true;
		for (SetafAttack f : attacks) {
			boolean sub = this.add(f);
			result = result && sub;
		}
		return result;
	}
	
	/**
	 * Adds an attack from the first argument to the second to thisDdung theory.
	 * @param hashSet some argument
	 * @param attacked some argument
	 * @return "true" if the set of attacks has been modified.
	 */
	public boolean addAttack(HashSet<Argument> hashSet, Argument attacked){
		boolean result = false;
		if(!parents.containsKey(attacked)) {
			HashSet<Argument> att = new HashSet<Argument>();
			att.add(attacked);
			parents.put(att, new HashSet<Argument>());
		}
		result |= parents.get(attacked).addAll(hashSet);
		if(!children.containsKey(hashSet))
			children.put(attacked, hashSet);
		result |= children.get(hashSet).add(attacked);		
		return result; 
	}
	
	/**
	 * Removes the given attack from this Dung theory.
	 * @param attack an attack
	 * @return "true" if the set of attacks has been modified.
	 */
	public boolean remove(SetafAttack attack){
		boolean result = false;
		if(parents.containsKey(attack.getAttacked()))		
			result |= parents.get(attack.getAttacked()).remove(attack.getAttackers());
		if(children.containsKey(attack.getAttackers()))
			result |= children.get(attack.getAttackers()).remove(attack.getAttacked());
		return result; 
	}
	
	/**
	 * Removes the argument and all its attacks
	 * @param a some argument
	 * @return true if this structure has been changed
	 */
	public boolean remove(Argument a){
		if(this.parents.get(a) != null){
			for(Argument b: this.parents.get(a))
				this.children.get(b).remove(a);
			this.parents.remove(a);
		}
		if(this.children.get(a) != null){
			for(Argument b: this.children.get(a))
				this.parents.get(b).remove(a);
			this.children.remove(a);
		}		
		return super.remove(a);
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.BeliefSet#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> c){
		boolean result = true;
		for(Object a: c)
			if(a instanceof Argument)
				result |= this.remove((Argument)a);
			else if(a instanceof SetafAttack)
				result |= this.remove((SetafAttack)a);
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.kr.BeliefSet#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o){
		if(o instanceof Argument)
			return super.contains(o);
		if(o instanceof SetafAttack)
			return this.containsAttack((SetafAttack)o);
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.kr.BeliefSet#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c){
		for(Object o: c)
			if(!this.contains(o))
				return false;
		return true;
	}
	
	/**
	 * Checks whether this theory contains the given attack.
	 * @param att some attack
	 * @return "true" iff this theory contains the given attack.
	 */
	public boolean containsAttack(SetafAttack att) {
		if(this.parents.get(att.getAttacked()) == null)
			return false;
		return this.parents.get(att.getAttacked()).contains(att.getAttackers());
	}		
	
	/**
	 * Adds the set of attacks to this Dung theory.
	 * @param collection a collection of attacks
	 * @return "true" if this Dung theory has been modified.
	 */
	public boolean addAllAttacks(Collection<SetafAttack> collection){
		boolean result = false;
		for(SetafAttack att: collection)
			result |= this.add(att);
		return result;
	}

	/**
	 * Adds all arguments and attacks of the given theory to
	 * this theory
	 * @param theory some Dung theory
	 * @return "true" if this Dung Theory has been modified 
	 */
	public boolean add(SetafTheory theory){
		boolean b1 = this.addAll(theory);
		boolean b2 = this.addAllAttacks(theory.getAttacks());
		return b1 || b2 ;		
	}
	
	public boolean add(Argument argument) {
		return super.add(argument);
	}
	
	/**
	 * Returns all attacks of this theory.
	 * @return all attacks of this theory.
	 */
	public Collection<SetafAttack> getAttacks(){
		Set<SetafAttack> attacks = new HashSet<SetafAttack>();
		for(Set<Argument> a: this) {
			if(this.children.containsKey(a)) {
				for(Argument b: this.children.get(a))
					attacks.add(new SetafAttack(a,b));
			}
		}
		return attacks;
	}
	

	


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((this.parents == null) ? 0 : this.parents.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SetafTheory other = (SetafTheory) obj;
		if (this.parents == null) {
			if (other.parents != null)
				return false;
		} else if (!this.parents.equals(other.parents))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.graphs.Graph#add(org.tweetyproject.graphs.Edge)
	 */
	@Override
	public boolean add(Edge<Argument> edge) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.graphs.Graph#getNodes()
	 */
	@Override
	public Collection<Argument> getNodes() {		
		return this;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.graphs.Graph#getNumberOfNodes()
	 */
	@Override
	public int getNumberOfNodes() {
		return this.size();
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.graphs.Graph#areAdjacent(org.tweetyproject.graphs.Node, org.tweetyproject.graphs.Node)
	 */
	@Override
	public boolean areAdjacent(Argument a, Argument b) {
		return this.isAttackedBy(b, a);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.graphs.Graph#getEdges()
	 */
	@Override
	public Collection<? extends Edge<? extends Argument>> getEdges() {
		return this.getAttacks();		
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.graphs.Graph#getChildren(org.tweetyproject.graphs.Node)
	 */
	@Override
	public Collection<Argument> getChildren(Node node) {
		if(!(node instanceof Argument))
			throw new IllegalArgumentException("Node of type argument expected");
		return this.getAttacked((Argument)node);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.graphs.Graph#getParents(org.tweetyproject.graphs.Node)
	 */
	@Override
	public Collection<Argument> getParents(Node node) {
		if(!(node instanceof Argument))
			throw new IllegalArgumentException("Node of type argument expected");
		return this.getAttackers((Argument)node);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.graphs.Graph#existsDirectedPath(org.tweetyproject.graphs.Node, org.tweetyproject.graphs.Node)
	 */
	@Override
	public boolean existsDirectedPath(Argument node1, Argument node2) {
		return DefaultGraph.existsDirectedPath(this, node1, node2);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.graphs.Graph#getNeighbors(org.tweetyproject.graphs.Node)
	 */
	@Override
	public Collection<Argument> getNeighbors(Argument node) {
		Set<Argument> neighbours = new HashSet<Argument>();
		neighbours.addAll(this.getAttacked(node));
		neighbours.addAll((Collection<? extends Argument>) this.getAttackers(node));
		return neighbours;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.graphs.Graph#getAdjancyMatrix()
	 */
	@Override
	public Matrix getAdjacencyMatrix() {
		Matrix m = new Matrix(this.getNumberOfNodes(), this.getNumberOfNodes());
		int i = 0, j;
		for(Argument a: this){
			j = 0;
			for(Argument b : this){
				m.setEntry(i, j, new IntegerConstant(this.areAdjacent(a, b) ? 1 : 0));				
				j++;
			}
			i++;
		}
		return m;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.graphs.Graph#getComplementGraph(int)
	 */
	@Override
	public SetafTheory getComplementGraph(int selfloops) {
		SetafTheory comp = new SetafTheory();
		for(Argument node: this)
			comp.add(node);
		for(Argument node1: this)
			for(Argument node2: this)
				if(node1 == node2){
					if(selfloops == Graph.INVERT_SELFLOOPS){
						if(!this.isAttackedBy(node2, node1))
							comp.add(new SetafAttack(node1, node2));
					}else if(selfloops == Graph.IGNORE_SELFLOOPS){
						if(this.isAttackedBy(node2, node1))
							comp.add(new SetafAttack(node1, node2));						
					}
				}else if(!this.isAttackedBy(node2, node1))
					comp.add(new SetafAttack(node1, node2));
		return comp;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.graphs.Graph#hasSelfLoops()
	 */
	@Override
	public boolean hasSelfLoops() {
		for(Argument a: this)
			if(this.isAttackedBy(a, a))
				return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.graphs.Graph#getEdge(org.tweetyproject.graphs.Node, org.tweetyproject.graphs.Node)
	 */
	@Override
	public Edge<Argument> getEdge(Argument a, Argument b) {
		if(this.isAttackedBy(b, a))
			return new SetafAttack(a, b);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.graphs.Graph#isWeightedGraph()
	 */
	@Override
	public boolean isWeightedGraph() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.graphs.Graph#getStronglyConnectedComponents()
	 */
	@Override
	public Collection<Collection<Argument>> getStronglyConnectedComponents() {
		return DefaultGraph.getStronglyConnectedComponents(this);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.graphs.Graph#getSubgraphs()
	 */
	@Override
	public Collection<Graph<Argument>> getSubgraphs() {	
		if(!SetafTheory.archivedSubgraphs.containsKey(this))			
			SetafTheory.archivedSubgraphs.put(this, DefaultGraph.<Argument>getSubgraphs(this));		
		return SetafTheory.archivedSubgraphs.get(this);
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SetafTheory o) {
		// DungTheory implements Comparable in order to 
		// have a fixed (but arbitrary) order among all theories
		// for that purpose we just use the hash code.
		return this.hashCode() - o.hashCode();
	}

	@Override
	protected DungSignature instantiateSignature() {
		return new DungSignature();
	}
	
	/**
	 * Checks whether there is at least one cycle in this DungTheory.
	 * @return "true" if there is a cycle in this DungTheory, "false" otherwise
	 * @param <S> the type of nodes
	 */
	public <S extends Node> boolean containsCycle() {
		return DefaultGraph.containsCycle(this);
	}

	/**
	 * Checks whether there is at least on eodd cycle in this DungTheory.
	 * A directed graph has an odd-length cycle if and only if at least one of its SCCs is non-bipartite
	 * @param <S> the type of nodes
	 * @return "true" if there is a cycle with odd length in this theory
	 */
	public <S extends Node> boolean containsOddCycle() {
		Collection<Collection<Argument>> sccs = this.getStronglyConnectedComponents();
		for (Collection<Argument> scc : sccs) {
			Graph<Argument> scc_g = this.getRestriction(scc);
			if (!DefaultGraph.isBipartite(scc_g))
				return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.graphs.DefaultGraph#getComponents()
	 */
	public Collection<Graph<Argument>> getComponents() {
		return DefaultGraph.getComponents(this);
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.graphs.DefaultGraph#getInducedSubgraphs()
	 */
	public Collection<Graph<Argument>> getInducedSubgraphs() {
		return DefaultGraph.getInducedSubgraphs(this);
	}

	@Override
	public Graph<Argument> getRestriction(Collection<Argument> nodes) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
