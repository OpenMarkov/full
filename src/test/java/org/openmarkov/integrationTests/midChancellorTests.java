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
import org.openmarkov.core.inference.TransitionTime;
import org.openmarkov.core.inference.tasks.CEAnalysis;
import org.openmarkov.core.inference.tasks.OptimalPolicies;
import org.openmarkov.core.inference.tasks.TemporalEvolution;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.model.network.CEP;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.UtilityOperations;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.GTablePotential;
import org.openmarkov.core.model.network.potential.StrategyTree;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDBranch;
import org.openmarkov.inference.temporalevaluation.tasks.TemporalEvaluation;
import org.openmarkov.inference.variableElimination.tasks.*;
import org.openmarkov.io.probmodel.reader.PGMXReader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

public class midChancellorTests {

    // Delta parameter for Assert.Equals methods
    private final double deltaEquals = Math.pow(10, -4);

    private ProbNet probNet;
    private EvidenceCase preResolutionEvidence;

    @Before
    public void setUp() {
        String networkName = "networks/mid/MID-Chancellor.pgmx";
        InputStream file = getClass().getClassLoader().getResourceAsStream(networkName);

        // Load the network: ID-decide-test
        PGMXReader pgmxReader = new PGMXReader();
        ProbNetInfo probNetInfo = null;
        try {
            probNetInfo = pgmxReader.loadProbNetInfo(networkName, file);
        } catch (ParserException e) {
            e.printStackTrace();
        }
        assert probNetInfo != null;
        this.probNet = probNetInfo.getProbNet();

        if (probNetInfo.getEvidence().size() != 0) {
            this.preResolutionEvidence = probNetInfo.getEvidence().get(0);
        }
    }

    @Test
    public void veResolutionTestWithoutEvidence() {
        VEEvaluation veEvaluation;
        try {
            veEvaluation = new VEEvaluation(probNet);
            veEvaluation.setPreResolutionEvidence(preResolutionEvidence);
            TablePotential utility = veEvaluation.getUtility();
            Assert.assertEquals(utility.getValues()[0], 50608.78077314, deltaEquals);
        } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void veOptimalPolicyTest() {
        OptimalPolicies veOptimalPolicy;
        try {
            Variable decisionVariable = probNet.getVariable("Therapy type");
            veOptimalPolicy = new VEEvaluation(probNet);
            TablePotential optimalPolicy = (TablePotential) veOptimalPolicy.getOptimalPolicy(decisionVariable);
            double[] expectedValues = {0, 1};
            Assert.assertArrayEquals(optimalPolicy.getValues(), expectedValues, deltaEquals);
        } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException | NodeNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void veOptimalIntervention() {
        VEOptimalIntervention veOptimalIntervention;
        try {
            veOptimalIntervention = new VEOptimalIntervention(probNet, preResolutionEvidence);
            StrategyTree optimalStrategyTree = veOptimalIntervention.getOptimalIntervention();

            Variable therapyType = probNet.getVariable("Therapy type");
            Assert.assertTrue(optimalStrategyTree.getRootVariable().equals(therapyType));
            Assert.assertTrue(veOptimalIntervention.getOptimalIntervention().getBranches().size() == 1);

            TreeADDBranch branchCombinationTherapy = veOptimalIntervention.getOptimalIntervention().getBranches()
                    .get(0);
            Assert.assertTrue(branchCombinationTherapy.getStates().get(0).getName().equals("combination therapy"));

        } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException | NodeNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void veCEAGlobalTests() {
        CEAnalysis veceaGlobal;
        try {
            veceaGlobal = new VECEAnalysis(probNet);
            veceaGlobal.setPreResolutionEvidence(preResolutionEvidence);
//			veceaGlobal.setUnicriterion(false);
            CEP cep = veceaGlobal.getCEP();
            Assert.assertTrue(cep.getNumIntervals() == 2);

            // First interval
            Assert.assertEquals(cep.getEffectiveness(0.0), 7.99121, deltaEquals);
            Assert.assertEquals(cep.getCost(6274.03), 44663.453558, deltaEquals);

            // Second interval
            Assert.assertEquals(cep.getEffectiveness(303383.4), 8.93739, deltaEquals);
            Assert.assertEquals(cep.getCost(6274.05), 50599.843384, deltaEquals);

        } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void veCEADecisionDecTestTests() {
        CEAnalysis veceaDecision;
        try {
            Variable decisionVariable = null;
            EvidenceCase evidenceCaseWithScenario = new EvidenceCase();

            try {
                decisionVariable = probNet.getVariable("Therapy type");
            } catch (NodeNotFoundException e) {
                e.printStackTrace();
            }

            veceaDecision = new VECEAnalysis(probNet);
//			veceaDecision.setUnicriterion(false);
            veceaDecision.setPreResolutionEvidence(evidenceCaseWithScenario);
            veceaDecision.setDecisionVariable(decisionVariable);
            GTablePotential cepPotential = veceaDecision.getGTablePotential();
            // There are two therapy types (monotherapy, combination therapy)
            Assert.assertTrue(cepPotential.elementTable.size() == 2);

            // CEP -> monotherapy
            CEP monotherapyCEP = (CEP) cepPotential.elementTable.get(0);
            Assert.assertTrue(monotherapyCEP.getNumIntervals() == 1);
            Assert.assertEquals(monotherapyCEP.getCost(11171.0), 44663.453558, deltaEquals);
            Assert.assertEquals(monotherapyCEP.getEffectiveness(0.0), 7.9912, deltaEquals);

            // CEP -> combination therapy
            CEP combinationtherapyCEP = (CEP) cepPotential.elementTable.get(1);
            Assert.assertTrue(combinationtherapyCEP.getNumIntervals() == 1);
            Assert.assertEquals(combinationtherapyCEP.getCost(500000.0), 50599.84338424, deltaEquals);
            Assert.assertEquals(combinationtherapyCEP.getEffectiveness(30000.0), 8.9374, deltaEquals);

        } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void stateVETemporalEvolutionTests() {
        Variable stateVariable = null;
        try {
            stateVariable = probNet.getVariable("State", 0);
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
        }
        // ProbNet network, Variable temporalVariable, EvidenceCase preResolutionEvidence, Variable decisionVariable)
        try {
            TemporalEvolution veTemporalEvolution = new VETemporalEvolution(probNet, stateVariable);
            veTemporalEvolution.setPreResolutionEvidence(preResolutionEvidence);
            HashMap<Variable, TablePotential> posteriorValues = veTemporalEvolution.getTemporalEvolution();

            // Check State [0]
            Variable variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("State", 0);
            double[] valuesToCheck = {0, 0, 0, 1};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check State [1]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("State", 1);
            valuesToCheck = new double[]{0.0049901961, 0.0340507497, 0.102739331, 0.8582197232};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check State [2]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("State", 2);
            valuesToCheck = new double[]{0.014226902493050322, 0.08022683278358073, 0.16900517143388435,
                    0.7365410932894846};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check State [3]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("State", 3);
            valuesToCheck = new double[]{0.04350829929448966, 0.17823853605004344, 0.24687317178302465,
                    0.5313799928724422};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check State [4]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("State", 4);
            valuesToCheck = new double[]{0.09619570266402257, 0.26972849455807596, 0.2507099601881366,
                    0.38336584258976464};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check State [5]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("State", 5);
            valuesToCheck = new double[]{0.17033714773860123, 0.3300188414850077, 0.22306346345041067,
                    0.27658054732598036};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check State [10]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("State", 10);
            valuesToCheck = new double[]{0.6088617435803294, 0.2709176367277771, 0.06616227856975207,
                    0.05405834112214166};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check State [15]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("State", 15);
            valuesToCheck = new double[]{0.8637223703879661, 0.11128536068983778, 0.014426434580920574,
                    0.010565834341275641};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check State [19]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("State", 19);
            valuesToCheck = new double[]{0.9472612120920146, 0.04584774372014692, 0.00402860239965864,
                    0.002862441788179781};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

        } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | NodeNotFoundException | UnexpectedInferenceException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void costLamiduvineVETemporalEvolutionTests() {
        Variable stateVariable = null;
        try {
            stateVariable = probNet.getVariable("Cost lamivudine", 0);
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
        }
        // ProbNet network, Variable temporalVariable, EvidenceCase preResolutionEvidence, Variable decisionVariable)
        try {
            TemporalEvolution veTemporalEvolution = new VETemporalEvolution(probNet, stateVariable);
            veTemporalEvolution.setPreResolutionEvidence(preResolutionEvidence);
            HashMap<Variable, TablePotential> posteriorValues = veTemporalEvolution.getTemporalEvolution();

            // Check Cost lamivudine [0]
            Variable variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Cost lamivudine", 0);
            double[] valuesToCheck = {0};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check Cost lamivudine [1]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Cost lamivudine", 1);
            valuesToCheck = new double[]{1957.639031912337};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check Cost lamivudine [2]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Cost lamivudine", 2);
            valuesToCheck = new double[]{1829.6851028311028};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check Cost lamivudine [3]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Cost lamivudine", 3);
            valuesToCheck = new double[]{0};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

        } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | NodeNotFoundException | UnexpectedInferenceException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void costAZTVETemporalEvolutionTests() {
        Variable stateVariable = null;
        try {
            stateVariable = probNet.getVariable("Cost AZT", 0);
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
        }
        // ProbNet network, Variable temporalVariable, EvidenceCase preResolutionEvidence, Variable decisionVariable)
        try {
            TemporalEvolution veTemporalEvolution = new VETemporalEvolution(probNet, stateVariable);
            veTemporalEvolution.setPreResolutionEvidence(preResolutionEvidence);
            HashMap<Variable, TablePotential> posteriorValues = veTemporalEvolution.getTemporalEvolution();

            // Check Cost AZT [0]
            Variable variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Cost AZT", 0);
            double[] valuesToCheck = {0};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check Cost AZT [1]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Cost AZT", 1);
            valuesToCheck = new double[]{2138.3323898907543};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check Cost AZT [2]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Cost AZT", 2);
            valuesToCheck = new double[]{1998.5680990751434};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check Cost AZT [3]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Cost AZT", 3);
            valuesToCheck = new double[]{1829.4364594658277};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check Cost AZT [4]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Cost AZT", 4);
            valuesToCheck = new double[]{1630.8148620243778};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check Cost AZT [5]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Cost AZT", 5);
            valuesToCheck = new double[]{1412.2970066384314};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check Cost AZT [10]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Cost AZT", 10);
            valuesToCheck = new double[]{497.5369763961882};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check Cost AZT [15]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Cost AZT", 15);
            valuesToCheck = new double[]{129.53594915827682};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check Cost AZT [19]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Cost AZT", 19);
            valuesToCheck = new double[]{39.707488969210104};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

        } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | NodeNotFoundException | UnexpectedInferenceException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void directMedicalCostVETemporalEvolutionTests() {
        Variable stateVariable = null;
        try {
            stateVariable = probNet.getVariable("Direct medical cost", 0);
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
        }
        // ProbNet network, Variable temporalVariable, EvidenceCase preResolutionEvidence, Variable decisionVariable)
        try {
            TemporalEvolution veTemporalEvolution = new VETemporalEvolution(probNet, stateVariable);
            veTemporalEvolution.setPreResolutionEvidence(preResolutionEvidence);
            HashMap<Variable, TablePotential> posteriorValues = veTemporalEvolution.getTemporalEvolution();

            // Check Direct medical cost [0]
            Variable variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Direct medical cost", 0);
            double[] valuesToCheck = {0};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check Direct medical cost [1]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Direct medical cost", 1);
            valuesToCheck = new double[]{1772.3357842196224};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check Direct medical cost [2]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Direct medical cost", 2);
            valuesToCheck = new double[]{1877.9704592287671};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check Direct medical cost [3]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Direct medical cost", 3);
            valuesToCheck = new double[]{2166.4123094020665};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check Direct medical cost [4]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Direct medical cost", 4);
            valuesToCheck = new double[]{2353.2614391699126};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check Direct medical cost [5]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Direct medical cost", 5);
            valuesToCheck = new double[]{2360.6999138859937};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check Direct medical cost [10]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Direct medical cost", 10);
            valuesToCheck = new double[]{1167.9724995341314};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check Direct medical cost [15]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Direct medical cost", 15);
            valuesToCheck = new double[]{340.8119475611998};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check Direct medical cost [19]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Direct medical cost", 19);
            valuesToCheck = new double[]{109.25632334292308};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

        } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | NodeNotFoundException | UnexpectedInferenceException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void communityCareCostVETemporalEvolutionTests() {
        Variable stateVariable = null;
        try {
            stateVariable = probNet.getVariable("Community care cost", 0);
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
        }
        // ProbNet network, Variable temporalVariable, EvidenceCase preResolutionEvidence, Variable decisionVariable)
        try {
            TemporalEvolution veTemporalEvolution = new VETemporalEvolution(probNet, stateVariable);
            veTemporalEvolution.setPreResolutionEvidence(preResolutionEvidence);
            HashMap<Variable, TablePotential> posteriorValues = veTemporalEvolution.getTemporalEvolution();

            // Check Community care cost [0]
            Variable variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Community care cost", 0);
            double[] valuesToCheck = {0};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check Community care cost [1]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Community care cost", 1);
            valuesToCheck = new double[]{1044.1822326663205};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check Community care cost [2]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Community care cost", 2);
            valuesToCheck = new double[]{1030.8174717108427};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check Community care cost [3]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Community care cost", 3);
            valuesToCheck = new double[]{1043.7332090804584};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check Community care cost [4]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Community care cost", 4);
            valuesToCheck = new double[]{1014.0614869896071};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check Community care cost [5]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Community care cost", 5);
            valuesToCheck = new double[]{938.8375169075567};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check Community care cost [10]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Community care cost", 10);
            valuesToCheck = new double[]{390.544856086124};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check Community care cost [15]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Community care cost", 15);
            valuesToCheck = new double[]{107.95502359582221};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

            // Check Community care cost [19]
            variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Community care cost", 19);
            valuesToCheck = new double[]{33.90036725029396};
            Assert.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

        } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | NodeNotFoundException | UnexpectedInferenceException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void veTemporalEvaluationTest() {
        try {
            TemporalEvaluation temporalEvaluation = new TemporalEvaluation(probNet);
            temporalEvaluation.setPreResolutionEvidence(preResolutionEvidence);
            TablePotential atemporalUtility = temporalEvaluation.getAtemporalUtility();
            Assert.assertEquals(atemporalUtility.values[0], 0, deltaEquals);

            List<TablePotential> potentialsPerSlice = temporalEvaluation.getUtilityPotentialsPerSlice();
            double[] costs_monotherapy = new double[21];
            double[] effectiveness_monotherapy = new double[21];
            double[] costs_combtherapy = new double[21];
            double[] effectiveness_combtherapy = new double[21];

            int slice = 0;
            for (TablePotential tablePotential : potentialsPerSlice) {
                costs_monotherapy[slice] = ((CEP) ((GTablePotential) tablePotential).elementTable.get(0)).getCost(0);
                effectiveness_monotherapy[slice] = ((CEP) ((GTablePotential) tablePotential).elementTable.get(0))
                        .getEffectiveness(0);
                costs_combtherapy[slice] = ((CEP) ((GTablePotential) tablePotential).elementTable.get(1)).getCost(0);
                effectiveness_combtherapy[slice] = ((CEP) ((GTablePotential) tablePotential).elementTable.get(1))
                        .getEffectiveness(0);
                slice++;
            }

            double c_monotherapy = UtilityOperations.applyLeftRiemannSum(costs_monotherapy, 1);
            double e_monotherapy = UtilityOperations.applyLeftRiemannSum(effectiveness_monotherapy, 1);

            double c_combtherapy = UtilityOperations.applyLeftRiemannSum(costs_combtherapy, 1);
            double e_combtherapy = UtilityOperations.applyLeftRiemannSum(effectiveness_combtherapy, 1);

            Variable decisionVariable = null;
            try {
                decisionVariable = probNet.getVariable("Therapy type");
            } catch (NodeNotFoundException e) {
                e.printStackTrace();
            }

            //Asserting that Left Rieman summ is equals to a transition at the end
            probNet.getInferenceOptions().getTemporalOptions().setTransition(TransitionTime.END);
            CEAnalysis veceaDecision = new VECEAnalysis(probNet);
            veceaDecision.setPreResolutionEvidence(preResolutionEvidence);
            veceaDecision.setDecisionVariable(decisionVariable);

            GTablePotential ceaResult = veceaDecision.getGTablePotential();
            double c_monotherapy_cea = ((CEP) (ceaResult.elementTable.get(0))).getCost(0);
            double e_monotherapy_cea = ((CEP) (ceaResult.elementTable.get(0))).getEffectiveness(0);
            double c_combtherapy_cea = ((CEP) (ceaResult.elementTable.get(1))).getCost(0);
            double e_combtherapy_cea = ((CEP) (ceaResult.elementTable.get(1))).getEffectiveness(0);

            Assert.assertEquals(c_monotherapy, c_monotherapy_cea, deltaEquals);
            Assert.assertEquals(e_monotherapy, e_monotherapy_cea, deltaEquals);
            Assert.assertEquals(c_combtherapy, c_combtherapy_cea, deltaEquals);
            Assert.assertEquals(e_combtherapy, e_combtherapy_cea, deltaEquals);

            //Asserting that Right Riemann Summ is equals to a transition at the beginning
            probNet.getInferenceOptions().getTemporalOptions().setTransition(TransitionTime.BEGINNING);
            c_monotherapy = UtilityOperations.applyRightRiemannSum(costs_monotherapy, 1);
            e_monotherapy = UtilityOperations.applyRightRiemannSum(effectiveness_monotherapy, 1);
            c_combtherapy = UtilityOperations.applyRightRiemannSum(costs_combtherapy, 1);
            e_combtherapy = UtilityOperations.applyRightRiemannSum(effectiveness_combtherapy, 1);

            veceaDecision = new VECEAnalysis(probNet);
            veceaDecision.setPreResolutionEvidence(preResolutionEvidence);
            veceaDecision.setDecisionVariable(decisionVariable);
            ceaResult = veceaDecision.getGTablePotential();
            c_monotherapy_cea = ((CEP) (ceaResult.elementTable.get(0))).getCost(0);
            e_monotherapy_cea = ((CEP) (ceaResult.elementTable.get(0))).getEffectiveness(0);
            c_combtherapy_cea = ((CEP) (ceaResult.elementTable.get(1))).getCost(0);
            e_combtherapy_cea = ((CEP) (ceaResult.elementTable.get(1))).getEffectiveness(0);

            Assert.assertEquals(c_monotherapy, c_monotherapy_cea, deltaEquals);
            Assert.assertEquals(e_monotherapy, e_monotherapy_cea, deltaEquals);
            Assert.assertEquals(c_combtherapy, c_combtherapy_cea, deltaEquals);
            Assert.assertEquals(e_combtherapy, e_combtherapy_cea, deltaEquals);

        } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
            e.printStackTrace();
        }
    }
}
