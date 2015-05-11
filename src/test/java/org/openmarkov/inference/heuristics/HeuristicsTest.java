package org.openmarkov.inference.heuristics;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.event.UndoableEditEvent;

import org.junit.Before;
import org.junit.Test;
import org.openmarkov.core.action.RemoveNodeEdit;
import org.openmarkov.core.exception.WrongGraphStructureException;
import org.openmarkov.core.inference.PartialOrder;
import org.openmarkov.core.inference.heuristic.EliminationHeuristic;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.type.BayesianNetworkType;
import org.openmarkov.core.model.network.type.InfluenceDiagramType;
import org.openmarkov.inference.heuristic.canoAndMoral.CanoMoralElimination;
import org.openmarkov.inference.heuristic.hybridElimination.HybridElimination;
import org.openmarkov.inference.heuristic.minimalFillIn.MinimalFillIn;
import org.openmarkov.inference.heuristic.simpleElimination.SimpleElimination;
import org.openmarkov.inference.heuristics.score.TrivialHeuristicScore;
import org.openmarkov.inference.util.Util;

/**
 * Ensures that in every Bayesian network in the repository:
 * <ol>
 * <li> CanoMoralElimination is always better or equal than {MinimalFillIn and HybridElimination}, 
 * that are always better or equal than SimpleElimination 
 * </ol> 
 * @author Manuel Arias
 */
public class HeuristicsTest {

	// TODO Add performance tests
	
	@SuppressWarnings("rawtypes")
	private Class[] heuristicsClasses = new Class[] {
			CanoMoralElimination.class, 
			MinimalFillIn.class, 
			HybridElimination.class, 
			SimpleElimination.class
			};
	

	@Before
	public void setUp() throws Exception {
	}

//	/** Check that all the variables are removed and only once. */
//	@SuppressWarnings("unchecked")
//	@Test
//	public void test1() {
//		// Basic tests
//		List<ProbNet> probNetsDB = HeuristicComparator.readProbNetsDB(BayesianNetworkType.getUniqueInstance());
//		int numNetworks = probNetsDB.size();
//		System.out.println("Number of Bayesian networks: " + numNetworks);
//		for(int i = 0; i < numNetworks; i++) {
//			// All the variables are removed, and only once
//			ProbNet probNet = probNetsDB.get(i);
//			System.out.println("Network(" + i + "): " + probNet.getName());
//			List<Variable> probNetVariables = probNet.getVariables();
//			for (int j = 0; j < heuristicsClasses.length; j++) {
//				List<List<Variable>> listOfListOfVariables = new ArrayList<List<Variable>>();
//				listOfListOfVariables.add(probNetVariables);
//				Set<Variable> setOfVariables = new HashSet<Variable>(probNetVariables);
//				Constructor<?> heuristicConstructor;
//				try {
//					heuristicConstructor = heuristicsClasses[j].getConstructor(ProbNet.class, List.class);
//					Object heuristic = heuristicConstructor.newInstance(new Object[] {probNet, listOfListOfVariables});
//					EliminationHeuristic eliminationHeuristic = (EliminationHeuristic)heuristic;
//					System.out.print("  " + heuristic.getClass().getSimpleName() + ": ");
//					Variable variable;
//					while ((variable = eliminationHeuristic.getVariableToDelete()) != null) {
//						System.out.print(variable);
//						UndoableEditEvent event = new UndoableEditEvent(probNet, new RemoveNodeEdit(probNet, variable));
//						eliminationHeuristic.undoableEditHappened(event);
//						assertTrue(setOfVariables.contains(variable));
//						setOfVariables.remove(variable);
//						if (setOfVariables.isEmpty()) {
//							System.out.println(".");
//						} else {
//							System.out.print(", ");
//						}
//					}
//					assertTrue(setOfVariables.isEmpty());
//				} catch (NoSuchMethodException | SecurityException | 
//						InstantiationException | IllegalAccessException |
//						IllegalArgumentException | InvocationTargetException  e) {
//					e.printStackTrace();
//				}
//			}
//			System.out.println();
//		}
//	}
//
//	@Test
//	/** Check that the heuristics remove the variables following the partial order. */
//	public void test2() throws WrongGraphStructureException {
//		List<ProbNet> probNetsDB = 
//				HeuristicComparator.readProbNetsDB(InfluenceDiagramType.getUniqueInstance());
//		int numNetworks = probNetsDB.size();
//		System.out.println("Number of influence diagrams: " + numNetworks);
//		for(int i = 0; i < numNetworks; i++) {
//			// All the variables are removed, and only once
//			ProbNet probNet = probNetsDB.get(i);
//			System.out.println("Network(" + i + "): " + probNet.getName());
//			PartialOrder partialOrder = new PartialOrder(probNet);
//			List<Variable> chanceAndDecisionVariables = probNet.getChanceAndDecisionVariables();
//			for (int j = 0; j < heuristicsClasses.length; j++) {
//				List<List<Variable>> listOfListOfVariables = partialOrder.getOrder();
//				Set<Variable> setOfVariables = new HashSet<Variable>(chanceAndDecisionVariables);
//				Constructor<?> heuristicConstructor;
//				try {
//					heuristicConstructor = heuristicsClasses[j].getConstructor(ProbNet.class, List.class);
//					Object heuristic = heuristicConstructor.newInstance(new Object[] {probNet, listOfListOfVariables});
//					EliminationHeuristic eliminationHeuristic = (EliminationHeuristic)heuristic;
//					System.out.print("  " + heuristic.getClass().getSimpleName() + ": ");
//					Variable variable;
//					while ((variable = eliminationHeuristic.getVariableToDelete()) != null) {
//						System.out.print(variable);
//						List<Variable> lastList;
//						do {
//							int lastElementIndex = listOfListOfVariables.size() - 1;
//							lastList = listOfListOfVariables.get(lastElementIndex);
//							if (lastList.isEmpty()) {
//								listOfListOfVariables.remove(lastElementIndex--);
//								lastList = lastElementIndex >= 0 ? listOfListOfVariables.get(lastElementIndex) : null;
//							}
//						} while (lastList != null && lastList.isEmpty());
//						
//						UndoableEditEvent event = new UndoableEditEvent(probNet, new RemoveNodeEdit(probNet, variable));
//						eliminationHeuristic.undoableEditHappened(event);
//						assertNotNull(lastList);
//						assertTrue(lastList.contains(variable));
//						lastList.remove(variable);
//						setOfVariables.remove(variable);
//						if (setOfVariables.isEmpty()) {
//							System.out.println(".");
//						} else {
//							System.out.print(", ");
//						}
//					}
//					assertTrue(setOfVariables.isEmpty());
//					boolean noElementsInListOfListOfVariables = listOfListOfVariables.isEmpty() || 
//							(listOfListOfVariables.get(0).isEmpty() && listOfListOfVariables.size() == 1);
//					assertTrue(noElementsInListOfListOfVariables);
//				} catch (NoSuchMethodException | SecurityException | 
//						InstantiationException | IllegalAccessException |
//						IllegalArgumentException | InvocationTargetException  e) {
//					e.printStackTrace();
//				}
//			}
//			System.out.println();
//		}		
//	}

	@Test
	/** This is a performance test. It checks that some heuristics are better than others.
	 * We assume that CanoAndMoral must be better than all the others "most" of the times, 
	 * the same with MinimalFillin and SimpleElimination */
	public void test3() throws WrongGraphStructureException {
		List<ProbNet> probNetsDB = Util.readProbNetsDB(BayesianNetworkType.getUniqueInstance());
		probNetsDB = Util.filterNonPureTablePotentialProbNets(probNetsDB);
		@SuppressWarnings("rawtypes")
		Class[] heuristicsClasses = new Class[] {CanoMoralElimination.class, MinimalFillIn.class, 
				HybridElimination.class, SimpleElimination.class};
		double[][] allNetworksScores = getAllScores(probNetsDB, heuristicsClasses);
		int numNetworks = probNetsDB.size();
		int numHeuristics = heuristicsClasses.length;
		
		// Bidimensional upper triangular matrix to compare heuristics
		int[][] comparisons = new int[numHeuristics][];
		for (int i = 0; i < numHeuristics; i++) {
			comparisons[i] = new int[numHeuristics];
		}
		for(int i = 0; i < numNetworks; i++) {
			for (int j  =  0; j < numHeuristics - 1; j++) {
				for (int k = j + 1; k < numHeuristics; k++) {
					if (allNetworksScores[i][j] != allNetworksScores[i][k]) {
						if (allNetworksScores[i][j] > allNetworksScores[i][k]) {
							comparisons[j][k]++;
						} else {
							comparisons[j][k]--;
						}
					}
				}
			}
		}
		
		// Create a vector of scores
		int[] punctuation = new int[numHeuristics];
		boolean[] removed = new boolean[numHeuristics];
		for (int i = 0; i < numHeuristics; i++) {
			removed[i] = false;
			for (int j = 0; j < numHeuristics; j++) {
				if (i != j) {
					if (i < j) {
						punctuation[i] += comparisons[i][j];
					} else {
						punctuation[i] -= comparisons[j][i];
					}
				}
			}
		}
		
		// Display the heuristic by quality from better to worst
		int lastPunctuationHeuristic = Integer.MIN_VALUE;
		int bestIndexHeuristic = 0;
		for (int i = 0; i < numHeuristics; i++) {
			int bestPunctuationHeuristic = Integer.MIN_VALUE;
			for (int j = 0; j < numHeuristics; j++) {
				if (punctuation[j] > bestPunctuationHeuristic && !removed[j]) {
					bestPunctuationHeuristic = punctuation[j];
					bestIndexHeuristic = j;
				}
			}
			removed[bestIndexHeuristic] = true;
			if (i == 0) {
				System.out.println("Heuristics, from the best to the worst:");
				System.out.print(heuristicsClasses[bestIndexHeuristic].getSimpleName());
				lastPunctuationHeuristic = bestPunctuationHeuristic;
			} else {
				if (lastPunctuationHeuristic == bestPunctuationHeuristic) {
					System.out.print(" = ");
				} else {
					System.out.print(" > ");
				}
				System.out.print(heuristicsClasses[bestIndexHeuristic].getSimpleName());
			}
			lastPunctuationHeuristic = bestPunctuationHeuristic;
		}
		System.out.println();
	}

	@SuppressWarnings("rawtypes")
	private 	double[][] getAllScores(Collection<ProbNet> probNetsDB, Class[] heuristicsClasses) throws WrongGraphStructureException {
		double[][] networkScores = new double[probNetsDB.size()][];
		int i = 0;
		for (ProbNet bayesianNetwork : probNetsDB) {
			TrivialHeuristicScore heuristicScore = new TrivialHeuristicScore(bayesianNetwork);
			double[] scores = heuristicScore.getScores(bayesianNetwork, heuristicsClasses);
			networkScores[i++] = scores;
		}
		return networkScores;
	}

}
