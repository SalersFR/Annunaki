package fr.salers.annunaki.check.impl.reach;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.data.PlayerData;
import fr.salers.annunaki.data.processor.impl.CollisionProcessor;
import fr.salers.annunaki.util.mc.AxisAlignedBB;
import fr.salers.annunaki.util.mc.MathHelper;
import fr.salers.annunaki.util.mc.MovingObjectPosition;
import fr.salers.annunaki.util.mc.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@CheckInfo(
        type = "B",
        name = "Reach",
        description = "Checks for attacking out of range",
        experimental = true,
        maxVl = 15,
        punish = true
)
public class ReachB extends Check {


    @Override
    public void handle(PacketReceiveEvent event) {
        if(data.getActionProcessor().getAttackTicks() < 3 && (event.getPacketType().equals(PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION) || event.getPacketType().equals(PacketType.Play.Client.PLAYER_POSITION))) {
            CollisionProcessor collision = data.getCollisionProcessor();
            boolean exempt = data.getPlayer().getGameMode() == GameMode.CREATIVE || collision.isTeleporting() || collision.isLastInVehicle() || !(data.getActionProcessor().getLastTarget() instanceof Player);
            if (!exempt) {
                Player target = (Player) data.getActionProcessor().getLastTarget();
                if(Annunaki.getInstance().getPlayerManager().get(target) != null) {
                    PlayerData targetData = Annunaki.getInstance().getPlayerManager().get(target);
                    if (targetData.getCollisionProcessor().isLastInVehicle() || targetData.getCollisionProcessor().isTeleporting()) {
                        return;
                    }

                    AxisAlignedBB targetBox = targetData.getCollisionProcessor().getBoundingBox();
                    targetBox.expand(0.103, 0.1, 0.103);

                    double min = Double.MAX_VALUE;

                    for (float eyeHeight : getEyeHeights()) {
                        // get their eye height
                        Vec3 eyeLoc = getPositionEyes(eyeHeight);

                        // get their head location, one is a fix for MDF which skips a look packet for some reason.
                        Vec3 look, look2;
                        if (event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION) {
                            look2 = this.getVectorForRotation(data.getRotationProcessor().getPitch(), data.getRotationProcessor().getYaw());
                            look = this.getVectorForRotation(data.getRotationProcessor().getPitch(), data.getRotationProcessor().getLastYaw());
                        } else {
                            look2 = this.getVectorForRotation(this.data.getRotationProcessor().getLastPitch(), data.getRotationProcessor().getLastYaw());
                            look = look2;
                        }

                        // raycast the eye location to the target
                        Vec3 rayEnd = eyeLoc.addVector(look.getX() * 6.0D, look.getY() * 6.0D, look.getZ() * 6.0D);
                        Vec3 rayEnd2 = eyeLoc.addVector(look2.getX() * 6.0D, look2.getY() * 6.0D, look2.getZ() * 6.0D);

                        MovingObjectPosition one = targetBox.calculateIntercept(eyeLoc, rayEnd);
                        MovingObjectPosition two = targetBox.calculateIntercept(eyeLoc, rayEnd2);

                        if (one != null && two != null) {
                            double m = eyeLoc.distanceTo(one.hitVec);
                            double d33 = rayEnd.distanceTo(two.hitVec);

                            debug(Bukkit.broadcastMessage("m: " + m + ", d33: " + d33));


                            min = Math.min(min, Math.min(d33, m));
                        }

                        // Bukkit limits reach to 6, so the ray is probably infinite.
                        if(min > 6.0D) {
                            min = 0;
                            break;
                        }

                        // They're inside eachother :flushed:
                        if (data.getCollisionProcessor().getBoundingBox().intersectsWith(targetBox)) {
                            min = 0;
                            break;
                        }
                    }

                    // reach is 3.0
                    if (min > 3.0D) {
                        // buffer to stop falses
                        if (buffer++ > 2) {
                            fail("reach: " + min);
                            buffer = 0;
                        }
                    } else {
                        buffer = Math.max(0, buffer - 0.1);
                    }
                }

            }
        }
    }

    public float[] getEyeHeights() {

        if(data.getVersion().getIntVersion() < 9) {
            return new float[]{1.62f};
        } else if(data.getVersion().getIntVersion() < 14 ) {
            return new float[] { 0.4f, 1.54f, 1.62f };
        } else {
            return new float[] { 0.4f, 1.27f, 1.62f };
        }
    }

    protected final Vec3 getVectorForRotation(float pitch, float yaw)
    {
        float var3 = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
        float var4 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float var5 = -MathHelper.cos(-pitch * 0.017453292F);
        float var6 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3(var4 * var5, var6, var3 * var5);
    }

    public Vec3 getPositionEyes(float eyeHeight)
    {
        return new Vec3(data.getPositionProcessor().getX(), data.getPositionProcessor().getY() + (double)eyeHeight, data.getPositionProcessor().getZ());
    }

    private double getBlockReachDistance(PlayerData pd) {
        return data.getPlayer().getGameMode() == GameMode.CREATIVE ? 5.0D : data.getPlayer().getGameMode() == GameMode.ADVENTURE
                ? 3.0D : data.getPlayer().getGameMode() == GameMode.SURVIVAL ? 3.0D : 5.0D;
    }
}
