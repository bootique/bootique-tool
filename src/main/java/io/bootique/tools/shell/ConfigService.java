package io.bootique.tools.shell;

public interface ConfigService {

    String TOOLCHAIN = "toolchain";
    String BQ_VERSION = "bq-version";
    String GROUP_ID = "group-id";

    void set(String param, String value);

    String get(String param);

    String get(String param, String defaultValue);

}
