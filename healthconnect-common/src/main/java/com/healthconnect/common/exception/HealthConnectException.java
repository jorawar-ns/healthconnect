package com.healthconnect.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Base application exception that carries an HTTP status and a machine-readable error code.
 * All service-specific exceptions should extend this.
 */
public class HealthConnectException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public HealthConnectException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public HealthConnectException(String message, HttpStatus status, String errorCode, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus()    { return status; }
    public String getErrorCode()     { return errorCode; }
}
