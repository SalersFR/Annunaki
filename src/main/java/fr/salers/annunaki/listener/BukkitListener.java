package fr.salers.annunaki.listener;

import fr.salers.annunaki.Annunaki;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BukkitListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Annunaki.getInstance().getPlayerDataManager().add(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Annunaki.getInstance().getPlayerDataManager().remove(event.getPlayer());
    }
}
