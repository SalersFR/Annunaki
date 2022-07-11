package fr.salers.annunaki.manager;

import fr.salers.annunaki.data.PlayerData;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerDataManager {

    private final Map<UUID, PlayerData> dataMap = new HashMap<>();

    public void add(Player player) {
        dataMap.put(player.getUniqueId(), new PlayerData(player));
    }

    public void remove(Player player) {
        dataMap.remove(player.getUniqueId());
    }

    public PlayerData get(Player player) {
        return dataMap.get(player.getUniqueId());
    }

    public List<PlayerData> values() {
        return new ArrayList<>(dataMap.values());
    }
}
