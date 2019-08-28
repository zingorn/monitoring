package com.zingorn.monitoring.core;

import static com.zingorn.monitoring.api.HttpUrl.CONTENT_REQUIREMENT_KEY;
import static com.zingorn.monitoring.api.HttpUrl.HEADERS_KEY;
import static com.zingorn.monitoring.api.HttpUrl.METHOD_KEY;
import static com.zingorn.monitoring.api.HttpUrl.STATUS_KEY;
import static com.zingorn.monitoring.api.HttpUrl.URL_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import com.zingorn.monitoring.BaseTest;
import com.zingorn.monitoring.api.HttpUrlReport;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

@RunWith(MockitoJUnitRunner.class)
public class HttpUrlJobTest extends BaseTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Client clientMock;
    @Mock
    private Response responseMock;
    @Mock
    private JobExecutionContext contextMock;

    private HttpUrlJob job;

    @Before
    public void setUp() throws Exception {
        job = new HttpUrlJob();
        job.setClient(clientMock);
        when(clientMock.target(any(URI.class)).request().method(anyString()))
                .thenReturn(responseMock);
    }

    @Test
    public void execute() throws Exception {
        String host = "http://goo.gl";
        String method = "POST";
        String expr = "a\\d+";
        int status = 200;
        when(responseMock.readEntity(String.class)).thenReturn("Your Code: a72837. END");
        when(responseMock.getStatus()).thenReturn(418);
        JobDataMap jobDataMap = new JobDataMap(map(
                URL_KEY, host,
                METHOD_KEY, method,
                CONTENT_REQUIREMENT_KEY, expr,
                STATUS_KEY, status,
                HEADERS_KEY, "{\"header1\":\"value1\"}"
        ));
        when(contextMock.getMergedJobDataMap()).thenReturn(jobDataMap);

        doAnswer(inv -> {
            HttpUrlReport report = inv.getArgument(0);
            assertEquals(host, report.getUrl());
            assertEquals(method, report.getMethod());
            assertTrue(report.getContentMatched());
            assertFalse(report.getStatusMatched());
            assertFalse(report.getVerdict());
            assertNotNull(report.getLastRun());
            assertNotNull(report.getElapsedTime());

            return null;
        }).when(contextMock).setResult(any());
        job.execute(contextMock);


    }
}
