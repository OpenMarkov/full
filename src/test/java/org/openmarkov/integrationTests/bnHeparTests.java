/*
* Copyright 2015 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/
package org.openmarkov.integrationTests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmarkov.core.exception.*;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.inference.variableElimination.tasks.VEPropagation;
import org.openmarkov.io.probmodel.reader.PGMXReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class bnHeparTests {
    private final String networkName = "networks/bn/BN-hepar.pgmx";

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
    public void vePropagationWithoutEvidence(){
        VEPropagation vePropagation;
        EvidenceCase postResolutionEvidence = new EvidenceCase();
        List<Variable> variablesOfInterest = new ArrayList<>();
        try {
            variablesOfInterest.add(probNet.getVariable("alt"));
            variablesOfInterest.add(probNet.getVariable("ascites"));
            variablesOfInterest.add(probNet.getVariable("carcinoma"));
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
        }

        try {
            vePropagation = new VEPropagation(probNet, variablesOfInterest, preResolutionEvidence, postResolutionEvidence, null);
            HashMap<Variable, TablePotential> posteriorVales = vePropagation.getPosteriorValues();

            for(Variable variable : variablesOfInterest) {
                double [] expectedValues = new double[0];
                switch (variable.getName()) {
                    case "alt":
                        expectedValues = new double[]{0.3657, 0.4180, 0.1690, 0.0473};
                        break;
                    case "ascites":
                        expectedValues = new double[]{0.8791, 0.1209};
                        break;
                    case "carcinoma":
                        expectedValues = new double[]{0.8718, 0.1282};
                        break;
                }
                Assert.assertArrayEquals(posteriorVales.get(variable).values, expectedValues,  deltaEquals);
            }
        } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void vePropagationWithPostResolutionEvidence2() {
        VEPropagation vePropagation;
        EvidenceCase postResolutionEvidence = new EvidenceCase();
        List<Variable> variablesOfInterest = new ArrayList<>();
        try {
            variablesOfInterest.add(probNet.getVariable("cholesterol"));
            variablesOfInterest.add(probNet.getVariable("PBC"));
            variablesOfInterest.add(probNet.getVariable("hepatomegaly"));
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
        }


        try {
            // New Finding: sex = female
            Finding finding1 = new Finding(probNet.getVariable("sex"), 1);
            postResolutionEvidence.addFinding(finding1);

            // New Finding: hepatotoxic = present
            Finding finding2 = new Finding(probNet.getVariable("hepatotoxic"), 1);
            postResolutionEvidence.addFinding(finding2);

            // New Finding: ChHepatitis = active
            Finding finding3 = new Finding(probNet.getVariable("ChHepatitis"), 2);
            postResolutionEvidence.addFinding(finding3);
        } catch (NodeNotFoundException | IncompatibleEvidenceException | InvalidStateException e) {
            e.printStackTrace();
        }

        try {
            vePropagation = new VEPropagation(probNet, variablesOfInterest, preResolutionEvidence, postResolutionEvidence, null);
            HashMap<Variable, TablePotential> posteriorVales = vePropagation.getPosteriorValues();

            for(Variable variable : variablesOfInterest) {
                double [] expectedValues = new double[0];
                switch (variable.getName()) {
                    case "cholesterol":
                        expectedValues = new double[]{0.7315, 0.1980, 0.0705};
                        break;
                    case "PBC":
                        expectedValues = new double[]{0.4841, 0.5159};
                        break;
                    case "hepatomegaly":
                        expectedValues = new double[]{0.2051, 0.7949};
                        break;
                }
                Assert.assertArrayEquals(posteriorVales.get(variable).values, expectedValues,  deltaEquals);
            }
        } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
            e.printStackTrace();
        }
    }
}
