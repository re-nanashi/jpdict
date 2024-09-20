package com.nana.jpdict.model;

import com.nana.jpdict.exceptions.InputException;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class QueryQueue {
    private final Queue<Query> queue;

    // Constructor to initialize the queue
    public QueryQueue() {
        this.queue = new LinkedList<>();
    }

    public void createQueue(List<String> queryStrings) throws InputException {
        if (queryStrings.stream().anyMatch(String::isEmpty)) {
            throw new InputException("Error extracting keywords from the query. Wrong input syntax.");
        }

        queryStrings.forEach(keyword -> {
            Query query = new Query(keyword.trim());
            this.offer(query);
        });
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