package fr.salers.annunaki.util;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

@UtilityClass
public class ColorUtil {

    public String translate(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
