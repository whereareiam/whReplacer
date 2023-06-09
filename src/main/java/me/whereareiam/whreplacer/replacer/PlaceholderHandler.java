package me.whereareiam.whreplacer.replacer;

import eu.cloudnetservice.node.event.service.CloudServicePrePrepareEvent;
import me.whereareiam.whreplacer.utils.FileUtils;
import me.whereareiam.whreplacer.utils.InternalPlaceholder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class PlaceholderHandler {
    private final CloudServicePrePrepareEvent event;

    public PlaceholderHandler(CloudServicePrePrepareEvent event) {
        this.event = event;
    }

    public void processTask(String taskName, String placeholder, String value, Map<String, List<String>> tasks) {
        if (!tasks.containsKey(taskName) || !isValidTaskData(placeholder, value)) {
            return;
        }

        value = InternalPlaceholder.replaceInternalPlaceholders(value, event);

        List<String> taskPaths = tasks.get(taskName);
        replacePlaceholdersInFiles(placeholder, value, taskPaths);
    }

    private boolean isValidTaskData(String placeholder, String value) {
        return placeholder != null && !placeholder.isEmpty()
                && value != null && !value.isEmpty();
    }

    @SuppressWarnings("unchecked")
    public void processService(String serviceName, Map<String, Map<String, Object>> services) {
        Map<String, Object> serviceData = services.get(serviceName);
        if (!services.containsKey(serviceName) || !isValidServiceData(serviceData)) {
            return;
        }

        String placeholder = (String) serviceData.get("placeholder");
        String value = (String) serviceData.get("value");
        List<String> servicePaths = (List<String>) serviceData.get("paths");

        value = InternalPlaceholder.replaceInternalPlaceholders(value, event);

        replacePlaceholdersInFiles(placeholder, value, servicePaths);
    }

    private boolean isValidServiceData(Map<String, Object> serviceData) {
        return serviceData.containsKey("placeholder")
                && serviceData.containsKey("value")
                && serviceData.containsKey("paths");
    }

    private void replacePlaceholdersInFiles(String placeholder, String value, List<String> paths) {
        for (String path : paths) {
            Path filePath = Paths.get(event.service().directory() + "/" + path);
            if (!Files.exists(filePath)) {
                continue;
            }

            try {
                String fileContent = FileUtils.readFileContent(filePath);

                if (fileContent.contains(placeholder)) {
                    fileContent = fileContent.replace(placeholder, value);
                    FileUtils.writeFileContent(filePath, fileContent);
                }
            } catch (IOException e) {
                System.out.println("Failed to read or write file at path: " + filePath);
            }
        }
    }
}
