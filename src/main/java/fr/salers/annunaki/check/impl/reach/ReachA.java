package fr.salers.annunaki.check.impl.reach;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.CheckInfo;
import fr.salers.annunaki.data.PlayerData;
import fr.salers.annunaki.util.PacketUtil;
import fr.salers.annunaki.util.Pair;
import fr.salers.annunaki.util.mc.AxisAlignedBB;
import org.bukkit.GameMode;
import org.bukkit.util.Vector;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CheckInfo(
        type = "Reach",
        name = "A",
        description = "Checks for attacking out of range",
        experimental = true,
        maxVl = 15,
        punish = true
)
public class ReachA extends Check {

    private boolean attacked;
    private int id;

    public ReachA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity useEntity = new WrapperPlayClientInteractEntity(event);

            if (useEntity.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                attacked = true;
                id = useEntity.getEntityId();
            }
        } else if (PacketUtil.isFlying(event.getPacketType())) {
            if (!attacked
                    || data.getPlayer().getGameMode() == GameMode.CREATIVE
                    || data.getTargetLocs().isEmpty()
            ) return;
            attacked = false;


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

    public List<Pair<AxisAlignedBB, Long>> getRightLocations(int time, int delta) {
        return data.getTargetLocs().stream()
                .sorted(Comparator.comparingLong(pair -> Math.abs(pair.getY() - (System.currentTimeMillis() - time))))
                .filter(pair -> Math.abs(pair.getY() - (System.currentTimeMillis() - time)) < delta)
                .collect(Collectors.toList());
    }

}
