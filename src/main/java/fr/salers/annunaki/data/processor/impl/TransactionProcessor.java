package fr.salers.annunaki.data.processor.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientWindowConfirmation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowConfirmation;
import fr.salers.annunaki.data.PlayerData;
import fr.salers.annunaki.data.processor.Processor;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

@Getter
public class TransactionProcessor extends Processor {

    private final ArrayDeque<List<Runnable>> transactionTasks = new ArrayDeque<>();
    private final List<Runnable> postTasks = new ArrayList<>();
    private int sent, received;

    long timestamp;

    int ping;

    public TransactionProcessor(PlayerData data) {
        super(data);
    }

    @Override
    public void handlePre(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.WINDOW_CONFIRMATION) {
            WrapperPlayClientWindowConfirmation transaction = new WrapperPlayClientWindowConfirmation(event);

            if (!transaction.isAccepted()) {
                Bukkit.broadcastMessage("error 1");
                return;
            }

            if (transaction.getWindowId() != 0) return;

            short id = transaction.getActionId();

            if (id != -4516 && id != -4517) return;

            ping = (int) (System.currentTimeMillis() - timestamp);

            ++received;

            if (transactionTasks.peekFirst() == null) {
                Bukkit.broadcastMessage("null 2");
                return;
            }
            transactionTasks.pollFirst().forEach(task -> {
                if (task != null) task.run();
            });
        }
    }

    @Override
    public void handlePre(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.WINDOW_CONFIRMATION) {
            WrapperPlayServerWindowConfirmation transaction = new WrapperPlayServerWindowConfirmation(event);

            if (transaction.getWindowId() != 0 || transaction.isAccepted()) return;

            short id = transaction.getActionId();

            // We only use these 2 ids to stop minimize conflicts with the server and other plugins
            if (id != -4516 && id != -4517) return;

            ++sent;

            timestamp = System.currentTimeMillis();

            if (postTasks.isEmpty()) {
                transactionTasks.add(new ArrayList<>());
            } else {
                transactionTasks.add(postTasks);
                postTasks.clear();
            }
        }
    }

    public void confirm(Runnable runnable) {
        if (sent == received) {
            runnable.run();

            return;
        }

        if (transactionTasks.isEmpty()) {
            Bukkit.broadcastMessage("Added to empty list!");
        } else {
            transactionTasks.peekLast().add(runnable);

        }
    }

    public void confirmPost(Runnable runnable) {
        postTasks.add(runnable);
    }
}
