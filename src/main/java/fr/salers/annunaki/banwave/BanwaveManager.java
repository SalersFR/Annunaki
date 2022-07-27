package fr.salers.annunaki.banwave;

import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BanwaveManager {

    public BukkitTask task;
    public boolean running = false;

    int total = 0;

    public BanwaveManager() {

    }

    public List<UUID> banwavePlayers = new ArrayList<UUID>();


    public boolean start() {
        if(!running) {
            running = true;
            if (!banwavePlayers.isEmpty()) {
                if(Config.BANWAVE_BROADCAST.getAsBoolean()) {
                    Bukkit.broadcastMessage(Config.BANWAVE_STARTED.getAsString());
                }

                task = Bukkit.getScheduler().runTaskTimer(Annunaki.getInstance(), new BanwaveTask(this), 0L, Config.BANWAVE_TICKS.getAsInt());
                return true;
            } else {
               return false;
            }
        }
        return false;
    }

    public void stop() {
        if(running) {
            running = false;

            if(Config.BANWAVE_BROADCAST.getAsBoolean()) {
                Bukkit.broadcastMessage(Config.BANWAVE_ENDED.getAsString().replace("%players%", "" + total));
            }

            if(task != null && Bukkit.getScheduler().isCurrentlyRunning(task.getTaskId())) {
                task.cancel();
            }
        }
    }
}
