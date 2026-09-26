package com.adharsh.adharshmart.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.StringWriter;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Servlet-layer tests (Section 9): request/response behavior and status codes for AuthServlet,
 * driven through real Mockito HttpServletRequest/HttpServletResponse mocks against a real
 * (embedded) datasource — see {@link ServletTestSupport}.
 */
class AuthServletTest {

    private final AuthServlet servlet = new AuthServlet();

    @BeforeAll
    static void bootDataSource() {
        ServletTestSupport.bootDataSource();
    }

    @AfterAll
    static void shutdownDataSource() {
        ServletTestSupport.shutdownDataSource();
    }

    @Test
    void registerReturns201ForValidPayload() throws Exception {
        String email = "servlet-test-" + UUID.randomUUID() + "@example.com";
        String body = "{\"name\":\"Servlet Test\",\"email\":\"" + email + "\",\"password\":\"password123\",\"role\":\"BUYER\"}";
        HttpServletRequest request = ServletTestSupport.mockRequestWithBody("/register", body);
        StringWriter responseBody = new StringWriter();
        HttpServletResponse response = ServletTestSupport.mockResponse(responseBody);

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        assertTrue(responseBody.toString().contains("\"success\":true"));
        assertTrue(responseBody.toString().contains(email));
    }

    @Test
    void registerReturns400ForInvalidEmail() throws Exception {
        String body = "{\"name\":\"Bad Email\",\"email\":\"not-an-email\",\"password\":\"password123\",\"role\":\"BUYER\"}";
        HttpServletRequest request = ServletTestSupport.mockRequestWithBody("/register", body);
        StringWriter responseBody = new StringWriter();
        HttpServletResponse response = ServletTestSupport.mockResponse(responseBody);

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(responseBody.toString().contains("VALIDATION_ERROR"));
    }

    @Test
    void loginReturns401ForWrongPassword() throws Exception {
        String registerEmail = "servlet-login-" + UUID.randomUUID() + "@example.com";
        HttpServletRequest registerRequest = ServletTestSupport.mockRequestWithBody("/register",
                "{\"name\":\"Login Test\",\"email\":\"" + registerEmail + "\",\"password\":\"password123\",\"role\":\"BUYER\"}");
        servlet.doPost(registerRequest, ServletTestSupport.mockResponse(new StringWriter()));

        HttpServletRequest loginRequest = ServletTestSupport.mockRequestWithBody("/login",
                "{\"email\":\"" + registerEmail + "\",\"password\":\"wrong-password\"}");
        StringWriter responseBody = new StringWriter();
        HttpServletResponse response = mock(HttpServletResponse.class);
        org.mockito.Mockito.when(response.getWriter()).thenReturn(new java.io.PrintWriter(responseBody));

        servlet.doPost(loginRequest, response);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(responseBody.toString().contains("UNAUTHENTICATED"));
    }

    @Test
    void unknownAuthActionReturns404() throws Exception {
        HttpServletRequest request = ServletTestSupport.mockRequestWithBody("/not-a-real-action", "{}");
        StringWriter responseBody = new StringWriter();
        HttpServletResponse response = ServletTestSupport.mockResponse(responseBody);

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}
