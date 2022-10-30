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

public class BroadcastCmd implements CommandExecutor {

    private static String perm;
    private static List<String> usage;
    private static List<String> msg;

    public BroadcastCmd() {
        perm = CommandFile.getStringPath("Command.Broadcast.Permission");
        usage = CommandFile.getStringListPath("Command.Broadcast.Usage");
        msg = CommandFile.getStringListPath("Command.Broadcast.Message");
        if(!NewSystem.loadedCommands.contains(this)) {
            NewSystem.loadedCommands.add(this);
            NewSystem.getInstance().getCommand("broadcast").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if(sender instanceof Player) {
            Player p = (Player) sender;
            if (NewSystem.hasPermission(p, perm)) {
                if (args.length > 0) {
                    for(String msgString : msg) {
                        Bukkit.broadcastMessage(msgString.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Message}", String.join(" ", args).replace('&', '§')));
                    }
                }else{
                    for(String value : usage) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            } else {
                p.sendMessage(SettingsFile.getNoPerm());
            }
        }else{
            if (args.length > 0) {
                for(String msgString : msg) {
                    Bukkit.broadcastMessage(msgString.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Message}", String.join(" ", args).replace('&', '§')));
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
