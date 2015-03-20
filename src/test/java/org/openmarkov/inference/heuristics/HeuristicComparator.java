package org.openmarkov.inference.heuristics;

import java.util.Collection;

import org.openmarkov.core.inference.heuristic.EliminationHeuristic;
import org.openmarkov.core.model.network.ProbNet;

/** Compares two heuristics with a score function obtained performing 
 * the elimination in a collection of <code>ProbNet</code>s. 
 * The score is sum( for each probNet, sum(clique sizes)). 
 * In this way, the best heuristic is the lowest. */
public class HeuristicComparator {

	/** Collection of probNets to be used in the comparison. */
	private Collection<ProbNet> probNetsDB;
	
	/**
	 * @param probNets. <code>Collection</code> of <code>ProbNet</code>s
	 */
	public HeuristicComparator(final Collection<ProbNet> probNets) {
		this.probNetsDB = probNets;
	}
	
	/**
	 * @param heuristic1. <code>EliminationHeuristic</code>
	 * @param heuristic2. <code>EliminationHeuristic</code>
	 * @return An integer that can be <ol>
	 * <li>Minor than zero if heuristic1 is better than heuristic2</li>
	 * <li>Zero if they are equal</li>
	 * <li>Bigger than zero if heuristic2 is better than heuristic1</li></ol> 
	 * The absolute value is a measure of the difference in quality.
	 */
	public int compare(final EliminationHeuristic heuristic1, final EliminationHeuristic heuristic2) {
		int difference = 0;
		final SimpleScore score1 = new SimpleScore(heuristic1);
		final SimpleScore score2 = new SimpleScore(heuristic2);
		for (ProbNet probNet : probNetsDB) {
			difference += score1.getScore(probNet) - score2.getScore(probNet);
		}
		return difference;
	}

	/**
	 * @param probNetsDB. Data base of probNets.
	 *   <code>Collection</code> of <code>ProbNet</code>
	 */
	public void setProbNetsDB(final Collection<ProbNet> probNetsDB) {
		this.probNetsDB = probNetsDB;
	}

	/**
	 * @return Data base of probNets. <code>Collection</code> of <code>ProbNet</code>
	 */
	public Collection<ProbNet> getProbNetsDB() {
		return probNetsDB;
	}

}
