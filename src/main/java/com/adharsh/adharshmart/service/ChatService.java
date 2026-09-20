package com.adharsh.adharshmart.service;

import com.adharsh.adharshmart.exception.ValidationException;
import com.adharsh.adharshmart.service.chat.ChatProvider;
import com.adharsh.adharshmart.service.chat.ChatProviderFactory;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Orchestrates the chatbot (Section 11/17): per-session rate limiting, an input length cap,
 * a fixed server-side prompt template (delegated to the provider), in-memory answer caching for
 * repeated identical questions, and a guaranteed degraded response when the provider throws.
 */
public class ChatService {

    private static final Logger LOG = LoggerFactory.getLogger(ChatService.class);
    private static final int MAX_MESSAGES_PER_MINUTE = 10;
    private static final int MAX_INPUT_LENGTH = 500;
    private static final String DEGRADED_REPLY =
            "I'm having trouble reaching the assistant right now — please try again shortly, "
            + "or browse Help & FAQs for shipping, returns, and sizing questions.";

    private final ChatProvider provider;
    private final Map<String, SessionState> sessions = new ConcurrentHashMap<>();

    public ChatService(ChatProvider provider) {
        this.provider = provider;
    }

    public String reply(String sessionId, String message) throws ValidationException {
        if (message == null || message.isBlank()) {
            throw new ValidationException("message", "VALIDATION_ERROR", "Message cannot be empty");
        }
        if (message.length() > MAX_INPUT_LENGTH) {
            throw new ValidationException("message", "VALIDATION_ERROR",
                    "Message must be " + MAX_INPUT_LENGTH + " characters or fewer");
        }

        SessionState state = sessions.computeIfAbsent(sessionId, k -> new SessionState());
        synchronized (state) {
            if (!state.tryConsume()) {
                throw new ValidationException("message", "RATE_LIMITED",
                        "Too many messages — please wait a moment before sending another");
            }
            String cacheKey = message.trim().toLowerCase();
            String cached = state.cache.get(cacheKey);
            if (cached != null) {
                return cached;
            }
            String replyText = fetchReply(message);
            state.cache.put(cacheKey, replyText);
            return replyText;
        }
    }

    private String fetchReply(String message) {
        try {
            return provider.getReply(message, "AdharshMart product and order support");
        } catch (RuntimeException e) {
            LOG.warn("Primary chat provider failed, degrading to fallback: {}", e.getMessage());
            try {
                return ChatProviderFactory.fallback().getReply(message, null);
            } catch (RuntimeException fallbackFailure) {
                LOG.error("Fallback chat provider also failed", fallbackFailure);
                return DEGRADED_REPLY;
            }
        }
    }

    /** Per-session sliding-window rate limiter plus a small answer cache. */
    private static final class SessionState {
        private final Deque<Instant> recentMessages = new ArrayDeque<>();
        private final Map<String, String> cache = new ConcurrentHashMap<>();

        boolean tryConsume() {
            Instant now = Instant.now();
            Instant windowStart = now.minusSeconds(60);
            while (!recentMessages.isEmpty() && recentMessages.peekFirst().isBefore(windowStart)) {
                recentMessages.pollFirst();
            }
            if (recentMessages.size() >= MAX_MESSAGES_PER_MINUTE) {
                return false;
            }
            recentMessages.addLast(now);
            return true;
        }
    }
}
