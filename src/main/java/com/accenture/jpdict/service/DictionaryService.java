package com.accenture.jpdict.service;

import com.accenture.jpdict.api.ApiClient;
import com.accenture.jpdict.api.ApiResponseHandler;
import com.accenture.jpdict.exceptions.ApiFetchException;
import com.accenture.jpdict.exceptions.WordExtractionException;
import com.accenture.jpdict.model.Query;
import com.accenture.jpdict.model.QueryQueue;
import com.accenture.jpdict.model.QueryResult;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DictionaryService {
    private final ApiClient apiClient = new ApiClient();
    private final ApiResponseHandler apiResponseHandler = new ApiResponseHandler();

    public Optional<QueryResult> query(String queryString) throws JsonProcessingException, ApiFetchException, WordExtractionException {
        String jsonData = apiClient.fetchData(queryString);
        QueryResult result = apiResponseHandler.parseResponse(jsonData, queryString);
        if (result.isEmpty())  return Optional.empty();
        return Optional.of(result);
    }

    public List<QueryResult> queryMultiple(QueryQueue queue) throws JsonProcessingException, ApiFetchException, WordExtractionException {
        List<QueryResult> multipleQueryResults = new ArrayList<>();

        while(!queue.isEmpty()) {
            Query curr = queue.poll();
            Optional<QueryResult> result = this.query(curr.getQueryString());
            result.ifPresent(multipleQueryResults::add);
        }

        return multipleQueryResults;
    }
}
