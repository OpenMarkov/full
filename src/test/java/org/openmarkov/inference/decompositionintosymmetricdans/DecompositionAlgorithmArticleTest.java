package org.openmarkov.inference.decompositionintosymmetricdans;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DecompositionAlgorithmArticleCEA;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DecompositionAlgorithmArticleCEA.DANEvaluationOutputCEA;
import org.openmarkov.io.probmodel.reader.PGMXReader;

public class DecompositionAlgorithmArticleTest {

	// Delta parameter for Assert.Equals methods
	private final double deltaEquals = Math.pow(10,-4);
	private final String dansPath = "networks/dan/";

//	private String networks[] = {"DAN-test-1.pgmx", "DAN-test-2.pgmx", "DAN-test-3.pgmx", 
//			"DAN-test-4.pgmx", "ID-test-1.pgmx", "ID-test-2.pgmx", "ID-test-3.pgmx", "ID-A-D1-D2.pgmx"};

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
		Assert.assertTrue(interventions[0].getRootVariable().getName().equals("Therapy"));
		Assert.assertTrue(interventions[0].getBranches().size() == 1);
		Assert.assertTrue(interventions[0].getBranches().get(0).getStates().size() == 1);
		Assert.assertTrue(interventions[0].getBranches().get(0).getStates().get(0).getName().equals("therapy 1"));

		// Check third intervention
		Assert.assertTrue(interventions[0].getRootVariable().getName().equals("Therapy"));
		Assert.assertTrue(interventions[0].getBranches().size() == 1);
		Assert.assertTrue(interventions[0].getBranches().get(0).getStates().size() == 1);
		Assert.assertTrue(interventions[0].getBranches().get(0).getStates().get(0).getName().equals("therapy 2"));
	}

}
