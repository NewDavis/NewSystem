package me.newdavis.spigot.command;

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ClearChatCmd implements CommandExecutor {

    private static List<String> usage;
    private static String perm;
    private static int emptyLines;
    private static List<String> msg;

    public void init() {
        usage = CommandFile.getStringListPath("Command.ClearChat.Usage");
        perm = CommandFile.getStringPath("Command.ClearChat.Permission");
        emptyLines = CommandFile.getIntegerPath("Command.ClearChat.EmptyLines");
        msg = CommandFile.getStringListPath("Command.ClearChat.Message");
        NewSystem.getInstance().getCommand("clearchat").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if (NewSystem.hasPermission(p, perm)) {
                if (args.length == 0) {
                    for(Player all : Bukkit.getOnlinePlayers()) {
                        for (int i = 0; i < emptyLines; i++) {
                            all.sendMessage("");
                        }
                    }

                    for(String key : msg) {
                        Bukkit.broadcastMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(p, false)).replace("{DisplayName}", NewSystem.getName(p, true)));
                    }
                }else{
                    for(String key : usage) {
                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            } else {
                p.sendMessage(SettingsFile.getNoPerm());
            }
        }else{
            if (args.length == 0) {
                for(int i = 0; i < emptyLines; i++) {
                    Bukkit.broadcastMessage("§f§6 ");
                }

                for(String key : msg) {
                    Bukkit.broadcastMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", SettingsFile.getConsolePrefix()));
                }
            }else{
                for(String key : usage) {
                    sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }
        return false;
    }
}
