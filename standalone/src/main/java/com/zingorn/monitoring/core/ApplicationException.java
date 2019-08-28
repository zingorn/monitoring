package com.zingorn.monitoring.core;

import lombok.Getter;

import java.util.HashMap;

import javax.ws.rs.core.Response.Status;

/**
 * Common application exception with response status code.
 */
public class ApplicationException extends RuntimeException {

    @Getter
    private Status status;

    @Getter
    private Object responseEntity = new HashMap<String, Object>() {{
        put("error", "true");
    }};

    public ApplicationException(String message, Status status) {
        super(message);
        this.status = status;
    }

    public static ApplicationException badRequest(String message) {
        return new ApplicationException(message, Status.BAD_REQUEST);
    }

    public static ApplicationException internalServerError(String message) {
        return new ApplicationException(message, Status.INTERNAL_SERVER_ERROR);
    }
}
