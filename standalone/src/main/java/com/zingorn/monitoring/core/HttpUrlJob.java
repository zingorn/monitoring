package com.zingorn.monitoring.core;

import static com.zingorn.monitoring.UrlMonitoringModule.MAP_TYPE;
import static com.zingorn.monitoring.UrlMonitoringModule.mapper;
import static java.util.Objects.nonNull;

import com.zingorn.monitoring.api.HttpUrl;
import com.zingorn.monitoring.api.HttpUrlReport;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.regex.Pattern;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

@Data
@Slf4j
public class HttpUrlJob extends BaseJob<HttpUrlReport> {
    public static final String JOB_LOGGER_NAME = "jobs.HttpUlrJob";
    public static final String ELAPSED_TIME = "elapsedTime";

    private Client client;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String url = getString(context, HttpUrl.URL_KEY);
        String method = getString(context, HttpUrl.METHOD_KEY);
        String contentRequirement = getString(context, HttpUrl.CONTENT_REQUIREMENT_KEY);
        Integer status = context.getMergedJobDataMap().getIntValue(HttpUrl.STATUS_KEY);

        HttpUrlReport report = createReport();
        report.setUrl(url);
        report.setMethod(method);
        report.setLastRun(Instant.now());
        try {
            URI uri = URI.create(url);
            Invocation.Builder builder = client.target(uri).request();

            applyHeaders(builder, context);
            Response response = null;
            long startTime = System.currentTimeMillis();
            try {
                response = builder.method(method);
                report.setElapsedTime(System.currentTimeMillis() - startTime);
                String body = response.readEntity(String.class);
                report.setContentMatched(Pattern.compile(contentRequirement).matcher(body).find());
                report.setStatus(response.getStatus());
                report.setStatusMatched(status.equals(response.getStatus()));
                report.setVerdict(report.getStatusMatched() && report.getContentMatched());
            } finally {
                if (nonNull(response)) {
                    response.close();
                }
                report.setElapsedTime(System.currentTimeMillis() - startTime);
            }
        } catch (ProcessingException ex) {
            // ignore
        }

        context.setResult(report);
    }

    private String getString(JobExecutionContext context, String key) {
        return context.getMergedJobDataMap().getString(key);
    }

    private void applyHeaders(Invocation.Builder builder, JobExecutionContext context) {
        String jsonHeaders = context.getMergedJobDataMap().getString(HttpUrl.HEADERS_KEY);
        try {
            Map<String, Object> headers = mapper.readValue(jsonHeaders, MAP_TYPE);
            if (nonNull(headers)) {
                headers.forEach(builder::header);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public HttpUrlReport createReport() {
        return new HttpUrlReport();
    }
}
