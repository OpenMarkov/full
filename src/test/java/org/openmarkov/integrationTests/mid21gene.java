package org.openmarkov.integrationTests;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Before;
import org.junit.Test;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.inference.variableElimination.tasks.VEEvaluation;
import org.openmarkov.inference.variableElimination.tasks.VEPropagation;
import org.openmarkov.io.probmodel.reader.PGMXReader_0_2;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class mid21gene {


	// Delta parameter for Assert.Equals methods
	private final double deltaEquals = Math.pow(10, -4);

	private ProbNet probNet;
	private EvidenceCase preResolutionEvidence;

	@Before public void setUp() {
		Configurator.setRootLevel(Level.DEBUG);

		String networkName = "networks/mid/21-gene-190527B.pgmx";
		InputStream file = getClass().getClassLoader().getResourceAsStream(networkName);

		// Load the network: ID-decide-test
		PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
		ProbNetInfo probNetInfo = null;
		try {
			probNetInfo = pgmxReader.loadProbNetInfo(networkName, file);
		} catch (ParserException e) {
			e.printStackTrace();
		}
		assert probNetInfo != null;
		this.probNet = probNetInfo.getProbNet();

		if (probNetInfo.getEvidence().size() != 0) {
			this.preResolutionEvidence = probNetInfo.getEvidence().get(0);
		}
	}

	@Test public void veResolutionTestWithoutEvidence() {
		try {
			VEEvaluation evaluation = new VEEvaluation(probNet);
			LogManager.getLogger().debug("Utility " + evaluation.getUtility());

			VEPropagation vePropagation = new VEPropagation(probNet);
			List<Variable> variablesOfInterest = new ArrayList<>();
			variablesOfInterest.add(probNet.getVariable("Group"));
			vePropagation.setVariablesOfInterest(variablesOfInterest);
			for (Variable variable : vePropagation.getPosteriorValues().keySet()) {
				TablePotential potential = vePropagation.getPosteriorValues().get(variable);
				LogManager.getLogger().debug("Potential for " + variable.getBaseName() + " = " + evaluation.getUtility());
			}

		} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException | NodeNotFoundException e) {
			e.printStackTrace();
		}
	}
}
