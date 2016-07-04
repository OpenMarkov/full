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
import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.exception.WriterException;
import org.openmarkov.core.gui.dialog.io.NetsIO;
import org.openmarkov.core.model.network.ProbNet;
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

	HashSet<String> skippedNetworkNames = new HashSet<>();

	/**
	 * This setup method allow to add networks to the skip list
	 */
	@Before
	public void setUp(){
//		//Already passed with VEPropagation
//		skippedNetworkNames.add("BN-alarm.pgmx");
//		skippedNetworkNames.add("BN-asia.pgmx");
//		skippedNetworkNames.add("BN-catarnet.pgmx");
//		skippedNetworkNames.add("BN-hepar.pgmx");
//		skippedNetworkNames.add("BN-nasonet.pgmx");
//		skippedNetworkNames.add("BN-one-disease.pgmx");
//		skippedNetworkNames.add("BN-prostanet.pgmx");
//		skippedNetworkNames.add("BN-two-diseases.pgmx");
//
//		//Already passed with load, save and reload
//		skippedNetworkNames.add("DAN-3-test-problem.pgmx");
//		skippedNetworkNames.add("DAN-4-test-problem.pgmx");
//		skippedNetworkNames.add("DAN-5-test-problem.pgmx");
//		skippedNetworkNames.add("DAN-6-test-problem.pgmx");
//		skippedNetworkNames.add("DAN-7-test-problem.pgmx");
//		skippedNetworkNames.add("DAN-arthronet.pgmx");
//		skippedNetworkNames.add("DAN-dating.pgmx");
//		skippedNetworkNames.add("DAN-decide-test-ce.pgmx");
//		skippedNetworkNames.add("DAN-decide-test-ordered.pgmx");
//		skippedNetworkNames.add("DAN-decide-test-symptom.pgmx");
//		skippedNetworkNames.add("DAN-decide-test-with-restrictive-symptom.pgmx");
//		skippedNetworkNames.add("DAN-decide-test-with-symptom.pgmx");
//		skippedNetworkNames.add("DAN-decide-test.pgmx");
//		skippedNetworkNames.add("DAN-delayed-result-of-test.pgmx");
//		skippedNetworkNames.add("DAN-diabetes.pgmx");
//		skippedNetworkNames.add("DAN-economic-mediastinet.pgmx");
//		skippedNetworkNames.add("DAN-king.pgmx");
//		skippedNetworkNames.add("DAN-mediastinet.pgmx");
//		skippedNetworkNames.add("DAN-qale-mediastinet.pgmx");
//		skippedNetworkNames.add("DAN-reactor.pgmx");
//		skippedNetworkNames.add("DAN-test-always.pgmx");
//		skippedNetworkNames.add("DAN-unordered-two-decs.pgmx");
//		skippedNetworkNames.add("DAN-used-car-buyer.pgmx");
//		skippedNetworkNames.add("LIMID-Nilsson-Lauritzen.pgmx");
//		skippedNetworkNames.add("LIMID-decide-test-symptom.pgmx");
//		skippedNetworkNames.add("Dec-POMDP-wireless-network.pgmx");
//		skippedNetworkNames.add("POMDP-coffee-robot.pgmx");
//
////		// TODO - Check CEA: Already passed with VEResolution, VEPropagation, VETemporalEvolution, VECEADecision, VECEAGlobal, VECEPSA
//		skippedNetworkNames.add("ID-CEA-minimal.pgmx");
//		skippedNetworkNames.add("ID-CEA-test-2therapies-3criteria.pgmx");
//		skippedNetworkNames.add("ID-CEA-test-2therapies-new-test.pgmx");
//		skippedNetworkNames.add("ID-CEA-test-2therapies.pgmx");
//		skippedNetworkNames.add("ID-decide-test-without-dummy-state.pgmx");
//		skippedNetworkNames.add("ID-decide-test.pgmx");
//		skippedNetworkNames.add("MID-Chancellor.pgmx");
//		skippedNetworkNames.add("MID-hip-Briggs.pgmx");
//		skippedNetworkNames.add("MID-dmhee-2.5.pgmx");
//		skippedNetworkNames.add("MID-dmhee-3.5.pgmx");
//		skippedNetworkNames.add("MID-dmhee-4.7.pgmx");
//		skippedNetworkNames.add("MID-dmhee-4.8.pgmx");
//
//		// TODO - Failed on VEPropagation (Draw/Tie Policy ?)
		skippedNetworkNames.add("ID-delayed-result-of-test.pgmx");
//
//		// TODO - Failed getting optimal intervention on Resolution
		skippedNetworkNames.add("ID-mediastinet-ce.pgmx");

		// TODO - Failed on Resolution?
		skippedNetworkNames.add("MID-CHD-Walker.pgmx");

//		// TODO - Failed in VEPropagation (All with supervalue nodes)
		skippedNetworkNames.add("ID-arthronet.pgmx");
		skippedNetworkNames.add("ID-arthronet-ce.pgmx");
		skippedNetworkNames.add("ID-mediastinet.pgmx");
		skippedNetworkNames.add("ID-used-car-buyer.pgmx");
		skippedNetworkNames.add("MID-CHAP-Ryan-Griffin.pgmx");
		skippedNetworkNames.add("MID-HPV.pgmx");

		// Too big
		skippedNetworkNames.add("MID-Cochlear.pgmx");
		skippedNetworkNames.add("MID-Colorectal.pgmx");

		// TODO - only for 'Augmented bayesian networks' branch
		skippedNetworkNames.add("ID-decide-test-0.4.0.pgmx");
		skippedNetworkNames.add("ID-decide-test-0.5.0.pgmx");

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
				probNet = pgmxReader.loadProbNet(file, networkName).getProbNet();
				assertNotNull(probNet);
				assertNotNull(probNet.getNodes());

				
			} catch (WriterException | FileNotFoundException | ParserException e) {
				e.printStackTrace();
				
			} finally{
				File fileToBeDeleted = new File(networkName);
				fileToBeDeleted.delete();
			}
        }
	}
}
