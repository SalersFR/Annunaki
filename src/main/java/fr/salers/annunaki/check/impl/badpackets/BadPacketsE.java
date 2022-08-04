package fr.salers.annunaki.check.impl.badpackets;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientKeepAlive;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerKeepAlive;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;

import java.util.ArrayDeque;
import java.util.Deque;

@CheckInfo(name = "BadPackets", type = "E", description = "Checks for wrong keep alive order", experimental = false, maxVl = 5, punish = true, enabled = true)
public class BadPacketsE extends Check {

    Deque<Long> queue = new ArrayDeque<Long>();

    @Override
    public void handle(PacketReceiveEvent event) {
        if(event.getPacketType().equals(PacketType.Play.Client.KEEP_ALIVE)) {
            WrapperPlayClientKeepAlive packet = new WrapperPlayClientKeepAlive(event.clone());
            long id = packet.getId();
            if(!queue.contains(id)) {
                fail("Wrong keep alive order");
            } else {
                queue.remove(id);
            }
        }
    }

    @Override
    public void handle(PacketSendEvent event) {
        if(event.getPacketType().equals(PacketType.Play.Server.KEEP_ALIVE)) {
            WrapperPlayServerKeepAlive packet = new WrapperPlayServerKeepAlive(event.clone());
            queue.add(packet.getId());
        }
    }
}
