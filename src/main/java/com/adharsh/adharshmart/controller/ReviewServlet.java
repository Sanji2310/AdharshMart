package com.adharsh.adharshmart.controller;

import com.adharsh.adharshmart.dto.ReviewRequest;
import com.adharsh.adharshmart.exception.UnauthorizedException;
import com.adharsh.adharshmart.exception.ValidationException;
import com.adharsh.adharshmart.service.ReviewService;
import com.adharsh.adharshmart.service.ServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * F8 — product reviews and star ratings on completed orders.
 * GET  /api/v1/reviews?productId={id} -> public list of reviews
 * POST /api/v1/reviews                -> add a review {productId, rating, comment} (BUYER)
 */
@WebServlet("/api/v1/reviews/*")
public class ReviewServlet extends BaseServlet {

    private final ReviewService reviewService = ServiceFactory.reviewService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String productIdParam = req.getParameter("productId");
            if (productIdParam == null) {
                writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "VALIDATION_ERROR", "productId is required");
                return;
            }
            Long productId = Long.parseLong(productIdParam);
            writeOk(resp, HttpServletResponse.SC_OK, reviewService.findByProduct(productId));
        } catch (Exception e) {
            handle(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Long userId = currentUserId(req);
            if (userId == null) {
                throw new UnauthorizedException("Login required", false);
            }
            ReviewRequest body = readBody(req, ReviewRequest.class);
            if (body.getProductId() == null) {
                throw new ValidationException("productId", "VALIDATION_ERROR", "productId is required");
            }
            var created = reviewService.addReview(userId, body.getProductId(), body.getRating(), body.getComment());
            writeOk(resp, HttpServletResponse.SC_CREATED, created);
        } catch (Exception e) {
            handle(resp, e);
        }
    }
}
