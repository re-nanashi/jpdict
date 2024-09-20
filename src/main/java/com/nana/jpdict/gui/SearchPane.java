package com.nana.jpdict.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.border.Border;

public class SearchPane extends JPanel {
    private JTextField searchField;

    public SearchPane() {
        this.searchField = new JTextField();
        this.searchField.setMinimumSize(new Dimension(600, 30));
        this.searchField.setPreferredSize(new Dimension(600, 30));

        // init search pane border
        Border padding = BorderFactory.createEmptyBorder(10, 0, 10, 0);
        this.setBorder(padding);
        // set layout
        this.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel("Search keyword/s:    "), gbc);

        gbc.gridx++;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(this.searchField, gbc);
    }

    public void searchQuery(ActionListener actionListener) {
        this.searchField.addActionListener(actionListener);
    }

    public String getQueryString() {
        return this.searchField.getText();
    }

    public void reset() {
        this.searchField.setText("");
    }
}