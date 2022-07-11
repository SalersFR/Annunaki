package fr.salers.annunaki.data.processor;


import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import fr.salers.annunaki.data.PlayerData;
import lombok.Getter;

@Getter
public abstract class Processor {

    protected final PlayerData data;

    public Processor(PlayerData data) {
        this.data = data;

        data.getProcessors().add(this);
    }

    public void handlePre(PacketReceiveEvent event) {
    }

    public void handlePre(PacketSendEvent event) {
    }

    public void handlePost(PacketReceiveEvent event) {
    }

    public void handlePost(PacketSendEvent event) {
    }
}
