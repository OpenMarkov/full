/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.inference.heuristics.score;

import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.exception.WrongGraphStructureException;
import org.openmarkov.core.inference.heuristic.EliminationHeuristic;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.inference.huginPropagation.ClusterOfVariables;
import org.openmarkov.inference.huginPropagation.HuginForest;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class applies several heuristics to one <code>ProbNet</code> to produce an array of scores.<p>
 * The score is defined, per each <code>ProbNet</code>, as 1/(size of network),
 * and <i>size of network</i> = summation of the size of the cliques of the network.
 *
 * @author Manuel Arias
 */
public class TrivialHeuristicScore extends Thread implements EliminationHeuristicScore {

	// Attributes
	private ProbNet probNet;

	private double[] scores;

	private int scoreIndex;

	private HuginForest forest = null;

	private EliminationHeuristic heuristic;

	// Constructors

	/**
	 * Initializes the
	 *
	 * @param probNet
	 */
	public TrivialHeuristicScore(ProbNet probNet) {
		this.probNet = probNet;
	}

	/**
	 * This constructor is to be used internally to evaluate networks in parallel.<p>
	 * A single instance of this class applies one <code>EliminationHeuristic</code> to one <code>ProbNet</code> and
	 * stores the result in <code>scores[scoreIndex]</code>
	 *
	 * @param scores     <code>double[]</code>
	 * @param scoreIndex <code>double</code>
	 * @param probNet    <code>ProbNet</code>
	 * @param heuristic  <code>EliminationHeuristic</code>
	 * @throws WrongGraphStructureException
	 */
	private TrivialHeuristicScore(double[] scores, int scoreIndex, ProbNet probNet, EliminationHeuristic heuristic)
			throws WrongGraphStructureException {
		this.scores = scores;
		this.scoreIndex = scoreIndex;
		this.probNet = probNet;
		this.heuristic = heuristic;
		createHuginForest();
	}

	// Methods

	/**
	 * Builds a HuginForest
	 *
	 * @throws WrongGraphStructureException
	 */
	private void createHuginForest() throws WrongGraphStructureException {
		try {
			forest = new HuginForest(probNet, heuristic);
		} catch (DoEditException | NonProjectablePotentialException | WrongCriterionException e) {
			e.printStackTrace();
		}
		int accumulatedSize = getSumClustersSize(forest);
		scores[scoreIndex] = 1 / (1 + new Double(accumulatedSize));
	}

	/**
	 * @param probNet
	 */
	public void setProbNet(ProbNet probNet) {
		this.probNet = probNet;
	}

	/**
	 * @throws WrongGraphStructureException
	 * @see org.openmarkov.inference.heuristics.score.EliminationHeuristicScore#getScores(org.openmarkov.core.model.network.ProbNet, java.lang.Class[])
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" }) @Override public double[] getScores(ProbNet probNet,
			Class[] heuristicsClasses) throws WrongGraphStructureException {

		int numHeuristics = heuristicsClasses.length;
		double[] scores = new double[numHeuristics];
		TrivialHeuristicScore[] trivialScores = new TrivialHeuristicScore[numHeuristics];
		for (int scoreIndex = 0; scoreIndex < numHeuristics; scoreIndex++) {
			try {
				// Create instance of heuristic given its class
				List<List<Variable>> listOfListOfVariables = new ArrayList<List<Variable>>();
				listOfListOfVariables.add(probNet.getVariables());
				Constructor<?> heuristicConstructor = heuristicsClasses[scoreIndex]
						.getConstructor(ProbNet.class, List.class);
				// Create a thread for each heuristic
				trivialScores[scoreIndex] = new TrivialHeuristicScore(scores, scoreIndex, probNet.copy(),
						(EliminationHeuristic) heuristicConstructor
								.newInstance(new Object[] { probNet, listOfListOfVariables }));
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				System.err.println("Can not create heuristic: " + heuristicsClasses[scoreIndex].getName() + "\n");
				e.printStackTrace();
			} catch (NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}

		//		// Wait until all threads finish
		//		while (counter.getCount() < scoreIndex) {
		//			try {
		//				wait();
		//			} catch (InterruptedException e) {
		//				e.printStackTrace();
		//			}
		//		}
		//
		return scores;
	}

	/**
	 * @param forest <code>HuginForest</code>
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
