package org.openmarkov.costEffectiveness;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmarkov.core.gui.costeffectiveness.CostEffectivenessAnalysis;
import org.openmarkov.core.gui.costeffectiveness.ProbabilisticCEA;
import org.openmarkov.core.inference.TransitionTime;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.io.probmodel.PGMXReader;

public class CostEffectivenessAnalysisTests {

    @Before
    public void setUp() throws Exception {

    }    
    
    @Test
    public void testCHAP() throws Exception{
    	// Constants
    	String modelFilePath = "cea\\chap.pgmx";
    	// Open the file containing the network
		InputStream file = getClass().getClassLoader ().
				getResourceAsStream (modelFilePath);

		// Load the Bayesian network
		PGMXReader pgmxReader = new PGMXReader();
		ProbNet probNet = pgmxReader.loadProbNet(file, "CHAP").getProbNet();

		EvidenceCase evidence = new EvidenceCase();
		
		CostEffectivenessAnalysis ceAnalysis = new CostEffectivenessAnalysis(probNet, evidence, 3.0, 3.0, 3, TransitionTime.BEGINNING);
		
		TablePotential result = ceAnalysis.getGlobalUtility();
		
		double[] expectedResults = new double[]{1066.744,1.444,852.399,1.709};
		
    	Assert.assertArrayEquals(expectedResults, result.values, 0.001);
    }
    
    /**
     * Test chap model with a super value cost node
     * @throws Exception
     */
    @Test
    public void testCHAPSV() throws Exception{
    	// Constants
    	String modelFilePath = "cea\\chapSV.pgmx";
    	// Open the file containing the network
		InputStream file = getClass().getClassLoader ().
				getResourceAsStream (modelFilePath);

		// Load the Bayesian network
		PGMXReader pgmxReader = new PGMXReader();
		ProbNet probNet = pgmxReader.loadProbNet(file, "CHAP").getProbNet();

		EvidenceCase evidence = new EvidenceCase();
		
		CostEffectivenessAnalysis ceAnalysis = new CostEffectivenessAnalysis(probNet, evidence, 3.0, 3.0, 3, TransitionTime.BEGINNING);
		
		TablePotential result = ceAnalysis.getGlobalUtility();
		
		double[] expectedResults = new double[]{1066.744,1.444,852.399,1.709};
		
    	Assert.assertArrayEquals(expectedResults, result.values, 0.001);
    }    
    
    @Test
    public void testChancellor() throws Exception{
    	// Constants
    	String modelFilePath = "cea\\MPAD-dmhee-2.5.pgmx";
    	// Open the file containing the network
		InputStream file = getClass().getClassLoader ().
				getResourceAsStream (modelFilePath);

		// Load the Bayesian network
		PGMXReader pgmxReader = new PGMXReader();
		ProbNet probNet = pgmxReader.loadProbNet(file, "Chancellor").getProbNet();

		EvidenceCase evidence = new EvidenceCase();
		
		CostEffectivenessAnalysis ceAnalysis = new CostEffectivenessAnalysis(probNet, evidence, 6.0, 0.0, 20, TransitionTime.BEGINNING);
		
		TablePotential result = ceAnalysis.getGlobalUtility();
		
		double[] expectedResults = new double[]{50585.917,8.935,44662.217,7.991};
		
    	Assert.assertArrayEquals(expectedResults, result.values, 0.001);
    }
    
    @Test
    public void testBriggs() throws Exception{
    	// Constants
    	String modelFilePath = "cea\\MPAD-dmhee-3.5.pgmx";
    	// Open the file containing the network
		InputStream file = getClass().getClassLoader ().
				getResourceAsStream (modelFilePath);

		// Load the Bayesian network
		PGMXReader pgmxReader = new PGMXReader();
		ProbNet probNet = pgmxReader.loadProbNet(file, "Briggs").getProbNet();

		EvidenceCase evidence = new EvidenceCase();
		Variable sexVariable = probNet.getVariable("Sex");
		evidence.addFinding(new Finding(sexVariable, 0));
		
		CostEffectivenessAnalysis ceAnalysis = new CostEffectivenessAnalysis(probNet, evidence, 6.0, 1.5, 60, TransitionTime.BEGINNING);
		
		TablePotential result = ceAnalysis.getGlobalUtility();
		
		double[] expectedResults = new double[]{510.948,14.666,609.904,14.701};
		
    	Assert.assertArrayEquals(expectedResults, result.values, 0.001);
    	
    	evidence.changeFinding(new Finding(sexVariable, 1));
    	
		ceAnalysis = new CostEffectivenessAnalysis(probNet, evidence, 6.0, 1.5, 60, TransitionTime.BEGINNING);
		
		result = ceAnalysis.getGlobalUtility();
		
		expectedResults = new double[]{604.264,12.59,635.217,12.643};
		
    	Assert.assertArrayEquals(expectedResults, result.values, 0.001);
	
    } 
    
    @Test
    public void testChancellorSA() throws Exception{
    	// Constants
    	String modelFilePath = "cea\\MPAD-dmhee-4.7.pgmx";
    	// Open the file containing the network
		InputStream file = getClass().getClassLoader ().
				getResourceAsStream (modelFilePath);

		// Load the Bayesian network
		PGMXReader pgmxReader = new PGMXReader();
		ProbNet probNet = pgmxReader.loadProbNet(file, "Chancellor").getProbNet();

		EvidenceCase evidence = new EvidenceCase();
		
		ProbabilisticCEA ceAnalysis = new ProbabilisticCEA (probNet, evidence, 6.0, 0.0, 20, 5000, TransitionTime.BEGINNING);
		ceAnalysis.run();
		TablePotential result = ceAnalysis.getGlobalUtility();
		
		double[] expectedResults = new double[]{50600,8.935,44680,7.991};
		
    	Assert.assertEquals(expectedResults[0], result.values[0], 200);
    	Assert.assertEquals(expectedResults[1], result.values[1], 0.01);
    	Assert.assertEquals(expectedResults[2], result.values[2], 200);
    	Assert.assertEquals(expectedResults[3], result.values[3], 0.01);
    }   
    
    @Test
    public void testBriggsSA() throws Exception{
    	// Constants
    	String modelFilePath = "cea\\MPAD-dmhee-4.8.pgmx";
    	// Open the file containing the network
		InputStream file = getClass().getClassLoader ().
				getResourceAsStream (modelFilePath);

		// Load the Bayesian network
		PGMXReader pgmxReader = new PGMXReader();
		ProbNet probNet = pgmxReader.loadProbNet(file, "Briggs").getProbNet();

		// Sex = 0
		EvidenceCase evidence = new EvidenceCase();
		Variable sexVariable = probNet.getVariable("Sex");
		evidence.addFinding(new Finding(sexVariable, 0));
		
		ProbabilisticCEA ceAnalysis = new ProbabilisticCEA (probNet, evidence, 6.0, 1.5, 60, 1000, TransitionTime.BEGINNING);
		ceAnalysis.run();
		TablePotential result = ceAnalysis.getGlobalUtility();

		double[] expectedResults = new double[]{513.85,14.664,612.80,14.700};
		
    	Assert.assertEquals(expectedResults[0], result.values[0], 1);
    	Assert.assertEquals(expectedResults[1], result.values[1], 0.01);
    	Assert.assertEquals(expectedResults[2], result.values[2], 1);
    	Assert.assertEquals(expectedResults[3], result.values[3], 0.01);

    	// Sex = 1
		evidence.changeFinding(new Finding(sexVariable, 1));
		ceAnalysis = new ProbabilisticCEA (probNet, evidence, 6.0, 1.5, 60, 1000, TransitionTime.BEGINNING);
		ceAnalysis.run();
		result = ceAnalysis.getGlobalUtility();
		
		expectedResults = new double[]{609.24,12.588,640.46,12.64};
		
    	Assert.assertEquals(expectedResults[0], result.values[0], 1);
    	Assert.assertEquals(expectedResults[1], result.values[1], 0.01);
    	Assert.assertEquals(expectedResults[2], result.values[2], 1);
    	Assert.assertEquals(expectedResults[3], result.values[3], 0.01);
    }        

    @Test
    public void testHPV() throws Exception{
    	// Constants
    	String modelFilePath = "cea\\MPAD-HPV.pgmx";
    	// Open the file containing the network
		InputStream file = getClass().getClassLoader ().
				getResourceAsStream (modelFilePath);

		// Load the Bayesian network
		PGMXReader pgmxReader = new PGMXReader();
		ProbNet probNet = pgmxReader.loadProbNet(file, "MPAD-HPV").getProbNet();

		EvidenceCase evidence = new EvidenceCase();
		
		CostEffectivenessAnalysis ceAnalysis = new CostEffectivenessAnalysis(probNet, evidence, 0.0, 0.0, 88, TransitionTime.BEGINNING);
		
		TablePotential result = ceAnalysis.getGlobalUtility();
		
		double[] expectedResults = new double[]{1205.296,59.81,2897.377,59.855,3420.872,59.86,1171.416,60.158,2813.055,60.162,3332.291,60.162};
		
    	Assert.assertArrayEquals(expectedResults, result.values, 0.001);
    }    
}
