package com.nana.jpdict.service;

import com.nana.jpdict.api.ApiClient;
import com.nana.jpdict.api.ApiResponseHandler;
import com.nana.jpdict.exceptions.ApiFetchException;
import com.nana.jpdict.exceptions.InputException;
import com.nana.jpdict.exceptions.WordExtractionException;
import com.nana.jpdict.model.Query;
import com.nana.jpdict.model.QueryQueue;
import com.nana.jpdict.model.QueryResult;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DictionaryService {
    private final ApiClient apiClient = new ApiClient();
    private final ApiResponseHandler apiResponseHandler = new ApiResponseHandler();

    public Optional<QueryResult> query(String queryString) throws JsonProcessingException, ApiFetchException, WordExtractionException {
        String jsonData = apiClient.fetchData(queryString);

        // Parse response
        QueryResult result = apiResponseHandler.parseResponse(jsonData, queryString);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result);
    }

    public List<QueryResult> queryMultiple(List<String> keywordsToQuery) throws JsonProcessingException, ApiFetchException, WordExtractionException, InputException {
        List<QueryResult> multipleQueryResults = new ArrayList<>();

        QueryQueue queue = new QueryQueue();
        queue.createQueue(keywordsToQuery);

        while(!queue.isEmpty()) {
            Query curr = queue.poll();
            // TODO: Multithreading here
            Optional<QueryResult> result = this.query(curr.getQueryString());
            result.ifPresent(multipleQueryResults::add);
        }

        return multipleQueryResults;
    }
}
