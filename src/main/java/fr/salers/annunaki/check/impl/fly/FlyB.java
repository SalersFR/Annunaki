package fr.salers.annunaki.check.impl.fly;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.util.PacketUtil;

@CheckInfo(
        type = "B",
        name = "Fly",
        description = "Checks if a player is ignoring gravity.",
        experimental = true,
        maxVl = 50,
        punish = true
)

public class FlyB extends Check {

    @Override
    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isPosition(event.getPacketType())) {

        }
    }
}
