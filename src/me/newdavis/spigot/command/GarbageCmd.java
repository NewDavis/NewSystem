package me.newdavis.spigot.command;

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class GarbageCmd implements CommandExecutor {

    private static List<String> usage;
    private static String perm;
    private static int size;
    public static String title;
    public static List<String> close;

    public void init() {
        usage = CommandFile.getStringListPath("Command.Garbage.Usage");
        perm = CommandFile.getStringPath("Command.Garbage.Permission");
        size = CommandFile.getIntegerPath("Command.Garbage.Size");
        title = CommandFile.getStringPath("Command.Garbage.Title");
        close = CommandFile.getStringListPath("Command.Garbage.MessageClose");
        NewSystem.getInstance().getCommand("garbage").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if(args.length == 0) {
                    Inventory inventory = Bukkit.createInventory(null, size, title.replace("{Prefix}", SettingsFile.getPrefix()));
                    p.openInventory(inventory);
                }else{
                    for(String key : usage) {
                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
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
