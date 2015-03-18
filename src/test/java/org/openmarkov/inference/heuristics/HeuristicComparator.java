package org.openmarkov.inference.heuristics;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.inference.heuristic.EliminationHeuristic;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.inference.huginPropagation.HuginForest;
import org.openmarkov.inference.huginPropagation.HuginPropagation;

/** Compares two heuristics with a score function obtained performing the elimination in a collection of <code>ProbNet</code>s. 
 * The score is sum( for each probNet, sum(clique sizes)). In this way, the best heuristic is the lowest. */
//public class HeuristicComparator {
//
//	/** Collection of probNets to be used in the comparison. */
//	private Collection<ProbNet> probNets;
//	
//	/**
//	 * @param probNets. <code>Collection</code> of <code>ProbNet</code>s
//	 */
//	public HeuristicComparator(Collection<ProbNet> probNets) {
//		this.probNets = probNets;
//	}
//	
//	/**
//	 * @param heuristic1. <code>EliminationHeuristic</code>
//	 * @param heuristic2. <code>EliminationHeuristic</code>
//	 * @return An integer that can be <ol><li>Minor than zero if heuristic1 is better than heuristic2</li>
//	 * <li>Zero if they are equal</li><li>Bigger than zero if heuristic2 is better than heuristic1</li></ol> 
//	 * The absolute value is a measure of the difference in quality.
//	 */
//	public int compare(EliminationHeuristic heuristic1, EliminationHeuristic heuristic2) {
//		return getScore(heuristic2) - getScore(heuristic1);
//	}
//
//	private int getScore(EliminationHeuristic heuristic) {
//		int score = 0;
//		for (ProbNet probNet : probNets) {
//			try {
//				HuginForest forest = new HuginForest(probNet, heuristic);
//			} catch (DoEditException | NonProjectablePotentialException
//					| WrongCriterionException e) {
//				System.err.println("Can not create a HuginForest in this network:\n" + probNet.toString());
//				e.printStackTrace();
//			}
//		}
//		return score;
//	}
//	
//	/**
//	 * @param nodes
//	 * @param maxCliqueSize
//	 * @return Max clique size
//	 */
//	public static int getMaxCliqueSize(Collection<Node> nodes, int maxCliqueSize) {
//		if (isClique(nodes)) {
//			maxCliqueSize = getCliqueSize(nodes);
//		} else {
//			for (Node node : nodes) {
//				HashSet<Node> nodesMinusOne = new HashSet<Node>(nodes);
//				nodesMinusOne.remove(node);
//				if (getCliqueSize(nodesMinusOne) > maxCliqueSize) {
//					maxCliqueSize = max(maxCliqueSize, getCliqueSize(nodesMinusOne));
//				}
//			}
//		}
//		return maxCliqueSize;
//	}
//	
//	/**
//	 * @param a. <code>int</code>
//	 * @param b. <code>int</code>
//	 * @return max(a,b). <code>int</code>
//	 */
//	private int max(int a, int b) {
//		return a > b ? a : b;
//	}
//	
//	/**
//	 * @param nodes. <code>Collection</code> of <code>Node</code>
//	 * @return <code>true</code> if the nodes are a clique.
//	 */
//	private boolean isClique(Collection<Node> nodes) {
//		boolean isClique = true;
//		Iterator<Node> iterator = nodes.iterator();
//		while (isClique && iterator.hasNext()) {
//			Node node = iterator.next();
//			isClique = node.getNeighbors().containsAll(nodes);
//		}
//		return isClique;
//	}
//	
//	/**
//	 * @param cliqueNodes
//	 * @return
//	 */
//	private int getCliqueSize(Collection<Node> cliqueNodes) {
//		int size = 1;
//		for (Node node : cliqueNodes) {
//			size *= node.getVariable().getNumStates();
//		}
//		return size;
//	}
//
//	
//}
