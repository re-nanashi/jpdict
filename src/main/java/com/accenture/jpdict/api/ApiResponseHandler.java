package com.accenture.jpdict.api;

import com.accenture.jpdict.exceptions.WordExtractionException;
import com.accenture.jpdict.model.JpWord;
import com.accenture.jpdict.model.QueryResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApiResponseHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public QueryResult parseResponse(String jsonResponse, String query) throws JsonProcessingException, WordExtractionException {
        QueryResult queryResult = new QueryResult(query);

        // Parse the JSON string to extract the list of word results
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode dataNode = rootNode.get("data"); // can never go null due to API specification

        // Extract the jp word and its english definitions from the current node
        if (!dataNode.isEmpty()) {
            for (JsonNode currentNode : dataNode) {
                Optional<JpWord> word = extractWord(currentNode);
                word.ifPresent(queryResult::insertResultItem);
            }
        }

        return queryResult;
    }

    private Optional<JpWord> extractWord(JsonNode currentNode) throws JsonProcessingException, WordExtractionException {
        // Extract the japanese word from the "japanese" node
        JsonNode jpNode = currentNode.get("japanese");
        JpWord word = objectMapper.treeToValue(jpNode.get(0), JpWord.class);
        if (word.getWord() == null) {
            word.setWord(jpNode.get(0).get("reading").asText());
        }

        // Extract english definitions from the "senses" node
        Optional<JsonNode> enNode = Optional.ofNullable(currentNode.get("senses").get(0).get("english_definitions"));
        if (enNode.isEmpty()) {
            throw new WordExtractionException("Error extracting english definitions");
        }

        List<String> englishDefinitions = new ArrayList<>();
        JsonNode node = enNode.get();
        if (node.isArray()) {
            node.forEach(definition -> englishDefinitions.add(definition.asText()));
        }

        // Insert other english definition of  the queried word
        word.setOtherDefs(String.join(",", englishDefinitions));

        return Optional.of(word);
    }
}
