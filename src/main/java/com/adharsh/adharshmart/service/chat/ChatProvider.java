package com.adharsh.adharshmart.service.chat;

/**
 * Strategy pattern — the chatbot is accessed through this interface, never a hardcoded
 * implementation (Section 17). Selected at runtime via the {@code ai.chatbot.provider} config flag.
 */
public interface ChatProvider {
    String getReply(String userMessage, String context);
}
