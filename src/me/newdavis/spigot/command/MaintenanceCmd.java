package me.newdavis.spigot.command;

import me.newdavis.manager.NewPermManager;
import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.sql.MySQL;
import me.newdavis.spigot.sql.SQLTables;
import me.newdavis.spigot.util.placeholder.PlaceholderManager;
import net.md_5.bungee.api.chat.*;
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
import java.util.UUID;

public class MaintenanceCmd implements CommandExecutor, TabCompleter {

    private static MySQL mySQL = NewSystem.getMySQL();
    private static boolean mySQLEnabled  = SettingsFile.getMySQLEnabled();

    private static List<String> usage;
    private static String perm;
    private static String permPlayer;
    private static String permStatus;
    private static String permRole;
    private static List<String> enableMaintenance;
    private static List<String> alreadyEnabledMaintenance;
    private static List<String> disableMaintenance;
    private static List<String> alreadyDisabledMaintenance;
    private static List<String> addPlayer;
    private static List<String> alreadyAddedPlayer;
    private static List<String> removePlayer;
    private static List<String> alreadyRemovedPlayer;
    private static List<String> addRole;
    private static List<String> alreadyAddedRole;
    private static List<String> removeRole;
    private static List<String> alreadyRemovedRole;
    private static String kickMessage;
    private static List<String> messageListPlayer;
    private static String msgNoPlayersAdded;
    private static String listPlayerFormat;
    private static String listPlayerHover;
    private static List<String> messageListRole;
    private static String msgNoRolesAdded;
    private static String listRoleFormat;
    private static String listRoleHover;
    private static String listPlayerConsoleMessage;
    private static String listRoleConsoleMessage;
    public static String maintenanceMOTD;

    public void init() {
        usage = CommandFile.getStringListPath("Command.Maintenance.Usage");
        perm = CommandFile.getStringPath("Command.Maintenance.Permission.Use");
        permPlayer = CommandFile.getStringPath("Command.Maintenance.Permission.Player");
        permStatus = CommandFile.getStringPath("Command.Maintenance.Permission.Status");
        permRole = CommandFile.getStringPath("Command.Maintenance.Permission.Role");
        enableMaintenance = CommandFile.getStringListPath("Command.Maintenance.MessageEnableMaintenance");
        alreadyEnabledMaintenance = CommandFile.getStringListPath("Command.Maintenance.MessageMaintenanceAlreadyEnabled");
        disableMaintenance = CommandFile.getStringListPath("Command.Maintenance.MessageDisableMaintenance");
        alreadyDisabledMaintenance = CommandFile.getStringListPath("Command.Maintenance.MessageMaintenanceAlreadyDisabled");
        addPlayer = CommandFile.getStringListPath("Command.Maintenance.MessageAddPlayer");
        alreadyAddedPlayer = CommandFile.getStringListPath("Command.Maintenance.MessagePlayerAlreadyAdded");
        removePlayer = CommandFile.getStringListPath("Command.Maintenance.MessageRemovePlayer");
        alreadyRemovedPlayer = CommandFile.getStringListPath("Command.Maintenance.MessagePlayerAlreadyRemoved");
        addRole = CommandFile.getStringListPath("Command.Maintenance.MessageAddRole");
        alreadyAddedRole = CommandFile.getStringListPath("Command.Maintenance.MessageRoleAlreadyAdded");
        removeRole = CommandFile.getStringListPath("Command.Maintenance.MessageRemoveRole");
        alreadyRemovedRole = CommandFile.getStringListPath("Command.Maintenance.MessageRoleAlreadyRemoved");
        kickMessage = CommandFile.getStringPath("Command.Maintenance.MessageNotAdded").replace("{Prefix}", SettingsFile.getPrefix()).replace("||", "\n");
        messageListPlayer = CommandFile.getStringListPath("Command.Maintenance.MessageListPlayer");
        msgNoPlayersAdded = CommandFile.getStringPath("Command.Maintenance.MessageNoPlayersAdded").replace("{Prefix}", SettingsFile.getPrefix());
        listPlayerFormat = CommandFile.getStringPath("Command.Maintenance.ListPlayerFormat");
        listPlayerHover = CommandFile.getStringPath("Command.Maintenance.ListPlayerHover");
        messageListRole = CommandFile.getStringListPath("Command.Maintenance.MessageListRoles");
        msgNoRolesAdded = CommandFile.getStringPath("Command.Maintenance.MessageNoRolesAdded").replace("{Prefix}", SettingsFile.getPrefix());
        listRoleFormat = CommandFile.getStringPath("Command.Maintenance.ListRoleFormat");
        listRoleHover = CommandFile.getStringPath("Command.Maintenance.ListRoleHover");
        listPlayerConsoleMessage = CommandFile.getStringPath("Command.Maintenance.ListPlayerConsole");
        listRoleConsoleMessage = CommandFile.getStringPath("Command.Maintenance.ListRoleConsole");
        maintenanceMOTD = CommandFile.getStringPath("Command.Maintenance.MOTD").replace("{Prefix}", SettingsFile.getPrefix());
        getAddedPlayers();
        getAddedRoles();
        getStatus();
        NewSystem.getInstance().getCommand("maintenance").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if (NewSystem.hasPermission(p, perm)) {
                if (args.length == 0) {
                    for(String value : usage) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }else if(args.length == 1) {
                    if(args[0].equalsIgnoreCase("list")) {
                        if(NewSystem.hasPermission(p, permPlayer)) {
                            sendList(p);
                        }else{
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    }else if(args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("an")) {
                        if(NewSystem.hasPermission(p, permStatus)) {
                            if (startMaintenance()) {
                                for(String value : enableMaintenance) {
                                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                            } else {
                                for(String value : alreadyEnabledMaintenance) {
                                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                            }
                        }else{
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    }else if(args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("aus")) {
                        if (NewSystem.hasPermission(p, permStatus)) {
                            if (stopMaintenance()) {
                                for(String value : disableMaintenance) {
                                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                            } else {
                                for(String value : alreadyDisabledMaintenance) {
                                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                            }
                        } else {
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    }else{
                        for(String value : usage) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                }else if(args.length == 2) {
                    if(args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("hinzufügen")) {
                        if (NewSystem.hasPermission(p, permPlayer)) {
                            if(NewSystem.newPerm) {
                                if(NewSystem.hasPermission(p, permRole)) {
                                    String role = args[1];
                                    for (String npRole : NewPermManager.getRoleList()) {
                                        if (role.equalsIgnoreCase(npRole)) {
                                            String suffix = NewPermManager.getRoleSuffix(role);
                                            if (addRole(p, role)) {
                                                for (String key : addRole) {
                                                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Suffix}", suffix).replace("{Role}", role));
                                                }
                                            } else {
                                                for (String key : alreadyAddedRole) {
                                                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Suffix}", suffix).replace("{Role}", role));
                                                }
                                            }
                                            return true;
                                        }
                                    }
                                }
                            }
                            OfflinePlayer t = Bukkit.getOfflinePlayer(args[1]);
                            if (addPlayer(p, t)) {
                                for(String key : addPlayer) {
                                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)));
                                }
                            } else {
                                for(String key : alreadyAddedPlayer) {
                                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)));
                                }
                            }
                        } else {
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    }else if(args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("entfernen")) {
                        if (NewSystem.hasPermission(p, permPlayer)) {
                            if(NewSystem.newPerm) {
                                if(NewSystem.hasPermission(p, permRole)) {
                                    String role = args[1];
                                    for (String npRole : NewPermManager.getRoleList()) {
                                        if (role.equalsIgnoreCase(npRole)) {
                                            String suffix = NewPermManager.getRoleSuffix(role);
                                            if (removeRole(role)) {
                                                for (String key : removeRole) {
                                                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Suffix}", suffix).replace("{Role}", role));
                                                }
                                            } else {
                                                for (String key : alreadyRemovedRole) {
                                                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Suffix}", suffix).replace("{Role}", role));
                                                }
                                            }
                                            return true;
                                        }
                                    }
                                }
                            }
                            OfflinePlayer t = Bukkit.getOfflinePlayer(args[1]);
                            if (removePlayer(t)) {
                                for(String key : removePlayer) {
                                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)));
                                }
                            } else {
                                for(String key : alreadyRemovedPlayer) {
                                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)));
                                }
                            }
                        } else {
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    }else{
                        for(String value : usage) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
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
            if (args.length == 0) {
                for(String value : usage) {
                    sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }else if(args.length == 1) {
                if(args[0].equalsIgnoreCase("list")) {
                    sendList(sender);
                }else if(args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("an")) {
                    if (startMaintenance()) {
                        for (String value : enableMaintenance) {
                            sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    } else {
                        for (String value : alreadyEnabledMaintenance) {
                            sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                }else if(args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("aus")) {
                    if (stopMaintenance()) {
                        for (String value : disableMaintenance) {
                            sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    } else {
                        for (String value : alreadyDisabledMaintenance) {
                            sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                }else{
                    for(String value : usage) {
                        sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }else if(args.length == 2) {
                if(args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("hinzufügen")) {
                    if(NewSystem.newPerm) {
                        String role = args[1];
                        for(String npRole : NewPermManager.getRoleList()) {
                            if(role.equalsIgnoreCase(npRole)) {
                                String suffix = NewPermManager.getRoleSuffix(role);
                                if (addRole(role)) {
                                    for(String key : addRole) {
                                        sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Suffix}", suffix).replace("{Role}", role));
                                    }
                                } else {
                                    for(String key : alreadyAddedRole) {
                                        sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Suffix}", suffix).replace("{Role}", role));
                                    }
                                }
                                return true;
                            }
                        }
                    }
                    OfflinePlayer t = Bukkit.getOfflinePlayer(args[1]);
                    if (addPlayer(t)) {
                        for (String key : addPlayer) {
                            sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)));
                        }
                    } else {
                        for (String key : alreadyAddedPlayer) {
                            sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)));
                        }
                    }
                }else if(args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("entfernen")) {
                    if(NewSystem.newPerm) {
                        String role = args[1];
                        for(String npRole : NewPermManager.getRoleList()) {
                            if(role.equalsIgnoreCase(npRole)) {
                                String suffix = NewPermManager.getRoleSuffix(role);
                                if (removeRole(role)) {
                                    for(String key : removeRole) {
                                        sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Suffix}", suffix).replace("{Role}", role));
                                    }
                                } else {
                                    for(String key : alreadyRemovedRole) {
                                        sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Suffix}", suffix).replace("{Role}", role));
                                    }
                                }
                                return true;
                            }
                        }
                    }
                    OfflinePlayer t = Bukkit.getOfflinePlayer(args[1]);
                    if (removePlayer(t)) {
                        for (String key : removePlayer) {
                            sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)));
                        }
                    } else {
                        for (String key : alreadyRemovedPlayer) {
                            sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)));
                        }
                    }
                }else{
                    for(String value : usage) {
                        sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }else{
                for(String value : usage) {
                    sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }
        return false;
    }

    public static boolean addPlayer(Player p, OfflinePlayer t) {
        if (!isPlayerAdded(t)) {
            addedPlayers.add(t.getUniqueId().toString());
            if (mySQLEnabled) {
                mySQL.executeUpdate("INSERT INTO " + SQLTables.MAINTENANCE_PLAYER.getTableName() + " (UUID,UUID_ADDED_OF,ADDED_DATE) VALUES " +
                        "('" + t.getUniqueId().toString() + "'," +
                        "'" + p.getUniqueId().toString() + "'," +
                        "'" + SettingsFile.DateFormat(System.currentTimeMillis()) + "')");
            } else {
                SavingsFile.setPath("Maintenance.Added", addedPlayers);
                SavingsFile.setPath("Maintenance.Info." + t.getUniqueId() + ".AddedOf", p.getUniqueId().toString());
                SavingsFile.setPath("Maintenance.Info." + t.getUniqueId() + ".AddedDate", SettingsFile.DateFormat(System.currentTimeMillis()));
            }
            return true;
        }
        return false;
    }

    public static boolean addPlayer(OfflinePlayer t) {
        if (!isPlayerAdded(t)) {
            addedPlayers.add(t.getUniqueId().toString());
            if (mySQLEnabled) {
                mySQL.executeUpdate("INSERT INTO " + SQLTables.MAINTENANCE_PLAYER.getTableName() + " (UUID,UUID_ADDED_OF,ADDED_DATE) VALUES " +
                        "('" + t.getUniqueId().toString() + "'," +
                        "'Console'," +
                        "'" + SettingsFile.DateFormat(System.currentTimeMillis()) + "')");
            } else {
                SavingsFile.setPath("Maintenance.Added", addedPlayers);
                SavingsFile.setPath("Maintenance.Info." + t.getUniqueId() + ".AddedOf", "Console");
                SavingsFile.setPath("Maintenance.Info." + t.getUniqueId() + ".AddedDate", SettingsFile.DateFormat(System.currentTimeMillis()));
            }
            return true;
        }
        return false;
    }

    public static boolean removePlayer(OfflinePlayer t) {
        if(isPlayerAdded(t)) {
            addedPlayers.remove(t.getUniqueId().toString());
            if (mySQLEnabled) {
                mySQL.executeUpdate("DELETE FROM " + SQLTables.MAINTENANCE_PLAYER.getTableName() + " WHERE UUID='" + t.getUniqueId().toString() + "'");
            } else {
                SavingsFile.setPath("Maintenance.Added", addedPlayers);
                SavingsFile.setPath("Maintenance.Info." + t.getUniqueId(), null);
            }
            return true;
        }
        return false;
    }

    public static boolean addRole(Player p, String role) {
        if (!isRoleAdded(role)) {
            addedRoles.add(role);
            if (mySQLEnabled) {
                mySQL.executeUpdate("INSERT INTO " + SQLTables.MAINTENANCE_ROLE.getTableName() + " (ROLE,UUID_ADDED_OF,ADDED_DATE) VALUES " +
                        "('" + role + "'," +
                        "'" + p.getUniqueId().toString() + "'," +
                        "'" + SettingsFile.DateFormat(System.currentTimeMillis()) + "')");
            } else {
                SavingsFile.setPath("Maintenance.AddedRoles", addedRoles);
                SavingsFile.setPath("Maintenance.Info." + role + ".AddedOf", p.getUniqueId().toString());
                SavingsFile.setPath("Maintenance.Info." + role + ".AddedDate", SettingsFile.DateFormat(System.currentTimeMillis()));
            }
            return true;
        }
        return false;
    }

    public static boolean addRole(String role) {
        if (!isRoleAdded(role)) {
            addedRoles.add(role);
            if (mySQLEnabled) {
                mySQL.executeUpdate("INSERT INTO " + SQLTables.MAINTENANCE_ROLE.getTableName() + " (ROLE,UUID_ADDED_OF,ADDED_DATE) VALUES " +
                        "('" + role + "'," +
                        "'Console'," +
                        "'" + SettingsFile.DateFormat(System.currentTimeMillis()) + "')");
            } else {
                SavingsFile.setPath("Maintenance.AddedRoles", addedRoles);
                SavingsFile.setPath("Maintenance.Info." + role + ".AddedOf", "Console");
                SavingsFile.setPath("Maintenance.Info." + role + ".AddedDate", SettingsFile.DateFormat(System.currentTimeMillis()));
            }
            return true;
        }
        return false;
    }

    public static boolean removeRole(String role) {
        if (isRoleAdded(role)) {
            addedRoles.remove(role);
            if (mySQLEnabled) {
                mySQL.executeUpdate("DELETE FROM " + SQLTables.MAINTENANCE_ROLE.getTableName() + " WHERE ROLE='" + role + "'");
            } else {
                SavingsFile.setPath("Maintenance.AddedRoles", addedRoles);
                SavingsFile.setPath("Maintenance.Info." + role, null);
            }
            return true;
        }
        return false;
    }

    public static boolean startMaintenance() {
        boolean kickAll = false;
        if(mySQLEnabled) {
            if (!status) {
                mySQL.executeUpdate("UPDATE " + SQLTables.MAINTENANCE.getTableName() + " SET STATUS='" + 1 + "'");
                kickAll = true;
            }
        }else {
            if(!status) {
                SavingsFile.setPath("Maintenance.Status", true);
                kickAll = true;
            }
        }
        if(kickAll) {
            for(Player all : Bukkit.getOnlinePlayers()) {
                if (!(MaintenanceCmd.isPlayerAdded(all) || NewSystem.newPerm && MaintenanceCmd.isRoleAdded(new PlaceholderManager(all).getPlaceholder("{Role}").getValue()))) {
                    all.kickPlayer(kickMessage);
                }
            }
            status = true;
            return true;
        }
        return false;
    }

    public static boolean stopMaintenance() {
        if(mySQLEnabled) {
            if (status) {
                mySQL.executeUpdate("UPDATE " + SQLTables.MAINTENANCE.getTableName() + " SET STATUS='" + 0 + "'");
                status = false;
                return true;
            }
        }else {
            if(status) {
                SavingsFile.setPath("Maintenance.Status", false);
                status = false;
                return true;
            }
        }
        return false;
    }

    public static void sendList(Player p) {
        for(String msg : messageListPlayer) {
            if(msg.contains("{AddedPlayer}")) {
                if(addedPlayers.isEmpty()) {
                    p.sendMessage(msg.replace("{AddedPlayer}", msgNoPlayersAdded));
                }else {
                    for (String addedPlayer : addedPlayers) {
                        OfflinePlayer t = Bukkit.getOfflinePlayer(UUID.fromString(addedPlayer));
                        String prefix = NewSystem.getName(t);
                        String addedDate = "";
                        String prefixAddedPlayer = "";
                        if(mySQLEnabled){
                            try {
                                ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.MAINTENANCE_PLAYER.getTableName() + " WHERE UUID='" + addedPlayer + "'");

                                if(rs.next()) {
                                    addedDate = rs.getString("ADDED_DATE");
                                    if (!rs.getString("UUID_ADDED_OF").equalsIgnoreCase("Console")) {
                                        OfflinePlayer addedOfPlayer = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_ADDED_OF")));
                                        prefixAddedPlayer = NewSystem.getName(addedOfPlayer);
                                    } else {
                                        prefixAddedPlayer = SettingsFile.getConsolePrefix();
                                    }
                                }
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }else {
                            addedDate = SavingsFile.getStringPath("Maintenance.Info." + addedPlayer + ".AddedDate");
                            if (!SavingsFile.getStringPath("Maintenance.Info." + addedPlayer + ".AddedOf").equalsIgnoreCase("Console")) {
                                OfflinePlayer addedOfPlayer = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Maintenance.Info." + addedPlayer + ".AddedOf")));
                                prefixAddedPlayer = NewSystem.getName(addedOfPlayer);
                            } else {
                                prefixAddedPlayer = SettingsFile.getConsolePrefix();
                            }
                        }

                        TextComponent component = new TextComponent(listPlayerFormat.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)));
                        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(listPlayerHover
                                .replace("{Prefix}", SettingsFile.getPrefix())
                                .replace("{Player}", prefix)
                                .replace("{AddedOf}", prefixAddedPlayer)
                                .replace("{AddedDate}", addedDate)).create()));
                        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/wartung remove " + t.getName()));

                        p.spigot().sendMessage(component);
                    }
                }
            }else{
                int addedPlayerCount = addedPlayers.size();
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Count}", String.valueOf(addedPlayerCount)));
            }
        }

        if(NewSystem.newPerm) {
            for (String msg : messageListRole) {
                if (msg.contains("{AddedRoles}")) {
                    if (addedRoles.isEmpty()) {
                        p.sendMessage(msg.replace("{AddedRoles}", msgNoRolesAdded));
                    } else {
                        for (String role : addedRoles) {
                            String suffix = NewPermManager.getRoleSuffix(role);
                            String addedDate = "";
                            String prefixAddedPlayer = "";
                            if(mySQLEnabled){
                                try {
                                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.MAINTENANCE_ROLE.getTableName() + " WHERE ROLE='" + role + "'");

                                    if(rs.next()) {
                                        addedDate = rs.getString("ADDED_DATE");
                                        if (!rs.getString("UUID_ADDED_OF").equalsIgnoreCase("Console")) {
                                            OfflinePlayer addedOfPlayer = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_ADDED_OF")));
                                            prefixAddedPlayer = NewSystem.getName(addedOfPlayer);
                                        } else {
                                            prefixAddedPlayer = SettingsFile.getConsolePrefix();
                                        }
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }else {
                                addedDate = SavingsFile.getStringPath("Maintenance.Info." + role + ".AddedDate");
                                if (!SavingsFile.getStringPath("Maintenance.Info." + role + ".AddedOf").equalsIgnoreCase("Console")) {
                                    OfflinePlayer addedOfPlayer = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Maintenance.Info." + role + ".AddedOf")));
                                    prefixAddedPlayer = NewSystem.getName(addedOfPlayer);
                                } else {
                                    prefixAddedPlayer = SettingsFile.getConsolePrefix();
                                }
                            }

                            TextComponent component = new TextComponent(listRoleFormat
                                    .replace("{Prefix}", SettingsFile.getPrefix())
                                    .replace("{Suffix}", suffix)
                                    .replace("{Role}", role));
                            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(listRoleHover
                                    .replace("{Prefix}", SettingsFile.getPrefix())
                                    .replace("{Suffix}", suffix)
                                    .replace("{Role}", role)
                                    .replace("{AddedOf}", prefixAddedPlayer)
                                    .replace("{AddedDate}", addedDate)).create()));
                            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/wartung remove " + role));

                            p.spigot().sendMessage(component);
                        }
                    }
                } else {
                    int addedRolesCount = addedRoles.size();
                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Count}", String.valueOf(addedRolesCount)));
                }
            }
        }
    }

    public static void sendList(CommandSender p) {
        List<String> player = SavingsFile.getStringListPath("Maintenance.Added");

        for(String msg : messageListPlayer) {
            if(msg.contains("{AddedPlayer}")) {
                if(player.isEmpty()) {
                    p.sendMessage(msg.replace("{AddedPlayer}", msgNoPlayersAdded));
                }else {
                    for (String addedPlayer : player) {
                        OfflinePlayer t = Bukkit.getOfflinePlayer(UUID.fromString(addedPlayer));
                        String prefix = NewSystem.getName(t);
                        String addedDate = "";
                        String prefixAddedPlayer = "";
                        if(mySQLEnabled){
                            try {
                                ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.MAINTENANCE_PLAYER.getTableName() + " WHERE UUID='" + addedPlayer + "'");

                                if(rs.next()) {
                                    addedDate = rs.getString("ADDED_DATE");
                                    if (!rs.getString("UUID_ADDED_OF").equalsIgnoreCase("Console")) {
                                        OfflinePlayer addedOfPlayer = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_ADDED_OF")));
                                        prefixAddedPlayer = NewSystem.getName(addedOfPlayer);
                                    } else {
                                        prefixAddedPlayer = SettingsFile.getConsolePrefix();
                                    }
                                }
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }else {
                            addedDate = SavingsFile.getStringPath("Maintenance.Info." + addedPlayer + ".AddedDate");
                            if (!SavingsFile.getStringPath("Maintenance.Info." + addedPlayer + ".AddedOf").equalsIgnoreCase("Console")) {
                                OfflinePlayer addedOfPlayer = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Maintenance.Info." + addedPlayer + ".AddedOf")));
                                prefixAddedPlayer = NewSystem.getName(addedOfPlayer);
                            } else {
                                prefixAddedPlayer = SettingsFile.getConsolePrefix();
                            }
                        }

                        String listMessage = listPlayerConsoleMessage.replace("{Prefix}", SettingsFile.getPrefix())
                                .replace("{Player}", prefix)
                                .replace("{AddedOf}", prefixAddedPlayer)
                                .replace("{AddedDate}", addedDate);
                        p.sendMessage(listMessage);
                    }
                }
            }else{
                int addedPlayerCount = addedPlayers.size();
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Count}", String.valueOf(addedPlayerCount)));
            }
        }

        if(NewSystem.newPerm) {
            List<String> roles = SavingsFile.getStringListPath("Maintenance.AddedRoles");

            for (String msg : messageListRole) {
                if (msg.contains("{AddedRoles}")) {
                    if (roles.isEmpty()) {
                        p.sendMessage(msg.replace("{AddedRoles}", msgNoRolesAdded));
                    } else {
                        for (String role : roles) {
                            String suffix = NewPermManager.getRoleSuffix(role);
                            String addedDate = "";
                            String prefixAddedPlayer = "";
                            if(mySQLEnabled){
                                try {
                                    ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.MAINTENANCE_ROLE.getTableName() + " WHERE ROLE='" + role + "'");

                                    if(rs.next()) {
                                        addedDate = rs.getString("ADDED_DATE");
                                        if (!rs.getString("UUID_ADDED_OF").equalsIgnoreCase("Console")) {
                                            OfflinePlayer addedOfPlayer = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("UUID_ADDED_OF")));
                                            prefixAddedPlayer = NewSystem.getName(addedOfPlayer);
                                        } else {
                                            prefixAddedPlayer = SettingsFile.getConsolePrefix();
                                        }
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }else {
                                addedDate = SavingsFile.getStringPath("Maintenance.Info." + role + ".AddedDate");
                                if (!SavingsFile.getStringPath("Maintenance.Info." + role + ".AddedOf").equalsIgnoreCase("Console")) {
                                    OfflinePlayer addedOfPlayer = Bukkit.getOfflinePlayer(UUID.fromString(SavingsFile.getStringPath("Maintenance.Info." + role + ".AddedOf")));
                                    prefixAddedPlayer = NewSystem.getName(addedOfPlayer);
                                } else {
                                    prefixAddedPlayer = SettingsFile.getConsolePrefix();
                                }
                            }

                            String listMessage = listRoleConsoleMessage
                                    .replace("{Prefix}", SettingsFile.getPrefix())
                                    .replace("{Suffix}", suffix)
                                    .replace("{Role}", role)
                                    .replace("{AddedOf}", prefixAddedPlayer)
                                    .replace("{AddedDate}", addedDate);
                            p.sendMessage(listMessage);
                        }
                    }
                } else {
                    int addedRolesCount = addedRoles.size();
                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Count}", String.valueOf(addedRolesCount)));
                }
            }
        }
    }

    public static List<String> addedPlayers = new ArrayList<>();
    public static List<String> addedRoles = new ArrayList<>();
    public static boolean status = false;

    private static void getAddedPlayers() {
        if(mySQLEnabled) {
            try {
                ResultSet rs = mySQL.executeQuery("SELECT UUID FROM " + SQLTables.MAINTENANCE_PLAYER.getTableName());

                while(rs.next()) {
                    addedPlayers.add(rs.getString("UUID"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else{
            addedPlayers = SavingsFile.getStringListPath("Maintenance.Added");
        }
    }

    public static boolean isPlayerAdded(OfflinePlayer p) {
        return addedPlayers.contains(p.getUniqueId().toString());
    }

    private static void getAddedRoles() {
        if(mySQLEnabled) {
            try {
                ResultSet rs = mySQL.executeQuery("SELECT ROLE FROM " + SQLTables.MAINTENANCE_ROLE.getTableName());

                while(rs.next()) {
                    addedRoles.add(rs.getString("ROLE"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else{
            addedRoles = SavingsFile.getStringListPath("Maintenance.AddedRoles");
        }
    }

    public static boolean isRoleAdded(String role) {
        return addedRoles.contains(role);
    }

    public static void getStatus() {
        if(mySQLEnabled) {
            try {
                ResultSet rs = mySQL.executeQuery("SELECT STATUS FROM " + SQLTables.MAINTENANCE.getTableName());

                if(rs.next()) {
                    status = (rs.getInt("STATUS") == 1);
                }else{
                    mySQL.executeUpdate("INSERT INTO " + SQLTables.MAINTENANCE.getTableName() + " (STATUS) VALUES ('" + 0 + "')");
                    status = false;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else{
            if(SavingsFile.isPathSet("Maintenance.Status")) {
                status = SavingsFile.getBooleanPath("Maintenance.Status");
            }else{
                SavingsFile.setPath("Maintenance.Status", false);
                status = false;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        List<String> tabCompletions = new ArrayList<>();

        if(sender instanceof Player) {
            Player p = (Player) sender;
            if (NewSystem.hasPermission(p, perm)) {
                if (args.length == 1) {
                    String[] completions = {"list", "add", "remove", "on", "off"};
                    for(String completion : completions) {
                        if(completion.contains(args[0])) {
                            tabCompletions.add(completion);
                        }
                    }
                }else if(args.length == 2) {
                    if(!(args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off"))) {
                        for(Player all : Bukkit.getOnlinePlayers()) {
                            if(all.getName().contains(args[1])) {
                                tabCompletions.add(all.getName());
                            }
                        }
                        if(NewSystem.newPerm) {
                            for(String role : NewPermManager.getRoleList()) {
                                if(role.contains(args[1])) {
                                    tabCompletions.add(role);
                                }
                            }
                        }
                    }
                }
            }
        }else{
            if (args.length == 1) {
                String[] completions = {"list", "add", "remove", "on", "off"};
                for(String completion : completions) {
                    if(completion.contains(args[0])) {
                        tabCompletions.add(completion);
                    }
                }
            }else if(args.length == 2) {
                if(!(args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off"))) {
                    for(Player all : Bukkit.getOnlinePlayers()) {
                        if(all.getName().contains(args[1])) {
                            tabCompletions.add(all.getName());
                        }
                    }

                    if(NewSystem.newPerm) {
                        for(String role : NewPermManager.getRoleList()) {
                            if(role.contains(args[1])) {
                                tabCompletions.add(role);
                            }
                        }
                    }
                }
            }
        }

        return tabCompletions;
    }
}
