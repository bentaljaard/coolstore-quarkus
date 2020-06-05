package com.github.bentaljaard.coolstore.gateway.api;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("api")
public class GatewayApi {

    @Inject GatewayService service;

    @GET
    @Path("products")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProducts() {
        return Response.ok(service.getProducts()).build();
    }
}