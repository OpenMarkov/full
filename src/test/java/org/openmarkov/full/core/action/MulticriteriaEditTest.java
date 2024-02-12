/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.full.core.action;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.openmarkov.core.action.MulticriteriaEdit;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.inference.MulticriteriaOptions;
import org.openmarkov.core.model.network.Criterion;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.io.probmodel.reader.PGMXReader_0_2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class MulticriteriaEditTest {

	private ProbNet probNet;

	@BeforeAll public void setUp() throws Exception {
		this.probNet = getProbNet4Test();
		probNet.getPNESupport().setWithUndo(true);
	}

	@Test public void multiCriteriaOptionsTest() {

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
		} catch (DoEditException | NonProjectablePotentialException | WrongCriterionException e) {
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
		} catch (DoEditException | NonProjectablePotentialException | WrongCriterionException e) {
			e.printStackTrace();
			assertTrue(false);
		}

		probNet.getPNESupport().undo();

		assertTrue(probNet.getInferenceOptions().getMultiCriteriaOptions().getMainUnit().equals("Unit A"));
		assertTrue(!probNet.getDecisionCriteria().equals(decisionCriteria));

	}

	private ProbNet getProbNet4Test() {
		String bayesNetworkName = "networks/bn/BN-MulticriteriaEditTest.pgmx";
		
		URL res = getClass().getClassLoader().getResource(bayesNetworkName);
		File f = null;
		try {
			f = Paths.get(res.toURI()).toFile();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		String absolutePath = f.getAbsolutePath();

		// Load the Bayesian network
		PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
		ProbNet probNet = null;
		try {
			probNet = pgmxReader.loadProbNet(absolutePath);
		} catch (ParserException e) {
			e.printStackTrace();
		}
		return probNet;
	}
}
