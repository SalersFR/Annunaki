package fr.salers.annunaki.check.impl.groundspoof;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.data.PlayerData;
import fr.salers.annunaki.util.PacketUtil;
import org.bukkit.Bukkit;


@CheckInfo(
        type = "Groundspoof",
        name = "A",
        description = "Checks if a player is spoofing their ground status.",
        experimental = true,
        maxVl = 50,
        punish = true)
public class Groundspoof extends Check {

    public Groundspoof(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isPosition(event.getPacketType())) {
            boolean exempt = data.getCollisionProcessor().isBuggedInBlocks() ||
                    data.getCollisionProcessor().isNearBoat() ||
                    data.getCollisionProcessor().isLastInVehicle() ||
                    data.getPlayer().getVehicle() != null ||
                    data.getPlayer().getLocation() == null ||
                    !data.getCollisionProcessor().isChunkLoaded(data.getPlayer().getLocation());
            if(!exempt) {
                boolean isOnGround = data.getCollisionProcessor().isMathOnGround() && data.getCollisionProcessor().isLastMathOnGround();

                if (!isOnGround && data.getCollisionProcessor().isClientOnGround()) {
                    if (buffer++ >= 1) {
                        fail("client=" + data.getCollisionProcessor().isClientOnGround() + ", server=" + false);
                        buffer = 0;
                    }
                } else {
                    buffer = Math.max(buffer - 0.1, 0);
                }

               // Bukkit.broadcastMessage("" + data.getCollisionProcessor().isCollisionOnGround());
            }
        }
    }
}
