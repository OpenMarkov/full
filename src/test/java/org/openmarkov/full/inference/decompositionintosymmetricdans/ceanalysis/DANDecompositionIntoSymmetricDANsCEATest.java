package org.openmarkov.full.inference.decompositionintosymmetricdans.ceanalysis;

import org.openmarkov.core.inference.tasks.CEAnalysis;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.ceanalysis.DANDecompositionIntoSymmetricDANsCEA;

public class DANDecompositionIntoSymmetricDANsCEATest extends DANCEATest {

	protected CEAnalysis buildCEAnalysis(ProbNet network) {
		CEAnalysis cea = null;
		cea = new DANDecompositionIntoSymmetricDANsCEA(network);
		return cea;
	}

}
