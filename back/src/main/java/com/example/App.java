package com.example;

public class App {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Backend started. Waiting indefinitely...");
        Thread.currentThread().join();
    }
}
