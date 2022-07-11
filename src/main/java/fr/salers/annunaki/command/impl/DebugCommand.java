package fr.salers.annunaki.command.impl;

import fr.salers.annunaki.command.CommandInfo;
import fr.salers.annunaki.command.SubCommand;
import fr.salers.annunaki.config.Config;
import fr.salers.annunaki.data.PlayerData;

@CommandInfo(command = "debug", permission = "ptsd.command.debug")
public class DebugCommand extends SubCommand {

    @Override
    public void handle(PlayerData data, String[] args) {
        if (args.length < 2) {
            data.setDebugging("");

            send(data, Config.DEBUGGING_DISABLED.getAsString());
            return;
        }

        String debugging = args[1];

        data.setDebugging(debugging);

        send(data,
                Config.DEBUGGING_ENABLED.getAsString().replaceAll("%debugging%", debugging));
    }
}
