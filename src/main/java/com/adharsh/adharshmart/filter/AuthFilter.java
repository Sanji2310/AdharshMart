package com.adharsh.adharshmart.filter;

import com.adharsh.adharshmart.dto.ApiResponse;
import com.adharsh.adharshmart.util.JsonUtil;
import java.io.IOException;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Session-gate for every protected route (mandatory engineering rule #3, security checklist item
 * "all protected servlets enforce session checks via AuthFilter"). Fine-grained role checks
 * (e.g. SELLER-only, ADMIN-only) still happen in the owning servlet — this filter only verifies
 * that a session with an authenticated user exists. Registered via web.xml, after EncodingFilter.
 */
public class AuthFilter implements Filter {

    /** Path prefixes reachable without an authenticated session. */
    private static final List<String> PUBLIC_PREFIXES = List.of(
            "/css/", "/js/", "/images/",
            "/index.jsp", "/login.jsp", "/register.jsp", "/products.jsp", "/product-detail.jsp",
            "/404.jsp", "/500.jsp",
            "/api/v1/auth/", "/api/v1/health", "/api/v1/chat"
    );

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI().substring(request.getContextPath().length());
        boolean isPublicPage = path.equals("/") || path.isEmpty();
        boolean isPublicApi = "GET".equalsIgnoreCase(request.getMethod())
                && (path.equals("/api/v1/products") || path.startsWith("/api/v1/products/")
                    || path.equals("/api/v1/reviews") || path.startsWith("/api/v1/reviews/"));

        if (isPublicPage || isPublicApi || matchesPublicPrefix(path)) {
            chain.doFilter(req, res);
            return;
        }

        if (requiresAuth(path)) {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                respondUnauthenticated(path, response);
                return;
            }
        }
        chain.doFilter(req, res);
    }

    private boolean matchesPublicPrefix(String path) {
        for (String prefix : PUBLIC_PREFIXES) {
            if (path.equals(prefix) || path.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private boolean requiresAuth(String path) {
        return path.startsWith("/api/v1/cart") || path.startsWith("/api/v1/orders")
                || path.startsWith("/api/v1/admin") || path.startsWith("/api/v1/seller")
                || path.startsWith("/api/v1/reviews") // write path; public GET already returned above
                || path.startsWith("/api/v1/products") // write path; public GET already returned above
                || path.startsWith("/cart.jsp") || path.startsWith("/checkout.jsp")
                || path.startsWith("/orders.jsp") || path.startsWith("/seller") || path.startsWith("/admin.jsp");
    }

    private void respondUnauthenticated(String path, HttpServletResponse response) throws IOException {
        if (path.startsWith("/api/")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(
                    JsonUtil.toJson(ApiResponse.fail("UNAUTHENTICATED", "Login required")));
        } else {
            response.sendRedirect("login.jsp");
        }
    }
}
