package com.zingorn.monitoring;

import com.zingorn.monitoring.core.JobFactory;
import com.zingorn.monitoring.core.JobResultListener;
import com.zingorn.monitoring.core.Manager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dagger.Module;
import dagger.Provides;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Environment;

import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.client.Client;

/**
 * Dependency injection module.
 */
@Module
public class UrlMonitoringModule {
    public static final String RESULT_CACHE = "resultCache";
    public static final TypeReference MAP_TYPE = new TypeReference<Map<String, Object>>() {
    };
    public static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    private final UrlMonitoringConfiguration configuration;
    private final Environment environment;

    public UrlMonitoringModule(UrlMonitoringConfiguration configuration, Environment environment) {
        this.configuration = configuration;
        this.environment = environment;
    }

    @Provides
    @Singleton
    public Manager getManager(JobFactory jobFactory, JobResultListener listener) {
        return new Manager(configuration, jobFactory, listener);
    }

    @Provides
    @Singleton
    public Configuration getConfiguration() {
        return configuration;
    }

    @Provides
    @Singleton
    public Environment getEnvironment() {
        return environment;
    }

    @Provides
    @Singleton
    public JobFactory getJobFactory() {
        final Client client = new JerseyClientBuilder(environment)
                .using(configuration.getHttpJobClient())
                .build(UrlMonitoringApplication.NAME + "_HttpUrlJob");
        return new JobFactory(client);
    }

    @Provides
    @Singleton
    @Named(RESULT_CACHE)
    public Cache<String, String> getResultsCache() {
        return CacheBuilder.newBuilder().build();
    }
}
