package me.newdavis.spigot.command;
//Plugin by NewDavis

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class NightCmd implements CommandExecutor {

    private static String perm;
    private static List<String> msgP;
    private static List<String> msgAll;
    private static boolean globalMessage;

    public void init() {
        perm = CommandFile.getStringPath("Command.Night.Permission");
        msgP = CommandFile.getStringListPath("Command.Night.Message");
        msgAll = CommandFile.getStringListPath("Command.Night.GlobalMessage");
        globalMessage = CommandFile.getBooleanPath("Command.Night.GlobalMessageEnabled");
        NewSystem.getInstance().getCommand("night").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                for(String value : msgP) {
                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }

                if(globalMessage) {
                    for(Player all : Bukkit.getOnlinePlayers()) {
                        for(String value : msgAll) {
                            all.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                }

                for(World world : Bukkit.getWorlds()) {
                    world.setTime(40000);
                }
            }else{
                p.sendMessage(SettingsFile.getNoPerm());
            }
        }else{
            for(String value : msgP) {
                sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }

            if(globalMessage) {
                for(Player all : Bukkit.getOnlinePlayers()) {
                    for(String value : msgAll) {
                        all.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }

            for(World world : Bukkit.getWorlds()) {
                world.setTime(40000);
            }
        }
        return false;
    }
}
