package me.timur.servicesearchtelegrambot;

/**
 * Created by Temurbek Ismoilov on 23/10/22.
 */

public class Test extends TestParent {

    public static void main(String[] args) {
        new TestParent().outerMethod();
    }
}

class TestParent {

    public void outerMethod() {
        try {
            System.out.println("outer method");
            innerMethod();
        } finally {
            System.out.println("finally block");
        }
    }

    private void innerMethod() {
        System.out.println("inner method");
        throw new RuntimeException("smth");
    }
}
