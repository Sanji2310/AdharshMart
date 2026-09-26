package com.adharsh.adharshmart.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.adharsh.adharshmart.model.Role;
import com.adharsh.adharshmart.service.ServiceFactory;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** Servlet-layer tests (Section 9): session-gated request handling for CartServlet. */
class CartServletTest {

    private final CartServlet servlet = new CartServlet();
    private static final Long SEEDED_PRODUCT_ID = 1L; // "Atelier Wool Overcoat" from seed.sql

    @BeforeAll
    static void bootDataSource() {
        ServletTestSupport.bootDataSource();
    }

    @AfterAll
    static void shutdownDataSource() {
        ServletTestSupport.shutdownDataSource();
    }

    @Test
    void getWithoutSessionReturns401() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getSession(false)).thenReturn(null);
        StringWriter body = new StringWriter();
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(body));

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(body.toString().contains("UNAUTHENTICATED"));
    }

    @Test
    void addItemSucceedsForAuthenticatedBuyer() throws Exception {
        Long buyerId = registerFreshBuyer();

        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("userId")).thenReturn(buyerId);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getSession(false)).thenReturn(session);
        when(request.getReader()).thenReturn(new java.io.BufferedReader(
                new java.io.StringReader("{\"productId\":" + SEEDED_PRODUCT_ID + ",\"quantity\":2}")));

        StringWriter body = new StringWriter();
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(body));

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        assertTrue(body.toString().contains("\"success\":true"));
        assertTrue(body.toString().contains("\"total\""));
    }

    private Long registerFreshBuyer() throws Exception {
        String email = "cart-servlet-test-" + UUID.randomUUID() + "@example.com";
        var dto = ServiceFactory.userService().register("Cart Test Buyer", email, "password123", Role.BUYER);
        return dto.getId();
    }
}
