package com.zingorn.monitoring.api;

import static java.util.Objects.isNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

import java.util.Map;

/**
 * Describes http url checker job config.
 */
@Value
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class HttpUrl extends BaseJobConfig {
    public static final String URL_KEY = "url";
    public static final String CONTENT_REQUIREMENT_KEY = "contentRequirement";
    public static final String METHOD_KEY = "method";
    public static final String HEADERS_KEY = "headers";
    public static final String STATUS_KEY = "status";

    public enum Method {
        GET, POST, HEAD
    }

    /**
     * Request method.
     * Note: supports only GET, POST and HEAD.
     */
    Method method;

    /**
     * Expected response status code;
     */
    Integer status;

    /**
     * Tested url.
     */
    @NonNull
    String url;

    /**
     * Headers list for test request.
     */
    Map<String, String> headers;

    /**
     * Expected content substring or regular expression.
     * <p>
     * Example:
     * - placeholder="value"
     * - value=\d+
     */
    @NonNull
    String contentRequirement;

    public HttpUrl(
            @JsonProperty("method") Method method,
            @JsonProperty("url") @NonNull String url,
            @JsonProperty("headers") Map<String, String> headers,
            @JsonProperty("contentRequirement") @NonNull String contentRequirement,
            @JsonProperty("period") @NonNull String period,
            @JsonProperty("status") Integer status) {
        super(period);
        this.method = isNull(method) ? Method.GET : method;
        this.url = url;
        this.headers = headers;
        this.contentRequirement = contentRequirement;
        this.status = status;
    }
}
