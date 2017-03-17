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
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.inference.tasks.VariableElimination.VECEADecision;
import org.openmarkov.inference.tasks.VariableElimination.VETemporalEvaluation;
import org.openmarkov.inference.tasks.VariableElimination.VETemporalEvolution;
import org.openmarkov.io.probmodel.reader.PGMXReader;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

//    @Test
//    public void veTemporalEvaluationTest(){
//        try {
//            long startTime = System.currentTimeMillis();
//            VETemporalEvaluation veTemporalEvaluation = new VETemporalEvaluation(probNet, preResolutionEvidence, null);
//            long endTime = System.currentTimeMillis();
//            System.out.println("VETemporalEvaluation ha tardado:" + (endTime-startTime));
//
//            double cost_NoBCI = 0;
//            double eff_NoBCI = 0;
//            double cost_SeqBCI = 0;
//            double eff_SeqBCI = 0;
//            double cost_SimBCI = 0;
//            double eff_SimBCI = 0;
//
//            ArrayList<TablePotential> utilityPotentials = new ArrayList<>();
//            utilityPotentials.add(veTemporalEvaluation.getAtemporalUtility());
//            List<TablePotential> temporaPotentials = new ArrayList<>();
//            temporaPotentials.addAll(veTemporalEvaluation.getUtilityPotentialsPerSlice().subList(1, veTemporalEvaluation.getUtilityPotentialsPerSlice().size()));
//            utilityPotentials.addAll(temporaPotentials);
//
//            for (int i = 0; i < utilityPotentials.size(); i++) {
//                GTablePotential utilityInSlice = (GTablePotential) veTemporalEvaluation.getUtilityPotentialsPerSlice().get(i);
//                CEP cep_NoBCI = (CEP) utilityInSlice.elementTable.get(0);
//                cost_NoBCI += cep_NoBCI.getCost(0);
//                eff_NoBCI += cep_NoBCI.getEffectiveness(0);
//
//                CEP cep_SeqBCI = (CEP) utilityInSlice.elementTable.get(1);
//                cost_SeqBCI += cep_SeqBCI.getCost(0);
//                eff_SeqBCI += cep_SeqBCI.getEffectiveness(0);;
//
//                CEP cep_SimBCI = (CEP) utilityInSlice.elementTable.get(2);
//                cost_SimBCI += cep_SimBCI.getCost(0);
//                eff_SimBCI += cep_SimBCI.getEffectiveness(0);
//            }
//
//            //Comparing TemporalEvaluation with CE analysis at the beggining
//            Variable decisionVariable = probNet.getVariable("Intervention decided");
//            probNet.getInferenceOptions().getTemporalOptions().setTransition(TransitionTime.END);
//
//            startTime = System.currentTimeMillis();
//            VECEADecision veceaDecision = new VECEADecision(probNet, preResolutionEvidence, decisionVariable);
//            endTime = System.currentTimeMillis();
//            System.out.println("VECEADecision ha tardado:" + (endTime-startTime));
//
//            GTablePotential tablePotential = veceaDecision.getCEPPotential();
//            Assert.assertEquals(((CEP) tablePotential.elementTable.get(0)).getCost(0), cost_NoBCI, deltaEquals);
//            Assert.assertEquals(((CEP) tablePotential.elementTable.get(0)).getEffectiveness(0), eff_NoBCI, deltaEquals);
//
//            Assert.assertEquals(((CEP) tablePotential.elementTable.get(1)).getCost(0), cost_SeqBCI, deltaEquals);
//            Assert.assertEquals(((CEP) tablePotential.elementTable.get(1)).getEffectiveness(0), eff_SeqBCI, deltaEquals);
//
//            Assert.assertEquals(((CEP) tablePotential.elementTable.get(2)).getCost(0), cost_SimBCI, deltaEquals);
//            Assert.assertEquals(((CEP) tablePotential.elementTable.get(2)).getEffectiveness(0), eff_SimBCI, deltaEquals);
//
//
//
//        } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | NodeNotFoundException | UnexpectedInferenceException e) {
//            e.printStackTrace();
//        }
//    }

    @Test
    public void veTemporalEvolutionTest(){
        try {
            VETemporalEvaluation veTemporalEvaluation = new VETemporalEvaluation(probNet, preResolutionEvidence, null);
            TablePotential atemporalUtility = veTemporalEvaluation.getAtemporalUtility();
            Assert.assertEquals(0, atemporalUtility.values[0], deltaEquals);
            Assert.assertEquals(21639.98, atemporalUtility.values[1], deltaEquals);
            Assert.assertEquals(26100, atemporalUtility.values[2], deltaEquals);

            List<TablePotential> potentialsPerSlice = veTemporalEvaluation.getUtilityPotentialsPerSlice();
            double[] costs_UCI = new double[101];
            double[] effectiveness_UCI = new double[101];
            double[] costs_BCI_Sim = new double[101];
            double[] effectiveness_BCI_Sim = new double[101];
            double[] costs_BCI_Seq = new double[101];
            double[] effectiveness_BCI_Seq = new double[101];

            int slice = 0;
            for (TablePotential tablePotential : potentialsPerSlice) {
                costs_UCI[slice] = ((CEP)((GTablePotential) tablePotential).elementTable.get(0)).getCost(0);
                effectiveness_UCI[slice] = ((CEP)((GTablePotential) tablePotential).elementTable.get(0)).getEffectiveness(0);
                costs_BCI_Sim[slice] = ((CEP)((GTablePotential) tablePotential).elementTable.get(1)).getCost(0);
                effectiveness_BCI_Sim[slice] = ((CEP)((GTablePotential) tablePotential).elementTable.get(1)).getEffectiveness(0);
                costs_BCI_Seq[slice] = ((CEP)((GTablePotential) tablePotential).elementTable.get(2)).getCost(0);
                effectiveness_BCI_Seq[slice] = ((CEP)((GTablePotential) tablePotential).elementTable.get(2)).getEffectiveness(0);
                slice++;
            }

            double c_UCI = UtilityOperations.applyLeftRiemannSum(costs_UCI, 1) + atemporalUtility.values[0];
            double e_UCI = UtilityOperations.applyLeftRiemannSum(effectiveness_UCI, 1);

            double c_BCI_Sim = UtilityOperations.applyLeftRiemannSum(costs_BCI_Sim, 1) + atemporalUtility.values[1];
            double e_BCI_Sim = UtilityOperations.applyLeftRiemannSum(effectiveness_BCI_Sim, 1);

            double c_BCI_Seq = UtilityOperations.applyLeftRiemannSum(costs_BCI_Seq, 1) + atemporalUtility.values[2];
            double e_BCI_Seq = UtilityOperations.applyLeftRiemannSum(effectiveness_BCI_Seq, 1);

            Variable decisionVariable = null;
            try {
                decisionVariable = probNet.getVariable("Intervention decided");
            } catch (NodeNotFoundException e) {
                e.printStackTrace();
            }

            //Asserting that Left Rieman summ is equals to a transition at the end
            probNet.getInferenceOptions().getTemporalOptions().setTransition(TransitionTime.END);
            VECEADecision veceaDecision = new VECEADecision(probNet,preResolutionEvidence,decisionVariable);
            GTablePotential ceaResult = veceaDecision.getCEPPotential();
            double c_uci_cea = ((CEP)(ceaResult.elementTable.get(0))).getCost(0);
            double e_uci_cea = ((CEP)(ceaResult.elementTable.get(0))).getEffectiveness(0);
            double c_bciSim_cea = ((CEP)(ceaResult.elementTable.get(1))).getCost(0);
            double e_bciSim_cea = ((CEP)(ceaResult.elementTable.get(1))).getEffectiveness(0);
            double c_bciSeq_cea = ((CEP)(ceaResult.elementTable.get(2))).getCost(0);
            double e_bciSeq_cea = ((CEP)(ceaResult.elementTable.get(2))).getEffectiveness(0);

            Assert.assertEquals(c_UCI, c_uci_cea, deltaEquals);
            Assert.assertEquals(e_UCI, e_uci_cea, deltaEquals);
            Assert.assertEquals(c_BCI_Sim, c_bciSim_cea, deltaEquals);
            Assert.assertEquals(e_BCI_Sim, e_bciSim_cea, deltaEquals);
            Assert.assertEquals(c_BCI_Seq, c_bciSeq_cea, deltaEquals);
            Assert.assertEquals(e_BCI_Seq, e_bciSeq_cea, deltaEquals);

            //Asserting that Right Riemann Summ is equals to a transition at the beginning
            probNet.getInferenceOptions().getTemporalOptions().setTransition(TransitionTime.BEGINNING);
            c_UCI = UtilityOperations.applyRightRiemannSum(costs_UCI, 1) + atemporalUtility.values[0];
            e_UCI = UtilityOperations.applyRightRiemannSum(effectiveness_UCI, 1);

            c_BCI_Sim = UtilityOperations.applyRightRiemannSum(costs_BCI_Sim, 1) + atemporalUtility.values[1];
            e_BCI_Sim = UtilityOperations.applyRightRiemannSum(effectiveness_BCI_Sim, 1);

            c_BCI_Seq = UtilityOperations.applyRightRiemannSum(costs_BCI_Seq, 1) + atemporalUtility.values[2];
            e_BCI_Seq = UtilityOperations.applyRightRiemannSum(effectiveness_BCI_Seq, 1);

            veceaDecision = new VECEADecision(probNet,preResolutionEvidence,decisionVariable);
            ceaResult = veceaDecision.getCEPPotential();
            c_uci_cea = ((CEP)(ceaResult.elementTable.get(0))).getCost(0);
            e_uci_cea = ((CEP)(ceaResult.elementTable.get(0))).getEffectiveness(0);
            c_bciSim_cea = ((CEP)(ceaResult.elementTable.get(1))).getCost(0);
            e_bciSim_cea = ((CEP)(ceaResult.elementTable.get(1))).getEffectiveness(0);
            c_bciSeq_cea = ((CEP)(ceaResult.elementTable.get(2))).getCost(0);
            e_bciSeq_cea = ((CEP)(ceaResult.elementTable.get(2))).getEffectiveness(0);

            Assert.assertEquals(c_UCI, c_uci_cea, deltaEquals);
            Assert.assertEquals(e_UCI, e_uci_cea, deltaEquals);
            Assert.assertEquals(c_BCI_Sim, c_bciSim_cea, deltaEquals);
            Assert.assertEquals(e_BCI_Sim, e_bciSim_cea, deltaEquals);
            Assert.assertEquals(c_BCI_Seq, c_bciSeq_cea, deltaEquals);
            Assert.assertEquals(e_BCI_Seq, e_bciSeq_cea, deltaEquals);

        } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void veTemporalEvaluationVSCEADecisionTest(){
        try {
            probNet.getInferenceOptions().getTemporalOptions().setNumberOfSlices(100);
            long startTime = System.currentTimeMillis();
            VETemporalEvaluation veTemporalEvaluation = new VETemporalEvaluation(probNet, preResolutionEvidence, null);
            long endTime = System.currentTimeMillis();
            System.out.println("VETemporalEvaluation ha tardado:" + (endTime-startTime));

            //Comparing TemporalEvaluation with CE analysis at the beggining
            Variable decisionVariable = probNet.getVariable("Intervention decided");
            probNet.getInferenceOptions().getTemporalOptions().setTransition(TransitionTime.END);

            startTime = System.currentTimeMillis();
            VECEADecision veceaDecision = new VECEADecision(probNet, preResolutionEvidence, decisionVariable);
            endTime = System.currentTimeMillis();
            System.out.println("VECEADecision ha tardado:" + (endTime-startTime));

        } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | NodeNotFoundException | UnexpectedInferenceException e) {
            e.printStackTrace();
        }
    }

    Variable decision;
    private int numSimulations = 20;
    @Test
    public void psaCEAvsTemporalEvaluationTest(){
        long startTime = System.currentTimeMillis();
        try {
            decision = probNet.getVariable("Intervention decided");
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
        }
        int numThreads = Runtime.getRuntime().availableProcessors();
        boolean success = false;
        while (!success && numThreads > 0) {
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            List<Future<GTablePotential>> list = new ArrayList<>();
            for (int i = 0; i < numSimulations; ++i) {
                Simulation2 simulation = new Simulation2(probNet);
                list.add(executor.submit(simulation));
            }
            int simulationIndex = 0;
            try {
                for (Future<GTablePotential> result : list) {
                    result.get();
                    simulationIndex++;
                }
                success = true;
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("WARNING: PSA failed with " + numThreads + " threads.");
                e.printStackTrace();
                System.out.println(e.getMessage());
                numThreads /= 2;
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("VETemporalEvaluation ha tardado:" + (endTime-startTime));
    }


    private class Simulation2 implements Callable<GTablePotential> {

        ProbNet probNet;

        public Simulation2(ProbNet probNet) {
            super();
            this.probNet = probNet;
        }

        @Override
        public GTablePotential call() throws Exception {
            sampleNetworkPotentials(probNet);
            GTablePotential result = null;
            VETemporalEvaluation veTemporalEvaluation;
            try {
                veTemporalEvaluation = new VETemporalEvaluation(probNet, preResolutionEvidence, null);
            } catch (NotEvaluableNetworkException e) {
                e.printStackTrace();
            } catch (IncompatibleEvidenceException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    private class Simulation implements Callable<GTablePotential> {

        ProbNet probNet;

        public Simulation(ProbNet probNet) {
            super();
            this.probNet = probNet;
        }

        @Override
        public GTablePotential call() throws Exception {
            sampleNetworkPotentials(probNet);
            GTablePotential result = null;
            VECEADecision veceaDecision = null;
            try {
                veceaDecision = new VECEADecision(probNet, preResolutionEvidence, decision);
                result = veceaDecision.getCEPPotential();
            } catch (NotEvaluableNetworkException e) {
                e.printStackTrace();
            } catch (IncompatibleEvidenceException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    private void sampleNetworkPotentials(ProbNet probNet) {
        for (Node node : probNet.getNodes()) {
            List<Potential> sampledPotentials = new ArrayList<>();
            for (Potential potential : node.getPotentials()) {
                sampledPotentials.add(potential.sample());
            }
            node.setPotentials(sampledPotentials);
        }

    }
}


