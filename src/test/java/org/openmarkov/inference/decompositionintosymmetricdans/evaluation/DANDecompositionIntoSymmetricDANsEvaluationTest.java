/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.inference.decompositionintosymmetricdans.evaluation;

import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.evaluation.DANEvaluation;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.evaluation.DANDecompositionIntoSymmetricDANsEvaluation;

public class DANDecompositionIntoSymmetricDANsEvaluationTest extends DANEvaluationTest {

	@Override protected DANEvaluation buildNetworkEvaluation(ProbNet network) {
		DANEvaluation eval = null;
		eval = new DANDecompositionIntoSymmetricDANsEvaluation(network);
		return eval;
	}

}
