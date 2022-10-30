package me.newdavis.spigot.command;
//Plugin by NewDavis

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.file.KitFile;
import me.newdavis.spigot.file.SavingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KitCmd implements CommandExecutor, TabCompleter {

    private static List<String> usage;
    private static String perm;
    private static String permOther;
    private static List<String> msg;
    private static List<String> msgP;
    private static String kitPerm;
    private static String delayFormat;
    private static List<String> messageKitDelay;
    private static List<String> messageKitList;
    private static String kitListFormat;

    public KitCmd() {
        usage = CommandFile.getStringListPath("Command.Kit.Usage");
        perm = CommandFile.getStringPath("Command.Kit.Permission.Use");
        permOther = CommandFile.getStringPath("Command.Kit.Permission.Other");
        kitPerm = CommandFile.getStringPath("Command.Kit.Permission.Kit");
        msg = CommandFile.getStringListPath("Command.Kit.Message");
        msgP = CommandFile.getStringListPath("Command.Kit.MessagePlayer");
        delayFormat = CommandFile.getStringPath("Command.Kit.DelayFormat");
        messageKitDelay = CommandFile.getStringListPath("Command.Kit.MessageKitDelay");
        messageKitList = CommandFile.getStringListPath("Command.Kit.MessageKitList");
        kitListFormat = CommandFile.getStringPath("Command.Kit.KitListFormat");
        if(!NewSystem.loadedCommands.contains(this)) {
            NewSystem.loadedCommands.add(this);
            NewSystem.getInstance().getCommand("kit").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (NewSystem.hasPermission(p, perm)) {
                if (args.length == 0) {
                    sendList(p);
                }else if(args.length == 1) {
                    String kit = args[0];
                    if(!kitExist(kit).equalsIgnoreCase("")) {
                        kit = kitExist(kit);
                        if(KitFile.getKit(p, p, kit)) {
                            for(String key : msgP) {
                                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Kit}", kit).replace("[", "").replace("]", ""));
                            }
                        }else{
                            sendDelay(p, kit);
                        }
                    }else{
                        sendList(p);
                    }
                }else if(args.length == 2) {
                    Player t = Bukkit.getPlayer(args[1]);
                    if(t != null) {
                        String kit = args[0];
                        if (!kitExist(kit).equalsIgnoreCase("")) {
                            kit = kitExist(kit);
                            if(NewSystem.hasPermission(p, permOther)) {
                                if (KitFile.getKit(p, t, kit)) {
                                    for(String key : msg) {
                                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Kit}", kit).replace("[", "").replace("]", ""));
                                    }
                                    for(String key : msgP) {
                                        t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Kit}", kit).replace("[", "").replace("]", ""));
                                    }
                                }else{
                                    sendDelay(p, kit);
                                }
                            }else{
                                p.sendMessage(SettingsFile.getNoPerm());
                            }
                        } else {
                            sendList(p);
                        }
                    }else{
                        p.sendMessage(SettingsFile.getOffline());
                    }
                }else{
                    for(String value : usage) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            } else {
                p.sendMessage(SettingsFile.getNoPerm());
            }
        }else{
            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("list")) {
                    sendList(sender);
                }else{
                    for(String value : usage) {
                        sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }else if(args.length == 2) {
                Player t = Bukkit.getPlayer(args[1]);
                if(t != null) {
                    String kit = args[0];
                    if (!kitExist(kit).equalsIgnoreCase("")) {
                        kit = kitExist(kit);
                            if (KitFile.getKit(t, kit)) {
                                for(String key : msg) {
                                    sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Kit}", kit).replace("[", "").replace("]", ""));
                                }
                                for(String key : msgP) {
                                    t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Kit}", kit).replace("[", "").replace("]", ""));
                                }
                            }
                    } else {
                        sendList(sender);
                    }
                }else{
                    sender.sendMessage(SettingsFile.getOffline());
                }
            }else{
                for(String value : usage) {
                    sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }
        return false;
    }

    public static String kitExist(String kit) {
        Collection<String> kitNames = KitFile.getKitsName();
        for(String forKit : kitNames) {
            if(forKit.equalsIgnoreCase(kit)) {
                return forKit;
            }
        }
        return "";
    }

    public static void sendDelay(Player p, String kit) {
        long end = SavingsFile.getLongPath("Kit." + kit + "." + p.getUniqueId() + ".Abgeholt");
        long now = System.currentTimeMillis();
        int rest = (int) ((end + KitFile.yaml.getLong("Kit." + kit + ".DelayInTicks")) - now);

        int days = rest/1000/60/60/24;
        rest = rest-(days*1000*60*60*24);
        int hours = (rest/1000/60/60);
        rest = rest-(hours*1000*60*60);
        int minutes = (rest/1000/60);
        rest = rest-(minutes*1000*60);
        int seconds = (rest/1000);

        String format = delayFormat.replace("{Days}", String.valueOf(days)).replace("{Hours}", String.valueOf(hours))
                .replace("{Minutes}", String.valueOf(minutes)).replace("{Seconds}", String.valueOf(seconds));
        for(String key : messageKitDelay) {
            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Delay}", format).replace("[", "").replace("]", ""));
        }
    }

    public static void sendList(Player p) {
        if(CommandFile.getBooleanPath("Command.Kit.ShowOnlyKitsWithPermission")) {
            Collection<String> kitNames = KitFile.getKitsName();
            String kits = "";
            for(String kit : kitNames) {
                if(NewSystem.hasPermission(p, kitPerm.replace("{Kit}", kit))) {
                    if(kits.equalsIgnoreCase("")) {
                        kits = kitListFormat.replace("{Kit}", kit);
                    }else{
                        kits = kits + " " + kitListFormat.replace("{Kit}", kit);
                    }
                }
            }
            for(String msg : messageKitList) {
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Kits}", kits));
            }
        }else{
            Collection<String> kitNames = KitFile.getKitsName();
            String kits = "";
            for(String kit : kitNames) {
                if(kits.equalsIgnoreCase("")) {
                    kits = kitListFormat.replace("{Kit}", kit);
                }else{
                    kits = kits + " " + kitListFormat.replace("{Kit}", kit);
                }
            }
            for(String msg : messageKitList) {
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Kits}", kits));
            }
        }
    }

    public static void sendList(CommandSender p) {
        Collection<String> kitNames = KitFile.getKitsName();
        String kits = "";
        for (String kit : kitNames) {
            if (kits.equalsIgnoreCase("")) {
                kits = kitListFormat.replace("{Kit}", kit);
            } else {
                kits = kits + " " + kitListFormat.replace("{Kit}", kit);
            }
        }
        for (String msg : messageKitList) {
            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Kits}", kits));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        List<String> tabCompletions = new ArrayList<>();

        if(sender instanceof Player) {
            Player p = (Player) sender;
            if (NewSystem.hasPermission(p, perm)) {
                if (args.length == 1) {
                    String[] completions = {"list"};
                    for(String completion : completions) {
                        if(completion.contains(args[0])) {
                            tabCompletions.add(completion);
                        }
                    }

                    for(String kit : KitFile.getKitsName()) {
                        if(NewSystem.hasPermission(p, kitPerm.replace("{Kit}", kit))) {
                            if(kit.contains(args[0])) {
                                tabCompletions.add(kit);
                            }
                        }
                    }
                }else if(args.length == 2) {
                    if(!(args[0].equalsIgnoreCase("list"))) {
                        for(Player all : Bukkit.getOnlinePlayers()) {
                            if(all.getName().contains(args[1])) {
                                tabCompletions.add(all.getName());
                            }
                        }
                    }
                }
            }
        }else{
            if (args.length == 1) {
                String[] completions = {"list"};
                for(String completion : completions) {
                    if(completion.contains(args[0])) {
                        tabCompletions.add(completion);
                    }
                }

                for(String kit : KitFile.getKitsName()) {
                    if(kit.contains(args[0])) {
                        tabCompletions.add(kit);
                    }
                }
            }else if(args.length == 2) {
                if(!(args[0].equalsIgnoreCase("list"))) {
                    for(Player all : Bukkit.getOnlinePlayers()) {
                        if(all.getName().contains(args[1])) {
                            tabCompletions.add(all.getName());
                        }
                    }
                }
            }
        }

        return tabCompletions;
    }
}
