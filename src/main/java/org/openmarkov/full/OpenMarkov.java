/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.full;

import com.formdev.flatlaf.IntelliJTheme;
import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.exception.UnrecoverableException;
import org.openmarkov.core.io.format.annotation.NoReaderForFileException;
import org.openmarkov.core.localize.StringDatabase;
import org.openmarkov.gui.configuration.*;
import org.openmarkov.gui.dialog.OMExceptionHandler;
import org.openmarkov.gui.exception.CorruptNetworkFile;
import org.openmarkov.gui.window.MainGUI;
import org.xml.sax.SAXException;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class stores a set of additionalProperties and the {@code main}
 * method.
 * <p>
 * If there is some other main method in other class is only for test.
 * <p>
 *
 * @author manuel
 * @author fjdiez
 * @author jmendoza
 * @version 1.1 jlgozalo - Suppress public modifier in the configuration
 * attributes (not required); add explicit initial value and fix bug in
 * the getUniqueInstance with the mainGui starting inside the singleton
 * (not outside) to prevent double GUI initialization
 * @since OpenMarkov 1.0
 */
public class OpenMarkov {
    // Attributes
    /**
     * Stores variables such as initialPath, netsDirectory ...
     */
    ComponentConfiguration openMarkovKernelConfiguration = null;
    /**
     * Stores the configuration of each component.
     */
    OpenMarkovConfiguration openMarkovConfiguration = null;
    
    /**
     * OpenMarkov main class
     *
     * @param args Arguments
     */
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new OMExceptionHandler());
        List<String> filesToOpen = new ArrayList<String>();
        boolean languageWasSet = false;
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-l") || args[i].equals("-language")) {
                if (i + 1 < args.length) {
                    StringDatabase.getUniqueInstance().setLanguage(args[i + 1]);
                    ++i;
                    languageWasSet = true;
                }
            } else if (new File(args[i]).exists()) {
                filesToOpen.add(args[i]);
            }
        }
        if (!languageWasSet) {
            StringDatabase.getUniqueInstance().setLanguage(LocalPreferences.PREFERENCE_LANGUAGE.get());
        }
        MainGUI.INSTANCE.setVisible(true);
        for (String filename : filesToOpen) {
            try {
                MainGUI.INSTANCE.openNetwork(filename);
            } catch (ParserException | IOException | SAXException | NoReaderForFileException | CorruptNetworkFile e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
        
        if (LocalPreferences.PREFERS_DARK_THEME.get()) {
            MainGUI.INSTANCE.setVisible(false);
            SwingUtilities.invokeLater(() -> {
                try {
                    org.openmarkov.gui.toolplugin.DarkModePlugin.updateInterfaceToLook(
                            org.openmarkov.gui.window.MainPanel.getUniqueInstance().getMainFrame());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                         UnsupportedLookAndFeelException e) {
                    throw new UnrecoverableException(e);
                } finally {
                    MainGUI.INSTANCE.setVisible(true);
                }
            });
        }
        
        
        var developmentTheme = new File("development.theme.json").getAbsoluteFile();
        System.out.println("Reading changes at " + developmentTheme);
        long lastModified;
        while (true) {
            lastModified = developmentTheme.lastModified();
            System.out.println("Loading development theme from " + developmentTheme);
            try {
                SwingUtilities.invokeAndWait(() -> {
                    try {
                        UIManager.setLookAndFeel(new IntelliJTheme.ThemeLaf(new IntelliJTheme(new FileInputStream(developmentTheme))));
                        SwingUtilities.updateComponentTreeUI(MainGUI.INSTANCE);
                    } catch (UnsupportedLookAndFeelException | IOException e) {
                        System.out.println(e);
                    }
                });
            } catch (InterruptedException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            while (lastModified == developmentTheme.lastModified()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}