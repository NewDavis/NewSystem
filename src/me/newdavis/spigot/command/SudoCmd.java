package me.newdavis.spigot.command;
//Plugin by NewDavis

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SudoCmd implements CommandExecutor {

    private static List<String> usage;
    private static String perm;
    private static String permCommand;
    private static String permMessage;
    private static List<String> msg;

    public void init() {
        usage = CommandFile.getStringListPath("Command.Sudo.Usage");
        perm = CommandFile.getStringPath("Command.Sudo.Permission.Use");
        permCommand = CommandFile.getStringPath("Command.Sudo.Permission.Command");
        permMessage = CommandFile.getStringPath("Command.Sudo.Permission.Message");
        msg = CommandFile.getStringListPath("Command.Sudo.Message");
        NewSystem.getInstance().getCommand("sudo").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if(args.length == 0) {
                    for(String value : usage) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }else if(args.length > 1) {
                    Player t = Bukkit.getPlayer(args[0]);
                    if(t != null) {
                        if (args[1].contains("/")) {
                            if(NewSystem.hasPermission(p, permCommand)) {
                                String cmdString = "";
                                for (int i = 1; i < args.length; i++) {
                                    if (i == 1) {
                                        cmdString = args[i];
                                    } else {
                                        cmdString += " " + args[i];
                                    }
                                }
                                t.performCommand(cmdString.replace("/", ""));
                                for(String key : msg) {
                                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)).replace("{Action}", cmdString));
                                }
                            }
                        }else{
                            if(NewSystem.hasPermission(p, permMessage)) {
                                String msgString = "";
                                for (int i = 1; i < args.length; i++) {
                                    if (i == 1) {
                                        msgString = args[i];
                                    } else {
                                        msgString = msgString + " " + args[i];
                                    }
                                }
                                t.chat(msgString);
                                for(String key : msg) {
                                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)).replace("{Action}", msgString));
                                }
                            }
                        }
                    }else{
                        p.sendMessage(SettingsFile.getOffline());
                    }
                }else{
                    for(String key : usage) {
                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }else{
                p.sendMessage(SettingsFile.getNoPerm());
            }
        }else{
            if (args.length > 1) {
                Player t = Bukkit.getPlayer(args[0]);
                if (t != null) {
                    if (args[1].contains("/")) {
                        String cmdString = "";
                        for (int i = 1; i < args.length; i++) {
                            if (i == 1) {
                                cmdString = args[i];
                            } else {
                                cmdString += " " + args[i];
                            }
                        }
                        t.performCommand(cmdString.replace("/", ""));
                        for (String key : msg) {
                            sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)).replace("{Action}", cmdString));
                        }
                    } else {
                        String msgString = "";
                        for (int i = 1; i < args.length; i++) {
                            if (i == 1) {
                                msgString = args[i];
                            } else {
                                msgString = msgString + " " + args[i];
                            }
                        }
                        t.chat(msgString);
                        for (String key : msg) {
                            sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)).replace("{Action}", msgString));
                        }
                    }
                } else {
                    sender.sendMessage(SettingsFile.getOffline());
                }
            }else{
                for(String key : usage) {
                    sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }
        return false;
    }
}
