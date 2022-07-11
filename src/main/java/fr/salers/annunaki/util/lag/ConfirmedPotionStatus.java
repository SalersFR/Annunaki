package fr.salers.annunaki.util.lag;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Salers
 * made on dev.notonweed.annunaki.util.lag
 */

@AllArgsConstructor
@Getter
public class ConfirmedPotionStatus {

    private List<ConfirmedPotionEffect> confirmedPotionEffects = new ArrayList<>();
}
