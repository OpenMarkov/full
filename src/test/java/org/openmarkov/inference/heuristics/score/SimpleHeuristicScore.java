package org.openmarkov.inference.heuristics.score;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.inference.heuristic.EliminationHeuristic;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.inference.huginPropagation.ClusterOfVariables;
import org.openmarkov.inference.huginPropagation.HuginForest;

/** This score is defined as the summation per each ProbNet of 1/size of network, 
 * and size of network = summation of the size of the cliques of the network. 
 * <code>HuginForest</code> produced from the 
 * <code>ProbNet</code> with the heuristic.
 * @author Manuel Arias */
public class SimpleHeuristicScore extends Thread implements EliminationHeuristicScore {

	// Attributes
	private ProbNet probNet;
	
	private double[] scores;
	
	private int scoreIndex;
	
	private HuginForest forest = null;
	
	private EliminationHeuristic heuristic;

	private Counter counter;
	
	// Constructors
	/**
	 * @param scores
	 * @param scoreIndex
	 * @param probNet
	 * @param heuristic
	 * @param counter
	 */
	public SimpleHeuristicScore(double[] scores, int scoreIndex, ProbNet probNet, EliminationHeuristic heuristic, Counter counter) {
		
		this.scores = scores;
		this.scoreIndex = scoreIndex;
		this.probNet = probNet;
		this.heuristic = heuristic;
		this.counter = counter;
	}
	
	/**
	 * This constructor is to be used internally to evaluate networks in parallel
	 * @param probNet
	 */
	public SimpleHeuristicScore(ProbNet probNet) {
		
		this.probNet = probNet;
		counter = new Counter(0);
	}

	// Methods
    /** Builds a HuginForest
     * @see java.lang.Thread#run()
     */
    public void run() {
    	
		try {
			forest = new HuginForest(probNet, heuristic);
		} catch (DoEditException | NonProjectablePotentialException	| WrongCriterionException | SecurityException e) {
			System.err.println("Can not create a HuginForest in this network: " + probNet.toString() + "\n");
			e.printStackTrace();
		}
		int accumulatedSize = getSumClustersSize(forest);
		scores[scoreIndex] = 1 / new Double(accumulatedSize);
		counter.incrementCount();
		notify();
    }
	
	/**
	 * @param probNet
	 */
	public void setProbNet(ProbNet probNet) {
		this.probNet = probNet;
	}
	
	/**
	 * @see org.openmarkov.inference.heuristics.score.EliminationHeuristicScore#getScores(org.openmarkov.core.model.network.ProbNet, java.lang.Class[])
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public double[] getScores(ProbNet probNet, Class[] heuristicsClasses) {
		
		int numHeuristics = heuristicsClasses.length;
		double[] scores = new double[numHeuristics];
		SimpleHeuristicScore[] threads = new SimpleHeuristicScore[numHeuristics];
		for (int scoreIndex = 0; scoreIndex < numHeuristics; scoreIndex++) {
			try {
				// Create instance of heuristic given its class
				List<List<Variable>> listOfListOfVariables = new ArrayList<List<Variable>>();
				listOfListOfVariables.add(probNet.getVariables());
				Constructor<?> heuristicConstructor = heuristicsClasses[scoreIndex].getConstructor(ProbNet.class);
				Object heuristic = heuristicConstructor.newInstance(new Object[] {probNet, listOfListOfVariables});
				// Create a thread for each heuristic
			    threads[scoreIndex] = new SimpleHeuristicScore(scores, scoreIndex, probNet, (EliminationHeuristic)heuristic, counter);
			    threads[scoreIndex].start();
			} catch (InstantiationException |IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				System.err.println("Can not create heuristic: " + heuristicsClasses[scoreIndex].getName() +"\n");
				e.printStackTrace();
			} catch (NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}
		
		// Wait until all threads finish
		while (counter.getCount() < scoreIndex) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return scores;
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
