package io.bootique.tools;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import io.bootique.Bootique;

public class Application {

    public static void main(String[] args) {
        // TODO: can we detect this in native mode?
        if(System.getProperty("sun.arch.data.model") == null) {
            System.setProperty("sun.arch.data.model", "64");
        }

        // turn JLine logging off
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(Level.OFF);

        Bootique
                .app(args)
                .autoLoadModules()
                .exec()
                .exit();
    }
}