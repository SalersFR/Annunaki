package fr.salers.annunaki.command;

import fr.salers.annunaki.util.ColorUtil;
import org.bukkit.command.CommandSender;

public abstract class SubCommand {

    public abstract void handle(CommandSender sender, String[] args);

    protected void send(CommandSender sender, String message) {
        sender.sendMessage(ColorUtil.translate(message));
    }
}
