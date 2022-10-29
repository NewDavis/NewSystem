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

public class MuteCmd implements CommandExecutor, TabCompleter {

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
    private static String noMutePerm;
    private static String permPermanent;
    private static List<String> msgMutedPermanent;
    private static List<String> messageListPermanent;
    private static List<String> msgAlreadyMuted;
    private static List<String> msgPlayerCanNotBeMuted;
    private static String permTemporary;
    private static List<String> msgMutedTemporary;
    private static List<String> messageListTemporary;
    private static List<String> listMessage;
    private static String noPlayerMuted = "";
    private static String hoverMessageTemporary;
    private static String hoverMessagePermanent;
    private static String consoleMessageTemporary;
    private static String consoleMessagePermanent;
    public static List<String> chatMessagePermanent = CommandFile.getStringListPath("Command.Mute.ChatMessagePermanent");
    public static List<String> chatMessageTemporary = CommandFile.getStringListPath("Command.Mute.ChatMessageTemporary");

    public void init() {
        usage = CommandFile.getStringListPath("Command.Mute.Usage");
        perm = CommandFile.getStringPath("Command.Mute.Permission.Use");
        seconds = CommandFile.getStringPath("Command.Mute.Seconds");
        minutes = CommandFile.getStringPath("Command.Mute.Minutes");
        hours = CommandFile.getStringPath("Command.Mute.Hours");
        days = CommandFile.getStringPath("Command.Mute.Days");
        weeks = CommandFile.getStringPath("Command.Mute.Weeks");
        months = CommandFile.getStringPath("Command.Mute.Months");
        years = CommandFile.getStringPath("Command.Mute.Years");
        noMutePerm = CommandFile.getStringPath("Command.Mute.Permission.CanNotMute");
        permPermanent = CommandFile.getStringPath("Command.Mute.Permission.Permanent");
        msgMutedPermanent = CommandFile.getStringListPath("Command.Mute.MessageMutedPermanentPlayer");
        messageListPermanent = CommandFile.getStringListPath("Command.Mute.MessageMutedPermanent");
        msgAlreadyMuted = CommandFile.getStringListPath("Command.Mute.MessagePlayerAlreadyMuted");
        msgPlayerCanNotBeMuted = CommandFile.getStringListPath("Command.Mute.PlayerCanNotGetMuted");
        permTemporary = CommandFile.getStringPath("Command.Mute.Permission.Temporary");
        msgMutedTemporary = CommandFile.getStringListPath("Command.Mute.MessageMutedTemporaryPlayer");
        messageListTemporary = CommandFile.getStringListPath("Command.Mute.MessageMutedTemporary");
        listMessage = CommandFile.getStringListPath("Command.Mute.ListMessage");
        noPlayerMuted = CommandFile.getStringPath("Command.Mute.NoPlayerMutedMessage").replace("{Prefix}", SettingsFile.getPrefix());
        hoverMessageTemporary = CommandFile.getStringPath("Command.Mute.HoverMessageTemporary");
        hoverMessagePermanent = CommandFile.getStringPath("Command.Mute.HoverMessagePermanent");
        consoleMessageTemporary = CommandFile.getStringPath("Command.Mute.PlayerListConsoleTemporary");
        consoleMessagePermanent = CommandFile.getStringPath("Command.Mute.PlayerListConsolePermanent");
        chatMessagePermanent = CommandFile.getStringListPath("Command.Mute.ChatMessagePermanent");
        chatMessageTemporary = CommandFile.getStringListPath("Command.Mute.ChatMessageTemporary");
        getMutedPlayers();
        NewSystem.getInstance().getCommand("mute").setExecutor(this);
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
                        mutePlayerPermanent(p, t, reason);
                    }else{
                        for(String value : usage) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                }else if (args.length > 2) {
                    OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                        if (!args[0].equalsIgnoreCase("list")) {
                            String[] duration = getDurate(args[1]);
                            if (duration[1].equalsIgnoreCase("s") || duration[1].equalsIgnoreCase("sec")) {
                                String reason = getReasonTemporary(args);
                                int time = Integer.parseInt(duration[0]);
                                String wort = seconds;
                                long muteEnds = System.currentTimeMillis() + (1000L * time);
                                mutePlayerTemporary(p, t, reason, muteEnds, time, wort);
                            } else if (duration[1].equalsIgnoreCase("m") || duration[1].equalsIgnoreCase("min")) {
                                String reason = getReasonTemporary(args);
                                int time = Integer.parseInt(duration[0]);
                                String wort = minutes;
                                long muteEnds = System.currentTimeMillis() + (1000L * 60 * time);
                                mutePlayerTemporary(p, t, reason, muteEnds, time, wort);
                            } else if (duration[1].equalsIgnoreCase("h") || duration[1].equalsIgnoreCase("hour") || duration[1].equalsIgnoreCase("hours")) {
                                String reason = getReasonTemporary(args);
                                int time = Integer.parseInt(duration[0]);
                                String wort = hours;
                                long muteEnds = System.currentTimeMillis() + (1000L * 60 * 60 * time);
                                mutePlayerTemporary(p, t, reason, muteEnds, time, wort);
                            } else if (duration[1].equalsIgnoreCase("d") || duration[1].equalsIgnoreCase("day") || duration[1].equalsIgnoreCase("days")) {
                                String reason = getReasonTemporary(args);
                                int time = Integer.parseInt(duration[0]);
                                String wort = days;
                                long muteEnds = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * time);
                                mutePlayerTemporary(p, t, reason, muteEnds, time, wort);
                            } else if (duration[1].equalsIgnoreCase("w") || duration[1].equalsIgnoreCase("week") || duration[1].equalsIgnoreCase("weeks")) {
                                String reason = getReasonTemporary(args);
                                int time = Integer.parseInt(duration[0]);
                                String wort = weeks;
                                long muteEnds = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 7 * time);
                                mutePlayerTemporary(p, t, reason, muteEnds, time, wort);
                            } else if (duration[1].equalsIgnoreCase("month") || duration[1].equalsIgnoreCase("months")) {
                                String reason = getReasonTemporary(args);
                                int time = Integer.parseInt(duration[0]);
                                String wort = months;
                                long muteEnds = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 30 * time);
                                mutePlayerTemporary(p, t, reason, muteEnds, time, wort);
                            } else if (duration[1].equalsIgnoreCase("y") || duration[1].equalsIgnoreCase("year")) {
                                String reason = getReasonTemporary(args);
                                int time = Integer.parseInt(duration[0]);
                                String wort = years;
                                long muteEnds = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365 * time);
                                mutePlayerTemporary(p, t, reason, muteEnds, time, wort);
                            } else {
                                String reason = getReasonPermanent(args);
                                mutePlayerPermanent(p, t, reason);
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
                    mutePlayerPermanent(sender, t, reason);
                }else{
                    for(String value : usage) {
                        sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }else if (args.length > 2) {
                OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                if (!args[0].equalsIgnoreCase("list")) {
                    String[] duration = getDurate(args[1]);
                    if (duration[1].equalsIgnoreCase("s") || duration[1].equalsIgnoreCase("sec")) {
                        String reason = getReasonTemporary(args);
                        int time = Integer.parseInt(duration[0]);
                        String wort = seconds;
                        long muteEnds = System.currentTimeMillis() + (1000L * time);
                        mutePlayerTemporary(sender, t, reason, muteEnds, time, wort);
                    } else if (duration[1].equalsIgnoreCase("m") || duration[1].equalsIgnoreCase("min")) {
                        String reason = getReasonTemporary(args);
                        int time = Integer.parseInt(duration[0]);
                        String wort = minutes;
                        long muteEnds = System.currentTimeMillis() + (1000L * 60 * time);
                        mutePlayerTemporary(sender, t, reason, muteEnds, time, wort);
                    } else if (duration[1].equalsIgnoreCase("h") || duration[1].equalsIgnoreCase("hour") || duration[1].equalsIgnoreCase("hours")) {
                        String reason = getReasonTemporary(args);
                        int time = Integer.parseInt(duration[0]);
                        String wort = hours;
                        long muteEnds = System.currentTimeMillis() + (1000L * 60 * 60 * time);
                        mutePlayerTemporary(sender, t, reason, muteEnds, time, wort);
                    } else if (duration[1].equalsIgnoreCase("d") || duration[1].equalsIgnoreCase("day") || duration[1].equalsIgnoreCase("days")) {
                        String reason = getReasonTemporary(args);
                        int time = Integer.parseInt(duration[0]);
                        String wort = days;
                        long muteEnds = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * time);
                        mutePlayerTemporary(sender, t, reason, muteEnds, time, wort);
                    } else if (duration[1].equalsIgnoreCase("w") || duration[1].equalsIgnoreCase("week") || duration[1].equalsIgnoreCase("weeks")) {
                        String reason = getReasonTemporary(args);
                        int time = Integer.parseInt(duration[0]);
                        String wort = weeks;
                        long muteEnds = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 7 * time);
                        mutePlayerTemporary(sender, t, reason, muteEnds, time, wort);
                    } else if (duration[1].equalsIgnoreCase("month") || duration[1].equalsIgnoreCase("months")) {
                        String reason = getReasonTemporary(args);
                        int time = Integer.parseInt(duration[0]);
                        String wort = months;
                        long muteEnds = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 30 * time);
                        mutePlayerTemporary(sender, t, reason, muteEnds, time, wort);
                    } else if (duration[1].equalsIgnoreCase("y") || duration[1].equalsIgnoreCase("year")) {
                        String reason = getReasonTemporary(args);
                        int time = Integer.parseInt(duration[0]);
                        String wort = years;
                        long muteEnds = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365 * time);
                        mutePlayerTemporary(sender, t, reason, muteEnds, time, wort);
                    } else {
                        String reason = getReasonPermanent(args);
                        mutePlayerPermanent(sender, t, reason);
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

    public static List<String> mutedPlayers = new ArrayList<>();

    public static boolean isPlayerMuted(OfflinePlayer p) {
        return mutedPlayers.contains(p.getUniqueId().toString());
    }

    public static void getMutedPlayers() {
        if(mySQLEnabled) {
            mutedPlayers = mySQL.getStringList("UUID", SQLTables.MUTED_PLAYERS.getTableName());
            for (int i = 0; i < mutedPlayers.size(); i++) {
                OfflinePlayer t = Bukkit.getOfflinePlayer(UUID.fromString(mutedPlayers.get(i)));
                int punishmentCount = getPlayerPunishmentCount(t) - 1;
                try {
                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.MUTE.getTableName() + " WHERE " +
                            "(UUID='" + t.getUniqueId().toString() + "' AND " +
                            "PUNISHMENT_COUNT='" + punishmentCount + "')");

                    if(rs.next()) {
                        if(!rs.getString("DURATE").equalsIgnoreCase("Permanent")) {
                            long dateOfMuteEnds = Long.parseLong(rs.getString("DATE_OF_MUTE_ENDS"));
                            long now = System.currentTimeMillis();
                            if (dateOfMuteEnds - now <= 0) {
                                mySQL.executeUpdate("DELETE FROM " + SQLTables.MUTED_PLAYERS.getTableName() + " WHERE UUID='" + t.getUniqueId().toString() + "'");
                                mutedPlayers.remove(t.getUniqueId().toString());
                            }
                        }
                    }
                    mySQL.disconnect();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }else {
            mutedPlayers = SavingsFile.getStringListPath("Mute.MutedPlayers");
            if (SavingsFile.isPathSet("Mute.MutedPlayers")) {
                for (int i = 0; i < mutedPlayers.size(); i++) {
                    OfflinePlayer t = Bukkit.getOfflinePlayer(UUID.fromString(mutedPlayers.get(i)));
                    int punishmentCount = getPlayerPunishmentCount(t) - 1;
                    if (!SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Durate").equalsIgnoreCase("Permanent")) {
                        long dateOfMuteEnds = SavingsFile.getLongPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Mute-Ends");
                        long now = System.currentTimeMillis();
                        if (dateOfMuteEnds - now <= 0) {
                            mutedPlayers.remove(t.getUniqueId().toString());
                            SavingsFile.setPath("Mute.MutedPlayers", mutedPlayers);
                        }
                    }
                }
            }
        }
    }

    public static int getPlayerPunishmentCount(OfflinePlayer p) {
        if (mySQLEnabled) {
            if (mySQL.hasNext("SELECT UUID FROM " + SQLTables.MUTE.getTableName() + " WHERE UUID='" + p.getUniqueId().toString() + "'")) {
                try {
                    ResultSet rs = mySQL.executeQuery("SELECT UUID FROM " + SQLTables.MUTE.getTableName() + " WHERE UUID='" + p.getUniqueId().toString() + "'");

                    int i = 1;
                    while (rs.next()) {
                        i++;
                    }
                    mySQL.disconnect();
                    return i;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            if (SavingsFile.isPathSet("Punishment.Mute." + p.getUniqueId() + ".1")) {
                Set<String> keys = SavingsFile.yaml.getConfigurationSection("Punishment.Mute." + p.getUniqueId()).getKeys(false);
                return keys.size() + 1;
            }
        }
        return 1;
    }

    public static void mutePlayerPermanent(Player p, OfflinePlayer t, String reason) {
        if(!NewSystem.hasPermission(t, noMutePerm)) {
            if (NewSystem.hasPermission(p, permPermanent)) {
                if (!isPlayerMuted(t)) {
                    int punishmentCount = getPlayerPunishmentCount(t);
                    String dateOfMute = SettingsFile.DateFormat(System.currentTimeMillis());

                    mutedPlayers.add(t.getUniqueId().toString());
                    if(mySQLEnabled) {
                        mySQL.executeUpdate("INSERT INTO " + SQLTables.MUTED_PLAYERS.getTableName() + " (UUID) VALUES ('" + t.getUniqueId().toString() + "')");

                        mySQL.executeUpdate("INSERT INTO " + SQLTables.MUTE.getTableName() + " (UUID,PUNISHMENT_COUNT,UUID_MUTED_OF,REASON,DURATE,DATE_OF_MUTE,DATE_OF_MUTE_ENDS) VALUES " +
                                "('" + t.getUniqueId().toString() + "'," +
                                "'" + punishmentCount + "'," +
                                "'" + p.getUniqueId().toString() + "'," +
                                "'" + reason + "'," +
                                "'" + "Permanent" + "'," +
                                "'" + System.currentTimeMillis() + "'," +
                                "'" + "Permanent" + "')");
                    }else {
                        SavingsFile.setPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".MutedOf", p.getUniqueId().toString());
                        SavingsFile.setPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Reason", reason);
                        SavingsFile.setPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Durate", "Permanent");
                        SavingsFile.setPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Mute", System.currentTimeMillis());
                        SavingsFile.setPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Mute-Ends", "Permanent");
                        SavingsFile.setPath("Mute.MutedPlayers", mutedPlayers);
                    }

                    for(String key : msgMutedPermanent) {
                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                    }

                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (NewSystem.hasPermission(all, perm)) {
                            for (String msg : messageListPermanent) {
                                all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true))
                                        .replace("{Muted-Of}", NewSystem.getName(p, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName")))
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Mute}", dateOfMute));
                            }
                        }
                    }

                    if (t.isOnline()) {
                        for (String msg : chatMessagePermanent) {
                            t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Reason}", reason).replace("{Date-Of-Mute}", dateOfMute));
                        }
                    }
                } else {
                    for(String value : msgAlreadyMuted) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            } else {
                p.sendMessage(SettingsFile.getNoPerm());
            }
        }else{
            for(String value : msgPlayerCanNotBeMuted) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void mutePlayerPermanent(CommandSender p, OfflinePlayer t, String reason) {
        if (!isPlayerMuted(t)) {
            int punishmentCount = getPlayerPunishmentCount(t);
            String dateOfMute = SettingsFile.DateFormat(System.currentTimeMillis());

            mutedPlayers.add(t.getUniqueId().toString());
            if(mySQLEnabled) {
                mySQL.executeUpdate("INSERT INTO " + SQLTables.MUTED_PLAYERS.getTableName() + " (UUID) VALUES ('" + t.getUniqueId().toString() + "')");

                mySQL.executeUpdate("INSERT INTO " + SQLTables.MUTE.getTableName() + " (UUID,PUNISHMENT_COUNT,UUID_MUTED_OF,REASON,DURATE,DATE_OF_MUTE,DATE_OF_MUTE_ENDS) VALUES " +
                        "('" + t.getUniqueId().toString() + "'," +
                        "'" + punishmentCount + "'," +
                        "'Console'," +
                        "'" + reason + "'," +
                        "'" + "Permanent" + "'," +
                        "'" + System.currentTimeMillis() + "'," +
                        "'" + "Permanent" + "')");
            }else {
                SavingsFile.setPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".MutedOf", "Console");
                SavingsFile.setPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Reason", reason);
                SavingsFile.setPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Durate", "Permanent");
                SavingsFile.setPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Mute", System.currentTimeMillis());
                SavingsFile.setPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Mute-Ends", "Permanent");
                SavingsFile.setPath("Mute.MutedPlayers", mutedPlayers);
            }

            for (String key : msgMutedPermanent) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
            }

            for (Player all : Bukkit.getOnlinePlayers()) {
                if (NewSystem.hasPermission(all, perm)) {
                    for (String msg : messageListPermanent) {
                        all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                .replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true))
                                .replace("{Muted-Of}", SettingsFile.getConsolePrefix())
                                .replace("{Reason}", reason)
                                .replace("{Date-Of-Mute}", dateOfMute));
                    }
                }
            }

            if (t.isOnline()) {
                for (String msg : chatMessagePermanent) {
                    t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Reason}", reason).replace("{Date-Of-Mute}", dateOfMute));
                }
            }
        } else {
            for (String value : msgAlreadyMuted) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void mutePlayerTemporary(Player p, OfflinePlayer t, String reason, long muteEnds, int number, String word) {
        if(!NewSystem.hasPermission(t, noMutePerm)) {
            if (NewSystem.hasPermission(p, permTemporary)) {
                if (!isPlayerMuted(t)) {
                    int punishmentCount = getPlayerPunishmentCount(t);
                    String dateOfMute = SettingsFile.DateFormat(System.currentTimeMillis());
                    String dateOfMuteEnds = SettingsFile.DateFormat(muteEnds);
                    String durate = number + " " + word;

                    mutedPlayers.add(t.getUniqueId().toString());
                    if(mySQLEnabled) {
                        mySQL.executeUpdate("INSERT INTO " + SQLTables.MUTED_PLAYERS.getTableName() + " (UUID) VALUES ('" + t.getUniqueId().toString() + "')");

                        mySQL.executeUpdate("INSERT INTO " + SQLTables.MUTE.getTableName() + " (UUID,PUNISHMENT_COUNT,UUID_MUTED_OF,REASON,DURATE,DATE_OF_MUTE,DATE_OF_MUTE_ENDS) VALUES " +
                                "('" + t.getUniqueId().toString() + "'," +
                                "'" + punishmentCount + "'," +
                                "'" + p.getUniqueId().toString() + "'," +
                                "'" + reason + "'," +
                                "'" + durate + "'," +
                                "'" + System.currentTimeMillis() + "'," +
                                "'" + muteEnds + "')");
                    }else {
                        SavingsFile.setPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".MutedOf", p.getUniqueId().toString());
                        SavingsFile.setPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Reason", reason);
                        SavingsFile.setPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Durate", durate);
                        SavingsFile.setPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Mute", System.currentTimeMillis());
                        SavingsFile.setPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Mute-Ends", muteEnds);
                        SavingsFile.setPath("Mute.MutedPlayers", mutedPlayers);
                    }

                    for(String key : msgMutedTemporary) {
                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Durate}", durate));
                    }

                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (NewSystem.hasPermission(all, perm)) {
                            for (String msg : messageListTemporary) {
                                all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true))
                                        .replace("{Muted-Of}", NewSystem.getName(p, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName")))
                                        .replace("{Durate}", durate)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Mute}", dateOfMute)
                                        .replace("{Date-Of-Mute-Ends}", dateOfMuteEnds));
                            }
                        }
                    }

                    if (t.isOnline()) {
                        for (String msg : chatMessageTemporary) {
                            t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Durate}", durate).replace("{Reason}", reason).replace("{Date-Of-Mute}", dateOfMute).replace("{Date-Of-Mute-Ends}", dateOfMuteEnds));
                        }
                    }
                } else {
                    for(String value : msgAlreadyMuted) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            } else {
                p.sendMessage(SettingsFile.getNoPerm());
            }
        } else {
            for(String value : msgPlayerCanNotBeMuted) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void mutePlayerTemporary(CommandSender p, OfflinePlayer t, String reason, long muteEnds, int number, String word) {
        if (!isPlayerMuted(t)) {
            int punishmentCount = getPlayerPunishmentCount(t);
            String dateOfMute = SettingsFile.DateFormat(System.currentTimeMillis());
            String dateOfMuteEnds = SettingsFile.DateFormat(muteEnds);
            String durate = number + " " + word;

            mutedPlayers.add(t.getUniqueId().toString());
            if(mySQLEnabled) {
                mySQL.executeUpdate("INSERT INTO " + SQLTables.MUTED_PLAYERS.getTableName() + " (UUID) VALUES ('" + t.getUniqueId().toString() + "')");

                mySQL.executeUpdate("INSERT INTO " + SQLTables.MUTE.getTableName() + " (UUID,PUNISHMENT_COUNT,UUID_MUTED_OF,REASON,DURATE,DATE_OF_MUTE,DATE_OF_MUTE_ENDS) VALUES " +
                        "('" + t.getUniqueId().toString() + "'," +
                        "'" + punishmentCount + "'," +
                        "'Console'," +
                        "'" + reason + "'," +
                        "'" + durate + "'," +
                        "'" + System.currentTimeMillis() + "'," +
                        "'" + muteEnds + "')");
            }else {
                SavingsFile.setPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".MutedOf", "Console");
                SavingsFile.setPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Reason", reason);
                SavingsFile.setPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Durate", durate);
                SavingsFile.setPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Mute", System.currentTimeMillis());
                SavingsFile.setPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Mute-Ends", muteEnds);
                SavingsFile.setPath("Mute.MutedPlayers", mutedPlayers);
            }

            for (String key : msgMutedTemporary) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Durate}", durate));
            }

            for (Player all : Bukkit.getOnlinePlayers()) {
                if (NewSystem.hasPermission(all, perm)) {
                    for (String msg : messageListTemporary) {
                        all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                .replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true))
                                .replace("{Muted-Of}", SettingsFile.getConsolePrefix())
                                .replace("{Durate}", durate)
                                .replace("{Reason}", reason)
                                .replace("{Date-Of-Mute}", dateOfMute)
                                .replace("{Date-Of-Mute-Ends}", dateOfMuteEnds));
                    }
                }
            }

            if (t.isOnline()) {
                for (String msg : chatMessageTemporary) {
                    t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Durate}", durate).replace("{Reason}", reason).replace("{Date-Of-Mute}", dateOfMute).replace("{Date-Of-Mute-Ends}", dateOfMuteEnds));
                }
            }
        } else {
            for (String value : msgAlreadyMuted) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void sendList(Player p) {
        for (String msg : listMessage) {
            if (msg.contains("{Muted-Player}")) {
                if (mutedPlayers.size() == 0) {
                    p.sendMessage(noPlayerMuted.replace("{Prefix}", SettingsFile.getPrefix()));
                } else {
                    for (String uuid : mutedPlayers) {
                        OfflinePlayer t = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                        if (isPlayerMuted(t)) {
                            int punishmentCount = getPlayerPunishmentCount(t) - 1;
                            String mutedOf = "";
                            String reason = "";
                            String dateOfMute = "";
                            String durate = "";
                            String dateOfMuteEnds = "";
                            if(mySQLEnabled) {
                                try {
                                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.MUTE.getTableName() + " WHERE (UUID='" + t.getUniqueId().toString() + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");

                                    if (rs.next()) {
                                        reason = rs.getString("REASON");
                                        durate = rs.getString("DURATE");
                                        dateOfMute = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_MUTE")));

                                        dateOfMuteEnds = rs.getString("DATE_OF_MUTE_ENDS");
                                        if(!dateOfMuteEnds.equalsIgnoreCase("Permanent")) {
                                            dateOfMuteEnds = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_MUTE_ENDS")));
                                        }

                                        if(rs.getString("UUID_MUTED_OF").equalsIgnoreCase("Console")) {
                                            mutedOf = SettingsFile.getConsolePrefix();
                                        }else{
                                            OfflinePlayer mutedOfPlayer = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_MUTED_OF")));
                                            mutedOf = NewSystem.getName(mutedOfPlayer, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"));
                                        }
                                    }
                                    mySQL.disconnect();
                                }catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }else {
                                reason = SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Reason");
                                dateOfMute = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Mute"));
                                durate = SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Durate");

                                if (SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Mute-Ends").equalsIgnoreCase("Permanent")) {
                                    dateOfMuteEnds = SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Mute-Ends");
                                } else {
                                    dateOfMuteEnds = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Mute-Ends"));
                                }

                                if (SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".MutedOf").equalsIgnoreCase("Console")) {
                                    OfflinePlayer muteOfPlayer = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".MutedOf")));
                                    mutedOf = NewSystem.getName(muteOfPlayer, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"));
                                } else {
                                    mutedOf = SettingsFile.getConsolePrefix();
                                }
                            }

                            if (!isMutePermanent(t)) {
                                String hoverMessage = hoverMessageTemporary.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Muted-Of}", mutedOf)
                                        .replace("{Reason}", reason)
                                        .replace("{Durate}", durate)
                                        .replace("{Date-Of-Mute}", dateOfMute)
                                        .replace("{Date-Of-Mute-Ends}", dateOfMuteEnds);
                                TextComponent text = new TextComponent(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Muted-Player}", NewSystem.getName(t, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"))));
                                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
                                p.spigot().sendMessage(text);
                            } else {
                                String hoverMessage = hoverMessagePermanent.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Muted-Of}", mutedOf)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Mute}", dateOfMute);
                                TextComponent text = new TextComponent(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Muted-Player}", NewSystem.getName(t, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"))));
                                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
                                p.spigot().sendMessage(text);
                            }
                        }
                    }
                }
            }else{
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Mute-Count}", String.valueOf(mutedPlayers.size())));
            }
        }
    }

    public static void sendList(CommandSender p) {
        for (String msg : listMessage) {
            if (msg.contains("{Muted-Player}")) {
                if (mutedPlayers.size() == 0) {
                    p.sendMessage(noPlayerMuted.replace("{Prefix}", SettingsFile.getPrefix()));
                } else {
                    for (String uuid : mutedPlayers) {
                        OfflinePlayer t = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                        if (isPlayerMuted(t)) {
                            int punishmentCount = getPlayerPunishmentCount(t) - 1;
                            String mutedOf = "";
                            String reason = "";
                            String dateOfMute = "";
                            String durate = "";
                            String dateOfMuteEnds = "";
                            if(mySQLEnabled) {
                                try {
                                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.MUTE.getTableName() + " WHERE (UUID='" + t.getUniqueId().toString() + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");

                                    if (rs.next()) {
                                        reason = rs.getString("REASON");
                                        durate = rs.getString("DURATE");
                                        dateOfMute = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_MUTE")));

                                        dateOfMuteEnds = rs.getString("DATE_OF_MUTE_ENDS");
                                        if(!dateOfMuteEnds.equalsIgnoreCase("Permanent")) {
                                            dateOfMuteEnds = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_MUTE_ENDS")));
                                        }

                                        if(rs.getString("UUID_MUTED_OF").equalsIgnoreCase("Console")) {
                                            mutedOf = SettingsFile.getConsolePrefix();
                                        }else{
                                            OfflinePlayer mutedOfPlayer = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_MUTED_OF")));
                                            mutedOf = NewSystem.getName(mutedOfPlayer, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"));
                                        }
                                    }
                                    mySQL.disconnect();
                                }catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }else {
                                reason = SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Reason");
                                dateOfMute = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Mute"));
                                durate = SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Durate");

                                if (SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Mute-Ends").equalsIgnoreCase("Permanent")) {
                                    dateOfMuteEnds = SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Mute-Ends");
                                } else {
                                    dateOfMuteEnds = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Mute-Ends"));
                                }

                                if (SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".MutedOf").equalsIgnoreCase("Console")) {
                                    OfflinePlayer muteOfPlayer = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".MutedOf")));
                                    mutedOf = NewSystem.getName(muteOfPlayer, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"));
                                } else {
                                    mutedOf = SettingsFile.getConsolePrefix();
                                }
                            }

                            if (!isMutePermanent(t)) {
                                String message = consoleMessagePermanent.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Muted-Player}", NewSystem.getName(t, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName")))
                                        .replace("{Muted-Of}", mutedOf)
                                        .replace("{Reason}", reason)
                                        .replace("{Durate}", durate)
                                        .replace("{Date-Of-Mute}", dateOfMute)
                                        .replace("{Date-Of-Mute-Ends}", dateOfMuteEnds);
                                p.sendMessage(message);
                            } else {
                                String message = consoleMessageTemporary.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Muted-Player}", NewSystem.getName(t, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName")))
                                        .replace("{Muted-Of}", mutedOf)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Mute}", dateOfMute);
                                p.sendMessage(message);
                            }
                        }
                    }
                }
            }else{
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Mute-Count}", String.valueOf(mutedPlayers.size())));
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

    public static boolean isMutePermanent(OfflinePlayer t) {
        if(isPlayerMuted(t)) {
            int punishmentCount = getPlayerPunishmentCount(t) -1;
            if(mySQLEnabled) {
                try {
                    ResultSet rs = mySQL.executeQuery("SELECT DURATE FROM " + SQLTables.MUTE.getTableName() + " WHERE (UUID='" + t.getUniqueId().toString() + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");

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
                return SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Durate").equalsIgnoreCase("Permanent");
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
