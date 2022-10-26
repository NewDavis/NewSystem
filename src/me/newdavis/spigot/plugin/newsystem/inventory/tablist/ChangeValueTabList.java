package me.newdavis.spigot.plugin.newsystem.inventory.tablist;

import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.file.TabListFile;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;

public class ChangeValueTabList {

    public static HashMap<Player, String> pathHM = new HashMap<>();

    public static boolean setPath(Player p, String value) {
        String path = "TabList." + pathHM.get(p);
        if(TabListChoosedInventory.tablist.containsKey(p)) {
            String tablist = TabListChoosedInventory.tablist.get(p);
            path = "TabList." + tablist + "." + pathHM.get(p);
        }

        if(TabListFile.isPathSet(path)) {
            if (value.split("")[0].equals("'") && value.split("")[value.split("").length - 1].equals("'")) {
                TabListFile.yaml.set(path, value.replace("'", ""));
            } else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("true")) {
                TabListFile.yaml.set(path, Boolean.parseBoolean(value));
            } else {
                try {
                    if (value.contains(".")) {
                        TabListFile.yaml.set(path, Double.parseDouble(value));
                    } else {
                        TabListFile.yaml.set(path, Integer.parseInt(value));
                    }
                } catch (NumberFormatException e) {
                    TabListFile.yaml.set(path, value);
                }
            }
            try {
                TabListFile.yaml.save(TabListFile.file);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            p.sendMessage(SettingsFile.getPrefix() + " §cDieser Pfad existiert nicht!");
        }
        return false;
    }

    public static boolean chat(Player p, String msg) {
        if(pathHM.containsKey(p)) {
            if(msg.equalsIgnoreCase("delete")) {
                String value = "";
                setPath(p, value);
                pathHM.remove(p);
                p.sendMessage(SettingsFile.getPrefix() + " §7The value of the path got §cdeleted§7!");
                if(TabListChoosedInventory.tablist.containsKey(p)) {
                    new TabListChoosedInventory().openInventoryPage(p, TabListChoosedInventory.tablist.get(p), TabListChoosedInventory.page.get(p));
                }else{
                    new TabListFileInventory().openInventoryPage(p, TabListFileInventory.page.get(p));
                }
            }else if(!msg.equalsIgnoreCase("cancel")) {
                String value = msg.replace("&", "§");
                setPath(p, value);
                pathHM.remove(p);
                p.sendMessage(SettingsFile.getPrefix() + " §7The path was changed §asuccessfully§7.");
                if(TabListChoosedInventory.tablist.containsKey(p)) {
                    new TabListChoosedInventory().openInventoryPage(p, TabListChoosedInventory.tablist.get(p), TabListChoosedInventory.page.get(p));
                }else{
                    new TabListFileInventory().openInventoryPage(p, TabListFileInventory.page.get(p));
                }
            }else{
                pathHM.remove(p);
                p.sendMessage(SettingsFile.getPrefix() + " §cThe process had been canceled!");
            }
            return true;
        }
        return false;
    }

}
