package org.openmarkov.inference.heuristics;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.inference.heuristic.EliminationHeuristic;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.type.NetworkType;
import org.openmarkov.inference.heuristic.canoAndMoral.CanoMoralElimination;
import org.openmarkov.inference.heuristic.hybridElimination.HybridElimination;
import org.openmarkov.inference.heuristic.minimalFillIn.MinimalFillIn;
import org.openmarkov.inference.heuristic.simpleElimination.SimpleElimination;
import org.openmarkov.io.probmodel.PGMXReader;

import bitbucket.NetsRepository;

/** Compares two heuristics with a score function obtained performing 
 * the elimination in a collection of <code>ProbNet</code>s. 
 * @author Manuel Arias */
public class HeuristicComparator {

	// Attributes
	/** Collection of probNets to be used in the comparison. */
	private Collection<ProbNet> probNetsDB;
	
	private Class[] heuristicsClasses;
	
	// Constructor
	/**
	 * @param probNets. <code>Collection</code> of <code>ProbNet</code>s
	 */
	public HeuristicComparator(final Collection<ProbNet> probNets) {
		this.probNetsDB = probNets;
		heuristicsClasses = new Class[] {CanoMoralElimination.class, MinimalFillIn.class, 
				HybridElimination.class, SimpleElimination.class};
		// TODO finish
	}
	
	// Methods
	/**
	 * @param networkType. <code>NetworkType</code>
	 */
	public void readProbNetsDB(NetworkType networkType) {
    	NetsRepository netsRepository = new NetsRepository();
    	List<URL> bayesianNetworksURLList = netsRepository.getNetworks(networkType);
    	PGMXReader reader = new PGMXReader();
    	probNetsDB = new ArrayList<ProbNet>();
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
	}
	
}
