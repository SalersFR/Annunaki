package fr.salers.annunaki.check.impl.timer;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.util.PacketUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Bitbot25
 * made on fr.salers.annunaki.check.impl.timer
 *
 * October 2021, Orress, Salers and Bitbot decided to make an ac for fun
 * Accidentally, bitbot made an actual decent timer check
 * Since December 2021, either Orress and Bibot vanished
 * Thank you for all guys, you were my only friends in this community <3
 */

@CheckInfo(
        type = "A",
        name = "Timer",
        description = "Checks is sending packets too slow or too fast.",
        experimental = false,
        maxVl = 50,
        punish = true
)

public class TimerA extends Check {

    private long last = System.currentTimeMillis();
    private final List<Long> delays = new ArrayList<>();

    @Override
    public void handle(PacketReceiveEvent event) {
        if(PacketUtil.isFlying(event.getPacketType())) {
            delays.add(data.getTeleportProcessor().getTeleportTicks() <= 3 ? 50L : event.getTimestamp() - last);

            if(delays.size() >= 40) {
                double avg = delays.stream().mapToLong(l -> l).average().orElse(0.0d);
                double dev = dev(delays, avg);

                avg += dev * 0.25d;
                double pct = (50L / avg) * 100.0d;

                if(pct > 100.75 || pct < 93.25) {
                    if(++buffer > 5)
                        fail("percentage=" + (int) pct + "%");
                } else if(buffer > 0) buffer -= 0.25;

                delays.clear();

            }

            last = event.getTimestamp();

        } 
    }

    public double dev(Collection<? extends Number> collection, double avg) {
        double variance = collection.stream().mapToDouble(n -> {
            double v = n.doubleValue() - avg;
            return v * v;
        }).average().orElse(0D);
        return Math.sqrt(variance);
    }
}
