package org.openmarkov.inference.decompositionintosymmetricdans;

import org.openmarkov.inference.decompositionIntoSymmetricDANs.DANDecisionTreeAlgorithm;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DANExactAlgorithm;

public class DANDecisionTreeAlgorithmInferenceTest extends
		DANExactAlgorithmInferenceTest {

	@Override
	protected DANExactAlgorithm buildDANExactAlgorithm() {
		return new DANDecisionTreeAlgorithm();
	}

}
