package org.openmarkov.inference.decompositionintosymmetricdans;

import org.openmarkov.core.dt.DecisionTreeNode;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.inference.tasks.CEAnalysis;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.CEADANDecisionTreeEvaluation;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DANDecisionTreeEvaluation;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DANEvaluation;

import junit.framework.Assert;

public class CEADANDecisionTreeEvaluationInferenceTest extends CEADANEvaluationInferenceTest {

	@Override
	protected CEAnalysis buildCEAnalysis(ProbNet network) {
		return buildCEAnalysis(network, true);
	}
	
	
	protected CEAnalysis buildCEAnalysis(ProbNet network, boolean computeDTForGUI) {
		CEAnalysis cea = null;
		try {
			cea = new CEADANDecisionTreeEvaluation(network, computeDTForGUI);
		} catch (NotEvaluableNetworkException e) {
			e.printStackTrace();
		}
		return cea;
	}

	@Override
	public void testCEADANEvaluation(String danName, int globalNumberOfCEPIntervals, double... expectedThreshods) {
		Tools t = new Tools();
		ProbNet network = t.loadDAN(danName);
		System.out.println("*** CEA with DAN " + danName + " ***");
		System.out.println();
		boolean computeDTValues[] = {true, false};
		for (boolean computeDT: computeDTValues) {
			CEAnalysis eval = buildCEAnalysis(network, computeDT);
			testCEADANEvaluation(globalNumberOfCEPIntervals, eval, expectedThreshods);
			DecisionTreeNode dt = ((CEADANDecisionTreeEvaluation) eval).getDecisionTree();
			if (computeDT) {
				Assert.assertNotNull(dt);
			}
			else {
				Assert.assertNull(dt);
			}
		}
	}
	
	

}
