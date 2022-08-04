package fr.salers.annunaki.check;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.data.PlayerData;
import lombok.Getter;
import lombok.Setter;
import org.atteo.classindex.IndexSubclasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@IndexSubclasses
public abstract class Check {

    @Setter
    protected PlayerData data;

    private final CheckInfo checkInfo;

    private final ConfigInfo configInfo;
    protected double buffer = -1;
    private int vl;

    private final HashMap<Integer, List<String>> punishCommands = new HashMap<>();

    public Check() {
        checkInfo = getClass().getAnnotation(CheckInfo.class);
        configInfo = loadConfigInfo();
    }

    public void handle(PacketReceiveEvent event) {

    }

    public void handle(PacketSendEvent event) {
    }

    protected void fail(String info) {
        fail(info, 1);
    }

    protected void debug(Object info) {
        if(data.getDebugging().contains(this)) {
            data.getPlayer().sendMessage("§c§lDEBUG §8§l» §7" + info);
        }
    }

    protected void fail(String info, int vl) {
        this.vl += vl;
        Annunaki.getInstance().getCheckManager().alert(data, this, info);
    }

    private ConfigInfo loadConfigInfo() {
        final ConfigInfo info = new ConfigInfo();

        info.setMaxVl((int) Annunaki.getInstance().getCheckConfig().get(checkInfo, "max-vl", checkInfo.maxVl()));
        info.setPunish(((boolean) Annunaki.getInstance().getCheckConfig().get(checkInfo, "punish", checkInfo.punish())));
        info.setEnabled(((boolean) Annunaki.getInstance().getCheckConfig().get(checkInfo, "enabled", checkInfo.enabled())));

        for(String s : Annunaki.getInstance().getCheckConfig().getStringList(checkInfo.name().toLowerCase()
                + "." + checkInfo.type().toLowerCase() + ".punish-commands")) {
            String[] split = s.split(":");
            for(String sr :punishCommands.getOrDefault(Integer.parseInt(split[0]), new ArrayList<>())) {
                punishCommands.get(Integer.parseInt(split[0])).add(split[1]);
           }
        }

        return info;
    }

}
