package org.openmarkov.full.core.inference;


import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.List;

import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.inference.tasks.CEAnalysis;
import org.openmarkov.core.model.network.Criterion;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNetOperations;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.constraint.OnlyAtemporalVariables;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.type.BayesianNetworkType;
import org.openmarkov.core.model.network.type.InfluenceDiagramType;
import org.openmarkov.core.model.network.type.MIDType;
import org.openmarkov.inference.algorithm.variableElimination.tasks.VECEAnalysis;
import org.openmarkov.inference.algorithm.variableElimination.tasks.VECEPSA;
import org.openmarkov.inference.algorithm.variableElimination.tasks.VEEvaluation;
import org.openmarkov.inference.algorithm.variableElimination.tasks.VEOptimalIntervention;
import org.openmarkov.inference.algorithm.variableElimination.tasks.VEPropagation;
import org.openmarkov.inference.algorithm.variableElimination.tasks.VETemporalEvolution;

public class InferenceTestsTools {
	
	public static void testResolveNetwork(ProbNet probNet, EvidenceCase evidenceCase)
			throws NotEvaluableNetworkException, IncompatibleEvidenceException, UnexpectedInferenceException {
		testResolveNetwork(probNet,evidenceCase, true);
	}
	
	public static void testResolveNetwork(ProbNet probNet, EvidenceCase evidenceCase, boolean checkStrategyTree)
			throws NotEvaluableNetworkException, IncompatibleEvidenceException, UnexpectedInferenceException {
		VEEvaluation veEvaluation;
		if (evidenceCase != null) {
			veEvaluation = new VEEvaluation(probNet);
			veEvaluation.setPreResolutionEvidence(evidenceCase);
		} else {
			veEvaluation = new VEEvaluation(probNet);
		}
		double meu = veEvaluation.getUtility().getFirstValue();

		if (checkStrategyTree && thereAreDecisionNodes(probNet)) {
			VEOptimalIntervention veOptimalStrategy = new VEOptimalIntervention(probNet, evidenceCase);
			assertNotNull(veOptimalStrategy.getOptimalIntervention());
		}

		System.out.println("VEResolution successful");
	}
	
	public static void testPropagateNetwork(ProbNet probNet, List<Variable> variables, EvidenceCase evidenceCase)
			throws NotEvaluableNetworkException, IncompatibleEvidenceException, UnexpectedInferenceException {
		VEPropagation vePropagation = null;
		if (!probNet.getNetworkType().equals(BayesianNetworkType.getUniqueInstance())) {
			VEEvaluation veEvaluation = new VEEvaluation(probNet);
			vePropagation = new VEPropagation(probNet, veEvaluation.getOptimalPolicies());
		} else {
			vePropagation = new VEPropagation(probNet);
		}


		vePropagation.setVariablesOfInterest(variables);
		vePropagation.setPreResolutionEvidence(evidenceCase);
		HashMap<Variable, TablePotential> posteriorValues = vePropagation.getPosteriorValues();
		for (Variable variable : probNet.getVariables()) {
			if (!variable.getVariableType().equals(VariableType.NUMERIC)) {
				assertNotNull(posteriorValues.get(variable));
			}
		}
		System.out.println("VEPropagation successful");
	}
	
	public static void testBasicInference(ProbNet probNet, EvidenceCase preResolutionEvidence, int numSimulations,
			boolean useMultithreading) {
		if (probNet.getNetworkType().equals(BayesianNetworkType.getUniqueInstance())) {
			try {
				testPropagateNetwork(probNet, probNet.getVariables(), preResolutionEvidence);
			} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
				e.printStackTrace();
			}
		} else if (probNet.getNetworkType().equals(InfluenceDiagramType.getUniqueInstance())) {
			try {
				testResolutionAndPropagation(probNet, preResolutionEvidence);

				if (hasCostEffectiveness(probNet)) {
					testCEADecisionNetwork(probNet, preResolutionEvidence);
					testCEAGlobalNetwork(probNet, preResolutionEvidence);
					testCEPSANetwork(probNet, preResolutionEvidence, numSimulations, useMultithreading);
				}

			} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
				e.printStackTrace();
			}
		} else if (probNet.getNetworkType().equals(MIDType.getUniqueInstance())) {
			try {
				testResolutionAndPropagation(probNet, preResolutionEvidence);

				if (hasCostEffectiveness(probNet)) {
					testCEADecisionNetwork(probNet, preResolutionEvidence);
					testCEAGlobalNetwork(probNet, preResolutionEvidence);
					testCEPSANetwork(probNet, preResolutionEvidence, numSimulations, useMultithreading);
				}

				if (!probNet.hasConstraint(OnlyAtemporalVariables.class)) {
					testTemporalEvolutionNetwork(probNet, preResolutionEvidence);
				}

			} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
				e.printStackTrace();
			}
		}
	}

	private static void testResolutionAndPropagation(ProbNet probNet, EvidenceCase preResolutionEvidence)
			throws NotEvaluableNetworkException, IncompatibleEvidenceException, UnexpectedInferenceException {
		testResolveNetwork(probNet, preResolutionEvidence);

		// TODO - Check propagate errors
		testPropagateNetwork(probNet, probNet.getVariables(), preResolutionEvidence);
	}
	
	private static boolean thereAreDecisionNodes(ProbNet network) {
		return network.getNodes(NodeType.DECISION).size() > 0;
	}
	
	private static void testTemporalEvolutionNetwork(ProbNet probNet, EvidenceCase evidenceCase)
			throws NotEvaluableNetworkException, IncompatibleEvidenceException, UnexpectedInferenceException {
		HashMap<String, Variable> filteredTemporalVariables = new HashMap<>();
		for (Variable variable : probNet.getVariables()) {
			if (variable.isTemporal()) {
				if (!variable.getVariableType().equals(VariableType.NUMERIC)) {
					Variable oldVariable = filteredTemporalVariables.get(variable.getBaseName());
					if (oldVariable != null) {
						if (variable.getTimeSlice() < oldVariable.getTimeSlice()) {
							filteredTemporalVariables.remove(oldVariable);
							filteredTemporalVariables.put(variable.getBaseName(), variable);
						}
					} else {
						filteredTemporalVariables.put(variable.getBaseName(), variable);
					}
				} else {
					if (probNet.getNode(variable).getNodeType().equals(NodeType.UTILITY)) {
						Variable oldVariable = filteredTemporalVariables.get(variable.getBaseName());
						if (oldVariable != null) {
							if (variable.getTimeSlice() < oldVariable.getTimeSlice()) {
								filteredTemporalVariables.remove(oldVariable);
								filteredTemporalVariables.put(variable.getBaseName(), variable);
							}
						} else {
							filteredTemporalVariables.put(variable.getBaseName(), variable);
						}
					}
				}
			}
		}

		for (Variable variable : filteredTemporalVariables.values()) {

			VETemporalEvolution veTemporalEvolution = new VETemporalEvolution(probNet, variable);
			veTemporalEvolution.setPreResolutionEvidence(evidenceCase);
			ProbNet expandedNetwork = veTemporalEvolution.getExpandedNetwork();
			assertNotNull(veTemporalEvolution.getTemporalEvolution());
			for (int i = variable.getTimeSlice();
                 i < expandedNetwork.getInferenceOptions().getTemporalOptions().getHorizon(); i++) {
				try {
					Variable variableInSlicei = expandedNetwork.getVariable(variable.getBaseName(), i);
					assertNotNull(veTemporalEvolution.getTemporalEvolution().get(variableInSlicei));
				} catch (NodeNotFoundException e) {
					e.printStackTrace();
				}

			}

		}

		System.out.println("VETemporalEvolution successful");
	}

	
	private static void testCEADecisionNetwork(ProbNet probNet, EvidenceCase evidenceCase)
			throws NotEvaluableNetworkException, IncompatibleEvidenceException, UnexpectedInferenceException {
		List<Variable> decisionVariables = probNet.getVariables(NodeType.DECISION);

		for (Variable decisionVariable : decisionVariables) {
			List<Variable> informationalPredecesors = ProbNetOperations
					.getInformationalPredecessors(probNet, decisionVariable);
			informationalPredecesors.remove(decisionVariable);

			for (Variable informationalPredecesor : informationalPredecesors) {
				// Set the first state as an evidence
				Finding finding = new Finding(informationalPredecesor, informationalPredecesor.getStates()[0]);
				try {
					evidenceCase.addFinding(finding);
				} catch (InvalidStateException e) {
					e.printStackTrace();
				}
			}
			CEAnalysis veceaDecision = new VECEAnalysis(probNet);
			veceaDecision.setPreResolutionEvidence(evidenceCase);
			veceaDecision.setDecisionVariable(decisionVariable);
			assertNotNull(veceaDecision.getUtility());
		}
		System.out.println("VECEADecision successful");
	}
	
	private static boolean hasCostEffectiveness(ProbNet probNet) {
		
		boolean hasCost = false;
		boolean hasEffectiveness = false;

		for (Criterion criterion : probNet.getDecisionCriteria()) {
			if (criterion.getCECriterion().equals(Criterion.CECriterion.Cost)) {
				hasCost = true;
			} else if (criterion.getCECriterion().equals(Criterion.CECriterion.Effectiveness)) {
				hasEffectiveness = true;
			}
		}

		return hasCost && hasEffectiveness;
	}
	
	
	
	private static void testCEAGlobalNetwork(ProbNet probNet, EvidenceCase evidenceCase)
			throws NotEvaluableNetworkException, IncompatibleEvidenceException, UnexpectedInferenceException {
		CEAnalysis veceaGlobal = new VECEAnalysis(probNet);
		veceaGlobal.setPreResolutionEvidence(evidenceCase);
		assertNotNull(veceaGlobal.getUtility());
		System.out.println("VECEAGlobal successful");
	}

	

	private static void testCEPSANetwork(ProbNet probNet, EvidenceCase evidenceCase, int numSimulations,
			boolean useMultithreading) {
		List<Variable> decisionVariables = probNet.getVariables(NodeType.DECISION);

		for (Variable decisionVariable : decisionVariables) {
			List<Variable> informationalPredecesors = ProbNetOperations
					.getInformationalPredecessors(probNet, decisionVariable);
			informationalPredecesors.remove(decisionVariable);

			for (Variable informationalPredecesor : informationalPredecesors) {
				// Set the first state as an evidence
				Finding finding = new Finding(informationalPredecesor, informationalPredecesor.getStates()[0]);
				try {
					evidenceCase.addFinding(finding);
				} catch (InvalidStateException | IncompatibleEvidenceException e) {
					e.printStackTrace();
				}
			}
			try {
				VECEPSA vecepsa = null;
				vecepsa = new VECEPSA(probNet);
				vecepsa.setPreResolutionEvidence(evidenceCase);
				vecepsa.setDecisionVariable(decisionVariable);
				vecepsa.setNumSimulations(numSimulations);
				vecepsa.setUseMultithreading(useMultithreading);
				assertNotNull(vecepsa.getCEPPotentials());

			} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
				e.printStackTrace();
			}

		}
		System.out.println("VECEPSA successful");
	}

	




}
