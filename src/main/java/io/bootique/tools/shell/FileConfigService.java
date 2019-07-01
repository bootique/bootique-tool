package io.bootique.tools.shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;

public class FileConfigService implements ConfigService {

    @Inject
    private Shell shell;

    private final Path configFile;

    private final Map<String, String> storage;

    @Inject
    public FileConfigService(@ConfigDir Path configDirectory) {
        configFile = configDirectory.resolve("bq.config");
        storage = new HashMap<>();
        if(Files.exists(configFile)) {
            if(Files.isReadable(configFile)) {
                try {
                    Files.readAllLines(configFile)
                            .forEach(this::parseLine);
                } catch (IOException ex) {
                    shell.println("Unable to read config file");
                    shell.println(ex);
                }
            }
        }
    }

    private void parseLine(String line) {
        String[] values = line.split("=");
        storage.put(values[0], values[1]);
    }

    private String createLine(Map.Entry<String, String> entry) {
        return entry.getKey() + '=' + entry.getValue();
    }

    @Override
    public void set(String param, String value) {
        storage.put(param, value);
        List<String> lines = storage.entrySet().stream()
                .map(this::createLine)
                .collect(Collectors.toList());
        try {
            Files.write(configFile, lines,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (IOException ex) {
            shell.println("Unable to write config file");
            shell.println(ex);
        }
    }

    @Override
    public String get(String param) {
        return storage.get(param);
    }

    @Override
    public String get(String param, String defaultValue) {
        return storage.getOrDefault(param, defaultValue);
    }
}
