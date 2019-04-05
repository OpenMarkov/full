package org.openmarkov.inference.decompositionintosymmetricdans;

import org.junit.Test;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DecompositionGenerateDecisionTree;

public class DecompositionGenerateDecisionTreeTest {
	
	
	@Test
	public void testOnlyTwoUtilitySV() throws IncompatibleEvidenceException, UnexpectedInferenceException,
			NodeNotFoundException, NotEvaluableNetworkException {
			
		testNetworkEvaluation("only-two-utility-sv", 5);
	}

	private void testNetworkEvaluation(String string, double eu) {
		// TODO Auto-generated method stub
		DecompositionGenerateDecisionTree eval;
		
	}

}
