package com.javapapers.CompletableFuture;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by lijunteng on 16/3/31.
 */
public class Main {
    private static Random rand = new Random();
    private static long t = System.currentTimeMillis();
    static int getMoreData() {
        System.out.println("begin to start compute");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("end to start compute. passed " + (System.currentTimeMillis() - t)/1000 + " seconds");
        return rand.nextInt(1000);
    }

    /**
     * 计算结果完成时的处理
     */
    //当CompletableFuture的计算结果完成，
    // 或者抛出异常的时候，我们可以执行特定的Action。
    //注意这几个方法都会返回CompletableFuture，
    // 当Action执行完毕后它的结果返回原始的CompletableFuture的计算结果或者返回异常
    @org.junit.Test
    public void mainTest() throws Exception {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(Main::getMoreData);
        CompletableFuture<Integer> f = future.whenComplete((integer, throwable) -> {
            System.out.println(integer);
            //throwable.printStackTrace();
        });
        System.out.println(f.get());
    }

    //下面一组方法虽然也返回CompletableFuture对象，
    // 但是对象的值和原来的CompletableFuture计算的值不同。
    // 当原先的CompletableFuture的值计算完成或者抛出异常的时候，
    // 会触发这个CompletableFuture对象的计算，结果由BiFunction参数计算而得。
    // 因此这组方法兼有whenComplete和转换的两个功能。
    @org.junit.Test
    public void mainTest1() throws Exception{
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            throw new RuntimeException("future error!");
            try {
                Thread.sleep(999);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            return 0;
        });

        CompletableFuture<String> f = future.handle((v, e) -> {
            //e.printStackTrace();
            System.out.println("System.out.println v:" + v);
            //System.out.println("System.out.println null != v:" + (null != v));
//            if(null != v){
//                throw new RuntimeException("error!");
//            }
            return String.valueOf(v);
        }).exceptionally((e) -> {
            System.out.println("System.out.println:" + e.getMessage());
            //e.printStackTrace();
            return "";
        });

        System.out.println(f.get(1, TimeUnit.SECONDS));
    }

    /**
     * 转换
     */
    //这一组函数的功能是当原来的CompletableFuture计算完后，
    // 将结果传递给函数fn，将fn的结果作为新的CompletableFuture计算结果。
    // 因此它的功能相当于将CompletableFuture<T>转换成CompletableFuture<U>。
    @org.junit.Test
    public void mainTest2() throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> 100);

        CompletableFuture<String> f = future.thenApplyAsync((v) -> v * 10).thenApply(String::valueOf);
        System.out.println(f.get());
    }

    /**
     * 纯消费(执行Action)
     */
    //只有输入 没有输出
    @org.junit.Test
    public void test() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> 100);
        CompletableFuture<Void> f =  future.thenAccept(System.out::println);
        System.out.println(f.get());
    }

    //thenAcceptBoth以及相关方法提供了类似的功能，
    // 当两个CompletionStage都正常完成计算的时候，
    // 就会执行提供的action，它用来组合另外一个异步的结果。
    @org.junit.Test
    public void test1() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            //throw new RuntimeException("11");
            return 100;
        });
        CompletableFuture<Void> f = future.thenAcceptBoth(CompletableFuture.completedFuture(10), (x, y) -> System.out.println(x * y));
        System.out.println(f.get());
    }

    //runAfterBoth是当两个CompletionStage都正常完成计算的时候,
    // 执行一个Runnable，这个Runnable并不使用计算的结果。
    @org.junit.Test
    public void test2() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(()->100);
        CompletableFuture<Void> f = future.runAfterBoth(CompletableFuture.completedFuture(10), () -> System.out.println("finish!"));
        System.out.println(f.get());
    }

    //下面一组方法当计算完成的时候会执行一个Runnable,
    // 与thenAccept不同，Runnable并不使用CompletableFuture计算的结果。
    @org.junit.Test
    public void test3() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "好");
        CompletableFuture<Void> f = future.thenRun(()->System.out.println("finish!"));
        System.out.println(future.get());
    }

    /**
     * 组合
     */
    //这一组方法接受一个Function作为参数，
    // 这个Function的输入是当前的CompletableFuture的计算值，
    // 返回结果将是一个新的CompletableFuture，
    // 这个新的CompletableFuture会组合原来的CompletableFuture和函数返回的CompletableFuture。
    @org.junit.Test
    public void test4() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(()-> "11");
        CompletableFuture<Integer> f = future.thenCompose(new Function<String, CompletableFuture<Integer>>() {
            @Override
            public CompletableFuture<Integer> apply(String s) {
                return CompletableFuture.supplyAsync(()->Integer.parseInt(s) + 11);
            }
        });
        System.out.println(f.get());
    }

    /**
     * 合并
     */
    //而下面的一组方法thenCombine用来复合另外一个CompletionStage的结果
    //两个CompletionStage是并行执行的，它们之间并没有先后依赖顺序，
    //other并不会等待先前的CompletableFuture执行完毕后再执行。

    //其实从功能上来讲,它们的功能更类似thenAcceptBoth，
    // 只不过thenAcceptBoth是纯消费，它的函数参数没有返回值，
    // 而thenCombine的函数参数fn有返回值。
    @org.junit.Test
    public void test5() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(()->{
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 100;
        });
        CompletableFuture<String> future1 = CompletableFuture.completedFuture("11");

        CompletableFuture<String> result = future.thenCombine(future1,(i, s) -> i+s);
        System.out.println(future1.get());
        System.out.println(result.get());
    }

    /**
     * Either
     */
     //acceptEither方法是当任意一个CompletionStage完成的时候，
    // action这个消费者就会被执行。这个方法返回CompletableFuture<Void>
    @org.junit.Test
    public void test6() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(()->{

            try {
                int second = 1000+rand.nextInt(2000);
                System.out.println("future1 sleep:" + second + "s");
                Thread.sleep(second);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "future1";
        });
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(()->{

            try {
                int second = 1000+rand.nextInt(2000);
                System.out.println("future2 sleep:" + second + "s");
                Thread.sleep(second);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "future2";
        });
        CompletableFuture<Void> result = future1.acceptEither(future2, System.out::println);
        System.out.println(result.get());
    }


    //applyToEither方法是当任意一个CompletionStage完成的时候，
    // fn会被执行，
    // 它的返回值会当作新的CompletableFuture<U>的计算结果。
    @org.junit.Test
    public void test7() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(()->{

            try {
                int second = rand.nextInt(2000) + 1000;
                System.out.println("future1 sleep:" + second + "s");
                Thread.sleep(second);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 100;
        });

        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(()->{

            try {
                int second = rand.nextInt(2000) + 1000;
                System.out.println("future2 sleep:" + second + "s");
                Thread.sleep(second);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 200;
        });
        CompletableFuture<String> result = future1.applyToEitherAsync(future2, integer -> {
            System.out.println(integer);
            return String.valueOf(integer);
        });

        System.out.println(result.get());
    }

    /**
     * 辅助方法 allOf 和 anyOf
     *
     * allOf方法是当所有的CompletableFuture都执行完后执行计算。
     * anyOf方法是当任意一个CompletableFuture执行完后就会执行计算，计算的结果相同。
     * 下面的代码运行结果有时是100,有时是"abc"。但是anyOf和applyToEither不同。
     * anyOf接受任意多的CompletableFuture但是applyToEither只是判断两个CompletableFuture,
     * anyOf返回值的计算结果是参数中其中一个CompletableFuture的计算结果，
     * applyToEither返回值的计算结果却是要经过fn处理的。当然还有静态方法的区别，线程池的选择等。
     */
    @org.junit.Test
    public void test8() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(()->{

            try {
                int second = rand.nextInt(2000) + 1000;
                System.out.println("future1 sleep!" + second + "s");
                Thread.sleep(second);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "future1";
        });

        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(()->{

            try {
                int second = rand.nextInt(2000) + 1000;
                System.out.println("future2 sleep!" + second + "s");
                Thread.sleep(second);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 200;
        });

        CompletableFuture<Object> result = CompletableFuture.anyOf(future1,future2);
        System.out.println(result.get());
    }


    public static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> futures) {
        CompletableFuture<Void> allDoneFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
        return allDoneFuture.thenApply(v -> futures.stream().map(CompletableFuture::join).collect(Collectors.<T>toList()));
    }
    public static <T> CompletableFuture<List<T>> sequence(Stream<CompletableFuture<T>> futures) {
        List<CompletableFuture<T>> futureList = futures.filter(f -> f != null).collect(Collectors.toList());
        return sequence(futureList);
    }
}