package com.adharsh.adharshmart.controller;

import com.adharsh.adharshmart.dto.ApiResponse;
import com.adharsh.adharshmart.exception.NotFoundException;
import com.adharsh.adharshmart.exception.UnauthorizedException;
import com.adharsh.adharshmart.exception.ValidationException;
import com.adharsh.adharshmart.model.Role;
import com.adharsh.adharshmart.util.JsonUtil;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Shared HTTP orchestration helpers for every controller — servlets stay thin (SOLID rule: no
 * SQL, no business logic here), and the {@code /api/v1} response envelope (Section 13) is
 * produced in exactly one place.
 */
public abstract class BaseServlet extends HttpServlet {

    protected <T> T readBody(HttpServletRequest req, Class<T> type) throws IOException {
        return JsonUtil.fromJson(req.getReader(), type);
    }

    protected void writeOk(HttpServletResponse resp, int status, Object data) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.getWriter().write(JsonUtil.toJson(ApiResponse.ok(data)));
    }

    protected void writeError(HttpServletResponse resp, int status, String code, String message) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.getWriter().write(JsonUtil.toJson(ApiResponse.fail(code, message)));
    }

    protected Long currentUserId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (Long) session.getAttribute("userId");
    }

    protected Role currentRole(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (Role) session.getAttribute("role");
    }

    protected boolean isAdmin(HttpServletRequest req) {
        return currentRole(req) == Role.ADMIN;
    }

    protected void requireRole(HttpServletRequest req, Role required) throws UnauthorizedException {
        Long userId = currentUserId(req);
        if (userId == null) {
            throw new UnauthorizedException("Login required", false);
        }
        Role role = currentRole(req);
        if (role != required && role != Role.ADMIN) {
            throw new UnauthorizedException("This action requires the " + required + " role", true);
        }
    }

    /** Central mapping from a thrown service-layer exception to the API contract's status codes. */
    protected void handle(HttpServletResponse resp, Exception e) throws IOException {
        if (e instanceof ValidationException ve) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, ve.getCode(), ve.getMessage());
        } else if (e instanceof NotFoundException) {
            writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", e.getMessage());
        } else if (e instanceof UnauthorizedException ue) {
            int status = ue.isForbidden() ? HttpServletResponse.SC_FORBIDDEN : HttpServletResponse.SC_UNAUTHORIZED;
            writeError(resp, status, ue.isForbidden() ? "FORBIDDEN" : "UNAUTHENTICATED", e.getMessage());
        } else if (e instanceof NumberFormatException) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "VALIDATION_ERROR", "Invalid numeric parameter");
        } else {
            getServletContext().log("Unhandled servlet exception", e);
            writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SERVER_ERROR", "Something went wrong");
        }
    }

    /** Extracts the numeric id from a path like /12 or /12/status. Returns -1 if absent/invalid. */
    protected long pathId(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            return -1;
        }
        String[] segments = pathInfo.substring(1).split("/");
        try {
            return Long.parseLong(segments[0]);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
