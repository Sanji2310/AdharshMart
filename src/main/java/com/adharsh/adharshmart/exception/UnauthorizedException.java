package com.adharsh.adharshmart.exception;

/** Thrown on failed authentication or when a session lacks the required role — HTTP 401/403. */
public class UnauthorizedException extends Exception {
    private final boolean forbidden;

    public UnauthorizedException(String message, boolean forbidden) {
        super(message);
        this.forbidden = forbidden;
    }

    /** True -> HTTP 403 (authenticated but not allowed); false -> HTTP 401 (not authenticated). */
    public boolean isForbidden() {
        return forbidden;
    }
}
