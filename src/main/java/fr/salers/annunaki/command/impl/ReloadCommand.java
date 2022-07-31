package fr.salers.annunaki.command.impl;

import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.command.CommandInfo;
import fr.salers.annunaki.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

@CommandInfo(command = "reload", console = true, permission = "annunaki.command.reload")
public class ReloadCommand extends SubCommand {


    @Override
    public void handle(CommandSender sender, String[] args) {
        if(args.length == 1) {
            long start = System.currentTimeMillis();
            Bukkit.getPluginManager().disablePlugin(Annunaki.getInstance());
            Bukkit.getPluginManager().enablePlugin(Bukkit.getPluginManager().getPlugin("Annunaki"));
            send(sender, "§dAnnunaki has been reloaded in §r" + (System.currentTimeMillis() - start) + "ms§d.");
        }
    }
}
