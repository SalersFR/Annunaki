package fr.salers.annunaki.command.impl;

import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.check.Check;
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
        PlayerData data = Annunaki.getInstance().getPlayerManager().get((Player)sender);

        if (args.length < 2) {
            for(Check check1 : data.getDebugging()) {
                send(sender, check1.getCheckInfo().name() + " " + check1.getCheckInfo().type() + "\n");
            }
            return;
        }

        StringBuilder check = new StringBuilder();
        for(int i = 2; i < args.length; i++) {
            check.append(args[i]).append(" ");
        }

        if(Annunaki.getInstance().getCheckManager().getCheck(check) != null) {
            Check c = Annunaki.getInstance().getCheckManager().getCheck(check);
            if(data.getDebugging().contains(c)) {
                data.getDebugging().remove(c);
                send(sender, Config.DEBUGGING_DISABLED.getAsString());
            } else {
                data.getDebugging().add(c);
                send(sender, Config.DEBUGGING_ENABLED.getAsString());
            }
        } else {
            send(sender, "§c§lDEBUG §8§l» §7Check not found");
        }
    }
}
