package com.adharsh.adharshmart.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.adharsh.adharshmart.exception.ValidationException;
import com.adharsh.adharshmart.service.chat.ChatProvider;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class ChatServiceTest {

    @Test
    void repliesUsingProviderAndCachesIdenticalQuestions() throws Exception {
        AtomicInteger callCount = new AtomicInteger();
        ChatProvider countingProvider = (msg, ctx) -> {
            callCount.incrementAndGet();
            return "Reply to: " + msg;
        };
        ChatService chatService = new ChatService(countingProvider);

        String first = chatService.reply("session-1", "What is your return policy?");
        String second = chatService.reply("session-1", "What is your return policy?");

        assertEquals(first, second);
        assertEquals(1, callCount.get()); // second call served from the per-session cache
    }

    @Test
    void rejectsMessageOverLengthCap() {
        ChatService chatService = new ChatService((msg, ctx) -> "reply");
        String tooLong = "x".repeat(600);

        assertThrows(ValidationException.class, () -> chatService.reply("session-2", tooLong));
    }

    @Test
    void enforcesPerSessionRateLimit() throws Exception {
        ChatService chatService = new ChatService((msg, ctx) -> "reply");
        for (int i = 0; i < 10; i++) {
            chatService.reply("session-3", "question " + i);
        }

        assertThrows(ValidationException.class, () -> chatService.reply("session-3", "one too many"));
    }

    @Test
    void degradesGracefullyWhenProviderThrows() throws Exception {
        ChatProvider failingProvider = (msg, ctx) -> { throw new RuntimeException("boom"); };
        ChatService chatService = new ChatService(failingProvider);

        String reply = chatService.reply("session-4", "shipping question");

        // Falls back to the mock provider rather than propagating the failure.
        org.junit.jupiter.api.Assertions.assertNotNull(reply);
    }
}
