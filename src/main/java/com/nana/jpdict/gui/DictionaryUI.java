package com.nana.jpdict.gui;

import com.nana.jpdict.controller.DataExportController;
import com.nana.jpdict.controller.DictionaryController;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class DictionaryUI extends JFrame {
    private JLabel mainPanel;

    private SearchPane searchPane;
    private ActionPane actionPane;
    private ResultsPane resultsPane;

    public DictionaryUI() {
        super("EN-JP Dictionary");
        // Load icon image from resources
        ImageIcon icon = new ImageIcon(this.getClass().getResource("/icon_titlebar.png"));
        if (icon == null) {
            System.err.println("Icon not found!");
        }
        this.setIconImage(icon.getImage());

        this.mainPanel = new JLabel();
        this.searchPane = new SearchPane();
        this.actionPane = new ActionPane();
        this.resultsPane = new ResultsPane();

        // Initialize dictionary controller
        new DictionaryController(this);
        new DataExportController(this);

        // Set main panel padding and layout
        Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        this.mainPanel.setBorder(padding);
        this.mainPanel.setLayout(new GridBagLayout()); // set layout

        // Add search and results pane to the main frame
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        this.mainPanel.add(searchPane, gbc);
        gbc.gridy++;
        this.mainPanel.add(resultsPane, gbc);

        // Add action pane
        gbc.gridx++;
        gbc.gridy = 0;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.weightx = 0;
        gbc.weighty = 1;
        this.mainPanel.add(actionPane, gbc);

        setContentPane(mainPanel); // set main panel as the content pane of the main frame
    }

    public void display() {
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(400, 250));
        this.setSize(1280, 800);
        this.setVisible(true);
    }

    // Getters
    public JLabel getMainPanel() {
        return this.mainPanel;
    }

    public SearchPane getSearchPane() {
        return this.searchPane;
    }

    public ActionPane getActionPane() {
        return this.actionPane;
    }

    public ResultsPane getResultsPane() {
        return resultsPane;
    }

}