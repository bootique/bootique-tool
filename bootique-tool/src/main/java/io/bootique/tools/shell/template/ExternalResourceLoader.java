package io.bootique.tools.shell.template;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public interface ExternalResourceLoader extends ResourceLoader{
    default InputStream getResourceAsStream(String path){
        try {
            return new FileInputStream(path);
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}
