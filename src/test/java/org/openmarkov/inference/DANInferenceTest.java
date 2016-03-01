package org.openmarkov.inference;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmarkov.core.dt.DecisionTreeBuilder;
import org.openmarkov.core.dt.DecisionTreeElement;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.inference.InferenceAlgorithm;
import org.openmarkov.core.model.network.Criterion;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.potential.Intervention;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.type.DecisionAnalysisNetworkType;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DecompositionAlgorithm;
import org.openmarkov.io.probmodel.PGMXReader;

import bitbucket.NetsRepository;

public class DANInferenceTest {

	// TODO - Check DANs inference when it becomes available
	// @Test
	public void testDANsInference() {
		NetsRepository netsRepository = new NetsRepository();
    	List<URL> danURLs = netsRepository.getNetworks(DecisionAnalysisNetworkType.getUniqueInstance());
    	PGMXReader reader = new PGMXReader();
    	for (URL danURL : danURLs) {
    		System.out.println("Checking " + danURL.toString());
    		ProbNet decisionAnalysisNetwork = null;
			try {
				decisionAnalysisNetwork = reader.loadProbNet(danURL.openStream(), danURL.getFile()).getProbNet();
			} catch (ParserException | IOException e) {
				System.err.println("Can not read network: " + danURL.getFile());
				fail();
			}
			List<Criterion> decisionCriteria = decisionAnalysisNetwork.getDecisionCriteria(); 
			if(decisionCriteria.size() ==  1)
			{
				// Convert to decision tree
				if(decisionAnalysisNetwork.getNumNodes() < 10)
				{
					DecisionTreeElement equivalentDT = DecisionTreeBuilder.buildDecisionTree(decisionAnalysisNetwork);
					Assert.assertNotNull(equivalentDT);
				}
				InferenceAlgorithm inferenceAlgorithm = null;
	    		try {
					inferenceAlgorithm = new DecompositionAlgorithm(decisionAnalysisNetwork);
					// Calculate expected utility 
					TablePotential expectedUtility = inferenceAlgorithm.getGlobalUtility();
					Assert.assertNotNull(expectedUtility);
				} catch (NotEvaluableNetworkException e) {
					System.err.println("Network " + danURL.getFile() + " is not evaluable with the Decomposition into Symmetric DANs");
					fail();
				} catch (Exception e) {
					System.err.println("Decomposition into Symmetric DANs failed to calculate the expected utility for network: " + danURL.getFile());
					fail();
				}
	    		//Calculate optimal strategy
	    		Intervention optimalStrategy = null;
	    		try {
	    			optimalStrategy = inferenceAlgorithm.getOptimalStrategy();
				} catch (Exception e) {
					System.err.println("Decomposition into Symmetric DANs inference fails in: " + danURL.getFile());
					fail();
				}
	    		Assert.assertNotNull(optimalStrategy);
			}else if (decisionCriteria.size() >  1)
			{
				// Assume it is cost-effectiveness, since there is no good way to know it
				//TODO CE with DANs
//				try {
//					VariableEliminationCE varEliminationCE = new VariableEliminationCE(decisionAnalysisNetwork, 0, Double.POSITIVE_INFINITY, decisionAnalysisNetwork.getPNESupport());
//					// Calculate cost-effectiveness partition
//					Intervention intervention = varEliminationCE.getOptimalIntervention();
//					Assert.assertNotNull(intervention);
//				} catch (NotEvaluableNetworkException e) {
//					System.err.println("Network " + danURL.getFile() + " is not evaluable with VariableEliminationCE");
//					fail(e.getMessage());
//				} catch (Exception e) {
//					System.err.println("VariableEliminationCE inference fails in: " + danURL.getFile());
//					e.printStackTrace();
//					fail(e.getMessage());
//				}
			}
    	}	
    }
}
