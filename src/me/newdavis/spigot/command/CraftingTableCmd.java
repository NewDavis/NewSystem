package me.newdavis.spigot.command;

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class CraftingTableCmd implements CommandExecutor {

    private static String perm;
    private static List<String> usage;
    private static List<String> message;

    public CraftingTableCmd() {
        perm = CommandFile.getStringPath("Command.CraftingTable.Permission");
        usage = CommandFile.getStringListPath("Command.CraftingTable.Usage");
        message = CommandFile.getStringListPath("Command.CraftingTable.Message");
        if(!NewSystem.loadedCommands.contains(this)) {
            NewSystem.loadedCommands.add(this);
            NewSystem.getInstance().getCommand("craftingtable").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if(args.length > 0) {
                    for(String value : usage) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }else{
                    p.openWorkbench(p.getLocation(), true);
                    for(String value : message) {
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
