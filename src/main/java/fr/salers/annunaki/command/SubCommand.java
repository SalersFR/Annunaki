package fr.salers.annunaki.command;

import fr.salers.annunaki.data.PlayerData;
import fr.salers.annunaki.util.ColorUtil;

public abstract class SubCommand {

    public abstract void handle(PlayerData data, String[] args);

    protected void send(PlayerData data, String message) {
        data.getPlayer().sendMessage(ColorUtil.translate(message));
    }
}
