package com.zingorn.monitoring.api;

import lombok.Data;

import java.util.List;

/**
 * Application config.
 */
@Data
public class Config {

    /**
     * List of jobs.
     */
    List<BaseJobConfig> jobs;
}
