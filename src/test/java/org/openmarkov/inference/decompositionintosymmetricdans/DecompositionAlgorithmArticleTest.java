package org.openmarkov.inference.decompositionintosymmetricdans;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.model.network.CEP;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.GTablePotential;
import org.openmarkov.core.model.network.potential.Intervention;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DecompositionAlgorithmArticleCEA;
import org.openmarkov.io.probmodel.reader.PGMXReader;

import java.io.InputStream;
import java.util.ArrayList;

@SuppressWarnings("ALL")
public class DecompositionAlgorithmArticleTest {

	// Delta parameter for Assert.Equals methods
	private final double deltaEquals = Math.pow(10,-4);
	private final String dansPath = "networks/dan/";

	@Before
    public void setUp() throws Exception {

    }

	@Test
	public void testDAN1() throws IncompatibleEvidenceException, UnexpectedInferenceException,
			NodeNotFoundException, NotEvaluableNetworkException
	{
		String networkName = dansPath + "DAN-test-1.pgmx";
		InputStream file = getClass().getClassLoader().getResourceAsStream(networkName);

		// Load the network
		PGMXReader pgmxReader = new PGMXReader();
		ProbNetInfo probNetInfo = null;
		try {
			probNetInfo = pgmxReader.loadProbNetInfo(networkName, file);
		} catch (ParserException e) {
			e.printStackTrace();
		}

        ProbNet probNet = probNetInfo.getProbNet();
		EvidenceCase preResolutionEvidence = null;
		if (probNetInfo.getEvidence().size() != 0) {
			preResolutionEvidence = probNetInfo.getEvidence().get(0);
		}

//        VECEAGlobal veGlobalCEA = new VECEAGlobal(probNet, preResolutionEvidence);
//        CEP cep = veGlobalCEA.getCEP();

		DecompositionAlgorithmArticleCEA decompositionAlgorithmArticleCEA = new DecompositionAlgorithmArticleCEA();
		DecompositionAlgorithmArticleCEA.DANEvaluationOutputCEA outputCEA = decompositionAlgorithmArticleCEA.evaluateDSD_CEA(probNet, new ArrayList<Variable>(), preResolutionEvidence);
		CEP cep = (CEP) ((GTablePotential) outputCEA.getUtility()).elementTable.get(0);

		// Check num intervals
		Assert.assertEquals(cep.getNumIntervals(), 3);

		// Check Thresholds
		double[] expectedValues = new double[]{10000.0, 25000.0};
		Assert.assertArrayEquals(cep.getThresholds(), expectedValues, deltaEquals);

		// Check Effectiveness
		expectedValues = new double[]{0.0, 2.0, 4.0};
		Assert.assertArrayEquals(cep.getEffectivities(), expectedValues, deltaEquals);

		// Check Costs
		expectedValues = new double[]{0.0, 20000.0, 70000.0};
		Assert.assertArrayEquals(cep.getCosts(), expectedValues, deltaEquals);

		Intervention[] interventions = cep.getInterventions();
		// Check first intervention
		Assert.assertTrue(interventions[0].getRootVariable().getName().equals("Therapy"));
		Assert.assertTrue(interventions[0].getBranches().size() == 1);
		Assert.assertTrue(interventions[0].getBranches().get(0).getStates().size() == 1);
		Assert.assertTrue(interventions[0].getBranches().get(0).getStates().get(0).getName().equals("no therapy"));

		// Check second intervention
		Assert.assertTrue(interventions[1].getRootVariable().getName().equals("Therapy"));
		Assert.assertTrue(interventions[1].getBranches().size() == 1);
		Assert.assertTrue(interventions[1].getBranches().get(0).getStates().size() == 1);
		Assert.assertTrue(interventions[1].getBranches().get(0).getStates().get(0).getName().equals("therapy 1"));

		// Check third intervention
		Assert.assertTrue(interventions[2].getRootVariable().getName().equals("Therapy"));
		Assert.assertTrue(interventions[2].getBranches().size() == 1);
		Assert.assertTrue(interventions[2].getBranches().get(0).getStates().size() == 1);
		Assert.assertTrue(interventions[2].getBranches().get(0).getStates().get(0).getName().equals("therapy 2"));
	}

    @Test
    public void testDAN2() throws IncompatibleEvidenceException, UnexpectedInferenceException,
            NodeNotFoundException, NotEvaluableNetworkException
    {
        String networkName = dansPath + "DAN-test-2.pgmx";
        InputStream file = getClass().getClassLoader().getResourceAsStream(networkName);

        // Load the network
        PGMXReader pgmxReader = new PGMXReader();
        ProbNetInfo probNetInfo = null;
        try {
            probNetInfo = pgmxReader.loadProbNetInfo(networkName, file);
        } catch (ParserException e) {
            e.printStackTrace();
        }

        ProbNet probNet = probNetInfo.getProbNet();
        EvidenceCase preResolutionEvidence = null;
        if (probNetInfo.getEvidence().size() != 0) {
            preResolutionEvidence = probNetInfo.getEvidence().get(0);
        }

//        VECEAGlobal veGlobalCEA = new VECEAGlobal(probNet, preResolutionEvidence);
//        CEP cep = veGlobalCEA.getCEP();

        DecompositionAlgorithmArticleCEA decompositionAlgorithmArticleCEA = new DecompositionAlgorithmArticleCEA();
        DecompositionAlgorithmArticleCEA.DANEvaluationOutputCEA outputCEA = decompositionAlgorithmArticleCEA.evaluateDSD_CEA(probNet, new ArrayList<Variable>(), preResolutionEvidence);
        CEP cep = (CEP) ((GTablePotential) outputCEA.getUtility()).elementTable.get(0);

        // Check num intervals
        Assert.assertEquals(cep.getNumIntervals(), 2);

        // Check Thresholds
        double[] expectedValues = new double[]{65359.477124182806};
        Assert.assertArrayEquals(cep.getThresholds(), expectedValues, deltaEquals);

        // Check Effectiveness
        expectedValues = new double[]{8.767999999999999, 9.074};
        Assert.assertArrayEquals(cep.getEffectivities(), expectedValues, deltaEquals);

        // Check Costs
        expectedValues = new double[]{0.0, 20000.0};
        Assert.assertArrayEquals(cep.getCosts(), expectedValues, deltaEquals);

        Intervention[] interventions = cep.getInterventions();
        // Check first intervention
        Assert.assertTrue(interventions[0].getRootVariable().getName().equals("Therapy"));
        Assert.assertTrue(interventions[0].getBranches().size() == 1);
        Assert.assertTrue(interventions[0].getBranches().get(0).getStates().size() == 1);
        Assert.assertTrue(interventions[0].getBranches().get(0).getStates().get(0).getName().equals("no therapy"));

        // Check second intervention
        Assert.assertTrue(interventions[1].getRootVariable().getName().equals("Therapy"));
        Assert.assertTrue(interventions[1].getBranches().size() == 1);
        Assert.assertTrue(interventions[1].getBranches().get(0).getStates().size() == 1);
        Assert.assertTrue(interventions[1].getBranches().get(0).getStates().get(0).getName().equals("therapy 1"));
    }

    @Test
    public void testDAN3() throws IncompatibleEvidenceException, UnexpectedInferenceException,
            NodeNotFoundException, NotEvaluableNetworkException
    {
        String networkName = dansPath + "DAN-test-3.pgmx";
        InputStream file = getClass().getClassLoader().getResourceAsStream(networkName);

        // Load the network
        PGMXReader pgmxReader = new PGMXReader();
        ProbNetInfo probNetInfo = null;
        try {
            probNetInfo = pgmxReader.loadProbNetInfo(networkName, file);
        } catch (ParserException e) {
            e.printStackTrace();
        }

        ProbNet probNet = probNetInfo.getProbNet();
        EvidenceCase preResolutionEvidence = null;
        if (probNetInfo.getEvidence().size() != 0) {
            preResolutionEvidence = probNetInfo.getEvidence().get(0);
        }

//        VECEAGlobal veGlobalCEA = new VECEAGlobal(probNet, preResolutionEvidence);
//        CEP cep = veGlobalCEA.getCEP();

        DecompositionAlgorithmArticleCEA decompositionAlgorithmArticleCEA = new DecompositionAlgorithmArticleCEA();
        DecompositionAlgorithmArticleCEA.DANEvaluationOutputCEA outputCEA = decompositionAlgorithmArticleCEA.evaluateDSD_CEA(probNet, new ArrayList<Variable>(), preResolutionEvidence);
        CEP cep = (CEP) ((GTablePotential) outputCEA.getUtility()).elementTable.get(0);

        // Check num intervals
        Assert.assertEquals(cep.getNumIntervals(), 3);

        // Check Thresholds
        double[] expectedValues = new double[]{7142.857142857143, 20000.0};
        Assert.assertArrayEquals(cep.getThresholds(), expectedValues, deltaEquals);

        // Check Effectiveness
        expectedValues = new double[]{8.767999999999999, 9.16, 9.51};
        Assert.assertArrayEquals(cep.getEffectivities(), expectedValues, deltaEquals);

        // Check Costs
        expectedValues = new double[]{0.0, 2800.0000000000005, 9800.000000000002};
        Assert.assertArrayEquals(cep.getCosts(), expectedValues, deltaEquals);

        Intervention[] interventions = cep.getInterventions();
        Intervention intervention;

        // Check first intervention
        intervention = interventions[0];
        Assert.assertTrue(intervention.getRootVariable().getName().equals("Therapy"));
        Assert.assertTrue(intervention.getBranches().size() == 1);
        Assert.assertTrue(intervention.getBranches().get(0).getStates().size() == 1);
        Assert.assertTrue(intervention.getBranches().get(0).getStates().get(0).getName().equals("no therapy"));

        // Check second intervention
        intervention = interventions[1];
        Assert.assertTrue(intervention.getRootVariable().getName().equals("Disease"));
        Assert.assertTrue(intervention.getBranches().size() == 2);
        //Check first branch of the second intervention
        intervention = interventions[1];
        Assert.assertTrue(intervention.getBranches().get(0).getStates().size() == 1);
        Assert.assertTrue(intervention.getBranches().get(0).getStates().get(0).getName().equals("absent"));
        intervention = (Intervention) intervention.getBranches().get(0).getPotential();
        Assert.assertTrue(intervention.getRootVariable().getName().equals("Therapy"));
        Assert.assertTrue(intervention.getBranches().get(0).getStates().size() == 1);
        Assert.assertTrue(intervention.getBranches().get(0).getStates().get(0).getName().equals("no therapy"));
        //Check second branch of the second intervention
        intervention = interventions[1];
        Assert.assertTrue(intervention.getBranches().get(1).getStates().size() == 1);
        Assert.assertTrue(intervention.getBranches().get(1).getStates().get(0).getName().equals("present"));
        intervention = (Intervention) intervention.getBranches().get(1).getPotential();
        Assert.assertTrue(intervention.getRootVariable().getName().equals("Therapy"));
        Assert.assertTrue(intervention.getBranches().get(0).getStates().size() == 1);
        Assert.assertTrue(intervention.getBranches().get(0).getStates().get(0).getName().equals("therapy 1"));

        // Check third intervention
        intervention = interventions[2];
        Assert.assertTrue(intervention.getRootVariable().getName().equals("Disease"));
        Assert.assertTrue(intervention.getBranches().size() == 2);
        //Check first branch of the second intervention
        intervention = interventions[2];
        Assert.assertTrue(intervention.getBranches().get(0).getStates().size() == 1);
        Assert.assertTrue(intervention.getBranches().get(0).getStates().get(0).getName().equals("absent"));
        intervention = (Intervention) intervention.getBranches().get(0).getPotential();
        Assert.assertTrue(intervention.getRootVariable().getName().equals("Therapy"));
        Assert.assertTrue(intervention.getBranches().get(0).getStates().size() == 1);
        Assert.assertTrue(intervention.getBranches().get(0).getStates().get(0).getName().equals("no therapy"));
        //Check second branch of the second intervention
        intervention = interventions[2];
        Assert.assertTrue(intervention.getBranches().get(1).getStates().size() == 1);
        Assert.assertTrue(intervention.getBranches().get(1).getStates().get(0).getName().equals("present"));
        intervention = (Intervention) intervention.getBranches().get(1).getPotential();
        Assert.assertTrue(intervention.getRootVariable().getName().equals("Therapy"));
        Assert.assertTrue(intervention.getBranches().get(0).getStates().size() == 1);
        Assert.assertTrue(intervention.getBranches().get(0).getStates().get(0).getName().equals("therapy 2"));
    }

    @Test
    public void testDAN4() throws IncompatibleEvidenceException, UnexpectedInferenceException,
            NodeNotFoundException, NotEvaluableNetworkException
    {
        String networkName = dansPath + "DAN-test-4.pgmx";
        InputStream file = getClass().getClassLoader().getResourceAsStream(networkName);

        // Load the network
        PGMXReader pgmxReader = new PGMXReader();
        ProbNetInfo probNetInfo = null;
        try {
            probNetInfo = pgmxReader.loadProbNetInfo(networkName, file);
        } catch (ParserException e) {
            e.printStackTrace();
        }

        ProbNet probNet = probNetInfo.getProbNet();
        EvidenceCase preResolutionEvidence = null;
        if (probNetInfo.getEvidence().size() != 0) {
            preResolutionEvidence = probNetInfo.getEvidence().get(0);
        }

        DecompositionAlgorithmArticleCEA decompositionAlgorithmArticleCEA = new DecompositionAlgorithmArticleCEA();
        DecompositionAlgorithmArticleCEA.DANEvaluationOutputCEA outputCEA = decompositionAlgorithmArticleCEA.evaluateDSD_CEA(probNet, new ArrayList<Variable>(), preResolutionEvidence);
        CEP cep = (CEP) ((GTablePotential) outputCEA.getUtility()).elementTable.get(0);

        // Check num intervals
        Assert.assertEquals(cep.getNumIntervals(), 3);

        // Check Thresholds
        double[] expectedValues = new double[]{7142.857142857143, 20000.0};
        Assert.assertArrayEquals(cep.getThresholds(), expectedValues, deltaEquals);

        // Check Effectiveness
        expectedValues = new double[]{8.767999999999999, 9.16, 9.51};
        Assert.assertArrayEquals(cep.getEffectivities(), expectedValues, deltaEquals);

        // Check Costs
        expectedValues = new double[]{0.0, 2800.0000000000005, 9800.000000000002};
        Assert.assertArrayEquals(cep.getCosts(), expectedValues, deltaEquals);

        Intervention[] interventions = cep.getInterventions();
        Intervention intervention;

        // Check first intervention
        intervention = interventions[0];
        Assert.assertTrue(intervention.getRootVariable().getName().equals("Disease"));
        Assert.assertTrue(intervention.getBranches().size() == 2);
        //Check first branch of the second intervention
        intervention = interventions[0];
        Assert.assertTrue(intervention.getBranches().get(0).getStates().size() == 1);
        Assert.assertTrue(intervention.getBranches().get(0).getStates().get(0).getName().equals("absent"));
        intervention = (Intervention) intervention.getBranches().get(0).getPotential();
        Assert.assertTrue(intervention.getRootVariable().getName().equals("Therapy"));
        Assert.assertTrue(intervention.getBranches().get(0).getStates().size() == 1);
        Assert.assertTrue(intervention.getBranches().get(0).getStates().get(0).getName().equals("no therapy"));
        //Check second branch of the second intervention
        intervention = interventions[0];
        Assert.assertTrue(intervention.getBranches().get(1).getStates().size() == 1);
        Assert.assertTrue(intervention.getBranches().get(1).getStates().get(0).getName().equals("present"));
        intervention = (Intervention) intervention.getBranches().get(1).getPotential();
        Assert.assertTrue(intervention.getRootVariable().getName().equals("Therapy"));
        Assert.assertTrue(intervention.getBranches().get(0).getStates().size() == 1);
        Assert.assertTrue(intervention.getBranches().get(0).getStates().get(0).getName().equals("no therapy"));

        // Check second intervention
        intervention = interventions[1];
        Assert.assertTrue(intervention.getRootVariable().getName().equals("Disease"));
        Assert.assertTrue(intervention.getBranches().size() == 2);
        //Check first branch of the second intervention
        intervention = interventions[1];
        Assert.assertTrue(intervention.getBranches().get(0).getStates().size() == 1);
        Assert.assertTrue(intervention.getBranches().get(0).getStates().get(0).getName().equals("absent"));
        intervention = (Intervention) intervention.getBranches().get(0).getPotential();
        Assert.assertTrue(intervention.getRootVariable().getName().equals("Therapy"));
        Assert.assertTrue(intervention.getBranches().get(0).getStates().size() == 1);
        Assert.assertTrue(intervention.getBranches().get(0).getStates().get(0).getName().equals("no therapy"));
        //Check second branch of the second intervention
        intervention = interventions[1];
        Assert.assertTrue(intervention.getBranches().get(1).getStates().size() == 1);
        Assert.assertTrue(intervention.getBranches().get(1).getStates().get(0).getName().equals("present"));
        intervention = (Intervention) intervention.getBranches().get(1).getPotential();
        Assert.assertTrue(intervention.getRootVariable().getName().equals("Therapy"));
        Assert.assertTrue(intervention.getBranches().get(0).getStates().size() == 1);
        Assert.assertTrue(intervention.getBranches().get(0).getStates().get(0).getName().equals("therapy 1"));

        // Check third intervention
        intervention = interventions[2];
        Assert.assertTrue(intervention.getRootVariable().getName().equals("Disease"));
        Assert.assertTrue(intervention.getBranches().size() == 2);
        //Check first branch of the second intervention
        intervention = interventions[2];
        Assert.assertTrue(intervention.getBranches().get(0).getStates().size() == 1);
        Assert.assertTrue(intervention.getBranches().get(0).getStates().get(0).getName().equals("absent"));
        intervention = (Intervention) intervention.getBranches().get(0).getPotential();
        Assert.assertTrue(intervention.getRootVariable().getName().equals("Therapy"));
        Assert.assertTrue(intervention.getBranches().get(0).getStates().size() == 1);
        Assert.assertTrue(intervention.getBranches().get(0).getStates().get(0).getName().equals("no therapy"));
        //Check second branch of the second intervention
        intervention = interventions[2];
        Assert.assertTrue(intervention.getBranches().get(1).getStates().size() == 1);
        Assert.assertTrue(intervention.getBranches().get(1).getStates().get(0).getName().equals("present"));
        intervention = (Intervention) intervention.getBranches().get(1).getPotential();
        Assert.assertTrue(intervention.getRootVariable().getName().equals("Therapy"));
        Assert.assertTrue(intervention.getBranches().get(0).getStates().size() == 1);
        Assert.assertTrue(intervention.getBranches().get(0).getStates().get(0).getName().equals("therapy 2"));
    }

}
