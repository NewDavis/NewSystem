package me.newdavis.spigot.command;
//Plugin by NewDavis

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.file.SavingsFile;
import me.newdavis.spigot.listener.OtherListeners;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpawnCmd implements CommandExecutor, TabCompleter {

    private static List<String> usage;
    private static String perm;
    private static String permEdit;
    private static String permOther;
    private static String permNoDelay;
    private static List<String> msgSet;
    private static List<String> msgAlreadySet;
    private static List<String> msgDeleted;
    private static List<String> msgNotSet;
    private static List<String> msg;
    private static List<String> msgP;
    private static List<String> msgDelay;
    private static int delay;
    private static String countIsOne;
    private static List<String> msgAlreadyInTeleport;
    public static List<String> msgMovedWhileTeleport;

    public SpawnCmd() {
        usage = CommandFile.getStringListPath("Command.Spawn.Usage");
        perm = CommandFile.getStringPath("Command.Spawn.Permission.Use");
        permEdit = CommandFile.getStringPath("Command.Spawn.Permission.Edit");
        permOther = CommandFile.getStringPath("Command.Spawn.Permission.Other");
        permNoDelay = CommandFile.getStringPath("Command.Spawn.Permission.NoDelay");
        msgSet = CommandFile.getStringListPath("Command.Spawn.MessageSpawnSet");
        msgAlreadySet = CommandFile.getStringListPath("Command.Spawn.MessageSpawnAlreadySet");
        msgDeleted = CommandFile.getStringListPath("Command.Spawn.MessageSpawnDelete");
        msgNotSet = CommandFile.getStringListPath("Command.Spawn.MessageSpawnNotSet");
        msg = CommandFile.getStringListPath("Command.Spawn.Message");
        msgP = CommandFile.getStringListPath("Command.Spawn.MessagePlayer");
        msgDelay = CommandFile.getStringListPath("Command.Spawn.DelayedMessage");
        delay = CommandFile.getIntegerPath("Command.Spawn.TeleportDelayInSeconds");
        countIsOne = CommandFile.getStringPath("Command.Spawn.CountIsOne");
        msgAlreadyInTeleport = CommandFile.getStringListPath("Command.Spawn.MessageAlreadyInTeleport");
        msgMovedWhileTeleport = CommandFile.getStringListPath("Command.Spawn.MessageMovedWhileTeleportation");
        if(!NewSystem.loadedCommands.contains(this)) {
            NewSystem.loadedCommands.add(this);
            NewSystem.getInstance().getCommand("spawn").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(args.length == 0) {
                teleportSpawn(p, p);
            }else if(args.length == 1) {
                if(args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("setzen")) {
                    if(NewSystem.hasPermission(p, perm)) {
                        if (NewSystem.hasPermission(p, permEdit)) {
                            setSpawn(p);
                        } else {
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    } else {
                        p.sendMessage(SettingsFile.getNoPerm());
                    }
                }else if(args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("entfernen")) {
                    if(NewSystem.hasPermission(p, perm)) {
                        if (NewSystem.hasPermission(p, permEdit)) {
                            deleteSpawn(p);
                        } else {
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    }else {
                        p.sendMessage(SettingsFile.getNoPerm());
                    }
                }else{
                    if(NewSystem.hasPermission(p, permOther)) {
                        Player t = Bukkit.getPlayer(args[0]);
                        if (t != null) {
                            teleportSpawn(p, t);
                        } else {
                            p.sendMessage(SettingsFile.getOffline());
                        }
                    }else{
                        p.sendMessage(SettingsFile.getNoPerm());
                    }
                }
            }else{
                if(NewSystem.hasPermission(p, perm)) {
                    for(String value : usage) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }else{
                    teleportSpawn(p, p);
                }
            }
        }else{
            sender.sendMessage(SettingsFile.getOnlyPlayerCanExecute());
        }
        return false;
    }

    public static void setSpawn(Player p) {
        if(!SavingsFile.isPathSet("Spawn.World")) {
            String world = p.getWorld().getName();
            double x = p.getLocation().getX();
            double y = p.getLocation().getY();
            double z = p.getLocation().getZ();
            double yaw = p.getLocation().getYaw();
            double pitch = p.getLocation().getPitch();

            SavingsFile.setPath("Spawn.World", world);
            SavingsFile.setPath("Spawn.X", x);
            SavingsFile.setPath("Spawn.Y", y);
            SavingsFile.setPath("Spawn.Z", z);
            SavingsFile.setPath("Spawn.Yaw", yaw);
            SavingsFile.setPath("Spawn.Pitch", pitch);

            for(String value : msgSet) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }else{
            for(String value : msgAlreadySet) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void deleteSpawn(Player p) {
        if(SavingsFile.isPathSet("Spawn.World")) {
            SavingsFile.setPath("Spawn.World", null);
            SavingsFile.setPath("Spawn.X", null);
            SavingsFile.setPath("Spawn.Y", null);
            SavingsFile.setPath("Spawn.Z", null);
            SavingsFile.setPath("Spawn.Yaw", null);
            SavingsFile.setPath("Spawn.Pitch", null);

            for(String value : msgDeleted) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }else{
            for(String value : msgNotSet) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void teleportSpawn(Player p, Player t) {
        if(SavingsFile.isPathSet("Spawn.World")) {
            World world = Bukkit.getWorld(SavingsFile.getStringPath("Spawn.World"));
            double x = SavingsFile.getDoublePath("Spawn.X");
            double y = SavingsFile.getDoublePath("Spawn.Y");
            double z = SavingsFile.getDoublePath("Spawn.Z");
            float yaw = (float) (double) SavingsFile.getDoublePath("Spawn.Yaw");
            float pitch = (float) (double) SavingsFile.getDoublePath("Spawn.Pitch");
            Location loc = new Location(world, x, y, z, yaw, pitch);

            if(p != t) {
                for(String key : msg) {
                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                }
            }
            if(NewSystem.hasPermission(p, permNoDelay)) {
                t.teleport(loc);
                for(String key : msgP) {
                    t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }else{
                teleportCoolDown(t, loc);
            }
        }else{
            for(String value : msgNotSet) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static HashMap<Player, Integer> taskIDs = new HashMap<>();
    
    public static void teleportCoolDown(Player p, Location loc) {
        if (OtherListeners.spawn.contains(p)) {
            for (String value : msgAlreadyInTeleport) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
            return;
        }

        OtherListeners.spawn.add(p);
        final Integer[] seconds = new Integer[]{delay};

        taskIDs.put(p, Bukkit.getScheduler().scheduleSyncRepeatingTask(NewSystem.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (seconds[0] == 1) {
                    for (String key : msgDelay) {
                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Seconds}", countIsOne));
                    }
                    seconds[0]--;
                } else if (seconds[0] >= 1) {
                    for (String key : msgDelay) {
                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Seconds}", String.valueOf(seconds[0])));
                    }
                    seconds[0]--;
                } else if (seconds[0] == 0) {
                    p.teleport(loc);
                    for (String value : msgP) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                    OtherListeners.spawn.remove(p);

                    int taskID = taskIDs.get(p);
                    taskIDs.clear();
                    Bukkit.getScheduler().cancelTask(taskID);
                }
            }
        }, 0, 20));
    }
    
    public static void teleportSpawnJoin(Player p) {
        if(SavingsFile.isPathSet("Spawn.World")) {
            World world = Bukkit.getWorld(SavingsFile.getStringPath("Spawn.World"));
            double x = SavingsFile.getDoublePath("Spawn.X");
            double y = SavingsFile.getDoublePath("Spawn.Y");
            double z = SavingsFile.getDoublePath("Spawn.Z");
            float yaw = (float) (double) SavingsFile.getDoublePath("Spawn.Yaw");
            float pitch = (float) (double) SavingsFile.getDoublePath("Spawn.Pitch");

            Location loc = new Location(world, x, y, z, yaw, pitch);
            p.teleport(loc);
        }else{
            for(String value : msgNotSet) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        List<String> tabCompletions = new ArrayList<>();

        if(sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 1) {
                if(NewSystem.hasPermission(p, permEdit)) {
                    String[] completions = {"set", "delete"};
                    for(String completion : completions) {
                        if(completion.contains(args[0])) {
                            tabCompletions.add(completion);
                        }
                    }
                }
                if(NewSystem.hasPermission(p, permOther)) {
                    for(Player all : Bukkit.getOnlinePlayers()) {
                        if(all.getName().contains(args[0])) {
                            tabCompletions.add(all.getName());
                        }
                    }
                }
            }
        }

        return tabCompletions;
    }
}
