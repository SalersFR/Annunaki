package fr.salers.annunaki.gui.impl.checks.combat;

import fr.salers.annunaki.gui.AbstractGUI;
import org.bukkit.Bukkit;

/**
 * @author Salers
 * made on fr.salers.annunaki.gui.impl.checks.combat
 */
public class CombatChecksGUI extends AbstractGUI {

    public CombatChecksGUI() {
        super(Bukkit.createInventory(null, 36   , "§eCombat Checks"));
    }

    @Override
    public void createItems() {

    }
}
