package me.whereareiam.whreplacer;

import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.module.ModuleLifeCycle;
import eu.cloudnetservice.driver.module.ModuleTask;
import eu.cloudnetservice.driver.module.driver.DriverModule;
import eu.cloudnetservice.node.command.CommandProvider;
import jakarta.inject.Inject;
import me.whereareiam.whreplacer.commands.ReloadCommand;
import me.whereareiam.whreplacer.config.ConfigManager;
import me.whereareiam.whreplacer.config.ReplacementsManager;
import me.whereareiam.whreplacer.listeners.CloudServicePrePrepareListener;
import me.whereareiam.whreplacer.replacer.PlaceholderReplacer;

import javax.annotation.Nonnull;
import java.io.IOException;

public final class Replacer extends DriverModule {
    private ConfigManager configManager;
    private ReplacementsManager replacementsManager;
    private PlaceholderReplacer placeholderReplacer;

    @Inject
    public Replacer() {
        this.configManager = new ConfigManager();
        this.replacementsManager = new ReplacementsManager();
    }

    @ModuleTask(order = 50, lifecycle = ModuleLifeCycle.LOADED)
    public void onLoad() {
        Replacer module = this;
        configManager = new ConfigManager();
        replacementsManager = new ReplacementsManager();
        placeholderReplacer = new PlaceholderReplacer(module);

        try {
            configManager.loadConfig();
            replacementsManager.loadReplacements();
            placeholderReplacer.loadReplacements();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ModuleTask(lifecycle = ModuleLifeCycle.STARTED)
    public void onStart(@Nonnull CommandProvider commandProvider, @Nonnull EventManager eventManager) {
        commandProvider.register(new ReloadCommand(this, placeholderReplacer));
        eventManager.registerListener(CloudServicePrePrepareListener.class);
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ReplacementsManager getReplacementsManager() {
        return replacementsManager;
    }
}
