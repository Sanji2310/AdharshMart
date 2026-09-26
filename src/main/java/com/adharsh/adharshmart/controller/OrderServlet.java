package com.adharsh.adharshmart.controller;

import com.adharsh.adharshmart.dto.OrderResponseDTO;
import com.adharsh.adharshmart.exception.UnauthorizedException;
import com.adharsh.adharshmart.exception.ValidationException;
import com.adharsh.adharshmart.model.OrderStatus;
import com.adharsh.adharshmart.model.Role;
import com.adharsh.adharshmart.service.OrderService;
import com.adharsh.adharshmart.service.ServiceFactory;
import java.io.IOException;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * F5 (checkout), F6 (buyer order history), O2 (status workflow).
 * POST /api/v1/orders/checkout      -> place order from cart (BUYER)
 * GET  /api/v1/orders               -> buyer's own order history
 * GET  /api/v1/orders/{id}          -> single order (owner or ADMIN)
 * PUT  /api/v1/orders/{id}/status   -> advance status {status} (SELLER/ADMIN)
 */
@WebServlet("/api/v1/orders/*")
public class OrderServlet extends BaseServlet {

    private final OrderService orderService = ServiceFactory.orderService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Long userId = requireUser(req);
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                writeOk(resp, HttpServletResponse.SC_OK, orderService.findByBuyer(userId));
                return;
            }
            long id = pathId(req);
            if (id < 0) {
                writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "VALIDATION_ERROR", "Invalid order id");
                return;
            }
            OrderResponseDTO order = orderService.getOrder(id, userId, isAdmin(req));
            writeOk(resp, HttpServletResponse.SC_OK, order);
        } catch (Exception e) {
            handle(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Long userId = requireUser(req);
            String pathInfo = req.getPathInfo();
            if (!"/checkout".equals(pathInfo)) {
                writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "Unknown order action");
                return;
            }
            OrderResponseDTO order = orderService.checkout(userId);
            writeOk(resp, HttpServletResponse.SC_CREATED, order);
        } catch (Exception e) {
            handle(resp, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            requireUser(req);
            Role role = currentRole(req);
            if (role != Role.SELLER && role != Role.ADMIN) {
                throw new UnauthorizedException("Only sellers or admins can update order status", true);
            }
            String pathInfo = req.getPathInfo();
            long id = pathId(req);
            if (id < 0 || pathInfo == null || !pathInfo.endsWith("/status")) {
                writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "VALIDATION_ERROR", "Expected /{id}/status");
                return;
            }
            Map<?, ?> body = readBody(req, Map.class);
            OrderStatus next = parseStatus(String.valueOf(body.get("status")));
            orderService.updateStatus(id, next);
            writeOk(resp, HttpServletResponse.SC_OK, null);
        } catch (Exception e) {
            handle(resp, e);
        }
    }

    private OrderStatus parseStatus(String raw) throws ValidationException {
        try {
            return OrderStatus.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("status", "VALIDATION_ERROR", "Unknown order status: " + raw);
        }
    }

    private Long requireUser(HttpServletRequest req) throws UnauthorizedException {
        Long userId = currentUserId(req);
        if (userId == null) {
            throw new UnauthorizedException("Login required", false);
        }
        return userId;
    }
}
