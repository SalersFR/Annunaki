package fr.salers.annunaki.data.processor.impl;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEffect;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerAbilities;
import fr.salers.annunaki.data.PlayerData;
import fr.salers.annunaki.data.processor.Processor;
import fr.salers.annunaki.util.lag.ConfirmedAbilities;
import fr.salers.annunaki.util.lag.ConfirmedPotionEffect;
import fr.salers.annunaki.util.lag.ConfirmedPotionStatus;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;

/**
 * @author Salers
 * made on dev.notonweed.annunaki.data.processor.impl
 */

@Getter
public class StatusProcessor extends Processor {

    private final Deque<ConfirmedAbilities> statuses = new LinkedList<>();
    private final Deque<ConfirmedPotionStatus> confirmedPotionStatuses = new LinkedList<>();

    public StatusProcessor(PlayerData data) {
        super(data);

    }

    @Override
    public void handlePre(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.PLAYER_ABILITIES) {
            final WrapperPlayServerPlayerAbilities wrapper = new WrapperPlayServerPlayerAbilities(event);

            data.getTransactionProcessor().confirm(() -> statuses.add(
                    new ConfirmedAbilities(
                            wrapper.isInGodMode(),
                            wrapper.isFlying(),
                            wrapper.isFlightAllowed(),
                            wrapper.isInCreativeMode(),
                            wrapper.getFlySpeed(),
                            wrapper.getFOVModifier())
            ));

            if (statuses.size() > 1)
                data.getTransactionProcessor().confirmPost(statuses::removeFirst);


        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_EFFECT) {
            final WrapperPlayServerEntityEffect wrapper = new WrapperPlayServerEntityEffect(event);

            if (wrapper.getEntityId() != data.getPlayer().getEntityId()) return;

            data.getTransactionProcessor().confirm(() -> confirmedPotionStatuses.add(
                    new ConfirmedPotionStatus(Arrays.asList(
                            new ConfirmedPotionEffect(
                                    PotionEffectType.getById(wrapper.getPotionType().getId()),
                                    wrapper.getEffectAmplifier())
                    ))));

            if (confirmedPotionStatuses.size() > 1)
                data.getTransactionProcessor().confirmPost(confirmedPotionStatuses::removeFirst);
        }
    }

    public ConfirmedAbilities getLastAbilities() {
        return statuses.peekLast() == null ? new ConfirmedAbilities(false, data.getPlayer().isFlying(), data.getPlayer().getAllowFlight(),
                data.getPlayer().getGameMode() == GameMode.CREATIVE, data.getPlayer().getFlySpeed(), data.getPlayer().getWalkSpeed() / 2) : statuses.peekLast();
    }
}
