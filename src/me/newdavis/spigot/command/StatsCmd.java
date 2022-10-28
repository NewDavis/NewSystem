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
import java.text.DecimalFormat;
import java.util.List;

public class StatsCmd implements CommandExecutor {

    private static MySQL mySQL = NewSystem.getMySQL();
    private static boolean mySQLEnabled  = SettingsFile.getMySQLEnabled();

    private static List<String> usage;
    private static String perm;
    private static String permOther;
    private static String showSelf;
    private static List<String> message;

    public void init() {
        usage = CommandFile.getStringListPath("Command.Stats.Usage");
        perm = CommandFile.getStringPath("Command.Stats.Permission.Use");
        permOther = CommandFile.getStringPath("Command.Stats.Permission.Other");
        showSelf = CommandFile.getStringPath("Command.Stats.ShowSelf");
        message = CommandFile.getStringListPath("Command.Stats.Message");
        NewSystem.getInstance().getCommand("stats").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if(args.length == 0) {
                    sendStats(p, p);
                }else if(args.length == 1) {
                    if(NewSystem.hasPermission(p, permOther)) {
                        OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                        sendStats(p, t);
                    }else{
                        p.sendMessage(SettingsFile.getNoPerm());
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
                OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                sendStats(sender, t);
            }else{
                for(String value : usage) {
                    sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }
        return false;
    }

    private void sendStats(Player p, OfflinePlayer t) {
        if((mySQLEnabled && mySQL.hasNext("SELECT UUID FROM " + SQLTables.STATS.getTableName() + "WHERE UUID='" + t.getUniqueId().toString() + "'"))
                || (mySQLEnabled && (!mySQL.hasNext("SELECT UUID FROM " + SQLTables.STATS.getTableName() + "WHERE UUID='" + t.getUniqueId().toString() + "'")) && t.isOnline())
                || SavingsFile.isPathSet("Stats." + t.getUniqueId())) {
            int kills = 0;
            int deaths = 0;
            if(mySQLEnabled) {
                if(mySQL.hasNext("SELECT UUID FROM " + SQLTables.STATS.getTableName() + "WHERE UUID='" + t.getUniqueId().toString() + "'")) {
                    try {
                        ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.STATS.getTableName() + " WHERE UUID='" + t.getUniqueId().toString() + "'");

                        if (rs.next()) {
                            kills = rs.getInt("KILLS");
                            deaths = rs.getInt("DEATHS");
                        }
                        mySQL.disconnect();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }else {
                    mySQL.executeUpdate("INSERT INTO " + SQLTables.STATS.getTableName() + " (UUID,KILLS,DEATHS) VALUES " +
                            "('" + t.getUniqueId().toString() + "'," +
                            "'" + kills + "'," +
                            "'" + deaths + "')");
                }
            }else{
                kills = SavingsFile.getIntegerPath("Stats." + t.getUniqueId() + ".Kills");
                deaths = SavingsFile.getIntegerPath("Stats." + t.getUniqueId() + ".Deaths");
            }
            String kd = String.valueOf((double) kills / deaths);
            if(!kd.equalsIgnoreCase("NaN")) {
                DecimalFormat format = new DecimalFormat("#0.00");
                kd = format.format(((double) kills / deaths));
            }

            String playTime = PlayTimeCmd.getPlayTime(t.getUniqueId().toString());

            for (String msg : message) {
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", (p == t ? showSelf : NewSystem.getName(t)))
                        .replace("{Kills}", String.valueOf(kills)).replace("{Deaths}", String.valueOf(deaths))
                        .replace("{K/D}", kd).replace("{PlayTime}", playTime));
            }
        }else{
            p.sendMessage(SettingsFile.getOffline());
        }
    }

    private void sendStats(CommandSender p, OfflinePlayer t) {
        if((mySQLEnabled && mySQL.hasNext("SELECT UUID FROM " + SQLTables.STATS.getTableName() + "WHERE UUID='" + t.getUniqueId().toString() + "'"))
                || (mySQLEnabled && (!mySQL.hasNext("SELECT UUID FROM " + SQLTables.STATS.getTableName() + "WHERE UUID='" + t.getUniqueId().toString() + "'")) && t.isOnline())
                || SavingsFile.isPathSet("Stats." + t.getUniqueId())) {
            int kills = 0;
            int deaths = 0;
            if(mySQLEnabled) {
                if(mySQL.hasNext("SELECT UUID FROM " + SQLTables.STATS.getTableName() + "WHERE UUID='" + t.getUniqueId().toString() + "'")) {
                    try {
                        ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.STATS.getTableName() + " WHERE UUID='" + t.getUniqueId().toString() + "'");

                        if (rs.next()) {
                            kills = rs.getInt("KILLS");
                            deaths = rs.getInt("DEATHS");
                        }
                        mySQL.disconnect();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }else{
                    mySQL.executeUpdate("INSERT INTO " + SQLTables.STATS.getTableName() + " (UUID,KILLS,DEATHS) VALUES " +
                            "('" + t.getUniqueId().toString() + "'," +
                            "'" + kills + "'," +
                            "'" + deaths + "')");
                }
            }else{
                kills = SavingsFile.getIntegerPath("Stats." + t.getUniqueId() + ".Kills");
                deaths = SavingsFile.getIntegerPath("Stats." + t.getUniqueId() + ".Deaths");
            }
            String kd = String.valueOf((double) kills / deaths);
            if(!kd.equalsIgnoreCase("NaN")) {
                DecimalFormat format = new DecimalFormat("#0.00");
                kd = format.format(((double) kills / deaths));
            }

            String playTime = PlayTimeCmd.getPlayTime(t.getUniqueId().toString());

            for (String msg : message) {
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", (p == t ? showSelf : NewSystem.getName(t)))
                        .replace("{Kills}", String.valueOf(kills)).replace("{Deaths}", String.valueOf(deaths))
                        .replace("{K/D}", kd).replace("{PlayTime}", playTime));
            }
        }else{
            p.sendMessage(SettingsFile.getOffline());
        }
    }
}
