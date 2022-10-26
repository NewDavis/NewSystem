package me.newdavis.spigot.util;

import me.newdavis.spigot.file.OtherFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatFilter {

    private static final HashMap<String, List<String>> badWords = new HashMap<>();
    private static final HashMap<String, List<String>> advertisementThatWillBePunished = new HashMap<>();
    private static List<String> advertisementWhitelist = new ArrayList<>();
    private static List<String> advertisementBlacklist = new ArrayList<>();
    private static boolean executeConsoleCommand = false;
    private static String permNotify;
    private static String permByPass;
    private static List<String> notifyMessage = new ArrayList<>();
    private static List<String> messageCouldNotBeSend = new ArrayList<>();

    public ChatFilter() {
    }

    public void init() {
        for(String cmd : OtherFile.getConfigurationSection("Other.ChatFilter.BadWords")) {
            List<String> badWordsList = OtherFile.getStringListPath("Other.ChatFilter.BadWords." + cmd);
            badWords.put(cmd, badWordsList);
        }

        for(String cmd : OtherFile.getConfigurationSection("Other.ChatFilter.Advertising")) {
            if(!(cmd.equalsIgnoreCase("Whitelist") || cmd.equalsIgnoreCase("Blacklist") || cmd.equalsIgnoreCase("NotifyMessage"))) {
                List<String> advertisementList = OtherFile.getStringListPath("Other.ChatFilter.Advertising." + cmd);
                advertisementThatWillBePunished.put(cmd, advertisementList);
            }
        }

        advertisementWhitelist = OtherFile.getStringListPath("Other.ChatFilter.Advertising.Whitelist");
        advertisementBlacklist = OtherFile.getStringListPath("Other.ChatFilter.Advertising.Blacklist");

        executeConsoleCommand = OtherFile.getBooleanPath("Other.ChatFilter.ExecuteConsoleCommand");

        permNotify = OtherFile.getStringPath("Other.ChatFilter.Permission.Notify");
        permByPass = OtherFile.getStringPath("Other.ChatFilter.Permission.ByPass");
        notifyMessage = OtherFile.getStringListPath("Other.ChatFilter.Advertising.NotifyMessage");
        messageCouldNotBeSend = OtherFile.getStringListPath("Other.ChatFilter.MessageCouldNotBeSend");
    }


    private Player p;
    private String message;

    public ChatFilter(Player p, String message) {
        this.p = p;
        this.message = message;
    }

    public boolean checkForBadWords() {
        if(NewSystem.hasPermission(p, permByPass)) {
            return false;
        }

        for(String cmd : badWords.keySet()) {
            List<String> badWordsList = badWords.get(cmd);
            for(String badWord : badWordsList) {
                for(String word : message.split(" ")) {
                    if (word.toLowerCase().contains(badWord.toLowerCase())) {
                        if (executeConsoleCommand) {
                            executeConsoleCommand(cmd, badWord);
                            return true;
                        } else {
                            sendMessageCouldNotBeSend();
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean checkForAdvertising() {
        if(NewSystem.hasPermission(p, permByPass)) {
            return false;
        }

        for(String advertise : advertisementWhitelist) {
            if(message.toLowerCase().contains(advertise.toLowerCase())) {
                return false;
            }
        }

        for(String cmd : advertisementThatWillBePunished.keySet()) {
            List<String> adsThatWillBePunishedList = advertisementThatWillBePunished.get(cmd);
            for(String ad : adsThatWillBePunishedList) {
                for(String word : message.split(" ")) {
                    if (word.toLowerCase().contains(ad.toLowerCase())) {
                        if (executeConsoleCommand) {
                            executeConsoleCommand(cmd, word);
                            return true;
                        } else {
                            sendNotify(word);
                            sendMessageCouldNotBeSend();
                            return true;
                        }
                    }
                }
            }
        }

        for(String advertise : advertisementBlacklist) {
            for(String word : message.split(" ")) {
                if (word.toLowerCase().contains(advertise.toLowerCase())) {
                    sendNotify(word);
                    sendMessageCouldNotBeSend();
                    return true;
                }
            }
        }
        return false;
    }

    private void executeConsoleCommand(String cmd, String reason) {
        if(executeConsoleCommand) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("_", " ")
                    .replace("{Player}", p.getName()).replace("{Advertisement}", reason).replace("{BadWord}", reason));
        }
    }

    private void sendNotify(String advertisement) {
        for(Player all : Bukkit.getOnlinePlayers()) {
            if(NewSystem.hasPermission(all, permNotify)) {
                for (String msg : notifyMessage) {
                    all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                            .replace("{Player}", NewSystem.getName(p))
                            .replace("{Advertisement}", advertisement));
                }
            }
        }
    }

    private void sendMessageCouldNotBeSend() {
        for(String msg : messageCouldNotBeSend) {
            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
        }
    }

}
