package org.anasantana.resource.handlers;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.anasantana.service.exception.InfrastructureException;

import java.time.Instant;

@Provider
public class InfrastructureHandler implements ExceptionMapper<InfrastructureException> {
    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(InfrastructureException e) {
        int status = 500; // INTERNAL_SERVER_ERROR
        CustomError err = new CustomError(Instant.now(), status, e.getMessage(), uriInfo.getPath());
        return Response.status(status).entity(err).build();
    }
}