package me.newdavis.spigot.file;

import me.newdavis.spigot.plugin.NewSystem;
import me.newdavis.spigot.util.placeholder.PlaceholderManager;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SavingsFile {

    private static File file = new File("plugins/NewSystem/Savings.yml");
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
    }

    public static void saveConfig() {
        try {
            yaml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getSavedIPs() {
        return getConfigurationSection("IP.User");
    }

    public static List<String> getConfigurationSection(String path) {
        List<String> list = new ArrayList<>();
        if(isPathSet(path)) {
            Set<String> keys = yaml.getConfigurationSection(path).getKeys(false);
            for(String key : keys) {
                list.add(key);
            }
        }
        return list;
    }

    public static void setPath(String path, Object wert) {
        yaml.set(path, wert);
        saveConfig();
    }

    public static String getStringPath(String path) {
        if(file.exists() && isPathSet(path)) {
            return yaml.getString(path);
        }else{
            return "";
        }
    }

    public static boolean getBooleanPath(String path) {
        if(file.exists() && isPathSet(path)) {
            return yaml.getBoolean(path);
        }
        return false;
    }

    public static Integer getIntegerPath(String path) {
        if(file.exists() && isPathSet(path)) {
            return yaml.getInt(path);
        }
        return 0;
    }

    public static Short getShortPath(String path) {
        if(file.exists() && isPathSet(path)) {
            return (short) yaml.getInt(path);
        }
        return 0;
    }

    public static Long getLongPath(String path) {
        if(file.exists() && isPathSet(path)) {
            return yaml.getLong(path);
        }
        return 0L;
    }

    public static Double getDoublePath(String path) {
        if(file.exists() && isPathSet(path)) {
            return yaml.getDouble(path);
        }
        return 0D;
    }

    public static List<String> getStringListPath(String path) {
        if(file.exists() && isPathSet(path)) {
            return yaml.getStringList(path);
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

    public static ItemStack getItemStack(String path) {
        if(file.exists()) {
            if(isPathSet(path)) {
                return yaml.getItemStack(path);
            }
        }
        return new ItemStack(Material.AIR);
    }

}
