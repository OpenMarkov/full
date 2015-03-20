package org.openmarkov.inference.heuristics;

import org.openmarkov.core.model.network.ProbNet;

/** Interface to calculate the quality of an 
 * <code>EliminationHeuristic</code> applied to a ProbNet. */
public interface EliminationHeuristicScore {

	/**
	 * @param probNet. <code>ProbNet</code>
	 * @return A measure of the quality of heuristic. The lowest score is the best. 
	 */
	int getScore(ProbNet probNet);
	
}
