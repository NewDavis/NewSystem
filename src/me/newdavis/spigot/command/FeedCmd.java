package me.newdavis.spigot.command;

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class FeedCmd implements CommandExecutor {

    private static List<String> usage;
    private static String perm;
    private static String permOther;
    private static List<String> msg;
    private static List<String> msgP;

    public void init() {
        usage = CommandFile.getStringListPath("Command.Feed.Usage");
        perm = CommandFile.getStringPath("Command.Feed.Permission.Use");
        permOther = CommandFile.getStringPath("Command.Feed.Permission.Other");
        msg = CommandFile.getStringListPath("Command.Feed.Message");
        msgP = CommandFile.getStringListPath("Command.Feed.MessagePlayer");
        NewSystem.getInstance().getCommand("feed").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if (NewSystem.hasPermission(p, perm)) {
                if (args.length == 0) {
                    p.setFoodLevel(20);
                    for(String key : msgP) {
                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                } else if (args.length == 1) {
                    Player t = Bukkit.getPlayer(args[0]);
                    if (t != null) {
                        if (NewSystem.hasPermission(p, permOther)) {
                            t.setFoodLevel(20);
                            for(String key : msgP) {
                                t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                            for(String key : msg) {
                                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)));
                            }
                        } else {
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    } else {
                        p.sendMessage(SettingsFile.getOffline());
                    }
                } else {
                    for(String value : usage) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }else{
                p.sendMessage(SettingsFile.getNoPerm());
            }
        }else{
            if (args.length == 1) {
                Player t = Bukkit.getPlayer(args[0]);
                if (t != null) {
                    t.setFoodLevel(20);
                    for (String key : msgP) {
                        t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                    for (String key : msg) {
                        sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)));
                    }
                } else {
                    sender.sendMessage(SettingsFile.getOffline());
                }
            } else {
                for(String value : usage) {
                    sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }
        return false;
    }
}
