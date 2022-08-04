package fr.salers.annunaki.command;

import fr.salers.annunaki.command.impl.*;
import fr.salers.annunaki.config.Config;
import fr.salers.annunaki.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BaseCommand implements CommandExecutor {

    private final List<SubCommand> subCommands = Arrays.asList(
            new AlertsCommand(),
            new DebugCommand(),
            new BanwaveCommand(),
            new SettingsCommand(),
            new ReloadCommand()
    );

    // Could probably be cleaner, but this is better than what I've done before so it's good for now

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("annunaki.command.base")) {
            send(sender, Config.NO_PERMISSION.getAsString());
            return true;
        }

        if (args.length == 0) {
            send(sender, Config.HELP.getAsString());
            return true;
        }

        Optional<SubCommand> subCommand = subCommands.stream().filter(command -> {
            CommandInfo info = command.getClass().getAnnotation(CommandInfo.class);

            return info != null && info.command().equalsIgnoreCase(args[0]);
        }).findFirst();

        if (!subCommand.isPresent()) {
            send(sender, Config.HELP.getAsString());
            return true;
        }

        if (sender.hasPermission(subCommand.get().getClass().getAnnotation(CommandInfo.class).permission())) {
            if(!subCommand.get().getClass().getAnnotation(CommandInfo.class).console() && !(sender instanceof Player)) {
                send(sender, "You must be a player to use this command.");
                return true;
            }
            subCommand.get().handle(sender, args);
        } else {
            send(sender, Config.NO_PERMISSION.getAsString());
        }

        return true;
    }

    protected void send(CommandSender sender, String message) {
        sender.sendMessage(ColorUtil.translate(message));
    }

}
