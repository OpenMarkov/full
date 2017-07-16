package org.openmarkov.inference.decompositionintosymmetricdans;

import org.openmarkov.inference.decompositionIntoSymmetricDANs.DANDecompositionAlgorithm;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DANExactAlgorithm;

public class DANDecompositionAlgorithmInferenceTest extends
		DANExactAlgorithmInferenceTest {

	@Override
	protected DANExactAlgorithm buildDANExactAlgorithm() {
		return new DANDecompositionAlgorithm();
	}

}
