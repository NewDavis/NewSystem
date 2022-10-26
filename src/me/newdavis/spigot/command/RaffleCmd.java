package me.newdavis.spigot.command;

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import me.newdavis.spigot.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class RaffleCmd implements CommandExecutor {

    private static List<String> usage;
    private static String perm;
    private static List<String> msgAir;
    private static List<String> msgStarted;
    private static List<String> msgChoose;
    private static List<String> msgSelected;

    public void init() {
        usage = CommandFile.getStringListPath("Command.GiveAll.Usage");
        perm = CommandFile.getStringPath("Command.Raffle.Permission");
        msgAir = CommandFile.getStringListPath("Command.Raffle.MessageItemIsAir");
        msgStarted = CommandFile.getStringListPath("Command.Raffle.MessageStarted");
        msgChoose = CommandFile.getStringListPath("Command.Raffle.MessageChoose");
        msgSelected = CommandFile.getStringListPath("Command.Raffle.MessageSelected");
        NewSystem.getInstance().getCommand("raffle").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (NewSystem.hasPermission(p, perm)) {
                if (args.length == 0) {
                    ItemStack item = new ItemStack(ItemBuilder.getItemInHand(p));
                    item.setAmount(1);
                    if (!ItemBuilder.getNameOfMaterial(ItemBuilder.getMaterialOfItemStack(item)).equalsIgnoreCase("AIR")) {
                        if (item.hasItemMeta()) {
                            raffle(p, item, 1, item.getItemMeta().hasDisplayName());
                        } else {
                            raffle(p, item, 1, false);
                        }
                    } else {
                        for (String value : msgAir) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                } else if (args.length == 1) {
                    try {
                        int amount = Integer.parseInt(args[0]);
                        ItemStack item = new ItemStack(ItemBuilder.getItemInHand(p));
                        item.setAmount(amount);
                        if (!ItemBuilder.getNameOfMaterial(ItemBuilder.getMaterialOfItemStack(item)).equalsIgnoreCase("AIR")) {
                            if (item.hasItemMeta()) {
                                raffle(p, item, amount, item.getItemMeta().hasDisplayName());
                            } else {
                                raffle(p, item, amount, false);
                            }
                        } else {
                            for (String msg : usage) {
                                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        }
                    } catch (NumberFormatException e) {
                        for (String msg : usage) {
                            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                        e.printStackTrace();
                    }
                } else {
                    for (String msg : usage) {
                        p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            } else {
                p.sendMessage(SettingsFile.getNoPerm());
            }
        } else {
            sender.sendMessage(SettingsFile.getOnlyPlayerCanExecute());
        }
        return false;
    }

    private void raffle(Player p, ItemStack item, int amount, boolean itemMeta) {
        if(p.getItemInHand().getAmount()-amount > 0) {
            p.getItemInHand().setAmount(p.getItemInHand().getAmount()-1);
        }else{
            p.getInventory().setItem(p.getInventory().getHeldItemSlot(), new ItemStack(Material.AIR));
        }
        item.setAmount(amount);

        for(String msg : msgStarted) {
            Bukkit.broadcastMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Amount}", String.valueOf(amount)).replace("{Item}", (itemMeta && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : ItemBuilder.getNameOfMaterial(ItemBuilder.getMaterialOfItemStack(item)))));
        }
        for(String key : msgChoose) {
            Bukkit.broadcastMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Amount}", String.valueOf(amount)).replace("{Item}", (itemMeta && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : ItemBuilder.getNameOfMaterial(ItemBuilder.getMaterialOfItemStack(item)))));
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(NewSystem.getInstance(), new Runnable() {
            @Override
            public void run() {
                int count = 0;
                int random = new Random().nextInt(Bukkit.getOnlinePlayers().size());
                OfflinePlayer winner = null;
                for(Player all : Bukkit.getOnlinePlayers()) {
                    if(count == random) {
                        all.getInventory().addItem(item);
                        winner = all;
                    }
                    count++;
                }
                for(Player all : Bukkit.getOnlinePlayers()) {
                    for (String key : msgSelected) {
                        all.sendMessage(key
                                .replace("{Prefix}", SettingsFile.getPrefix())
                                .replace("{Amount}", String.valueOf(amount))
                                .replace("{Item}", (itemMeta && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : ItemBuilder.getNameOfMaterial(ItemBuilder.getMaterialOfItemStack(item))))
                                .replace("{Player}", NewSystem.getName(winner)));
                    }
                }
            }
        }, 20*3);
    }

}
