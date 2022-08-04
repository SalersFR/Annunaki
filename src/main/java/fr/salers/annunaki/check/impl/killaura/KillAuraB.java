package fr.salers.annunaki.check.impl.killaura;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.data.processor.impl.PositionProcessor;
import fr.salers.annunaki.util.PacketUtil;

/**
 * @author Salers
 * made on fr.salers.annunaki.check.impl.killaura
 */

@CheckInfo(
        type = "B",
        name = "KillAura",
        description = "Checks for aura like ground-moves.",
        experimental = false,
        maxVl = 50,
        punish = true
)

public class KillAuraB extends Check {

    private int hitTicks;

    @Override
    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) {
            final String dir = getCardinalDirection();
            final PositionProcessor positionProcessor = data.getPositionProcessor();

            if (hitTicks < 4 && data.getVelocityProcessor().getVelTicks() > 4 && (positionProcessor.getDeltaXZ() > 0.23
                    || data.getActionProcessor().isSprinting())) {
                switch (dir) {
                    case "S":
                        if (positionProcessor.getDeltaZ() < -0.065 && !(Math.abs(positionProcessor.getDeltaZ()) <= 0.04)) {
                            if (++buffer > 4) {
                                fail("deltaZ=" + positionProcessor.getDeltaZ());
                            }
                        } else if (buffer > 0) buffer -= 0.5;
                        break;
                    case "N":
                        if (positionProcessor.getDeltaZ() > 0.065 && !(Math.abs(positionProcessor.getDeltaZ()) <= 0.04)) {
                            if (++buffer > 4) {
                                fail("deltaZ=" + positionProcessor.getDeltaZ());
                            }
                        } else if (buffer > 0) buffer -= 0.5;
                        break;
                    case "E":
                        if (positionProcessor.getDeltaX() < -0.065 && !(Math.abs(positionProcessor.getDeltaX()) <= 0.04)) {
                            if (++buffer > 4) {
                                fail("deltaX=" + positionProcessor.getDeltaX());
                            }
                        } else if (buffer > 0) buffer -= 0.25;
                        break;
                    case "W":
                        if (positionProcessor.getDeltaX() > 0.065 && !(Math.abs(positionProcessor.getDeltaX()) <= 0.04)) {
                            if (++buffer > 4) {
                                fail("deltaX=" + positionProcessor.getDeltaX());
                            }
                        } else if (buffer > 0) buffer -= 0.25;
                        break;
                }
            }

            hitTicks++;
        } else if(event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            if(data.getActionProcessor().getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                hitTicks = 0;
            }
        }
    }

    public String getCardinalDirection() {

        double rotation = (data.getRotationProcessor().getYaw() - 90.0F) % 360.0F;

        if (rotation < 0.0D) {
            rotation += 360.0D;
        }
        if ((0.0D <= rotation) && (rotation < 45.0D))
            return "W";
        if ((45.0D <= rotation) && (rotation < 135.0D))
            return "N";
        if ((135.0D <= rotation) && (rotation < 225.0D))
            return "E";
        if ((225.0D <= rotation) && (rotation < 315.0D))
            return "S";
        if ((315.0D <= rotation) && (rotation < 360.0D)) {
            return "W";
        }
        return null;
    }
}
