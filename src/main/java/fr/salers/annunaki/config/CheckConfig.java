package fr.salers.annunaki.config;

import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.manager.CheckManager;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public class CheckConfig extends YamlConfiguration {

    File checkConfig;

    public void setup() throws IOException {

        checkConfig = new File(Annunaki.getInstance().getDataFolder(), "checks.yml");

        if(!checkConfig.exists()) {
            if(checkConfig.createNewFile()) {
                Annunaki.getInstance().getLogger().log(Level.INFO, "Created checks.yml");
            } else {
                Annunaki.getInstance().getLogger().log(Level.SEVERE, "Could not create checks.yml");
            }
        }

        try {
            load(checkConfig);

            CheckManager cm = new CheckManager();
            for(Check c : cm.getChecks()) {
                if(!isSet(c.getCheckInfo().name().toLowerCase())) {
                    String path = c.getCheckInfo().name().toLowerCase() + "." + c.getCheckInfo().type().toLowerCase();
                    set(path + ".enabled", true);
                    set(path + ".punish", c.getCheckInfo().punish());
                    set(path + ".max-vl", c.getCheckInfo().maxVl());
                    set(path + ".punish-commands", List.of("" + c.getCheckInfo().maxVl() + ":kick %player% Unfair Advantage"));
                }
            }
            // dont save every check... helps
            save(checkConfig);

            cm = null;
        } catch (Exception exception) {
            Annunaki.getInstance().getLogger().log(Level.SEVERE, "Exception while loading checks.yml.");

            exception.printStackTrace();
        }
    }

    public void modifyCheckStatus(final boolean enabled, final String path) {
        this.set(path, enabled);
        try {
            this.load(checkConfig);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void setValue(String path, Object value) {
        set(path, value);

        try {
            save(checkConfig);
        } catch (Exception exception) {
            Annunaki.getInstance().getLogger().log(Level.SEVERE, "Exception while saving checks.yml.");

            exception.printStackTrace();
        }
    }

    public Object get(CheckInfo checkInfo, String path, Object value) {
        path = checkInfo.name().toLowerCase()
                + "." + checkInfo.type().toLowerCase()
                + "." + path;

        if (contains(path)) {
            return get(path);
        } else {
            setValue(path, value);

            return value;
        }
    }
}
