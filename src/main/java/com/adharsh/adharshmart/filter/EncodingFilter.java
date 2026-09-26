package com.adharsh.adharshmart.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Forces UTF-8 on every request/response so escaped/JSTL output round-trips correctly.
 * Registered (in order) via web.xml rather than @WebFilter — filter sequence is architecturally
 * significant (encoding -> request id -> auth) and annotation-discovery order is unspecified.
 */
public class EncodingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        ((HttpServletResponse) response).setHeader("X-Content-Type-Options", "nosniff");
        chain.doFilter(request, response);
    }
}
