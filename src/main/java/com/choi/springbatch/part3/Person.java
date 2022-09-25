package com.choi.springbatch.part3;

import lombok.Getter;

@Getter
public class Person {

    private Integer id;
    private String name;
    private String age;
    private String address;

    public Person(Integer id, String name, String age, String address) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.address = address;
    }

}
