package me.whereareiam.whreplacer.replacer;

import de.dytanic.cloudnet.event.service.CloudServicePreStartEvent;
import me.whereareiam.whreplacer.Replacer;

import java.util.List;
import java.util.Map;

public class PlaceholderReplacer {
    private final Replacer module;
    private Map<String, Map<String, Map<String, Object>>> replacements;

    public PlaceholderReplacer(Replacer module) {
        this.module = module;
    }

    public void loadReplacements() {
        replacements = module.getReplacementsManager().getAllReplacements();
    }

    @SuppressWarnings("unchecked")
    public void replacePlaceholders(CloudServicePreStartEvent event) {
        String task = event.getCloudService().getServiceId().getTaskName();
        String service = event.getCloudService().getServiceId().getName();

        if (!replacements.containsKey("placeholders")) {
            return;
        }

        Map<String, Map<String, Object>> placeholders = replacements.get("placeholders");

        PlaceholderHandler placeholderHandler = new PlaceholderHandler(event);

        for (Map.Entry<String, Map<String, Object>> entry : placeholders.entrySet()) {
            Map<String, Object> placeholderData = entry.getValue();

            if (placeholderData.containsKey("tasks")) {
                String placeholder = (String) placeholderData.get("placeholder");
                String value = (String) placeholderData.get("value");
                Map<String, List<String>> tasks = (Map<String, List<String>>) placeholderData.get("tasks");
                placeholderHandler.processTask(task, placeholder, value, tasks);
            }

            if (placeholderData.containsKey("services")) {
                Map<String, Map<String, Object>> services = (Map<String, Map<String, Object>>) placeholderData.get("services");
                placeholderHandler.processService(service, services);
            }
        }
    }
}
