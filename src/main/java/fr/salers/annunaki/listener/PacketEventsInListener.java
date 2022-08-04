package fr.salers.annunaki.listener;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.data.PlayerData;
import org.bukkit.entity.Player;

public class PacketEventsInListener extends PacketListenerAbstract {

    public PacketEventsInListener() {
        super(PacketListenerPriority.HIGHEST);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        Player player = (Player) event.getPlayer();

        if (player == null) return;

        PlayerData data = Annunaki.getInstance().getPlayerManager().get(player);

        if (data == null) return;

        data.getProcessors().forEach(processor -> processor.handlePre(event));

        data.getChecks().stream().filter(c -> c.getConfigInfo().isEnabled()).forEach(c -> c.handle(event));

        data.getProcessors().forEach(processor -> processor.handlePost(event));
    }
}
