package fr.salers.annunaki.util.lag;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Salers
 * made on dev.notonweed.annunaki.util
 */

@Data
@AllArgsConstructor
public class ConfirmedAbilities {

    private boolean godMode, flying, flightAllowed, creativeMode;
    private float flySpeed, fovModifier;
}
