package fr.salers.annunaki.check.impl.motion;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.data.processor.impl.CollisionProcessor;
import fr.salers.annunaki.data.processor.impl.PositionProcessor;
import fr.salers.annunaki.util.MathUtil;
import fr.salers.annunaki.util.PacketUtil;
import org.bukkit.potion.PotionEffectType;

/**
 * @author Salers
 * made on fr.salers.annunaki.check.impl.motion
 */

@CheckInfo(
        type = "A",
        name = "Motion",
        description = "Checks for an invalid jump motion.",
        experimental = false,
        maxVl = 50,
        punish = true
)

public class MotionA extends Check {

    @Override
    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isPosition(event.getPacketType())) {
            final PositionProcessor positionProcessor = data.getPositionProcessor();
            final CollisionProcessor collisionProcessor = data.getCollisionProcessor();

            final double jumpMotion = 0.42F;
            final double fixedJumpMotion = data.getPlayer().hasPotionEffect(PotionEffectType.JUMP) ?
                    jumpMotion + MathUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1f : jumpMotion;

            final double delta = positionProcessor.getDeltaY();
            final boolean exempt = collisionProcessor.isBonkingHead() || collisionProcessor.isOnSlime() || collisionProcessor.isLastOnSlime()
                    || collisionProcessor.isNearPiston() || data.getVelocityProcessor().getVelTicks() < 5 || collisionProcessor.isNearStairs() || collisionProcessor.isNearSlab() || collisionProcessor.isLastInVehicle();


            if (delta > 0 && collisionProcessor.getClientAirTicks() == 1 && !exempt && delta != fixedJumpMotion) {
                if (++buffer > 1) {
                    fail("delta=" + (float) delta + " motion=" + fixedJumpMotion);
                }
            } else if (buffer > 0) buffer -= 0.5;


        }
    }
}
