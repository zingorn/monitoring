package com.zingorn.monitoring.resources;

import com.zingorn.monitoring.api.Config;
import com.zingorn.monitoring.core.Manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Provides access to scheduled jobs.
 */
@Path("/v1.0/scheduler")
@Produces(MediaType.APPLICATION_JSON)
public class SchedulerResource extends BaseResource {

    private Manager manager;

    @Inject
    public SchedulerResource(Manager manager) {
        this.manager = manager;
    }

    /**
     * Update job list.
     * You can update jobs list without application restarting.
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/update-jobs")
    public Response updateJobs(Config config) {
        return runOrThrow(manager::updateJobs, config);
    }

    /**
     * Returns the last job's result reports.
     * Each job report has at least job verdict, job elapsed time and the last run time.
     */
    @GET
    @Path("/last-runs")
    public Response lastRuns() {
        return runOrThrow(this::getLastRuns);
    }

    private Map<String, Object> getLastRuns() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> data = manager.getLastResults();
        result.put("data", data);
        result.put("recordsTotal", data.size());
        result.put("recordsFiltered", data.size());
        return result;
    }
}
