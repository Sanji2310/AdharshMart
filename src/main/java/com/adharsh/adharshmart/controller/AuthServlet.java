package com.adharsh.adharshmart.controller;

import com.adharsh.adharshmart.dto.LoginRequest;
import com.adharsh.adharshmart.dto.RegisterRequest;
import com.adharsh.adharshmart.dto.UserResponseDTO;
import com.adharsh.adharshmart.exception.ValidationException;
import com.adharsh.adharshmart.model.Role;
import com.adharsh.adharshmart.service.ServiceFactory;
import com.adharsh.adharshmart.service.UserService;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/** F1 — /api/v1/auth/register, /api/v1/auth/login, /api/v1/auth/logout, /api/v1/auth/me. */
@WebServlet("/api/v1/auth/*")
public class AuthServlet extends BaseServlet {

    private final UserService userService = ServiceFactory.userService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getPathInfo() == null ? "" : req.getPathInfo();
        try {
            switch (action) {
                case "/register" -> register(req, resp);
                case "/login" -> login(req, resp);
                case "/logout" -> logout(req, resp);
                default -> writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "Unknown auth action");
            }
        } catch (Exception e) {
            handle(resp, e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getPathInfo() == null ? "" : req.getPathInfo();
        if (!"/me".equals(action)) {
            writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "Unknown auth action");
            return;
        }
        Long userId = currentUserId(req);
        if (userId == null) {
            writeError(resp, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHENTICATED", "Not logged in");
            return;
        }
        try {
            writeOk(resp, HttpServletResponse.SC_OK, userService.getById(userId));
        } catch (Exception e) {
            handle(resp, e);
        }
    }

    private void register(HttpServletRequest req, HttpServletResponse resp) throws IOException, ValidationException {
        RegisterRequest body = readBody(req, RegisterRequest.class);
        Role role = parseRole(body.getRole());
        UserResponseDTO created = userService.register(body.getName(), body.getEmail(), body.getPassword(), role);
        writeOk(resp, HttpServletResponse.SC_CREATED, created);
    }

    private void login(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        LoginRequest body = readBody(req, LoginRequest.class);
        UserResponseDTO user = userService.login(body.getEmail(), body.getPassword());

        // Mandatory engineering rule #3: regenerate the session id on login to defeat fixation.
        HttpSession oldSession = req.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        HttpSession session = req.getSession(true);
        session.setAttribute("userId", user.getId());
        session.setAttribute("role", user.getRole());
        session.setAttribute("name", user.getName());
        session.setMaxInactiveInterval(30 * 60);

        writeOk(resp, HttpServletResponse.SC_OK, user);
    }

    private void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        writeOk(resp, HttpServletResponse.SC_OK, null);
    }

    private Role parseRole(String raw) throws ValidationException {
        try {
            return Role.valueOf(raw == null ? "" : raw.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("role", "VALIDATION_ERROR", "Role must be BUYER or SELLER");
        }
    }
}
