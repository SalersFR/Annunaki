package fr.salers.annunaki.check.impl.badpackets;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.util.PacketUtil;

/**
 * @author Salers
 * made on dev.notonweed.annunaki.check.impl.badpackets
 */

@CheckInfo(
        type = "B",
        name = "BadPackets",
        description = "Checks for an impossible pitch.",
        experimental = false,
        maxVl = 30,
        punish = true
)
public class BadPacketsB extends Check {

    @Override
    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) {

            final float pitch = Math.abs(data.getRotationProcessor().getPitch());

            if (pitch > 90.f && data.getTeleportProcessor().getTeleportTicks() > 1)
                fail("pitch=" + pitch);
        }
    }
}
