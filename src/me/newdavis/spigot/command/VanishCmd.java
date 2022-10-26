package me.newdavis.spigot.command;
//Plugin by NewDavis

import me.newdavis.spigot.api.TabListAPI;
import me.newdavis.spigot.file.*;
import me.newdavis.spigot.util.ScoreboardManager;
import me.newdavis.spigot.util.TabListPrefix;
import me.newdavis.spigot.plugin.NewSystem;
import me.newdavis.spigot.util.placeholder.PlaceholderManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class VanishCmd implements CommandExecutor {

    private static List<String> usage;
    private static String perm;
    private static String permOther;
    private static String permList;
    private static String vanishedPlayers;
    private static List<String> vanishListMessage;
    private static String noVanish;
    private static List<String> quitMsg;
    private static List<String> joinMsg;
    private static List<String> msg;
    private static List<String> msgP;
    private static String activated;
    private static String deactivated;
    private static List<String> msgStillVanish;

    public void init() {
        usage = CommandFile.getStringListPath("Command.Vanish.Usage");
        perm = CommandFile.getStringPath("Command.Vanish.Permission.Use");
        permOther = CommandFile.getStringPath("Command.Vanish.Permission.Other");
        permList = CommandFile.getStringPath("Command.Vanish.Permission.List");
        vanishedPlayers = CommandFile.getStringPath("Command.Vanish.List.VanishedPlayers");
        vanishListMessage = CommandFile.getStringListPath("Command.Vanish.List.Message");
        noVanish = CommandFile.getStringPath("Command.Vanish.List.MessageNoVanish");
        quitMsg = ListenerFile.getStringListPath("Listener.Quit.Message");
        joinMsg = ListenerFile.getStringListPath("Listener.Join.Message");
        msg = CommandFile.getStringListPath("Command.Vanish.Message");
        msgP = CommandFile.getStringListPath("Command.Vanish.MessagePlayer");
        activated = CommandFile.getStringPath("Command.Vanish.Activated");
        deactivated = CommandFile.getStringPath("Command.Vanish.Deactivated");
        msgStillVanish = CommandFile.getStringListPath("Command.Vanish.MessageStillInVanish");
        NewSystem.getInstance().getCommand("vanish").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if(args.length == 0) {
                    toggleVanish(p, p);
                }else if(args.length == 1) {
                    if(args[0].equalsIgnoreCase("list")) {
                        if(NewSystem.hasPermission(p, permList)) {
                            sendVanishList(p);
                        }else{
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    }else {
                        Player t = Bukkit.getPlayer(args[0]);
                        if (t != null) {
                            if (NewSystem.hasPermission(p, permOther)) {
                                toggleVanish(p, t);
                            }
                        } else {
                            p.sendMessage(SettingsFile.getOffline());
                        }
                    }
                }else{
                    for(String value : usage) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }else{
                p.sendMessage(SettingsFile.getNoPerm());
            }
        }else{
            sender.sendMessage(SettingsFile.getOnlyPlayerCanExecute());
        }
        return false;
    }

    public static void sendVanishList(Player p) {
        String vanishedPlayerFormat = "";
        int vanishCount = 0;
        for(String uuid : SavingsFile.getStringListPath("Vanish.Vanished")) {
            Player vanishedPlayer = Bukkit.getPlayer(UUID.fromString(uuid));
            if(vanishedPlayer != null) {
                vanishCount++;
                if(vanishedPlayerFormat.equalsIgnoreCase("")) {
                    vanishedPlayerFormat = vanishedPlayers.replace("{Player}", NewSystem.getName(vanishedPlayer));
                }else{
                    vanishedPlayerFormat = vanishedPlayerFormat + " " + vanishedPlayers.replace("{Player}", NewSystem.getName(vanishedPlayer));
                }
            }
        }

        if(vanishCount == 0) {
            for (String msg : vanishListMessage) {
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Vanished-Player}", noVanish).replace("{Vanish-Count}", String.valueOf(vanishCount)));
            }
        }else {
            for (String msg : vanishListMessage) {
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Vanish-Count}", String.valueOf(vanishCount)).replace("{Vanished-Player}", vanishedPlayerFormat));
            }
        }
    }

    public static boolean playerIsVanished(OfflinePlayer p) {
        List<String> vanished = SavingsFile.getStringListPath("Vanish.Vanished");
        return vanished.contains(p.getUniqueId().toString());
    }

    public static void toggleVanish(Player p, Player t) {
        List<String> vanished = SavingsFile.getStringListPath("Vanish.Vanished");
        if(!vanished.contains(t.getUniqueId().toString())) {
            for(Player all : Bukkit.getOnlinePlayers()) {
                if(!NewSystem.hasPermission(all, perm)) {
                    all.hidePlayer(t);
                }
            }
            vanished.add(t.getUniqueId().toString());
            SavingsFile.setPath("Vanish.Vanished", vanished);

            for(String key : quitMsg) {
                Bukkit.broadcastMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)));
            }

            for(String key : msgP) {
                t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Vanish}", activated));
            }

            //Update Placeholder
            PlaceholderManager phManager = new PlaceholderManager(t);
            phManager.updatePlaceholder("{Vanish}");
            phManager = new PlaceholderManager();
            phManager.updatePlaceholder("{VanishCount}");

            if (TabListFile.getBooleanPath("TabList.Enabled")) {
                TabListPrefix.setTabListForAll();
            }

            if(OtherFile.getBooleanPath("Other.TabList.Enabled")) {
                TabListAPI.setTabList();
            }

            if(OtherFile.getBooleanPath("Other.ScoreBoard.Enabled")) {
                new ScoreboardManager(t).updateScoreBoard();
            }

            if(p != t) {
                for(String key : msg) {
                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Vanish}", activated).replace("{Player}", NewSystem.getName(t)));
                }
            }
        }else{
            for(Player all : Bukkit.getOnlinePlayers()) {
                all.showPlayer(t);
            }
            vanished.remove(t.getUniqueId().toString());
            SavingsFile.setPath("Vanish.Vanished", vanished);

            for (String key : joinMsg) {
                Bukkit.broadcastMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)));
            }

            for(String key : msgP) {
                t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Vanish}", deactivated));
            }

            //Update Placeholder
            PlaceholderManager phManager = new PlaceholderManager(t);
            phManager.updatePlaceholder("{Vanish}");
            phManager = new PlaceholderManager();
            phManager.updatePlaceholder("{VanishCount}");

            if (TabListFile.getBooleanPath("TabList.Enabled")) {
                TabListPrefix.setTabListForAll();
            }

            if(OtherFile.getBooleanPath("Other.TabList.Enabled")) {
                TabListAPI.setTabList();
            }

            if(OtherFile.getBooleanPath("Other.ScoreBoard.Enabled")) {
                ScoreboardManager.updateEveryScoreboard();
            }

            if(p != t) {
                for(String key : msg) {
                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Vanish}", deactivated).replace("{Player}", NewSystem.getName(t)));
                }
            }
        }
    }

    public static void joinMethod(Player p) {
        List<String> vanished = SavingsFile.getStringListPath("Vanish.Vanished");
        if(vanished.contains(p.getUniqueId().toString())) {
            for(Player all : Bukkit.getOnlinePlayers()) {
                if(!NewSystem.hasPermission(all, perm)) {
                    all.hidePlayer(p);
                }
            }

            for(String value : msgStillVanish) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }

            //Update Placeholder
            PlaceholderManager phManager = new PlaceholderManager();
            phManager.updatePlaceholder("{VanishCount}");

            if (TabListFile.getBooleanPath("TabList.Enabled")) {
                TabListPrefix.setTabListForAll();
            }

            if(OtherFile.getBooleanPath("Other.TabList.Enabled")) {
                TabListAPI.setTabList();
            }

            if(OtherFile.getBooleanPath("Other.ScoreBoard.Enabled")) {
                ScoreboardManager.updateEveryScoreboard();
            }
        }else{
            if(!NewSystem.hasPermission(p, perm)) {
                for(String vanishedPlayer : vanished) {
                    Player t = Bukkit.getPlayer(UUID.fromString(vanishedPlayer));
                    if(t != null) {
                        p.hidePlayer(t);
                    }
                }
            }
        }
    }
}
