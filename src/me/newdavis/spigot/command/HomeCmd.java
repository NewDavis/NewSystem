package me.newdavis.spigot.command;

import me.newdavis.manager.NewPermManager;
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
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeCmd implements CommandExecutor, TabCompleter {

    private static String perm;
    private static String permNoDelay;
    private static List<String> usage;
    private static List<String> msgNoHomes;
    private static String listFormat;
    private static List<String> msgList;
    private static List<String> msgTp;
    private static int delay;
    private static List<String> msgAlreadyInTelport;
    private static String countIsOne;
    private static List<String> msgCooldown;
    private static List<String> msgHomeNotExist;
    private static List<String> msgMovedWhileTeleport;
    private static List<String> messageHomeCreated;
    private static List<String> messageAlreadyCreated;
    private static List<String> messageCanNotCreateMoreHomes;
    private static List<String> messageHomeDeleted;

    public HomeCmd() {
        perm = CommandFile.getStringPath("Command.Home.Permission.Use");
        permNoDelay = CommandFile.getStringPath("Command.Home.Permission.NoDelay");
        usage = CommandFile.getStringListPath("Command.Home.Usage");
        msgNoHomes = CommandFile.getStringListPath("Command.Home.MessageNoHomes");
        listFormat = CommandFile.getStringPath("Command.Home.MessageHomesFormat");
        msgList = CommandFile.getStringListPath("Command.Home.MessageHomeList");
        msgAlreadyInTelport = CommandFile.getStringListPath("Command.Home.MessageHomeAlreadyInTeleport");
        countIsOne = CommandFile.getStringPath("Command.Home.HomeTeleportCountIsOne");
        msgCooldown = CommandFile.getStringListPath("Command.Home.MessageHomeTeleportDelay");
        msgHomeNotExist = CommandFile.getStringListPath("Command.Home.MessageHomeNotExist");
        msgMovedWhileTeleport = CommandFile.getStringListPath("Command.Home.MessageHomeMovedWhileTeleport");
        messageHomeCreated = CommandFile.getStringListPath("Command.Home.MessageHomeCreated");
        messageAlreadyCreated = CommandFile.getStringListPath("Command.Home.MessageHomeAlreadyCreated");
        messageCanNotCreateMoreHomes = CommandFile.getStringListPath("Command.Home.MessageHomeCanNotCreateMoreHomes");
        messageHomeDeleted = CommandFile.getStringListPath("Command.Home.MessageHomeDeleted");
        if(!NewSystem.loadedCommands.contains(this)) {
            NewSystem.loadedCommands.add(this);
            NewSystem.getInstance().getCommand("home").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("list")) {
                        sendList(p);
                    } else {
                        String home = args[0];
                        teleportToHome(p, home);
                    }
                } else if (args.length == 2) {
                    String home = args[1];
                    if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("set")) {
                        setHome(p, home);
                    } else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("remove")) {
                        deleteHome(p, home);
                    } else {
                        for(String value : usage) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                } else {
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

    public void sendList(Player p) {
        List<String> homes = getHomes(p);
        String count = String.valueOf(homes.size());

        String homesString = listFormat;
        for(int i = 0; i < homes.size(); i++) {
            if(i != homes.size()-1) {
                homesString = homesString.replace("{Home}", homes.get(i)) + listFormat;
            }else{
                homesString = homesString.replace("{Home}", homes.get(i));
            }
        }

        for(String msg : msgList) {
            if(!homes.isEmpty()) {
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Homes}", homesString).replace("{Count}", count));
            }else{
                if(!msg.contains("{Homes}")) {
                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Count}", count));
                }else{
                    for(String value : msgNoHomes) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }
        }
    }

    private static final HashMap<Player, Integer> coolDown = new HashMap<>();

    public void teleportToHome(Player p, String home) {
        List<String> homes = getHomes(p);

        World homeWorld = Bukkit.getWorld(SavingsFile.getStringPath("Home." + p.getUniqueId() + "." + home + ".World"));
        int homeX = SavingsFile.getIntegerPath("Home." + p.getUniqueId() + "." + home + ".X");
        int homeY = SavingsFile.getIntegerPath("Home." + p.getUniqueId() + "." + home + ".Y");
        int homeZ = SavingsFile.getIntegerPath("Home." + p.getUniqueId() + "." + home + ".Z");
        Location loc = new Location(homeWorld, homeX, homeY, homeZ);

        if(homes.contains(home)) {
            if(NewSystem.hasPermission(p, permNoDelay)) {
                p.teleport(loc);
                for(String msg : msgTp) {
                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Home}", home));
                }
            }else{
                if(coolDown.containsKey(p)) {
                    for(String value : msgAlreadyInTelport) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }else{
                    final Integer[] seconds = new Integer[]{delay};
                    int x = p.getLocation().getBlockX();
                    int z = p.getLocation().getBlockZ();

                    coolDown.put(p, Bukkit.getScheduler().scheduleSyncRepeatingTask(NewSystem.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            if (checkLocation(p, x, z)) {
                                if (seconds[0] == 1) {
                                    for(String key : msgCooldown) {
                                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Seconds}", countIsOne));
                                    }
                                    seconds[0]--;
                                } else if (seconds[0] >= 1) {
                                    for(String key : msgCooldown) {
                                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Seconds}", String.valueOf(seconds[0])));
                                    }
                                    seconds[0]--;
                                } else if (seconds[0] == 0) {
                                    p.teleport(loc);
                                    for(String msg : msgTp) {
                                        p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Home}", home));
                                    }

                                    int taskID = coolDown.get(p);
                                    coolDown.remove(p);
                                    Bukkit.getScheduler().cancelTask(taskID);
                                }
                            }
                        }
                    }, 0, 20));
                }
            }
        }else{
            for(String value : msgHomeNotExist) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static boolean checkLocation(Player p, int x, int z) {
        if(p.getLocation().getBlockX() == x) {
            if(p.getLocation().getBlockZ() == z) {
                return true;
            }else{
                for(String value : msgMovedWhileTeleport) {
                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }else{
            for(String value : msgMovedWhileTeleport) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
        int taskID = coolDown.get(p);
        coolDown.remove(p);
        Bukkit.getScheduler().cancelTask(taskID);
        return false;
    }

    public void setHome(Player p, String home) {
        int currentHomes = currentHomes(p);
        int maxHomes = maxHomes(p);
        List<String> homes = getHomes(p);

        if(currentHomes+1 <= maxHomes) {
            if(!homes.contains(home)) {
                SavingsFile.setPath("Home." + p.getUniqueId() + "." + home + ".X", p.getLocation().getBlockX());
                SavingsFile.setPath("Home." + p.getUniqueId() + "." + home + ".Y", p.getLocation().getBlockY());
                SavingsFile.setPath("Home." + p.getUniqueId() + "." + home + ".Z", p.getLocation().getBlockZ());
                SavingsFile.setPath("Home." + p.getUniqueId() + "." + home + ".World", p.getLocation().getWorld().getName());

                for(String msg : messageHomeCreated) {
                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Home}", home));
                }
            }else{
                for(String value : messageAlreadyCreated) {
                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }else{
            for(String value : messageCanNotCreateMoreHomes) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public void deleteHome(Player p, String home) {
        List<String> homes = getHomes(p);

        if (homes.contains(home)) {
            SavingsFile.setPath("Home." + p.getUniqueId() + "." + home, null);

            for (String msg : messageHomeDeleted) {
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Home}", home));
            }
        } else {
            for(String value : msgHomeNotExist) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public List<String> getHomes(Player p) {
        List<String> homes = new ArrayList<>();
        for(String home : SavingsFile.getConfigurationSection("Home." + p.getUniqueId())) {
            homes.add(home);
        }
        return homes;
    }

    public int currentHomes(Player p) {
        return SavingsFile.getConfigurationSection("Home." + p.getUniqueId()).size();
    }

    public int maxHomes(Player p) {
        String permMaxHomes = CommandFile.getStringPath("Command.Home.Permission.MaxHomes").replace("{MaxHomes}", "");
        List<String> permissions = new ArrayList<>();
        for(PermissionAttachmentInfo info : p.getEffectivePermissions()) {
            for(String perm : info.getAttachment().getPermissions().keySet()) {
                if(info.getAttachment().getPermissions().get(perm)) {
                    permissions.add(perm);
                }
            }
        }

        for (String perm : permissions) {
            if(NewSystem.hasPermission(p, "*")) {
                return Integer.MAX_VALUE;
            }else if(perm.contains(permMaxHomes) && perm.replace(permMaxHomes, "").equalsIgnoreCase("*")) {
                return Integer.MAX_VALUE;
            }else if (perm.contains(permMaxHomes)) {
                int homes = -1;
                try {
                    homes = Integer.parseInt(perm);
                } catch (NumberFormatException ignored) {
                }
                if (homes != -1) {
                    return homes;
                }
            }
        }
        return -1;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        List<String> tabCompletions = new ArrayList<>();

        if(sender instanceof Player) {
            Player p = (Player) sender;
            if (NewSystem.hasPermission(p, perm)) {
                if (args.length == 1) {
                    String[] completions = {"create", "delete", "list"};
                    for(String completion : completions) {
                        if(completion.contains(args[0])) {
                            tabCompletions.add(completion);
                        }
                    }
                    for(String home : getHomes(p)) {
                        if(home.contains(args[0])) {
                            tabCompletions.add(home);
                        }
                    }
                }
            }
        }
        return tabCompletions;
    }
}
