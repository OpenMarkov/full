/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.integrationTests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.inference.variableElimination.tasks.VEPropagation;
import org.openmarkov.io.probmodel.reader.PGMXReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class bnCatarnetTests {
	private final String networkName = "networks/bn/BN-catarnet.pgmx";

	// Delta parameter for Assert.Equals methods
	private final double deltaEquals = Math.pow(10, -4);

	private ProbNet probNet;
	private EvidenceCase preResolutionEvidence;

	@Before public void setUp() throws Exception {
		InputStream file = getClass().getClassLoader().getResourceAsStream(networkName);

		// Load the network: ID-decide-test
		PGMXReader pgmxReader = new PGMXReader();
		ProbNetInfo probNetInfo = null;
		try {
			probNetInfo = pgmxReader.loadProbNetInfo(networkName, file);
		} catch (ParserException e) {
			e.printStackTrace();
		}
		this.probNet = probNetInfo.getProbNet();
		if (probNetInfo.getEvidence().size() != 0) {
			this.preResolutionEvidence = probNetInfo.getEvidence().get(0);
		}
	}

	@Test public void vePropagationWithoutEvidence() {
		VEPropagation vePropagation;
		EvidenceCase postResolutionEvidence = new EvidenceCase();
		List<Variable> variablesOfInterest = new ArrayList<>();
		try {
			variablesOfInterest.add(probNet.getVariable("ganancia_av"));
			variablesOfInterest.add(probNet.getVariable("deslu_global_post"));
			variablesOfInterest.add(probNet.getVariable("deslu_pre_no_catar"));
		} catch (NodeNotFoundException e) {
			e.printStackTrace();
		}

		try {
			vePropagation = new VEPropagation(probNet);
			vePropagation.setVariablesOfInterest(variablesOfInterest);
			vePropagation.setPreResolutionEvidence(preResolutionEvidence);
			vePropagation.setPostResolutionEvidence(postResolutionEvidence);
			HashMap<Variable, TablePotential> posteriorVales = vePropagation.getPosteriorValues();

			for (Variable variable : variablesOfInterest) {
				double[] expectedValues = new double[0];
				switch (variable.getName()) {
				case "ganancia_av":
					expectedValues = new double[] { 0.0010, 0.0053, 0.0095, 0.3974, 0.4059, 0.1435, 0.0374 };
					break;
				case "deslu_global_post":
					expectedValues = new double[] { 0.7735, 0.0344, 0.0202, 0.1071, 0.0647 };
					break;
				case "deslu_pre_no_catar":
					expectedValues = new double[] { 0.9190, 0.0810 };
					break;
				}
				Assert.assertArrayEquals(posteriorVales.get(variable).values, expectedValues, deltaEquals);
			}
		} catch (NotEvaluableNetworkException | IncompatibleEvidenceException e) {
			e.printStackTrace();
		}
	}

	@Test public void vePropagationWithPostResolutionEvidence2() {
		VEPropagation vePropagation;
		EvidenceCase postResolutionEvidence = new EvidenceCase();
		List<Variable> variablesOfInterest = new ArrayList<>();
		try {
			variablesOfInterest.add(probNet.getVariable("ganancia_deslu"));
			variablesOfInterest.add(probNet.getVariable("deslu_global_post"));
			variablesOfInterest.add(probNet.getVariable("ruptura_caps_post"));
		} catch (NodeNotFoundException e) {
			e.printStackTrace();
		}
		try {
			// New Finding: ganancia_av = g3
			Finding finding1 = new Finding(probNet.getVariable("ganancia_av"), 6);
			postResolutionEvidence.addFinding(finding1);

			// New Finding: deslu_global_pre = ojo contral
			Finding finding2 = new Finding(probNet.getVariable("deslu_global_pre"), 3);
			postResolutionEvidence.addFinding(finding2);

		} catch (NodeNotFoundException | IncompatibleEvidenceException | InvalidStateException e) {
			e.printStackTrace();
		}

		try {
			vePropagation = new VEPropagation(probNet);
			vePropagation.setVariablesOfInterest(variablesOfInterest);
			vePropagation.setPreResolutionEvidence(preResolutionEvidence);
			vePropagation.setPostResolutionEvidence(postResolutionEvidence);
			HashMap<Variable, TablePotential> posteriorVales = vePropagation.getPosteriorValues();

			for (Variable variable : variablesOfInterest) {
				double[] expectedValues = new double[0];
				switch (variable.getName()) {
				case "ganancia_deslu":
					expectedValues = new double[] { 0.0009, 0.7776, 0.2215 };
					break;
				case "deslu_global_post":
					expectedValues = new double[] { 0.0231, 0.1460, 0.0546, 0.7741, 0.0022 };
					break;
				case "ruptura_caps_post":
					expectedValues = new double[] { 0.8508, 0.1492 };
					break;
				}
				Assert.assertArrayEquals(posteriorVales.get(variable).values, expectedValues, deltaEquals);
			}
		} catch (NotEvaluableNetworkException | IncompatibleEvidenceException e) {
			e.printStackTrace();
		}
	}

	@Test public void vePropagationIncompatibleEvidence() {
		VEPropagation vePropagation;
		EvidenceCase postResolutionEvidence = new EvidenceCase();
		List<Variable> variablesOfInterest = probNet.getVariables();

		try {
			// New Finding: ganancia_av = g3
			Finding finding1 = new Finding(probNet.getVariable("ganancia_av"), 6);
			postResolutionEvidence.addFinding(finding1);

			// New Finding: deslu_global_pre = ojo contral
			Finding finding2 = new Finding(probNet.getVariable("deslu_global_pre"), 3);
			postResolutionEvidence.addFinding(finding2);

			// New Finding: av_pre = (0.7, 1] --> Incompatible evidence!
			Finding finding3 = new Finding(probNet.getVariable("av_pre"), 3);
			postResolutionEvidence.addFinding(finding3);
		} catch (NodeNotFoundException | IncompatibleEvidenceException | InvalidStateException e) {
			e.printStackTrace();
		}

		boolean incompatibleEvidenceExceptionOcurred = false;
		try {
			vePropagation = new VEPropagation(probNet);
			vePropagation.setVariablesOfInterest(variablesOfInterest);
			vePropagation.setPreResolutionEvidence(preResolutionEvidence);
			vePropagation.setPostResolutionEvidence(postResolutionEvidence);
			vePropagation.getPosteriorValues();
		} catch (NotEvaluableNetworkException e) {
			e.printStackTrace();
		} catch (IncompatibleEvidenceException e) {
			incompatibleEvidenceExceptionOcurred = true;
		}

		Assert.assertTrue(incompatibleEvidenceExceptionOcurred);
	}

}
