package me.newdavis.spigot.command;

import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SavingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class BackpackCmd implements CommandExecutor {

    private static List<String> usage;
    private static String perm;
    private static String permOther;
    private static int size;
    public static String title;
    public static String titleOther;
    private static List<String> message;
    private static List<String> messageOther;
    public static List<String> messageSaved;
    public static List<String> messageSavedOther;

    public static HashMap<Player, Inventory> playerBackpack = new HashMap<>();
    public static HashMap<Player, OfflinePlayer> backpack = new HashMap<>();

    public void init() {
        usage = CommandFile.getStringListPath("Command.Backpack.Usage");
        perm = CommandFile.getStringPath("Command.Backpack.Permission.Use");
        permOther = CommandFile.getStringPath("Command.Backpack.Permission.Other");
        size = CommandFile.getIntegerPath("Command.Backpack.Size");
        title = CommandFile.getStringPath("Command.Backpack.Title");
        titleOther = CommandFile.getStringPath("Command.Backpack.TitleOther");
        message = CommandFile.getStringListPath("Command.Backpack.Message");
        messageOther = CommandFile.getStringListPath("Command.Backpack.MessageOther");
        messageSaved = CommandFile.getStringListPath("Command.Backpack.MessageSaved");
        messageSavedOther = CommandFile.getStringListPath("Command.Backpack.MessageSavedOther");
        NewSystem.getInstance().getCommand("backpack").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if(args.length == 0) {
                    if(openBackpack(p, p, title, size)) {
                        for(String value : message) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                }else if(args.length == 1) {
                    if(NewSystem.hasPermission(p, permOther)) {
                        OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                        if(p == t) {
                            if(openBackpack(p, p, title, size)) {
                                for(String value : message) {
                                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                            }
                        }else {
                            if (openBackpack(p, t, titleOther.replace("{Player}", t.getName()), size)) {
                                for (String msg : messageOther) {
                                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)));
                                }
                            }
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

    public boolean openBackpack(Player p, OfflinePlayer t, String title, int size) {
        if(SavingsFile.isPathSet("Backpack." + t.getUniqueId())) {
            Inventory inventory = Bukkit.createInventory(null, size, title);
            HashMap<Integer, ItemStack> backpackItems = getBackpackItems(t);
            for(int slot : backpackItems.keySet()) {
                if(inventory.getSize() >= slot) {
                    inventory.setItem(slot, backpackItems.get(slot));
                }
            }
            p.openInventory(inventory);
            if(p != t) {
                backpack.put(p, t);
            }
            return true;
        }else{
            if(t.isOnline()) {
                Inventory inventory;
                HashMap<Integer, ItemStack> backpackItems;
                if(!playerBackpack.containsKey(t.getPlayer())) {
                    inventory = Bukkit.createInventory(null, size, title);
                    backpackItems = getBackpackItems(t);
                    for(int slot : backpackItems.keySet()) {
                        if(inventory.getSize() >= slot) {
                            inventory.setItem(slot, backpackItems.get(slot));
                        }
                    }
                }else{
                    inventory = playerBackpack.get(t.getPlayer());
                }
                p.openInventory(inventory);
                if(p != t) {
                    backpack.put(p, t);
                }
                return true;
            }else{
                p.sendMessage(SettingsFile.getOffline());
            }
        }
        return false;
    }

    public HashMap<Integer, ItemStack> getBackpackItems(OfflinePlayer p) {
        HashMap<Integer, ItemStack> items = new HashMap<>();
        List<String> itemSlots = SavingsFile.getConfigurationSection("Backpack." + p.getUniqueId() + ".Slot");
        for(String slotS : itemSlots) {
            int slot = Integer.parseInt(slotS);
            ItemStack item = SavingsFile.getItemStack("Backpack." + p.getUniqueId() + ".Slot." + slotS);
            items.put(slot, item);
        }
        return items;
    }
}
