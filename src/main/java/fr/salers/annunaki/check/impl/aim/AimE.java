package fr.salers.annunaki.check.impl.aim;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.data.PlayerData;
import fr.salers.annunaki.data.processor.impl.RotationProcessor;
import fr.salers.annunaki.util.MathUtil;
import fr.salers.annunaki.util.PacketUtil;
import org.bukkit.Bukkit;

/**
 * @author Salers
 * made on fr.salers.annunaki.check.impl.aim
 */

@CheckInfo(
        type = "E",
        name = "Aim",
        description = "Checks for smalls rotations.",
        experimental = false,
        maxVl = 50,
        punish = true
)
public class AimE extends Check {



    public AimE(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if(PacketUtil.isRotation(event.getPacketType())) {

            final RotationProcessor rotationProcessor = data.getRotationProcessor();
            final double deltaYaw = rotationProcessor.getAccelYaw();
            final double expandedGCD = MathUtil.getAbsGcd(Math.abs(rotationProcessor.getDeltaPitch()),
                    Math.abs(rotationProcessor.getLastDeltaPitch()));

            if(expandedGCD > 130172L && deltaYaw != 0 && deltaYaw < 1.0E-8) {
                if(++buffer > 3)
                    fail("delta=" + deltaYaw);
            } else if(buffer > 0) buffer -= deltaYaw * 25f;


        }

    }
}
