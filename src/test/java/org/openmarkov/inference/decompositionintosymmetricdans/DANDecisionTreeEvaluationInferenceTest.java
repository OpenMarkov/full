package org.openmarkov.inference.decompositionintosymmetricdans;

import junit.framework.Assert;

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
	protected void testDANEvaluation(DANEvaluation eval, ProbNet network, double expectedEU,
			String... namesVariablesIntervention) {
		super.testDANEvaluation(eval, network, expectedEU, namesVariablesIntervention);
		Assert.assertNotNull(((DANDecisionTreeEvaluation) eval).getDecisionTree());
	}

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
	
	@Test
	public void testDANOnlyUtility() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		//testDANEvaluation("only-utility",10.0);
	}
	
	@Test
	public void testDANOneDecision() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		//testDANEvaluation("one-decision",87.4,"D");
	}
}
