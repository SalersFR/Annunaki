package fr.salers.annunaki.check.impl.strafe;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.data.PlayerData;
import fr.salers.annunaki.data.processor.impl.CollisionProcessor;
import fr.salers.annunaki.data.processor.impl.PositionProcessor;
import fr.salers.annunaki.util.PacketUtil;

/**
 * @author Salers
 * made on dev.annunaki.anticheat.check.impl.strafe
 */
@CheckInfo(
        type = "Strafe",
        name = "A",
        description = "Checks if player isn't following friction on ground.",
        experimental = false,
        maxVl = 50,
        punish = true
)
public class StrafeA extends Check {

    private int hitTicks;

    public StrafeA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) {
            final PositionProcessor positionProcessor = data.getPositionProcessor();
            final CollisionProcessor collisionProcessor = data.getCollisionProcessor();

            final double deltaX = positionProcessor.getDeltaX();
            final double deltaZ = positionProcessor.getDeltaZ();

            final double predictedX = positionProcessor.getLastDeltaX() * 0.91f;
            final double predictedZ = positionProcessor.getLastDeltaZ() * 0.91f;

            final double offsetX = Math.abs(deltaX - predictedX);
            final double offsetZ = Math.abs(deltaZ - predictedZ);

            if (collisionProcessor.getClientAirTicks() > 2 && positionProcessor.getDeltaXZ() > 0.1 && ++hitTicks > 3) {
                if (offsetX > 0.026F || offsetZ > 0.026F) {
                    if (++buffer > 3)
                        fail("offsetX=" + offsetX + " offsetZ=" + offsetZ);
                } else if (buffer > 0) buffer -= 0.3;

            }

        } else if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            if (new WrapperPlayClientInteractEntity(event).getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK)
                hitTicks = 0;
        }
    }
}
