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
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameModeCmd implements CommandExecutor, TabCompleter {

    private static List<String> usage;
    private static String perm;
    private static String permGM;
    private static String permGMOther;
    private static List<String> msg;
    private static List<String> msgP;
    private static String survival;
    private static String creative;
    private static String adventure;
    private static String spectator;

    public GameModeCmd() {
        usage = CommandFile.getStringListPath("Command.GameMode.Usage");
        perm = CommandFile.getStringPath("Command.GameMode.Permission.Use");
        permGM = CommandFile.getStringPath("Command.GameMode.Permission.GameMode");
        permGMOther = CommandFile.getStringPath("Command.GameMode.Permission.GameModeOther");
        msg = CommandFile.getStringListPath("Command.GameMode.Message");
        msgP = CommandFile.getStringListPath("Command.GameMode.MessagePlayer");
        survival = CommandFile.getStringPath("Command.GameMode.Survival");
        creative = CommandFile.getStringPath("Command.GameMode.Creative");
        adventure = CommandFile.getStringPath("Command.GameMode.Adventure");
        spectator = CommandFile.getStringPath("Command.GameMode.Spectator");
        if(!NewSystem.loadedCommands.contains(this)) {
            NewSystem.loadedCommands.add(this);
            NewSystem.getInstance().getCommand("gamemode").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if (NewSystem.hasPermission(p, perm)) {
                if (args.length == 0) {
                    for(String value : usage) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                } else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("Survival") || args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase("Überleben") || args[0].equalsIgnoreCase("0")) {
                        if (NewSystem.hasPermission(p, permGM.replace("{GameMode}", survival))) {
                            p.setGameMode(GameMode.SURVIVAL);
                            for(String key : msgP) {
                                p.sendMessage(key.replace("{GameMode}", survival).replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                            DoubleJumpListener.flyMode.remove(p);
                        } else {
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    } else if (args[0].equalsIgnoreCase("Creative") || args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("Kreativ") || args[0].equalsIgnoreCase("1")) {
                        if (NewSystem.hasPermission(p, permGM.replace("{GameMode}", creative))) {
                            p.setGameMode(GameMode.CREATIVE);
                            for(String key : msgP) {
                                p.sendMessage(key.replace("{GameMode}", creative).replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                            DoubleJumpListener.flyMode.add(p);
                        } else {
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    } else if (args[0].equalsIgnoreCase("Adventure") || args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("Abenteuer") || args[0].equalsIgnoreCase("2")) {
                        if (NewSystem.hasPermission(p, permGM.replace("{GameMode}", adventure))) {
                            p.setGameMode(GameMode.ADVENTURE);
                            for(String key : msgP) {
                                p.sendMessage(key.replace("{GameMode}", adventure).replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                            DoubleJumpListener.flyMode.remove(p);
                        } else {
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    } else if (args[0].equalsIgnoreCase("Spectator") || args[0].equalsIgnoreCase("sp") || args[0].equalsIgnoreCase("Zuschauer") || args[0].equalsIgnoreCase("3")) {
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
                } else if (args.length == 2) {
                    Player t = Bukkit.getPlayer(args[1]);
                    if (t != null) {
                        if (args[0].equalsIgnoreCase("Survival") || args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase("Überleben") || args[0].equalsIgnoreCase("0")) {
                            if (NewSystem.hasPermission(p, permGMOther.replace("{GameMode}", survival))) {
                                t.setGameMode(GameMode.SURVIVAL);
                                for(String key : msg) {
                                    p.sendMessage(key.replace("{GameMode}", survival).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                                for(String key : msgP) {
                                    t.sendMessage(key.replace("{GameMode}", survival).replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                                DoubleJumpListener.flyMode.remove(t);
                            } else {
                                p.sendMessage(SettingsFile.getNoPerm());
                            }
                        } else if (args[0].equalsIgnoreCase("Creative") || args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("Kreativ") || args[0].equalsIgnoreCase("1")) {
                            if (NewSystem.hasPermission(p, permGMOther.replace("{GameMode}", creative))) {
                                t.setGameMode(GameMode.CREATIVE);
                                for(String key : msg) {
                                    p.sendMessage(key.replace("{GameMode}", creative).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                                for(String key : msgP) {
                                    t.sendMessage(key.replace("{GameMode}", creative).replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                                DoubleJumpListener.flyMode.add(t);
                            } else {
                                p.sendMessage(SettingsFile.getNoPerm());
                            }
                        } else if (args[0].equalsIgnoreCase("Adventure") || args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("Abenteuer") || args[0].equalsIgnoreCase("2")) {
                            if (NewSystem.hasPermission(p, permGMOther.replace("{GameMode}", adventure))) {
                                t.setGameMode(GameMode.ADVENTURE);
                                for(String key : msg) {
                                    p.sendMessage(key.replace("{GameMode}", adventure).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                                for(String key : msgP) {
                                    t.sendMessage(key.replace("{GameMode}", adventure).replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                                DoubleJumpListener.flyMode.remove(t);
                            } else {
                                p.sendMessage(SettingsFile.getNoPerm());
                            }
                        } else if (args[0].equalsIgnoreCase("Spectator") || args[0].equalsIgnoreCase("sp") || args[0].equalsIgnoreCase("Zuschauer") || args[0].equalsIgnoreCase("3")) {
                            if (NewSystem.hasPermission(p, permGMOther.replace("{GameMode}", spectator))) {
                                t.setGameMode(GameMode.SPECTATOR);
                                for(String key : msg) {
                                    p.sendMessage(key.replace("{GameMode}", spectator).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                                for(String key : msgP) {
                                    t.sendMessage(key.replace("{GameMode}", spectator).replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                                DoubleJumpListener.flyMode.remove(t);
                            } else {
                                p.sendMessage(SettingsFile.getNoPerm());
                            }
                        } else {
                            p.sendMessage(SettingsFile.getArgument());
                        }
                    } else {
                        p.sendMessage(SettingsFile.getOffline());
                    }
                }else{
                    for(String value : usage) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            } else {
                p.sendMessage(SettingsFile.getNoPerm());
            }
        }else {
            if (args.length == 2) {
                Player t = Bukkit.getPlayer(args[1]);
                if (t != null) {
                    if (args[0].equalsIgnoreCase("Survival") || args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase("Überleben") || args[0].equalsIgnoreCase("0")) {
                        t.setGameMode(GameMode.SURVIVAL);
                        for (String key : msg) {
                            sender.sendMessage(key.replace("{GameMode}", survival).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                        for (String key : msgP) {
                            t.sendMessage(key.replace("{GameMode}", survival).replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                        DoubleJumpListener.flyMode.remove(t);
                    } else if (args[0].equalsIgnoreCase("Creative") || args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("Kreativ") || args[0].equalsIgnoreCase("1")) {
                        t.setGameMode(GameMode.CREATIVE);
                        for (String key : msg) {
                            sender.sendMessage(key.replace("{GameMode}", creative).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                        for (String key : msgP) {
                            t.sendMessage(key.replace("{GameMode}", creative).replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                        DoubleJumpListener.flyMode.add(t);
                    } else if (args[0].equalsIgnoreCase("Adventure") || args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("Abenteuer") || args[0].equalsIgnoreCase("2")) {
                        t.setGameMode(GameMode.ADVENTURE);
                        for (String key : msg) {
                            sender.sendMessage(key.replace("{GameMode}", adventure).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                        for (String key : msgP) {
                            t.sendMessage(key.replace("{GameMode}", adventure).replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                        DoubleJumpListener.flyMode.remove(t);
                    } else if (args[0].equalsIgnoreCase("Spectator") || args[0].equalsIgnoreCase("sp") || args[0].equalsIgnoreCase("Zuschauer") || args[0].equalsIgnoreCase("3")) {
                        t.setGameMode(GameMode.SPECTATOR);
                        for (String key : msg) {
                            sender.sendMessage(key.replace("{GameMode}", spectator).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                        for (String key : msgP) {
                            t.sendMessage(key.replace("{GameMode}", spectator).replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                        DoubleJumpListener.flyMode.remove(t);
                    } else {
                        sender.sendMessage(SettingsFile.getArgument());
                    }
                } else {
                    sender.sendMessage(SettingsFile.getOffline());
                }
            } else {
                for (String value : usage) {
                    sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        List<String> tabCompletions = new ArrayList<>();

        if(sender instanceof Player) {
            Player p = (Player) sender;
            if (NewSystem.hasPermission(p, perm)) {
                if (args.length == 1) {
                    String[] completions = {survival, creative, adventure, spectator};
                    for(String completion : completions) {
                        if(completion.contains(args[0])) {
                            if(NewSystem.hasPermission(p, permGM.replace("{GameMode}", completion))) {
                                tabCompletions.add(completion);
                            }
                        }
                    }
                }else if(args.length == 2) {
                    if(args[0].equalsIgnoreCase(survival) && NewSystem.hasPermission(p, permGMOther.replace("{GameMode}", survival))) {
                        for(Player all : Bukkit.getOnlinePlayers()) {
                            if(all.getName().contains(args[1])) {
                                tabCompletions.add(all.getName());
                            }
                        }
                    }
                    if(args[0].equalsIgnoreCase(creative) && NewSystem.hasPermission(p, permGMOther.replace("{GameMode}", creative))) {
                        for(Player all : Bukkit.getOnlinePlayers()) {
                            if(all.getName().contains(args[1])) {
                                tabCompletions.add(all.getName());
                            }
                        }
                    }
                    if(args[0].equalsIgnoreCase(adventure) && NewSystem.hasPermission(p, permGMOther.replace("{GameMode}", adventure))) {
                        for(Player all : Bukkit.getOnlinePlayers()) {
                            if(all.getName().contains(args[1])) {
                                tabCompletions.add(all.getName());
                            }
                        }
                    }
                    if(args[0].equalsIgnoreCase(spectator) && NewSystem.hasPermission(p, permGMOther.replace("{GameMode}", spectator))) {
                        for(Player all : Bukkit.getOnlinePlayers()) {
                            if(all.getName().contains(args[1])) {
                                tabCompletions.add(all.getName());
                            }
                        }
                    }
                }
            }
        }else{
            if (args.length == 1) {
                String[] completions = {survival, creative, adventure, spectator};
                for(String completion : completions) {
                    if(completion.contains(args[0])) {
                        tabCompletions.add(completion);
                    }
                }
            }else if(args.length == 2) {
                if (args[0].equalsIgnoreCase(survival)) {
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (all.getName().contains(args[1])) {
                            tabCompletions.add(all.getName());
                        }
                    }
                }
                if (args[0].equalsIgnoreCase(creative)) {
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (all.getName().contains(args[1])) {
                            tabCompletions.add(all.getName());
                        }
                    }
                }
                if (args[0].equalsIgnoreCase(adventure)) {
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (all.getName().contains(args[1])) {
                            tabCompletions.add(all.getName());
                        }
                    }
                }
                if (args[0].equalsIgnoreCase(spectator)) {
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (all.getName().contains(args[1])) {
                            tabCompletions.add(all.getName());
                        }
                    }
                }
            }
        }
        return tabCompletions;
    }
}
