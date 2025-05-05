/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.integrationTests;

import org.junit.jupiter.api.*;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
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
import org.openmarkov.inference.algorithm.temporalevaluation.tasks.TemporalEvaluation;
import org.openmarkov.inference.algorithm.variableElimination.tasks.VECEAnalysis;
import org.openmarkov.inference.algorithm.variableElimination.tasks.VEEvaluation;
import org.openmarkov.inference.algorithm.variableElimination.tasks.VEOptimalIntervention;
import org.openmarkov.inference.algorithm.variableElimination.tasks.VETemporalEvolution;
import org.openmarkov.io.probmodel.reader.PGMXReader_0_2;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class midChancellorTests {

	// Delta parameter for Assertions.Equals methods
	private final double deltaEquals = Math.pow(10, -4);

	private ProbNet probNet;
	private EvidenceCase preResolutionEvidence;

	@BeforeEach public void setUp() {
		String networkName = "networks/mid/MID-Chancellor.pgmx";
		URL res = getClass().getClassLoader().getResource(networkName);
		File f = null;
		try {
			f = Paths.get(res.toURI()).toFile();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		String absolutePath = f.getAbsolutePath();

		// Load the network: ID-decide-test
		PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
		ProbNetInfo probNetInfo = null;
		try {
			probNetInfo = pgmxReader.loadProbNetInfo(absolutePath);
		} catch (ParserException e) {
			e.printStackTrace();
		}
		assert probNetInfo != null;
		this.probNet = probNetInfo.getProbNet();

		if (probNetInfo.getEvidence().size() != 0) {
			this.preResolutionEvidence = probNetInfo.getEvidence().get(0);
		}
	}
	@Disabled
	@Test public void veResolutionTestWithoutEvidence() {
		VEEvaluation veEvaluation;
		try {
			veEvaluation = new VEEvaluation(probNet);
			veEvaluation.setPreResolutionEvidence(preResolutionEvidence);
			TablePotential utility = veEvaluation.getUtility();
			Assertions.assertEquals(utility.getValues()[0], 50608.78077314, deltaEquals);
		} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
			e.printStackTrace();
		}
	}
	@Disabled
	@Test public void veOptimalPolicyTest() {
		OptimalPolicies veOptimalPolicy;
		try {
			Variable decisionVariable = probNet.getVariable("Therapy type");
			veOptimalPolicy = new VEEvaluation(probNet);
			TablePotential optimalPolicy = (TablePotential) veOptimalPolicy.getOptimalPolicy(decisionVariable);
			double[] expectedValues = { 0, 1 };
			Assertions.assertArrayEquals(optimalPolicy.getValues(), expectedValues, deltaEquals);
		} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException | NodeNotFoundException e) {
			e.printStackTrace();
		}
	}
	@Disabled
	@Test public void veOptimalIntervention() {
		VEOptimalIntervention veOptimalIntervention;
		try {
			veOptimalIntervention = new VEOptimalIntervention(probNet, preResolutionEvidence);
			StrategyTree optimalStrategyTree = veOptimalIntervention.getOptimalIntervention();

			Variable therapyType = probNet.getVariable("Therapy type");
			Assertions.assertTrue(optimalStrategyTree.getRootVariable().equals(therapyType));
			Assertions.assertTrue(veOptimalIntervention.getOptimalIntervention().getBranches().size() == 1);

			TreeADDBranch branchCombinationTherapy = veOptimalIntervention.getOptimalIntervention().getBranches()
					.get(0);
			Assertions.assertTrue(branchCombinationTherapy.getStates().get(0).getName().equals("combination therapy"));

		} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException | NodeNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Test public void veCEAGlobalTests() {
		CEAnalysis veceaGlobal;
		try {
			veceaGlobal = new VECEAnalysis(probNet);
			veceaGlobal.setPreResolutionEvidence(preResolutionEvidence);
			//			veceaGlobal.setUnicriterion(false);
			CEP cep = veceaGlobal.getCEP();
			Assertions.assertTrue(cep.getNumIntervals() == 2);

			// First interval
			Assertions.assertEquals(cep.getEffectiveness(0.0), 7.99121, deltaEquals);
			Assertions.assertEquals(cep.getCost(6274.03), 44663.453558, deltaEquals);

			// Second interval
			Assertions.assertEquals(cep.getEffectiveness(303383.4), 8.93739, deltaEquals);
			Assertions.assertEquals(cep.getCost(6274.05), 50599.843384, deltaEquals);

		} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
			e.printStackTrace();
		}
	}
	@Disabled
	@Test public void veCEADecisionDecTestTests() {
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
			GTablePotential cepPotential = veceaDecision.getUtility();
			// There are two therapy types (monotherapy, combination therapy)
			Assertions.assertTrue(cepPotential.elementTable.size() == 2);

			// CEP -> monotherapy
			CEP monotherapyCEP = (CEP) cepPotential.elementTable.get(0);
			Assertions.assertTrue(monotherapyCEP.getNumIntervals() == 1);
			Assertions.assertEquals(monotherapyCEP.getCost(11171.0), 44663.453558, deltaEquals);
			Assertions.assertEquals(monotherapyCEP.getEffectiveness(0.0), 7.9912, deltaEquals);

			// CEP -> combination therapy
			CEP combinationtherapyCEP = (CEP) cepPotential.elementTable.get(1);
			Assertions.assertTrue(combinationtherapyCEP.getNumIntervals() == 1);
			Assertions.assertEquals(combinationtherapyCEP.getCost(500000.0), 50599.84338424, deltaEquals);
			Assertions.assertEquals(combinationtherapyCEP.getEffectiveness(30000.0), 8.9374, deltaEquals);

		} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
			e.printStackTrace();
		}
	}
	@Disabled
	@Test public void stateVETemporalEvolutionTests() {
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
			double[] valuesToCheck = { 0, 0, 0, 1 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check State [1]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("State", 1);
			valuesToCheck = new double[] { 0.0049901961, 0.0340507497, 0.102739331, 0.8582197232 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check State [2]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("State", 2);
			valuesToCheck = new double[] { 0.014226902493050322, 0.08022683278358073, 0.16900517143388435,
					0.7365410932894846 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check State [3]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("State", 3);
			valuesToCheck = new double[] { 0.04350829929448966, 0.17823853605004344, 0.24687317178302465,
					0.5313799928724422 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check State [4]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("State", 4);
			valuesToCheck = new double[] { 0.09619570266402257, 0.26972849455807596, 0.2507099601881366,
					0.38336584258976464 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check State [5]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("State", 5);
			valuesToCheck = new double[] { 0.17033714773860123, 0.3300188414850077, 0.22306346345041067,
					0.27658054732598036 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check State [10]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("State", 10);
			valuesToCheck = new double[] { 0.6088617435803294, 0.2709176367277771, 0.06616227856975207,
					0.05405834112214166 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check State [15]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("State", 15);
			valuesToCheck = new double[] { 0.8637223703879661, 0.11128536068983778, 0.014426434580920574,
					0.010565834341275641 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check State [19]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("State", 19);
			valuesToCheck = new double[] { 0.9472612120920146, 0.04584774372014692, 0.00402860239965864,
					0.002862441788179781 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

		} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | NodeNotFoundException | UnexpectedInferenceException e) {
			e.printStackTrace();
		}
	}
	@Disabled
	@Test public void costLamiduvineVETemporalEvolutionTests() {
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
			double[] valuesToCheck = { 0 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check Cost lamivudine [1]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Cost lamivudine", 1);
			valuesToCheck = new double[] { 1957.639031912337 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check Cost lamivudine [2]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Cost lamivudine", 2);
			valuesToCheck = new double[] { 1829.6851028311028 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check Cost lamivudine [3]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Cost lamivudine", 3);
			valuesToCheck = new double[] { 0 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

		} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | NodeNotFoundException | UnexpectedInferenceException e) {
			e.printStackTrace();
		}
	}

	@Test public void costAZTVETemporalEvolutionTests() {
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
			double[] valuesToCheck = { 0 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check Cost AZT [1]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Cost AZT", 1);
			valuesToCheck = new double[] { 2138.3323898907543 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check Cost AZT [2]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Cost AZT", 2);
			valuesToCheck = new double[] { 1998.5680990751434 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check Cost AZT [3]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Cost AZT", 3);
			valuesToCheck = new double[] { 1829.4364594658277 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check Cost AZT [4]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Cost AZT", 4);
			valuesToCheck = new double[] { 1630.8148620243778 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check Cost AZT [5]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Cost AZT", 5);
			valuesToCheck = new double[] { 1412.2970066384314 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check Cost AZT [10]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Cost AZT", 10);
			valuesToCheck = new double[] { 497.5369763961882 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check Cost AZT [15]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Cost AZT", 15);
			valuesToCheck = new double[] { 129.53594915827682 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check Cost AZT [19]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Cost AZT", 19);
			valuesToCheck = new double[] { 39.707488969210104 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

		} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | NodeNotFoundException | UnexpectedInferenceException e) {
			e.printStackTrace();
		}
	}

	@Test public void directMedicalCostVETemporalEvolutionTests() {
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
			double[] valuesToCheck = { 0 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check Direct medical cost [1]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Direct medical cost", 1);
			valuesToCheck = new double[] { 1772.3357842196224 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check Direct medical cost [2]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Direct medical cost", 2);
			valuesToCheck = new double[] { 1877.9704592287671 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check Direct medical cost [3]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Direct medical cost", 3);
			valuesToCheck = new double[] { 2166.4123094020665 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check Direct medical cost [4]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Direct medical cost", 4);
			valuesToCheck = new double[] { 2353.2614391699126 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check Direct medical cost [5]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Direct medical cost", 5);
			valuesToCheck = new double[] { 2360.6999138859937 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check Direct medical cost [10]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Direct medical cost", 10);
			valuesToCheck = new double[] { 1167.9724995341314 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check Direct medical cost [15]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Direct medical cost", 15);
			valuesToCheck = new double[] { 340.8119475611998 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check Direct medical cost [19]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Direct medical cost", 19);
			valuesToCheck = new double[] { 109.25632334292308 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

		} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | NodeNotFoundException | UnexpectedInferenceException e) {
			e.printStackTrace();
		}
	}
	@Disabled
	@Test public void communityCareCostVETemporalEvolutionTests() {
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
			double[] valuesToCheck = { 0 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check Community care cost [1]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Community care cost", 1);
			valuesToCheck = new double[] { 1044.1822326663205 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check Community care cost [2]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Community care cost", 2);
			valuesToCheck = new double[] { 1030.8174717108427 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check Community care cost [3]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Community care cost", 3);
			valuesToCheck = new double[] { 1043.7332090804584 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check Community care cost [4]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Community care cost", 4);
			valuesToCheck = new double[] { 1014.0614869896071 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check Community care cost [5]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Community care cost", 5);
			valuesToCheck = new double[] { 938.8375169075567 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check Community care cost [10]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Community care cost", 10);
			valuesToCheck = new double[] { 390.544856086124 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check Community care cost [15]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Community care cost", 15);
			valuesToCheck = new double[] { 107.95502359582221 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

			// Check Community care cost [19]
			variableToCheck = veTemporalEvolution.getExpandedNetwork().getVariable("Community care cost", 19);
			valuesToCheck = new double[] { 33.90036725029396 };
			Assertions.assertArrayEquals(valuesToCheck, posteriorValues.get(variableToCheck).getValues(), deltaEquals);

		} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | NodeNotFoundException | UnexpectedInferenceException e) {
			e.printStackTrace();
		}
	}
	@Disabled
	@Test public void veTemporalEvaluationTest() {
		try {
			TemporalEvaluation temporalEvaluation = new TemporalEvaluation(probNet);
			temporalEvaluation.setPreResolutionEvidence(preResolutionEvidence);
			TablePotential atemporalUtility = temporalEvaluation.getAtemporalUtility();
			Assertions.assertEquals(atemporalUtility.values[0], 0, deltaEquals);

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

			GTablePotential ceaResult = veceaDecision.getUtility();
			double c_monotherapy_cea = ((CEP) (ceaResult.elementTable.get(0))).getCost(0);
			double e_monotherapy_cea = ((CEP) (ceaResult.elementTable.get(0))).getEffectiveness(0);
			double c_combtherapy_cea = ((CEP) (ceaResult.elementTable.get(1))).getCost(0);
			double e_combtherapy_cea = ((CEP) (ceaResult.elementTable.get(1))).getEffectiveness(0);

			Assertions.assertEquals(c_monotherapy, c_monotherapy_cea, deltaEquals);
			Assertions.assertEquals(e_monotherapy, e_monotherapy_cea, deltaEquals);
			Assertions.assertEquals(c_combtherapy, c_combtherapy_cea, deltaEquals);
			Assertions.assertEquals(e_combtherapy, e_combtherapy_cea, deltaEquals);

			//Asserting that Right Riemann Summ is equals to a transition at the beginning
			probNet.getInferenceOptions().getTemporalOptions().setTransition(TransitionTime.BEGINNING);
			c_monotherapy = UtilityOperations.applyRightRiemannSum(costs_monotherapy, 1);
			e_monotherapy = UtilityOperations.applyRightRiemannSum(effectiveness_monotherapy, 1);
			c_combtherapy = UtilityOperations.applyRightRiemannSum(costs_combtherapy, 1);
			e_combtherapy = UtilityOperations.applyRightRiemannSum(effectiveness_combtherapy, 1);

			veceaDecision = new VECEAnalysis(probNet);
			veceaDecision.setPreResolutionEvidence(preResolutionEvidence);
			veceaDecision.setDecisionVariable(decisionVariable);
			ceaResult = veceaDecision.getUtility();
			c_monotherapy_cea = ((CEP) (ceaResult.elementTable.get(0))).getCost(0);
			e_monotherapy_cea = ((CEP) (ceaResult.elementTable.get(0))).getEffectiveness(0);
			c_combtherapy_cea = ((CEP) (ceaResult.elementTable.get(1))).getCost(0);
			e_combtherapy_cea = ((CEP) (ceaResult.elementTable.get(1))).getEffectiveness(0);

			Assertions.assertEquals(c_monotherapy, c_monotherapy_cea, deltaEquals);
			Assertions.assertEquals(e_monotherapy, e_monotherapy_cea, deltaEquals);
			Assertions.assertEquals(c_combtherapy, c_combtherapy_cea, deltaEquals);
			Assertions.assertEquals(e_combtherapy, e_combtherapy_cea, deltaEquals);

		} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
			e.printStackTrace();
		}
	}
}
