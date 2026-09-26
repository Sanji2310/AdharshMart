package com.adharsh.adharshmart.dto;

/** Response shape rendered by the chat widget: {"reply": "..."}. */
public class ChatResponse {
    private final String reply;

    public ChatResponse(String reply) {
        this.reply = reply;
    }

    public String getReply() {
        return reply;
    }
}
