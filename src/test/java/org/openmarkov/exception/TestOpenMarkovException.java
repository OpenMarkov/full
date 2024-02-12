package org.openmarkov.exception;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.openmarkov.core.exception.OpenMarkovException;
import org.openmarkov.gui.localize.LocalizedException;


public class TestOpenMarkovException {

    @Test
    public void testMessages() {
        OpenMarkovException openMarkovException = new OpenMarkovException("ConfigurationException", "Test");
        assertNotNull(openMarkovException);
        LocalizedException configurationException = new LocalizedException(openMarkovException, null);
        assertNotNull(configurationException);
    }
}
