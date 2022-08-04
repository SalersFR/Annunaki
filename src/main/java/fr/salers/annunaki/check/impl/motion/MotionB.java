package fr.salers.annunaki.check.impl.motion;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.data.processor.impl.CollisionProcessor;
import fr.salers.annunaki.data.processor.impl.PositionProcessor;
import fr.salers.annunaki.util.PacketUtil;

/**
 * @author Salers
 * made on fr.salers.annunaki.check.impl.motion
 */

@CheckInfo(
        type = "B",
        name = "Motion",
        description = "Checks for repeated horizontal motions in air.",
        experimental = false,
        maxVl = 50,
        punish = true
)

public class MotionB extends Check {
    @Override
    public void handle(PacketReceiveEvent event) {
        if(PacketUtil.isPosition(event.getPacketType())) {
            final PositionProcessor positionProcessor = data.getPositionProcessor();
            final CollisionProcessor collisionProcessor = data.getCollisionProcessor();

            final double accel = Math.abs(positionProcessor.getDeltaXZ() - positionProcessor.getLastDeltaXZ());
            final boolean exempt = collisionProcessor.isInWater() || collisionProcessor.isInLava() ||
                    collisionProcessor.isInWeb() || positionProcessor.getDeltaXZ() < 0.2;

            if(collisionProcessor.getClientAirTicks() > 2 && !exempt) {
                if(accel < 1.0E-5) {
                    if(++buffer > 2)
                        fail("accel=" + accel);
                }
            } else if(buffer > 0) buffer -= 0.025;
        }
    }
}
