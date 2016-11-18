package org.openmarkov.inference.decompositionintosymmetricdans;



	import java.io.InputStream;

import junit.framework.Assert;

	import org.junit.Before;
import org.junit.Test;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.factory.DANFactory;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DecompositionAlgorithmArticle;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DecompositionAlgorithmArticle.DANEvaluationOutput;
import org.openmarkov.io.probmodel.PGMXReader;

	public class DecompositionAlgorithmInferenceTest {
		
		@Before
		public void setUp() throws Exception {
		}
		
		
		private ProbNet loadDAN(String nameSuffix){
			String networkName = "networks/dan/DAN-"+nameSuffix+".pgmx";
			InputStream file = getClass().getClassLoader().getResourceAsStream(networkName);

		   // Load the network: ID-decide-test
		   PGMXReader pgmxReader = new PGMXReader();
		   ProbNetInfo probNetInfo = null;
		   try {
		       probNetInfo = pgmxReader.loadProbNet(file, networkName);
		        } catch (ParserException e) {
		            e.printStackTrace();
		        }
		   return probNetInfo.getProbNet();
		}
		
		public void testMEU(String danName,double expectedEU){
			ProbNet network = loadDAN(danName);
			DecompositionAlgorithmArticle dsd = new DecompositionAlgorithmArticle();
			DANEvaluationOutput output = dsd.evaluateDSD(network);
			TablePotential globalUtility = output.getUtility();			
			Assert.assertEquals(expectedEU, globalUtility.values[0], 0.0001);
		}
		
		@Test
		public void testDANOnlyUtility() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
			testMEU("only-utility",10.0);
		}
		
		@Test
		public void testDANOneChance() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
			testMEU("one-chance",83.7);
		}
		
		@Test
		public void testDANOneDecision() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
			testMEU("one-decision",87.4);
		}
		
		@Test
		public void testDANNoKnowledge() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
			testMEU("no-knowledge",9.16);
		}
		
		//@Test
		//TODO This test requires a correct calculation of the elimination order in a symmetric DAN
		public void testDANPerfectKnowledge() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
			testMEU("perfect-knowledge",9.72);
		}
		
		
		

		//@Test
		public void testDecideTestDAN() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
			ProbNet decideTestDAN = DANFactory.buildDecideTestDAN();
			long startTime = System.nanoTime();
			DecompositionAlgorithmArticle dsd = new DecompositionAlgorithmArticle();
			DANEvaluationOutput output = dsd.evaluateDSD(decideTestDAN);
			TablePotential globalUtility = output.getUtility();
			long ellapsedTime = (System.nanoTime() - startTime) / 1000000;
			System.out.println(" Execution time =" +ellapsedTime);
			Assert.assertEquals(9.3929, globalUtility.values[0], 0.0001);
			//Assert.assertNotNull(recursiveEvaluation.getOptimalStrategy());
		}
		
		/*@Test
		public void testDiabetesDAN() throws NodeNotFoundException, IncompatibleEvidenceException,
				UnexpectedInferenceException, NotEvaluableNetworkException {

			ProbNet diabetesDAN = DANFactory.buildDiabetesDAN();
			long startTime = System.nanoTime();
			DecompositionAlgorithm recursiveEvaluation = new DecompositionAlgorithm(diabetesDAN);
			TablePotential globalUtility = recursiveEvaluation.getGlobalUtility();
			long ellapsedTime = (System.nanoTime() - startTime) / 1000000;
			System.out.println(" Execution time =" +ellapsedTime);
			Assert.assertEquals(9.8261, globalUtility.values[0], 0.0001);
			Assert.assertNotNull(recursiveEvaluation.getOptimalStrategy());
		}		
		
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
		public void testReactorDAN() throws NodeNotFoundException, IncompatibleEvidenceException,
				UnexpectedInferenceException, NotEvaluableNetworkException {

			ProbNet reactorDAN = DANFactory.buildReactorDAN();
			long startTime = System.nanoTime();
			DecompositionAlgorithm recursiveEvaluation = new DecompositionAlgorithm(reactorDAN);
			TablePotential globalUtility = recursiveEvaluation.getGlobalUtility();
			long ellapsedTime = (System.nanoTime() - startTime) / 1000000;
			System.out.println(" Execution time =" +ellapsedTime);
			Assert.assertEquals(10.0627, globalUtility.values[0], 0.0001);
		}	
		
		@Test
		public void testWooerDAN() throws NodeNotFoundException, IncompatibleEvidenceException,
				UnexpectedInferenceException, NotEvaluableNetworkException {

			ProbNet wooerDAN = DANFactory.buildWooerDAN();
			long startTime = System.nanoTime();
			DecompositionAlgorithm recursiveEvaluation = new DecompositionAlgorithm(wooerDAN);
			TablePotential globalUtility = recursiveEvaluation.getGlobalUtility();
			long ellapsedTime = (System.nanoTime() - startTime) / 1000000;
			System.out.println(" Execution time =" +ellapsedTime);
			Assert.assertEquals(7.73, globalUtility.values[0], 0.0001);
		}	
		
		@Test
		public void testUsedCarBuyerDAN() throws NodeNotFoundException, IncompatibleEvidenceException,
				UnexpectedInferenceException, NotEvaluableNetworkException {

			ProbNet usedCarBuyerDAN = DANFactory.buildUsedCarBuyer();
			long startTime = System.nanoTime();
			DecompositionAlgorithm recursiveEvaluation = new DecompositionAlgorithm(usedCarBuyerDAN);
			TablePotential globalUtility = recursiveEvaluation.getGlobalUtility();
			long ellapsedTime = (System.nanoTime() - startTime) / 1000000;
			System.out.println(" Execution time =" +ellapsedTime);
			Assert.assertEquals(32.96, globalUtility.values[0], 0.0001);
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
