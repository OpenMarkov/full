package org.openmarkov.inference.decompositionintosymmetricdans;

import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.inference.tasks.CEAnalysis;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.CEADANDecisionTreeEvaluation;

public class CEADANDecisionTreeEvaluationInferenceTest extends CEADANEvaluationInferenceTest {

	@Override
	protected CEAnalysis buildCEAnalysis(ProbNet network) {
		CEAnalysis cea = null;
		try {
			cea = new CEADANDecisionTreeEvaluation(network);
		} catch (NotEvaluableNetworkException e) {
			e.printStackTrace();
		}
		return cea;
	}

}
