package me.newdavis.spigot.plugin.newsystem.inventory.kit;

import me.newdavis.spigot.file.KitFile;
import me.newdavis.spigot.file.SettingsFile;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ChangeValueKit {

    public static HashMap<Player, Integer> indexHM = new HashMap<>();
    public static HashMap<Player, String> pathHM = new HashMap<>();

    public static boolean isList(Player p) {
        String kit = KitChoosedInventory.kit.get(p);
        String path = "Kit." + kit + "." + pathHM.get(p);
        if(String.valueOf(KitFile.yaml.get(path)).contains("[") || String.valueOf(KitFile.yaml.get(path)).contains("]")) {
            return true;
        }
        return false;
    }

    public static boolean setPath(Player p, String value) {
        String kit = KitChoosedInventory.kit.get(p);
        String path = "Kit." + kit + "." + pathHM.get(p);

        if(KitFile.isPathSet(path)) {
            if (value.split("")[0].equals("'") && value.split("")[value.split("").length - 1].equals("'")) {
                KitFile.yaml.set(path, value.replace("'", ""));
            } else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("true")) {
                KitFile.yaml.set(path, Boolean.parseBoolean(value));
            } else {
                try {
                    if (value.contains(".")) {
                        KitFile.yaml.set(path, Double.parseDouble(value));
                    } else {
                        KitFile.yaml.set(path, Integer.parseInt(value));
                    }
                } catch (NumberFormatException e) {
                    KitFile.yaml.set(path, value);
                }
            }
            try {
                KitFile.yaml.save(KitFile.file);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            p.sendMessage(SettingsFile.getPrefix() + " §cThis path does not exist!");
        }
        return false;
    }

    public static boolean setList(Player p, String value) {
        int index = indexHM.get(p);
        String kit = KitChoosedInventory.kit.get(p);
        String path = "Kit." + kit + "." + pathHM.get(p);

        if(KitFile.isPathSet(path)) {
            List<String> list = KitFile.getStringListPath(path);
            if (list.size() > index) {
                list.set(index, value);
            } else {
                list.add(value);
            }
            KitFile.yaml.set(path, list);
            try {
                KitFile.yaml.save(KitFile.file);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            p.sendMessage(SettingsFile.getPrefix() + " §cThis path does not exist!");
        }
        return false;
    }

    public static boolean removeIndex(Player p) {
        int index = indexHM.get(p);
        String kit = KitChoosedInventory.kit.get(p);
        String path = "Kit." + kit + "." + pathHM.get(p);

        if(KitFile.isPathSet(path)) {
            List<String> list = KitFile.getStringListPath(path);
            list.remove(index);
            KitFile.yaml.set(path, list);
            try {
                KitFile.yaml.save(KitFile.file);
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
            if(!msg.equalsIgnoreCase("cancel")) {
                if(isList(p)) {
                    if(indexHM.containsKey(p)) {
                        if(msg.equalsIgnoreCase("delete")) {
                            removeIndex(p);
                            pathHM.remove(p);
                            indexHM.remove(p);
                            p.sendMessage(SettingsFile.getPrefix() + " §7The index got §cremoved §7of the list.");
                            new KitChoosedInventory().openInventoryPage(p, KitChoosedInventory.kit.get(p), KitChoosedInventory.page.get(p));
                        }else{
                            String value = msg.replace("&", "§");
                            setList(p, value);
                            pathHM.remove(p);
                            indexHM.remove(p);
                            p.sendMessage(SettingsFile.getPrefix() + " §7The index of the list was changed §asuccessfully§7!");
                            new KitChoosedInventory().openInventoryPage(p, KitChoosedInventory.kit.get(p), KitChoosedInventory.page.get(p));
                        }
                    }else{
                        try{
                            int index = Integer.parseInt(msg);
                            indexHM.put(p, index);
                            p.sendMessage(SettingsFile.getPrefix() + " §7You have choosed the index §a" + index + "§7!");
                            p.sendMessage(SettingsFile.getPrefix() + " §7Please insert a new value for the index §a" + index + "§7!");
                            p.sendMessage(SettingsFile.getPrefix() + " §7To delete the index, write §cdelete§7!");
                            p.sendMessage(SettingsFile.getPrefix() + " §7To cancel this process, write §ccancel§7!");
                        }catch (NumberFormatException ignored) {}
                    }
                }else if(msg.equalsIgnoreCase("delete")) {
                    String value = "";
                    setPath(p, value);
                    pathHM.remove(p);
                    p.sendMessage(SettingsFile.getPrefix() + " §7The value of the path got §cdeleted§7!");
                    if(KitChoosedInventory.kit.containsKey(p)) {
                        new KitChoosedInventory().openInventoryPage(p, KitChoosedInventory.kit.get(p), KitChoosedInventory.page.get(p));
                    }else{
                        new KitFileInventory().openInventoryPage(p, KitFileInventory.page.get(p));
                    }
                }else{
                    String value = msg.replace("&", "§");
                    setPath(p, value);
                    pathHM.remove(p);
                    p.sendMessage(SettingsFile.getPrefix() + " §7The path was changed §asuccessfully§7.");
                    new KitChoosedInventory().openInventoryPage(p, KitChoosedInventory.kit.get(p), KitChoosedInventory.page.get(p));
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
