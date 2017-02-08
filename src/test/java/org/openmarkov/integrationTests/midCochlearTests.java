package org.openmarkov.integrationTests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.model.network.CEP;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.GTablePotential;
import org.openmarkov.core.model.network.potential.Intervention;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDBranch;
import org.openmarkov.inference.tasks.VariableElimination.VECEADecision;
import org.openmarkov.inference.tasks.VariableElimination.VECEAGlobal;
import org.openmarkov.inference.tasks.VariableElimination.VEOptimalIntervention;
import org.openmarkov.inference.tasks.VariableElimination.VEOptimalPolicy;
import org.openmarkov.inference.tasks.VariableElimination.VEResolution;
import org.openmarkov.inference.tasks.VariableElimination.VETemporalEvaluation;
import org.openmarkov.inference.tasks.VariableElimination.VETemporalEvolution;
import org.openmarkov.io.probmodel.reader.PGMXReader;

import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by JORGE on 08/02/2017.
 */
public class midCochlearTests {
    private final String networkName = "networks/mid/MID-Cochlear.pgmx";

    // Delta parameter for Assert.Equals methods
    private final double deltaEquals = Math.pow(10,-4);

    private ProbNet probNet;
    private EvidenceCase preResolutionEvidence;

    @Before
    public void setUp() throws Exception {
        InputStream file = getClass().getClassLoader().getResourceAsStream(networkName);

        // Load the network: ID-decide-test
        PGMXReader pgmxReader = new PGMXReader();
        ProbNetInfo probNetInfo = null;
        try {
            probNetInfo = pgmxReader.loadProbNetInfo(networkName, file);
        } catch (ParserException e) {
            e.printStackTrace();
        }
        this.probNet = probNetInfo.getProbNet();
        if (probNetInfo.getEvidence().size() != 0) {
            this.preResolutionEvidence = probNetInfo.getEvidence().get(0);
        }
    }

    @Test
    public void veTemporalEvolutionTest(){
        try {
            VETemporalEvaluation veTemporalEvaluation = new VETemporalEvaluation(probNet, preResolutionEvidence, null);

        } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
            e.printStackTrace();
        }
    }
}

