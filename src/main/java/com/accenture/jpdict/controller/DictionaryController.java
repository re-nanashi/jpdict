package com.accenture.jpdict.controller;

import com.accenture.jpdict.exceptions.ApiFetchException;
import com.accenture.jpdict.exceptions.WordExtractionException;
import com.accenture.jpdict.gui.ActionPane;
import com.accenture.jpdict.gui.ResultsPane;
import com.accenture.jpdict.gui.SearchPane;
import com.accenture.jpdict.model.QueryQueue;
import com.accenture.jpdict.model.QueryResult;
import com.accenture.jpdict.service.DictionaryService;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DictionaryController {
    private final DictionaryService service;

    private final SearchPane searchPane;
    private final ActionPane actionPane;
    private final ResultsPane resultsPane;

    public DictionaryController(SearchPane searchPane, ActionPane actionPane, ResultsPane resultsPane) {
        this.service = new DictionaryService();

        this.searchPane = searchPane;
        this.actionPane = actionPane;
        this.resultsPane = resultsPane;

        // Search the dictionary
        Action searchAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Extract search field text then reset
                String queryString = searchPane.getQueryString();
                searchPane.reset();

                // Remove from queue already searched words
                // get all the tab titles then use as argument to createQueue method as reference
                // for already searched words
                List<String> alreadyShown = new ArrayList<>();
                int totalTabs = resultsPane.getTabCount();
                for (int i = 0; i < totalTabs; i++) {
                    alreadyShown.add(resultsPane.getTitleAt(i));
                }

                QueryQueue queue = new QueryQueue();
                boolean success = queue.createQueue(queryString.trim(), alreadyShown);
                if (!success) {
                    throw new RuntimeException("Error encountered while creating a queue");
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
