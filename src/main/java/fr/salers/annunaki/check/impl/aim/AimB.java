package fr.salers.annunaki.check.impl.aim;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.data.PlayerData;
import fr.salers.annunaki.data.processor.impl.RotationProcessor;
import fr.salers.annunaki.util.MathUtil;
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
        type = "B",
        name = "Aim",
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

            final double expandedGCD = MathUtil.getAbsGcd(Math.abs(rotationProcessor.getDeltaPitch()),
                    Math.abs(rotationProcessor.getLastDeltaPitch()));
            if(sensitivity != -1 && deltaPitch != 0 && expandedGCD < 131072L) {
                if(++buffer > 5)
                    fail("gcd=" + expandedGCD + " sens=" + sensitivity);
            } else if(sensitivity == -1 && deltaPitch != 0 && expandedGCD > 50000L && distanceX > 7.5 && hitTicks < 4) {
                if(++buffer > 10)
                    fail("gcd=" + expandedGCD + " sens=" + sensitivity);
            } else if(buffer > 0) buffer -= 0.25;

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
