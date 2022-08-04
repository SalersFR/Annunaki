package fr.salers.annunaki.command.impl;

import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.command.CommandInfo;
import fr.salers.annunaki.command.SubCommand;
import fr.salers.annunaki.data.PlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Salers
 * made on fr.salers.annunaki.command.impl
 */

@CommandInfo(command = "settings", console = false, permission = "annuanki.commands.settings")
public class SettingsCommand extends SubCommand {

    @Override
    public void handle(CommandSender sender, String[] args) {
        final Player player = (Player) sender;
        final PlayerData data = Annunaki.getInstance().getPlayerManager().get(player);

        data.getGuiManager().getMainGUI().display(player);

    }
}
