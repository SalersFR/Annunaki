package fr.salers.annunaki.gui;

import fr.salers.annunaki.gui.item.SimpleItem;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Salers
 * made on fr.salers.annunaki.gui
 */

@Getter
public abstract class AbstractGUI {

    protected final Inventory inventory;
    private final List<SimpleItem> items;

    public AbstractGUI(final Inventory inventory) {
        this.inventory = inventory;
        items = new ArrayList<>();

        createItems();
    }

    public SimpleItem createItem(final Material type, final Runnable runnable, final String name, final List<String> lore) {
        return new SimpleItem(type, runnable).setName(name).setLore(lore);


    }

    public void addItem(final SimpleItem simpleItem) {
        this.items.add(simpleItem);

    }

    public abstract void createItems();



}
