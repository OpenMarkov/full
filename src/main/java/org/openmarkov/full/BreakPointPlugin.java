package org.openmarkov.full;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openmarkov.core.action.base.PNEdit;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.gui.loader.element.IconBind;
import org.openmarkov.gui.toolplugin.ToolPlugin;
import org.openmarkov.gui.window.MainPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BreakPointPlugin implements ToolPlugin {
    
    @Override public @NotNull String menuOptionText() {
        return "Break-point (Developers' tool)";
    }
    
    @Override public @NotNull ToolPluginGroup pluginGroup() {
        return ToolPluginGroup.UNCATEGORIZED;
    }
    
    @Override public int priorityInGroup() {
        return 0;
    }
    
    @Override public void showDialog(@Nullable JFrame parent) {
        ProbNet currentProbNet = MainPanel.getCurrentProbNet();
        boolean a = true;
    }
    
    
}
