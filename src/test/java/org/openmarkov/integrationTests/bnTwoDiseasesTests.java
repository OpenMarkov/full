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
import java.util.HashMap;
import java.util.List;

public class bnTwoDiseasesTests {

	private final String networkName = "networks/bn/BN-two-diseases.pgmx";

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
		List<Variable> variablesOfInterest = probNet.getVariables();
		try {
			vePropagation = new VEPropagation(probNet);
			vePropagation.setVariablesOfInterest(variablesOfInterest);
			vePropagation.setPreResolutionEvidence(preResolutionEvidence);
			vePropagation.setPostResolutionEvidence(postResolutionEvidence);

			HashMap<Variable, TablePotential> posteriorVales = vePropagation.getPosteriorValues();

			for (Variable variable : variablesOfInterest) {
				double[] expectedValues = new double[0];
				switch (variable.getName()) {
				case "Virus A":
					expectedValues = new double[] { 0.98, 0.02 };
					break;
				case "Virus B":
					expectedValues = new double[] { 0.99, 0.01 };
					break;
				case "Vaccination":
					expectedValues = new double[] { 0.2, 0.8 };
					break;
				case "Disease 1":
					expectedValues = new double[] { 0.9732, 0.0268 };
					break;
				case "Disease 2":
					expectedValues = new double[] { 0.9820, 0.0180 };
					break;
				case "Symptom":
					expectedValues = new double[] { 0.9483, 0.0517 };
					break;
				case "Anomaly":
					expectedValues = new double[] { 0.9725, 0.0275 };
					break;
				case "X-ray":
					expectedValues = new double[] { 0.9586, 0.0414 };
					break;
				case "Ecography":
					expectedValues = new double[] { 0.9278, 0.0722 };
					break;
				default:
				case "Sign":
					expectedValues = new double[] { 0.9715, 0.0285 };
					break;
				}
				Assert.assertArrayEquals(posteriorVales.get(variable).values, expectedValues, deltaEquals);
			}
		} catch (NotEvaluableNetworkException | IncompatibleEvidenceException e) {
			e.printStackTrace();
		}
	}

	@Test public void vePropagationWithPostResolutionEvidence1() {
		VEPropagation vePropagation;
		EvidenceCase postResolutionEvidence = new EvidenceCase();
		List<Variable> variablesOfInterest = probNet.getVariables();

		// New Finding: Disease 1 = present
		try {
			Finding finding = new Finding(probNet.getVariable("Disease 1"), 1);
			postResolutionEvidence.addFinding(finding);
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
				case "Virus A":
					expectedValues = new double[] { 0.3286, 0.6714 };
					break;
				case "Virus B":
					expectedValues = new double[] { 0.6640, 0.3360 };
					break;
				case "Vaccination":
					expectedValues = new double[] { 0.2, 0.8 };
					break;
				case "Disease 1":
					expectedValues = new double[] { 0, 1 };
					break;
				case "Disease 2":
					expectedValues = new double[] { 0.9820, 0.0180 };
					break;
				case "Symptom":
					expectedValues = new double[] { 0.0389, 0.9611 };
					break;
				case "Anomaly":
					expectedValues = new double[] { 0.9725, 0.0275 };
					break;
				case "X-ray":
					expectedValues = new double[] { 0.9586, 0.0414 };
					break;
				case "Ecography":
					expectedValues = new double[] { 0.9278, 0.0722 };
					break;
				default:
				case "Sign":
					expectedValues = new double[] { 0.3, 0.7 };
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
		List<Variable> variablesOfInterest = probNet.getVariables();

		try {
			// New Finding: Disease 1 = present
			Finding finding1 = new Finding(probNet.getVariable("Disease 1"), 1);
			postResolutionEvidence.addFinding(finding1);

			// New Finding: Disease 2 = present
			Finding finding2 = new Finding(probNet.getVariable("Disease 2"), 1);
			postResolutionEvidence.addFinding(finding2);

			// New Finding: X-ray = negative
			Finding finding3 = new Finding(probNet.getVariable("X-ray"), 0);
			postResolutionEvidence.addFinding(finding3);
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
				case "Virus A":
					expectedValues = new double[] { 0.3286, 0.6714 };
					break;
				case "Virus B":
					expectedValues = new double[] { 0.6640, 0.3360 };
					break;
				case "Vaccination":
					expectedValues = new double[] { 0.5556, 0.4444 };
					break;
				case "Disease 1":
					expectedValues = new double[] { 0, 1 };
					break;
				case "Disease 2":
					expectedValues = new double[] { 0, 1 };
					break;
				case "Symptom":
					expectedValues = new double[] { 0.0028, 0.9972 };
					break;
				case "Anomaly":
					expectedValues = new double[] { 0.0909, 0.9091 };
					break;
				case "X-ray":
					expectedValues = new double[] { 1, 0 };
					break;
				case "Ecography":
					expectedValues = new double[] { 0.2136, 0.7864 };
					break;
				case "Sign":
					expectedValues = new double[] { 0.3, 0.7 };
					break;

				}
				Assert.assertArrayEquals(posteriorVales.get(variable).values, expectedValues, deltaEquals);
			}
		} catch (NotEvaluableNetworkException | IncompatibleEvidenceException e) {
			e.printStackTrace();
		}
	}

}
