package me.newdavis.spigot.listener;

import me.newdavis.spigot.api.CurrencyAPI;
import me.newdavis.spigot.api.ReflectionAPI;
import me.newdavis.spigot.api.TabListAPI;
import me.newdavis.spigot.command.*;
import me.newdavis.spigot.file.*;
import me.newdavis.spigot.sql.MySQL;
import me.newdavis.spigot.sql.SQLTables;
import me.newdavis.spigot.util.ItemBuilder;
import me.newdavis.spigot.util.Portal;
import me.newdavis.spigot.util.ScoreboardManager;
import me.newdavis.spigot.util.TabListPrefix;
import me.newdavis.spigot.plugin.NewSystem;
import me.newdavis.spigot.util.placeholder.PlaceholderManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OtherListeners implements Listener {

    private static MySQL mySQL = NewSystem.getMySQL();
    private static boolean mySQLEnabled  = SettingsFile.getMySQLEnabled();

    public static HashMap<String, String[]> commandAliases = null;
    public static HashMap<String, String[]> customCommandAliases = null;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        //Placeholder
        for(Player all : Bukkit.getOnlinePlayers()) {
            new PlaceholderManager(all);
        }

        //Save IP
        boolean saveIP = SettingsFile.getSaveIPs();
        if (saveIP) {
            String ip = new ReflectionAPI().getPlayerIP(p);
            if(mySQLEnabled) {
                try {
                    ResultSet rs = mySQL.executeQuery("SELECT UUID FROM " + SQLTables.IP.getTableName() + " WHERE IP='" + ip + "'");

                    boolean alreadySet = false;
                    while(rs.next()) {
                        if(rs.getString("UUID").equalsIgnoreCase(p.getUniqueId().toString())) {
                            alreadySet = true;
                        }
                    }
                    mySQL.disconnect();
                    if(!alreadySet) {
                        mySQL.executeUpdate("INSERT INTO " + SQLTables.IP.getTableName() + " (IP,UUID) VALUES ('" + ip + "','" + p.getUniqueId() + "')");
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }else {
                List<String> uuids = (SavingsFile.isPathSet("IP.User." + ip) ? SavingsFile.getStringListPath("IP.User." + ip) : new ArrayList<>());
                if (!uuids.contains(p.getUniqueId().toString())) {
                    uuids.add(p.getUniqueId().toString());
                }
                SavingsFile.setPath("IP.User." + ip, uuids);
            }
        }

        //Authentication
        p.removePotionEffect(PotionEffectType.BLINDNESS);
        if(CommandFile.getBooleanPath("Command.Authentication.Enabled")) {
            if(AuthenticationCmd.isRegistered(p)) {
                List<String> haveToLogin = CommandFile.getStringListPath("Command.Authentication.MessageHaveToLogin");
                for(String value : haveToLogin) {
                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }else{
                List<String> haveToRegister = CommandFile.getStringListPath("Command.Authentication.MessageHaveToRegister");
                for(String value : haveToRegister) {
                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
            if(CommandFile.getBooleanPath("Command.Authentication.BlindPlayer")) {
                PotionEffect potionEffect = new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 2);
                p.addPotionEffect(potionEffect, true);
            }
        }

        //Set Currency
        if(CommandFile.getBooleanPath("Command.Currency.Enabled")) {
            CurrencyAPI.getCurrencyOfPlayer(p);
        }

        //TabList
        if(OtherFile.getBooleanPath("Other.TabList.Enabled")) {
            TabListAPI.setTabList();
        }

        //TabListPrefix
        if (TabListFile.getBooleanPath("TabList.Enabled")) {
            TabListPrefix.setTabListForAll();
        }

        //Spawn
        if (CommandFile.getBooleanPath("Command.Spawn.Enabled")) {
            if (CommandFile.getBooleanPath("Command.Spawn.TeleportWhileJoin")) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(NewSystem.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        SpawnCmd.teleportSpawnJoin(p);
                    }
                }, 2);
            }
        }

        //ScoreBoard
        if(OtherFile.getBooleanPath("Other.ScoreBoard.Enabled")) {
            new ScoreboardManager(p).setDisplaySlot().setScoreBoard();
            ScoreboardManager.updateEveryScoreboard();
        }

        //Vanish
        if (CommandFile.getBooleanPath("Command.Vanish.Enabled")) {
            VanishCmd.joinMethod(p);
        }
    }

    @EventHandler
    public void onPlayerLogin(AsyncPlayerPreLoginEvent e) {
        OfflinePlayer p = Bukkit.getOfflinePlayer(e.getName());

        //Maintenance
        List<String> tryJoinMessage = CommandFile.getStringListPath("Command.Maintenance.MessagePlayerTryJoin");
        if(CommandFile.getBooleanPath("Command.Maintenance.Enabled")) {
            if(MaintenanceCmd.status) {
                if (!(MaintenanceCmd.isPlayerAdded(p) || NewSystem.newPerm && MaintenanceCmd.isRoleAdded(new PlaceholderManager(p).getPlaceholder("{Role}").getValue()))) {
                    String kickMessage = CommandFile.getStringPath("Command.Maintenance.MessageNotAdded")
                            .replace("{Prefix}", SettingsFile.getPrefix())
                            .replace("||", "\n");
                    e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, kickMessage);
                    if (CommandFile.getBooleanPath("Command.Maintenance.MessageIfPlayerTryJoin")) {
                        String prefix = NewSystem.getName(p);
                        for (String key : tryJoinMessage) {
                            Bukkit.broadcastMessage(key
                                    .replace("{Prefix}", SettingsFile.getPrefix())
                                    .replace("{Player}", prefix));
                        }
                    }
                }
            }
        }

        //BanIP
        if(CommandFile.getBooleanPath("Command.BanIP.Enabled")) {
            String ip = e.getAddress().toString().replace("/", "").split(":")[0].replace(".", "-");
            if (BanIPCmd.isIPBanned(ip)) {
                int punishmentCount = BanIPCmd.getIPPunishmentCount(ip) - 1;
                String reason = "";
                String dateOfBan = "";
                String durate = "";
                String dateOfBanEnds = "";
                if(mySQLEnabled) {
                    try {
                        ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.BANIP.getTableName() + " WHERE (IP='" + ip + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");

                        if(rs.next()) {
                            reason = rs.getString("REASON");
                            dateOfBan = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_BAN")));
                            durate = rs.getString("DURATE");
                            if(!rs.getString("DATE_OF_BAN_ENDS").equalsIgnoreCase("Permanent")) {
                                dateOfBanEnds = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_BAN_ENDS")));
                            }
                        }
                        mySQL.disconnect();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }else {
                    reason = SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Reason");
                    dateOfBan = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban"));
                    durate = SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Durate");
                    if(!SavingsFile.getStringPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban-Ends").equalsIgnoreCase("Permanent")) {
                        dateOfBanEnds = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.BanIP." + ip + "." + punishmentCount + ".Date-Of-Ban-Ends"));
                    }
                }

                if (BanIPCmd.isBanPermanent(ip)) {
                    String msg = BanIPCmd.kickMessagePermanent.replace("{Prefix}", SettingsFile.getPrefix())
                            .replace("{Reason}", reason)
                            .replace("{Date-Of-Ban}", dateOfBan);
                    e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, msg);
                } else {
                    String msg = BanIPCmd.kickMessageTemporary.replace("{Prefix}", SettingsFile.getPrefix())
                            .replace("{Reason}", reason)
                            .replace("{Durate}", durate)
                            .replace("{Date-Of-Ban}", dateOfBan)
                            .replace("{Date-Of-Ban-Ends}", dateOfBanEnds);
                    e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, msg);
                }
            }
        }

        //Ban
        if(CommandFile.getBooleanPath("Command.Ban.Enabled")) {
            if(BanCmd.isPlayerBanned(p)) {
                int punishmentCount = BanCmd.getPlayerPunishmentCount(p)-1;
                String reason = "";
                String dateOfBan = "";
                String durate = "";
                String dateOfBanEnds = "";
                if(mySQLEnabled) {
                    try {
                        ResultSet rs = mySQL.executeQuery("SELECT * FROM " + SQLTables.BAN.getTableName() + " WHERE (UUID='" + p.getUniqueId().toString() + "' AND PUNISHMENT_COUNT='" + punishmentCount + "')");

                        if(rs.next()) {
                            reason = rs.getString("REASON");
                            dateOfBan = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_BAN")));
                            durate = rs.getString("DURATE");
                            if(!rs.getString("DATE_OF_BAN_ENDS").equalsIgnoreCase("Permanent")) {
                                dateOfBanEnds = SettingsFile.DateFormat(Long.parseLong(rs.getString("DATE_OF_BAN_ENDS")));
                            }
                        }
                        mySQL.disconnect();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }else {
                    reason = SavingsFile.getStringPath("Punishment.Ban." + p.getUniqueId().toString() + "." + punishmentCount + ".Reason");
                    dateOfBan = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Ban." + p.getUniqueId().toString() + "." + punishmentCount + ".Date-Of-Ban"));
                    durate = SavingsFile.getStringPath("Punishment.Ban." + p.getUniqueId().toString() + "." + punishmentCount + ".Durate");
                    if(!SavingsFile.getStringPath("Punishment.Ban." + p.getUniqueId().toString() + "." + punishmentCount + ".Date-Of-Ban-Ends").equalsIgnoreCase("Permanent")) {
                        dateOfBanEnds = SettingsFile.DateFormat(SavingsFile.getLongPath("Punishment.Ban." + p.getUniqueId().toString() + "." + punishmentCount + ".Date-Of-Ban-Ends"));
                    }
                }

                if(BanCmd.isBanPermanent(p)) {
                    String msg = BanCmd.kickMessagePermanent.replace("{Prefix}", SettingsFile.getPrefix())
                            .replace("{Reason}", reason)
                            .replace("{Date-Of-Ban}", dateOfBan);
                    e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, msg);
                }else{
                    String msg = BanCmd.kickMessageTemporary.replace("{Prefix}", SettingsFile.getPrefix())
                            .replace("{Reason}", reason)
                            .replace("{Durate}", durate)
                            .replace("{Date-Of-Ban}", dateOfBan)
                            .replace("{Date-Of-Ban-Ends}", dateOfBanEnds);
                    e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, msg);
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        //Update Placeholder
        PlaceholderManager phManager = new PlaceholderManager();
        phManager.updatePlaceholder("{VanishCount}");

        //Support Quit
        if(CommandFile.getBooleanPath("Command.Support.Enabled")) {
            SupportCmd.QuitEvent(p);
        }

        //Report Quit
        if(CommandFile.getBooleanPath("Command.Report.Enabled")) {
            ReportCmd.QuitEvent(p);
        }

        //ScoreBoard
        if(OtherFile.getBooleanPath("Other.ScoreBoard.Enabled")) {
            ScoreboardManager.updateEveryScoreboard();
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            //God
            if (CommandFile.getBooleanPath("Command.God.Enabled")) {
                Player p = (Player) e.getEntity();
                if (GodCmd.godList.contains(p)) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDamageOfEntity(EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof Player) {
            //Peace
            if(CommandFile.getBooleanPath("Command.Peace.Enabled")) {
                List<String> tryToDamage = CommandFile.getStringListPath("Command.Peace.MessageTryToDamage");
                Player p = (Player) e.getEntity();
                if(e.getDamager() instanceof Player) {
                    Player t = (Player) e.getDamager();
                    if (t != null) {
                        if (new PeaceCmd().inPeace(p, t)) {
                            e.setCancelled(true);
                            for (String msg : tryToDamage) {
                                t.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(p)));
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();

        //Spawn
        if (CommandFile.getBooleanPath("Command.Spawn.Enabled")) {
            if (CommandFile.getBooleanPath("Command.Spawn.TeleportWhileRespawn")) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(NewSystem.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        SpawnCmd.teleportSpawnJoin(p);
                    }
                }, 2);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();

        //BackCmd
        if(CommandFile.getBooleanPath("Command.Back.Enabled")) {
            BackCmd.deathLocation.put(p, p.getLocation());
        }

        //Stats
        int deaths = 1;
        int kills = 0;
        if(mySQLEnabled) {
            try {
                if(mySQL.hasNext("SELECT UUID FROM " + SQLTables.STATS.getTableName() + " WHERE UUID='" + p.getUniqueId() + "'")) {
                    ResultSet rs = mySQL.executeQuery("SELECT DEATHS FROM " + SQLTables.STATS.getTableName() + " WHERE UUID='" + p.getUniqueId().toString() + "'");

                    if (rs.next()) {
                        deaths += rs.getInt("DEATHS");
                    }
                    mySQL.disconnect();

                    mySQL.executeUpdate("UPDATE " + SQLTables.STATS.getTableName() + " SET DEATHS='" + deaths + "' WHERE UUID='" + p.getUniqueId().toString() + "'");
                }else{
                    mySQL.executeUpdate("INSERT INTO " + SQLTables.STATS.getTableName() + " (UUID,KILLS,DEATHS) VALUES " +
                            "('" + p.getUniqueId().toString() + "'," +
                            "'" + kills + "'," +
                            "'" + deaths + "')");
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }else {
            deaths = SavingsFile.getIntegerPath("Stats." + p.getUniqueId() + ".Deaths") + 1;
            kills = SavingsFile.getIntegerPath("Stats." + p.getUniqueId() + ".Kills");
            SavingsFile.setPath("Stats." + p.getUniqueId() + ".Deaths", deaths);
            SavingsFile.setPath("Stats." + p.getUniqueId() + ".Kills", kills);
        }

        if(p.getKiller() != null) {
            Player killer = p.getKiller();
            int deathsK = 0;
            int killsK = 1;
            if(mySQLEnabled) {
                try {
                    if(mySQL.hasNext("SELECT UUID FROM " + SQLTables.STATS.getTableName() + " WHERE UUID='" + killer.getUniqueId() + "'")) {
                        ResultSet rs = mySQL.executeQuery("SELECT KILLS FROM " + SQLTables.STATS.getTableName() + " WHERE UUID='" + killer.getUniqueId().toString() + "'");

                        if (rs.next()) {
                            killsK += rs.getInt("KILLS");
                        }
                        mySQL.disconnect();

                        mySQL.executeUpdate("UPDATE " + SQLTables.STATS.getTableName() + " SET KILLS='" + killsK + "' WHERE UUID='" + killer.getUniqueId().toString() + "'");
                    }else{
                        mySQL.executeUpdate("INSERT INTO " + SQLTables.STATS.getTableName() + " (UUID,KILLS,DEATHS) VALUES " +
                                "('" + killer.getUniqueId().toString() + "'," +
                                "'" + killsK + "'," +
                                "'" + deathsK + "')");
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }else {
                deathsK = SavingsFile.getIntegerPath("Stats." + killer.getUniqueId() + ".Deaths");
                killsK += SavingsFile.getIntegerPath("Stats." + killer.getUniqueId() + ".Kills");
                SavingsFile.setPath("Stats." + killer.getUniqueId() + ".Deaths", deathsK);
                SavingsFile.setPath("Stats." + killer.getUniqueId() + ".Kills", killsK);
            }
        }
    }

    @EventHandler
    public void onSeverCommand(ServerCommandEvent e) {
        CommandSender sender = e.getSender();
        String commandMessage = e.getCommand();

        //CommandSpy
        if (CommandFile.getBooleanPath("Command.CommandSpy.Enabled") && CommandFile.getBooleanPath("Command.CommandSpy.ShowConsoleExecutingCommands")) {
            for (int i = 0; i < CommandSpyCmd.cmdSpyList.size(); i++) {
                Player t = CommandSpyCmd.cmdSpyList.get(i);
                for (String key : CommandSpyCmd.format) {
                    t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", SettingsFile.getConsolePrefix()).replace("{Command}", commandMessage));
                }
            }
        }

        //Command Aliases
        if(commandAliases != null) {
            String cmd = (commandMessage.split(" ")[0]).replace("/", "");
            if (!commandAliases.containsKey(cmd)) {
                String args = " ";
                for (int i = 1; i < commandMessage.split(" ").length; i++) {
                    if (i == commandMessage.split(" ").length) {
                        args += commandMessage.split(" ")[i];
                    } else {
                        args += commandMessage.split(" ")[i] + " ";
                    }
                }
                for (String cmd2 : commandAliases.keySet()) {
                    if (cmd2.equalsIgnoreCase("CustomCommands")) {
                        if (CommandFile.getBooleanPath("Command.CustomCommands.Enabled")) {
                            if (customCommandAliases != null) {
                                for (String customCommand : customCommandAliases.keySet()) {
                                    if (cmd.equalsIgnoreCase(customCommand)) {
                                        if (args.equalsIgnoreCase(" ")) {
                                            List<String> message = CommandFile.getStringListPath("Command.CustomCommands." + customCommand + ".Message");
                                            for (String msg : message) {
                                                sender.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                                            }
                                        } else {
                                            List<String> usage = CommandFile.getStringListPath("Command.CustomCommands." + customCommand + ".Usage");
                                            for (String msg : usage) {
                                                sender.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                                            }
                                        }
                                        e.setCancelled(true);
                                    } else {
                                        for (String customCommandAlias : customCommandAliases.get(customCommand)) {
                                            if (cmd.equalsIgnoreCase(customCommandAlias)) {
                                                if (args.equalsIgnoreCase(" ")) {
                                                    List<String> message = CommandFile.getStringListPath("Command.CustomCommands." + customCommand + ".Message");
                                                    for (String msg : message) {
                                                        sender.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                                                    }
                                                } else {
                                                    List<String> usage = CommandFile.getStringListPath("Command.CustomCommands." + customCommand + ".Usage");
                                                    for (String msg : usage) {
                                                        sender.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                                                    }
                                                }
                                                e.setCancelled(true);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        String[] aliases = commandAliases.get(cmd2);
                        for (String alias : aliases) {
                            if (cmd.equalsIgnoreCase(alias)) {
                                if (CommandFile.getBooleanPath("Command." + cmd2 + ".Enabled") || CommandFile.getBooleanPath("Command." + cmd2 + ".Enabled.Currency")) {
                                    Bukkit.dispatchCommand(sender, cmd2 + args);
                                    e.setCancelled(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onExecuteCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        String commandMessage = e.getMessage();

        //CommandSpy
        if (CommandFile.getBooleanPath("Command.CommandSpy.Enabled")) {
            for (int i = 0; i < CommandSpyCmd.cmdSpyList.size(); i++) {
                Player t = CommandSpyCmd.cmdSpyList.get(i);
                if (t != p) {
                    for(String key : CommandSpyCmd.format) {
                        t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(p)).replace("{Command}", commandMessage));
                    }
                }
            }
        }

        //Command Aliases
        if(commandAliases != null) {
            String cmd = (commandMessage.split(" ")[0]).replace("/", "");
            if(BlockCommandListener.haveAccessToCommand(p, cmd)) {
                if (!commandAliases.containsKey(cmd)) {
                    String args = " ";
                    for (int i = 1; i < commandMessage.split(" ").length; i++) {
                        if (i == commandMessage.split(" ").length) {
                            args += commandMessage.split(" ")[i];
                        } else {
                            args += commandMessage.split(" ")[i] + " ";
                        }
                    }
                    for (String cmd2 : commandAliases.keySet()) {
                        if (cmd2.equalsIgnoreCase("CustomCommands")) {
                            if (CommandFile.getBooleanPath("Command.CustomCommands.Enabled")) {
                                if (customCommandAliases != null) {
                                    for (String customCommand : customCommandAliases.keySet()) {
                                        if (cmd.equalsIgnoreCase(customCommand)) {
                                            String perm = (CommandFile.isPathSet("Command.CustomCommands." + customCommand + ".Permission") ? CommandFile.getStringPath("Command.CustomCommands." + customCommand + ".Permission") : "");
                                            if (NewSystem.hasPermission(p, perm)) {
                                                if (args.equalsIgnoreCase(" ")) {
                                                    List<String> message = CommandFile.getStringListPath("Command.CustomCommands." + customCommand + ".Message");
                                                    for (String msg : message) {
                                                        p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                                                    }
                                                } else {
                                                    List<String> usage = CommandFile.getStringListPath("Command.CustomCommands." + customCommand + ".Usage");
                                                    for (String msg : usage) {
                                                        p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                                                    }
                                                }
                                            } else {
                                                p.sendMessage(SettingsFile.getNoPerm());
                                            }
                                            e.setCancelled(true);
                                        } else {
                                            for (String customCommandAlias : customCommandAliases.get(customCommand)) {
                                                if (cmd.equalsIgnoreCase(customCommandAlias)) {
                                                    String perm = (CommandFile.isPathSet("Command.CustomCommands." + customCommand + ".Permission") ? CommandFile.getStringPath("Command.CustomCommands." + customCommand + ".Permission") : "");
                                                    if (NewSystem.hasPermission(p, perm)) {
                                                        if (args.equalsIgnoreCase(" ")) {
                                                            List<String> message = CommandFile.getStringListPath("Command.CustomCommands." + customCommand + ".Message");
                                                            for (String msg : message) {
                                                                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                                                            }
                                                        } else {
                                                            List<String> usage = CommandFile.getStringListPath("Command.CustomCommands." + customCommand + ".Usage");
                                                            for (String msg : usage) {
                                                                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                                                            }
                                                        }
                                                    } else {
                                                        p.sendMessage(SettingsFile.getNoPerm());
                                                    }
                                                    e.setCancelled(true);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            String[] aliases = commandAliases.get(cmd2);
                            for (String alias : aliases) {
                                if (cmd.equalsIgnoreCase(alias)) {
                                    if (CommandFile.getBooleanPath("Command." + cmd2 + ".Enabled") || CommandFile.getBooleanPath("Command." + cmd2 + ".Enabled.Currency")) {
                                        p.performCommand(cmd2 + args);
                                        e.setCancelled(true);
                                    }
                                }
                            }
                        }
                    }
                }
            }else{
                e.setCancelled(true);
            }
        }
    }

    //Move
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        //Freeze
        if (CommandFile.getBooleanPath("Command.Freeze.Enabled")) {
            HashMap<Player, Location> freezed = FreezeCmd.freezed;
            if (freezed.containsKey(p)) {
                Location location = freezed.get(p);
                p.teleport(location);
            }
        }

        //Authentication
        if (CommandFile.getBooleanPath("Command.Authentication.Enabled")) {
            if (!AuthenticationCmd.loggedInPlayer.contains(p)) {
                if (CommandFile.getBooleanPath("Command.Authentication.CancelMove")) {
                    p.teleport(e.getFrom());
                }
            }
        }

        //Portal
        if(e.getFrom().getBlockX() != e.getTo().getBlockX()
                || e.getFrom().getBlockY() != e.getTo().getBlockY()
                || e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {
            if (OtherFile.getBooleanPath("Other.Portal.Enabled")) {
                Portal portal = new Portal();
                if (portal.isInPortal(p)) {
                    if (portal.hasPermission()) {
                        portal.sendMessage();
                        portal.executeCommand();
                        portal.teleportPlayer();
                        portal.sendToServer();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();

        //BuildCmd Block Break
        if(CommandFile.getBooleanPath("Command.Build.Enabled")) {
            if (!BuildCmd.buildList.contains(p)) {
                e.setCancelled(true);
                for (String msg : BuildCmd.denyMessage) {
                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();

        //BuildCmd Block Place
        if(CommandFile.getBooleanPath("Command.Build.Enabled")) {
            if (!BuildCmd.buildList.contains(p)) {
                e.setCancelled(true);
                for (String msg : BuildCmd.denyMessage) {
                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (CommandFile.getBooleanPath("Command.InvSee.Enabled")) {
            if (InvseeCmd.invSeePlayer.contains(p)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        Inventory inventory = e.getInventory();
        String title = e.getView().getTitle();

        //Invsee
        if (CommandFile.getBooleanPath("Command.InvSee.Enabled")) {
            InvseeCmd.invSeePlayer.remove(p);
        }

        //Backpack
        if(CommandFile.getBooleanPath("Command.Backpack.Enabled")) {
            if(title != null && title.contains(BackpackCmd.title.split(" ")[0])) {
                if(!BackpackCmd.backpack.containsKey(p)) {
                    HashMap<Integer, ItemStack> items = new HashMap<>();
                    for (int i = 0; i < inventory.getSize(); i++) {
                        if (inventory.getItem(i) != null) {
                            items.put(i, inventory.getItem(i));
                        }else{
                            items.put(i, new ItemStack(Material.AIR));
                        }
                    }
                    BackpackCmd.playerBackpack.put(p, inventory);
                    for (int slotI : items.keySet()) {
                        SavingsFile.setPath("Backpack." + p.getUniqueId() + ".Slot." + slotI, items.get(slotI));
                    }
                    for(String value : BackpackCmd.messageSaved) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }
            if(title != null && title.contains(BackpackCmd.titleOther.split(" ")[0])) {
                if(BackpackCmd.backpack.containsKey(p)) {
                    OfflinePlayer t = BackpackCmd.backpack.get(p);
                    HashMap<Integer, ItemStack> items = new HashMap<>();
                    for (int i = 0; i < inventory.getSize(); i++) {
                        if (inventory.getItem(i) != null) {
                            items.put(i, inventory.getItem(i));
                        }else{
                            items.put(i, new ItemStack(Material.AIR));
                        }
                    }
                    if(t.isOnline()) {
                        if(BackpackCmd.playerBackpack.containsKey(t.getPlayer())) {
                            BackpackCmd.playerBackpack.put(t.getPlayer(), inventory);
                        }
                    }
                    for (int slotI : items.keySet()) {
                        SavingsFile.setPath("Backpack." + t.getUniqueId() + ".Slot." + slotI, items.get(slotI));
                    }
                    for (String msg : BackpackCmd.messageSavedOther) {
                        p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)));
                    }
                    BackpackCmd.backpack.remove(p);
                }
            }
        }

        //Garbage
        if (CommandFile.getBooleanPath("Command.Garbage.Enabled")) {
            if(title.equalsIgnoreCase(GarbageCmd.title)) {
                if(inventoryHasItem(inventory)) {
                    for (String key : GarbageCmd.close) {
                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }
        }
    }

    private boolean inventoryHasItem(Inventory inventory) {
        for(ItemStack item : ItemBuilder.getInventory(inventory)) {
            if(item != null && ItemBuilder.getMaterialOfItemStack(item) != Material.AIR) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        //Vouchers
        if(CommandFile.getBooleanPath("Command.Voucher.Enabled")) {
            if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                ItemStack item = ItemBuilder.getItemInHand(p);
                if((ItemBuilder.getMaterialOfItemStack(item) != Material.AIR) && item.hasItemMeta() && item.getItemMeta().hasLore()) {
                    VoucherCmd.redeemVoucher(p);
                }
            }
        }
    }

}
