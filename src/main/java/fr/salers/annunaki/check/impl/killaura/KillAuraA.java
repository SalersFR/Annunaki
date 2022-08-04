package fr.salers.annunaki.check.impl.killaura;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.util.MathUtil;
import fr.salers.annunaki.util.PacketUtil;

import java.util.Deque;
import java.util.LinkedList;

/**
 * @author Salers
 * made on dev.notonweed.annunaki.check.impl.killaura
 */
@CheckInfo(
        type = "A",
        name = "KillAura",
        description = "Checks for a post interact packet.",
        experimental = false,
        maxVl = 50,
        punish = true
)

public class KillAuraA extends Check {

    private long lastFlying;
    private final Deque<Long> delays = new LinkedList<>();

    @Override
    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) {
            lastFlying = event.getTimestamp();
        } else if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            final long diff = Math.abs(event.getTimestamp() - lastFlying);
            delays.add(diff);

            if (delays.size() >= 10) {
                final int count = (int) delays.stream().filter(delay -> delay < 15L).count();
                final double std = MathUtil.getStandardDeviation(delays);

                if (std < 6.5 && count > 8)
                    fail("count=" + count + " std=" + std);


                delays.removeFirst();
            }


        }
    }
}
