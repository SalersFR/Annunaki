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
        type = "C",
        name = "Autoclicker",
        description = "Checks for an impossible consistency.",
        experimental = false,
        maxVl = -1,
        punish = false
)
public class AutoclickerC extends Check {

    private final List<Integer> delays = new LinkedList<>();
    private int updates;

    @Override
    public void handle(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.ANIMATION) {

            if (delays.size() >= 60) {
                final ClickingStats stats = new ClickingStats(delays);

                final double std = stats.getStd();
                final double entropy = stats.getEntropy();

                final int duplicates = stats.getDuplicates();
                final int outliers = stats.getOutliers();

                if (std < 3.25 && entropy < 1.25 && duplicates > 59 && outliers <= 2) {
                    if (++buffer > 3)
                        fail("std=" + std + " ent=" + entropy + " sms=" + duplicates + " out=" + outliers);
                } else if (buffer > 0) buffer -= 0.1;


                debug("std=" + std + " ent=" + entropy);


            }

            if (delays.size() >= 65)
                delays.clear();

            if (data.getActionProcessor().getDigTicks() > 10 && updates <= 3)
                delays.add(updates);

            updates = 0;

        } else if (PacketUtil.isFlying(event.getPacketType())) {
            updates++;
        }
    }
}
