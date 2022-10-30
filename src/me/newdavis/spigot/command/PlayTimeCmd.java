package me.newdavis.spigot.command;
//Plugin by NewDavis

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.OtherFile;
import me.newdavis.spigot.file.SavingsFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.sql.MySQL;
import me.newdavis.spigot.sql.SQLTables;
import me.newdavis.spigot.util.PlayTimeReward;
import me.newdavis.spigot.util.ScoreboardManager;
import me.newdavis.spigot.plugin.NewSystem;
import me.newdavis.spigot.util.placeholder.PlaceholderManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayTimeCmd implements CommandExecutor {

    private static MySQL mySQL = NewSystem.getMySQL();
    private static boolean mySQLEnabled  = SettingsFile.getMySQLEnabled();

    private static List<String> usage;
    private static String perm;
    private static List<String> msg;
    private static List<String> msgP;
    private static String format;

    public PlayTimeCmd() {
        usage = CommandFile.getStringListPath("Command.PlayTime.Usage");
        perm = CommandFile.getStringPath("Command.PlayTime.Permission.Use");
        msg = CommandFile.getStringListPath("Command.PlayTime.Message");
        msgP = CommandFile.getStringListPath("Command.PlayTime.MessagePlayer");
        format = CommandFile.getStringPath("Command.PlayTime.PlayTimeFormat");
        if(!NewSystem.loadedCommands.contains(this)) {
            NewSystem.loadedCommands.add(this);
            NewSystem.getInstance().getCommand("playtime").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(args.length == 0) {
                for(String key : msgP) {
                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{PlayTime}", getPlayTime(p.getUniqueId().toString())));
                }
            }else if(args.length == 1) {
                OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                if(NewSystem.hasPermission(p, perm)) {
                    for(String key : msg) {
                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{PlayTime}", getPlayTime(t.getUniqueId().toString())).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                    }
                }
            }else{
                for(String value : usage) {
                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }else{
            if(args.length == 1) {
                OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                for (String key : msg) {
                    sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{PlayTime}", getPlayTime(t.getUniqueId().toString())).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                }
            }else{
                for(String value : usage) {
                    sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }
        return false;
    }

    public static String getPlayTime(String uuid) {
        int minutes = 0;
        int hours = 0;
        int days = 0;
        if(mySQLEnabled) {
            if(NewSystem.getMySQL() != null) {
                if (mySQL.hasNext("SELECT MINUTES FROM " + SQLTables.PLAYTIME.getTableName() + " WHERE UUID='" + uuid + "'")) {
                    minutes = mySQL.getInteger("SELECT MINUTES FROM " + SQLTables.PLAYTIME.getTableName() + " WHERE UUID='" + uuid + "'");
                } else {
                    mySQL.executeUpdate("INSERT INTO " + SQLTables.PLAYTIME.getTableName() + " (UUID,MINUTES) VALUES " +
                            "('" + uuid + "'," +
                            "'" + minutes + "')");
                }
            }
        }else {
            if (SavingsFile.isPathSet("PlayTime." + uuid + ".Minutes")) {
                minutes = SavingsFile.getIntegerPath("PlayTime." + uuid + ".Minutes");
            } else {
                SavingsFile.setPath("PlayTime." + uuid + ".Minutes", 0);
            }
        }

        if(format.contains("{Hours}")) {
            hours = minutes / 60;
            minutes = (minutes - (hours * 60));
        }
        if(format.contains("{Days}")) {
            days = hours / 24;
            hours = (hours - (days * 24));
        }

        return format.replace("{Days}", String.valueOf(days))
                .replace("{Hours}", String.valueOf(hours))
                .replace("{Minutes}", String.valueOf(minutes));
    }

    public static int getMinutesPlayed(Player p) {
        int minutes = 0;
        if(mySQLEnabled) {
            if(NewSystem.getMySQL() != null) {
                if (mySQL.hasNext("SELECT MINUTES FROM " + SQLTables.PLAYTIME.getTableName() + " WHERE UUID='" + p.getUniqueId().toString() + "'")) {
                    minutes = mySQL.getInteger("SELECT MINUTES FROM " + SQLTables.PLAYTIME.getTableName() + " WHERE UUID='" + p.getUniqueId().toString() + "'");
                } else {
                    mySQL.executeUpdate("INSERT INTO " + SQLTables.PLAYTIME.getTableName() + " (UUID,MINUTES) VALUES " +
                            "('" + p.getUniqueId().toString() + "'," +
                            "'" + minutes + "')");
                }
            }
        }else {
            minutes += SavingsFile.getIntegerPath("PlayTime." + p.getUniqueId() + ".Minutes");
        }
        return minutes;
    }

    private static int seconds = 0;

    public static void startTimer() {
        seconds++;
        if(seconds >= 60) {
            for (Player all : Bukkit.getOnlinePlayers()) {
                int minutes = getMinutesPlayed(all) + 1;
                if (mySQLEnabled) {
                    if (mySQL.hasNext("SELECT MINUTES FROM " + SQLTables.PLAYTIME.getTableName() + " WHERE UUID='" + all.getUniqueId().toString() + "'")) {
                        mySQL.executeUpdate("UPDATE " + SQLTables.PLAYTIME.getTableName() + " SET MINUTES='" + minutes + "' WHERE UUID='" + all.getUniqueId().toString() + "'");
                    } else {
                        mySQL.executeUpdate("INSERT INTO " + SQLTables.PLAYTIME.getTableName() + " (UUID,MINUTES) VALUES " +
                                "('" + all.getUniqueId().toString() + "'," +
                                "'" + minutes + "')");
                    }
                } else {
                    SavingsFile.setPath("PlayTime." + all.getUniqueId() + ".Minutes", minutes);
                }

                //Update Placeholder
                PlaceholderManager phManager = new PlaceholderManager(all);
                phManager.updatePlaceholder("{PlayTime}");

                //ScoreBoard
                if (OtherFile.getBooleanPath("Other.ScoreBoard.Enabled")) {
                    new ScoreboardManager(all).updateScoreBoard();
                }

                //PlayTimeReward
                if (OtherFile.getBooleanPath("Other.PlayTimeReward.Enabled")) {
                    new PlayTimeReward(all).collect();
                }
            }
            seconds = 0;
        }
    }
}
