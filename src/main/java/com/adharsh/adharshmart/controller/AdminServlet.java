package com.adharsh.adharshmart.controller;

import com.adharsh.adharshmart.model.Role;
import com.adharsh.adharshmart.service.AdminService;
import com.adharsh.adharshmart.service.ServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * F7 — admin view of all users/orders/inventory and listing moderation.
 * GET    /api/v1/admin/users            -> all users
 * GET    /api/v1/admin/orders           -> all orders
 * GET    /api/v1/admin/products         -> marketplace-wide inventory (every seller, active or not)
 * DELETE /api/v1/admin/products/{id}    -> remove/moderate a listing
 */
@WebServlet("/api/v1/admin/*")
public class AdminServlet extends BaseServlet {

    private final AdminService adminService = ServiceFactory.adminService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            requireRole(req, Role.ADMIN);
            String pathInfo = req.getPathInfo() == null ? "" : req.getPathInfo();
            switch (pathInfo) {
                case "/users" -> writeOk(resp, HttpServletResponse.SC_OK, adminService.listUsers());
                case "/orders" -> writeOk(resp, HttpServletResponse.SC_OK, adminService.listOrders());
                case "/products" -> writeOk(resp, HttpServletResponse.SC_OK, adminService.listAllProducts());
                default -> writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "Unknown admin resource");
            }
        } catch (Exception e) {
            handle(resp, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            requireRole(req, Role.ADMIN);
            String pathInfo = req.getPathInfo() == null ? "" : req.getPathInfo();
            if (!pathInfo.startsWith("/products/")) {
                writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "Unknown admin resource");
                return;
            }
            long productId = Long.parseLong(pathInfo.substring("/products/".length()));
            adminService.removeListing(productId);
            writeOk(resp, HttpServletResponse.SC_OK, null);
        } catch (Exception e) {
            handle(resp, e);
        }
    }
}
