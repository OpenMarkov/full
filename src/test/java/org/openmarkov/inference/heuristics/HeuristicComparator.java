package org.openmarkov.inference.heuristics;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.type.BayesianNetworkType;
import org.openmarkov.core.model.network.type.NetworkType;
import org.openmarkov.inference.heuristic.canoAndMoral.CanoMoralElimination;
import org.openmarkov.inference.heuristic.hybridElimination.HybridElimination;
import org.openmarkov.inference.heuristic.minimalFillIn.MinimalFillIn;
import org.openmarkov.inference.heuristic.simpleElimination.SimpleElimination;
import org.openmarkov.inference.heuristics.score.TrivialHeuristicScore;
import org.openmarkov.io.probmodel.PGMXReader;

import bitbucket.NetsRepository;

/** Compares a set of heuristics with a score function obtained building a <code>HuginForest</code> 
 * in a collection of <code>ProbNet</code>s. 
 * @author Manuel Arias */
public class HeuristicComparator {

	private double[][] networkScores;
	
	// Constructor
	/**
	 */
	public HeuristicComparator(Collection<ProbNet> probNetsDB) {
	
		Class[] heuristicsClasses = new Class[] {CanoMoralElimination.class, MinimalFillIn.class, 
				HybridElimination.class, SimpleElimination.class};
		networkScores = new double[probNetsDB.size()][];
		int i = 0;
		for (ProbNet bayesianNetwork : probNetsDB) {
			TrivialHeuristicScore heuristicScore = new TrivialHeuristicScore(bayesianNetwork);
			double[] scores = heuristicScore.getScores(bayesianNetwork, heuristicsClasses);
			networkScores[i++] = scores;
		}
	}
	
	public double[][] getAllScores() {
		return networkScores;
	}
	
	// Methods
	/**
	 * @param networkType. <code>NetworkType</code>
	 */
	public static List<ProbNet> readProbNetsDB(NetworkType networkType) {
    	NetsRepository netsRepository = new NetsRepository();
    	List<URL> bayesianNetworksURLList = netsRepository.getNetworks(networkType);
    	PGMXReader reader = new PGMXReader();
    	List<ProbNet> probNetsDB = new ArrayList<ProbNet>();
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
    	// Order the networks, from smallest to largest number of variables
    	int numNetworks = probNetsDB.size();
    	ProbNet aux;
    	for (int i = 0; i < numNetworks - 1; i++) {
    		for (int j = i+1; j < numNetworks; j++) {
    			if (probNetsDB.get(i).getVariables().size() > probNetsDB.get(j).getVariables().size()) {
    				aux = probNetsDB.get(j);
    				probNetsDB.set(j, probNetsDB.get(i));
    				probNetsDB.set(i, aux);
    			}
    		}
    	}
    	
    	return probNetsDB;
	}

}
