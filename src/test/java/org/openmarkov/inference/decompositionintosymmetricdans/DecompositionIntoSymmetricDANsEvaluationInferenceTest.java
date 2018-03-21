/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.inference.decompositionintosymmetricdans;

import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DANEvaluation;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DecompositionIntoSymmetricDANsEvaluation;

public class DecompositionIntoSymmetricDANsEvaluationInferenceTest extends DANEvaluationInferenceTest {

	@Override protected DANEvaluation buildDANEvaluation(ProbNet network) {
		DANEvaluation eval = null;
		try {
			eval = new DecompositionIntoSymmetricDANsEvaluation(network);
		} catch (NotEvaluableNetworkException e) {
			e.printStackTrace();
		}
		return eval;
	}

}
