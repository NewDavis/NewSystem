package me.newdavis.spigot.command;

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.file.SavingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import me.newdavis.spigot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DupeCmd implements CommandExecutor {

    private static String perm;
    private static String permNoDelay;
    private static List<String> usage;
    private static long delay;
    private static String delayFormat;
    private static List<String> blockedMaterials;
    private static List<String> messageItemIsAir;
    private static List<String> message;
    private static List<String> messageItemCanNotDuped;
    private static List<String> messageInventoryIsFull;
    private static List<String> delayMessage;

    public DupeCmd() {
        perm = CommandFile.getStringPath("Command.Dupe.Permission.Use");
        permNoDelay = CommandFile.getStringPath("Command.Dupe.Permission.NoDelay");
        usage = CommandFile.getStringListPath("Command.Dupe.Usage");
        delay = CommandFile.getLongPath("Command.Dupe.DelayInTicks");
        delayFormat = CommandFile.getStringPath("Command.Dupe.DelayFormat");
        blockedMaterials = CommandFile.getStringListPath("Command.Dupe.BlockedMaterials");
        messageItemIsAir = CommandFile.getStringListPath("Command.Dupe.MessageItemIsAir");
        message = CommandFile.getStringListPath("Command.Dupe.Message");
        messageItemCanNotDuped = CommandFile.getStringListPath("Command.Dupe.MessageItemCanNotDuped");
        messageInventoryIsFull = CommandFile.getStringListPath("Command.Dupe.MessageInventoryIsFull");
        delayMessage = CommandFile.getStringListPath("Command.Dupe.MessageDelay");
        if(!NewSystem.loadedCommands.contains(this)) {
            NewSystem.loadedCommands.add(this);
            NewSystem.getInstance().getCommand("dupe").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if(args.length == 0) {
                    dupe(p);
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

    public static boolean canDupe(Player p) {
        if(NewSystem.hasPermission(p, permNoDelay)) {
            return true;
        }else{
            long end = SavingsFile.getLongPath("Dupe." + p.getUniqueId() + ".End");
            long now = System.currentTimeMillis();

            return (end + delay) - now <= 0;
        }
    }

    public static String getDelay(Player p) {
        if(!canDupe(p)) {
            long end = SavingsFile.getLongPath("Dupe." + p.getUniqueId() + ".End");
            long now = System.currentTimeMillis();
            int rest = (int) ((end + delay) - now);

            int days = rest/1000/60/60/24;
            rest = rest-(days*1000*60*60*24);
            int hours = (rest/1000/60/60);
            rest = rest-(hours*1000*60*60);
            int minutes = (rest/1000/60);
            rest = rest-(minutes*1000*60);
            int seconds = (rest/1000);

            return delayFormat.replace("{Days}", String.valueOf(days))
                    .replace("{Hours}", String.valueOf(hours))
                    .replace("{Minutes}", String.valueOf(minutes))
                    .replace("{Seconds}", String.valueOf(seconds));
        }
        return delayFormat;
    }

    public static boolean canDupeThisItem(ItemStack item) {
        boolean canDupe = true;
        Material material = ItemBuilder.getMaterialOfItemStack(item);
        for(String mat : blockedMaterials) {
            Material material2 = ItemBuilder.getMaterial(mat);
            if(material2 != null) {
                if(material == material2) {
                    canDupe = false;
                }
            }
        }
        return canDupe;
    }

    public static boolean isInventoryFull(Player p) {
        for(ItemStack item : ItemBuilder.getInventory(p)) {
            if(item == null || ItemBuilder.getMaterialOfItemStack(item) == Material.AIR) {
                return false;
            }
        }
        return true;
    }

    public static void dupe(Player p) {
        if(canDupe(p)) {
            ItemStack item = ItemBuilder.getItemInHand(p);
            if(ItemBuilder.getMaterialOfItemStack(item) == Material.AIR) {
                for(String value : messageItemIsAir) {
                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
                return;
            }
            if(!isInventoryFull(p)) {
                if(canDupeThisItem(item)) {
                    if((item.getAmount()*2) <= item.getMaxStackSize()) {
                        item.setAmount(item.getAmount()*2);
                    }else {
                        p.getInventory().addItem(item);
                    }
                    for(String value : message) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }

                    if(!NewSystem.hasPermission(p, permNoDelay)) {
                        long now = System.currentTimeMillis();
                        SavingsFile.setPath("Dupe." + p.getUniqueId() + ".End", now);
                    }
                }else{
                    for(String value : messageItemCanNotDuped) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }else{
                for(String value : messageInventoryIsFull) {
                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }else{
            for(String msg : delayMessage) {
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Delay}", getDelay(p)));
            }
        }
    }
}
