package fr.salers.annunaki.manager;

import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.impl.aim.*;
import fr.salers.annunaki.check.impl.autoclicker.*;
import fr.salers.annunaki.check.impl.badpackets.*;
import fr.salers.annunaki.check.impl.fly.FlyA;
import fr.salers.annunaki.check.impl.fly.FlyB;
import fr.salers.annunaki.check.impl.groundspoof.Groundspoof;
import fr.salers.annunaki.check.impl.killaura.KillAuraA;
import fr.salers.annunaki.check.impl.killaura.KillAuraB;
import fr.salers.annunaki.check.impl.motion.MotionA;
import fr.salers.annunaki.check.impl.motion.MotionB;
import fr.salers.annunaki.check.impl.motion.MotionC;
import fr.salers.annunaki.check.impl.reach.ReachA;
import fr.salers.annunaki.check.impl.reach.ReachB;
import fr.salers.annunaki.check.impl.speed.SpeedA;
import fr.salers.annunaki.check.impl.strafe.StrafeA;
import fr.salers.annunaki.check.impl.timer.TimerA;
import fr.salers.annunaki.check.impl.timer.TimerB;
import fr.salers.annunaki.check.impl.velocity.VelocityA;
import fr.salers.annunaki.config.Config;
import fr.salers.annunaki.data.PlayerData;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.List;

@Getter
public class CheckManager {
    private final List<Check> checks;

    public static boolean silent;

    public CheckManager() {

        this.checks = Arrays.asList(
                new AimA(),
                new AimB(),
                new AimC(),
                new AimD(),
                new AimE(),
                new AutoclickerA(),
                new AutoclickerB(),
                new AutoclickerC(),
                new AutoclickerD(),
                new AutoclickerE(),
                new BadPacketsA(),
                new BadPacketsB(),
                new BadPacketsC(),
                new BadPacketsD(),
                new BadPacketsE(),
                new BadPacketsF(),
                new Groundspoof(),
                new KillAuraA(),
                new KillAuraB(),
                new MotionA(),
                new MotionB(),
                new MotionC(),
                new ReachA(),
                new ReachB(),
                new SpeedA(),
                new StrafeA(),
                new FlyA(),
                new FlyB(),
                new TimerA(),
                new TimerB(),
                new VelocityA()
        );
    }

    public void addChecks(PlayerData data) {
        data.getChecks().addAll(checks);
        data.getChecks().forEach(check -> check.setData(data));
    }

    public void alert(PlayerData data, Check check, String info) {
        TextComponent alert = new TextComponent();

        alert.setText(Config.ALERT.getAsString()
                .replaceAll("%player%", data.getPlayer().getName())
                .replaceAll("%type%", check.getCheckInfo().type())
                .replaceAll("%name%", check.getCheckInfo().name())
                .replaceAll("%experimental%", check.getCheckInfo().experimental() ? "*" : "")
                .replaceAll("%vl%", String.valueOf(check.getVl()))
                .replaceAll("%tps%", "" + SpigotReflectionUtil.getTPS())
                .replaceAll("%version%", data.getVersion().getVersion())
                .replaceAll("%ping%", "" + data.getTransactionProcessor().getPing()));

        alert.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(
                        Config.HOVER.getAsString()
                                .replaceAll("%description%", check.getCheckInfo().description())
                                .replaceAll("%info%", info).replaceAll("%tps%", "" +SpigotReflectionUtil.getTPS()).replaceAll("%version%", data.getVersion().getVersion()).replaceAll("%ping%", "" + data.getTransactionProcessor().getPing())
                ).create()));

        alert.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                Config.CLICK_COMMAND.getAsString()
                        .replaceAll("%player%", data.getPlayer().getName())));

        // TODO: Make a list of alerting players so we dont have to sort every alert
        Annunaki.getInstance().getPlayerManager().values().stream()
                .filter(PlayerData::isAlerts)
                .filter(s -> (System.currentTimeMillis() - s.getLastAlert() > s.getAlertDelay())).forEach(s -> {
                    s.getPlayer().spigot().sendMessage(alert);
                    s.setLastAlert(System.currentTimeMillis());
                });

        if(!data.getPlayer().hasPermission("annunaki.bypass.punishment")
                && check.getCheckInfo().punish() && check.getPunishCommands().get(check.getVl()) != null && !Config.SILENT.getAsBoolean()) {
            check.getPunishCommands().get(check.getVl()).forEach(s ->  Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player%", data.getPlayer().getName()).replace("%check%", check.getCheckInfo().name())));
        }

        // TODO: log in database
    }

    public Check getCheck(StringBuilder check) {
        return checks.stream().filter(c -> check.toString().compareTo(c.getCheckInfo().name() + " " + c.getCheckInfo().type()) < 2).findFirst().orElse(null);
    }
}
