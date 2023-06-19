package me.whereareiam.whreplacer.utils;

import eu.cloudnetservice.node.event.service.CloudServicePrePrepareEvent;

public class InternalPlaceholder {
    public static String replaceInternalPlaceholders(String value, CloudServicePrePrepareEvent event) {
        String serviceName = event.service().serviceId().name();
        String taskName = event.service().serviceId().taskName();
        String nodeId = event.service().serviceId().nodeUniqueId().replaceAll("\\D+", "");

        value = value.replaceAll("%service_name%", serviceName)
                .replaceAll("%task_name%", taskName)
                .replaceAll("%node_id%", nodeId);

        return value;
    }
}
