package fr.salers.annunaki.gui.impl;

import fr.salers.annunaki.gui.AbstractGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

/**
 * @author Salers
 * made on fr.salers.annunaki.gui.impl
 */
public class MainGUI extends AbstractGUI {

    public MainGUI() {
        super(Bukkit.createInventory(null, 17, "§dAnnunaki - Main GUI"));
    }

    @Override
    public void createItems() {
        createItem(Material.ITEM_FRAME, () -> Bukkit.broadcastMessage("L"), "", null);
    }
}
