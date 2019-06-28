package io.bootique.tools;

import io.bootique.Bootique;

public class Application {

    public static void main(String[] args) {
        // TODO: can we detect this in native mode?
        if(System.getProperty("sun.arch.data.model") == null) {
            System.setProperty("sun.arch.data.model", "64");
        }

        Bootique
                .app(args)
                .autoLoadModules()
                .exec()
                .exit();
    }
}