package me.newdavis.spigot.command;

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.listener.DoubleJumpListener;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GameModeShortCmd implements CommandExecutor {

    private static List<String> usage;
    private static String perm;
    private static String permGM;
    private static List<String> msgP;
    private static String survival;
    private static String creative;
    private static String adventure;
    private static String spectator;

    public GameModeShortCmd() {
        usage = CommandFile.getStringListPath("Command.GameMode.Usage");
        perm = CommandFile.getStringPath("Command.GameMode.Permission.Use");
        permGM = CommandFile.getStringPath("Command.GameMode.Permission.GameMode");
        msgP = CommandFile.getStringListPath("Command.GameMode.MessagePlayer");
        survival = CommandFile.getStringPath("Command.GameMode.Survival");
        creative = CommandFile.getStringPath("Command.GameMode.Creative");
        adventure = CommandFile.getStringPath("Command.GameMode.Adventure");
        spectator = CommandFile.getStringPath("Command.GameMode.Spectator");
        if(!NewSystem.loadedCommands.contains(this)) {
            NewSystem.loadedCommands.add(this);
            NewSystem.getInstance().getCommand("gms").setExecutor(this);
            NewSystem.getInstance().getCommand("gmc").setExecutor(this);
            NewSystem.getInstance().getCommand("gma").setExecutor(this);
            NewSystem.getInstance().getCommand("gmsp").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if (NewSystem.hasPermission(p, perm)) {
                if (args.length == 0) {
                    if (cmd.getName().equalsIgnoreCase("gms") || cmd.getName().equalsIgnoreCase("gm0")) {
                        if (NewSystem.hasPermission(p, permGM.replace("{GameMode}", survival))) {
                            p.setGameMode(GameMode.SURVIVAL);
                            for(String key : msgP) {
                                p.sendMessage(key.replace("{GameMode}", survival).replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                            DoubleJumpListener.flyMode.remove(p);
                        } else {
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    } else if (cmd.getName().equalsIgnoreCase("gmc") || cmd.getName().equalsIgnoreCase("gm1")) {
                        if (NewSystem.hasPermission(p, permGM.replace("{GameMode}", creative))) {
                            p.setGameMode(GameMode.CREATIVE);
                            for(String key : msgP) {
                                p.sendMessage(key.replace("{GameMode}", creative).replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                            DoubleJumpListener.flyMode.add(p);
                        } else {
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    } else if (cmd.getName().equalsIgnoreCase("gma") || cmd.getName().equalsIgnoreCase("gm2")) {
                        if (NewSystem.hasPermission(p, permGM.replace("{GameMode}", adventure))) {
                            p.setGameMode(GameMode.ADVENTURE);
                            for(String key : msgP) {
                                p.sendMessage(key.replace("{GameMode}", adventure).replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                            DoubleJumpListener.flyMode.remove(p);
                        } else {
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    } else if (cmd.getName().equalsIgnoreCase("gmsp") || cmd.getName().equalsIgnoreCase("gm3")) {
                        if (NewSystem.hasPermission(p, permGM.replace("{GameMode}", spectator))) {
                            p.setGameMode(GameMode.SPECTATOR);
                            for(String key : msgP) {
                                p.sendMessage(key.replace("{GameMode}", spectator).replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                            DoubleJumpListener.flyMode.remove(p);
                        } else {
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    } else {
                        p.sendMessage(SettingsFile.getArgument());
                    }
                }else{
                    for(String msg : usage) {
                        p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            } else {
                p.sendMessage(SettingsFile.getNoPerm());
            }
        }else{
            sender.sendMessage(SettingsFile.getOnlyPlayerCanExecute());
        }
        return false;
    }
}
