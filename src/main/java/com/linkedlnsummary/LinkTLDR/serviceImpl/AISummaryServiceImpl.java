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
            if (postContent == null || postContent.isBlank()) {
                return "No content to summarize.";
            }

            // Clean & prepare text
            String safeText = sanitize(postContent);

            // 🚨 Truncate super-long text if necessary
            if (safeText.length() > 12000) {
                safeText = safeText.substring(0, 12000);
            }

            // If text is very long, break into smaller chunks (~1000 chars each)
            if (safeText.length() > 3000) {
                System.out.println("🧩 Splitting input into smaller chunks for summarization...");
                String[] chunks = safeText.split("(?<=\\G.{1000})");
                StringBuilder combinedSummary = new StringBuilder();

                for (String chunk : chunks) {
                    String partial = summarizeChunk(chunk); // custom helper below
                    combinedSummary.append(partial).append(" ");
                    // Small delay to avoid API throttling
                    Thread.sleep(300);
                }

                // Finally, summarize all mini-summaries together (recursive short summary)
                return summarizeChunk(combinedSummary.toString());
            } else {
                // Small text — summarize directly
                return summarizeChunk(safeText);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Unable to summarize this post. Please try again later.";
        }
    }

    // Prevents JSON parsing errors due to quotes or newlines
    private String sanitize(String text) {
        return text
                .replaceAll("\\r?\\n+", " ")
                .replaceAll("\\s+", " ")
                .replace("\"", "'")
                .replaceAll("[^\\p{L}\\p{N}\\p{P}\\p{Z}]", "") // remove emojis / weird tokens
                .trim();
    }

    private String summarizeChunk(String text) {
        try {
            String requestBody = String.format("{\"inputs\": %s}", objectMapper.writeValueAsString(text));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(MODEL_URL))
                    .header("Authorization", "Bearer " + huggingfaceAPIKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("⚠️ HF chunk error: " + response.statusCode() + " " + response.body());
                return "";
            }

            JsonNode root = objectMapper.readTree(response.body());
            if (root.isArray() && root.size() > 0 && root.get(0).has("summary_text")) {
                return root.get(0).get("summary_text").asText();
            }
            return "";
        } catch (Exception e) {
            System.err.println("❌ summarizeChunk failed: " + e.getMessage());
            return "";
        }
    }

}