package org.openmarkov.integrationTests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.inference.TransitionTime;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.model.network.*;
import org.openmarkov.core.model.network.potential.GTablePotential;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.inference.tasks.VariableElimination.VECEADecision;
import org.openmarkov.inference.tasks.VariableElimination.VETemporalEvaluation;
import org.openmarkov.inference.tasks.VariableElimination.VETemporalEvolution;
import org.openmarkov.io.probmodel.reader.PGMXReader;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

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
    public void veTemporalEvaluationTest(){
        try {
            long startTime = System.currentTimeMillis();
            VETemporalEvaluation veTemporalEvaluation = new VETemporalEvaluation(probNet, preResolutionEvidence, null);
            long endTime = System.currentTimeMillis();
            System.out.println("VETemporalEvaluation ha tardado:" + (endTime-startTime));

            double cost_NoBCI = 0;
            double eff_NoBCI = 0;
            double cost_SeqBCI = 0;
            double eff_SeqBCI = 0;
            double cost_SimBCI = 0;
            double eff_SimBCI = 0;

            ArrayList<TablePotential> utilityPotentials = new ArrayList<>();
            utilityPotentials.add(veTemporalEvaluation.getAtemporalUtility());
            List<TablePotential> temporaPotentials = new ArrayList<>();
            temporaPotentials.addAll(veTemporalEvaluation.getUtilityPotentialsPerSlice().subList(1, veTemporalEvaluation.getUtilityPotentialsPerSlice().size()));
            utilityPotentials.addAll(temporaPotentials);

            for (int i = 0; i < utilityPotentials.size(); i++) {
                GTablePotential utilityInSlice = (GTablePotential) veTemporalEvaluation.getUtilityPotentialsPerSlice().get(i);
                CEP cep_NoBCI = (CEP) utilityInSlice.elementTable.get(0);
                cost_NoBCI += cep_NoBCI.getCost(0);
                eff_NoBCI += cep_NoBCI.getEffectiveness(0);

                CEP cep_SeqBCI = (CEP) utilityInSlice.elementTable.get(1);
                cost_SeqBCI += cep_SeqBCI.getCost(0);
                eff_SeqBCI += cep_SeqBCI.getEffectiveness(0);;

                CEP cep_SimBCI = (CEP) utilityInSlice.elementTable.get(2);
                cost_SimBCI += cep_SimBCI.getCost(0);
                eff_SimBCI += cep_SimBCI.getEffectiveness(0);
            }

            //Comparing TemporalEvaluation with CE analysis at the beggining
            Variable decisionVariable = probNet.getVariable("Intervention decided");
            probNet.getInferenceOptions().getTemporalOptions().setTransition(TransitionTime.END);

            startTime = System.currentTimeMillis();
            VECEADecision veceaDecision = new VECEADecision(probNet, preResolutionEvidence, decisionVariable);
            endTime = System.currentTimeMillis();
            System.out.println("VECEADecision ha tardado:" + (endTime-startTime));

            GTablePotential tablePotential = veceaDecision.getCEPPotential();
            Assert.assertEquals(((CEP) tablePotential.elementTable.get(0)).getCost(0), cost_NoBCI, deltaEquals);
            Assert.assertEquals(((CEP) tablePotential.elementTable.get(0)).getEffectiveness(0), eff_NoBCI, deltaEquals);

            Assert.assertEquals(((CEP) tablePotential.elementTable.get(1)).getCost(0), cost_SeqBCI, deltaEquals);
            Assert.assertEquals(((CEP) tablePotential.elementTable.get(1)).getEffectiveness(0), eff_SeqBCI, deltaEquals);

            Assert.assertEquals(((CEP) tablePotential.elementTable.get(2)).getCost(0), cost_SimBCI, deltaEquals);
            Assert.assertEquals(((CEP) tablePotential.elementTable.get(2)).getEffectiveness(0), eff_SimBCI, deltaEquals);



        } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | NodeNotFoundException | UnexpectedInferenceException e) {
            e.printStackTrace();
        }
    }
}


