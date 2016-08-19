package com.javapapers.stream;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lijunteng on 16/4/5.
 */
public class StreamTest {

    public static void main(String... ages) {
//        Integer[] integers = {1,2,3,4,5,6,7,8,9,0};
//        List<Integer> numbers = Arrays.asList(integers);
//        numbers.stream().filter((x)-> x % 2 ==0).map((x) -> x * x).forEach(System.out::println);

//        Optional<String> optional = Optional.of("is");
//
//        optional.ifPresent((s) -> System.out.println(s.charAt(0)));

//        Optional<String> reduced = numbers.parallelStream().sorted().map(String::valueOf).reduce((x, y) -> x + "#" + y);
//
//        reduced.ifPresent(System.out::println);

        Map<String, Integer> map = new HashMap<>();

        for(int i = 0; i < 10; i++){
            map.putIfAbsent("key"+i, i);
        }
        map.forEach((s, i) -> System.out.println("key:" + s +"," + "val:" + i));

        /**
         * 如果key存在就替换value  返回替换后的value
         * 如果key不存在就不替换 返回null
         */
        //Integer result = map.computeIfPresent("key20", (s, i) -> 10);

        /**
         *  如果key存在就不替换value  返回原value
         *  如果key不存在就put一组新的key value
         */
        Integer result = map.computeIfAbsent("key100", (s) -> Integer.parseInt("100"));
        System.out.println(result);

        System.out.println("-------------------------------");
//        boolean b = map.containsValue(1);
//        System.out.println(b);

        System.out.println("-------------------------------");
        System.out.println(map.getOrDefault("key10", 10000));

        /**
         * Merge做的事情是如果键名不存在则插入，否则则对原键对应的值做合并操作并重新插入到map中。
         */
        map.merge("key100", 111, (integer, integer2) -> integer + integer2);

        map.forEach((s, i) -> System.out.println("key:" + s + "," + "val:" + i));
    }
}
