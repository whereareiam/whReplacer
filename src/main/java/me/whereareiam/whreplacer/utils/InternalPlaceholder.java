package me.whereareiam.whreplacer.utils;

import de.dytanic.cloudnet.event.service.CloudServicePreStartEvent;

public class InternalPlaceholder {
    public static String replaceInternalPlaceholders(String value, CloudServicePreStartEvent event) {
        String serviceName = event.getCloudService().getServiceId().getName();
        String taskName = event.getCloudService().getServiceId().getTaskName();
        String nodeId = event.getCloudService().getServiceId().getNodeUniqueId().replaceAll("\\D+", "");

        value = value.replaceAll("%service_name%", serviceName)
                .replaceAll("%task_name%", taskName)
                .replaceAll("%node_id%", nodeId);

        return value;
    }
}
