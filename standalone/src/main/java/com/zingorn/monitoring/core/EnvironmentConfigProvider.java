package com.zingorn.monitoring.core;

import static java.util.Objects.nonNull;

import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.configuration.FileConfigurationSourceProvider;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.InputStream;

/**
 * Reads an application config for Dropwizard.
 */
@AllArgsConstructor
public class EnvironmentConfigProvider implements ConfigurationSourceProvider {
    private static String ENV_PREFIX = "env:";
    private static String ENV_VARIABLE = "ENV";

    private ConfigurationSourceProvider delegate;

    public EnvironmentConfigProvider() {
        this(new FileConfigurationSourceProvider());
    }

    @Override
    public InputStream open(String path) throws IOException {
        if (path.startsWith(ENV_PREFIX)) {
            String environment = path.substring(ENV_PREFIX.length());
            if ("auto".equals(environment)) {
                environment = System.getenv(ENV_VARIABLE);
            }
            if (nonNull(environment)) {
                String resource = String.format("env/%s/server.yml", environment);
                InputStream in = this.getClass().getClassLoader().getResourceAsStream(resource);
                if (nonNull(in)) {
                    return in;
                }
            }
        }

        return this.delegate.open(path);
    }
}
