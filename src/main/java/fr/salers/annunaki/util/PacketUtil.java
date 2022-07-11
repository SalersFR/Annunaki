package fr.salers.annunaki.util;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerPosition;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerPositionAndRotation;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerRotation;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PacketUtil {

    public boolean isFlying(PacketTypeCommon type) {
        return type == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION
                || type == PacketType.Play.Client.PLAYER_POSITION
                || type == PacketType.Play.Client.PLAYER_ROTATION
                || type == PacketType.Play.Client.PLAYER_FLYING;
    }

    public boolean isRotation(PacketTypeCommon type) {
        return type == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION
                || type == PacketType.Play.Client.PLAYER_ROTATION;
    }

    public boolean isPosition(PacketTypeCommon type) {
        return type == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION
                || type == PacketType.Play.Client.PLAYER_POSITION;
    }

    public boolean isRelativeMove(PacketTypeCommon type) {
        return type == PacketType.Play.Server.ENTITY_RELATIVE_MOVE_AND_ROTATION
                || type == PacketType.Play.Server.ENTITY_RELATIVE_MOVE;
    }

    public WrapperPlayClientPlayerFlying getFlyingPacket(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_FLYING)
            return new WrapperPlayClientPlayerFlying(event);

        if (event.getPacketType() == PacketType.Play.Client.PLAYER_ROTATION)
            return new WrapperPlayClientPlayerRotation(event);

        if (event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION)
            return new WrapperPlayClientPlayerPosition(event);

        if (event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION)
            return new WrapperPlayClientPlayerPositionAndRotation(event);

        return null;
    }
}
