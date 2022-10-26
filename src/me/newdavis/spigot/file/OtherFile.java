package me.newdavis.spigot.file;

import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class OtherFile {

    public static File file = new File("plugins/NewSystem/Other.yml");
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
        if (!isPathSet("Other.TabList.Enabled")) {
            yaml.set("Other.TabList.Enabled", true);
            yaml.set("Other.TabList.UpdateSpeed", 10);
            yaml.set("Other.TabList." + 1 + ".Header", "{Prefix} §7Dein NewSystem Test-Server!||||§7Uhrzeit §8» §a{Time}||§7Online §8» §a{Online}§8/§a{MaxPlayers}||§7Ping §8» §a{Ping}ms||");
            yaml.set("Other.TabList." + 1 + ".Footer", "||§7Discord §8» §9seMwEpjUkD||§7TeamSpeak §8» §9NewDavis.me||§7Twitter §8» §b@_NewDavis_");
            pathsChanged = true;
        }

        //Scoreboard
        if (!isPathSet("Other.ScoreBoard.Enabled")) {
            List<String> scoreBoardScores = Arrays.asList("§1", "§7Dein Profil", "§8» §a{Name}", "§2", "§7Unser TeamSpeak", "§8» §3NewDavis.me");
            yaml.set("Other.ScoreBoard.Enabled", true);
            yaml.set("Other.ScoreBoard.UpdateSpeed", 10);
            yaml.set("Other.ScoreBoard.Title", Arrays.asList("§b§lNewSystem", "§3§lN§b§lewSystem", "§b§lN§3§le§b§lwSystem", "§b§lNe§3§lw§b§lSystem", "§b§lNew§3§lS§b§lystem", "§b§lNewS§3§ly§b§lstem",
                    "§b§lNewSy§3§ls§b§ltem", "§b§lNewSys§3§lt§b§lem", "§b§lNewSyst§3§le§b§lm", "§b§lNewSyste§3§lm"));
            yaml.set("Other.ScoreBoard.Scores", scoreBoardScores);
            pathsChanged = true;
        }

        //AutoBroadcast
        if (!isPathSet("Other.AutoBroadcast.Enabled")) {
            yaml.set("Other.AutoBroadcast.Enabled", true);
            yaml.set("Other.AutoBroadcast.MessageDelayInMinutes", 5);
            yaml.set("Other.AutoBroadcast.Message1", Arrays.asList("", "{Prefix} §7Unser Discord Server:", "{Prefix} §9https://discord.gg/seMwEpjUkD", ""));
            pathsChanged = true;
        }

        //CustomRecipes
        if (!isPathSet("Other.CustomRecipes.Enabled")) {
            yaml.set("Other.CustomRecipes.Enabled", true);
            yaml.set("Other.CustomRecipes.OPSword.IgnoreSorting", false);
            yaml.set("Other.CustomRecipes.OPSword.Result.Type", "GOLD_SWORD");
            yaml.set("Other.CustomRecipes.OPSword.Result.Amount", 1);
            yaml.set("Other.CustomRecipes.OPSword.Result.DisplayName", "§3§lOP §b§lSchwert");
            yaml.set("Other.CustomRecipes.OPSword.Result.Enchantments.SHARPNESS", 2);
            yaml.set("Other.CustomRecipes.OPSword.Material.1.Type", "AIR");
            yaml.set("Other.CustomRecipes.OPSword.Material.2.Type", "AIR");
            yaml.set("Other.CustomRecipes.OPSword.Material.3.Type", "AIR");
            yaml.set("Other.CustomRecipes.OPSword.Material.4.Type", "AIR");
            yaml.set("Other.CustomRecipes.OPSword.Material.5.Type", "DIAMOND");
            yaml.set("Other.CustomRecipes.OPSword.Material.6.Type", "AIR");
            yaml.set("Other.CustomRecipes.OPSword.Material.7.Type", "AIR");
            yaml.set("Other.CustomRecipes.OPSword.Material.8.Type", "STICK");
            yaml.set("Other.CustomRecipes.OPSword.Material.9.Type", "AIR");
            pathsChanged = true;
        }

        //ChatFilter
        if (!isPathSet("Other.ChatFilter.Enabled")) {
            yaml.set("Other.ChatFilter.Enabled", true);
            yaml.set("Other.ChatFilter.Permission.Notify", "system.chatfilter.notify");
            yaml.set("Other.ChatFilter.Permission.ByPass", "system.chatfilter.bypass");
            yaml.set("Other.ChatFilter.ExecuteConsoleCommand", true);
            yaml.set("Other.ChatFilter.BadWords.muteIP_{Player}_2h_Chatverhalten:_{BadWord}", Arrays.asList("hurensohn", "nutte", "nuttensohn", "hure", "arschloch", "bitch", "behindert", "mistgeburt", "missgeburt", "nazi", "scheiß", "schwuchtel", "hundesohn", "huan", "du hund", "nigger"));
            yaml.set("Other.ChatFilter.Advertising.Whitelist", Arrays.asList("NewDavis.me", "YourServer.com"));
            yaml.set("Other.ChatFilter.Advertising.Blacklist", Arrays.asList(".de", ".me", ".eu", ".net", ".com", ".en", ".dev", ".xyz", ".world", ".org", ".ch", ".info", ".shop", ".app"));
            yaml.set("Other.ChatFilter.Advertising.muteIP_{Player}_1d_Werbung:_{Advertisement}", Collections.singletonList("NotYourServer.net"));
            yaml.set("Other.ChatFilter.Advertising.NotifyMessage", Arrays.asList("§8§m----------§8(§c§lWERBUNG§8)§8§m----------", "", "{Prefix} Der Spieler §c{Player} §7wollte für §c{Advertisement} §7Werbung gemacht!", "", "§8§m----------§8(§c§lWERBUNG§8)§8§m----------"));
            yaml.set("Other.ChatFilter.MessageCouldNotBeSend", Arrays.asList("§8§m----------§8(§c§lChatverhalten§8)§8§m----------", "", "{Prefix} §cDeine Nachricht konnte aus Verdacht auf Werbung oder Beleidigung nicht versendet werden!", "", "§8§m----------§8(§c§lChatverhalten§8)§8§m----------"));
            pathsChanged = true;
        }

        //Portal
        if (!isPathSet("Other.Portal.Enabled")) {
            yaml.set("Other.Portal.Enabled", true);
            yaml.set("Other.Portal.ServerNotFound", Collections.singletonList("{Prefix} §cDer Server {Server} wurde nicht gefunden!"));
            yaml.set("Other.Portal.Spawn.Permission", "system.portal.spawn.enter");
            yaml.set("Other.Portal.Spawn.Location.From.World", "world");
            yaml.set("Other.Portal.Spawn.Location.From.X", 57);
            yaml.set("Other.Portal.Spawn.Location.From.Y", 81);
            yaml.set("Other.Portal.Spawn.Location.From.Z", 146);
            yaml.set("Other.Portal.Spawn.Location.To.World", "world");
            yaml.set("Other.Portal.Spawn.Location.To.X", 57);
            yaml.set("Other.Portal.Spawn.Location.To.Y", 85);
            yaml.set("Other.Portal.Spawn.Location.To.Z", 143);
            yaml.set("Other.Portal.Spawn.ExecuteCommand", "spawn");
            yaml.set("Other.Portal.Spawn.Teleport.Location.World", "world");
            yaml.set("Other.Portal.Spawn.Teleport.Location.X", 59D);
            yaml.set("Other.Portal.Spawn.Teleport.Location.Y", 81D);
            yaml.set("Other.Portal.Spawn.Teleport.Location.Z", 144D);
            yaml.set("Other.Portal.Spawn.Teleport.Location.Yaw", 90F);
            yaml.set("Other.Portal.Spawn.Teleport.Location.Pitch", 0F);
            yaml.set("Other.Portal.Spawn.Message", Collections.singletonList("{Prefix} *Wushhhhh*"));
            yaml.set("Other.Portal.Lobby.Location.From.World", "world");
            yaml.set("Other.Portal.Lobby.Location.From.X", 57);
            yaml.set("Other.Portal.Lobby.Location.From.Y", 81);
            yaml.set("Other.Portal.Lobby.Location.From.Z", 143);
            yaml.set("Other.Portal.Lobby.Location.To.World", "world");
            yaml.set("Other.Portal.Lobby.Location.To.X", 57);
            yaml.set("Other.Portal.Lobby.Location.To.Y", 85);
            yaml.set("Other.Portal.Lobby.Location.To.Z", 140);
            yaml.set("Other.Portal.Lobby.SendToServer", "Lobby");
            yaml.set("Other.Portal.Lobby.Message", Collections.singletonList("{Prefix} Du wurdest zum §eServer {Server} §7vom §ePortal {Portal} §7geschmissen."));
            pathsChanged = true;
        }

        //PlayTimeReward
        if (!isPathSet("Other.PlayTimeReward.Enabled")) {
            yaml.set("Other.PlayTimeReward.Enabled", true);
            yaml.set("Other.PlayTimeReward.10.Minutes", 10);
            yaml.set("Other.PlayTimeReward.10.Message", Arrays.asList("{Prefix} Du hast 10 Minuten hier gespielt, hier deine Belohnung:",
                    "{Prefix} §a+100{CurrencyPrefix}",
                    "{Prefix} §7Du bist nun §5VIP",
                    "{Prefix} §7Du kannst nun das Kit §a10min §7abholen."));
            yaml.set("Other.PlayTimeReward.10.Currency", 100);
            yaml.set("Other.PlayTimeReward.10.Role", "VIP");
            yaml.set("Other.PlayTimeReward.10.Permission", Collections.singletonList("system.kit.10min"));
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

    public static HashMap<String, Collection<String>> getAutoBroadcastMessages() {
        HashMap<String, Collection<String>> messages = new HashMap<>();

        Set<String> keys = yaml.getConfigurationSection("Other.AutoBroadcast").getKeys(false);
        for(String key : keys) {
            if(!(key.equalsIgnoreCase("Enabled") || key.equalsIgnoreCase("MessageDelayInMinutes"))) {
                messages.put(key, yaml.getStringList("Other.AutoBroadcast." + key));
            }
        }
        return messages;
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
