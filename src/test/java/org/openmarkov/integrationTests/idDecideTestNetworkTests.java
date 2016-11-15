/*
* Copyright 2015 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/
package org.openmarkov.integrationTests;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmarkov.core.exception.*;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.modelUncertainty.AxisVariation;
import org.openmarkov.core.model.network.modelUncertainty.DeterministicAxisVariationType;
import org.openmarkov.core.model.network.modelUncertainty.SystematicSampling;
import org.openmarkov.core.model.network.modelUncertainty.UncertainParameter;
import org.openmarkov.core.model.network.potential.Intervention;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDBranch;
import org.openmarkov.inference.tasks.VariableElimination.*;
import org.openmarkov.io.probmodel.PGMXReader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

public class idDecideTestNetworkTests {

    private final String networkName = "networks/id/ID-decide-test.pgmx";

    // Delta parameter for Assert.Equals methods
    private final double deltaEquals = Math.pow(10,-4);

    private ProbNet probNet;
    private EvidenceCase preResolutionEvidence;

    @Before
    public void setUp() throws Exception {
        InputStream file = getClass().getClassLoader().getResourceAsStream(networkName);

        // Load the network: ID-decide-test
        PGMXReader pgmxReader = new PGMXReader();
        ProbNetInfo probNetInfo = null;
        try {
            probNetInfo = pgmxReader.loadProbNet(file, networkName);
        } catch (ParserException e) {
            e.printStackTrace();
        }
        this.probNet = probNetInfo.getProbNet();
        if (probNetInfo.getEvidence().size() != 0) {
            this.preResolutionEvidence = probNetInfo.getEvidence().get(0);
        }
    }

    @Test
    public void veResolutionTestWithoutEvidence(){
        VEResolution veResolution;
        try {
            veResolution = new VEResolution(probNet, preResolutionEvidence, null);
            TablePotential utility = veResolution.getUtility();
            Assert.assertEquals(utility.getValues()[0], 9.3289, deltaEquals);
        } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void veResolutionTestWithEvidences(){
        EvidenceCase evidenceCase = new EvidenceCase();
        Variable disease = null;
        Variable doTest = null;
        VEResolution veResolution;
        Finding finding;
        Finding secondFinding;

        // First evidence - Finding -> Disease = absent
        try {
            disease = probNet.getVariable("Disease");
            finding = new Finding(disease, 0);
            evidenceCase.addFinding(finding);
            veResolution = new VEResolution(probNet, evidenceCase, null);
            TablePotential utility = veResolution.getUtility();
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
            veResolution = new VEResolution(probNet, evidenceCase, null);
            TablePotential utility = veResolution.getUtility();
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

            veResolution = new VEResolution(probNet, evidenceCase, null);
            TablePotential utility = veResolution.getUtility();
            Assert.assertEquals(utility.getValues()[0], 7.05, deltaEquals);

        } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException | NodeNotFoundException | InvalidStateException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void veOptimalPolicyTest() {
        VEOptimalPolicy veOptimalPolicy;
        try {
            Variable decisionVariable = probNet.getVariable("Therapy");
            veOptimalPolicy = new VEOptimalPolicy(probNet, decisionVariable);
            TablePotential optimalPolicy = veOptimalPolicy.getOptimalPolicy();
            double [] expectedValues = {1,0, 1,0, 1,0, 1,0, 1,0, 0,1};
            Assert.assertArrayEquals(optimalPolicy.getValues(), expectedValues,  deltaEquals);
        } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException | NodeNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void veExpectedUtilityTest() {
        VEExpectedUtilityDecision veExpectedUtilityDecision;
        try {
            Variable decisionVariable = probNet.getVariable("Therapy");
            veExpectedUtilityDecision = new VEExpectedUtilityDecision(probNet, decisionVariable);
            TablePotential expectedUtility = veExpectedUtilityDecision.getExpectedUtility();
            double [] expectedValues = {9.16, 8.11, -0.2, -0.95, 0.0,-0.75, 9.7107227,8.03512,0.0,-0.75,4.810443,7.2184073};
            Assert.assertArrayEquals(expectedUtility.getValues(), expectedValues,  deltaEquals);
        } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException | NodeNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void veOptimalIntervention() {
        VEOptimalIntervention veOptimalIntervention;
        try {
            veOptimalIntervention = new VEOptimalIntervention(probNet, preResolutionEvidence);
            Intervention optimalIntervention = veOptimalIntervention.getOptimalIntervention();

            Variable doTestVariable = probNet.getVariable("Do test?");
            Assert.assertTrue(optimalIntervention.getRootVariable().equals(doTestVariable));
            Assert.assertTrue(veOptimalIntervention.getOptimalIntervention().getBranches().size() == 1);

            TreeADDBranch branchDoTestYes = veOptimalIntervention.getOptimalIntervention().getBranches().get(0);
            Assert.assertTrue(branchDoTestYes.getStates().get(0).getName().equals("yes"));

            Intervention subIntervention = (Intervention) branchDoTestYes.getPotential();
            Variable resultOfTestVariable = probNet.getVariable("Result of test");
            Assert.assertTrue(subIntervention.getRootVariable().equals(resultOfTestVariable));
            Assert.assertTrue(subIntervention.getBranches().size() == 2);

            Assert.assertTrue(subIntervention.getBranches().get(0).getStates().get(0).getName().equals("negative"));
            Intervention potBranch0 = (Intervention) subIntervention.getBranches().get(0).getPotential();
            Assert.assertTrue(potBranch0.getRootVariable().getName().equals("Therapy"));
            Assert.assertTrue(potBranch0.getBranches().get(0).getStates().get(0).getName().equals("no"));

            Assert.assertTrue(subIntervention.getBranches().get(1).getStates().get(0).getName().equals("positive"));
            Intervention potBranch1 = (Intervention) subIntervention.getBranches().get(1).getPotential();
            Assert.assertTrue(potBranch1.getRootVariable().getName().equals("Therapy"));
            Assert.assertTrue(potBranch1.getBranches().get(0).getStates().get(0).getName().equals("yes"));

        } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
            e.printStackTrace();
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void veSensAnTornadoSpiderTests() {
        List<UncertainParameter> uncertainParameterList = SystematicSampling.getUncertainParameters(this.probNet);
        AxisVariation axisVariation = new AxisVariation();
        axisVariation.setVariationType(DeterministicAxisVariationType.POPP);
        axisVariation.setVariationValue(0.8);

        try {
            VESensAnTornadoSpider veSensAnTornadoSpider = new VESensAnTornadoSpider(probNet, preResolutionEvidence, uncertainParameterList, axisVariation,50);
            HashMap<UncertainParameter, TablePotential> uncertainParameterTablePotentialHashMap = veSensAnTornadoSpider.getUncertainParametersPotentials();

        } catch (NotEvaluableNetworkException e) {
            e.printStackTrace();
        }
    }

}
