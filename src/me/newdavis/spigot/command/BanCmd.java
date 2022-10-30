package me.newdavis.spigot.command;
//Plugin by NewDavis

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.sql.MySQL;
import me.newdavis.spigot.sql.SQLTables;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import me.newdavis.spigot.file.SavingsFile;
import me.newdavis.spigot.plugin.NewSystem;
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

public class BanCmd implements CommandExecutor, TabCompleter {

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
    private static String noBanPerm;
    private static String permPermanent;
    private static List<String> msgBannedPermanent;
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

    public BanCmd() {
        usage = CommandFile.getStringListPath("Command.Ban.Usage");
        perm = CommandFile.getStringPath("Command.Ban.Permission.Use");
        seconds = CommandFile.getStringPath("Command.Ban.Seconds");
        minutes = CommandFile.getStringPath("Command.Ban.Minutes");
        hours = CommandFile.getStringPath("Command.Ban.Hours");
        days = CommandFile.getStringPath("Command.Ban.Days");
        weeks = CommandFile.getStringPath("Command.Ban.Weeks");
        months = CommandFile.getStringPath("Command.Ban.Months");
        years = CommandFile.getStringPath("Command.Ban.Years");
        noBanPerm = CommandFile.getStringPath("Command.Ban.Permission.CanNotBan");
        permPermanent = CommandFile.getStringPath("Command.Ban.Permission.Permanent");
        msgBannedPermanent = CommandFile.getStringListPath("Command.Ban.MessageBannedPermanentPlayer");
        messageListPermanent = CommandFile.getStringListPath("Command.Ban.MessageBannedPermanent");
        msgAlreadyBanned = CommandFile.getStringListPath("Command.Ban.MessagePlayerAlreadyBanned");
        msgPlayerCanNotBeBanned = CommandFile.getStringListPath("Command.Ban.PlayerCanNotGetBanned");
        kickMessagePermanent = CommandFile.getStringPath("Command.Ban.KickMessagePermanent").replace("{Prefix}", SettingsFile.getPrefix());
        permTemporary = CommandFile.getStringPath("Command.Ban.Permission.Temporary");
        msgBannedTemporary = CommandFile.getStringListPath("Command.Ban.MessageBannedTemporaryPlayer");
        messageListTemporary = CommandFile.getStringListPath("Command.Ban.MessageBannedTemporary");
        kickMessageTemporary = CommandFile.getStringPath("Command.Ban.KickMessageTemporary").replace("{Prefix}", SettingsFile.getPrefix());
        listMessage = CommandFile.getStringListPath("Command.Ban.ListMessage");
        noPlayerBanned = CommandFile.getStringPath("Command.Ban.NoPlayerBannedMessage").replace("{Prefix}", SettingsFile.getPrefix());
        hoverMessageTemporary = CommandFile.getStringPath("Command.Ban.HoverMessageTemporary");
        hoverMessagePermanent = CommandFile.getStringPath("Command.Ban.HoverMessagePermanent");
        consoleMessageTemporary = CommandFile.getStringPath("Command.Ban.PlayerListConsoleTemporary");
        consoleMessagePermanent = CommandFile.getStringPath("Command.Ban.PlayerListConsolePermanent");
        bannedPlayers.clear();
        getBannedPlayers();
        if(!NewSystem.loadedCommands.contains(this)) {
            NewSystem.loadedCommands.add(this);
            NewSystem.getInstance().getCommand("ban").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (NewSystem.hasPermission(p, perm)) {
                if(args.length == 1) {
                    if(args[0].equalsIgnoreCase("list")) {
                        if(NewSystem.hasPermission(p, perm)) {
                            sendList(p);
                        }else{
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    }else{
                        for(String value : usage) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                }else if(args.length == 2) {
                    if(!args[0].equalsIgnoreCase("list")) {
                        OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                        String reason = getReasonPermanent(args);
                        banPlayerPermanent(p, t, reason);
                    }else{
                        for(String value : usage) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                }else if (args.length > 2) {
                    OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                    if (t != null) {
                        if(!args[0].equalsIgnoreCase("list")) {
                            String[] duration = getDurate(args[1]);
                            if (duration[1].equalsIgnoreCase("s") || duration[1].equalsIgnoreCase("sec")) {
                                String reason = getReasonTemporary(args);
                                int time = Integer.parseInt(duration[0]);
                                String wort = seconds;
                                long banEnds = System.currentTimeMillis() + (1000L * time);
                                banPlayerTemporary(p, t, reason, banEnds, time, wort);
                            } else if (duration[1].equalsIgnoreCase("m") || duration[1].equalsIgnoreCase("min")) {
                                String reason = getReasonTemporary(args);
                                int time = Integer.parseInt(duration[0]);
                                String wort = minutes;
                                long banEnds = System.currentTimeMillis() + (1000L * 60 * time);
                                banPlayerTemporary(p, t, reason, banEnds, time, wort);
                            } else if (duration[1].equalsIgnoreCase("h") || duration[1].equalsIgnoreCase("hour") || duration[1].equalsIgnoreCase("hours")) {
                                String reason = getReasonTemporary(args);
                                int time = Integer.parseInt(duration[0]);
                                String wort = hours;
                                long banEnds = System.currentTimeMillis() + (1000L * 60 * 60 * time);
                                banPlayerTemporary(p, t, reason, banEnds, time, wort);
                            } else if (duration[1].equalsIgnoreCase("d") || duration[1].equalsIgnoreCase("day") || duration[1].equalsIgnoreCase("days")) {
                                String reason = getReasonTemporary(args);
                                int time = Integer.parseInt(duration[0]);
                                String wort = days;
                                long banEnds = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * time);
                                banPlayerTemporary(p, t, reason, banEnds, time, wort);
                            } else if (duration[1].equalsIgnoreCase("w") || duration[1].equalsIgnoreCase("week") || duration[1].equalsIgnoreCase("weeks")) {
                                String reason = getReasonTemporary(args);
                                int time = Integer.parseInt(duration[0]);
                                String wort = weeks;
                                long banEnds = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 7 * time);
                                banPlayerTemporary(p, t, reason, banEnds, time, wort);
                            } else if (duration[1].equalsIgnoreCase("month") || duration[1].equalsIgnoreCase("months")) {
                                String reason = getReasonTemporary(args);
                                int time = Integer.parseInt(duration[0]);
                                String wort = months;
                                long banEnds = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 30 * time);
                                banPlayerTemporary(p, t, reason, banEnds, time, wort);
                            } else if (duration[1].equalsIgnoreCase("y") || duration[1].equalsIgnoreCase("year")) {
                                String reason = getReasonTemporary(args);
                                int time = Integer.parseInt(duration[0]);
                                String wort = years;
                                long banEnds = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365 * time);
                                banPlayerTemporary(p, t, reason, banEnds, time, wort);
                            } else {
                                String reason = getReasonPermanent(args);
                                banPlayerPermanent(p, t, reason);
                            }
                        }else{
                            for(String value : usage) {
                                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        }
                    } else {
                        p.sendMessage(SettingsFile.getOffline());
                    }
                } else {
                    for(String value : usage) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            } else {
                p.sendMessage(SettingsFile.getNoPerm());
            }
        }else{
            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("list")) {
                    sendList(sender);
                }else{
                    for(String value : usage) {
                        sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }else if(args.length == 2) {
                if(!args[0].equalsIgnoreCase("list")) {
                    OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                    String reason = getReasonPermanent(args);
                    banPlayerPermanent(sender, t, reason);
                }else{
                    for(String value : usage) {
                        sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }else if (args.length > 2) {
                OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                if (t != null) {
                    if(!args[0].equalsIgnoreCase("list")) {
                        String[] duration = getDurate(args[1]);
                        if (duration[1].equalsIgnoreCase("s") || duration[1].equalsIgnoreCase("sec")) {
                            String reason = getReasonTemporary(args);
                            int time = Integer.parseInt(duration[0]);
                            String wort = seconds;
                            long banEnds = System.currentTimeMillis() + (1000L * time);
                            banPlayerTemporary(sender, t, reason, banEnds, time, wort);
                        } else if (duration[1].equalsIgnoreCase("m") || duration[1].equalsIgnoreCase("min")) {
                            String reason = getReasonTemporary(args);
                            int time = Integer.parseInt(duration[0]);
                            String wort = minutes;
                            long banEnds = System.currentTimeMillis() + (1000L * 60 * time);
                            banPlayerTemporary(sender, t, reason, banEnds, time, wort);
                        } else if (duration[1].equalsIgnoreCase("h") || duration[1].equalsIgnoreCase("hour") || duration[1].equalsIgnoreCase("hours")) {
                            String reason = getReasonTemporary(args);
                            int time = Integer.parseInt(duration[0]);
                            String wort = hours;
                            long banEnds = System.currentTimeMillis() + (1000L * 60 * 60 * time);
                            banPlayerTemporary(sender, t, reason, banEnds, time, wort);
                        } else if (duration[1].equalsIgnoreCase("d") || duration[1].equalsIgnoreCase("day") || duration[1].equalsIgnoreCase("days")) {
                            String reason = getReasonTemporary(args);
                            int time = Integer.parseInt(duration[0]);
                            String wort = days;
                            long banEnds = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * time);
                            banPlayerTemporary(sender, t, reason, banEnds, time, wort);
                        } else if (duration[1].equalsIgnoreCase("w") || duration[1].equalsIgnoreCase("week") || duration[1].equalsIgnoreCase("weeks")) {
                            String reason = getReasonTemporary(args);
                            int time = Integer.parseInt(duration[0]);
                            String wort = weeks;
                            long banEnds = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 7 * time);
                            banPlayerTemporary(sender, t, reason, banEnds, time, wort);
                        } else if (duration[1].equalsIgnoreCase("month") || duration[1].equalsIgnoreCase("months")) {
                            String reason = getReasonTemporary(args);
                            int time = Integer.parseInt(duration[0]);
                            String wort = months;
                            long banEnds = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 30 * time);
                            banPlayerTemporary(sender, t, reason, banEnds, time, wort);
                        } else if (duration[1].equalsIgnoreCase("y") || duration[1].equalsIgnoreCase("year")) {
                            String reason = getReasonTemporary(args);
                            int time = Integer.parseInt(duration[0]);
                            String wort = years;
                            long banEnds = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365 * time);
                            banPlayerTemporary(sender, t, reason, banEnds, time, wort);
                        } else {
                            String reason = getReasonPermanent(args);
                            banPlayerPermanent(sender, t, reason);
                        }
                    }else{
                        for(String value : usage) {
                            sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                } else {
                    sender.sendMessage(SettingsFile.getOffline());
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

    public static List<String> bannedPlayers = new ArrayList<>();

    public static boolean isPlayerBanned(OfflinePlayer p) {
        return bannedPlayers.contains(p.getUniqueId().toString());
    }

    public static void getBannedPlayers() {
        if(mySQLEnabled) {
            bannedPlayers = mySQL.getStringList("UUID", SQLTables.BANNED_PLAYERS.getTableName());
            for (int i = 0; i < bannedPlayers.size(); i++) {
                OfflinePlayer t = Bukkit.getOfflinePlayer(UUID.fromString(bannedPlayers.get(i)));
                int punishmentCount = getPlayerPunishmentCount(t) - 1;
                try {
                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.BAN.getTableName() + " WHERE (UUID='" + t.getUniqueId().toString() + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");
                    if(rs.next()) {
                        if(!rs.getString("DURATE").equalsIgnoreCase("Permanent")) {
                            long dateOfBanEnds = Long.parseLong(rs.getString("DATE_OF_BAN_ENDS"));
                            long now = System.currentTimeMillis();
                            if (dateOfBanEnds - now <= 0) {
                                mySQL.executeUpdate("DELETE FROM " + SQLTables.BANNED_PLAYERS.getTableName() + " WHERE UUID='" + t.getUniqueId().toString() + "'");
                                bannedPlayers.remove(t.getUniqueId().toString());
                            }
                        }
                    }
                    mySQL.disconnect();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }else {
            bannedPlayers = SavingsFile.getStringListPath("Ban.BannedPlayers");
            for (int i = 0; i < bannedPlayers.size(); i++) {
                OfflinePlayer t = Bukkit.getOfflinePlayer(UUID.fromString(bannedPlayers.get(i)));
                int punishmentCount = getPlayerPunishmentCount(t) - 1;
                if (!SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Durate").equalsIgnoreCase("Permanent")) {
                    long dateOfBanEnds = SavingsFile.getLongPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Ban-Ends");
                    long now = System.currentTimeMillis();
                    if (dateOfBanEnds - now <= 0) {
                        bannedPlayers.remove(t.getUniqueId().toString());
                        SavingsFile.setPath("Ban.BannedPlayers", bannedPlayers);
                    }
                }
            }
        }
    }

    public static int getPlayerPunishmentCount(OfflinePlayer p) {
        if(mySQLEnabled) {
            try {
                ResultSet rs = mySQL.executeQuery("SELECT UUID FROM " + SQLTables.BAN.getTableName() + " WHERE (UUID='" + p.getUniqueId().toString() + "')");

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
            if (SavingsFile.isPathSet("Punishment.Ban." + p.getUniqueId() + ".1")) {
                Set<String> keys = SavingsFile.yaml.getConfigurationSection("Punishment.Ban." + p.getUniqueId()).getKeys(false);
                return keys.size() + 1;
            }
        }
        return 1;
    }

    public static void banPlayerPermanent(Player p, OfflinePlayer t, String reason) {
        if(!NewSystem.hasPermission(t, noBanPerm)) {
            if (NewSystem.hasPermission(p, permPermanent)) {
                if (!isPlayerBanned(t)) {
                    int punishmentCount = getPlayerPunishmentCount(t);
                    String dateOfBan = SettingsFile.DateFormat(System.currentTimeMillis());

                    if(mySQLEnabled) {
                        String sql = "INSERT INTO " + SQLTables.BANNED_PLAYERS.getTableName() + " (UUID) VALUES ('" + t.getUniqueId().toString() + "')";
                        mySQL.executeUpdate(sql);

                        sql = "INSERT INTO " + SQLTables.BAN.getTableName() + " (UUID,PUNISHMENT_COUNT,UUID_BANNED_OF,REASON,DURATE,DATE_OF_BAN,DATE_OF_BAN_ENDS) VALUES ('" + t.getUniqueId().toString() + "'," +
                                "'" + punishmentCount + "'," +
                                "'" + p.getUniqueId().toString() + "'," +
                                "'" + reason + "'," +
                                "'Permanent'," +
                                "'" + System.currentTimeMillis() + "'," +
                                "'Permanent')";
                        mySQL.executeUpdate(sql);
                    }else{
                        bannedPlayers.add(t.getUniqueId().toString());

                        SavingsFile.setPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".BannedOf", p.getUniqueId().toString());
                        SavingsFile.setPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Reason", reason);
                        SavingsFile.setPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Durate", "Permanent");
                        SavingsFile.setPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Ban", System.currentTimeMillis());
                        SavingsFile.setPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Ban-Ends", "Permanent");
                        SavingsFile.setPath("Ban.BannedPlayers", bannedPlayers);
                    }

                    for(String key : msgBannedPermanent) {
                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                    }

                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (NewSystem.hasPermission(all, perm)) {
                            for (String msg : messageListPermanent) {
                                all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true))
                                        .replace("{Banned-Of}", NewSystem.getName(p, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName")))
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Ban}", dateOfBan));
                            }
                        }
                    }

                    if (t.isOnline()) {
                        t.getPlayer().kickPlayer(kickMessagePermanent.replace("{Reason}", reason).replace("{Date-Of-Ban}", dateOfBan));
                    }
                } else {
                    for(String value : msgAlreadyBanned) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            } else {
                p.sendMessage(SettingsFile.getNoPerm());
            }
        }else {
            for(String value : msgPlayerCanNotBeBanned) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void banPlayerPermanent(CommandSender p, OfflinePlayer t, String reason) {
        if (!isPlayerBanned(t)) {
            int punishmentCount = getPlayerPunishmentCount(t);
            String dateOfBan = SettingsFile.DateFormat(System.currentTimeMillis());

            if(mySQLEnabled) {
                String sql = "INSERT INTO " + SQLTables.BANNED_PLAYERS.getTableName() + " (UUID) VALUES ('" + t.getUniqueId().toString() + "')";
                mySQL.executeUpdate(sql);

                sql = "INSERT INTO " + SQLTables.BAN.getTableName() + " (UUID,PUNISHMENT_COUNT,UUID_BANNED_OF,REASON,DURATE,DATE_OF_BAN,DATE_OF_BAN_ENDS) VALUES ('" + t.getUniqueId().toString() + "'," +
                        "'" + punishmentCount + "'," +
                        "'Console'," +
                        "'" + reason + "'," +
                        "'Permanent'," +
                        "'" + System.currentTimeMillis() + "'," +
                        "'Permanent')";
                mySQL.executeUpdate(sql);
            }else {
                bannedPlayers.add(t.getUniqueId().toString());

                SavingsFile.setPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".BannedOf", "Console");
                SavingsFile.setPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Reason", reason);
                SavingsFile.setPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Durate", "Permanent");
                SavingsFile.setPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Ban", System.currentTimeMillis());
                SavingsFile.setPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Ban-Ends", "Permanent");
                SavingsFile.setPath("Ban.BannedPlayers", bannedPlayers);
            }

            for (String key : msgBannedPermanent) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
            }

            for (Player all : Bukkit.getOnlinePlayers()) {
                if (NewSystem.hasPermission(all, perm)) {
                    for (String msg : messageListPermanent) {
                        all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                .replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true))
                                .replace("{Banned-Of}", SettingsFile.getConsolePrefix())
                                .replace("{Reason}", reason)
                                .replace("{Date-Of-Ban}", dateOfBan));
                    }
                }
            }

            if (t.isOnline()) {
                t.getPlayer().kickPlayer(kickMessagePermanent.replace("{Reason}", reason).replace("{Date-Of-Ban}", dateOfBan));
            }
        } else {
            for (String value : msgAlreadyBanned) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void banPlayerTemporary(Player p, OfflinePlayer t, String reason, long banEnds, int number, String word) {
        if(!NewSystem.hasPermission(t, noBanPerm)) {
            if (NewSystem.hasPermission(p, permTemporary)) {
                if (!isPlayerBanned(t)) {
                    int punishmentCount = getPlayerPunishmentCount(t);
                    String dateOfBan = SettingsFile.DateFormat(System.currentTimeMillis());
                    String dateOfBanEnds = SettingsFile.DateFormat(banEnds);
                    String durate = number + " " + word;

                    if(mySQLEnabled) {
                        String sql = "INSERT INTO " + SQLTables.BANNED_PLAYERS.getTableName() + " (UUID) VALUES ('" + t.getUniqueId().toString() + "')";
                        mySQL.executeUpdate(sql);

                        sql = "INSERT INTO " + SQLTables.BAN.getTableName() + " (UUID,PUNISHMENT_COUNT,UUID_BANNED_OF,REASON,DURATE,DATE_OF_BAN,DATE_OF_BAN_ENDS) VALUES ('" + t.getUniqueId().toString() + "'," +
                                "'" + punishmentCount + "'," +
                                "'" + p.getUniqueId().toString() + "'," +
                                "'" + reason + "'," +
                                "'" + durate + "'," +
                                "'" + System.currentTimeMillis() + "'," +
                                "'" + banEnds + "')";
                        mySQL.executeUpdate(sql);
                    }else {
                        bannedPlayers.add(t.getUniqueId().toString());

                        SavingsFile.setPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".BannedOf", p.getUniqueId().toString());
                        SavingsFile.setPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Reason", reason);
                        SavingsFile.setPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Durate", durate);
                        SavingsFile.setPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Ban", System.currentTimeMillis());
                        SavingsFile.setPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Ban-Ends", banEnds);
                        SavingsFile.setPath("Ban.BannedPlayers", bannedPlayers);
                    }


                    for(String key : msgBannedTemporary) {
                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Durate}", durate));
                    }

                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (NewSystem.hasPermission(all, perm)) {
                            for (String msg : messageListTemporary) {
                                all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true))
                                        .replace("{Banned-Of}", NewSystem.getName(p, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName")))
                                        .replace("{Durate}", durate)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Ban}", dateOfBan)
                                        .replace("{Date-Of-Ban-Ends}", dateOfBanEnds));
                            }
                        }
                    }

                    if (t.isOnline()) {
                        t.getPlayer().kickPlayer(kickMessageTemporary.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Durate}", durate).replace("{Reason}", reason).replace("{Date-Of-Ban}", dateOfBan).replace("{Date-Of-Ban-Ends}", dateOfBanEnds));
                    }
                } else {
                    for(String value : msgAlreadyBanned) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            } else {
                p.sendMessage(SettingsFile.getNoPerm());
            }
        } else {
            for(String value : msgPlayerCanNotBeBanned) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void banPlayerTemporary(CommandSender p, OfflinePlayer t, String reason, long banEnds, int number, String word) {
        if (!isPlayerBanned(t)) {
            int punishmentCount = getPlayerPunishmentCount(t);
            String dateOfBan = SettingsFile.DateFormat(System.currentTimeMillis());
            String dateOfBanEnds = SettingsFile.DateFormat(banEnds);
            String durate = number + " " + word;

            if(mySQLEnabled) {
                String sql = "INSERT INTO " + SQLTables.BANNED_PLAYERS.getTableName() + " (UUID) VALUES ('" + t.getUniqueId().toString() + "')";
                mySQL.executeUpdate(sql);

                sql = "INSERT INTO " + SQLTables.BAN.getTableName() + " (UUID,PUNISHMENT_COUNT,UUID_BANNED_OF,REASON,DURATE,DATE_OF_BAN,DATE_OF_BAN_ENDS) VALUES ('" + t.getUniqueId().toString() + "'," +
                        "'" + punishmentCount + "'," +
                        "'Console'," +
                        "'" + reason + "'," +
                        "'" + durate + "'," +
                        "'" + System.currentTimeMillis() + "'," +
                        "'" + banEnds + "')";
                mySQL.executeUpdate(sql);
            }else {
                bannedPlayers.add(t.getUniqueId().toString());

                SavingsFile.setPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".BannedOf", "Console");
                SavingsFile.setPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Reason", reason);
                SavingsFile.setPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Durate", durate);
                SavingsFile.setPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Ban", System.currentTimeMillis());
                SavingsFile.setPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Ban-Ends", banEnds);
                SavingsFile.setPath("Ban.BannedPlayers", bannedPlayers);
            }

            for (String key : msgBannedTemporary) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Durate}", durate));
            }

            for (Player all : Bukkit.getOnlinePlayers()) {
                if (NewSystem.hasPermission(all, perm)) {
                    for (String msg : messageListTemporary) {
                        all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                .replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true))
                                .replace("{Banned-Of}", SettingsFile.getConsolePrefix())
                                .replace("{Durate}", durate)
                                .replace("{Reason}", reason)
                                .replace("{Date-Of-Ban}", dateOfBan)
                                .replace("{Date-Of-Ban-Ends}", dateOfBanEnds));
                    }
                }
            }

            if (t.isOnline()) {
                t.getPlayer().kickPlayer(kickMessageTemporary.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Durate}", durate).replace("{Reason}", reason).replace("{Date-Of-Ban}", dateOfBan).replace("{Date-Of-Ban-Ends}", dateOfBanEnds));
            }
        } else {
            for (String value : msgAlreadyBanned) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void sendList(Player p) {
        for (String msg : listMessage) {
            if (msg.contains("{Banned-Player}")) {
                if (bannedPlayers.size() == 0) {
                    p.sendMessage(noPlayerBanned);
                } else {
                    for (String uuid : bannedPlayers) {
                        OfflinePlayer t = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                        if (isPlayerBanned(t)) {
                            int punishmentCount = getPlayerPunishmentCount(t) - 1;
                            String bannedOf = "";
                            String reason = "";
                            String dateOfBan = "";
                            String durate= "";
                            String dateOfBanEnds = "";
                            if(mySQLEnabled) {
                                try {
                                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.BAN.getTableName() + " WHERE (UUID='" + t.getUniqueId().toString() + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");

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
                                            bannedOf = NewSystem.getName(bannedOfPlayer, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"));
                                        }
                                    }
                                    mySQL.disconnect();
                                }catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }else{
                                reason = SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Reason");
                                dateOfBan = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Ban"));
                                durate = SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Durate");

                                if(SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Ban-Ends").equalsIgnoreCase("Permanent")) {
                                    dateOfBanEnds = SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Ban-Ends");
                                }else{
                                    dateOfBanEnds = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Ban-Ends"));
                                }

                                if (SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".BannedOf").equalsIgnoreCase("Console")) {
                                    bannedOf = SettingsFile.getConsolePrefix();
                                } else {
                                    OfflinePlayer bannedOfPlayer = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".BannedOf")));
                                    bannedOf = NewSystem.getName(bannedOfPlayer, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"));
                                }
                            }
                            if (!isBanPermanent(t)) {
                                String hoverMessage = hoverMessageTemporary.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Banned-Of}", bannedOf)
                                        .replace("{Reason}", reason)
                                        .replace("{Durate}", durate)
                                        .replace("{Date-Of-Ban}", dateOfBan)
                                        .replace("{Date-Of-Ban-Ends}", dateOfBanEnds);
                                TextComponent text = new TextComponent(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Banned-Player}", NewSystem.getName(t, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"))));
                                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
                                p.spigot().sendMessage(text);
                            } else {
                                String hoverMessage = hoverMessagePermanent.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Banned-Of}", bannedOf)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Ban}", dateOfBan);
                                TextComponent text = new TextComponent(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Banned-Player}", NewSystem.getName(t, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"))));
                                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
                                p.spigot().sendMessage(text);
                            }
                        }
                    }
                }
            }else{
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Ban-Count}", String.valueOf(bannedPlayers.size())));
            }
        }
    }

    public static void sendList(CommandSender p) {
        for (String msg : listMessage) {
            if (msg.contains("{Banned-Player}")) {
                if (bannedPlayers.size() == 0) {
                    p.sendMessage(noPlayerBanned);
                } else {
                    for (String uuid : bannedPlayers) {
                        OfflinePlayer t = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                        if (isPlayerBanned(t)) {
                            int punishmentCount = getPlayerPunishmentCount(t) - 1;
                            String bannedOf = "";
                            String reason = "";
                            String dateOfBan = "";
                            String durate= "";
                            String dateOfBanEnds = "";
                            if(mySQLEnabled) {
                                try {
                                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.BAN.getTableName() + " WHERE (UUID='" + t.getUniqueId().toString() + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");

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
                                            bannedOf = NewSystem.getName(bannedOfPlayer, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"));
                                        }
                                    }
                                    mySQL.disconnect();
                                }catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }else {
                                reason = SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Reason");
                                dateOfBan = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Ban"));
                                durate = SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Durate");

                                if(SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Ban-Ends").equalsIgnoreCase("Permanent")) {
                                    dateOfBanEnds = SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Ban-Ends");
                                }else{
                                    dateOfBanEnds = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Ban-Ends"));
                                }

                                if (SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".BannedOf").equalsIgnoreCase("Console")) {
                                    bannedOf = SettingsFile.getConsolePrefix();
                                } else {
                                    OfflinePlayer bannedOfPlayer = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".BannedOf")));
                                    bannedOf = NewSystem.getName(bannedOfPlayer, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"));
                                }
                            }
                            if (!isBanPermanent(t)) {
                                p.sendMessage(consoleMessageTemporary.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Banned-Player}", NewSystem.getName(t, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName")))
                                        .replace("{Banned-Of}", bannedOf)
                                        .replace("{Reason}", reason)
                                        .replace("{Durate}", durate)
                                        .replace("{Date-Of-Ban}", dateOfBan)
                                        .replace("{Date-Of-Ban-Ends}", dateOfBanEnds));
                            } else {
                                p.sendMessage(consoleMessagePermanent.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Banned-Player}", NewSystem.getName(t, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName")))
                                        .replace("{Banned-Of}", bannedOf)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Ban}", dateOfBan));
                            }
                        }
                    }
                }
            }else{
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Ban-Count}", String.valueOf(bannedPlayers.size())));
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

    public static boolean isBanPermanent(OfflinePlayer t) {
        if(isPlayerBanned(t)) {
            int punishmentCount = getPlayerPunishmentCount(t) -1;
            if(mySQLEnabled) {
                try {
                    ResultSet rs = mySQL.executeQuery("SELECT DURATE FROM " + SQLTables.BAN.getTableName() + " WHERE (UUID='" + t.getUniqueId().toString() + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");

                    boolean permanent = false;
                    if(rs.next()) {
                        permanent = rs.getString("DURATE").equalsIgnoreCase("Permanent");
                    }
                    mySQL.disconnect();
                    return permanent;
                }catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }else {
                return SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Durate").equalsIgnoreCase("Permanent");
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
