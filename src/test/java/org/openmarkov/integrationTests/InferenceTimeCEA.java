package org.openmarkov.integrationTests;

import org.apache.logging.log4j.LogManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmarkov.core.exception.*;
import org.openmarkov.core.inference.tasks.CEAnalysis;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.model.network.CEP;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.CEADecompositionIntoSymmetricDANsEvaluation;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DANDecisionTreeEvaluation;
import org.openmarkov.inference.variableElimination.tasks.VECEAnalysis;
import org.openmarkov.inference.variableElimination.tasks.VEEvaluation;
import org.openmarkov.io.probmodel.reader.PGMXReader_0_2;

import java.io.*;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;

public class InferenceTimeCEA {

    private final String[] networkNames = {"networks/IDCEAnTherapies/DAN-2tests.pgmx",
            "networks/IDCEAnTherapies/DAN-3tests.pgmx",
            "networks/IDCEAnTherapies/DAN-4tests.pgmx",
            "networks/IDCEAnTherapies/DAN-5tests.pgmx",
            "networks/IDCEAnTherapies/DAN-6tests.pgmx",
            "networks/IDCEAnTherapies/DAN-7tests.pgmx",
            "networks/IDCEAnTherapies/DAN-8tests.pgmx"};

    private ProbNet[] probNets;

    // Delta parameter for Assert.Equals methods
    private final double deltaEquals = Math.pow(10, -4);

    private ProbNet probNet;
    private EvidenceCase preResolutionEvidence;

    @Before
    public void setUp() throws Exception {
        probNets = new ProbNet[networkNames.length];
        for (int i = 0; i < networkNames.length; i++) {
            System.out.println("Reading: " + networkNames[i]);
            InputStream file = getClass().getClassLoader().getResourceAsStream(networkNames[i]);
            // Load the network: ID-decide-test
            PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
            ProbNetInfo probNetInfo = null;
            try {
                probNetInfo = pgmxReader.loadProbNetInfo(networkNames[i], file);
            } catch (ParserException e) {
                e.printStackTrace();
            }
            probNets[i] = probNetInfo.getProbNet();
        }
    }

    @Test public void measureInferenceTime() {
        long startTime, endTime;
        FileWriter fileWriter;
        PrintWriter printWriter = null;
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream("/home/manuel/idea-workspace/org.openmarkov.full/src/test/resources/networks/IDCEAnTherapies/results.txt"), "utf-8"));
            writer.write("Inference times\n");
            writer.write("---------------\n");

            startTime = System.nanoTime();
            CEAnalysis decompositionAlgorithmArticleCEA = null;
            try {
                decompositionAlgorithmArticleCEA = new CEADecompositionIntoSymmetricDANsEvaluation(
                        probNets[0], null, preResolutionEvidence);
            } catch (NotEvaluableNetworkException e) {
                e.printStackTrace();
            }
            CEP cep = (CEP) decompositionAlgorithmArticleCEA.getUtility().elementTable.get(0);
            endTime = System.nanoTime();

            for (int i = 0; i < probNets.length; i++) {
                VECEAnalysis veEvaluation;
                try {
                    startTime = System.nanoTime();
                    decompositionAlgorithmArticleCEA = new CEADecompositionIntoSymmetricDANsEvaluation(
                            probNets[i], null, preResolutionEvidence);
                    cep = (CEP) decompositionAlgorithmArticleCEA.getUtility().elementTable.get(0);
                    endTime = System.nanoTime();
                    int numIterations = 1;
                    if (endTime - startTime < 100000000L) {
                        numIterations = 100;
                        startTime = System.nanoTime();
                        for (int j = 0; j < numIterations; j++) {
                            decompositionAlgorithmArticleCEA = new CEADecompositionIntoSymmetricDANsEvaluation(
                                    probNets[i], null, preResolutionEvidence);
                            cep = (CEP) decompositionAlgorithmArticleCEA.getUtility().elementTable.get(0);
                        }
                        endTime = System.nanoTime();
                    }
                    long totalTime = (endTime - startTime) / numIterations;
                    long timeInMiliSeconds = totalTime / 1000000L;
                    String infoLine = networkNames[i] + ": " + timeInMiliSeconds + " milisegundos\n";
                    System.out.print(infoLine);
                    writer.write(infoLine);
                } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
                    System.err.println("Something went wrong with inference test.");
                }
            }
        } catch (IOException ex) {
            System.err.println("Something went wrong with Input/Output.");
            System.err.println(ex.getMessage());
        } catch (UnexpectedInferenceException e) {
            e.printStackTrace();
        } catch (NotEvaluableNetworkException e) {
            e.printStackTrace();
        } catch (IncompatibleEvidenceException e) {
            e.printStackTrace();
        } finally {
            try {writer.close();} catch (Exception ex) {/*ignore*/}
        }
        assertTrue(true);
    }


    @Test public void demo() throws IncompatibleEvidenceException, UnexpectedInferenceException {
        long startTime, endTime;
        ProbNet probNet = probNets[0];

        try {
            LogManager.getLogger().debug("CEA_DSD for " + probNet.getName());
            startTime = System.nanoTime();
            CEADecompositionIntoSymmetricDANsEvaluation evaluationDSD = new CEADecompositionIntoSymmetricDANsEvaluation(probNet);
            CEP cep = evaluationDSD.getCEP();
            endTime = System.nanoTime();
            LogManager.getLogger().debug("Time = " + (endTime - startTime));

            LogManager.getLogger().debug("DT_DAN for " + probNet.getName());
            startTime = System.nanoTime();
            DANDecisionTreeEvaluation evaluationDT = new DANDecisionTreeEvaluation(probNet);
            TablePotential utility = evaluationDT.getUtility();
            endTime = System.nanoTime();
            LogManager.getLogger().debug("Time = " + (endTime - startTime));
        } catch (NotEvaluableNetworkException e) {
            e.printStackTrace();
        }

    }
}
