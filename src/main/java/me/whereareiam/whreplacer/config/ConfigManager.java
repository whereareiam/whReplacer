package me.whereareiam.whreplacer.config;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    private final Path moduleFolder;
    private final Path configPath;
    private Map<String, Object> configData;

    public ConfigManager() {
        moduleFolder = Paths.get("modules/whReplacer");
        configPath = moduleFolder.resolve("config.yml");
    }

    public void loadConfig() throws IOException {
        ensureDirectoriesExist();
        copyConfigIfMissing();

        Yaml yaml = new Yaml();
        try (InputStream is = Files.newInputStream(configPath)) {
            configData = yaml.load(is);
        }
    }

    private void ensureDirectoriesExist() throws IOException {
        if (!Files.exists(moduleFolder)) {
            Files.createDirectories(moduleFolder);
        }
    }

    private void copyConfigIfMissing() throws IOException {
        if (!Files.exists(configPath)) {
            try (InputStream is = getClass().getResourceAsStream("/config.yml")) {
                if (is == null) {
                    throw new RuntimeException("Cannot find resource /config.yml");
                }
                Files.copy(is, configPath, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    public String getString(String key) {
        Object value = getValueForKey(key);
        return value instanceof String && !((String) value).isEmpty() ? (String) value : key;
    }

    public List<String> getStringList(String key) {
        Object value = getValueForKey(key);
        return value instanceof List<?> ? (List<String>) value : Collections.emptyList();
    }

    public int getInt(String key) {
        Object value = getValueForKey(key);
        return ((Number) value).intValue();
    }

    public double getDouble(String key) {
        Object value = getValueForKey(key);
        return ((Number) value).doubleValue();
    }

    private Object getValueForKey(String key) {
        String[] keys = key.split("\\.");
        Map<String, Object> current = configData;
        for (String k : keys) {
            Object value = current.get(k);
            if (value instanceof Map) {
                current = (Map<String, Object>) value;
            } else {
                return value;
            }
        }
        return null;
    }

    public Path getConfigPath() {
        return configPath;
    }
}
