package fr.salers.annunaki.manager;

import fr.salers.annunaki.gui.impl.MainGUI;
import fr.salers.annunaki.gui.impl.checks.ChecksMainGUI;
import fr.salers.annunaki.gui.impl.checks.combat.CombatChecksGUI;
import fr.salers.annunaki.gui.impl.checks.combat.impl.AimChecksGUI;
import lombok.Getter;

/**
 * @author Salers
 * made on fr.salers.annunaki.manager
 */

@Getter
public class GuiManager {

    private final MainGUI mainGUI = new MainGUI();
    private final ChecksMainGUI checksMainGUI = new ChecksMainGUI();
    private final CombatChecksGUI combatChecksGUI = new CombatChecksGUI();

    private final AimChecksGUI aimChecksGUI = new AimChecksGUI();
}
