package com.javapapers.jvmTest;

/**
 * Created by lijunteng on 16/3/22.
 */
public class SubClass extends SuperClass {
    static {
        System.out.println("SubClass init");
    }

    static int a;

    public SubClass() {
        System.out.println("init SubClass");
    }
}

class NotInitialization {
    public static void main(String[] args) {
        System.out.println(SubClass.value);
    }
}