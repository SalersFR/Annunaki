package fr.salers.annunaki.check.impl.aim;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
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
        type = "A",
        name = "Aim",
        description = "Checks for flaws in aim modules.",
        experimental = false,
        maxVl = 50,
        punish = true
)
public class AimA extends Check {

    private int hitTicks;

    @Override
    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isRotation(event.getPacketType())) {
            final RotationProcessor rotationProcessor = data.getRotationProcessor();


            if (Math.abs(rotationProcessor.getDeltaPitch()) > 0 &&
                    hitTicks <= 5 &&
                    rotationProcessor.getAccelYaw() > 1.25 &&
                    rotationProcessor.getAccelYaw() < 22.5 &&
                    rotationProcessor.getDeltaYaw() > 7.5 &&
                    data.getPositionProcessor().getDeltaXZ() > 0.12
            ) {

                final double gcd = MathUtil.getGcd(rotationProcessor.getDeltaPitch(), rotationProcessor.getLastDeltaPitch());
                final double tickSensitivity = (Math.cbrt(gcd / 0.8 / 0.15) - 0.2) / 0.6;

                final float smoothed = (float) tickSensitivity * 0.6F + 0.2F;
                final float result = (float) (Math.pow(smoothed, 3) * 1.2F);

                if (result < 0.05 && tickSensitivity != 0 && gcd != 0) {
                    if (++buffer > 5)
                        fail("result=" + result);
                } else if (buffer > 0 && tickSensitivity != 0) buffer -= 0.1D;

            }

        } else if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            if(data.getActionProcessor().getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK)
                hitTicks = 0;
        } else if (PacketUtil.isFlying(event.getPacketType())) {
            hitTicks++;
        }
    }
}
