package com.adharsh.adharshmart.controller;

import com.adharsh.adharshmart.dto.CartRequest;
import com.adharsh.adharshmart.exception.UnauthorizedException;
import com.adharsh.adharshmart.service.ServiceFactory;
import com.adharsh.adharshmart.service.WishlistService;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * O1 — wishlist / save-for-later.
 * GET    /api/v1/wishlist       -> the caller's saved products
 * POST   /api/v1/wishlist       -> save a product {productId} (idempotent)
 * DELETE /api/v1/wishlist/{id}  -> remove a saved product
 */
@WebServlet("/api/v1/wishlist/*")
public class WishlistServlet extends BaseServlet {

    private final WishlistService wishlistService = ServiceFactory.wishlistService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Long userId = requireUser(req);
            writeOk(resp, HttpServletResponse.SC_OK, wishlistService.getWishlist(userId));
        } catch (Exception e) {
            handle(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Long userId = requireUser(req);
            CartRequest body = readBody(req, CartRequest.class);
            var saved = wishlistService.add(userId, body.getProductId());
            writeOk(resp, HttpServletResponse.SC_CREATED, saved);
        } catch (Exception e) {
            handle(resp, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Long userId = requireUser(req);
            long id = pathId(req);
            if (id < 0) {
                writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "VALIDATION_ERROR", "Missing wishlist item id");
                return;
            }
            wishlistService.remove(userId, id);
            writeOk(resp, HttpServletResponse.SC_OK, null);
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
}
