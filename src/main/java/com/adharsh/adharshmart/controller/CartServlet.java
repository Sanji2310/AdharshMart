package com.adharsh.adharshmart.controller;

import com.adharsh.adharshmart.dto.CartItemDTO;
import com.adharsh.adharshmart.dto.CartRequest;
import com.adharsh.adharshmart.exception.UnauthorizedException;
import com.adharsh.adharshmart.service.CartService;
import com.adharsh.adharshmart.service.ServiceFactory;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * F4 — cart add/update/remove with a running total.
 * GET    /api/v1/cart       -> { items: [...], total: 123.45 }
 * POST   /api/v1/cart       -> add {productId, quantity}
 * PUT    /api/v1/cart/{id}  -> update quantity {quantity}
 * DELETE /api/v1/cart/{id}  -> remove
 */
@WebServlet("/api/v1/cart/*")
public class CartServlet extends BaseServlet {

    private final CartService cartService = ServiceFactory.cartService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Long userId = requireUser(req);
            writeOk(resp, HttpServletResponse.SC_OK, cartPayload(cartService.getCart(userId)));
        } catch (Exception e) {
            handle(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Long userId = requireUser(req);
            CartRequest body = readBody(req, CartRequest.class);
            List<CartItemDTO> items = cartService.addItem(userId, body.getProductId(), body.getQuantity());
            writeOk(resp, HttpServletResponse.SC_CREATED, cartPayload(items));
        } catch (Exception e) {
            handle(resp, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Long userId = requireUser(req);
            long cartItemId = pathId(req);
            CartRequest body = readBody(req, CartRequest.class);
            List<CartItemDTO> items = cartService.updateItem(userId, cartItemId, body.getQuantity());
            writeOk(resp, HttpServletResponse.SC_OK, cartPayload(items));
        } catch (Exception e) {
            handle(resp, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Long userId = requireUser(req);
            long cartItemId = pathId(req);
            List<CartItemDTO> items = cartService.removeItem(userId, cartItemId);
            writeOk(resp, HttpServletResponse.SC_OK, cartPayload(items));
        } catch (Exception e) {
            handle(resp, e);
        }
    }

    private Long requireUser(HttpServletRequest req) throws UnauthorizedException {
        Long userId = currentUserId(req);
        if (userId == null) {
            throw new UnauthorizedException("Login required", false);
        }
        return userId;
    }

    private Map<String, Object> cartPayload(List<CartItemDTO> items) {
        BigDecimal total = cartService.runningTotal(items);
        return Map.of("items", items, "total", total);
    }
}
