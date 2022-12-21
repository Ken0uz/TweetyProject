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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.rankings.examples;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.rankings.reasoner.SerialisableRankingReasoner;

public class SerialisableRankingReasonerExample {
	public static void main(String[] args) {
		// Construct AAF
		DungTheory aaf = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Argument d = new Argument("d");
		Argument e = new Argument("e");
		aaf.add(a);
		aaf.add(b);
		aaf.add(c);
		aaf.add(d);
		aaf.add(e);
		aaf.add(new Attack(a,b));
		aaf.add(new Attack(b,c));
		aaf.add(new Attack(c,d));
		aaf.add(new Attack(d,e));
		
		SerialisableRankingReasoner reasoner = new SerialisableRankingReasoner();
		
		System.out.println(reasoner.getModel(aaf));
	}
}
