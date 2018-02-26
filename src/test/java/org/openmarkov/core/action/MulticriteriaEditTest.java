/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.action;

import org.junit.Before;
import org.junit.Test;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.inference.MulticriteriaOptions;
import org.openmarkov.core.model.network.Criterion;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.io.probmodel.reader.PGMXReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class MulticriteriaEditTest {

	private ProbNet probNet;
	
	@Before
	public void setUp() throws Exception {
		this.probNet = getProbNet4Test();
		probNet.getPNESupport().setWithUndo(true);
	}

	@Test
	public void multiCriteriaOptionsTest() {

		MulticriteriaOptions multicriteriaOptions = new MulticriteriaOptions();
		multicriteriaOptions.setMainUnit("Unit A");
		multicriteriaOptions.setMulticriteriaType(MulticriteriaOptions.Type.UNICRITERION);
		
		List<Criterion> decisionCriteria = new ArrayList<>();
		Criterion criterion1 = new Criterion("Criterion A");
		decisionCriteria.add(criterion1);
		probNet.setDecisionCriteria(decisionCriteria);
		MulticriteriaEdit edit = new MulticriteriaEdit(probNet, decisionCriteria, multicriteriaOptions);
		
		try {
			probNet.getPNESupport().doEdit(edit);
			assertTrue(probNet.getInferenceOptions().getMultiCriteriaOptions().getMainUnit().equals("Unit A"));
			assertTrue(probNet.getDecisionCriteria().equals(decisionCriteria));
		} catch (DoEditException | NonProjectablePotentialException
				| WrongCriterionException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		

		MulticriteriaOptions multicriteriaOptions2 = new MulticriteriaOptions();
		multicriteriaOptions2.setMainUnit("Unit B");
		multicriteriaOptions2.setMulticriteriaType(MulticriteriaOptions.Type.COST_EFFECTIVENESS);
		List<Criterion> decisionCriteria2 = new ArrayList<>();
		Criterion criterion2 = new Criterion("Criterion B");
		decisionCriteria2.add(criterion2);
		probNet.setDecisionCriteria(decisionCriteria2);
		
		MulticriteriaEdit edit2 = new MulticriteriaEdit(probNet, decisionCriteria2, multicriteriaOptions2);
		try {
			probNet.getPNESupport().doEdit(edit2);
			assertTrue(probNet.getInferenceOptions().getMultiCriteriaOptions().getMainUnit().equals("Unit B"));
			assertTrue(probNet.getDecisionCriteria().equals(decisionCriteria2));
		} catch (DoEditException | NonProjectablePotentialException
				| WrongCriterionException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
		probNet.getPNESupport().undo();
		
		assertTrue(probNet.getInferenceOptions().getMultiCriteriaOptions().getMainUnit().equals("Unit A"));
		assertTrue(!probNet.getDecisionCriteria().equals(decisionCriteria));
		
		
	}
	
	

	private ProbNet getProbNet4Test() {
		String bayesNetworkName = "networks/bn/BN-MulticriteriaEditTest.pgmx";
		InputStream file = getClass().getClassLoader ().
				getResourceAsStream (bayesNetworkName);

		// Load the Bayesian network
		PGMXReader pgmxReader = new PGMXReader();
		ProbNet probNet = null;
		try {
			probNet = pgmxReader.loadProbNet(bayesNetworkName, file);
		} catch (ParserException e) {
			e.printStackTrace();
		}
		return probNet;
	}
}
