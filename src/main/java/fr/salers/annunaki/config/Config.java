package fr.salers.annunaki.config;

import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.util.ColorUtil;
import org.bukkit.configuration.file.FileConfiguration;

public enum Config {
    // Taken from https://github.com/KidOGzz/OGPearls

    NO_PERMISSION("commands.no-permission", "&cYou don't have permission to do that!"),
    NOT_PLAYER("commands.not-player", "&cYou must be a player to do that!"),
    HELP("commands.help", "&a&lPTSD AntiCheat\n     \n&8- &f/ptsd alerts \n&8- &f/ptsd debug"),
    ALERTS_ENABLED("commands.alerts-enabled", "&aAlerts are now enabled."),
    ALERTS_DISABLED("commands.alerts-disabled", "&cAlerts are now disabled."),
    DEBUGGING_ENABLED("commands.debugging-enabled", "&aNow debugging &f%debugging%&a."),
    DEBUGGING_DISABLED("commands.debugging-disabled", "&cDebug messages are now disabled."),
    ALERT("checks.alert", "&aPTSD &8// &f%player% &7failed &a%type% (%name%)%experimental% &fx%vl%"),
    HOVER("checks.hover", "&f%description%\n    \n&7%info%"),
    CLICK_COMMAND("checks.click-command", "/tp %player%"),
    AUTOBAN_COMMAND("checks.autoban-command", "ban %player% Unfair Advantage");

    private final String path;
    private Object value;

    Config(String path, Object value) {
        FileConfiguration config = Annunaki.getInstance().getConfig();

        this.path = path;

        if (config.contains(path)) {
            this.value = config.get(path);
        } else {
            setValue(value);
        }
    }

    public void setValue(Object value) {
        this.value = value;
        Annunaki.getInstance().getConfig().set(path, value);
        Annunaki.getInstance().saveConfig();
    }

    public boolean getAsBoolean() {
        return (boolean) value;
    }

    public int getAsInt() {
        return (int) value;
    }

    public double getAsDouble() {
        return (double) value;
    }

    public String getAsString() {
        return ColorUtil.translate((String) value);
    }
}
