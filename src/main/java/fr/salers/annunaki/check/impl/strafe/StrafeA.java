package fr.salers.annunaki.check.impl.strafe;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.data.processor.impl.CollisionProcessor;
import fr.salers.annunaki.data.processor.impl.PositionProcessor;
import fr.salers.annunaki.util.PacketUtil;

/**
 * @author Salers
 * made on dev.annunaki.anticheat.check.impl.strafe
 */
@CheckInfo(
        type = "A",
        name = "Strafe",
        description = "Checks if player isn't following friction on ground.",
        experimental = false,
        maxVl = 50,
        punish = true
)
public class StrafeA extends Check {

    @Override
    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) {
            boolean exempt = data.getPlayer().isFlying();
            if(!exempt) {
                final PositionProcessor positionProcessor = data.getPositionProcessor();
                final CollisionProcessor collisionProcessor = data.getCollisionProcessor();

                final double deltaX = positionProcessor.getDeltaX();
                final double deltaZ = positionProcessor.getDeltaZ();

                final double predictedX = positionProcessor.getLastDeltaX() * 0.91f;
                final double predictedZ = positionProcessor.getLastDeltaZ() * 0.91f;

                final double offsetX = Math.abs(deltaX - predictedX);
                final double offsetZ = Math.abs(deltaZ - predictedZ);

                if (collisionProcessor.getClientAirTicks() > 2 && positionProcessor.getDeltaXZ() > 0.1 && data.getActionProcessor().getAttackTicks() > 3) {
                    if (offsetX > 0.026F || offsetZ > 0.026F) {
                        if (++buffer > 3)
                            fail("offsetX=" + offsetX + " offsetZ=" + offsetZ);
                    } else if (buffer > 0) buffer -= 0.3;

                }
            }
        }
    }
}
