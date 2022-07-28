package fr.salers.annunaki.check.impl.aim;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.data.PlayerData;
import fr.salers.annunaki.data.processor.impl.RotationProcessor;
import fr.salers.annunaki.util.MathUtil;
import fr.salers.annunaki.util.PacketUtil;
import org.bukkit.Bukkit;

/**
 * @author Salers
 * made on dev.notonweed.annunaki.check.impl.aim
 */

@CheckInfo(
        type = "C",
        name = "Aim",
        description = "Checks for incoherent rotation values.",
        experimental = false,
        maxVl = 50,
        punish = true
)
public class AimC extends Check {

    public AimC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isRotation(event.getPacketType())) {
            final RotationProcessor rotationProcessor = data.getRotationProcessor();

            if (Math.abs(rotationProcessor.getDeltaYaw()) > 0 &&
                    Math.abs(rotationProcessor.getDeltaPitch()) > 0 &&
                    rotationProcessor.getAccelYaw() > 1.025 &&
                    rotationProcessor.getAccelYaw() < 17.5) {

                final double gcdPitch = MathUtil.getGcd(rotationProcessor.getDeltaPitch(), rotationProcessor.getLastDeltaPitch());
                final double gcdYaw = MathUtil.getGcd(rotationProcessor.getDeltaYaw(), rotationProcessor.getLastDeltaYaw());

                if (gcdYaw == 0 && gcdPitch == 0) return;

                final double yawSens = (Math.cbrt(gcdYaw / 0.8 / 0.15) - 0.2) / 0.6;
                final double pitchSens = (Math.cbrt(gcdPitch / 0.8 / 0.15) - 0.2) / 0.6;

                final double ratio = yawSens / pitchSens;

                if (ratio > 8.75) {
                    if (++buffer > 4)
                        fail("ratio=" + ratio);
                } else if (buffer > 0) buffer -= 0.05D;

                if (data.getDebugging().equalsIgnoreCase("AimC"))
                    Bukkit.broadcastMessage("ratio=" + ratio + " buffer=" + buffer);
            }

        }
    }
}
