package fr.salers.annunaki.check.impl.velocity;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.data.processor.impl.CollisionProcessor;
import fr.salers.annunaki.util.PacketUtil;
import org.bukkit.Bukkit;

/**
 * @author Salers
 * made on dev.notonweed.annunaki.check.impl.velocity
 */

@CheckInfo(
        type = "A",
        name = "Velocity",
        description = "Checks for vertical velocity modifications.",
        experimental = true,
        maxVl = 30,
        punish = true
)
public class VelocityA extends Check {

    @Override
    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) {

            final CollisionProcessor collisionProcessor = data.getCollisionProcessor();

            if (data.getVelocityProcessor().getVelocities().isEmpty()) return;

            final double velocity = data.getVelocityProcessor().getLastVelocity().getY();
            final double delta = data.getPositionProcessor().getDeltaY();

            final double ratio = (delta / velocity) * 100.D;
            final boolean exempt = delta == 0.42F ||
                    collisionProcessor.isInWeb() || collisionProcessor.isBonkingHead() ||
                    collisionProcessor.isInWater() || collisionProcessor.isInLava();

            if ((ratio < 99.999D || ratio > 100.05) && !exempt && data.getVelocityProcessor().getVelTicks() <= 1 && ratio > 0) {
                if (buffer++ > 2)
                    fail("ratio= " + ratio);
            } else if (buffer > 0 && !exempt && data.getVelocityProcessor().getVelTicks() <= 1) buffer -= 0.025D;

            if (!exempt && data.getVelocityProcessor().getVelTicks() == 1 && data.getDebugging().contains("Velocity") && data.getDebugging().contains("A")) {
                Bukkit.broadcastMessage(((ratio > 100.5 || ratio < 99.99) ? "§c" : "") + "ratio=" + ratio);
            }

        }
    }
}
