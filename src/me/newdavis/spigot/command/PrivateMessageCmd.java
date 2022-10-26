package me.newdavis.spigot.command;

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class PrivateMessageCmd implements CommandExecutor {

    private final HashMap<Player, Player> replyHashMap = new HashMap<>();

    private static List<String> usage;
    private static String perm;
    private static List<String> canNotSendSelf;
    private static List<String> messageSender;
    private static List<String> messagePlayer;
    private static boolean color;

    public void init() {
        usage = CommandFile.getStringListPath("Command.PrivateMessage.Usage");
        perm = CommandFile.getStringPath("Command.PrivateMessage.Permission");
        canNotSendSelf = CommandFile.getStringListPath("Command.PrivateMessage.CanNotSendSelf");
        messageSender = CommandFile.getStringListPath("Command.PrivateMessage.Message");
        messagePlayer = CommandFile.getStringListPath("Command.PrivateMessage.MessagePlayer");
        color = CommandFile.getBooleanPath("Command.PrivateMessage.EnabledColoredMessage");
        NewSystem.getInstance().getCommand("privatemessage").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if(args.length == 0) {
                    for(String value : usage) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }else if(args.length == 1) {
                    Player t = Bukkit.getPlayer(args[0]);
                    if(t == null) {
                        if(replyHashMap.containsKey(p) && replyHashMap.get(p) != null) {
                            t = replyHashMap.get(p);
                            String message = args[0];
                            sendMessage(p, t, message);
                        }else{
                            p.sendMessage(SettingsFile.getOffline());
                        }
                    }else{
                        for(String value : usage) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                }else{
                    Player t = Bukkit.getPlayer(args[0]);
                    if(t == null) {
                        if(replyHashMap.containsKey(p) && replyHashMap.get(p) != null) {
                            t = replyHashMap.get(p);
                            String message = getMessage(args, 0);
                            sendMessage(p, t, message);
                        }else{
                            p.sendMessage(SettingsFile.getOffline());
                        }
                    }else{
                        if(p != t) {
                            String message = getMessage(args, 1);
                            sendMessage(p, t, message);
                        }else{
                            for(String value : canNotSendSelf) {
                                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        }
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

    private String getMessage(String[] args, int start) {
        String msg = "";
        for(int i = start; i < args.length; i++) {
            msg += args[i] + " ";
        }
        return msg;
    }

    private void sendMessage(Player p, Player t, String message) {
        if(color) {
            message = message.replace("&", "§");
        }

        for(String msg : messageSender) {
            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)).replace("{Message}", message));
        }
        for(String msg : messagePlayer) {
            t.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(p)).replace("{Message}", message));
        }
        replyHashMap.put(p, t);
        replyHashMap.put(t, p);
    }
}
