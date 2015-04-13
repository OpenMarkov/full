package org.openmarkov.inference.heuristics;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.exception.WrongGraphStructureException;
import org.openmarkov.core.gui.util.NetworkType;
import org.openmarkov.core.inference.PartialOrder;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.type.BayesianNetworkType;
import org.openmarkov.inference.heuristic.canoAndMoral.CanoMoralElimination;
import org.openmarkov.inference.heuristic.hybridElimination.HybridElimination;
import org.openmarkov.inference.heuristic.minimalFillIn.MinimalFillIn;
import org.openmarkov.inference.heuristic.simpleElimination.SimpleElimination;
import org.openmarkov.inference.variableElimination.VariableElimination;
import org.openmarkov.io.probmodel.PGMXReader;

import bitbucket.NetsRepository;

public class HeuristicsTest {

	private Class[] heuristicsClasses = new Class[] {
			CanoMoralElimination.class, 
			MinimalFillIn.class, 
			HybridElimination.class, 
			SimpleElimination.class
			};
	

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		
		// Read all networks
		Collection<ProbNet> bayesianNetworks = readAllBayesianNetworks();
		// TODO finish
	}
	

	private Collection<ProbNet> readAllBayesianNetworks() {
    	NetsRepository netsRepository = new NetsRepository();
    	List<URL> bayesianNetworksURLList = netsRepository.getNetworks(BayesianNetworkType.getUniqueInstance());
    	PGMXReader reader = new PGMXReader();
    	Collection<ProbNet> probNetsDB = new ArrayList<ProbNet>();
    	List<String> wrongNetworksNames = new ArrayList<String>();
    	int readingErrors = 0;
    	for (URL bayesianNetworkURL : bayesianNetworksURLList) {
    		ProbNet probNet = null;
    		String fileName = null;
			try {
				fileName = bayesianNetworkURL.getFile();
				probNet = reader.loadProbNet(bayesianNetworkURL.openStream(), fileName).getProbNet();
				probNetsDB.add(probNet);
			} catch (ParserException | IOException e) {
				readingErrors++;
				wrongNetworksNames.add(fileName);
			}
    	}
    	if (readingErrors > 0) {
    		if (probNetsDB.isEmpty()) {
    			System.err.println("No Bayesian networks for testing due to reading errors.");
    		} else {
    			System.err.println("Some errors reading these networks:");
    		}
    		System.err.println();
			for (String wrongNetworkName : wrongNetworksNames) {
				System.err.println(wrongNetworkName);
			}
    	} else {
    		if (probNetsDB.isEmpty()) {
    			System.err.println("No networks found in repository.");
    		}
    	}

    	return probNetsDB;
	}

}
