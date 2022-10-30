package me.newdavis.spigot.command;
//Plugin by NewDavis

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.file.SavingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class WarpCmd implements CommandExecutor, TabCompleter {

    private static List<String> usage;
    private static String perm;
    private static String permWarp;
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
    private static List<String> msgAlreadyInTp;
    private static List<String> msgMoved;
    private static List<String> listMessage;
    private static String noWarps;

    public WarpCmd() {
        usage = CommandFile.getStringListPath("Command.Warp.Usage");
        perm = CommandFile.getStringPath("Command.Warp.Permission.Use");
        permWarp = CommandFile.getStringPath("Command.Warp.Permission.Teleport");
        permEdit = CommandFile.getStringPath("Command.Warp.Permission.Edit");
        permOther = CommandFile.getStringPath("Command.Warp.Permission.Other");
        permNoDelay = CommandFile.getStringPath("Command.Warp.Permission.NoDelay");
        msgSet = CommandFile.getStringListPath("Command.Warp.MessageWarpSet");
        msgAlreadySet = CommandFile.getStringListPath("Command.Warp.MessageWarpAlreadySet");
        msgDeleted = CommandFile.getStringListPath("Command.Warp.MessageWarpDelete");
        msgNotSet = CommandFile.getStringListPath("Command.Warp.MessageWarpNotSet");
        msg = CommandFile.getStringListPath("Command.Warp.Message");
        msgP = CommandFile.getStringListPath("Command.Warp.MessagePlayer");
        msgDelay = CommandFile.getStringListPath("Command.Warp.DelayedMessage");
        delay = CommandFile.getIntegerPath("Command.Warp.TeleportDelayInSeconds");
        countIsOne = CommandFile.getStringPath("Command.Warp.CountIsOne");
        msgAlreadyInTp = CommandFile.getStringListPath("Command.Warp.MessageAlreadyInTeleport");
        msgMoved = CommandFile.getStringListPath("Command.Warp.MessageMovedWhileTeleportation");
        listMessage = CommandFile.getStringListPath("Command.Warp.WarpListFormat");
        noWarps = CommandFile.getStringPath("Command.Warp.MessageNoWarps");
        if(!NewSystem.loadedCommands.contains(this)) {
            NewSystem.loadedCommands.add(this);
            NewSystem.getInstance().getCommand("warp").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if (args.length == 1) {
                    if(args[0].equalsIgnoreCase("list")) {
                        sendList(p);
                    }else{
                        String warp = args[0];
                        if(NewSystem.hasPermission(p, permWarp.replace("{Warp}", warp))) {
                            teleportWarp(p, p, warp);
                        }else{
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    }
                }else if(args.length == 2) {
                    if (args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("setzen")) {
                        if (NewSystem.hasPermission(p, permEdit)) {
                            setWarp(p, args[0]);
                        } else {
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    } else if (args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("entfernen")) {
                        if (NewSystem.hasPermission(p, permEdit)) {
                            deleteWarp(p, args[0]);
                        } else {
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    }else {
                        String warp = args[0];
                        if (NewSystem.hasPermission(p, permOther)) {
                            if (NewSystem.hasPermission(p, permWarp.replace("{Warp}", warp))) {
                                Player t = Bukkit.getPlayer(args[1]);
                                if (t != null) {
                                    teleportWarp(p, t, warp);
                                } else {
                                    p.sendMessage(SettingsFile.getOffline());
                                }
                            } else {
                                p.sendMessage(SettingsFile.getNoPerm());
                            }
                        } else {
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
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

    public static void setWarp(Player p, String warp) {
        Collection<String> warps = getWarps();
        if(!warps.contains(warp)) {
            SavingsFile.setPath("Warp." + warp + ".World", p.getWorld().getName());
            SavingsFile.setPath("Warp." + warp + ".X", p.getLocation().getX());
            SavingsFile.setPath("Warp." + warp + ".Y", p.getLocation().getY());
            SavingsFile.setPath("Warp." + warp + ".Z", p.getLocation().getZ());
            SavingsFile.setPath("Warp." + warp + ".Yaw", p.getLocation().getYaw());
            SavingsFile.setPath("Warp." + warp + ".Pitch", p.getLocation().getPitch());

            for(String key : msgSet) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Warp}", warp));
            }
        }else{
            for(String key : msgAlreadySet) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Warp}", warp));
            }
        }
    }

    public static void deleteWarp(Player p, String warp) {
        Collection<String> warps = getWarps();
        if(warps.contains(warp)) {
            SavingsFile.setPath("Warp." + warp + ".World", null);
            SavingsFile.setPath("Warp." + warp + ".X", null);
            SavingsFile.setPath("Warp." + warp + ".Y", null);
            SavingsFile.setPath("Warp." + warp + ".Z", null);
            SavingsFile.setPath("Warp." + warp + ".Yaw", null);
            SavingsFile.setPath("Warp." + warp + ".Pitch", null);

            for(String key : msgDeleted) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Warp}", warp));
            }
        }else{
            for(String key : msgNotSet) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Warp}", warp));
            }
        }
    }

    public static void teleportWarp(Player p, Player t, String warp) {
        if(SavingsFile.isPathSet("Warp." + warp + ".World")) {
            World world = Bukkit.getWorld(SavingsFile.getStringPath("Warp." + warp + ".World"));
            double x = SavingsFile.getDoublePath("Warp." + warp + ".X");
            double y = SavingsFile.getDoublePath("Warp." + warp + ".Y");
            double z = SavingsFile.getDoublePath("Warp." + warp + ".Z");
            float yaw = (float) (double) SavingsFile.getDoublePath("Warp." + warp + ".Yaw");
            float pitch = (float) (double) SavingsFile.getDoublePath("Warp." + warp + ".Pitch");

            Location loc = new Location(world, x, y, z, yaw, pitch);

            if(p != t) {
                for(String key : msg) {
                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Warp}", warp));
                }
            }
            if(NewSystem.hasPermission(p, permNoDelay)) {
                t.teleport(loc);
                for(String key : msgP) {
                    t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Warp}", warp));
                }
            }else{
                teleportCoolDown(t, loc, warp);
            }
        }else{
            for(String value : msgNotSet) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static HashMap<Player, Integer> coolDown = new HashMap<>();

    public static void teleportCoolDown(Player p, Location loc, String warp) {
        if(!coolDown.containsKey(p)) {
            final Integer[] seconds = new Integer[]{delay};
            int x = p.getLocation().getBlockX();
            int z = p.getLocation().getBlockZ();

            coolDown.put(p, Bukkit.getScheduler().scheduleSyncRepeatingTask(NewSystem.getInstance(), new Runnable() {
                @Override
                public void run() {
                    if (checkLocation(p, x, z)) {
                        if (seconds[0] == 1) {
                            for(String key : msgDelay) {
                                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Seconds}", countIsOne));
                            }
                            seconds[0]--;
                        } else if (seconds[0] >= 1) {
                            for(String key : msgDelay) {
                                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Seconds}", String.valueOf(seconds[0])));
                            }
                            seconds[0]--;
                        } else if (seconds[0] == 0) {

                            p.teleport(loc);
                            for(String key : msgP) {
                                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Warp}", warp));
                            }

                            int taskID = coolDown.get(p);
                            coolDown.remove(p);
                            Bukkit.getScheduler().cancelTask(taskID);
                        }
                    }
                }
            }, 0, 20));
        }else{
            for(String value : msgAlreadyInTp) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static boolean checkLocation(Player p, int x, int z) {
        if(p.getLocation().getBlockX() == x) {
            if(p.getLocation().getBlockZ() == z) {
                return true;
            }else{
                for(String value : msgMoved) {
                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }else{
            for(String value : msgMoved) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
        int taskID = coolDown.get(p);
        coolDown.remove(p);
        Bukkit.getScheduler().cancelTask(taskID);
        return false;
    }

    public static Collection<String> getWarps() {
        if(SavingsFile.isPathSet("Warp")) {
            Collection<String> keys = SavingsFile.yaml.getConfigurationSection("Warp").getKeys(false);
            keys.removeIf(key -> !SavingsFile.isPathSet("Warp." + key + ".World"));

            return keys;
        }
        return new ArrayList<>();
    }

    public static void sendList(Player p) {
        Collection<String> warps = getWarps();
        for (String message : listMessage) {
            if (msg.contains("{Warps}")) {
                String warpString = "";
                if (warps.size() == 0) {
                    warpString = noWarps.replace("{Prefix}", SettingsFile.getPrefix());
                    p.sendMessage(warpString);
                }else {
                    for(String warp : warps) {
                        warpString += warp + " ";
                    }
                    p.sendMessage(message.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Warps}", warpString));
                }
            } else {
                p.sendMessage(message.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        List<String> tabCompletions = new ArrayList<>();

        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if (args.length == 1) {
                    String[] completions = {"list"};
                    for (String completion : completions) {
                        if (completion.contains(args[0])) {
                            tabCompletions.add(completion);
                        }
                    }

                    for (String warp : getWarps()) {
                        if (warp.contains(args[0])) {
                            if (NewSystem.hasPermission(p, permWarp.replace("{Warp}", warp))) {
                                tabCompletions.add(warp);
                            }
                        }
                    }
                } else if (args.length == 2) {
                    if(NewSystem.hasPermission(p, permEdit)) {
                        String[] completions = {"set", "remove"};
                        for (String completion : completions) {
                            if (completion.contains(args[1])) {
                                tabCompletions.add(completion);
                            }
                        }

                        if (!(args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("list"))) {
                            for (Player all : Bukkit.getOnlinePlayers()) {
                                if (all.getName().contains(args[1])) {
                                    tabCompletions.add(all.getName());
                                }
                            }
                        }
                    }
                }
            }
        }

        return tabCompletions;
    }
}
