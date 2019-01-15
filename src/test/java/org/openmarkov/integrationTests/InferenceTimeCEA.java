package org.openmarkov.integrationTests;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.inference.tasks.CEAnalysis;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.model.network.CEP;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.CEADecompositionIntoSymmetricDANsEvaluation;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DANDecisionTreeEvaluation;
import org.openmarkov.inference.variableElimination.tasks.VECEAnalysis;
import org.openmarkov.io.probmodel.reader.PGMXReader_0_2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class InferenceTimeCEA {

	private List<String> networkNames = new ArrayList<>();

	private final String path = "networks/dan/";

    // Delta parameter for Assert.Equals methods
    private final double deltaEquals = Math.pow(10, -4);

	private List<AnalysisResult> results = new ArrayList<>();

	/**
	 * Class to store the analysis made for each network
	 */
	private class AnalysisResult {
		private long dsdEvaluationTime;
		private long dtEvaluationTime;
		private String networkName;

		public AnalysisResult() {
			this("", 0, 0);
		}

		/**
		 * Class to store the analysis made for each network
		 *
		 * @param networkName       Network name
		 * @param dsdEvaluationTime Time spent in the evaluation of DSD algorithm
		 * @param dtEvaluationTime  Time spent in the evaluation of DT allgorithm
		 */
		public AnalysisResult(String networkName, long dsdEvaluationTime, long dtEvaluationTime) {
			this.networkName = networkName;
			this.dsdEvaluationTime = dsdEvaluationTime;
			this.dtEvaluationTime = dtEvaluationTime;
		}

		public long getDsdEvaluationTime() {
			return dsdEvaluationTime;
		}

		public void setDsdEvaluationTime(long dsdEvaluationTime) {
			this.dsdEvaluationTime = dsdEvaluationTime;
		}

		public long getDtEvaluationTime() {
			return dtEvaluationTime;
		}

		public void setDtEvaluationTime(long dtEvaluationTime) {
			this.dtEvaluationTime = dtEvaluationTime;
		}

		public String getNetworkName() {
			return networkName;
		}

		public void setNetworkName(String networkName) {
			this.networkName = networkName;
		}
	}

	@Before public void setUp() {
		Configurator.setRootLevel(Level.DEBUG);
		// New cost-effectiveness networks
		networkNames.add("DAN-CE-2-test-problem.pgmx");
		networkNames.add("DAN-CE-3-test-problem.pgmx");
		networkNames.add("DAN-CE-4-test-problem.pgmx");
		networkNames.add("DAN-CE-5-test-problem.pgmx");
		//        networkNames.add("DAN-CE-6-test-problem.pgmx");
		//        networkNames.add("DAN-CE-7-test-problem.pgmx");
		//        networkNames.add("DAN-CE-8-test-problem.pgmx");
		//
		//        // Old unicriterion n-test DANs
		networkNames.add("DAN-3-test-problem.pgmx");
		networkNames.add("DAN-4-test-problem.pgmx");
		//        networkNames.add("DAN-5-test-problem.pgmx");
		//        networkNames.add("DAN-6-test-problem.pgmx");
		//        networkNames.add("DAN-7-test-problem.pgmx");
	}

	@Test public void dansTEST() {
		for (String networkName : networkNames) {
			InputStream file = getClass().getClassLoader().getResourceAsStream(path + networkName);
			PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
			ProbNetInfo probNetInfo = null;
			try {
				probNetInfo = pgmxReader.loadProbNetInfo(networkName, file);
				ProbNet probNet = probNetInfo.getProbNet();
				EvidenceCase evidenceCase = probNetInfo.getEvidence().isEmpty() ?
						new EvidenceCase() :
						probNetInfo.getEvidence().get(0);
				LogManager.getLogger().debug("Evaluating DSD algorithm for " + networkName);
				long dsdEvaluationTime = calculateCEDSDEvaluationTime(probNet, evidenceCase);
				LogManager.getLogger().debug("Evaluating DT algorithm for " + networkName);
				long dtEvaluationTime = calculateDTEvaluationTime(probNet, evidenceCase);

				AnalysisResult result = new AnalysisResult(networkName, dsdEvaluationTime, dtEvaluationTime);
				results.add(result);
			} catch (ParserException e) {
				e.printStackTrace();
			}
		}

		saveResultsToXSLX();
	}

	public void saveResultsToXSLX() {
		// Abstract output file
		File resultFile = new File("results.xlsx");
		LogManager.getLogger().debug("Output file: " + resultFile.getAbsolutePath());

		// OOXML Excel workbook
		Workbook workbook = new XSSFWorkbook();

		// Excel sheet
		Sheet sheet = workbook.createSheet("Execution time");

		// Heading row
		int rowNumber = 0;
		Row row = sheet.createRow(rowNumber);
		row.createCell(0).setCellValue("Network name");
		row.createCell(1).setCellValue("DSD Time (ns)");
		row.createCell(2).setCellValue("DT Time (ns)");

		rowNumber++;
		for (AnalysisResult result : results) {
			row = sheet.createRow(rowNumber);
			row.createCell(0).setCellValue(result.getNetworkName());
			row.createCell(1).setCellValue(result.getDsdEvaluationTime());
			row.createCell(2).setCellValue(result.getDtEvaluationTime());
			rowNumber++;
		}

		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(resultFile);
			workbook.write(outputStream);
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//    @Test public void measureInferenceTime() {
	//        long startTime, endTime;
	//        FileWriter fileWriter;
	//        PrintWriter printWriter = null;
	//        Writer writer = null;
	//        EvidenceCase preResolutionEvidence = new EvidenceCase();
	//        try {
	//            writer = new BufferedWriter(new OutputStreamWriter(
	//                        new FileOutputStream("/home/manuel/idea-workspace/org.openmarkov.full/src/test/resources/networks/IDCEAnTherapies/results.txt"), "utf-8"));
	//            writer.write("Inference times\n");
	//            writer.write("---------------\n");
	//
	//            startTime = System.nanoTime();
	//            CEAnalysis decompositionAlgorithmArticleCEA = null;
	//            try {
	//                decompositionAlgorithmArticleCEA = new CEADecompositionIntoSymmetricDANsEvaluation(
	//                        probNets[0], null, preResolutionEvidence);
	//            } catch (NotEvaluableNetworkException e) {
	//                e.printStackTrace();
	//            }
	//            CEP cep = (CEP) decompositionAlgorithmArticleCEA.getUtility().elementTable.get(0);
	//            endTime = System.nanoTime();
	//
	//            for (int i = 0; i < probNets.length; i++) {
	//                VECEAnalysis veEvaluation;
	//                try {
	//                    startTime = System.nanoTime();
	//                    decompositionAlgorithmArticleCEA = new CEADecompositionIntoSymmetricDANsEvaluation(
	//                            probNets[i], null, preResolutionEvidence);
	//                    cep = (CEP) decompositionAlgorithmArticleCEA.getUtility().elementTable.get(0);
	//                    endTime = System.nanoTime();
	//                    int numIterations = 1;
	//                    if (endTime - startTime < 100000000L) {
	//                        numIterations = 100;
	//                        startTime = System.nanoTime();
	//                        for (int j = 0; j < numIterations; j++) {
	//                            decompositionAlgorithmArticleCEA = new CEADecompositionIntoSymmetricDANsEvaluation(
	//                                    probNets[i], null, preResolutionEvidence);
	//                            cep = (CEP) decompositionAlgorithmArticleCEA.getUtility().elementTable.get(0);
	//                        }
	//                        endTime = System.nanoTime();
	//                    }
	//                    long totalTime = (endTime - startTime) / numIterations;
	//                    long timeInMiliSeconds = totalTime / 1000000L;
	//                    String infoLine = networkNames[i] + ": " + timeInMiliSeconds + " milisegundos\n";
	//                    System.out.print(infoLine);
	//                    writer.write(infoLine);
	//                } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
	//                    System.err.println("Something went wrong with inference test.");
	//                }
	//            }
	//        } catch (IOException ex) {
	//            System.err.println("Something went wrong with Input/Output.");
	//            System.err.println(ex.getMessage());
	//        } catch (UnexpectedInferenceException e) {
	//            e.printStackTrace();
	//        } catch (NotEvaluableNetworkException e) {
	//            e.printStackTrace();
	//        } catch (IncompatibleEvidenceException e) {
	//            e.printStackTrace();
	//        } finally {
	//            try {writer.close();} catch (Exception ex) {/*ignore*/}
	//        }
	//        assertTrue(true);
	//    }

	/**
	 * Time spent in the evaluation of a <ProbNet>probNet</ProbNet> with an <EvidenceCase>evidenceCase</EvidenceCase> using the
	 * <CEADecompositionIntoSymmetricDANsEvaluation>CEADecompositionIntoSymmetricDANsEvaluation</CEADecompositionIntoSymmetricDANsEvaluation>
	 * algorithm.
	 *
	 * @param probNet
	 * @param evidenceCase
	 * @return time in nanoseconds
	 */
	private long calculateCEDSDEvaluationTime(ProbNet probNet, EvidenceCase evidenceCase) {
		long startTime, endTime;
		startTime = System.nanoTime();
		CEADecompositionIntoSymmetricDANsEvaluation decompositionAlgorithmArticleCEA = null;
		try {
			decompositionAlgorithmArticleCEA = new CEADecompositionIntoSymmetricDANsEvaluation(probNet, evidenceCase);
            CEP cep = (CEP) decompositionAlgorithmArticleCEA.getUtility().elementTable.get(0);
		} catch (UnexpectedInferenceException | NotEvaluableNetworkException | IncompatibleEvidenceException e) {
			e.printStackTrace();
		}
		endTime = System.nanoTime();
		return endTime - startTime;
	}

	/**
	 * Time spent in the evaluation of a <ProbNet>probNet</ProbNet> with an <EvidenceCase>evidenceCase</EvidenceCase> using the
	 * <DANDecisionTreeEvaluation>DANDecisionTreeEvaluation</DANDecisionTreeEvaluation> algorithm.
	 *
	 * @param probNet
	 * @param evidenceCase
	 * @return time in nanoseconds
	 */
	private long calculateDTEvaluationTime(ProbNet probNet, EvidenceCase evidenceCase) {
		long startTime, endTime;
		startTime = System.nanoTime();
		DANDecisionTreeEvaluation decompositionAlgorithmArticleCEA = null;
		try {
			decompositionAlgorithmArticleCEA = new DANDecisionTreeEvaluation(probNet, evidenceCase);
			TablePotential cep = decompositionAlgorithmArticleCEA.getUtility();
        } catch (NotEvaluableNetworkException e) {
			e.printStackTrace();
		}
		endTime = System.nanoTime();
		return endTime - startTime;
    }
}
