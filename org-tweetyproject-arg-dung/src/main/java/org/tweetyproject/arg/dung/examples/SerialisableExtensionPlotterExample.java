package org.tweetyproject.arg.dung.examples;

import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.Graph;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisibility.SerialisableExtensionReasonerWithAnalysis;
import org.tweetyproject.arg.dung.serialisibility.plotter.SerialisableExtensionAnalysisNode;
import org.tweetyproject.arg.dung.serialisibility.plotter.SerialisableExtensionPlotter;

import java.util.HashSet;

public class SerialisableExtensionPlotterExample {

	private static void plotExamplesForReasoner(Semantics semantic, DungTheory[] examples) {
		HashSet<Graph<SerialisableExtensionAnalysisNode>> graphs = new HashSet<Graph<SerialisableExtensionAnalysisNode>>();
		for (DungTheory example : examples) {
			graphs.add(
					SerialisableExtensionReasonerWithAnalysis
						.getSerialisableReasonerForSemantics(semantic)
						.getModelsWithAnalysis(example).getGraph()
					);
		}
		SerialisableExtensionPlotter.plotGraph(graphs, 2000, 1000);
		
	}
	
	
	public static void main(String[] args) {
		System.out.println("======================================== all Examples ========================================");
		plotExamplesForReasoner(Semantics.CO, new DungTheory[] {
				SerialisableExtensionReasonerWithAnalysisExample.buildExample1(),
				SerialisableExtensionReasonerWithAnalysisExample.buildExample2(),
				SerialisableExtensionReasonerWithAnalysisExample.buildExample3()
				});
		System.out.println("");
		/*
		System.out.println("======================================== Example 2 ========================================");
		DungTheoryPlotter.plotFramework(example2(), 2000, 1000);
		System.out.println("");
		
		System.out.println("======================================== Example 3 ========================================");
		DungTheoryPlotter.plotFramework(example3(), 2000, 1000);
		System.out.println("");
		*/
	}

}
