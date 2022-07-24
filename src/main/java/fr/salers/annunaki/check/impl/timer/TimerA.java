package fr.salers.annunaki.check.impl.timer;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.data.PlayerData;
import fr.salers.annunaki.util.MathUtil;
import fr.salers.annunaki.util.PacketUtil;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Salers
 * made on fr.salers.annunaki.check.impl.timer
 */

@CheckInfo(
        type = "Timer",
        name = "A",
        description = "Checks is sending packets too slow or too fast.",
        experimental = false,
        maxVl = 50,
        punish = true
)
public class TimerA extends Check {

    private int packets;
    private List<Integer> delays = new ArrayList<>();

    public TimerA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if(PacketUtil.isFlying(event.getPacketType())) {
            packets++;

        } else if(event.getPacketType() == PacketType.Play.Client.WINDOW_CONFIRMATION) {
            delays.add(data.getTeleportProcessor().getTeleportTicks() <= 5 ? 1 : packets);

            if(delays.size() == 60) {
                final long count = delays.stream().filter(d -> d == 0 || d >= 2).count();
                if(Math.abs(count - 30) > 2) {
                    if(++buffer > 5)
                        fail("count=" + count);
                } else if(buffer > 0) buffer -= 0.125;
                delays.clear();
            }


            packets = 0;
        }
    }
}
