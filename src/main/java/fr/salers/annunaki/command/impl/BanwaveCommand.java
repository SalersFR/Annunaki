package fr.salers.annunaki.command.impl;

import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.command.CommandInfo;
import fr.salers.annunaki.command.SubCommand;
import fr.salers.annunaki.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.UUID;

@CommandInfo(command = "banwave", console = true, permission = "annunaki.command.alerts")
public class BanwaveCommand extends SubCommand {
    @Override
    public void handle(CommandSender sender, String[] args) {
        if(args.length == 1) {
            send(sender, "&cUsage: /annunaki banwave <add|remove|start|stop> [player]");
        } else if(args.length == 2) {
            if(args[1].equalsIgnoreCase("list")) {
                String message = "&dBanwave players: &r";
                if(Annunaki.getInstance().getBanwaveManager().banwavePlayers.size() > 0) {
                    for (UUID id : Annunaki.getInstance().getBanwaveManager().banwavePlayers) {
                        OfflinePlayer player = Bukkit.getOfflinePlayer(id);
                        if (player != null) {
                            message = String.join(", ", message, player.getName());
                        }
                    }
                } else {
                    message = "&dBanwave players: &rNone";
                }
                send(sender, message);
            } else if(args[1].equalsIgnoreCase("start")) {
                if(Annunaki.getInstance().getBanwaveManager().running) {
                    send(sender, Config.BANWAVE_ALREADY_RUNNING.getAsString());
                } else {
                    if(Annunaki.getInstance().getBanwaveManager().start()) {
                        send(sender, Config.BANWAVE_COMMAND_STARTED.getAsString());
                    } else {
                        send(sender, "&cCould not start banwave");
                    }
                }
            } else if(args[1].equalsIgnoreCase("stop")) {
                if(!Annunaki.getInstance().getBanwaveManager().running) {
                    send(sender, Config.BANWAVE_NOT_RUNNING.getAsString());
                } else {
                    Annunaki.getInstance().getBanwaveManager().stop();
                    send(sender, Config.BANWAVE_COMMAND_STOPPED.getAsString());
                }
            }
        } else {
            String player = args[2];
            if(args[1].equalsIgnoreCase("remove")) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(player);
                if (Annunaki.getInstance().getBanwaveManager().banwavePlayers.contains(target.getUniqueId())) {
                    Annunaki.getInstance().getBanwaveManager().banwavePlayers.remove(target.getUniqueId());
                    send(sender, "&dRemoved " + target.getName() + " from banwave.");
                } else {
                    send(sender, "&c" + target.getName() + " is not in banwave.");
                }
            } else if(args[1].equalsIgnoreCase("add")) {
                if (Bukkit.getOfflinePlayer(player) == null) {
                    send(sender, "&cPlayer not found.");
                } else {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(player);

                    if (!Annunaki.getInstance().getBanwaveManager().banwavePlayers.contains(target.getUniqueId())) {
                        Annunaki.getInstance().getBanwaveManager().banwavePlayers.add(target.getUniqueId());
                    } else {
                        send(sender, "&cPlayer already in banwave.");
                        return;
                    }
                    send(sender, "&a" + target.getName() + " added to banwave.");
                }
            }
        }
    }
}
