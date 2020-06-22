package com.github.bentaljaard.coolstore.inventory.api;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.opentracing.Traced;

@Traced
@Path("availability")
public class InventoryApi {

    @Inject
    InventoryService service;

    //Read the environment hostname so that we can see which instance is returning the response
    @ConfigProperty(name = "HOSTNAME")
    String hostname;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAvailability(@PathParam("id") String id) {

        return Response.ok(service.getInventory(id)).header("instanceName", hostname ).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAvailabilityAll() {
        return Response.ok(service.getInventoryAll()).header("instanceName", hostname ).build();
    }

    //Currently no create/update methods are implemented as part of the demos
}