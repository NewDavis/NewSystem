package me.newdavis.spigot.command;

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ClearLagCmd implements CommandExecutor, TabCompleter {

    private static String perm;
    private static List<String> usage;
    private static List<String> message;
    private static int minutes;
    private static boolean enabled;
    private static boolean globalMessage;
    private static List<String> deletedMessage;

    public ClearLagCmd() {
        perm = CommandFile.getStringPath("Command.ClearLag.Permission");
        usage = CommandFile.getStringListPath("Command.ClearLag.Usage");
        message = CommandFile.getStringListPath("Command.ClearLag.Message");
        minutes = CommandFile.getIntegerPath("Command.ClearLag.Auto.DelayInMinutes");
        enabled = CommandFile.getBooleanPath("Command.ClearLag.Auto.Enabled");
        globalMessage = CommandFile.getBooleanPath("Command.ClearLag.Auto.Message.Enabled");
        deletedMessage = CommandFile.getStringListPath("Command.ClearLag.Auto.Message.Deleted");
        if(!NewSystem.loadedCommands.contains(this)) {
            NewSystem.loadedCommands.add(this);
            NewSystem.getInstance().getCommand("clearlag").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (NewSystem.hasPermission(p, perm)) {
                if (args.length == 0) {
                    String world = p.getWorld().getName();
                    int removedEntities = removeEntities(world);

                    for (String msg : message) {
                        p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Amount}", String.valueOf(removedEntities)));
                    }
                } else if (args.length == 1) {
                    String world = args[0];
                    int removedEntities = removeEntities(world);

                    for (String msg : message) {
                        p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Amount}", String.valueOf(removedEntities)));
                    }
                } else {
                    for (String value : usage) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
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

    public static Integer removeEntities(String world) {
        int removedEntities = 0;
        World w = Bukkit.getWorld(world);
        if (w != null) {
            for (Entity entity : w.getEntities()) {
                if (entity instanceof Item) {
                    Item item = (Item) entity;
                    removedEntities = removedEntities + item.getItemStack().getAmount();
                    entity.remove();
                }
            }
        }
        return removedEntities;
    }

    private static int seconds = 60;

    public static void startAutoClearLag() {
        if (enabled) {
            seconds--;
            if (seconds <= 0) {
                minutes--;
                seconds = 60;

                if (minutes > 0) {
                    if (globalMessage) {
                        if (CommandFile.isPathSet("Command.ClearLag.Auto.Message.MinutesLeft." + minutes)) {
                            List<String> minutesLeftMessage = CommandFile.getStringListPath("Command.ClearLag.Auto.Message.MinutesLeft." + minutes);
                            for (String msg : minutesLeftMessage) {
                                for (Player all : Bukkit.getOnlinePlayers()) {
                                    all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                            }
                        }
                    }
                } else {
                    int amount = 0;
                    for (World world : Bukkit.getWorlds()) {
                        amount += removeEntities(world.getName());
                    }
                    if (globalMessage) {
                        for (String msg : deletedMessage) {
                            for (Player all : Bukkit.getOnlinePlayers()) {
                                all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Amount}", String.valueOf(amount)));
                            }
                        }
                    }
                    seconds = 60;
                    minutes = CommandFile.getIntegerPath("Command.ClearLag.Auto.DelayInMinutes");
                }
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        List<String> tabCompletions = new ArrayList<>();

        if (args.length != 0) {
            return tabCompletions;
        }

        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (NewSystem.hasPermission(p, perm)) {
                for (World world : Bukkit.getWorlds()) {
                    String completion = world.getName();
                    if (completion.contains(args[0])) {
                        tabCompletions.add(completion);
                    }
                }
            }
        } else {
            for (World world : Bukkit.getWorlds()) {
                String completion = world.getName();
                if (completion.contains(args[0])) {
                    tabCompletions.add(completion);
                }
            }
        }

        return tabCompletions;
    }
}
