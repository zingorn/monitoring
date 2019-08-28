package com.zingorn.monitoring.core;

import static com.zingorn.monitoring.api.HttpUrl.URL_KEY;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zingorn.monitoring.BaseTest;
import com.zingorn.monitoring.UrlMonitoringConfiguration;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.triggers.CronTriggerImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

@RunWith(MockitoJUnitRunner.class)
public class ManagerTest extends BaseTest {
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Scheduler schedulerMock;
    @Mock
    private UrlMonitoringConfiguration configurationMock;
    @Mock
    private JobFactory jobFactoryMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private JobResultListener jobResultListenerMock;
    private Manager manager;

    @Before
    public void setUp() throws Exception {
        manager = new Manager(configurationMock, () -> schedulerMock,
                jobFactoryMock, jobResultListenerMock);
    }

    @Test
    public void updateJobs() throws Exception {
        List<String> urls = new ArrayList<>();
        List<String> periods = new ArrayList<>();
        when(schedulerMock.scheduleJob(any(), any())).thenAnswer(inv -> {
            JobDetail details = inv.getArgument(0);
            CronTriggerImpl trigger = inv.getArgument(1);
            urls.add(details.getJobDataMap().getString(URL_KEY));
            periods.add(trigger.getCronExpression());
            return null;
        });
        manager.start();
        manager.updateJobs(loadConfig("valid_config.json"));
        manager.stop();
        verify(schedulerMock).setJobFactory(jobFactoryMock);
        verify(schedulerMock.getListenerManager()).addJobListener(jobResultListenerMock);
        verify(schedulerMock).start();
        verify(schedulerMock).clear();
        verify(jobResultListenerMock.getResultsCache()).invalidateAll();
        verify(schedulerMock, times(7)).scheduleJob(any(JobDetail.class), any(Trigger.class));
        verify(schedulerMock).shutdown(true);

        List<String> expectedUrls = asList("http://localhost:8080/api/health",
                "https://www.facebook.com/",
                "https://identity.flickr.com/login?redir=https%3A%2F%2Fwww.flickr.com%2F",
                "https://identity.flickr.com/", "https://www.google.com/search?q=perpetuum+mobile",
                "http://gmail.com", "https://www.domain.co.uk/");
        assertEquals(expectedUrls, urls);

        List<String> expectedCrons = asList("*/30 * * * * ?", "*/20 * * * * ?", "*/10 * * * * ?",
                "*/10 * * * * ?", "*/10 * * * * ?", "0 */2 * * * ?", "*/30 * * * * ?");
        assertEquals(expectedCrons, periods);
    }

    @Test
    public void lastRuns() throws Exception {
        ConcurrentMap<String, String> values = loadObject("reports.json",
                new TypeReference<ConcurrentMap<String, String>>() {
                });

        when(jobResultListenerMock.getResultsCache().asMap()).thenReturn(values);
        List<Map<String, Object>> result = manager.getLastResults();
        assertEquals(6, result.size());
        verify(jobResultListenerMock.getResultsCache()).asMap();
    }
}
