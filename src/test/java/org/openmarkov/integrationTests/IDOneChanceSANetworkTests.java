package org.openmarkov.integrationTests;

import org.junit.Before;
import org.openmarkov.core.io.ProbNetReader;
import org.openmarkov.io.probmodel.reader.PGMXReader_0_5;

public class IDOneChanceSANetworkTests extends IDNetworkTests {
	
	@Override
	@Before public void setUp() throws Exception {
		networkName = "networks/id/ID-one-chance-sa.pgmx";
		super.setUp();
	}

	@Override
	protected ProbNetReader newPGMXReader() {
		// TODO Auto-generated method stub
		return new PGMXReader_0_5();
	}

}
