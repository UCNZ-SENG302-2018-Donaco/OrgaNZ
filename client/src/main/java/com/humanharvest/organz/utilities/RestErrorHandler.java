package com.humanharvest.organz.utilities;

import java.io.IOException;
import java.net.URI;

import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import com.humanharvest.organz.utilities.exceptions.BadRequestException;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.IfMatchRequiredException;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

public class RestErrorHandler extends DefaultResponseErrorHandler {

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        //Throw a ServerRestException for any 500 level code
        if (response.getStatusCode().is5xxServerError()) {
            throw new ServerRestException(response.getStatusText());
        }

        //Throw specific exceptions for different 400 level codes
        switch (response.getStatusCode()) {
            case BAD_REQUEST:
                String body = new String(getResponseBody(response));
                throw new BadRequestException(body.isEmpty() ? response.getStatusText() : body);
            case NOT_FOUND:
                throw new NotFoundException(response.getStatusText());
            case PRECONDITION_FAILED:
                throw new IfMatchFailedException(response.getStatusText());
            case PRECONDITION_REQUIRED:
                throw new IfMatchRequiredException(response.getStatusText());
            case UNAUTHORIZED:
            case FORBIDDEN:
                throw new AuthenticationException(response.getStatusText());
            default:
                throw new IOException(response.getStatusText());
        }
    }
}
