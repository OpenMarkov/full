/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.inference;

import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.potential.Intervention;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.type.InfluenceDiagramType;

import bitbucket.NetsRepository;

public class IDInferenceTest {
	
	
	
	//	public void testInfluenceDiagramsInference() {

	

	//	@Test
	//	public void testInfluenceDiagramsInference() {
	//		NetsRepository netsRepository = new NetsRepository();
	//    	List<URL> influenceDiagramURLs = netsRepository.getNetworks(InfluenceDiagramType.getUniqueInstance());
	//    	PGMXReader reader = new PGMXReader();
	//    	for (URL influenceDiagramURL : influenceDiagramURLs) {
	//    		ProbNet influenceDiagram = null;
	//			try {
	//				influenceDiagram = reader.loadProbNet(influenceDiagramURL.openStream(), influenceDiagramURL.getFile()).getProbNet();
	//			} catch (ParserException | IOException e) {
	//				System.err.println("Can not read network: " + influenceDiagramURL.getFile());
	//				fail();
	//			}
	//			List<Criterion> decisionCriteria = influenceDiagram.getDecisionCriteria();
	//			if(decisionCriteria.size() ==  1)
	//			{
	//				// Convert to decision tree
	//				if(influenceDiagram.getNumNodes() < 10)
	//				{
	//					DecisionTreeElement equivalentDT = DecisionTreeBuilder.buildDecisionTree(influenceDiagram);
	//					Assert.assertNotNull(equivalentDT);
	//				}
	//				VariableElimination elimination = null;
	//	    		try {
	//					elimination = new VariableElimination(influenceDiagram);
	//					// Calculate expected utility with VariableElimination
	//					TablePotential expectedUtility = elimination.getGlobalUtility();
	//					Assert.assertNotNull(expectedUtility);
	//				} catch (NotEvaluableNetworkException e) {
	//					System.err.println("Network " + influenceDiagramURL.getFile() + " is not evaluable with VariableElimination");
	//					fail();
	//				} catch (Exception e) {
	//					System.err.println("VariableElimination inference failed to calculate the expected utility for network: " + influenceDiagramURL.getFile());
	//					fail();
	//				}
	//	    		//Calculate optimal strategy
	//	    		Intervention optimalStrategy = null;
	//	    		try {
	//	    			optimalStrategy = elimination.getOptimalIntervention();
	//				} catch (Exception e) {
	//					System.err.println("VariableElimination inference fails in: " + influenceDiagramURL.getFile());
	//					fail();
	//				}
	//	    		Assert.assertNotNull(optimalStrategy);
	//			}else if (decisionCriteria.size() >  1)
	//			{
	//				// Assume it is cost-effectiveness, since there is no good way to know it
	//				try {
	//					VariableEliminationCE varEliminationCE = new VariableEliminationCE(influenceDiagram, 0, Double.POSITIVE_INFINITY, influenceDiagram.getPNESupport());
	//					// Calculate cost-effectiveness partition
	//					Intervention intervention = varEliminationCE.getOptimalIntervention();
	//					Assert.assertNotNull(intervention);
	//				} catch (NotEvaluableNetworkException e) {
	//					System.err.println("Network " + influenceDiagramURL.getFile() + " is not evaluable with VariableEliminationCE");
	//					fail(e.getMessage());
	//				} catch (Exception e) {
	//					System.err.println("VariableEliminationCE inference fails in: " + influenceDiagramURL.getFile());
	//					e.printStackTrace();
	//					fail(e.getMessage());
	//				}
	//			}
	//    	}
	//    }
}
