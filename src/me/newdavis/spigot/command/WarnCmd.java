package me.newdavis.spigot.command;

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

public class WarnCmd implements CommandExecutor {

    private static MySQL mySQL = NewSystem.getMySQL();
    private static boolean mySQLEnabled = SettingsFile.getMySQLEnabled();

    private static String perm;
    private static List<String> usage;
    private static List<String> msg;

    public void init() {
        perm = CommandFile.getStringPath("Command.Warn.Permission");
        usage = CommandFile.getStringListPath("Command.Warn.Usage");
        msg = CommandFile.getStringListPath("Command.Warn.Message");
        NewSystem.getInstance().getCommand("warn").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if(args.length == 2) {
                    OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                    String reason = "";
                    for(int i = 0; i < args.length ;i++) {
                        if(i != 0) {
                            reason += (reason.isEmpty() ? "" : " ") + args[i];
                        }
                    }
                    createWarn(p, t, reason);
                }else{
                    for(String value : usage) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }else{
                p.sendMessage(SettingsFile.getNoPerm());
            }
        }else{
            if(args.length == 2) {
                OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                String reason = "";
                for(int i = 0; i < args.length ;i++) {
                    if(i != 0) {
                        reason = reason + (reason.isEmpty() ? "" : " ") + args[i];
                    }
                }
                createWarn(sender, t, reason);
            }else{
                for(String value : usage) {
                    sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }
        return false;
    }

    public static int getPlayerPunishmentCount(OfflinePlayer p) {
        if (mySQLEnabled) {
            if (mySQL.hasNext("SELECT UUID FROM " + SQLTables.WARN.getTableName() + " WHERE UUID='" + p.getUniqueId().toString() + "'")) {
                try {
                    ResultSet rs = NewSystem.getMySQL().executeQuery("SELECT UUID FROM " + SQLTables.WARN.getTableName() + " WHERE (UUID='" + p.getUniqueId().toString() + "')");

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
            if (SavingsFile.isPathSet("Punishment.Warn." + p.getUniqueId() + ".1")) {
                Set<String> keys = SavingsFile.yaml.getConfigurationSection("Punishment.Warn." + p.getUniqueId()).getKeys(false);
                return keys.size() + 1;
            }
        }
        return 1;
    }

    private static void createWarn(Player p, OfflinePlayer t, String reason) {
        int punishmentCount = getPlayerPunishmentCount(t);

        if(mySQLEnabled) {
            mySQL.executeUpdate("INSERT INTO " + SQLTables.WARN.getTableName() + " (UUID,PUNISHMENT_COUNT,UUID_WARN_OF,REASON,DATE_OF_WARN)" + " VALUES " +
                    "('" + t.getUniqueId().toString() + "'," +
                    "'" + punishmentCount + "'," +
                    "'" + p.getUniqueId().toString() + "'," +
                    "'" + reason + "'," +
                    "'" + System.currentTimeMillis() + "')");
        }else {
            SavingsFile.setPath("Punishment.Warn." + t.getUniqueId() + "." + punishmentCount + ".WarnOf", p.getUniqueId().toString());
            SavingsFile.setPath("Punishment.Warn." + t.getUniqueId() + "." + punishmentCount + ".Reason", reason);
            SavingsFile.setPath("Punishment.Warn." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Warn", System.currentTimeMillis());
        }

        for(String key : msg) {
            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Reason}", reason));
        }
    }

    private static void createWarn(CommandSender p, OfflinePlayer t, String reason) {
        int punishmentCount = getPlayerPunishmentCount(t);

        if(mySQLEnabled) {
            mySQL.executeUpdate("INSERT INTO " + SQLTables.WARN.getTableName() + " (UUID,PUNISHMENT_COUNT,UUID_WARN_OF,REASON,DATE_OF_WARN)" + " VALUES " +
                    "('" + t.getUniqueId().toString() + "'," +
                    "'" + punishmentCount + "'," +
                    "'Console'," +
                    "'" + reason + "'," +
                    "'" + System.currentTimeMillis() + "')");
        }else {
            SavingsFile.setPath("Punishment.Warn." + t.getUniqueId() + "." + punishmentCount + ".WarnOf", "Console");
            SavingsFile.setPath("Punishment.Warn." + t.getUniqueId() + "." + punishmentCount + ".Reason", reason);
            SavingsFile.setPath("Punishment.Warn." + t.getUniqueId() + "." + punishmentCount + ".Date-Of-Warn", System.currentTimeMillis());
        }

        for(String key : msg) {
            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Reason}", reason));
        }
    }
}
