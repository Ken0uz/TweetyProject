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
 package org.tweetyproject.arg.dung.reasoner;

import java.util.Collection;
import java.util.HashSet;
import java.util.StringTokenizer;

import org.tweetyproject.arg.dung.semantics.Semantics;

/**
 * This enum contains all supported problems for probo-based reasoner.
 * 
 * @author Matthias Thimm
 */
public enum ProboProblem {
	/**DC_CF*/
	DC_CF (SubProblem.DC, Semantics.CF),
	/**DC_ADM*/
	DC_ADM (SubProblem.DC, Semantics.ADM),
	/**DC_CO*/
	DC_CO (SubProblem.DC, Semantics.CO),
	/**DC_GR*/
	DC_GR (SubProblem.DC, Semantics.GR),
	/**DC_PR*/
	DC_PR (SubProblem.DC, Semantics.PR),
	/**DC_ST*/
	DC_ST (SubProblem.DC, Semantics.ST),
	/**DC_STG*/
	DC_STG (SubProblem.DC, Semantics.STG),
	/**DC_SST*/
	DC_SST (SubProblem.DC, Semantics.SST),
	/**DC_ID*/
	DC_ID (SubProblem.DC, Semantics.ID),
	/**DC_CF2*/
	DC_CF2 (SubProblem.DC, Semantics.CF2),
	/**DS_CF*/
	DS_CF (SubProblem.DS, Semantics.CF),
	/**DS_ADM*/
	DS_ADM (SubProblem.DS, Semantics.ADM),
	/**DS_CO*/
	DS_CO (SubProblem.DS, Semantics.CO),
	/**DS_GR*/
	DS_GR (SubProblem.DS, Semantics.GR),
	/**DS_PR*/
	DS_PR (SubProblem.DS, Semantics.PR),
	/**DS_ST*/
	DS_ST (SubProblem.DS, Semantics.ST),
	/**DS_STG*/
	DS_STG (SubProblem.DS, Semantics.STG),
	/**DS_SST*/
	DS_SST (SubProblem.DS, Semantics.SST),
	/**DS_ID*/
	DS_ID (SubProblem.DS, Semantics.ID),
	/**DS_CF2*/
	DS_CF2 (SubProblem.DS, Semantics.CF2),
	/**DE_CF*/
	DE_CF (SubProblem.DE, Semantics.CF),
	/**DE_ADM*/
	DE_ADM (SubProblem.DE, Semantics.ADM),
	/**DE_CO*/
    DE_CO (SubProblem.DE, Semantics.CO),
    /**DE_GR*/
	DE_GR (SubProblem.DE, Semantics.GR),
	/**DE_PR*/
	
	DE_PR (SubProblem.DE, Semantics.PR),
	/**DE_ST*/
	DE_ST (SubProblem.DE, Semantics.ST),
	/**DE_STG*/
	DE_STG (SubProblem.DE, Semantics.STG),
	/**DE_SST*/
	DE_SST (SubProblem.DE, Semantics.SST),
	/**DE_ID*/
	DE_ID (SubProblem.DE, Semantics.ID),
	/**DE_CF2*/
	DE_CF2 (SubProblem.DE, Semantics.CF2),
	/**DL_CF*/
	
	DL_CF (SubProblem.DL, Semantics.CF),
	/**DL_ADM*/
	DL_ADM (SubProblem.DL, Semantics.ADM),
	/**DL_CO*/
	DL_CO (SubProblem.DL, Semantics.CO),
	/**DL_GR*/
	DL_GR (SubProblem.DL, Semantics.GR),
	/**DL_PR*/
	DL_PR (SubProblem.DL, Semantics.PR),
	/**DL_ST*/
	DL_ST (SubProblem.DL, Semantics.ST),
	/**DL_STG*/
	DL_STG (SubProblem.DL, Semantics.STG),
	/**DL_SST*/
	DL_SST (SubProblem.DL, Semantics.SST),
	/**DL_ID*/
	DL_ID (SubProblem.DL, Semantics.ID),
	/**DL_CF2*/
	DL_CF2 (SubProblem.DL, Semantics.CF2),
	/**DX_CF*/
	DX_CF (SubProblem.DX, Semantics.CF),
	/**DX_ADM*/
	DX_ADM (SubProblem.DX, Semantics.ADM),
	/**DX_CO*/
	DX_CO (SubProblem.DX, Semantics.CO),
	/**DX_GR*/
	DX_GR (SubProblem.DX, Semantics.GR),
	/**DX_PR*/
	DX_PR (SubProblem.DX, Semantics.PR),
	/**DX_ST*/
	DX_ST (SubProblem.DX, Semantics.ST),
	/**DX_STG*/
	DX_STG (SubProblem.DX, Semantics.STG),
	/**DX_SST*/
	DX_SST (SubProblem.DX, Semantics.SST),
	/**DX_ID*/
	DX_ID (SubProblem.DX, Semantics.ID),
	/**DX_CF2*/
	DX_CF2 (SubProblem.DX, Semantics.CF2),
	/**DN_CF*/
	DN_CF (SubProblem.DN, Semantics.CF),
	/**DN_ADM*/
	DN_ADM (SubProblem.DN, Semantics.ADM),
	/**DN_CO*/
	DN_CO (SubProblem.DN, Semantics.CO),
	/**DN_GR*/
	DN_GR (SubProblem.DN, Semantics.GR),
	/**DN_PR*/
	DN_PR (SubProblem.DN, Semantics.PR),
	/**DN_ST*/
	DN_ST (SubProblem.DN, Semantics.ST),
	/**DN_STG*/
	DN_STG (SubProblem.DN, Semantics.STG),
	/**DN_SST*/
	DN_SST (SubProblem.DN, Semantics.SST),
	/**DN_ID*/
	DN_ID (SubProblem.DN, Semantics.ID),
	/**DN_CF2*/
	DN_CF2 (SubProblem.DN, Semantics.CF2),
	/**EC_CF*/
	EC_CF (SubProblem.EC, Semantics.CF),
	/**EC_ADM*/
	EC_ADM (SubProblem.EC, Semantics.ADM),
	/**EC_CO*/
	EC_CO (SubProblem.EC, Semantics.CO),
	/**EC_GR*/
	EC_GR (SubProblem.EC, Semantics.GR),
	/**EC_PR*/
	EC_PR (SubProblem.EC, Semantics.PR),
	/**EC_ST*/
	EC_ST (SubProblem.EC, Semantics.ST),
	/**EC_STG*/
	EC_STG (SubProblem.EC, Semantics.STG),
	/**EC_SST*/
	EC_SST (SubProblem.EC, Semantics.SST),
	/**EC_ID*/
	EC_ID (SubProblem.EC, Semantics.ID),
	/**EC_CF2*/
	EC_CF2 (SubProblem.EC, Semantics.CF2),
	/**ES_CF*/
	ES_CF (SubProblem.ES, Semantics.CF),
	/**ES_ADM*/
	ES_ADM (SubProblem.ES, Semantics.ADM),
	/**ES_CO*/
	ES_CO (SubProblem.ES, Semantics.CO),
	/**ES_GR*/
	ES_GR (SubProblem.ES, Semantics.GR),
	/**ES_PR*/
	ES_PR (SubProblem.ES, Semantics.PR),
	/**ES_ST*/
	ES_ST (SubProblem.ES, Semantics.ST),
	/**ES_STG*/
	ES_STG (SubProblem.ES, Semantics.STG),
	/**ES_SST*/
	ES_SST (SubProblem.ES, Semantics.SST),
	/**ES_ID*/
	ES_ID (SubProblem.ES, Semantics.ID),
	/**ES_CF2*/
	ES_CF2 (SubProblem.ES, Semantics.CF2),
	/**EE_CF*/
	EE_CF (SubProblem.EE, Semantics.CF),
	/**EE_ADM*/
	EE_ADM (SubProblem.EE, Semantics.ADM),
	/**EE_CO*/
	EE_CO (SubProblem.EE, Semantics.CO),
	/**EE_GR*/
	EE_GR (SubProblem.EE, Semantics.GR),
	/**EE_PR*/
	EE_PR (SubProblem.EE, Semantics.PR),
	/**EE_ST*/
	EE_ST (SubProblem.EE, Semantics.ST),
	/**EE_STG*/
	EE_STG (SubProblem.EE, Semantics.STG),
	/**EE_SST*/
	EE_SST (SubProblem.EE, Semantics.SST),
	/**EE_ID*/
	EE_ID (SubProblem.EE, Semantics.ID),
	/**EE_CF2*/
	EE_CF2 (SubProblem.EE, Semantics.CF2),	
	/**EL_CF*/
	EL_CF (SubProblem.EL, Semantics.CF),
	/**EL_ADM*/
	EL_ADM (SubProblem.EL, Semantics.ADM),
	/**EL_CO*/
	EL_CO (SubProblem.EL, Semantics.CO),
	/**EL_GR*/
	EL_GR (SubProblem.EL, Semantics.GR),
	/**EL_PR*/
	EL_PR (SubProblem.EL, Semantics.PR),
	/**EL_ST*/
	EL_ST (SubProblem.EL, Semantics.ST),
	/**EL_STG*/
	EL_STG (SubProblem.EL, Semantics.STG),
	/**EL_SST*/
	EL_SST (SubProblem.EL, Semantics.SST),
	/**EL_ID*/
	EL_ID (SubProblem.EL, Semantics.ID),
	/**EL_CF2*/
	EL_CF2 (SubProblem.EL, Semantics.CF2),
	/**SE_CF*/
	SE_CF (SubProblem.SE, Semantics.CF),
	/**SE_ADM*/
	SE_ADM (SubProblem.SE, Semantics.ADM),
	/**SE_STG*/
	SE_STG (SubProblem.SE, Semantics.STG),
	/**SE_SST*/
	SE_SST (SubProblem.SE, Semantics.SST),
	/**SE_ID*/
	SE_ID (SubProblem.SE, Semantics.ID),
	/**SE_CF2*/
	SE_CF2 (SubProblem.SE, Semantics.CF2),
	/**SE_CO*/
	SE_CO (SubProblem.SE, Semantics.CO),
	/**SE_GR*/
	SE_GR (SubProblem.SE, Semantics.GR),
	/**SE_PR*/
	SE_PR (SubProblem.SE, Semantics.PR),
	/**SE_ST*/
	SE_ST (SubProblem.SE, Semantics.ST),
	/**D3*/
	D3 (SubProblem.EE, Semantics.diverse);
	
	/**
	 * The actual computational sub-problem.
	 * @author Matthias Thimm
	 */
	public enum SubProblem{
		/**DC*/
		DC ("Decide credulously","DC",true),
		/**DS*/
		DS ("Decide skeptically","DS",true),
		/**DE*/
		DE ("Decide extension","DE",true),
		/**DL*/
		DL ("Decide labeling","DL",true),
		/**DX*/
		DX ("Decide existence","DX",true),
		/**DN*/
		DN ("Decide non-empty","DN",true),
		/**EC*/
		EC ("Enumerate credulously","EC",false),
		/**ES*/
		ES ("Enumerate skeptically","ES",false),
		/**EE*/
		EE ("Enumerate extension","EE",false),
		/**EL*/
		EL ("Enumerate labeling","EL",false),
		/**SE*/
		SE ("Some extension", "SE",false);
		
		/** The description of the sub-problem. */
		private String description;
		/** The abbreviation of the sub-problem. */
		private String abbreviation;
		
		private boolean justification;
		
		/**
		 * Creates a new sub-problem
		 * @param description some description.
		 * @param abbreviation the abbreviation of the sub-problem.
		 * @param just whether justification is needed
		 */
		private SubProblem(String description, String abbreviation, boolean just){
			this.description = description;
			this.abbreviation = abbreviation;
			justification =just;
		}
		/**
		 * Returns the description of the sub-problem.
		 * @return the description of the sub-problem.
		 */
		public String description(){
			return this.description;
		}
		/**
		 * Returns the abbreviation of the sub-problem.
		 * @return the abbreviation of the sub-problem.
		 */
		public String abbreviation(){
			return this.abbreviation;
		}
		
		/**
		 * 
		 * @return isJustificationProblem
		 */
		public boolean isJustificationProblem() {
			return justification;
		}
		
	};
	
	/** The description of the problem. */
	private SubProblem subProblem;
	/** The semantics for the problem. */
	private Semantics semantics;
		
	/**
	 * Creates a new problem.
	 * @param subProblem the sub-problem
	 * @param semantics the semantics.
	 */
	private ProboProblem(SubProblem subProblem, Semantics semantics){
		this.subProblem = subProblem;
		this.semantics = semantics;
	}
	
	/**
	 * Returns the sub-problem of the problem.
	 * @return the sub-problem of the problem.
	 */
	public SubProblem subProblem(){
		return this.subProblem;
	}
	
	/**
	 * Returns the semantics of the problem.
	 * @return the semantics of the problem.
	 */
	public Semantics semantics(){
		return this.semantics;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString(){
		if(this.equals(D3))
			return "D3";
		return this.subProblem.abbreviation() + "-" + this.semantics.abbreviation();
	}
	
	/**
	 * Returns the problem instance that corresponds to the given abbreviation.
	 * @param s some string representing a problem instance
	 * @return the actual problem
	 */
	public static ProboProblem getProblem(String s){
		s = s.trim();
		for(ProboProblem p: ProboProblem.values())
			if(s.equals(p.toString()))
				return p;		
		return null;		
	}
	
	/**
	 * Returns a collection of problems parsed from the given string which
	 * has to be in the format "[problem1,...,problemn]".
	 * @param s some string
	 * @return a collection of problems
	 */
	public static Collection<ProboProblem> getProblems(String s){
		s = s.trim();
		if(!s.startsWith("[") || !s.endsWith("]"))
			throw new IllegalArgumentException("Malformed problem specification: " + s);
		s = s.substring(1, s.length()-1);
		StringTokenizer tokenizer = new StringTokenizer(s, ",");
		Collection<ProboProblem> problems = new HashSet<ProboProblem>();
		while(tokenizer.hasMoreTokens()){
			 problems.add(ProboProblem.getProblem(tokenizer.nextToken()));
		}
		return problems;
	}	


	/**
	 * 
	 * @return if problem is justified
	 */
	public boolean isJustificationProblem() {
		return subProblem.isJustificationProblem();
	}
}
