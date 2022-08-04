package fr.salers.annunaki.util.lag;

import com.github.retrooper.packetevents.util.Vector3d;
import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.data.PlayerData;
import fr.salers.annunaki.util.MathUtil;
import lombok.Getter;
import org.bukkit.potion.PotionEffectType;

/**
 * @author Salers
 * made on dev.notonweed.annunaki.util
 */

@Getter
public class ConfirmedVelocity {

    private double x, y, z;

    public ConfirmedVelocity(final Vector3d vector3d) {
        this.x = vector3d.x;
        this.y = vector3d.y;
        this.z = vector3d.z;

    }

    public void handleTick(final PlayerData data, final int ticks) {

        /*
        using lasts, since its delayed from the client
         */
        final float friction = data.getCollisionProcessor().isLastClientOnGround() ?
                (Annunaki.getInstance().getNmsManager().getNmsImplementation().getFriction(data.getPositionProcessor().getLastVectorPos()) * 0.91F)
                : 0.91F;

        final float f = 0.16277136F / (friction * friction * friction);

        //is this right?
        final double moveFactor = data.getCollisionProcessor().isLastClientOnGround() ? getAiMoveSpeed(data) *
                f : (data.getActionProcessor().isSprinting() ? 0.026F : 0.02F);

        x += moveFactor;
        z += moveFactor;


        if (data.getPlayer().hasPotionEffect(PotionEffectType.SPEED)) {
            final int amplifier = MathUtil.getPotionLevel(data.getPlayer(), PotionEffectType.SPEED);
            x = (x * Math.pow(0.9, amplifier)) - 0.01;
            z = (z * Math.pow(0.9, amplifier)) - 0.01;
        }

            /*
            have to check this since this isn't done on the first vel tick
             */
        if (ticks > 0) {

            y -= 0.08;
            y *= 0.98F;

            x *= friction;
            z *= friction;
        }

    }


    private float getAiMoveSpeed(final PlayerData data) {

        final float speed = MathUtil.getPotionLevel(data.getPlayer(), PotionEffectType.SPEED);
        final float slowness = MathUtil.getPotionLevel(data.getPlayer(), PotionEffectType.SLOW);

        double movementFactor = data.getStatusProcessor().getLastAbilities().getFovModifier();

        movementFactor += movementFactor * 0.2F * speed;
        movementFactor += movementFactor * -0.15F * slowness;

        if (data.getActionProcessor().isSprinting())
            movementFactor += movementFactor * 0.3F;

        return (float) movementFactor;
    }


}
