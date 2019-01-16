/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.integrationTests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.model.network.CEP;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.CEADecompositionIntoSymmetricDANsEvaluation;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DecompositionIntoSymmetricDANsEvaluation;
import org.openmarkov.io.probmodel.reader.PGMXReader_0_2;

import java.io.InputStream;

public class danAlgorithmTests {

	private final double deltaEquals = Math.pow(10, -4);

	@Before
	public void setUp(){


	}

	@Test
	public void testOneDecision(){
		String networkName = "DAN-one-decision-CE.pgmx";
		String path = "networks/dan/";
		double lambda = 30000;
		InputStream file = getClass().getClassLoader().getResourceAsStream(path + networkName);
		PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
		ProbNetInfo probNetInfo = null;
		try {
			probNetInfo = pgmxReader.loadProbNetInfo(networkName, file);
			ProbNet probNet = probNetInfo.getProbNet();

			CEP resultCEA = new CEADecompositionIntoSymmetricDANsEvaluation(probNet).getCEP();
			TablePotential resultUNI = new DecompositionIntoSymmetricDANsEvaluation(probNet).getUtility();

			Assert.assertEquals(resultUNI.values[0], resultCEA.getEffectiveness(lambda) * lambda - resultCEA.getCost(lambda), deltaEquals);
		} catch (ParserException e) {
			e.printStackTrace();
		} catch (UnexpectedInferenceException e) {
			e.printStackTrace();
		} catch (NotEvaluableNetworkException e) {
			e.printStackTrace();
		} catch (IncompatibleEvidenceException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDAN2tests(){
		String networkName = "DAN-2tests.pgmx";
		String path = "networks/IDCEAnTherapies/";
		double lambda = 30000;
		InputStream file = getClass().getClassLoader().getResourceAsStream(path + networkName);
		PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
		ProbNetInfo probNetInfo = null;
		try {
			probNetInfo = pgmxReader.loadProbNetInfo(networkName, file);
			ProbNet probNet = probNetInfo.getProbNet();

			CEP resultCEA = new CEADecompositionIntoSymmetricDANsEvaluation(probNet).getCEP();
			TablePotential resultUNI = new DecompositionIntoSymmetricDANsEvaluation(probNet).getUtility();

			Assert.assertEquals(resultUNI.values[0], resultCEA.getEffectiveness(lambda) * lambda - resultCEA.getCost(lambda), deltaEquals);
		} catch (ParserException e) {
			e.printStackTrace();
		} catch (UnexpectedInferenceException e) {
			e.printStackTrace();
		} catch (NotEvaluableNetworkException e) {
			e.printStackTrace();
		} catch (IncompatibleEvidenceException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDAN3tests(){
		String networkName = "DAN-3tests.pgmx";
		String path = "networks/IDCEAnTherapies/";
		double lambda = 30000;
		InputStream file = getClass().getClassLoader().getResourceAsStream(path + networkName);
		PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
		ProbNetInfo probNetInfo = null;
		try {
			probNetInfo = pgmxReader.loadProbNetInfo(networkName, file);
			ProbNet probNet = probNetInfo.getProbNet();

			CEP resultCEA = new CEADecompositionIntoSymmetricDANsEvaluation(probNet).getCEP();
			TablePotential resultUNI = new DecompositionIntoSymmetricDANsEvaluation(probNet).getUtility();

			Assert.assertEquals(resultUNI.values[0], resultCEA.getEffectiveness(lambda) * lambda - resultCEA.getCost(lambda), deltaEquals);
		} catch (ParserException e) {
			e.printStackTrace();
		} catch (UnexpectedInferenceException e) {
			e.printStackTrace();
		} catch (NotEvaluableNetworkException e) {
			e.printStackTrace();
		} catch (IncompatibleEvidenceException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDAN4tests(){
		String networkName = "DAN-4tests.pgmx";
		String path = "networks/IDCEAnTherapies/";
		double lambda = 30000;
		InputStream file = getClass().getClassLoader().getResourceAsStream(path + networkName);
		PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
		ProbNetInfo probNetInfo = null;
		try {
			probNetInfo = pgmxReader.loadProbNetInfo(networkName, file);
			ProbNet probNet = probNetInfo.getProbNet();

			CEP resultCEA = new CEADecompositionIntoSymmetricDANsEvaluation(probNet).getCEP();
			TablePotential resultUNI = new DecompositionIntoSymmetricDANsEvaluation(probNet).getUtility();

			Assert.assertEquals(resultUNI.values[0], resultCEA.getEffectiveness(lambda) * lambda - resultCEA.getCost(lambda), deltaEquals);
		} catch (ParserException e) {
			e.printStackTrace();
		} catch (UnexpectedInferenceException e) {
			e.printStackTrace();
		} catch (NotEvaluableNetworkException e) {
			e.printStackTrace();
		} catch (IncompatibleEvidenceException e) {
			e.printStackTrace();
		}
	}
}
