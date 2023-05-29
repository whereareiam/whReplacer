package me.whereareiam.whreplacer.commands;

import de.dytanic.cloudnet.command.sub.SubCommand;
import de.dytanic.cloudnet.command.sub.SubCommandArgumentTypes;
import de.dytanic.cloudnet.command.sub.SubCommandBuilder;
import de.dytanic.cloudnet.command.sub.SubCommandHandler;
import me.whereareiam.whreplacer.Replacer;
import me.whereareiam.whreplacer.replacer.PlaceholderReplacer;

import java.io.IOException;

public final class ReloadCommand extends SubCommandHandler {
    public ReloadCommand(Replacer module, PlaceholderReplacer placeholderReplacer) {
        super(SubCommandBuilder.create()
                .generateCommand((subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
                    try {
                        module.getConfigManager().loadConfig();
                        module.getReplacementsManager().loadReplacements();
                        placeholderReplacer.loadReplacements();
                        sender.sendMessage(module.getConfigManager().getString("messages.reload-successful"));
                    } catch (IOException e) {
                        e.printStackTrace();
                        sender.sendMessage(module.getConfigManager().getString("messages.reload-failure"));
                    }
                }, SubCommand::onlyConsole, SubCommandArgumentTypes.anyStringIgnoreCase("reload"))
                .getSubCommands(), "whreplacer");
        this.usage = "whreplacer reload";
        this.permission = "whreplacer.command.reload";
        this.prefix = "whreplacer";
        this.description = module.getConfigManager().getString("messages.reload-description");

    }
}
