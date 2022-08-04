package fr.salers.annunaki.data.processor.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerMultiBlockChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook;
import fr.salers.annunaki.data.PlayerData;
import fr.salers.annunaki.data.processor.Processor;
import fr.salers.annunaki.util.PacketUtil;
import fr.salers.annunaki.util.mc.AxisAlignedBB;
import fr.salers.annunaki.util.mc.MathHelper;
import fr.salers.annunaki.util.world.SimpleCollisionBox;
import fr.salers.annunaki.util.world.WrappedBlock;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Salers
 * made on dev.notonweed.annunaki.data.processor.impl
 */

@Getter
public class CollisionProcessor extends Processor {

    private boolean clientOnGround, mathOnGround, collisionOnGround,
            onIce, onSlime, onSoulSand, onClimbable,
            inWeb, inWater, inLava,
            nearVehicle, nearBoat, nearPiston, nearCarpet, bonkingHead, teleporting, placing, nearAnvil;

    private boolean lastClientOnGround, lastMathOnGround, lastCollisionOnGround,
            lastOnIce, lastOnSlime, lastOnSoulSand, lastOnClimbable, lastNearPiston,
            lastNearCarpet, lastInWeb, lastInWater, lastInLava, lastInVehicle, nearSlab, nearStairs,
            lastNearVehicle, lastNearBoat, lastBonkingHead, lastTeleporting,
            lastOnGroundSlime, lastOnGroundIce, lastPlacing, lastNearSlab, lastNearStairs, lastLastClientOnGround,
            lastNearAnvil, buggedInBlocks;

    private final Deque<Vector> teleportVecs = new LinkedList<>();

    private int collisionAirTicks, clientAirTicks, mathAirTicks, collisionGroundTicks, clientGroundTicks, mathGroundTicks,
            waterTicks, ticksAlive, iceTicks;

    private List<Entity> entityCollisions;

    private List<WrappedBlock> collidingBlocks, bonkingCollisions, fenceCollisions = new ArrayList<>();


    private SimpleCollisionBox collisionBox, bonkingBoundingBox, fenceBoundingBox, horizontalCollisionBox;

    private AxisAlignedBB boundingBox;

    public CollisionProcessor(PlayerData data) {
        super(data);
    }

    @Override
    public void handlePre(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType()) && data.getTick() > 1) {
            final Vector loc = data.getPositionProcessor().getVectorPos();
            final WrapperPlayClientPlayerFlying wrapper = new WrapperPlayClientPlayerFlying(event);

            collisionBox = new SimpleCollisionBox(loc);

            // expand: positive to expand, negative to shrink
            collisionBox.expand(0, 0.1, 0);

            boundingBox = new AxisAlignedBB(loc.getX(), loc.getY(), loc.getZ());

            horizontalCollisionBox = collisionBox.expand(0.1, 0.01, 0.01);

            bonkingBoundingBox = new SimpleCollisionBox(loc).expand(0, 0, -1.81, 0.01, 0, 0);

            bonkingCollisions = bonkingBoundingBox.getCollidingBlocks(data.getPlayer().getWorld());

            fenceBoundingBox = new SimpleCollisionBox(loc).expand(0, 0, 0.61, -2.41, 0, 0);

            fenceCollisions = fenceBoundingBox.getCollidingBlocks(data.getPlayer().getWorld());

            collisionBox.getCollidingBlocks(data.getPlayer().getWorld()).
                    addAll(horizontalCollisionBox.getCollidingBlocks(data.getPlayer().getWorld()));

            collidingBlocks = collisionBox.getCollidingBlocks(data.getPlayer().getWorld());

            final int flooredX = NumberConversions.floor(loc.getX());
            final int flooredY = NumberConversions.floor(loc.getY());
            final int flooredZ = NumberConversions.floor(loc.getZ());


            Block climbableBlock = getBlock(new Location(data.getPlayer().getWorld(), flooredX, flooredY, flooredZ));
            entityCollisions = getEntitiesWithinRadius(
                    data.getPlayer(), loc.toLocation(data.getPlayer().getWorld()),
                    0.5);


            if (collidingBlocks == null) return;

            this.clientOnGround = wrapper.isOnGround();
            this.mathOnGround = wrapper.getLocation().getY() % 0.015625 <= 0.0001;
            this.collisionOnGround = collidingBlocks.stream().anyMatch(block -> block.isSolid() && block.getY() <= loc.getY())
                    || fenceCollisions.stream().anyMatch(block -> block.isFence() || block.isFenceGate() || block.isWall());


            onIce = collidingBlocks.stream().anyMatch(WrappedBlock::isIce);
            onSlime = collidingBlocks.stream().anyMatch(WrappedBlock::isSlime);

            onSoulSand = collidingBlocks.stream().anyMatch(WrappedBlock::isSoulSand) || collidingBlocks.stream().
                    anyMatch(block -> block.isSoulSand() && block.getY() <= loc.getY());

            inWeb = collidingBlocks.stream().anyMatch(WrappedBlock::isWeb);
            inWater = collidingBlocks.stream().anyMatch(WrappedBlock::isWater) || isInLiquid(data.getPlayer());
            inLava = collidingBlocks.stream().anyMatch(WrappedBlock::isLava) || isInLiquid(data.getPlayer());
            nearPiston = collidingBlocks.stream().anyMatch(WrappedBlock::isPiston);
            nearCarpet = collidingBlocks.stream().anyMatch(WrappedBlock::isCarpet);
            nearSlab = collidingBlocks.stream().anyMatch(WrappedBlock::isSlab);
            nearStairs = collidingBlocks.stream().anyMatch(WrappedBlock::isStairs);
            nearAnvil = collidingBlocks.stream().anyMatch(WrappedBlock::isAnvil);

            onClimbable = climbableBlock != null && (climbableBlock.getType() == Material.LADDER || climbableBlock.getType() == Material.VINE);

            nearVehicle = entityCollisions.stream().anyMatch(entity -> entity instanceof Vehicle);
            nearBoat = entityCollisions.stream().anyMatch(entity -> entity instanceof Boat);

            bonkingHead = bonkingCollisions.stream().anyMatch(block -> block.isSolid()
                    && block.getY() - data.getPositionProcessor().getY() >= 1.8) || haveABlockNearHead(data.getPlayer())
                    || haveABlockNearHead(data.getPositionProcessor().getVectorPos().toLocation(data.getPlayer().getWorld()))
                    || blockNearHead(data.getPositionProcessor().getVectorPos().toLocation(data.getPlayer().getWorld()))
                    || blockNearHead(data.getPlayer());

            teleporting = placing = false;

            buggedInBlocks = collidingBlocks.stream().anyMatch(block -> new SimpleCollisionBox(block.getLocation())
                    .isCollidedWith(collisionBox));


            if (collisionOnGround) {
                collisionAirTicks = 0;
                collisionGroundTicks++;
            } else {
                collisionAirTicks++;
                collisionGroundTicks = 0;
            }

            if (clientOnGround) {
                clientAirTicks = 0;
                clientGroundTicks++;
            } else {
                clientAirTicks++;
                clientGroundTicks = 0;
            }

            if (mathOnGround) {
                mathAirTicks = 0;
                mathGroundTicks++;
            } else {
                mathAirTicks++;
                mathGroundTicks = 0;
            }

            if (inWater) {
                waterTicks++;
            } else waterTicks = 0;

            if (data.getPlayer().getHealth() <= 0 || data.getPlayer().isDead())
                ticksAlive = 0;
            this.ticksAlive++;

            if (onIce)
                iceTicks = 0;
            else iceTicks++;


            updateLastVars();


        }
    }

    public boolean isInLiquid(final Player player) {
        final double expand = 0.31;
        final Location location = player.getLocation();
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (location.add(x, -0.5001, z).getBlock().isLiquid()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void handlePre(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.BLOCK_CHANGE) {
            final WrapperPlayServerBlockChange wrapper = new WrapperPlayServerBlockChange(event);

            // SUPPOSED to handle ghost blocks
            for (final WrappedBlock blocks : collidingBlocks) {
                final Vector block = new Vector(blocks.getX(), blocks.getY(), blocks.getZ());
                final Vector update = new Vector(wrapper.getBlockPosition().x, wrapper.getBlockPosition().y, wrapper.getBlockPosition().z);

                //uh, don't ask
                if (block.distance(update) < 0.01)
                    data.getTransactionProcessor().confirmPost(() -> {
                        blocks.setMaterial(Material.getMaterial(wrapper.getBlockId()));
                    });


            }

        }

        if (event.getPacketType() == PacketType.Play.Server.MULTI_BLOCK_CHANGE) {
            final WrapperPlayServerMultiBlockChange wrapper = new WrapperPlayServerMultiBlockChange(event);

            for (WrappedBlock blocks : collidingBlocks) {
                for (WrapperPlayServerMultiBlockChange.EncodedBlock updates : wrapper.getBlocks()) {
                    final Vector block = new Vector(blocks.getX(), blocks.getY(), blocks.getZ());
                    final Vector update = new Vector(updates.getX(), updates.getY(), updates.getZ());

                    //uh, don't ask
                    if (block.distance(update) < 0.01)
                        data.getTransactionProcessor().confirmPost(() -> {
                            blocks.setMaterial(Material.getMaterial(updates.getBlockId()));
                        });


                }
            }
        } else if (event.getPacketType() == PacketType.Play.Server.PLAYER_POSITION_AND_LOOK) {
            final WrapperPlayServerPlayerPositionAndLook wrapper = new WrapperPlayServerPlayerPositionAndLook(event);

            final Vector pos = new Vector(wrapper.getX(), wrapper.getY(), wrapper.getZ());
            final Vector loc = data.getPositionProcessor().getVectorPos();

            if (pos.distance(loc) < 0.001D)
                data.getTransactionProcessor().confirm(() -> teleportVecs.add(pos));

            if (teleportVecs.size() > 1)
                data.getTransactionProcessor().confirmPost(() -> teleportVecs.removeFirst());


        }

    }

    private boolean haveABlockNearHead(final Player player) {

        final Location location = player.getLocation();
        final Block highest = location.getWorld().getHighestBlockAt(location);

        return highest.getType() != Material.AIR;
    }

    public boolean blockNearHead(final Player location) {
        final double expand = 0.31;

        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (location.getLocation().clone().add(x, 2.0, z).getBlock().getType() != Material.AIR)
                    return true;
            }
        }
        return false;
    }

    public boolean nearLiquid(final Player location) {
        final double expand = 0.31;

        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (location.getLocation().clone().add(x, -0.5001, z).getBlock().isLiquid())
                    return true;
            }
        }

        return (location.getLocation().getBlock().isLiquid() || location.getLocation().clone().add(0, 0.01, 0).getBlock().isLiquid());
    }


    private void updateLastVars() {
        lastLastClientOnGround = lastClientOnGround;
        lastClientOnGround = clientOnGround;
        lastMathOnGround = mathOnGround;
        lastCollisionOnGround = collisionOnGround;
        lastOnIce = onIce;
        lastOnSlime = onSlime;
        lastOnSoulSand = onSoulSand;
        lastOnClimbable = onClimbable;
        lastNearPiston = nearPiston;
        lastInWeb = inWeb;
        lastInWater = inWater;
        lastInLava = inLava;
        lastNearVehicle = nearVehicle;
        lastNearBoat = nearBoat;
        lastBonkingHead = bonkingHead;
        lastTeleporting = teleporting;
        lastPlacing = placing;
        lastNearSlab = nearSlab;
        lastNearStairs = nearStairs;
        lastNearCarpet = nearCarpet;
        lastNearAnvil = nearAnvil;
    }

    public Block getBlock(Location location) {
        if (location.getChunk().isLoaded()) {
            return location.getBlock();
        }

        return null;
    }

    public List<Entity> getEntitiesWithinRadius(Player player, Location location, double radius) {

        double expander = 16.0D;

        double x = location.getX();
        double z = location.getZ();

        int minX = (int) Math.floor((x - radius) / expander);
        int maxX = (int) Math.floor((x + radius) / expander);

        int minZ = (int) Math.floor((z - radius) / expander);
        int maxZ = (int) Math.floor((z + radius) / expander);

        World world = location.getWorld();

        List<Entity> entities = new LinkedList<>();

        for (int xVal = minX; xVal <= maxX; xVal++) {

            for (int zVal = minZ; zVal <= maxZ; zVal++) {

                if (!world.isChunkLoaded(xVal, zVal)) continue;

                for (Entity entity : world.getChunkAt(xVal, zVal).getEntities()) {
                    //We have to do this due to stupidness
                    if(entity != null && entity.getEntityId() != player.getEntityId() && entity.getLocation() != null) {
                        if (entity.getLocation().distance(location) <= radius) {
                            entities.add(entity);
                        }
                    }
                }
            }
        }

        return entities;
    }

    public boolean blockNearHead(final Location location) {
        final double expand = 0.31;

        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (location.clone().add(x, 2.0, z).getBlock().getType().isSolid())
                    return true;
            }
        }
        return false;
    }

    public boolean haveABlockNearHead(final Location location) {
        if(location != null) {
            Location head = location.clone().add(0, 1.8, 0);

            for(int x = -1; x <= 1; x++) {
                for(int z = -1; z <= 1; z++) {
                    if(head.clone().add(x, 0, z).getBlock().getType().isSolid()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public static boolean isChunkLoaded(Location loc) {
        int x = MathHelper.floor_double(loc.getX()), z = MathHelper.floor_double(loc.getZ());

        return loc.getWorld().isChunkLoaded(x >> 4, z >> 4);
    }
}
