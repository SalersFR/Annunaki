package fr.salers.annunaki.command.impl;

import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.command.CommandInfo;
import fr.salers.annunaki.command.SubCommand;
import fr.salers.annunaki.config.Config;
import fr.salers.annunaki.data.PlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(command = "alerts", console = false, permission = "annunaki.command.alerts")
public class AlertsCommand extends SubCommand {

    @Override
    public void handle(CommandSender sender, String[] args) {
        PlayerData data = Annunaki.getInstance().getPlayerDataManager().get((Player)sender);

        String message = data.isAlerts()
                ? Config.ALERTS_DISABLED.getAsString()
                : Config.ALERTS_ENABLED.getAsString();

        data.setAlerts(!data.isAlerts());

        send(sender, message);
    }
}
