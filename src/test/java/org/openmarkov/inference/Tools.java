package org.openmarkov.inference;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.openmarkov.core.dt.DecisionTreeBranch;
import org.openmarkov.core.dt.DecisionTreeElement;
import org.openmarkov.core.dt.DecisionTreeNode;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.StrategyTree;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.gui.window.dt.DecisionTreePanel;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DecisionTreeComputation;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.core.DANOperations;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.core.EvaluationDecisionTreeNode;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.evaluation.DANEvaluation;
import org.openmarkov.io.probmodel.reader.PGMXReader_0_2;

import junit.framework.Assert;

public class Tools {
	
	public ProbNet loadNetwork(String networkNameSuffix,String networkNamePrefix,String subfolderName) {
		String networkName = "networks/"+subfolderName+"/"+networkNamePrefix+"-" + networkNameSuffix + ".pgmx";
		InputStream file = getClass().getClassLoader().getResourceAsStream(networkName);

		PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
		ProbNetInfo probNetInfo = null;
		try {
			probNetInfo = pgmxReader.loadProbNetInfo(networkName, file);
		} catch (ParserException e) {
			e.printStackTrace();
		}
		return probNetInfo.getProbNet();
	}

	
	public ProbNet loadDAN(String nameSuffix) {
		return loadNetwork(nameSuffix,"DAN","dan");
	}
	
	public ProbNet loadID(String nameSuffix) {
		return loadNetwork(nameSuffix,"ID","id");
	}


	static List<String> getDifferentNamesVariables(List<Variable> variables) {
		List<String> differentNames = new ArrayList<>();
		for (Variable var : variables) {
			String name = var.getName();
			if (!differentNames.contains(name)) {
				differentNames.add(name);
			}
		}
		return differentNames;
	}


	public static void testEvaluationResults(ProbNet network, double expectedEU, TablePotential globalUtility,
			String... namesVariablesIntervention) {
		//String strIntervention = globalUtility.interventions[0].toStringForGraphviz(network);
		Assert.assertEquals(expectedEU, globalUtility.getFirstValue(), 0.0001);
		StrategyTree[] inter = globalUtility.strategyTrees;
		if (inter != null && namesVariablesIntervention != null && namesVariablesIntervention.length > 0) {
			StrategyTree strategyTree = inter[0];
			String strIntervention = strategyTree.toStringForGraphviz(network);
			Assert.assertTrue(areEquals(getVariablesOfIntervention(strategyTree), namesVariablesIntervention));
		}
	}


	static boolean areEqualsListsOfStrings(List<String> namesVariablesIntervention, String[] expectedNamesVariables) {
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


	static boolean isStringInList(String search, String[] list) {
		boolean contains = false;
		for (int i = 0; i < list.length && !contains; i++) {
			String str = list[i];
			contains = Objects.equals(str, search);
		}
		return contains;
	
	}


	static boolean isSubset(String[] subsetCandidate, String[] set) {
		int subsetSize = subsetCandidate.length;
		boolean isSubset = true;
		for (int i = 0; i < subsetSize && isSubset; i++) {
			isSubset = Tools.isStringInList(subsetCandidate[i], set);
		}
		return isSubset;
	}


	static boolean areEquals(String a[], String b[]) {
		return Tools.isSubset(a, b) && Tools.isSubset(b, a);
	}


	static List<Variable> getVariablesOfIntervention(StrategyTree inter) {
		List<Variable> variables = new ArrayList<>();
	
		if (inter != null) {
			variables.add(inter.getRootVariable());
			for (StrategyTree child : inter.getInterventionsChildren()) {
				variables = DANOperations.join(variables, getVariablesOfIntervention(child));
			}
		}
		return variables;
	
	}


	static boolean areEquals(List<Variable> variables, String[] expectedNamesVariables) {
		return Tools.areEqualsListsOfStrings(Tools.getDifferentNamesVariables(variables), expectedNamesVariables);
	
	}


	public static void testDecisionTreeNode(DecisionTreeNode<Double> treeNode, boolean exploreZeroProbabilityBranches) {
		
		boolean isCEA = !(treeNode.getClass() == EvaluationDecisionTreeNode.class);
		double deltaEquals = Math.pow(10, -6);
		if (treeNode.getNodeType().equals(NodeType.CHANCE)) {
	
			double totalProbability = 0;
			double weightedUtility = 0;
			for (DecisionTreeElement childElement : treeNode.getChildren()) {
				DecisionTreeBranch branch = (DecisionTreeBranch) childElement;
				double branchProbability = branch.getBranchProbability();
	
				// Test that the probability is between 0 and 1
				Assert.assertTrue(branchProbability >= -deltaEquals);
				Assert.assertTrue(branchProbability <= 1.0 + deltaEquals);
	
				totalProbability += branchProbability;
				if (!isCEA) {
					weightedUtility += branchProbability * (double) branch.getUtility();
				}
	
				// Recursive call
				DecisionTreeNode childNode = branch.getChild();
				if (exploreZeroProbabilityBranches || branchProbability > deltaEquals) {
					testDecisionTreeNode(childNode, exploreZeroProbabilityBranches);
				}
			}
	
			// Test that the configurations are exhaustive (the probability of all the
			// possible configurations sum 1)
			Assert.assertEquals(1.0, totalProbability, deltaEquals);
	
			// Test that the utility assigned to a chance node is the weighted sum of the
			// utility of its configurations
			if (!isCEA) {
				Assert.assertEquals(treeNode.getUtility(), weightedUtility, deltaEquals);
			}
	
		} else if (treeNode.getNodeType().equals(NodeType.DECISION) && !isCEA) {
			double maxUtility = Double.MIN_VALUE;
			for (DecisionTreeElement childElement : treeNode.getChildren()) {
				DecisionTreeBranch branch = (DecisionTreeBranch) childElement;
				double auxUtility = (double) branch.getUtility();
				if (auxUtility > maxUtility) {
					maxUtility = auxUtility;
				}
	
				// Recursive call
				DecisionTreeNode childNode = branch.getChild();
				testDecisionTreeNode(childNode, exploreZeroProbabilityBranches);
			}
	
			// Test that the utility assigned to a chance node is the max value of the
			// utility of its configurations
			Assert.assertEquals(maxUtility, treeNode.getUtility(), deltaEquals);
		}
	}


	protected static void testDecisionTreeAfterLevelsExpansion(ProbNet network, boolean exploreZeroProbabilityBranches) {
		int maxNumberLevelsToExpandMore = 3;
		DecisionTreePanel dtPanel;
		
		try {
			dtPanel = new DecisionTreePanel(network);
			for (int i = 0; i < maxNumberLevelsToExpandMore; i++) {
				dtPanel.inferenceExpandNextLevel();				
				Tools.testDecisionTreeNode(dtPanel.getDecisionTreeNode(), exploreZeroProbabilityBranches);
			}
		} catch (NotEvaluableNetworkException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}


	public static void testDecisionTree(ProbNet network, boolean computeDT, DecisionTreeComputation eval) {
		DecisionTreeNode dt = eval.getDecisionTree();
		if (computeDT) {
			Assert.assertNotNull(dt);
		}
		else {
			Assert.assertNull(dt);
		}
		if (computeDT) {
			Tools.testDecisionTreeNode(dt, false);
			Tools.testDecisionTreeAfterLevelsExpansion(network, false);
		}
	}

}
