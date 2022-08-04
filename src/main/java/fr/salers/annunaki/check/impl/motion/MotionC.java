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
        type = "C",
        name = "Motion",
        description = "Checks for repeated up & down motions.",
        experimental = false,
        maxVl = 50,
        punish = true
)

public class MotionC extends Check {

    @Override
    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isPosition(event.getPacketType())) {
            final CollisionProcessor collisionProcessor = data.getCollisionProcessor();
            final PositionProcessor positionProcessor = data.getPositionProcessor();

            final double delta = positionProcessor.getDeltaY();
            final double lastDelta = positionProcessor.getLastDeltaY();

            final boolean exempt = delta == 0 || collisionProcessor.isInLava() ||
                    collisionProcessor.isInWater() || collisionProcessor.isOnClimbable() || collisionProcessor.isBonkingHead();

            if (!exempt && delta == -lastDelta) {
                if (++buffer > 5)
                    fail("delta=" + delta + " lastDelta=" + lastDelta);
            } else if (!exempt && buffer > 0) buffer -= 0.75;

        }
    }
}
