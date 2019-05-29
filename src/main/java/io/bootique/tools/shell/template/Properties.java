package io.bootique.tools.shell.template;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Properties {

    private final Map<String, Object> props;

    public static Builder builder() {
        return new Builder();
    }

    private Properties(Map<String, Object> props) {
        this.props = props;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        return (T)props.get(name);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name, T defaultValue) {
        return (T)props.getOrDefault(name, defaultValue);
    }

    public static class Builder {

        private final Map<String, Object> props = new HashMap<>();

        public Builder with(String name, Object value) {
            props.put(name, value);
            return this;
        }

        public Properties build() {
            return new Properties(Collections.unmodifiableMap(props));
        }
    }

}
