package me.newdavis.spigot.command;
//Plugin by NewDavis

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import me.newdavis.spigot.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GiveAllCmd implements CommandExecutor {

    private static List<String> usage;
    private static String perm;
    private static List<String> msg;
    private static List<String> msgP;
    private static List<String> msgAir;

    public void init() {
        usage = CommandFile.getStringListPath("Command.GiveAll.Usage");
        perm = CommandFile.getStringPath("Command.GiveAll.Permission");
        msg = CommandFile.getStringListPath("Command.GiveAll.Message");
        msgP = CommandFile.getStringListPath("Command.GiveAll.MessagePlayer");
        msgAir = CommandFile.getStringListPath("Command.GiveAll.MessageItemIsAir");
        NewSystem.getInstance().getCommand("giveall").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if(args.length == 0) {
                    ItemStack item = new ItemStack(ItemBuilder.getItemInHand(p));
                    item.setAmount(1);
                    if(!ItemBuilder.getNameOfMaterial(ItemBuilder.getMaterialOfItemStack(item)).equalsIgnoreCase("AIR")) {
                        if(item.hasItemMeta()) {
                            if (item.getItemMeta().hasDisplayName()) {
                                for (Player all : Bukkit.getOnlinePlayers()) {
                                    if (all != p) {
                                        all.getInventory().addItem(item);
                                        for(String key : msgP) {
                                            all.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Amount}", "1").replace("{Item}", item.getItemMeta().getDisplayName()));
                                        }
                                    }
                                }
                                for(String key : msg) {
                                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Amount}", "1").replace("{Item}", item.getItemMeta().getDisplayName()));
                                }
                            } else {
                                for(Player all : Bukkit.getOnlinePlayers()) {
                                    if(all != p) {
                                        all.getInventory().addItem(item);
                                        for(String key : msgP) {
                                            all.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Amount}", "1").replace("{Item}", ItemBuilder.getNameOfMaterial(ItemBuilder.getMaterialOfItemStack(item))));
                                        }
                                    }
                                }
                                for(String key : msg) {
                                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Amount}", "1").replace("{Item}", ItemBuilder.getNameOfMaterial(ItemBuilder.getMaterialOfItemStack(item))));
                                }
                            }
                        } else {
                            for(Player all : Bukkit.getOnlinePlayers()) {
                                if(all != p) {
                                    all.getInventory().addItem(item);
                                    for(String key : msgP) {
                                        all.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Amount}", "1").replace("{Item}", ItemBuilder.getNameOfMaterial(ItemBuilder.getMaterialOfItemStack(item))));
                                    }
                                }
                            }
                            for(String key : msg) {
                                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Amount}", "1").replace("{Item}", ItemBuilder.getNameOfMaterial(ItemBuilder.getMaterialOfItemStack(item))));
                            }
                        }
                    } else {
                        for(String value : msgAir) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                }else if(args.length == 1) {
                    try {
                        int amount = Integer.parseInt(args[0]);
                        ItemStack item = new ItemStack(ItemBuilder.getItemInHand(p));
                        item.setAmount(amount);
                        if(!ItemBuilder.getNameOfMaterial(ItemBuilder.getMaterialOfItemStack(item)).equalsIgnoreCase("AIR")) {
                            if(item.hasItemMeta()) {
                                if (item.getItemMeta().hasDisplayName()) {
                                    for (Player all : Bukkit.getOnlinePlayers()) {
                                        if (all != p) {
                                            all.getInventory().addItem(item);
                                            for(String key : msgP) {
                                                all.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Amount}", String.valueOf(amount)).replace("{Item}", item.getItemMeta().getDisplayName()));
                                            }
                                        }
                                    }
                                    for(String key : msg) {
                                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Amount}", String.valueOf(amount)).replace("{Item}", item.getItemMeta().getDisplayName()));
                                    }
                                } else {
                                    for(Player all : Bukkit.getOnlinePlayers()) {
                                        if(all != p) {
                                            all.getInventory().addItem(item);
                                            for(String key : msgP) {
                                                all.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Amount}", String.valueOf(amount)).replace("{Item}", ItemBuilder.getNameOfMaterial(ItemBuilder.getMaterialOfItemStack(item))));
                                            }
                                        }
                                    }
                                    for(String key : msg) {
                                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Amount}", String.valueOf(amount)).replace("{Item}", ItemBuilder.getNameOfMaterial(ItemBuilder.getMaterialOfItemStack(item))));
                                    }
                                }
                            } else {
                                for(Player all : Bukkit.getOnlinePlayers()) {
                                    if(all != p) {
                                        all.getInventory().addItem(item);
                                        for(String key : msgP) {
                                            all.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Amount}", String.valueOf(amount)).replace("{Item}", ItemBuilder.getNameOfMaterial(ItemBuilder.getMaterialOfItemStack(item))));
                                        }
                                    }
                                }
                                for(String key : msg) {
                                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Amount}", String.valueOf(amount)).replace("{Item}", ItemBuilder.getNameOfMaterial(ItemBuilder.getMaterialOfItemStack(item))));
                                }
                            }
                        } else {
                            for(String value : usage) {
                                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        }
                    }catch(NumberFormatException e) {
                        for(String value : usage) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                        e.printStackTrace();
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
