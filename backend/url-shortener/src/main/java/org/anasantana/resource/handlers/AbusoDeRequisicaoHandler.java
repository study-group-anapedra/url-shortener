package org.anasantana.resource.handlers;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.anasantana.service.exception.AbusoDeRequisicaoException;

import java.time.Instant;

@Provider
public class AbusoDeRequisicaoHandler implements ExceptionMapper<AbusoDeRequisicaoException> {
    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(AbusoDeRequisicaoException e) {
        int status = 429; // TOO_MANY_REQUESTS
        CustomError err = new CustomError(Instant.now(), status, e.getMessage(), uriInfo.getPath());
        return Response.status(status).entity(err).build();
    }
}

/*
package org.anasantana.resource.handlers;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.anasantana.service.exception.SuaExcecaoAqui; // Troque pela exceção específica
import java.time.Instant;

@Provider // Registra o componente no servidor
public class SuaExcecaoHandler implements ExceptionMapper<SuaExcecaoAqui> {

    @Context
    private UriInfo uriInfo; // Forma correta de obter a URL no Jakarta

    @Override
    public Response toResponse(SuaExcecaoAqui e) {
        // Defina o código numérico correto para cada uma
        int status = 400;

        CustomError err = new CustomError(
            Instant.now(),
            status,
            e.getMessage(),
            uriInfo.getPath()
        );

        return Response.status(status)
                       .entity(err)
                       .build();
    }
}
 */