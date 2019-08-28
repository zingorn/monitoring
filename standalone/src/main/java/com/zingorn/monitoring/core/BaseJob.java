package com.zingorn.monitoring.core;

import com.zingorn.monitoring.api.JobReport;

import org.quartz.Job;

/**
 * Base job class.
 *
 * @param <R> job report class.
 */
public abstract class BaseJob<R extends JobReport> implements Job {

    /**
     * Provides new job report object.
     */
    public abstract R createReport();
}
