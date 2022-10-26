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
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class HistoryCmd implements CommandExecutor {

    private static MySQL mySQL = NewSystem.getMySQL();
    private static boolean mySQLEnabled  = SettingsFile.getMySQLEnabled();

    private static List<String> usage;
    private static String perm;
    private static String noPunishmentsOfThis;
    private static List<String> listMsg;
    private static List<String> noPunishments;
    private static String notUnbanned;
    private static String messagePlayerWarns;
    private static String hoverMessageWarn;
    private static String messagePlayerBans;
    private static String hoverMessageBanPermanent;
    private static String hoverMessageBanTemporary;
    private static String messagePlayerMutes;
    private static String hoverMessageMutePermanent;
    private static String hoverMessageMuteTemporary;
    private static String messagePlayerKicks;
    private static String hoverMessageKick;
    private static List<String> listMsgIP;
    private static List<String> noPunishmentsIP;
    private static String consoleMessageWarn;
    private static String consoleMessageBanPermanent;
    private static String consoleMessageBanTemporary;
    private static String consoleMessageMutePermanent;
    private static String consoleMessageMuteTemporary;
    private static String consoleMessageKick;

    public void init() {
        usage = CommandFile.getStringListPath("Command.History.Usage");
        perm = CommandFile.getStringPath("Command.History.Permission");
        noPunishmentsOfThis = CommandFile.getStringPath("Command.History.MessageNoPunishmentsOfThis").replace("{Prefix}", SettingsFile.getPrefix());
        listMsg = CommandFile.getStringListPath("Command.History.Message");
        noPunishments = CommandFile.getStringListPath("Command.History.MessageNoPunishments");
        notUnbanned = CommandFile.getStringPath("Command.History.MessageNoOneUnBanned");
        messagePlayerWarns = CommandFile.getStringPath("Command.History.MessagePlayerWarns");
        hoverMessageWarn = CommandFile.getStringPath("Command.History.HoverMessageWarn");
        messagePlayerBans = CommandFile.getStringPath("Command.History.MessagePlayerBans");
        hoverMessageBanPermanent = CommandFile.getStringPath("Command.History.HoverMessageBanPermanent");
        hoverMessageBanTemporary = CommandFile.getStringPath("Command.History.HoverMessageBanTemporary");
        messagePlayerMutes = CommandFile.getStringPath("Command.History.MessagePlayerMutes");
        hoverMessageMutePermanent = CommandFile.getStringPath("Command.History.HoverMessageMutePermanent");
        hoverMessageMuteTemporary = CommandFile.getStringPath("Command.History.HoverMessageMuteTemporary");
        messagePlayerKicks = CommandFile.getStringPath("Command.History.MessagePlayerKicks");
        hoverMessageKick = CommandFile.getStringPath("Command.History.HoverMessageKick");
        listMsgIP = CommandFile.getStringListPath("Command.History.MessageIP");
        noPunishmentsIP = CommandFile.getStringListPath("Command.History.MessageNoPunishmentsIP");
        consoleMessageWarn = CommandFile.getStringPath("Command.History.MessageWarnConsole");
        consoleMessageBanPermanent = CommandFile.getStringPath("Command.History.MessageBanPermanentConsole");
        consoleMessageBanTemporary = CommandFile.getStringPath("Command.History.MessageBanTemporaryConsole");
        consoleMessageMutePermanent = CommandFile.getStringPath("Command.History.MessageMutePermanentConsole");
        consoleMessageMuteTemporary = CommandFile.getStringPath("Command.History.MessageMuteTemporaryConsole");
        consoleMessageKick = CommandFile.getStringPath("Command.History.MessageKickConsole");
        NewSystem.getInstance().getCommand("history").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if(args.length == 1) {
                    String ip = args[0].replace(".", "-");
                    if(SavingsFile.getSavedIPs().contains(ip)) {
                        sendHistory(p, ip);
                    }else {
                        OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                        sendHistory(p, t);
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
                String ip = args[0].replace(".", "-");
                if(SavingsFile.getSavedIPs().contains(ip)) {
                    sendHistory(sender, ip);
                }else {
                    OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                    sendHistory(sender, t);
                }
            }else{
                for(String msg : usage) {
                    sender.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }
        return false;
    }

    public static void sendHistory(Player p, OfflinePlayer t) {
        String prefix = NewSystem.getName(t);
        int punishmentsBan = BanCmd.getPlayerPunishmentCount(t) - 1;
        int punishmentsMute = MuteCmd.getPlayerPunishmentCount(t) - 1;
        int punishmentsKick = KickCmd.getPlayerPunishmentCount(t) - 1;
        int punishmentsWarn = WarnCmd.getPlayerPunishmentCount(t) - 1;
        int punishments = punishmentsBan + punishmentsMute + punishmentsKick;
        if (punishments > 0 || punishmentsWarn > 0) {
            for (String msg : listMsg) {
                if(msg.contains("{Player-Warns}")) {
                    if(punishmentsWarn != 0) {
                        for (int i = 0; i < punishmentsWarn; i++) {
                            String warnedOfPrefix = "";
                            String reason = "";
                            String dateOfWarn = "";

                            if(mySQLEnabled) {
                                try{
                                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.WARN.getTableName() + " WHERE (UUID='" + t.getUniqueId().toString() + "' AND PUNISHMENT_COUNT='" + (i + 1) + "')");

                                    if(rs.next()) {
                                        reason = rs.getString("REASON");
                                        dateOfWarn = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_WARN")));

                                        if (rs.getString("UUID_WARN_OF").equalsIgnoreCase("Console")) {
                                            warnedOfPrefix = SettingsFile.getConsolePrefix();
                                        } else {
                                            OfflinePlayer warnOf = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_WARN_OF")));
                                            warnedOfPrefix = NewSystem.getName(warnOf);
                                        }
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }else{
                                if (SavingsFile.getStringPath("Punishment.Warn." + t.getUniqueId() + "." + (i + 1) + ".WarnOf").equalsIgnoreCase("Console")) {
                                    warnedOfPrefix = SettingsFile.getConsolePrefix();
                                } else {
                                    OfflinePlayer warnOf = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.Warn." + t.getUniqueId() + "." + (i + 1) + ".WarnOf")));
                                    warnedOfPrefix = NewSystem.getName(warnOf);
                                }
                                reason = SavingsFile.getStringPath("Punishment.Warn." + t.getUniqueId() + "." + (i + 1) + ".Reason");
                                dateOfWarn = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Warn." + t.getUniqueId() + "." + (i + 1) + ".Date-Of-Warn"));
                            }

                            String playerWarns = messagePlayerWarns.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Date-Of-Warn}", dateOfWarn).replace("{Reason}", reason);
                            TextComponent text = new TextComponent(playerWarns);

                            String hoverMessage = hoverMessageWarn.replace("{Prefix}", SettingsFile.getPrefix())
                                    .replace("{Warned-Of}", warnedOfPrefix)
                                    .replace("{Reason}", reason)
                                    .replace("{Date-Of-Warn}", dateOfWarn);
                            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
                            p.spigot().sendMessage(text);
                        }
                    }else{
                        p.sendMessage(noPunishmentsOfThis);
                    }
                }else if (msg.contains("{Player-Bans}")) {
                    if (punishmentsBan != 0) {
                        for (int i = 0; i < punishmentsBan; i++) {
                            String unbanOf = notUnbanned;
                            String bannedOfPrefix = "";
                            String reason = "";
                            String durate = "";
                            String dateOfBan = "";
                            String dateOfBanEnds = "";

                            if(mySQLEnabled) {
                                try{
                                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.BAN.getTableName() + " WHERE (UUID='" + t.getUniqueId().toString() + "' AND PUNISHMENT_COUNT='" + (i + 1) + "')");

                                    if(rs.next()) {
                                        reason = rs.getString("REASON");
                                        dateOfBan = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_BAN")));
                                        durate = rs.getString("DURATE");
                                        if(durate.equalsIgnoreCase("Permanent")) {
                                            dateOfBanEnds = durate;
                                        }else{
                                            dateOfBanEnds = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_BAN_ENDS")));
                                        }

                                        if (rs.getString("UUID_BANNED_OF").equalsIgnoreCase("Console")) {
                                            bannedOfPrefix = SettingsFile.getConsolePrefix();
                                        } else {
                                            OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_BANNED_OF")));
                                            bannedOfPrefix = NewSystem.getName(bannedOf);
                                        }

                                        if (rs.getString("UUID_UNBAN_OF") != null) {
                                            if (rs.getString("UUID_UNBAN_OF").equalsIgnoreCase("Console")) {
                                                unbanOf = SettingsFile.getConsolePrefix();
                                            } else {
                                                OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_UNBAN_OF")));
                                                unbanOf = NewSystem.getName(bannedOf);
                                            }
                                        }
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }else{
                                reason = SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + (i + 1) + ".Reason");
                                dateOfBan = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Ban." + t.getUniqueId() + "." + (i + 1) + ".Date-Of-Ban"));
                                durate = SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + (i + 1) + ".Durate");
                                if(SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + (i + 1) + ".Date-Of-Ban-Ends").equalsIgnoreCase("Permanent")) {
                                    dateOfBanEnds = SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + (i + 1) + ".Date-Of-Ban-Ends");
                                }else{
                                    dateOfBanEnds = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Ban." + t.getUniqueId() + "." + (i + 1) + ".Date-Of-Ban-Ends"));
                                }
                                if (SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + (i + 1) + ".BannedOf").equalsIgnoreCase("Console")) {
                                    bannedOfPrefix = SettingsFile.getConsolePrefix();
                                } else {
                                    OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + (i + 1) + ".BannedOf")));
                                    bannedOfPrefix = NewSystem.getName(bannedOf);
                                }
                                if (SavingsFile.isPathSet("Punishment.Ban." + t.getUniqueId() + "." + (i + 1) + ".UnbanOf")) {
                                    if (SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + (i + 1) + ".UnbanOf").equalsIgnoreCase("Console")) {
                                        unbanOf = SettingsFile.getConsolePrefix();
                                    } else {
                                        OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + (i + 1) + ".UnbanOf")));
                                        unbanOf = NewSystem.getName(bannedOf);
                                    }
                                }
                            }

                            if (durate.equalsIgnoreCase("Permanent")) {
                                String playerBans = messagePlayerBans.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Date-Of-Ban}", dateOfBan).replace("{Reason}", reason);
                                TextComponent text = new TextComponent(playerBans);

                                String hoverMessage = hoverMessageBanPermanent.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Banned-Of}", bannedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Ban}", dateOfBan)
                                        .replace("{Unban-Of}", unbanOf);
                                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
                                p.spigot().sendMessage(text);
                            } else {
                                String playerBans = messagePlayerBans.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Date-Of-Ban}", dateOfBan).replace("{Reason}", reason);
                                TextComponent text = new TextComponent(playerBans);

                                String hoverMessage = hoverMessageBanTemporary.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Banned-Of}", bannedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Durate}", durate)
                                        .replace("{Date-Of-Ban}", dateOfBan)
                                        .replace("{Date-Of-Ban-Ends}", dateOfBanEnds)
                                        .replace("{Unban-Of}", unbanOf);
                                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
                                p.spigot().sendMessage(text);
                            }
                        }
                    } else {
                        p.sendMessage(noPunishmentsOfThis);
                    }
                } else if (msg.contains("{Player-Mutes}")) {
                    if (punishmentsMute != 0) {
                        for (int i = 0; i < punishmentsMute; i++) {
                            String unMuteOf = notUnbanned;
                            String mutedOfPrefix = "";
                            String reason = "";
                            String durate = "";
                            String dateOfMute = "";
                            String dateOfMuteEnds = "";

                            if(mySQLEnabled) {
                                try{
                                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.MUTE.getTableName() + " WHERE (UUID='" + t.getUniqueId().toString() + "' AND PUNISHMENT_COUNT='" + (i + 1) + "')");

                                    if(rs.next()) {
                                        reason = rs.getString("REASON");
                                        dateOfMute = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_MUTE")));
                                        durate = rs.getString("DURATE");
                                        if(durate.equalsIgnoreCase("Permanent")) {
                                            dateOfMuteEnds = durate;
                                        }else{
                                            dateOfMuteEnds = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_MUTE_ENDS")));
                                        }
                                        if (rs.getString("UUID_MUTED_OF").equalsIgnoreCase("Console")) {
                                            mutedOfPrefix = SettingsFile.getConsolePrefix();
                                        } else {
                                            OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_MUTED_OF")));
                                            mutedOfPrefix = NewSystem.getName(bannedOf);
                                        }
                                        if (rs.getString("UUID_UNMUTE_OF") != null) {
                                            if (rs.getString("UUID_UNMUTE_OF").equalsIgnoreCase("Console")) {
                                                unMuteOf = SettingsFile.getConsolePrefix();
                                            } else {
                                                OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_UNMUTE_OF")));
                                                unMuteOf = NewSystem.getName(bannedOf);
                                            }
                                        }
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }else {
                                reason = SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + (i + 1) + ".Reason");
                                dateOfMute = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Mute." + t.getUniqueId() + "." + (i + 1) + ".Date-Of-Mute"));
                                durate = SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + (i + 1) + ".Durate");

                                if(SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + (i + 1) + ".Date-Of-Mute-Ends").equalsIgnoreCase("Permanent")) {
                                    dateOfMuteEnds = SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + (i + 1) + ".Date-Of-Mute-Ends");
                                }else{
                                    dateOfMuteEnds = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Mute." + t.getUniqueId() + "." + (i + 1) + ".Date-Of-Mute-Ends"));
                                }

                                if (SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + (i + 1) + ".MutedOf").equalsIgnoreCase("Console")) {
                                    mutedOfPrefix = SettingsFile.getConsolePrefix();
                                } else {
                                    OfflinePlayer mutedOf = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + (i + 1) + ".MutedOf")));
                                    mutedOfPrefix = NewSystem.getName(mutedOf);
                                }

                                if (SavingsFile.isPathSet("Punishment.Mute." + t.getUniqueId() + "." + (i + 1) + ".UnMuteOf")) {
                                    if (SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + (i + 1) + ".UnMuteOf").equalsIgnoreCase("Console")) {
                                        unMuteOf = SettingsFile.getConsolePrefix();
                                    } else {
                                        OfflinePlayer unMutedOf = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + (i + 1) + ".UnMuteOf")));
                                        unMuteOf = NewSystem.getName(unMutedOf);
                                    }
                                }
                            }

                            if (durate.equalsIgnoreCase("Permanent")) {
                                String playerMute = messagePlayerMutes.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Date-Of-Mute}", dateOfMute).replace("{Reason}", reason);
                                TextComponent text = new TextComponent(playerMute);

                                String hoverMessage = hoverMessageMutePermanent.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Muted-Of}", mutedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Mute}", dateOfMute)
                                        .replace("{UnMute-Of}", unMuteOf);
                                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
                                p.spigot().sendMessage(text);
                            } else {
                                String playerMute = messagePlayerMutes.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Date-Of-Mute}", dateOfMute).replace("{Reason}", reason);
                                TextComponent text = new TextComponent(playerMute);

                                String hoverMessage = hoverMessageMuteTemporary.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Muted-Of}", mutedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Durate}", durate)
                                        .replace("{Date-Of-Mute}", dateOfMute)
                                        .replace("{Date-Of-Mute-Ends}", dateOfMuteEnds)
                                        .replace("{UnMute-Of}", unMuteOf);
                                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
                                p.spigot().sendMessage(text);
                            }
                        }
                    } else {
                        p.sendMessage(noPunishmentsOfThis);
                    }
                } else if (msg.contains("{Player-Kicks}")) {
                    if (punishmentsKick != 0) {
                        for (int i = 0; i < punishmentsKick; i++) {
                            String kickedOfPrefix = "";
                            String reason = "";
                            String dateOfKick = "";

                            if(mySQLEnabled){
                                try {
                                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.KICK.getTableName() + " WHERE (UUID='" + t.getUniqueId().toString() + "' AND PUNISHMENT_COUNT='" + (i + 1) + "')");

                                    if(rs.next()) {
                                        reason = rs.getString("REASON");
                                        dateOfKick = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_KICK")));
                                        if (rs.getString("UUID_KICK_OF").equalsIgnoreCase("Console")) {
                                            kickedOfPrefix = SettingsFile.getConsolePrefix();
                                        } else {
                                            OfflinePlayer kickedOf = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_KICK_OF")));
                                            kickedOfPrefix = NewSystem.getName(kickedOf);
                                        }
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }else {
                                reason = SavingsFile.getStringPath("Punishment.Kick." + t.getUniqueId() + "." + (i + 1) + ".Reason");
                                dateOfKick = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Kick." + t.getUniqueId() + "." + (i + 1) + ".Date-Of-Kick"));
                                if (SavingsFile.getStringPath("Punishment.Kick." + t.getUniqueId() + "." + (i + 1) + ".KickOf").equalsIgnoreCase("Console")) {
                                    kickedOfPrefix = SettingsFile.getConsolePrefix();
                                } else {
                                    OfflinePlayer kickedOf = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.Kick." + t.getUniqueId() + "." + (i + 1) + ".KickOf")));
                                    kickedOfPrefix = NewSystem.getName(kickedOf);
                                }
                            }

                            String playerKick = messagePlayerKicks.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Date-Of-Kick}", dateOfKick).replace("{Reason}", reason);
                            TextComponent text = new TextComponent(playerKick);

                            String hoverMessage = hoverMessageKick.replace("{Prefix}", SettingsFile.getPrefix())
                                    .replace("{Kicked-Of}", kickedOfPrefix)
                                    .replace("{Reason}", reason)
                                    .replace("{Date-Of-Kick}", dateOfKick);
                            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
                            p.spigot().sendMessage(text);
                        }
                    } else {
                        p.sendMessage(noPunishmentsOfThis);
                    }
                } else if (msg.contains("{Active-Punishments}")) {
                    if (BanCmd.isPlayerBanned(t) || MuteCmd.isPlayerMuted(t)) {
                        if (BanCmd.isPlayerBanned(t)) {
                            int punishmentCount = BanCmd.getPlayerPunishmentCount(t) - 1;
                            String dateOfBanEnds = "";
                            String durate = "";
                            String bannedOfPrefix = "";
                            String reason = "";
                            String dateOfBan = "";

                            if(mySQLEnabled) {
                                try {
                                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.BAN.getTableName() + " WHERE (UUID='" + t.getUniqueId().toString() + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");

                                    if(rs.next()) {
                                        durate = rs.getString("DURATE");
                                        reason = rs.getString("REASON");
                                        dateOfBan = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_BAN")));
                                        if(durate.equalsIgnoreCase("Permanent")) {
                                            dateOfBanEnds = durate;
                                        }else{
                                            dateOfBanEnds = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_BAN_ENDS")));
                                        }

                                        if (rs.getString("UUID_BANNED_OF").equalsIgnoreCase("Console")) {
                                            bannedOfPrefix = SettingsFile.getConsolePrefix();
                                        } else {
                                            OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_BANNED_OF")));
                                            bannedOfPrefix = NewSystem.getName(bannedOf);
                                        }
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }else{
                                durate = SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Durate");
                                reason = SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Reason");
                                dateOfBan = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Ban"));
                                if(SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Ban-Ends").equalsIgnoreCase("Permanent")) {
                                    dateOfBanEnds = "Permanent";
                                }else{
                                    dateOfBanEnds = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Ban-Ends"));
                                }
                                if (SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".BannedOf").equalsIgnoreCase("Console")) {
                                    bannedOfPrefix = SettingsFile.getConsolePrefix();
                                } else {
                                    OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".BannedOf")));
                                    bannedOfPrefix = NewSystem.getName(bannedOf);
                                }
                            }

                            if (BanCmd.isBanPermanent(t)) {
                                String playerBans = messagePlayerBans.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Date-Of-Ban}", dateOfBan).replace("{Reason}", reason);
                                TextComponent text = new TextComponent(playerBans);

                                String hoverMessage = hoverMessageBanPermanent.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Banned-Of}", bannedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Ban}", dateOfBan)
                                        .replace("{Unban-Of}", notUnbanned);
                                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
                                p.spigot().sendMessage(text);
                            } else {
                                String playerBans = messagePlayerBans.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Date-Of-Ban}", dateOfBan).replace("{Reason}", reason);
                                TextComponent text = new TextComponent(playerBans);

                                String hoverMessage = hoverMessageBanTemporary.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Banned-Of}", bannedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Durate}", durate)
                                        .replace("{Date-Of-Ban}", dateOfBan)
                                        .replace("{Date-Of-Ban-Ends}", dateOfBanEnds)
                                        .replace("{Unban-Of}", notUnbanned);
                                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
                                p.spigot().sendMessage(text);
                            }
                        }
                        if (MuteCmd.isPlayerMuted(t)) {
                            int punishmentCount = MuteCmd.getPlayerPunishmentCount(t) - 1;
                            String mutedOfPrefix = "";
                            String reason = "";
                            String dateOfMute = "";
                            String dateOfMuteEnds = "";
                            String durate = "";

                            if(mySQLEnabled) {
                                try {
                                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.MUTE.getTableName() + " WHERE (UUID='" + t.getUniqueId().toString() + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");

                                    if(rs.next()) {
                                        reason = rs.getString("REASON");
                                        dateOfMute = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_MUTE")));
                                        durate = rs.getString("DURATE");
                                        if(durate.equalsIgnoreCase("Permanent")) {
                                            dateOfMuteEnds = durate;
                                        }else{
                                            dateOfMuteEnds = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_MUTE_ENDS")));
                                        }
                                        if (rs.getString("UUID_MUTED_OF").equalsIgnoreCase("Console")) {
                                            mutedOfPrefix = SettingsFile.getConsolePrefix();
                                        } else {
                                            OfflinePlayer mutedOf = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_MUTED_OF")));
                                            mutedOfPrefix = NewSystem.getName(mutedOf);
                                        }
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }else {
                                reason = SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Reason");
                                dateOfMute = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Mute"));
                                durate = SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Durate");
                                if(SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + "Date-Of-Mute-Ends").equalsIgnoreCase("Permanent")) {
                                    dateOfMuteEnds = SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + "Date-Of-Mute-Ends");
                                }else{
                                    dateOfMuteEnds = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + "Date-Of-Mute-Ends"));
                                }
                                if (SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".MutedOf").equalsIgnoreCase("Console")) {
                                    mutedOfPrefix = SettingsFile.getConsolePrefix();
                                } else {
                                    OfflinePlayer mutedOf = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".MutedOf")));
                                    mutedOfPrefix = NewSystem.getName(mutedOf);
                                }
                            }

                            if (MuteCmd.isMutePermanent(t)) {
                                String playerMute = messagePlayerMutes.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Date-Of-Mute}", dateOfMute).replace("{Reason}", reason);
                                TextComponent text = new TextComponent(playerMute);

                                String hoverMessage = hoverMessageMutePermanent.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Muted-Of}", mutedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Mute}", dateOfMute)
                                        .replace("{UnMute-Of}", notUnbanned);
                                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
                                p.spigot().sendMessage(text);
                            } else {

                                String playerMute = messagePlayerMutes.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Date-Of-Mute}", dateOfMute).replace("{Reason}", reason);
                                TextComponent text = new TextComponent(playerMute);

                                String hoverMessage = hoverMessageMuteTemporary.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Muted-Of}", mutedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Durate}", durate)
                                        .replace("{Date-Of-Mute}", dateOfMute)
                                        .replace("{Date-Of-Mute-Ends}", dateOfMuteEnds)
                                        .replace("{UnMute-Of}", notUnbanned);
                                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
                                p.spigot().sendMessage(text);
                            }
                        }
                    } else {
                        p.sendMessage(noPunishmentsOfThis);
                    }
                } else {
                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", prefix).replace("{Punishment-Count}", String.valueOf(punishments)));
                }
            }
        } else {
            for(String key : noPunishments) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", prefix));
            }

        }
    }

    public static void sendHistory(Player p, String ip) {
        String prefix = ip.replace("-", ".");
        int punishmentsBan = BanIPCmd.getIPPunishmentCount(ip) - 1;
        int punishmentsMute = MuteIPCmd.getIPPunishmentCount(ip) - 1;
        int punishments = punishmentsBan + punishmentsMute;
        if (punishments > 0) {
            for (String msg : listMsgIP) {
                if (msg.contains("{Player-Bans}")) {
                    if (punishmentsBan != 0) {
                        for (int i = 0; i < punishmentsBan; i++) {
                            String bannedOfPrefix = "";
                            String reason = "";
                            String durate = "";
                            String dateOfBan = "";
                            String dateOfBanEnds = "";
                            String unbanOf = notUnbanned;

                            if(mySQLEnabled) {
                                try{
                                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.BANIP.getTableName() + " WHERE (IP='" + ip + "' AND PUNISHMENT_COUNT='" + (i + 1) + "')");

                                    if(rs.next()) {
                                        reason = rs.getString("REASON");
                                        dateOfBan = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_BAN")));
                                        durate = rs.getString("DURATE");
                                        if(durate.equalsIgnoreCase("Permanent")) {
                                            dateOfBanEnds = durate;
                                        }else{
                                            dateOfBanEnds = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_BAN_ENDS")));
                                        }

                                        if (rs.getString("UUID_BANNED_OF").equalsIgnoreCase("Console")) {
                                            bannedOfPrefix = SettingsFile.getConsolePrefix();
                                        } else {
                                            OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_BANNED_OF")));
                                            bannedOfPrefix = NewSystem.getName(bannedOf);
                                        }

                                        if (rs.getString("UUID_UNBAN_OF") != null) {
                                            if (rs.getString("UUID_UNBAN_OF").equalsIgnoreCase("Console")) {
                                                unbanOf = SettingsFile.getConsolePrefix();
                                            } else {
                                                OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_UNBAN_OF")));
                                                unbanOf = NewSystem.getName(bannedOf);
                                            }
                                        }
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }else {
                                dateOfBanEnds = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.BanIP." + ip + "." + (i + 1) + ".Date-Of-Ban-Ends"));
                                durate = SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + (i + 1) + ".Durate");
                                reason = SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + (i + 1) + ".Reason");

                                if(durate.equalsIgnoreCase("Permanent")) {
                                    dateOfBanEnds = durate;
                                }else{
                                    dateOfBan = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.BanIP." + ip + "." + (i + 1) + ".Date-Of-Ban"));
                                }

                                if (SavingsFile.isPathSet("Punishment.BanIP." + ip + "." + (i + 1) + ".UnbanOf")) {
                                    if(SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + (i + 1) + ".UnbanOf").equalsIgnoreCase("Console")) {
                                        unbanOf = SettingsFile.getConsolePrefix();
                                    }else {
                                        OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + (i + 1) + ".UnbanOf")));
                                        unbanOf = NewSystem.getName(bannedOf);
                                    }
                                }

                                if (SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + (i + 1) + ".BannedOf").equalsIgnoreCase("Console")) {
                                    bannedOfPrefix = SettingsFile.getConsolePrefix();
                                } else {
                                    OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + (i + 1) + ".BannedOf")));
                                    bannedOfPrefix = NewSystem.getName(bannedOf);
                                }
                            }

                            if (SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + (i + 1) + ".Durate").equalsIgnoreCase("Permanent")) {
                                String playerBans = messagePlayerBans.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Date-Of-Ban}", dateOfBan).replace("{Reason}", reason);
                                TextComponent text = new TextComponent(playerBans);

                                String hoverMessage = hoverMessageBanPermanent.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Banned-Of}", bannedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Ban}", dateOfBan)
                                        .replace("{Unban-Of}", unbanOf);
                                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
                                p.spigot().sendMessage(text);
                            } else {
                                String playerBans = messagePlayerBans.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Date-Of-Ban}", dateOfBan).replace("{Reason}", reason);
                                TextComponent text = new TextComponent(playerBans);

                                String hoverMessage = hoverMessageBanTemporary.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Banned-Of}", bannedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Durate}", durate)
                                        .replace("{Date-Of-Ban}", dateOfBan)
                                        .replace("{Date-Of-Ban-Ends}", dateOfBanEnds)
                                        .replace("{Unban-Of}", unbanOf);
                                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
                                p.spigot().sendMessage(text);
                            }
                        }
                    } else {
                        p.sendMessage(noPunishmentsOfThis);
                    }
                } else if (msg.contains("{Player-Mutes}")) {
                    if (punishmentsMute != 0) {
                        for (int i = 0; i < punishmentsMute; i++) {
                            String mutedOfPrefix = "";
                            String reason = "";
                            String durate = "";
                            String dateOfMute = "";
                            String dateOfMuteEnds = "";
                            String unMuteOf = notUnbanned;

                            if(mySQLEnabled) {
                                try{
                                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.MUTEIP.getTableName() + " WHERE (IP='" + ip + "' AND PUNISHMENT_COUNT='" + (i + 1) + "')");

                                    if(rs.next()) {
                                        reason = rs.getString("REASON");
                                        dateOfMute = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_MUTE")));
                                        durate = rs.getString("DURATE");
                                        if(durate.equalsIgnoreCase("Permanent")) {
                                            dateOfMuteEnds = durate;
                                        }else{
                                            dateOfMuteEnds = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_MUTE_ENDS")));
                                        }
                                        if (rs.getString("UUID_MUTED_OF").equalsIgnoreCase("Console")) {
                                            mutedOfPrefix = SettingsFile.getConsolePrefix();
                                        } else {
                                            OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_MUTED_OF")));
                                            mutedOfPrefix = NewSystem.getName(bannedOf);
                                        }
                                        if (rs.getString("UUID_UNMUTE_OF") != null) {
                                            if (rs.getString("UUID_UNMUTE_OF").equalsIgnoreCase("Console")) {
                                                unMuteOf = SettingsFile.getConsolePrefix();
                                            } else {
                                                OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_UNMUTE_OF")));
                                                unMuteOf = NewSystem.getName(bannedOf);
                                            }
                                        }
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }else {
                                reason = SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + (i + 1) + ".Reason");
                                dateOfMute = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.MuteIP." + ip + "." + (i + 1) + ".Date-Of-Mute"));
                                durate = SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + (i + 1) + ".Durate");

                                if(durate.equalsIgnoreCase("Permanent")) {
                                    dateOfMuteEnds = durate;
                                }else{
                                    dateOfMuteEnds = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.MuteIP." + ip + "." + (i + 1) + ".Date-Of-Mute-Ends"));
                                }

                                if (SavingsFile.isPathSet("Punishment.MuteIP." + ip + "." + (i + 1) + ".UnMuteOf")) {
                                    if (SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + (i + 1) + ".UnMuteOf").equalsIgnoreCase("Console")) {
                                        unMuteOf = SettingsFile.getConsolePrefix();
                                    } else {
                                        OfflinePlayer unMutedOf = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + (i + 1) + ".UnMuteOf")));
                                        unMuteOf = NewSystem.getName(unMutedOf);
                                    }
                                }

                                if (SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + (i + 1) + ".MutedOf").equalsIgnoreCase("Console")) {
                                    mutedOfPrefix = SettingsFile.getConsolePrefix();
                                } else {
                                    OfflinePlayer mutedOf = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + (i + 1) + ".MutedOf")));
                                    mutedOfPrefix = NewSystem.getName(mutedOf);
                                }
                            }

                            if (SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + (i + 1) + ".Durate").equalsIgnoreCase("Permanent")) {
                                String playerMute = messagePlayerMutes.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Date-Of-Mute}", dateOfMute).replace("{Reason}", reason);
                                TextComponent text = new TextComponent(playerMute);

                                String hoverMessage = hoverMessageMutePermanent.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Muted-Of}", mutedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Mute}", dateOfMute)
                                        .replace("{UnMute-Of}", unMuteOf);
                                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
                                p.spigot().sendMessage(text);
                            } else {
                                String playerMute = messagePlayerMutes.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Date-Of-Mute}", dateOfMute).replace("{Reason}", reason);
                                TextComponent text = new TextComponent(playerMute);

                                String hoverMessage = hoverMessageMuteTemporary.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Muted-Of}", mutedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Durate}", durate)
                                        .replace("{Date-Of-Mute}", dateOfMute)
                                        .replace("{Date-Of-Mute-Ends}", dateOfMuteEnds)
                                        .replace("{UnMute-Of}", unMuteOf);
                                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
                                p.spigot().sendMessage(text);
                            }
                        }
                    } else {
                        p.sendMessage(noPunishmentsOfThis);
                    }
                } else if (msg.contains("{Active-Punishments}")) {
                    if (BanIPCmd.isIPBanned(ip) || MuteIPCmd.isIPMuted(ip)) {
                        String unBanOf = notUnbanned;
                        if (BanIPCmd.isIPBanned(ip)) {
                            int punishmentCount = BanIPCmd.getIPPunishmentCount(ip) - 1;
                            String dateOfBanEnds = "";
                            String durate = "";
                            String bannedOfPrefix = "";
                            String reason = "";
                            String dateOfBan = "";

                            if(mySQLEnabled) {
                                try {
                                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.BANIP.getTableName() + " WHERE (IP='" + ip + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");

                                    if(rs.next()) {
                                        durate = rs.getString("DURATE");
                                        reason = rs.getString("REASON");
                                        dateOfBan = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_BAN")));
                                        if(durate.equalsIgnoreCase("Permanent")) {
                                            dateOfBanEnds = durate;
                                        }else{
                                            dateOfBanEnds = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_BAN_ENDS")));
                                        }

                                        if (rs.getString("UUID_BANNED_OF").equalsIgnoreCase("Console")) {
                                            bannedOfPrefix = SettingsFile.getConsolePrefix();
                                        } else {
                                            OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_BANNED_OF")));
                                            bannedOfPrefix = NewSystem.getName(bannedOf);
                                        }
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }else {
                                reason = SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Reason");
                                dateOfBan = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban"));
                                durate = SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Durate");

                                if(durate.equalsIgnoreCase("Permanent")) {
                                    dateOfBanEnds = durate;
                                }else{
                                    dateOfBanEnds = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban-Ends"));
                                }

                                if (SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + punishmentCount + ".BannedOf").equalsIgnoreCase("Console")) {
                                    bannedOfPrefix = SettingsFile.getConsolePrefix();
                                } else {
                                    OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + punishmentCount + ".BannedOf")));
                                    bannedOfPrefix = NewSystem.getName(bannedOf);
                                }
                            }

                            if (BanIPCmd.isBanPermanent(ip)) {
                                String playerBans = messagePlayerBans.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Date-Of-Ban}", dateOfBan).replace("{Reason}", reason);
                                TextComponent text = new TextComponent(playerBans);

                                String hoverMessage = hoverMessageBanPermanent.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Banned-Of}", bannedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Ban}", dateOfBan)
                                        .replace("{Unban-Of}", unBanOf);
                                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
                                p.spigot().sendMessage(text);
                            } else {
                                String playerBans = messagePlayerBans.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Date-Of-Ban}", dateOfBan).replace("{Reason}", reason);
                                TextComponent text = new TextComponent(playerBans);

                                String hoverMessage = hoverMessageBanTemporary.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Banned-Of}", bannedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Durate}", durate)
                                        .replace("{Date-Of-Ban}", dateOfBan)
                                        .replace("{Date-Of-Ban-Ends}", dateOfBanEnds)
                                        .replace("{Unban-Of}", unBanOf);
                                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
                                p.spigot().sendMessage(text);
                            }
                        }
                        if (MuteIPCmd.isIPMuted(ip)) {
                            int punishmentCount = MuteIPCmd.getIPPunishmentCount(ip) - 1;
                            String mutedOfPrefix = "";
                            String reason = "";
                            String dateOfMute = "";
                            String dateOfMuteEnds = "";
                            String durate = "";

                            if(mySQLEnabled) {
                                try {
                                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.MUTEIP.getTableName() + " WHERE (IP='" + ip + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");

                                    if(rs.next()) {
                                        reason = rs.getString("REASON");
                                        dateOfMute = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_MUTE")));
                                        durate = rs.getString("DURATE");
                                        if(durate.equalsIgnoreCase("Permanent")) {
                                            dateOfMuteEnds = durate;
                                        }else{
                                            dateOfMuteEnds = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_MUTE_ENDS")));
                                        }
                                        if (rs.getString("UUID_MUTED_OF").equalsIgnoreCase("Console")) {
                                            mutedOfPrefix = SettingsFile.getConsolePrefix();
                                        } else {
                                            OfflinePlayer mutedOf = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_MUTED_OF")));
                                            mutedOfPrefix = NewSystem.getName(mutedOf);
                                        }
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }else {
                                reason = SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Reason");
                                dateOfMute = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Date-Of-Mute"));
                                durate = SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Durate");

                                if(durate.equalsIgnoreCase("Permanent")) {
                                    dateOfMuteEnds = durate;
                                }else{
                                    dateOfMuteEnds = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.MuteIP." + ip + "." + punishmentCount + "Date-Of-Mute-Ends"));
                                }

                                if (SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".MutedOf").equalsIgnoreCase("Console")) {
                                    mutedOfPrefix = SettingsFile.getConsolePrefix();
                                } else {
                                    OfflinePlayer mutedOf = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".MutedOf")));
                                    mutedOfPrefix = NewSystem.getName(mutedOf);
                                }
                            }

                            if (MuteIPCmd.isMutePermanent(ip)) {
                                String playerMute = messagePlayerMutes.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Date-Of-Mute}", dateOfMute).replace("{Reason}", reason);
                                TextComponent text = new TextComponent(playerMute);

                                String hoverMessage = hoverMessageMutePermanent.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Muted-Of}", mutedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Mute}", dateOfMute)
                                        .replace("{UnMute-Of}", unBanOf);
                                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
                                p.spigot().sendMessage(text);
                            } else {
                                String playerMute = messagePlayerMutes.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Date-Of-Ban}", dateOfMute).replace("{Reason}", reason);
                                TextComponent text = new TextComponent(playerMute);

                                String hoverMessage = hoverMessageBanTemporary.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Muted-Of}", mutedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Durate}", durate)
                                        .replace("{Date-Of-Mute}", dateOfMute)
                                        .replace("{Date-Of-Mute-Ends}", dateOfMuteEnds)
                                        .replace("{UnMute-Of}", unBanOf);
                                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
                                p.spigot().sendMessage(text);
                            }
                        }
                    } else {
                        p.sendMessage(noPunishmentsOfThis);
                    }
                } else {
                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", prefix).replace("{Punishment-Count}", String.valueOf(punishments)));
                }
            }
        } else {
            for(String key : noPunishmentsIP) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{IP}", prefix));
            }

        }
    }

    public static void sendHistory(CommandSender p, OfflinePlayer t) {
        String prefix = NewSystem.getName(t);
        int punishmentsBan = BanCmd.getPlayerPunishmentCount(t) - 1;
        int punishmentsMute = MuteCmd.getPlayerPunishmentCount(t) - 1;
        int punishmentsKick = KickCmd.getPlayerPunishmentCount(t) - 1;
        int punishmentsWarn = WarnCmd.getPlayerPunishmentCount(t) - 1;
        int punishments = punishmentsBan + punishmentsMute + punishmentsKick;
        if (punishments > 0 || punishmentsWarn > 0) {
            for (String msg : listMsg) {
                if(msg.contains("{Player-Warns}")) {
                    if(punishmentsWarn != 0) {
                        for (int i = 0; i < punishmentsWarn; i++) {
                            String warnedOfPrefix = "";
                            String reason = "";
                            String dateOfWarn = "";

                            if(mySQLEnabled) {
                                try{
                                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.WARN.getTableName() + " WHERE (UUID='" + t.getUniqueId().toString() + "' AND PUNISHMENT_COUNT='" + (i + 1) + "')");

                                    if(rs.next()) {
                                        reason = rs.getString("REASON");
                                        dateOfWarn = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_WARN")));

                                        if (rs.getString("UUID_WARN_OF").equalsIgnoreCase("Console")) {
                                            warnedOfPrefix = SettingsFile.getConsolePrefix();
                                        } else {
                                            OfflinePlayer warnOf = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_WARN_OF")));
                                            warnedOfPrefix = NewSystem.getName(warnOf);
                                        }
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }else{
                                if (SavingsFile.getStringPath("Punishment.Warn." + t.getUniqueId() + "." + (i + 1) + ".WarnOf").equalsIgnoreCase("Console")) {
                                    warnedOfPrefix = SettingsFile.getConsolePrefix();
                                } else {
                                    OfflinePlayer warnOf = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.Warn." + t.getUniqueId() + "." + (i + 1) + ".WarnOf")));
                                    warnedOfPrefix = NewSystem.getName(warnOf);
                                }
                                reason = SavingsFile.getStringPath("Punishment.Warn." + t.getUniqueId() + "." + (i + 1) + ".Reason");
                                dateOfWarn = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Warn." + t.getUniqueId() + "." + (i + 1) + ".Date-Of-Warn"));
                            }

                            String message = consoleMessageWarn.replace("{Prefix}", SettingsFile.getPrefix())
                                    .replace("{Warned-Of}", warnedOfPrefix)
                                    .replace("{Reason}", reason)
                                    .replace("{Date-Of-Warn}", dateOfWarn);
                            p.sendMessage(message);
                        }
                    }else{
                        p.sendMessage(noPunishmentsOfThis);
                    }
                }else if (msg.contains("{Player-Bans}")) {
                    if (punishmentsBan != 0) {
                        for (int i = 0; i < punishmentsBan; i++) {
                            String bannedOfPrefix = "";
                            String reason = "";
                            String durate = "";
                            String dateOfBan = "";
                            String dateOfBanEnds = "";
                            String unbanOf = notUnbanned;

                            if(mySQLEnabled) {
                                try{
                                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.BAN.getTableName() + " WHERE (UUID='" + t.getUniqueId().toString() + "' AND PUNISHMENT_COUNT='" + (i + 1) + "')");

                                    if(rs.next()) {
                                        reason = rs.getString("REASON");
                                        dateOfBan = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_BAN")));
                                        durate = rs.getString("DURATE");
                                        if(durate.equalsIgnoreCase("Permanent")) {
                                            dateOfBanEnds = durate;
                                        }else{
                                            dateOfBanEnds = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_BAN_ENDS")));
                                        }

                                        if (rs.getString("UUID_BANNED_OF").equalsIgnoreCase("Console")) {
                                            bannedOfPrefix = SettingsFile.getConsolePrefix();
                                        } else {
                                            OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_BANNED_OF")));
                                            bannedOfPrefix = NewSystem.getName(bannedOf);
                                        }

                                        if (rs.getString("UUID_UNBAN_OF") != null) {
                                            if (rs.getString("UUID_UNBAN_OF").equalsIgnoreCase("Console")) {
                                                unbanOf = SettingsFile.getConsolePrefix();
                                            } else {
                                                OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_UNBAN_OF")));
                                                unbanOf = NewSystem.getName(bannedOf);
                                            }
                                        }
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }else{
                                reason = SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + (i + 1) + ".Reason");
                                dateOfBan = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Ban." + t.getUniqueId() + "." + (i + 1) + ".Date-Of-Ban"));
                                durate = SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + (i + 1) + ".Durate");
                                if(SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + (i + 1) + ".Date-Of-Ban-Ends").equalsIgnoreCase("Permanent")) {
                                    dateOfBanEnds = SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + (i + 1) + ".Date-Of-Ban-Ends");
                                }else{
                                    dateOfBanEnds = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Ban." + t.getUniqueId() + "." + (i + 1) + ".Date-Of-Ban-Ends"));
                                }
                                if (SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + (i + 1) + ".BannedOf").equalsIgnoreCase("Console")) {
                                    bannedOfPrefix = SettingsFile.getConsolePrefix();
                                } else {
                                    OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + (i + 1) + ".BannedOf")));
                                    bannedOfPrefix = NewSystem.getName(bannedOf);
                                }
                                if (SavingsFile.isPathSet("Punishment.Ban." + t.getUniqueId() + "." + (i + 1) + ".UnbanOf")) {
                                    if (SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + (i + 1) + ".UnbanOf").equalsIgnoreCase("Console")) {
                                        unbanOf = SettingsFile.getConsolePrefix();
                                    } else {
                                        OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + (i + 1) + ".UnbanOf")));
                                        unbanOf = NewSystem.getName(bannedOf);
                                    }
                                }
                            }

                            if (SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + (i + 1) + ".Durate").equalsIgnoreCase("Permanent")) {
                                String message = consoleMessageBanPermanent.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Banned-Of}", bannedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Ban}", dateOfBan)
                                        .replace("{Unban-Of}", unbanOf);
                                p.sendMessage(message);
                            } else {
                                String message = consoleMessageBanTemporary.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Banned-Of}", bannedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Durate}", durate)
                                        .replace("{Date-Of-Ban}", dateOfBan)
                                        .replace("{Date-Of-Ban-Ends}", dateOfBanEnds)
                                        .replace("{Unban-Of}", unbanOf);
                                p.sendMessage(message);
                            }
                        }
                    } else {
                        p.sendMessage(noPunishmentsOfThis);
                    }
                } else if (msg.contains("{Player-Mutes}")) {
                    if (punishmentsMute != 0) {
                        for (int i = 0; i < punishmentsMute; i++) {
                            String mutedOfPrefix = "";
                            String reason = "";
                            String durate = "";
                            String dateOfMute = "";
                            String dateOfMuteEnds = "";
                            String unMuteOf = notUnbanned;

                            if(mySQLEnabled) {
                                try{
                                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.MUTE.getTableName() + " WHERE (UUID='" + t.getUniqueId().toString() + "' AND PUNISHMENT_COUNT='" + (i + 1) + "')");

                                    if(rs.next()) {
                                        reason = rs.getString("REASON");
                                        dateOfMute = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_MUTE")));
                                        durate = rs.getString("DURATE");
                                        if(durate.equalsIgnoreCase("Permanent")) {
                                            dateOfMuteEnds = durate;
                                        }else{
                                            dateOfMuteEnds = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_MUTE_ENDS")));
                                        }
                                        if (rs.getString("UUID_MUTED_OF").equalsIgnoreCase("Console")) {
                                            mutedOfPrefix = SettingsFile.getConsolePrefix();
                                        } else {
                                            OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_MUTED_OF")));
                                            mutedOfPrefix = NewSystem.getName(bannedOf);
                                        }
                                        if (rs.getString("UUID_UNMUTE_OF") != null) {
                                            if (rs.getString("UUID_UNMUTE_OF").equalsIgnoreCase("Console")) {
                                                unMuteOf = SettingsFile.getConsolePrefix();
                                            } else {
                                                OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_UNMUTE_OF")));
                                                unMuteOf = NewSystem.getName(bannedOf);
                                            }
                                        }
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }else {
                                reason = SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + (i + 1) + ".Reason");
                                dateOfMute = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Mute." + t.getUniqueId() + "." + (i + 1) + ".Date-Of-Mute"));
                                durate = SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + (i + 1) + ".Durate");

                                if(SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + (i + 1) + ".Date-Of-Mute-Ends").equalsIgnoreCase("Permanent")) {
                                    dateOfMuteEnds = SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + (i + 1) + ".Date-Of-Mute-Ends");
                                }else{
                                    dateOfMuteEnds = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Mute." + t.getUniqueId() + "." + (i + 1) + ".Date-Of-Mute-Ends"));
                                }

                                if (SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + (i + 1) + ".MutedOf").equalsIgnoreCase("Console")) {
                                    mutedOfPrefix = SettingsFile.getConsolePrefix();
                                } else {
                                    OfflinePlayer mutedOf = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + (i + 1) + ".MutedOf")));
                                    mutedOfPrefix = NewSystem.getName(mutedOf);
                                }

                                if (SavingsFile.isPathSet("Punishment.Mute." + t.getUniqueId() + "." + (i + 1) + ".UnMuteOf")) {
                                    if (SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + (i + 1) + ".UnMuteOf").equalsIgnoreCase("Console")) {
                                        unMuteOf = SettingsFile.getConsolePrefix();
                                    } else {
                                        OfflinePlayer unMutedOf = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + (i + 1) + ".UnMuteOf")));
                                        unMuteOf = NewSystem.getName(unMutedOf);
                                    }
                                }
                            }

                            if (SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + (i + 1) + ".Durate").equalsIgnoreCase("Permanent")) {
                                String message = consoleMessageMutePermanent.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Muted-Of}", mutedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Mute}", dateOfMute)
                                        .replace("{UnMute-Of}", unMuteOf);
                                p.sendMessage(message);
                            } else {
                                String message = consoleMessageMuteTemporary.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Muted-Of}", mutedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Durate}", durate)
                                        .replace("{Date-Of-Mute}", dateOfMute)
                                        .replace("{Date-Of-Mute-Ends}", dateOfMuteEnds)
                                        .replace("{UnMute-Of}", unMuteOf);
                                p.sendMessage(message);
                            }
                        }
                    } else {
                        p.sendMessage(noPunishmentsOfThis);
                    }
                } else if (msg.contains("{Player-Kicks}")) {
                    if (punishmentsKick != 0) {
                        for (int i = 0; i < punishmentsKick; i++) {
                            String kickedOfPrefix = "";
                            String reason = "";
                            String dateOfKick = "";

                            if(mySQLEnabled){
                                try {
                                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.KICK.getTableName() + " WHERE (UUID='" + t.getUniqueId().toString() + "' AND PUNISHMENT_COUNT='" + (i + 1) + "')");

                                    if(rs.next()) {
                                        reason = rs.getString("REASON");
                                        dateOfKick = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_KICK")));
                                        if (rs.getString("UUID_KICK_OF").equalsIgnoreCase("Console")) {
                                            kickedOfPrefix = SettingsFile.getConsolePrefix();
                                        } else {
                                            OfflinePlayer kickedOf = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_KICK_OF")));
                                            kickedOfPrefix = NewSystem.getName(kickedOf);
                                        }
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }else {
                                reason = SavingsFile.getStringPath("Punishment.Kick." + t.getUniqueId() + "." + (i + 1) + ".Reason");
                                dateOfKick = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Kick." + t.getUniqueId() + "." + (i + 1) + ".Date-Of-Kick"));
                                if (SavingsFile.getStringPath("Punishment.Kick." + t.getUniqueId() + "." + (i + 1) + ".KickOf").equalsIgnoreCase("Console")) {
                                    kickedOfPrefix = SettingsFile.getConsolePrefix();
                                } else {
                                    OfflinePlayer kickedOf = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.Kick." + t.getUniqueId() + "." + (i + 1) + ".KickOf")));
                                    kickedOfPrefix = NewSystem.getName(kickedOf);
                                }
                            }

                            String message = consoleMessageKick.replace("{Prefix}", SettingsFile.getPrefix())
                                    .replace("{Kicked-Of}", kickedOfPrefix)
                                    .replace("{Reason}", reason)
                                    .replace("{Date-Of-Kick}", dateOfKick);
                            p.sendMessage(message);
                        }
                    } else {
                        p.sendMessage(noPunishmentsOfThis);
                    }
                } else if (msg.contains("{Active-Punishments}")) {
                    if (BanCmd.isPlayerBanned(t) || MuteCmd.isPlayerMuted(t)) {
                        String unBanOf = notUnbanned;
                        if (BanCmd.isPlayerBanned(t)) {
                            int punishmentCount = BanCmd.getPlayerPunishmentCount(t) - 1;
                            String dateOfBanEnds = "";
                            String durate = "";
                            String bannedOfPrefix = "";
                            String reason = "";
                            String dateOfBan = "";

                            if(mySQLEnabled) {
                                try {
                                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.BAN.getTableName() + " WHERE (UUID='" + t.getUniqueId().toString() + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");

                                    if(rs.next()) {
                                        durate = rs.getString("DURATE");
                                        reason = rs.getString("REASON");
                                        dateOfBan = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_BAN")));
                                        if(durate.equalsIgnoreCase("Permanent")) {
                                            dateOfBanEnds = durate;
                                        }else{
                                            dateOfBanEnds = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_BAN_ENDS")));
                                        }

                                        if (rs.getString("UUID_BANNED_OF").equalsIgnoreCase("Console")) {
                                            bannedOfPrefix = SettingsFile.getConsolePrefix();
                                        } else {
                                            OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_BANNED_OF")));
                                            bannedOfPrefix = NewSystem.getName(bannedOf);
                                        }
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }else{
                                durate = SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Durate");
                                reason = SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Reason");
                                dateOfBan = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Ban"));
                                if(SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Ban-Ends").equalsIgnoreCase("Permanent")) {
                                    dateOfBanEnds = "Permanent";
                                }else{
                                    dateOfBanEnds = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Ban-Ends"));
                                }
                                if (SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".BannedOf").equalsIgnoreCase("Console")) {
                                    bannedOfPrefix = SettingsFile.getConsolePrefix();
                                } else {
                                    OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.Ban." + t.getUniqueId() + "." + punishmentCount + ".BannedOf")));
                                    bannedOfPrefix = NewSystem.getName(bannedOf);
                                }
                            }

                            if (BanCmd.isBanPermanent(t)) {
                                String message = consoleMessageBanPermanent.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Banned-Of}", bannedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Ban}", dateOfBan)
                                        .replace("{Unban-Of}", unBanOf);
                                p.sendMessage(message);
                            } else {
                                String message = consoleMessageBanTemporary.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Banned-Of}", bannedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Durate}", durate)
                                        .replace("{Date-Of-Ban}", dateOfBan)
                                        .replace("{Date-Of-Ban-Ends}", dateOfBanEnds)
                                        .replace("{Unban-Of}", unBanOf);
                                p.sendMessage(message);
                            }
                        }
                        if (MuteCmd.isPlayerMuted(t)) {
                            int punishmentCount = MuteCmd.getPlayerPunishmentCount(t) - 1;
                            String mutedOfPrefix = "";
                            String reason = "";
                            String dateOfMute = "";
                            String dateOfMuteEnds = "";
                            String durate = "";

                            if(mySQLEnabled) {
                                try {
                                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.MUTE.getTableName() + " WHERE (UUID='" + t.getUniqueId().toString() + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");

                                    if(rs.next()) {
                                        reason = rs.getString("REASON");
                                        dateOfMute = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_MUTE")));
                                        durate = rs.getString("DURATE");
                                        if(durate.equalsIgnoreCase("Permanent")) {
                                            dateOfMuteEnds = durate;
                                        }else{
                                            dateOfMuteEnds = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_MUTE_ENDS")));
                                        }
                                        if (rs.getString("UUID_MUTED_OF").equalsIgnoreCase("Console")) {
                                            mutedOfPrefix = SettingsFile.getConsolePrefix();
                                        } else {
                                            OfflinePlayer mutedOf = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_MUTED_OF")));
                                            mutedOfPrefix = NewSystem.getName(mutedOf);
                                        }
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }else {
                                reason = SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Reason");
                                dateOfMute = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Mute"));
                                durate = SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".Durate");
                                if(SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + "Date-Of-Mute-Ends").equalsIgnoreCase("Permanent")) {
                                    dateOfMuteEnds = SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + "Date-Of-Mute-Ends");
                                }else{
                                    dateOfMuteEnds = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + "Date-Of-Mute-Ends"));
                                }
                                if (SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".MutedOf").equalsIgnoreCase("Console")) {
                                    mutedOfPrefix = SettingsFile.getConsolePrefix();
                                } else {
                                    OfflinePlayer mutedOf = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.Mute." + t.getUniqueId() + "." + punishmentCount + ".MutedOf")));
                                    mutedOfPrefix = NewSystem.getName(mutedOf);
                                }
                            }

                            if (MuteCmd.isMutePermanent(t)) {
                                String message = consoleMessageMutePermanent.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Muted-Of}", mutedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Mute}", dateOfMute)
                                        .replace("{UnMute-Of}", unBanOf);
                                p.sendMessage(message);
                            } else {
                                String message = consoleMessageMuteTemporary.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Muted-Of}", mutedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Durate}", durate)
                                        .replace("{Date-Of-Mute}", dateOfMute)
                                        .replace("{Date-Of-Mute-Ends}", dateOfMuteEnds)
                                        .replace("{UnMute-Of}", unBanOf);
                                p.sendMessage(message);
                            }
                        }
                    } else {
                        p.sendMessage(noPunishmentsOfThis);
                    }
                } else {
                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", prefix).replace("{Punishment-Count}", String.valueOf(punishments)));
                }
            }
        } else {
            for(String key : noPunishments) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", prefix));
            }

        }
    }

    public static void sendHistory(CommandSender p, String ip) {
        String prefix = ip.replace("-", ".");
        int punishmentsBan = BanIPCmd.getIPPunishmentCount(ip) - 1;
        int punishmentsMute = MuteIPCmd.getIPPunishmentCount(ip) - 1;
        int punishments = punishmentsBan + punishmentsMute;
        if (punishments > 0) {
            for (String msg : listMsgIP) {
                if (msg.contains("{Player-Bans}")) {
                    if (punishmentsBan != 0) {
                        for (int i = 0; i < punishmentsBan; i++) {
                            String bannedOfPrefix = "";
                            String reason = "";
                            String durate = "";
                            String dateOfBan = "";
                            String dateOfBanEnds = "";
                            String unbanOf = notUnbanned;

                            if(mySQLEnabled) {
                                try{
                                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.BANIP.getTableName() + " WHERE (IP='" + ip + "' AND PUNISHMENT_COUNT='" + (i + 1) + "')");

                                    if(rs.next()) {
                                        reason = rs.getString("REASON");
                                        dateOfBan = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_BAN")));
                                        durate = rs.getString("DURATE");
                                        if(durate.equalsIgnoreCase("Permanent")) {
                                            dateOfBanEnds = durate;
                                        }else{
                                            dateOfBanEnds = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_BAN_ENDS")));
                                        }

                                        if (rs.getString("UUID_BANNED_OF").equalsIgnoreCase("Console")) {
                                            bannedOfPrefix = SettingsFile.getConsolePrefix();
                                        } else {
                                            OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_BANNED_OF")));
                                            bannedOfPrefix = NewSystem.getName(bannedOf);
                                        }

                                        if (rs.getString("UUID_UNBAN_OF") != null) {
                                            if (rs.getString("UUID_UNBAN_OF").equalsIgnoreCase("Console")) {
                                                unbanOf = SettingsFile.getConsolePrefix();
                                            } else {
                                                OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_UNBAN_OF")));
                                                unbanOf = NewSystem.getName(bannedOf);
                                            }
                                        }
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }else {
                                dateOfBanEnds = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.BanIP." + ip + "." + (i + 1) + ".Date-Of-Ban-Ends"));
                                durate = SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + (i + 1) + ".Durate");
                                reason = SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + (i + 1) + ".Reason");

                                if(durate.equalsIgnoreCase("Permanent")) {
                                    dateOfBanEnds = durate;
                                }else{
                                    dateOfBan = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.BanIP." + ip + "." + (i + 1) + ".Date-Of-Ban"));
                                }

                                if (SavingsFile.isPathSet("Punishment.BanIP." + ip + "." + (i + 1) + ".UnbanOf")) {
                                    if(SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + (i + 1) + ".UnbanOf").equalsIgnoreCase("Console")) {
                                        unbanOf = SettingsFile.getConsolePrefix();
                                    }else {
                                        OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + (i + 1) + ".UnbanOf")));
                                        unbanOf = NewSystem.getName(bannedOf);
                                    }
                                }

                                if (SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + (i + 1) + ".BannedOf").equalsIgnoreCase("Console")) {
                                    bannedOfPrefix = SettingsFile.getConsolePrefix();
                                } else {
                                    OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + (i + 1) + ".BannedOf")));
                                    bannedOfPrefix = NewSystem.getName(bannedOf);
                                }
                            }

                            if (SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + (i + 1) + ".Durate").equalsIgnoreCase("Permanent")) {
                                String message = consoleMessageBanPermanent.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Banned-Of}", bannedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Ban}", dateOfBan)
                                        .replace("{Unban-Of}", unbanOf);
                                p.sendMessage(message);
                            } else {
                                String message = consoleMessageBanTemporary.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Banned-Of}", bannedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Durate}", durate)
                                        .replace("{Date-Of-Ban}", dateOfBan)
                                        .replace("{Date-Of-Ban-Ends}", dateOfBanEnds)
                                        .replace("{Unban-Of}", unbanOf);
                                p.sendMessage(message);
                            }
                        }
                    } else {
                        p.sendMessage(noPunishmentsOfThis);
                    }
                } else if (msg.contains("{Player-Mutes}")) {
                    if (punishmentsMute != 0) {
                        for (int i = 0; i < punishmentsMute; i++) {
                            String mutedOfPrefix = "";
                            String reason = "";
                            String durate = "";
                            String dateOfMute = "";
                            String dateOfMuteEnds = "";
                            String unMuteOf = notUnbanned;

                            if(mySQLEnabled) {
                                try{
                                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.MUTEIP.getTableName() + " WHERE (IP='" + ip + "' AND PUNISHMENT_COUNT='" + (i + 1) + "')");

                                    if(rs.next()) {
                                        reason = rs.getString("REASON");
                                        dateOfMute = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_MUTE")));
                                        durate = rs.getString("DURATE");
                                        if(durate.equalsIgnoreCase("Permanent")) {
                                            dateOfMuteEnds = durate;
                                        }else{
                                            dateOfMuteEnds = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_MUTE_ENDS")));
                                        }
                                        if (rs.getString("UUID_MUTED_OF").equalsIgnoreCase("Console")) {
                                            mutedOfPrefix = SettingsFile.getConsolePrefix();
                                        } else {
                                            OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_MUTED_OF")));
                                            mutedOfPrefix = NewSystem.getName(bannedOf);
                                        }
                                        if (rs.getString("UUID_UNMUTE_OF") != null) {
                                            if (rs.getString("UUID_UNMUTE_OF").equalsIgnoreCase("Console")) {
                                                unMuteOf = SettingsFile.getConsolePrefix();
                                            } else {
                                                OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_UNMUTE_OF")));
                                                unMuteOf = NewSystem.getName(bannedOf);
                                            }
                                        }
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }else {
                                reason = SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + (i + 1) + ".Reason");
                                dateOfMute = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.MuteIP." + ip + "." + (i + 1) + ".Date-Of-Mute"));
                                durate = SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + (i + 1) + ".Durate");

                                if(durate.equalsIgnoreCase("Permanent")) {
                                    dateOfMuteEnds = durate;
                                }else{
                                    dateOfMuteEnds = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.MuteIP." + ip + "." + (i + 1) + ".Date-Of-Mute-Ends"));
                                }

                                if (SavingsFile.isPathSet("Punishment.MuteIP." + ip + "." + (i + 1) + ".UnMuteOf")) {
                                    if (SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + (i + 1) + ".UnMuteOf").equalsIgnoreCase("Console")) {
                                        unMuteOf = SettingsFile.getConsolePrefix();
                                    } else {
                                        OfflinePlayer unMutedOf = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + (i + 1) + ".UnMuteOf")));
                                        unMuteOf = NewSystem.getName(unMutedOf);
                                    }
                                }

                                if (SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + (i + 1) + ".MutedOf").equalsIgnoreCase("Console")) {
                                    mutedOfPrefix = SettingsFile.getConsolePrefix();
                                } else {
                                    OfflinePlayer mutedOf = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + (i + 1) + ".MutedOf")));
                                    mutedOfPrefix = NewSystem.getName(mutedOf);
                                }
                            }

                            if (SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + (i + 1) + ".Durate").equalsIgnoreCase("Permanent")) {
                                String message = consoleMessageMutePermanent.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Muted-Of}", mutedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Mute}", dateOfMute)
                                        .replace("{UnMute-Of}", unMuteOf);
                                p.sendMessage(message);
                            } else {
                                String message = consoleMessageMuteTemporary.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Muted-Of}", mutedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Durate}", durate)
                                        .replace("{Date-Of-Mute}", dateOfMute)
                                        .replace("{Date-Of-Mute-Ends}", dateOfMuteEnds)
                                        .replace("{UnMute-Of}", unMuteOf);
                                p.sendMessage(message);
                            }
                        }
                    } else {
                        p.sendMessage(noPunishmentsOfThis);
                    }
                }else if (msg.contains("{Active-Punishments}")) {
                    if (BanIPCmd.isIPBanned(ip) || MuteIPCmd.isIPMuted(ip)) {
                        String unBanOf = notUnbanned;
                        if (BanIPCmd.isIPBanned(ip)) {
                            int punishmentCount = BanIPCmd.getIPPunishmentCount(ip) - 1;
                            String dateOfBanEnds = "";
                            String durate = "";
                            String bannedOfPrefix = "";
                            String reason = "";
                            String dateOfBan = "";

                            if(mySQLEnabled) {
                                try {
                                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.BANIP.getTableName() + " WHERE (IP='" + ip + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");

                                    if(rs.next()) {
                                        durate = rs.getString("DURATE");
                                        reason = rs.getString("REASON");
                                        dateOfBan = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_BAN")));
                                        if(durate.equalsIgnoreCase("Permanent")) {
                                            dateOfBanEnds = durate;
                                        }else{
                                            dateOfBanEnds = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_BAN_ENDS")));
                                        }

                                        if (rs.getString("UUID_BANNED_OF").equalsIgnoreCase("Console")) {
                                            bannedOfPrefix = SettingsFile.getConsolePrefix();
                                        } else {
                                            OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_BANNED_OF")));
                                            bannedOfPrefix = NewSystem.getName(bannedOf);
                                        }
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }else {
                                reason = SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Reason");
                                dateOfBan = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban"));
                                durate = SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Durate");

                                if(durate.equalsIgnoreCase("Permanent")) {
                                    dateOfBanEnds = durate;
                                }else{
                                    dateOfBanEnds = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban-Ends"));
                                }

                                if (SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + punishmentCount + ".BannedOf").equalsIgnoreCase("Console")) {
                                    bannedOfPrefix = SettingsFile.getConsolePrefix();
                                } else {
                                    OfflinePlayer bannedOf = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + punishmentCount + ".BannedOf")));
                                    bannedOfPrefix = NewSystem.getName(bannedOf);
                                }
                            }

                            if (BanIPCmd.isBanPermanent(ip)) {
                                String message = consoleMessageBanPermanent.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Banned-Of}", bannedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Ban}", dateOfBan)
                                        .replace("{Unban-Of}", unBanOf);
                                p.sendMessage(message);
                            } else {
                                String message = consoleMessageBanTemporary.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Banned-Of}", bannedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Durate}", durate)
                                        .replace("{Date-Of-Ban}", dateOfBan)
                                        .replace("{Date-Of-Ban-Ends}", dateOfBanEnds)
                                        .replace("{Unban-Of}", unBanOf);
                                p.sendMessage(message);
                            }
                        }
                        if (MuteIPCmd.isIPMuted(ip)) {
                            int punishmentCount = MuteIPCmd.getIPPunishmentCount(ip) - 1;
                            String mutedOfPrefix = "";
                            String reason = "";
                            String dateOfMute = "";
                            String dateOfMuteEnds = "";
                            String durate = "";

                            if(mySQLEnabled) {
                                try {
                                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.MUTEIP.getTableName() + " WHERE (IP='" + ip + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");

                                    if(rs.next()) {
                                        reason = rs.getString("REASON");
                                        dateOfMute = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_MUTE")));
                                        durate = rs.getString("DURATE");
                                        if(durate.equalsIgnoreCase("Permanent")) {
                                            dateOfMuteEnds = durate;
                                        }else{
                                            dateOfMuteEnds = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_MUTE_ENDS")));
                                        }
                                        if (rs.getString("UUID_MUTED_OF").equalsIgnoreCase("Console")) {
                                            mutedOfPrefix = SettingsFile.getConsolePrefix();
                                        } else {
                                            OfflinePlayer mutedOf = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_MUTED_OF")));
                                            mutedOfPrefix = NewSystem.getName(mutedOf);
                                        }
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }else {
                                reason = SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Reason");
                                dateOfMute = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Date-Of-Mute"));
                                durate = SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Durate");

                                if(durate.equalsIgnoreCase("Permanent")) {
                                    dateOfMuteEnds = durate;
                                }else{
                                    dateOfMuteEnds = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.MuteIP." + ip + "." + punishmentCount + "Date-Of-Mute-Ends"));
                                }

                                if (SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".MutedOf").equalsIgnoreCase("Console")) {
                                    mutedOfPrefix = SettingsFile.getConsolePrefix();
                                } else {
                                    OfflinePlayer mutedOf = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".MutedOf")));
                                    mutedOfPrefix = NewSystem.getName(mutedOf);
                                }
                            }

                            if (MuteIPCmd.isMutePermanent(ip)) {
                                String message = consoleMessageMutePermanent.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Muted-Of}", mutedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Date-Of-Mute}", dateOfMute)
                                        .replace("{UnMute-Of}", unBanOf);
                                p.sendMessage(message);
                            } else {
                                String message = consoleMessageMuteTemporary.replace("{Prefix}", SettingsFile.getPrefix())
                                        .replace("{Muted-Of}", mutedOfPrefix)
                                        .replace("{Reason}", reason)
                                        .replace("{Durate}", durate)
                                        .replace("{Date-Of-Mute}", dateOfMute)
                                        .replace("{Date-Of-Mute-Ends}", dateOfMuteEnds)
                                        .replace("{UnMute-Of}", unBanOf);
                                p.sendMessage(message);
                            }
                        }
                    } else {
                        p.sendMessage(noPunishmentsOfThis);
                    }
                } else {
                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", prefix).replace("{Punishment-Count}", String.valueOf(punishments)));
                }
            }
        } else {
            for(String key : noPunishmentsIP) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{IP}", prefix));
            }

        }
    }

}
