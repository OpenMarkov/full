package org.openmarkov.full.io;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.openmarkov.core.exception.*;
import org.openmarkov.core.gui.dialog.io.NetsIO;
import org.openmarkov.core.inference.MulticriteriaOptions;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
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
 * This class tests the class {@link openmarkov.gui.io.NetsIO}.
 * 
 * @author jmendoza
 * @author mkpalacio
 * @author jperez
 */
public class NetsIOTest {
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
				if(probNetInfo.getEvidence().size() > 0) {
					preResolutionEvidence = probNetInfo.getEvidence().get(0);
				} else {
					preResolutionEvidence = new EvidenceCase();
				}
				if(probNet.getNetworkType().equals(BayesianNetworkType.getUniqueInstance())){
					try {
						testResolveNetwork(probNet, preResolutionEvidence, false);
						testPropagateNetwork(probNet, probNet.getVariables(), preResolutionEvidence);
					} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
						e.printStackTrace();
					}
				} else if (probNet.getNetworkType().equals(InfluenceDiagramType.getUniqueInstance())){
					try {
						if(probNet.getNodes(NodeType.DECISION).size() > 0){
							testResolveNetwork(probNet, preResolutionEvidence, true);
						} else {
							testResolveNetwork(probNet, preResolutionEvidence, false);
						}
						testPropagateNetwork(probNet, probNet.getVariables(), preResolutionEvidence);

						if(probNet.getInferenceOptions().getMultiCriteriaOptions().getMulticriteriaType().equals(MulticriteriaOptions.Type.COST_EFFECTIVENESS)){
							testCEADecisionNetwork(probNet, preResolutionEvidence);
							testCEAGlobalNetwork(probNet, preResolutionEvidence);
						}

					} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
						e.printStackTrace();
					}
				} else if(probNet.getNetworkType().equals(MIDType.getUniqueInstance())){
					try {
						if(probNet.getNodes(NodeType.DECISION).size() > 0){
							testResolveNetwork(probNet, preResolutionEvidence, true);
						} else {
							testResolveNetwork(probNet, preResolutionEvidence, false);
						}
						testPropagateNetwork(probNet, probNet.getVariables(), preResolutionEvidence);

						if(probNet.getInferenceOptions().getMultiCriteriaOptions().getMulticriteriaType().equals(MulticriteriaOptions.Type.COST_EFFECTIVENESS)){
							testCEADecisionNetwork(probNet, preResolutionEvidence);
							testCEAGlobalNetwork(probNet, preResolutionEvidence);
						}

						if(!probNet.hasConstraint(OnlyAtemporalVariables.class)){
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

	private void testCEAGlobalNetwork(ProbNet probNet, EvidenceCase evidenceCase) throws NotEvaluableNetworkException, IncompatibleEvidenceException, UnexpectedInferenceException {
		VECEAGlobal veceaGlobal = new VECEAGlobal(probNet, evidenceCase);
		assertNotNull(veceaGlobal.getGlobalUtility());
	}

	private void testCEADecisionNetwork(ProbNet probNet, EvidenceCase evidenceCase) throws NotEvaluableNetworkException, IncompatibleEvidenceException, UnexpectedInferenceException {
		VECEADecision veceaDecision = new VECEADecision(probNet,probNet.getNodes(NodeType.DECISION).get(0).getVariable(), evidenceCase);
		assertNotNull(veceaDecision.getGlobalUtility());
	}

	private void testPropagateNetwork(ProbNet probNet, List<Variable> variables, EvidenceCase evidenceCase) throws NotEvaluableNetworkException, IncompatibleEvidenceException, UnexpectedInferenceException {
		VEPropagation vePropagation	= new VEPropagation(probNet,variables, evidenceCase, null, null);
		HashMap<Variable, TablePotential> posteriorValues = vePropagation.getPosteriorValues();
		for(Variable variable : probNet.getVariables()){
			assertNotNull(posteriorValues.get(variable));
		}
		System.out.println("VEPropagation succesfull");
	}

	private void testResolveNetwork(ProbNet probNet, EvidenceCase evidenceCase, Boolean checkStrategy) throws NotEvaluableNetworkException, IncompatibleEvidenceException, UnexpectedInferenceException {
		VEResolution veResolution;
		if(evidenceCase != null){
			veResolution = new VEResolution(probNet, evidenceCase, null);
		} else {
			veResolution = new VEResolution(probNet, null, null);
		}


		if(checkStrategy){
			VEOptimalStrategy veOptimalStrategy = new VEOptimalStrategy(probNet, evidenceCase);
			assertNotNull(veOptimalStrategy.getOptimalStrategy());
		}

		System.out.println("VEResolution succesfull");
	}

	private void testTemporalEvolutionNetwork(ProbNet probNet, EvidenceCase evidenceCase) throws NotEvaluableNetworkException, IncompatibleEvidenceException, UnexpectedInferenceException {
		List<Variable> temporalVariables = probNet.getVariables();
		for(Variable variable : temporalVariables){
			if(variable.isTemporal()){
				VETemporalEvolution veTemporalEvolution = new VETemporalEvolution(probNet,variable, evidenceCase, null);
				assertNotNull(veTemporalEvolution.getPosteriorValues());
				for(int i = 0; i < probNet.getInferenceOptions().getTemporalOptions().getNumberOfSlices(); i++){
					try {
						Variable variableInSlicei = probNet.getVariable(variable.getBaseName(), i);
						assertNotNull(veTemporalEvolution.getPosteriorValues().get(variableInSlicei));
					} catch (NodeNotFoundException e) {
						e.printStackTrace();
					}

				}
			}
		}

	}
}
