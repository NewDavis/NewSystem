package me.newdavis.spigot.file;
//Plugin by NewDavis

import me.newdavis.spigot.util.TabListPrefix;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class TabListFile {

    public static File file = new File("plugins/NewSystem/TabList.yml");
    public static YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

    public static void loadConfig() {
        if(file.exists()) {
            try {
                yaml.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }else{
            saveConfig();
        }

        configurationSection.clear();
        string.clear();
        booleanSavings.clear();
        integer.clear();
        longSavings.clear();
        doubleSavings.clear();
        stringList.clear();
    }

    public static void saveConfig() {
        if(file.exists()) {
            try {
                yaml.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            if(!isPathSet("TabList.Enabled")) {
                yaml.set("TabList.Enabled", true);
                yaml.set("TabList.RoleNotFound", "{Prefix} §cDeine Rolle wurde nicht gefunden!");
                yaml.set("TabList.Administrator.prefix", "§4Admin §8┃ §4");
                yaml.set("TabList.Administrator.suffix", "§4");
                yaml.set("TabList.Administrator.priority", "001");
                yaml.set("TabList.Administrator.permission", "system.tablist.administrator");
                yaml.set("TabList.Administrator.vanishSuffix", " §8(§dV§8)");
                yaml.set("TabList.Administrator.vanishPriority", "000");
                yaml.set("TabList.VIP.prefix", "§5VIP §8┃ §5");
                yaml.set("TabList.VIP.suffix", "§5");
                yaml.set("TabList.VIP.priority", "005");
                yaml.set("TabList.VIP.permission", "system.tablist.vip");
                yaml.set("TabList.VIP.vanishSuffix", " §8(§dV§8)");
                yaml.set("TabList.VIP.vanishPriority", "000");
                yaml.set("TabList.Spieler.prefix", "§7");
                yaml.set("TabList.Spieler.suffix", "§7");
                yaml.set("TabList.Spieler.priority", "999");
                yaml.set("TabList.Spieler.permission", "system.tablist.spieler");
                yaml.set("TabList.Spieler.vanishSuffix", " §8(§dV§8)");
                yaml.set("TabList.Spieler.vanishPriority", "000");
            }

            try {
                yaml.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getPrefix(String role) {
        if(isPathSet("TabList." + role + ".prefix")) {
            return getStringPath("TabList." + role + ".prefix");
        }
        return role;
    }

    public static String getSuffix(String role) {
        if(isPathSet("TabList." + role + ".suffix")) {
            return getStringPath("TabList." + role + ".suffix");
        }
        return role;
    }

    public static String getPriority(String role) {
        if(isPathSet("TabList." + role + ".priority")) {
            return getStringPath("TabList." + role + ".priority");
        }
        return "0";
    }

    public static String getPermission(String role) {
        if(isPathSet("TabList." + role + ".permission")) {
            return getStringPath("TabList." + role + ".permission");
        }
        return role;
    }

    public static String getRoleByPermission(Player p) {
        for(String role : TabListPrefix.getRoles()) {
            String perm = getPermission(role);
            if(NewSystem.hasPermission(p, perm)) {
                return role;
            }
        }
        return "";
    }

    public static String getVanishSuffix(String role) {
        if(isPathSet("TabList." + role + ".vanishSuffix")) {
            return getStringPath("TabList." + role + ".vanishSuffix");
        }
        return role;
    }

    public static String getVanishPriority(String role) {
        if(isPathSet("TabList." + role + ".vanishPriority")) {
            return getStringPath("TabList." + role + ".vanishPriority");
        }
        return "0";
    }

    private static HashMap<String, List<String>> configurationSection = new HashMap<>();
    public static List<String> getConfigurationSection(String path) {
        if(configurationSection.containsKey(path)) {
            return configurationSection.get(path);
        }

        List<String> list = new ArrayList<>();
        if(isPathSet(path)) {
            Set<String> keys = yaml.getConfigurationSection(path).getKeys(false);
            for(String key : keys) {
                list.add(key);
            }

            configurationSection.put(path, list);
        }
        return list;
    }

    public static void setPath(String path, Object wert) {
        yaml.set(path, wert);
        saveConfig();
    }

    private static HashMap<String, String> string = new HashMap<>();
    public static String getStringPath(String path) {
        if(string.containsKey(path)) {
            return string.get(path);
        }

        if(file.exists() && isPathSet(path)) {
            string.put(path, NewSystem.replace(yaml.getString(path)));
            return string.get(path);
        }else{
            return "";
        }
    }

    private static HashMap<String, Boolean> booleanSavings = new HashMap<>();
    public static boolean getBooleanPath(String path) {
        if(booleanSavings.containsKey(path)) {
            return booleanSavings.get(path);
        }

        if(file.exists() && isPathSet(path)) {
            booleanSavings.put(path, yaml.getBoolean(path));
            return booleanSavings.get(path);
        }
        return false;
    }

    private static HashMap<String, Integer> integer = new HashMap<>();
    public static Integer getIntegerPath(String path) {
        if(integer.containsKey(path)) {
            return integer.get(path);
        }

        if(file.exists() && isPathSet(path)) {
            integer.put(path, yaml.getInt(path));
            return integer.get(path);
        }
        return 0;
    }

    private static HashMap<String, Long> longSavings = new HashMap<>();
    public static Long getLongPath(String path) {
        if(longSavings.containsKey(path)) {
            return longSavings.get(path);
        }

        if(file.exists() && isPathSet(path)) {
            longSavings.put(path, yaml.getLong(path));
            return longSavings.get(path);
        }
        return 0L;
    }

    private static HashMap<String, Double> doubleSavings = new HashMap<>();
    public static Double getDoublePath(String path) {
        if(doubleSavings.containsKey(path)) {
            return doubleSavings.get(path);
        }

        if(file.exists() && isPathSet(path)) {
            doubleSavings.put(path, yaml.getDouble(path));
            return doubleSavings.get(path);
        }
        return 0D;
    }

    private static HashMap<String, List<String>> stringList = new HashMap<>();
    public static List<String> getStringListPath(String path) {
        if(stringList.containsKey(path)) {
            return stringList.get(path);
        }

        if(file.exists() && isPathSet(path)) {
            List<String> list = yaml.getStringList(path);
            list.replaceAll(NewSystem::replace);
            stringList.put(path, list);
            return list;
        }
        return new ArrayList<>();
    }

    public static boolean isPathSet(String path) {
        if(file.exists()) {
            return yaml.contains(path);
        }else{
            try {
                if(file.createNewFile()) {
                    saveConfig();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
