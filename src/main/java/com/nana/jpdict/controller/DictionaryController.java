package com.nana.jpdict.controller;

import com.nana.jpdict.exceptions.*;
import com.nana.jpdict.model.QueryResult;
import com.nana.jpdict.service.DictionaryService;
import com.nana.jpdict.gui.*;

import com.fasterxml.jackson.core.JsonProcessingException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class DictionaryController {
    private final DictionaryService service;

    private final JLabel mainPanel;
    private final SearchPane searchPane;
    private final ActionPane actionPane;
    private final ResultsPane resultsPane;

    public DictionaryController(DictionaryUI ui) {
        this.service = new DictionaryService();

        this.mainPanel = ui.getMainPanel();
        this.searchPane = ui.getSearchPane();
        this.actionPane = ui.getActionPane();
        this.resultsPane = ui.getResultsPane();

        // Search the dictionary
        Action searchAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create search popup dialog
                JDialog popup = new JDialog(ui, "Status", true);
                JPanel panel = new JPanel();
                JLabel label = new JLabel("Searching...");
                panel.add(label);
                popup.add(panel);
                popup.setSize(200, 100);
                popup.setLocationRelativeTo(ui);

                // Extract search field text then reset
                String queryString = searchPane.getQueryString().trim();
                searchPane.reset();

                if (queryString.isEmpty()) {
                    JOptionPane.showMessageDialog(mainPanel, "Empty user input. Search field cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Get current tab titles then filter the queryString with only new keywords
                List<String> queryStrings = Stream.of(queryString.split(",", -1)).map(String::trim).toList();
                // Create a list of keyword strings to query. Remove already searched words from the list
                List<String> currentTabTitles = resultsPane.getAllTabTitles();
                List<String> newKeywordsToQuery = queryStrings
                        .stream()
                        .filter(keyword -> !currentTabTitles.contains(keyword))
                        .toList();

                // Create a popup if there are already searched words
                List<String> alreadySearchedWords = queryStrings
                        .stream()
                        .filter(keyword -> !newKeywordsToQuery.contains(keyword))
                        .map(keyword -> String.format("* %s", keyword))
                        .toList();
                if (!alreadySearchedWords.isEmpty()) {
                    String message = String.format("Word/s already queried. Skipping: \n%s", String.join("\n", alreadySearchedWords));
                    JOptionPane.showMessageDialog(mainPanel, message, "Info", JOptionPane.INFORMATION_MESSAGE);
                }

                // Display searching notification if there are new keywords to query
                if (newKeywordsToQuery.isEmpty()) {
                    JOptionPane.showMessageDialog(mainPanel, "No new words to query.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // TODO: Learn about this concurrency
                CompletableFuture<List<QueryResult>> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        return service.queryMultiple(newKeywordsToQuery);
                    } catch (JsonProcessingException | ApiFetchException | WordExtractionException | InputException ex) {
                        // Close the searching status popup
                        popup.dispose();

                        JOptionPane.showMessageDialog(mainPanel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        return null;
                    }
                });

                future.thenAccept(data -> {
                    // Close the searching status popup
                    popup.dispose();

                    // Handle response
                    if (data == null) {
                        return;
                    }

                    if (data.isEmpty()) {
                        JOptionPane.showMessageDialog(mainPanel, "No results found", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        if (newKeywordsToQuery.size() != data.size()) {
                            List<String> keywordsWithResults = data
                                    .stream()
                                    .map(QueryResult::getQueryString)
                                    .toList();
                            List<String> keywordsWithNoResults = newKeywordsToQuery
                                    .stream()
                                    .filter(keyword -> !keywordsWithResults.contains(keyword))
                                    .map(keyword -> String.format("* %s", keyword))
                                    .toList();

                            String errorMessage = String.format("No results found for the following: \n%s", String.join("\n", keywordsWithNoResults));
                            JOptionPane.showMessageDialog(mainPanel, errorMessage, "Warning", JOptionPane.WARNING_MESSAGE);
                        }
                        for (QueryResult result : data) {
                            resultsPane.createTab(result);
                        }
                        // Set active to last query
                        resultsPane.setSelectedIndex(resultsPane.getTabCount() - 1);
                    }
                }).exceptionally(ex -> {
                    // What should we do with this data?
                    ex.printStackTrace();
                    return null;
                });

                // Show the searching popup
                popup.setVisible(true);
            }
        };
        this.searchPane.searchQuery(searchAction);
        this.actionPane.searchQuery(searchAction);

        // Copy to clipboard
        this.actionPane.copySelectedResultsToClipboard(_ -> {
            this.resultsPane.copyResultsFromActiveTabToClipboard();

            final int COPY_BUTTON_IDX = 1;
            JButton copyButton = (JButton) this.actionPane.getComponent(COPY_BUTTON_IDX);
            JToolTip toolTip = copyButton.createToolTip();
            toolTip.setTipText("Selected data copied to clipboard");

            final int OFFSET = 10;
            PopupFactory popupFactory = PopupFactory.getSharedInstance();
            Point locationOnScreen = copyButton.getLocationOnScreen();
            Popup popup = popupFactory.getPopup(copyButton, toolTip, locationOnScreen.x + (copyButton.getWidth() / 2), locationOnScreen.y + copyButton.getHeight() - OFFSET);

            popup.show(); // display the tooltip

            // Hide the tooltip after a short delay
            Timer timer = new Timer(2000, _ -> popup.hide());
            timer.setRepeats(false);
            timer.start();
        });

        // Close the active tab
        this.actionPane.closeActiveTab(_ -> {
            int indexToClose = this.resultsPane.getSelectedIndex();
            this.resultsPane.removeTabAt(indexToClose);

            if (this.resultsPane.getTabCount() == 0) {
                this.resultsPane.createDefaultTab();
            }
        });
    }
}
