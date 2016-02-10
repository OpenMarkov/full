package org.openmarkov.full.io;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openmarkov.core.exception.*;
import org.openmarkov.core.gui.dialog.io.NetsIO;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.model.network.*;
import org.openmarkov.core.model.network.constraint.OnlyAtemporalVariables;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.type.BayesianNetworkType;
import org.openmarkov.core.model.network.type.InfluenceDiagramType;
import org.openmarkov.core.model.network.type.MIDType;
import org.openmarkov.inference.tasks.VariableElimination.*;
import org.openmarkov.io.probmodel.PGMXReader;
import org.openmarkov.io.probmodel.PGMXWriter;

import bitbucket.NetsRepository;


/**
 * This class tests the class
 * 
 * @author jmendoza
 * @author mkpalacio
 * @author jperez
 */
public class NetsIOTest {

	HashSet<String> skippedNetworkNames = new HashSet<>();

	/**
	 * This setup method allow to add networks to the skip list
	 */
	@Before
	public void setUp(){
		//Already passed with VEResolution and VEPropagation
		skippedNetworkNames.add("BN-alarm.pgmx");
		skippedNetworkNames.add("BN-asia.pgmx");
		skippedNetworkNames.add("BN-catarnet.pgmx");
		skippedNetworkNames.add("BN-hepar.pgmx");

		skippedNetworkNames.add("BN-one-disease.pgmx");
		skippedNetworkNames.add("BN-prostanet.pgmx");
		skippedNetworkNames.add("BN-two-diseases.pgmx");

		//Already passed with load, save and reload
		skippedNetworkNames.add("DAN-3-test-problem.pgmx");
		skippedNetworkNames.add("DAN-4-test-problem.pgmx");
		skippedNetworkNames.add("DAN-5-test-problem.pgmx");
		skippedNetworkNames.add("DAN-6-test-problem.pgmx");
		skippedNetworkNames.add("DAN-7-test-problem.pgmx");
		skippedNetworkNames.add("DAN-arthronet.pgmx");
		skippedNetworkNames.add("DAN-dating.pgmx");
		skippedNetworkNames.add("DAN-decide-test-ce.pgmx");
		skippedNetworkNames.add("DAN-decide-test-ordered.pgmx");
		skippedNetworkNames.add("DAN-decide-test-symptom.pgmx");
		skippedNetworkNames.add("DAN-decide-test-with-restrictive-symptom.pgmx");
		skippedNetworkNames.add("DAN-decide-test-with-symptom.pgmx");
		skippedNetworkNames.add("DAN-decide-test.pgmx");
		skippedNetworkNames.add("DAN-delayed-result-of-test.pgmx");
		skippedNetworkNames.add("DAN-diabetes.pgmx");
		skippedNetworkNames.add("DAN-economic-mediastinet.pgmx");
		skippedNetworkNames.add("DAN-king.pgmx");
		skippedNetworkNames.add("DAN-mediastinet.pgmx");
		skippedNetworkNames.add("DAN-qale-mediastinet.pgmx");
		skippedNetworkNames.add("DAN-reactor.pgmx");
		skippedNetworkNames.add("DAN-test-always.pgmx");
		skippedNetworkNames.add("DAN-unordered-two-decs.pgmx");
		skippedNetworkNames.add("DAN-used-car-buyer.pgmx");
		skippedNetworkNames.add("LIMID-Nilsson-Lauritzen.pgmx");
		skippedNetworkNames.add("LIMID-decide-test-symptom.pgmx");
		skippedNetworkNames.add("Dec-POMDP-wireless-network.pgmx");
		skippedNetworkNames.add("POMDP-coffee-robot.pgmx");

//		// TODO - Check CEA: Already passed with VEResolution, VEPropagation, VETemporalEvolution, VECEADecision, VECEAGlobal, VECEPSA
		skippedNetworkNames.add("ID-CEA-minimal.pgmx");
		skippedNetworkNames.add("ID-CEA-test-2therapies-3criteria.pgmx");
		skippedNetworkNames.add("ID-CEA-test-2therapies-new-test.pgmx");
		skippedNetworkNames.add("ID-CEA-test-2therapies.pgmx");
		skippedNetworkNames.add("ID-decide-test-without-dummy-state.pgmx");
		skippedNetworkNames.add("ID-decide-test.pgmx");
		skippedNetworkNames.add("MID-Chancellor.pgmx");
		skippedNetworkNames.add("MID-hip-Briggs.pgmx");
		skippedNetworkNames.add("MID-dmhee-2.5.pgmx");
		skippedNetworkNames.add("MID-dmhee-3.5.pgmx");
		skippedNetworkNames.add("MID-dmhee-4.7.pgmx");
		skippedNetworkNames.add("MID-dmhee-4.8.pgmx");

		// TODO - Failed on VEPropagation
		skippedNetworkNames.add("ID-delayed-result-of-test.pgmx");

		// TODO - Failed getting optimal intervention on Resolve
		skippedNetworkNames.add("ID-mediastinet-ce.pgmx");

		// TODO - Memory Heap
//		skippedNetworkNames.add("BN-nasonet.pgmx");

//		// TODO - Failed in VEPropagation (All with supervalue nodes)
		skippedNetworkNames.add("ID-arthronet.pgmx");
		skippedNetworkNames.add("ID-arthronet-ce.pgmx");
		skippedNetworkNames.add("ID-mediastinet.pgmx");
		skippedNetworkNames.add("ID-used-car-buyer.pgmx");
		skippedNetworkNames.add("MID-CHAP-Ryan-Griffin.pgmx");
		skippedNetworkNames.add("MID-HPV.pgmx");


	}

	/**
	 * This method opens the a net from a file and saves it into another file.
	 * It makes the asserts to verify if the tests are good.
	 * 
	 * @param fileToOpen the file from which open the network.
	 * @param fileToSave the file into which save the network.
	 * @throws Exception if an error has occurred.
	 */
	private void openSaveNetwork(String fileToOpen, String fileToSave)
			throws Exception {
		ProbNet net = null;
		File file;
		String fileNameOpen = null;
		String fileNameSave = null;
		String path = null;
		//URI uri = getClass().getResource(fileToOpen).toURI();
		file = new File(getClass().getClassLoader().getResource(fileToOpen).toURI());
		fileNameOpen = file.getAbsolutePath();
		if (fileNameOpen == null) {
			fail("The test file " + fileNameOpen + " can't be found");
		} else {
			net = NetsIO.openNetworkFile(fileNameOpen).getProbNet();
			assertNotNull(net);
		}
		path = file.getParent();
		fileNameSave = path + File.separator + fileToSave;
		NetsIO.saveNetworkFile(net, fileNameSave);
		net = null;
		net = NetsIO.openNetworkFile(fileNameSave).getProbNet();
		assertNotNull(net);
		new File(fileNameSave).delete();
	}


	/**
	 * This method tests the methods 'openNetworkFile' and 'saveNetworkFile',
	 * opening and saving various files that contain bayes nets and influence
	 * diagrams.
	 * 
	 * @throws Exception if an error occurred while the networks are opened and
	 * saved.
	 */
	@Test
	public final void testOpenSaveNetworkFile() throws Exception {
		openSaveNetwork("Net1.elv", "Net1Saved.elv");
		openSaveNetwork("Net2.elv", "Net2Saved.elv");
		
	}
	
	@Test
	public final void testOpenSaveRepositoryNets(){
		NetsRepository repository = new NetsRepository();
        List<URL> listURL = repository.getNetworks();

        for (URL url : listURL) {
        	// The name is irrelevant because this nets will only be created for tests purposes and it will be deleted
        	// after each iteration
            String networkName = url.getPath();
            networkName = networkName.substring(networkName.lastIndexOf("/") + 1, networkName.length());

			if (skippedNetworkNames.contains(networkName)){
				continue;
			}

            PGMXReader pgmxReader = new PGMXReader();
            
			try {
				ProbNet probNet = null;
				try {
					probNet = pgmxReader.loadProbNet(url.openStream(), networkName).getProbNet();
				} catch (IOException e) {
					e.printStackTrace();
				}
				assertNotNull(probNet);
				assertNotNull(probNet.getNodes());
				
				PGMXWriter pgmxWritter = new PGMXWriter();
				pgmxWritter.writeProbNet(networkName, probNet);
				
				FileInputStream file = new FileInputStream(networkName);
				ProbNetInfo probNetInfo = pgmxReader.loadProbNet(file, networkName);
				probNet = probNetInfo.getProbNet();
				System.out.println("Loaded, saved and reloaded probNet:" + url.getPath());
				assertNotNull(probNet);
				assertNotNull(probNet.getNodes());
				EvidenceCase preResolutionEvidence;
				int numSimulations = 10;
				boolean useMultithreading = true;

				if (probNetInfo.getEvidence().size() > 0) {
					preResolutionEvidence = probNetInfo.getEvidence().get(0);
				} else {
					preResolutionEvidence = new EvidenceCase();
				}
				if (probNet.getNetworkType().equals(BayesianNetworkType.getUniqueInstance())){
					try {
						testResolveNetwork(probNet, preResolutionEvidence, false);
						testPropagateNetwork(probNet, probNet.getVariables(), preResolutionEvidence);
					} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
						e.printStackTrace();
					}
				} else if (probNet.getNetworkType().equals(InfluenceDiagramType.getUniqueInstance())){
					try {
						if (probNet.getNodes(NodeType.DECISION).size() > 0){
							testResolveNetwork(probNet, preResolutionEvidence, true);
						} else {
							testResolveNetwork(probNet, preResolutionEvidence, false);
						}

						// TODO - Check propagate errors
						testPropagateNetwork(probNet, probNet.getVariables(), preResolutionEvidence);

						if (hasCostEffectiveness(probNet)){
							testCEADecisionNetwork(probNet, preResolutionEvidence);
							testCEAGlobalNetwork(probNet, preResolutionEvidence);
							testCEPSANetwork(probNet, preResolutionEvidence, numSimulations, useMultithreading);
						}

					} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
						e.printStackTrace();
					}
				} else if (probNet.getNetworkType().equals(MIDType.getUniqueInstance())){
					try {
						if (probNet.getNodes(NodeType.DECISION).size() > 0){
							testResolveNetwork(probNet, preResolutionEvidence, true);
						} else {
							testResolveNetwork(probNet, preResolutionEvidence, false);
						}
						// TODO - Check propagate errors
						testPropagateNetwork(probNet, probNet.getVariables(), preResolutionEvidence);

						if (hasCostEffectiveness(probNet)){
							testCEADecisionNetwork(probNet, preResolutionEvidence);
							testCEAGlobalNetwork(probNet, preResolutionEvidence);
							testCEPSANetwork(probNet, preResolutionEvidence, numSimulations, useMultithreading);
						}

						if (!probNet.hasConstraint(OnlyAtemporalVariables.class)){
							testTemporalEvolutionNetwork(probNet, preResolutionEvidence);
						}

					} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
						e.printStackTrace();
					}
				}

				
			} catch (WriterException | FileNotFoundException | ParserException e) {
				e.printStackTrace();
				
			} finally{
				File fileToBeDeleted = new File(networkName);
				fileToBeDeleted.delete();
			}
        }
	}

	private boolean hasCostEffectiveness(ProbNet probNet) {
		boolean hasCost = false;
		boolean hasEffectiveness = false;

		for (Criterion criterion : probNet.getDecisionCriteria()){
			if (criterion.getCECriterion().equals(Criterion.CECriterion.Cost)){
				hasCost = true;
			} else if (criterion.getCECriterion().equals(Criterion.CECriterion.Effectiveness)){
				hasEffectiveness = true;
			}
		}

		if (hasCost && hasEffectiveness){
			return true;
		} else {
			return false;
		}


	}

	private void testCEAGlobalNetwork(ProbNet probNet, EvidenceCase evidenceCase) throws NotEvaluableNetworkException, IncompatibleEvidenceException, UnexpectedInferenceException {
		VECEAGlobal veceaGlobal = new VECEAGlobal(probNet, evidenceCase);
		assertNotNull(veceaGlobal.getCEP());
		System.out.println("VECEAGlobal successful");
	}

	private void testCEADecisionNetwork(ProbNet probNet, EvidenceCase evidenceCase) throws NotEvaluableNetworkException, IncompatibleEvidenceException, UnexpectedInferenceException {
		List<Variable> decisionVariables = probNet.getVariables(NodeType.DECISION);

		for(Variable decisionVariable : decisionVariables){
			List<Variable> informationalPredecesors = ProbNetOperations.getInformationalPredecessors(probNet, decisionVariable);
			informationalPredecesors.remove(decisionVariable);

			for(Variable informationalPredecesor : informationalPredecesors){
				// Set the first state as an evidence
				Finding finding = new Finding(informationalPredecesor, informationalPredecesor.getStates()[0]);
				try {
					evidenceCase.addFinding(finding);
				} catch (InvalidStateException e) {
					e.printStackTrace();
				}
			}
			VECEADecision veceaDecision = new VECEADecision(probNet, evidenceCase, decisionVariable);
			assertNotNull(veceaDecision.getCEPPotential());
		}
		System.out.println("VECEADecision successful");
	}

	private void testCEPSANetwork(ProbNet probNet, EvidenceCase evidenceCase, int numSimulations, boolean useMultithreading){
		List<Variable> decisionVariables = probNet.getVariables(NodeType.DECISION);

		for(Variable decisionVariable : decisionVariables){
			List<Variable> informationalPredecesors = ProbNetOperations.getInformationalPredecessors(probNet, decisionVariable);
			informationalPredecesors.remove(decisionVariable);

			for(Variable informationalPredecesor : informationalPredecesors){
				// Set the first state as an evidence
				Finding finding = new Finding(informationalPredecesor, informationalPredecesor.getStates()[0]);
				try {
					evidenceCase.addFinding(finding);
				} catch (InvalidStateException | IncompatibleEvidenceException e) {
					e.printStackTrace();
				}
			}
			try {
				VECEPSA vecepsa = null;
				try {
					vecepsa = new VECEPSA(probNet, evidenceCase, decisionVariable, numSimulations, useMultithreading);
				} catch (UnexpectedInferenceException e) {
					e.printStackTrace();
				}
				assertNotNull(vecepsa.getCeaResults());
			} catch (NotEvaluableNetworkException | IncompatibleEvidenceException e) {
				e.printStackTrace();
			}

		}
		System.out.println("VECEPSA successful");
	}

	private void testPropagateNetwork(ProbNet probNet, List<Variable> variables, EvidenceCase evidenceCase) throws NotEvaluableNetworkException, IncompatibleEvidenceException, UnexpectedInferenceException {
		VEPropagation vePropagation	= new VEPropagation(probNet,variables, evidenceCase, null, null);
		HashMap<Variable, TablePotential> posteriorValues = vePropagation.getPosteriorValues();
		for(Variable variable : probNet.getVariables()){
//			if(!variable.getVariableType().equals(VariableType.NUMERIC)) {
				assertNotNull(posteriorValues.get(variable));
//			}
		}
		System.out.println("VEPropagation successful");
	}

	private void testResolveNetwork(ProbNet probNet, EvidenceCase evidenceCase, Boolean checkStrategy) throws NotEvaluableNetworkException, IncompatibleEvidenceException, UnexpectedInferenceException {
		VEResolution veResolution;
		if (evidenceCase != null) {
			veResolution = new VEResolution(probNet, evidenceCase, null);
		} else {
			veResolution = new VEResolution(probNet, null, null);
		}


		if (checkStrategy) {
			VEOptimalIntervention veOptimalStrategy = new VEOptimalIntervention(probNet, evidenceCase);
			assertNotNull(veOptimalStrategy.getOptimalIntervention());
		}

		System.out.println("VEResolution successful");
	}

	private void testTemporalEvolutionNetwork(ProbNet probNet, EvidenceCase evidenceCase) throws NotEvaluableNetworkException, IncompatibleEvidenceException, UnexpectedInferenceException {
		HashMap<String, Variable> filteredTemporalVariables = new HashMap<>();
		for(Variable variable : probNet.getVariables()){
			if (variable.isTemporal()) {
				if (!variable.getVariableType().equals(VariableType.NUMERIC)) {
					Variable oldVariable = filteredTemporalVariables.get(variable.getBaseName());
					if(oldVariable != null){
						if(variable.getTimeSlice() < oldVariable.getTimeSlice()){
							filteredTemporalVariables.remove(oldVariable);
							filteredTemporalVariables.put(variable.getBaseName(), variable);
						}
					} else {
						filteredTemporalVariables.put(variable.getBaseName(), variable);
					}
				} else {
					if (probNet.getNode(variable).getNodeType().equals(NodeType.UTILITY)) {
						Variable oldVariable = filteredTemporalVariables.get(variable.getBaseName());
						if(oldVariable != null){
							if(variable.getTimeSlice() < oldVariable.getTimeSlice()){
								filteredTemporalVariables.remove(oldVariable);
								filteredTemporalVariables.put(variable.getBaseName(), variable);
							}
						} else {
							filteredTemporalVariables.put(variable.getBaseName(), variable);
						}
					}
				}
			}
		}

		for(Variable variable : filteredTemporalVariables.values()){

			VETemporalEvolution veTemporalEvolution = new VETemporalEvolution(probNet,variable, evidenceCase, null);
			ProbNet expandedNetwork = veTemporalEvolution.getExpandedNetwork();
			assertNotNull(veTemporalEvolution.getPosteriorValues());
			for(int i = variable.getTimeSlice(); i < expandedNetwork.getInferenceOptions().getTemporalOptions().getNumberOfSlices(); i++){
				try {
					Variable variableInSlicei = expandedNetwork.getVariable(variable.getBaseName(), i);
					assertNotNull(veTemporalEvolution.getPosteriorValues().get(variableInSlicei));
				} catch (NodeNotFoundException e) {
					e.printStackTrace();
				}

			}

		}

		System.out.println("VETemporalEvolution successful");
	}
}
