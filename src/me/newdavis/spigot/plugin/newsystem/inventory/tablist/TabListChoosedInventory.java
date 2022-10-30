package me.newdavis.spigot.plugin.newsystem.inventory.tablist;

import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.file.TabListFile;
import me.newdavis.spigot.plugin.newsystem.listener.Listeners;
import me.newdavis.spigot.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class TabListChoosedInventory {

    public static HashMap<Player, Integer> page = new HashMap<>();
    public static HashMap<Player, String> tablist = new HashMap<>();

    public final String TITLE = "§8» §fTabList ";
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

    public void openInventoryPage(Player p, String inventoryTabList, int inventoryPage) {
        if(!(inventoryTabList.equalsIgnoreCase("Enabled") || inventoryTabList.equalsIgnoreCase("RoleNotFound"))) {
            Inventory inventory = Bukkit.createInventory(null, 9 * rows, TITLE);

            List<String> paths = getPaths(inventoryTabList);
            int pathsAmount = paths.size();
            int maxPages = pathsAmount / 28;
            pathsAmount -= maxPages*28;
            if(pathsAmount > 0) {
                maxPages++;
            }
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

            if (inventoryPage > 0 && inventoryPage <= maxPages) {
                if (inventoryPage == 1) {
                    for (String path : paths) {
                        if (inventory.getItem(43) == null || inventory.getItem(43).getType() == Material.AIR) {
                            Object value = TabListFile.yaml.get("TabList." + inventoryTabList + "." + path);
                            if (!String.valueOf(value).contains("MemorySection")) {
                                List<String> lore = new ArrayList<>();
                                lore.add("§7Derzeitiger Wert: §7");
                                lore.add("§7" + String.valueOf(value).replace("[", "").replace("]", "").replace(", ", "§8'§7 §8'§7"));
                                ItemStack item = new ItemBuilder(Material.PAPER)
                                        .setName("§7" + path)
                                        .setLore(lore)
                                        .build();
                                inventory.addItem(item);
                            }
                        }
                    }
                } else {
                    for (int i = (inventoryPage - 1) * 28; i < (inventoryPage - 1) * 28 + 29; i++) {
                        if (inventory.getItem(43) == null || inventory.getItem(43).getType() == Material.AIR) {
                            if (paths.size() > i) {
                                Object value = TabListFile.yaml.get("TabList." + inventoryTabList + "." + paths.get(i));
                                if (!String.valueOf(value).contains("MemorySection")) {
                                    List<String> lore = new ArrayList<>();
                                    lore.add("§7Derzeitiger Wert: §7");
                                    lore.add("§7" + String.valueOf(value).replace("[", "").replace("]", "").replace(", ", "§8'§7 §8'§7"));
                                    ItemStack item = new ItemBuilder(Material.PAPER)
                                            .setName("§7" + paths.get(i))
                                            .setLore(lore)
                                            .build();
                                    inventory.addItem(item);
                                }
                            }
                        }
                    }
                }
            } else {
                p.sendMessage(SettingsFile.getPrefix() + " §cThis page does not exist!");
                return;
            }

            p.openInventory(inventory);
            page.put(p, inventoryPage);
            tablist.put(p, inventoryTabList);
        }else{
            new TabListFileInventory().openInventoryPage(p, TabListFileInventory.page.get(p));
        }
    }

    private List<String> getPaths(String tablist) {
        if(TabListFile.isPathSet("TabList." + tablist)) {
            Set<String> keys = TabListFile.yaml.getConfigurationSection("TabList." + tablist).getKeys(true);
            List<String> list = new ArrayList<>();
            for (String key : keys) {
                list.add(key);
            }
            return list;
        }
        return new ArrayList<>();
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
