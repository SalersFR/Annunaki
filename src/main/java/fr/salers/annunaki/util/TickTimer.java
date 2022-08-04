package fr.salers.annunaki.util;

import lombok.Getter;

/**
 * @author Salers
 * made on fr.salers.annunaki.util
 */

@Getter
public class TickTimer {

    private int lastTrue = 0;
    private int lastFalse = 0;

    public void tick(final boolean tick) {
        if (tick) {
            lastTrue = 0;
            lastFalse++;

        } else {
            lastTrue++;
            lastFalse = 0;
        }
    }

    public boolean passed(final int ticks) {
        return lastTrue > ticks;
    }
    public boolean occurred(final int ticks) {
        return lastFalse > ticks;
    }

}
