package fr.salers.annunaki.check.impl.groundspoof;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.data.processor.impl.CollisionProcessor;
import fr.salers.annunaki.util.PacketUtil;
import org.bukkit.Bukkit;


@CheckInfo(
        type = "A",
        name = "Groundspoof",
        description = "Checks if a player is spoofing their ground status.",
        experimental = true,
        maxVl = 50,
        punish = true)
public class Groundspoof extends Check {

    @Override
    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isPosition(event.getPacketType())) {
            boolean exempt = data.getCollisionProcessor().isBuggedInBlocks() ||
                    data.getCollisionProcessor().isNearBoat() ||
                    data.getCollisionProcessor().isLastInVehicle() ||
                    data.getPlayer().getVehicle() != null ||
                    data.getPlayer().getLocation() == null ||
                    !CollisionProcessor.isChunkLoaded(data.getPlayer().getLocation());
            if(!exempt) {

                boolean isOnGround = (data.getCollisionProcessor().isLastCollisionOnGround() || data.getCollisionProcessor().isCollisionOnGround()) && (data.getCollisionProcessor().isMathOnGround() || data.getCollisionProcessor().isLastMathOnGround());

                if (!isOnGround &&  data.getCollisionProcessor().isClientOnGround()) {
                    if (buffer++ > 1) {
                        fail("client=" + data.getCollisionProcessor().isClientOnGround() + ", server=false");
                        buffer = 0;
                    }
                } else {
                    buffer = Math.max(buffer - 0.05, 0);
                }

               Bukkit.broadcastMessage("" + data.getCollisionProcessor().isCollisionOnGround());
            } else {
                Bukkit.broadcastMessage("exempt");
            }
        }
    }
}
