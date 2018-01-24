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
import org.openmarkov.core.model.network.*;
import org.openmarkov.core.model.network.potential.GTablePotential;
import org.openmarkov.core.model.network.potential.Intervention;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDBranch;
import org.openmarkov.inference.variableElimination.tasks.VECEADecision;
import org.openmarkov.inference.variableElimination.tasks.VECEAGlobal;
import org.openmarkov.inference.variableElimination.tasks.VEOptimalIntervention;
import org.openmarkov.inference.variableElimination.tasks.VEOptimalPolicy;
import org.openmarkov.inference.variableElimination.tasks.VEEvaluation;
import org.openmarkov.io.probmodel.reader.PGMXReader;

import java.io.InputStream;

public class idCEATest2therapiesTests {

    private final String networkName = "networks/id/ID-CEA-test-2therapies.pgmx";

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
            probNetInfo = pgmxReader.loadProbNetInfo(networkName, file);
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
        VEEvaluation veEvaluation;
        try {
            veEvaluation = new VEEvaluation(probNet, preResolutionEvidence, null);
            TablePotential utility = veEvaluation.getUtility();
            Assert.assertEquals(utility.getValues()[0], 269569.4, deltaEquals);
        } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void veResolutionTestWithEvidences(){
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
            veEvaluation = new VEEvaluation(probNet, evidenceCase, null);
            TablePotential utility = veEvaluation.getUtility();
            Assert.assertEquals(utility.getValues()[0], 10*30000, deltaEquals);

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
            veEvaluation = new VEEvaluation(probNet, evidenceCase, null);
            TablePotential utility = veEvaluation.getUtility();
            Assert.assertEquals(utility.getValues()[0], 125000, deltaEquals);

        } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException | NodeNotFoundException | InvalidStateException e) {
            e.printStackTrace();
        }

        // Third evidence - Multiple findings -> Disease = present; Do test? = yes
        try {
            evidenceCase = new EvidenceCase();
            disease = probNet.getVariable("Disease");
            doTest = probNet.getVariable("Dec:Test");

            // Set Disease = present
            finding = new Finding(disease, 1);
            evidenceCase.addFinding(finding);

            // Set Do Test? = yes
            secondFinding = new Finding(doTest, 1);
            evidenceCase.addFinding(secondFinding);

            veEvaluation = new VEEvaluation(probNet, evidenceCase, null);
            TablePotential utility = veEvaluation.getUtility();
            Assert.assertEquals(utility.getValues()[0], 124850, deltaEquals);

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
            double [] expectedValues = {1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0};
            Assert.assertArrayEquals(optimalPolicy.getValues(), expectedValues,  deltaEquals);
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

            Variable doTestVariable = probNet.getVariable("Dec:Test");
            Assert.assertTrue(optimalIntervention.getRootVariable().equals(doTestVariable));
            Assert.assertTrue(veOptimalIntervention.getOptimalIntervention().getBranches().size() == 1);

            TreeADDBranch branchDoTestYes = veOptimalIntervention.getOptimalIntervention().getBranches().get(0);
            Assert.assertTrue(branchDoTestYes.getStates().get(0).getName().equals("yes"));

            Intervention subIntervention = (Intervention) branchDoTestYes.getPotential();
            Variable resultOfTestVariable = probNet.getVariable("Test");
            Assert.assertTrue(subIntervention.getRootVariable().equals(resultOfTestVariable));
            Assert.assertTrue(subIntervention.getBranches().size() == 2);

            Assert.assertTrue(subIntervention.getBranches().get(0).getStates().get(0).getName().equals("negative"));
            Intervention potBranch0 = (Intervention) subIntervention.getBranches().get(0).getPotential();
            Assert.assertTrue(potBranch0.getRootVariable().getName().equals("Therapy"));
            Assert.assertTrue(potBranch0.getBranches().get(0).getStates().get(0).getName().equals("no"));

            Assert.assertTrue(subIntervention.getBranches().get(1).getStates().get(0).getName().equals("positive"));
            Intervention potBranch1 = (Intervention) subIntervention.getBranches().get(1).getPotential();
            Assert.assertTrue(potBranch1.getRootVariable().getName().equals("Therapy"));
            Assert.assertTrue(potBranch1.getBranches().get(0).getStates().get(0).getName().equals("therapy 1"));

        } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
            e.printStackTrace();
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void veCEAGlobalTests(){
        VECEAGlobal veceaGlobal;
        try {
            veceaGlobal = new VECEAGlobal(probNet, preResolutionEvidence);
            CEP cep = veceaGlobal.getCEP();
            Assert.assertTrue(cep.getNumIntervals() == 3);

            // First interval
            Assert.assertEquals(cep.getCost(11171.0), 0, deltaEquals);
            Assert.assertEquals(cep.getEffectiveness(0.0), 8.768, deltaEquals);

            // Second interval
            Assert.assertEquals(cep.getCost(11171.4), 3874, deltaEquals);
            Assert.assertEquals(cep.getEffectiveness(33383.4), 9.11478, deltaEquals);

            // Third interval
            Assert.assertEquals(cep.getCost(500000.0), 13184, deltaEquals);
            Assert.assertEquals(cep.getEffectiveness(33383.6), 9.39366, deltaEquals);

        } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void veCEADecisionDecTestTests(){
        VECEADecision veceaDecision;
        try {
            Variable decisionVariable = null;
            EvidenceCase evidenceCaseWithScenario = new EvidenceCase();

            try {
                decisionVariable = probNet.getVariable("Therapy");

                // Scenario
                Variable decTestVariable = probNet.getVariable("Dec:Test");
                Finding decTestYes = new Finding(decTestVariable, 1);
                evidenceCaseWithScenario.addFinding(decTestYes);

                Variable testVariable = probNet.getVariable("Test");
                Finding testNegative = new Finding(testVariable, 1);
                evidenceCaseWithScenario.addFinding(testNegative);
            } catch (NodeNotFoundException e) {
                e.printStackTrace();
            } catch (InvalidStateException e) {
                e.printStackTrace();
            }


            veceaDecision = new VECEADecision(probNet, evidenceCaseWithScenario, decisionVariable);
            GTablePotential cepPotential = veceaDecision.getCEPPotential();
            // There are three therapies (no, therapy 1, therapy 2)
            Assert.assertTrue(cepPotential.elementTable.size() == 3);

            // CEP -> no therapy
            CEP noTherapyCEP = (CEP) cepPotential.elementTable.get(0);
            Assert.assertTrue(noTherapyCEP.getNumIntervals() == 1);
            Assert.assertEquals(noTherapyCEP.getCost(11171.0), 150.0, deltaEquals);
            Assert.assertEquals(noTherapyCEP.getEffectiveness(0.0), 9.8486, deltaEquals);

            // CEP -> therapy 1
            CEP therapyOneCEP = (CEP) cepPotential.elementTable.get(1);
            Assert.assertTrue(therapyOneCEP.getNumIntervals() == 1);
            Assert.assertEquals(therapyOneCEP.getCost(500000.0), 20150, deltaEquals);
            Assert.assertEquals(therapyOneCEP.getEffectiveness(30000.0), 9.7985, deltaEquals);

            // CEP -> therapy 1
            CEP therapyTwoCEP = (CEP) cepPotential.elementTable.get(2);
            Assert.assertTrue(therapyTwoCEP.getNumIntervals() == 1);
            Assert.assertEquals(therapyTwoCEP.getCost(10.0), 70150, deltaEquals);
            Assert.assertEquals(therapyTwoCEP.getEffectiveness(5.0), 9.2518, deltaEquals);

        } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
            e.printStackTrace();
        }
    }

}
