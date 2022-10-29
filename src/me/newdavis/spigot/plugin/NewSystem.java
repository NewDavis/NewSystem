package me.newdavis.spigot.plugin;

import me.newdavis.manager.NewPermManager;
import me.newdavis.spigot.api.*;
import me.newdavis.spigot.command.*;
import me.newdavis.spigot.file.*;
import me.newdavis.spigot.listener.*;
import me.newdavis.spigot.sql.*;
import me.newdavis.spigot.util.*;
import me.newdavis.spigot.plugin.newsystem.command.*;
import me.newdavis.spigot.plugin.newsystem.listener.*;
import me.newdavis.spigot.util.placeholder.PlaceholderManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class NewSystem extends JavaPlugin {

    private static NewSystem instance;
    public static MySQL mySQL;
    public static boolean newPerm;
    public static boolean enabled = false;
    public final String PLUGIN_VERSION = getDescription().getVersion();
    public static HashMap<String, Boolean> status = new HashMap<>();
    public static HashMap<Player, Scoreboard> playerScoreboard = new HashMap<>();

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        //Start TPS Scheduler
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new TPS(), 100L, 1L);

        //Load all Files
        loadFiles();

        newPerm = SettingsFile.getNewPermActivated() && Bukkit.getServer().getPluginManager().isPluginEnabled("NewPerm");

        //MySQL
        boolean connected = false;
        if(SettingsFile.getMySQLEnabled()) {
            mySQL = new MySQL();
            if(!mySQL.connect()) {
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }else {
                connected = true;
                mySQL.createTables();
            }
        }

        //Load all commands, listeners, etc.
        loadAll();

        //MySQL connected message
        if(SettingsFile.getMySQLEnabled()) {
            if(connected) {
                Bukkit.getConsoleSender().sendMessage("");
                Bukkit.getConsoleSender().sendMessage(SettingsFile.getPrefix() + " §7Connected to §f§lMySQL database §7successfully!");
            }
        }

        //Updater
        String newestVersion = updateChecker();
        if(newestVersion != null) {
            Bukkit.getConsoleSender().sendMessage("");
            Bukkit.getConsoleSender().sendMessage(SettingsFile.getPrefix() + " §cThere is a newer version of NewSystem! §8(§4" + newestVersion + "§8)");
        }

        //Start Message
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(SettingsFile.getPrefix() + " §aPlugin was §astarted successfully§7! §8(§b" + PLUGIN_VERSION + "§8)");
        Bukkit.getConsoleSender().sendMessage("");
        enabled = true;
    }

    @Override
    public void onDisable() {
        //Stop Message
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(SettingsFile.getPrefix() + " §aPlugin was §cstopped §asuccessfully§7! §8(§b" + PLUGIN_VERSION + "§8)");
        Bukkit.getConsoleSender().sendMessage("");

        //Delete Support Tickets and Reports
        SavingsFile.setPath("Report", null);
        SavingsFile.setPath("Support", null);

        if(CommandFile.getBooleanPath("Command.Generator.Enabled")) {
            GeneratorAPI.stopAllGenerator();
        }
    }

    private static void loadAll() {
        //Command Aliases & Custom Command Aliases
        OtherListeners.commandAliases = CommandFile.getCommandAliases();
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(SettingsFile.getPrefix() + " §aCommand Aliases got reloaded!");
        if(CommandFile.getBooleanPath("Command.CustomCommands.Enabled")) {
            OtherListeners.customCommandAliases = CommandFile.getCustomCommandAliases();
            Bukkit.getConsoleSender().sendMessage(SettingsFile.getPrefix() + " §aCustom Command Aliases got reloaded!");
        }else{
            Bukkit.getConsoleSender().sendMessage(SettingsFile.getPrefix() + " §aCustom Command Aliases are §cdeactivated§7!");
        }
        Bukkit.getConsoleSender().sendMessage("");

        //Server Materials
        ItemBuilder.setServerMaterials();

        Bukkit.getConsoleSender().sendMessage(SettingsFile.getPrefix() + " §8§m---------------");
        Bukkit.getConsoleSender().sendMessage(SettingsFile.getPrefix() + " §8» §e§eCommands");
        Bukkit.getConsoleSender().sendMessage(SettingsFile.getPrefix() + " §8§m---------------");
        Bukkit.getConsoleSender().sendMessage("");

        String commands = "";

        boolean startScheduler = false;

        //Commands
        status.put("GameMode CMD", CommandFile.getBooleanPath("Command.GameMode.Enabled"));
        if (CommandFile.getBooleanPath("Command.GameMode.Enabled")) {
            new GameModeCmd().init();
            new GameModeShortCmd().init();
            commands += "§aGameMode§8, ";
        } else {
            commands += "§cGameMode§8, ";
        }

        status.put("Fly CMD", CommandFile.getBooleanPath("Command.Fly.Enabled"));
        if (CommandFile.getBooleanPath("Command.Fly.Enabled")) {
            new FlyCmd().init();
            commands += "§aFly§8, ";
        } else {
            commands += "§cFly§8, ";
        }

        status.put("Speed CMD", CommandFile.getBooleanPath("Command.Speed.Enabled"));
        if(CommandFile.getBooleanPath("Command.Speed.Enabled")) {
            new SpeedCmd().init();
            commands += "§aSpeed§8, ";
        }else{
            commands += "§cSpeed§8, ";
        }

        status.put("Heal CMD", CommandFile.getBooleanPath("Command.Heal.Enabled"));
        if (CommandFile.getBooleanPath("Command.Heal.Enabled")) {
            new HealCmd().init();
            commands += "§aHeal§8, ";
        } else {
            commands += "§cHeal§8, ";
        }

        status.put("Feed CMD", CommandFile.getBooleanPath("Command.Feed.Enabled"));
        if (CommandFile.getBooleanPath("Command.Feed.Enabled")) {
            new FeedCmd().init();
            commands += "§aFeed§8, ";
        } else {
            commands += "§cFeed§8, ";
        }

        status.put("Back CMD", CommandFile.getBooleanPath("Command.Back.Enabled"));
        if (CommandFile.getBooleanPath("Command.Back.Enabled")) {
            new BackCmd().init();
            commands += "§aBack§8, ";
        } else {
            commands += "§cBack§8, ";
        }

        status.put("EnderChest CMD", CommandFile.getBooleanPath("Command.EnderChest.Enabled"));
        if (CommandFile.getBooleanPath("Command.EnderChest.Enabled")) {
            new EnderchestCmd().init();
            commands += "§aEnderchest§8, ";
        } else {
            commands += "§cEnderchest§8, ";
        }

        status.put("InvSee CMD", CommandFile.getBooleanPath("Command.InvSee.Enabled"));
        if (CommandFile.getBooleanPath("Command.InvSee.Enabled")) {
            new InvseeCmd().init();
            commands += "§aInvsee§8, ";
        } else {
            commands += "§cInvsee§8, ";
        }

        status.put("CraftingTable CMD", CommandFile.getBooleanPath("Command.CraftingTable.Enabled"));
        if (CommandFile.getBooleanPath("Command.CraftingTable.Enabled")) {
            new CraftingTableCmd().init();
            commands += "§aCraftingTable§8, ";
        } else {
            commands += "§cCraftingTable§8, ";
        }

        status.put("Anvil CMD", CommandFile.getBooleanPath("Command.Anvil.Enabled"));
        /*if (CommandFile.getBooleanPath("Command.Anvil.Enabled")) {
            new AnvilCmd().init();
            commands += "§aAnvil§8, ";
        } else {*/
            commands += "§cAnvil§8, ";
        //}

        status.put("EnchantingTable CMD", CommandFile.getBooleanPath("Command.EnchantingTable.Enabled"));
        /*if (CommandFile.getBooleanPath("Command.EnchantingTable.Enabled")) {
            new EnchantingTableCmd().init();
            commands += "§aEnchantingTable§8, ";
        } else {*/
            commands += "§cEnchantingTable§8, ";
        //}

        status.put("Kit CMD", CommandFile.getBooleanPath("Command.Kit.Enabled"));
        if (CommandFile.getBooleanPath("Command.Kit.Enabled")) {
            new KitCmd().init();
            commands += "§aKit§8, ";
        } else {
            commands += "§cKit§8, ";
        }

        status.put("Give CMD", CommandFile.getBooleanPath("Command.Give.Enabled"));
        if(CommandFile.getBooleanPath("Command.Give.Enabled")) {
            new GiveCmd().init();
            commands += "§aGive§8, ";
        }else{
            commands += "§cGive§8, ";
        }

        status.put("GiveAll CMD", CommandFile.getBooleanPath("Command.GiveAll.Enabled"));
        if (CommandFile.getBooleanPath("Command.GiveAll.Enabled")) {
            new GiveAllCmd().init();
            commands += "§aGiveAll§8, ";
        } else {
            commands += "§cGiveAll§8, ";
        }

        status.put("Raffle CMD", CommandFile.getBooleanPath("Command.Raffle.Enabled"));
        if (CommandFile.getBooleanPath("Command.Raffle.Enabled")) {
            new RaffleCmd().init();
            commands += "§aRaffle§8, ";
        } else {
            commands += "§cRaffle§8, ";
        }

        status.put("SpawnMob CMD", CommandFile.getBooleanPath("Command.SpawnMob.Enabled"));
        if(CommandFile.getBooleanPath("Command.SpawnMob.Enabled")) {
            SpawnMobCmd.setMobEntityTypes();
            new SpawnMobCmd().init();
            commands += "§aSpawnMob§8, ";
        }else{
            commands += "§cSpawnMob§8, ";
        }

        status.put("Clear CMD", CommandFile.getBooleanPath("Command.Clear.Enabled"));
        if(CommandFile.getBooleanPath("Command.Clear.Enabled")) {
            new ClearCmd().init();
            commands += "§aClear§8, ";
        }else{
            commands += "§cClear§8, ";
        }

        status.put("Dupe CMD", CommandFile.getBooleanPath("Command.Dupe.Enabled"));
        if (CommandFile.getBooleanPath("Command.Dupe.Enabled")) {
            new DupeCmd().init();
            commands += "§aDupe§8, ";
        } else {
            commands += "§cDupe§8, ";
        }

        status.put("ItemEdit CMD", CommandFile.getBooleanPath("Command.ItemEdit.Enabled"));
        if (CommandFile.getBooleanPath("Command.ItemEdit.Enabled")) {
            new ItemEditCmd().init();
            commands += "§aItemEdit§8, ";
        } else {
            commands += "§cItemEdit§8, ";
        }

        status.put("Repair CMD", CommandFile.getBooleanPath("Command.Repair.Enabled"));
        if (CommandFile.getBooleanPath("Command.Repair.Enabled")) {
            new RepairCmd().init();
            commands += "§aRepair§8, ";
        } else {
            commands += "§cRepair§8, ";
        }

        status.put("Skull CMD", CommandFile.getBooleanPath("Command.Skull.Enabled"));
        if (CommandFile.getBooleanPath("Command.Skull.Enabled")) {
            new SkullCmd().init();
            commands += "§aSkull§8, ";
        } else {
            commands += "§cSkull§8, ";
        }

        status.put("Hat CMD", CommandFile.getBooleanPath("Command.Hat.Enabled"));
        if (CommandFile.getBooleanPath("Command.Hat.Enabled")) {
            new HatCmd().init();
            commands += "§aHat§8, ";
        } else {
            commands += "§cHat§8, ";
        }

        status.put("Backpack CMD", CommandFile.getBooleanPath("Command.Backpack.Enabled"));
        if (CommandFile.getBooleanPath("Command.Backpack.Enabled")) {
            new BackpackCmd().init();
            commands += "§aBackpack§8, ";
        } else {
            commands += "§cBackpack§8, ";
        }

        status.put("Garbage CMD", CommandFile.getBooleanPath("Command.Garbage.Enabled"));
        if (CommandFile.getBooleanPath("Command.Garbage.Enabled")) {
            new GarbageCmd().init();
            commands += "§aGarbage§8, ";
        } else {
            commands += "§cGarbage§8, ";
        }

        status.put("Poll CMD", CommandFile.getBooleanPath("Command.Poll.Enabled"));
        if (CommandFile.getBooleanPath("Command.Poll.Enabled")) {
            PollCmd pollCmd = new PollCmd();
            status.put("ja CMD", true);
            OtherListeners.commandAliases.put("ja", new String[]{"yes"});
            status.put("nein CMD", true);
            OtherListeners.commandAliases.put("nein", new String[]{"no"});
            pollCmd.init();
            SavingsFile.setPath("Poll.Enabled", false);
            commands += "§aPoll§8, ";
        } else {
            commands += "§cPoll§8, ";
        }

        status.put("Peace CMD", CommandFile.getBooleanPath("Command.Peace.Enabled"));
        if(CommandFile.getBooleanPath("Command.Peace.Enabled")) {
            new PeaceCmd().init();
            commands += "§aPeace§8, ";
        }else{
            commands += "§cPeace§8, ";
        }

        status.put("God CMD", CommandFile.getBooleanPath("Command.God.Enabled"));
        if(CommandFile.getBooleanPath("Command.God.Enabled")) {
            new GodCmd().init();
            commands += "§aGod§8, ";
        }else{
            commands += "§cGod§8, ";
        }

        status.put("Freeze CMD", CommandFile.getBooleanPath("Command.Freeze.Enabled"));
        if(CommandFile.getBooleanPath("Command.Freeze.Enabled")) {
            new FreezeCmd().init();
            commands += "§aFreeze§8, ";
        }else{
            commands += "§cFreeze§8, ";
        }

        status.put("Vanish CMD", CommandFile.getBooleanPath("Command.Vanish.Enabled"));
        if (CommandFile.getBooleanPath("Command.Vanish.Enabled")) {
            new VanishCmd().init();
            commands += "§aVanish§8, ";
        } else {
            commands += "§cVanish§8, ";
        }

        status.put("PrivateMessage CMD", CommandFile.getBooleanPath("Command.PrivateMessage.Enabled"));
        if (CommandFile.getBooleanPath("Command.PrivateMessage.Enabled")) {
            new PrivateMessageCmd().init();
            commands += "§aPrivateMessage§8, ";
        } else {
            commands += "§cPrivateMessage§8, ";
        }

        status.put("Build CMD", CommandFile.getBooleanPath("Command.Build.Enabled"));
        if (CommandFile.getBooleanPath("Command.Build.Enabled")) {
            new BuildCmd().init();
            commands += "§aBuild§8, ";
        } else {
            commands += "§cBuild§8, ";
        }

        status.put("Hologram CMD", CommandFile.getBooleanPath("Command.Hologram.Enabled"));
        if (CommandFile.getBooleanPath("Command.Hologram.Enabled")) {
            new HologramCmd().init();
            commands += "§aHologram§8, ";
        } else {
            commands += "§cHologram§8, ";
        }

        status.put("Currency CMD", CommandFile.getBooleanPath("Command.Currency.Enabled.Currency"));
        if(CommandFile.getBooleanPath("Command.Currency.Enabled.Currency")) {
            new CurrencyCmd().init();
            commands += "§aCurrency§8, ";
        }else{
            commands += "§cCurrency§8, ";
        }

        status.put("Pay CMD", CommandFile.getBooleanPath("Command.Currency.Enabled.Pay"));
        if(CommandFile.getBooleanPath("Command.Currency.Enabled.Pay")) {
            new PayCmd().init();
            commands += "§aPay§8, ";
        }else{
            commands += "§cPay§8, ";
        }

        status.put("PlayTime CMD", CommandFile.getBooleanPath("Command.PlayTime.Enabled"));
        if (CommandFile.getBooleanPath("Command.PlayTime.Enabled")) {
            new PlayTimeCmd().init();
            startScheduler = true;
            commands += "§aPlayTime§8, ";
        } else {
            commands += "§cPlayTime§8, ";
        }

        status.put("ClearLag CMD", CommandFile.getBooleanPath("Command.ClearLag.Enabled"));
        if (CommandFile.getBooleanPath("Command.ClearLag.Enabled")) {
            new ClearLagCmd().init();
            startScheduler = true;
            commands += "§aClearLag§8, ";
        } else {
            commands += "§cClearLag§8, ";
        }

        TeleportCmd teleport = new TeleportCmd();
        boolean[] registered = teleport.init();
        if(registered[0]) {
            commands += "§aTeleportAsk§8, ";
        }else {
            commands += "§cTeleportAsk§8, ";
        }
        if(registered[1]) {
            commands += "§aTeleportAskHere§8, ";
        }else {
            commands += "§cTeleportAskHere§8, ";
        }
        if(registered[2]) {
            commands += "§aTeleportAccept§8, ";
        }else {
            commands += "§cTeleportAccept§8, ";
        }
        if(registered[3]) {
            commands += "§aTeleportAskAll§8, ";
        }else {
            commands += "§cTeleportAskAll§8, ";
        }
        if(registered[4]) {
            commands += "§aTeleportAll§8, ";
        }else {
            commands += "§cTeleportAll§8, ";
        }
        if(registered[5]) {
            commands += "§aTeleport§8, ";
        }else {
            commands += "§cTeleport§8, ";
        }
        if(registered[6]) {
            commands += "§aTeleportHere§8, ";
        }else {
            commands += "§cTeleportHere§8, ";
        }

        status.put("Home CMD", CommandFile.getBooleanPath("Command.Home.Enabled"));
        if (CommandFile.getBooleanPath("Command.Home.Enabled")) {
            new HomeCmd().init();
            commands += "§aHome§8, ";
        } else {
            commands += "§cHome§8, ";
        }

        status.put("Spawn CMD", CommandFile.getBooleanPath("Command.Spawn.Enabled"));
        if (CommandFile.getBooleanPath("Command.Spawn.Enabled")) {
            new SpawnCmd().init();
            commands += "§aSpawn§8, ";
        } else {
            commands += "§cSpawn§8, ";
        }

        status.put("Warp CMD", CommandFile.getBooleanPath("Command.Warp.Enabled"));
        if (CommandFile.getBooleanPath("Command.Warp.Enabled")) {
            new WarpCmd().init();
            commands += "§aWarp§8, ";
        } else {
            commands += "§cWarp§8, ";
        }

        status.put("List CMD", CommandFile.getBooleanPath("Command.List.Enabled"));
        if(NewSystem.newPerm) {
            if(CommandFile.getBooleanPath("Command.List.Enabled")) {
                new ListCmd().init();
                commands += "§aList§8, ";
            }else{
                commands += "§cList§8, ";
            }
        }else{
            commands += "§cList §8(§cYou need NewPerm to activate!§8), ";
        }

        status.put("Authentication CMD", CommandFile.getBooleanPath("Command.Authentication.Enabled"));
        if (CommandFile.getBooleanPath("Command.Authentication.Enabled")) {
            new AuthenticationCmd().init();
            for(Player all : Bukkit.getOnlinePlayers()) {
                AuthenticationCmd.setPasswordRequired(all);
            }
            commands += "§aAuthentication§8, ";
        } else {
            commands += "§cAuthentication§8, ";
        }

        status.put("Ban CMD", CommandFile.getBooleanPath("Command.Ban.Enabled"));
        if (CommandFile.getBooleanPath("Command.Ban.Enabled")) {
            new BanCmd().init();
            commands += "§aBan§8, ";
        } else {
            commands += "§cBan§8, ";
        }

        status.put("BanIP CMD", CommandFile.getBooleanPath("Command.BanIP.Enabled"));
        if (CommandFile.getBooleanPath("Command.BanIP.Enabled")) {
            new BanIPCmd().init();
            commands += "§aBanIP§8, ";
        } else {
            commands += "§cBanIP§8, ";
        }

        status.put("UnBan CMD", CommandFile.getBooleanPath("Command.UnBan.Enabled"));
        if (CommandFile.getBooleanPath("Command.UnBan.Enabled")) {
            new UnBanCmd().init();
            commands += "§aUnban§8, ";
        } else {
            commands += "§cUnban§8, ";
        }

        status.put("Mute CMD", CommandFile.getBooleanPath("Command.Mute.Enabled"));
        if (CommandFile.getBooleanPath("Command.Mute.Enabled")) {
            new MuteCmd().init();
            commands += "§aMute§8, ";
        } else {
            commands += "§cMute§8, ";
        }

        status.put("MuteIP CMD", CommandFile.getBooleanPath("Command.MuteIP.Enabled"));
        if (CommandFile.getBooleanPath("Command.MuteIP.Enabled")) {
            new MuteIPCmd().init();
            commands += "§aMuteIP§8, ";
        } else {
            commands += "§cMuteIP§8, ";
        }

        status.put("UnMute CMD", CommandFile.getBooleanPath("Command.UnMute.Enabled"));
        if (CommandFile.getBooleanPath("Command.UnMute.Enabled")) {
            new UnMuteCmd().init();
            commands += "§aUnMute§8, ";
        } else {
            commands += "§cUnMute§8, ";
        }

        status.put("Kick CMD", CommandFile.getBooleanPath("Command.Kick.Enabled"));
        if (CommandFile.getBooleanPath("Command.Kick.Enabled")) {
            new KickCmd().init();
            commands += "§aKick§8, ";
        } else {
            commands += "§cKick§8, ";
        }

        status.put("Warn CMD", CommandFile.getBooleanPath("Command.Warn.Enabled"));
        if(CommandFile.getBooleanPath("Command.Warn.Enabled")) {
            new WarnCmd().init();
            commands += "§aWarn§8, ";
        }else{
            commands += "§cWarn§8, ";
        }

        status.put("ShowIP CMD", CommandFile.getBooleanPath("Command.ShowIP.Enabled"));
        if(CommandFile.getBooleanPath("Command.ShowIP.Enabled")) {
            new ShowIPCmd().init();
            commands += "§aShowIP§8, ";
        }else{
            commands += "§cShowIP§8, ";
        }

        status.put("History CMD", CommandFile.getBooleanPath("Command.History.Enabled"));
        if (CommandFile.getBooleanPath("Command.History.Enabled")) {
            new HistoryCmd().init();
            commands += "§aHistory§8, ";
        } else {
            commands += "§cHistory§8, ";
        }

        status.put("Maintenance CMD", CommandFile.getBooleanPath("Command.Maintenance.Enabled"));
        if (CommandFile.getBooleanPath("Command.Maintenance.Enabled")) {
            new MaintenanceCmd().init();
            commands += "§aMaintenance§8, ";
        } else {
            commands += "§cMaintenance§8, ";
        }

        status.put("CommandSpy CMD", CommandFile.getBooleanPath("Command.CommandSpy.Enabled"));
        if (CommandFile.getBooleanPath("Command.CommandSpy.Enabled")) {
            new CommandSpyCmd().init();
            commands += "§aCommandSpy§8, ";
        } else {
            commands += "§cCommandSpy§8, ";
        }

        status.put("GlobalMute CMD", CommandFile.getBooleanPath("Command.GlobalMute.Enabled"));
        if (CommandFile.getBooleanPath("Command.GlobalMute.Enabled")) {
            new GlobalMuteCmd().init();
            commands += "§aGlobalMute§8, ";
        } else {
            commands += "§cGlobalMute§8, ";
        }

        status.put("ClearChat CMD", CommandFile.getBooleanPath("Command.ClearChat.Enabled"));
        if (CommandFile.getBooleanPath("Command.ClearChat.Enabled")) {
            new ClearChatCmd().init();
            commands += "§aClearChat§8, ";
        } else {
            commands += "§cClearChat§8, ";
        }

        status.put("Broadcast CMD", CommandFile.getBooleanPath("Command.Broadcast.Enabled"));
        if (CommandFile.getBooleanPath("Command.Broadcast.Enabled")) {
            new BroadcastCmd().init();
            commands += "§aBroadcast§8, ";
        } else {
            commands += "§cBroadcast§8, ";
        }

        status.put("Sudo CMD", CommandFile.getBooleanPath("Command.Sudo.Enabled"));
        if (CommandFile.getBooleanPath("Command.Sudo.Enabled")) {
            new SudoCmd().init();
            commands += "§aSudo§8, ";
        } else {
            commands += "§cSudo§8, ";
        }

        status.put("TeamChat CMD", CommandFile.getBooleanPath("Command.TeamChat.Enabled"));
        if (CommandFile.getBooleanPath("Command.TeamChat.Enabled")) {
            new TeamChatCmd().init();
            commands += "§aTeamChat§8, ";
        } else {
            commands += "§cTeamChat§8, ";
        }

        status.put("BuildChat CMD", CommandFile.getBooleanPath("Command.BuildChat.Enabled"));
        if (CommandFile.getBooleanPath("Command.BuildChat.Enabled")) {
            new BuildChatCmd().init();
            commands += "§aBuildChat§8, ";
        } else {
            commands += "§cTeamChat§8, ";
        }

        status.put("AdminChat CMD", CommandFile.getBooleanPath("Command.AdminChat.Enabled"));
        if (CommandFile.getBooleanPath("Command.AdminChat.Enabled")) {
            new AdminChatCmd().init();
            commands += "§aAdminChat§8, ";
        } else {
            commands += "§cAdminChat§8, ";
        }

        status.put("Support CMD", CommandFile.getBooleanPath("Command.Support.Enabled"));
        if(CommandFile.getBooleanPath("Command.Support.Enabled")) {
            new SupportCmd().init();
            commands += "§aSupport§8, ";
        }else{
            commands += "§cSupport§8, ";
        }

        status.put("SupportMessage CMD", CommandFile.getBooleanPath("Command.SupportMessage.Enabled"));
        if(CommandFile.getBooleanPath("Command.SupportMessage.Enabled")) {
            new SupportMessageCmd().init();
            commands += "§aSupportMessage§8, ";
        }else{
            commands += "§cSupportMessage§8, ";
        }

        status.put("Report CMD", CommandFile.getBooleanPath("Command.Report.Enabled"));
        if (CommandFile.getBooleanPath("Command.Report.Enabled")) {
            new ReportCmd().init();
            commands += "§aReport§8, ";
        } else {
            commands += "§cReport§8, ";
        }

        status.put("Stats CMD", CommandFile.getBooleanPath("Command.Stats.Enabled"));
        if(CommandFile.getBooleanPath("Command.Stats.Enabled")) {
            new StatsCmd().init();
            commands += "§aStats§8, ";
        }else{
            commands += "§cStats§8, ";
        }

        status.put("Role CMD", CommandFile.getBooleanPath("Command.Role.Enabled"));
        if (NewSystem.newPerm) {
            if (CommandFile.getBooleanPath("Command.Role.Enabled")) {
                new RoleCmd().init();
                startScheduler = true;
                commands += "§aRole§8, ";
            } else {
                commands += "§cRole§8, ";
            }
        } else {
            commands += "§cRole §8(§cYou need NewPerm to activate!§8), ";
        }

        status.put("Voucher CMD", CommandFile.getBooleanPath("Command.Voucher.Enabled"));
        if(CommandFile.getBooleanPath("Command.Voucher.Enabled")) {
            new VoucherCmd().init();
            commands += "§aVoucher§8, ";
        }else{
            commands += "§cVoucher§8, ";
        }

        status.put("Generator CMD", CommandFile.getBooleanPath("Command.Generator.Enabled"));
        if(CommandFile.getBooleanPath("Command.Generator.Enabled")) {
            new GeneratorCmd().init();
            if(CommandFile.getBooleanPath("Command.Generator.StartAllAtServerStart")) {
                for(String gen : GeneratorAPI.getAllGenerator()) {
                    GeneratorAPI.start(gen);
                }
            }
            commands += "§aGenerator§8, ";
        }else{
            commands += "§cGenerator§8, ";
        }

        status.put("Day CMD", CommandFile.getBooleanPath("Command.Day.Enabled"));
        if(CommandFile.getBooleanPath("Command.Day.Enabled")) {
            new DayCmd().init();
            commands += "§aDay§8, ";
        }else{
            commands += "§cDay§8, ";
        }

        status.put("Night CMD", CommandFile.getBooleanPath("Command.Night.Enabled"));
        if(CommandFile.getBooleanPath("Command.Night.Enabled")) {
            new NightCmd().init();
            commands += "§aNight§8, ";
        }else{
            commands += "§cNight§8, ";
        }

        status.put("Ping CMD", CommandFile.getBooleanPath("Command.Ping.Enabled"));
        if(CommandFile.getBooleanPath("Command.Ping.Enabled")) {
            new PingCmd().init();
            commands += "§aPing§8, ";
        }else{
            commands += "§cPing§8, ";
        }

        status.put("CustomCommands CMD", CommandFile.getBooleanPath("Command.CustomCommands.Enabled"));
        if (CommandFile.getBooleanPath("Command.CustomCommands.Enabled")) {
            commands += "§aCustomCommands";
        } else {
            commands += "§cCustomCommands";
        }

        String[] splitted = commands.split("§8, ");
        String msg = SettingsFile.getPrefix() + " ";
        int count = 0;
        for(String split : splitted) {
            if(count == 10) {
                msg += split + "§8, ";
                Bukkit.getConsoleSender().sendMessage(msg);
                count = 0;
                msg = SettingsFile.getPrefix() + " ";
            }else{
                msg += split + "§8, ";
                count++;
            }
        }
        if(count < 10) {
            Bukkit.getConsoleSender().sendMessage(msg);
        }

        //
        //Placeholder
        //
        for(Player all : Bukkit.getOnlinePlayers()) {
            new PlaceholderManager(all);
        }

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(SettingsFile.getPrefix() + " §8§m---------------");
        Bukkit.getConsoleSender().sendMessage(SettingsFile.getPrefix() + " §8» §eListeners");
        Bukkit.getConsoleSender().sendMessage(SettingsFile.getPrefix() + " §8§m---------------");
        Bukkit.getConsoleSender().sendMessage("");

        String listeners = SettingsFile.getPrefix() + " ";

        //Listeners
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new OtherListeners(), instance);
        pm.registerEvents(new ChatListener(), instance);

        status.put("Chat", ListenerFile.getBooleanPath("Listener.Chat.Enabled"));
        if (ListenerFile.getBooleanPath("Listener.Chat.Enabled")) {
            new ChatListener().init();
            listeners += "§aChat§8, ";
        } else {
            listeners += "§cChat§8, ";
        }

        status.put("ServerPing", ListenerFile.getBooleanPath("Listener.ServerPing.Enabled"));
        if (ListenerFile.getBooleanPath("Listener.ServerPing.Enabled")) {
            new ServerPingListener().init();
            listeners += "§aServerPing§8, ";
        } else {
            listeners += "§cServerPing§8, ";
        }

        status.put("Join", ListenerFile.getBooleanPath("Listener.Join.Enabled"));
        if (ListenerFile.getBooleanPath("Listener.Join.Enabled")) {
            new JoinListener().init();
            listeners += "§aJoin§8, ";
        } else {
            listeners += "§cJoin§8, ";
        }

        status.put("Quit", ListenerFile.getBooleanPath("Listener.Quit.Enabled"));
        if (ListenerFile.getBooleanPath("Listener.Quit.Enabled")) {
            new QuitListener().init();
            listeners += "§aQuit§8, ";
        } else {
            listeners += "§cQuit§8, ";
        }

        status.put("BlockBreak", ListenerFile.getBooleanPath("Listener.BlockBreak.Enabled"));
        if (ListenerFile.getBooleanPath("Listener.BlockBreak.Enabled")) {
            new BlockBreakListener().init();
            listeners += "§aBlockBreak§8, ";
        } else {
            listeners += "§cBlockBreak§8, ";
        }

        status.put("BlockPlace", ListenerFile.getBooleanPath("Listener.BlockPlace.Enabled"));
        if (ListenerFile.getBooleanPath("Listener.BlockPlace.Enabled")) {
            new BlockPlaceListener().init();
            listeners += "§aBlockPlace§8, ";
        } else {
            listeners += "§cBlockPlace§8, ";
        }

        status.put("Death", ListenerFile.getBooleanPath("Listener.Death.Enabled"));
        if (ListenerFile.getBooleanPath("Listener.Death.Enabled")) {
            new DeathListener().init();
            listeners += "§aDeath§8, ";
        } else {
            listeners += "§cDeath§8, ";
        }

        status.put("DeathDrop", ListenerFile.getBooleanPath("Listener.DeathDrop.Enabled"));
        if (ListenerFile.getBooleanPath("Listener.DeathDrop.Enabled")) {
            new DeathDropListener().init();
            listeners += "§aDeathDrop§8, ";
        } else {
            listeners += "§cDeathDrop§8, ";
        }

        status.put("CancelWeatherChange", ListenerFile.getBooleanPath("Listener.CancelWeatherChange.Enabled"));
        if (ListenerFile.getBooleanPath("Listener.CancelWeatherChange.Enabled")) {
            new WeatherChangeListener().init();
            listeners += "§aCancelWeatherChange§8, ";
        } else {
            listeners += "§cCancelWeatherChange§8, ";
        }

        status.put("DoubleJump", ListenerFile.getBooleanPath("Listener.DoubleJump.Enabled"));
        if (ListenerFile.getBooleanPath("Listener.DoubleJump.Enabled")) {
            new DoubleJumpListener().init();
            listeners += "§aDoubleJump§8, ";
        } else {
            listeners += "§cDoubleJump§8, ";
        }

        status.put("NoHunger", ListenerFile.getBooleanPath("Listener.NoHunger.Enabled"));
        if (ListenerFile.getBooleanPath("Listener.NoHunger.Enabled")) {
            new NoHungerListener().init();
            listeners += "§aNoHunger§8, ";
        } else {
            listeners += "§cNoHunger§8, ";
        }

        status.put("NoDamage", ListenerFile.getBooleanPath("Listener.NoDamage.Fall.Enabled") || ListenerFile.getBooleanPath("Listener.NoDamage.Every.Enabled"));
        if (ListenerFile.getBooleanPath("Listener.NoDamage.Every.Enabled")) {
            new NoDamageListener().init();
            listeners += "§aNoDamage§8, ";
        } else if (ListenerFile.getBooleanPath("Listener.NoDamage.Fall.Enabled")) {
            new NoDamageListener().init();
            listeners += "§aNoFallDamage§8, ";
        } else {
            listeners += "§cNoDamage§8, ";
        }

        status.put("BlockCommand", ListenerFile.getBooleanPath("Listener.BlockCommand.Enabled"));
        if (ListenerFile.getBooleanPath("Listener.BlockCommand.Enabled")) {
            new BlockCommandListener().init();
            listeners += "§aBlockCommand§8, ";
        } else {
            listeners += "§cBlockCommand§8, ";
        }

        status.put("ColorSign", ListenerFile.getBooleanPath("Listener.ColorSign.Enabled"));
        if (ListenerFile.getBooleanPath("Listener.ColorSign.Enabled")) {
            new ColorSignListener().init();
            listeners += "§aColorSign§8, ";
        } else {
            listeners += "§cColorSign§8, ";
        }

        status.put("FreeItemSign", ListenerFile.getBooleanPath("Listener.FreeItemSign.Enabled"));
        if (ListenerFile.getBooleanPath("Listener.FreeItemSign.Enabled")) {
            new FreeItemSignListener().init();
            listeners += "§aFreeItemSign";
        } else {
            listeners += "§cFreeItemSign";
        }

        splitted = listeners.split("§8, ");
        msg = "";
        count = 0;
        for(String split : splitted) {
            if(count == 10) {
                msg += split + "§8, ";
                Bukkit.getConsoleSender().sendMessage(msg);
                count = 0;
                msg = SettingsFile.getPrefix() + " ";
            }else{
                msg += split + "§8, ";
                count++;
            }
        }
        if(count < 10) {
            Bukkit.getConsoleSender().sendMessage(msg);
        }

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(SettingsFile.getPrefix() + " §8§m---------------");
        Bukkit.getConsoleSender().sendMessage(SettingsFile.getPrefix() + " §8» §eOther");
        Bukkit.getConsoleSender().sendMessage(SettingsFile.getPrefix() + " §8§m---------------");
        Bukkit.getConsoleSender().sendMessage("");

        String other = SettingsFile.getPrefix() + " ";

        //Other
        status.put("TabList", OtherFile.getBooleanPath("Other.TabList.Enabled"));
        if (OtherFile.getBooleanPath("Other.TabList.Enabled")) {
            //Update Placeholder
            PlaceholderManager phManager = new PlaceholderManager();
            phManager.updatePlaceholder("{VanishCount}");

            if(TabListAPI.UPDATE_SPEED == 20) {
                startScheduler = true;
            }else {
                TabListAPI.TabListUpdater();
            }
            other += "§aTablist§8, ";
        } else {
            other += "§cTablist§8, ";
        }

        status.put("ScoreBoard", OtherFile.getBooleanPath("Other.ScoreBoard.Enabled"));
        if (OtherFile.getBooleanPath("Other.ScoreBoard.Enabled")) {
            for(Player all : Bukkit.getOnlinePlayers()) {
                new ScoreboardManager(all).setDisplaySlot().setScoreBoard();
            }
            ScoreboardManager.updateEveryScoreboard();
            if(ScoreboardManager.speed == 20) {
                startScheduler = true;
            }else {
                ScoreboardManager.startTimer();
            }
            other += "§aScoreboard§8, ";
        } else {
            other += "§cScoreboard§8, ";
        }

        status.put("AutoBroadcast", OtherFile.getBooleanPath("Other.AutoBroadcast.Enabled"));
        if (OtherFile.getBooleanPath("Other.AutoBroadcast.Enabled")) {
            AutoBroadcast.startAutoBroadcast();
            other += "§aAutoBroadcast§8, ";
        } else {
            other += "§cAutoBroadcast§8, ";
        }

        status.put("TabListPrefix", TabListFile.getBooleanPath("TabList.Enabled"));
        if (TabListFile.getBooleanPath("TabList.Enabled")) {
            TabListPrefix.setTabListForAll();
            other += "§aTabListPrefix§8, ";
        } else {
            other += "§cTabListPrefix§8, ";
        }

        status.put("CustomRecipes", OtherFile.getBooleanPath("Other.CustomRecipes.Enabled"));
        if (OtherFile.getBooleanPath("Other.CustomRecipes.Enabled")) {
            String enabled = CustomRecipe.addRecipes();
            other += "§aCustomRecipes §8(§7" + enabled + "§8), ";
        } else {
            other += "§cCustomRecipes§8, ";
        }

        status.put("ChatFilter", OtherFile.getBooleanPath("Other.ChatFilter.Enabled"));
        if (OtherFile.getBooleanPath("Other.ChatFilter.Enabled")) {
            new ChatFilter().init();
            other += "§aChatFilter§8, ";
        } else {
            other += "§cChatFilter§8, ";
        }

        status.put("Portal", OtherFile.getBooleanPath("Other.Portal.Enabled"));
        if (OtherFile.getBooleanPath("Other.Portal.Enabled")) {
            Bukkit.getMessenger().registerOutgoingPluginChannel(instance, "BungeeCord");
            new Portal().init();
            other += "§aPortal§8, ";
        } else {
            other += "§cPortal§8, ";
        }

        status.put("PlayTimeReward", OtherFile.getBooleanPath("Other.PlayTimeReward.Enabled"));
        if (OtherFile.getBooleanPath("Other.PlayTimeReward.Enabled") && CommandFile.getBooleanPath("Command.PlayTime.Enabled")) {
            new PlayTimeReward().init();
            other += "§aPlayTimeReward";
        } else {
            other += "§cPlayTimeReward";
        }

        splitted = other.split("§8, ");
        msg = "";
        count = 0;
        for(String split : splitted) {
            if(count == 10) {
                msg += split + "§8, ";
                Bukkit.getConsoleSender().sendMessage(msg);
                count = 0;
                msg = SettingsFile.getPrefix() + " ";
            }else{
                msg += split + "§8, ";
                count++;
            }
        }
        if(count < 10) {
            Bukkit.getConsoleSender().sendMessage(msg);
        }

        //NewSystem
        instance.getCommand("newsystem").setExecutor(new NewSystemCmd());
        status.put("NewSystem CMD", true);
        OtherListeners.commandAliases.put("NewSystem", new String[]{"ns"});
        pm.registerEvents(new Listeners(), instance);

        //One Second Scheduler Start
        if(startScheduler) {
            startOneSecondScheduler();
        }
    }

    public static void loadFiles() {
        SettingsFile.loadConfig();
        CommandFile.loadConfig();
        ListenerFile.loadConfig();
        OtherFile.loadConfig();
        SavingsFile.loadConfig();
        TabListFile.loadConfig();
        KitFile.loadConfig();
    }

    public static void startOneSecondScheduler() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
            @Override
            public void run() {
                if (CommandFile.getBooleanPath("Command.PlayTime.Enabled")) {
                    PlayTimeCmd.startTimer();
                }

                if (NewSystem.newPerm) {
                    if (CommandFile.getBooleanPath("Command.Role.Enabled")) {
                        RoleCmd.temporaryRoleTimer();
                    }
                }

                if (CommandFile.getBooleanPath("Command.ClearLag.Enabled")) {
                    ClearLagCmd.startAutoClearLag();
                }

                if (OtherFile.getBooleanPath("Other.TabList.Enabled")) {
                    if(TabListAPI.UPDATE_SPEED == 20) {
                        TabListAPI.setTabList();
                        TabListAPI.animationID++;
                        if(TabListAPI.animationID > TabListAPI.getAnimationAmount()) {
                            TabListAPI.animationID = 1;
                        }
                    }
                }

                if (OtherFile.getBooleanPath("Other.ScoreBoard.Enabled")) {
                    if(ScoreboardManager.speed == 20) {
                        for(Player all : Bukkit.getOnlinePlayers()) {
                            new ScoreboardManager(all).updateScoreBoardTitle();
                        }
                    }
                }
            }
        }, 0, 20);
    }

    public static boolean hasPermission(OfflinePlayer p, String perm) {
        if (newPerm) {
            return NewPermManager.playerHasPermission(p, perm);
        }
        if(p.isOnline()) {
            return p.getPlayer().hasPermission(perm);
        }
        return p.isOp();
    }

    public static String getName(OfflinePlayer p, boolean displayName) {
        if(!displayName) {
            return p.getName();
        }

        if (!p.isOnline()) {
            if (newPerm) {
                if (SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName")) {
                    return NewPermManager.getPlayerPrefix(p) + p.getName();
                }
            }
        }
        if (p.isOnline() && SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName")) {
            return p.getPlayer().getDisplayName();
        }
        return p.getName();
    }

    public static String replace(String msg) {
        if(SettingsFile.getRGBActivated()) {
            if (ReflectionAPI.VERSION_ID >= 16) {
                List<String> numbers = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
                if (msg.contains("rgb(") && msg.contains(")")) {
                    for (String s : msg.split(" ")) {
                        if (s.contains("rgb(") && s.contains(")")) {
                            String rgb = s.split("rgb")[1].replace("(", "");
                            int r = Integer.parseInt(rgb.split(",")[0]);
                            int g = Integer.parseInt(rgb.split(",")[1]);
                            int b;
                            try {
                                b = Integer.parseInt(rgb.split(",")[2].replace(")", ""));
                            } catch (NumberFormatException ignored) {
                                String s2 = rgb.split(",")[2].replace(")", "");
                                for (String s3 : s2.split("")) {
                                    if (!numbers.contains(s3)) {
                                        s2 = s2.replace(s3, "");
                                    }
                                }
                                b = Integer.parseInt(s2);
                            }

                            try {
                                Color color = new Color(r, g, b);
                                Class<?> clazz = Class.forName("net.md_5.bungee.api.ChatColor");
                                Method of = clazz.getMethod("of", Color.class);
                                Object chatcolor = of.invoke(clazz, color);
                                return msg.replace("rgb(" + r + "," + g + "," + b + ")", "" + (ChatColor) chatcolor);
                            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                                     ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        }
        return msg;
    }

    public static ChatColor getChatColor(String msg) {
        if(SettingsFile.getRGBActivated()) {
            if (ReflectionAPI.VERSION_ID >= 16) {
                List<String> numbers = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
                if (msg.contains("rgb(") && msg.contains(")")) {
                    for (String s : msg.split(" ")) {
                        if (s.contains("rgb(") && s.contains(")")) {
                            String rgb = s.split("rgb")[1].replace("(", "");
                            int r = Integer.parseInt(rgb.split(",")[0]);
                            int g = Integer.parseInt(rgb.split(",")[1]);
                            int b;
                            try {
                                b = Integer.parseInt(rgb.split(",")[2].replace(")", ""));
                            } catch (NumberFormatException ignored) {
                                String s2 = rgb.split(",")[2].replace(")", "");
                                for (String s3 : s2.split("")) {
                                    if (!numbers.contains(s3)) {
                                        s2 = s2.replace(s3, "");
                                    }
                                }
                                b = Integer.parseInt(s2);
                            }

                            try {
                                Color color = new Color(r, g, b);
                                Class<?> clazz = Class.forName("net.md_5.bungee.api.ChatColor");
                                Method of = clazz.getMethod("of", Color.class);
                                Object chatcolor = of.invoke(clazz, color);
                                return (ChatColor) chatcolor;
                            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                                     InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public String updateChecker() {
        try {
            URLConnection url = new URL("https://newdavis.me/plugin/update/index.php?plugin=newsystem").openConnection();
            url.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            url.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(url.getInputStream()));
            String newestVersion = br.readLine();
            if(!PLUGIN_VERSION.equals(newestVersion)) {
                return newestVersion;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static NewSystem getInstance() {
        return instance;
    }

    public static MySQL getMySQL() {
        return mySQL;
    }
}
