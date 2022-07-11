package fr.salers.annunaki.data.processor.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import fr.salers.annunaki.data.PlayerData;
import fr.salers.annunaki.data.processor.Processor;
import fr.salers.annunaki.util.PacketUtil;
import lombok.Getter;
import org.bukkit.util.Vector;


@Getter
public class PositionProcessor extends Processor {

    private double x, y, z, deltaX, deltaY, deltaZ, deltaXZ,
            lastX, lastY, lastZ, lastDeltaX, lastDeltaY, lastDeltaZ, lastDeltaXZ;

    private boolean sentPosition, lastSentPosition;

    public PositionProcessor(PlayerData data) {
        super(data);
    }

    @Override
    public void handlePre(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying flying = PacketUtil.getFlyingPacket(event);

            lastX = x;
            lastY = y;
            lastZ = z;
            lastDeltaX = deltaX;
            lastDeltaY = deltaY;
            lastDeltaZ = deltaZ;
            lastDeltaXZ = deltaXZ;

            lastSentPosition = sentPosition;

            if (PacketUtil.isPosition(event.getPacketType())) {
                Location location = flying.getLocation();

                x = location.getX();
                y = location.getY();
                z = location.getZ();

                deltaX = x - lastX;
                deltaY = y - lastY;
                deltaZ = z - lastZ;

                deltaXZ = Math.hypot(deltaX, deltaZ);

                sentPosition = true;
            } else {
                deltaX = 0;
                deltaY = 0;
                deltaZ = 0;
                deltaXZ = 0;

                sentPosition = false;
            }

            data.setTick(data.getTick() + 1);
        }
    }

    public Vector getVectorPos() {
        return new Vector(x, y, z);
    }

    public Vector getLastVectorPos() {
        return new Vector(lastX, lastY, lastZ);
    }
}
