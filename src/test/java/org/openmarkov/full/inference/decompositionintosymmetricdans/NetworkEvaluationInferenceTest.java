package org.openmarkov.full.inference.decompositionintosymmetricdans;

import org.junit.jupiter.api.Assertions;

import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.full.inference.Tools;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DecisionTreeComputation;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.evaluation.DANEvaluation;


public abstract class NetworkEvaluationInferenceTest {

	public void testNetworkEvaluation(String networkName, double expectedEU, String... namesVariablesIntervention) throws NotEvaluableNetworkException {
		ProbNet network = loadNetwork(networkName);
		System.out.println("*** Evaluating network " + networkName + " ***");
		System.out.println();
		testNetworkEvaluation(network,expectedEU,namesVariablesIntervention);
	}
	
	public void testNetworkEvaluation(ProbNet network, double expectedEU, String... namesVariablesIntervention) throws NotEvaluableNetworkException {
		System.out.println();
		DANEvaluation eval = buildNetworkEvaluation(network);
		testDANEvaluation(eval, network, expectedEU, namesVariablesIntervention);
	}

	protected void testDANEvaluation(DANEvaluation eval, ProbNet network, double expectedEU,
			String... namesVariablesIntervention) {
		TablePotential globalUtility = eval.getUtility();
		Tools.testEvaluationResults(network, expectedEU, globalUtility, namesVariablesIntervention);
	}

	protected abstract ProbNet loadNetwork(String networkName);
	
	protected abstract DANEvaluation buildNetworkEvaluation(ProbNet network) throws NotEvaluableNetworkException;



	protected abstract DANEvaluation buildNetworkEvaluation(ProbNet network, boolean computeDecisionTreeForGUI) throws NotEvaluableNetworkException;
	
	public void testNetworkEvaluationAndDecisionTree(ProbNet network, double expectedEU, String... namesVariablesIntervention) {
		System.out.println();
		
		boolean computeDTValues []= {true, false};
		for (boolean computeDT: computeDTValues) {			
			DANEvaluation eval = null;
			try {
				eval = buildNetworkEvaluation(network, computeDT);
			} catch (NotEvaluableNetworkException e1) {
				e1.printStackTrace();
				Assertions.fail();
			}
			testDANEvaluation(eval, network, expectedEU, namesVariablesIntervention);
			Tools.testDecisionTree(network, computeDT, (DecisionTreeComputation) eval);
		}
	}
	
	
	
}
