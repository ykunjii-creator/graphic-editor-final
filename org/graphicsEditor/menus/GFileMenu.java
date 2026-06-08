package org.graphicsEditor.menus;

import org.graphicsEditor.frames.GDrawingPanel;

import javax.swing.*;
import java.io.File;

public class GFileMenu extends JMenu {
    private JMenuItem saveItem;
    private JMenuItem resetItem;

    private GDrawingPanel drawingPanel;

    public GFileMenu() {
        super("File");

        this.saveItem = new JMenuItem("Save");
        this.resetItem = new JMenuItem("Reset");

        this.add(saveItem);
        this.add(resetItem);

        this.saveItem.addActionListener(e -> {
            if (drawingPanel == null) return;

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File("drawing.png"));

            int result = fileChooser.showSaveDialog(GFileMenu.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                drawingPanel.saveImage(file);
            }
        });

        this.resetItem.addActionListener(e -> {
            if (drawingPanel == null) return;
            drawingPanel.reset();
        });
    }

    public void associateWith(GDrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
    }
}