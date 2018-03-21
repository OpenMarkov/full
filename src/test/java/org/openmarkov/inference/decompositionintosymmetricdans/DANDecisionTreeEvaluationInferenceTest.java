/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.inference.decompositionintosymmetricdans;

import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DANDecisionTreeEvaluation;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DANEvaluation;

public class DANDecisionTreeEvaluationInferenceTest extends DANEvaluationInferenceTest {

	@Override protected void testDANEvaluation(DANEvaluation eval, ProbNet network, double expectedEU,
			String... namesVariablesIntervention) {
		super.testDANEvaluation(eval, network, expectedEU, namesVariablesIntervention);
		Assert.assertNotNull(((DANDecisionTreeEvaluation) eval).getDecisionTree());
	}

	@Override protected DANEvaluation buildDANEvaluation(ProbNet network) {
		DANEvaluation eval = null;
		try {
			eval = new DANDecisionTreeEvaluation(network);
		} catch (NotEvaluableNetworkException e) {
			e.printStackTrace();
		}
		return eval;
	}

	//Next tests are commented because running them very often takes too much time
	@Ignore("Old DAN evaluation") @Test public void testDANKingNobleDescentYesFirstTask1()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		//testDANEvaluation("king-noble-descent-yes-first-task-1",9.03);

	}

	@Ignore("Old DAN evaluation") @Test public void testDANKingNobleDescentYesFirstTask1SecondTask2()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		//testDANEvaluation("king-noble-descent-yes-first-task-1-second-task-2",9.03);
	}

	@Ignore("Old DAN evaluation") @Test public void testDANKingNobleDescentNo()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		//testDANEvaluation("king-noble-descent-no",6.43);

	}

	@Ignore("Old DAN evaluation") @Test public void testDANKingNobleDescentYes()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		//testDANEvaluation("king-noble-descent-yes",9.03);
	}

	@Ignore("Old DAN evaluation") @Test public void testDANKing()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		//testDANEvaluation("king",7.73);
	}

	
	/*	
	@Test
	public void testDANKing() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		//TODO
		//This test works now with DANDecisionTreeEvaluation, but it takes 52 seconds in the core i7 laptop borrowed from Miguel.
		//That's why I have decided to comment it
		//testDANEvaluation("king",7.73);
	}
	*/
}
