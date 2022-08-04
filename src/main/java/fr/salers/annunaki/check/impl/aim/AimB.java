package fr.salers.annunaki.check.impl.aim;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.util.MathUtil;
import fr.salers.annunaki.util.PacketUtil;
import lombok.SneakyThrows;

/**
 * @author Salers
 * made on dev.notonweed.annunaki.check.impl.aim
 * <p>
 * orignally made by BitBot25, took w/ permission & upgraded by Salers
 */

@CheckInfo(
        type = "B",
        name = "Aim",
        description = "Checks for switch-aiming.",
        experimental = false,
        maxVl = 50,
        punish = true
)


public class AimB extends Check {

    private double deltaY, deltaXY;

    private int hitTicks;

    @SneakyThrows
    @Override
    public void handle(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            if(data.getActionProcessor().getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK)
                hitTicks = 0;
        } else if (PacketUtil.isRotation(event.getPacketType())) {
            deltaXY = Math.hypot(getExpiermentalDeltaX(), getExpiermentalDeltaY());

            if(hitTicks < 6 && data.getActionProcessor().getLastTarget() != null) {
                double[] offset = MathUtil.getOffsetFromLocation(data.getPlayer().getLocation(), data.getActionProcessor().getLastTarget().getLocation());

                if(Math.abs(offset[0]) > 30 || Math.abs(offset[1]) > 60) {
                    return;
                }

                double accel = Math.abs(deltaY - deltaXY);

                if(accel > 200) {
                    buffer++;
                } else if(accel > 150) {
                    buffer+=0.5f;
                } else if(buffer > 0) buffer-= 0.2f;

                if(buffer > 4) {
                    fail(String.format("d=%.4f p=%.4f b=%s yo=%.1f po=%.1f", accel, deltaXY, buffer, offset[0], offset[1]));
                    buffer /= 2;
                }

            }
            deltaY = deltaXY;
        } else if (PacketUtil.isFlying(event.getPacketType()))
            hitTicks++;
    }

    public float getExpiermentalDeltaX() {
        float deltaYaw = data.getRotationProcessor().getDeltaYaw();
        float sens = data.getRotationProcessor().getSensitivity();
        float f = sens * 0.6f + .2f;
        float calc = f * f * f * 8;

        return deltaYaw / (calc * .15f);
    }

    public float getExpiermentalDeltaY() {
        float deltaPitch = data.getRotationProcessor().getDeltaPitch();
        float sens = data.getRotationProcessor().getSensitivity();
        float f = sens * 0.6f + .2f;
        float calc = f * f * f * 8;

        return deltaPitch / (calc * .15f);
    }
}
