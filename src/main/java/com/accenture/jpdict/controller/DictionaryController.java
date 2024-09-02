package com.accenture.jpdict.controller;

import com.accenture.jpdict.exceptions.ApiFetchException;
import com.accenture.jpdict.exceptions.InputException;
import com.accenture.jpdict.exceptions.WordExtractionException;
import com.accenture.jpdict.gui.*;
import com.accenture.jpdict.model.QueryQueue;
import com.accenture.jpdict.model.QueryResult;
import com.accenture.jpdict.service.DictionaryService;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class DictionaryController {
    private final DictionaryService service;

    private final JLabel mainPanel;
    private final SearchPane searchPane;
    private final ActionPane actionPane;
    private final ResultsPane resultsPane;

    // TODO: The model should be only be interacted by a service
    // Therefore we have to create multiple controllers instead of one
    // and service per controller

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
                // Extract search field text then reset
                String queryString = searchPane.getQueryString().trim();
                List<String> queryStrings = Stream.of(queryString.split(",", -1)).map(String::trim).toList();
                searchPane.reset();

                // Get current tab titles
                List<String> currentTabTitles = resultsPane.getAllTabTitles();
                // Create a list of keyword strings to query. Remove already searched words from the list
                List<String> newKeywordsToQuery = queryStrings.stream()
                        .filter(keyword -> !currentTabTitles.contains(keyword))
                        .toList();

                // Create a popup if there are already shown words
                List<String> alreadySearchedWords = queryStrings.stream()
                        .filter(keyword -> !newKeywordsToQuery.contains(keyword))
                        .toList();
                if (!alreadySearchedWords.isEmpty()) {
                    String message = String.format("Term/s already queried. Skipping: %s", String.join(", ", alreadySearchedWords));
                    JOptionPane.showMessageDialog(mainPanel,
                            message,
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

                // Display searching notification if there are new keywords to query
                if (!newKeywordsToQuery.isEmpty()) {
                    JOptionPane.showMessageDialog(mainPanel, "Searching...");
                }

                // Create a queue of user queries
                QueryQueue queue = new QueryQueue();
                boolean success = false;
                try {
                    success = queue.createQueue(newKeywordsToQuery);
                } catch (InputException ex) {
                    throw new RuntimeException(ex);
                }
                if (!success) {
                    // Close the searching notification
                    JOptionPane.showMessageDialog(mainPanel,
                            "Empty user input. Search field cannot be empty.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

                // TODO: Learn about this concurrency
                CompletableFuture<List<QueryResult>> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        return service.queryMultiple(queue);
                    } catch (JsonProcessingException | ApiFetchException | WordExtractionException ex) {
                        ex.printStackTrace();
                        return null;
                    }
                });

                future.thenAccept(data -> {
                    if (data.isEmpty()) {
                        System.out.println("No results found");
                    } else {
                        for (QueryResult result : data) {
                            resultsPane.createTab(result);
                        }
                        // Set active to last query
                        resultsPane.setSelectedIndex(resultsPane.getTabCount() - 1);
                    }
                }).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
            }
        };
        this.searchPane.searchQuery(searchAction);
        this.actionPane.searchQuery(searchAction);


        // Close the active tab
        this.actionPane.closeActiveTab(e -> {
            int indexToClose = this.resultsPane.getSelectedIndex();
            this.resultsPane.removeTabAt(indexToClose);

            if (this.resultsPane.getTabCount() == 0) {
                this.resultsPane.createDefaultTab();
            }
        });

        // Copy to clipboard
        this.actionPane.copySelectedResultsToClipboard(e -> {
            this.resultsPane.copyResultsFromActiveTabToClipboard();
        });
    }
}
