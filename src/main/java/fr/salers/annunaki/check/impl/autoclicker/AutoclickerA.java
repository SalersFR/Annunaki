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
 * made on dev.notonweed.annunaki.check.impl.autoclicker
 */

@CheckInfo(
        type = "A",
        name = "Autoclicker",
        description = "Checks for an impossible consistency.",
        experimental = false,
        maxVl = -1,
        punish = false
)
public class AutoclickerA extends Check {

    private final List<Integer> delays = new LinkedList<>();
    private int updates;


    @Override
    public void handle(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.ANIMATION) {

            if (delays.size() >= 60) {
                final ClickingStats stats = new ClickingStats(delays);

                final double entropy = stats.getEntropy();
                final double std = stats.getStd();

                if (entropy < 0.975 && std < 2.75 && stats.getCps() > 9) {
                    if (++buffer > 3)
                        fail(" std=" + std + " ent=" + entropy);
                } else if (buffer > 0) buffer -= 0.06;

                debug("std=" + std + " ent=" + entropy + " cps=" + stats.getCps());


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
