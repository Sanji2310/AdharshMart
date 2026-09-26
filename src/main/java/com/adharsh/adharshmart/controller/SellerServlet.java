package com.adharsh.adharshmart.controller;

import com.adharsh.adharshmart.model.Role;
import com.adharsh.adharshmart.service.OrderService;
import com.adharsh.adharshmart.service.ProductService;
import com.adharsh.adharshmart.service.ServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Seller dashboard reads.
 * GET /api/v1/seller/products -> the calling seller's own listings (F2)
 * GET /api/v1/seller/orders   -> incoming orders for the seller's products (F6)
 */
@WebServlet("/api/v1/seller/*")
public class SellerServlet extends BaseServlet {

    private final ProductService productService = ServiceFactory.productService();
    private final OrderService orderService = ServiceFactory.orderService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            requireRole(req, Role.SELLER);
            Long sellerId = currentUserId(req);
            String pathInfo = req.getPathInfo() == null ? "" : req.getPathInfo();
            switch (pathInfo) {
                case "/products" -> writeOk(resp, HttpServletResponse.SC_OK, productService.findBySeller(sellerId));
                case "/orders" -> writeOk(resp, HttpServletResponse.SC_OK, orderService.findIncomingForSeller(sellerId));
                default -> writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "Unknown seller resource");
            }
        } catch (Exception e) {
            handle(resp, e);
        }
    }
}
