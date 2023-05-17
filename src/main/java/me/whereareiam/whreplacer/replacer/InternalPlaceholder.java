package me.whereareiam.whreplacer.replacer;

public class InternalPlaceholder {

    public static final String TASK_NAME = "%task_name%";
    public static final String SERVICE_NAME = "%service_name%";
    public static final String NODE_ID = "%node_id%";

    public static String replaceInternalPlaceholders(String value, String taskName, String serviceName, String nodeId) {
        return value.replace(TASK_NAME, taskName).replace(SERVICE_NAME, serviceName).replace(NODE_ID, nodeId);
    }
}
