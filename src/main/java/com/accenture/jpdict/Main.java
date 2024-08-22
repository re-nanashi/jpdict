package com.accenture.jpdict;

import com.accenture.jpdict.exceptions.ApiFetchException;
import com.accenture.jpdict.exceptions.WordExtractionException;
import com.accenture.jpdict.gui.DictionaryUI;
import com.accenture.jpdict.model.JpWord;
import com.accenture.jpdict.model.QueryQueue;
import com.accenture.jpdict.model.QueryResult;
import com.accenture.jpdict.service.DictionaryService;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                DictionaryUI ui = new DictionaryUI();
                ui.display();
            }
        });

        /*
        Scanner in = new Scanner(System.in);
        System.out.println("Enter english keyword to query:");
        String s = in.nextLine();

        QueryQueue queue = new QueryQueue();
        boolean success = queue.createQueue(s.trim());
        if (!success) {
            System.out.println("Error encountered while creating a queue");
            return;
        }

        DictionaryService service = new DictionaryService();

        try {
            List<QueryResult> results = service.queryMultiple(queue);
            if (results.isEmpty()) {
                System.out.println("No results found");
            } else {
                for (QueryResult result : results) {
                    for (JpWord word : result.getResults()) {
                        System.out.println(word);
                    }
                }
            }
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        } catch (ApiFetchException e) {
            e.printStackTrace();
        } catch (WordExtractionException e) {
            throw new RuntimeException(e);
        }
         */
    }
}