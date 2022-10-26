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
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class KickCmd implements CommandExecutor {

    private static MySQL mySQL = NewSystem.getMySQL();
    private static boolean mySQLEnabled = SettingsFile.getMySQLEnabled();

    private static List<String> usage;
    private static String perm;
    private static String permNoKick;
    private static String kickMessage;
    private static List<String> msgP;
    private static List<String> listMessage;
    private static List<String> msgCanNotKicked;

    public void init() {
        usage = CommandFile.getStringListPath("Command.Kick.Usage");
        perm = CommandFile.getStringPath("Command.Kick.Permission.Use");
        permNoKick = CommandFile.getStringPath("Command.Kick.Permission.CanNotKick");
        kickMessage = CommandFile.getStringPath("Command.Kick.KickMessage").replace("{Prefix}", SettingsFile.getPrefix());
        msgP = CommandFile.getStringListPath("Command.Kick.MessagePlayer");
        listMessage = CommandFile.getStringListPath("Command.Kick.Message");
        msgCanNotKicked = CommandFile.getStringListPath("Command.Kick.PlayerCanNotGetKicked");
        NewSystem.getInstance().getCommand("kick").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (NewSystem.hasPermission(p, perm)) {
                if (args.length >= 2) {
                    Player t = Bukkit.getPlayer(args[0]);
                    if (t != null) {
                        String reason = getReason(args);
                        kickPlayer(p, t, reason);
                    } else {
                        p.sendMessage(SettingsFile.getOffline());
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
            if (args.length >= 2) {
                Player t = Bukkit.getPlayer(args[0]);
                if (t != null) {
                    String reason = getReason(args);
                    kickPlayer(sender, t, reason);
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

    public static String getReason(String[] args) {
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

    public static int getPlayerPunishmentCount(OfflinePlayer p) {
        if (mySQLEnabled) {
            if (mySQL.hasNext("SELECT UUID FROM " + SQLTables.KICK.getTableName() + " WHERE UUID='" + p.getUniqueId().toString() + "'")) {
                try {
                    ResultSet rs = mySQL.executeQuery("SELECT UUID FROM " + SQLTables.KICK.getTableName() + " WHERE UUID='" + p.getUniqueId().toString() + "'");

                    int i = 1;
                    while (rs.next()) {
                        i++;
                    }
                    return i;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            if (SavingsFile.isPathSet("Punishment.Kick." + p.getUniqueId() + ".1")) {
                Set<String> keys = SavingsFile.yaml.getConfigurationSection("Punishment.Kick." + p.getUniqueId()).getKeys(false);
                return keys.size() + 1;
            }
        }
        return 1;
    }

    public static void kickPlayer(Player p, Player t, String reason) {
        if (!NewSystem.hasPermission(t, permNoKick)) {
            t.kickPlayer(kickMessage.replace("{Reason}", reason));
            int punishmentCount = getPlayerPunishmentCount(t);

            if (mySQLEnabled) {
                mySQL.executeUpdate("INSERT INTO " + SQLTables.KICK.getTableName() + " (UUID,PUNISHMENT_COUNT,UUID_KICK_OF,REASON,DATE_OF_KICK) VALUES " +
                        "('" + t.getUniqueId().toString() + "'," +
                        "'" + punishmentCount + "'," +
                        "'" + p.getUniqueId().toString() + "'," +
                        "'" + reason + "'," +
                        "'" + System.currentTimeMillis() + "')");
            } else {
                SavingsFile.setPath("Punishment.Kick." + t.getUniqueId() + "." + punishmentCount + ".KickOf", p.getUniqueId().toString());
                SavingsFile.setPath("Punishment.Kick." + t.getUniqueId() + "." + punishmentCount + ".Reason", reason);
                SavingsFile.setPath("Punishment.Kick." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Kick", System.currentTimeMillis());
            }

            for (String key : msgP) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)));
            }

            for (Player all : Bukkit.getOnlinePlayers()) {
                for (String msg : listMessage) {
                    if (NewSystem.hasPermission(all, perm)) {
                        all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                .replace("{Player}", NewSystem.getName(t))
                                .replace("{Kick-Of}", NewSystem.getName(p))
                                .replace("{Reason}", reason));
                    }
                }
            }
        } else {
            for (String value : msgCanNotKicked) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void kickPlayer(CommandSender p, Player t, String reason) {
        t.kickPlayer(kickMessage.replace("{Reason}", reason));
        int punishmentCount = getPlayerPunishmentCount(t);

        if (mySQLEnabled) {
            mySQL.executeUpdate("INSERT INTO " + SQLTables.KICK.getTableName() + " (UUID,PUNISHMENT_COUNT,UUID_KICK_OF,REASON,DATE_OF_KICK) VALUES " +
                    "('" + t.getUniqueId().toString() + "'," +
                    "'" + punishmentCount + "'," +
                    "'Console'," +
                    "'" + reason + "'," +
                    "'" + System.currentTimeMillis() + "')");
        } else {
            SavingsFile.setPath("Punishment.Kick." + t.getUniqueId() + "." + punishmentCount + ".KickOf", "Console");
            SavingsFile.setPath("Punishment.Kick." + t.getUniqueId() + "." + punishmentCount + ".Reason", reason);
            SavingsFile.setPath("Punishment.Kick." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Kick", System.currentTimeMillis());
        }

        for (String key : msgP) {
            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)));
        }

        for (Player all : Bukkit.getOnlinePlayers()) {
            for (String msg : listMessage) {
                if (NewSystem.hasPermission(all, perm)) {
                    all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                            .replace("{Player}", NewSystem.getName(t))
                            .replace("{Kick-Of}", SettingsFile.getConsolePrefix())
                            .replace("{Reason}", reason));
                }
            }
        }
    }
}