package fr.salers.annunaki.command.impl;

import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.command.CommandInfo;
import fr.salers.annunaki.command.SubCommand;
import fr.salers.annunaki.config.Config;
import fr.salers.annunaki.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

@CommandInfo(command = "banwave", permission = "annunaki.command.alerts")
public class BanwaveCommand extends SubCommand {
    @Override
    public void handle(PlayerData data, String[] args) {
        if(args.length <= 2) {
            if(args[1].equalsIgnoreCase("list")) {
                StringBuilder sb = new StringBuilder();
                for(UUID id : Annunaki.getInstance().getBanwaveManager().banwavePlayers) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(id);
                    if(player != null) {
                        sb.append(player.getName()).append(", ");
                    }
                }
                send(data, sb.toString());
            } else if(args[1].equalsIgnoreCase("start")) {
                if(Annunaki.getInstance().getBanwaveManager().running) {
                    send(data, Config.BANWAVE_ALREADY_RUNNING.getAsString());
                } else {
                    Annunaki.getInstance().getBanwaveManager().start();
                    send(data, Config.BANWAVE_COMMAND_STARTED.getAsString());
                }
            } else if(args[1].equalsIgnoreCase("stop")) {
                if(!Annunaki.getInstance().getBanwaveManager().running) {
                    send(data, Config.BANWAVE_NOT_RUNNING.getAsString());
                } else {
                    Annunaki.getInstance().getBanwaveManager().stop();
                    send(data, Config.BANWAVE_COMMAND_STOPPED.getAsString());
                }
            } else {
                send(data, "&cUsage: /banwave add <player>");
            }
        } else {
            String player = args[2];
            if(Bukkit.getOfflinePlayer(player) == null) {
                send(data, "&cPlayer not found.");
            } else {
                OfflinePlayer target = Bukkit.getOfflinePlayer(player);

                Annunaki.getInstance().getBanwaveManager().banwavePlayers.add(Bukkit.getOfflinePlayer(player).getUniqueId());
                send(data, "&a" + target.getPlayer().getName() + " added to banwave.");
            }
        }
    }
}
