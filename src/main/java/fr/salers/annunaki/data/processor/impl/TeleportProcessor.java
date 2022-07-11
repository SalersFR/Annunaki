package fr.salers.annunaki.data.processor.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook;
import fr.salers.annunaki.data.PlayerData;
import fr.salers.annunaki.data.processor.Processor;
import fr.salers.annunaki.util.PacketUtil;
import lombok.Getter;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Salers
 * made on fr.salers.annunaki.data.processor.impl
 */

@Getter
public class TeleportProcessor extends Processor {

    private int teleportTicks;
    private final List<Vector> tps = new ArrayList<>();

    public TeleportProcessor(PlayerData data) {
        super(data);
    }

    @Override
    public void handlePre(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.PLAYER_POSITION_AND_LOOK) {
            final WrapperPlayServerPlayerPositionAndLook wrapper = new WrapperPlayServerPlayerPositionAndLook(event);
            data.confirm(() -> tps.add(new Vector(wrapper.getX(), wrapper.getY(), wrapper.getZ())));
        }
    }

    @Override
    public void handlePre(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) {

            teleportTicks++;

            if (tps.size() > 0) {
                for (Vector vectors : tps) {
                    if (vectors.distance(data.getPositionProcessor().getVectorPos()) <= 0.001) {
                        data.confirmPost(() -> {
                            teleportTicks = 0;
                            tps.remove(vectors);
                        });
                    }


                }
            }


        }
    }
}

