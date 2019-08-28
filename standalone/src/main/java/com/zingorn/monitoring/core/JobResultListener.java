package com.zingorn.monitoring.core;

import static com.zingorn.monitoring.UrlMonitoringModule.RESULT_CACHE;
import static com.zingorn.monitoring.UrlMonitoringModule.mapper;
import static com.zingorn.monitoring.core.HttpUrlJob.JOB_LOGGER_NAME;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.cache.Cache;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Quartz job listener.
 * <p>
 * Saves job result to cache when job finished.
 */
@Slf4j
@Getter
@AllArgsConstructor
public class JobResultListener implements JobListener {

    private Logger jobLogger = LoggerFactory.getLogger(JOB_LOGGER_NAME);
    private final Cache<String, String> resultsCache;

    @Inject
    public JobResultListener(@Named(RESULT_CACHE) Cache<String, String> resultsCache) {
        this.resultsCache = resultsCache;
    }

    @Override
    public String getName() {
        return "JobResultListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {

    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {

    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {

        try {
            // dump job result as JSON string and save to cache
            String dump = mapper.writeValueAsString(context.getResult());
            jobLogger.info(mapper.writeValueAsString(dump));
            resultsCache.put(context.getJobDetail().getKey().getName(), dump);
        } catch (JsonProcessingException e) {
            log.error("JSON encoding failed", e);
        }
    }
}
