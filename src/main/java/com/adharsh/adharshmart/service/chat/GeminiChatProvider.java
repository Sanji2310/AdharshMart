package com.adharsh.adharshmart.service.chat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Calls the Gemini API server-side only — the API key never appears in client-side code
 * (Section 11, requirement 1). Every outbound call is time-bounded (requirement/guardrail:
 * "timeout on outbound API call") and the caller is expected to fall back to
 * {@link MockChatProvider} on any failure (requirement 3).
 */
public class GeminiChatProvider implements ChatProvider {

    private static final Logger LOG = LoggerFactory.getLogger(GeminiChatProvider.class);
    private static final String ENDPOINT_TEMPLATE =
            "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";

    private final String apiKey;
    private final String model;
    private final Duration timeout;
    private final HttpClient httpClient;

    public GeminiChatProvider(String apiKey, String model, Duration timeout) {
        this.apiKey = apiKey;
        this.model = model;
        this.timeout = timeout;
        this.httpClient = HttpClient.newBuilder().connectTimeout(timeout).build();
    }

    @Override
    public String getReply(String userMessage, String context) {
        JsonObject part = new JsonObject();
        part.addProperty("text", buildPrompt(userMessage, context));
        JsonArray parts = new JsonArray();
        parts.add(part);
        JsonObject content = new JsonObject();
        content.add("parts", parts);
        JsonArray contents = new JsonArray();
        contents.add(content);
        JsonObject body = new JsonObject();
        body.add("contents", contents);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(ENDPOINT_TEMPLATE, model, apiKey)))
                .timeout(timeout)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IllegalStateException("Gemini API returned HTTP " + response.statusCode());
            }
            return extractText(response.body());
        } catch (Exception e) {
            LOG.warn("Gemini API call failed: {}", e.getMessage());
            throw new ChatProviderException("Gemini API call failed", e);
        }
    }

    /** Fixed server-side prompt template (Section 17 guardrail) — keeps the bot scoped to the marketplace domain. */
    private String buildPrompt(String userMessage, String context) {
        return "You are the AdharshMart shopping assistant. Answer only questions about products, "
                + "orders, shipping, returns, and this marketplace. Keep replies under 60 words. "
                + "Context: " + (context == null ? "none" : context) + "\nCustomer question: " + userMessage;
    }

    private String extractText(String responseBody) {
        JsonObject root = JsonParser.parseString(responseBody).getAsJsonObject();
        return root.getAsJsonArray("candidates").get(0).getAsJsonObject()
                .getAsJsonObject("content").getAsJsonArray("parts")
                .get(0).getAsJsonObject().get("text").getAsString().trim();
    }

    /** Unchecked — signals the caller (ChatService) to degrade to {@link MockChatProvider}. */
    public static class ChatProviderException extends RuntimeException {
        public ChatProviderException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
