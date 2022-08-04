package fr.salers.annunaki.gui.impl;

import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.gui.AbstractGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;

/**
 * @author Salers
 * made on fr.salers.annunaki.gui.impl
 */
public class MainGUI extends AbstractGUI {

    public MainGUI() {
        super(Bukkit.createInventory(null, 27, "§dAnnunaki"));
    }

    @Override
    public void createItems() {
        createItem(Material.ITEM_FRAME, (player) -> Annunaki.getInstance().getPlayerManager().get(player).getGuiManager()
                .getChecksMainGUI().display(player), "§cChecks", null, 11);
    }
}
