package fr.salers.annunaki.manager;

import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.nms.NmsImplementation;
import fr.salers.annunaki.nms.impl.NmsImplementation8;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.logging.Level;

@Getter
public class NmsManager {

    private NmsImplementation nmsImplementation;

    public void setup() {
        String version = Bukkit.getServer().getClass().getPackage()
                .getName().replace("org.bukkit.craftbukkit.", "");

        switch (version) {
            case "v1_8_R3":
                nmsImplementation = new NmsImplementation8();

                break;
            default:
                nmsImplementation = new NmsImplementation8();

                Annunaki.getInstance().getLogger().log(Level.WARNING, "Could not determine server version, defaulting to v1_8_R3");
                break;
        }
    }
}
