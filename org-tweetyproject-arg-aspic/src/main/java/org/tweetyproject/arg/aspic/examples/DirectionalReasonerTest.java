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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.aspic.examples;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.tweetyproject.arg.aspic.reasoner.DirectionalAspicReasoner;
import org.tweetyproject.arg.aspic.reasoner.ModuleBasedAspicReasoner;
import org.tweetyproject.arg.aspic.syntax.AspicArgumentationTheory;
import org.tweetyproject.arg.aspic.util.RandomAspicArgumentationTheoryGenerator;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.commons.util.Pair;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * Test runtime of module-based vs. directional reasoners.
 * 
 * Also checks if they give the same answers
 * 
 * @author Tjitze Rienstra
 *
 */
public class DirectionalReasonerTest {
	/**
	 * 
	 * @param args args
	 */
	public static void main(String[] args) {		 
		int repetitions = 5000;
		int numberAtoms = 65;
		int numberFormulas = 120;
		int maxLiteralsInPremises = 3;
		double percentageStrictRules = 0.2;
		
		ModuleBasedAspicReasoner<PlFormula> moduleReasoner = new ModuleBasedAspicReasoner<PlFormula>(AbstractExtensionReasoner.getSimpleReasonerForSemantics(Semantics.GR));
		DirectionalAspicReasoner<PlFormula> directionalReasoner = new DirectionalAspicReasoner<PlFormula>(AbstractExtensionReasoner.getSimpleReasonerForSemantics(Semantics.GR));
		
		long totalTimeModuleBased = 0;
		long totalTimeDirectional = 0;
		long totalArgsModuleBased = 0;
		long totalArgsDirectional = 0;
		long totalTrue = 0;
		RandomAspicArgumentationTheoryGenerator gen = new RandomAspicArgumentationTheoryGenerator(numberAtoms, numberFormulas, maxLiteralsInPremises, percentageStrictRules);		
		for(int i = 0; i < repetitions; i++) {
			AspicArgumentationTheory<PlFormula> theory = gen.next();
			System.out.println(i + "\t" + theory);
			PlFormula query = new Proposition("A1");
			
			// Skip instances taking longer than 10sec
			System.out.println("Module-based...");
			boolean answer1 = false;
			try {
				Pair<Long, Pair<Integer, Boolean>> res = runWithTimeout(new Callable<Pair<Long, Pair<Integer, Boolean>>>() {
					@Override
					public Pair<Long, Pair<Integer, Boolean>> call() throws Exception {
						long millis = System.currentTimeMillis();
						DungTheory af1 = moduleReasoner.getDungTheory(theory, query);
						boolean answer1 = moduleReasoner.query(af1, query, InferenceMode.CREDULOUS);
						return new Pair<Long, Pair<Integer, Boolean>>(millis, new Pair<Integer, Boolean>(af1.getNumberOfNodes(), answer1));
					}
				}, 10, TimeUnit.SECONDS);
				totalTimeModuleBased += System.currentTimeMillis()-res.getFirst();
				totalArgsModuleBased += res.getSecond().getFirst();
				answer1 = res.getSecond().getSecond();
				if (res.getSecond().getSecond()) totalTrue++;
			} catch (Exception e) {
				System.out.println("Timeout... skipping");
				continue;
			}
				
			System.out.println("Directional...");
			long millis = System.currentTimeMillis();
			DungTheory af2 = directionalReasoner.getDungTheory(theory, query);
			boolean answer2 = directionalReasoner.query(af2, query, InferenceMode.CREDULOUS);
			totalTimeDirectional += System.currentTimeMillis()-millis;
			totalArgsDirectional += af2.getNumberOfNodes();
			
			if (answer1 != answer2) {
				System.out.println("Module-based and directional reasoners gave different answers");
				System.out.println("Module-based answer: " + answer1 + " directional answer: " + answer2);
				System.out.println("Query: " + query);
				System.out.println("Theory: " + theory.toString());
				throw new RuntimeException();
			}
			millis = System.currentTimeMillis();
		}	
		System.out.println();
		System.out.println("Runtime module-based        : " + totalTimeModuleBased + "ms");
		System.out.println("Argument count module-based : " + totalArgsModuleBased);
		System.out.println("Runtime directional         : " + totalTimeDirectional+ "ms");
		System.out.println("Argument count directiona   : " + totalArgsDirectional);
		System.out.println("Queries returning accepted  : " + totalTrue + "/" + repetitions);
	}

//	private static void printAF(DungTheory af) {
//		for (Argument a: af) {
//			System.out.println("arg: " + a);
//		}
//		for (Attack a: af.getAttacks()) {
//			System.out.println("att: " + a);
//		}
//	}
  /**
   * 
   * @param <T> some callable object
   * @param callable callable object
   * @param timeout timeout for runtime
   * @param timeUnit unit for timeout
   * @return directional reasoner result
   * @throws Exception exception
   */
  public static <T> T runWithTimeout(Callable<T> callable, long timeout, TimeUnit timeUnit) throws Exception {
    final ExecutorService executor = Executors.newSingleThreadExecutor();
    final Future<T> future = executor.submit(callable);
    executor.shutdown(); // This does not cancel the already-scheduled task.
    try {
      return future.get(timeout, timeUnit);
    }
    catch (TimeoutException e) {
      //remove this if you do not want to cancel the job in progress
      //or set the argument to 'false' if you do not want to interrupt the thread
      future.cancel(true);
      throw e;
    }
    catch (ExecutionException e) {
      //unwrap the root cause
      Throwable t = e.getCause();
      if (t instanceof Error) {
        throw (Error) t;
      } else if (t instanceof Exception) {
        throw (Exception) t;
      } else {
        throw new IllegalStateException(t);
      }
    }
  }

	
}
