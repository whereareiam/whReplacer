package me.whereareiam.whreplacer.listeners;

import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.node.event.service.CloudServicePrePrepareEvent;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import me.whereareiam.whreplacer.replacer.PlaceholderReplacer;

import javax.annotation.Nonnull;

public class CloudServicePrePrepareListener {
    private final Provider<PlaceholderReplacer> placeholderReplacerProvider;

    @Inject
    public CloudServicePrePrepareListener(Provider<PlaceholderReplacer> placeholderReplacerProvider) {
        this.placeholderReplacerProvider = placeholderReplacerProvider;
    }

    @EventListener
    public void onServicePreStart(@Nonnull CloudServicePrePrepareEvent event) {
        placeholderReplacerProvider.get().replacePlaceholders(event);
    }
}
