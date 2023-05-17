package me.whereareiam.whreplacer.listeners;

import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.event.service.CloudServicePreStartEvent;
import me.whereareiam.whreplacer.replacer.PlaceholderReplacer;

public class PreServiceStartListener {

    private final PlaceholderReplacer placeholderReplacer;

    public PreServiceStartListener(PlaceholderReplacer placeholderReplacer) {
        this.placeholderReplacer = placeholderReplacer;
    }

    @EventListener
    public void onServicePreStart(CloudServicePreStartEvent event) {
        placeholderReplacer.replacePlaceholders(event);
    }
}
