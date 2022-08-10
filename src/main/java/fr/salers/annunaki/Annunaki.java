package fr.salers.annunaki;

import com.github.retrooper.packetevents.PacketEvents;
import fr.salers.annunaki.banwave.BanwaveManager;
import fr.salers.annunaki.command.BaseCommand;
import fr.salers.annunaki.config.CheckConfig;
import fr.salers.annunaki.listener.BukkitListener;
import fr.salers.annunaki.listener.PacketEventsInListener;
import fr.salers.annunaki.listener.PacketEventsOutListener;
import fr.salers.annunaki.manager.*;
import fr.salers.annunaki.util.version.ServerVersion;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

@Getter
public class Annunaki extends JavaPlugin {

    private static Annunaki instance;

    private final PlayerManager playerManager = new PlayerManager();
    private final NmsManager nmsManager = new NmsManager();
    private final TaskManager taskManager = new TaskManager();

    private ServerVersion serverVersion;

    private final CheckConfig checkConfig = new CheckConfig();

    private CheckManager checkManager;

    private final BanwaveManager banwaveManager = new BanwaveManager();

    private ViaManager viaManager;

    public static Annunaki getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));

        //Are all listeners read only?
        PacketEvents.getAPI().getSettings().readOnlyListeners(false)
                .checkForUpdates(false)
                .bStats(true);

        PacketEvents.getAPI().load();

        serverVersion = ServerVersion.get();
    }

    @Override
    public void onEnable() {
        if (serverVersion == ServerVersion.UNSUPPORTED) {
            Bukkit.getConsoleSender().sendMessage(
                    "Your server version is unsupported! Please use a server version between 1.8 and 1.19");
            Bukkit.getPluginManager().disablePlugin(instance);
            return;
        }

        saveDefaultConfig();

        try {
            checkConfig.setup();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        checkManager = new CheckManager();

        nmsManager.setup();
        taskManager.setup();

        getCommand("annunaki").setExecutor(new BaseCommand());

        BukkitListener listener = new BukkitListener();

        Bukkit.getPluginManager().registerEvents(listener, this);

        if(serverVersion.getVersion() < 13) {
            Bukkit.getMessenger().registerIncomingPluginChannel(this, "MC|Brand", listener);
        } else {
            Bukkit.getMessenger().registerIncomingPluginChannel(this, "minecraft:brand", listener);
        }

        PacketEvents.getAPI().getEventManager()
                .registerListeners(new PacketEventsInListener(), new PacketEventsOutListener());

        PacketEvents.getAPI().init();

        if(Bukkit.getPluginManager().getPlugin("ViaVersion") != null) {
            viaManager = new ViaManager();
        }

        if(Bukkit.getPluginManager().getPlugin("floodgate") != null) {
            // TODO: Add support for floodgate
        }

        taskManager.start();

        for(Player p : Bukkit.getOnlinePlayers()) {
            playerManager.add(p);
        }
    }

    @Override
    public void onDisable() {
        taskManager.stop();

        PacketEvents.getAPI().terminate();
    }
}
