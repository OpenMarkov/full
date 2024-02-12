/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.integrationTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.model.network.CEP;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.ceanalysis.DANDecompositionIntoSymmetricDANsCEA;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.evaluation.DANDecompositionIntoSymmetricDANsEvaluation;
import org.openmarkov.io.probmodel.reader.PGMXReader_0_2;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public class danAlgorithmTests {

	private final double deltaEquals = Math.pow(10, -4);

	@BeforeAll
	public void setUp(){


	}

	@Test
	public void testOneDecision(){
		String networkName = "DAN-one-decision-CE.pgmx";
		String path = "networks/dan/";
		double lambda = 30000;
		String absolutePath = null;
		try {
			URL res = getClass().getClassLoader().getResource(path + networkName);
			File f = f = Paths.get(res.toURI()).toFile();

			absolutePath = f.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
		ProbNetInfo probNetInfo = null;
		try {
			probNetInfo = pgmxReader.loadProbNetInfo(absolutePath);
			ProbNet probNet = probNetInfo.getProbNet();

			CEP resultCEA = new DANDecompositionIntoSymmetricDANsCEA(probNet).getCEP();
			TablePotential resultUNI = new DANDecompositionIntoSymmetricDANsEvaluation(probNet).getUtility();

			Assertions.assertEquals(resultUNI.values[0], resultCEA.getEffectiveness(lambda) * lambda - resultCEA.getCost(lambda), deltaEquals);
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
		PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
		ProbNetInfo probNetInfo = null;
		try {
			URL res = getClass().getClassLoader().getResource(networkName);
			File f = Paths.get(res.toURI()).toFile();
			String absolutePath = f.getAbsolutePath();

			probNetInfo = pgmxReader.loadProbNetInfo(absolutePath);
			ProbNet probNet = probNetInfo.getProbNet();

			CEP resultCEA = new DANDecompositionIntoSymmetricDANsCEA(probNet).getCEP();
			TablePotential resultUNI = new DANDecompositionIntoSymmetricDANsEvaluation(probNet).getUtility();

			Assertions.assertEquals(resultUNI.values[0], resultCEA.getEffectiveness(lambda) * lambda - resultCEA.getCost(lambda), deltaEquals);
		} catch (ParserException e) {
			e.printStackTrace();
		} catch (UnexpectedInferenceException e) {
			e.printStackTrace();
		} catch (NotEvaluableNetworkException e) {
			e.printStackTrace();
		} catch (IncompatibleEvidenceException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (NullPointerException e){
			e.printStackTrace();
		}
	}

	@Test
	public void testDAN3tests(){
		String networkName = "DAN-3tests.pgmx";
		String path = "networks/IDCEAnTherapies/";
		double lambda = 30000;
		PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
		ProbNetInfo probNetInfo = null;
		try {
			URL res = getClass().getClassLoader().getResource(networkName);
			File f = Paths.get(res.toURI()).toFile();
			String absolutePath = f.getAbsolutePath();

			probNetInfo = pgmxReader.loadProbNetInfo(absolutePath);
			ProbNet probNet = probNetInfo.getProbNet();

			CEP resultCEA = new DANDecompositionIntoSymmetricDANsCEA(probNet).getCEP();
			TablePotential resultUNI = new DANDecompositionIntoSymmetricDANsEvaluation(probNet).getUtility();

			Assertions.assertEquals(resultUNI.values[0], resultCEA.getEffectiveness(lambda) * lambda - resultCEA.getCost(lambda), deltaEquals);
		} catch (ParserException e) {
			e.printStackTrace();
		} catch (UnexpectedInferenceException e) {
			e.printStackTrace();
		} catch (NotEvaluableNetworkException e) {
			e.printStackTrace();
		} catch (IncompatibleEvidenceException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}catch (NullPointerException e){
			e.printStackTrace();
		}
	}

	@Test
	public void testDAN4tests(){
		String networkName = "DAN-4tests.pgmx";
		String path = "networks/IDCEAnTherapies/";
		double lambda = 30000;
		PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
		ProbNetInfo probNetInfo = null;
		try {
			URL res = getClass().getClassLoader().getResource(path + networkName);
			File f = f = Paths.get(res.toURI()).toFile();
			String absolutePath = f.getAbsolutePath();

			probNetInfo = pgmxReader.loadProbNetInfo(absolutePath);
			ProbNet probNet = probNetInfo.getProbNet();

			CEP resultCEA = new DANDecompositionIntoSymmetricDANsCEA(probNet).getCEP();
			TablePotential resultUNI = new DANDecompositionIntoSymmetricDANsEvaluation(probNet).getUtility();

			Assertions.assertEquals(resultUNI.values[0], resultCEA.getEffectiveness(lambda) * lambda - resultCEA.getCost(lambda), deltaEquals);
		} catch (ParserException e) {
			e.printStackTrace();
		} catch (UnexpectedInferenceException e) {
			e.printStackTrace();
		} catch (NotEvaluableNetworkException e) {
			e.printStackTrace();
		} catch (IncompatibleEvidenceException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (NullPointerException e){
			e.printStackTrace();
		}
	}


	@Test public void oneChanceCETest() {
		String networkName = "DAN-one-chance-ce.pgmx";
		String path = "networks/dan/";
		double lambda = 30000;
		URL res = getClass().getClassLoader().getResource(path + networkName);
		File f = null;
		try {
			f = Paths.get(res.toURI()).toFile();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		String absolutePath = f.getAbsolutePath();

		PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
		ProbNetInfo probNetInfo = null;
		try {
			probNetInfo = pgmxReader.loadProbNetInfo(absolutePath);
			ProbNet probNet = probNetInfo.getProbNet();

			CEP resultCEA = new DANDecompositionIntoSymmetricDANsCEA(probNet).getCEP();
			TablePotential resultUNI = new DANDecompositionIntoSymmetricDANsEvaluation(probNet).getUtility();

			Assertions.assertEquals(resultUNI.values[0], resultCEA.getEffectiveness(lambda) * lambda - resultCEA.getCost(lambda), deltaEquals);
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
