package org.openmarkov.inference.heuristics;

import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.inference.heuristic.EliminationHeuristic;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.inference.huginPropagation.ClusterOfVariables;
import org.openmarkov.inference.huginPropagation.HuginForest;

/** This score is summation of the sizes of the cliques of a 
 * <code>HuginForest</code> produced from the 
 * <code>ProbNet</code> with the heuristic. */
public class SimpleScore implements EliminationHeuristicScore {

	private EliminationHeuristic heuristic;
	
	public SimpleScore(EliminationHeuristic heuristic) {
		this.heuristic = heuristic;
	}
	
	@Override
	public int getScore(ProbNet probNet) {
		int score = 0;
		try {
			HuginForest forest = new HuginForest(probNet, heuristic);
			score = getSumClustersSize(forest);
		} catch (DoEditException | NonProjectablePotentialException
				| WrongCriterionException e) {
			System.err.println("Can not create a HuginForest in this network:\n" + 
				probNet.toString());
			e.printStackTrace();
		}
		return score;
	}
	
	/**
	 * @param forest. <code>HuginForest</code>
	 * @return Sum of the clusters sizes. <code>int</code>
	 */
	public int getSumClustersSize(HuginForest forest) {
		int sumCliquesSize = 0;
		for (ClusterOfVariables cluster : forest.getNodes()) {
			sumCliquesSize += cluster.size(); 
		}
		return sumCliquesSize;
	}

}
