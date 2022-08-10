package fr.salers.annunaki.data.processor.impl.entity;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import fr.salers.annunaki.data.PlayerData;
import fr.salers.annunaki.data.processor.Processor;
import fr.salers.annunaki.util.PacketUtil;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.concurrent.ConcurrentHashMap;

@Getter
public class EntityProcessor extends Processor {

    private final ConcurrentHashMap<Integer, TrackedEntityContainer> trackedEntities = new ConcurrentHashMap<>();

    public EntityProcessor(PlayerData data) {
        super(data);
    }

    @Override
    public void handlePre(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.SPAWN_PLAYER) {
            WrapperPlayServerSpawnPlayer spawnPlayer = new WrapperPlayServerSpawnPlayer(event.clone());

            Vector3d position = spawnPlayer.getPosition();

            TrackedEntity trackedEntity = new TrackedEntity(position.x, position.y, position.z);

            TrackedEntityContainer container = new TrackedEntityContainer(trackedEntity);


            data.confirm(()
                    -> trackedEntities.put(spawnPlayer.getEntityId(), container));

            data.confirm(container::onPostTransaction);


        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_TELEPORT) {
            WrapperPlayServerEntityTeleport entityTeleport = new WrapperPlayServerEntityTeleport(event.clone());

            Vector3d position = entityTeleport.getPosition();

            TrackedEntityContainer container = trackedEntities.get(entityTeleport.getEntityId());

            if (container == null) {
                return;
            }


            data.confirm(()
                    -> container.handleMovement(new EntityMovement(EntityMovementType.ABSOLUTE,
                    position.x, position.y, position.z)));

            data.confirm(container::onPostTransaction);


        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE) {
            WrapperPlayServerEntityRelativeMove relMove = new WrapperPlayServerEntityRelativeMove(event.clone());

            TrackedEntityContainer container = trackedEntities.get(relMove.getEntityId());

            if (container == null) {
                return;
            }

            data.confirm(()
                    -> container.handleMovement(new EntityMovement(EntityMovementType.RELATIVE,
                    relMove.getDeltaX(), relMove.getDeltaY(), relMove.getDeltaZ())));

            data.confirm(container::onPostTransaction);


        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE_AND_ROTATION) {
            WrapperPlayServerEntityRelativeMoveAndRotation relMove = new WrapperPlayServerEntityRelativeMoveAndRotation(event.clone());

            TrackedEntityContainer container = trackedEntities.get(relMove.getEntityId());

            if (container == null) {
                return;
            }

            data.confirm(()
                    -> container.handleMovement(new EntityMovement(EntityMovementType.RELATIVE,
                    relMove.getDeltaX(), relMove.getDeltaY(), relMove.getDeltaZ())));

            data.confirm(container::onPostTransaction);
        } else if (event.getPacketType() == PacketType.Play.Server.DESTROY_ENTITIES) {
            WrapperPlayServerDestroyEntities destroyEntities = new WrapperPlayServerDestroyEntities(event);

            data.confirm(() -> {
                for (int id : destroyEntities.getEntityIds()) {
                    trackedEntities.remove(id);
                }
            });
        }
        event.cleanUp();
    }

    @Override
    public void handlePre(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) {
            trackedEntities.values().forEach(TrackedEntityContainer::onPreTick);
        }
    }

    @Override
    public void handlePost(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) {
            trackedEntities.values().forEach(TrackedEntityContainer::onPostTick);
        }
    }
}
