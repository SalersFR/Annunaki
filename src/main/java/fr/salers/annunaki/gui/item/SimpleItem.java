package fr.salers.annunaki.gui.item;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Salers
 * made on fr.salers.annunaki.gui.item
 */

@Getter
public class SimpleItem {

    private final Material type;
    private final Consumer<Player> clickAction;
    private final ItemStack itemStack;
    private final ItemMeta itemMeta;
    private final int salersId, slot;

    public SimpleItem(final Material type, final Consumer<Player> clickAction, final int slot) {
        this.type = type;
        this.clickAction = clickAction;

        this.itemStack = new ItemStack(type);
        this.itemMeta = itemStack.getItemMeta();

        this.salersId = slot * type.getId();
        this.slot = slot;
    }

    public SimpleItem setName(final String s) {
        this.itemMeta.setDisplayName(s);
        return this;
    }

    public SimpleItem setLore(final List<String> lore) {
        this.itemMeta.setLore(lore);
        return this;
    }

    public ItemStack create() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }


}
