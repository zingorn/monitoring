package com.zingorn.monitoring;

import com.zingorn.monitoring.core.EnvironmentConfigProvider;
import com.zingorn.monitoring.resources.HealthResource;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * Dropwizard application base class.
 */
public class UrlMonitoringApplication extends Application<UrlMonitoringConfiguration> {
    public static final String NAME = "UrlMonitoring";


    public static void main(final String[] args) throws Exception {
        new UrlMonitoringApplication().run(args);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void initialize(final Bootstrap<UrlMonitoringConfiguration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(new EnvironmentConfigProvider());
        bootstrap.addBundle(new AssetsBundle("/assets", "/", "index.html"));
    }

    @Override
    public void run(final UrlMonitoringConfiguration configuration,
                    final Environment environment) {

        // initialization DI
        UrlMonitoringModule module = new UrlMonitoringModule(configuration, environment);
        UrlMonitoringComponent component = DaggerUrlMonitoringComponent.builder()
                .urlMonitoringModule(module).build();

        // register job scheduler manager
        environment.lifecycle().manage(component.getManager());

        // register API endpoints
        environment.jersey().setUrlPattern("/api/*");
        environment.jersey().register(component.getSchedulerResource());
        environment.jersey().register(new HealthResource());
    }

}
