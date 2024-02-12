package org.openmarkov.full.inference.decompositionintosymmetricdans.ceanalysis;

import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.inference.MulticriteriaOptions;
import org.openmarkov.core.inference.MulticriteriaOptions.Type;
import org.openmarkov.core.inference.tasks.CEAnalysis;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.full.inference.Tools;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DecisionTreeComputation;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.ceanalysis.DANDecisionTreeCEA;

public class DANDecisionTreeCEATest extends DANCEATest {

	@Override
	protected CEAnalysis buildCEAnalysis(ProbNet network) throws NotEvaluableNetworkException {
		return buildCEAnalysis(network, true);
	}
	
	
	protected CEAnalysis buildCEAnalysis(ProbNet network, boolean computeDTForGUI) throws NotEvaluableNetworkException {
		CEAnalysis cea = null;
		cea = new DANDecisionTreeCEA(network, computeDTForGUI);
		return cea;
	}

	@Override
	public void testCEADANEvaluation(String danName, int globalNumberOfCEPIntervals, double... expectedThreshods) throws NotEvaluableNetworkException {
		Tools t = new Tools();
		ProbNet network = t.loadDAN(danName);
		MulticriteriaOptions options = new MulticriteriaOptions();
		options.setMulticriteriaType(Type.COST_EFFECTIVENESS);
		network.getInferenceOptions().setMultiCriteriaOptions(options);
		System.out.println("*** CEA with DAN " + danName + " ***");
		System.out.println();
		boolean computeDTValues[] = {true, false};
		for (boolean computeDT: computeDTValues) {
			CEAnalysis eval = buildCEAnalysis(network, computeDT);
			testCEADANEvaluation(globalNumberOfCEPIntervals, eval, expectedThreshods);
			Tools.testDecisionTree(network, computeDT, (DecisionTreeComputation) eval);
		}
	}
	
	

}
