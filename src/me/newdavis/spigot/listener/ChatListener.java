package me.newdavis.spigot.listener;

import me.newdavis.other.EnterValue;
import me.newdavis.spigot.api.ReflectionAPI;
import me.newdavis.spigot.command.MuteIPCmd;
import me.newdavis.spigot.command.SupportCmd;
import me.newdavis.spigot.file.*;
import me.newdavis.spigot.sql.MySQL;
import me.newdavis.spigot.sql.SQLTables;
import me.newdavis.spigot.util.ChatFilter;
import me.newdavis.spigot.command.GlobalMuteCmd;
import me.newdavis.spigot.command.MuteCmd;
import me.newdavis.spigot.plugin.NewSystem;
import me.newdavis.spigot.plugin.newsystem.inventory.command.ChangeValueCommand;
import me.newdavis.spigot.plugin.newsystem.inventory.listener.ChangeValueListener;
import me.newdavis.spigot.plugin.newsystem.inventory.kit.ChangeValueKit;
import me.newdavis.spigot.plugin.newsystem.inventory.other.ChangeValueOther;
import me.newdavis.spigot.plugin.newsystem.inventory.settings.ChangeValueSettings;
import me.newdavis.spigot.plugin.newsystem.inventory.tablist.ChangeValueTabList;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ChatListener implements Listener {

    private static final MySQL mySQL = NewSystem.getMySQL();
    private static final boolean mySQLEnabled  = SettingsFile.getMySQLEnabled();

    private static String chatColorPerm;
    private static String chatColorRGB;
    private static String format;
    private static boolean mention;
    private static String mentionKey;
    private static String mentionPrefix;

    public void init() {
        chatColorPerm = ListenerFile.getStringPath("Listener.Chat.Permission.Color");
        chatColorRGB = ListenerFile.getStringPath("Listener.Chat.Permission.RGB");
        format = ListenerFile.getStringPath("Listener.Chat.Format");
        mention = ListenerFile.getBooleanPath("Listener.Chat.Mention");
        mentionKey = ListenerFile.getStringPath("Listener.Chat.MentionKey");
        mentionPrefix = ListenerFile.getStringPath("Listener.Chat.MentionPrefix");
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String msg = e.getMessage();

        if(NewSystem.newPerm) {
            if (EnterValue.hasToEnterValue(p)) {
                e.setCancelled(true);
                return;
            }
        }

        if(ChangeValueCommand.chat(p, msg)) {
            e.setCancelled(true);
            return;
        }
        if(ChangeValueListener.chat(p, msg)) {
            e.setCancelled(true);
            return;
        }
        if(ChangeValueKit.chat(p, msg)) {
            e.setCancelled(true);
            return;
        }
        if(ChangeValueOther.chat(p, msg)) {
            e.setCancelled(true);
            return;
        }
        if(ChangeValueTabList.chat(p, msg)) {
            e.setCancelled(true);
            return;
        }
        if(ChangeValueSettings.chat(p, msg)) {
            e.setCancelled(true);
            return;
        }

        if(CommandFile.getBooleanPath("Command.Support.Enabled")) {
            if(SupportCmd.playerIsInSupportTicket(p)) {
                e.setCancelled(true);
                SupportCmd.ChatEvent(p, (NewSystem.hasPermission(p, chatColorRGB) ? NewSystem.replace(msg) : msg));
                return;
            }
        }

        if(CommandFile.getBooleanPath("Command.MuteIP.Enabled")) {
            String ip = new ReflectionAPI().getPlayerIP(p);
            if(MuteIPCmd.isIPMuted(ip)) {
                e.setCancelled(true);
                int punishmentCount = MuteIPCmd.getIPPunishmentCount(ip)-1;
                String reason = SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Reason");
                String dateOfMute = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Date-Of-Mute"));
                String durate = "";
                String dateOfMuteEnds = "";

                if(mySQLEnabled) {
                    try {
                        ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.MUTEIP.getTableName() + " WHERE (IP='" + ip + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");

                        if(rs.next()) {
                            reason = rs.getString("REASON");
                            dateOfMute = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_MUTE")));
                            durate = rs.getString("DURATE");
                            if(durate.equalsIgnoreCase("Permanent")) {
                                dateOfMuteEnds = durate;
                            }else {
                                dateOfMuteEnds = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_MUTE_ENDS")));
                            }
                        }
                        mySQL.disconnect();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }else{
                    reason = SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Reason");
                    dateOfMute = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Date-Of-Mute"));
                    durate = SavingsFile.getStringPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Durate");
                    if(durate.equalsIgnoreCase("Permanent")) {
                        dateOfMuteEnds = durate;
                    }else {
                        dateOfMuteEnds = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.MuteIP." + ip + "." + punishmentCount + ".Date-Of-Mute-Ends"));
                    }
                }

                if(MuteIPCmd.isMutePermanent(ip)) {
                    for(String message : MuteIPCmd.chatMessagePermanent) {
                        p.sendMessage(message.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Reason}", reason).replace("{Date-Of-Mute}", dateOfMute));
                    }
                }else{
                    for(String message : MuteIPCmd.chatMessageTemporary) {
                        p.sendMessage(message.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Reason}", reason).replace("{Durate}", durate).replace("{Date-Of-Mute}", dateOfMute).replace("{Date-Of-Mute-Ends}", dateOfMuteEnds));
                    }
                }
                return;
            }
        }

        if(CommandFile.getBooleanPath("Command.Mute.Enabled")) {
            if(MuteCmd.isPlayerMuted(p)) {
                e.setCancelled(true);
                int punishmentCount = MuteCmd.getPlayerPunishmentCount(p)-1;
                String reason = "";
                String dateOfMute = "";
                String durate = "";
                String dateOfMuteEnds = "";

                if(mySQLEnabled) {
                    try {
                        ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.MUTE.getTableName() + " WHERE (UUID='" + p.getUniqueId() + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");

                        if(rs.next()) {
                            reason = rs.getString("REASON");
                            dateOfMute = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_MUTE")));
                            durate = rs.getString("DURATE");
                            if(durate.equalsIgnoreCase("Permanent")) {
                                dateOfMuteEnds = durate;
                            }else {
                                dateOfMuteEnds = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_MUTE_ENDS")));
                            }
                        }
                        mySQL.disconnect();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }else{
                    reason = SavingsFile.getStringPath("Punishment.Mute." + p.getUniqueId().toString() + "." + punishmentCount + ".Reason");
                    dateOfMute = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Mute." + p.getUniqueId().toString() + "." + punishmentCount + ".Date-Of-Mute"));
                    durate = SavingsFile.getStringPath("Punishment.Mute." + p.getUniqueId().toString() + "." + punishmentCount + ".Durate");
                    if(durate.equalsIgnoreCase("Permanent")) {
                        dateOfMuteEnds = durate;
                    }else {
                        dateOfMuteEnds = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Mute." + p.getUniqueId().toString() + "." + punishmentCount + ".Date-Of-Mute-Ends"));
                    }
                }

                if(MuteCmd.isMutePermanent(p)) {
                    for(String message : MuteCmd.chatMessagePermanent) {
                        p.sendMessage(message.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Reason}", reason).replace("{Date-Of-Mute}", dateOfMute));
                    }
                }else{
                    for(String message : MuteCmd.chatMessageTemporary) {
                        p.sendMessage(message.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Reason}", reason).replace("{Durate}", durate).replace("{Date-Of-Mute}", dateOfMute).replace("{Date-Of-Mute-Ends}", dateOfMuteEnds));
                    }
                }
                return;
            }
        }

        if(CommandFile.getBooleanPath("Command.GlobalMute.Enabled")) {
            if(GlobalMuteCmd.globalMute) {
                if(!NewSystem.hasPermission(p, GlobalMuteCmd.permByPass)) {
                    e.setCancelled(true);
                    for(String value : GlobalMuteCmd.messageListener) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                    return;
                }
            }
        }

        if(OtherFile.getBooleanPath("Other.ChatFilter.Enabled")) {
            ChatFilter cf = new ChatFilter(p, msg);
            if(cf.checkForBadWords() || cf.checkForAdvertising()) {
                e.setCancelled(true);
                return;
            }
        }

        if(mention) {
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (all != p) {
                    if (msg.contains(mentionKey + all.getName())) {
                        msg = msg.replace(mentionKey + all.getName(), mentionPrefix.replace("{Player}", all.getName()));
                        all.playSound(all.getLocation(), Sound.LEVEL_UP, 3, 10);
                    } else if (msg.contains(all.getName())) {
                        msg = msg.replace(all.getName(), mentionPrefix.replace("{Player}", all.getName()));
                        all.playSound(all.getLocation(), Sound.LEVEL_UP, 3, 10);
                    }
                }
            }
        }

        if(ListenerFile.getBooleanPath("Listener.Chat.Enabled")) {
            if(NewSystem.hasPermission(p, chatColorRGB)) {
                msg = NewSystem.replace(msg);
            }
            e.setCancelled(true);
            if (NewSystem.hasPermission(p, chatColorPerm)) {
                Bukkit.broadcastMessage(format.replace("{Prefix}", SettingsFile.getPrefix())
                        .replace("{Player}", NewSystem.getName(p, false)).replace("{DisplayName}", NewSystem.getName(p, true))
                        .replace("{Time}", SettingsFile.TimeFormat(System.currentTimeMillis()))
                        .replace("{Date}", SettingsFile.DateFormat(System.currentTimeMillis()))
                        .replace("{Message}", msg.replace('&', '§')));
            } else {
                Bukkit.broadcastMessage(format.replace("{Prefix}", SettingsFile.getPrefix())
                        .replace("{Player}", NewSystem.getName(p, false)).replace("{DisplayName}", NewSystem.getName(p, true))
                        .replace("{Time}", SettingsFile.TimeFormat(System.currentTimeMillis()))
                        .replace("{Date}", SettingsFile.DateFormat(System.currentTimeMillis()))
                        .replace("{Message}", msg));
            }
        }
    }

}
