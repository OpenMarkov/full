package org.openmarkov.inference.decompositionintosymmetricdans;

import junit.framework.Assert;
import org.junit.Test;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.inference.tasks.CEAnalysis;
import org.openmarkov.core.model.network.CEP;
import org.openmarkov.core.model.network.ProbNet;

public abstract class CEADANEvaluationInferenceTest {

	public void testCEADANEvaluation(String danName, int globalNumberOfCEPIntervals, double... expectedThreshods) {
		Tools t = new Tools();
		ProbNet network = t.loadDAN(danName);
		System.out.println("*** CEA with DAN " + danName + " ***");
		System.out.println();
		CEAnalysis eval = buildCEAnalysis(network);
		CEP cep = null;
		try {
			cep = eval.getCEP();
		} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(cep);
		Assert.assertEquals(globalNumberOfCEPIntervals, cep.getNumIntervals());
		int numThresholds = globalNumberOfCEPIntervals - 1;
		Assert.assertEquals(numThresholds, expectedThreshods.length);
		double[] obtainedThresholds = cep.getThresholds();
		for (int i = 0; i < numThresholds; i++) {
			Assert.assertEquals(expectedThreshods[i], obtainedThresholds[i], 0.1);
		}
	}

	protected abstract CEAnalysis buildCEAnalysis(ProbNet network);

	@Test public void testDANTest2Therapies()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		testCEADANEvaluation("decide-test-2therapies", 3, 11171.3, 33383.5);
	}

	@Test public void testDANOneDecisionCE()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		testCEADANEvaluation("one-decision-CE", 2, 1.333333333);
	}

}
