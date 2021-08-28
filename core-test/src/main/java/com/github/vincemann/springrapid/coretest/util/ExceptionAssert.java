package com.github.vincemann.springrapid.coretest.util;

public class ExceptionAssert {

    public static void assertTrue(boolean condition){
        if (!condition){
            System.err.println("Test failed");
            throw new IllegalArgumentException("Test failed");
        }
    }
    public static void assertFalse(boolean condition){
        if (condition){
            System.err.println("Test failed");
            throw new IllegalArgumentException("Test failed");
        }
    }



    public static void assertEquals(Object expected, Object actual){
        if (!expected.equals(actual)){
            System.err.println("Test failed");
            throw new IllegalArgumentException("Test failed");
        }
    }

    public static void assertNotEquals(Object expected, Object actual){
        if (expected.equals(actual)){
            System.err.println("Test failed");
            throw new IllegalArgumentException("Test failed");
        }
    }
}
