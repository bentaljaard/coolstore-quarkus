package com.github.bentaljaard.coolstore.catalog.api;

import com.github.bentaljaard.coolstore.catalog.models.Product;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/products")
public class CatalogAPI {

    @Inject CatalogService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listProducts() {
        return Response.ok(service.list()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addProduct(Product newProduct) {
        return Response.accepted(service.add(newProduct)).build();
    }

}