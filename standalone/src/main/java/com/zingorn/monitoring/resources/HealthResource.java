package com.zingorn.monitoring.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Health checker resource.
 */
@Produces("text/plain")
@Path("/health")
public class HealthResource {

    @GET
    public Response health() {
        return Response.status(418).entity("OK").build();
    }
}
