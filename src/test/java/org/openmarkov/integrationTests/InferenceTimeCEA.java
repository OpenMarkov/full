package org.openmarkov.integrationTests;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Table;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmarkov.core.exception.*;
import org.openmarkov.core.inference.tasks.CEAnalysis;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.model.network.CEP;
import org.openmarkov.core.model.network.Criterion;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.CEADANDecisionTreeEvaluation;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.CEADecompositionIntoSymmetricDANsEvaluation;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DANDecisionTreeEvaluation;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DecompositionIntoSymmetricDANsEvaluation;
import org.openmarkov.inference.variableElimination.operation.CEPotentialOperation;
import org.openmarkov.inference.variableElimination.tasks.VECEAnalysis;
import org.openmarkov.inference.variableElimination.tasks.VEEvaluation;
import org.openmarkov.io.probmodel.reader.PGMXReader_0_2;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class InferenceTimeCEA {

	private List<String> networkNames = new ArrayList<>();

	private final String path = "networks/dan/";

	// Delta parameter for Assert.Equals methods
	private final double deltaEquals = Math.pow(10, -4);

	private List<AnalysisResult> results = new ArrayList<>();

	private final double lambda = 30000;

	@Before public void setUp() {
		Configurator.setRootLevel(Level.DEBUG);
		// New cost-effectiveness networks
		//		networkNames.add("DAN-CE-2-test-problem.pgmx");
		//		networkNames.add("DAN-CE-3-test-problem.pgmx");
		networkNames.add("DAN-CE-4-test-problem.pgmx");
		//		networkNames.add("DAN-CE-5-test-problem.pgmx");
		//		networkNames.add("DAN-CE-6-test-problem.pgmx");
		//		networkNames.add("DAN-CE-7-test-problem.pgmx");
		//		networkNames.add("DAN-CE-8-test-problem.pgmx");

		// Old unicriterion n-test DANs
		//		networkNames.add("DAN-3-test-problem.pgmx");
		//		networkNames.add("DAN-4-test-problem.pgmx");
		//		networkNames.add("DAN-5-test-problem.pgmx");
		//		networkNames.add("DAN-6-test-problem.pgmx");
		//		networkNames.add("DAN-7-test-problem.pgmx");
	}

	@Test public void dansTEST() {
		for (String networkName : networkNames) {
			InputStream file = getClass().getClassLoader().getResourceAsStream(path + networkName);
			AnalysisResult result = new AnalysisResult();
			PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
			ProbNetInfo probNetInfo = null;
			try {
				long startTime, endTime;
				probNetInfo = pgmxReader.loadProbNetInfo(networkName, file);
				ProbNet probNet = probNetInfo.getProbNet();
				EvidenceCase evidenceCase = probNetInfo.getEvidence().isEmpty() ?
						new EvidenceCase() :
						probNetInfo.getEvidence().get(0);
				CEP cepDSD = null;
				CEP cepDT = null;
				TablePotential utilityDSD;
				TablePotential utilityDT;

				result.setNetworkName(networkName);

				for (Criterion criterion : probNet.getDecisionCriteria()) {
					LogManager.getLogger()
							.debug(criterion.getCriterionName() + " scale = (x " + criterion.getUnicriteriaScale()
									+ ")");
				}

				// UNICRITERION ANALYSIS
				LogManager.getLogger().debug("DSD for " + probNet.getName());
				startTime = System.nanoTime();
				DecompositionIntoSymmetricDANsEvaluation evaluationDSD = new DecompositionIntoSymmetricDANsEvaluation(
						probNet, evidenceCase);
				utilityDSD = evaluationDSD.getUtility();
				endTime = System.nanoTime();
				result.setDsdUNIEvaluationTime(endTime - startTime);
				result.setDsdUNIResult(utilityDSD);
				evaluationDSD = null;

				LogManager.getLogger().debug("DT for " + probNet.getName());
				startTime = System.nanoTime();
				DANDecisionTreeEvaluation evaluationDT = new DANDecisionTreeEvaluation(probNet, evidenceCase);
				utilityDT = evaluationDT.getUtility();
				endTime = System.nanoTime();
				result.setDtUNIEvaluationTime(endTime - startTime);
				result.setDtUNIResult(utilityDT);
				evaluationDT = null;

				// Check that the result of both unicriterion algorithms are the same
				Assert.assertArrayEquals(utilityDSD.values, utilityDT.values, deltaEquals);

				// COST-EFFECTIVENESS ANALYSIS
				LogManager.getLogger().debug("CEA_DSD with lambda = " + lambda);
				startTime = System.nanoTime();
				CEADecompositionIntoSymmetricDANsEvaluation evaluationCEADSD = new CEADecompositionIntoSymmetricDANsEvaluation(
						probNet, evidenceCase);
				cepDSD = evaluationCEADSD.getCEP();
				endTime = System.nanoTime();
				result.setDsdCEEvaluationTime(endTime - startTime);
				result.setDsdCEResult(cepDSD);
				evaluationCEADSD = null;

				LogManager.getLogger().debug("CEA_DT with lambda = " + lambda);
				startTime = System.nanoTime();
				CEADANDecisionTreeEvaluation evaluationCEADT = new CEADANDecisionTreeEvaluation(probNet);
				cepDT = evaluationCEADT.getCEP();
				endTime = System.nanoTime();
				result.setDtCEEvaluationTime(endTime - startTime);
				result.setDtCEResult(cepDT);
				evaluationCEADT = null;

				// Check that the result of both CE algorithms are the same
				Assert.assertArrayEquals(cepDSD.getThresholds(), cepDT.getThresholds(), deltaEquals);
				Assert.assertArrayEquals(cepDSD.getCosts(), cepDT.getCosts(), deltaEquals);
				Assert.assertArrayEquals(cepDSD.getEffectivities(), cepDT.getEffectivities(), deltaEquals);

				// Check that the result obtained for CE algorithms (lambda=30,000) and Unicriterion algorithms are the same
				Assert.assertEquals(utilityDSD.values[0],
						cepDSD.getEffectiveness(lambda) * lambda - cepDSD.getCost(lambda), deltaEquals);
				results.add(result);
			} catch (ParserException | UnexpectedInferenceException | NotEvaluableNetworkException | IncompatibleEvidenceException e) {
				e.printStackTrace();
			}
		}

		saveResultsToXSLX();
	}

	//    @Test public void measureInferenceTime() {
	//        long startTime, endTime;
	//        FileWriter fileWriter;
	//        PrintWriter printWriter = null;
	//        Writer writer = null;
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

	@Test public void checkCEPThresholdsWithUnicreterionAnalysis() {
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
				// Set effectiveness scale to lambda - 1
				probNet.getDecisionCriteria().get(1).setUnicriteriaScale(lambda);
				CEP cepDSD = null;
				CEP cepDT = null;
				TablePotential utilityDSD;
				TablePotential utilityDT;

				for (Criterion criterion : probNet.getDecisionCriteria()) {
					LogManager.getLogger()
							.debug(criterion.getCriterionName() + " scale = (x " + criterion.getUnicriteriaScale()
									+ ")");
				}

				// COST-EFFECTIVENESS ANALYSIS
				LogManager.getLogger().debug("CEA_DSD for " + probNet.getName());
				CEADecompositionIntoSymmetricDANsEvaluation evaluationCEADSD = new CEADecompositionIntoSymmetricDANsEvaluation(
						probNet, evidenceCase);
				cepDSD = evaluationCEADSD.getCEP();
				evaluationCEADSD = null;

				LogManager.getLogger().debug("CEA_DT for " + probNet.getName());
				CEADANDecisionTreeEvaluation evaluationCEADT = new CEADANDecisionTreeEvaluation(probNet);
				cepDT = evaluationCEADT.getCEP();
				evaluationCEADT = null;

				// Check that the result of both CE algorithms are the same
				try {
					Assert.assertArrayEquals(cepDSD.getThresholds(), cepDT.getThresholds(), deltaEquals);
					Assert.assertArrayEquals(cepDSD.getCosts(), cepDT.getCosts(), deltaEquals);
					Assert.assertArrayEquals(cepDSD.getEffectivities(), cepDT.getEffectivities(), deltaEquals);
				} catch (AssertionError error) {
					LogManager.getLogger().error("CEPs are not equals, analyzing with unicreterion analysis");

					Arrays.asList(cepDSD.getThresholds());

					for (double lambda : cepDSD.getThresholds()) {
						// Set effectiveness scale to lambda - 1
						probNet.getDecisionCriteria().get(1).setUnicriteriaScale(lambda);

						// UNICRITERION ANALYSIS
						LogManager.getLogger().debug("DSD for " + probNet.getName());
						DecompositionIntoSymmetricDANsEvaluation evaluationDSD = new DecompositionIntoSymmetricDANsEvaluation(
								probNet, evidenceCase);
						utilityDSD = evaluationDSD.getUtility();
						evaluationDSD = null;

						LogManager.getLogger().debug("DT for " + probNet.getName());
						DANDecisionTreeEvaluation evaluationDT = new DANDecisionTreeEvaluation(probNet, evidenceCase);
						utilityDT = evaluationDT.getUtility();
						evaluationDT = null;

						// Check that the result of both unicriterion algorithms are the same
						Assert.assertArrayEquals(utilityDSD.values, utilityDT.values, deltaEquals);

						// Check that the result obtained for CE algorithms (lambda=30,000) and Unicriterion algorithms are the same
						Assert.assertEquals(utilityDSD.values[0],
								cepDSD.getEffectiveness(lambda) * lambda - cepDSD.getCost(lambda), deltaEquals);
					}

					for (double lambda : cepDT.getThresholds()) {
						// Set effectiveness scale to lambda - 1
						probNet.getDecisionCriteria().get(1).setUnicriteriaScale(lambda);

						// UNICRITERION ANALYSIS
						LogManager.getLogger().debug("DSD for " + probNet.getName());
						DecompositionIntoSymmetricDANsEvaluation evaluationDSD = new DecompositionIntoSymmetricDANsEvaluation(
								probNet, evidenceCase);
						utilityDSD = evaluationDSD.getUtility();
						evaluationDSD = null;

						LogManager.getLogger().debug("DT for " + probNet.getName());
						DANDecisionTreeEvaluation evaluationDT = new DANDecisionTreeEvaluation(probNet, evidenceCase);
						utilityDT = evaluationDT.getUtility();
						evaluationDT = null;

						// Check that the result of both unicriterion algorithms are the same
						Assert.assertArrayEquals(utilityDSD.values, utilityDT.values, deltaEquals);

						// Check that the result obtained for CE algorithms (lambda=30,000) and Unicriterion algorithms are the same
						Assert.assertEquals(utilityDSD.values[0],
								cepDSD.getEffectiveness(lambda) * lambda - cepDSD.getCost(lambda), deltaEquals);
					}
				}
			} catch (ParserException | UnexpectedInferenceException | NotEvaluableNetworkException | IncompatibleEvidenceException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Class to store the analysis made for each network
	 */
	private class AnalysisResult {
		private long dsdCEEvaluationTime;
		private long dtCEEvaluationTime;
		private long dsdUNIEvaluationTime;
		private long dtUNIEvaluationTime;
		private String networkName;

		private CEP dsdCEResult;
		private CEP dtCEResult;
		private TablePotential dsdUNIResult;
		private TablePotential dtUNIResult;

		public long getDsdCEEvaluationTime() {
			return dsdCEEvaluationTime;
		}

		public void setDsdCEEvaluationTime(long dsdCEEvaluationTime) {
			this.dsdCEEvaluationTime = dsdCEEvaluationTime;
		}

		public long getDtCEEvaluationTime() {
			return dtCEEvaluationTime;
		}

		public void setDtCEEvaluationTime(long dtCEEvaluationTime) {
			this.dtCEEvaluationTime = dtCEEvaluationTime;
		}

		public long getDsdUNIEvaluationTime() {
			return dsdUNIEvaluationTime;
		}

		public void setDsdUNIEvaluationTime(long dsdUNIEvaluationTime) {
			this.dsdUNIEvaluationTime = dsdUNIEvaluationTime;
		}

		public long getDtUNIEvaluationTime() {
			return dtUNIEvaluationTime;
		}

		public void setDtUNIEvaluationTime(long dtUNIEvaluationTime) {
			this.dtUNIEvaluationTime = dtUNIEvaluationTime;
		}

		public String getNetworkName() {
			return networkName;
		}

		public void setNetworkName(String networkName) {
			this.networkName = networkName;
		}

		public CEP getDsdCEResult() {
			return dsdCEResult;
		}

		public void setDsdCEResult(CEP dsdCEResult) {
			this.dsdCEResult = dsdCEResult;
		}

		public CEP getDtCEResult() {
			return dtCEResult;
		}

		public void setDtCEResult(CEP dtCEResult) {
			this.dtCEResult = dtCEResult;
		}

		public TablePotential getDsdUNIResult() {
			return dsdUNIResult;
		}

		public void setDsdUNIResult(TablePotential dsdUNIResult) {
			this.dsdUNIResult = dsdUNIResult;
		}

		public TablePotential getDtUNIResult() {
			return dtUNIResult;
		}

		public void setDtUNIResult(TablePotential dtUNIResult) {
			this.dtUNIResult = dtUNIResult;
		}
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
		row.createCell(1).setCellValue("DSD UNI time (ns)");
		row.createCell(2).setCellValue("DT UNI time (ns)");
		row.createCell(3).setCellValue("DSD CE time (ns)");
		row.createCell(4).setCellValue("DT CE time (ns)");

		rowNumber++;
		for (AnalysisResult result : results) {
			row = sheet.createRow(rowNumber);
			row.createCell(0).setCellValue(result.getNetworkName());
			row.createCell(1).setCellValue(result.getDsdUNIEvaluationTime());
			row.createCell(2).setCellValue(result.getDtUNIEvaluationTime());
			row.createCell(3).setCellValue(result.getDsdCEEvaluationTime());
			row.createCell(4).setCellValue(result.getDtCEEvaluationTime());
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
}
