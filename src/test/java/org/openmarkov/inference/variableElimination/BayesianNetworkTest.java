/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.inference.variableElimination;

import org.junit.Before;
import org.junit.Test;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.type.BayesianNetworkType;

/**
 * @author Manuel Arias
 */
public class BayesianNetworkTest {

	@Before public void setUp() throws Exception {
	}

	//    @Test
	//    public void testBayesianNetworksInference() {
	//    	NetsRepository netsRepository = new NetsRepository();
	//    	List<URL> bayesianNetworksURLList = netsRepository.getNetworks(BayesianNetworkType.getUniqueInstance());
	//    	PGMXReader reader = new PGMXReader();
	//    	for (URL bayesianNetworkURL : bayesianNetworksURLList) {
	//    		ProbNet probNet = null;
	//			try {
	//				probNet = reader.loadProbNet(bayesianNetworkURL.openStream(), bayesianNetworkURL.getFile()).getProbNet();
	//				System.out.println("Checking network: " + bayesianNetworkURL.getFile());
	//			} catch (ParserException | IOException e) {
	//				System.err.println("Can not read network: " + bayesianNetworkURL.getFile());
	//				fail();
	//			}
	//			VariableEliminationCore elimination = null;
	//    		try {
	//				elimination = new VariableEliminationCore(probNet);
	//			} catch (NotEvaluableNetworkException e) {
	//				System.err.println("Not evaluable network: " + bayesianNetworkURL.getFile());
	//				fail();
	//			}
	//    		try {
	//				elimination.getPosteriorValues();
	//			} catch (Exception e) {
	//				System.err.println("VariableElimination inference fails in: " + bayesianNetworkURL.getFile());
	//				fail();
	//			}
	//    	}
	//    }

}
