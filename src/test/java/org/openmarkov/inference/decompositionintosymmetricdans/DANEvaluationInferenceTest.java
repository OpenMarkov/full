package org.openmarkov.inference.decompositionintosymmetricdans;

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
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Intervention;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DANEvaluation;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DANOperations;
import org.openmarkov.io.probmodel.reader.PGMXReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class DANEvaluationInferenceTest {

	@Before
	public void setUp() throws Exception {
	}

	public ProbNet loadDAN(String nameSuffix) {
		String networkName = "networks/dan/DAN-" + nameSuffix + ".pgmx";
		InputStream file = getClass().getClassLoader().getResourceAsStream(
				networkName);

		PGMXReader pgmxReader = new PGMXReader();
		ProbNetInfo probNetInfo = null;
		try {
			probNetInfo = pgmxReader.loadProbNetInfo(networkName, file);
		} catch (ParserException e) {
			e.printStackTrace();
		}
		return probNetInfo.getProbNet();
	}

	/*//@Test
	public void testDiabetesDANCE() 
			throws IncompatibleEvidenceException, UnexpectedInferenceException, 
			NodeNotFoundException, NotEvaluableNetworkException {
		System.out.println("hola");
		ProbNet danDiabetesCE = loadDAN("DAN-diabetes-CE");
		System.out.println("adios");
		long startTime = System.nanoTime();
		DANDecompositionAlgorithm algorithm = new DANDecompositionAlgorithm();
		DANEvaluationOutput output = algorithm.evaluate(danDiabetesCE);
		TablePotential globalUtility = output.getUtility().get(0);
		assertNotNull(globalUtility);
		long ellapsedTime = (System.nanoTime() - startTime) / 1000000;
		System.out.println(" Execution time =" +ellapsedTime);
	}*/

	public void testDANEvaluation(String danName,double expectedEU,String ...namesVariablesIntervention){
		ProbNet network = loadDAN(danName);
		System.out.println("*** Evaluating DAN "+danName+" ***");
		System.out.println();
		DANEvaluation eval = buildDANEvaluation(network);
		testDANEvaluation(eval,network, expectedEU,namesVariablesIntervention);
	}

	protected void testDANEvaluation(DANEvaluation eval,ProbNet network, double expectedEU,String ...namesVariablesIntervention) {
		//DANExactAlgorithm dsd = new DANDecompositionAlgorithm();
		TablePotential globalUtility = null;
		try {
			globalUtility = eval.getUtility();
		} catch (UnexpectedInferenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Only debugging

		//String strIntervention = globalUtility.interventions[0].toStringForGraphviz(network);
		Assert.assertEquals(expectedEU, globalUtility.getFirstValue(), 0.0001);
		Intervention[] inter = globalUtility.interventions;
		if (inter!=null && namesVariablesIntervention!=null && namesVariablesIntervention.length > 0){				
			Intervention intervention = inter[0];
			String strIntervention = intervention.toStringForGraphviz(network);
			Assert.assertTrue(areEquals(getVariablesOfIntervention(intervention),namesVariablesIntervention));
		}
	}
	
	
	protected abstract DANEvaluation buildDANEvaluation(ProbNet network);

	/*private void testMEUAndInterventionCEA(ProbNet network, double expectedEU,String ...namesVariablesIntervention) {
		DANDecompositionAlgorithm dsd = new DANDecompositionAlgorithm();
		DANEvaluationOutput output = dsd.evaluate(network);
		TablePotential globalUtility = output.getUtility().get(0);
		//Only debugging

		//String strIntervention = globalUtility.interventions[0].toStringForGraphviz(network);
		Assert.assertEquals(expectedEU, globalUtility.values[0], 0.0001);
		Intervention[] inter = globalUtility.interventions;
		if (inter!=null && namesVariablesIntervention!=null && namesVariablesIntervention.length > 0){				
			Intervention intervention = inter[0];
			String strIntervention = intervention.toStringForGraphviz(network);
			Assert.assertTrue(areEquals(getVariablesOfIntervention(intervention),namesVariablesIntervention));
		}
	}*/

	private boolean areEquals(List<Variable> variables,String[] expectedNamesVariables) {
		return areEqualsListsOfStrings(getDifferentNamesVariables(variables),expectedNamesVariables);		

	}

	private List<String> getDifferentNamesVariables(List<Variable> variables) {
		List<String> differentNames = new ArrayList<>();
		for (Variable var:variables){
			String name = var.getName();
			if (!differentNames.contains(name)){
				differentNames.add(name);
			}
		}
		return differentNames;
	}

	private boolean areEqualsListsOfStrings(List<String> namesVariablesIntervention,String[] expectedNamesVariables) {
		boolean areEqual = true;
		int varSize = namesVariablesIntervention.size();
		if (expectedNamesVariables.length!=varSize){
			areEqual = false;
		}
		else{
			String [] namesInVariables = new String[varSize];
			int i=0;				
			for (String var:namesVariablesIntervention){
				namesInVariables[i]=var;
				i++;
			}
			areEqual = areEquals(namesInVariables,expectedNamesVariables);
		}
		return areEqual;		

	}


	private List<Variable> getVariablesOfIntervention(Intervention inter){
		List<Variable> variables = new ArrayList<>();

		if (inter!=null){
			variables.add(inter.getRootVariable());
			for (Intervention child:inter.getInterventionsChildren()){
				variables = DANOperations.join(variables, getVariablesOfIntervention(child));
			}				
		}
		return variables;		

	}


	private boolean areEquals(String a[],String b[]){
		return isSubset(a,b) && isSubset(b,a);
	}

	private boolean isSubset(String []subsetCandidate,String []set){
		int subsetSize = subsetCandidate.length;
		boolean isSubset = true;
		for (int i=0;i<subsetSize&&isSubset;i++){
			isSubset = isStringInList(subsetCandidate[i],set);					
		}
		return isSubset;			
	}



	private boolean isStringInList(String search,String[] list){
		boolean contains = false;
		for(int i=0;i<list.length&&!contains;i++) {
			String str = list[i];
			contains = Objects.equals(str, search);
		}
		return contains;	

	}

	@Test
	public void testDANOnlyUtility() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		testDANEvaluation("only-utility",10.0);
	}

	@Test
	public void testDANOneChance() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		testDANEvaluation("one-chance",83.7);
	}

	@Test
	public void testDANOneDecision() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		testDANEvaluation("one-decision",87.4,"D");
	}

	@Test
	public void testDANNoKnowledge() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		testDANEvaluation("no-knowledge",9.16,"D");
	}

	@Test
	public void testDANPerfectKnowledge() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		testDANEvaluation("perfect-knowledge",9.72,"A","D");
	}

	@Test
	public void testDANTest2Therapies() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		testDANEvaluation("test-2therapies",9.39366,"Test","Therapy");
	}


	@Test
	public void testDANTest2TherapiesNoCostSymmetrizedOrderForced() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		testDANEvaluation("decide-test-2therapies-no-cost-symmetrized-order-forced",9.39366,"Do test?","Result of test","Therapy");
	}

	@Test
	public void testDANTest2TherapiesNoCostOrderForced() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		testDANEvaluation("decide-test-2therapies-no-cost-order-forced",9.39366,"Do test?","Result of test","Therapy");
	}

	@Test
	public void testDANTest2TherapiesNoCost() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		testDANEvaluation("decide-test-2therapies-no-cost",9.39366,"Do test?","Result of test","Therapy");
	}

	@Test
	public void testDANUIDsPaper() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		testDANEvaluation("UID-luque2016-OM-0-2-0",10,"OD","D","X","E");

	}

	@Test
	public void testDANDiabetes() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		testDANEvaluation("diabetes",979.8337,"Symptom","OD","Dec: Blood Test","Dec: Urine test","Blood test result","Urine test result","Therapy");

	}
	
	@Test
	public void testDANSimplifiedUsedCarBuyer() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		testDANEvaluation("simplified-used-car-buyer",32.96,"Dec: First Test","First Result","Dec: Purchase");

	}

	@Test
	public void testDANUsedCarBuyer() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		testDANEvaluation("used-car-buyer",32.96,"Dec: First Test","First Result","Dec: Second Test","Dec: Purchase");

	}

	@Test
	public void testDANReactor() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		testDANEvaluation("reactor",8.1280,"Test decision","Result of test","Build decision");

	}
	
		
	@Test
	public void testDANKingNobleDescentYesFirstTask1() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		testDANEvaluation("king-noble-descent-yes-first-task-1",9.03);

	}
	
	@Test
	public void testDANKingNobleDescentYesFirstTask1SecondTask2() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		testDANEvaluation("king-noble-descent-yes-first-task-1-second-task-2",9.03);
	}
	
	@Test
	public void testDANSimplifiedTwoTasksKingNobleDescentYes() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		testDANEvaluation("simplified-two-tasks-king-noble-descent-yes",9.08);
	}
	
	/**
	 * This network is as "simplified-two-tasks-king-noble-descent-yes", but removing zero utility potentials.
	 */
	@Test
	public void testDANSimplified2TwoTasksKingNobleDescentYes() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		testDANEvaluation("simplified-2-two-tasks-king-noble-descent-yes",9.08);
	}
	
	/**
	 * This network is as "simplified-two-tasks-king-noble-descent-yes", but removing zero utility potentials.
	 */
	@Test
	public void testDANSimplifiedOneTaskKingNobleDescentYes() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		testDANEvaluation("simplified-one-task-king-noble-descent-yes",9.28);
	}
	
	@Test
	public void testDANKingNobleDescentNo() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		testDANEvaluation("king-noble-descent-no",6.43);

	}
	
	@Test
	public void testDANKingNobleDescentYes() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		testDANEvaluation("king-noble-descent-yes",9.03);
	}


	@Test
	public void testDANKing() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		testDANEvaluation("king",7.73);
	}

	@Test
	public void testDAN3Tests() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		testDANEvaluation("3-test-problem",9.6162,"Symptom","OD","Dec: Test 0","Dec: Test 1",
				"Dec: Test 2","Test Result 1","Test Result 2","Therapy");
	}

	//@Test
	public void testDANDating() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		testDANEvaluation("dating",9.4076);

	}

	//@Test
	//TODO DAN-mediastinet has super-value nodes. It must be converted into a DAN with only ordinary utility nodes.
	public void testDANMediastinet() throws IncompatibleEvidenceException, UnexpectedInferenceException, NodeNotFoundException, NotEvaluableNetworkException{
		testDANEvaluation("mediastinet",1.4710368294106826);

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
