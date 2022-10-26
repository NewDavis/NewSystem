package me.newdavis.spigot.command;
//Plugin by NewDavis

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TeamChatCmd implements CommandExecutor {

    private static String perm;
    private static List<String> usage;
    private static boolean colorCodes;
    private static List<String> format;

    public void init() {
        perm = CommandFile.getStringPath("Command.TeamChat.Permission");
        usage = CommandFile.getStringListPath("Command.TeamChat.Usage");
        colorCodes = CommandFile.getBooleanPath("Command.TeamChat.ColorCodes");
        format = CommandFile.getStringListPath("Command.TeamChat.Format");
        NewSystem.getInstance().getCommand("teamchat").setExecutor(this);
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
                }else {
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (NewSystem.hasPermission(all, perm)) {
                            for(String key : format) {
                                all.sendMessage((colorCodes ?
                                        key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(p)).replace("{Message}", ChatColor.translateAlternateColorCodes('&', String.join(" ", args))) :
                                        key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(p)).replace("{Message}", String.join(" ", args))));
                            }
                        }
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
