package fr.salers.annunaki.gui.impl.checks.combat.impl;

import fr.salers.annunaki.Annunaki;
import fr.salers.annunaki.gui.AbstractGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.List;
import java.util.Locale;

/**
 * @author Salers
 * made on fr.salers.annunaki.gui.impl.checks.combat.impl
 */
public class AimChecksGUI extends AbstractGUI {

    public AimChecksGUI() {
        super(Bukkit.createInventory(null, 36, "§aAim Checks"));

    }

    @Override
    public void createItems() {
        int i = 0;
        for(String checks : Annunaki.getInstance().getCheckConfig().getConfigurationSection("aim.").getKeys(false)) {

            final boolean enabled = Annunaki.getInstance().getCheckConfig().getBoolean("aim." + checks + ".enabled");
            final String enabledChar = !enabled ? "§a✓" : "§c✗";

            createItem(enabled ? Material.WRITTEN_BOOK : Material.BOOK, player -> Annunaki.getInstance().
                    getCheckConfig().modifyCheckStatus(!enabled, "aim." + checks + ".enabled"), "Aim " + checks.toUpperCase(Locale.ROOT)
                    , List.of("§dEnabled : " + enabled), i);
            i++;
        }

    }
}
