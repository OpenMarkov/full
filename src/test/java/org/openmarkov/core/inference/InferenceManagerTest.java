package org.openmarkov.core.inference;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openmarkov.core.inference.InferenceAlgorithm;
import org.openmarkov.core.inference.annotation.InferenceManager;
import org.openmarkov.core.model.network.ProbNet;

public class InferenceManagerTest
{
    private InferenceManager inferenceManager;
    private ProbNet probNet;
    private final static String VariableEliminationName  = "VariableElimination";
    
    @Before
    public void setUp() throws Exception {
        inferenceManager = new InferenceManager ();
        probNet = new ProbNet ();
    }    
    // TODO - Check InferenceManager Class and this test (remove it or change to tasks logic?)
    //@Test
    public void testGetInferenceAlgorithms() throws Exception{
        
        assertTrue( inferenceManager.getInferenceAlgorithmNames (probNet).contains (VariableEliminationName));
    }
    
    //@Test
    public void testGetInferenceAlgorithmsByName() throws Exception{
        
        InferenceAlgorithm algorithm = inferenceManager.getInferenceAlgorithmByName (VariableEliminationName, probNet);
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
