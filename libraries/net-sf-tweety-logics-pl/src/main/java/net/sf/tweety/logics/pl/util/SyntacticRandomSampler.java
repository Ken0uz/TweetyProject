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
package net.sf.tweety.logics.pl.util;

import java.util.HashSet;
import java.util.Random;

import net.sf.tweety.commons.BeliefSetSampler;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.PlSignature;
import net.sf.tweety.math.probability.Probability;
import net.sf.tweety.math.probability.ProbabilityFunction;

/**
 * <p>This sampler implements a random generation algorithm for generating formulas, based on 
 * the syntax tree of propositional formulas. Consider the following BNF for a propositional logic
 * formula:</p>
 * F ::==  !F | F && F | F || F | A
 * <p>with propositions A. The algorithm for constructing formulas takes four parameters:</p>
 *  <ul>
 *  	<li>probneg: the probability to generate a negation</li>
 *  	<li>probconj: the probability to generate a conjunction</li>
 *  	<li>probdisj: the probability to generate a disjunction</li>  	
 *  	<li>recDecrease: a value in (0,1) by which the above probabilities are multiplied in each recursive step to
 *  	increase likelihood of termination.</li>
 *  </ul>
 *  From these parameters the probability to generate a proposition "probprop" is computed via
 *  probprop=1-probneg-probconj-probdisj (the actual proposition is selected with a uniform distribution).
 *  Then a formula is generated by randomly selecting which
 *  rule in the above BNF is followed wrt. these probabilities. Note that the algorithm is not guaranteed
 *  to terminate.</p>  
 * 
 * @author Matthias Thimm
 *
 */
public class SyntacticRandomSampler extends BeliefSetSampler<PlFormula,PlBeliefSet> {
	/** The probability function modeling the random decisions.*/
	private ProbabilityFunction<Byte> prob;
	/** The probability function used to sample propositions*/
	private ProbabilityFunction<Proposition> probProp;
	
	private double recDecrease;
	
	private static final byte NEG = 0;
	private static final byte CONJ = 1;
	private static final byte DISJ = 2;
	private static final byte PROP = 3;
	
	private Random rand = new Random();
	
	/**
	 * 
	 * Creates a new sampler.
	 * @param signature some set of propositions
	 * @param probneg the probability to generate a negation
	 * @param probconj the probability to generate a conjunction
	 * @param probdisj the probability to generate a disjunction
	 * @param recDecrease a value in (0,1) by which the above probabilities are multiplied in each recursive step to
 *  	increase likelihood of termination.
	 */
	public SyntacticRandomSampler(Signature signature, Probability probneg, Probability probconj, Probability probdisj, double recDecrease) {
		super(signature);
		if(probneg.doubleValue() + probconj.doubleValue() + probdisj.doubleValue() >= 1)
			throw new IllegalArgumentException("Sum of probabilities should be strictly less than one, otherwise the sampling algorithm will never terminate.");
		if(recDecrease <= 0 || recDecrease >= 1)
			throw new IllegalArgumentException("recDecrease should be in (0,1).");
		this.prob = new ProbabilityFunction<Byte>();
		this.prob.put(NEG, probneg);
		this.prob.put(CONJ, probconj);
		this.prob.put(DISJ, probdisj);
		this.prob.put(PROP, new Probability(1-probneg.doubleValue()-probconj.doubleValue() - probdisj.doubleValue()));
		this.recDecrease = recDecrease;
		this.probProp = ProbabilityFunction.getUniformDistribution(new HashSet<Proposition>(((PlSignature)this.getSamplerSignature()).toCollection()));
	}

	/**
	 * 
	 * Creates a new sampler.
	 * @param signature some set of propositions
	 * @param probneg the probability to generate a negation
	 * @param probconj the probability to generate a conjunction
	 * @param probdisj the probability to generate a disjunction
	 * @param recDecrease a value in (0,1) by which the above probabilities are multiplied in each recursive step to
 *  	increase likelihood of termination.
	 * @param minLength the minimum length of knowledge bases
	 * @param maxLength the maximum length of knowledge bases
	 */
	public SyntacticRandomSampler(Signature signature, Probability probneg, Probability probconj, Probability probdisj, double recDecrease, int minLength, int maxLength) {
		super(signature,minLength,maxLength);
		if(probneg.doubleValue() + probconj.doubleValue() + probdisj.doubleValue() >= 1)
			throw new IllegalArgumentException("Sum of probabilities should be strictly less than one, otherwise the sampling algorithm will never terminate.");
		if(recDecrease <= 0 || recDecrease >= 1)
			throw new IllegalArgumentException("recDecrease should be in (0,1).");
		this.prob = new ProbabilityFunction<Byte>();
		this.prob.put(NEG, probneg);
		this.prob.put(CONJ, probconj);
		this.prob.put(DISJ, probdisj);
		this.prob.put(PROP, new Probability(1-probneg.doubleValue()-probconj.doubleValue() - probdisj.doubleValue()));
		this.recDecrease = recDecrease;
		this.probProp = ProbabilityFunction.getUniformDistribution(new HashSet<Proposition>(((PlSignature)this.getSamplerSignature()).toCollection()));
		
	}	
	
	/**
	 * Adapts the given probability function by decreasing the probabilities of NEG, CONJ and DISJ.
	 * @param prob a probability function.
	 * @param value some value in (0,1)
	 * @return the modified probability function.
	 */
	private ProbabilityFunction<Byte> decrease(ProbabilityFunction<Byte> prob, double value){
		ProbabilityFunction<Byte> newProb = new ProbabilityFunction<Byte>(prob);
		newProb.put(NEG, newProb.get(NEG).mult(value));
		newProb.put(CONJ, newProb.get(CONJ).mult(value));
		newProb.put(DISJ, newProb.get(DISJ).mult(value));
		newProb.normalize();
		return newProb;
	}
	
	/**
	 * Samples a single formula.
	 * @return a single formula.
	 */
	public PlFormula sampleFormula(ProbabilityFunction<Byte> prob){
		switch(prob.sample(rand)){
		case NEG:
			return new Negation(this.sampleFormula(this.decrease(prob, this.recDecrease)));
		case CONJ:
			return new Conjunction(this.sampleFormula(this.decrease(prob, this.recDecrease)),this.sampleFormula(this.decrease(prob, this.recDecrease)));
		case DISJ:
			return new Disjunction(this.sampleFormula(this.decrease(prob, this.recDecrease)),this.sampleFormula(this.decrease(prob, this.recDecrease)));
		default:
			return this.probProp.sample(rand);
		}
	}
	
	/**
	 * @return
	 */
	@Override
	public PlBeliefSet next() {		
		PlBeliefSet bs = new PlBeliefSet();
		int numFormulas = rand.nextInt(this.getMaxLength()-this.getMinLength()+1) + this.getMinLength();
		while(numFormulas > 0){
			bs.add(this.sampleFormula(this.prob));
			numFormulas--;
		}		
		return bs;
	}

	
}

