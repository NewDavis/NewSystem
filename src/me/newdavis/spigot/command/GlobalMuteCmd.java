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

public class GlobalMuteCmd implements CommandExecutor {

    public static boolean globalMute = false;

    private static String perm;
    private static List<String> usage;
    private static List<String> message;
    private static String activated;
    private static String deactivated;
    private static boolean globalMessage;
    private static List<String> messageGlobal;
    public static List<String> messageListener;
    public static String permByPass;

    public void init() {
        perm = CommandFile.getStringPath("Command.GlobalMute.Permission.Use");
        usage = CommandFile.getStringListPath("Command.GlobalMute.Usage");
        message = CommandFile.getStringListPath("Command.GlobalMute.Message");
        activated = CommandFile.getStringPath("Command.GlobalMute.Status.Activated");
        deactivated = CommandFile.getStringPath("Command.GlobalMute.Status.Deactivated");
        globalMessage = CommandFile.getBooleanPath("Command.GlobalMute.GlobalMessage");
        messageGlobal = CommandFile.getStringListPath("Command.GlobalMute.MessageGlobal");
        messageListener = CommandFile.getStringListPath("Command.GlobalMute.MessageMute");
        permByPass = CommandFile.getStringPath("Command.GlobalMute.Permission.ByPass");
        NewSystem.getInstance().getCommand("globalmute").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(args.length == 0) {
                if(NewSystem.hasPermission(p, perm)) {
                    if (globalMute) {
                        for (String msg : message) {
                            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Status}", deactivated));
                        }
                        if (globalMessage) {
                            for(Player all : Bukkit.getOnlinePlayers()) {
                                for (String msg : messageGlobal) {
                                    all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Status}", deactivated).replace("{Player}", NewSystem.getName(p, false)).replace("{DisplayName}", NewSystem.getName(p, true)));
                                }
                            }
                        }
                        globalMute = false;
                    } else {
                        for (String msg : message) {
                            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Status}", activated));
                        }
                        if (globalMessage) {
                            for(Player all : Bukkit.getOnlinePlayers()) {
                                for (String msg : messageGlobal) {
                                    all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Status}", activated).replace("{Player}", NewSystem.getName(p, false)).replace("{DisplayName}", NewSystem.getName(p, true)));
                                }
                            }
                        }
                        globalMute = true;
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
            if(args.length == 0) {
                if (globalMute) {
                    for (String msg : message) {
                        sender.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Status}", deactivated));
                    }
                    if (globalMessage) {
                        for(Player all : Bukkit.getOnlinePlayers()) {
                            for (String msg : messageGlobal) {
                                all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Status}", deactivated).replace("{Player}", SettingsFile.getConsolePrefix()));
                            }
                        }
                    }
                    globalMute = false;
                } else {
                    for (String msg : message) {
                        sender.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Status}", activated));
                    }
                    if (globalMessage) {
                        for(Player all : Bukkit.getOnlinePlayers()) {
                            for (String msg : messageGlobal) {
                                all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Status}", activated).replace("{Player}", SettingsFile.getConsolePrefix()));
                            }
                        }
                    }
                    globalMute = true;
                }
            }else{
                for(String value : usage) {
                    sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }
        return false;
    }
}
