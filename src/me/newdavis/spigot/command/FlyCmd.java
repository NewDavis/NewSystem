package me.newdavis.spigot.command;

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.listener.DoubleJumpListener;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class FlyCmd implements CommandExecutor {

    private static List<String> usage;
    private static String perm;
    private static String permOther;
    private static List<String> msg;
    private static List<String> msgP;
    private static String deactivated;
    private static String activated;

    public void init() {
        usage = CommandFile.getStringListPath("Command.Fly.Usage");
        perm = CommandFile.getStringPath("Command.Fly.Permission.Use");
        permOther = CommandFile.getStringPath("Command.Fly.Permission.Other");
        msg = CommandFile.getStringListPath("Command.Fly.Message");
        msgP = CommandFile.getStringListPath("Command.Fly.MessagePlayer");
        deactivated = CommandFile.getStringPath("Command.Fly.Deactivated");
        activated = CommandFile.getStringPath("Command.Fly.Activated");
        NewSystem.getInstance().getCommand("fly").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if (NewSystem.hasPermission(p, perm)) {
                if (args.length == 0) {
                    if (p.getAllowFlight()) {
                        p.setAllowFlight(false);
                        for(String key : msgP) {
                            p.sendMessage(key.replace("{FlyMode}", deactivated).replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                        DoubleJumpListener.flyMode.remove(p);
                    } else {
                        p.setAllowFlight(true);
                        for(String key : msgP) {
                            p.sendMessage(key.replace("{FlyMode}", activated).replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                        DoubleJumpListener.flyMode.add(p);
                    }
                } else if (args.length == 1) {
                    if (NewSystem.hasPermission(p, permOther)) {
                        Player t = Bukkit.getPlayer(args[0]);
                        if (t != null) {
                            if (t.getAllowFlight()) {
                                t.setAllowFlight(false);
                                for(String key : msgP) {
                                    p.sendMessage(key.replace("{FlyMode}", deactivated).replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                                for(String key : msg) {
                                    p.sendMessage(key.replace("{FlyMode}", deactivated).replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)));
                                }
                                DoubleJumpListener.flyMode.remove(t);
                            } else {
                                t.setAllowFlight(true);
                                for(String key : msgP) {
                                    p.sendMessage(key.replace("{FlyMode}", activated).replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                                for(String key : msg) {
                                    p.sendMessage(key.replace("{FlyMode}", activated).replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)));
                                }
                                DoubleJumpListener.flyMode.add(t);
                            }
                        } else {
                            p.sendMessage(SettingsFile.getOffline());
                        }
                    } else {
                        p.sendMessage(SettingsFile.getNoPerm());
                    }
                }else{
                    for(String value : usage) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            } else {
                p.sendMessage(SettingsFile.getNoPerm());
            }
        }else {
            if (args.length == 1) {
                Player t = Bukkit.getPlayer(args[0]);
                if (t != null) {
                    if (t.getAllowFlight()) {
                        t.setAllowFlight(false);
                        for (String key : msgP) {
                            t.sendMessage(key.replace("{FlyMode}", deactivated).replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                        for (String key : msg) {
                            sender.sendMessage(key.replace("{FlyMode}", deactivated).replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)));
                        }
                        DoubleJumpListener.flyMode.remove(t);
                    } else {
                        t.setAllowFlight(true);
                        for (String key : msgP) {
                            t.sendMessage(key.replace("{FlyMode}", activated).replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                        for (String key : msg) {
                            sender.sendMessage(key.replace("{FlyMode}", activated).replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)));
                        }
                        DoubleJumpListener.flyMode.add(t);
                    }
                } else {
                    sender.sendMessage(SettingsFile.getOffline());
                }
            } else {
                for (String value : usage) {
                    sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }
        return false;
    }
}
