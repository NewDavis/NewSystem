package me.newdavis.spigot.command;

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SpeedCmd implements CommandExecutor {

    private static List<String> usage;
    private static String perm;
    private static String permOther;
    private static String permFly;
    private static String permWalk;
    private static List<String> msg;
    private static List<String> msgP;

    public SpeedCmd() {
        usage = CommandFile.getStringListPath("Command.Speed.Usage");
        perm = CommandFile.getStringPath("Command.Speed.Permission.Use");
        permOther = CommandFile.getStringPath("Command.Speed.Permission.Other");
        permFly = CommandFile.getStringPath("Command.Speed.Permission.Fly");
        permWalk = CommandFile.getStringPath("Command.Speed.Permission.Walk");
        msg = CommandFile.getStringListPath("Command.Speed.Message");
        msgP = CommandFile.getStringListPath("Command.Speed.MessagePlayer");
        if(!NewSystem.loadedCommands.contains(this)) {
            NewSystem.loadedCommands.add(this);
            NewSystem.getInstance().getCommand("speed").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if(args.length == 1) {
                    if(NewSystem.hasPermission(p, permFly) && NewSystem.hasPermission(p, permWalk)) {
                        float speed = getSpeed(args[0]);
                        String speedMsg = getMessageSpeed(speed);
                        p.setFlySpeed(speed);
                        p.setWalkSpeed(speed);
                        for(String key : msgP) {
                            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Speed}", speedMsg));
                        }
                    }else{
                        p.sendMessage(SettingsFile.getNoPerm());
                    }
                }else if(args.length == 2) {
                    if(args[0].equalsIgnoreCase("fly")) {
                        if(NewSystem.hasPermission(p, permFly)) {
                            float speed = getSpeed(args[1]);
                            String speedMsg = getMessageSpeed(speed);

                            p.setFlySpeed(speed);
                            for(String key : msgP) {
                                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Speed}", speedMsg));
                            }
                        }else{
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    }else if(args[0].equalsIgnoreCase("walk")) {
                        if(NewSystem.hasPermission(p, permWalk)) {
                            float speed = getSpeed(args[1]);
                            String speedMsg = getMessageSpeed(speed);

                            p.setWalkSpeed(speed);
                            for(String key : msgP) {
                                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Speed}", speedMsg));
                            }
                        }else{
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    }else{
                        if(NewSystem.hasPermission(p, permFly) && NewSystem.hasPermission(p, permWalk) && NewSystem.hasPermission(p, permOther)) {
                            Player t = Bukkit.getPlayer(args[1]);
                            if(t != null) {
                                float speed = getSpeed(args[1]);
                                String speedMsg = getMessageSpeed(speed);

                                if(p != t) {
                                    t.setFlySpeed(speed);
                                    t.setWalkSpeed(speed+1);
                                    for(String key : msgP) {
                                        t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Speed}", speedMsg));
                                    }
                                    for(String key : msg) {
                                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Speed}", speedMsg).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                                    }
                                }else{
                                    p.setFlySpeed(speed);
                                    p.setWalkSpeed(speed+1);
                                    for(String key : msgP) {
                                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Speed}", speedMsg));
                                    }
                                }
                            }else{
                                p.sendMessage(SettingsFile.getOffline());
                            }
                        }else{
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    }
                }else if(args.length == 3) {
                    if(args[0].equalsIgnoreCase("fly")) {
                        if(NewSystem.hasPermission(p, permFly)) {
                            float speed = getSpeed(args[1]);
                            String speedMsg = getMessageSpeed(speed);

                            Player t = Bukkit.getPlayer(args[2]);
                            if(t != null) {
                                if(p != t) {
                                    t.setFlySpeed(speed);
                                    for(String key : msgP) {
                                        t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Speed}", speedMsg));
                                    }
                                    for(String key : msg) {
                                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Speed}", speedMsg).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                                    }
                                }else{
                                    for(String key : msgP) {
                                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Speed}", speedMsg));
                                    }
                                }
                            }else{
                                p.sendMessage(SettingsFile.getOffline());
                            }
                        }else{
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    }else if(args[0].equalsIgnoreCase("walk")) {
                        if(NewSystem.hasPermission(p, permWalk)) {
                            float speed = getSpeed(args[1]);
                            String speedMsg = getMessageSpeed(speed);

                            Player t = Bukkit.getPlayer(args[2]);
                            if(t != null) {
                                if(p != t) {
                                    t.setWalkSpeed(speed);
                                    for(String key : msgP) {
                                        t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Speed}", speedMsg));
                                    }
                                    for(String key : msg) {
                                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Speed}", speedMsg).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                                    }
                                }else{
                                    for(String key : msgP) {
                                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Speed}", speedMsg));
                                    }
                                }
                            }else{
                                p.sendMessage(SettingsFile.getOffline());
                            }
                        }else{
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    }else {
                        for(String value : usage) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
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

    private static float getSpeed(String speed) {
        float speedFloat = 0.2F;
        try {
            if(speed.equalsIgnoreCase("0")) {
                speedFloat = 0.0F;
            }else if(speed.equalsIgnoreCase("1")) {
                speedFloat = 0.1F;
            }else if(speed.equalsIgnoreCase("3")) {
                speedFloat = 0.3F;
            }else if(speed.equalsIgnoreCase("4")) {
                speedFloat = 0.4F;
            }else if(speed.equalsIgnoreCase("5")) {
                speedFloat = 0.5F;
            }else if(speed.equalsIgnoreCase("6")) {
                speedFloat = 0.6F;
            }else if(speed.equalsIgnoreCase("7")) {
                speedFloat = 0.7F;
            }else if(speed.equalsIgnoreCase("8")) {
                speedFloat = 0.8F;
            }else if(speed.equalsIgnoreCase("9")) {
                speedFloat = 0.9F;
            }else if(speed.equalsIgnoreCase("10")) {
                speedFloat = 1.0F;
            }
        } catch (NumberFormatException ignored) {
        }

        return speedFloat;

    }

    private static String getMessageSpeed(float speed) {
        String msg = "2";
        if(speed == 0.0F) {
            msg = "0";
        }else if(speed == 0.1F) {
            msg = "1";
        }else if(speed == 0.3F) {
            msg = "3";
        }else if(speed == 0.4F) {
            msg = "4";
        }else if(speed == 0.5F) {
            msg = "5";
        }else if(speed == 0.6F) {
            msg = "6";
        }else if(speed == 0.7F) {
            msg = "7";
        }else if(speed == 0.8F) {
            msg = "8";
        }else if(speed == 0.9F) {
            msg = "9";
        }else if(speed == 1.0F) {
            msg = "10";
        }

        return msg;
    }
}
