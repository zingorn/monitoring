package com.zingorn.monitoring.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.Instant;

@Data
public class JobReport {

    /**
     * Final result job status.
     * <p>
     * True   - job passed and job result is valid.
     * False  - job passed and job result is invalid.
     * Null   - job not passed (i.e. request failed with an exception).
     */
    Boolean verdict;

    /**
     * Elapsed job time.
     * <p>
     * Note: Depends on job logic and measured in job.
     */
    Long elapsedTime;

    /**
     * When job was run at the last time.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss",
            timezone = "UTC")
    Instant lastRun;
}
