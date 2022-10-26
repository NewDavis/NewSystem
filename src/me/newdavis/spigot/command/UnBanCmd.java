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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UnBanCmd implements CommandExecutor, TabCompleter {

    private static MySQL mySQL = NewSystem.getMySQL();
    private static boolean mySQLEnabled  = SettingsFile.getMySQLEnabled();

    private static List<String> usage;
    private static String perm;
    private static String permIP;
    private static List<String> msgPlayerNotBanned;
    private static List<String> msgUnBannedPlayer;
    private static List<String> listMessage;
    private static List<String> msgIPNotBanned;

    public void init() {
        usage = CommandFile.getStringListPath("Command.UnBan.Usage");
        perm = CommandFile.getStringPath("Command.UnBan.Permission.Use");
        permIP = CommandFile.getStringPath("Command.UnBan.Permission.IP");
        msgPlayerNotBanned = CommandFile.getStringListPath("Command.UnBan.MessagePlayerNotBanned");
        msgUnBannedPlayer = CommandFile.getStringListPath("Command.UnBan.MessagePlayer");
        listMessage = CommandFile.getStringListPath("Command.UnBan.Message");
        msgIPNotBanned = CommandFile.getStringListPath("Command.UnBan.MessageIPNotBanned");
        NewSystem.getInstance().getCommand("unban").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if(args.length == 1) {
                    String ip = args[0].replace(".", "-");
                    if(BanIPCmd.isIPBanned(ip)) {
                        unbanIP(p, ip);
                    }else {
                        OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                        unbanPlayer(p, t);
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
                if(BanIPCmd.isIPBanned(ip)) {
                    unbanIP(sender, ip);
                }else {
                    OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                    unbanPlayer(sender, t);
                }
            }else{
                for(String value : usage) {
                    sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }
        return false;
    }

    public static void unbanPlayer(Player p, OfflinePlayer t) {
        if(BanCmd.isPlayerBanned(t)) {
            int punishmentCount = BanCmd.getPlayerPunishmentCount(t) - 1;
            if(mySQLEnabled) {
                mySQL.executeUpdate("DELETE FROM " + SQLTables.BANNED_PLAYERS.getTableName() + " WHERE UUID='" + t.getUniqueId() + "'");
                mySQL.executeUpdate("UPDATE " + SQLTables.BAN.getTableName() + " SET UUID_UNBAN_OF='" + p.getUniqueId().toString() + "' WHERE " +
                        "(UUID='" + t.getUniqueId().toString() + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");
            }else {
                List<String> bannedPlayers = BanCmd.getBannedPlayers();
                bannedPlayers.remove(t.getUniqueId().toString());
                SavingsFile.setPath("Ban.BannedPlayers", bannedPlayers);
                SavingsFile.setPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".UnbanOf", p.getUniqueId().toString());
            }

            for(String key : msgUnBannedPlayer) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)));
            }
            for(String msg : listMessage) {
                for(Player all : Bukkit.getOnlinePlayers()) {
                    if(NewSystem.hasPermission(all, perm)) {
                        all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(p)).replace("{Banned-Player}", NewSystem.getName(t)));
                    }
                }
            }
        }else{
            for(String value : msgPlayerNotBanned) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void unbanPlayer(CommandSender p, OfflinePlayer t) {
        if(BanCmd.isPlayerBanned(t)) {
            int punishmentCount = BanCmd.getPlayerPunishmentCount(t) - 1;
            if(mySQLEnabled) {
                mySQL.executeUpdate("DELETE FROM " + SQLTables.BANNED_PLAYERS.getTableName() + " WHERE UUID='" + t.getUniqueId() + "'");
                mySQL.executeUpdate("UPDATE " + SQLTables.BAN.getTableName() + " SET UUID_UNBAN_OF='Console' WHERE " +
                        "(UUID='" + t.getUniqueId().toString() + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");
            }else {
                List<String> bannedPlayers = BanCmd.getBannedPlayers();
                bannedPlayers.remove(t.getUniqueId().toString());
                SavingsFile.setPath("Ban.BannedPlayers", bannedPlayers);
                SavingsFile.setPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".UnbanOf", "Console");
            }

            for(String key : msgUnBannedPlayer) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)));
            }
            for(String msg : listMessage) {
                for(Player all : Bukkit.getOnlinePlayers()) {
                    if(NewSystem.hasPermission(all, perm)) {
                        all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", SettingsFile.getConsolePrefix()).replace("{Banned-Player}", NewSystem.getName(t)));
                    }
                }
            }
        }else{
            for(String value : msgPlayerNotBanned) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void unbanIP(Player p, String ip) {
        if(NewSystem.hasPermission(p, permIP)) {
            if (BanIPCmd.isIPBanned(ip)) {
                int punishmentCount = BanIPCmd.getIPPunishmentCount(ip) - 1;
                if(mySQLEnabled) {
                    mySQL.executeUpdate("DELETE FROM " + SQLTables.BANNED_IPS.getTableName() + " WHERE IP='" + ip + "'");
                    mySQL.executeUpdate("UPDATE " + SQLTables.BANIP.getTableName() + " SET UUID_UNBAN_OF='" + p.getUniqueId().toString() + "' WHERE " +
                            "(IP='" + ip + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");
                }else {
                    List<String> bannedIPs = BanIPCmd.getBannedIPs();
                    bannedIPs.remove(ip);
                    SavingsFile.setPath("BanIP.BannedIPs", bannedIPs);
                    SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".UnbanOf", p.getUniqueId().toString());
                }

                for (String key : msgUnBannedPlayer) {
                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", ip.replace("-", ".")));
                }
                for (String msg : listMessage) {
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (NewSystem.hasPermission(all, perm)) {
                            all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(p)).replace("{Banned-Player}", ip.replace("-", ".")));
                        }
                    }
                }
            } else {
                for (String value : msgIPNotBanned) {
                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }else{
            p.sendMessage(SettingsFile.getNoPerm());
        }
    }

    public static void unbanIP(CommandSender p, String ip) {
        if (BanIPCmd.isIPBanned(ip)) {
            int punishmentCount = BanIPCmd.getIPPunishmentCount(ip) - 1;
            if(mySQLEnabled) {
                mySQL.executeUpdate("DELETE FROM " + SQLTables.BANNED_IPS.getTableName() + " WHERE IP='" + ip + "'");
                mySQL.executeUpdate("UPDATE " + SQLTables.BANIP.getTableName() + " SET UUID_UNBAN_OF='Console' WHERE " +
                        "(IP='" + ip + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");
            }else {
                List<String> bannedIPs = BanIPCmd.getBannedIPs();
                bannedIPs.remove(ip);
                SavingsFile.setPath("BanIP.BannedIPs", bannedIPs);
                SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".UnbanOf", "Console");
            }

            for (String key : msgUnBannedPlayer) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", ip.replace("-", ".")));
            }
            for (String msg : listMessage) {
                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (NewSystem.hasPermission(all, perm)) {
                        all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", SettingsFile.getConsolePrefix()).replace("{Banned-Player}", ip.replace("-", ".")));
                    }
                }
            }
        } else {
            for (String value : msgIPNotBanned) {
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
                    for (String uuid : BanCmd.getBannedPlayers()) {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                        if(offlinePlayer.getName().contains(args[0])) {
                            tabCompletions.add(offlinePlayer.getName());
                        }
                    }
                }

                if(NewSystem.hasPermission(p, permIP)) {
                    for (String ip : BanIPCmd.getBannedIPs()) {
                        if(ip.contains(args[0])) {
                            tabCompletions.add(ip);
                        }
                    }
                }
            }
        }else {
            if (args.length == 1) {
                for (String uuid : BanCmd.getBannedPlayers()) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                    if (offlinePlayer.getName().contains(args[0])) {
                        tabCompletions.add(offlinePlayer.getName());
                    }
                }

                for (String ip : BanIPCmd.getBannedIPs()) {
                    if (ip.contains(args[0])) {
                        tabCompletions.add(ip);
                    }
                }
            }
        }

        return tabCompletions;
    }
}
