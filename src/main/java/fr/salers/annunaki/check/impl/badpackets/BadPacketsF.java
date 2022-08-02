package fr.salers.annunaki.check.impl.badpackets;


import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.data.PlayerData;
import org.bukkit.GameMode;

@CheckInfo(name = "BadPackets", type = "F", description = "Checks for invalid packets", experimental = false, maxVl = 5, punish = true, enabled = true)
public class BadPacketsF extends Check {
    public BadPacketsF(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if(event.getPacketType() == PacketType.Play.Client.SPECTATE && data.getPlayer().getGameMode() != GameMode.SPECTATOR) {
            fail("g=" + data.getPlayer().getGameMode().name() + ", spectate");
        } if(event.getPacketType() == PacketType.Play.Client.SET_DIFFICULTY) {
            fail("set difficulty");
        }
    }
}
