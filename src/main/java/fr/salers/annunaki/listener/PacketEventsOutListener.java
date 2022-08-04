package fr.salers.annunaki.listener;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.data.PlayerData;
import org.bukkit.entity.Player;

public class PacketEventsOutListener extends PacketListenerAbstract {

    public PacketEventsOutListener() {
        super(PacketListenerPriority.LOWEST);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        Player player = (Player) event.getPlayer();

        if (player == null) return;

        PlayerData data = Annunaki.getInstance().getPlayerManager().get(player);

        if (data == null) return;

        data.getProcessors().forEach(processor -> processor.handlePre(event));

        data.getChecks().stream().filter(check -> check.getConfigInfo().isEnabled()).forEach(check -> check.handle(event));

        data.getProcessors().forEach(processor -> processor.handlePost(event));
    }
}
