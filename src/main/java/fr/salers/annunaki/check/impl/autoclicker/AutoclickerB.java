package fr.salers.annunaki.check.impl.autoclicker;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.util.ClickingStats;
import fr.salers.annunaki.util.PacketUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Salers
 * made on dev.annunaki.anticheat.check.impl.autoclicker
 */

@CheckInfo(
        type = "B",
        name = "Autoclicker",
        description = "Checks for repeated & consistent stats.",
        experimental = false,
        maxVl = -1,
        punish = false
)
public class AutoclickerB extends Check {

    private final List<Integer> delays = new LinkedList<>();
    private int updates;
    private double lastStd, lastEntropy;

    @Override
    public void handle(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.ANIMATION) {

            if (delays.size() >= 60) {
                final ClickingStats stats = new ClickingStats(delays);

                final double entropy = stats.getEntropy();
                final double std = stats.getStd();

                if (std < 6 && entropy < 1.75 && (entropy % lastEntropy) < 0.01 && (std % lastStd) < 0.05 &&
                        stats.getCps() >= 9.25D) {
                    if (++buffer > 5)
                        fail(" std=" + std + " ent=" + entropy);
                } else if (buffer > 0) buffer -= 0.04;

                debug("std=" + std + " ent=" + entropy);

                lastStd = std;
                lastEntropy = entropy;

                delays.clear();


            }


            if (data.getActionProcessor().getDigTicks() > 10 && updates <= 3)
                delays.add(updates);

            updates = 0;

        } else if (PacketUtil.isFlying(event.getPacketType())) {
            updates++;
        }
    }
}
