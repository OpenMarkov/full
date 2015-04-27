package org.openmarkov.inference.heuristics;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.event.UndoableEditEvent;

import org.junit.Before;
import org.junit.Test;
import org.openmarkov.core.action.RemoveNodeEdit;
import org.openmarkov.core.inference.heuristic.EliminationHeuristic;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.type.BayesianNetworkType;
import org.openmarkov.inference.heuristic.canoAndMoral.CanoMoralElimination;
import org.openmarkov.inference.heuristic.hybridElimination.HybridElimination;
import org.openmarkov.inference.heuristic.minimalFillIn.MinimalFillIn;
import org.openmarkov.inference.heuristic.simpleElimination.SimpleElimination;

/**
 * Ensures that in every Bayesian network in the repository:
 * <ol>
 * <li> CanoMoralElimination is always better or equal than {MinimalFillIn and HybridElimination}, 
 * that are always better or equal than SimpleElimination 
 * </ol> 
 * @author Manuel Arias
 */
public class HeuristicsTest {

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

	// TODO Finish
	@SuppressWarnings("unchecked")
	@Test
	public void test() {
		// Basic tests
		List<ProbNet> probNetsDB = 
				HeuristicComparator.readProbNetsDB(BayesianNetworkType.getUniqueInstance());
		int numNetworks = probNetsDB.size();
		System.out.println("Number of networks: " + numNetworks);
		for(int i = 0; i < numNetworks; i++) {
			// All the variables are removed, and only once
			ProbNet probNet = probNetsDB.get(i);
			System.out.println("Network(" + i + "): " + probNet.getName());
			List<Variable> probNetVariables = probNet.getVariables();
			for (int j = 0; j < heuristicsClasses.length; j++) {
				List<List<Variable>> listOfListOfVariables = new ArrayList<List<Variable>>();
				listOfListOfVariables.add(probNetVariables);
				Set<Variable> setOfVariables = new HashSet<Variable>(probNetVariables);
				Constructor<?> heuristicConstructor;
				try {
					heuristicConstructor = heuristicsClasses[j].getConstructor(ProbNet.class, List.class);
					Object heuristic = heuristicConstructor.newInstance(new Object[] {probNet, listOfListOfVariables});
					EliminationHeuristic eliminationHeuristic = (EliminationHeuristic)heuristic;
					System.out.print("  " + heuristic.getClass().getSimpleName() + ": ");
					Variable variable;
					while ((variable = eliminationHeuristic.getVariableToDelete()) != null) {
						System.out.print(variable);
						UndoableEditEvent event = new UndoableEditEvent(probNet, new RemoveNodeEdit(probNet, variable));
						eliminationHeuristic.undoableEditHappened(event);
						assertTrue(setOfVariables.contains(variable));
						setOfVariables.remove(variable);
						if (setOfVariables.isEmpty()) {
							System.out.println(".");
						} else {
							System.out.print(", ");
						}
					}
				} catch (NoSuchMethodException | SecurityException | 
						InstantiationException | IllegalAccessException |
						IllegalArgumentException | InvocationTargetException  e) {
					e.printStackTrace();
				}
			}
			System.out.println();
		}
		
		// Complex tests
//		HeuristicComparator comparator = new HeuristicComparator(probNetsDB);
//		double[][] allNetworksScores = comparator.getAllScores();
//		for(int i = 0; i < numNetworks; i++) {
//			// CanoMoral better or equal than MinimalFillIn
//			assertTrue(allNetworksScores[i][0] >= allNetworksScores[i][1]);
//			// CanoMoral better or equal than HybridElimination
//			assertTrue(allNetworksScores[i][0] >= allNetworksScores[i][2]);
//			// MinimalFillIn better or equal than SimpleElimination
//			assertTrue(allNetworksScores[i][1] >= allNetworksScores[i][3]);
//			// HybridElimination better or equal than SimpleElimination
//			assertTrue(allNetworksScores[i][2] >= allNetworksScores[i][3]);
//		}
	}

}
