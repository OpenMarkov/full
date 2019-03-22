package org.openmarkov.inference.decompositionintosymmetricdans.evaluation;

import org.junit.Test;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.evaluation.DANEvaluation;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.evaluation.IDDecisionTreeEvaluation;
import org.openmarkov.inference.decompositionintosymmetricdans.NetworkEvaluationInferenceTest;
import org.openmarkov.inference.decompositionintosymmetricdans.Tools;

//@Ignore
public class IDDecisionTreeEvaluationTest extends NetworkEvaluationInferenceTest {

	@Override
	protected DANEvaluation buildNetworkEvaluation(ProbNet network) throws NotEvaluableNetworkException {
		return buildNetworkEvaluation(network, true);
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
		testNetworkEvaluationAndDecisionTree(network, expectedEU, namesVariablesIntervention);
	}

	@Test
	public void testIDTest2Therapies_Tree() {
		ProbNet network = loadNetwork("test-2therapies");
		try {
            IDDecisionTreeEvaluation eval = new IDDecisionTreeEvaluation(network, Integer.MAX_VALUE, true, new EvidenceCase());
			testDecisionTreeNode(eval.getDecisionTree(), false);
		} catch (NotEvaluableNetworkException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected DANEvaluation buildNetworkEvaluation(ProbNet network, boolean computeDecisionTreeForGUI)
			throws NotEvaluableNetworkException {
		return new IDDecisionTreeEvaluation(network, computeDecisionTreeForGUI);
	}

	
	

	

}
