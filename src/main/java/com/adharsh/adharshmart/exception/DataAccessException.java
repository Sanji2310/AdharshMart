package com.adharsh.adharshmart.exception;

/** Unchecked wrapper around a lower-level {@link java.sql.SQLException} — maps to HTTP 500. */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
