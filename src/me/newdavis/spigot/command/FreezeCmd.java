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

import java.util.HashMap;
import java.util.List;

public class FreezeCmd implements CommandExecutor {

    public static HashMap<Player, Location> freezed = new HashMap<>();

    private static List<String> usage;
    private static String perm;
    private static List<String> msgFreezed;
    private static List<String> msgFreezedP;
    private static List<String> msgUnfreezed;
    private static List<String> msgUnfreezedP;

    public FreezeCmd() {
        usage = CommandFile.getStringListPath("Command.Freeze.Usage");
        perm = CommandFile.getStringPath("Command.Freeze.Permission");
        msgFreezed = CommandFile.getStringListPath("Command.Freeze.MessageFreezed");
        msgFreezedP = CommandFile.getStringListPath("Command.Freeze.MessageFreezedPlayer");
        msgUnfreezed = CommandFile.getStringListPath("Command.Freeze.MessageUnfreezed");
        msgUnfreezedP = CommandFile.getStringListPath("Command.Freeze.MessageUnfreezedPlayer");
        if(!NewSystem.loadedCommands.contains(this)) {
            NewSystem.loadedCommands.add(this);
            NewSystem.getInstance().getCommand("freeze").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if (args.length == 1) {
                    Player t = Bukkit.getPlayer(args[0]);
                    if (t != null) {
                        if(freezed.containsKey(t)) {
                            freezed.remove(t);
                            for(String key : msgUnfreezed) {
                                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                            }
                            for(String value : msgUnfreezedP) {
                                t.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        }else{
                            freezed.put(t, t.getLocation());
                            for(String key : msgFreezed) {
                                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                            }
                            for(String value : msgFreezedP) {
                                t.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        }
                    } else {
                        p.sendMessage(SettingsFile.getOffline());
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
            if (args.length == 1) {
                Player t = Bukkit.getPlayer(args[0]);
                if (t != null) {
                    if(freezed.containsKey(t)) {
                        freezed.remove(t);
                        for(String key : msgUnfreezed) {
                            sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                        }
                        for(String value : msgUnfreezedP) {
                            t.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }else{
                        freezed.put(t, t.getLocation());
                        for(String key : msgFreezed) {
                            sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                        }
                        for(String value : msgFreezedP) {
                            t.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                } else {
                    sender.sendMessage(SettingsFile.getOffline());
                }
            }else{
                for(String value : usage) {
                    sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }
        return false;
    }
}
