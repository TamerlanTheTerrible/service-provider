package me.timur.servicesearchtelegrambot;

import java.sql.Timestamp;

/**
 * Created by Temurbek Ismoilov on 23/10/22.
 */

public class Test extends TestParent {

    public static void main(String[] args) {
        long eleven = 110000 * 8;
        long twelve = 120000 * 6;
        long thirteen = 130000;
        long three = 30000;

        System.out.println((eleven+twelve+thirteen+three)/175);
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
