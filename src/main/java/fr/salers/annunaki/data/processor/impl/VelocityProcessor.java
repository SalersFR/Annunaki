package fr.salers.annunaki.data.processor.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityVelocity;
import fr.salers.annunaki.data.PlayerData;
import fr.salers.annunaki.data.processor.Processor;
import fr.salers.annunaki.util.PacketUtil;
import fr.salers.annunaki.util.lag.ConfirmedVelocity;
import lombok.Getter;

import java.util.Deque;
import java.util.LinkedList;

/**
 * @author Salers
 * made on dev.notonweed.annunaki.data.processor.impl
 */

@Getter
public class VelocityProcessor extends Processor {

    private final Deque<ConfirmedVelocity> velocities = new LinkedList<>();
    private int velTicks;

    public VelocityProcessor(PlayerData data) {
        super(data);
    }

    @Override
    public void handlePre(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) {

            if (velTicks > 0) {
                velocities.forEach(confirmedVelocity -> confirmedVelocity.handleTick(data, velTicks));
            }

            velTicks++;
        }

    }

    @Override
    public void handlePre(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_VELOCITY) {
            final WrapperPlayServerEntityVelocity wrapper = new WrapperPlayServerEntityVelocity(event);

            if (wrapper.getEntityId() != data.getPlayer().getEntityId()) return;

            data.getTransactionProcessor().confirm(() -> {
                        velocities.add(new ConfirmedVelocity(wrapper.getVelocity()));
                        velTicks = 0;
                    }
            );

            if (velocities.size() > 1)
                data.getTransactionProcessor().confirmPost(velocities::removeFirst);

        }
    }

    public ConfirmedVelocity getLastVelocity() {
        return velocities.peekLast();
    }
}
