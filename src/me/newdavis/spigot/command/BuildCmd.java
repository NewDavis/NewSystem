package me.newdavis.spigot.command;

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.listener.DoubleJumpListener;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BuildCmd implements CommandExecutor {

    public static ArrayList<Player> buildList = new ArrayList<>();

    private static List<String> usage;
    private static String perm;
    private static String permOther;
    private static List<String> msg;
    private static List<String> msgP;
    private static String activated;
    private static String deactivated;
    public static List<String> denyMessage;

    public BuildCmd() {
        usage = CommandFile.getStringListPath("Command.Build.Usage");
        perm = CommandFile.getStringPath("Command.Build.Permission.Use");
        permOther = CommandFile.getStringPath("Command.Build.Permission.Other");
        msg = CommandFile.getStringListPath("Command.Build.Message");
        msgP = CommandFile.getStringListPath("Command.Build.MessagePlayer");
        activated = CommandFile.getStringPath("Command.Build.Activated");
        deactivated = CommandFile.getStringPath("Command.Build.Deactivated");
        denyMessage = CommandFile.getStringListPath("Command.Build.DenyMessage");
        if(!NewSystem.loadedCommands.contains(this)) {
            NewSystem.loadedCommands.add(this);
            NewSystem.getInstance().getCommand("build").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if (NewSystem.hasPermission(p, perm)) {
                if (args.length == 0) {
                    if (buildList.contains(p)) {
                        buildList.remove(p);
                        for(String key : msgP) {
                            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{BuildMode}", deactivated));
                        }
                        p.setGameMode(GameMode.SURVIVAL);
                        DoubleJumpListener.flyMode.remove(p);
                    } else {
                        buildList.add(p);
                        for(String key : msgP) {
                            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{BuildMode}", activated));
                        }
                        p.setGameMode(GameMode.CREATIVE);
                        DoubleJumpListener.flyMode.add(p);
                    }
                } else if (args.length == 1) {
                    Player t = Bukkit.getPlayer(args[0]);
                    if (t != null) {
                        if (NewSystem.hasPermission(p, permOther)) {
                            if (buildList.contains(t)) {
                                buildList.remove(t);
                                for(String key : msgP) {
                                    t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{BuildMode}", deactivated));
                                }
                                for(String key : msg) {
                                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{BuildMode}", deactivated).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                                }
                                t.setGameMode(GameMode.SURVIVAL);
                                DoubleJumpListener.flyMode.remove(t);
                            } else {
                                buildList.add(t);
                                for(String key : msgP) {
                                    t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{BuildMode}", activated));
                                }
                                for(String key : msg) {
                                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{BuildMode}", activated).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                                }
                                t.setGameMode(GameMode.CREATIVE);
                                DoubleJumpListener.flyMode.add(t);
                            }
                        } else {
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    } else {
                        p.sendMessage(SettingsFile.getOffline());
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
}
