package fr.salers.annunaki.command;

import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.command.impl.AlertsCommand;
import fr.salers.annunaki.command.impl.DebugCommand;
import fr.salers.annunaki.config.Config;
import fr.salers.annunaki.data.PlayerData;
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
            new DebugCommand()
    );

    // Could probably be cleaner, but this is better than what I've done before so it's good for now

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            send(sender, Config.NOT_PLAYER.getAsString());
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("ptsd.command.base")) {
            send(player, Config.NO_PERMISSION.getAsString());
            return true;
        }

        PlayerData data = Annunaki.getInstance().getPlayerDataManager().get(player);

        if (args.length == 0) {
            send(player, Config.HELP.getAsString());
            return true;
        }

        Optional<SubCommand> subCommand = subCommands.stream().filter(command -> {
            CommandInfo info = command.getClass().getAnnotation(CommandInfo.class);

            return info != null && info.command().equalsIgnoreCase(args[0]);
        }).findFirst();

        if (!subCommand.isPresent()) {
            send(player, Config.HELP.getAsString());
            return true;
        }

        if (player.hasPermission(subCommand.get().getClass().getAnnotation(CommandInfo.class).permission())) {
            subCommand.get().handle(data, args);
        } else {
            send(player, Config.NO_PERMISSION.getAsString());
        }

        return true;
    }

    protected void send(CommandSender sender, String message) {
        sender.sendMessage(ColorUtil.translate(message));
    }

}
