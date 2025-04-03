package org.openmarkov;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openmarkov.gui.localize.GUIResourceBundleProvider;

import java.io.IOException;

public class testLocalizeBundleProvider {
    
    @Test
    public void testLocalizeGUI() throws IOException {
        var read = new GUIResourceBundleProvider().getResourceAsStream("/gui/localize/Buttons_en.xml");
        var contents = new String(read.readAllBytes());
        Assertions.assertFalse(contents.isEmpty());
        System.out.println("The file /gui/localize/Buttons_en.xml of org.openmarkov.gui exists and has contents");
    }
    
}
