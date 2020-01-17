package io.bootique.tools.shell;

import java.util.function.Function;

public class ConfigParameter<T> {
    private final String name;
    private final T defaultValue;
    private final Function<T, String> toStringConverter;
    private final Function<String, T> fromStringConverter;

    ConfigParameter(String name, T defaultValue, Function<T, String> toStringConverter, Function<String, T> fromStringConverter) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.toStringConverter = toStringConverter;
        this.fromStringConverter = fromStringConverter;
    }

    public String getName() {
        return name;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public String valueToString(T value) {
        return toStringConverter.apply(value);
    }

    public T valueFromString(String value) {
        return fromStringConverter.apply(value);
    }
}
