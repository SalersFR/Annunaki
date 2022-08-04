package fr.salers.annunaki.config;

import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.util.ColorUtil;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

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
    BANWAVE_COMMANDS("banwave.ban.commands", List.of("ban %player% Unfair Advantage", "broadcast &d%player% &rwas banned for an &dUnfair Advantage")),
    BANWAVE_STARTED("banwave.broadcast.started", "&aBanwave started!"),
    BANWAVE_TICKS("banwave.ban.delay-ticks", 20),
    BANWAVE_BROADCAST("banwave.broadcast.enabled", true),
    BANWAVE_ENABLED("banwave.enabled", true),
    BANWAVE_INTERVAL("banwave.interval", "6h"),
    BANWAVE_ENDED("banwave.broadcast.ended", "&dBanwave ended, banning &r%players% &dplayers!"),
    BANWAVE_ALREADY_RUNNING("banwave.command.already-running", "&dA banwave is already running"),
    BANWAVE_COMMAND_STARTED("banwave.command.started", "&dBanwave has been started!"),
    BANWAVE_NOT_RUNNING("banwave.command.not-running", "&dNo banwave is running"),
    BANWAVE_COMMAND_STOPPED("banwave.command.stopped", "&dBanwave has been stopped!"),

    SILENT("checks.silent", false);


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

    public List<String> getAsStringList() {
        return (List<String>) value;
    }
}
