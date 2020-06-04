package com.github.bentaljaard.coolstore.inventory.inventory.api;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("availability")
public class InventoryApi {

    @Inject InventoryService service;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAvailability(@PathParam("id") String id) {
        return Response.ok(service.getInventory(id)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAvailabilityAll() {
        return Response.ok(service.getInventoryAll()).build();
    }
}