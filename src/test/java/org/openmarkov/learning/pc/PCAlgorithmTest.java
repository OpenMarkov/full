/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.learning.pc;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.io.database.CaseDatabase;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;
import org.openmarkov.io.database.elvira.ElviraDataBaseIO;
import org.openmarkov.io.database.excel.CSVDataBaseIO;
import org.openmarkov.io.probmodel.reader.PGMXReader_0_2;
import org.openmarkov.learning.algorithm.pc.PCAlgorithm;
import org.openmarkov.learning.algorithm.pc.independencetester.CrossEntropyIndependenceTester;
import org.openmarkov.learning.algorithm.pc.independencetester.IndependenceTester;
import org.openmarkov.learning.core.algorithm.LearningAlgorithm;
import org.openmarkov.learning.core.util.ModelNetUse;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.fail;

public class PCAlgorithmTest {
	private final double maxError = 1E-6;

	private double alpha = 0.5;

	private IndependenceTester independenceTester;
	private double significanceLevel = 0.05;

	private String learnTestDatabaseFilename = "/learnTestDataBase.dbc";
	private String asiaDatabaseFilename = "/asia10K.csv";
	private String alarmDatabaseFilename = "/alarm500.csv";
	private String alarm10kDatabaseFilename = "/alarm10k.csv";

	private String asia10kProbNet = "/asia10k.pgmx";
	private String rootPath;
	private PGMXReader_0_2 reader;


	@Before public void setUp() throws Exception {
		independenceTester = new CrossEntropyIndependenceTester();

		URL url = getClass().getClassLoader ().getResource (asia10kProbNet);
		File file = new File(url.getPath());
		String absolutePath = file.getAbsolutePath();
		rootPath = absolutePath.substring(0, absolutePath.length() - asia10kProbNet.length());
		reader = new PGMXReader_0_2();
	}

	private ProbNet readNetwork(String networkName) {
		ProbNet probNet = null;
		try {
			probNet = reader.loadProbNet(networkName);
		} catch (ParserException e) {
			System.err.println(e.getMessage());
			System.err.println(e.getStackTrace());
			fail("Can not read network:" + e.getLocalizedMessage ());
		}
		return probNet;
	}

	@Test public void testLearnTestDataBase() throws Exception {

		ElviraDataBaseIO databaseIO = new ElviraDataBaseIO();
		CaseDatabase learnTestDatabase = databaseIO.load(getClass().getResource(learnTestDatabaseFilename).getFile());
		ProbNet learnedNet = new ProbNet();
		for (Variable variable : learnTestDatabase.getVariables()) {
			learnedNet.addNode(variable, NodeType.CHANCE);
		}

		LearningAlgorithm learningAlgorithm = new PCAlgorithm(learnedNet, learnTestDatabase, alpha, independenceTester,
				significanceLevel);

		double[] probabilities;
		learningAlgorithm.run(new ModelNetUse());
		Node nodeA = learnedNet.getNode("A");
		Node nodeB = learnedNet.getNode("B");
		Node nodeC = learnedNet.getNode("C");
		Node nodeD = learnedNet.getNode("D");
		Node nodeE = learnedNet.getNode("E");
		Node nodeF = learnedNet.getNode("F");
		Variable variableA = nodeA.getVariable();
		Variable variableB = nodeB.getVariable();
		Variable variableC = nodeC.getVariable();
		Variable variableD = nodeD.getVariable();
		//Variable variableE = nodeE.getVariable();

		// check the structure of the learned net
		// present links
		Assert.assertTrue(nodeC.isParent(nodeA));
		Assert.assertTrue(nodeD.isParent(nodeA));
		Assert.assertTrue(nodeC.isParent(nodeB));
		Assert.assertTrue(nodeD.isParent(nodeC));
		Assert.assertTrue(nodeE.isParent(nodeD));
		// non-present links
		Assert.assertFalse(nodeA.isParent(nodeB));
		Assert.assertFalse(nodeA.isParent(nodeC));
		Assert.assertFalse(nodeA.isParent(nodeD));
		Assert.assertFalse(nodeA.isParent(nodeE));
		Assert.assertFalse(nodeA.isParent(nodeF));
		Assert.assertFalse(nodeB.isParent(nodeA));
		Assert.assertFalse(nodeB.isParent(nodeC));
		Assert.assertFalse(nodeB.isParent(nodeD));
		Assert.assertFalse(nodeB.isParent(nodeE));
		Assert.assertFalse(nodeB.isParent(nodeF));
		Assert.assertFalse(nodeC.isParent(nodeD));
		Assert.assertFalse(nodeC.isParent(nodeE));
		Assert.assertFalse(nodeC.isParent(nodeF));
		Assert.assertFalse(nodeD.isParent(nodeB));
		Assert.assertFalse(nodeD.isParent(nodeE));
		Assert.assertFalse(nodeD.isParent(nodeF));
		Assert.assertFalse(nodeE.isParent(nodeA));
		Assert.assertFalse(nodeE.isParent(nodeB));
		Assert.assertFalse(nodeE.isParent(nodeC));
		Assert.assertFalse(nodeE.isParent(nodeF));
		Assert.assertFalse(nodeF.isParent(nodeA));
		Assert.assertFalse(nodeF.isParent(nodeB));
		Assert.assertFalse(nodeF.isParent(nodeC));
		Assert.assertFalse(nodeF.isParent(nodeD));
		Assert.assertFalse(nodeF.isParent(nodeE));
		// chek the CPTs
		// A
		probabilities = ((TablePotential) nodeA.getPotentials().get(0)).getValues();
		Assert.assertEquals(0.305194, probabilities[0], maxError);
		Assert.assertEquals(0.694805, probabilities[1], maxError);
		//B
		probabilities = ((TablePotential) nodeB.getPotentials().get(0)).getValues();
		Assert.assertEquals(0.602897, probabilities[0], maxError);
		Assert.assertEquals(0.397102, probabilities[1], maxError);
		//C | B, A
		TablePotential cGivenBAPotential = (TablePotential) nodeC.getPotentials().get(0);
		List<Variable> cGivenBAVariables = Arrays.asList(variableC, variableB, variableA);
		cGivenBAPotential = (TablePotential) cGivenBAPotential.reorder(cGivenBAVariables);
		probabilities = cGivenBAPotential.getValues();

		Assert.assertEquals(0.286111, probabilities[0], maxError);
		Assert.assertEquals(0.713888, probabilities[1], maxError);
		Assert.assertEquals(0.5, probabilities[2], maxError);
		Assert.assertEquals(0.5, probabilities[3], maxError);
		Assert.assertEquals(0.791764, probabilities[4], maxError);
		Assert.assertEquals(0.208235, probabilities[5], maxError);
		Assert.assertEquals(0.402573, probabilities[6], maxError);
		Assert.assertEquals(0.597426, probabilities[7], maxError);

		//D | C, A
		TablePotential dGivenCAPotential = (TablePotential) nodeD.getPotentials().get(0);
		List<Variable> dGivenCAVariables = Arrays.asList(variableD, variableC, variableA);
		dGivenCAPotential = (TablePotential) dGivenCAPotential.reorder(dGivenCAVariables);
		probabilities = dGivenCAPotential.getValues();

		Assert.assertEquals(0.491304, probabilities[0], maxError);
		Assert.assertEquals(0.5086956, probabilities[1], maxError);
		Assert.assertEquals(0.1536458, probabilities[2], maxError);
		Assert.assertEquals(0.8463541, probabilities[3], maxError);
		Assert.assertEquals(0.7343049, probabilities[4], maxError);
		Assert.assertEquals(0.2656950, probabilities[5], maxError);
		Assert.assertEquals(0.8585657, probabilities[6], maxError);
		Assert.assertEquals(0.1414342, probabilities[7], maxError);
	}

	@Test public void testAsia10k() throws Exception {
		CSVDataBaseIO csvReader = new CSVDataBaseIO();
		CaseDatabase asiaDatabase = csvReader.load(getClass().getResource(asiaDatabaseFilename).getFile());
		ProbNet learnedNet = new ProbNet();
		for (Variable variable : asiaDatabase.getVariables()) {
			learnedNet.addNode(variable, NodeType.CHANCE);
		}

		LearningAlgorithm learningAlgorithm = new PCAlgorithm(learnedNet, asiaDatabase, alpha, independenceTester,
				significanceLevel);

		learningAlgorithm.run(new ModelNetUse());

		Node nodeAsia = learnedNet.getNode("VisitToAsia");
		Node nodeTuberculosis = learnedNet.getNode("Tuberculosis");
		Node nodeLungCancer = learnedNet.getNode("LungCancer");
		Node nodeTuberculosisOrCancer = learnedNet.getNode("TuberculosisOrCancer");
		Node nodeBronchitis = learnedNet.getNode("Bronchitis");
		Node nodeSmoker = learnedNet.getNode("Smoker");
		Node nodeXRay = learnedNet.getNode("X-ray");
		Node nodeDyspnea = learnedNet.getNode("Dyspnea");

		Assert.assertNotNull(nodeAsia);
		Assert.assertNotNull(nodeTuberculosis);
		Assert.assertNotNull(nodeLungCancer);
		Assert.assertNotNull(nodeTuberculosisOrCancer);
		Assert.assertNotNull(nodeBronchitis);
		Assert.assertNotNull(nodeSmoker);
		Assert.assertNotNull(nodeXRay);
		Assert.assertNotNull(nodeDyspnea);

		// Node asia
		Assert.assertTrue(nodeTuberculosis.isParent(nodeAsia));
		Assert.assertFalse(nodeLungCancer.isParent(nodeAsia));
		Assert.assertFalse(nodeTuberculosisOrCancer.isParent(nodeAsia));
		Assert.assertFalse(nodeBronchitis.isParent(nodeAsia));
		Assert.assertFalse(nodeSmoker.isParent(nodeAsia));
		Assert.assertFalse(nodeXRay.isParent(nodeAsia));
		Assert.assertFalse(nodeDyspnea.isParent(nodeAsia));

		// Node LungCancer
		Assert.assertFalse(nodeAsia.isParent(nodeLungCancer));
		Assert.assertFalse(nodeTuberculosis.isParent(nodeLungCancer));
		Assert.assertTrue(nodeTuberculosisOrCancer.isParent(nodeLungCancer));
		Assert.assertFalse(nodeBronchitis.isParent(nodeLungCancer));
		Assert.assertFalse(nodeSmoker.isParent(nodeLungCancer));
		Assert.assertFalse(nodeXRay.isParent(nodeLungCancer));
		Assert.assertFalse(nodeDyspnea.isParent(nodeLungCancer));

		// Node Tuberculosis
		Assert.assertFalse(nodeAsia.isParent(nodeTuberculosis));
		Assert.assertFalse(nodeLungCancer.isParent(nodeTuberculosis));
		Assert.assertTrue(nodeTuberculosisOrCancer.isParent(nodeTuberculosis));
		Assert.assertFalse(nodeBronchitis.isParent(nodeTuberculosis));
		Assert.assertFalse(nodeSmoker.isParent(nodeTuberculosis));
		Assert.assertFalse(nodeXRay.isParent(nodeTuberculosis));
		Assert.assertFalse(nodeDyspnea.isParent(nodeTuberculosis));

		// Node TuberculosisOrCancer
		Assert.assertFalse(nodeAsia.isParent(nodeTuberculosisOrCancer));
		Assert.assertFalse(nodeLungCancer.isParent(nodeTuberculosisOrCancer));
		Assert.assertFalse(nodeTuberculosis.isParent(nodeTuberculosisOrCancer));
		Assert.assertFalse(nodeBronchitis.isParent(nodeTuberculosisOrCancer));
		Assert.assertFalse(nodeSmoker.isParent(nodeTuberculosisOrCancer));
		Assert.assertTrue(nodeXRay.isParent(nodeTuberculosisOrCancer));
		Assert.assertTrue(nodeDyspnea.isParent(nodeTuberculosisOrCancer));

		// Node Bronchitis
		Assert.assertFalse(nodeAsia.isParent(nodeBronchitis));
		Assert.assertFalse(nodeLungCancer.isParent(nodeBronchitis));
		Assert.assertFalse(nodeTuberculosis.isParent(nodeBronchitis));
		Assert.assertFalse(nodeTuberculosisOrCancer.isParent(nodeBronchitis));
		Assert.assertFalse(nodeBronchitis.isParent(nodeBronchitis));
		Assert.assertFalse(nodeSmoker.isParent(nodeBronchitis));
		Assert.assertFalse(nodeXRay.isParent(nodeBronchitis));
		Assert.assertTrue(nodeDyspnea.isParent(nodeBronchitis));

		// Node Smoker
		Assert.assertFalse(nodeAsia.isParent(nodeSmoker));
		Assert.assertTrue(nodeLungCancer.isParent(nodeSmoker));
		Assert.assertFalse(nodeTuberculosis.isParent(nodeSmoker));
		Assert.assertFalse(nodeTuberculosisOrCancer.isParent(nodeSmoker));
		Assert.assertTrue(nodeBronchitis.isParent(nodeSmoker));
		Assert.assertFalse(nodeXRay.isParent(nodeSmoker));
		Assert.assertFalse(nodeDyspnea.isParent(nodeSmoker));

		// Node X-ray
		Assert.assertFalse(nodeAsia.isParent(nodeXRay));
		Assert.assertFalse(nodeLungCancer.isParent(nodeXRay));
		Assert.assertFalse(nodeTuberculosis.isParent(nodeXRay));
		Assert.assertFalse(nodeTuberculosisOrCancer.isParent(nodeXRay));
		Assert.assertFalse(nodeBronchitis.isParent(nodeXRay));
		Assert.assertFalse(nodeSmoker.isParent(nodeXRay));
		Assert.assertFalse(nodeDyspnea.isParent(nodeXRay));

		// Node X-ray
		Assert.assertFalse(nodeAsia.isParent(nodeDyspnea));
		Assert.assertFalse(nodeLungCancer.isParent(nodeDyspnea));
		Assert.assertFalse(nodeTuberculosis.isParent(nodeDyspnea));
		Assert.assertFalse(nodeTuberculosisOrCancer.isParent(nodeDyspnea));
		Assert.assertFalse(nodeBronchitis.isParent(nodeDyspnea));
		Assert.assertFalse(nodeSmoker.isParent(nodeDyspnea));
		Assert.assertFalse(nodeXRay.isParent(nodeDyspnea));

	}

	@Test public void testAlarm500() throws Exception {

		CSVDataBaseIO csvReader = new CSVDataBaseIO();
		CaseDatabase alarmDatabase = csvReader.load(getClass().getResource(alarmDatabaseFilename).getFile());
		ProbNet learnedNet = new ProbNet();
		for (Variable variable : alarmDatabase.getVariables()) {
			learnedNet.addNode(variable, NodeType.CHANCE);
		}

		LearningAlgorithm learningAlgorithm = new PCAlgorithm(learnedNet, alarmDatabase, alpha, independenceTester,
				significanceLevel);

		learningAlgorithm.run(new ModelNetUse());

		Assert.assertEquals(34, learnedNet.getLinks().size());
		PGMXReader_0_2 reader = new PGMXReader_0_2();
		ProbNet readNet = reader.loadProbNet(getClass().getResource("/BN-alarm.pgmx").getFile());
		printDifferences(readNet, learnedNet);
	}

	//@Test
	public void testAlarm10k() throws Exception {

		CSVDataBaseIO csvReader = new CSVDataBaseIO();
		CaseDatabase alarm10kDatabase = csvReader.load(getClass().getResource(alarm10kDatabaseFilename).getFile());
		ProbNet learnedNet = new ProbNet();
		for (Variable variable : alarm10kDatabase.getVariables()) {
			learnedNet.addNode(variable, NodeType.CHANCE);
		}

		LearningAlgorithm learningAlgorithm = new PCAlgorithm(learnedNet, alarm10kDatabase, alpha, independenceTester,
				significanceLevel);

		learningAlgorithm.run(new ModelNetUse());

		Assert.assertEquals(46, learnedNet.getLinks().size());

		PGMXReader_0_2 reader = new PGMXReader_0_2();
		ProbNet readNet = reader.loadProbNet(getClass().getResource("/BN-alarm.pgmx").getFile());
		printDifferences(readNet, learnedNet);
	}

	private void printDifferences(ProbNet originalNet, ProbNet learnedNet) throws NodeNotFoundException {
		int missingLinkCount = 0;
		for (Node node : originalNet.getNodes()) {
			Node learnedNode = learnedNet.getNode(node.getName());
			List<Node> learnedNeighbors = learnedNode.getNeighbors();
			for (Node neighbor : node.getNeighbors()) {
				if (node.getName().compareTo(neighbor.getName()) < 0) {
					boolean found = false;
					int i = 0;
					while (!found && i < learnedNeighbors.size()) {
						found = learnedNeighbors.get(i++).getName().equals(neighbor.getName());
					}
					if (!found) {
						System.out.println("Missing: " + node.getName() + " --- " + neighbor.getName());
						missingLinkCount++;
					}
				}
			}
		}

		int addedLinkCount = 0;
		for (Node node : learnedNet.getNodes()) {
			Node readNode = originalNet.getNode(node.getName());
			List<Node> readNeighbors = readNode.getNeighbors();
			for (Node neighbor : node.getNeighbors()) {
				if (node.getName().compareTo(neighbor.getName()) < 0) {
					boolean found = false;
					int i = 0;
					while (!found && i < readNeighbors.size()) {
						found = readNeighbors.get(i++).getName().equals(neighbor.getName());
					}
					if (!found) {
						System.out.println("Added: " + node.getName() + " --- " + neighbor.getName());
						addedLinkCount++;
					}
				}
			}
		}
		System.out.println("Missing: " + missingLinkCount + "; Added: " + addedLinkCount);
	}




	@Test public void testVStructuresInNetworks() {
		ProbNet asia10k = readNetwork(asia10kProbNet);
		if (asia10k != null)
			System.out.println("Se lee");
		else
			System.out.println("No se lee");
		//ProbNet alarm =
	}
}
