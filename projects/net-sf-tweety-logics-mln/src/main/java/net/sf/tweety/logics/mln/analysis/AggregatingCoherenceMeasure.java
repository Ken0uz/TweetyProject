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
package net.sf.tweety.logics.mln.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import net.sf.tweety.commons.BeliefBaseReasoner;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.mln.MarkovLogicNetwork;
import net.sf.tweety.logics.mln.syntax.MlnFormula;
import net.sf.tweety.math.func.AggregationFunction;
import net.sf.tweety.math.norm.RealVectorNorm;
import net.sf.tweety.logics.commons.syntax.RelationalFormula;

/**
 * This coherence measure uses an aggregation function and a distance function
 * to measure the coherence of an MLN. For each formula in the MLN the distance
 * function looks at the probabilities of all ground instance and compares this 
 * vector to the intended probabilities. The aggregation function is used to
 * aggregate the distances for all formulas. 
 * 
 * @author Matthias Thimm
 *
 */
public class AggregatingCoherenceMeasure extends AbstractCoherenceMeasure {

	private static final long serialVersionUID = 4162719595968757160L;
	
	/** The norm used to measure the difference of the probabilities
	 * of each ground instance for a single formula. */
	private RealVectorNorm norm;
	/** The aggregation function used to aggregate the distances for each formula. */
	private AggregationFunction aggregator;
	
	public AggregatingCoherenceMeasure(RealVectorNorm norm, AggregationFunction aggregator){
		this.aggregator = aggregator;
		this.norm = norm;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.markovlogic.analysis.AbstractCoherenceMeasure#coherence(net.sf.tweety.logics.markovlogic.MarkovLogicNetwork, net.sf.tweety.Reasoner, net.sf.tweety.logics.firstorderlogic.syntax.FolSignature)
	 */
	@Override
	public double coherence(MarkovLogicNetwork mln, BeliefBaseReasoner<MarkovLogicNetwork> reasoner, FolSignature signature) {
		List<Double> distances = new ArrayList<Double>();
		for(MlnFormula f: mln){
			Vector<Double> intended = new Vector<Double>();
			Vector<Double> observed = new Vector<Double>();
			Double pObserved;
			if(f.isStrict())
				pObserved = 1d;
			else pObserved = (Math.exp(f.getWeight())/(f.getFormula().getSatisfactionRatio()+Math.exp(f.getWeight())));
			for(RelationalFormula groundFormula: f.getFormula().allGroundInstances(signature.getConstants())){
				observed.add(reasoner.query(mln,groundFormula).getAnswerDouble());
				intended.add(pObserved);
			}			
			distances.add(this.norm.distance(intended, observed));
		}
		return 1-this.aggregator.eval(distances);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.markovlogic.analysis.AbstractCoherenceMeasure#toString()
	 */
	@Override
	public String toString() {		
		return "C<" + this.norm.toString() + ", " + this.aggregator.toString() + ">";
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((aggregator == null) ? 0 : aggregator.hashCode());
		result = prime * result
				+ ((norm == null) ? 0 : norm.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AggregatingCoherenceMeasure other = (AggregatingCoherenceMeasure) obj;
		if (aggregator == null) {
			if (other.aggregator != null)
				return false;
		} else if (!aggregator.equals(other.aggregator))
			return false;
		if (norm == null) {
			if (other.norm != null)
				return false;
		} else if (!norm.equals(other.norm))
			return false;
		return true;
	}

}
