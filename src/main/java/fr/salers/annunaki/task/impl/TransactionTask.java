package fr.salers.annunaki.task.impl;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowConfirmation;
import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.data.PlayerData;
import fr.salers.annunaki.task.type.PostTask;
import fr.salers.annunaki.task.type.PreTask;
import fr.salers.annunaki.util.Pair;
import fr.salers.annunaki.util.mc.AxisAlignedBB;

import java.util.Objects;

public class TransactionTask implements PreTask, PostTask {

    @Override
    public void handlePreTick() {
        WrapperPlayServerWindowConfirmation transaction =
                new WrapperPlayServerWindowConfirmation(0, (short) -4516, false);
        for (PlayerData value : Annunaki.getInstance().getPlayerManager().values()) {
            if (value.getActionProcessor().getLastTarget() != null) {
                final AxisAlignedBB aabb = Annunaki.getInstance().getNmsManager().getNmsImplementation().getAABB(value.getPlayer());
                value.getTargetLocs().add(new Pair<>(new AxisAlignedBB(value.getActionProcessor().getLastTarget().getLocation()), System.currentTimeMillis()));
                if (value.getTargetLocs().size() >= 60)
                    value.getTargetLocs().clear();
            }
        }

        Annunaki.getInstance().getPlayerManager().values().forEach(data
                -> PacketEvents.getAPI().getPlayerManager().sendPacket(data.getPlayer(), transaction));
    }

    @Override
    public void handlePostTick() {
        WrapperPlayServerWindowConfirmation transaction =
                new WrapperPlayServerWindowConfirmation(0, (short) -4517, false);

        Annunaki.getInstance().getPlayerManager().values().stream().filter(Objects::nonNull).forEach(data
                -> PacketEvents.getAPI().getPlayerManager().sendPacket(data.getPlayer(), transaction));
    }
}
