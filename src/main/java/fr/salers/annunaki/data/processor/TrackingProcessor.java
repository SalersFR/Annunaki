package fr.salers.annunaki.data.processor;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import fr.salers.annunaki.data.PlayerData;
import fr.salers.annunaki.util.PacketUtil;
import fr.salers.annunaki.util.mc.AxisAlignedBB;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Salers
 * made on fr.salers.annunaki.data.processor
 */

@Getter
public class TrackingProcessor extends Processor {

    private final Map<Integer, TrackedEntity> trackedEntityMap = new HashMap<>();

    public TrackingProcessor(PlayerData data) {
        super(data);
    }

    @Override
    public void handlePost(PacketReceiveEvent event) {
        if(PacketUtil.isFlying(event.getPacketType())) {
            trackedEntityMap.values().forEach(TrackedEntity::onLivingUpdate);
        }


    }

    @Override
    public void handlePre(PacketSendEvent event) {
        if(event.getPacketType() == PacketType.Play.Server.SPAWN_PLAYER) {
            final WrapperPlayServerSpawnPlayer wrapper = new WrapperPlayServerSpawnPlayer(event);
            trackedEntityMap.put(wrapper.getEntityId(), new TrackedEntity(wrapper.getPosition().multiply(32.0D)));
        } else if(event.getPacketType() == PacketType.Play.Server.SPAWN_LIVING_ENTITY) {
            final WrapperPlayServerSpawnLivingEntity wrapper = new WrapperPlayServerSpawnLivingEntity(event);
            trackedEntityMap.put(wrapper.getEntityId(), new TrackedEntity(wrapper.getPosition().multiply(32.0D)));
        } else if(event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE) {

            final WrapperPlayServerEntityRelativeMove wrapper = new WrapperPlayServerEntityRelativeMove(event);
            final TrackedEntity entity = trackedEntityMap.get(wrapper.getEntityId());

            data.getTransactionProcessor().confirmPost(() -> {
                entity.serverPosX += wrapper.getDeltaX() * 32.0D;
                entity.serverPosY += wrapper.getDeltaY() * 32.0D;
                entity.serverPosZ += wrapper.getDeltaZ() * 32.0D;

                double d0 = (double) entity.serverPosX / 32.0D;
                double d1 = (double) entity.serverPosY / 32.0D;
                double d2 = (double) entity.serverPosZ / 32.0D;

                entity.setPositionAndRotation2(d0, d1, d2);

            });
        } else if(event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE_AND_ROTATION) {

            final WrapperPlayServerEntityRelativeMoveAndRotation wrapper = new WrapperPlayServerEntityRelativeMoveAndRotation(event);
            final TrackedEntity entity = trackedEntityMap.get(wrapper.getEntityId());

            data.getTransactionProcessor().confirmPost(() -> {
                entity.serverPosX += wrapper.getDeltaX() * 32.0D;
                entity.serverPosY += wrapper.getDeltaY() * 32.0D;
                entity.serverPosZ += wrapper.getDeltaZ() * 32.0D;

                double d0 = (double) entity.serverPosX / 32.0D;
                double d1 = (double) entity.serverPosY / 32.0D;
                double d2 = (double) entity.serverPosZ / 32.0D;

                entity.setPositionAndRotation2(d0, d1, d2);

            });
        } else if(event.getPacketType() == PacketType.Play.Server.ENTITY_TELEPORT) {
            final WrapperPlayServerEntityTeleport packetIn = new WrapperPlayServerEntityTeleport(event);
            final TrackedEntity entity = trackedEntityMap.get(packetIn.getEntityId());

            data.getTransactionProcessor().confirmPost(() -> {
                entity.serverPosX = (int) (packetIn.getPosition().getX() * 32.0D);
                entity.serverPosY =(int) (packetIn.getPosition().getY() * 32.0D);
                entity.serverPosZ = (int) (packetIn.getPosition().getZ() * 32.0D);
                double d0 = (double) entity.serverPosX / 32.0D;
                double d1 = (double) entity.serverPosY / 32.0D;
                double d2 = (double) entity.serverPosZ / 32.0D;

                if (Math.abs(entity.posX - d0) < 0.03125D && Math.abs(entity.posY - d1) < 0.015625D && Math.abs(entity.posZ - d2) < 0.03125D) {
                    entity.setPositionAndRotation2(entity.posX, entity.posY, entity.posZ);
                } else {
                    entity.setPositionAndRotation2(d0, d1, d2);
                }

            });

        }
    }

    @Getter
    @Setter
    public class TrackedEntity {
        /**
         * Entity position X
         */
        public double posX;

        /**
         * Entity position Y
         */
        public double posY;

        /**
         * Entity position Z
         */
        public double posZ;

        public int serverPosX;
        public int serverPosY;
        public int serverPosZ;

        private int otherPlayerMPPosRotationIncrements;
        private double otherPlayerMPX;
        private double otherPlayerMPY;

        public int entityId;
        private double otherPlayerMPZ;

        public AxisAlignedBB box;

        private final float width;
        public final float height;

        public TrackedEntity( int serverPosX, int serverPosY, int serverPosZ) {

            this.serverPosX = serverPosX;
            this.serverPosY = serverPosY;
            this.serverPosZ = serverPosZ;

            this.posX = serverPosX / 32.0D;
            this.posY = serverPosY / 32.0D;
            this.posZ = serverPosZ / 32.0D;

            this.width = 0.6F;
            this.height = 1.8F;

            float expandX = this.width / 2.0F;
            this.box = new AxisAlignedBB(
                    posX - expandX,
                    posY,
                    posZ - expandX,
                    posX + expandX,
                    posY + this.height,
                    posZ + expandX
            );


        }

        public TrackedEntity(Vector3d vector3d) {
            this((int) vector3d.x, (int) vector3d.y, (int) vector3d.z);
        }

        public void setPositionAndRotation2(double x, double y, double z) {
            this.otherPlayerMPX = x;
            this.otherPlayerMPY = y;
            this.otherPlayerMPZ = z;
            this.otherPlayerMPPosRotationIncrements = 3;
        }

        public void onLivingUpdate() {
            if (this.otherPlayerMPPosRotationIncrements > 0) {
                double d0 = this.posX + (this.otherPlayerMPX - this.posX) / (double) this.otherPlayerMPPosRotationIncrements;
                double d1 = this.posY + (this.otherPlayerMPY - this.posY) / (double) this.otherPlayerMPPosRotationIncrements;
                double d2 = this.posZ + (this.otherPlayerMPZ - this.posZ) / (double) this.otherPlayerMPPosRotationIncrements;
                --this.otherPlayerMPPosRotationIncrements;
                this.setPosition(d0, d1, d2);

            }


        }

        public void setPosition(double x, double y, double z) {
            this.posX = x;
            this.posY = y;
            this.posZ = z;

            float f = this.width / 2.0F;
            this.box = new AxisAlignedBB(x - f, y, z - f, x + f, y + this.height, z + f);

        }
    }
}
