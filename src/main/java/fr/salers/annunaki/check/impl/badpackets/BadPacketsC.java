package fr.salers.annunaki.check.impl.badpackets;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.data.processor.impl.ActionProcessor;

/**
 * @author Salers
 * made on dev.notonweed.annunaki.check.impl.badpackets
 */

@CheckInfo(
        type = "C",
        name = "BadPackets",
        description = "Checks for invalid sneak/sprint packets during an attack.",
        experimental = false,
        maxVl = 30,
        punish = true
)
public class BadPacketsC extends Check {

    @Override
    public void handle(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            final ActionProcessor actionProcessor = data.getActionProcessor();

            final boolean sent = actionProcessor.isSentSprint() || actionProcessor.isSentSneak();

            if (sent) {
                if (++buffer > 1)
                    fail("sprint=" + actionProcessor.isSprinting() + " sneak=" + (actionProcessor.isSneaking()));
            } else if (buffer > 0) buffer -= 0.01D;
        }
    }
}
