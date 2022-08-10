package fr.salers.annunaki.manager;

import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.data.PlayerData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerManager {

    private final Map<UUID, PlayerData> dataMap = new HashMap<>();

    public void add(Player player) {
        PlayerData data = new PlayerData(player.getUniqueId());

        File dir = new File(Annunaki.getInstance().getDataFolder(), "players");
        if(!dir.exists())
            dir.mkdirs();

        File temp = new File(Annunaki.getInstance().getDataFolder() + File.separator + "players", player.getUniqueId().toString() + ".yml");
        if(!temp.exists()) {
            try {
                temp.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(temp);
            data.setAlertDelay(config.getLong("alerts.delay"));
            data.setAlerts(config.getBoolean("alerts.enabled"));
            data.setDebugDelay(config.getLong("debug.delay"));
        }

        Annunaki.getInstance().getCheckManager().addChecks(data);
        dataMap.put(player.getUniqueId(), data);

    }

    public void remove(Player player) {
        if(dataMap.containsKey(player.getUniqueId())) {
            PlayerData data = dataMap.get(player.getUniqueId());


            File temp = new File(Annunaki.getInstance().getDataFolder() + File.separator + "players", player.getUniqueId().toString() + ".yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(temp);

            config.set("alerts.delay", data.getAlertDelay());
            config.set("alerts.enabled", data.isAlerts());
            config.set("debug.delay", data.getDebugDelay());

            try {
                config.save(temp);
            } catch (Exception e) {

            }

            dataMap.remove(player.getUniqueId());
        }
    }

    public PlayerData get(Player player) {
        return dataMap.get(player.getUniqueId());
    }

    public List<PlayerData> values() {
        return new ArrayList<>(dataMap.values());
    }
}
