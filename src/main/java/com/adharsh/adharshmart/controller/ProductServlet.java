package com.adharsh.adharshmart.controller;

import com.adharsh.adharshmart.dto.ProductDTO;
import com.adharsh.adharshmart.model.Role;
import com.adharsh.adharshmart.service.ProductService;
import com.adharsh.adharshmart.service.ServiceFactory;
import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * F2 (seller CRUD) and F3 (buyer browse/search).
 * GET    /api/v1/products?keyword=&category=   -> search/browse
 * GET    /api/v1/products/{id}                 -> detail
 * POST   /api/v1/products                      -> create (SELLER)
 * PUT    /api/v1/products/{id}                 -> update (SELLER, own listing)
 * DELETE /api/v1/products/{id}                 -> deactivate (SELLER own, or ADMIN)
 */
@WebServlet("/api/v1/products/*")
public class ProductServlet extends BaseServlet {

    private final ProductService productService = ServiceFactory.productService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            long id = pathId(req);
            if (id > 0) {
                writeOk(resp, HttpServletResponse.SC_OK, productService.getById(id));
                return;
            }
            String keyword = req.getParameter("keyword");
            String category = req.getParameter("category");
            List<ProductDTO> results = productService.search(keyword, category);
            writeOk(resp, HttpServletResponse.SC_OK, results);
        } catch (Exception e) {
            handle(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            requireRole(req, Role.SELLER);
            ProductDTO body = readBody(req, ProductDTO.class);
            ProductDTO created = productService.create(currentUserId(req), body);
            writeOk(resp, HttpServletResponse.SC_CREATED, created);
        } catch (Exception e) {
            handle(resp, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            requireRole(req, Role.SELLER);
            long id = pathId(req);
            if (id < 0) {
                writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "VALIDATION_ERROR", "Missing product id");
                return;
            }
            ProductDTO body = readBody(req, ProductDTO.class);
            ProductDTO updated = productService.update(currentUserId(req), id, body);
            writeOk(resp, HttpServletResponse.SC_OK, updated);
        } catch (Exception e) {
            handle(resp, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Long userId = currentUserId(req);
            if (userId == null) {
                writeError(resp, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHENTICATED", "Login required");
                return;
            }
            long id = pathId(req);
            if (id < 0) {
                writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "VALIDATION_ERROR", "Missing product id");
                return;
            }
            productService.delete(userId, id, isAdmin(req));
            writeOk(resp, HttpServletResponse.SC_OK, null);
        } catch (Exception e) {
            handle(resp, e);
        }
    }
}
