package com.accenture.jpdict.gui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;

public class ActionPane extends JPanel {
    private JButton searchButton;
    private JButton copyButton;
    private JButton extractButton;
    private JButton closeTabButton;

    public ActionPane() {
        this.searchButton = new JButton("Search");
        this.copyButton = new JButton("Copy");
        this.extractButton = new JButton("Extract to file");
        this.closeTabButton = new JButton("Close tab");

        this.copyButton.setPreferredSize(new Dimension(100, 120)); // copy button has custom size

        // init pane border
        Border padding = BorderFactory.createEmptyBorder(7, 0, 0, 0);
        this.setBorder(padding);

        setLayout(new GridBagLayout());;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);

        // Set each button's position then add to action pane
        // search button
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.NORTH;
        add(this.searchButton, gbc);

        // copy button
        gbc.gridy++;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.SOUTH;
        add(this.copyButton, gbc);

        // extract button
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.SOUTH;
        add(this.extractButton, gbc);

        // close button
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.SOUTH;
        add(this.closeTabButton, gbc);
    }

    // Event handling
    public void searchQuery(ActionListener actionListener) {
        this.searchButton.addActionListener(actionListener);
    }

    public void copySelectedResultsToClipboard(ActionListener actionListener) {
        this.copyButton.addActionListener(actionListener);
    }

    public void extractResultsToFile(ActionListener actionListener) {
        this.extractButton.addActionListener(actionListener);
    }

    public void closeActiveTab(ActionListener actionListener) {
        this.closeTabButton.addActionListener(actionListener);
    }
}
