package org.openmarkov.full.io;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

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
