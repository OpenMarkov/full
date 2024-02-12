package org.openmarkov.full.inference;

import org.junit.jupiter.api.Test;

import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.full.core.inference.InferenceTestsTools;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNet;

public class IDResolutionAndPropagationTest {

	private ProbNet loadID(String nameSuffix) {
		Tools t = new Tools();
		return t.loadID(nameSuffix);
	}
	
	private void testBasicInferenceNoCEA(String nameSuffix, boolean checkStrategyTree) {
		try {
			testResolutionAndPropagation(loadID(nameSuffix),null,checkStrategyTree);
		} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testIDOnlyDecisionNoUtility() {
		testBasicInferenceNoCEA("only-decision-no-utility", false);
	}
	
	@Test
	public void testIDThreeDecTwoUtil() {
		testBasicInferenceNoCEA("three-dec-two-util", true);
	}
	
	@Test
	public void testIDOnlyImposedUniformDecisionNoUtility() {
		testBasicInferenceNoCEA("only-imposed-uniform-dec-no-util", false);
	}
	
	@Test
	public void testIDOnlyImposedDecisionAndChanceNoUtility() {
		testBasicInferenceNoCEA("only-imposed-decision-and-chance-no-utility", false);
	}
	
	private static void testResolutionAndPropagation(ProbNet probNet, EvidenceCase preResolutionEvidence, boolean checkStrategyTree)
			throws NotEvaluableNetworkException, IncompatibleEvidenceException, UnexpectedInferenceException {
		InferenceTestsTools.testResolveNetwork(probNet, preResolutionEvidence, checkStrategyTree);

		// TODO - Check propagate errors
		InferenceTestsTools.testPropagateNetwork(probNet, probNet.getVariables(), preResolutionEvidence);
	}
	
}
