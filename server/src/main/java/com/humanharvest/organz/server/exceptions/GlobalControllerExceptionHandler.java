package com.humanharvest.organz.server.exceptions;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ResponseStatus(value = HttpStatus.PRECONDITION_REQUIRED, reason = "If-Match header is required to modify "
            + "resources")
    @ExceptionHandler(IfMatchRequiredException.class)
    public void ifMatchRequired() {}

    @ResponseStatus(value = HttpStatus.PRECONDITION_FAILED, reason = "If-Match header does not match resource ETag")
    @ExceptionHandler(IfMatchFailedException.class)
    public void ifMatchFailed(HttpServletRequest req) {
        System.out.println(req.getHeader("If-Match"));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidRequestException extends RuntimeException {}

}

