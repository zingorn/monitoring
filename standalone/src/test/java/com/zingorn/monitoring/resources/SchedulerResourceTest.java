package com.zingorn.monitoring.resources;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zingorn.monitoring.BaseTest;
import com.zingorn.monitoring.api.Config;
import com.zingorn.monitoring.core.Manager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@RunWith(MockitoJUnitRunner.class)
public class SchedulerResourceTest extends BaseTest {

    @Mock
    private Manager managerMock;
    private SchedulerResource resource;

    @Before
    public void setUp() throws Exception {
        resource = new SchedulerResource(managerMock);
    }

    @Test
    public void updateJobs() throws Exception {
        Config config = new Config();
        resource.updateJobs(config);
        verify(managerMock).updateJobs(config);
    }

    @Test
    public void invalidConfig() {
        Config config = new Config();
        doThrow(UncheckedIOException.class).when(managerMock).updateJobs(config);
        Response response = resource.updateJobs(config);
        assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals("{}", response.getEntity());
    }

    @Test
    public void lastRun() {
        List<Map<String, Object>> data = asList(map("a", "1"), map("b", "12"));

        when(managerMock.getLastResults()).thenReturn(data);
        Response result = resource.lastRuns();
        assertEquals(Status.OK.getStatusCode(), result.getStatus());
        Map<String, Object> entity = (Map<String, Object>) result.getEntity();
        assertEquals(data, entity.get("data"));
        assertEquals(2, entity.get("recordsTotal"));
        assertEquals(2, entity.get("recordsFiltered"));
    }
}
