package me.newdavis.spigot.file;

import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ListenerFile {

    public static File file = new File("plugins/NewSystem/Listener.yml");
    public static YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

    public static void loadConfig() {
        if(file.exists()) {
            try {
                yaml.load(file);
                checkPaths();
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
        shortSavings.clear();
        longSavings.clear();
        doubleSavings.clear();
        stringList.clear();
    }

    public static void saveConfig() {
        checkPaths();
    }

    public static void checkPaths() {
        boolean pathsChanged = false;
        //Chat
        if (!isPathSet("Listener.Chat.Enabled")) {
            yaml.set("Listener.Chat.Enabled", true);
            yaml.set("Listener.Chat.Format", "{Prefix} §a{Player} §8» §7{Message}");
            yaml.set("Listener.Chat.Permission.Color", "system.chat.color");
            yaml.set("Listener.Chat.Permission.RGB", "system.chat.rgb");
            yaml.set("Listener.Chat.Mention", true);
            yaml.set("Listener.Chat.PlaySoundMentioned", true);
            yaml.set("Listener.Chat.MentionKey", "@");
            yaml.set("Listener.Chat.MentionPrefix", "§f§l@§e{Player}§7");
            pathsChanged = true;
        }

        //Server Ping Event
        if (!isPathSet("Listener.ServerPing.Enabled")) {
            yaml.set("Listener.ServerPing.Enabled", true);
            yaml.set("Listener.ServerPing.MOTD", "{Prefix} §7Netzwerk §8(§a1.8§8)\n§8» §7Dieser Server nutzt §3NewSystem§7.");
            pathsChanged = true;
        }

        //Join Event
        if (!isPathSet("Listener.Join.Enabled")) {
            yaml.set("Listener.Join.Enabled", true);
            yaml.set("Listener.Join.FirstJoin.Enabled", true);
            yaml.set("Listener.Join.Message", Collections.singletonList("{Prefix} §a{Player} §7ist nun §aonline§7!"));
            yaml.set("Listener.Join.FirstJoin.Message", Arrays.asList(
                    "{Prefix} §8§m----------§e§l First§8-§e§lJoin §8§m----------",
                    "{Prefix}",
                    "{Prefix} {Player} §7ist der §8#§e{Count} §7Spieler!",
                    "{Prefix} §7Wir heißen Dich alle willkommen!",
                    "{Prefix}",
                    "{Prefix} §8§m----------§e§l First§8-§e§lJoin §8§m----------"));
            pathsChanged = true;
        }

        //Quit Event
        if (!isPathSet("Listener.Quit.Enabled")) {
            yaml.set("Listener.Quit.Enabled", true);
            yaml.set("Listener.Quit.Message", Collections.singletonList("{Prefix} §a{Player} §7ist nun §coffline§7!"));
            pathsChanged = true;
        }

        //BlockBreak Event
        if (!isPathSet("Listener.BlockBreak.Enabled")) {
            yaml.set("Listener.BlockBreak.Enabled", true);
            yaml.set("Listener.BlockBreak.Permission", "system.build");
            yaml.set("Listener.BlockBreak.Message", Collections.singletonList("{Prefix} §cDu hast keine Rechte dazu, diesen Block ab zubauen."));
            pathsChanged = true;
        }

        //BlockPlace Event
        if (!isPathSet("Listener.BlockPlace.Enabled")) {
            yaml.set("Listener.BlockPlace.Enabled", true);
            yaml.set("Listener.BlockPlace.Permission", "system.build");
            yaml.set("Listener.BlockPlace.Message", Collections.singletonList("{Prefix} §cDu hast keine Rechte dazu, diesen Block zu platzieren!"));
            pathsChanged = true;
        }

        //Death
        if (!isPathSet("Listener.Death.Enabled")) {
            yaml.set("Listener.Death.Enabled", true);
            yaml.set("Listener.Death.Message", Collections.singletonList("{Prefix} §a{Player} §7ist gestorben."));
            yaml.set("Listener.Death.ForceRespawn", true);
            pathsChanged = true;
        }

        //DeathDrop Event
        if (!isPathSet("Listener.DeathDrop.Enabled")) {
            yaml.set("Listener.DeathDrop.Enabled", true);
            yaml.set("Listener.DeathDrop.ClearDrops", true);
            yaml.set("Listener.DeathDrop.Items.Item1.Type", "GOLD_NUGGET");
            yaml.set("Listener.DeathDrop.Items.Item1.Amount", 16);
            yaml.set("Listener.DeathDrop.Items.Item1.DisplayName", "§eGold Klumpen");
            pathsChanged = true;
        }

        //WeatherChange
        if (!isPathSet("Listener.CancelWeatherChange.Enabled")) {
            yaml.set("Listener.CancelWeatherChange.Enabled", true);
            pathsChanged = true;
        }

        //DoubleJump
        if (!isPathSet("Listener.DoubleJump.Enabled")) {
            yaml.set("Listener.DoubleJump.Enabled", true);
            double height = 1.2;
            yaml.set("Listener.DoubleJump.Height", height);
            pathsChanged = true;
        }

        //NoHunger
        if (!isPathSet("Listener.NoHunger.Enabled")) {
            yaml.set("Listener.NoHunger.Enabled", true);
            pathsChanged = true;
        }

        //NoDamage
        if (!isPathSet("Listener.NoDamage.Every.Enabled")) {
            yaml.set("Listener.NoDamage.Every.Enabled", true);
            yaml.set("Listener.NoDamage.Fall.Enabled", false);
            pathsChanged = true;
        }

        //BlockCommand
        if (!isPathSet("Listener.BlockCommand.Enabled")) {
            yaml.set("Listener.BlockCommand.Enabled", true);
            yaml.set("Listener.BlockCommand.EnabledCommands", Arrays.asList("fly", "spawn"));
            yaml.set("Listener.BlockCommand.EnabledNewSystemCommands", true);
            yaml.set("Listener.BlockCommand.Permission", "system.blockcmd");
            yaml.set("Listener.BlockCommand.Message", Collections.singletonList("{Prefix} §7Der Befehl §8'§b{Command}§8' §7existiert nicht!"));
            pathsChanged = true;
        }

        //ColorSigns
        if (!isPathSet("Listener.ColorSign.Enabled")) {
            yaml.set("Listener.ColorSign.Enabled", true);
            yaml.set("Listener.ColorSign.Permission", "system.sign.color");
            pathsChanged = true;
        }

        //FreeItemSign
        if (!isPathSet("Listener.FreeItemSign.Enabled")) {
            yaml.set("Listener.FreeItemSign.Enabled", true);
            yaml.set("Listener.FreeItemSign.Permission", "system.freeitemsign");
            yaml.set("Listener.FreeItemSign.Title", "[NewSystem]");
            yaml.set("Listener.FreeItemSign.TitleReplace", "§3§lN§b§lew§3§lS§b§lystem");
            yaml.set("Listener.FreeItemSign.DefaultAmount", 1);
            yaml.set("Listener.FreeItemSign.RightClickSign", "§aRk mit Item");
            yaml.set("Listener.FreeItemSign.NoItemInHand", "{Prefix} §cIn deiner Hand hältst du keinen Gegenstand");
            yaml.set("Listener.FreeItemSign.Color.Item", "§a");
            yaml.set("Listener.FreeItemSign.Color.Amount", "§a");
            yaml.set("Listener.FreeItemSign.ItemGivenMessageEnabled", true);
            yaml.set("Listener.FreeItemSign.ItemGivenMessage", "{Prefix} Du hast den Gegenstand {Item-Name} §7erhalten!");
            yaml.set("Listener.FreeItemSign.Inventory.Enabled", true);
            yaml.set("Listener.FreeItemSign.Inventory.Title", "{Item-Name}");
            yaml.set("Listener.FreeItemSign.Inventory.Rows", 1);
            yaml.set("Listener.FreeItemSign.Inventory.ItemSlots", "2|4|6|8");
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

    private static HashMap<String, Short> shortSavings = new HashMap<>();
    public static Short getShortPath(String path) {
        if(shortSavings.containsKey(path)) {
            return shortSavings.get(path);
        }

        if(file.exists() && isPathSet(path)) {
            shortSavings.put(path, (short) yaml.getInt(path));
            return shortSavings.get(path);
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
