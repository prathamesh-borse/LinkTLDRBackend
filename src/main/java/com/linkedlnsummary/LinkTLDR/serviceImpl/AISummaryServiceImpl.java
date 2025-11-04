package com.linkedlnsummary.LinkTLDR.serviceImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkedlnsummary.LinkTLDR.service.AISummaryService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class AISummaryServiceImpl implements AISummaryService {

    @Value("${huggingface.api.token}")
    private String huggingfaceAPIKey;

    @Value("${MODEL.URL}")
    private String MODEL_URL;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String summarizePost(String postContent) {
        try {
            // Create JSON body
            String requestBody = String.format("""
                    {
                      "inputs": "%s"
                    }
                    """, sanitize(postContent));

            // Build HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(MODEL_URL))
                    .header("Authorization", "Bearer " + huggingfaceAPIKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // Send the request
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Check HTTP response
            if (response.statusCode() != 200) {
                throw new RuntimeException("Hugging Face API error: " + response.statusCode() + " " + response.body());
            }

            // Parse JSON response
            JsonNode root = objectMapper.readTree(response.body());

            // Example response format:
            // [ { "summary_text": "This is the summarized text." } ]
            if (root.isArray() && root.size() > 0) {
                return root.get(0).get("summary_text").asText();
            } else {
                return "No summary returned from Hugging Face.";
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error calling Hugging Face API", e);
        }
    }

    // Prevents JSON parsing errors due to quotes or newlines
    private String sanitize(String text) {
        return text.replace("\"", "'").replace("\n", " ");
    }
}