package com.humanharvest.organz.server.exceptions;

import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Requires a valid X-Auth-Token header.")
    @ExceptionHandler(AuthenticationException.class)
    public void authenticationRequired() {
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "The given resource could not be located.")
    @ExceptionHandler(NotFoundException.class)
    public void notFound() {
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidRequestException extends RuntimeException {

    }

}

