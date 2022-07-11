package fr.salers.annunaki.config;

import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.check.CheckInfo;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.logging.Level;

public class CheckConfig extends YamlConfiguration {

    public void setup() {
        try {
            load(new File(Annunaki.getInstance().getDataFolder(), "checks.yml"));
        } catch (Exception exception) {
            Annunaki.getInstance().getLogger().log(Level.SEVERE, "Exception while loading checks.yml.");

            exception.printStackTrace();
        }
    }

    public void setValue(String path, Object value) {
        set(path, value);

        try {
            save(new File(Annunaki.getInstance().getDataFolder(), "checks.yml"));
        } catch (Exception exception) {
            Annunaki.getInstance().getLogger().log(Level.SEVERE, "Exception while saving checks.yml.");

            exception.printStackTrace();
        }
    }

    public Object get(CheckInfo checkInfo, String path, Object value) {
        path = checkInfo.type().toLowerCase()
                + "." + checkInfo.name().toLowerCase()
                + "." + path;

        if (contains(path)) {
            return get(path);
        } else {
            setValue(path, value);

            return value;
        }
    }
}
