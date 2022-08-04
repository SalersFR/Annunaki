package fr.salers.annunaki.gui.impl.checks.combat;

import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.gui.AbstractGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;

/**
 * @author Salers
 * made on fr.salers.annunaki.gui.impl.checks.combat
 */
public class CombatChecksGUI extends AbstractGUI {

    public CombatChecksGUI() {
        super(Bukkit.createInventory(null, 27   , "§eCombat Checks"));
    }

    @Override
    public void createItems() {
        createItem(Material.FISHING_ROD,
                (player) -> Annunaki.getInstance().getPlayerManager().get(player).getGuiManager().getAimChecksGUI().display(player)
        , "§Aim Checks", null, 11);

    }
}
