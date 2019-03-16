/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.inference.decompositionintosymmetricdans.evaluation;

import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmarkov.core.dt.DecisionTreeBranch;
import org.openmarkov.core.dt.DecisionTreeElement;
import org.openmarkov.core.dt.DecisionTreeNode;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.gui.window.dt.DecisionTree;
import org.openmarkov.gui.window.dt.DecisionTreeBranchPanel;
import org.openmarkov.gui.window.dt.DecisionTreeModel;
import org.openmarkov.gui.window.dt.DecisionTreePanel;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.evaluation.DANDecisionTreeEvaluation;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.evaluation.DANEvaluation;
//@Ignore
public class DANDecisionTreeEvaluationTest extends DANEvaluationTest {
	

	
	
	@Override public void testNetworkEvaluation(ProbNet network, double expectedEU, String... namesVariablesIntervention) {
		testNetworkEvaluationAndDecisionTree(network,expectedEU,namesVariablesIntervention);
	}


	
	
	

	protected DANEvaluation buildNetworkEvaluation(ProbNet network, boolean computeDecisionTreeForGUI) throws NotEvaluableNetworkException {
		DANEvaluation eval = null;
		eval = new DANDecisionTreeEvaluation(network, computeDecisionTreeForGUI);
		return eval;
	}

	//Next tests are commented because running them very often takes too much time
	@Ignore("Old DAN evaluation") @Test public void testDANKingNobleDescentYesFirstTask1()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		//testDANEvaluation("king-noble-descent-yes-first-task-1",9.03);

	}

	@Ignore("Old DAN evaluation") @Test public void testDANKingNobleDescentYesFirstTask1SecondTask2()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		//testDANEvaluation("king-noble-descent-yes-first-task-1-second-task-2",9.03);
	}

	@Ignore("Old DAN evaluation") @Test public void testDANKingNobleDescentNo()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		//testDANEvaluation("king-noble-descent-no",6.43);

	}

	@Ignore("Old DAN evaluation") @Test public void testDANKingNobleDescentYes()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		//testDANEvaluation("king-noble-descent-yes",9.03);
	}

	@Ignore("Old DAN evaluation") @Test public void testDANKing()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		//testDANEvaluation("king",7.73);
	}





	@Override
	protected DANEvaluation buildNetworkEvaluation(ProbNet network) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/*	
	@Test
	public void testDANKing() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		//TODO
		//This test works now with DANDecisionTreeEvaluation, but it takes 52 seconds in the core i7 laptop borrowed from Miguel.
		//That's why I have decided to comment it
		//testDANEvaluation("king",7.73);
	}
	*/
	

}
