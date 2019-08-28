package com.zingorn.monitoring;

import com.zingorn.monitoring.core.HttpUrlJob;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.jobs.JobConfiguration;
import lombok.NonNull;

import javax.validation.Valid;

public class UrlMonitoringConfiguration extends Configuration implements JobConfiguration {

    @Valid
    @NonNull
    private JerseyClientConfiguration httpJobClient = new JerseyClientConfiguration();

    /**
     * Http client configuration for {@link HttpUrlJob}
     */
    @JsonProperty("httpUrlClient")
    public JerseyClientConfiguration getHttpJobClient() {
        return httpJobClient;
    }

    @JsonProperty("httpUrlClient")
    public void setHttpJobClient(JerseyClientConfiguration httpJobClient) {
        this.httpJobClient = httpJobClient;
    }
}
