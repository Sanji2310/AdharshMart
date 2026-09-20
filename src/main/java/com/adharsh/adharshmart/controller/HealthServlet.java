package com.adharsh.adharshmart.controller;

import com.adharsh.adharshmart.listener.DataSourceListener;
import java.io.IOException;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Section 18 — GET /api/v1/health verifies both the app and the database connection. */
@WebServlet("/api/v1/health")
public class HealthServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, String> status = new LinkedHashMap<>();
        status.put("status", "UP");
        try (Connection conn = DataSourceListener.getDataSource().getConnection()) {
            status.put("db", conn.isValid(2) ? "UP" : "DOWN");
        } catch (Exception e) {
            status.put("db", "DOWN");
        }
        writeOk(resp, HttpServletResponse.SC_OK, status);
    }
}
