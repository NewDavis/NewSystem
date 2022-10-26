package me.newdavis.spigot.sql;

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.OtherFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQL {

    private Connection connection;
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    public MySQL() {
        host = SettingsFile.getMySQLHost();
        port = SettingsFile.getMySQLPort();
        database = SettingsFile.getMySQLDatabase();
        username = SettingsFile.getMySQLUser();
        password = SettingsFile.getMySQLPassword();
    }

    public boolean connect() {
        try {
            if(connection != null && !connection.isClosed()) {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=" + SettingsFile.getMySQLUseSSL(), username, password);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean executeUpdate(String sql) {
        MySQL mySQL = NewSystem.getMySQL();

        try {
            PreparedStatement ps = mySQL.getConnection().prepareStatement(sql);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet executeQuery(String sql) {
        MySQL mySQL = NewSystem.getMySQL();

        try {
            PreparedStatement ps = mySQL.getConnection().prepareStatement(sql);
            return ps.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createTables() {
        MySQL mySQL = NewSystem.getMySQL();
        PreparedStatement ps;
        String sql;
        try{
            //Currency
            if(CommandFile.getBooleanPath("Command.Currency.Enabled.Currency")) {
                sql = "CREATE TABLE IF NOT EXISTS currency (UUID VARCHAR(40)," +
                        "AMOUNT DOUBLE(40,2))";
                ps = mySQL.getConnection().prepareStatement(sql);
                ps.executeUpdate();
            }

            //Ban && BannedPlayers
            if(CommandFile.getBooleanPath("Command.Ban.Enabled")) {
                sql = "CREATE TABLE IF NOT EXISTS ban (UUID VARCHAR(40)," +
                        "PUNISHMENT_COUNT INT(10)," +
                        "UUID_BANNED_OF VARCHAR(40)," +
                        "UUID_UNBAN_OF VARCHAR(40)," +
                        "REASON VARCHAR(100)," +
                        "DURATE VARCHAR(40)," +
                        "DATE_OF_BAN VARCHAR(40)," +
                        "DATE_OF_BAN_ENDS VARCHAR(40))";
                ps = mySQL.getConnection().prepareStatement(sql);
                ps.executeUpdate();

                sql = "CREATE TABLE IF NOT EXISTS bannedplayers (UUID VARCHAR(40))";
                ps = mySQL.getConnection().prepareStatement(sql);
                ps.executeUpdate();
            }

            //BanIP && BannedIPs
            if(CommandFile.getBooleanPath("Command.BanIP.Enabled")) {
                sql = "CREATE TABLE IF NOT EXISTS banip (IP VARCHAR(40)," +
                        "PUNISHMENT_COUNT INT(10)," +
                        "UUID_BANNED_OF VARCHAR(40)," +
                        "UUID_UNBAN_OF VARCHAR(40)," +
                        "REASON VARCHAR(100)," +
                        "DURATE VARCHAR(40)," +
                        "DATE_OF_BAN VARCHAR(40)," +
                        "DATE_OF_BAN_ENDS VARCHAR(40))";
                ps = mySQL.getConnection().prepareStatement(sql);
                ps.executeUpdate();

                sql = "CREATE TABLE IF NOT EXISTS bannedips (IP VARCHAR(40))";
                ps = mySQL.getConnection().prepareStatement(sql);
                ps.executeUpdate();
            }

            //IP
            if(SettingsFile.getSaveIPs()) {
                sql = "CREATE TABLE IF NOT EXISTS ip_storage (IP VARCHAR(40)," +
                        "UUID VARCHAR(40))";
                ps = mySQL.getConnection().prepareStatement(sql);
                ps.executeUpdate();
            }

            //Maintenance
            if(CommandFile.getBooleanPath("Command.Maintenance.Enabled")) {
                sql = "CREATE TABLE IF NOT EXISTS maintenance_player (UUID VARCHAR(40)," +
                        "UUID_ADDED_OF VARCHAR(40)," +
                        "ADDED_DATE VARCHAR(40))";
                ps = mySQL.getConnection().prepareStatement(sql);
                ps.executeUpdate();

                if (NewSystem.newPerm) {
                    sql = "CREATE TABLE IF NOT EXISTS maintenance_role (ROLE VARCHAR(40)," +
                            "UUID_ADDED_OF VARCHAR(40)," +
                            "ADDED_DATE VARCHAR(40))";
                    ps = mySQL.getConnection().prepareStatement(sql);
                    ps.executeUpdate();
                }

                sql = "CREATE TABLE IF NOT EXISTS maintenance (STATUS INT(1))";
                ps = mySQL.getConnection().prepareStatement(sql);
                ps.executeUpdate();
            }

            //Mute && MutedPlayers
            if(CommandFile.getBooleanPath("Command.Mute.Enabled")) {
                sql = "CREATE TABLE IF NOT EXISTS mute (UUID VARCHAR(40)," +
                        "PUNISHMENT_COUNT INT(10)," +
                        "UUID_MUTED_OF VARCHAR(40)," +
                        "UUID_UNMUTE_OF VARCHAR(40)," +
                        "REASON VARCHAR(100)," +
                        "DURATE VARCHAR(40)," +
                        "DATE_OF_MUTE VARCHAR(40)," +
                        "DATE_OF_MUTE_ENDS VARCHAR(40))";
                ps = mySQL.getConnection().prepareStatement(sql);
                ps.executeUpdate();

                sql = "CREATE TABLE IF NOT EXISTS mutedplayers (UUID VARCHAR(40))";
                ps = mySQL.getConnection().prepareStatement(sql);
                ps.executeUpdate();
            }

            if(CommandFile.getBooleanPath("Command.Kick.Enabled")) {
                //Kick
                sql = "CREATE TABLE IF NOT EXISTS kick (UUID VARCHAR(40)," +
                        "PUNISHMENT_COUNT INT(10)," +
                        "UUID_KICK_OF VARCHAR(40)," +
                        "REASON VARCHAR(40)," +
                        "DATE_OF_KICK VARCHAR(40))";
                ps = mySQL.getConnection().prepareStatement(sql);
                ps.executeUpdate();
            }

            if(CommandFile.getBooleanPath("Command.MuteIP.Enabled")) {
                //MuteIP && MutedIPs
                sql = "CREATE TABLE IF NOT EXISTS muteip (IP VARCHAR(40)," +
                        "PUNISHMENT_COUNT INT(10)," +
                        "UUID_MUTED_OF VARCHAR(40)," +
                        "UUID_UNMUTE_OF VARCHAR(40)," +
                        "REASON VARCHAR(100)," +
                        "DURATE VARCHAR(40)," +
                        "DATE_OF_MUTE VARCHAR(40)," +
                        "DATE_OF_MUTE_ENDS VARCHAR(40))";
                ps = mySQL.getConnection().prepareStatement(sql);
                ps.executeUpdate();

                sql = "CREATE TABLE IF NOT EXISTS mutedips (IP VARCHAR(40))";
                ps = mySQL.getConnection().prepareStatement(sql);
                ps.executeUpdate();
            }

            if(CommandFile.getBooleanPath("Command.PlayTime.Enabled")) {
                //PlayTime
                sql = "CREATE TABLE IF NOT EXISTS playtime (UUID VARCHAR(40)," +
                        "MINUTES INT(20))";
                ps = mySQL.getConnection().prepareStatement(sql);
                ps.executeUpdate();

                if(OtherFile.getBooleanPath("Other.PlayTimeReward.Enabled")) {
                    //PlayTimeRewards
                    sql = "CREATE TABLE IF NOT EXISTS playtimereward (MINUTES INT(20), UUID VARCHAR(40))";
                    ps = mySQL.getConnection().prepareStatement(sql);
                    ps.executeUpdate();
                }
            }

            if(NewSystem.newPerm && CommandFile.getBooleanPath("Command.Role.Enabled")) {
                //Role
                sql = "CREATE TABLE IF NOT EXISTS role (UUID VARCHAR(40)," +
                        "ROLE_END VARCHAR(40)," +
                        "LAST_ROLE VARCHAR(100)," +
                        "TEMPORARY_ROLE VARCHAR(100))";
                ps = mySQL.getConnection().prepareStatement(sql);
                ps.executeUpdate();
            }

            //Stats
            sql = "CREATE TABLE IF NOT EXISTS stats (UUID VARCHAR(40)," +
                    "KILLS INT(40)," +
                    "DEATHS INT(40))";
            ps = mySQL.getConnection().prepareStatement(sql);
            ps.executeUpdate();

            if(CommandFile.getBooleanPath("Command.Warn.Enabled")) {
                //Warn
                sql = "CREATE TABLE IF NOT EXISTS warn (UUID VARCHAR(40)," +
                        "PUNISHMENT_COUNT INT(10)," +
                        "UUID_WARN_OF VARCHAR(40)," +
                        "REASON VARCHAR(100)," +
                        "DATE_OF_WARN VARCHAR(40))";
                ps = mySQL.getConnection().prepareStatement(sql);
                ps.executeUpdate();
            }

            disconnect();
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(SettingsFile.getPrefix() + " §cAn error occurred while creating the MySQL tables!");
        }
    }

    public boolean hasNext(String sql) {
        MySQL mySQL = NewSystem.getMySQL();

        boolean result = false;
        ResultSet rs = mySQL.executeQuery(sql);
        try {
            result = rs.next();
            disconnect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public String getString(String sql) {
        MySQL mySQL = NewSystem.getMySQL();
        ResultSet rs = mySQL.executeQuery(sql);

        String result = "";
        try {
            if (rs.next()) {
                result = rs.getString(1);
                disconnect();
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public int getInteger(String sql) {
        MySQL mySQL = NewSystem.getMySQL();
        ResultSet rs = mySQL.executeQuery(sql);

        int result = 0;
        try {
            if (rs.next()) {
                result = rs.getInt(1);
                disconnect();
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public double getDouble(String sql) {
        MySQL mySQL = NewSystem.getMySQL();
        ResultSet rs = mySQL.executeQuery(sql);

        double result = 0D;
        try {
            if (rs.next()) {
                result = rs.getDouble(1);
                disconnect();
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean getBoolean(String sql) {
        return getInteger(sql) == 1;
    }

    public List<String> getStringList(String column, String table) {
        MySQL mySQL = NewSystem.getMySQL();
        List<String> stringList = new ArrayList<>();

        ResultSet rs = mySQL.executeQuery("SELECT " + column + " FROM " + table);

        try {
            while (rs.next()) {
                stringList.add(rs.getString(1));
            }
            disconnect();
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return stringList;
    }

    public Connection getConnection() {
        connect();
        return connection;
    }
}
