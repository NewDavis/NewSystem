package me.newdavis.spigot.command;

import me.newdavis.spigot.api.ReflectionAPI;
import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ShowIPCmd implements CommandExecutor {

    private static String perm;
    private static List<String> usage;
    private static boolean suggestIP;
    private static List<String> message;

    public ShowIPCmd() {
        perm = CommandFile.getStringPath("Command.ShowIP.Permission");
        usage = CommandFile.getStringListPath("Command.ShowIP.Usage");
        suggestIP = CommandFile.getBooleanPath("Command.ShowIP.SuggestIP");
        message = CommandFile.getStringListPath("Command.ShowIP.Message");
        if(!NewSystem.loadedCommands.contains(this)) {
            NewSystem.loadedCommands.add(this);
            NewSystem.getInstance().getCommand("showip").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if(args.length == 1) {
                    Player t = Bukkit.getPlayer(args[0]);
                    if(t != null) {
                        sendIP(p, t);
                    }else{
                        p.sendMessage(SettingsFile.getOffline());
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
            if(args.length == 1) {
                Player t = Bukkit.getPlayer(args[0]);
                if(t != null) {
                    sendIP(sender, t);
                }else{
                    sender.sendMessage(SettingsFile.getOffline());
                }
            }else{
                for(String msg : usage) {
                    sender.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }
        return false;
    }

    private void sendIP(Player p, Player t) {
        String ip = new ReflectionAPI().getPlayerIP(t);

        for(String msg : message) {
            if(msg.contains("{IP}")) {
                TextComponent component = new TextComponent(msg
                        .replace("{Prefix}", SettingsFile.getPrefix())
                        .replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true))
                        .replace("{IP}", ip));
                if(suggestIP) {
                    component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ip));
                }
                p.spigot().sendMessage(component);
            }else{
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    private void sendIP(CommandSender p, Player t) {
        String ip = new ReflectionAPI().getPlayerIP(t);

        for(String msg : message) {
            p.sendMessage(msg
                    .replace("{Prefix}", SettingsFile.getPrefix())
                    .replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true))
                    .replace("{IP}", ip));
        }
    }
}
