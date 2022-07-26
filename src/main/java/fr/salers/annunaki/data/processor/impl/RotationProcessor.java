package fr.salers.annunaki.data.processor.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import fr.salers.annunaki.data.PlayerData;
import fr.salers.annunaki.data.processor.Processor;
import fr.salers.annunaki.util.MathUtil;
import fr.salers.annunaki.util.PacketUtil;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RotationProcessor extends Processor {
    // Sensitivity stuff inspired by Nova

    private final static List<Float> SENSITIVITIES = new ArrayList<>();

    static {
        for (int mouseX = 346; mouseX < 496; mouseX++) {
            float sliderValue = (float) (mouseX - 350) / (float) 142;

            if (sliderValue < 0 || sliderValue > 1) continue;

            float f = sliderValue * 0.6F + 0.2F;
            float f1 = f * f * f * 8.0F * 0.15F;

            SENSITIVITIES.add(f1);
        }
    }

    private final List<Float> sensitivitySamples = new ArrayList<>();
    private float pitch, lastPitch, yaw, lastYaw,
            deltaPitch, deltaYaw, lastDeltaPitch, lastDeltaYaw, accelYaw, accelPitch, sensitivity;
    private double modulo, closestSensitivity;
    private int sensitivityPercent;

    private double cinematicTicks;

    public RotationProcessor(PlayerData data) {
        super(data);
    }

    @Override
    public void handlePre(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying flying = PacketUtil.getFlyingPacket(event);

            lastPitch = pitch;
            lastYaw = yaw;

            lastDeltaPitch = deltaPitch;
            lastDeltaYaw = deltaYaw;

            if (PacketUtil.isRotation(event.getPacketType())) {

                pitch = flying.getLocation().getPitch();

                yaw = flying.getLocation().getYaw() % 360;

                deltaPitch = pitch - lastPitch;
                deltaYaw = Math.abs(yaw - lastYaw) % 360;

                accelPitch = Math.abs(deltaPitch - lastPitch);
                accelYaw = Math.abs(deltaYaw - lastDeltaYaw);

                if (deltaPitch == 0) return;

                handleCinematic();

                double lowestModulo = Double.MAX_VALUE;
                float closestSensitivity = Float.NaN;

                for (float possibleSensitivity : SENSITIVITIES) {
                    float absDeltaPitch = Math.abs(deltaPitch);

                    if (absDeltaPitch < possibleSensitivity) {
                        float diff = possibleSensitivity - absDeltaPitch;

                        absDeltaPitch += diff * 2;
                    }

                    double modulo = absDeltaPitch % possibleSensitivity;

                    if (modulo < lowestModulo) {
                        lowestModulo = modulo;
                        closestSensitivity = possibleSensitivity;
                    }
                }

                this.closestSensitivity = closestSensitivity;

                modulo = lowestModulo;

                sensitivitySamples.add(closestSensitivity);

                if (sensitivitySamples.size() != 40) return;

                sensitivity = MathUtil.getMode(sensitivitySamples).floatValue();

                float f = (float) Math.cbrt((sensitivity / 0.15F) / 8F);
                float sliderValue = (f - 0.2F) / 0.6F;

                sensitivityPercent = (int) (sliderValue * 200F);

                sensitivitySamples.clear();
            } else {
                deltaPitch = 0;
                deltaYaw = 0;
            }
        }
    }

    private void handleCinematic() {
        if (deltaPitch < 0.33 && deltaPitch > 0)
            cinematicTicks++;
        else if (pitch < 0.92 && pitch > 0)
            cinematicTicks++;
        else {
            cinematicTicks = cinematicTicks <= 0 ? 0 : cinematicTicks / 2;
        }
    }
}
