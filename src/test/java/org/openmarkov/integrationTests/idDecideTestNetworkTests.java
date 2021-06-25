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
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.inference.tasks.OptimalPolicies;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.modelUncertainty.AxisVariation;
import org.openmarkov.core.model.network.modelUncertainty.DeterministicAxisVariationType;
import org.openmarkov.core.model.network.modelUncertainty.SystematicSampling;
import org.openmarkov.core.model.network.modelUncertainty.UncertainParameter;
import org.openmarkov.core.model.network.potential.StrategyTree;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDBranch;
import org.openmarkov.inference.variableElimination.tasks.VEEvaluation;
import org.openmarkov.inference.variableElimination.tasks.VEExpectedUtilityDecision;
import org.openmarkov.inference.variableElimination.tasks.VEOptimalIntervention;
import org.openmarkov.inference.variableElimination.tasks.VESensAnTornadoSpider;
import org.openmarkov.io.probmodel.reader.PGMXReader_0_2;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

public abstract class idDecideTestNetworkTests extends IDNetworkTests {

	

	@Test public void veResolutionTestWithoutEvidence() {
		VEEvaluation veEvaluation;
		try {
			veEvaluation = new VEEvaluation(probNet);
			veEvaluation.setPreResolutionEvidence(preResolutionEvidence);
			TablePotential utility = veEvaluation.getUtility();
			Assert.assertEquals(utility.getValues()[0], 9.3289, deltaEquals);
		} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
			e.printStackTrace();
		}
	}

	@Test public void veResolutionTestWithEvidences() {
		EvidenceCase evidenceCase = new EvidenceCase();
		Variable disease = null;
		Variable doTest = null;
		VEEvaluation veEvaluation;
		Finding finding;
		Finding secondFinding;

		// First evidence - Finding -> Disease = absent
		try {
			disease = probNet.getVariable("Disease");
			finding = new Finding(disease, 0);
			evidenceCase.addFinding(finding);
			veEvaluation = new VEEvaluation(probNet);
			veEvaluation.setPreResolutionEvidence(evidenceCase);
			TablePotential utility = veEvaluation.getUtility();
			Assert.assertEquals(utility.getValues()[0], 10, deltaEquals);

		} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException | NodeNotFoundException | InvalidStateException e) {
			e.printStackTrace();
		}

		// Second evidence - Finding -> Disease = present
		try {
			evidenceCase = new EvidenceCase();
			disease = probNet.getVariable("Disease");
			// Set disease as present
			finding = new Finding(disease, 1);
			evidenceCase.addFinding(finding);
			veEvaluation = new VEEvaluation(probNet);
			veEvaluation.setPreResolutionEvidence(evidenceCase);
			TablePotential utility = veEvaluation.getUtility();
			Assert.assertEquals(utility.getValues()[0], 7.25, deltaEquals);

		} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException | NodeNotFoundException | InvalidStateException e) {
			e.printStackTrace();
		}

		// Third evidence - Multiple findings -> Disease = present; Do test? = yes
		try {
			evidenceCase = new EvidenceCase();
			disease = probNet.getVariable("Disease");
			doTest = probNet.getVariable("Do test?");

			// Set Disease = present
			finding = new Finding(disease, 1);
			evidenceCase.addFinding(finding);

			// Set Do Test? = yes
			secondFinding = new Finding(doTest, 1);
			evidenceCase.addFinding(secondFinding);

			veEvaluation = new VEEvaluation(probNet);
			veEvaluation.setPreResolutionEvidence(evidenceCase);
			TablePotential utility = veEvaluation.getUtility();
			Assert.assertEquals(utility.getValues()[0], 7.05, deltaEquals);

		} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException | NodeNotFoundException | InvalidStateException e) {
			e.printStackTrace();
		}
	}

	@Test public void veOptimalPolicyTest() {
		OptimalPolicies veOptimalPolicy;
		try {
			Variable decisionVariable = probNet.getVariable("Therapy");
			veOptimalPolicy = new VEEvaluation(probNet);
			TablePotential optimalPolicy = (TablePotential) veOptimalPolicy.getOptimalPolicy(decisionVariable);
			double[] expectedValues = { 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1 };
			Assert.assertArrayEquals(optimalPolicy.getValues(), expectedValues, deltaEquals);
		} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException | NodeNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Test public void veExpectedUtilityTest() {
		VEExpectedUtilityDecision veExpectedUtilityDecision;
		try {
			Variable decisionVariable = probNet.getVariable("Therapy");
			veExpectedUtilityDecision = new VEExpectedUtilityDecision(probNet, decisionVariable);
			TablePotential expectedUtility = veExpectedUtilityDecision.getExpectedUtility();
			double[] expectedValues = { 9.16, 8.11, -0.2, -0.95, 0.0, -0.75, 9.7107227, 8.03512, 0.0, -0.75, 4.810443,
					7.2184073 };
			Assert.assertArrayEquals(expectedUtility.getValues(), expectedValues, deltaEquals);
		} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException | NodeNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Test public void veOptimalIntervention() {
		VEOptimalIntervention veOptimalIntervention;
		try {
			veOptimalIntervention = new VEOptimalIntervention(probNet, preResolutionEvidence);
			StrategyTree optimalStrategyTree = veOptimalIntervention.getOptimalIntervention();

			Variable doTestVariable = probNet.getVariable("Do test?");
			Assert.assertTrue(optimalStrategyTree.getRootVariable().equals(doTestVariable));
			Assert.assertTrue(veOptimalIntervention.getOptimalIntervention().getBranches().size() == 1);

			TreeADDBranch branchDoTestYes = veOptimalIntervention.getOptimalIntervention().getBranches().get(0);
			Assert.assertTrue(branchDoTestYes.getStates().get(0).getName().equals("yes"));

			StrategyTree subStrategyTree = (StrategyTree) branchDoTestYes.getPotential();
			Variable resultOfTestVariable = probNet.getVariable("Result of test");
			Assert.assertTrue(subStrategyTree.getRootVariable().equals(resultOfTestVariable));
			Assert.assertTrue(subStrategyTree.getBranches().size() == 2);

			Assert.assertTrue(subStrategyTree.getBranches().get(0).getStates().get(0).getName().equals("negative"));
			StrategyTree potBranch0 = (StrategyTree) subStrategyTree.getBranches().get(0).getPotential();
			Assert.assertTrue(potBranch0.getRootVariable().getName().equals("Therapy"));
			Assert.assertTrue(potBranch0.getBranches().get(0).getStates().get(0).getName().equals("no"));

			Assert.assertTrue(subStrategyTree.getBranches().get(1).getStates().get(0).getName().equals("positive"));
			StrategyTree potBranch1 = (StrategyTree) subStrategyTree.getBranches().get(1).getPotential();
			Assert.assertTrue(potBranch1.getRootVariable().getName().equals("Therapy"));
			Assert.assertTrue(potBranch1.getBranches().get(0).getStates().get(0).getName().equals("yes"));

		} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
			e.printStackTrace();
		} catch (NodeNotFoundException e) {
			e.printStackTrace();
		}
	}

	

}
