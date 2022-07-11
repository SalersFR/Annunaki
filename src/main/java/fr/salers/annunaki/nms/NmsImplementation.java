package fr.salers.annunaki.nms;

import fr.salers.annunaki.manager.TaskManager;
import fr.salers.annunaki.util.mc.AxisAlignedBB;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public interface NmsImplementation {

    void insertPostTask(TaskManager taskManager);

    float getFriction(final Vector pos);

    AxisAlignedBB getAABB(final Entity player);
}
