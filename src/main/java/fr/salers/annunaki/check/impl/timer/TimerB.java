package fr.salers.annunaki.check.impl.timer;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.util.PacketUtil;

/**
 * @author Salers
 * made on fr.salers.annunaki.check.impl.timer
 */
@CheckInfo(
        type = "B",
        name = "Timer",
        description = "Checks is sending packets too fast.",
        experimental = false,
        maxVl = 50,
        punish = true
)
public class TimerB extends Check {

    private long balance = -150L, last = System.currentTimeMillis();


    @Override
    public void handle(PacketReceiveEvent event) {
        if(PacketUtil.isFlying(event.getPacketType())) {
            balance += 50L - (event.getTimestamp() - last);

            if(data.getTeleportProcessor().getTeleportTicks() <= 3)
                balance -= 50L;

            //balance abuse fix
            if(balance < -500) {
                balance = -500;
                buffer -= 5.0E-6;
            }

            if(balance > 50L) {
                if(++buffer > 5) {
                    fail("balance=" + balance);
                    balance -= 60L;
                }
            } else if(buffer > 0) buffer -= 0.005;

            last = event.getTimestamp();


        } else if(event.getPacketType().equals(PacketType.Play.Server.PLAYER_POSITION_AND_LOOK)) {
            balance = 0;
            last = event.getTimestamp();
        }
    }
}
