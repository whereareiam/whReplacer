package me.whereareiam.whreplacer.replacer;

import eu.cloudnetservice.node.event.service.CloudServicePrePrepareEvent;
import jakarta.inject.Inject;
import me.whereareiam.whreplacer.Replacer;

import java.util.List;
import java.util.Map;

public class PlaceholderReplacer {
    private final Replacer module;
    private Map<String, Map<String, Map<String, Object>>> replacements;

    @Inject
    public PlaceholderReplacer(Replacer module) {
        this.module = module;
        loadReplacements();
    }

    public void loadReplacements() {
        replacements = module.getReplacementsManager().getAllReplacements();
    }

    @SuppressWarnings("unchecked")
    public void replacePlaceholders(CloudServicePrePrepareEvent event) {
        if(replacements == null){
            System.err.println("Replacements Map is not loaded yet");
            return;
        }

        String taskName = event.service().serviceId().taskName();
        String serviceName = event.service().serviceId().name();

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
                placeholderHandler.processTask(taskName, placeholder, value, tasks);
            }

            if (placeholderData.containsKey("services")) {
                Map<String, Map<String, Object>> services = (Map<String, Map<String, Object>>) placeholderData.get("services");
                placeholderHandler.processService(serviceName, services);
            }
        }
    }
}
