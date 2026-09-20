package com.adharsh.adharshmart.service.chat;

import com.adharsh.adharshmart.util.AppConfig;
import java.time.Duration;

/** Factory pattern — selects the {@link ChatProvider} implementation via {@code ai.chatbot.provider}. */
public final class ChatProviderFactory {

    private static final MockChatProvider MOCK = new MockChatProvider();

    private ChatProviderFactory() {
    }

    public static ChatProvider create() {
        String providerName = AppConfig.get("ai.chatbot.provider", "mock");
        if ("gemini".equalsIgnoreCase(providerName)) {
            String apiKey = AppConfig.get("ai.chatbot.apiKey", "");
            if (apiKey.isBlank()) {
                return MOCK; // no key configured -> degrade to mock rather than fail every request
            }
            String model = AppConfig.get("ai.chatbot.model", "gemini-1.5-flash");
            int timeoutSeconds = Integer.parseInt(AppConfig.get("ai.chatbot.timeoutSeconds", "8"));
            return new GeminiChatProvider(apiKey, model, Duration.ofSeconds(timeoutSeconds));
        }
        return MOCK;
    }

    /** Always-available fallback used by {@code ChatService} when the primary provider throws. */
    public static ChatProvider fallback() {
        return MOCK;
    }
}
