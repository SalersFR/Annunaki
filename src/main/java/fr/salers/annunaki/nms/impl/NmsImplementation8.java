package fr.salers.annunaki.nms.impl;

import fr.salers.annunaki.manager.TaskManager;
import fr.salers.annunaki.nms.NmsImplementation;
import fr.salers.annunaki.util.mc.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

public class NmsImplementation8 implements NmsImplementation {

    // Stolen from Ares
    @Override
    public void insertPostTask(TaskManager taskManager) {
        MinecraftServer.getServer().a(taskManager::handlePostTick);
    }

    @Override
    public float getFriction(Vector pos) {
        return MinecraftServer.getServer()
                .getWorld()
                .getType(new BlockPosition(pos.getX(), pos.getY() - 1.0, pos.getZ()))
                .getBlock().frictionFactor * 0.91F;
    }

    @Override
    public AxisAlignedBB getAABB(final Entity player) {
        if (player.getType() != EntityType.PLAYER) return new AxisAlignedBB(player.getLocation());
        final EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        final net.minecraft.server.v1_8_R3.AxisAlignedBB bb = entityPlayer.getBoundingBox();
        return new AxisAlignedBB(bb.a, bb.b, bb.c, bb.d, bb.e, bb.f);
    }
}
