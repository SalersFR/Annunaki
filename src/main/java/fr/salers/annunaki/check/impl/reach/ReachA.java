package fr.salers.annunaki.check.impl.reach;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.util.PacketUtil;
import fr.salers.annunaki.util.Pair;
import fr.salers.annunaki.util.mc.AxisAlignedBB;
import org.bukkit.GameMode;
import org.bukkit.util.Vector;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CheckInfo(
        type = "A",
        name = "Reach",
        description = "Checks for attacking out of range",
        experimental = true,
        maxVl = 15,
        punish = true
)
public class ReachA extends Check {

    @Override
    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) {
            if (data.getActionProcessor().getAttackTicks() < 2
                    || data.getPlayer().getGameMode() == GameMode.CREATIVE
                    || data.getTargetLocs().isEmpty()
            ) return;


            final List<Pair<AxisAlignedBB, Long>> locs = getRightLocations(PacketEvents.getAPI().getPlayerManager().getPing(data.getPlayer()), 150);

            double distance = locs.stream().mapToDouble(pair -> {

                AxisAlignedBB aabb = pair.getX();

                aabb.expand(0.1, 0.1, 0.1);

                double reach = 10;

                Vector[] vecs = {
                        new Vector(aabb.minX, 0, aabb.minZ),
                        new Vector(aabb.minX, 0, aabb.maxZ),
                        new Vector(aabb.maxX, 0, aabb.minZ),
                        new Vector(aabb.maxX, 0, aabb.maxZ)
                };

                for (Vector vec : vecs) {
                    if (data.getPositionProcessor().getVectorPos().clone().setY(0).distance(vec) < reach)
                        reach = data.getPositionProcessor().getVectorPos().clone().setY(0).distance(vec) - 0.125;

                }

                return reach;
            }).min().orElse(10);

            if (distance > 3.05 && distance < 6.0) {
                if (++buffer > 5)
                    fail("reach=" + (float) distance);
            } else if (buffer > 0) buffer -= 0.02;


        }
    }

    //TY FUNKEEEEEEEEEE
    public List<Pair<AxisAlignedBB, Long>> getRightLocations(int time, int delta) {
        return data.getTargetLocs().stream()
                .sorted(Comparator.comparingLong(pair -> Math.abs(pair.getY() - (System.currentTimeMillis() - time))))
                .filter(pair -> Math.abs(pair.getY() - (System.currentTimeMillis() - time)) < delta)
                .collect(Collectors.toList());
    }

}
