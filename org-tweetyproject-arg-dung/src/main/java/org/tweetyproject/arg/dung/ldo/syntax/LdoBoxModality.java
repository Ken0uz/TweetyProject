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
package org.tweetyproject.arg.dung.ldo.syntax;

/**
 * *description missing*
 * @author Matthias Thimm
 *
 */
public class LdoBoxModality extends AbstractLdoModality {

	/**
	 * 
	 * @param innerFormula innerFormula
	 */
	public LdoBoxModality(LdoFormula innerFormula) {
		super(innerFormula);
	}

	@Override
	public LdoFormula clone() {
		return new LdoBoxModality(this.getInnerFormula());
	}

	public String toString(){
		return "[](" + this.getInnerFormula() + ")";
	}
}
