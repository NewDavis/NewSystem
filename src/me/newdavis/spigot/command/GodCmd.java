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

public class GodCmd implements CommandExecutor {

    public static ArrayList<Player> godList = new ArrayList<>();

    private static List<String> usage;
    private static String perm;
    private static String permOther;
    private static String activated;
    private static String deactivated;
    private static List<String> msg;
    private static List<String> msgP;

    public void init() {
        usage = CommandFile.getStringListPath("Command.God.Usage");
        perm = CommandFile.getStringPath("Command.God.Permission.Use");
        permOther = CommandFile.getStringPath("Command.God.Permission.Other");
        activated = CommandFile.getStringPath("Command.God.Activated");
        deactivated = CommandFile.getStringPath("Command.God.Deactivated");
        msg = CommandFile.getStringListPath("Command.God.Message");
        msgP = CommandFile.getStringListPath("Command.God.MessagePlayer");
        NewSystem.getInstance().getCommand("god").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if(args.length == 0) {
                    if(godList.contains(p)) {
                        godList.remove(p);
                        for(String key : msgP) {
                            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Status}", deactivated));
                        }
                    }else{
                        godList.add(p);
                        for(String key : msgP) {
                            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Status}", activated));
                        }
                    }
                }else if(args.length == 1) {
                    if(NewSystem.hasPermission(p, permOther)) {
                        Player t = Bukkit.getPlayer(args[0]);
                        if(t != null){
                            if(p != t) {
                                if(godList.contains(t)) {
                                    godList.remove(t);
                                    for(String key : msgP) {
                                        t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Status}", deactivated));
                                    }
                                    for(String key : msg) {
                                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Status}", deactivated).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                                    }
                                }else{
                                    godList.add(t);
                                    for(String key : msgP) {
                                        t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Status}", activated));
                                    }
                                    for(String key : msg) {
                                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Status}", activated).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                                    }
                                }
                            }else{
                                if(godList.contains(p)) {
                                    godList.remove(p);
                                    for(String key : msgP) {
                                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Status}", deactivated));
                                    }
                                }else{
                                    godList.add(p);
                                    for(String key : msgP) {
                                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Status}", activated));
                                    }
                                }
                            }
                        }else{
                            p.sendMessage(SettingsFile.getOffline());
                        }
                    }else{
                        p.sendMessage(SettingsFile.getNoPerm());
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
}
