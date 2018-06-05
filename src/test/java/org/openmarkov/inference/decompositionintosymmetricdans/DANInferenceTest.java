package org.openmarkov.inference.decompositionintosymmetricdans;

import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.io.probmodel.reader.PGMXReader_0_2;

import java.io.InputStream;

public abstract class DANInferenceTest {

	public ProbNet loadDAN(String nameSuffix) {
		String networkName = "networks/dan/DAN-" + nameSuffix + ".pgmx";
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

}
