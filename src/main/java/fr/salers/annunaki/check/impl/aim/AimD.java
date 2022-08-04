package fr.salers.annunaki.check.impl.aim;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.data.processor.impl.RotationProcessor;
import fr.salers.annunaki.util.PacketUtil;

/**
 * @author Salers
 * made on dev.annunaki.anticheat.check.impl.aim
 */

@CheckInfo(
        type = "D",
        name = "Aim",
        description = "Checks for rounded rotation values.",
        experimental = false,
        maxVl = 50,
        punish = true
)
public class AimD extends Check {

    @Override
    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isRotation(event.getPacketType())) {
            final RotationProcessor rotationProcessor = data.getRotationProcessor();

            final double[] mods = {0.5, 1};

            final double deltaYaw = rotationProcessor.getDeltaYaw();
            final double deltaPitch = rotationProcessor.getDeltaPitch();

            if(rotationProcessor.getCinematicTicks() < 1) {
                for (double mod : mods) {
                    if ((deltaPitch % mod == 0 || deltaYaw % mod == 0) && deltaPitch != 0 && deltaYaw != 0) {
                        if (++buffer > 4)
                            fail("deltaYaw=" + deltaYaw + " deltaPitch=" + deltaPitch + " mod=" + mod);
                    } else if (buffer > 0) buffer -= 0.5;
                }
            }


        }
    }
}
