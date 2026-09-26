package com.adharsh.adharshmart.controller;

import com.adharsh.adharshmart.listener.DataSourceListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.mockito.Mockito;

/**
 * Shared plumbing for servlet-layer tests (Section 9's "Servlet" test row): boots the same
 * DataSourceListener the real container would, against an embedded H2 instance, so servlets can
 * be exercised through real Mockito HttpServletRequest/HttpServletResponse mocks end-to-end
 * (controller -> service -> DAO) rather than mocking every collaborator by hand.
 *
 * <p>Boots exactly once for the whole test JVM (mirroring the real container's single
 * contextInitialized call): re-running seed.sql across test classes would reset the id
 * AUTO_INCREMENT counter and collide with rows earlier servlet tests already inserted.
 */
final class ServletTestSupport {

    private static final AtomicBoolean BOOTED = new AtomicBoolean(false);

    private ServletTestSupport() {
    }

    static void bootDataSource() {
        if (!BOOTED.compareAndSet(false, true)) {
            return; // already booted by an earlier servlet test class in this JVM
        }
        ServletContext context = Mockito.mock(ServletContext.class);
        ServletContextEvent event = Mockito.mock(ServletContextEvent.class);
        Mockito.when(event.getServletContext()).thenReturn(context);
        new DataSourceListener().contextInitialized(event);
    }

    /** No-op: the datasource stays alive for the rest of the test JVM; Surefire tears it down on exit. */
    static void shutdownDataSource() {
    }

    /** A response mock whose getWriter() is backed by a real StringWriter, so body content can be asserted. */
    static HttpServletResponse mockResponse(StringWriter bodyCapture) throws Exception {
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(bodyCapture));
        return response;
    }

    static HttpServletRequest mockRequestWithBody(String pathInfo, String jsonBody) throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getPathInfo()).thenReturn(pathInfo);
        Mockito.when(request.getReader()).thenReturn(new java.io.BufferedReader(new java.io.StringReader(jsonBody)));
        return request;
    }
}
