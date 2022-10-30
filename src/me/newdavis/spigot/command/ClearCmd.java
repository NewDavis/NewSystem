package me.newdavis.spigot.command;

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ClearCmd implements CommandExecutor {

    private static List<String> usage;
    private static String perm;
    private static String permOther;
    private static List<String> msg;
    private static List<String> msgP;

    public ClearCmd() {
        usage = CommandFile.getStringListPath("Command.Clear.Usage");
        perm = CommandFile.getStringPath("Command.Clear.Permission.Use");
        permOther = CommandFile.getStringPath("Command.Clear.Permission.Other");
        msg = CommandFile.getStringListPath("Command.Clear.Message");
        msgP = CommandFile.getStringListPath("Command.Clear.MessagePlayer");
        if(!NewSystem.loadedCommands.contains(this)) {
            NewSystem.loadedCommands.add(this);
            NewSystem.getInstance().getCommand("clear").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if(args.length == 0) {
                    clearInventory(p);
                    for(String key : msgP) {
                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }else if(args.length == 1) {
                    if(NewSystem.hasPermission(p, permOther)) {
                        Player t = Bukkit.getPlayer(args[0]);
                        if(t != null) {
                            if(p != t) {
                                clearInventory(t);
                                for(String key : msg) {
                                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                                }
                                for(String key : msgP) {
                                    t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                            }else{
                                clearInventory(p);
                                for(String key : msgP) {
                                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
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

    private static void clearInventory(Player p) {
        p.getInventory().setBoots(new ItemStack(Material.AIR));
        p.getInventory().setLeggings(new ItemStack(Material.AIR));
        p.getInventory().setChestplate(new ItemStack(Material.AIR));
        p.getInventory().setHelmet(new ItemStack(Material.AIR));

        p.getInventory().clear();
    }
}
