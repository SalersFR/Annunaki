package fr.salers.annunaki.gui.impl.checks;

import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.gui.AbstractGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;

/**
 * @author Salers
 * made on fr.salers.annunaki.gui.impl.checks
 */
public class ChecksMainGUI extends AbstractGUI {

    public ChecksMainGUI() {
        super(Bukkit.createInventory(null, 27, "§cChecks"));
    }

    @Override
    public void createItems() {
        createItem(Material.DIAMOND_AXE, (player) -> Annunaki.getInstance().getPlayerManager().get(player).getGuiManager()
                .getCombatChecksGUI().display(player),"§6Combat Checks", null, 11);


    }
}
