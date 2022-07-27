package fr.salers.annunaki.command;

import fr.salers.annunaki.data.PlayerData;
import fr.salers.annunaki.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;

public abstract class SubCommand {

    public abstract void handle(CommandSender sender, String[] args);

    protected void send(CommandSender sender, String message) {
        sender.sendMessage(ColorUtil.translate(message));
    }
}
