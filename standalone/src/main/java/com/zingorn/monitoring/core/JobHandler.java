package com.zingorn.monitoring.core;

import static com.zingorn.monitoring.UrlMonitoringModule.mapper;
import static java.util.Optional.ofNullable;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import com.zingorn.monitoring.api.BaseJobConfig;
import com.zingorn.monitoring.api.HttpUrl;
import com.zingorn.monitoring.api.HttpUrl.Method;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Class to register job handlers in Quartz scheduler.
 */
@Slf4j
public enum JobHandler {

    /**
     * Registers http job in scheduler.
     */
    HTTP_URL(JobHandler::generateJobIdentity) {
        @Override
        void register(Scheduler scheduler, BaseJobConfig config) {
            try {
                HttpUrl httpUrl = (HttpUrl) config;
                JobDetail job = newJob(HttpUrlJob.class)
                        .withIdentity(HTTP_URL.nameGenerator.get())
                        .usingJobData(HttpUrl.URL_KEY, httpUrl.getUrl())
                        .usingJobData(HttpUrl.CONTENT_REQUIREMENT_KEY,
                                httpUrl.getContentRequirement())
                        .usingJobData(HttpUrl.STATUS_KEY,
                                ofNullable(httpUrl.getStatus()).orElse(200))
                        .usingJobData(HttpUrl.METHOD_KEY, ofNullable(httpUrl.getMethod())
                                .orElse(Method.GET).toString())
                        .usingJobData(HttpUrl.HEADERS_KEY,
                                mapper.writeValueAsString(httpUrl.getHeaders()))
                        .build();

                Trigger trigger = newTrigger()
                        .startNow()
                        .withSchedule(cronSchedule(httpUrl.getPeriod()))
                        .build();
                scheduler.scheduleJob(job, trigger);
                log.info("Job ({}, {}) was registered for {} {}",
                        job.getKey().getName(), job.getKey().getGroup(),
                        httpUrl.getMethod(), httpUrl.getUrl());
            } catch (SchedulerException | JsonProcessingException e) {
                log.error("Register job failed", e);
                throw ApplicationException.badRequest(e.getMessage());
            }
        }
    };

    /**
     * Job name generator supplier function.
     */
    private final Supplier<String> nameGenerator;

    JobHandler(Supplier<String> nameGenerator) {
        this.nameGenerator = nameGenerator;
    }

    abstract void register(Scheduler scheduler, BaseJobConfig config);

    /**
     * Contains mapping job config class to his handlers.
     */
    private static Map<Class, JobHandler> jobHandlers;

    static {
        jobHandlers = new HashMap<>();
        jobHandlers.put(HttpUrl.class, HTTP_URL);
    }

    private static String generateJobIdentity() {
        return UUID.randomUUID().toString();
    }

    /**
     * Registers job in scheduler.
     */
    public static void registerJob(Scheduler scheduler, BaseJobConfig config) {
        jobHandlers.get(config.getClass()).register(scheduler, config);
    }
}
