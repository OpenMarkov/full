package org.openmarkov.full;

import org.jetbrains.annotations.NotNull;
import org.openmarkov.gui.componentBuilder.JMenuItemBuilder;
import org.openmarkov.gui.configuration.LocalPreferences;
import org.openmarkov.gui.toolplugin.ToolPlugin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.util.Collections;

/**
 * Developer tool plugin that logs the class name and name of any Swing component
 * hovered by the mouse. Can be toggled on/off via the Tools menu.
 */
public class HoverLoggerPlugin implements ToolPlugin {
    
    @Override public @NotNull ToolPluginGroup pluginGroup() {
        return ToolPluginGroup.UNCATEGORIZED;
    }
    
    @Override public int priorityInGroup() {
        return 0;
    }
    
    @Override public JMenuItem toMenuItem() {
        return new JMenuItemBuilder("Hover GUI logger (Developers' tool)")
                .asCheckbox()
                .selected(LocalPreferences.HOVER_LOGGER_ENABLED.get())
                .onItemEvent(e -> LocalPreferences.HOVER_LOGGER_ENABLED.set(e.getStateChange() == ItemEvent.SELECTED))
                .build();
    }
    
    static {
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (!LocalPreferences.HOVER_LOGGER_ENABLED.get()) {
                return;
            }
            if (!(event instanceof MouseEvent mouseEvent)) {
                return;
            }
            if (mouseEvent.getID() != MouseEvent.MOUSE_MOVED) {
                return;
            }
            if (!(mouseEvent.getSource() instanceof Component hoveredComponent)) {
                return;
            }
            var componentTree = org.openmarkov.java.swing.ComponentUtilities.parents(hoveredComponent);
            componentTree.add(0, hoveredComponent);
            Collections.reverse(componentTree);
            for (int i = 0; i < componentTree.size(); i++) {
                Component component = componentTree.get(i);
                String name = component.getName() == null ? "" : " with name " + component.getName();
                System.out.println("\t".repeat(i) + "- " + component.getClass().getName() + name);
            }
        }, AWTEvent.MOUSE_MOTION_EVENT_MASK);
        
    }
}
