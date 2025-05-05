package org.openmarkov.integrationTests;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.*;

import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.io.ProbNetReader;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.modelUncertainty.AxisVariation;
import org.openmarkov.core.model.network.modelUncertainty.DeterministicAxisVariationType;
import org.openmarkov.core.model.network.modelUncertainty.SystematicSampling;
import org.openmarkov.core.model.network.modelUncertainty.UncertainParameter;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.inference.algorithm.variableElimination.tasks.VESensAnTornadoSpider;
import org.openmarkov.io.probmodel.reader.PGMXReader_0_2;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public abstract class IDNetworkTests {
	
	protected String networkName;

	// Delta parameter for Assert.Equals methods
	protected final double deltaEquals = Math.pow(10, -4);

	protected ProbNet probNet;
	protected EvidenceCase preResolutionEvidence;

	@BeforeEach public void setUp() throws Exception {
		URL res = getClass().getClassLoader().getResource(networkName);
		File f = Paths.get(res.toURI()).toFile();
		String absolutePath = f.getAbsolutePath();

		// Load the network: ID-decide-test
		ProbNetReader pgmxReader = newPGMXReader();
		ProbNetInfo probNetInfo = null;
		try {
			probNetInfo = pgmxReader.loadProbNetInfo(absolutePath);
		} catch (ParserException e) {
			e.printStackTrace();
		}
		this.probNet = probNetInfo.getProbNet();
		if (probNetInfo.getEvidence().size() != 0) {
			this.preResolutionEvidence = probNetInfo.getEvidence().get(0);
		}
	}
	
	protected ProbNetReader newPGMXReader() {
		return new PGMXReader_0_2();
	}

	@Disabled
	@Test public void veSensAnTornadoSpiderTests() {
		List<UncertainParameter> uncertainParameterList = SystematicSampling.getUncertainParameters(this.probNet);
		AxisVariation axisVariation = new AxisVariation();
		axisVariation.setVariationType(DeterministicAxisVariationType.POPP);
		axisVariation.setVariationValue(0.8);

		try {
			VESensAnTornadoSpider veSensAnTornadoSpider = new VESensAnTornadoSpider(probNet, preResolutionEvidence,
					uncertainParameterList, axisVariation, 50);
			HashMap<UncertainParameter, TablePotential> uncertainParameterTablePotentialHashMap = veSensAnTornadoSpider
					.getUncertainParametersPotentials();

		} catch (NotEvaluableNetworkException | IncompatibleEvidenceException e) {
			e.printStackTrace();
		}
	}

}
