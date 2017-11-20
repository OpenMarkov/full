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
	public void testDANKing() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		//TODO
		//This test works now with DANDecisionTreeEvaluation, but it takes 52 seconds in the core i7 laptop borrowed from Miguel.
		//That's why I have decided to comment it
		//testDANEvaluation("king",7.73);
	}
	
}
