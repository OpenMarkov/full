/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.costeffectiveness;

import org.junit.jupiter.api.*;
import org.openmarkov.core.inference.TransitionTime;
import org.openmarkov.core.model.network.CEP;
import org.openmarkov.core.model.network.Criterion;
import org.openmarkov.core.model.network.Criterion.CECriterion;
import org.openmarkov.core.model.network.CycleLength.DiscountUnit;
import org.openmarkov.core.model.network.CycleLength.Unit;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.GTablePotential;
import org.openmarkov.inference.algorithm.variableElimination.tasks.VECEAnalysis;
import org.openmarkov.inference.algorithm.variableElimination.tasks.VECEPSA;
import org.openmarkov.inference.algorithm.variableElimination.tasks.VEEvaluation;
import org.openmarkov.io.probmodel.reader.PGMXReader_0_2;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class CEAGlobalAnalysisTest {

	private boolean useMultithreading = true;

	@BeforeEach public void setUp() throws Exception {

	}

	@Disabled @Test public void testCHAP() throws Exception {
		// Constants
		String modelFilePath = "networks" + File.separator + "mid" + File.separator + "chap.pgmx";
		// Open the file containing the network
		File f = null;
		URL res = getClass().getClassLoader().getResource(modelFilePath);
		f = Paths.get(res.toURI()).toFile();
		String absolutePath = f.getAbsolutePath();
		// Load the Bayesian network
		PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
		ProbNet probNet = pgmxReader.loadProbNet(absolutePath);

		EvidenceCase evidence = new EvidenceCase();

		// Set cost and effectiveness discounts to all the criteria with that CECriteria. Set the number of cycles and the transition time.
		setOldMethodParameters(probNet, 3.0, 3.0, 3, TransitionTime.BEGINNING);
		//CostEffectivenessAnalysis ceAnalysis = new CostEffectivenessAnalysis(probNet, evidence, 3.0, 3.0, 3, TransitionTime.BEGINNING);

		VECEAnalysis veceAnalysis = new VECEAnalysis(probNet);
		veceAnalysis.setDecisionVariable(probNet.getNodes(NodeType.DECISION).get(0).getVariable());
		veceAnalysis.setPreResolutionEvidence(evidence);

		GTablePotential<?> result = veceAnalysis.getUtility();

		double[] expectedResults = new double[] { 1066.744, 1.444, 852.399, 1.709 };

		Assertions.assertArrayEquals(expectedResults, result.values, 0.001);
	}

	/**
	 * Test chap model with a super value cost node
	 *
	 * @throws Exception
	 */
	@Disabled @Test public void testCHAPSV() throws Exception {
		// Constants
		String modelFilePath = "networks" + File.separator + "mid" + File.separator + "chap-sv.pgmx";
		// Open the file containing the network
		File f = null;
		URL res = getClass().getClassLoader().getResource(modelFilePath);
		f = Paths.get(res.toURI()).toFile();
		String absolutePath = f.getAbsolutePath();
		// Load the Bayesian network
		PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
		ProbNet probNet = pgmxReader.loadProbNet(absolutePath);

		EvidenceCase evidence = new EvidenceCase();

		// Set cost and effectiveness discounts to all the criteria with that CECriteria. Set the number of cycles and the transition time.
		setOldMethodParameters(probNet, 3.0, 3.0, 3, TransitionTime.BEGINNING);

		VECEAnalysis veceAnalysis = new VECEAnalysis(probNet);
		veceAnalysis.setDecisionVariable(probNet.getNodes(NodeType.DECISION).get(0).getVariable());
		veceAnalysis.setPreResolutionEvidence(evidence);

		GTablePotential<?> result = veceAnalysis.getUtility();

		double[] expectedResults = new double[] { 1066.744, 1.444, 852.399, 1.709 };

		Assertions.assertArrayEquals(expectedResults, result.values, 0.001);
	}

	@Disabled @Test public void testChancellorHC() throws Exception {
		// Constants
		String modelFilePath = "networks" + File.separator + "mid" + File.separator + "MID-dmhee-2.5.pgmx";
		// Open the file containing the network
		File f = null;
		URL res = getClass().getClassLoader().getResource(modelFilePath);
		f = Paths.get(res.toURI()).toFile();
		String absolutePath = f.getAbsolutePath();

		// Load the Bayesian network
		PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
		ProbNet probNet = pgmxReader.loadProbNet(absolutePath);

		EvidenceCase evidence = new EvidenceCase();

		// Set cost and effectiveness discounts to all the criteria with that CECriteria. Set the number of cycles and the transition time.
		setOldMethodParameters(probNet, 6.0, 0.0, 20, TransitionTime.HALF);
		//		CostEffectivenessAnalysis ceAnalysis = new CostEffectivenessAnalysis(probNet, evidence, 6.0, 0.0, 20, TransitionTime.HALF);

		VECEAnalysis ceAnalysis = new VECEAnalysis(probNet);
		ceAnalysis.setDecisionVariable(probNet.getNodes(NodeType.DECISION).get(0).getVariable());
		ceAnalysis.setPreResolutionEvidence(evidence);

		GTablePotential<?> result = ceAnalysis.getUtility();

		double[] expectedResults = new double[] { 50585.917, 9.412, 44662.217, 8.471 };
		Assertions.assertArrayEquals(expectedResults, result.values, 0.001);
	}

	@Test public void testChancellorUnicriterion() throws Exception {
		// Constants
		String modelFilePath = "networks" + File.separator + "mid" + File.separator
				+ "MID-Chancellor-Unicriterion.pgmx";
		// Open the file containing the network
		File f = null;
		URL res = getClass().getClassLoader().getResource(modelFilePath);
		f = Paths.get(res.toURI()).toFile();
		String absolutePath = f.getAbsolutePath();
		// Load the Bayesian network
		PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
		ProbNet probNet = pgmxReader.loadProbNet(absolutePath);

		EvidenceCase evidence = new EvidenceCase();
		List<Variable> conditioningVariables = new ArrayList<Variable>();

		double wtp = 30000;
		for (Criterion criterion : probNet.getDecisionCriteria()) {
			if (criterion.getCECriterion().equals(CECriterion.Cost)) {
				criterion.setUnicriterizationScale(-1);
			} else if (criterion.getCECriterion().equals(CECriterion.Effectiveness)) {
				criterion.setUnicriterizationScale(wtp);
			}
		}

		VEEvaluation veResolution = new VEEvaluation(probNet);
		veResolution.setPreResolutionEvidence(evidence);
		veResolution.setConditioningVariables(conditioningVariables);

		double globalUtility = veResolution.getUtility().getValues()[0];
		Assertions.assertEquals(globalUtility, 195546.556793745, Math.pow(10, -8));

		wtp = 8000;
		for (Criterion criterion : probNet.getDecisionCriteria()) {
			if (criterion.getCECriterion().equals(CECriterion.Cost)) {
				criterion.setUnicriterizationScale(-1);
			} else if (criterion.getCECriterion().equals(CECriterion.Effectiveness)) {
				criterion.setUnicriterizationScale(wtp);
			}
		}

		veResolution = new VEEvaluation(probNet);
		veResolution.setPreResolutionEvidence(evidence);
		veResolution.setConditioningVariables(conditioningVariables);
		globalUtility = veResolution.getUtility().getValues()[0];
		Assertions.assertEquals(globalUtility, 184.440530353197, Math.pow(10, -8));

	}

	@Disabled @Test public void testDMHEE25SV() throws Exception {
		// Constants
		String modelFilePath = "networks" + File.separator + "mid" + File.separator + "MID-dmhee-2.5-sv.pgmx";
		// Open the file containing the network
		File f = null;
		URL res = getClass().getClassLoader().getResource(modelFilePath);
		f = Paths.get(res.toURI()).toFile();
		String absolutePath = f.getAbsolutePath();

		// Load the Bayesian network
		PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
		ProbNet probNet = pgmxReader.loadProbNet(absolutePath);

		EvidenceCase evidence = new EvidenceCase();

		// Set cost and effectiveness discounts to all the criteria with that CECriteria. Set the number of cycles and the transition time.
		setOldMethodParameters(probNet, 6.0, 0.0, 20, TransitionTime.BEGINNING);
		//		CostEffectivenessAnalysis ceAnalysis = new CostEffectivenessAnalysis(probNet, evidence, 6.0, 0.0, 20, TransitionTime.BEGINNING);

		VECEAnalysis ceAnalysis = new VECEAnalysis(probNet);
		ceAnalysis.setDecisionVariable(probNet.getNodes(NodeType.DECISION).get(0).getVariable());
		ceAnalysis.setPreResolutionEvidence(evidence);

		GTablePotential<?> result = ceAnalysis.getUtility();

		double[] expectedResults = new double[] { 50585.917, 8.935, 44662.217, 7.991 };

		Assertions.assertArrayEquals(expectedResults, result.values, 0.001);
	}

	@Test public void testDMHEE35() throws Exception {
		// Constants
		String modelFilePath = "networks" + File.separator + "mid" + File.separator + "MID-dmhee-3.5.pgmx";
		// Open the file containing the network
		File f = null;
		URL res = getClass().getClassLoader().getResource(modelFilePath);
		f = Paths.get(res.toURI()).toFile();
		String absolutePath = f.getAbsolutePath();

		// Load the Bayesian network
		PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
		ProbNet probNet = pgmxReader.loadProbNet(absolutePath);

		EvidenceCase evidence = new EvidenceCase();
		Variable sexVariable = probNet.getVariable("Sex");
		evidence.addFinding(new Finding(sexVariable, 0));

		// Set cost and effectiveness discounts to all the criteria with that CECriteria. Set the number of cycles and the transition time.
		setOldMethodParameters(probNet, 6.0, 1.5, 60, TransitionTime.BEGINNING);

		VECEAnalysis ceAnalysis = new VECEAnalysis(probNet);
		ceAnalysis.setDecisionVariable(probNet.getNodes(NodeType.DECISION).get(0).getVariable());
		ceAnalysis.setPreResolutionEvidence(evidence);

		GTablePotential<?> result = ceAnalysis.getUtility();

		double[] expectedResults = new double[] { 510.948, 14.666, 609.904, 14.701 };
		CEP[] ceps = new CEP[] {((CEP)result.elementTable.get(0)),((CEP)result.elementTable.get(1))};
		double[] results = new double[] {ceps[0].getCost(0), ceps[0].getEffectiveness(0), ceps[1].getCost(0), ceps[1].getEffectiveness(0)};
		Assertions.assertArrayEquals(expectedResults, results, 0.001);

		evidence.changeFinding(new Finding(sexVariable, 1));

		ceAnalysis = new VECEAnalysis(probNet);
		ceAnalysis.setDecisionVariable(probNet.getNodes(NodeType.DECISION).get(0).getVariable());
		ceAnalysis.setPreResolutionEvidence(evidence);

		result = ceAnalysis.getUtility();
		ceps = new CEP[] {((CEP)result.elementTable.get(0)),((CEP)result.elementTable.get(1))};
		results = new double[] {ceps[0].getCost(0), ceps[0].getEffectiveness(0), ceps[1].getCost(0), ceps[1].getEffectiveness(0)};
		expectedResults = new double[] { 604.264, 12.59, 635.217, 12.643 };

		Assertions.assertArrayEquals(expectedResults, results, 0.001);

	}

	@Disabled
	@SuppressWarnings("rawtypes")
	@Test public void testDMHEE47PSA() throws Exception {
		// Constants
		String modelFilePath = "networks" + File.separator + "mid" + File.separator + "MID-dmhee-4.7.pgmx";
		// Open the file containing the network
		File f = null;
		URL res = getClass().getClassLoader().getResource(modelFilePath);
		f = Paths.get(res.toURI()).toFile();
		String absolutePath = f.getAbsolutePath();

		// Load the Bayesian network
		PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
		ProbNet probNet = pgmxReader.loadProbNet(absolutePath);

		EvidenceCase evidence = new EvidenceCase();

		// Set cost and effectiveness discounts to all the criteria with that CECriteria. Set the number of cycles and the transition time.
		setOldMethodParameters(probNet, 6.0, 0.0, 20, TransitionTime.BEGINNING);
		//		ProbabilisticCEA ceAnalysis = new ProbabilisticCEA (probNet, evidence, 6.0, 0.0, 20, 5000, TransitionTime.BEGINNING, useMultithreading);

		VECEPSA vecepsa = new VECEPSA(probNet);
		vecepsa.setDecisionVariable(probNet.getNodes(NodeType.DECISION).get(0).getVariable());
		vecepsa.setPreResolutionEvidence(evidence);
		vecepsa.setNumSimulations(5000);
		vecepsa.setUseMultithreading(useMultithreading);

		List<GTablePotential> result = (List<GTablePotential>) vecepsa.getCEPPotentials();
		Assertions.assertNotNull(result);
		Assertions.assertTrue(result.size() > 0);

		double[] expectedResults = new double[] { 50600, 8.935, 44680, 7.991 };

		Assertions.assertEquals(expectedResults[0], ((CEP)result.get(0).elementTable.get(0)).getCost(0), 200);
		Assertions.assertEquals(expectedResults[1], ((CEP)result.get(0).elementTable.get(0)).getEffectiveness(0), 0.01);
		Assertions.assertEquals(expectedResults[2], ((CEP)result.get(0).elementTable.get(1)).getCost(0), 200);
		Assertions.assertEquals(expectedResults[3], ((CEP)result.get(0).elementTable.get(0)).getEffectiveness(0), 0.01);
	}

	@Disabled
	@SuppressWarnings("rawtypes")
	@Test public void testBriggsSA() throws Exception {
		// Constants
		String modelFilePath = "networks" + File.separator + "mid" + File.separator + "MID-dmhee-4.8.pgmx";
		// Open the file containing the network
		File f = null;
		URL res = getClass().getClassLoader().getResource(modelFilePath);
		f = Paths.get(res.toURI()).toFile();
		String absolutePath = f.getAbsolutePath();

		// Load the Bayesian network
		PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
		ProbNet probNet = pgmxReader.loadProbNet(absolutePath);

		// Sex = 0
		EvidenceCase evidence = new EvidenceCase();
		Variable sexVariable = probNet.getVariable("Sex");
		evidence.addFinding(new Finding(sexVariable, 0));

		// Set cost and effectiveness discounts to all the criteria with that CECriteria. Set the number of cycles and the transition time.
		setOldMethodParameters(probNet, 6.0, 1.5, 60, TransitionTime.BEGINNING);

		//		ProbabilisticCEA ceAnalysis = new ProbabilisticCEA (probNet, evidence, 6.0, 1.5, 60, 1000, TransitionTime.BEGINNING, useMultithreading);


		VECEPSA vecepsa = new VECEPSA(probNet);
		vecepsa.setDecisionVariable(probNet.getNodes(NodeType.DECISION).get(0).getVariable());
		vecepsa.setPreResolutionEvidence(evidence);
		vecepsa.setNumSimulations(1000);
		vecepsa.setUseMultithreading(useMultithreading);

		List<GTablePotential> result = (List<GTablePotential>) vecepsa.getCEPPotentials();
		Assertions.assertNotNull(result);
		Assertions.assertTrue(result.size() > 0);

		double[] expectedResults = new double[] { 510.948, 14.666, 609.904, 14.701 };

		Assertions.assertEquals(expectedResults[0], ((CEP)result.get(0).elementTable.get(0)).getCost(0), 2);
		Assertions.assertEquals(expectedResults[1], ((CEP)result.get(0).elementTable.get(0)).getEffectiveness(0), 0.02);
		Assertions.assertEquals(expectedResults[2], ((CEP)result.get(0).elementTable.get(1)).getCost(0), 2);
		Assertions.assertEquals(expectedResults[3], ((CEP)result.get(0).elementTable.get(0)).getEffectiveness(0), 0.02);


		// Sex = 1
		evidence.changeFinding(new Finding(sexVariable, 1));

		vecepsa = new VECEPSA(probNet);
		vecepsa.setDecisionVariable(probNet.getNodes(NodeType.DECISION).get(0).getVariable());
		vecepsa.setPreResolutionEvidence(evidence);
		vecepsa.setNumSimulations(1000);
		vecepsa.setUseMultithreading(useMultithreading);

		result = (List<GTablePotential>) vecepsa.getCEPPotentials();
		expectedResults = new double[] { 604.264, 12.59, 635.217, 12.643 };

		Assertions.assertEquals(expectedResults[0], ((CEP)result.get(0).elementTable.get(0)).getCost(0), 2);
		Assertions.assertEquals(expectedResults[1], ((CEP)result.get(0).elementTable.get(0)).getEffectiveness(0), 0.02);
		Assertions.assertEquals(expectedResults[2], ((CEP)result.get(0).elementTable.get(1)).getCost(0), 2);
		Assertions.assertEquals(expectedResults[3], ((CEP)result.get(0).elementTable.get(0)).getEffectiveness(0), 0.02);
	}

	@Disabled @Test public void testHPV() throws Exception {
		// Constants
		String modelFilePath = "networks" + File.separator + "mid" + File.separator + "MID-HPV.pgmx";
		// Open the file containing the network
		File f = null;
		URL res = getClass().getClassLoader().getResource(modelFilePath);
		f = Paths.get(res.toURI()).toFile();
		String absolutePath = f.getAbsolutePath();
		// Load the Bayesian network
		PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
		ProbNet probNet = pgmxReader.loadProbNet(absolutePath);

		EvidenceCase evidence = new EvidenceCase();

		// Set cost and effectiveness discounts to all the criteria with that CECriteria. Set the number of cycles and the transition time.
		setOldMethodParameters(probNet, 0.0, 0.0, 88, TransitionTime.BEGINNING);
		//		CostEffectivenessAnalysis ceAnalysis = new CostEffectivenessAnalysis(probNet, evidence, 0.0, 0.0, 88, TransitionTime.BEGINNING);

		VECEAnalysis veceAnalysis = new VECEAnalysis(probNet);
		veceAnalysis.setDecisionVariable(probNet.getNodes(NodeType.DECISION).get(0).getVariable());
		veceAnalysis.setPreResolutionEvidence(evidence);

		GTablePotential<?> result = veceAnalysis.getUtility();

		/*List<Variable> variablesInOrder = Arrays.asList(result.getVariable(0), probNet.getVariable("Dec:Test type"),
				probNet.getVariable("Dec:Vaccine"));
		result = DiscretePotentialOperations.reorder(result, variablesInOrder);*/
		double[] expectedResults = new double[] { 1205.296, 59.81, 2897.377, 59.855, 3420.872, 59.86, 1171.416, 60.158,
				2813.055, 60.162, 3332.291, 60.162 };
		Assertions.assertArrayEquals(expectedResults, result.values, 0.001);
	}

	/**
	 * Set old CostEffectivenessAnalysis constructor parameters
	 *
	 * @param probNet
	 * @param costDiscount
	 * @param effectivenessDiscount
	 * @param numberOfSlices
	 * @param transitionTime
	 */
	private void setOldMethodParameters(ProbNet probNet, double costDiscount, double effectivenessDiscount,
			int numberOfSlices, TransitionTime transitionTime) {
		costDiscount /= 100;
		effectivenessDiscount /= 100;
		// Set default unit and value for cycle length
		probNet.getCycleLength().setUnit(Unit.YEAR);
		probNet.getCycleLength().setValue(1);

		// Set number of slices and transition time in temporal options
		probNet.getInferenceOptions().getTemporalOptions().setHorizon(numberOfSlices);
		probNet.getInferenceOptions().getTemporalOptions().setTransition(transitionTime);

		// Set the cost/effectiveness discount to all nodes with that criterion
		for (Node node : probNet.getNodes(NodeType.UTILITY)) {
			Criterion criterion = node.getVariable().getDecisionCriterion();
			if (criterion.getCECriterion() == CECriterion.Cost) {
				criterion.setDiscount(costDiscount);
				criterion.setDiscountUnit(DiscountUnit.CYCLE);
			} else if (criterion.getCECriterion() == CECriterion.Effectiveness) {
				criterion.setDiscount(effectivenessDiscount);
				criterion.setDiscountUnit(DiscountUnit.CYCLE);
			} else {
				System.out.println("Fail");
			}
		}
	}
}
