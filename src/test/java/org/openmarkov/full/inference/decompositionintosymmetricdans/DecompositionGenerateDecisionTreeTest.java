package org.openmarkov.full.inference.decompositionintosymmetricdans;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.inference.algorithm.decompositionIntoSymmetricDANs.DecompositionGenerateDecisionTree;

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
