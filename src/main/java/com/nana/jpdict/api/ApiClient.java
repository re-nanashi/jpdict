package com.nana.jpdict.api;

import com.nana.jpdict.exceptions.ApiFetchException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiClient {
    private static final String API_URL = "https://jisho.org/api/v1/search/words";

    public String fetchData(String query) throws ApiFetchException {
        try {
            URL url = new URL(API_URL + "?keyword=" + transformQueryText(query));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new ApiFetchException("Failed to fetch data from API. Response code: " + responseCode);
            }

            return extractResponse(connection.getInputStream());
        } catch (IOException e) {
            throw new ApiFetchException("Error occurred while fetching data from API", e);
        }
    }

    private String transformQueryText(String query) {
        return query.replaceAll("\\s+", "%20");
    }

    private String extractResponse(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder response = new StringBuilder();
            // Process input string
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}