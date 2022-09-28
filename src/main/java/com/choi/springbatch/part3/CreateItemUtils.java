package com.choi.springbatch.part3;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CreateItemUtils {

    public static List<Person> nonIdItem(int index) {
        return IntStream.range(0, index)
                .mapToObj(i -> new Person( "test name" + i, "test age", "test address"))
                .collect(Collectors.toList());
    }

    public static List<Person> idItem(int index) {
        return IntStream.range(0, index)
                .mapToObj(i -> new Person(i + 1, "test name" + i, "test age", "test address"))
                .collect(Collectors.toList());
    }

}
