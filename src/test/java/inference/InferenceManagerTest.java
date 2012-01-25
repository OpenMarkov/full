package inference;

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
    
    @Before
    public void setUp() throws Exception {
        inferenceManager = new InferenceManager ();
        probNet = new ProbNet ();
    }    
    
    @Test
    public void testGetInferenceAlgorithms() throws Exception{
        
        assertTrue( inferenceManager.getInferenceAlgorithms (probNet).contains ("VariableElimination for BN"));
    }
    
    @Test
    public void testGetInferenceAlgorithmsByName() throws Exception{
        
        InferenceAlgorithm algorithm = inferenceManager.getInferenceAlgorithmByName ("VariableElimination for BN", probNet);
        assertNotNull(algorithm);
    }    
}
