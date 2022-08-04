package fr.salers.annunaki.check.impl.badpackets;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.util.PacketUtil;

/**
 * @author Salers
 * made on dev.annunaki.anticheat.check.impl.badpackets
 */

@CheckInfo(
        type = "D",
        name = "BadPackets",
        description = "Checks for invalid place order.",
        experimental = false,
        maxVl = 30,
        punish = true
)
public class BadPacketsD extends Check {

    private boolean dig, place;

    @Override
    public void handle(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            if (place || dig) {
                if (++buffer > 3)
                    fail("dig=" + dig + " place=" + place);
            } else if (buffer > 0) buffer -= 0.2;
        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            place = true;
        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            dig = true;
        } else if (PacketUtil.isFlying(event.getPacketType())) {
            dig = place = false;
        }
    }
}
