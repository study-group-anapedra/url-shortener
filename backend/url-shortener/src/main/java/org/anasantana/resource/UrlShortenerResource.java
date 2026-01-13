package org.anasantana.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.anasantana.dto.UrlShortenerDTO;
import org.anasantana.service.UrlShortenerService;

@Path("/url")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UrlShortenerResource {

    @Inject
    private UrlShortenerService service;

    @POST
    public Response encurtar(UrlShortenerDTO dto, @HeaderParam("X-Client-ID") String clientId) {
        UrlShortenerDTO resultado = service.encurtar(dto.getOriginalUrl(), clientId);
        return Response.status(Response.Status.CREATED).entity(resultado).build();
    }

    @GET
    @Path("/{shortCode}")
    public Response buscar(@PathParam("shortCode") String shortCode) {
        UrlShortenerDTO resultado = service.buscarPorShortCode(shortCode);
        return Response.ok(resultado).build();
    }
}