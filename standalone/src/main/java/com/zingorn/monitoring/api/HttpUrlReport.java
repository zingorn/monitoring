package com.zingorn.monitoring.api;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Http url checker job report class.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HttpUrlReport extends JobReport {

    /**
     * Request method (i.e. GET, POST...)
     */
    String method;

    /**
     * Url string.
     */
    String url;

    /**
     * Actual response status code.
     */
    Integer status;

    /**
     * True if response body satisfies {@link HttpUrl#contentRequirement}
     */
    Boolean contentMatched;

    /**
     * True if response body satisfies {@link HttpUrl#status}
     */
    Boolean statusMatched;
}
