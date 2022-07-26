package fr.salers.annunaki.check;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.config.Config;
import fr.salers.annunaki.data.PlayerData;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.atteo.classindex.IndexSubclasses;
import org.bukkit.Bukkit;

import java.util.HashMap;

@Getter
@IndexSubclasses
public abstract class Check {

    protected final PlayerData data;

    private final CheckInfo checkInfo;

    private final ConfigInfo configInfo;
    protected double buffer = -1;
    private int vl;

    private HashMap<Integer, String> punishCommands;

    public Check(final PlayerData data) {
        this.data = data;
        checkInfo = getClass().getAnnotation(CheckInfo.class);
        configInfo = loadConfigInfo();
        punishCommands = new HashMap<>();
    }

    public void handle(PacketReceiveEvent event) {

    }

    public void handle(PacketSendEvent event) {
    }

    protected void fail(String info) {
        fail(info, 1);
    }

    protected void fail(String info, int vl) {
        this.vl += vl;

        TextComponent alert = new TextComponent();

        alert.setText(Config.ALERT.getAsString()
                .replaceAll("%player%", data.getPlayer().getName())
                .replaceAll("%type%", checkInfo.type())
                .replaceAll("%name%", checkInfo.name())
                .replaceAll("%experimental%", checkInfo.experimental() ? "*" : "")
                .replaceAll("%vl%", String.valueOf(this.vl)));

        alert.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(
                        Config.HOVER.getAsString()
                                .replaceAll("%description%", checkInfo.description())
                                .replaceAll("%info%", info)
                ).create()));

        alert.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                Config.CLICK_COMMAND.getAsString()
                        .replaceAll("%player%", data.getPlayer().getName())));

        // TODO: Make a list of alerting players so we dont have to sort every alert
        Annunaki.getInstance().getPlayerDataManager().values().stream()
                .filter(PlayerData::isAlerts)
                .forEach(data -> data.getPlayer().spigot().sendMessage(alert));

        if(!data.getPlayer().hasPermission("ptsd.bypass.punishment")
                && configInfo.isPunish() && punishCommands.containsKey(vl)) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), punishCommands.get(vl).replace("%player%", data.getPlayer().getName()).replace("%check%", checkInfo.name()));
        }

        // TODO: banwaves

        // TODO: log in database
    }

    private ConfigInfo loadConfigInfo() {
        final ConfigInfo info = new ConfigInfo();

        info.setMaxVl((int) Annunaki.getInstance().getCheckConfig().get(checkInfo, "max-vl", checkInfo.maxVl()));
        info.setPunish(((boolean) Annunaki.getInstance().getCheckConfig().get(checkInfo, "punish", checkInfo.punish())));

        for(String s : Annunaki.getInstance().getCheckConfig().getStringList(checkInfo.type().toLowerCase()
                + "." + checkInfo.name().toLowerCase() + ".punish-commands")) {
            String[] split = s.split(":");
            punishCommands.put(Integer.valueOf(split[0]), s.split(":")[1]);
        }

        return info;
    }

}
