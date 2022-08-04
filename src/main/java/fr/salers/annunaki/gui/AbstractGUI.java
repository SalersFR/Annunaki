package fr.salers.annunaki.gui;

import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.gui.item.SimpleItem;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
        setItems();
    }

    public SimpleItem createItem(final Material type, final Consumer<Player> runnable, final String name, final List<String> lore, final int slot) {
        return addItem(new SimpleItem(type, runnable, slot).setName(name).setLore(lore));

    }

    public SimpleItem addItem(final SimpleItem simpleItem) {
        this.items.add(simpleItem);
        return simpleItem;

    }

    public void display(final Player player) {
        Annunaki.getInstance().getPlayerManager().get(player).setGuiOpen(this);
        player.openInventory(this.inventory);
    }

    public void handleClickEvent(final InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final int itemId = event.getCurrentItem().getTypeId() * event.getRawSlot();

        //wtf??
        if(event.getCurrentItem().getType() == Material.AIR) return;

        this.items.stream().filter(simpleItem -> simpleItem.getSalersId() == itemId).findAny().get().getClickAction().accept(player);

        this.inventory.clear();
        this.items.clear();

        createItems();
        setItems();

        player.updateInventory();


    }

    protected void setItems() {
        items.forEach(simpleItem -> this.inventory.setItem(simpleItem.getSlot(), simpleItem.create()));

    }

    public abstract void createItems();



}
