package me.newdavis.spigot.command;

import me.newdavis.spigot.api.ReflectionAPI;
import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SavingsFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import me.newdavis.spigot.sql.MySQL;
import me.newdavis.spigot.sql.SQLTables;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class BanIPCmd implements CommandExecutor, TabCompleter {

    private static MySQL mySQL = NewSystem.getMySQL();
    private static boolean mySQLEnabled  = SettingsFile.getMySQLEnabled();

    private static List<String> usage;
    private static String perm;
    private static String seconds;
    private static String minutes;
    private static String hours;
    private static String days;
    private static String weeks;
    private static String months;
    private static String years;
    private static List<String> blacklist;
    private static String permPermanent;
    private static List<String> msgBannedPermanently;
    private static List<String> messageListPermanent;
    private static List<String> msgAlreadyBanned;
    private static List<String> msgPlayerCanNotBeBanned;
    public static String kickMessagePermanent;
    private static String permTemporary;
    private static List<String> msgBannedTemporary;
    private static List<String> messageListTemporary;
    public static String kickMessageTemporary;
    private static List<String> listMessage;
    private static String noPlayerBanned;
    private static String hoverMessageTemporary;
    private static String hoverMessagePermanent;
    private static String consoleMessageTemporary;
    private static String consoleMessagePermanent;

    public void init() {
        usage = CommandFile.getStringListPath("Command.BanIP.Usage");
        perm = CommandFile.getStringPath("Command.BanIP.Permission.Use");
        seconds = CommandFile.getStringPath("Command.BanIP.Seconds");
        minutes = CommandFile.getStringPath("Command.BanIP.Minutes");
        hours = CommandFile.getStringPath("Command.BanIP.Hours");
        days = CommandFile.getStringPath("Command.BanIP.Days");
        weeks = CommandFile.getStringPath("Command.BanIP.Weeks");
        months = CommandFile.getStringPath("Command.BanIP.Months");
        years = CommandFile.getStringPath("Command.BanIP.Years");
        blacklist = CommandFile.getStringListPath("Command.BanIP.Blacklist");
        permPermanent = CommandFile.getStringPath("Command.BanIP.Permission.Permanent");
        msgBannedPermanently = CommandFile.getStringListPath("Command.BanIP.MessageIPBannedPermanentPlayer");
        messageListPermanent = CommandFile.getStringListPath("Command.BanIP.MessageIPBannedPermanent");
        msgAlreadyBanned = CommandFile.getStringListPath("Command.BanIP.MessageIPAlreadyBanned");
        msgPlayerCanNotBeBanned = CommandFile.getStringListPath("Command.BanIP.MessageIPCanNotGetBanned");
        kickMessagePermanent = CommandFile.getStringPath("Command.BanIP.KickMessagePermanent").replace("{Prefix}", SettingsFile.getPrefix());
        permTemporary = CommandFile.getStringPath("Command.BanIP.Permission.Temporary");
        msgBannedTemporary = CommandFile.getStringListPath("Command.BanIP.MessageIPBannedTemporaryPlayer");
        messageListTemporary = CommandFile.getStringListPath("Command.BanIP.MessageIPBannedTemporary");
        kickMessageTemporary = CommandFile.getStringPath("Command.BanIP.KickMessageTemporary").replace("{Prefix}", SettingsFile.getPrefix());
        listMessage = CommandFile.getStringListPath("Command.BanIP.ListMessage");
        noPlayerBanned = CommandFile.getStringPath("Command.BanIP.NoIPBannedMessage").replace("{Prefix}", SettingsFile.getPrefix());
        hoverMessageTemporary = CommandFile.getStringPath("Command.BanIP.HoverMessageTemporary");
        hoverMessagePermanent = CommandFile.getStringPath("Command.BanIP.HoverMessagePermanent");
        consoleMessageTemporary = CommandFile.getStringPath("Command.BanIP.IPListConsoleTemporary");
        consoleMessagePermanent = CommandFile.getStringPath("Command.BanIP.IPListConsolePermanent");
        NewSystem.getInstance().getCommand("banip").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (NewSystem.hasPermission(p, perm)) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("list")) {
                        if (NewSystem.hasPermission(p, perm)) {
                            sendList(p);
                        } else {
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    } else {
                        for (String value : usage) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                } else if (args.length == 2) {
                    if (!args[0].equalsIgnoreCase("list")) {
                        if(args[0].contains(".")) {
                            String ip = args[0].replace(".", "-");
                            String reason = getReasonPermanent(args);
                            banIPPermanent(p, ip, reason);
                        }else {
                            OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                            String reason = getReasonPermanent(args);
                            banIPPermanent(p, t, reason);
                        }
                    } else {
                        for (String value : usage) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                } else if (args.length > 2) {
                    String ip = args[0].replace(".", "-");
                    OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                    String reason;
                    int time;
                    String timeUnit;
                    long banEnds;

                    if (!args[0].equalsIgnoreCase("list")) {
                        String[] duration = getDurate(args[1]);
                        if (duration[1].equalsIgnoreCase("s") || duration[1].equalsIgnoreCase("sec")) {
                            reason = getReasonTemporary(args);
                            time = Integer.parseInt(duration[0]);
                            timeUnit = seconds;
                            banEnds = System.currentTimeMillis() + (1000L * time);
                        } else if (duration[1].equalsIgnoreCase("m") || duration[1].equalsIgnoreCase("min")) {
                            reason = getReasonTemporary(args);
                            time = Integer.parseInt(duration[0]);
                            timeUnit = minutes;
                            banEnds = System.currentTimeMillis() + (1000L * 60 * time);
                        } else if (duration[1].equalsIgnoreCase("h") || duration[1].equalsIgnoreCase("hour") || duration[1].equalsIgnoreCase("hours")) {
                            reason = getReasonTemporary(args);
                            time = Integer.parseInt(duration[0]);
                            timeUnit = hours;
                            banEnds = System.currentTimeMillis() + (1000L * 60 * 60 * time);
                        } else if (duration[1].equalsIgnoreCase("d") || duration[1].equalsIgnoreCase("day") || duration[1].equalsIgnoreCase("days")) {
                            reason = getReasonTemporary(args);
                            time = Integer.parseInt(duration[0]);
                            timeUnit = days;
                            banEnds = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * time);
                        } else if (duration[1].equalsIgnoreCase("w") || duration[1].equalsIgnoreCase("week") || duration[1].equalsIgnoreCase("weeks")) {
                            reason = getReasonTemporary(args);
                            time = Integer.parseInt(duration[0]);
                            timeUnit = weeks;
                            banEnds = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 7 * time);
                        } else if (duration[1].equalsIgnoreCase("month") || duration[1].equalsIgnoreCase("months")) {
                            reason = getReasonTemporary(args);
                            time = Integer.parseInt(duration[0]);
                            timeUnit = months;
                            banEnds = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 30 * time);
                        } else if (duration[1].equalsIgnoreCase("y") || duration[1].equalsIgnoreCase("year")) {
                            reason = getReasonTemporary(args);
                            time = Integer.parseInt(duration[0]);
                            timeUnit = years;
                            banEnds = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365 * time);
                        } else {
                            reason = getReasonPermanent(args);
                            if (SavingsFile.getSavedIPs().contains(ip)) {
                                banIPPermanent(p, ip, reason);
                            } else {
                                banIPPermanent(p, t, reason);
                            }
                            return true;
                        }

                        if (SavingsFile.getSavedIPs().contains(ip)) {
                            banIPTemporary(p, ip, reason, banEnds, time, timeUnit);
                        } else {
                            banIPTemporary(p, t, reason, banEnds, time, timeUnit);
                        }
                    } else {
                        for (String value : usage) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                } else {
                    for (String value : usage) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            } else {
                p.sendMessage(SettingsFile.getNoPerm());
            }
        } else {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("list")) {
                    sendList(sender);
                } else {
                    for (String value : usage) {
                        sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            } else if (args.length == 2) {
                if (!args[0].equalsIgnoreCase("list")) {
                    if(args[0].contains(".")) {
                        String ip = args[0].replace(".", "-");
                        String reason = getReasonPermanent(args);
                        banIPPermanent(sender, ip, reason);
                    }else {
                        OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                        String reason = getReasonPermanent(args);
                        banIPPermanent(sender, t, reason);
                    }
                } else {
                    for (String value : usage) {
                        sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            } else if (args.length > 2) {
                if (!args[0].equalsIgnoreCase("list")) {
                    String ip = args[0].replace(".", "-");
                    OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                    String reason;
                    int time;
                    String timeUnit;
                    long banEnds;

                    if (!args[0].equalsIgnoreCase("list")) {
                        String[] duration = getDurate(args[1]);
                        if (duration[1].equalsIgnoreCase("s") || duration[1].equalsIgnoreCase("sec")) {
                            reason = getReasonTemporary(args);
                            time = Integer.parseInt(duration[0]);
                            timeUnit = seconds;
                            banEnds = System.currentTimeMillis() + (1000L * time);
                        } else if (duration[1].equalsIgnoreCase("m") || duration[1].equalsIgnoreCase("min")) {
                            reason = getReasonTemporary(args);
                            time = Integer.parseInt(duration[0]);
                            timeUnit = minutes;
                            banEnds = System.currentTimeMillis() + (1000L * 60 * time);
                        } else if (duration[1].equalsIgnoreCase("h") || duration[1].equalsIgnoreCase("hour") || duration[1].equalsIgnoreCase("hours")) {
                            reason = getReasonTemporary(args);
                            time = Integer.parseInt(duration[0]);
                            timeUnit = hours;
                            banEnds = System.currentTimeMillis() + (1000L * 60 * 60 * time);
                        } else if (duration[1].equalsIgnoreCase("d") || duration[1].equalsIgnoreCase("day") || duration[1].equalsIgnoreCase("days")) {
                            reason = getReasonTemporary(args);
                            time = Integer.parseInt(duration[0]);
                            timeUnit = days;
                            banEnds = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * time);
                        } else if (duration[1].equalsIgnoreCase("w") || duration[1].equalsIgnoreCase("week") || duration[1].equalsIgnoreCase("weeks")) {
                            reason = getReasonTemporary(args);
                            time = Integer.parseInt(duration[0]);
                            timeUnit = weeks;
                            banEnds = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 7 * time);
                        } else if (duration[1].equalsIgnoreCase("month") || duration[1].equalsIgnoreCase("months")) {
                            reason = getReasonTemporary(args);
                            time = Integer.parseInt(duration[0]);
                            timeUnit = months;
                            banEnds = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 30 * time);
                        } else if (duration[1].equalsIgnoreCase("y") || duration[1].equalsIgnoreCase("year")) {
                            reason = getReasonTemporary(args);
                            time = Integer.parseInt(duration[0]);
                            timeUnit = years;
                            banEnds = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365 * time);
                        } else {
                            reason = getReasonPermanent(args);
                            if (SavingsFile.getSavedIPs().contains(ip)) {
                                banIPPermanent(sender, ip, reason);
                            } else {
                                banIPPermanent(sender, t, reason);
                            }
                            return true;
                        }

                        if (SavingsFile.getSavedIPs().contains(ip)) {
                            banIPTemporary(sender, ip, reason, banEnds, time, timeUnit);
                        } else {
                            banIPTemporary(sender, t, reason, banEnds, time, timeUnit);
                        }
                    } else {
                        for (String value : usage) {
                            sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
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

    public static String getReasonPermanent(String[] args) {
        String reason = "";
        for (int i = 1; i < args.length; i++) {
            if (i == 1) {
                reason = args[i];
            } else {
                reason = reason + " " + args[i];
            }
        }
        return reason;
    }

    public static String getReasonTemporary(String[] args) {
        String reason = "";
        for (int i = 2; i < args.length; i++) {
            if (i == 2) {
                reason = args[i];
            } else {
                reason = reason + " " + args[i];
            }
        }
        return reason;
    }

    public static boolean isIPBanned(String ip) {
        return getBannedIPs().contains(ip);
    }

    public static List<String> getBannedIPs() {
        List<String> bannedIPs;
        if(mySQLEnabled) {
            bannedIPs = mySQL.getStringList("IP", SQLTables.BANNED_IPS.getTableName());
            for (String ip : bannedIPs) {
                int punishmentCount = getIPPunishmentCount(ip) - 1;
                try {
                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.BANIP.getTableName() + " WHERE (IP='" + ip + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");

                    if(rs.next()) {
                        if(!rs.getString("DURATE").equalsIgnoreCase("Permanent")) {
                            long dateOfBanEnds = Long.parseLong(rs.getString("DATE_OF_BAN_ENDS"));
                            long now = System.currentTimeMillis();
                            if (dateOfBanEnds - now <= 0) {
                                mySQL.executeUpdate("DELETE FROM " + SQLTables.BANNED_IPS.getTableName() + " WHERE IP='" + ip + "'");
                            }
                        }
                    }
                    mySQL.disconnect();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }else {
            bannedIPs = SavingsFile.getStringListPath("BanIP.BannedIPs");
            for (int i = 0; i < bannedIPs.size(); i++) {
                String ip = bannedIPs.get(i);
                int punishmentCount = getIPPunishmentCount(ip) - 1;
                if (!SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Durate").equalsIgnoreCase("Permanent")) {
                    long dateOfBanEnds = SavingsFile.getLongPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban-Ends");
                    long now = System.currentTimeMillis();
                    if (dateOfBanEnds - now <= 0) {
                        bannedIPs.remove(ip);
                        SavingsFile.setPath("BanIP.BannedIPs", bannedIPs);
                    }
                }
            }
        }
        return bannedIPs;
    }

    public static int getIPPunishmentCount(String ip) {
        if(mySQLEnabled) {
            try {
                ResultSet rs = mySQL.executeQuery("SELECT IP FROM " + SQLTables.BANIP.getTableName() + " WHERE (IP='" + ip + "')");

                int i = 1;
                while (rs.next()) {
                    i++;
                }
                mySQL.disconnect();
                return i;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else {
            if (SavingsFile.isPathSet("Punishment.BanIP." + ip + ".1")) {
                Set<String> keys = SavingsFile.yaml.getConfigurationSection("Punishment.BanIP." + ip).getKeys(false);
                return keys.size() + 1;
            }
        }
        return 1;
    }

    private static String getIPOfOfflinePlayer(OfflinePlayer p) {
        if(mySQLEnabled) {
            try {
                ResultSet rs = mySQL.executeQuery("SELECT IP FROM " + SQLTables.IP.getTableName() + " WHERE UUID='" + p.getUniqueId().toString() + "'");

                String ip = "";
                if (rs.next()) {
                    ip = rs.getString("IP");
                }
                mySQL.disconnect();
                return ip;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else{
            for(String ip : SavingsFile.getConfigurationSection("IP.User")) {
                List<String> uuids = SavingsFile.getStringListPath("IP.User." + ip);
                if(uuids.contains(p.getUniqueId().toString())) {
                    return ip;
                }
            }
        }
        return null;
    }

    public static void banIPPermanent(Player p, OfflinePlayer t, String reason) {
        if (NewSystem.hasPermission(p, permPermanent)) {
            String ip;
            if (t.isOnline()) {
                ip = new ReflectionAPI().getPlayerIP(t.getPlayer()).replace(".", "-");
            } else {
                ip = getIPOfOfflinePlayer(t);
            }

            if (!isIPBanned(ip)) {
                if (ip != null) {
                    if (!blacklist.contains(ip)) {
                        int punishmentCount = getIPPunishmentCount(ip);
                        String dateOfBan = SettingsFile.DateFormat(System.currentTimeMillis());

                        if(mySQLEnabled) {
                            mySQL.executeUpdate("INSERT INTO " + SQLTables.BANNED_IPS.getTableName() + " (IP) VALUES ('" + ip + "')");

                            mySQL.executeUpdate("INSERT INTO " + SQLTables.BANIP.getTableName() + " (IP,PUNISHMENT_COUNT,UUID_BANNED_OF,REASON,DURATE,DATE_OF_BAN,DATE_OF_BAN_ENDS) VALUES ('" + ip + "'," +
                                    "'" + punishmentCount + "'," +
                                    "'" + p.getUniqueId().toString() + "'," +
                                    "'" + reason + "'," +
                                    "'Permanent'," +
                                    "'" + System.currentTimeMillis() + "'," +
                                    "'Permanent')");
                        }else{
                            List<String> bannedPlayers = getBannedIPs();
                            bannedPlayers.add(ip);

                            SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".BannedOf", p.getUniqueId().toString());
                            SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Reason", reason);
                            SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Durate", "Permanent");
                            SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban", System.currentTimeMillis());
                            SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban-Ends", "Permanent");
                            SavingsFile.setPath("BanIP.BannedIPs", bannedPlayers);
                        }

                        for (String key : msgBannedPermanently) {
                            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{IP}", ip.replace("-", ".")));
                        }

                        for (Player all : Bukkit.getOnlinePlayers()) {
                            if (NewSystem.hasPermission(all, perm)) {
                                for (String msg : messageListPermanent) {
                                    all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                            .replace("{IP}", ip.replace("-", "."))
                                            .replace("{Banned-Of}", NewSystem.getName(p))
                                            .replace("{Reason}", reason)
                                            .replace("{Date-Of-Ban}", dateOfBan));
                                }
                            }
                        }

                        List<String> uuids = new ArrayList<>();
                        if(mySQLEnabled) {
                            try {
                                ResultSet rs = mySQL.executeQuery("SELECT UUID FROM " + SQLTables.IP.getTableName() + " WHERE IP='" + ip + "'");

                                while(rs.next()) {
                                    uuids.add(rs.getString("UUID"));
                                }
                                mySQL.disconnect();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }else{
                            uuids = SavingsFile.getStringListPath("IP.User." + ip);
                        }
                        for (String uuid : uuids) {
                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                            if (offlinePlayer.isOnline()) {
                                offlinePlayer.getPlayer().kickPlayer(kickMessagePermanent.replace("{Reason}", reason).replace("{Date-Of-Ban}", dateOfBan));
                            }
                        }
                    } else {
                        for (String msg : msgPlayerCanNotBeBanned) {
                            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                }
            } else {
                for (String value : msgAlreadyBanned) {
                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        } else {
            p.sendMessage(SettingsFile.getNoPerm());
        }
    }

    public static void banIPPermanent(Player p, String ip, String reason) {
        if (NewSystem.hasPermission(p, permPermanent)) {
            if (!isIPBanned(ip)) {
                if (ip != null) {
                    if (!blacklist.contains(ip)) {
                        int punishmentCount = getIPPunishmentCount(ip);
                        String dateOfBan = SettingsFile.DateFormat(System.currentTimeMillis());

                        if(mySQLEnabled) {
                            mySQL.executeUpdate("INSERT INTO " + SQLTables.BANNED_IPS.getTableName() + " (IP) VALUES ('" + ip + "')");

                            mySQL.executeUpdate("INSERT INTO " + SQLTables.BANIP.getTableName() + " (IP,PUNISHMENT_COUNT,UUID_BANNED_OF,REASON,DURATE,DATE_OF_BAN,DATE_OF_BAN_ENDS) VALUES " +
                                    "('" + ip + "'," +
                                    "'" + punishmentCount + "'," +
                                    "'" + p.getUniqueId().toString() + "'," +
                                    "'" + reason + "'," +
                                    "'Permanent'," +
                                    "'" + System.currentTimeMillis() + "'," +
                                    "'Permanent')");
                        }else {
                            List<String> bannedPlayers = getBannedIPs();
                            bannedPlayers.add(ip);

                            SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".BannedOf", p.getUniqueId().toString());
                            SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Reason", reason);
                            SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Durate", "Permanent");
                            SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban", System.currentTimeMillis());
                            SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban-Ends", "Permanent");
                            SavingsFile.setPath("BanIP.BannedIPs", bannedPlayers);
                        }

                        for (String key : msgBannedPermanently) {
                            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{IP}", ip.replace("-", ".")));
                        }

                        for (Player all : Bukkit.getOnlinePlayers()) {
                            if (NewSystem.hasPermission(all, perm)) {
                                for (String msg : messageListPermanent) {
                                    all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                            .replace("{IP}", ip.replace("-", "."))
                                            .replace("{Banned-Of}", NewSystem.getName(p))
                                            .replace("{Reason}", reason)
                                            .replace("{Date-Of-Ban}", dateOfBan));
                                }
                            }
                        }

                        List<String> uuids = new ArrayList<>();
                        if(mySQLEnabled) {
                            try {
                                ResultSet rs = mySQL.executeQuery("SELECT UUID FROM " + SQLTables.IP.getTableName() + " WHERE IP='" + ip + "'");

                                while(rs.next()) {
                                    uuids.add(rs.getString("UUID"));
                                }
                                mySQL.disconnect();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }else{
                            uuids = SavingsFile.getStringListPath("IP.User." + ip);
                        }
                        for (String uuid : uuids) {
                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                            if (offlinePlayer.isOnline()) {
                                offlinePlayer.getPlayer().kickPlayer(kickMessagePermanent.replace("{Reason}", reason).replace("{Date-Of-Ban}", dateOfBan));
                            }
                        }
                    } else {
                        for (String msg : msgPlayerCanNotBeBanned) {
                            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                }
            } else {
                for (String value : msgAlreadyBanned) {
                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        } else {
            p.sendMessage(SettingsFile.getNoPerm());
        }
    }

    public static void banIPPermanent(CommandSender p, OfflinePlayer t, String reason) {
        String ip;
        if (t.isOnline()) {
            ip = new ReflectionAPI().getPlayerIP(t.getPlayer()).replace(".", "-");
        } else {
            ip = getIPOfOfflinePlayer(t);
        }

        if (!isIPBanned(ip)) {
            if (ip != null) {
                if (!blacklist.contains(ip)) {
                    int punishmentCount = getIPPunishmentCount(ip);
                    String dateOfBan = SettingsFile.DateFormat(System.currentTimeMillis());

                    if(mySQLEnabled) {
                        mySQL.executeUpdate("INSERT INTO " + SQLTables.BANNED_IPS.getTableName() + " (IP) VALUES ('" + ip + "')");

                        mySQL.executeUpdate("INSERT INTO " + SQLTables.BANIP.getTableName() + " (IP,PUNISHMENT_COUNT,UUID_BANNED_OF,REASON,DURATE,DATE_OF_BAN,DATE_OF_BAN_ENDS) VALUES " +
                                "('" + ip + "'," +
                                "'" + punishmentCount + "'," +
                                "'Console'," +
                                "'" + reason + "'," +
                                "'Permanent'," +
                                "'" + System.currentTimeMillis() + "'," +
                                "'Permanent')");
                    }else {
                        List<String> bannedPlayers = getBannedIPs();
                        bannedPlayers.add(ip);

                        SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".BannedOf", "Console");
                        SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Reason", reason);
                        SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Durate", "Permanent");
                        SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban", System.currentTimeMillis());
                        SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban-Ends", "Permanent");
                        SavingsFile.setPath("BanIP.BannedIPs", bannedPlayers);
                    }

                    for (String key : msgBannedPermanently) {
                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{IP}", ip.replace("-", ".")));
                    }

                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (NewSystem.hasPermission(all, perm)) {
                            for (String msg : messageListPermanent) {
                                all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{IP}", ip.replace("-", "."))
                                        .replace("{Banned-Of}", SettingsFile.getConsolePrefix())
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Ban}", dateOfBan));
                            }
                        }
                    }

                    List<String> uuids = new ArrayList<>();
                    if(mySQLEnabled) {
                        try {
                            ResultSet rs = mySQL.executeQuery("SELECT UUID FROM " + SQLTables.IP.getTableName() + " WHERE IP='" + ip + "'");

                            while(rs.next()) {
                                uuids.add(rs.getString("UUID"));
                            }
                            mySQL.disconnect();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }else{
                        uuids = SavingsFile.getStringListPath("IP.User." + ip);
                    }
                    for (String uuid : uuids) {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                        if (offlinePlayer.isOnline()) {
                            offlinePlayer.getPlayer().kickPlayer(kickMessagePermanent.replace("{Reason}", reason).replace("{Date-Of-Ban}", dateOfBan));
                        }
                    }
                } else {
                    for (String msg : msgPlayerCanNotBeBanned) {
                        p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }
        } else {
            for (String value : msgAlreadyBanned) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void banIPPermanent(CommandSender p, String ip, String reason) {
        if (!isIPBanned(ip)) {
            if (ip != null) {
                if (!blacklist.contains(ip)) {
                    int punishmentCount = getIPPunishmentCount(ip);
                    String dateOfBan = SettingsFile.DateFormat(System.currentTimeMillis());

                    if(mySQLEnabled) {
                        mySQL.executeUpdate("INSERT INTO " + SQLTables.BANNED_IPS.getTableName() + " (IP) VALUES ('" + ip + "')");

                        mySQL.executeUpdate("INSERT INTO " + SQLTables.BANIP.getTableName() + " (IP,PUNISHMENT_COUNT,UUID_BANNED_OF,REASON,DURATE,DATE_OF_BAN,DATE_OF_BAN_ENDS) VALUES " +
                                "('" + ip + "'," +
                                "'" + punishmentCount + "'," +
                                "'Console'," +
                                "'" + reason + "'," +
                                "'Permanent'," +
                                "'" + System.currentTimeMillis() + "'," +
                                "'Permanent')");
                    }else {
                        List<String> bannedPlayers = getBannedIPs();
                        bannedPlayers.add(ip);

                        SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".BannedOf", "Console");
                        SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Reason", reason);
                        SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Durate", "Permanent");
                        SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban", System.currentTimeMillis());
                        SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban-Ends", "Permanent");
                        SavingsFile.setPath("BanIP.BannedIPs", bannedPlayers);
                    }

                    for (String key : msgBannedPermanently) {
                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{IP}", ip.replace("-", ".")));
                    }

                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (NewSystem.hasPermission(all, perm)) {
                            for (String msg : messageListPermanent) {
                                all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{IP}", ip.replace("-", "."))
                                        .replace("{Banned-Of}", SettingsFile.getConsolePrefix())
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Ban}", dateOfBan));
                            }
                        }
                    }

                    List<String> uuids = new ArrayList<>();
                    if(mySQLEnabled) {
                        try {
                            ResultSet rs = mySQL.executeQuery("SELECT UUID FROM " + SQLTables.IP.getTableName() + " WHERE IP='" + ip + "'");

                            while(rs.next()) {
                                uuids.add(rs.getString("UUID"));
                            }
                            mySQL.disconnect();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }else{
                        uuids = SavingsFile.getStringListPath("IP.User." + ip);
                    }
                    for (String uuid : uuids) {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                        if (offlinePlayer.isOnline()) {
                            offlinePlayer.getPlayer().kickPlayer(kickMessagePermanent.replace("{Reason}", reason).replace("{Date-Of-Ban}", dateOfBan));
                        }
                    }
                } else {
                    for (String msg : msgPlayerCanNotBeBanned) {
                        p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }
        } else {
            for (String value : msgAlreadyBanned) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void banIPTemporary(Player p, OfflinePlayer t, String reason, long banEnds, int number, String word) {
        if (NewSystem.hasPermission(p, permTemporary)) {
            String ip;
            if (t.isOnline()) {
                ip = new ReflectionAPI().getPlayerIP(t.getPlayer()).replace(".", "-");
            } else {
                ip = getIPOfOfflinePlayer(t);
            }

            if (!isIPBanned(ip)) {
                if (ip != null) {
                    if (!blacklist.contains(ip)) {
                        int punishmentCount = getIPPunishmentCount(ip);
                        String dateOfBan = SettingsFile.DateFormat(System.currentTimeMillis());
                        String dateOfBanEnds = SettingsFile.DateFormat(banEnds);
                        String durate = number + " " + word;

                        if(mySQLEnabled) {
                            mySQL.executeUpdate("INSERT INTO " + SQLTables.BANNED_IPS.getTableName() + " (IP) VALUES ('" + ip + "')");

                            mySQL.executeUpdate("INSERT INTO " + SQLTables.BANIP.getTableName() + " (IP,PUNISHMENT_COUNT,UUID_BANNED_OF,REASON,DURATE,DATE_OF_BAN,DATE_OF_BAN_ENDS) VALUES " +
                                    "('" + ip + "'," +
                                    "'" + punishmentCount + "'," +
                                    "'" + p.getUniqueId().toString() + "'," +
                                    "'" + reason + "'," +
                                    "'" + durate + "'," +
                                    "'" + System.currentTimeMillis() + "'," +
                                    "'" + banEnds + "')");
                        }else {
                            List<String> bannedPlayers = getBannedIPs();
                            bannedPlayers.add(ip);

                            SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".BannedOf", p.getUniqueId().toString());
                            SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Reason", reason);
                            SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Durate", durate);
                            SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban", System.currentTimeMillis());
                            SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban-Ends", banEnds);
                            SavingsFile.setPath("BanIP.BannedIPs", bannedPlayers);
                        }

                        for (String key : msgBannedTemporary) {
                            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{IP}", ip.replace("-", ".")).replace("{Durate}", durate));
                        }

                        for (Player all : Bukkit.getOnlinePlayers()) {
                            if (NewSystem.hasPermission(all, perm)) {
                                for (String msg : messageListTemporary) {
                                    all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                            .replace("{IP}", ip.replace("-", "."))
                                            .replace("{Banned-Of}", NewSystem.getName(p))
                                            .replace("{Durate}", durate)
                                            .replace("{Reason}", reason)
                                            .replace("{Date-Of-Ban}", dateOfBan)
                                            .replace("{Date-Of-Ban-Ends}", dateOfBanEnds));
                                }
                            }
                        }

                        List<String> uuids = new ArrayList<>();
                        if(mySQLEnabled) {
                            try {
                                ResultSet rs = mySQL.executeQuery("SELECT UUID FROM " + SQLTables.IP.getTableName() + " WHERE IP='" + ip + "'");

                                while(rs.next()) {
                                    uuids.add(rs.getString("UUID"));
                                }
                                mySQL.disconnect();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }else{
                            uuids = SavingsFile.getStringListPath("IP.User." + ip);
                        }
                        for (String uuid : uuids) {
                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                            if (offlinePlayer.isOnline()) {
                                offlinePlayer.getPlayer().kickPlayer(kickMessageTemporary.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Durate}", durate)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Ban}", dateOfBan)
                                        .replace("{Date-Of-Ban-Ends}", dateOfBanEnds));
                            }
                        }
                    } else {
                        for (String msg : msgPlayerCanNotBeBanned) {
                            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                } else {
                    p.sendMessage(SettingsFile.getOffline());
                }
            } else {
                for (String value : msgAlreadyBanned) {
                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        } else {
            p.sendMessage(SettingsFile.getNoPerm());
        }
    }

    public static void banIPTemporary(Player p, String ip, String reason, long banEnds, int number, String word) {
        if (NewSystem.hasPermission(p, permTemporary)) {
            if (!isIPBanned(ip)) {
                if (ip != null) {
                    if (!blacklist.contains(ip)) {
                        int punishmentCount = getIPPunishmentCount(ip);
                        String dateOfBan = SettingsFile.DateFormat(System.currentTimeMillis());
                        String dateOfBanEnds = SettingsFile.DateFormat(banEnds);
                        String durate = number + " " + word;

                        if(mySQLEnabled) {
                            mySQL.executeUpdate("INSERT INTO " + SQLTables.BANNED_IPS.getTableName() + " (IP) VALUES ('" + ip + "')");

                            mySQL.executeUpdate("INSERT INTO " + SQLTables.BANIP.getTableName() + " (IP,PUNISHMENT_COUNT,UUID_BANNED_OF,REASON,DURATE,DATE_OF_BAN,DATE_OF_BAN_ENDS) VALUES " +
                                    "('" + ip + "'," +
                                    "'" + punishmentCount + "'," +
                                    "'" + p.getUniqueId().toString() + "'," +
                                    "'" + reason + "'," +
                                    "'" + durate + "'," +
                                    "'" + System.currentTimeMillis() + "'," +
                                    "'" + banEnds + "')");
                        }else {
                            List<String> bannedPlayers = getBannedIPs();
                            bannedPlayers.add(ip);

                            SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".BannedOf", p.getUniqueId().toString());
                            SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Reason", reason);
                            SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Durate", durate);
                            SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban", System.currentTimeMillis());
                            SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban-Ends", banEnds);
                            SavingsFile.setPath("BanIP.BannedIPs", bannedPlayers);
                        }

                        for (String key : msgBannedTemporary) {
                            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{IP}", ip.replace("-", ".")).replace("{Durate}", durate));
                        }

                        for (Player all : Bukkit.getOnlinePlayers()) {
                            if (NewSystem.hasPermission(all, perm)) {
                                for (String msg : messageListTemporary) {
                                    all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                            .replace("{IP}", ip.replace("-", "."))
                                            .replace("{Banned-Of}", NewSystem.getName(p))
                                            .replace("{Durate}", durate)
                                            .replace("{Reason}", reason)
                                            .replace("{Date-Of-Ban}", dateOfBan)
                                            .replace("{Date-Of-Ban-Ends}", dateOfBanEnds));
                                }
                            }
                        }

                        List<String> uuids = new ArrayList<>();
                        if(mySQLEnabled) {
                            try {
                                ResultSet rs = mySQL.executeQuery("SELECT UUID FROM " + SQLTables.IP.getTableName() + " WHERE IP='" + ip + "'");

                                while(rs.next()) {
                                    uuids.add(rs.getString("UUID"));
                                }
                                mySQL.disconnect();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }else{
                            uuids = SavingsFile.getStringListPath("IP.User." + ip);
                        }
                        for (String uuid : uuids) {
                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                            if (offlinePlayer.isOnline()) {
                                offlinePlayer.getPlayer().kickPlayer(kickMessageTemporary.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Durate}", durate)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Ban}", dateOfBan)
                                        .replace("{Date-Of-Ban-Ends}", dateOfBanEnds));
                            }
                        }
                    } else {
                        for (String msg : msgPlayerCanNotBeBanned) {
                            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                } else {
                    p.sendMessage(SettingsFile.getOffline());
                }
            } else {
                for (String value : msgAlreadyBanned) {
                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        } else {
            p.sendMessage(SettingsFile.getNoPerm());
        }
    }

    public static void banIPTemporary(CommandSender p, OfflinePlayer t, String reason, long banEnds, int number, String word) {
        String ip;
        if (t.isOnline()) {
            ip = new ReflectionAPI().getPlayerIP(t.getPlayer()).replace(".", "-");
        } else {
            ip = getIPOfOfflinePlayer(t);
        }

        if (!isIPBanned(ip)) {
            if (ip != null) {
                int punishmentCount = getIPPunishmentCount(ip);
                String dateOfBan = SettingsFile.DateFormat(System.currentTimeMillis());
                String dateOfBanEnds = SettingsFile.DateFormat(banEnds);
                String durate = number + " " + word;

                if(mySQLEnabled) {
                    mySQL.executeUpdate("INSERT INTO " + SQLTables.BANNED_IPS.getTableName() + " (IP) VALUES ('" + ip + "')");

                    mySQL.executeUpdate("INSERT INTO " + SQLTables.BANIP.getTableName() + " (IP,PUNISHMENT_COUNT,UUID_BANNED_OF,REASON,DURATE,DATE_OF_BAN,DATE_OF_BAN_ENDS) VALUES " +
                            "('" + ip + "'," +
                            "'" + punishmentCount + "'," +
                            "'Console'," +
                            "'" + reason + "'," +
                            "'" + durate + "'," +
                            "'" + System.currentTimeMillis() + "'," +
                            "'" + banEnds + "')");
                }else {
                    List<String> bannedPlayers = getBannedIPs();
                    bannedPlayers.add(ip);

                    SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".BannedOf", "Console");
                    SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Reason", reason);
                    SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Durate", durate);
                    SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban", System.currentTimeMillis());
                    SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban-Ends", banEnds);
                    SavingsFile.setPath("BanIP.BannedIPs", bannedPlayers);
                }

                for (String key : msgBannedTemporary) {
                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{IP}", ip.replace("-", ".")).replace("{Durate}", durate));
                }

                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (NewSystem.hasPermission(all, perm)) {
                        for (String msg : messageListTemporary) {
                            all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                    .replace("{IP}", ip.replace("-", "."))
                                    .replace("{Banned-Of}", SettingsFile.getConsolePrefix())
                                    .replace("{Durate}", durate)
                                    .replace("{Reason}", reason)
                                    .replace("{Date-Of-Ban}", dateOfBan)
                                    .replace("{Date-Of-Ban-Ends}", dateOfBanEnds));
                        }
                    }
                }

                List<String> uuids = new ArrayList<>();
                if(mySQLEnabled) {
                    try {
                        ResultSet rs = mySQL.executeQuery("SELECT UUID FROM " + SQLTables.IP.getTableName() + " WHERE IP='" + ip + "'");

                        while(rs.next()) {
                            uuids.add(rs.getString("UUID"));
                        }
                        mySQL.disconnect();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }else{
                    uuids = SavingsFile.getStringListPath("IP.User." + ip);
                }
                for (String uuid : uuids) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                    if (offlinePlayer.isOnline()) {
                        offlinePlayer.getPlayer().kickPlayer(kickMessageTemporary.replace("{Prefix}", SettingsFile.getPrefix())
                                .replace("{Durate}", durate)
                                .replace("{Reason}", reason)
                                .replace("{Date-Of-Ban}", dateOfBan)
                                .replace("{Date-Of-Ban-Ends}", dateOfBanEnds));
                    }
                }
            } else {
                p.sendMessage(SettingsFile.getOffline());
            }
        } else {
            for (String value : msgAlreadyBanned) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void banIPTemporary(CommandSender p, String ip, String reason, long banEnds, int number, String word) {
        if (!isIPBanned(ip)) {
            if (ip != null) {
                int punishmentCount = getIPPunishmentCount(ip);
                String dateOfBan = SettingsFile.DateFormat(System.currentTimeMillis());
                String dateOfBanEnds = SettingsFile.DateFormat(banEnds);
                String durate = number + " " + word;

                if(mySQLEnabled) {
                    mySQL.executeUpdate("INSERT INTO " + SQLTables.BANNED_IPS.getTableName() + " (IP) VALUES ('" + ip + "')");

                    mySQL.executeUpdate("INSERT INTO " + SQLTables.BANIP.getTableName() + " (IP,PUNISHMENT_COUNT,UUID_BANNED_OF,REASON,DURATE,DATE_OF_BAN,DATE_OF_BAN_ENDS) VALUES " +
                            "('" + ip + "'," +
                            "'" + punishmentCount + "'," +
                            "'Console'," +
                            "'" + reason + "'," +
                            "'" + durate + "'," +
                            "'" + System.currentTimeMillis() + "'," +
                            "'" + banEnds + "')");
                }else {
                    List<String> bannedPlayers = getBannedIPs();
                    bannedPlayers.add(ip);

                    SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".BannedOf", "Console");
                    SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Reason", reason);
                    SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Durate", durate);
                    SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban", System.currentTimeMillis());
                    SavingsFile.setPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban-Ends", banEnds);
                    SavingsFile.setPath("BanIP.BannedIPs", bannedPlayers);
                }

                for (String key : msgBannedTemporary) {
                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{IP}", ip.replace("-", ".")).replace("{Durate}", durate));
                }

                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (NewSystem.hasPermission(all, perm)) {
                        for (String msg : messageListTemporary) {
                            all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                    .replace("{IP}", ip.replace("-", "."))
                                    .replace("{Banned-Of}", SettingsFile.getConsolePrefix())
                                    .replace("{Durate}", durate)
                                    .replace("{Reason}", reason)
                                    .replace("{Date-Of-Ban}", dateOfBan)
                                    .replace("{Date-Of-Ban-Ends}", dateOfBanEnds));
                        }
                    }
                }

                List<String> uuids = new ArrayList<>();
                if(mySQLEnabled) {
                    try {
                        ResultSet rs = mySQL.executeQuery("SELECT UUID FROM " + SQLTables.IP.getTableName() + " WHERE IP='" + ip + "'");

                        while(rs.next()) {
                            uuids.add(rs.getString("UUID"));
                        }
                        mySQL.disconnect();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }else{
                    uuids = SavingsFile.getStringListPath("IP.User." + ip);
                }
                for (String uuid : uuids) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                    if (offlinePlayer.isOnline()) {
                        offlinePlayer.getPlayer().kickPlayer(kickMessageTemporary.replace("{Prefix}", SettingsFile.getPrefix())
                                .replace("{Durate}", durate)
                                .replace("{Reason}", reason)
                                .replace("{Date-Of-Ban}", dateOfBan)
                                .replace("{Date-Of-Ban-Ends}", dateOfBanEnds));
                    }
                }
            } else {
                p.sendMessage(SettingsFile.getOffline());
            }
        } else {
            for (String value : msgAlreadyBanned) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void sendList(Player p) {
        List<String> bannedIPs = getBannedIPs();

        for (String msg : listMessage) {
            if (msg.contains("{Banned-IPs}")) {
                if (bannedIPs.size() == 0) {
                    p.sendMessage(noPlayerBanned);
                } else {
                    for (String ip : bannedIPs) {
                        if (isIPBanned(ip)) {
                            int punishmentCount = getIPPunishmentCount(ip) - 1;
                            String bannedOf = "";
                            String reason = "";
                            String dateOfBan = "";
                            String durate = "";
                            String dateOfBanEnds = "";
                            if(mySQLEnabled) {
                                try {
                                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.BANIP.getTableName() + " WHERE (IP='" + ip + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");

                                    if (rs.next()) {
                                        reason = rs.getString("REASON");
                                        durate = rs.getString("DURATE");
                                        dateOfBan = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_BAN")));

                                        dateOfBanEnds = rs.getString("DATE_OF_BAN_ENDS");
                                        if(!dateOfBanEnds.equalsIgnoreCase("Permanent")) {
                                            try {
                                                dateOfBanEnds = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_BAN_ENDS")));
                                            }catch (NumberFormatException ignored) {}
                                        }

                                        if(rs.getString("UUID_BANNED_OF").equalsIgnoreCase("Console")) {
                                            bannedOf = SettingsFile.getConsolePrefix();
                                        }else{
                                            OfflinePlayer bannedOfPlayer = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_BANNED_OF")));
                                            bannedOf = NewSystem.getName(bannedOfPlayer);
                                        }
                                    }
                                    mySQL.disconnect();
                                }catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }else {
                                reason = SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Reason");
                                dateOfBan = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban"));
                                durate = SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Durate");

                                if(SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban-Ends").equalsIgnoreCase("Permanent")) {
                                    dateOfBanEnds = SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban-Ends");
                                }else{
                                    dateOfBanEnds = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban-Ends"));
                                }

                                if (SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + punishmentCount + ".BannedOf").equalsIgnoreCase("Console")) {
                                    bannedOf = SettingsFile.getConsolePrefix();
                                } else {
                                    OfflinePlayer bannedOfPlayer = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + punishmentCount + ".BannedOf")));
                                    bannedOf = NewSystem.getName(bannedOfPlayer);
                                }
                            }

                            if (!isBanPermanent(ip)) {
                                String hoverMessage = hoverMessageTemporary.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Banned-Of}", bannedOf)
                                        .replace("{Reason}", reason)
                                        .replace("{Durate}", durate)
                                        .replace("{Date-Of-Ban}", dateOfBan)
                                        .replace("{Date-Of-Ban-Ends}", dateOfBanEnds);
                                TextComponent text = new TextComponent(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Banned-IPs}", ip.replace("-", ".")));
                                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
                                p.spigot().sendMessage(text);
                            } else {
                                String hoverMessage = hoverMessagePermanent.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Banned-Of}", bannedOf)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Ban}", dateOfBan);
                                TextComponent text = new TextComponent(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Banned-IPs}", ip.replace("-", ".")));
                                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
                                p.spigot().sendMessage(text);
                            }
                        }
                    }
                }
            }else{
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Ban-Count}", String.valueOf(bannedIPs.size())));
            }
        }
    }

    public static void sendList(CommandSender p) {
        List<String> bannedIPs = getBannedIPs();

        for (String msg : listMessage) {
            if (msg.contains("{Banned-IPs}")) {
                if (bannedIPs.size() == 0) {
                    p.sendMessage(noPlayerBanned);
                } else {
                    for (String ip : bannedIPs) {
                        if (isIPBanned(ip)) {
                            int punishmentCount = getIPPunishmentCount(ip) - 1;
                            String bannedOf = "";
                            String reason = "";
                            String dateOfBan = "";
                            String durate = "";
                            String dateOfBanEnds = "";
                            if(mySQLEnabled) {
                                try {
                                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.BANIP.getTableName() + " WHERE (IP='" + ip + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");

                                    if (rs.next()) {
                                        reason = rs.getString("REASON");
                                        durate = rs.getString("DURATE");
                                        dateOfBan = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_BAN")));

                                        dateOfBanEnds = rs.getString("DATE_OF_BAN_ENDS");
                                        if(!dateOfBanEnds.equalsIgnoreCase("Permanent")) {
                                            try {
                                                dateOfBanEnds = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_BAN_ENDS")));
                                            }catch (NumberFormatException ignored) {}
                                        }

                                        if(rs.getString("UUID_BANNED_OF").equalsIgnoreCase("Console")) {
                                            bannedOf = SettingsFile.getConsolePrefix();
                                        }else{
                                            OfflinePlayer bannedOfPlayer = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_BANNED_OF")));
                                            bannedOf = NewSystem.getName(bannedOfPlayer);
                                        }
                                    }
                                    mySQL.disconnect();
                                }catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }else {
                                reason = SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Reason");
                                dateOfBan = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban"));
                                durate = SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Durate");

                                if(SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban-Ends").equalsIgnoreCase("Permanent")) {
                                    dateOfBanEnds = SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban-Ends");
                                }else {
                                    dateOfBanEnds = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban-Ends"));
                                }

                                if (SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + punishmentCount + ".BannedOf").equalsIgnoreCase("Console")) {
                                    bannedOf = SettingsFile.getConsolePrefix();
                                } else {
                                    OfflinePlayer bannedOfPlayer = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + punishmentCount + ".BannedOf")));
                                    bannedOf = NewSystem.getName(bannedOfPlayer);
                                }
                            }
                            if (!isBanPermanent(ip)) {
                                p.sendMessage(consoleMessageTemporary.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Banned-IP}", ip.replace("-", "."))
                                        .replace("{Banned-Of}", bannedOf)
                                        .replace("{Reason}", reason)
                                        .replace("{Durate}", durate)
                                        .replace("{Date-Of-Ban}", dateOfBan)
                                        .replace("{Date-Of-Ban-Ends}", dateOfBanEnds));
                            } else {
                                p.sendMessage(consoleMessagePermanent.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Banned-IP}", ip.replace("-", "."))
                                        .replace("{Banned-Of}", bannedOf)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Ban}", dateOfBan));
                            }
                        }
                    }
                }
            }else{
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Ban-Count}", String.valueOf(bannedIPs.size())));
            }
        }
    }

    public static String[] getDurate(String arg) {
        int zahl = 0;
        String zahlString = "";
        String wort = "";
        for(String value : arg.split("")) {
            try {
                zahl = zahl + Integer.parseInt(value);
                zahlString = zahlString + value;
            } catch (NumberFormatException e) {
                wort = wort + value;
            }
        }
        String[] durate = new String[2];
        durate[0] = zahlString;
        durate[1] = wort;
        return durate;
    }

    public static boolean isBanPermanent(String ip) {
        if (isIPBanned(ip)) {
            int punishmentCount = getIPPunishmentCount(ip) -1;
            if (mySQLEnabled) {
                try {
                    ResultSet rs = mySQL.executeQuery("SELECT DURATE FROM " + SQLTables.BANIP.getTableName() + " WHERE (IP='" + ip + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");

                    boolean permanent = false;
                    if(rs.next()) {
                        permanent = rs.getString("DURATE").equalsIgnoreCase("Permanent");
                    }
                    mySQL.disconnect();
                    return permanent;
                }catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                return SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Durate").equalsIgnoreCase("Permanent");
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        List<String> tabCompletions = new ArrayList<>();

        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if (args.length == 1) {
                    String[] completions = {"list"};
                    for(String completion : completions) {
                        if(completion.contains(args[0])) {
                            tabCompletions.add(completion);
                        }
                    }
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (all.getName().contains(args[0])) {
                            tabCompletions.add(all.getName());
                        }
                    }
                }
            }
        }else{
            if (args.length == 1) {
                String[] completions = {"list"};
                for(String completion : completions) {
                    if(completion.contains(args[0])) {
                        tabCompletions.add(completion);
                    }
                }
                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (all.getName().contains(args[0])) {
                        tabCompletions.add(all.getName());
                    }
                }
            }
        }
        return tabCompletions;
    }
}
