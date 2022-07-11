package fr.salers.annunaki.check.impl.autoclicker;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.data.PlayerData;
import fr.salers.annunaki.util.ClickingStats;
import fr.salers.annunaki.util.PacketUtil;
import org.bukkit.Bukkit;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * @author Salers
 * made on dev.annunaki.anticheat.check.impl.autoclicker
 */

@CheckInfo(
        type = "Autoclicker",
        name = "E",
        description = "Checks for common clicker flaws.",
        experimental = false,
        maxVl = -1,
        punish = false
)
public class AutoclickerE extends Check {

    private int lastOutliers;

    private List<Integer> delays = new LinkedList<>();
    private int updates;

    public AutoclickerE(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.ANIMATION) {

            if (delays.size() >= 60) {
                final ClickingStats stats = new ClickingStats(delays);

                final double std = stats.getStd();
                final double entropy = stats.getEntropy();

                final double skewness = stats.getSkewness();
                final double kurtosis = stats.getKurtosis();

                final int outliers = stats.getOutliers();

                if (entropy < 3.5 && skewness < 0 && std <= 3.95 && kurtosis <= 11.5 && ((Math.abs(outliers - lastOutliers)
                        <= 1 && outliers < 13) || outliers <= 1)) {
                    if (++buffer > 4)
                        fail("ent=" + entropy + " skew=" + skewness + " std=" + std + " kurt=" + kurtosis + " outs=" + outliers);
                } else if (buffer > 0) buffer -= 0.025D;

                if (data.getDebugging().toLowerCase(Locale.ROOT).contains("autoclickere"))
                    Bukkit.broadcastMessage("std=" + std + " ent=" + entropy);

                lastOutliers = outliers;


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
