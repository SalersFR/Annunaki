package fr.salers.annunaki.check.impl.speed;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.data.PlayerData;
import fr.salers.annunaki.data.processor.impl.CollisionProcessor;
import fr.salers.annunaki.data.processor.impl.PositionProcessor;
import fr.salers.annunaki.util.MathUtil;
import fr.salers.annunaki.util.PacketUtil;
import fr.salers.annunaki.util.lag.ConfirmedVelocity;
import fr.salers.annunaki.util.mc.MathHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.potion.PotionEffectType;

/**
 * @author Salers
 * made on dev.notonweed.annunaki.check.impl.speed
 */

@CheckInfo(
        type = "Speed",
        name = "A",
        description = "Checks if player isn't following friction.",
        experimental = false,
        maxVl = 50,
        punish = true
)
public class SpeedA extends Check {

    private double clientMotion;

    public SpeedA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isPosition(event.getPacketType())) {

            final PositionProcessor positionProcessor = data.getPositionProcessor();
            final CollisionProcessor collisionProcessor = data.getCollisionProcessor();

            //getting last value from the player, so we can apply client movement calculation to it
            clientMotion = positionProcessor.getLastDeltaXZ();

            //this will multiply the motion every tick, to slow the player
            //also using last client ground, and last position
            //because those are delayed from 1 tick compared to what the client uses
            final float friction = collisionProcessor.isLastClientOnGround() ?
                    (float) (getBlockFriction(positionProcessor.getLastVectorPos().toLocation(data.getPlayer().getWorld()))) * 0.91F : 0.91F;

            final float d = 0.16277136F / (friction * friction * friction);

            //this will be added to the motion every tick
            final double moveFactorAdd = collisionProcessor.isLastClientOnGround() ? getAiMoveSpeed() *
                    d : (data.getActionProcessor().isSprinting() ? 0.026F : 0.02F);

            //actually applying these values
            clientMotion *= friction;
            clientMotion += moveFactorAdd;

            //how high the player should jump
            final double jumpValue = 0.42F + (data.getPlayer().hasPotionEffect(PotionEffectType.JUMP) ?
                    MathUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1F : 0);

            //accurate check for jump
            //could also make some lows hops flags
            if ((!collisionProcessor.isClientOnGround() && Math.abs(jumpValue - positionProcessor.getDeltaY()) <= 0.001)
                    || (positionProcessor.getDeltaXZ() > clientMotion && (collisionProcessor.isBonkingHead() || collisionProcessor.isLastBonkingHead()))) {
                final float yawAdd = (float) data.getRotationProcessor().getYaw() * 0.017453292F;
                clientMotion += Math.hypot(-(double) (MathHelper.sin(yawAdd) * 0.2F), (double) (MathHelper.cos(yawAdd) * 0.2F));
            }

            //in the case player took velocity we add the values to our prediction
            //i know we could predict movement directly using the velocity value,
            //but it would be useless since this check isn't very accurate
            if (data.getVelocityProcessor().getVelTicks() <= 5 && data.getVelocityProcessor().getVelocities().size() > 0) {
                final ConfirmedVelocity velocity = data.getVelocityProcessor().getVelocities().peekLast();
                clientMotion += Math.hypot(velocity.getX(), velocity.getZ()) * 1.2;
            }

            //when the player land he accelerates a bit
            //since i'm lazy to do a proper fix i do that
            if (collisionProcessor.getClientGroundTicks() > 0 && collisionProcessor.getClientGroundTicks() < 3)
                clientMotion += 0.3 - (collisionProcessor.getClientGroundTicks() / 20D);

            //player could get hit by a piston or bonk his head
            //which can make him false flags our check
            //so instead of exempting, we try to account without creating bypasses
            if (collisionProcessor.isLastNearPiston() || collisionProcessor.isNearPiston() ||
                    collisionProcessor.isBonkingHead() || collisionProcessor.isLastBonkingHead()
            || collisionProcessor.isNearStairs() || collisionProcessor.isLastNearStairs() || collisionProcessor.isNearSlab() ||
            collisionProcessor.isLastNearSlab())
                clientMotion *= 1.5;

            if(positionProcessor.getDeltaXZ() > clientMotion && positionProcessor.getDeltaY() <= 0)
                clientMotion *= 2;

            //the speed difference between we expect the player to move, and what he actually moved
            final double ratio = (positionProcessor.getDeltaXZ() - clientMotion);

            if (ratio > 0.0005 && positionProcessor.getDeltaXZ() > 0.1 && data.getTeleportProcessor().getTeleportTicks() > 1) {
                //don't make the buffer redundant
                buffer += buffer < 6.0 ? 1.0 : 5.0E-5;
                if (buffer > 3.0)
                    fail("exceeded maximum predicted movement, " + (ratio * 100.0) + "% faster than normal");

            } else if (buffer > 0) buffer -= 0.085D;


        }
    }

    private double getBlockFriction(Location loc) {
        Block block = loc.getBlock();
        return block.getType() == Material.PACKED_ICE ||
                block.getType() == Material.ICE ? 0.98F :
                block.getType().toString().contains("SLIME") ? 0.8F : 0.6F;
    }

    private float getAiMoveSpeed() {

        final float speed = MathUtil.getPotionLevel(data.getPlayer(), PotionEffectType.SPEED);
        final float slowness = MathUtil.getPotionLevel(data.getPlayer(), PotionEffectType.SLOW);

        double movementFactor = data.getStatusProcessor().getLastAbilities().getFovModifier();

        movementFactor += movementFactor * 0.2F * speed;
        movementFactor += movementFactor * -0.15F * slowness;

        //sprint desync idc
        movementFactor += movementFactor * 0.3F;

        return (float) movementFactor;
    }
}
