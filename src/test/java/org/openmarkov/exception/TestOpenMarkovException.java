package org.openmarkov.exception;

import org.junit.Test;
import org.openmarkov.core.exception.OpenMarkovException;
import org.openmarkov.gui.localize.LocalizedException;

import static junit.framework.Assert.assertNotNull;

public class TestOpenMarkovException {

    @Test
    public void testMessages() {
        OpenMarkovException openMarkovException = new OpenMarkovException("ConfigurationException", "Test");
        assertNotNull(openMarkovException);
        LocalizedException configurationException = new LocalizedException(openMarkovException, null);
        assertNotNull(configurationException);
    }
}
