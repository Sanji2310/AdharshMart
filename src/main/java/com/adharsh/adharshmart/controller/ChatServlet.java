package com.adharsh.adharshmart.controller;

import com.adharsh.adharshmart.dto.ChatRequest;
import com.adharsh.adharshmart.dto.ChatResponse;
import com.adharsh.adharshmart.service.ChatService;
import com.adharsh.adharshmart.service.ServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The AI chatbot endpoint (Section 11).
 * POST /api/v1/chat {"message": "..."} -> {"reply": "..."}
 * Session-scoped: creates a session if one doesn't exist, purely to key the per-session rate
 * limiter/cache — no login is required to talk to the assistant.
 */
@WebServlet("/api/v1/chat")
public class ChatServlet extends BaseServlet {

    private final ChatService chatService = ServiceFactory.chatService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            ChatRequest body = readBody(req, ChatRequest.class);
            String sessionId = req.getSession(true).getId();
            String reply = chatService.reply(sessionId, body.getMessage());
            writeOk(resp, HttpServletResponse.SC_OK, new ChatResponse(reply));
        } catch (Exception e) {
            handle(resp, e);
        }
    }
}
