package org.openmarkov.inference.decompositionintosymmetricdans;

import java.io.InputStream;

import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.gui.window.dt.DecisionTreePanel;
import org.openmarkov.io.probmodel.reader.PGMXReader_0_2;

public class Tools {
	
	public ProbNet loadNetwork(String networkNameSuffix,String networkNamePrefix,String subfolderName) {
		String networkName = "networks/"+subfolderName+"/"+networkNamePrefix+"-" + networkNameSuffix + ".pgmx";
		InputStream file = getClass().getClassLoader().getResourceAsStream(networkName);

		PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
		ProbNetInfo probNetInfo = null;
		try {
			probNetInfo = pgmxReader.loadProbNetInfo(networkName, file);
		} catch (ParserException e) {
			e.printStackTrace();
		}
		return probNetInfo.getProbNet();
	}

	
	public ProbNet loadDAN(String nameSuffix) {
		return loadNetwork(nameSuffix,"DAN","dan");
	}
	
	public ProbNet loadID(String nameSuffix) {
		return loadNetwork(nameSuffix,"ID","id");
	}
	
	public static void buildDecisionTreePanelAndExpandLevels(ProbNet network) {
		DecisionTreePanel dt = new DecisionTreePanel(network);
		for (int i=0;i<1;i++) {
			dt.inferenceExpandLevels(1);
		}
	}



}
