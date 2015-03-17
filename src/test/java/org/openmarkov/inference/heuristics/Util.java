package org.openmarkov.inference.heuristics;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.openmarkov.core.model.network.Node;

/** Useful method for getting the tree width of a collection of nodes. */
public class Util {

	/**
	 * @param nodes
	 * @param maxCliqueSize
	 * @return Max clique size
	 */
	public static int getMaxCliqueSize(Collection<Node> nodes, int maxCliqueSize) {
		if (isClique(nodes)) {
			maxCliqueSize = getCliqueSize(nodes);
		} else {
			for (Node node : nodes) {
				HashSet<Node> nodesMinusOne = new HashSet<Node>(nodes);
				nodesMinusOne.remove(node);
				if (getCliqueSize(nodesMinusOne) > maxCliqueSize) {
					maxCliqueSize = max(maxCliqueSize, getCliqueSize(nodesMinusOne));
				}
			}
		}
		return maxCliqueSize;
	}
	
	/**
	 * @param a. <code>int</code>
	 * @param b. <code>int</code>
	 * @return max(a,b). <code>int</code>
	 */
	private static int max(int a, int b) {
		return a > b ? a : b;
	}
	
	/**
	 * @param nodes. <code>Collection</code> of <code>Node</code>
	 * @return <code>true</code> if the nodes are a clique.
	 */
	private static boolean isClique(Collection<Node> nodes) {
		boolean isClique = true;
		Iterator<Node> iterator = nodes.iterator();
		while (isClique && iterator.hasNext()) {
			Node node = iterator.next();
			isClique = node.getNeighbors().containsAll(nodes);
		}
		return isClique;
	}
	
	/**
	 * @param cliqueNodes
	 * @return
	 */
	private static int getCliqueSize(Collection<Node> cliqueNodes) {
		int size = 1;
		for (Node node : cliqueNodes) {
			size *= node.getVariable().getNumStates();
		}
		return size;
	}
	
}
