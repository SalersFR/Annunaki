package fr.salers.annunaki.manager;

import fr.salers.annunaki.check.Check;
import fr.salers.annunaki.check.impl.aim.*;
import fr.salers.annunaki.check.impl.autoclicker.*;
import fr.salers.annunaki.check.impl.badpackets.BadPacketsA;
import fr.salers.annunaki.check.impl.badpackets.BadPacketsB;
import fr.salers.annunaki.check.impl.badpackets.BadPacketsC;
import fr.salers.annunaki.check.impl.badpackets.BadPacketsD;
import fr.salers.annunaki.check.impl.fly.FlyA;
import fr.salers.annunaki.check.impl.killaura.KillAuraA;
import fr.salers.annunaki.check.impl.killaura.KillAuraB;
import fr.salers.annunaki.check.impl.motion.MotionA;
import fr.salers.annunaki.check.impl.motion.MotionB;
import fr.salers.annunaki.check.impl.reach.ReachA;
import fr.salers.annunaki.check.impl.speed.SpeedA;
import fr.salers.annunaki.check.impl.strafe.StrafeA;
import fr.salers.annunaki.check.impl.timer.TimerA;
import fr.salers.annunaki.check.impl.timer.TimerB;
import fr.salers.annunaki.check.impl.velocity.VelocityA;
import fr.salers.annunaki.data.PlayerData;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public class CheckManager {

    private final List<Check> checks;

    public CheckManager(final PlayerData data) {
        this.checks = Arrays.asList(
                new AimA(data),
                new AimB(data),
                new AimC(data),
                new AimD(data),
                new AimE(data),
                new VelocityA(data),
                new BadPacketsA(data),
                new BadPacketsB(data),
                new BadPacketsC(data),
                new BadPacketsD(data),
                new AutoclickerA(data),
                new AutoclickerB(data),
                new AutoclickerC(data),
                new AutoclickerD(data),
                new AutoclickerE(data),
                new KillAuraA(data),
                new KillAuraB(data),
                new ReachA(data),
                new SpeedA(data),
                new StrafeA(data),
                new MotionA(data),
                new MotionB(data),
                new FlyA(data),
                new TimerA(data),
                new TimerB(data)


        );
    }
}
