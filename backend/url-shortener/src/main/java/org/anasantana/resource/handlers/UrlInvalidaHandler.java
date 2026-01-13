package org.anasantana.resource.handlers;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.anasantana.service.exception.UrlInvalidaException;

import java.time.Instant;

@Provider
public class UrlInvalidaHandler implements ExceptionMapper<UrlInvalidaException> {
    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(UrlInvalidaException e) {
        int status = 400; // BAD_REQUEST
        CustomError err = new CustomError(Instant.now(), status, e.getMessage(), uriInfo.getPath());
        return Response.status(status).entity(err).build();
    }
}