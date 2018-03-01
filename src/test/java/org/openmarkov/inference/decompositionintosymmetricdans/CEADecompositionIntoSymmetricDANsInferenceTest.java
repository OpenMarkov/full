package org.openmarkov.inference.decompositionintosymmetricdans;

import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.inference.tasks.CEAnalysis;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.CEADecompositionIntoSymmetricDANsEvaluation;

public class CEADecompositionIntoSymmetricDANsInferenceTest extends CEADANEvaluationInferenceTest {

	protected CEAnalysis buildCEAnalysis(ProbNet network) {
		CEAnalysis cea = null;
		try {
			cea = new CEADecompositionIntoSymmetricDANsEvaluation(network);
		} catch (NotEvaluableNetworkException e) {
			e.printStackTrace();
		}
		return cea;
	}

}
