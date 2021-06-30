package org.openmarkov.integrationTests;

import org.junit.Before;

/**
 * This class implements some basic tests for ID-decide-test, which is an influence diagram where all the potentials are represented as tables.
 *
 */
public class IDDecideTestTablePotentialNetworkTests extends idDecideTestNetworkTests {
	
	@Override
	@Before public void setUp() throws Exception {
		networkName = "networks/id/ID-decide-test.pgmx";
		super.setUp();
	}

}
