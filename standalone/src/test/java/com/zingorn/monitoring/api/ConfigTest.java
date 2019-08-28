package com.zingorn.monitoring.api;


import static org.junit.Assert.assertEquals;

import com.zingorn.monitoring.BaseTest;

import org.junit.Test;

public class ConfigTest extends BaseTest {

    @Test
    public void name() throws Exception {
        Config config = loadConfig("valid_config.json");
        assertEquals(7, config.getJobs().size());

        assertEquals(418, getHttpJob(config, 0).getStatus().longValue());
        assertEquals(1, getHttpJob(config, 0).getHeaders().size());
    }

}
