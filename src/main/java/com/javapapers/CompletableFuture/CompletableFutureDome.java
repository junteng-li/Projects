package com.javapapers.CompletableFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lijunteng on 16/3/23.
 */
public class CompletableFutureDome {

    public CompletableFuture<String> ask() {
        final CompletableFuture<String> future = new CompletableFuture<>();
        //...
        //future.complete("42");
        return future;
    }

    public static void main(String... args) throws ExecutionException, InterruptedException, NoSuchFieldException, IllegalAccessException {
        CompletableFutureDome futureDome = new CompletableFutureDome();

        CompletableFuture<String> future = futureDome.ask();
        //future.complete("42");
        String result = future.get();
        System.out.println(result);

        ExecutorService executor = Executors.newSingleThreadExecutor();

//
//        final CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "42", executor);
//
//        CompletableFuture<Integer> f2 = f1.thenApply(Integer::parseInt);
//        CompletableFuture<Double> f3 =
//                f1.thenApply(Integer::parseInt).thenApply(r -> r * r * Math.PI);
//
//        f1.thenAcceptAsync(System.out::println, executor);
//
//        CompletableFuture<Integer> safe = f1.handle((ok, ex) -> {
//            if (ok != null) {
//                return Integer.parseInt(ok);
//            } else {
//                System.out.println("Problem"+ex);
//                return -1;
//            }
//        });

        //两个futures的转换值(thenCombine())


    }
}