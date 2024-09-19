package com.accenture.jpdict.controller;

import com.accenture.jpdict.gui.ActionPane;
import com.accenture.jpdict.gui.DictionaryUI;
import com.accenture.jpdict.gui.ResultsPane;
import com.accenture.jpdict.service.DataExportService;

import javax.swing.*;
import java.awt.event.ActionEvent;
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
                // Create extracting popup dialog
                JDialog popup = new JDialog(ui, "Status", true);
                JPanel panel = new JPanel();
                JLabel label = new JLabel("Extracting...");
                panel.add(label);
                popup.add(panel);
                popup.setSize(200, 100);
                popup.setLocationRelativeTo(ui);

                // TODO: Popup done extracting
                List<List<String>> tabResults = resultsPane.getDataFromAllTables();
                try {
                    service.export(tabResults);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        };
    }
}
