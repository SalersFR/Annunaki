package fr.salers.annunaki.util.world;


import fr.salers.annunaki.Annunaki;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class SimpleCollisionBox {

    private double minX, minY, minZ;
    private double maxX, maxY, maxZ;

    public SimpleCollisionBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public SimpleCollisionBox(final Vector location) {
        this.minX = location.getX() - 0.3D;
        this.minY = location.getY();
        this.minZ = location.getZ() - 0.3D;
        this.maxX = location.getX() + 0.3D;
        this.maxY = location.getY() + 1.8D;
        this.maxZ = location.getZ() + 0.3D;
    }

    public SimpleCollisionBox expand(double x, double y, double z) {
        this.minX -= x;
        this.maxX += x;
        this.minY -= y;
        this.maxY += y;
        this.minZ -= z;
        this.maxZ += z;
        return this;
    }

    public SimpleCollisionBox expand(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX -= minX;
        this.minY -= minY;
        this.minZ -= minZ;
        this.maxX += maxX;
        this.maxY += maxY;
        this.maxZ += maxZ;
        return this;
    }

    public double getX() {
        return (minX + maxX) / 2.0;
    }

    public double getY() {
        return minY;
    }

    public double getZ() {
        return (minZ + maxZ) / 2.0;
    }

    public List<WrappedBlock> getCollidingBlocks(final World world) {
        final List<WrappedBlock> blockList = new ArrayList<>();

        Bukkit.getScheduler().runTask(Annunaki.getInstance(), () -> {
            if (minY == maxY) {
                for (double x = minX; x <= maxX; x += (maxX - minX)) {
                    for (double z = minZ; z <= maxZ; z += (maxZ - minZ)) {
                        Location location = new Location(world, x, minY, z);

                        blockList.add(new WrappedBlock(location.getBlock()));
                    }
                }
            } else {
                for (double x = minX; x <= maxX; x += (maxX - minX)) {
                    for (double y = minY; y <= maxY; y += (maxY - minY)) {
                        for (double z = minZ; z <= maxZ; z += (maxZ - minZ)) {
                            Location location = new Location(world, x, y, z);

                            blockList.add(new WrappedBlock(location.getBlock()));
                        }
                    }
                }
            }

            blockList.removeIf(block -> !block.isLoaded());
        });

        return blockList;
    }


    public boolean isCollidedWith(SimpleCollisionBox other) {
        return other.getMaxX() >= minX && other.getMinX() <= maxX
                && other.getMaxY() >= minY && other.getMinY() <= maxY
                && other.getMaxZ() >= minZ && other.getMinZ() <= maxZ;
    }

    public boolean isIntersectedWith(SimpleCollisionBox other) {
        return other.getMaxX() > minX && other.getMinX() < maxX
                && other.getMaxY() > minY && other.getMinY() < maxY
                && other.getMaxZ() > minZ && other.getMinZ() < maxZ;


    }


}
