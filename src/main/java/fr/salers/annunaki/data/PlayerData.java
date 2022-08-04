package fr.salers.annunaki.data;


import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.data.processor.Processor;
import fr.salers.annunaki.data.processor.impl.*;
import fr.salers.annunaki.gui.AbstractGUI;
import fr.salers.annunaki.manager.GuiManager;
import fr.salers.annunaki.util.Pair;
import fr.salers.annunaki.util.mc.AxisAlignedBB;
import fr.salers.annunaki.util.version.ClientVersion;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class PlayerData {

    private final UUID id;
    private final Player player = null;

    private final List<Check> checks = List.of();
    private final List<Processor> processors = new ArrayList<>();

    private final PositionProcessor positionProcessor = new PositionProcessor(this);
    private final RotationProcessor rotationProcessor = new RotationProcessor(this);
    private final TransactionProcessor transactionProcessor = new TransactionProcessor(this);
    private final VelocityProcessor velocityProcessor = new VelocityProcessor(this);
    private final ActionProcessor actionProcessor = new ActionProcessor(this);
    private final CollisionProcessor collisionProcessor = new CollisionProcessor(this);
    private final StatusProcessor statusProcessor = new StatusProcessor(this);
    private final TeleportProcessor teleportProcessor = new TeleportProcessor(this);

    @Setter
    private long lastAlert = 0, alertDelay = 1000, debugDelay = 1000;

    @Setter
    private List<Check> debugging = List.of(); // trollage


    @Setter
    private AbstractGUI guiOpen;
    // private final EntityProcessor entityProcessor = new EntityProcessor(this);

    private final GuiManager guiManager = new GuiManager();

    private final List<Pair<AxisAlignedBB, Long>> targetLocs = new ArrayList<>();

    @Setter
    private boolean alerts, punished;

    @Setter
    private int tick;

    private ClientVersion version = ClientVersion.v18;

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
