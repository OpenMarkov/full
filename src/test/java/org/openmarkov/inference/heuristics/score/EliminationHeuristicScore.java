package org.openmarkov.inference.heuristics.score;

import org.openmarkov.core.model.network.ProbNet;

/** Interface to calculate the quality of an 
 * <code>EliminationHeuristic</code> applied to a <code>ProbNet</code>. 
 * @author Manuel Arias */
public interface EliminationHeuristicScore {

	/**
	 * @param probNet. <code>ProbNet</code>
	 * @param heuristicsClasses. Set of heuristics to test. <code>Class[]</code>
	 * @return A measure of the quality of each heuristic. The bigger score is better. <code>double[]</code>
	 */
	@SuppressWarnings("rawtypes")
	double[] getScores(ProbNet probNet, Class[] heuristicsClasses);
	
}
