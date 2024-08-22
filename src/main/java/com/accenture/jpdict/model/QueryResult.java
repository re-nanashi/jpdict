package com.accenture.jpdict.model;

import java.util.ArrayList;
import java.util.List;

public class QueryResult {
    private String queryString;
    private final List<JpWord> results;

    public QueryResult(String queryString) {
        this.queryString = queryString;
        this.results = new ArrayList<>();
    }

    public String getQueryString() {
        return this.queryString;
    }

    public void insertResultItem(JpWord wordToInsert) {
        wordToInsert.setEnglish(this.queryString);
        this.results.add(wordToInsert);
    }

    public List<JpWord> getResults(){
        return List.copyOf(this.results);
    }

    public boolean isEmpty() {
        return this.results.isEmpty();
    }
}
