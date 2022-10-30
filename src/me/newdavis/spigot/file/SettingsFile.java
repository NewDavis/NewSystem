package me.newdavis.spigot.file;

import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SettingsFile {

    public static File file = new File("plugins/NewSystem/Settings.yml");
    public static YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

    public static void loadConfig() {
        configurationSection.clear();
        string.clear();
        booleanSavings.clear();
        integer.clear();
        longSavings.clear();
        doubleSavings.clear();
        stringList.clear();

        if(file.exists()) {
            try {
                yaml.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
        saveConfig();
    }

    public static void saveConfig() {
        boolean pathsChanged = false;

        if(file.exists()) {
            if (!isPathSet("Prefix")) {
                yaml.set("Prefix", "§8» §3§lN§b§lew§3§lS§b§lystem §8┃§7");
                pathsChanged = true;
            }
            if (!isPathSet("NoPerm")) {
                yaml.set("NoPerm", "{Prefix} §cDazu hast du keine Rechte!");
                pathsChanged = true;
            }
            if (!isPathSet("Offline")) {
                yaml.set("Offline", "{Prefix} §cDieser Spieler ist offline oder existiert nicht!");
                pathsChanged = true;
            }
            if (!isPathSet("Argument")) {
                yaml.set("Argument", "{Prefix} §cBitte gebe ein gültiges Argument an!");
                pathsChanged = true;
            }
            if (!isPathSet("Error")) {
                yaml.set("Error", "{Prefix} §cEs ist ein Fehler aufgetreten! §8(§c{Error}§8)");
                pathsChanged = true;
            }
            if(!isPathSet("OnlyPlayerCanExecute")) {
                yaml.set("OnlyPlayerCanExecute", "{Prefix} §cDieser Befehl kann nur von einem Spieler ausgeführt werden!");
                pathsChanged = true;
            }
            if(!isPathSet("ConsolePrefix")) {
                yaml.set("ConsolePrefix", "§cCONSOLE");
                pathsChanged = true;
            }
            if(!isPathSet("saveIPs")) {
                yaml.set("saveIPs", true);
                pathsChanged = true;
            }
            if (!isPathSet("NewPerm")) {
                yaml.set("NewPerm", false);
                pathsChanged = true;
            }
            if (!isPathSet("RGB")) {
                yaml.set("RGB", false);
                pathsChanged = true;
            }
            if (!isPathSet("ReplacePlayerWith")) {
                yaml.set("ReplacePlayerWith", "Name");
                pathsChanged = true;
            }

            //Time Format
            if (!isPathSet("Time.Format")) {
                yaml.set("Time.Format", "HH:mm:ss");
                yaml.set("Time.OtherTime", 1000 * 60 * 60);
                pathsChanged = true;
            }
            if (!isPathSet("Date.Format")) {
                yaml.set("Date.Format", "dd.MM.yyyy HH:mm:ss");
                yaml.set("Date.OtherDate", 1000 * 60 * 60);
                pathsChanged = true;
            }

            //MySQL Database
            if (!isPathSet("MySQL.Enabled")) {
                yaml.set("MySQL.Enabled", false);
                yaml.set("MySQL.useSSL", false);
                yaml.set("MySQL.host", "host");
                yaml.set("MySQL.port", 3306);
                yaml.set("MySQL.database", "");
                yaml.set("MySQL.user", "user");
                yaml.set("MySQL.password", "password");
                pathsChanged = true;
            }
        }else{
            yaml.set("Prefix", "§8» §3§lN§b§lew§3§lS§b§lystem §8┃§7");
            yaml.set("NoPerm", "{Prefix} §cDazu hast du keine Rechte!");
            yaml.set("Offline", "{Prefix} §cDieser Spieler ist offline oder existiert nicht!");
            yaml.set("Argument", "{Prefix} §cBitte gebe ein gültiges Argument an!");
            yaml.set("NoArgumentRequired", "{Prefix} §cEs wird kein Argument benötigt!");
            yaml.set("Error", "{Prefix} §cEs ist ein Fehler aufgetreten! §8(§c{Error}§8)");
            yaml.set("OnlyPlayerCanExecute", "{Prefix} §cDieser Befehl kann nur von einem Spieler ausgeführt werden!");
            yaml.set("ConsolePrefix", "§cCONSOLE");
            yaml.set("saveIPs", true);
            yaml.set("NewPerm", false);
            yaml.set("RGB", false);
            yaml.set("ReplacePlayerWith", "Name");

            //Time Format
            yaml.set("Time.Format", "HH:mm:ss");
            yaml.set("Time.TimeZone", "GMT+2");
            yaml.set("Date.Format", "dd.MM.yyyy HH:mm:ss");
            yaml.set("Date.TimeZone", "GMT+2");

            //MySQL
            yaml.set("MySQL.Enabled", false);
            yaml.set("MySQL.useSSL", false);
            yaml.set("MySQL.host", "localhost");
            yaml.set("MySQL.port", 3306);
            yaml.set("MySQL.database", "database");
            yaml.set("MySQL.user", "root");
            yaml.set("MySQL.password", "");
            pathsChanged = true;
        }

        if(!pathsChanged) {
            pathsChanged = checkForNewPaths();
        }

        if(pathsChanged) {
            try {
                yaml.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean checkForNewPaths() {
        return false;
    }

    public static String getPrefix() {
        if(file.exists()) {
            if(isPathSet("Prefix")) {
                return getStringPath("Prefix");
            }
        }else{
            saveConfig();
        }
        return "§cDer Pfad §c§n'Prefix'§c wurde nicht gefunden!";
    }

    public static String getNoPerm() {
        if(file.exists()) {
            if(isPathSet("Prefix") && isPathSet("NoPerm")) {
                String message = getStringPath("NoPerm");
                return message.replace("{Prefix}", getPrefix());
            }
        }else{
            saveConfig();
        }
        return "§cDer Pfad §c§n'NoPerm'§c wurde nicht gefunden!";
    }

    public static String getOffline() {
        if(file.exists()) {
            if(isPathSet("Prefix") && isPathSet("Offline")) {
                String message = getStringPath("Offline");
                return message.replace("{Prefix}", getPrefix());
            }
        }else{
            saveConfig();
        }
        return "§cDer Pfad §c§n'Offline'§c wurde nicht gefunden!";
    }

    public static String getArgument() {
        if(file.exists()) {
            if(isPathSet("Prefix") && isPathSet("Argument")) {
                String message = getStringPath("Argument");
                return message.replace("{Prefix}", getPrefix());
            }
        }else{
            saveConfig();
        }
        return "§cDer Pfad §c§n'Argument'§c wurde nicht gefunden!";
    }

    public static String getError() {
        if(file.exists()) {
            if(isPathSet("Prefix") && isPathSet("Error")) {
                String message = getStringPath("Error");
                return message.replace("{Prefix}", getPrefix());
            }
        }else{
            saveConfig();
        }
        return "§cDer Pfad §c§n'Error'§c wurde nicht gefunden!";
    }

    public static String getOnlyPlayerCanExecute() {
        if(file.exists()) {
            if(isPathSet("Prefix") && isPathSet("OnlyPlayerCanExecute")) {
                String message = getStringPath("OnlyPlayerCanExecute");
                return message.replace("{Prefix}", getPrefix());
            }
        }else{
            saveConfig();
        }
        return "§cDer Pfad §c§n'OnlyPlayerCanExecute'§c wurde nicht gefunden!";
    }

    public static String getConsolePrefix() {
        if(file.exists()) {
            if(isPathSet("Prefix") && isPathSet("ConsolePrefix")) {
                String message = getStringPath("ConsolePrefix");
                return message.replace("{Prefix}", getPrefix());
            }
        }else{
            saveConfig();
        }
        return "§cDer Pfad §c§n'ConsolePrefix'§c wurde nicht gefunden!";
    }

    public static boolean getSaveIPs() {
        if(file.exists()) {
            if(isPathSet("saveIPs")) {
                return getBooleanPath("saveIPs");
            }
        }else{
            saveConfig();
        }
        return false;
    }

    public static boolean getNewPermActivated() {
        if(NewSystem.enabled) {
            return NewSystem.newPerm;
        }

        if(file.exists()) {
            if(Bukkit.getPluginManager().isPluginEnabled("NewPerm")) {
                if (isPathSet("NewPerm")) {
                    return getBooleanPath("NewPerm");
                }
            }else{
                setPath("NewPerm", false);
                return false;
            }
        }else{
            saveConfig();
        }
        return false;
    }

    public static boolean getRGBActivated() {
        if(file.exists()) {
            if (isPathSet("RGB")) {
                return getBooleanPath("RGB");
            }
        }else{
            saveConfig();
        }
        return false;
    }

    public static String getPlayerReplace() {
        if(file.exists()) {
            if(isPathSet("ReplacePlayerWith")) {
                return getStringPath("ReplacePlayerWith");
            }
        }else{
            saveConfig();
        }
        return "Name";
    }

    public static String TimeFormat(long millis) {
        if(isPathSet("Time.Format")) {
            SimpleDateFormat sdf = new SimpleDateFormat(getStringPath("Time.Format"));
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
            if(isPathSet("Time.TimeZone")) {
                sdf.setTimeZone(TimeZone.getTimeZone(getStringPath("Time.TimeZone")));
                return sdf.format(millis + getLongPath("Time.TimeZone"));
            }else{
                return sdf.format(millis);
            }
        }else{
            yaml.set("Time.Format", "HH:mm:ss");
            yaml.set("Time.TimeZone", "GMT+2");
            saveConfig();
            SimpleDateFormat sdf = new SimpleDateFormat(getStringPath("HH:mm:ss"));
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
            return sdf.format(millis);
        }
    }

    public static String DateFormat(long millis) {
        if(isPathSet("Date.Format")) {
            SimpleDateFormat sdf = new SimpleDateFormat(getStringPath("Date.Format"));
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
            if(isPathSet("Date.TimeZone")) {
                sdf.setTimeZone(TimeZone.getTimeZone(getStringPath("Date.TimeZone")));
                return sdf.format(millis + getLongPath("Date.TimeZone"));
            }else{
                return sdf.format(millis);
            }
        }else{
            yaml.set("Date.Format", "dd.MM.yyyy HH:mm:ss");
            yaml.set("Date.TimeZone", "GMT+2");
            saveConfig();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
            return sdf.format(millis);
        }
    }

    public static boolean getMySQLEnabled() {
        return yaml.getBoolean("MySQL.Enabled");
    }
    public static boolean getMySQLUseSSL() {
        return yaml.getBoolean("MySQL.useSSL");
    }

    public static String getMySQLHost() {
        return yaml.getString("MySQL.host");
    }

    public static int getMySQLPort() {
        return yaml.getInt("MySQL.port");
    }

    public static String getMySQLDatabase() {
        return yaml.getString("MySQL.database");
    }

    public static String getMySQLUser() {
        return yaml.getString("MySQL.user");
    }

    public static String getMySQLPassword() {
        return yaml.getString("MySQL.password");
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
