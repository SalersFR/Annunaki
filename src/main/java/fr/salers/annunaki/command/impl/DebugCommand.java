package fr.salers.annunaki.command.impl;

import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.command.CommandInfo;
import fr.salers.annunaki.command.SubCommand;
import fr.salers.annunaki.config.Config;
import fr.salers.annunaki.data.PlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(command = "debug", console = false, permission = "annunaki.command.debug")
public class DebugCommand extends SubCommand {

    @Override
    public void handle(CommandSender sender, String[] args) {
        PlayerData data = Annunaki.getInstance().getPlayerDataManager().get((Player)sender);

        if (args.length < 2) {
            data.setDebugging("");

            send(sender, Config.DEBUGGING_DISABLED.getAsString());
            return;
        }

        String debugging = args[1];

        data.setDebugging(debugging);

        send(sender,
                Config.DEBUGGING_ENABLED.getAsString().replaceAll("%debugging%", debugging));
    }
}
