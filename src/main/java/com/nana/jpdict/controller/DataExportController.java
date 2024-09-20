package com.nana.jpdict.controller;

import com.nana.jpdict.gui.ActionPane;
import com.nana.jpdict.gui.DictionaryUI;
import com.nana.jpdict.gui.ResultsPane;
import com.nana.jpdict.service.DataExportService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

public class DataExportController {
    private DataExportService service;

    private final JLabel mainPanel;
    private final ActionPane actionPane;
    private final ResultsPane resultsPane;

    public DataExportController(DictionaryUI ui) {
        this.service = new DataExportService();

        this.mainPanel = ui.getMainPanel();
        this.actionPane = ui.getActionPane();
        this.resultsPane = ui.getResultsPane();

        // Export data to file
        Action exportAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                List<List<String>> tabResults = resultsPane.getDataFromAllTables();

                Thread exportThread = new Thread(() -> {
                    try {
                        service.export(tabResults);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

                // Start extraction then show the extracting popup
                exportThread.start();
                try {
                    exportThread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // Create extracting popup dialog
                JDialog popup = new JDialog(ui, "Status", true);

                // Create a panel with BoxLayout
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Set vertical layout

                JLabel label = new JLabel("Extraction Complete");
                label.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the label

                // Create the OK button
                JButton okButton = new JButton("OK");
                okButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        popup.dispose(); // Close the dialog when OK is clicked
                    }
                });

                // Add components to the panel
                panel.add(label);
                panel.add(Box.createVerticalStrut(10)); // Add some space between label and button
                panel.add(okButton); // Add the OK button to the panel

                popup.add(panel);

                // Configure the dialog
                popup.setSize(200, 100);
                popup.setLocationRelativeTo(ui); // Center the dialog relative to the main UI
                popup.setVisible(true);
            }
        };
        this.actionPane.extractResultsToFile(exportAction);
    }
}
