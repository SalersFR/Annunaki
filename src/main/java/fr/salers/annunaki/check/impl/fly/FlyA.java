package fr.salers.annunaki.check.impl.fly;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.data.PlayerData;
import fr.salers.annunaki.data.processor.impl.CollisionProcessor;
import fr.salers.annunaki.data.processor.impl.PositionProcessor;
import fr.salers.annunaki.util.PacketUtil;
import fr.salers.annunaki.util.world.WrappedBlock;
import org.bukkit.Bukkit;

import java.util.Locale;

/**
 * @author Salers
 * made on dev.annunaki.anticheat.check.impl.fly
 */
@CheckInfo(
        type = "Fly",
        name = "A",
        description = "Checks if player isn't following gravity.",
        experimental = false,
        maxVl = 50,
        punish = true
)

public class FlyA extends Check {

    private double prediction;

    public FlyA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isPosition(event.getPacketType())) {
            final CollisionProcessor collisionProcessor = data.getCollisionProcessor();
            final PositionProcessor positionProcessor = data.getPositionProcessor();

            final double delta = positionProcessor.getDeltaY();
            final double lastDelta = positionProcessor.getLastDeltaY();

            //handle multiple versions..
            final double[] minValues = {
                    0.005, /* 1.8 or less*/
                    0.003, /* + 1.9*/
            };

            double lowestOffset = 1;

            for (double mins : minValues) {
                for (boolean zeroZeroThree : new boolean[]{true, false}) {

                    double offset = 69;

                    prediction = (lastDelta - 0.08F) * 0.98F;

                    if(Math.abs(prediction) < mins)
                        prediction = 0;


                    if (zeroZeroThree) {
                        prediction = (prediction - 0.08F) * 0.98F;

                        if(Math.abs(prediction) < mins)
                            prediction = 0;
                    }

                    offset = Math.abs(delta - prediction);

                    if (offset < lowestOffset) {
                        lowestOffset = offset;

                    }
                }
            }

            final double threshold = positionProcessor.isLastSentPosition() ? 0.001 : 0.03125;
            final boolean exempt = collisionProcessor.isInWater()
                    || collisionProcessor.isInLava()
                    || collisionProcessor.isOnClimbable()
                    || collisionProcessor.isInWeb()
                    || data.getTeleportProcessor().getTeleportTicks() <= 2
                    || collisionProcessor.getFenceCollisions().stream().anyMatch(WrappedBlock::isFence)
                    || data.getVelocityProcessor().getVelTicks() <= 2
                    || collisionProcessor.isBonkingHead()
                    || collisionProcessor.isNearSlab()
                    || collisionProcessor.isNearAnvil()
                    || collisionProcessor.isNearStairs()
                    || collisionProcessor.getTicksAlive() < 30;

            if (exempt || collisionProcessor.getClientAirTicks() < 3)
                return;

            if (lowestOffset > threshold)
                if (++buffer > 5)
                    fail("offset=" + lowestOffset + " threshold=" + threshold);
                else if (buffer > 0) buffer -= 0.1;

            if (data.getDebugging().toLowerCase(Locale.ROOT).contains("flya"))
                Bukkit.broadcastMessage("offset=" + (float) lowestOffset);
        }
    }
}
