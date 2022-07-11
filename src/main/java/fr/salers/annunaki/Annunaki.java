package fr.salers.annunaki;

import com.github.retrooper.packetevents.PacketEvents;
import fr.salers.annunaki.command.BaseCommand;
import fr.salers.annunaki.config.CheckConfig;
import fr.salers.annunaki.listener.BukkitListener;
import fr.salers.annunaki.listener.PacketEventsInListener;
import fr.salers.annunaki.listener.PacketEventsOutListener;
import fr.salers.annunaki.manager.NmsManager;
import fr.salers.annunaki.manager.PlayerDataManager;
import fr.salers.annunaki.manager.TaskManager;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Annunaki extends JavaPlugin {

    private static Annunaki instance;

    private final PlayerDataManager playerDataManager = new PlayerDataManager();
    private final NmsManager nmsManager = new NmsManager();
    private final TaskManager taskManager = new TaskManager();

    private final CheckConfig checkConfig = new CheckConfig();

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
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("checks.yml", false);

        checkConfig.setup();

        nmsManager.setup();
        taskManager.setup();

        getCommand("annunaki").setExecutor(new BaseCommand());

        Bukkit.getPluginManager().registerEvents(new BukkitListener(), this);

        PacketEvents.getAPI().getEventManager()
                .registerListeners(new PacketEventsInListener(), new PacketEventsOutListener());

        PacketEvents.getAPI().init();

        taskManager.start();
    }

    @Override
    public void onDisable() {
        taskManager.stop();

        PacketEvents.getAPI().terminate();
    }
}
