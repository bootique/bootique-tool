package io.bootique.tools.shell.template;

import java.io.InputStream;


public interface ResourceLoader {
    default InputStream getResourceAsStream(String path){
        return getClass().getClassLoader().getResourceAsStream(path);
    }
}
