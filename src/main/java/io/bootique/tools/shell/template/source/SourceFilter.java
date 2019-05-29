package io.bootique.tools.shell.template.source;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Predicate;

@FunctionalInterface
public interface SourceFilter extends Predicate<Path> {

    default SourceFilter and(SourceFilter other) {
        Objects.requireNonNull(other);
        return (t) -> test(t) && other.test(t);
    }

    default SourceFilter or(SourceFilter other) {
        Objects.requireNonNull(other);
        return (t) -> test(t) || other.test(t);
    }

    default SourceFilter negate() {
        return (t) -> !test(t);
    }

}
