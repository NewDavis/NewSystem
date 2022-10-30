package me.newdavis.spigot.command;
//Plugin by NewDavis

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.file.SavingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import me.newdavis.spigot.sql.MySQL;
import me.newdavis.spigot.sql.SQLTables;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UnMuteCmd implements CommandExecutor, TabCompleter {

    private static MySQL mySQL = NewSystem.getMySQL();
    private static boolean mySQLEnabled  = SettingsFile.getMySQLEnabled();

    private static List<String> usage;
    private static String perm;
    private static String permIP;
    private static List<String> msgPlayerNotMuted;
    private static List<String> msgUnMutedPlayer;
    private static List<String> listMessage;
    private static List<String> msgIPNotMuted;

    public UnMuteCmd() {
        usage = CommandFile.getStringListPath("Command.UnMute.Usage");
        perm = CommandFile.getStringPath("Command.UnMute.Permission.Use");
        permIP = CommandFile.getStringPath("Command.UnMute.Permission.IP");
        msgPlayerNotMuted = CommandFile.getStringListPath("Command.UnMute.MessagePlayerNotMuted");
        msgUnMutedPlayer = CommandFile.getStringListPath("Command.UnMute.MessagePlayer");
        listMessage = CommandFile.getStringListPath("Command.UnMute.Message");
        msgIPNotMuted = CommandFile.getStringListPath("Command.UnMute.MessageIPNotMuted");
        if(!NewSystem.loadedCommands.contains(this)) {
            NewSystem.loadedCommands.add(this);
            NewSystem.getInstance().getCommand("unmute").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if(args.length == 1) {
                    String ip = args[0].replace(".", "-");
                    if(MuteIPCmd.isIPMuted(args[0])) {
                        unMuteIP(p, ip);
                    }else {
                        OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                        unMutePlayer(p, t);
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
            if(args.length == 1) {
                String ip = args[0].replace(".", "-");
                if(MuteIPCmd.isIPMuted(args[0])) {
                    unMuteIP(sender, ip);
                }else {
                    OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                    unMutePlayer(sender, t);
                }
            }else{
                for(String value : usage) {
                    sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }
        return false;
    }

    public static void unMutePlayer(Player p, OfflinePlayer t) {
        if(MuteCmd.isPlayerMuted(t)) {
            int punishmentCount = MuteCmd.getPlayerPunishmentCount(t)-1;
            if(mySQLEnabled) {
                mySQL.executeUpdate("DELETE FROM " + SQLTables.MUTED_PLAYERS.getTableName() + " WHERE UUID='" + t.getUniqueId() + "'");
                mySQL.executeUpdate("UPDATE " + SQLTables.MUTE.getTableName() + " SET UUID_UNMUTE_OF='" + p.getUniqueId().toString() + "' WHERE " +
                        "(UUID='" + t.getUniqueId().toString() + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");
            }else{
                List<String> mutedPlayers = MuteCmd.mutedPlayers;
                mutedPlayers.remove(t.getUniqueId().toString());
                SavingsFile.setPath("Mute.MutedPlayers", mutedPlayers);
                SavingsFile.setPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".UnMuteOf", p.getUniqueId().toString());
            }

            for(String key : msgUnMutedPlayer) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
            }
            for(String msg : listMessage) {
                for(Player all : Bukkit.getOnlinePlayers()) {
                    if(NewSystem.hasPermission(all, perm)) {
                        all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(p, false)).replace("{DisplayName}", NewSystem.getName(p, true)).replace("{Muted-Player}", NewSystem.getName(t, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"))));
                    }
                }
            }
        }else{
            for(String value : msgPlayerNotMuted) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void unMutePlayer(CommandSender p, OfflinePlayer t) {
        if(MuteCmd.isPlayerMuted(t)) {
            int punishmentCount = MuteCmd.getPlayerPunishmentCount(t) - 1;
            if(mySQLEnabled) {
                mySQL.executeUpdate("DELETE FROM " + SQLTables.MUTED_PLAYERS.getTableName() + " WHERE UUID='" + t.getUniqueId() + "'");
                mySQL.executeUpdate("UPDATE " + SQLTables.MUTE.getTableName() + " SET UUID_UNMUTE_OF='Console' WHERE " +
                        "(UUID='" + t.getUniqueId().toString() + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");
            }else {
                List<String> mutedPlayers = MuteCmd.mutedPlayers;
                mutedPlayers.remove(t.getUniqueId().toString());
                SavingsFile.setPath("Mute.MutedPlayers", mutedPlayers);
                SavingsFile.setPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".UnMuteOf", "Console");
            }

            for(String key : msgUnMutedPlayer) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
            }
            for(String msg : listMessage) {
                for(Player all : Bukkit.getOnlinePlayers()) {
                    if(NewSystem.hasPermission(all, perm)) {
                        all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", SettingsFile.getConsolePrefix()).replace("{Muted-Player}", NewSystem.getName(t, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"))));
                    }
                }
            }
        }else{
            for(String value : msgPlayerNotMuted) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void unMuteIP(Player p, String ip) {
        if(NewSystem.hasPermission(p, permIP)) {
            if (MuteIPCmd.isIPMuted(ip)) {
                int punishmentCount = MuteIPCmd.getIPPunishmentCount(ip) - 1;
                MuteIPCmd.mutedIPs.remove(ip);
                if(mySQLEnabled) {
                    mySQL.executeUpdate("DELETE FROM " + SQLTables.MUTED_IPS.getTableName() + " WHERE IP='" + ip + "'");
                    mySQL.executeUpdate("UPDATE " + SQLTables.MUTEIP.getTableName() + " SET UUID_UNMUTE_OF='" + p.getUniqueId().toString() + "' WHERE " +
                            "(IP='" + ip + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");
                }else {
                    List<String> mutedIPs = MuteIPCmd.mutedIPs;
                    mutedIPs.remove(ip);
                    SavingsFile.setPath("MuteIP.MutedIPs", mutedIPs);
                    SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".UnMuteOf", p.getUniqueId().toString());
                }

                for (String key : msgUnMutedPlayer) {
                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", ip.replace("-", ".")));
                }
                for (String msg : listMessage) {
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (NewSystem.hasPermission(all, perm)) {
                            all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(p, false)).replace("{DisplayName}", NewSystem.getName(p, true)).replace("{Muted-Player}", ip.replace("-", ".")));
                        }
                    }
                }
            } else {
                for (String value : msgIPNotMuted) {
                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }else{
            p.sendMessage(SettingsFile.getNoPerm());
        }
    }

    public static void unMuteIP(CommandSender p, String ip) {
        if (MuteIPCmd.isIPMuted(ip)) {
            int punishmentCount = MuteIPCmd.getIPPunishmentCount(ip) - 1;
            MuteIPCmd.mutedIPs.remove(ip);
            if(mySQLEnabled) {
                mySQL.executeUpdate("DELETE FROM " + SQLTables.MUTED_IPS.getTableName() + " WHERE IP='" + ip + "'");
                mySQL.executeUpdate("UPDATE " + SQLTables.MUTEIP.getTableName() + " SET UUID_UNMUTE_OF='Console' WHERE " +
                        "(IP='" + ip + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");
            }else {
                List<String> mutedIPs = MuteIPCmd.mutedIPs;
                mutedIPs.remove(ip);
                SavingsFile.setPath("MuteIP.MutedIPs", mutedIPs);
                SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".UnMuteOf", "Console");
            }

            for (String key : msgUnMutedPlayer) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", ip.replace("-", ".")));
            }
            for (String msg : listMessage) {
                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (NewSystem.hasPermission(all, perm)) {
                        all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", SettingsFile.getConsolePrefix()).replace("{Muted-Player}", ip.replace("-", ".")));
                    }
                }
            }
        } else {
            for (String value : msgIPNotMuted) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> tabCompletions = new ArrayList<>();

        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(args.length == 1) {
                if(NewSystem.hasPermission(p, perm)) {
                    for (String uuid : MuteCmd.mutedPlayers) {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                        if(offlinePlayer.getName().contains(args[0])) {
                            tabCompletions.add(offlinePlayer.getName());
                        }
                    }
                }

                if(NewSystem.hasPermission(p, permIP)) {
                    for (String ip : MuteIPCmd.mutedIPs) {
                        if(ip.contains(args[0])) {
                            tabCompletions.add(ip);
                        }
                    }
                }
            }
        }else{
            if (args.length == 1) {
                for (String uuid : MuteCmd.mutedPlayers) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                    if (offlinePlayer.getName().contains(args[0])) {
                        tabCompletions.add(offlinePlayer.getName());
                    }
                }

                for (String ip : MuteIPCmd.mutedIPs) {
                    if (ip.contains(args[0])) {
                        tabCompletions.add(ip);
                    }
                }
            }
        }
        return tabCompletions;
    }
}
