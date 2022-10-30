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

public class EnderchestCmd implements CommandExecutor {

    private static List<String> usage;
    private static String perm;
    private static String permOther;
    private static List<String> msg;
    private static List<String> msgP;

    public EnderchestCmd() {
        usage = CommandFile.getStringListPath("Command.EnderChest.Usage");
        perm = CommandFile.getStringPath("Command.EnderChest.Permission.Use");
        permOther = CommandFile.getStringPath("Command.EnderChest.Permission.Other");
        msg = CommandFile.getStringListPath("Command.EnderChest.Message");
        msgP = CommandFile.getStringListPath("Command.EnderChest.MessagePlayer");
        if(!NewSystem.loadedCommands.contains(this)) {
            NewSystem.loadedCommands.add(this);
            NewSystem.getInstance().getCommand("enderchest").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;

            if (NewSystem.hasPermission(p, perm)) {
                if (args.length == 0) {
                    for(String key : msgP) {
                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                    p.openInventory(p.getEnderChest());
                } else if (args.length == 1) {
                    Player t = Bukkit.getPlayer(args[0]);
                    if (t != null) {
                        if (NewSystem.hasPermission(p, permOther)) {
                            for(String key : msg) {
                                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                            }
                            p.openInventory(t.getEnderChest());
                        }else{
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
            sender.sendMessage(SettingsFile.getOnlyPlayerCanExecute());
        }
        return false;
    }
}
