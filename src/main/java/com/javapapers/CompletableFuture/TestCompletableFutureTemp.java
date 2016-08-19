package com.javapapers.CompletableFuture;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

/**
 * Created by lijunteng on 16/3/30.
 */
public class TestCompletableFutureTemp {

    private static final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();


    public static <T> CompletableFuture<T> failAfter(Duration duration) {
        final CompletableFuture<T> promise = new CompletableFuture<>();
        System.out.println(duration.toMillis());
        scheduler.schedule(() -> {
            final TimeoutException ex = new TimeoutException("Timeout after " + duration);
            return promise.completeExceptionally(ex);
        }, duration.toMillis(), TimeUnit.MILLISECONDS);
        return promise;
    }

    public static <T> CompletableFuture<T> within(CompletableFuture<T> future, Duration duration) {
        final CompletableFuture<T> timeout = failAfter(duration);
        return future.applyToEither(timeout, Function.identity());
    }
    @org.junit.Test
    public void test() throws ExecutionException, InterruptedException {
        final CompletableFuture<Integer> responseFuture = within(
                TestCompletableFuture.task1(1), Duration.ofMillis(1950));
        responseFuture
                .thenAccept(this::send)
                .exceptionally(throwable -> {
                    //log.error("Unrecoverable error", throwable);
                    System.out.println("error!");
                    return null;
                });
        System.out.println(responseFuture.get());
    }

    public void send(int i){
        System.out.println("sending!:" + i);

    }

}
