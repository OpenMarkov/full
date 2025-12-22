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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class EditsHistoryPlugin implements ToolPlugin {
    
    @Override public @NotNull String menuOptionText() {
        return "Edits history";
    }
    
    @Override public @NotNull ToolPluginGroup pluginGroup() {
        return ToolPluginGroup.UNCATEGORIZED;
    }
    
    @Override public int priorityInGroup() {
        return 0;
    }
    
    @Override public void showDialog(@Nullable JFrame parent) {
        JDialog editsDialog = new JDialog(parent);
        editsDialog.setVisible(true);
        editsDialog.setModalityType(Dialog.ModalityType.MODELESS);
        editsDialog.setTitle("Visualization of edits");
        
        
        JPanel editsPanel = new JPanel();
        editsPanel.setLayout(new BoxLayout(editsPanel, BoxLayout.Y_AXIS));
        var visualPanel = new JPanel();
        visualPanel.setLayout(new BorderLayout());
        visualPanel.add(new JScrollPane(editsPanel), BorderLayout.CENTER);
        editsDialog.setContentPane(visualPanel);
        
        
        final ArrayList<EditAndDone> edits = new ArrayList<>();
        new Thread(() -> {
            while (true) {
                
                ProbNet currentProbNet = MainPanel.getCurrentProbNet();
                try {
                    List<EditAndDone> newEdits;
                    try {
                        List<PNEdit> undoneEdits = currentProbNet.getPNESupport().undoManager.getUndoneEdits();
                        List<PNEdit> doneEdits = currentProbNet.getPNESupport().undoManager.getDoneEdits();
                        doneEdits = doneEdits.reversed();
                        newEdits = Stream.concat(
                                doneEdits.stream().map(edit -> new EditAndDone(edit, true)),
                                undoneEdits.stream().map(edit -> new EditAndDone(edit, false))
                        ).toList();
                    } catch (RuntimeException e) {
                        newEdits = Collections.emptyList();
                    }
                    if (!newEdits.equals(edits)) {
                        edits.clear();
                        edits.addAll(newEdits);
                        editsPanel.removeAll();
                        edits.stream().map(editAndDone -> {
                            JButton jButton = new JButton(editAndDone.edit.localize());
                            if (editAndDone.done) {
                                jButton.setIcon(IconBind.UNDO_ENABLED.icon());
                                jButton.addActionListener(e -> {
                                    while (!currentProbNet.getPNESupport().undo().contains(editAndDone.edit)) {
                                    
                                    }
                                });
                            } else {
                                jButton.setIcon(IconBind.REDO_ENABLED.icon());
                                jButton.addActionListener(e -> {
                                    while (!currentProbNet.getPNESupport().redo().contains(editAndDone.edit)) {
                                    
                                    }
                                });
                            }
                            
                            
                            return jButton;
                        }).forEach(editsPanel::add);
                        editsPanel.revalidate();
                        editsPanel.repaint();
                        editsDialog.pack();
                    }
                } catch (RuntimeException e) {
                    System.err.println(e);
                }
            }
        }).start();
        
    }
    
    
    record EditAndDone(PNEdit edit, boolean done) {
    }
    
}
