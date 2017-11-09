package org.openmarkov.inference.decompositionintosymmetricdans;

import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DANEvaluation;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DecompositionIntoSymmetricDANsEvaluation;

public class DecompositionIntoSymmetricDANsEvaluationInferenceTest extends DANEvaluationInferenceTest {

	@Override
	protected DANEvaluation buildDANEvaluation(ProbNet network) {
		DANEvaluation eval =  null;
		try {
			eval = new DecompositionIntoSymmetricDANsEvaluation(network);
		} catch (NotEvaluableNetworkException e) {
			e.printStackTrace();
		}
		return eval;
	}



}
