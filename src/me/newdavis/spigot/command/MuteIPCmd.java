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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MuteIPCmd implements CommandExecutor, TabCompleter {

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
    private static List<String> msgMutedPermanently;
    private static List<String> messageListPermanent;
    private static List<String> msgAlreadyMuted;
    private static List<String> msgIPCanNotBeMuted;
    private static String permTemporary;
    private static List<String> msgMutedTemporary;
    private static List<String> messageListTemporary;
    private static List<String> listMessage;
    private static String noIPMuted;
    private static String hoverMessageTemporary;
    private static String hoverMessagePermanent;
    private static String consoleMessageTemporary;
    private static String consoleMessagePermanent;
    public static List<String> chatMessagePermanent;
    public static List<String> chatMessageTemporary;

    public void init() {
        usage = CommandFile.getStringListPath("Command.MuteIP.Usage");
        perm = CommandFile.getStringPath("Command.MuteIP.Permission.Use");
        seconds = CommandFile.getStringPath("Command.MuteIP.Seconds");
        minutes = CommandFile.getStringPath("Command.MuteIP.Minutes");
        hours = CommandFile.getStringPath("Command.MuteIP.Hours");
        days = CommandFile.getStringPath("Command.MuteIP.Days");
        weeks = CommandFile.getStringPath("Command.MuteIP.Weeks");
        months = CommandFile.getStringPath("Command.MuteIP.Months");
        years = CommandFile.getStringPath("Command.MuteIP.Years");
        blacklist = CommandFile.getStringListPath("Command.MuteIP.Blacklist");
        permPermanent = CommandFile.getStringPath("Command.MuteIP.Permission.Permanent");
        msgMutedPermanently = CommandFile.getStringListPath("Command.MuteIP.MessageIPMutedPermanentPlayer");
        messageListPermanent = CommandFile.getStringListPath("Command.MuteIP.MessageIPMutedPermanent");
        msgAlreadyMuted = CommandFile.getStringListPath("Command.MuteIP.MessageIPAlreadyMuted");
        msgIPCanNotBeMuted = CommandFile.getStringListPath("Command.MuteIP.MessageIPCanNotGetMuted");
        permTemporary = CommandFile.getStringPath("Command.MuteIP.Permission.Temporary");
        msgMutedTemporary = CommandFile.getStringListPath("Command.MuteIP.MessageIPMutedTemporaryPlayer");
        messageListTemporary = CommandFile.getStringListPath("Command.MuteIP.MessageIPMutedTemporary");
        listMessage = CommandFile.getStringListPath("Command.MuteIP.ListMessage");
        noIPMuted = CommandFile.getStringPath("Command.MuteIP.NoIPMutedMessage").replace("{Prefix}", SettingsFile.getPrefix());
        hoverMessageTemporary = CommandFile.getStringPath("Command.MuteIP.HoverMessageTemporary");
        hoverMessagePermanent = CommandFile.getStringPath("Command.MuteIP.HoverMessagePermanent");
        consoleMessageTemporary = CommandFile.getStringPath("Command.MuteIP.IPListConsoleTemporary");
        consoleMessagePermanent = CommandFile.getStringPath("Command.MuteIP.IPListConsolePermanent");
        chatMessagePermanent = CommandFile.getStringListPath("Command.MuteIP.ChatMessagePermanent");
        chatMessageTemporary = CommandFile.getStringListPath("Command.MuteIP.ChatMessageTemporary");
        getMutedIPs();
        NewSystem.getInstance().getCommand("muteip").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if(args.length == 1) {
                    if (args[0].equalsIgnoreCase("list")) {
                        sendList(p);
                    } else {
                        for(String value : usage) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                }else if(args.length == 2) {
                    if (!args[0].equalsIgnoreCase("list")) {
                        OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                        String reason = getReasonPermanent(args);
                        muteIPPermanent(p, t, reason);
                    }else{
                        for(String value : usage) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                }else if (args.length > 2) {
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
                                muteIPPermanent(p, ip, reason);
                            } else {
                                muteIPPermanent(p, t, reason);
                            }
                            return true;
                        }

                        if (SavingsFile.getSavedIPs().contains(ip)) {
                            muteIPTemporary(p, ip, reason, banEnds, time, timeUnit);
                        } else {
                            muteIPTemporary(p, t, reason, banEnds, time, timeUnit);
                        }
                    }else{
                        for(String value : usage) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
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
            if(args.length == 1) {
                if (args[0].equalsIgnoreCase("list")) {
                    sendList(sender);
                } else {
                    for(String value : usage) {
                        sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }else if(args.length == 2) {
                if (!args[0].equalsIgnoreCase("list")) {
                    OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                    String reason = getReasonPermanent(args);
                    muteIPPermanent(sender, t, reason);
                }else{
                    for(String value : usage) {
                        sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }else if (args.length > 2) {
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
                            muteIPPermanent(sender, ip, reason);
                        } else {
                            muteIPPermanent(sender, t, reason);
                        }
                        return true;
                    }

                    if (SavingsFile.getSavedIPs().contains(ip)) {
                        muteIPTemporary(sender, ip, reason, banEnds, time, timeUnit);
                    } else {
                        muteIPTemporary(sender, t, reason, banEnds, time, timeUnit);
                    }
                }else{
                    for(String value : usage) {
                        sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            } else {
                for(String value : usage) {
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

    public static List<String> mutedIPs = new ArrayList<>();

    public static boolean isIPMuted(String ip) {
        return mutedIPs.contains(ip);
    }

    public static void getMutedIPs() {
        if(mySQLEnabled) {
            mutedIPs = mySQL.getStringList("IP", SQLTables.MUTED_IPS.getTableName());
            for (String ip : mutedIPs) {
                int punishmentCount = getIPPunishmentCount(ip) - 1;
                try {
                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.MUTEIP.getTableName() + " WHERE " +
                            "(IP='" + ip + "' AND " +
                            "PUNISHMENT_COUNT='" + punishmentCount + "')");

                    if(rs.next()) {
                        if(!rs.getString("DURATE").equalsIgnoreCase("Permanent")) {
                            long dateOfMuteEnds = Long.parseLong(rs.getString("DATE_OF_MUTE_ENDS"));
                            long now = System.currentTimeMillis();
                            if (dateOfMuteEnds - now <= 0) {
                                mySQL.executeUpdate("DELETE FROM " + SQLTables.MUTED_IPS.getTableName() + " WHERE IP='" + ip + "'");
                                mutedIPs.remove(ip);
                            }
                        }
                    }
                    mySQL.disconnect();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }else {
            mutedIPs = SavingsFile.getStringListPath("MuteIP.MutedIPs");
            if (SavingsFile.isPathSet("MuteIP.MutedIPs")) {
                for (int i = 0; i < mutedIPs.size(); i++) {
                    String ip = mutedIPs.get(i);
                    int punishmentCount = getIPPunishmentCount(ip) - 1;
                    if (!SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Durate").equalsIgnoreCase("Permanent")) {
                        long dateOfMuteEnds = SavingsFile.getLongPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Date-Of-Mute-Ends");
                        long now = System.currentTimeMillis();
                        if (dateOfMuteEnds - now <= 0) {
                            mutedIPs.remove(ip);
                            SavingsFile.setPath("MuteIP.MutedIPs", mutedIPs);
                        }
                    }
                }
            }
        }
    }

    public static int getIPPunishmentCount(String ip) {
        if(mySQLEnabled) {
            if(mySQL.hasNext("SELECT IP FROM " + SQLTables.MUTED_IPS.getTableName() + " WHERE IP='" + ip + "'")) {
                try {
                    ResultSet rs = mySQL.executeQuery("SELECT IP FROM " + SQLTables.MUTEIP.getTableName() + " WHERE (IP='" + ip + "')");

                    int i = 1;
                    while(rs.next()) {
                        i++;
                    }
                    mySQL.disconnect();
                    return i;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }else {
            if (SavingsFile.isPathSet("Punishment.MuteIP." + ip + ".1")) {
                Set<String> keys = SavingsFile.yaml.getConfigurationSection("Punishment.MuteIP." + ip).getKeys(false);
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

    public static void muteIPPermanent(Player p, OfflinePlayer t, String reason) {
        if (NewSystem.hasPermission(p, permPermanent)) {
            String ip;
            if (t.isOnline()) {
                ip = new ReflectionAPI().getPlayerIP(t.getPlayer()).replace(".", "-");
            } else {
                ip = getIPOfOfflinePlayer(t);
            }

            if (!isIPMuted(ip)) {
                if(ip != null) {
                    if (!blacklist.contains(ip)) {
                        int punishmentCount = getIPPunishmentCount(ip);
                        String dateOfMute = SettingsFile.DateFormat(System.currentTimeMillis());

                        mutedIPs.add(ip);
                        if(mySQLEnabled) {
                            mySQL.executeUpdate("INSERT INTO " + SQLTables.MUTED_IPS.getTableName() + " (IP) VALUES ('" + ip + "')");

                            mySQL.executeUpdate("INSERT INTO " + SQLTables.MUTEIP.getTableName() + " (IP,PUNISHMENT_COUNT,UUID_MUTED_OF,REASON,DURATE,DATE_OF_MUTE,DATE_OF_MUTE_ENDS) VALUES " +
                                    "('" + ip + "'," +
                                    "'" + punishmentCount + "'," +
                                    "'" + p.getUniqueId().toString() + "'," +
                                    "'" + reason + "'," +
                                    "'" + "Permanent" + "'," +
                                    "'" + System.currentTimeMillis() + "'," +
                                    "'" + "Permanent" + "')");
                        }else {
                            SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".MutedOf", p.getUniqueId().toString());
                            SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Reason", reason);
                            SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Durate", "Permanent");
                            SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Date-Of-Mute", System.currentTimeMillis());
                            SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Date-Of-Mute-Ends", "Permanent");
                            SavingsFile.setPath("MuteIP.MutedIPs", mutedIPs);
                        }

                        for (String key : msgMutedPermanently) {
                            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{IP}", ip.replace("-", ".")));
                        }

                        for (Player all : Bukkit.getOnlinePlayers()) {
                            if (NewSystem.hasPermission(all, perm)) {
                                for (String msg : messageListPermanent) {
                                    all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                            .replace("{IP}", ip.replace("-", "."))
                                            .replace("{Muted-Of}", NewSystem.getName(p, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName")))
                                            .replace("{Reason}", reason)
                                            .replace("{Date-Of-Mute}", dateOfMute));
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
                                for (String msg : chatMessagePermanent) {
                                    offlinePlayer.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                            .replace("{Reason}", reason)
                                            .replace("{Date-Of-Mute}", dateOfMute));
                                }
                            }
                        }
                    }else{
                        for(String msg : msgIPCanNotBeMuted) {
                            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                }else{
                    p.sendMessage(SettingsFile.getOffline());
                }
            } else {
                for (String value : msgAlreadyMuted) {
                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        } else {
            p.sendMessage(SettingsFile.getNoPerm());
        }
    }

    public static void muteIPPermanent(Player p, String ip, String reason) {
        if (NewSystem.hasPermission(p, permPermanent)) {
            if (!isIPMuted(ip)) {
                if(ip != null) {
                    if (!blacklist.contains(ip)) {
                        int punishmentCount = getIPPunishmentCount(ip);
                        String dateOfMute = SettingsFile.DateFormat(System.currentTimeMillis());

                        mutedIPs.add(ip);
                        if(mySQLEnabled) {
                            mySQL.executeUpdate("INSERT INTO " + SQLTables.MUTED_IPS.getTableName() + " (IP) VALUES ('" + ip + "')");

                            mySQL.executeUpdate("INSERT INTO " + SQLTables.MUTEIP.getTableName() + " (IP,PUNISHMENT_COUNT,UUID_MUTED_OF,REASON,DURATE,DATE_OF_MUTE,DATE_OF_MUTE_ENDS) VALUES " +
                                    "('" + ip + "'," +
                                    "'" + punishmentCount + "'," +
                                    "'" + p.getUniqueId().toString() + "'," +
                                    "'" + reason + "'," +
                                    "'" + "Permanent" + "'," +
                                    "'" + System.currentTimeMillis() + "'," +
                                    "'" + "Permanent" + "')");
                        }else {
                            SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".MutedOf", p.getUniqueId().toString());
                            SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Reason", reason);
                            SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Durate", "Permanent");
                            SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Date-Of-Mute", System.currentTimeMillis());
                            SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Date-Of-Mute-Ends", "Permanent");
                            SavingsFile.setPath("MuteIP.MutedIPs", mutedIPs);
                        }

                        for (String key : msgMutedPermanently) {
                            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{IP}", ip.replace("-", ".")));
                        }

                        for (Player all : Bukkit.getOnlinePlayers()) {
                            if (NewSystem.hasPermission(all, perm)) {
                                for (String msg : messageListPermanent) {
                                    all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                            .replace("{IP}", ip.replace("-", "."))
                                            .replace("{Muted-Of}", NewSystem.getName(p, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName")))
                                            .replace("{Reason}", reason)
                                            .replace("{Date-Of-Mute}", dateOfMute));
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
                                for (String msg : chatMessagePermanent) {
                                    offlinePlayer.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                            .replace("{Reason}", reason)
                                            .replace("{Date-Of-Mute}", dateOfMute));
                                }
                            }
                        }
                    }else{
                        for(String msg : msgIPCanNotBeMuted) {
                            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                }else{
                    p.sendMessage(SettingsFile.getOffline());
                }
            } else {
                for (String value : msgAlreadyMuted) {
                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        } else {
            p.sendMessage(SettingsFile.getNoPerm());
        }
    }

    public static void muteIPPermanent(CommandSender p, OfflinePlayer t, String reason) {
        String ip;
        if (t.isOnline()) {
            ip = new ReflectionAPI().getPlayerIP(t.getPlayer()).replace(".", "-");
        } else {
            ip = getIPOfOfflinePlayer(t);
        }

        if (!isIPMuted(ip)) {
            if (ip != null) {
                if (!blacklist.contains(ip)) {
                    int punishmentCount = getIPPunishmentCount(ip);
                    String dateOfMute = SettingsFile.DateFormat(System.currentTimeMillis());

                    mutedIPs.add(ip);
                    if(mySQLEnabled) {
                        mySQL.executeUpdate("INSERT INTO " + SQLTables.MUTED_IPS.getTableName() + " (IP) VALUES ('" + ip + "')");

                        mySQL.executeUpdate("INSERT INTO " + SQLTables.MUTEIP.getTableName() + " (IP,PUNISHMENT_COUNT,UUID_MUTED_OF,REASON,DURATE,DATE_OF_MUTE,DATE_OF_MUTE_ENDS) VALUES " +
                                "('" + ip + "'," +
                                "'" + punishmentCount + "'," +
                                "'Console'," +
                                "'" + reason + "'," +
                                "'" + "Permanent" + "'," +
                                "'" + System.currentTimeMillis() + "'," +
                                "'" + "Permanent" + "')");
                    }else {
                        SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".MutedOf", "Console");
                        SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Reason", reason);
                        SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Durate", "Permanent");
                        SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Date-Of-Mute", System.currentTimeMillis());
                        SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Date-Of-Mute-Ends", "Permanent");
                        SavingsFile.setPath("MuteIP.MutedIPs", mutedIPs);
                    }

                    for (String key : msgMutedPermanently) {
                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{IP}", ip.replace("-", ".")));
                    }

                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (NewSystem.hasPermission(all, perm)) {
                            for (String msg : messageListPermanent) {
                                all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{IP}", ip.replace("-", "."))
                                        .replace("{Muted-Of}", SettingsFile.getConsolePrefix())
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Mute}", dateOfMute));
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
                            for (String msg : chatMessagePermanent) {
                                offlinePlayer.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Mute}", dateOfMute));
                            }
                        }
                    }
                } else {
                    for (String msg : msgIPCanNotBeMuted) {
                        p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            } else {
                p.sendMessage(SettingsFile.getOffline());
            }
        } else {
            for (String value : msgAlreadyMuted) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void muteIPPermanent(CommandSender p, String ip, String reason) {
        if (!isIPMuted(ip)) {
            if (ip != null) {
                if (!blacklist.contains(ip)) {
                    int punishmentCount = getIPPunishmentCount(ip);
                    String dateOfMute = SettingsFile.DateFormat(System.currentTimeMillis());

                    mutedIPs.add(ip);
                    if(mySQLEnabled) {
                        mySQL.executeUpdate("INSERT INTO " + SQLTables.MUTED_IPS.getTableName() + " (IP) VALUES ('" + ip + "')");

                        mySQL.executeUpdate("INSERT INTO " + SQLTables.MUTEIP.getTableName() + " (IP,PUNISHMENT_COUNT,UUID_MUTED_OF,REASON,DURATE,DATE_OF_MUTE,DATE_OF_MUTE_ENDS) VALUES " +
                                "('" + ip + "'," +
                                "'" + punishmentCount + "'," +
                                "'Console'," +
                                "'" + reason + "'," +
                                "'" + "Permanent" + "'," +
                                "'" + System.currentTimeMillis() + "'," +
                                "'" + "Permanent" + "')");
                    }else {
                        SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".MutedOf", "Console");
                        SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Reason", reason);
                        SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Durate", "Permanent");
                        SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Date-Of-Mute", System.currentTimeMillis());
                        SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Date-Of-Mute-Ends", "Permanent");
                        SavingsFile.setPath("MuteIP.MutedIPs", mutedIPs);
                    }

                    for (String key : msgMutedPermanently) {
                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{IP}", ip.replace("-", ".")));
                    }

                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (NewSystem.hasPermission(all, perm)) {
                            for (String msg : messageListPermanent) {
                                all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{IP}", ip.replace("-", "."))
                                        .replace("{Muted-Of}", SettingsFile.getConsolePrefix())
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Mute}", dateOfMute));
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
                            for (String msg : chatMessagePermanent) {
                                offlinePlayer.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Mute}", dateOfMute));
                            }
                        }
                    }
                } else {
                    for (String msg : msgIPCanNotBeMuted) {
                        p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            } else {
                p.sendMessage(SettingsFile.getOffline());
            }
        } else {
            for (String value : msgAlreadyMuted) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void muteIPTemporary(Player p, OfflinePlayer t, String reason, long muteEnds, int number, String word) {
        if(NewSystem.hasPermission(p, permTemporary)) {
            String ip;
            if (t.isOnline()) {
                ip = new ReflectionAPI().getPlayerIP(t.getPlayer()).replace(".", "-");
            } else {
                ip = getIPOfOfflinePlayer(t);
            }

            if (!isIPMuted(ip)) {
                if (ip != null) {
                    if (!blacklist.contains(ip)) {
                        int punishmentCount = getIPPunishmentCount(ip);
                        String dateOfMute = SettingsFile.DateFormat(System.currentTimeMillis());
                        String dateOfMuteEnds = SettingsFile.DateFormat(muteEnds);
                        String durate = number + " " + word;

                        mutedIPs.add(ip);
                        if(mySQLEnabled) {
                            mySQL.executeUpdate("INSERT INTO " + SQLTables.MUTED_IPS.getTableName() + " (IP) VALUES ('" + ip + "')");

                            mySQL.executeUpdate("INSERT INTO " + SQLTables.MUTEIP.getTableName() + " (IP,PUNISHMENT_COUNT,UUID_MUTED_OF,REASON,DURATE,DATE_OF_MUTE,DATE_OF_MUTE_ENDS) VALUES " +
                                    "('" + ip + "'," +
                                    "'" + punishmentCount + "'," +
                                    "'" + p.getUniqueId().toString() + "'," +
                                    "'" + reason + "'," +
                                    "'" + durate + "'," +
                                    "'" + System.currentTimeMillis() + "'," +
                                    "'" + muteEnds + "')");
                        }else {
                            SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".MutedOf", p.getUniqueId().toString());
                            SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Reason", reason);
                            SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Durate", durate);
                            SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Date-Of-Mute", System.currentTimeMillis());
                            SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Date-Of-Mute-Ends", muteEnds);
                            SavingsFile.setPath("MuteIP.MutedIPs", mutedIPs);
                        }

                        for (String key : msgMutedTemporary) {
                            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{IP}", ip.replace("-", ".")).replace("{Durate}", durate));
                        }

                        for (Player all : Bukkit.getOnlinePlayers()) {
                            if (NewSystem.hasPermission(all, perm)) {
                                for (String msg : messageListTemporary) {
                                    all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                            .replace("{IP}", ip.replace("-", "."))
                                            .replace("{Muted-Of}", NewSystem.getName(p, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName")))
                                            .replace("{Durate}", durate)
                                            .replace("{Reason}", reason)
                                            .replace("{Date-Of-Mute}", dateOfMute)
                                            .replace("{Date-Of-Mute-Ends}", dateOfMuteEnds));
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
                                for (String msg : chatMessageTemporary) {
                                    offlinePlayer.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                            .replace("{Durate}", durate)
                                            .replace("{Reason}", reason)
                                            .replace("{Date-Of-Mute}", dateOfMute)
                                            .replace("{Date-Of-Mute-Ends}", dateOfMuteEnds));
                                }
                            }
                        }
                    } else {
                        for (String msg : msgIPCanNotBeMuted) {
                            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                } else {
                    p.sendMessage(SettingsFile.getOffline());
                }
            } else {
                for (String value : msgAlreadyMuted) {
                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }
    }

    public static void muteIPTemporary(Player p, String ip, String reason, long muteEnds, int number, String word) {
        if(NewSystem.hasPermission(p, permTemporary)) {
            if (!isIPMuted(ip)) {
                if (ip != null) {
                    if (!blacklist.contains(ip)) {
                        int punishmentCount = getIPPunishmentCount(ip);
                        String dateOfMute = SettingsFile.DateFormat(System.currentTimeMillis());
                        String dateOfMuteEnds = SettingsFile.DateFormat(muteEnds);
                        String durate = number + " " + word;

                        mutedIPs.add(ip);
                        if(mySQLEnabled) {
                            mySQL.executeUpdate("INSERT INTO " + SQLTables.MUTED_IPS.getTableName() + " (IP) VALUES ('" + ip + "')");

                            mySQL.executeUpdate("INSERT INTO " + SQLTables.MUTEIP.getTableName() + " (IP,PUNISHMENT_COUNT,UUID_MUTED_OF,REASON,DURATE,DATE_OF_MUTE,DATE_OF_MUTE_ENDS) VALUES " +
                                    "('" + ip + "'," +
                                    "'" + punishmentCount + "'," +
                                    "'" + p.getUniqueId().toString() + "'," +
                                    "'" + reason + "'," +
                                    "'" + durate + "'," +
                                    "'" + System.currentTimeMillis() + "'," +
                                    "'" + muteEnds + "')");
                        }else {
                            SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".MutedOf", p.getUniqueId().toString());
                            SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Reason", reason);
                            SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Durate", durate);
                            SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Date-Of-Mute", System.currentTimeMillis());
                            SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Date-Of-Mute-Ends", muteEnds);
                            SavingsFile.setPath("MuteIP.MutedIPs", mutedIPs);
                        }

                        for (String key : msgMutedTemporary) {
                            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{IP}", ip.replace("-", ".")).replace("{Durate}", durate));
                        }

                        for (Player all : Bukkit.getOnlinePlayers()) {
                            if (NewSystem.hasPermission(all, perm)) {
                                for (String msg : messageListTemporary) {
                                    all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                            .replace("{IP}", ip.replace("-", "."))
                                            .replace("{Muted-Of}", NewSystem.getName(p, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName")))
                                            .replace("{Durate}", durate)
                                            .replace("{Reason}", reason)
                                            .replace("{Date-Of-Mute}", dateOfMute)
                                            .replace("{Date-Of-Mute-Ends}", dateOfMuteEnds));
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
                                for (String msg : chatMessageTemporary) {
                                    offlinePlayer.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                            .replace("{Durate}", durate)
                                            .replace("{Reason}", reason)
                                            .replace("{Date-Of-Mute}", dateOfMute)
                                            .replace("{Date-Of-Mute-Ends}", dateOfMuteEnds));
                                }
                            }
                        }
                    } else {
                        for (String msg : msgIPCanNotBeMuted) {
                            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                } else {
                    p.sendMessage(SettingsFile.getOffline());
                }
            } else {
                for (String value : msgAlreadyMuted) {
                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }
    }

    public static void muteIPTemporary(CommandSender p, OfflinePlayer t, String reason, long muteEnds, int number, String word) {
        String ip;
        if (t.isOnline()) {
            ip = new ReflectionAPI().getPlayerIP(t.getPlayer()).replace(".", "-");
        } else {
            ip = getIPOfOfflinePlayer(t);
        }

        if (!isIPMuted(ip)) {
            if (ip != null) {
                if (!blacklist.contains(ip)) {
                    int punishmentCount = getIPPunishmentCount(ip);
                    String dateOfMute = SettingsFile.DateFormat(System.currentTimeMillis());
                    String dateOfMuteEnds = SettingsFile.DateFormat(muteEnds);
                    String durate = number + " " + word;

                    mutedIPs.add(ip);
                    if(mySQLEnabled) {
                        mySQL.executeUpdate("INSERT INTO " + SQLTables.MUTED_IPS.getTableName() + " (IP) VALUES ('" + ip + "')");

                        mySQL.executeUpdate("INSERT INTO " + SQLTables.MUTEIP.getTableName() + " (IP,PUNISHMENT_COUNT,UUID_MUTED_OF,REASON,DURATE,DATE_OF_MUTE,DATE_OF_MUTE_ENDS) VALUES " +
                                "('" + ip + "'," +
                                "'" + punishmentCount + "'," +
                                "'Console'," +
                                "'" + reason + "'," +
                                "'" + durate + "'," +
                                "'" + System.currentTimeMillis() + "'," +
                                "'" + muteEnds + "')");
                    }else {
                        SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".MutedOf", "Console");
                        SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Reason", reason);
                        SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Durate", durate);
                        SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Date-Of-Mute", System.currentTimeMillis());
                        SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Date-Of-Mute-Ends", muteEnds);
                        SavingsFile.setPath("MuteIP.MutedIPs", mutedIPs);
                    }

                    for (String key : msgMutedTemporary) {
                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{IP}", ip.replace("-", ".")).replace("{Durate}", durate));
                    }

                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (NewSystem.hasPermission(all, perm)) {
                            for (String msg : messageListTemporary) {
                                all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{IP}", ip.replace("-", "."))
                                        .replace("{Muted-Of}", SettingsFile.getConsolePrefix())
                                        .replace("{Durate}", durate)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Mute}", dateOfMute)
                                        .replace("{Date-Of-Mute-Ends}", dateOfMuteEnds));
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
                            for (String msg : chatMessageTemporary) {
                                offlinePlayer.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Durate}", durate)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Mute}", dateOfMute)
                                        .replace("{Date-Of-Mute-Ends}", dateOfMuteEnds));
                            }
                        }
                    }
                } else {
                    for (String msg : msgIPCanNotBeMuted) {
                        p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            } else {
                p.sendMessage(SettingsFile.getOffline());
            }
        } else {
            for (String value : msgAlreadyMuted) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void muteIPTemporary(CommandSender p, String ip, String reason, long muteEnds, int number, String word) {
        if (!isIPMuted(ip)) {
            if (ip != null) {
                if (!blacklist.contains(ip)) {
                    int punishmentCount = getIPPunishmentCount(ip);
                    String dateOfMute = SettingsFile.DateFormat(System.currentTimeMillis());
                    String dateOfMuteEnds = SettingsFile.DateFormat(muteEnds);
                    String durate = number + " " + word;

                    mutedIPs.add(ip);
                    if(mySQLEnabled) {
                        mySQL.executeUpdate("INSERT INTO " + SQLTables.MUTED_IPS.getTableName() + " (IP) VALUES ('" + ip + "')");

                        mySQL.executeUpdate("INSERT INTO " + SQLTables.MUTEIP.getTableName() + " (IP,PUNISHMENT_COUNT,UUID_MUTED_OF,REASON,DURATE,DATE_OF_MUTE,DATE_OF_MUTE_ENDS) VALUES " +
                                "('" + ip + "'," +
                                "'" + punishmentCount + "'," +
                                "'Console'," +
                                "'" + reason + "'," +
                                "'" + durate + "'," +
                                "'" + System.currentTimeMillis() + "'," +
                                "'" + muteEnds + "')");
                    }else {
                        SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".MutedOf", "Console");
                        SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Reason", reason);
                        SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Durate", durate);
                        SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Date-Of-Mute", System.currentTimeMillis());
                        SavingsFile.setPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Date-Of-Mute-Ends", muteEnds);
                        SavingsFile.setPath("MuteIP.MutedIPs", mutedIPs);
                    }

                    for (String key : msgMutedTemporary) {
                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{IP}", ip.replace("-", ".")).replace("{Durate}", durate));
                    }

                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (NewSystem.hasPermission(all, perm)) {
                            for (String msg : messageListTemporary) {
                                all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{IP}", ip.replace("-", "."))
                                        .replace("{Muted-Of}", SettingsFile.getConsolePrefix())
                                        .replace("{Durate}", durate)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Mute}", dateOfMute)
                                        .replace("{Date-Of-Mute-Ends}", dateOfMuteEnds));
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
                            for (String msg : chatMessageTemporary) {
                                offlinePlayer.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Durate}", durate)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Mute}", dateOfMute)
                                        .replace("{Date-Of-Mute-Ends}", dateOfMuteEnds));
                            }
                        }
                    }
                } else {
                    for (String msg : msgIPCanNotBeMuted) {
                        p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            } else {
                p.sendMessage(SettingsFile.getOffline());
            }
        } else {
            for (String value : msgAlreadyMuted) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void sendList(Player p) {
        for (String msg : listMessage) {
            if (msg.contains("{Muted-IPs}")) {
                if (mutedIPs.size() == 0) {
                    p.sendMessage(noIPMuted.replace("{Prefix}", SettingsFile.getPrefix()));
                } else {
                    for (String ip : mutedIPs) {
                        if (isIPMuted(ip)) {
                            int punishmentCount = getIPPunishmentCount(ip) - 1;
                            String mutedOf = "";
                            String reason = "";
                            String dateOfMute = "";
                            String durate = "";
                            String dateOfMuteEnds = "";
                            if(mySQLEnabled) {
                                try {
                                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.MUTEIP.getTableName() + " WHERE (IP='" + ip + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");

                                    if (rs.next()) {
                                        reason = rs.getString("REASON");
                                        durate = rs.getString("DURATE");
                                        dateOfMute = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_MUTE")));

                                        dateOfMuteEnds = rs.getString("DATE_OF_MUTE_ENDS");
                                        if(!dateOfMuteEnds.equalsIgnoreCase("Permanent")) {
                                            try {
                                                dateOfMuteEnds = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_MUTE_ENDS")));
                                            }catch (NumberFormatException ignored) {}
                                        }

                                        if(rs.getString("UUID_MUTED_OF").equalsIgnoreCase("Console")) {
                                            mutedOf = SettingsFile.getConsolePrefix();
                                        }else{
                                            OfflinePlayer bannedOfPlayer = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_MUTED_OF")));
                                            mutedOf = NewSystem.getName(bannedOfPlayer, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"));
                                        }
                                    }
                                    mySQL.disconnect();
                                }catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }else {
                                reason = SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Reason");
                                dateOfMute = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Date-Of-Mute"));
                                durate = SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Durate");

                                if (SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Date-Of-Mute-Ends").equalsIgnoreCase("Permanent")) {
                                    dateOfMuteEnds = SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Date-Of-Mute-Ends");
                                } else {
                                    dateOfMuteEnds = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Date-Of-Mute-Ends"));
                                }

                                if (SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".MutedOf").equalsIgnoreCase("Console")) {
                                    OfflinePlayer muteOfPlayer = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".MutedOf")));
                                    mutedOf = NewSystem.getName(muteOfPlayer, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"));
                                } else {
                                    mutedOf = SettingsFile.getConsolePrefix();
                                }
                            }

                            if (!isMutePermanent(ip)) {
                                String hoverMessage = hoverMessageTemporary.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Muted-Of}", mutedOf)
                                        .replace("{Reason}", reason)
                                        .replace("{Durate}", durate)
                                        .replace("{Date-Of-Mute}", dateOfMute)
                                        .replace("{Date-Of-Mute-Ends}", dateOfMuteEnds);
                                TextComponent text = new TextComponent(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Muted-IPs}", ip.replace("-", ".")));
                                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
                                p.spigot().sendMessage(text);
                            } else {
                                String hoverMessage = hoverMessagePermanent.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Muted-Of}", mutedOf)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Mute}", dateOfMute);
                                TextComponent text = new TextComponent(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Muted-IPs}", ip.replace("-", ".")));
                                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
                                p.spigot().sendMessage(text);
                            }
                        }
                    }
                }
            }else{
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Mute-Count}", String.valueOf(mutedIPs.size())));
            }
        }
    }

    public static void sendList(CommandSender p) {
        for (String msg : listMessage) {
            if (msg.contains("{Muted-IPs}")) {
                if (mutedIPs.size() == 0) {
                    p.sendMessage(noIPMuted.replace("{Prefix}", SettingsFile.getPrefix()));
                } else {
                    for (String ip : mutedIPs) {
                        if (isIPMuted(ip)) {
                            int punishmentCount = getIPPunishmentCount(ip) - 1;
                            String mutedOf = "";
                            String reason = "";
                            String dateOfMute = "";
                            String durate = "";
                            String dateOfMuteEnds = "";
                            if(mySQLEnabled) {
                                try {
                                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.MUTEIP.getTableName() + " WHERE (IP='" + ip + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");

                                    if (rs.next()) {
                                        reason = rs.getString("REASON");
                                        durate = rs.getString("DURATE");
                                        dateOfMute = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_MUTE")));

                                        dateOfMuteEnds = rs.getString("DATE_OF_MUTE_ENDS");
                                        if(!dateOfMuteEnds.equalsIgnoreCase("Permanent")) {
                                            try {
                                                dateOfMuteEnds = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_MUTE_ENDS")));
                                            }catch (NumberFormatException ignored) {}
                                        }

                                        if(rs.getString("UUID_MUTED_OF").equalsIgnoreCase("Console")) {
                                            mutedOf = SettingsFile.getConsolePrefix();
                                        }else{
                                            OfflinePlayer bannedOfPlayer = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_MUTED_OF")));
                                            mutedOf = NewSystem.getName(bannedOfPlayer, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"));
                                        }
                                    }
                                    mySQL.disconnect();
                                }catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }else {
                                reason = SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Reason");
                                dateOfMute = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Date-Of-Mute"));
                                durate = SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Durate");

                                if (SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Date-Of-Mute-Ends").equalsIgnoreCase("Permanent")) {
                                    dateOfMuteEnds = SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Date-Of-Mute-Ends");
                                } else {
                                    dateOfMuteEnds = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Date-Of-Mute-Ends"));
                                }

                                if (SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".MutedOf").equalsIgnoreCase("Console")) {
                                    OfflinePlayer muteOfPlayer = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".MutedOf")));
                                    mutedOf = NewSystem.getName(muteOfPlayer, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"));
                                } else {
                                    mutedOf = SettingsFile.getConsolePrefix();
                                }
                            }

                            if (!isMutePermanent(ip)) {
                                String message = consoleMessageTemporary.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Muted-IP}", ip.replace("-", "."))
                                        .replace("{Muted-Of}", mutedOf)
                                        .replace("{Reason}", reason)
                                        .replace("{Durate}", durate)
                                        .replace("{Date-Of-Mute}", dateOfMute)
                                        .replace("{Date-Of-Mute-Ends}", dateOfMuteEnds);
                                p.sendMessage(message);
                            } else {
                                String message = consoleMessagePermanent.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Muted-IP}", ip.replace("-", "."))
                                        .replace("{Muted-Of}", mutedOf)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Mute}", dateOfMute);
                                p.sendMessage(message);
                            }
                        }
                    }
                }
            }else{
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Mute-Count}", String.valueOf(mutedIPs.size())));
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

    public static boolean isMutePermanent(String ip) {
        if(isIPMuted(ip)) {
            int punishmentCount = getIPPunishmentCount(ip) -1;
            if (mySQLEnabled) {
                try {
                    ResultSet rs = mySQL.executeQuery("SELECT DURATE FROM " + SQLTables.MUTEIP.getTableName() + " WHERE (IP='" + ip + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");

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
                return SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Durate").equalsIgnoreCase("Permanent");
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
                    String[] completions = {"list"};
                    for(String completion : completions) {
                        if(completion.contains(args[0])) {
                            tabCompletions.add(completion);
                        }
                    }

                    for(Player all : Bukkit.getOnlinePlayers()) {
                        if(all.getName().contains(args[0])) {
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

                for(Player all : Bukkit.getOnlinePlayers()) {
                    if(all.getName().contains(args[0])) {
                        tabCompletions.add(all.getName());
                    }
                }
            }
        }

        return tabCompletions;
    }
}
