package com.minidb;

public class Main {
    public static void main(String[] args) {
        
        StorageManager.initializeStorage();
        
        REPL repl = new REPL();
        repl.run();

    }
}