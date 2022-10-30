package me.newdavis.spigot.command;

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import me.newdavis.spigot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class HatCmd implements CommandExecutor {

    private static String perm;
    private static List<String> usage;
    private static List<String> message;
    private static List<String> itemIsAirMessage;

    public HatCmd() {
        perm = CommandFile.getStringPath("Command.Hat.Permission");
        usage = CommandFile.getStringListPath("Command.Hat.Usage");
        message = CommandFile.getStringListPath("Command.Hat.Message");
        itemIsAirMessage = CommandFile.getStringListPath("Command.Hat.ItemIsAir");
        if(!NewSystem.loadedCommands.contains(this)) {
            NewSystem.loadedCommands.add(this);
            NewSystem.getInstance().getCommand("hat").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;

            if(NewSystem.hasPermission(p, perm)) {
                if(args.length > 0) {
                    for(String msg : usage) {
                        p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }else{
                    ItemStack item = ItemBuilder.getItemInHand(p);
                    if(item != null && item.getType() != Material.AIR) {
                        ItemStack helmet = null;
                        if(p.getInventory().getHelmet() != null && p.getInventory().getHelmet().getType() != Material.AIR) {
                            helmet = p.getInventory().getHelmet();
                        }

                        p.getInventory().setHelmet(item);
                        p.getInventory().setItem(p.getInventory().getHeldItemSlot(), helmet);

                        for(String msg : message) {
                            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Item}", (item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().name())));
                        }
                    }else{
                        for(String msg : itemIsAirMessage) {
                            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
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
