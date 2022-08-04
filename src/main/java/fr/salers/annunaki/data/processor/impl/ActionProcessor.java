package fr.salers.annunaki.data.processor.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import fr.salers.annunaki.data.PlayerData;
import fr.salers.annunaki.data.processor.Processor;
import fr.salers.annunaki.util.PacketUtil;
import lombok.Getter;
import org.bukkit.entity.Entity;

/**
 * @author Salers
 * made on dev.notonweed.annunaki.data.processor.impl
 */

@Getter
public class ActionProcessor extends Processor {

    private boolean sprinting, sneaking, sentSprint, sentSneak;
    private int placeTicks, digTicks;
    private Vector3i lastBlockPlace;
    private Entity lastTarget;

    private WrapperPlayClientInteractEntity.InteractAction action;

    private int lastBlockPlaceId;
    private int attackTicks;

    public ActionProcessor(PlayerData data) {
        super(data);
    }

    @Override
    public void handlePre(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
            final WrapperPlayClientEntityAction wrapper = new WrapperPlayClientEntityAction(event);
            switch (wrapper.getAction()) {
                case START_SPRINTING:
                    sprinting = true;
                    sentSprint = true;
                    break;
                case STOP_SPRINTING:
                    sprinting = false;
                    sentSprint = true;
                    break;
                case START_SNEAKING:
                    sneaking = true;
                    sentSneak = true;
                    break;
                case STOP_SNEAKING:
                    sneaking = false;
                    sentSneak = true;
                    break;
            }
        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            final WrapperPlayClientPlayerBlockPlacement wrapper = new WrapperPlayClientPlayerBlockPlacement(event);

            if (wrapper.getItemStack().isPresent()) {
                placeTicks = 0;
                //FIXME Replace 1.8 by actual version
                lastBlockPlaceId = wrapper.getItemStack().get().getType().getId(ClientVersion.V_1_8);
            }

            lastBlockPlace = wrapper.getBlockPosition();

        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            digTicks = 0;
        } else if (PacketUtil.isFlying(event.getPacketType())) {
            placeTicks++;
            digTicks++;
            attackTicks++;
            sentSneak = sentSprint = false;
        } else if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            final WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);

            action = wrapper.getAction();

            if (wrapper.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                lastTarget = data.getPlayer().getWorld().getEntities().stream().filter(entity -> entity.getEntityId()
                        == wrapper.getEntityId()).findAny().orElse(null);
                attackTicks = 0;
            }
        }
    }
}
