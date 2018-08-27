package io.bootique.tools.template.module;

import java.util.Map;

import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.tools.template.DefaultPropertyService;
import io.bootique.tools.template.PropertyService;

@BQConfig
public class PropertyServiceFactory {

    private Map<String, String> properties;

    public PropertyService createPropertyService() {
        PropertyService propertyService = new DefaultPropertyService();
        properties.forEach(propertyService::setProperty);
        return propertyService;
    }

    @BQConfigProperty("Template properties that will be used")
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
