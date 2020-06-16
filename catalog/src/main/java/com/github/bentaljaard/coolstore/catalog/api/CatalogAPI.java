package com.github.bentaljaard.coolstore.catalog.api;

import com.github.bentaljaard.coolstore.catalog.models.Product;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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