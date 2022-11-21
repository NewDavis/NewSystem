package me.newdavis.spigot.command;
//Plugin by NewDavis

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.file.SavingsFile;
import me.newdavis.spigot.listener.OtherListeners;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class TeleportCmd implements CommandExecutor {

    //TELEPORTASK
    public static HashMap<Player, Player> teleportAsk;
    private static boolean teleportAskEnabled = false;
    private static List<String> teleportAskUsage;
    private static String teleportAskPerm;
    private static List<String> teleportAskMsg;
    private static List<String> teleportAskMsgP;

    //TELEPORTASKHERE
    public static HashMap<Player, Player> teleportAskHere;
    private static boolean teleportAskHereEnabled = false;
    private static List<String> teleportAskHereUsage;
    private static String teleportAskHerePerm;
    private static List<String> teleportAskHereMsg;
    private static List<String> teleportAskHereMsgP;

    //TELEPORTACCEPT
    private static boolean teleportAcceptEnabled = false;
    private static List<String> teleportAcceptUsage;
    private static String teleportAcceptPerm;
    private static String teleportAcceptPermNoDelay;
    private static List<String> msgTpSendSelf;
    private static List<String> teleportAcceptMsgAccepted;
    private static List<String> teleportAcceptMsgAcceptedP;
    private static List<String> teleportAcceptMsgNoRequests;
    public static List<String> teleportAcceptMsgMovedWhileTeleport;
    private static List<String> teleportAcceptMsgTpWithDelay;
    private static List<String> teleportAcceptMsgAlreadyInTp;
    private static List<String> teleportAcceptMsgTeleport;
    private static int teleportAcceptDelay;

    //TELEPORTHERE
    private static boolean teleportHereEnabled = false;
    private static List<String> teleportHereUsage;
    private static String teleportHerePerm;
    private static List<String> teleportHereMsg;
    private static List<String> teleportHereMsgP;

    //TELEPORT
    private static boolean teleportEnabled = false;
    private static List<String> teleportUsage;
    private static String teleportPerm;
    private static List<String> teleportMsg;
    private static List<String> teleportMsgP;
    private static List<String> teleportMsgLocation;
    private static List<String> teleportMsgLocationP;

    //TELEPORTALL
    private static boolean teleportAllEnabled = false;
    private static List<String> teleportAllUsage;
    private static String teleportAllPerm;
    private static List<String> teleportAllMsgAll;
    private static List<String> teleportAllMsgPTpTo;
    private static List<String> teleportAllMsgP;

    //TELEPORTASKALL
    public static HashMap<Player, Player> teleportAskAll;
    private static boolean teleportAskAllEnabled = false;
    private static List<String> teleportAskAllUsage;
    private static String teleportAskAllPerm;
    private static List<String> teleportAskAllMsg;
    private static List<String> teleportAskAllMsgP;

    public boolean[] init() {
        boolean[] registered = new boolean[7];
        NewSystem.status.put("TeleportAsk CMD", CommandFile.getBooleanPath("Command.TeleportAsk.Enabled"));
        teleportAsk = new HashMap<>();
        if (CommandFile.getBooleanPath("Command.TeleportAsk.Enabled")) {
            teleportAskEnabled = true;
            teleportAskUsage = CommandFile.getStringListPath("Command.TeleportAsk.Usage");
            teleportAskPerm = CommandFile.getStringPath("Command.TeleportAsk.Permission");
            teleportAskMsg = CommandFile.getStringListPath("Command.TeleportAsk.Message");
            teleportAskMsgP = CommandFile.getStringListPath("Command.TeleportAsk.MessagePlayer");
            registered[0] = true;
        }
        NewSystem.status.put("TeleportAskHere CMD", CommandFile.getBooleanPath("Command.TeleportAskHere.Enabled"));
        teleportAskHere = new HashMap<>();
        if(CommandFile.getBooleanPath("Command.TeleportAskHere.Enabled")) {
            teleportAskHereEnabled = true;
            teleportAskHereUsage = CommandFile.getStringListPath("Command.TeleportAskHere.Usage");
            teleportAskHerePerm = CommandFile.getStringPath("Command.TeleportAskHere.Permission");
            teleportAskHereMsg = CommandFile.getStringListPath("Command.TeleportAskHere.Message");
            teleportAskHereMsgP = CommandFile.getStringListPath("Command.TeleportAskHere.MessagePlayer");
            registered[1] = true;
        }
        NewSystem.status.put("TeleportAccept CMD", CommandFile.getBooleanPath("Command.TeleportAccept.Enabled"));
        if(CommandFile.getBooleanPath("Command.TeleportAccept.Enabled")) {
            teleportAcceptEnabled = true;
            teleportAcceptUsage = CommandFile.getStringListPath("Command.TeleportAccept.Usage");
            teleportAcceptPerm = CommandFile.getStringPath("Command.TeleportAccept.Permission.Use");
            teleportAcceptPermNoDelay = CommandFile.getStringPath("Command.TeleportAccept.Permission.NoDelay");
            msgTpSendSelf = CommandFile.getStringListPath("Command.TeleportAccept.MessageTeleportSendYourSelf");
            teleportAcceptMsgAccepted = CommandFile.getStringListPath("Command.TeleportAccept.MessageAccepted");
            teleportAcceptMsgAcceptedP = CommandFile.getStringListPath("Command.TeleportAccept.MessageAcceptedPlayer");
            teleportAcceptMsgNoRequests = CommandFile.getStringListPath("Command.TeleportAccept.MessageNoRequests");
            teleportAcceptMsgMovedWhileTeleport = CommandFile.getStringListPath("Command.TeleportAccept.MessageMovedWhileTeleportation");
            teleportAcceptMsgTpWithDelay = CommandFile.getStringListPath("Command.TeleportAccept.MessageTeleportWithDelay");
            teleportAcceptMsgAlreadyInTp = CommandFile.getStringListPath("Command.TeleportAccept.MessageAlreadyInTeleport");
            teleportAcceptMsgTeleport = CommandFile.getStringListPath("Command.TeleportAccept.MessageTeleport");
            teleportAcceptDelay = CommandFile.getIntegerPath("Command.TeleportAccept.DelayInSeconds");
            registered[2] = true;
        }
        NewSystem.status.put("TeleportAskAll CMD", CommandFile.getBooleanPath("Command.TeleportAskAll.Enabled"));
        teleportAskAll = new HashMap<>();
        if(CommandFile.getBooleanPath("Command.TeleportAskAll.Enabled")) {
            teleportAskAllEnabled = true;
            teleportAskAllUsage = CommandFile.getStringListPath("Command.TeleportAskAll.Usage");
            teleportAskAllPerm = CommandFile.getStringPath("Command.TeleportAskAll.Permission");
            teleportAskAllMsg = CommandFile.getStringListPath("Command.TeleportAskAll.Message");
            teleportAskAllMsgP = CommandFile.getStringListPath("Command.TeleportAskAll.MessagePlayer");
            registered[3] = true;
        }
        NewSystem.status.put("TeleportAll CMD", CommandFile.getBooleanPath("Command.TeleportAll.Enabled"));
        if(CommandFile.getBooleanPath("Command.TeleportAll.Enabled")) {
            teleportAllEnabled = true;
            teleportAllUsage = CommandFile.getStringListPath("Command.TeleportAll.Usage");
            teleportAllPerm = CommandFile.getStringPath("Command.TeleportAll.Permission");
            teleportAllMsgAll = CommandFile.getStringListPath("Command.TeleportAll.MessageAll");
            teleportAllMsgPTpTo = CommandFile.getStringListPath("Command.TeleportAll.MessagePlayerTeleportTo");
            teleportAllMsgP = CommandFile.getStringListPath("Command.TeleportAll.MessagePlayer");
            registered[4] = true;
        }
        NewSystem.status.put("Teleport CMD", CommandFile.getBooleanPath("Command.Teleport.Enabled"));
        if(CommandFile.getBooleanPath("Command.Teleport.Enabled")) {
            teleportEnabled = true;
            teleportUsage = CommandFile.getStringListPath("Command.Teleport.Usage");
            teleportPerm = CommandFile.getStringPath("Command.Teleport.Permission");
            teleportMsg = CommandFile.getStringListPath("Command.Teleport.Message");
            teleportMsgP = CommandFile.getStringListPath("Command.Teleport.MessagePlayer");
            teleportMsgLocation = CommandFile.getStringListPath("Command.Teleport.MessageLocation");
            teleportMsgLocationP = CommandFile.getStringListPath("Command.Teleport.MessageLocationPlayer");
            registered[5] = true;
        }
        NewSystem.status.put("TeleportHere CMD", CommandFile.getBooleanPath("Command.TeleportHere.Enabled"));
        if(CommandFile.getBooleanPath("Command.TeleportHere.Enabled")) {
            teleportHereEnabled = true;
            teleportHereUsage = CommandFile.getStringListPath("Command.TeleportHere.Usage");
            teleportHerePerm = CommandFile.getStringPath("Command.TeleportHere.Permission");
            teleportHereMsg = CommandFile.getStringListPath("Command.TeleportHere.Message");
            teleportHereMsgP = CommandFile.getStringListPath("Command.TeleportHere.MessagePlayer");
            registered[6] = true;
        }

        if(!NewSystem.loadedCommands.contains(this)) {
            NewSystem.loadedCommands.add(this);
            if(registered[0]) {
                NewSystem.getInstance().getCommand("teleportask").setExecutor(this);
            }
            if(registered[1]) {
                NewSystem.getInstance().getCommand("teleportaskhere").setExecutor(this);
            }
            if(registered[2]) {
                NewSystem.getInstance().getCommand("teleportaccept").setExecutor(this);
            }
            if(registered[3]) {
                NewSystem.getInstance().getCommand("teleportaskall").setExecutor(this);
            }
            if(registered[4]) {
                NewSystem.getInstance().getCommand("teleportall").setExecutor(this);
            }
            if(registered[5]) {
                NewSystem.getInstance().getCommand("teleport").setExecutor(this);
            }
            if(registered[6]) {
                NewSystem.getInstance().getCommand("teleporthere").setExecutor(this);
            }
        }

        return registered;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(cmd.getName().equalsIgnoreCase("TeleportAsk")) {
                if(teleportAskEnabled) {
                    if(NewSystem.hasPermission(p, teleportAskPerm)) {
                        if(args.length == 0) {
                            for(String value : teleportAskUsage) {
                                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        }else if(args.length == 1) {
                            Player t = Bukkit.getPlayer(args[0]);
                            if(t != null) {
                                if(p != t) {
                                    teleportAskPlayer(p, t);
                                }else{
                                    for(String value : msgTpSendSelf) {
                                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                                    }
                                }
                            }else{
                                p.sendMessage(SettingsFile.getOffline());
                            }
                        }else{
                            for(String value : teleportAskUsage) {
                                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        }
                    }else{
                        p.sendMessage(SettingsFile.getNoPerm());
                    }
                }
            }else if(cmd.getName().equalsIgnoreCase("TeleportAskHere")) {
                if(teleportAskHereEnabled) {
                    if(NewSystem.hasPermission(p, teleportAskHerePerm)) {
                        if(args.length == 0) {
                            for(String value : teleportAskHereUsage) {
                                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        }else if(args.length == 1) {
                            Player t = Bukkit.getPlayer(args[0]);
                            if(t != null) {
                                if(p != t) {
                                    teleportAskHerePlayer(p, t);
                                }else{
                                    for(String value : msgTpSendSelf) {
                                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                                    }
                                }
                            }else{
                                p.sendMessage(SettingsFile.getOffline());
                            }
                        }else{
                            for(String value : teleportAskHereUsage) {
                                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        }
                    }else{
                        p.sendMessage(SettingsFile.getNoPerm());
                    }
                }
            }else if(cmd.getName().equalsIgnoreCase("TeleportAccept")) {
                if(teleportAcceptEnabled) {
                    if(NewSystem.hasPermission(p, teleportAcceptPerm)) {
                        if(args.length == 0) {
                            teleportAccept(p);
                        }else{
                            for(String value : teleportAcceptUsage) {
                                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        }
                    }else{
                        p.sendMessage(SettingsFile.getNoPerm());
                    }
                }
            }else if(cmd.getName().equalsIgnoreCase("teleportaskall")) {
                if(teleportAskAllEnabled) {
                    if(NewSystem.hasPermission(p, teleportAskAllPerm)) {
                        if(args.length == 0) {
                            teleportAskAll(p);
                        }else{
                            for(String value : teleportAskAllUsage) {
                                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        }
                    }else{
                        p.sendMessage(SettingsFile.getNoPerm());
                    }
                }
            }else if(cmd.getName().equalsIgnoreCase("TeleportHere")) {
                if(teleportHereEnabled) {
                    if(NewSystem.hasPermission(p, teleportHerePerm)) {
                        if(args.length == 1) {
                            Player t = Bukkit.getPlayer(args[0]);
                            if (t != null) {
                                if(p != t) {
                                    teleportHerePlayer(p, t);
                                }else{
                                    for(String value : msgTpSendSelf) {
                                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                                    }
                                }
                            } else {
                                p.sendMessage(SettingsFile.getOffline());
                            }
                        }else{
                            for(String value : teleportHereUsage) {
                                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        }
                    }else{
                        p.sendMessage(SettingsFile.getNoPerm());
                    }
                }
            }else if(cmd.getName().equalsIgnoreCase("teleport")) {
                if(teleportEnabled) {
                    if(NewSystem.hasPermission(p, teleportPerm)) {
                        if(args.length == 1) {
                            Player t = Bukkit.getPlayer(args[0]);
                            if (t != null) {
                                teleportPlayer(p, p, t);
                            } else {
                                p.sendMessage(SettingsFile.getOffline());
                            }
                        }else if(args.length == 2) {
                            Player t = Bukkit.getPlayer(args[0]);
                            Player teleportTo = Bukkit.getPlayer(args[1]);
                            if (t != null) {
                                if (teleportTo != null) {
                                    teleportPlayer(p, t, teleportTo);
                                } else {
                                    p.sendMessage(SettingsFile.getOffline());
                                }
                            } else {
                                p.sendMessage(SettingsFile.getOffline());
                            }
                        }else if(args.length == 3) {
                            try {
                                double x = Double.parseDouble(args[0]);
                                double y = Double.parseDouble(args[1]);
                                double z = Double.parseDouble(args[2]);
                                World world = p.getWorld();
                                teleportPlayerToCoordination(p, p, x, y, z, world);
                            }catch (NumberFormatException e) {
                                p.sendMessage(SettingsFile.getError().replace("{Error}", "Input Coordination"));
                            }
                        }else if(args.length == 4) {
                            Player t = Bukkit.getPlayer(args[0]);
                            if(t != null) {
                                try {
                                    double x = Double.parseDouble(args[1]);
                                    double y = Double.parseDouble(args[2]);
                                    double z = Double.parseDouble(args[3]);
                                    World world = p.getWorld();
                                    teleportPlayerToCoordination(p, t, x, y, z, world);
                                } catch (NumberFormatException e) {
                                    p.sendMessage(SettingsFile.getError().replace("{Error}", "Input Coordination"));
                                }
                            }else{
                                try {
                                    double x = Double.parseDouble(args[0]);
                                    double y = Double.parseDouble(args[1]);
                                    double z = Double.parseDouble(args[2]);
                                    World world = Bukkit.getWorld(args[3]);
                                    teleportPlayerToCoordination(p, p, x, y, z, world);
                                } catch (NumberFormatException e) {
                                    p.sendMessage(SettingsFile.getError().replace("{Error}", "Input Coordination"));
                                }
                            }
                        }else if(args.length == 5) {
                            try {
                                Player t = Bukkit.getPlayer(args[0]);
                                double x = Double.parseDouble(args[1]);
                                double y = Double.parseDouble(args[2]);
                                double z = Double.parseDouble(args[3]);
                                World world = null;
                                for(World w : Bukkit.getWorlds()) {
                                    if(w.getName().equalsIgnoreCase(args[4])) {
                                        world = Bukkit.getWorld(args[4]);
                                    }
                                }
                                if (t != null) {
                                    if(world == null) {
                                        world = t.getWorld();
                                    }
                                    teleportPlayerToCoordination(p, t, x, y, z, world);
                                } else {
                                    p.sendMessage(SettingsFile.getOffline());
                                }
                            }catch (NumberFormatException e) {
                                p.sendMessage(SettingsFile.getError().replace("{Error}", "Input Coordination"));
                            }
                        }else{
                            for(String value : teleportUsage) {
                                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        }
                    }else{
                        p.sendMessage(SettingsFile.getNoPerm());
                    }
                }
            }else if(cmd.getName().equalsIgnoreCase("teleportall")) {
                if(teleportAllEnabled) {
                    if(NewSystem.hasPermission(p, teleportAllPerm)) {
                        if(args.length == 0) {
                            teleportAll(p, p);
                        }else if(args.length == 1) {
                            Player t = Bukkit.getPlayer(args[0]);
                            if(t != null) {
                                teleportAll(p, t);
                            }else{
                                p.sendMessage(SettingsFile.getOffline());
                            }
                        }else{
                            for(String value : teleportAllUsage) {
                                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        }
                    }else{
                        p.sendMessage(SettingsFile.getNoPerm());
                    }
                }
            }
        }else {
            if (cmd.getName().equalsIgnoreCase("teleport")) {
                if (!teleportEnabled) {
                    return false;
                }
                if (args.length == 2) {
                    Player t = Bukkit.getPlayer(args[0]);
                    Player teleportTo = Bukkit.getPlayer(args[1]);
                    if (t != null) {
                        if (teleportTo != null) {
                            teleportPlayer(sender, t, teleportTo);
                        } else {
                            sender.sendMessage(SettingsFile.getOffline());
                        }
                    } else {
                        sender.sendMessage(SettingsFile.getOffline());
                    }
                } else if (args.length == 4) {
                    Player t = Bukkit.getPlayer(args[0]);
                    if (t != null) {
                        try {
                            double x = Double.parseDouble(args[1]);
                            double y = Double.parseDouble(args[2]);
                            double z = Double.parseDouble(args[3]);
                            World world = t.getWorld();
                            teleportPlayerToCoordination(sender, t, x, y, z, world);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(SettingsFile.getError().replace("{Error}", "Input Coordination"));
                        }
                    } else {
                        sender.sendMessage(SettingsFile.getOffline());
                    }
                } else if (args.length == 5) {
                    try {
                        Player t = Bukkit.getPlayer(args[0]);
                        double x = Double.parseDouble(args[1]);
                        double y = Double.parseDouble(args[2]);
                        double z = Double.parseDouble(args[3]);
                        World world = null;
                        for(World w : Bukkit.getWorlds()) {
                            if(w.getName().equalsIgnoreCase(args[4])) {
                                world = Bukkit.getWorld(args[4]);
                            }
                        }
                        if (t != null) {
                            if(world == null) {
                                world = t.getWorld();
                            }
                            teleportPlayerToCoordination(sender, t, x, y, z, world);
                        } else {
                            sender.sendMessage(SettingsFile.getOffline());
                        }
                    } catch (NumberFormatException e) {
                        sender.sendMessage(SettingsFile.getError().replace("{Error}", "Input Coordination"));
                    }
                } else {
                    for (String value : teleportUsage) {
                        sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }
        }
        return false;
    }

    public static void teleportAskPlayer(Player p, Player t) {
        for(String key : teleportAskMsg) {
            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
        }
        for (String key : teleportAskMsgP) {
            t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(p, false)).replace("{DisplayName}", NewSystem.getName(p, true)));
        }
        teleportAsk.put(t, p);
        teleportAskAll.remove(t);
        teleportAskHere.remove(t);
    }

    public static void teleportAskHerePlayer(Player p, Player t) {
        for(String key : teleportAskHereMsg) {
            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
        }
        for (String key : teleportAskHereMsgP) {
            t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(p, false)).replace("{DisplayName}", NewSystem.getName(p, true)));
        }
        teleportAskHere.put(t, p);
        teleportAskAll.remove(t);
        teleportAsk.remove(t);
    }

    private static Player getSender(Player target) {
        Player sender = null;

        if(teleportAskAll.containsKey(target)) {
            sender = teleportAskAll.get(target);
        }else if(teleportAsk.containsKey(target)) {
            sender = teleportAsk.get(target);
        }else{
            sender = teleportAskHere.get(target);
        }

        return sender;
    }

    public static void teleportAccept(Player target) {
        if (!(teleportAskAll.containsKey(target) || teleportAskHere.containsKey(target) || teleportAsk.containsKey(target))) {
            for (String value : teleportAcceptMsgNoRequests) {
                target.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
            return;
        }

        Player sender = getSender(target);
        if (sender != null) {
            if(!teleportAskAll.containsKey(target)) {
                for (String key : teleportAcceptMsgAccepted) {
                    sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(target, false)).replace("{DisplayName}", NewSystem.getName(target, true)));
                }
            }
            for (String key : teleportAcceptMsgAcceptedP) {
                target.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(sender, false)).replace("{DisplayName}", NewSystem.getName(sender, true)));
            }

            teleport(sender, target);
        } else {
            for (String value : teleportAcceptMsgNoRequests) {
                target.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static HashMap<Player, Integer> taskIDs = new HashMap<>();

    public static void teleportCoolDown(Player playerScheduler, Player player) {
        if (OtherListeners.teleport.contains(playerScheduler)) {
            for (String value : teleportAcceptMsgAlreadyInTp) {
                playerScheduler.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
            return;
        }

        OtherListeners.teleport.add(playerScheduler);
        final Integer[] seconds = new Integer[]{teleportAcceptDelay};

        taskIDs.put(playerScheduler, Bukkit.getScheduler().scheduleSyncRepeatingTask(NewSystem.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (seconds[0] == 1) {
                    for (String key : teleportAcceptMsgTpWithDelay) {
                        playerScheduler.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(player, false)).replace("{DisplayName}", NewSystem.getName(player, true)).replace("{Seconds}", CommandFile.getStringPath("Command.TeleportAccept.CountIsOne")));
                    }
                } else if (seconds[0] >= 1) {
                    for (String key : teleportAcceptMsgTpWithDelay) {
                        playerScheduler.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(player, false)).replace("{DisplayName}", NewSystem.getName(player, true)).replace("{Seconds}", String.valueOf(seconds[0])));
                    }
                } else if (seconds[0] == 0) {
                    playerScheduler.teleport(player);
                    for (String value : teleportAcceptMsgTeleport) {
                        playerScheduler.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }

                    teleportAskAll.remove(playerScheduler);
                    teleportAskHere.remove(playerScheduler);
                    teleportAsk.remove(playerScheduler);
                    OtherListeners.teleport.remove(playerScheduler);
                    int taskID = taskIDs.get(playerScheduler);
                    taskIDs.remove(playerScheduler);
                    Bukkit.getScheduler().cancelTask(taskID);
                }
                seconds[0]--;
            }
        }, 0, 20));
    }

    public static void teleport(Player sender, Player target) {
        if ((!NewSystem.hasPermission(target, teleportAcceptPermNoDelay)) && teleportAcceptDelay > 0) {
            if (teleportAskAll.containsKey(target) || teleportAskHere.containsKey(target)) {
                teleportCoolDown(target, sender);
                return;
            }
        } else if ((!NewSystem.hasPermission(sender, teleportAcceptPermNoDelay)) && teleportAcceptDelay > 0) {
            teleportCoolDown(sender, target);
            return;
        }

        if (teleportAskAll.containsKey(target) || teleportAskHere.containsKey(target)) {
            //target to sender
            target.teleport(sender);
            for (String value : teleportAcceptMsgTeleport) {
                target.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        } else {
            //sender to target
            sender.teleport(target);
            for (String value : teleportAcceptMsgTeleport) {
                sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
        teleportAskAll.remove(target);
        teleportAskHere.remove(target);
        teleportAsk.remove(target);
    }

    public static void teleportHerePlayer(Player p, Player t) {
        if(t != p) {
            t.teleport(p);
            for(String key : teleportHereMsgP) {
                t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(p, false)).replace("{DisplayName}", NewSystem.getName(p, true)));
            }
            for(String key : teleportHereMsg) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
            }
        }else{
            for(String value : msgTpSendSelf) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void teleportPlayer(Player p, Player t, Player teleportTo) {
        List<String> msg = CommandFile.getStringListPath("Command.Teleport.Message");
        List<String> msgP = CommandFile.getStringListPath("Command.Teleport.MessagePlayer");

        if(teleportTo != t) {
            t.teleport(teleportTo);
            for(String key : msgP) {
                t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{TeleportTo}", NewSystem.getName(teleportTo, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"))));
            }
            if(p != t) {
                for (String key : msg) {
                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{TeleportTo}", NewSystem.getName(teleportTo, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"))));
                }
            }
        }else{
            p.teleport(t);
            for(String key : msgP) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{TeleportTo}", NewSystem.getName(teleportTo, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"))));
            }
        }
    }

    public static void teleportPlayer(CommandSender p, Player t, Player teleportTo) {
        t.teleport(teleportTo);
        for (String key : teleportMsgP) {
            t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{TeleportTo}", NewSystem.getName(teleportTo, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"))));
        }
        for (String key : teleportMsg) {
            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{TeleportTo}", NewSystem.getName(teleportTo, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"))));
        }
    }

    public static void teleportPlayerToCoordination(Player p, Player t, double x, double y, double z, World world) {
        Location location = new Location(world, x, y, z);
        String xS = String.valueOf(x);
        String yS = String.valueOf(y);
        String zS = String.valueOf(z);

        if(p == t) {
            p.teleport(location);
            for(String key : teleportMsgLocationP) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{X}", xS).replace("{Y}", yS).replace("{Z}", zS).replace("{World}", world.getName()));
            }
        }else{
            t.teleport(location);
            for(String key : teleportMsgLocationP) {
                t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{X}", xS).replace("{Y}", yS).replace("{Z}", zS).replace("{World}", world.getName()));
            }
            for(String key : teleportMsgLocation) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{X}", xS).replace("{Y}", yS).replace("{Z}", zS).replace("{World}", world.getName()));
            }
        }
    }

    public static void teleportPlayerToCoordination(CommandSender p, Player t, double x, double y, double z, World world) {
        Location location = new Location(world, x, y, z);
        String xS = String.valueOf(x);
        String yS = String.valueOf(y);
        String zS = String.valueOf(z);

        t.teleport(location);
        for (String key : teleportMsgLocationP) {
            t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{X}", xS).replace("{Y}", yS).replace("{Z}", zS).replace("{World}", world.getName()));
        }
        for (String key : teleportMsgLocation) {
            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{X}", xS).replace("{Y}", yS).replace("{Z}", zS).replace("{World}", world.getName()));
        }
    }

    public static void teleportAll(Player p, Player t) {
        if(p == t) {
            for(String key : teleportAllMsgP) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }else{
            for(String key : teleportAllMsgPTpTo) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{TeleportTo}", NewSystem.getName(t, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"))));
            }
            for(String key : teleportAllMsgP) {
                t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }

        for(Player all : Bukkit.getOnlinePlayers()) {
            if(all != t) {
                all.teleport(t);
                for(String key : teleportAllMsgAll) {
                    all.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(p, false)).replace("{DisplayName}", NewSystem.getName(p, true)));
                }
            }
        }
    }

    public static void teleportAskAll(Player p) {
        for(String value : teleportAskAllMsgP) {
            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
        }
        for(Player all : Bukkit.getOnlinePlayers()) {
            if(all != p) {
                for (String key : teleportAskAllMsg) {
                    all.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(p, false)).replace("{DisplayName}", NewSystem.getName(p, true)));
                }
                teleportAskAll.put(all, p);
                teleportAskHere.remove(all);
                teleportAsk.remove(all);
            }
        }
    }
}
