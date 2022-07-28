package fr.salers.annunaki.listener;

import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.data.PlayerData;
import fr.salers.annunaki.util.version.ClientVersion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BukkitListener implements Listener, PluginMessageListener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Annunaki.getInstance().getPlayerDataManager().add(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Annunaki.getInstance().getPlayerDataManager().remove(event.getPlayer());
    }

    @EventHandler
    public void onInvClick(final InventoryClickEvent event) {
        final PlayerData data = new PlayerData((Player) event.getWhoClicked());
        data.getGuiOpen().handleClickEvent(event);

        event.setCancelled(true);
    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        try {
            PlayerData data = Annunaki.getInstance().getPlayerDataManager().get(player);
            if (data != null) {
                if (Annunaki.getInstance().getViaManager()!= null) {
                    data.setVersion(ClientVersion
                            .matchProtocol(Annunaki.getInstance().getViaManager().getProtocol(data.getPlayer().getUniqueId())));
                } else {
                    data.setVersion(ClientVersion.v18);
                }

                String brand = new String(bytes, "UTF-8").substring(1);

                if (brand.length() > 16) {
                    brand = "custom brand";
                }

                data.getVersion().setBrand(brand);

            }
        } catch(Exception e) {
            e.printStackTrace();
            player.kickPlayer("Could not determine your client.");
        }
    }
}
