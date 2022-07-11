package fr.salers.annunaki.check.impl.aim;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.data.PlayerData;
import fr.salers.annunaki.data.processor.impl.RotationProcessor;
import fr.salers.annunaki.util.PacketUtil;
import lombok.SneakyThrows;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Salers
 * made on dev.notonweed.annunaki.check.impl.aim
 * <p>
 * orignally made by BitBot25, took w/ permission & upgraded by Salers
 */

@CheckInfo(
        type = "Aim",
        name = "B",
        description = "Checks for switch-aiming.",
        experimental = false,
        maxVl = 50,
        punish = true
)
public class AimB extends Check {


    private final Set<Integer> candidates = new HashSet<>();
    private int hitTicks;
    private int s, sensitivity;


    public AimB(PlayerData data) {
        super(data);
    }

    @SneakyThrows
    @Override
    public void handle(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            final WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);

            final RotationProcessor rotationProcessor = data.getRotationProcessor();

            final Player damager = data.getPlayer();
            final Entity victim = data.getPlayer().getNearbyEntities(20, 20, 20).stream().
                    filter(entity -> entity.getEntityId() == wrapper.getEntityId()).findFirst().get();

            final double x1 = damager.getEyeLocation().getX();
            final double z1 = damager.getEyeLocation().getZ();
            final double vdX = damager.getEyeLocation().getDirection().getX();
            final double vdZ = damager.getEyeLocation().getDirection().getZ();
            final double x2 = victim.getLocation().getX();
            final double z2 = victim.getLocation().getZ();

            final double dotProduct = vdX * (x2 - x1) + vdZ * (z2 - z1);
            final double avMod = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(z2 - z1, 2));
            final double vdMod = Math.sqrt(vdX * vdX + vdZ * vdZ);

            final double cosAngle = dotProduct / (avMod * vdMod);
            final int angle = (int) Math.toDegrees(Math.acos(cosAngle));

            final double reach = /*data.getReachProcessor.getReach();*/ damager.getEyeLocation().toVector().clone().
                    setY(0).distance(victim.getLocation().toVector().clone().setY(0));

            final double accel = rotationProcessor.getAccelYaw();

            if (reach > 1.5 && angle > 50 && (accel <= 6.25 || accel > 90 || sensitivity == -1) && hitTicks > 2) {
                if (++buffer > 1)
                    fail("angle=" + angle + " accel=" + accel);
            } else if (buffer > 0) buffer -= 0.01;

            hitTicks = 0;


        } else if (PacketUtil.isRotation(event.getPacketType())) {
            final RotationProcessor rotationProcessor = data.getRotationProcessor();

            final float pitch = rotationProcessor.getPitch();
            final float lPitch = rotationProcessor.getLastPitch();

            final float yaw = rotationProcessor.getYaw();
            final float lYaw = rotationProcessor.getLastYaw();

            final float deltaPitch = rotationProcessor.getDeltaPitch();

            if (Math.abs(rotationProcessor.getPitch()) != 90.0f) {
                final double error = Math.max(Math.abs(pitch), Math.abs(lPitch)) * 3.814697265625E-6;
                computeSensitivity(deltaPitch, error);
            }

            final float distanceX = circularDistance(yaw, lYaw);
            final double error = Math.max(Math.abs(yaw), Math.abs(lYaw)) * 3.814697265625E-6;

            computeSensitivity(distanceX, error);

            if (candidates.size() == 1) {
                s = candidates.iterator().next();
                sensitivity = 200 * s / 143;
            } else {
                sensitivity = -1;
                forEach(candidates::add);
            }

        } else if (PacketUtil.isFlying(event.getPacketType()))
            hitTicks++;
    }

    public void computeSensitivity(double delta, double error) {
        final double start = delta - error;
        final double end = delta + error;
        forEach(s -> {
            final double f0 = ((double) s / 142.0) * 0.6 + 0.2;
            final double f = (f0 * f0 * f0 * 8.0) * 0.15;
            final int pStart = (int) Math.ceil(start / f);
            final int pEnd = (int) Math.floor(end / f);
            if (pStart <= pEnd) {
                for (int p = pStart; p <= pEnd; p++) {
                    final double d = p * f;
                    if (!(d >= start && d <= end)) {
                        candidates.remove(s);
                    }
                }
            } else {
                candidates.remove(s);
            }
        });
    }

    public float circularDistance(float a, float b) {
        float d = Math.abs(a % 360.0f - b % 360.0f);
        return d < 180.0f ? d : 360.0f - d;
    }

    public void forEach(Consumer<Integer> consumer) {
        for (int s = 0; s <= 143; s++) {
            consumer.accept(s);
        }
    }
}
