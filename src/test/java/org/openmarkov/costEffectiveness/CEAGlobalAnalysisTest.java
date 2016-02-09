//package org.openmarkov.costEffectiveness;
//
//import java.io.File;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.openmarkov.core.gui.costeffectiveness.CostEffectivenessAnalysis;
//import org.openmarkov.core.gui.costeffectiveness.ProbabilisticCEA;
//import org.openmarkov.core.inference.TransitionTime;
//import org.openmarkov.core.model.network.Criterion;
//import org.openmarkov.core.model.network.Criterion.CECriterion;
//import org.openmarkov.core.model.network.EvidenceCase;
//import org.openmarkov.core.model.network.Finding;
//import org.openmarkov.core.model.network.Node;
//import org.openmarkov.core.model.network.NodeType;
//import org.openmarkov.core.model.network.ProbNet;
//import org.openmarkov.core.model.network.Variable;
//import org.openmarkov.core.model.network.CycleLength.DiscountUnit;
//import org.openmarkov.core.model.network.CycleLength.Unit;
//import org.openmarkov.core.model.network.potential.TablePotential;
//import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;
//import org.openmarkov.inference.tasks.VariableElimination.VEResolution;
//import org.openmarkov.io.probmodel.PGMXReader;
//
//public class CEAGlobalAnalysisTest {
//
//	private boolean useMultithreading = true;
//
//    @Before
//    public void setUp() throws Exception {
//
//    }
//
//    //@Test
//    public void testCHAP() throws Exception{
//    	// Constants
//    	String modelFilePath = "cea" + File.separator +"chap.pgmx";
//    	// Open the file containing the network
//		InputStream file = getClass().getClassLoader ().
//				getResourceAsStream (modelFilePath);
//
//		// Load the Bayesian network
//		PGMXReader pgmxReader = new PGMXReader();
//		ProbNet probNet = pgmxReader.loadProbNet(file, "CHAP").getProbNet();
//
//		EvidenceCase evidence = new EvidenceCase();
//
//		// Set cost and effectiveness discounts to all the criteria with that CECriteria. Set the number of cycles and the transition time.
//		setOldMethodParameters(probNet, 3.0,3.0,3, TransitionTime.BEGINNING);
//
//		//CostEffectivenessAnalysis ceAnalysis = new CostEffectivenessAnalysis(probNet, evidence, 3.0, 3.0, 3, TransitionTime.BEGINNING);
//		CostEffectivenessAnalysis ceAnalysis = new CostEffectivenessAnalysis(probNet,probNet.getNodes(NodeType.DECISION).get(0).getVariable(), evidence);
//
//		TablePotential result = ceAnalysis.getCostEffectivenessTable();
//
//		double[] expectedResults = new double[]{1066.744,1.444,852.399,1.709};
//
//    	Assert.assertArrayEquals(expectedResults, result.values, 0.001);
//    }
//
//
//	/**
//     * Test chap model with a super value cost node
//     * @throws Exception
//     */
//    //@Test
//    public void testCHAPSV() throws Exception{
//    	// Constants
//    	String modelFilePath = "cea" + File.separator +"chap-sv.pgmx";
//    	// Open the file containing the network
//		InputStream file = getClass().getClassLoader ().
//				getResourceAsStream (modelFilePath);
//
//		// Load the Bayesian network
//		PGMXReader pgmxReader = new PGMXReader();
//		ProbNet probNet = pgmxReader.loadProbNet(file, "CHAP").getProbNet();
//
//		EvidenceCase evidence = new EvidenceCase();
//
//		// Set cost and effectiveness discounts to all the criteria with that CECriteria. Set the number of cycles and the transition time.
//		setOldMethodParameters(probNet, 3.0 , 3.0 , 3 , TransitionTime.BEGINNING);
//
////		CostEffectivenessAnalysis ceAnalysis = new CostEffectivenessAnalysis(probNet, evidence, 3.0, 3.0, 3, TransitionTime.BEGINNING);
//		CostEffectivenessAnalysis ceAnalysis = new CostEffectivenessAnalysis(probNet,probNet.getNodes(NodeType.DECISION).get(0).getVariable(), evidence);
//
//		TablePotential result = ceAnalysis.getCostEffectivenessTable();
//
//		double[] expectedResults = new double[]{1066.744,1.444,852.399,1.709};
//
//    	Assert.assertArrayEquals(expectedResults, result.values, 0.001);
//    }
//
//    @Test
//    public void testChancellor() throws Exception{
//    	// Constants
//    	String modelFilePath = "cea" + File.separator +"MID-dmhee-2.5.pgmx";
//    	// Open the file containing the network
//		InputStream file = getClass().getClassLoader ().
//				getResourceAsStream (modelFilePath);
//
//		// Load the Bayesian network
//		PGMXReader pgmxReader = new PGMXReader();
//		ProbNet probNet = pgmxReader.loadProbNet(file, "Chancellor").getProbNet();
//
//		EvidenceCase evidence = new EvidenceCase();
//
//		// Set cost and effectiveness discounts to all the criteria with that CECriteria. Set the number of cycles and the transition time.
//		setOldMethodParameters(probNet, 6.0 , 0.0 , 20 , TransitionTime.BEGINNING);
//
//		// CostEffectivenessAnalysis ceAnalysis = new CostEffectivenessAnalysis(probNet, evidence, 6.0, 0.0, 20, TransitionTime.BEGINNING);
//		CostEffectivenessAnalysis ceAnalysis = new CostEffectivenessAnalysis(probNet,probNet.getNodes(NodeType.DECISION).get(0).getVariable(), evidence);
//
//		TablePotential result = ceAnalysis.getCostEffectivenessTable();
//
//		double[] expectedResults = new double[]{50585.9167,8.9346,44662.2166,7.99134};
//
//    	Assert.assertArrayEquals(expectedResults, result.values, 0.001);
//    }
//
//    @Test
//    public void testChancellorHC() throws Exception{
//    	// Constants
//    	String modelFilePath = "cea" + File.separator +"MID-dmhee-2.5.pgmx";
//    	// Open the file containing the network
//		InputStream file = getClass().getClassLoader ().
//				getResourceAsStream (modelFilePath);
//
//		// Load the Bayesian network
//		PGMXReader pgmxReader = new PGMXReader();
//		ProbNet probNet = pgmxReader.loadProbNet(file, "Chancellor").getProbNet();
//
//		EvidenceCase evidence = new EvidenceCase();
//
//		// Set cost and effectiveness discounts to all the criteria with that CECriteria. Set the number of cycles and the transition time.
//		setOldMethodParameters(probNet, 6.0 , 0.0 , 20 , TransitionTime.HALF);
//
////		CostEffectivenessAnalysis ceAnalysis = new CostEffectivenessAnalysis(probNet, evidence, 6.0, 0.0, 20, TransitionTime.HALF);
//		CostEffectivenessAnalysis ceAnalysis = new CostEffectivenessAnalysis(probNet,probNet.getNodes(NodeType.DECISION).get(0).getVariable(), evidence);
//
//		TablePotential result = ceAnalysis.getCostEffectivenessTable();
//
//		double[] expectedResults = new double[]{50585.917,9.412,44662.217,8.471};
//    	Assert.assertArrayEquals(expectedResults, result.values, 0.001);
//    }
//
//	@Test
//	public void testChancellorUnicriterion() throws Exception{
//		// Constants
//		String modelFilePath = "cea" + File.separator +"MID-Chancellor-Unicriterion.pgmx";
//		// Open the file containing the network
//		InputStream file = getClass().getClassLoader ().
//				getResourceAsStream (modelFilePath);
//
//		// Load the Bayesian network
//		PGMXReader pgmxReader = new PGMXReader();
//		ProbNet probNet = pgmxReader.loadProbNet(file, "Chancellor").getProbNet();
//
//		EvidenceCase evidence = new EvidenceCase();
//		List<Variable> conditioningVariables = new ArrayList<Variable>();
//
//		double wtp = 30000;
//		for(Criterion criterion : probNet.getDecisionCriteria()){
//			if(criterion.getCECriterion().equals(CECriterion.Cost)) {
//				criterion.setUnicriteriaScale(-1);
//			} else if (criterion.getCECriterion().equals(CECriterion.Effectiveness)){
//				criterion.setUnicriteriaScale(wtp);
//			}
//		}
//
//		VEResolution veResolution = new VEResolution(probNet, evidence, conditioningVariables);
//		double globalUtility = veResolution.getGlobalUtility().getValues()[0];
//		Assert.assertEquals(globalUtility, 195546.556793745, Math.pow(10,-8));
//
//		wtp = 8000;
//		for(Criterion criterion : probNet.getDecisionCriteria()){
//			if(criterion.getCECriterion().equals(CECriterion.Cost)) {
//				criterion.setUnicriteriaScale(-1);
//			} else if (criterion.getCECriterion().equals(CECriterion.Effectiveness)){
//				criterion.setUnicriteriaScale(wtp);
//			}
//		}
//
//		veResolution = new VEResolution(probNet, evidence, conditioningVariables);
//		globalUtility = veResolution.getGlobalUtility().getValues()[0];
//		Assert.assertEquals(globalUtility, 184.440530353197, Math.pow(10, -8));
//
//	}
//
//    @Test
//    public void testChancellorSV() throws Exception{
//    	// Constants
//    	String modelFilePath = "cea" + File.separator +"MID-dmhee-2.5-sv.pgmx";
//    	// Open the file containing the network
//		InputStream file = getClass().getClassLoader ().
//				getResourceAsStream (modelFilePath);
//
//		// Load the Bayesian network
//		PGMXReader pgmxReader = new PGMXReader();
//		ProbNet probNet = pgmxReader.loadProbNet(file, "Chancellor").getProbNet();
//
//		EvidenceCase evidence = new EvidenceCase();
//
//		// Set cost and effectiveness discounts to all the criteria with that CECriteria. Set the number of cycles and the transition time.
//		setOldMethodParameters(probNet, 6.0 , 0.0 , 20 , TransitionTime.BEGINNING);
//
////		CostEffectivenessAnalysis ceAnalysis = new CostEffectivenessAnalysis(probNet, evidence, 6.0, 0.0, 20, TransitionTime.BEGINNING);
//		CostEffectivenessAnalysis ceAnalysis = new CostEffectivenessAnalysis(probNet, probNet.getNodes(NodeType.DECISION).get(0).getVariable(), evidence);
//
//		TablePotential result = ceAnalysis.getCostEffectivenessTable();
//
//		double[] expectedResults = new double[]{50585.917,8.935,44662.217,7.991};
//
//    	Assert.assertArrayEquals(expectedResults, result.values, 0.001);
//    }
//
//    @Test
//    public void testBriggs() throws Exception{
//    	// Constants
//    	String modelFilePath = "cea" + File.separator +"MID-dmhee-3.5.pgmx";
//    	// Open the file containing the network
//		InputStream file = getClass().getClassLoader ().
//				getResourceAsStream (modelFilePath);
//
//		// Load the Bayesian network
//		PGMXReader pgmxReader = new PGMXReader();
//		ProbNet probNet = pgmxReader.loadProbNet(file, "Briggs").getProbNet();
//
//		EvidenceCase evidence = new EvidenceCase();
//		Variable sexVariable = probNet.getVariable("Sex");
//		evidence.addFinding(new Finding(sexVariable, 0));
//
//		// Set cost and effectiveness discounts to all the criteria with that CECriteria. Set the number of cycles and the transition time.
//		setOldMethodParameters(probNet, 6.0 , 1.5 , 60 , TransitionTime.BEGINNING);
//
////		CostEffectivenessAnalysis ceAnalysis = new CostEffectivenessAnalysis(probNet, evidence, 6.0, 1.5, 60, TransitionTime.BEGINNING);
//		CostEffectivenessAnalysis ceAnalysis = new CostEffectivenessAnalysis(probNet, probNet.getNodes(NodeType.DECISION).get(0).getVariable(),evidence);
//
//		TablePotential result = ceAnalysis.getCostEffectivenessTable();
//
//		double[] expectedResults = new double[]{510.948,14.666,609.904,14.701};
//
//    	Assert.assertArrayEquals(expectedResults, result.values, 0.001);
//
//    	evidence.changeFinding(new Finding(sexVariable, 1));
//
//    	// The old parameters do not change in the execution
//
////		ceAnalysis = new CostEffectivenessAnalysis(probNet, evidence, 6.0, 1.5, 60, TransitionTime.BEGINNING);
//    	ceAnalysis = new CostEffectivenessAnalysis(probNet, probNet.getNodes(NodeType.DECISION).get(0).getVariable(), evidence);
//
//		result = ceAnalysis.getCostEffectivenessTable();
//
//		expectedResults = new double[]{604.264,12.59,635.217,12.643};
//
//    	Assert.assertArrayEquals(expectedResults, result.values, 0.001);
//
//    }
//
//    @Test
//    public void testChancellorSA() throws Exception{
//    	// Constants
//    	String modelFilePath = "cea" + File.separator +"MID-dmhee-4.7.pgmx";
//    	// Open the file containing the network
//		InputStream file = getClass().getClassLoader ().
//				getResourceAsStream (modelFilePath);
//
//		// Load the Bayesian network
//		PGMXReader pgmxReader = new PGMXReader();
//		ProbNet probNet = pgmxReader.loadProbNet(file, "Chancellor").getProbNet();
//
//		EvidenceCase evidence = new EvidenceCase();
//
//		// Set cost and effectiveness discounts to all the criteria with that CECriteria. Set the number of cycles and the transition time.
//		setOldMethodParameters(probNet, 6.0 , 0.0 , 20 , TransitionTime.BEGINNING);
//
////		ProbabilisticCEA ceAnalysis = new ProbabilisticCEA (probNet, evidence, 6.0, 0.0, 20, 5000, TransitionTime.BEGINNING, useMultithreading);
//		ProbabilisticCEA ceAnalysis = new ProbabilisticCEA (probNet, probNet.getNodes(NodeType.DECISION).get(0).getVariable(), evidence, 5000, useMultithreading);
//		ceAnalysis.run();
//		TablePotential result = ceAnalysis.getCostEffectivenessTable();
//
//		double[] expectedResults = new double[]{50600,8.935,44680,7.991};
//
//    	Assert.assertEquals(expectedResults[0], result.values[0], 200);
//    	Assert.assertEquals(expectedResults[1], result.values[1], 0.01);
//    	Assert.assertEquals(expectedResults[2], result.values[2], 200);
//    	Assert.assertEquals(expectedResults[3], result.values[3], 0.01);
//    }
//
//    @Test
//    public void testBriggsSA() throws Exception{
//    	// Constants
//    	String modelFilePath = "cea" + File.separator +"MID-dmhee-4.8.pgmx";
//    	// Open the file containing the network
//		InputStream file = getClass().getClassLoader ().
//				getResourceAsStream (modelFilePath);
//
//		// Load the Bayesian network
//		PGMXReader pgmxReader = new PGMXReader();
//		ProbNet probNet = pgmxReader.loadProbNet(file, "Briggs").getProbNet();
//
//		// Sex = 0
//		EvidenceCase evidence = new EvidenceCase();
//		Variable sexVariable = probNet.getVariable("Sex");
//		evidence.addFinding(new Finding(sexVariable, 0));
//
//		// Set cost and effectiveness discounts to all the criteria with that CECriteria. Set the number of cycles and the transition time.
//		setOldMethodParameters(probNet, 6.0 , 1.5 , 60 , TransitionTime.BEGINNING);
//
////		ProbabilisticCEA ceAnalysis = new ProbabilisticCEA (probNet, evidence, 6.0, 1.5, 60, 1000, TransitionTime.BEGINNING, useMultithreading);
//		ProbabilisticCEA ceAnalysis = new ProbabilisticCEA (probNet, probNet.getNodes(NodeType.DECISION).get(0).getVariable(), evidence, 1000, useMultithreading);
//		ceAnalysis.run();
//		TablePotential result = ceAnalysis.getCostEffectivenessTable();
//
//		double[] expectedResults = new double[]{510.948,14.666,609.904,14.701};
//
//    	Assert.assertEquals(expectedResults[0], result.values[0], 2);
//    	Assert.assertEquals(expectedResults[1], result.values[1], 0.02);
//    	Assert.assertEquals(expectedResults[2], result.values[2], 2);
//    	Assert.assertEquals(expectedResults[3], result.values[3], 0.02);
//
//    	// Sex = 1
//		evidence.changeFinding(new Finding(sexVariable, 1));
//
////		ceAnalysis = new ProbabilisticCEA (probNet, evidence, 6.0, 1.5, 60, 1000, TransitionTime.BEGINNING, useMultithreading);
//		ceAnalysis = new ProbabilisticCEA (probNet,probNet.getVariables(NodeType.DECISION).get(0), evidence, 1000, useMultithreading);
//		ceAnalysis.run();
//		result = ceAnalysis.getCostEffectivenessTable();
//
//		expectedResults = new double[]{604.264,12.59,635.217,12.643};
//
//    	Assert.assertEquals(expectedResults[0], result.values[0], 2);
//    	Assert.assertEquals(expectedResults[1], result.values[1], 0.02);
//    	Assert.assertEquals(expectedResults[2], result.values[2], 2);
//    	Assert.assertEquals(expectedResults[3], result.values[3], 0.02);
//    }
//
//    @Test
//    public void testHPV() throws Exception{
//    	// Constants
//    	String modelFilePath = "cea" + File.separator +"MID-HPV.pgmx";
//    	// Open the file containing the network
//		InputStream file = getClass().getClassLoader ().
//				getResourceAsStream (modelFilePath);
//
//		// Load the Bayesian network
//		PGMXReader pgmxReader = new PGMXReader();
//		ProbNet probNet = pgmxReader.loadProbNet(file, "MID-HPV").getProbNet();
//
//		EvidenceCase evidence = new EvidenceCase();
//
//		// Set cost and effectiveness discounts to all the criteria with that CECriteria. Set the number of cycles and the transition time.
//		setOldMethodParameters(probNet, 0.0 , 0.0 , 88 , TransitionTime.BEGINNING);
//
////		CostEffectivenessAnalysis ceAnalysis = new CostEffectivenessAnalysis(probNet, evidence, 0.0, 0.0, 88, TransitionTime.BEGINNING);
//		CostEffectivenessAnalysis ceAnalysis = new CostEffectivenessAnalysis(probNet,probNet.getNodes(NodeType.DECISION).get(0).getVariable(), evidence);
//
//		TablePotential result = ceAnalysis.getCostEffectivenessTable();
//
//		List<Variable> variablesInOrder = Arrays.asList(result.getVariable(0),
//				probNet.getVariable("Dec:Test type"), probNet.getVariable("Dec:Vaccine"));
//		result = DiscretePotentialOperations.reorder(result, variablesInOrder);
//		double[] expectedResults = new double[]{1205.296,59.81,2897.377,59.855,3420.872,59.86,1171.416,60.158,2813.055,60.162,3332.291,60.162};
//		Assert.assertArrayEquals(expectedResults, result.values, 0.001);
//    }
//
//    /**
//     * Set old CostEffectivenessAnalysis constructor parameters
//     * @param probNet
//     * @param costDiscount
//     * @param effectivenessDiscount
//     * @param numberOfSlices
//     * @param transitionTime
//     */
//    private void setOldMethodParameters(ProbNet probNet, double costDiscount, double effectivenessDiscount,
//			int numberOfSlices, TransitionTime transitionTime) {
//    	costDiscount /= 100;
//    	effectivenessDiscount /= 100;
//		// Set default unit and value for cycle length
//    	probNet.getCycleLength().setUnit(Unit.YEAR);
//		probNet.getCycleLength().setValue(1);
//
//		// Set number of slices and transition time in temporal options
//		probNet.getInferenceOptions().getTemporalOptions().setNumberOfSlices(numberOfSlices);
//		probNet.getInferenceOptions().getTemporalOptions().setTransition(transitionTime);
//
//		// Set the cost/effectiveness discount to all nodes with that criterion
//		for(Node node : probNet.getNodes(NodeType.UTILITY)){
//			Criterion criterion = node.getVariable().getDecisionCriterion();
//			if(criterion.getCECriterion() == CECriterion.Cost){
//				criterion.setDiscount(costDiscount);
//				criterion.setDiscountUnit(DiscountUnit.CYCLE);
//			}else if(criterion.getCECriterion() == CECriterion.Effectiveness){
//				criterion.setDiscount(effectivenessDiscount);
//				criterion.setDiscountUnit(DiscountUnit.CYCLE);
//			}else{
//				System.out.println("Fail");
//			}
//		}
//	}
//}
