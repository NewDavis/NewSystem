package me.newdavis.spigot.command;

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandSpyCmd implements CommandExecutor {

    public static ArrayList<Player> cmdSpyList = new ArrayList<>();

    private static List<String> usage;
    private static String perm;
    private static String permOther;
    private static String activated;
    private static String deactivated;
    private static List<String> msg;
    private static List<String> msgP;
    public static List<String> format;

    public void init() {
        usage = CommandFile.getStringListPath("Command.CommandSpy.Usage");
        perm = CommandFile.getStringPath("Command.CommandSpy.Permission.Use");
        permOther = CommandFile.getStringPath("Command.CommandSpy.Permission.Other");
        activated = CommandFile.getStringPath("Command.CommandSpy.Activated");
        deactivated = CommandFile.getStringPath("Command.CommandSpy.Deactivated");
        msg = CommandFile.getStringListPath("Command.CommandSpy.Message");
        msgP = CommandFile.getStringListPath("Command.CommandSpy.MessagePlayer");
        format = CommandFile.getStringListPath("Command.CommandSpy.Format");
        NewSystem.getInstance().getCommand("commandspy").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if (NewSystem.hasPermission(p, perm)) {
                if (args.length == 0) {
                    if (cmdSpyList.contains(p)) {
                        cmdSpyList.remove(p);
                        for(String key : msgP) {
                            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CommandSpyMode}", deactivated));
                        }
                    } else {
                        cmdSpyList.add(p);
                        for(String key : msgP) {
                            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CommandSpyMode}", activated));
                        }
                    }
                } else if (args.length == 1) {
                    Player t = Bukkit.getPlayer(args[0]);
                    if (t != null) {
                        if (NewSystem.hasPermission(p, permOther)) {
                            if (cmdSpyList.contains(t)) {
                                cmdSpyList.remove(t);
                                for(String key : msgP) {
                                    t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CommandSpyMode}", deactivated));
                                }
                                for(String key : msg) {
                                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CommandSpyMode}", deactivated).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                                }
                            } else {
                                cmdSpyList.add(t);
                                for(String key : msgP) {
                                    t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CommandSpyMode}", activated));
                                }
                                for(String key : msg) {
                                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CommandSpyMode}", activated).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                                }
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
            sender.sendMessage(SettingsFile.getOnlyPlayerCanExecute());
        }
        return false;
    }
}
