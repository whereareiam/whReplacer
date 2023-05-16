package me.whereareiam.whreplacer;

import de.dytanic.cloudnet.event.service.CloudServicePreStartEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

public class PlaceholderReplacer {

    private final Main module;
    private Map<String, Map<String, Map<String, Object>>> replacements;

    public PlaceholderReplacer(Main module) {
        this.module = module;
        loadReplacements();
    }

    private void loadReplacements() {
        replacements = module.getReplacementsManager().getAllReplacements();
    }

    public void reloadReplacements() {
        loadReplacements();
    }

    public void replacePlaceholders(CloudServicePreStartEvent event) {
        String task = event.getCloudService().getServiceId().getTaskName();
        String service = event.getCloudService().getServiceId().getName();

        if (!replacements.containsKey("placeholders")) {
            return;
        }

        Map<String, Map<String, Object>> placeholders = replacements.get("placeholders");

        for (Map.Entry<String, Map<String, Object>> entry : placeholders.entrySet()) {
            Map<String, Object> placeholderData = entry.getValue();

            if (placeholderData.containsKey("tasks")) {
                String placeholder = (String) placeholderData.get("placeholder");
                String value = (String) placeholderData.get("value");
                Map<String, List<String>> tasks = (Map<String, List<String>>) placeholderData.get("tasks");
                handleTask(event, task, placeholder, value, tasks);
            }

            if (placeholderData.containsKey("services")) {
                Map<String, Map<String, Object>> services = (Map<String, Map<String, Object>>) placeholderData.get("services");
                handleService(event, service, services);
            }
        }
    }

    private boolean isValidServiceData(Map<String, Object> serviceData) {
        return serviceData.containsKey("placeholder")
                && serviceData.containsKey("value")
                && serviceData.containsKey("paths");
    }

    private void handleTask(CloudServicePreStartEvent event, String task, String placeholder, String value, Map<String, List<String>> tasks) {
        if (!tasks.containsKey(task)) {
            return;
        }

        List<String> taskPaths = tasks.get(task);
        replacePlaceholdersInFiles(event, placeholder, value, taskPaths);
    }

    private void handleService(CloudServicePreStartEvent event, String service, Map<String, Map<String, Object>> services) {
        if (!services.containsKey(service)) {
            return;
        }

        Map<String, Object> serviceData = services.get(service);

        if (!isValidServiceData(serviceData)) {
            return;
        }

        String placeholder = (String) serviceData.get("placeholder");
        String value = (String) serviceData.get("value");

        if (!(serviceData.get("paths") instanceof List)) {
            return;
        }

        List<String> servicePaths = (List<String>) serviceData.get("paths");
        replacePlaceholdersInFiles(event, placeholder, value, servicePaths);
    }

    private void replacePlaceholdersInFiles(CloudServicePreStartEvent event, String placeholder, String value, List<String> paths) {
        for (String path : paths) {
            Path filePath = Paths.get(event.getCloudService().getDirectoryPath() + "/" + path);
            if (!Files.exists(filePath)) {
                System.out.println("Debug: File does not exist at path: " + filePath);
                continue;
            }

            try {
                String fileContent = Files.readString(filePath);

                if (fileContent.contains(placeholder)) {
                    fileContent = fileContent.replace(placeholder, value);
                    Files.writeString(filePath, fileContent, StandardOpenOption.TRUNCATE_EXISTING);
                    System.out.println("Debug: Replaced placeholder '" + placeholder + "' in file at path: " + filePath);
                } else {
                    System.out.println("Debug: Placeholder '" + placeholder + "' does not exist in file at path: " + filePath);
                }
            } catch (IOException e) {
                System.out.println("Debug: Failed to read or write file at path: " + filePath);
            }
        }
    }
}
