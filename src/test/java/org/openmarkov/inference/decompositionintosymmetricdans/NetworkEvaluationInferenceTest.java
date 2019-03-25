package org.openmarkov.inference.decompositionintosymmetricdans;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.tree.TreeModel;

import org.openmarkov.core.dt.DecisionTreeBranch;
import org.openmarkov.core.dt.DecisionTreeElement;
import org.openmarkov.core.dt.DecisionTreeNode;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.StrategyTree;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.gui.window.dt.DecisionTree;
import org.openmarkov.gui.window.dt.DecisionTreeBranchPanel;
import org.openmarkov.gui.window.dt.DecisionTreeNodePanel;
import org.openmarkov.gui.window.dt.DecisionTreePanel;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.core.DANOperations;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.evaluation.DANDecisionTreeEvaluation;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.evaluation.DANEvaluation;

import junit.framework.Assert;

public abstract class NetworkEvaluationInferenceTest {

	public void testNetworkEvaluation(String networkName, double expectedEU, String... namesVariablesIntervention) throws NotEvaluableNetworkException {
		ProbNet network = loadNetwork(networkName);
		System.out.println("*** Evaluating network " + networkName + " ***");
		System.out.println();
		testNetworkEvaluation(network,expectedEU,namesVariablesIntervention);
	}
	
	public void testNetworkEvaluation(ProbNet network, double expectedEU, String... namesVariablesIntervention) throws NotEvaluableNetworkException {
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
	
	protected abstract DANEvaluation buildNetworkEvaluation(ProbNet network) throws NotEvaluableNetworkException;



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
	
	protected abstract DANEvaluation buildNetworkEvaluation(ProbNet network, boolean computeDecisionTreeForGUI) throws NotEvaluableNetworkException;
	
	public void testNetworkEvaluationAndDecisionTree(ProbNet network, double expectedEU, String... namesVariablesIntervention) {
		System.out.println();
		
		boolean computeDTValues []= {true, false};
		for (boolean computeDT: computeDTValues) {			
			DANEvaluation eval = null;
			try {
				eval = buildNetworkEvaluation(network, computeDT);
			} catch (NotEvaluableNetworkException e1) {
				e1.printStackTrace();
				Assert.fail();
			}
			testDANEvaluation(eval, network, expectedEU, namesVariablesIntervention);
			DecisionTreeNode dt = ((DANDecisionTreeEvaluation) eval).getDecisionTree();
			if (computeDT) {
				Assert.assertNotNull(dt);
			}
			else {
				Assert.assertNull(dt);
			}
			if (computeDT) {
				testDecisionTreeNode(dt, false);
				testDecisionTreeAfterLevelsExpansion(network, false);
			}
		}
	}
	
	protected void testDecisionTreeAfterLevelsExpansion(ProbNet network, boolean exploreZeroProbabilityBranches) {
		int maxNumberLevelsToExpandMore = 3;
		DecisionTreePanel dtPanel;
		
		try {
			dtPanel = new DecisionTreePanel(network);
			for (int i = 0; i < maxNumberLevelsToExpandMore; i++) {
				dtPanel.inferenceExpandNextLevel();				
				testDecisionTreeNode(dtPanel.getDecisionTreeNode(), exploreZeroProbabilityBranches);
			}
		} catch (NotEvaluableNetworkException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	protected void testDecisionTreeNode(DecisionTreeNode treeNode, boolean exploreZeroProbabilityBranches) {
		double deltaEquals = Math.pow(10, -6);
		if (treeNode.getNodeType().equals(NodeType.CHANCE)) {

			double totalProbability = 0;
			double weightedUtility = 0;
			for (DecisionTreeElement childElement : treeNode.getChildren()) {
				DecisionTreeBranch branch = (DecisionTreeBranch) childElement;
				double branchProbability = branch.getBranchProbability();
				
				//Test that the probability is between 0 and 1
				Assert.assertTrue(branchProbability >= -deltaEquals);
				Assert.assertTrue(branchProbability <= 1.0 + deltaEquals);
				
				totalProbability += branchProbability;
				weightedUtility += branchProbability * branch.getUtility();

				// Recursive call
				DecisionTreeNode childNode = branch.getChild();
				if (exploreZeroProbabilityBranches || branchProbability > deltaEquals) {
					testDecisionTreeNode(childNode, exploreZeroProbabilityBranches);
				}
			}

			// Test that the configurations are exhaustive (the probability of all the possible configurations sum 1)
			Assert.assertEquals(1.0, totalProbability, deltaEquals);

			// Test that the utility assigned to a chance node is the weighted sum of the utility of its configurations
			Assert.assertEquals(treeNode.getUtility(), weightedUtility, deltaEquals);

		} else if (treeNode.getNodeType().equals(NodeType.DECISION)) {
			double maxUtility = Double.MIN_VALUE;
			for (DecisionTreeElement childElement : treeNode.getChildren()) {
				DecisionTreeBranch branch = (DecisionTreeBranch) childElement;
				if (branch.getUtility() > maxUtility) {
					maxUtility = branch.getUtility();
				}

				// Recursive call
				DecisionTreeNode childNode = branch.getChild();
				testDecisionTreeNode(childNode, exploreZeroProbabilityBranches);
			}

			// Test that the utility assigned to a chance node is the max value of the utility of its configurations
			Assert.assertEquals(maxUtility, treeNode.getUtility(), deltaEquals);
		}
	}
}
