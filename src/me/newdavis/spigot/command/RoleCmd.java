package me.newdavis.spigot.command;

import me.newdavis.manager.NewPermManager;
import me.newdavis.spigot.file.*;
import me.newdavis.spigot.sql.MySQL;
import me.newdavis.spigot.sql.SQLTables;
import me.newdavis.spigot.util.placeholder.PlaceholderManager;
import me.newdavis.spigot.util.ScoreboardManager;
import me.newdavis.spigot.util.TabListPrefix;
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
import java.util.*;

public class RoleCmd implements CommandExecutor, TabCompleter {

    private static MySQL mySQL = NewSystem.getMySQL();
    private static boolean mySQLEnabled  = SettingsFile.getMySQLEnabled();

    private static List<String> usage;
    private static String perm;
    private static String rolePerm;
    private static List<String> message;
    private static List<String> messageP;
    private static List<String> listMessage;
    private static String formatFC;
    private static String seconds;
    private static String minutes;
    private static String hours;
    private static String days;
    private static String weeks;
    private static String months;
    private static String years;
    private static List<String> alreadyHaveTemporary;
    private static List<String> messageTTemporary;
    private static List<String> messagePTemporary;
    private static List<String> messageTemporaryExpired;

    public RoleCmd() {
        usage = CommandFile.getStringListPath("Command.Role.Usage");
        perm = CommandFile.getStringPath("Command.Role.Permission.Use");
        rolePerm = CommandFile.getStringPath("Command.Role.Permission.Use");
        message = CommandFile.getStringListPath("Command.Role.Message");
        messageP = CommandFile.getStringListPath("Command.Role.MessagePlayer");
        listMessage = CommandFile.getStringListPath("Command.Role.ListMessage");
        formatFC = CommandFile.getStringPath("Command.Role.Format");
        seconds = CommandFile.getStringPath("Command.Role.Seconds");
        minutes = CommandFile.getStringPath("Command.Role.Minutes");
        hours = CommandFile.getStringPath("Command.Role.Hours");
        days = CommandFile.getStringPath("Command.Role.Days");
        weeks = CommandFile.getStringPath("Command.Role.Weeks");
        months = CommandFile.getStringPath("Command.Role.Months");
        years = CommandFile.getStringPath("Command.Role.Years");
        alreadyHaveTemporary = CommandFile.getStringListPath("Command.Role.MessageAlreadyHaveTemporary");
        messageTTemporary = CommandFile.getStringListPath("Command.Role.MessageTemporary");
        messagePTemporary = CommandFile.getStringListPath("Command.Role.MessagePlayerTemporary");
        messageTemporaryExpired = CommandFile.getStringListPath("Command.Role.MessageTemporaryExpired");
        if(!NewSystem.loadedCommands.contains(this)) {
            NewSystem.loadedCommands.add(this);
            NewSystem.getInstance().getCommand("role").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if (args.length == 0) {
                    for(String value : usage) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                } else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("list")) {
                        Collection<String> roles = NewPermManager.getRoleList();
                        String format = "";
                        for (String role : roles) {
                            format += formatFC.replace("{Role-Suffix}", new PlaceholderManager(p).getPlaceholder("{RoleSuffix}").getValue()).replace("{Role}", role);
                        }
                        for (String msg : listMessage) {
                            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Format}", format));
                        }
                    } else {
                        for(String value : usage) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                } else if (args.length == 2) {
                    OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                    String role = args[1];
                    if (!roleExist(role).equalsIgnoreCase("")) {
                        role = roleExist(role);
                        if (NewSystem.hasPermission(p, rolePerm.replace("{Role}", role))) {
                            if (p == t) {
                                for (String msg : messageP) {
                                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Role-Suffix}", NewPermManager.getRoleSuffix(role)).replace("{Role}", role));
                                }

                                NewPermManager.setPlayerRole(p, role);
                            } else {
                                for (String msg : message) {
                                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Role-Suffix}", NewPermManager.getRoleSuffix(role)).replace("{Role}", role));
                                }

                                if(t.isOnline()) {
                                    Player tOnline = t.getPlayer();
                                    for (String msg : messageP) {
                                        tOnline.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Role-Suffix}", NewPermManager.getRoleSuffix(role)).replace("{Role}", role));
                                    }
                                }

                                NewPermManager.setPlayerRole(t, role);
                            }

                            //Update Placeholder
                            if(t.isOnline()) {
                                PlaceholderManager phManager = new PlaceholderManager(t);
                                phManager.updatePlaceholder("{Role}");
                                phManager.updatePlaceholder("{PlayerPrefix}");
                                phManager.updatePlaceholder("{PlayerSuffix}");
                                phManager.updatePlaceholder("{RolePrefix}");
                                phManager.updatePlaceholder("{RoleSuffix}");
                            }

                            if(TabListFile.getBooleanPath("TabList.Enabled")) {
                                TabListPrefix.setTabListForAll();
                            }

                            if(OtherFile.getBooleanPath("Other.ScoreBoard.Enabled")) {
                                if(t.isOnline()) {
                                    new ScoreboardManager(t.getPlayer()).updateScoreBoard();
                                }
                            }
                        } else {
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    } else {
                        p.sendMessage(SettingsFile.getArgument());
                    }
                }else if(args.length == 3){
                    OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                    String role = args[2];
                    if (!roleExist(role).equalsIgnoreCase("")) {
                        role = roleExist(role);
                        if (NewSystem.hasPermission(p, rolePerm.replace("{Role}", role))) {
                            String[] duration = getDurate(args[1]);
                            if (duration[1].equalsIgnoreCase("s") || duration[1].equalsIgnoreCase("sec")) {
                                int time = Integer.parseInt(duration[0]);
                                String wort = seconds;
                                long roleEnd = System.currentTimeMillis() + (1000L * time);
                                setTemporaryRole(p, t, role, roleEnd, time, wort);
                            } else if (duration[1].equalsIgnoreCase("m") || duration[1].equalsIgnoreCase("min")) {
                                int time = Integer.parseInt(duration[0]);
                                String wort = minutes;
                                long roleEnd = System.currentTimeMillis() + (1000L * 60 * time);
                                setTemporaryRole(p, t, role, roleEnd, time, wort);
                            } else if (duration[1].equalsIgnoreCase("h") || duration[1].equalsIgnoreCase("hour") || duration[1].equalsIgnoreCase("hours")) {
                                int time = Integer.parseInt(duration[0]);
                                String wort = hours;
                                long roleEnd = System.currentTimeMillis() + (1000L * 60 * 60 * time);
                                setTemporaryRole(p, t, role, roleEnd, time, wort);
                            } else if (duration[1].equalsIgnoreCase("d") || duration[1].equalsIgnoreCase("day") || duration[1].equalsIgnoreCase("days")) {
                                int time = Integer.parseInt(duration[0]);
                                String wort = days;
                                long roleEnd = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * time);
                                setTemporaryRole(p, t, role, roleEnd, time, wort);
                            } else if (duration[1].equalsIgnoreCase("w") || duration[1].equalsIgnoreCase("week") || duration[1].equalsIgnoreCase("weeks")) {
                                int time = Integer.parseInt(duration[0]);
                                String wort = weeks;
                                long roleEnd = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 7 * time);
                                setTemporaryRole(p, t, role, roleEnd, time, wort);
                            } else if (duration[1].equalsIgnoreCase("month") || duration[1].equalsIgnoreCase("months")) {
                                int time = Integer.parseInt(duration[0]);
                                String wort = months;
                                long roleEnd = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 7 * 30 * time);
                                setTemporaryRole(p, t, role, roleEnd, time, wort);
                            } else if (duration[1].equalsIgnoreCase("y") || duration[1].equalsIgnoreCase("year")) {
                                int time = Integer.parseInt(duration[0]);
                                String wort = years;
                                long roleEnd = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365 * time);
                                setTemporaryRole(p, t, role, roleEnd, time, wort);
                            } else {
                                for(String value : usage) {
                                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                            }
                        }else{
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    }else{
                        p.sendMessage(SettingsFile.getArgument());
                    }
                }else{
                    for(String value : usage) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }else{
                p.sendMessage(SettingsFile.getNoPerm());
            }
        }else {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("list")) {
                    Collection<String> roles = NewPermManager.getRoleList();
                    String format = "";
                    for (String role : roles) {
                        format = format + formatFC.replace("{Role-Suffix}", NewPermManager.getRoleSuffix(role)).replace("{Role}", role);
                    }
                    for (String msg : listMessage) {
                        sender.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Format}", format));
                    }
                } else {
                    for (String value : usage) {
                        sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            } else if (args.length == 2) {
                OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                String role = args[1];
                if (!roleExist(role).equalsIgnoreCase("")) {
                    role = roleExist(role);
                    for (String msg : message) {
                        sender.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Role-Suffix}", NewPermManager.getRoleSuffix(role)).replace("{Role}", role));
                    }
                    if (t.isOnline()) {
                        Player tOnline = t.getPlayer();
                        for (String msg : messageP) {
                            tOnline.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Role-Suffix}", NewPermManager.getRoleSuffix(role)).replace("{Role}", role));
                        }
                    }
                    NewPermManager.setPlayerRole(t, role);

                    //Update Placeholder
                    if(t.isOnline()) {
                        PlaceholderManager phManager = new PlaceholderManager(t);
                        phManager.updatePlaceholder("{Role}");
                        phManager.updatePlaceholder("{PlayerPrefix}");
                        phManager.updatePlaceholder("{PlayerSuffix}");
                        phManager.updatePlaceholder("{RolePrefix}");
                        phManager.updatePlaceholder("{RoleSuffix}");
                    }

                    if (TabListFile.getBooleanPath("TabList.Enabled")) {
                        TabListPrefix.setTabListForAll();
                    }

                    if (OtherFile.getBooleanPath("Other.ScoreBoard.Enabled")) {
                        if(t.isOnline()) {
                            new ScoreboardManager(t.getPlayer()).updateScoreBoard();
                        }
                    }
                }
            } else if (args.length == 3) {
                OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                String role = args[2];
                if (!roleExist(role).equalsIgnoreCase("")) {
                    role = roleExist(role);
                    String[] duration = getDurate(args[1]);
                    if (duration[1].equalsIgnoreCase("s") || duration[1].equalsIgnoreCase("sec")) {
                        int time = Integer.parseInt(duration[0]);
                        String wort = seconds;
                        long roleEnd = System.currentTimeMillis() + (1000L * time);
                        setTemporaryRole(sender, t, role, roleEnd, time, wort);
                    } else if (duration[1].equalsIgnoreCase("m") || duration[1].equalsIgnoreCase("min")) {
                        int time = Integer.parseInt(duration[0]);
                        String wort = minutes;
                        long roleEnd = System.currentTimeMillis() + (1000L * 60 * time);
                        setTemporaryRole(sender, t, role, roleEnd, time, wort);
                    } else if (duration[1].equalsIgnoreCase("h") || duration[1].equalsIgnoreCase("hour") || duration[1].equalsIgnoreCase("hours")) {
                        int time = Integer.parseInt(duration[0]);
                        String wort = hours;
                        long roleEnd = System.currentTimeMillis() + (1000L * 60 * 60 * time);
                        setTemporaryRole(sender, t, role, roleEnd, time, wort);
                    } else if (duration[1].equalsIgnoreCase("d") || duration[1].equalsIgnoreCase("day") || duration[1].equalsIgnoreCase("days")) {
                        int time = Integer.parseInt(duration[0]);
                        String wort = days;
                        long roleEnd = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * time);
                        setTemporaryRole(sender, t, role, roleEnd, time, wort);
                    } else if (duration[1].equalsIgnoreCase("w") || duration[1].equalsIgnoreCase("week") || duration[1].equalsIgnoreCase("weeks")) {
                        int time = Integer.parseInt(duration[0]);
                        String wort = weeks;
                        long roleEnd = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 7 * time);
                        setTemporaryRole(sender, t, role, roleEnd, time, wort);
                    } else if (duration[1].equalsIgnoreCase("month") || duration[1].equalsIgnoreCase("months")) {
                        int time = Integer.parseInt(duration[0]);
                        String wort = months;
                        long roleEnd = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 7 * 30 * time);
                        setTemporaryRole(sender, t, role, roleEnd, time, wort);
                    } else if (duration[1].equalsIgnoreCase("y") || duration[1].equalsIgnoreCase("year")) {
                        int time = Integer.parseInt(duration[0]);
                        String wort = years;
                        long roleEnd = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365 * time);
                        setTemporaryRole(sender, t, role, roleEnd, time, wort);
                    } else {
                        for (String value : usage) {
                            sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                } else {
                    sender.sendMessage(SettingsFile.getArgument());
                }
            } else {
                for (String value : usage) {
                    sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }
        return false;
    }

    public static String roleExist(String role) {
        for(String forRole : NewPermManager.getRoleList()) {
            if(forRole.equalsIgnoreCase(role)) {
                return forRole;
            }
        }
        return "";
    }

    public static void setTemporaryRole(Player p, OfflinePlayer t, String role, long roleEnd, int time, String wort) {
        if((mySQLEnabled && !mySQL.hasNext("SELECT UUID FROM " + SQLTables.ROLE.getTableName() + " WHERE UUID='" + t.getUniqueId().toString() + "'"))
                || (!SavingsFile.isPathSet("TemporaryRole." + t.getUniqueId() + ".RoleEnd"))) {
            if(mySQLEnabled) {
                mySQL.executeUpdate("INSERT INTO " + SQLTables.ROLE.getTableName() + " (UUID,ROLE_END,LAST_ROLE,TEMPORARY_ROLE) VALUES " +
                        "('" + t.getUniqueId() + "'," +
                        "'" + roleEnd + "'," +
                        "'" + NewPermManager.getPlayerRole(t) + "'," +
                        "'" + role + "')");
            }else {
                Collection<String> uuids;
                if (!getTemporaryRolePlayer().isEmpty()) {
                    uuids = getTemporaryRolePlayer().keySet();
                } else {
                    uuids = new ArrayList<>();
                }

                SavingsFile.setPath("TemporaryRole." + t.getUniqueId() + ".RoleEnd", roleEnd);
                SavingsFile.setPath("TemporaryRole." + t.getUniqueId() + ".LastRole", NewPermManager.getPlayerRole(t));
                SavingsFile.setPath("TemporaryRole." + t.getUniqueId() + ".TemporaryRole", role);
                if (!uuids.contains(t.getUniqueId().toString())) {
                    uuids.add(t.getUniqueId().toString());
                    SavingsFile.setPath("TemporaryRole.List", uuids);
                }
            }

            for (String msg : messageTTemporary) {
                if (t.isOnline()) {
                    Player tOnline = t.getPlayer();
                    tOnline.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Durate}", time + " " + wort).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Role-Suffix}", NewPermManager.getRoleSuffix(role)).replace("{Role}", role));
                }
            }

            if (!p.getUniqueId().toString().equalsIgnoreCase(t.getUniqueId().toString())) {
                for (String msg : messagePTemporary) {
                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Durate}", time + " " + wort).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Role-Suffix}", NewPermManager.getRoleSuffix(role)).replace("{Role}", role));
                }
            }

            //Update Placeholder
            if(t.isOnline()) {
                PlaceholderManager phManager = new PlaceholderManager(t);
                phManager.updatePlaceholder("{Role}");
                phManager.updatePlaceholder("{PlayerPrefix}");
                phManager.updatePlaceholder("{PlayerSuffix}");
            }

            NewPermManager.setPlayerRole(t, role);
            if(TabListFile.getBooleanPath("TabList.Enabled")) {
                TabListPrefix.setTabListForAll();
            }

            if(OtherFile.getBooleanPath("Other.ScoreBoard.Enabled")) {
                if(t.isOnline()) {
                    new ScoreboardManager(t.getPlayer()).updateScoreBoard();
                }
            }
        }else{
            for(String value : alreadyHaveTemporary) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void setTemporaryRole(CommandSender p, OfflinePlayer t, String role, long roleEnd, int time, String wort) {
        if((mySQLEnabled && (!mySQL.hasNext("SELECT UUID FROM " + SQLTables.ROLE.getTableName() + " WHERE UUID='" + t.getUniqueId().toString() + "'")))
                || (!SavingsFile.isPathSet("TemporaryRole." + t.getUniqueId() + ".RoleEnd"))) {
            if(mySQLEnabled) {
                mySQL.executeUpdate("INSERT INTO " + SQLTables.ROLE.getTableName() + " (UUID,ROLE_END,LAST_ROLE,TEMPORARY_ROLE) VALUES " +
                        "('" + t.getUniqueId() + "'," +
                        "'" + roleEnd + "'," +
                        "'" + NewPermManager.getPlayerRole(t) + "'," +
                        "'" + role + "')");
            }else {
                Collection<String> uuids;
                if (!getTemporaryRolePlayer().isEmpty()) {
                    uuids = getTemporaryRolePlayer().keySet();
                } else {
                    uuids = new ArrayList<>();
                }

                SavingsFile.setPath("TemporaryRole." + t.getUniqueId() + ".RoleEnd", roleEnd);
                SavingsFile.setPath("TemporaryRole." + t.getUniqueId() + ".LastRole", NewPermManager.getPlayerRole(t));
                SavingsFile.setPath("TemporaryRole." + t.getUniqueId() + ".TemporaryRole", role);
                if (!uuids.contains(t.getUniqueId().toString())) {
                    uuids.add(t.getUniqueId().toString());
                    SavingsFile.setPath("TemporaryRole.List", uuids);
                }
            }

            for (String msg : messageTTemporary) {
                if (t.isOnline()) {
                    Player tOnline = t.getPlayer();
                    tOnline.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Durate}", time + " " + wort).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Role-Suffix}", NewPermManager.getRoleSuffix(role)).replace("{Role}", role));
                }
            }

            for (String msg : messagePTemporary) {
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Durate}", time + " " + wort).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Role-Suffix}", NewPermManager.getRoleSuffix(role)).replace("{Role}", role));
            }
            NewPermManager.setPlayerRole(t, role);

            //Update Placeholder
            if(t.isOnline()) {
                PlaceholderManager phManager = new PlaceholderManager(t);
                phManager.updatePlaceholder("{Role}");
                phManager.updatePlaceholder("{PlayerPrefix}");
                phManager.updatePlaceholder("{PlayerSuffix}");
            }

            if(TabListFile.getBooleanPath("TabList.Enabled")) {
                TabListPrefix.setTabListForAll();
            }

            if(OtherFile.getBooleanPath("Other.ScoreBoard.Enabled")) {
                if(t.isOnline()) {
                    new ScoreboardManager(t.getPlayer()).updateScoreBoard();
                }
            }
        }else{
            for(String value : alreadyHaveTemporary) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void temporaryRoleTimer() {
        Collection<String> uuids;
        if (!getTemporaryRolePlayer().isEmpty()) {
            uuids = getTemporaryRolePlayer().keySet();
        } else {
            uuids = new ArrayList<>();
        }
        for (String uuid : uuids) {
            long roleEnd = 0L;
            String latestRole = "";
            String temporaryRole = "";
            if (mySQLEnabled) {
                try {
                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.ROLE.getTableName() + " WHERE UUID='" + uuid + "'");

                    if (rs.next()) {
                        roleEnd = Long.parseLong(rs.getString("ROLE_END"));
                        latestRole = rs.getString("LAST_ROLE");
                        temporaryRole = rs.getString("TEMPORARY_ROLE");
                    }
                    mySQL.disconnect();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                roleEnd = SavingsFile.getLongPath("TemporaryRole." + uuid + ".RoleEnd");
                latestRole = SavingsFile.getStringPath("TemporaryRole." + uuid + ".LastRole");
                temporaryRole = SavingsFile.getStringPath("TemporaryRole." + uuid + ".TemporaryRole");
            }
            if ((roleEnd - System.currentTimeMillis()) < 0) {
                Collection<String> roles = NewPermManager.getRoleList();
                OfflinePlayer t = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                if (roles.contains(latestRole)) {
                    NewPermManager.setPlayerRole(t, latestRole);
                } else {
                    NewPermManager.setPlayerRole(t, NewPermManager.getDefaultRole());
                }

                for (String msg : messageTemporaryExpired) {
                    if (t.isOnline()) {
                        Player tOnline = t.getPlayer();
                        tOnline.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(tOnline, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"))).replace("{Role-Suffix}", NewPermManager.getRoleSuffix(latestRole)).replace("{Role}", latestRole).replace("{Latest-Role-Suffix}", NewPermManager.getRoleSuffix(temporaryRole)).replace("{Latest-Role}", temporaryRole));

                        //Update Placeholder
                        if(t.isOnline()) {
                            PlaceholderManager phManager = new PlaceholderManager(tOnline);
                            phManager.updatePlaceholder("{Role}");
                            phManager.updatePlaceholder("{PlayerPrefix}");
                            phManager.updatePlaceholder("{PlayerSuffix}");
                        }

                        if (OtherFile.getBooleanPath("Other.ScoreBoard.Enabled")) {
                            new ScoreboardManager(tOnline).updateScoreBoard();
                        }
                    }

                }

                if (TabListFile.getBooleanPath("TabList.Enabled")) {
                    TabListPrefix.setTabListForAll();
                }

                if (mySQLEnabled) {
                    mySQL.executeUpdate("DELETE FROM " + SQLTables.ROLE.getTableName() + " WHERE UUID='" + uuid + "'");
                } else {
                    uuids.remove(uuid);
                    SavingsFile.setPath("TemporaryRole." + uuid + ".RoleEnd", null);
                    SavingsFile.setPath("TemporaryRole." + uuid + ".LastRole", null);
                    SavingsFile.setPath("TemporaryRole." + uuid + ".TemporaryRole", null);
                    SavingsFile.setPath("TemporaryRole.List", uuids);
                }
            }
        }
    }

    public static HashMap<String, Long> getTemporaryRolePlayer() {
        HashMap<String, Long> player = new HashMap<>();

        if(mySQLEnabled) {
            try {
                ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.ROLE.getTableName());

                while(rs.next()) {
                    String uuid = rs.getString("UUID");
                    OfflinePlayer t = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                    if (t.isOnline()) {
                        long roleEnd = Long.parseLong(rs.getString("ROLE_END"));
                        player.put(uuid, roleEnd);
                    }
                }
                mySQL.disconnect();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else {
            for (String uuid : SavingsFile.getStringListPath("TemporaryRole.List")) {
                OfflinePlayer t = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                if (t.isOnline()) {
                    long roleEnd = SavingsFile.getLongPath("TemporaryRole." + uuid + ".RoleEnd");
                    player.put(uuid, roleEnd);
                }
            }
        }
        return player;
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        List<String> tabCompletions = new ArrayList<>();

        if(sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 1) {
                if(NewSystem.hasPermission(p, perm)) {
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
            }else if(args.length == 2) {
                if(!(args[0].equalsIgnoreCase("list"))) {
                    for(String role : NewPermManager.getRoleList()) {
                        if(role.contains(args[1])) {
                            if (NewSystem.hasPermission(p, rolePerm.replace("{Role}", role))) {
                                tabCompletions.add(role);
                            }
                        }
                    }
                }
            } else if(args.length == 3) {
                if(!(args[0].equalsIgnoreCase("list"))) {
                    for(String role : NewPermManager.getRoleList()) {
                        if(role.contains(args[2])) {
                            if (NewSystem.hasPermission(p, rolePerm.replace("{Role}", role))) {
                                tabCompletions.add(role);
                            }
                        }
                    }
                }
            }
        }else {
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
            } else if (args.length == 2) {
                if (!(args[0].equalsIgnoreCase("list"))) {
                    for (String role : NewPermManager.getRoleList()) {
                        if (role.contains(args[1])) {
                            tabCompletions.add(role);
                        }
                    }
                }
            } else if (args.length == 3) {
                if (!(args[0].equalsIgnoreCase("list"))) {
                    for (String role : NewPermManager.getRoleList()) {
                        if (role.contains(args[2])) {
                            tabCompletions.add(role);
                        }
                    }
                }
            }
        }

        return tabCompletions;
    }
}
