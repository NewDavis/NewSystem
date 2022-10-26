package me.newdavis.spigot.command;

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BackCmd implements CommandExecutor {

    public static HashMap<Player, Location> deathLocation = new HashMap<>();
    public static HashMap<Player, Long> cooldown = new HashMap<>();
    private List<Player> teleport = new ArrayList<>();

    private static List<String> usage;
    private static String permUse;
    private static String permNoDelay;
    private static String permTeleport;
    private static List<String> messageCoordinations;
    private static long commandCooldown;
    private static String cooldownFormat;
    private static int teleportCooldown;
    private static List<String> commandCooldownMessage;
    private static List<String> teleportCooldownMessage;
    private static String countIsOne;
    private static List<String> teleportedMessage;
    private static List<String> noDeathPointMessage;
    private static List<String> messageMovedWhileTeleportation;
    private static List<String> messageAlreadyInTeleport;

    public void init() {
        usage = CommandFile.getStringListPath("Command.Back.Usage");
        permUse = CommandFile.getStringPath("Command.Back.Permission.Use");
        permNoDelay = CommandFile.getStringPath("Command.Back.Permission.NoDelay");
        permTeleport = CommandFile.getStringPath("Command.Back.Permission.Teleport");
        messageCoordinations = CommandFile.getStringListPath("Command.Back.MessageCoordinations");
        commandCooldown = CommandFile.getIntegerPath("Command.Back.CommandCooldown");
        cooldownFormat = CommandFile.getStringPath("Command.Back.CooldownFormat");
        teleportCooldown = CommandFile.getIntegerPath("Command.Back.TeleportCooldownInSeconds");
        commandCooldownMessage = CommandFile.getStringListPath("Command.Back.CooldownMessage");
        teleportCooldownMessage = CommandFile.getStringListPath("Command.Back.TeleportCooldownMessage");
        countIsOne = CommandFile.getStringPath("Command.Back.TeleportCooldownCountIsOne");
        teleportedMessage = CommandFile.getStringListPath("Command.Back.TeleportedMessage");
        noDeathPointMessage = CommandFile.getStringListPath("Command.Back.NoDeathPointFound");
        messageMovedWhileTeleportation = CommandFile.getStringListPath("Command.Back.MessageMovedWhileTeleportation");
        messageAlreadyInTeleport = CommandFile.getStringListPath("Command.Back.MessageAlreadyInTeleport");
        NewSystem.getInstance().getCommand("back").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, permUse)) {
                if(args.length == 0) {
                    if (canTeleport(p)) {
                        if (!deathLocation.containsKey(p)) {
                            for (String msg : noDeathPointMessage) {
                                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                            return false;
                        }

                        if (NewSystem.hasPermission(p, permTeleport)) {
                            if (teleport.contains(p)) {
                                for (String msg : messageAlreadyInTeleport) {
                                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                            } else {
                                teleportToDeathLocation(p, NewSystem.hasPermission(p, permNoDelay));
                            }
                        } else {
                            sendDeathLocation(p);
                        }
                    } else {
                        sendCommandCooldownMessage(p);
                    }
                }else{
                    for(String msg : usage) {
                        p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
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

    public boolean canTeleport(Player p) {
        if(NewSystem.hasPermission(p, permNoDelay)) return true;

        if (cooldown.containsKey(p)) {
            return ((cooldown.get(p) + commandCooldown) - System.currentTimeMillis()) <= 0;
        }
        return true;
    }

    public void sendCommandCooldownMessage(Player p) {
        long timeLeft = (cooldown.get(p) + commandCooldown) - System.currentTimeMillis();

        int hours = (int) (timeLeft / 1000 / 60 / 60);
        timeLeft -= (long) hours * 1000 * 60 * 60;
        int minutes = (int) (timeLeft / 1000 / 60);
        timeLeft -= (long) minutes * 1000 * 60 * 60;
        int seconds = (int) (timeLeft / 1000);

        String format = cooldownFormat.replace("{Hours}", String.valueOf(hours))
                .replace("{Minutes}", String.valueOf(minutes))
                .replace("{Seconds}", String.valueOf(seconds));
        for(String msg : commandCooldownMessage) {
            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                    .replace("{Format}", format));
        }
    }

    public void sendDeathLocation(Player p) {
        DecimalFormat format = new DecimalFormat("##.##");
        Location location = deathLocation.get(p);
        for(String msg : messageCoordinations) {
            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                    .replace("{X}", format.format(location.getX()))
                    .replace("{Y}", format.format(location.getY()))
                    .replace("{Z}", format.format(location.getZ()))
                    .replace("{World}", location.getWorld().getName()));
        }
        deathLocation.remove(p);
    }

    public void teleportToDeathLocation(Player p, boolean noDelay) {
        Location location = deathLocation.get(p);

        if(!noDelay) {
            Location startedLocation = p.getLocation();
            final int[] count = {teleportCooldown};
            List<Integer> scheduler = new ArrayList<>();
            scheduler.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(NewSystem.getInstance(), new Runnable() {
                @Override
                public void run() {
                    if(!compareLocations(startedLocation, p.getLocation())) {
                        for(String msg : messageMovedWhileTeleportation) {
                            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                        Bukkit.getScheduler().cancelTask(scheduler.get(0));
                        return;
                    }

                    if(count[0] > 0) {
                        String secondsLeft = "";
                        if (count[0] > 1) {
                            secondsLeft = String.valueOf(count[0]);
                        } else if (count[0] == 1) {
                            secondsLeft = countIsOne;
                        }

                        for (String msg : teleportCooldownMessage) {
                            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Seconds}", secondsLeft));
                        }
                    }

                    if(count[0] == 0) {
                        p.teleport(location);
                        for(String msg : teleportedMessage) {
                            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                        deathLocation.remove(p);
                        Bukkit.getScheduler().cancelTask(scheduler.get(0));
                    }
                    count[0]--;
                }
            }, 0, 20));
        }else{
            p.teleport(location);
            for(String msg : teleportedMessage) {
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
            }
            deathLocation.remove(p);
        }
    }

    public boolean compareLocations(Location l1, Location l2) {
        return l1.getBlockX() == l2.getBlockX() && l1.getBlockY() == l2.getBlockY() && l1.getBlockZ() == l2.getBlockZ();
    }
}
