package me.newdavis.spigot.plugin.newsystem.inventory.listener;

import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.file.ListenerFile;
import me.newdavis.spigot.plugin.NewSystem;
import me.newdavis.spigot.plugin.newsystem.inventory.other.OtherChoosedInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ChangeValueListener {

    public static HashMap<Player, Integer> indexHM = new HashMap<>();
    public static HashMap<Player, String> pathHM = new HashMap<>();

    public static boolean isList(Player p) {
        String event = ListenerChoosedInventory.listener.get(p);
        String path = "Listener." + event + "." + pathHM.get(p);
        if(String.valueOf(ListenerFile.yaml.get(path)).contains("[") || String.valueOf(ListenerFile.yaml.get(path)).contains("]")) {
            return true;
        }
        return false;
    }

    public static boolean setPath(Player p, String value) {
        String event = ListenerChoosedInventory.listener.get(p);
        String path = "Listener." + event + "." + pathHM.get(p);

        if(ListenerFile.isPathSet(path)) {
            if (value.split("")[0].equals("'") && value.split("")[value.split("").length - 1].equals("'")) {
                ListenerFile.yaml.set(path, value.replace("'", ""));
            } else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("true")) {
                ListenerFile.yaml.set(path, Boolean.parseBoolean(value));
            } else {
                try {
                    if (value.contains(".")) {
                        ListenerFile.yaml.set(path, Double.parseDouble(value));
                    } else {
                        ListenerFile.yaml.set(path, Integer.parseInt(value));
                    }
                } catch (NumberFormatException e) {
                    ListenerFile.yaml.set(path, value);
                }
            }
            try {
                ListenerFile.yaml.save(ListenerFile.file);
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
        String event = ListenerChoosedInventory.listener.get(p);
        String path = "Listener." + event + "." + pathHM.get(p);

        if(ListenerFile.isPathSet(path)) {
            List<String> list = ListenerFile.getStringListPath(path);
            if (list.size() > index && index >= 0) {
                list.set(index, value);
            } else {
                list.add(value);
            }
            ListenerFile.yaml.set(path, list);
            try {
                ListenerFile.yaml.save(ListenerFile.file);
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
        String event = ListenerChoosedInventory.listener.get(p);
        String path = "Listener." + event + "." + pathHM.get(p);

        if(ListenerFile.isPathSet(path)) {
            List<String> list = ListenerFile.getStringListPath(path);
            list.remove(index);
            ListenerFile.yaml.set(path, list);
            try {
                ListenerFile.yaml.save(ListenerFile.file);
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
                            Bukkit.getScheduler().runTask(NewSystem.getInstance(), new Runnable() {
                                @Override
                                public void run() {
                                    new ListenerChoosedInventory().openInventoryPage(p, ListenerChoosedInventory.listener.get(p), ListenerChoosedInventory.page.get(p));
                                }
                            });
                        }else{
                            String value = msg.replace("&", "§");
                            setList(p, value);
                            pathHM.remove(p);
                            indexHM.remove(p);
                            p.sendMessage(SettingsFile.getPrefix() + " §7The index of the list was changed §asuccessfully§7!");
                            Bukkit.getScheduler().runTask(NewSystem.getInstance(), new Runnable() {
                                @Override
                                public void run() {
                                    new ListenerChoosedInventory().openInventoryPage(p, ListenerChoosedInventory.listener.get(p), ListenerChoosedInventory.page.get(p));
                                }
                            });
                        }
                    }else{
                        try{
                            int index = Integer.parseInt(msg);
                            indexHM.put(p, index);
                            p.sendMessage(SettingsFile.getPrefix() + " §7You have choosed the index §a" + index + "§7!");
                            p.sendMessage(SettingsFile.getPrefix() + " §7Please insert a new value for the index §a" + index + "§7!");
                            p.sendMessage(SettingsFile.getPrefix() + " §7To delete the index, write §cdelete§7!");
                            p.sendMessage(SettingsFile.getPrefix() + " §7To cancel this process, write §ccancel§7!");
                        }catch (NumberFormatException ignored) {
                            p.sendMessage(SettingsFile.getPrefix() + " §cPlease insert a number to choose the index!");
                        }
                    }
                }else if(msg.equalsIgnoreCase("delete")) {
                    String value = "";
                    setPath(p, value);
                    pathHM.remove(p);
                    p.sendMessage(SettingsFile.getPrefix() + " §7The value of the path got §cdeleted§7!");
                    if(ListenerChoosedInventory.listener.containsKey(p)) {
                        Bukkit.getScheduler().runTask(NewSystem.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                new ListenerChoosedInventory().openInventoryPage(p, ListenerChoosedInventory.listener.get(p), ListenerChoosedInventory.page.get(p));
                            }
                        });
                    }else{
                        Bukkit.getScheduler().runTask(NewSystem.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                new ListenerFileInventory().openInventoryPage(p, ListenerFileInventory.page.get(p));
                            }
                        });
                    }
                }else{
                    String value = msg.replace("&", "§");
                    setPath(p, value);
                    pathHM.remove(p);
                    p.sendMessage(SettingsFile.getPrefix() + " §7The path was changed §asuccessfully§7.");
                    Bukkit.getScheduler().runTask(NewSystem.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            new ListenerChoosedInventory().openInventoryPage(p, ListenerChoosedInventory.listener.get(p), ListenerChoosedInventory.page.get(p));
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
