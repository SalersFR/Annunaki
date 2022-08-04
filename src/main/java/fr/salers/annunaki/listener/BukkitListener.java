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

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

public class BukkitListener implements Listener, PluginMessageListener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Annunaki.getInstance().getPlayerManager().add(event.getPlayer());
        if(Annunaki.getInstance().getServerVersion().getVersion() < 13) {
            addChannel(event.getPlayer(), "MC|BRAND");
        } else {
            addChannel(event.getPlayer(), "minecraft:brand");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Annunaki.getInstance().getPlayerManager().remove(event.getPlayer());
    }

    @EventHandler
    public void onInvClick(final InventoryClickEvent event) {
        if(event.getWhoClicked() instanceof Player) {
            final PlayerData data = Annunaki.getInstance().getPlayerManager().get((Player) event.getWhoClicked());

            if (data.getGuiOpen() != null) {
                if (event.getClickedInventory().getName().equalsIgnoreCase(data.getGuiOpen().getInventory().getName())) {
                    data.getGuiOpen().handleClickEvent(event);
                    event.setCancelled(true);
                }
            }
        }
    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        try {
            PlayerData data = Annunaki.getInstance().getPlayerManager().get(player);
            if (data != null) {
                if (Annunaki.getInstance().getViaManager()!= null) {
                    data.setVersion(ClientVersion
                            .matchProtocol(Annunaki.getInstance().getViaManager().getProtocol(data.getPlayer().getUniqueId())));
                } else {
                    data.setVersion(ClientVersion.v18);
                }

                String brand = new String(bytes, StandardCharsets.UTF_8).substring(1);

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

    private void addChannel(final Player player, final String channel) {
        try {
            player.getClass().getMethod("addChannel", String.class).invoke(player, channel);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                 | SecurityException e) {
            e.printStackTrace();
        }
    }
}
