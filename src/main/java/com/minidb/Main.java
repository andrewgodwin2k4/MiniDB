package com.minidb;
import org.fusesource.jansi.AnsiConsole;

public class Main {
    public static void main(String[] args) {

	AnsiConsole.systemInstall();
        
        StorageManager.initializeStorage();
        
        REPL repl = new REPL();
        repl.run();

	AnsiConsole.systemUninstall();

    }
}