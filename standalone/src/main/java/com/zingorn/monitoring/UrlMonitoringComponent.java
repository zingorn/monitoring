package com.zingorn.monitoring;

import com.zingorn.monitoring.core.Manager;
import com.zingorn.monitoring.resources.SchedulerResource;

import dagger.Component;

import javax.inject.Singleton;

/**
 * Dependency injection component.
 */
@Singleton
@Component(modules = {UrlMonitoringModule.class})
public interface UrlMonitoringComponent {
    SchedulerResource getSchedulerResource();

    Manager getManager();

}
