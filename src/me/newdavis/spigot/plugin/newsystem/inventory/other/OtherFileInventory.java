package me.newdavis.spigot.plugin.newsystem.inventory.other;

import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.file.OtherFile;
import me.newdavis.spigot.util.ItemBuilder;
import me.newdavis.spigot.plugin.newsystem.Other;
import me.newdavis.spigot.plugin.newsystem.listener.Listeners;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class OtherFileInventory {

    public static HashMap<Player, Integer> page = new HashMap<>();

    public final String TITLE = "§8» §fOther";
    private final int rows = 6;

    //Items
    private final ItemStack GLASS = new ItemBuilder(Material.STAINED_GLASS_PANE, (short)7).build();
    private final ItemStack REDARROW_LEFT = ItemBuilder.getCustomSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjg0ZjU5NzEzMWJiZTI1ZGMwNThhZjg4OGNiMjk4MzFmNzk1OTliYzY3Yzk1YzgwMjkyNWNlNGFmYmEzMzJmYyJ9fX0="
            , "§7Vorherige Seite §8(§a{CurrentPage-1}§8/§a{MaxPages}§8)", Collections.singletonList(SettingsFile.getPrefix() + " §7Klicke, um zur vorherigen Seite zu gelangen."));
    private final ItemStack REDARROW_RIGHT = ItemBuilder.getCustomSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmNmZTg4NDVhOGQ1ZTYzNWZiODc3MjhjY2M5Mzg5NWQ0MmI0ZmMyZTZhNTNmMWJhNzhjODQ1MjI1ODIyIn19fQ="
            , "§7Nächste Seite §8(§a{CurrentPage+1}§8/§a{MaxPages}§8)", Collections.singletonList(SettingsFile.getPrefix() + " §7Klicke, um zur nächsten Seite zu gelangen."));
    private final ItemStack GRAYARROW_LEFT = ItemBuilder.getCustomSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTQyZmRlOGI4MmU4YzFiOGMyMmIyMjY3OTk4M2ZlMzVjYjc2YTc5Nzc4NDI5YmRhZGFiYzM5N2ZkMTUwNjEifX19="
            , "§7Zurück zur File Auswahl.", new ArrayList<>());
    private final ItemStack CURRENT_PAGE = new ItemBuilder(Material.COMPASS).setName("§7Aktuelle Seite: §a{CurrentPage}§8/§a{MaxPages}").build();

    public void openInventoryPage(Player p, int inventoryPage) {
        Inventory inventory = Bukkit.createInventory(null, 9*rows, TITLE);
        Other.deleteOther(p, "Other");

        List<String> others = OtherFile.getConfigurationSection("Other");
        double maxPagesDouble = (String.valueOf(others.size() / 27D).contains(".") ? (others.size() / 27D) +1 : others.size() / 27D);
        int maxPages = (int) maxPagesDouble;

        if(!(inventoryPage > maxPages)) {
            replace(REDARROW_LEFT, "{CurrentPage-1}", (inventoryPage - 1 != 0 ? String.valueOf(inventoryPage - 1) : String.valueOf(inventoryPage)));
            replace(REDARROW_RIGHT, "{CurrentPage+1}", (maxPages > inventoryPage ? String.valueOf(inventoryPage + 1) : String.valueOf(inventoryPage)));
            replace(REDARROW_LEFT, "{MaxPages}", String.valueOf(maxPages));
            replace(REDARROW_RIGHT, "{MaxPages}", String.valueOf(maxPages));
            replace(CURRENT_PAGE, "{CurrentPage}", String.valueOf(inventoryPage));
            replace(CURRENT_PAGE, "{MaxPages}", String.valueOf(maxPages));

            for (int i = 0; i < 9 * rows; i++) {
                if (i <= 9) {
                    inventory.setItem(i, GLASS);
                } else if (i == 17 || i == 18) {
                    inventory.setItem(i, GLASS);
                } else if (i == 26 || i == 27) {
                    inventory.setItem(i, GLASS);
                } else if (i == 35 || i == 36) {
                    inventory.setItem(i, GLASS);
                } else if (i == 44) {
                    inventory.setItem(i, GLASS);
                } else if (i == 45) {
                    inventory.setItem(i, GRAYARROW_LEFT);
                } else if (i == 46 || i == 47) {
                    inventory.setItem(i, GLASS);
                } else if (i == 48) {
                    inventory.setItem(i, REDARROW_LEFT);
                } else if (i == 49) {
                    inventory.setItem(i, CURRENT_PAGE);
                } else if (i == 50) {
                    inventory.setItem(i, REDARROW_RIGHT);
                } else if (i >= 51) {
                    inventory.setItem(i, GLASS);
                }
            }

            if (inventoryPage > 0) {
                if (inventoryPage == 1) {
                    for (String other : OtherFile.getConfigurationSection("Other")) {
                        if (inventory.getItem(43) == null || inventory.getItem(43).getType() == Material.AIR) {
                            ItemStack item = new ItemBuilder(Material.BOOK)
                                    .setName("§7Other: " + other)
                                    .build();
                            inventory.addItem(item);
                        }
                    }
                } else {
                    for (int i = (inventoryPage-1) * 28; i < (inventoryPage-1) * 28 + 29; i++) {
                        if (inventory.getItem(43) == null || inventory.getItem(43).getType() == Material.AIR) {
                            if(others.size() > (i)) {
                                ItemStack item = new ItemBuilder(Material.BOOK)
                                        .setName("§7Other: " + others.get(i))
                                        .build();
                                inventory.addItem(item);
                            }
                        }
                    }
                }
            }

            p.openInventory(inventory);
            page.put(p, inventoryPage);
        }else{
            p.sendMessage(SettingsFile.getPrefix() + " §cDiese Seite existiert nicht!");
        }
    }

    private void replace(ItemStack item, String replaceKey, String replaceValue) {
        if(new Listeners().checkItem(item)) {
            ItemMeta meta = item.getItemMeta();
            String displayName = meta.getDisplayName();
            displayName = displayName.replace(replaceKey, replaceValue);
            meta.setDisplayName(displayName);
            item.setItemMeta(meta);
        }
    }

}
