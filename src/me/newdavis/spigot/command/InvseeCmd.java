package me.newdavis.spigot.command;

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class InvseeCmd implements CommandExecutor {

    public static ArrayList<Player> invSeePlayer = new ArrayList<>();

    private static List<String> usage;
    private static String perm;
    private static String permEdit;
    private static List<String> msg;

    public void init() {
        usage = CommandFile.getStringListPath("Command.InvSee.Usage");
        perm = CommandFile.getStringPath("Command.InvSee.Permission.Use");
        permEdit = CommandFile.getStringPath("Command.InvSee.Permission.Edit");
        msg = CommandFile.getStringListPath("Command.InvSee.Message");
        NewSystem.getInstance().getCommand("invsee").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if (NewSystem.hasPermission(p, perm)) {
                if (args.length == 1) {
                    Player t = Bukkit.getPlayer(args[0]);
                    if (t != null) {
                        if (t != p) {
                            p.openInventory(t.getInventory());
                            for(String key : msg) {
                                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                            }
                            if (!(NewSystem.hasPermission(p, permEdit))) {
                                invSeePlayer.add(p);
                            }
                        } else {
                            p.sendMessage(SettingsFile.getArgument());
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
