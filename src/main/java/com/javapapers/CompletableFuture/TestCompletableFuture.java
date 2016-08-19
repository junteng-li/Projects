package com.javapapers.CompletableFuture;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;

/**
 * Created by lijunteng on 16/3/23.
 */
public class TestCompletableFuture {

    public static CompletableFuture<Integer> task1(int input) {
        return TimedEventSupport.delayedSuccess(1, input + 1);
    }
    private static CompletableFuture<Integer> task2(int input) {
        return TimedEventSupport.delayedSuccess(2, input + 2);
    }
    private static CompletableFuture<Integer> task3(int input) {
        return TimedEventSupport.delayedSuccess(2, input + 3);
    }
    private static CompletableFuture<Integer> task4(int input) {
        return TimedEventSupport.delayedSuccess(1, input + 4);
    }

    //阻塞等待任务执行
    @org.junit.Test
    public void runBlocking() throws ExecutionException, InterruptedException {
        Integer i1 = task1(1).join();

        CompletableFuture<Integer> future2 = task2(i1);

        CompletableFuture<Integer> future3 = task3(i1);

        Integer result = task4(future2.join() + future3.join()).join();
        CompletableFuture<Integer> re = CompletableFuture.completedFuture(result);
        System.out.println(re.get());
    }

    //非阻塞的合成和组合
    @org.junit.Test
    public void runNonblocking() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> re = task1(1).thenCompose(i1 -> ((CompletableFuture<Integer>)task2(i1)
                .thenCombine(task3(i1), (i2,i3) -> i2+i3)))
                .thenCompose(TestCompletableFuture::task4);

//        CompletableFuture<Integer> re1 = task1(1).thenCompose(new Function<Integer, CompletionStage<Integer>>() {
//            @Override
//            public CompletionStage<Integer> apply(Integer i1) {
//                return (CompletableFuture<Integer>)task2(i1).thenCombine(task3(i1), new BiFunction<Integer, Integer, Integer>() {
//                    @Override
//                    public Integer apply(Integer i2, Integer i3) {
//                        return i2 + i3;
//                    }
//                });
//            }
//        }).thenCompose(new Function<Integer, CompletionStage<Integer>>() {
//            @Override
//            public CompletionStage<Integer> apply(Integer integer) {
//                return task4(integer);
//            }
//        });

        System.out.println(re.get());
    }

    //重构后的非阻塞的合成和组合
    public static CompletableFuture<Integer> runTask2and3(Integer i1) {
        CompletableFuture<Integer> task2 = task2(i1);
        CompletableFuture<Integer> task3 = task3(i1);
        BiFunction<Integer, Integer, Integer> sum = (a, b) -> a + b;
        return task3.thenCombine(task2, sum);
    }
    @org.junit.Test
    public void runNonblockingAlt() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> task1 = task1(1);
        CompletableFuture<Integer> comp123 = task1.thenCompose(TestCompletableFuture::runTask2and3);
        CompletableFuture<Integer> re = comp123.thenCompose(TestCompletableFuture::task4);
        System.out.println(re.get());

        re.exceptionally(throwable -> {
            //log.error("Unrecoverable error", throwable);
            return null;
        });
    }

    //异常
    public static CompletableFuture<Integer> task5(int input) {
        return TimedEventSupport.delayedFailure(1, new IllegalArgumentException("This won't work!"));
    }
    @org.junit.Test
    public void runBlockingException() throws ExecutionException, InterruptedException {
        try {
            Integer i1 = task1(1).join();
            CompletableFuture<Integer> future2 = task2(i1);
            CompletableFuture<Integer> future3 = task3(i1);
            Integer result = task5(future2.join() + future3.join()).join();
            CompletableFuture.completedFuture(result);
        } catch (CompletionException e) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(e.getCause());
            System.out.println(result.get());
        }
    }
}


class TimedEventSupport {
    private static final Timer timer = new Timer();

    /**
     * Build a future to return the value after a delay.
     *
     * @param delay
     * @param value
     * @return future
     */
    public static <T> CompletableFuture<T> delayedSuccess( int delay,  T value) {
        CompletableFuture<T> future = new CompletableFuture<T>();
        TimerTask task = new TimerTask() {
            public void run() {
                try {
                    Thread.sleep(1000);
                    System.out.println("sleep!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("run delay :" + delay);
                future.complete(value);
            }
        };
        timer.schedule(task, delay * 1000);
        return future;
    }

    /**
     * Build a future to return a throwable after a delay.
     *
     * @param delay
     * @param t
     * @return future
     */
    public static <T> CompletableFuture<T> delayedFailure(int delay, final Throwable t) {
        CompletableFuture<T> future = new CompletableFuture<T>();
        TimerTask task = new TimerTask() {
            public void run() {
                future.completeExceptionally(t);
            }
        };
        timer.schedule(task, delay * 1000);
        return future;
    }
}
