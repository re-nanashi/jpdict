package com.accenture.jpdict.model;

import com.accenture.jpdict.exceptions.InputException;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Stream;

public class QueryQueue {
    private final Queue<Query> queue;

    // Constructor to initialize the queue
    public QueryQueue() {
        this.queue = new LinkedList<>();
    }

    // Returns true if the queue creation is successful
    // TODO:
    //  I think this should throw an exception or it should let the user know that no results found for the specific word
    //  or the word is already searched
    public boolean createQueue(String queryString, List<String> alreadyShown) throws InputException {
        List<String> extractedKeywords = Stream.of(queryString.split(",", -1))
                .map(String::trim)
                .filter(keyword -> !alreadyShown.contains(keyword))
                .toList();

        if (extractedKeywords.stream().anyMatch(String::isEmpty)) {
            throw new InputException("Error extracting keywords from the query");
        }

        extractedKeywords.forEach(keyword -> {
            Query query = new Query(keyword.trim());
            this.offer(query);
        });

        return true;
    }

    // Method to add an element to the queue
    public void offer(Query query) {
        queue.offer(query);
    }

    // Method to remove and return the front element of the queue
    public Query poll() {
        return queue.poll();
    }

    // Method to check if the queue is empty
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    // Method to get the size of the queue
    public int size() {
        return queue.size();
    }

    // Method to peek at the front element of the queue without removing it
    public Query peek() {
        return queue.peek();
    }

    @Override
    public String toString() {
        return queue.toString();
    }
}