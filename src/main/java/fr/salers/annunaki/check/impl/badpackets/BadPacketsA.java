package fr.salers.annunaki.check.impl.badpackets;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.util.PacketUtil;
import lombok.SneakyThrows;

/**
 * @author Salers
 * made on dev.notonweed.annunaki.check.impl.badpackets
 */

@CheckInfo(
        type = "A",
        name = "BadPackets",
        description = "Checks for a missing interact packet.",
        experimental = false,
        maxVl = 30,
        punish = true
)
public class BadPacketsA extends Check {

    private boolean sentInteract, sentInteractAt, sentAttack;

    @SneakyThrows
    @Override
    public void handle(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            final WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);

            switch (wrapper.getAction()) {
                case ATTACK:
                    sentAttack = true;
                    break;
                case INTERACT:
                    sentInteract = true;
                    break;
                case INTERACT_AT:
                    sentInteractAt = true;
                    break;
            }

            if (sentAttack && !sentInteractAt && sentInteract) {
                if (++buffer > 3)
                    fail("buffer=" + buffer);
            } else if (buffer > 0) buffer -= 0.1D;
        } else if (PacketUtil.isFlying(event.getPacketType()))
            sentInteract = sentInteractAt = sentAttack = false;
    }
}
