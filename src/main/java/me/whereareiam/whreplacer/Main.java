package me.whereareiam.whreplacer;

import de.dytanic.cloudnet.driver.module.ModuleLifeCycle;
import de.dytanic.cloudnet.driver.module.ModuleTask;
import de.dytanic.cloudnet.module.NodeCloudNetModule;
import me.whereareiam.whreplacer.commands.ReloadCommand;
import me.whereareiam.whreplacer.config.ConfigManager;
import me.whereareiam.whreplacer.config.ReplacementsManager;
import me.whereareiam.whreplacer.replacer.PlaceholderReplacer;
import me.whereareiam.whreplacer.listeners.PreServiceStartListener;

import java.io.IOException;

public final class Main extends NodeCloudNetModule {
    private ConfigManager configManager;
    private ReplacementsManager replacementsManager;
    private PlaceholderReplacer placeholderReplacer;

    @ModuleTask(event = ModuleLifeCycle.LOADED, order = 1)
    public void onLoad() {
        configManager = new ConfigManager();
        replacementsManager = new ReplacementsManager();
        placeholderReplacer = new PlaceholderReplacer(this);

        try {
            configManager.loadConfig();
            replacementsManager.loadReplacements();
            placeholderReplacer.loadReplacements();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ModuleTask(event = ModuleLifeCycle.STARTED, order = 2)
    public void onStart() {
        this.getEventManager().registerListener(new PreServiceStartListener(placeholderReplacer));
        this.registerCommand(new ReloadCommand(this, placeholderReplacer));
    }

    @ModuleTask(event = ModuleLifeCycle.STOPPED)
    public void onUnLoad() {
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ReplacementsManager getReplacementsManager() {
        return replacementsManager;
    }

    public PlaceholderReplacer getPlaceholderReplacer() {
        return placeholderReplacer;
    }
}
