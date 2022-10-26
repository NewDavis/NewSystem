package me.newdavis.spigot.plugin.newsystem.inventory;

import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChooseFileInventory {

    public final String TITLE = "§8» §fConfig";
    private final int rows = 3;

    //Items
    private final ItemStack GLASS = new ItemBuilder(Material.STAINED_GLASS_PANE, (short)7).build();

    //File Items
    private final ItemStack COMMANDFILE = new ItemBuilder(Material.CHEST)
            .setName("§7File: §fCommand.yml")
            .setLore(new String[]{SettingsFile.getPrefix() + " §7Klicke um die §fCommand.yml §7zu bearbeiten."}).build();
    private final ItemStack LISTENERFILE = new ItemBuilder(Material.CHEST)
            .setName("§7File: §fListener.yml")
            .setLore(new String[]{SettingsFile.getPrefix() + " §7Klicke um die §fListener.yml §7zu bearbeiten."}).build();
    private final ItemStack OTHERFILE = new ItemBuilder(Material.CHEST)
            .setName("§7File: §fOther.yml")
            .setLore(new String[]{SettingsFile.getPrefix() + " §7Klicke um die §fOther.yml §7zu bearbeiten."}).build();
    private final ItemStack KITFILE = new ItemBuilder(Material.CHEST)
            .setName("§7File: §fKit.yml")
            .setLore(new String[]{SettingsFile.getPrefix() + " §7Klicke um die §fKit.yml §7zu bearbeiten."}).build();
    private final ItemStack TABLISTFILE = new ItemBuilder(Material.CHEST)
            .setName("§7File: §fTabList.yml")
            .setLore(new String[]{SettingsFile.getPrefix() + " §7Klicke um die §fTabList.yml §7zu bearbeiten."}).build();
    private final ItemStack SETTINGSFILE = new ItemBuilder(Material.CHEST)
            .setName("§7File: §fSettings.yml")
            .setLore(new String[]{SettingsFile.getPrefix() + " §7Klicke um die §fSettings.yml §7zu bearbeiten."}).build();

    public void openInventory(Player p) {
        Inventory inventory = Bukkit.createInventory(null, 9*rows, TITLE);

        for(int i = 0; i < 9*rows; i++) {
            if(i <= 9) {
                inventory.setItem(i, GLASS);
            }else if(i >= 17) {
                inventory.setItem(i, GLASS);
            }
        }
        inventory.addItem(COMMANDFILE, LISTENERFILE, OTHERFILE, KITFILE, TABLISTFILE, SETTINGSFILE);

        p.openInventory(inventory);
    }

}
