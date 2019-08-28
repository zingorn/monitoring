package com.zingorn.monitoring.core;

import static com.zingorn.monitoring.UrlMonitoringModule.MAP_TYPE;
import static com.zingorn.monitoring.UrlMonitoringModule.mapper;
import static com.zingorn.monitoring.core.JobHandler.registerJob;
import static java.util.stream.Collectors.toList;

import com.zingorn.monitoring.UrlMonitoringConfiguration;
import com.zingorn.monitoring.api.BaseJobConfig;
import com.zingorn.monitoring.api.Config;

import io.dropwizard.lifecycle.Managed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Quartz jobs manager class.
 * <p>
 * Registers and updates jobs in scheduler and provides access to job results.
 */
@Slf4j
@RequiredArgsConstructor
public class Manager implements Managed {

    private final UrlMonitoringConfiguration configuration;
    private final Supplier<Scheduler> schedulerSupplier;
    private final JobFactory jobFactory;
    private final JobResultListener listener;

    private Scheduler scheduler = null;

    public Manager(UrlMonitoringConfiguration configuration, JobFactory jobFactory,
                   JobResultListener listener) {
        this(configuration, Manager::getStandardScheduler, jobFactory, listener);
    }

    private static Scheduler getStandardScheduler() {
        try {
            return StdSchedulerFactory.getDefaultScheduler();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Scheduler initialization.
     */
    @Override
    public void start() throws Exception {
        scheduler = schedulerSupplier.get();
        scheduler.setJobFactory(jobFactory);
        scheduler.getListenerManager().addJobListener(listener);
        scheduler.start();
    }

    @Override
    public void stop() throws Exception {
        Thread.sleep(100);
        scheduler.shutdown(true);
    }

    /**
     * Update jobs list.
     * <p>
     * Drops exists running jobs and creates the new ones based on provided config.
     */
    public void updateJobs(Config config) {
        try {
            scheduler.clear();
            listener.getResultsCache().invalidateAll();
            for (BaseJobConfig jobConfig : config.getJobs()) {
                registerJob(scheduler, jobConfig);
            }
        } catch (SchedulerException ex) {
            log.error("Register job failed", ex);
            throw ApplicationException.badRequest(ex.getMessage());
        }
    }

    private Map<String, Object> resultToObject(String result) {
        try {
            return mapper.readValue(result, MAP_TYPE);
        } catch (IOException e) {
            log.error("Can't decode result from JSON", e);
            throw ApplicationException.internalServerError("Can't decode result from JSON");
        }
    }

    /**
     * Returns the last jobs results(reports).
     */
    public List<Map<String, Object>> getLastResults() {
        return listener.getResultsCache().asMap().values().stream()
                .map(this::resultToObject)
                .sorted(Comparator
                        .comparing((Map<String, Object> entity) -> (String) entity.get("lastRun"))
                        .reversed())
                .collect(toList());
    }
}
