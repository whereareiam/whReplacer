package me.whereareiam.whreplacer.commands;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import eu.cloudnetservice.node.command.source.CommandSource;
import me.whereareiam.whreplacer.Replacer;
import me.whereareiam.whreplacer.config.ConfigManager;
import me.whereareiam.whreplacer.config.ReplacementsManager;
import me.whereareiam.whreplacer.replacer.PlaceholderReplacer;

import javax.annotation.Nonnull;
import java.io.IOException;

@CommandPermission("privilege.10")
@CommandDescription("Reloads the whreplacer configurations and replacements")
public class ReloadCommand {
    private final PlaceholderReplacer placeholderReplacer;
    private final ConfigManager configManager;
    private final ReplacementsManager replacementsManager;

    public ReloadCommand(Replacer module, PlaceholderReplacer placeholderReplacer) {
        this.placeholderReplacer = placeholderReplacer;
        this.configManager = module.getConfigManager();
        this.replacementsManager = module.getReplacementsManager();
    }

    @CommandMethod("whreplacer reload")
    public void handleReload(@Nonnull CommandSource source) {
        try {
            this.configManager.loadConfig();
            this.replacementsManager.loadReplacements();
            this.placeholderReplacer.loadReplacements();
            source.sendMessage(this.configManager.getString("messages.reload-successful"));
        } catch (IOException e) {
            e.printStackTrace();
            source.sendMessage(this.configManager.getString("messages.reload-failure"));
        }
    }
}
