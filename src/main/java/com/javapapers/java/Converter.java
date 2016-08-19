package com.javapapers.java;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by lijunteng on 16/3/10.
 */
interface Converter<F, T> {
    T convert(F from);
}

class testConverter{
    public static void main(String... args){
//        Converter<String, Integer> converter = Integer::valueOf;
//        Integer converted = converter.convert("123");
//        System.out.println(converted);    // 123
//
          String something = "fds";
//
//        Converter<String, Boolean> converted1 = something::startsWith;
//        boolean b = converted1.convert("Java");
//        System.out.println(b);    // "J"

        Predicate<String> predicate = (s) -> s.length() > 0;

        predicate.test("foo");              // true
        predicate.negate().test("foo");     // false

        Predicate<Boolean> nonNull = Objects::nonNull;
        System.out.println("nonNull:" + nonNull.test(true));

        Predicate<Boolean> isNull = Objects::isNull;
        System.out.println("isNull:" + isNull.test(true));

        Predicate<String> isEmpty = String::isEmpty;
        System.out.println("isEmpty:" + isEmpty);

        Predicate<String> isNotEmpty = isEmpty.negate();
        System.out.println("isNotEmpty:" + isNotEmpty);

        Predicate<String> or = isEmpty.or(nonNull::equals);
        System.out.println("or:" + or);

        Predicate<Integer> bi = (t) -> t == 11;
        System.out.println("bi:" + bi);
        Predicate<Integer> pi = (se) -> se == 1;
        System.out.println("pi:" + pi);

        Function<String, String> strInt = (s) -> s.substring(3);
        System.out.println("strInt:" + strInt);
        Function<String, Integer> toInteger = Integer::valueOf;
        Function<String, String> backToString = toInteger.andThen(String::valueOf);

        backToString.apply("123");

        Function<String, Boolean> backToString1 = toInteger.andThen(something::equals);


        Supplier<String> supplierStr = "123"::toString;

        Comparator<String> comparator = (o1, o2) -> {
            if(o1.hashCode() >= o2.hashCode()){
                return 1;
            }else if(o1.hashCode() == o2.hashCode()){
                return 0;
            }else {
                return -1;
            }
        };

        Optional<String> optional = Optional.of("bam");

        optional.isPresent();           // true
        optional.get();                 // "bam"
        optional.orElse("fallback");    // "bam"
        System.out.println(optional.orElse(null));
        optional.ifPresent((s) -> System.out.println(s.charAt(0)));


        List<String> stringCollection = new ArrayList<>();
        stringCollection.add("ddd2");
        stringCollection.add("aaa2");
        stringCollection.add("bbb1");
        stringCollection.add("aaa1");
        stringCollection.add("bbb3");
        stringCollection.add("ccc");
        stringCollection.add("bbb2");
        stringCollection.add("ddd1");

        stringCollection.stream().filter(s -> s.startsWith("a")).forEach(System.out::println);

        stringCollection.stream().filter(s -> s.startsWith("a")).sorted().forEach(System.out::println);

        System.out.println(stringCollection);

        stringCollection.stream().map(String::toUpperCase).sorted(String::compareTo).forEach(System.out::println);


        boolean anyStartsWithA = stringCollection.stream().anyMatch(s -> s.startsWith("a"));
        System.out.println(anyStartsWithA);

        boolean allStartWithA = stringCollection.stream().allMatch(s -> s.startsWith("a"));
        System.out.println(allStartWithA);

        boolean noneStartWithA = stringCollection.stream().noneMatch(s -> s.startsWith("z"));
        System.out.println(noneStartWithA);

        long startWithB = stringCollection.stream().filter((s) -> s.startsWith("b")).count();
        System.out.println(startWithB);

        Optional<String> reduce = stringCollection.stream().sorted().reduce((s1, s2) -> s1 + "#" + s2);
        System.out.println(reduce.get());
        reduce.ifPresent(System.out::println);

        //串行
        int max = 1000000;
        List<String> values = new ArrayList<>();
        for (int i = 0; i < max; i++){
            String s = UUID.randomUUID().toString();
            s = s.substring(0,8)+s.substring(9,13)+s.substring(14,18)+s.substring(19,23)+s.substring(24);
            values.add(s);
        }

        long t0 = System.nanoTime();
        //串行
        Long count = values.stream().sorted().count();
        //并行
        //Long count = values.parallelStream().sorted().count();

        System.out.println(count);

        long t1 = System.nanoTime();

        long millis = TimeUnit.NANOSECONDS.toMillis(t1 - t0);

        System.out.println(String.format("sequential sort took: %d ms", millis));

        Map<Integer, String> map = new HashMap<>();

        for(int i = 0; i < 10 ; i++){
            map.putIfAbsent(i, "val" + i);
        }
        map.computeIfPresent(3, (key, s) -> key + s);
        map.get(3); //val33

        map.computeIfPresent(9, (key, s) -> null);
        map.get(9); // null

        map.computeIfAbsent(23, num -> "val" + num);
        map.containsKey(23);//true

        map.computeIfAbsent(3, integer -> "bam");
        map.get(3); //val33

        map.remove(3, "val3");
        map.get(3); // val33

        map.remove(3, "val33");
        map.get(3); // null

        map.getOrDefault(42, "not found!");

        map.merge(9, "val9", String::concat);
        map.get(9);//val9

        map.merge(9, "concat", String :: concat);
        map.get(9);//val9concat
    }
}

