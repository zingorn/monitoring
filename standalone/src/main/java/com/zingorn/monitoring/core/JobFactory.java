package com.zingorn.monitoring.core;

import lombok.AllArgsConstructor;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.simpl.SimpleJobFactory;
import org.quartz.spi.TriggerFiredBundle;

import javax.ws.rs.client.Client;

/**
 * Quartz job factory.
 */
@AllArgsConstructor
public class JobFactory extends SimpleJobFactory {

    Client client;

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        final Job job = super.newJob(bundle, scheduler);
        if (job instanceof HttpUrlJob) {
            // inject http client
            ((HttpUrlJob) job).setClient(client);
        }
        return job;
    }
}
