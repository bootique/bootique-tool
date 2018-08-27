package io.bootique.tools.template;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultPropertyService implements PropertyService {

    private final Map<String, String> propertyMap = new ConcurrentHashMap<>();

    @Override
    public String getProperty(String property) {
        return propertyMap.get(property);
    }

    @Override
    public void setProperty(String property, String value) {
        propertyMap.put(property, value);
    }

    @Override
    public boolean hasProperty(String property) {
        return propertyMap.containsKey(property);
    }
}
