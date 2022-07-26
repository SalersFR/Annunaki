package fr.salers.annunaki.data;


import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.config.Config;
import fr.salers.annunaki.data.processor.Processor;
import fr.salers.annunaki.data.processor.TrackingProcessor;
import fr.salers.annunaki.data.processor.impl.*;
import fr.salers.annunaki.manager.CheckManager;
import fr.salers.annunaki.util.Pair;
import fr.salers.annunaki.util.mc.AxisAlignedBB;
import fr.salers.annunaki.util.version.ClientVersion;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class PlayerData {

    private final Player player;

    private final List<Check> checks = new CheckManager(this).getChecks();
    private final List<Processor> processors = new ArrayList<>();

    private final PositionProcessor positionProcessor = new PositionProcessor(this);
    private final RotationProcessor rotationProcessor = new RotationProcessor(this);
    private final TransactionProcessor transactionProcessor = new TransactionProcessor(this);
    private final VelocityProcessor velocityProcessor = new VelocityProcessor(this);
    private final ActionProcessor actionProcessor = new ActionProcessor(this);
    private final CollisionProcessor collisionProcessor = new CollisionProcessor(this);
    private final StatusProcessor statusProcessor = new StatusProcessor(this);
    private final TeleportProcessor teleportProcessor = new TeleportProcessor(this);
    // private final EntityProcessor entityProcessor = new EntityProcessor(this);

    private List<Pair<AxisAlignedBB, Long>> targetLocs = new ArrayList<>();

    @Setter
    private boolean alerts, autobanning;

    @Setter
    private int tick;

    @Setter
    private String debugging = "";

    private ClientVersion version;

    public void ban() {
        if (autobanning) return;
        autobanning = true;

        Bukkit.getScheduler().runTask(Annunaki.getInstance(), () ->
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        Config.AUTOBAN_COMMAND.getAsString()
                                .replaceAll("%player%", player.getName())));
    }

    public void confirm(Runnable runnable) {
        transactionProcessor.confirm(runnable);
    }

    public void confirmPost(Runnable runnable) {
        transactionProcessor.confirmPost(runnable);
    }

    public void setVersion(ClientVersion matchProtocol) {
        this.version = matchProtocol;
    }
}
