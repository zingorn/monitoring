package com.zingorn.monitoring;

import com.zingorn.monitoring.api.Config;
import com.zingorn.monitoring.api.HttpUrl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class BaseTest {

    public <T> T loadObject(String path, Class<T> clazz) throws IOException {
        InputStream file = BaseTest.class.getClassLoader()
                .getResourceAsStream(path);
        return new ObjectMapper().readValue(file, clazz);
    }

    public <T> T loadObject(String path, TypeReference<T> type) throws IOException {
        InputStream file = BaseTest.class.getClassLoader()
                .getResourceAsStream(path);
        return new ObjectMapper().readValue(file, type);
    }

    public Config loadConfig(String path) throws IOException {
        return loadObject(path, Config.class);
    }

    public <T> T getJob(Config config, int pos) {
        return (T) config.getJobs().get(pos);
    }

    public HttpUrl getHttpJob(Config config, int pos) {
        return getJob(config, pos);
    }

    public <K, V> Map<K, V> map(Object... args) {
        assert (args.length % 2) == 0;
        Map<K, V> result = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            result.put((K) args[i], (V) args[i + 1]);
        }
        return result;
    }
}
