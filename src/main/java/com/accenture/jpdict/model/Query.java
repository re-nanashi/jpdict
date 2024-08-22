package com.accenture.jpdict.model;

import java.util.Objects;

public class Query {
    private String queryString;

    public Query(String queryString) {
        this.queryString = queryString;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final Query other = (Query) obj;
        return this.queryString.equals(other.queryString);
    }


    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }
}