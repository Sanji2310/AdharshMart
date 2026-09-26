package com.adharsh.adharshmart.exception;

/** Thrown by the service layer when request input fails validation — maps to HTTP 400. */
public class ValidationException extends Exception {
    private final String field;
    private final String code;

    public ValidationException(String field, String code, String message) {
        super(message);
        this.field = field;
        this.code = code;
    }

    public String getField() {
        return field;
    }

    public String getCode() {
        return code;
    }
}
