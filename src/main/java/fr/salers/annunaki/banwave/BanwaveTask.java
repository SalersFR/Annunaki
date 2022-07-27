package fr.salers.annunaki.banwave;

import fr.salers.annunaki.config.Config;
import org.bukkit.Bukkit;

import java.util.UUID;

public class BanwaveTask implements Runnable {

    BanwaveManager manager;
    int total = 0;

    public BanwaveTask(BanwaveManager manager) {
        this.manager = manager;
        total = manager.banwavePlayers.size();
    }

    @Override
    public void run() {
        if(manager.banwavePlayers.iterator().hasNext()) {
            UUID uuid = manager.banwavePlayers.iterator().next();
            for(String s : Config.BANWAVE_COMMANDS.getAsStringList()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replaceAll("%player%", Bukkit.getOfflinePlayer(uuid).getName()));
            }
            manager.banwavePlayers.remove(uuid);
        }

        if(manager.banwavePlayers.isEmpty()) {
            manager.total = total;
            manager.stop();
        }
    }
}
