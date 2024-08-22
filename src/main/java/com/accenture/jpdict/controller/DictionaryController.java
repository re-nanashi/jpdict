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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DictionaryController {
    private final DictionaryService service;

    private final SearchPane searchPane;
    private ActionPane actionPane;
    private ResultsPane resultsPane;

    public DictionaryController(SearchPane searchPane, ActionPane actionPane, ResultsPane resultsPane) {
        service = new DictionaryService();

        this.searchPane = searchPane;
        this.actionPane = actionPane;
        this.resultsPane = resultsPane;

        // Search the dictionary
        this.actionPane.searchQuery(e -> {
            String queryString = this.searchPane.getQueryString(); // extract query string from the search field
            this.searchPane.reset(); // remove queryString after extraction

            // TODO: Remove from queue already searched words
            List<String> alreadyShown = new ArrayList<>();
            int totalTabs = this.resultsPane.getTabCount();
            for (int i = 0; i < totalTabs; i++) {
                alreadyShown.add(this.resultsPane.getTitleAt(i));
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
                throw new RuntimeException(ex.getMessage());
                }
            });

            future.thenAccept(data -> {
                if (data.isEmpty()) {
                    System.out.println("No results found");
                } else {
                    for (QueryResult result : data) {
                        this.resultsPane.createTab(result);
                    }
                    // Set active to last query
                    this.resultsPane.setSelectedIndex(this.resultsPane.getTabCount() - 1);
                }
            }).exceptionally(ex -> {
                ex.printStackTrace();
                return null;
            });
        });

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
