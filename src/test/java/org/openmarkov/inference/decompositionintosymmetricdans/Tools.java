package org.openmarkov.inference.decompositionintosymmetricdans;

import java.io.InputStream;

import org.openmarkov.core.dt.DecisionTreeBranch;
import org.openmarkov.core.dt.DecisionTreeElement;
import org.openmarkov.core.dt.DecisionTreeNode;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.gui.window.dt.DecisionTreePanel;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DecisionTreeComputation;
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
					weightedUtility += branchProbability * (double) branch.getValuation();
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
				Assert.assertEquals(treeNode.getValuation(), weightedUtility, deltaEquals);
			}
	
		} else if (treeNode.getNodeType().equals(NodeType.DECISION) && !isCEA) {
			double maxUtility = Double.MIN_VALUE;
			for (DecisionTreeElement childElement : treeNode.getChildren()) {
				DecisionTreeBranch branch = (DecisionTreeBranch) childElement;
				double auxUtility = (double) branch.getValuation();
				if (auxUtility > maxUtility) {
					maxUtility = auxUtility;
				}
	
				// Recursive call
				DecisionTreeNode childNode = branch.getChild();
				testDecisionTreeNode(childNode, exploreZeroProbabilityBranches);
			}
	
			// Test that the utility assigned to a chance node is the max value of the
			// utility of its configurations
			Assert.assertEquals(maxUtility, treeNode.getValuation(), deltaEquals);
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
