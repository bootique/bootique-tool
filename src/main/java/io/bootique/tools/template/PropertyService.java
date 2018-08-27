package io.bootique.tools.template;

public interface PropertyService {

    String getProperty(String property);

    void setProperty(String property, String value);

    boolean hasProperty(String property);

}
