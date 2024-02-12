package org.openmarkov.integrationTests;

import org.junit.jupiter.api.BeforeAll;


/**
 * This class implements some basic tests for ID-decide-test-tree-add, which is an influence diagram equivalent to ID-decide-test, but that,
 * instead of representing the potentials with tables, it uses Tree ADDs. All the results of inference should be the same. *
 */
public class IDDecideTestTreeADDNetworkTests extends idDecideTestNetworkTests {
	
	@Override
	@BeforeAll public void setUp() throws Exception {
		networkName = "networks/id/ID-decide-test-tree-add.pgmx";
		super.setUp();
	}

}
