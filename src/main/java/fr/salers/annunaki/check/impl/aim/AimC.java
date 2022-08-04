package fr.salers.annunaki.check.impl.aim;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.data.processor.impl.RotationProcessor;
import fr.salers.annunaki.util.MathUtil;
import fr.salers.annunaki.util.PacketUtil;

/**
 * @author Salers
 * made on dev.notonweed.annunaki.check.impl.aim
 */

@CheckInfo(
        type = "C",
        name = "Aim",
        description = "Checks for common gcd fixing methods.",
        experimental = false,
        maxVl = 50,
        punish = true
)
public class AimC extends Check {

    @Override
    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isRotation(event.getPacketType())) {
            final RotationProcessor rotationProcessor = data.getRotationProcessor();

            if (Math.abs(rotationProcessor.getDeltaYaw()) > 0 &&
                    Math.abs(rotationProcessor.getDeltaPitch()) > 0 &&
                    rotationProcessor.getAccelYaw() > 1.025 &&
                    rotationProcessor.getAccelYaw() < 17.5) {

                final double gcdPitch = MathUtil.getAbsGcd(rotationProcessor.getDeltaPitch(), rotationProcessor.getLastDeltaPitch()) / MathUtil.EXPANDER;
                final double pitch = rotationProcessor.getPitch();

                final double fixedPitch = pitch - (pitch % gcdPitch);
                final double pitchOffset = Math.abs(pitch - fixedPitch);

                if(Double.toString(pitchOffset).contains("E") && rotationProcessor.getSensitivity() > 10) {
                    if(++buffer > 5)
                        fail("offset=" + pitchOffset);
                } else if(buffer > 0) buffer -= 0.01;


            }

        }
    }
}
