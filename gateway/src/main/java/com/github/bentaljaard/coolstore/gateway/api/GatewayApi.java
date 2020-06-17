package com.github.bentaljaard.coolstore.gateway.api;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.opentracing.Traced;

@Traced
@Path("api")
public class GatewayApi {

    @Inject GatewayService service;

    @GET
    @Path("products")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed(name = "ListEnrichedProductsTimer", description = "A measure of how long it takes to retrieve a list of products", unit = MetricUnits.MILLISECONDS)
    public Response getProducts() {
        return Response.ok(service.getProducts()).build();
    }
}