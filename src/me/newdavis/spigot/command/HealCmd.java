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

public class HealCmd implements CommandExecutor {

    private static List<String> usage;
    private static String perm;
    private static String permOther;
    private static List<String> msg;
    private static List<String> msgP;

    public void init() {
        usage = CommandFile.getStringListPath("Command.Heal.Usage");
        perm = CommandFile.getStringPath("Command.Heal.Permission.Use");
        permOther = CommandFile.getStringPath("Command.Heal.Permission.Other");
        msg = CommandFile.getStringListPath("Command.Heal.Message");
        msgP = CommandFile.getStringListPath("Command.Heal.MessagePlayer");
        NewSystem.getInstance().getCommand("heal").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if (NewSystem.hasPermission(p, perm)) {
                if (args.length == 0) {
                    p.setHealth(20);
                    for(String key : msgP) {
                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                } else if (args.length == 1) {
                    Player t = Bukkit.getPlayer(args[0]);
                    if (t != null) {
                        if (NewSystem.hasPermission(p, permOther)) {
                            t.setHealth(20);
                            for(String key : msgP) {
                                t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                            for(String key : msg) {
                                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                            }
                        } else {
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    } else {
                        p.sendMessage(SettingsFile.getOffline());
                    }
                } else {
                    for(String msg2 : usage) {
                        p.sendMessage(msg2.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }else{
                p.sendMessage(SettingsFile.getNoPerm());
            }
        }else{
            if (args.length == 1) {
                Player t = Bukkit.getPlayer(args[0]);
                if (t != null) {
                        t.setHealth(20);
                        for(String key : msgP) {
                            t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                        for(String key : msg) {
                            sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                        }
                } else {
                    sender.sendMessage(SettingsFile.getOffline());
                }
            } else {
                for(String msg2 : usage) {
                    sender.sendMessage(msg2.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }
        return false;
    }
}
