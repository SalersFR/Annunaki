package fr.salers.annunaki.util.lag;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.potion.PotionEffectType;

/**
 * @author Salers
 * made on dev.notonweed.annunaki.util
 */

@AllArgsConstructor
@Data
public class ConfirmedPotionEffect {

    private PotionEffectType type;
    private int amplifier;
}
