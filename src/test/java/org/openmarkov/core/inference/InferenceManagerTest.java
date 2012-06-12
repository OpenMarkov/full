package org.openmarkov.core.inference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openmarkov.core.inference.InferenceAlgorithm;
import org.openmarkov.core.inference.annotation.InferenceManager;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.inference.variableElimination.VariableElimination;

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
    
    @Test
    public void testGetInferenceAlgorithms() throws Exception{
        
        assertTrue( inferenceManager.getInferenceAlgorithms (probNet).contains (VariableEliminationName));
    }
    
    @Test
    public void testGetInferenceAlgorithmsByName() throws Exception{
        
        InferenceAlgorithm algorithm = inferenceManager.getInferenceAlgorithmByName (VariableEliminationName, probNet);
        assertNotNull(algorithm);
    }    
    
    @Test
    public void testGetDefaultInferenceAlgorithm() throws Exception{
        
        InferenceAlgorithm algorithm = inferenceManager.getDefaultInferenceAlgorithm (probNet);
        assertNotNull(algorithm);
        assertEquals(algorithm.getClass (), VariableElimination.class);
    }        
}
