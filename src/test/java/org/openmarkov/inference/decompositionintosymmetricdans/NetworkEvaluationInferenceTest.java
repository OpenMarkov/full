package org.openmarkov.inference.decompositionintosymmetricdans;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.StrategyTree;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.core.DANOperations;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.evaluation.DANEvaluation;

import junit.framework.Assert;

public abstract class NetworkEvaluationInferenceTest {

	public void testNetworkEvaluation(String networkName, double expectedEU, String... namesVariablesIntervention) {
		ProbNet network = loadNetwork(networkName);
		System.out.println("*** Evaluating network " + networkName + " ***");
		System.out.println();
		testNetworkEvaluation(network,expectedEU,namesVariablesIntervention);
	}
	
	public void testNetworkEvaluation(ProbNet network, double expectedEU, String... namesVariablesIntervention) {
		System.out.println();
		DANEvaluation eval = buildNetworkEvaluation(network);
		testDANEvaluation(eval, network, expectedEU, namesVariablesIntervention);
	}

	protected void testDANEvaluation(DANEvaluation eval, ProbNet network, double expectedEU,
			String... namesVariablesIntervention) {
		//DANExactAlgorithm dsd = new DANDecompositionAlgorithm();
		TablePotential globalUtility = null;
		globalUtility = eval.getUtility();

		//String strIntervention = globalUtility.interventions[0].toStringForGraphviz(network);
		Assert.assertEquals(expectedEU, globalUtility.getFirstValue(), 0.0001);
		StrategyTree[] inter = globalUtility.strategyTrees;
		if (inter != null && namesVariablesIntervention != null && namesVariablesIntervention.length > 0) {
			StrategyTree strategyTree = inter[0];
			String strIntervention = strategyTree.toStringForGraphviz(network);
			Assert.assertTrue(areEquals(getVariablesOfIntervention(strategyTree), namesVariablesIntervention));
		}
	}

	protected abstract ProbNet loadNetwork(String networkName);
	
	protected abstract DANEvaluation buildNetworkEvaluation(ProbNet network);



	private boolean areEquals(List<Variable> variables, String[] expectedNamesVariables) {
		return areEqualsListsOfStrings(getDifferentNamesVariables(variables), expectedNamesVariables);

	}

	private List<String> getDifferentNamesVariables(List<Variable> variables) {
		List<String> differentNames = new ArrayList<>();
		for (Variable var : variables) {
			String name = var.getName();
			if (!differentNames.contains(name)) {
				differentNames.add(name);
			}
		}
		return differentNames;
	}

	private boolean areEqualsListsOfStrings(List<String> namesVariablesIntervention, String[] expectedNamesVariables) {
		boolean areEqual = true;
		int varSize = namesVariablesIntervention.size();
		if (expectedNamesVariables.length != varSize) {
			areEqual = false;
		} else {
			String[] namesInVariables = new String[varSize];
			int i = 0;
			for (String var : namesVariablesIntervention) {
				namesInVariables[i] = var;
				i++;
			}
			areEqual = areEquals(namesInVariables, expectedNamesVariables);
		}
		return areEqual;

	}

	private List<Variable> getVariablesOfIntervention(StrategyTree inter) {
		List<Variable> variables = new ArrayList<>();

		if (inter != null) {
			variables.add(inter.getRootVariable());
			for (StrategyTree child : inter.getInterventionsChildren()) {
				variables = DANOperations.join(variables, getVariablesOfIntervention(child));
			}
		}
		return variables;

	}

	private boolean areEquals(String a[], String b[]) {
		return isSubset(a, b) && isSubset(b, a);
	}

	private boolean isSubset(String[] subsetCandidate, String[] set) {
		int subsetSize = subsetCandidate.length;
		boolean isSubset = true;
		for (int i = 0; i < subsetSize && isSubset; i++) {
			isSubset = isStringInList(subsetCandidate[i], set);
		}
		return isSubset;
	}

	private boolean isStringInList(String search, String[] list) {
		boolean contains = false;
		for (int i = 0; i < list.length && !contains; i++) {
			String str = list[i];
			contains = Objects.equals(str, search);
		}
		return contains;

	}
}
