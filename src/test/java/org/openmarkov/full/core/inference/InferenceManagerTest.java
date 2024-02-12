/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.full.core.inference;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.openmarkov.core.inference.InferenceAlgorithm;
import org.openmarkov.core.inference.annotation.InferenceManager;
import org.openmarkov.core.model.network.ProbNet;


public class InferenceManagerTest {
	private final static String VariableEliminationName = "VariableElimination";
	private InferenceManager inferenceManager;
	private ProbNet probNet;

	@BeforeAll public void setUp() throws Exception {
		inferenceManager = new InferenceManager();
		probNet = new ProbNet();
	}

	// TODO - Check InferenceManager Class and this test (remove it or change to tasks logic?)
	//@Test
	public void testGetInferenceAlgorithms() throws Exception {

		assertTrue(inferenceManager.getInferenceAlgorithmNames(probNet).contains(VariableEliminationName));
	}

	//@Test
	public void testGetInferenceAlgorithmsByName() throws Exception {

		InferenceAlgorithm algorithm = inferenceManager.getInferenceAlgorithmByName(VariableEliminationName, probNet);
		assertNotNull(algorithm);
	}

	//    //@Test
	//    public void testGetDefaultInferenceAlgorithm() throws Exception{
	//
	//        InferenceAlgorithm algorithm = inferenceManager.getDefaultInferenceAlgorithm (probNet);
	//        assertNotNull(algorithm);
	//        assertEquals(algorithm.getClass (), VariableElimination.class);
	//    }
}
