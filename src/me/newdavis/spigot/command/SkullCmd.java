package me.newdavis.spigot.command;

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.util.ItemBuilder;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SkullCmd implements CommandExecutor {

    private static List<String> usage;
    private static String perm;
    private static String permOther;
    private static String skullName;
    private static List<String> msg;
    private static List<String> msgP;

    public void init() {
        usage = CommandFile.getStringListPath("Command.Skull.Usage");
        perm = CommandFile.getStringPath("Command.Skull.Permission.Use");
        permOther = CommandFile.getStringPath("Command.Skull.Permission.Other");
        skullName = CommandFile.getStringPath("Command.Skull.Name");
        msg = CommandFile.getStringListPath("Command.Skull.Message");
        msgP = CommandFile.getStringListPath("Command.Skull.MessageP");
        NewSystem.getInstance().getCommand("skull").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if (args.length == 0) {
                    for(String value : usage) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                } else if (args.length == 1) {
                    OfflinePlayer skullOwner = Bukkit.getOfflinePlayer(args[0]);
                    ItemStack skull = ItemBuilder.getSkull(skullOwner.getName(), skullName.replace("{SkullOwner}", skullOwner.getName()), new String[]{""}, 1);
                    p.getInventory().addItem(skull);
                    for(String key : msgP) {
                        p.sendMessage(key.replace("{SkullOwner}", skullOwner.getName()).replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                } else if (args.length == 2) {
                    if (NewSystem.hasPermission(p, permOther)) {
                        OfflinePlayer skullOwner = Bukkit.getOfflinePlayer(args[0]);
                        Player t = Bukkit.getPlayer(args[1]);
                        if (t != null) {
                            ItemStack skull = ItemBuilder.getSkull(skullOwner.getName(), skullName.replace("{SkullOwner}", skullOwner.getName()), new String[]{""}, 1);
                            p.getInventory().addItem(skull);
                            for(String key : msgP) {
                                t.sendMessage(key.replace("{SkullOwner}", skullOwner.getName()).replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                            for(String key : msg) {
                                p.sendMessage(key.replace("{SkullOwner}", skullOwner.getName()).replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)));
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
            }
        }else{
            sender.sendMessage(SettingsFile.getOnlyPlayerCanExecute());
        }
        return false;
    }
}
