package fr.salers.annunaki.manager;

import com.viaversion.viaversion.api.Via;

import java.util.UUID;

public class ViaManager {

    public int getProtocol(UUID id) {
        return Via.getAPI().getPlayerVersion(id);
    }
}
