package com.github.bentaljaard.coolstore.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/healthz")
public class HealthResource {

    @GET
    public String healthz() {
        return "OK";
    }
}
