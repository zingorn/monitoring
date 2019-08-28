package com.zingorn.monitoring.resources;

import static java.util.Optional.ofNullable;

import com.zingorn.monitoring.core.ApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Abstract resource class.
 * Provides helper methods for API requests.
 */
public class BaseResource {

    protected Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Simple request wrapper.
     * When request failed then generates pretty response.
     */
    public <R> Response runOrThrow(Supplier<R> call) {
        try {
            Object result = call.get();
            return Response.status(Status.OK).entity(result).build();
        } catch (ApplicationException ex) {
            log.error("Application error", ex);
            return Response.status(ex.getStatus())
                    .entity(ofNullable(ex.getResponseEntity()).orElse("{}"))
                    .build();
        } catch (Exception ex) {
            log.error("Unexpected error", ex);
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{}").build();
        }
    }

    public <A, R> Response runOrThrow(Function<A, R> call, A arg) {
        return runOrThrow(() -> call.apply(arg));
    }

    public <A> Response runOrThrow(Consumer<A> call, A arg) {
        return runOrThrow(() -> {
            call.accept(arg);
            return null;
        });
    }

    public <A, B, R> Response runOrThrow(BiFunction<A, B, R> call, A arg1, B arg2) {
        return runOrThrow(() -> call.apply(arg1, arg2));
    }


}
