package org.openmarkov.inference.decompositionintosymmetricdans.evaluation;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmarkov.core.dt.DecisionTreeBranch;
import org.openmarkov.core.dt.DecisionTreeElement;
import org.openmarkov.core.dt.DecisionTreeNode;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;

import org.openmarkov.gui.window.dt.DecisionTree;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.evaluation.DANDecisionTreeEvaluation;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.evaluation.DANEvaluation;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.evaluation.IDDecisionTreeEvaluation;
import org.openmarkov.inference.decompositionintosymmetricdans.NetworkEvaluationInferenceTest;
import org.openmarkov.inference.decompositionintosymmetricdans.Tools;

//@Ignore
public class IDDecisionTreeEvaluationTest extends NetworkEvaluationInferenceTest {

	@Override
	protected DANEvaluation buildNetworkEvaluation(ProbNet network) throws NotEvaluableNetworkException {
		DANEvaluation eval = null;
		eval = new IDDecisionTreeEvaluation(network, true);
		return eval;
	}

	@Override
	protected ProbNet loadNetwork(String networkName) {
		Tools t = new Tools();
		return t.loadID(networkName);
	}
	
	@Test
	public void testIDOnlyUtility() throws IncompatibleEvidenceException, UnexpectedInferenceException,
			NodeNotFoundException, NotEvaluableNetworkException {
		testNetworkEvaluation("only-utility", 10.0);
	}

	@Test
	public void testIDOneChance() throws IncompatibleEvidenceException, UnexpectedInferenceException,
			NodeNotFoundException, NotEvaluableNetworkException {
		testNetworkEvaluation("one-chance", 83.7);
	}

	@Test
	public void testIDOneDecision() throws IncompatibleEvidenceException, UnexpectedInferenceException,
			NodeNotFoundException, NotEvaluableNetworkException {
		testNetworkEvaluation("one-decision", 87.4, "D");
	}

	@Test
	public void testIDNoKnowledge() throws IncompatibleEvidenceException, UnexpectedInferenceException,
			NodeNotFoundException, NotEvaluableNetworkException {
		testNetworkEvaluation("no-knowledge", 9.16, "D");
	}
	
	@Test
	public void testIDPerfectKnowledge() throws IncompatibleEvidenceException, UnexpectedInferenceException,
			NodeNotFoundException, NotEvaluableNetworkException {
		testNetworkEvaluation("perfect-knowledge", 9.72, "D","A");
	}

	@Test
	public void testIDTest2Therapies() throws IncompatibleEvidenceException, UnexpectedInferenceException,
			NodeNotFoundException, NotEvaluableNetworkException {
		testNetworkEvaluation("test-2therapies", 9.39366, "Test", "Therapy");
	}
	 

	@Override
	public void testNetworkEvaluation(ProbNet network, double expectedEU, String... namesVariablesIntervention) throws NotEvaluableNetworkException {
		// TODO Auto-generated method stub
		super.testNetworkEvaluation(network, expectedEU, namesVariablesIntervention);
		//TODO Test dt expansion
		//Tools.buildDecisionTreePanelAndExpandLevels(network);
	}

	@Test
	public void testIDTest2Therapies_Tree() {
		ProbNet network = loadNetwork("test-2therapies");
		try {
			DANDecisionTreeEvaluation eval = new DANDecisionTreeEvaluation(network, new EvidenceCase());
			testDecisionTreeNode(eval.getDecisionTree());
		} catch (NotEvaluableNetworkException e) {
			e.printStackTrace();
		}
	}

	public void testDecisionTreeNode(DecisionTreeNode treeNode) {
		double deltaEquals = Math.pow(10, -6);
		if (treeNode.getNodeType().equals(NodeType.CHANCE)) {

			double totalProbability = 0;
			double weightedUtility = 0;
			for (DecisionTreeElement childElement : treeNode.getChildren()) {
				DecisionTreeBranch branch = (DecisionTreeBranch) childElement;
				totalProbability += branch.getBranchProbability();
				weightedUtility += branch.getBranchProbability() * branch.getUtility();

				// Recursive call
				DecisionTreeNode childNode = branch.getChild();
				testDecisionTreeNode(childNode);
			}

			// Test that the configurations are exhaustive (the probability of all the possible configurations sum 1)
			Assert.assertEquals(1.0, totalProbability, deltaEquals);

			// Test that the utility assigned to a chance node is the weighted sum of the utility of its configurations
			Assert.assertEquals(treeNode.getUtility(), weightedUtility, deltaEquals);

		} else if (treeNode.getNodeType().equals(NodeType.DECISION)) {
			double maxUtility = 0;
			for (DecisionTreeElement childElement : treeNode.getChildren()) {
				DecisionTreeBranch branch = (DecisionTreeBranch) childElement;
				if (branch.getUtility() > maxUtility) {
					maxUtility = branch.getUtility();
				}

				// Recursive call
				DecisionTreeNode childNode = branch.getChild();
				testDecisionTreeNode(childNode);
			}

			// Test that the utility assigned to a chance node is the max value of the utility of its configurations
			Assert.assertEquals(maxUtility, treeNode.getUtility(), deltaEquals);
		}
	}
	

	

}
