/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.inference.decompositionintosymmetricdans.evaluation;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.inference.decompositionintosymmetricdans.NetworkEvaluationInferenceTest;
import org.openmarkov.inference.decompositionintosymmetricdans.Tools;

public abstract class DANEvaluationTest extends NetworkEvaluationInferenceTest {

	




	@Before public void setUp() throws Exception {
	}




	@Override
	protected ProbNet loadNetwork(String networkName) {
		Tools t = new Tools();
		return t.loadDAN(networkName);
	}




	@Test public void testDANOnlyUtility()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		testNetworkEvaluation("only-utility", 10.0);
	}

	@Test public void testDANOneChance()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		testNetworkEvaluation("one-chance", 83.7);
	}

	@Test public void testDANOneDecision()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		testNetworkEvaluation("one-decision", 87.4, "D");
	}

	@Test public void testDANNoKnowledge()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		testNetworkEvaluation("no-knowledge", 9.16, "D");
	}

	@Test public void testDANPerfectKnowledge()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		testNetworkEvaluation("perfect-knowledge", 9.72, "A", "D");
	}

	@Test public void testDANTest2Therapies()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		testNetworkEvaluation("test-2therapies", 9.39366, "Test", "Therapy");
	}

	@Test public void testDANTest2TherapiesNoCostSymmetrizedOrderForced()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		testNetworkEvaluation("decide-test-2therapies-no-cost-symmetrized-order-forced", 9.39366, "Do test?",
				"Result of test", "Therapy");
	}

	@Test public void testDANTest2TherapiesNoCostOrderForced()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		testNetworkEvaluation("decide-test-2therapies-no-cost-order-forced", 9.39366, "Do test?", "Result of test",
				"Therapy");
	}

	@Test public void testDANTest2TherapiesNoCost()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		testNetworkEvaluation("decide-test-2therapies-no-cost", 9.39366, "Do test?", "Result of test", "Therapy");
	}

	@Test public void testDANUIDsPaper()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		testNetworkEvaluation("UID-luque2016-OM-0-2-0", 10, "OD", "D", "X", "E");

	}

	@Test public void testDANDiabetes()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		testNetworkEvaluation("diabetes", 979.8337, "Symptom", "OD", "Dec: Blood Test", "Dec: Urine test",
				"Blood test result", "Urine test result", "Therapy");

	}

	@Test public void testDANSimplifiedUsedCarBuyer()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		testNetworkEvaluation("simplified-used-car-buyer", 32.96, "Dec: First Test", "First Result", "Dec: Purchase");

	}

	@Test public void testDANUsedCarBuyer()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		//testDANEvaluation("used-car-buyer",32.96,"Dec: First Test","First Result","Dec: Second Test","Dec: Purchase");
		testNetworkEvaluation("used-car-buyer", 32.96);

	}

	@Test public void testDANReactor()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		testNetworkEvaluation("reactor", 8.1280, "Test decision", "Result of test", "Build decision");

	}

	@Test public void testDANKingNobleDescentYesFirstTask1()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		testNetworkEvaluation("king-noble-descent-yes-first-task-1", 9.03);

	}

	@Test public void testDANKingNobleDescentYesFirstTask1SecondTask2()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		testNetworkEvaluation("king-noble-descent-yes-first-task-1-second-task-2", 9.03);
	}

	@Test public void testDANSimplifiedTwoTasksKingNobleDescentYes()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		testNetworkEvaluation("simplified-two-tasks-king-noble-descent-yes", 9.08);
	}

	/**
	 * This network is as "simplified-two-tasks-king-noble-descent-yes", but removing zero utility potentials.
	 */
	@Test public void testDANSimplified2TwoTasksKingNobleDescentYes()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		testNetworkEvaluation("simplified-2-two-tasks-king-noble-descent-yes", 9.08);
	}

	/**
	 * This network is as "simplified-two-tasks-king-noble-descent-yes", but removing zero utility potentials.
	 */
	@Test public void testDANSimplifiedOneTaskKingNobleDescentYes()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		testNetworkEvaluation("simplified-one-task-king-noble-descent-yes", 9.28);
	}

	@Test public void testDANKingNobleDescentNo()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		testNetworkEvaluation("king-noble-descent-no", 6.43);

	}

	@Test public void testDANKingNobleDescentYes()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		testNetworkEvaluation("king-noble-descent-yes", 9.03);
	}

	@Test public void testDANKing()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		testNetworkEvaluation("king", 7.73);
	}

	@Test public void testDAN3Tests()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		testNetworkEvaluation("3-test-problem", 9.6162, "Symptom", "OD", "Dec: Test 0", "Dec: Test 1", "Dec: Test 2",
				"Test Result 1", "Test Result 2", "Therapy");
	}
	
	
	 @Ignore @Test public void testDANTutorial33()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		testNetworkEvaluation("tutorial-3-3", 7.73);
	}

	//@Test
	public void testDANDating()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		testNetworkEvaluation("dating", 9.4076);

	}

	//@Test
	//TODO DAN-mediastinet has super-value nodes. It must be converted into a DAN with only ordinary utility nodes.
	public void testDANMediastinet()
			throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException,
			NotEvaluableNetworkException {
		testNetworkEvaluation("mediastinet", 1.4710368294106826);

	}
	
	








	/*	

		@Test
		public void testDatingDAN() throws NodeNotFoundException, NotEvaluableNetworkException, IncompatibleEvidenceException, UnexpectedInferenceException {
			ProbNet datingDAN = DANFactory.buildDatingDAN();
			long startTime = System.nanoTime();
			DecompositionAlgorithm recursiveEvaluation = new DecompositionAlgorithm(datingDAN);
			TablePotential globalUtility = recursiveEvaluation.getGlobalUtility();
			long ellapsedTime = (System.nanoTime() - startTime) / 1000000;
			System.out.println(" Execution time =" +ellapsedTime);
			Assert.assertEquals(9.4076, globalUtility.values[0], 0.0001);
			Assert.assertNotNull(recursiveEvaluation.getOptimalStrategy());
		}


		@Test
		public void testMediastiNetDAN() throws NodeNotFoundException, IncompatibleEvidenceException,
				UnexpectedInferenceException, NotEvaluableNetworkException {

			ProbNet mediastiNetDAN = DANFactory.buildMediastinetDAN();
			long startTime = System.nanoTime();
			mediastiNetDAN = BasicOperations.removeSuperValueNodes(mediastiNetDAN, new EvidenceCase());
			DecompositionAlgorithm recursiveEvaluation = new DecompositionAlgorithm(mediastiNetDAN);
			TablePotential globalUtility = recursiveEvaluation.getGlobalUtility();
			long ellapsedTime = (System.nanoTime() - startTime) / 1000000;
			System.out.println(" Execution time =" +ellapsedTime);
			Assert.assertEquals(1.4710368294106826, globalUtility.values[0], 0.000001);
		}	

		@Test
		public void testNtests() throws NodeNotFoundException, IncompatibleEvidenceException,
				UnexpectedInferenceException, NotEvaluableNetworkException {

			ProbNet nTestsDAN = DANFactory.buildNTestsDAN(4);
			long startTime = System.nanoTime();
			DecompositionAlgorithm recursiveEvaluation = new DecompositionAlgorithm(nTestsDAN);
			TablePotential globalUtility = recursiveEvaluation.getGlobalUtility();
			long ellapsedTime = (System.nanoTime() - startTime) / 1000000;
			System.out.println(" Execution time =" +ellapsedTime);
		}

		@Test
		public void testTwoPhasesOfTests() throws NodeNotFoundException, IncompatibleEvidenceException,
		UnexpectedInferenceException, NotEvaluableNetworkException {
			ProbNet twoPhasesOfTestsDAN = DANFactory.buildTwoPhasesOfTestsDAN(2);
			DecompositionAlgorithm recursiveEvaluation = new DecompositionAlgorithm(twoPhasesOfTestsDAN);
			TablePotential globalUtility = recursiveEvaluation.getGlobalUtility();

		}
	 */

}
