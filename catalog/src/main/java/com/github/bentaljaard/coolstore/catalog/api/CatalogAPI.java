package com.github.bentaljaard.coolstore.catalog.api;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.github.bentaljaard.coolstore.catalog.models.Product;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/products")
public class CatalogAPI {

    @Inject CatalogService service;

    //Read the environment hostname so that we can see which instance is returning the response
    @ConfigProperty(name = "HOSTNAME")
    String hostname;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listProducts() {
        return Response.ok(service.list()).header("instanceName", hostname ).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addProduct(Product newProduct) {
        return Response.accepted(service.add(newProduct)).header("instanceName", hostname ).build();
    }

}