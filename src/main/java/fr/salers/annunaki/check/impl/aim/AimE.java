package fr.salers.annunaki.check.impl.aim;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.data.processor.impl.RotationProcessor;
import fr.salers.annunaki.util.MathUtil;
import fr.salers.annunaki.util.PacketUtil;

/**
 * @author Salers
 * made on fr.salers.annunaki.check.impl.aim
 */

@CheckInfo(
        type = "E",
        name = "Aim",
        description = "Checks for unlikely pitch rotations.",
        experimental = false,
        maxVl = 50,
        punish = true
)
public class AimE extends Check {

private double lastPitchAtan, result;

    @Override
    public void handle(PacketReceiveEvent event) {
        if(PacketUtil.isRotation(event.getPacketType())) {
            final RotationProcessor rotationProcessor = data.getRotationProcessor();
            //consistent or very huge rotations could make this check false
            //so we just don't check when we can't
            if (rotationProcessor.getAccelYaw() < 1.25 ||
                    rotationProcessor.getAccelYaw() > 30.25 ||
                    rotationProcessor.getDeltaPitch() > 20
            ) return;

            final double gcd = Math.abs(MathUtil.getGcd(rotationProcessor.getDeltaPitch() * MathUtil.EXPANDER, rotationProcessor.getLastDeltaPitch() * MathUtil.EXPANDER));

            //if player's following gcd, and he's making somewhat consistent rotations
            //plus he meets all conditions to check, then we could flag
            if (Math.min(this.lastPitchAtan, Math.atan(rotationProcessor.getPitch())) == this.result &&
                    gcd < 0x20000 &&
                    gcd > 0) {
                if (this.buffer < 7) buffer++;
                if (this.buffer > 5.25)
                    fail("result= " + (float) result + " lastpitchan=" + (float) lastPitchAtan);
            } else this.buffer -= this.buffer > 0 ? 0.2 : 0;


            this.result = Math.min(this.lastPitchAtan, Math.atan(rotationProcessor.getPitch()));
            this.lastPitchAtan = Math.atan(rotationProcessor.getPitch());


        }

    }
}
