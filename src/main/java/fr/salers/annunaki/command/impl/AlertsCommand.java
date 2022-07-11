package fr.salers.annunaki.command.impl;

import fr.salers.annunaki.command.CommandInfo;
import fr.salers.annunaki.command.SubCommand;
import fr.salers.annunaki.config.Config;
import fr.salers.annunaki.data.PlayerData;

@CommandInfo(command = "alerts", permission = "ptsd.command.alerts")
public class AlertsCommand extends SubCommand {

    @Override
    public void handle(PlayerData data, String[] args) {
        String message = data.isAlerts()
                ? Config.ALERTS_DISABLED.getAsString()
                : Config.ALERTS_ENABLED.getAsString();

        data.setAlerts(!data.isAlerts());

        send(data, message);
    }
}
