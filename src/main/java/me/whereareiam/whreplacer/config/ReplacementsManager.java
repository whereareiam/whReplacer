package me.whereareiam.whreplacer.config;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class ReplacementsManager {
    private static final String DEFAULT_REPLACEMENTS_FILE_NAME = "default.yml";
    private final Path replacementsFolder;

    public ReplacementsManager() {
        replacementsFolder = Paths.get("modules/whReplacer/replacements");
    }

    public void loadReplacements() throws IOException {
        if (!Files.exists(replacementsFolder)) {
            Files.createDirectories(replacementsFolder);
            createDefaultReplacementFile();
        }
    }

    private void createDefaultReplacementFile() throws IOException {
        Path defaultReplacementFile = replacementsFolder.resolve(DEFAULT_REPLACEMENTS_FILE_NAME);
        try (InputStream is = getClass().getResourceAsStream("/" + DEFAULT_REPLACEMENTS_FILE_NAME)) {
            if (is == null) {
                throw new RuntimeException("Cannot find resource /" + DEFAULT_REPLACEMENTS_FILE_NAME);
            }
            Files.copy(is, defaultReplacementFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public Map<String, Map<String, Map<String, Object>>> getAllReplacements() {
        Yaml yaml = new Yaml();
        try {
            List<Path> replacementFiles = getReplacementFiles();

            Map<String, Map<String, Map<String, Object>>> allReplacements = new HashMap<>();
            for (Path replacementFile : replacementFiles) {
                try (InputStream is = Files.newInputStream(replacementFile)) {
                    Map<String, Map<String, Map<String, Object>>> replacements = yaml.load(is);
                    mergeReplacements(allReplacements, replacements);
                }
            }

            return allReplacements;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load replacements", e);
        }
    }

    private void mergeReplacements(Map<String, Map<String, Map<String, Object>>> allReplacements,
                                   Map<String, Map<String, Map<String, Object>>> replacements) {
        for (Map.Entry<String, Map<String, Map<String, Object>>> entry : replacements.entrySet()) {
            String key = entry.getKey();
            Map<String, Map<String, Object>> value = entry.getValue();

            if (allReplacements.containsKey(key)) {
                allReplacements.get(key).putAll(value);
            } else {
                allReplacements.put(key, value);
            }
        }
    }

    private List<Path> getReplacementFiles() throws IOException {
        return Files.walk(replacementsFolder)
                .filter(path -> path.toString().endsWith(".yml"))
                .collect(Collectors.toList());
    }

    public Path getReplacementsFolder() {
        return replacementsFolder;
    }
}
