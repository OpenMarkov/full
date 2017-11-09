package org.openmarkov.inference.decompositionintosymmetricdans;

import org.junit.Test;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DANDecisionTreeEvaluation;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DANEvaluation;

public class DANDecisionTreeEvaluationInferenceTest extends DANEvaluationInferenceTest {

	@Override
	protected DANEvaluation buildDANEvaluation(ProbNet network) {
		DANEvaluation eval =  null;
		try {
			eval = new DANDecisionTreeEvaluation(network);
		} catch (NotEvaluableNetworkException e) {
			e.printStackTrace();
		}
		return eval;
	}
	
	//TODO This test belongs to DANEvaluationInferenceTest. The corresponding must be solved, and then, this method must be removed
	@Test
	public void testDANTest2TherapiesNoCostSymmetrizedOrderForced() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
	//	testMEUAndIntervention("decide-test-2therapies-no-cost-symmetrized-order-forced",9.39366,"Do test?","Result of test","Therapy");
	}

	//TODO This test belongs to DANEvaluationInferenceTest. The corresponding must be solved, and then, this method must be removed
		//@Test
	@Test
	public void testDANTest2TherapiesNoCostOrderForced() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		//testMEUAndIntervention("decide-test-2therapies-no-cost-order-forced",9.39366,"Do test?","Result of test","Therapy");
	}

	//TODO This test belongs to DANEvaluationInferenceTest. The corresponding must be solved, and then, this method must be removed
		//@Test
	@Test
	public void testDANTest2TherapiesNoCost() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		//testMEUAndIntervention("decide-test-2therapies-no-cost",9.39366,"Do test?","Result of test","Therapy");
	}

}
