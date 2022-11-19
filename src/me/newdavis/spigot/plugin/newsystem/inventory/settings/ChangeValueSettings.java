package me.newdavis.spigot.plugin.newsystem.inventory.settings;

import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import me.newdavis.spigot.plugin.newsystem.inventory.other.OtherChoosedInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;

public class ChangeValueSettings {

    public static HashMap<Player, String> pathHM = new HashMap<>();

    public static boolean setPath(Player p, String value) {
        String path = pathHM.get(p);

        if(SettingsFile.isPathSet(path)) {
            if (value.split("")[0].equals("'") && value.split("")[value.split("").length - 1].equals("'")) {
                SettingsFile.yaml.set(path, value.replace("'", ""));
            } else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("true")) {
                SettingsFile.yaml.set(path, Boolean.parseBoolean(value));
            } else {
                try {
                    if (value.contains(".")) {
                        SettingsFile.yaml.set(path, Double.parseDouble(value));
                    } else {
                        SettingsFile.yaml.set(path, Integer.parseInt(value));
                    }
                } catch (NumberFormatException e) {
                    SettingsFile.yaml.set(path, value);
                }
            }
            try {
                SettingsFile.yaml.save(SettingsFile.file);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            p.sendMessage(SettingsFile.getPrefix() + " §cThis path does not exist!");
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
                if(SettingChoosedInventory.setting.containsKey(p)) {
                    Bukkit.getScheduler().runTask(NewSystem.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            new SettingChoosedInventory().openInventoryPage(p, SettingChoosedInventory.setting.get(p), SettingChoosedInventory.page.get(p));
                        }
                    });
                }else{
                    Bukkit.getScheduler().runTask(NewSystem.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            new SettingsFileInventory().openInventoryPage(p, SettingsFileInventory.page.get(p));
                        }
                    });
                }
            }else if(!msg.equalsIgnoreCase("cancel")) {
                String value = msg.replace("&", "§");
                setPath(p, value);
                pathHM.remove(p);
                p.sendMessage(SettingsFile.getPrefix() + " §7The path was changed §asuccessfully§7.");
                if(SettingChoosedInventory.setting.containsKey(p)) {
                    Bukkit.getScheduler().runTask(NewSystem.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            new SettingChoosedInventory().openInventoryPage(p, SettingChoosedInventory.setting.get(p), SettingChoosedInventory.page.get(p));
                        }
                    });
                }else{
                    Bukkit.getScheduler().runTask(NewSystem.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            new SettingsFileInventory().openInventoryPage(p, SettingsFileInventory.page.get(p));
                        }
                    });
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
