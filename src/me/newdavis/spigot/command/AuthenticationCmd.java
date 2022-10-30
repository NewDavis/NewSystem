package me.newdavis.spigot.command;

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.file.SavingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class AuthenticationCmd implements CommandExecutor, TabCompleter {

    private static List<String> usageWP;
    private static List<String> usage;
    private static List<String> reset;
    private static List<String> get;
    private static List<String> set;
    private static String perm;
    private static List<String> changed;
    private static List<String> haveToLogin;
    private static List<String> haveToRegister;
    private static List<String> registered;
    private static List<String> alreadyRegistered;
    private static List<String> loggedIn;
    private static List<String> alreadyLoggedIn;
    private static List<String> incorrect;

    public AuthenticationCmd() {
        usageWP = CommandFile.getStringListPath("Command.Authentication.UsageWithPerms");
        usage = CommandFile.getStringListPath("Command.Authentication.Usage");
        reset = CommandFile.getStringListPath("Command.Authentication.MessageReseted");
        get = CommandFile.getStringListPath("Command.Authentication.MessageGet");
        set = CommandFile.getStringListPath("Command.Authentication.MessageSet");
        perm = CommandFile.getStringPath("Command.Authentication.Permission");
        changed = CommandFile.getStringListPath("Command.Authentication.MessageChanged");
        haveToLogin = CommandFile.getStringListPath("Command.Authentication.MessageHaveToLogin");
        haveToRegister = CommandFile.getStringListPath("Command.Authentication.MessageHaveToRegister");
        registered = CommandFile.getStringListPath("Command.Authentication.MessageRegistered");
        alreadyRegistered = CommandFile.getStringListPath("Command.Authentication.MessageAlreadyRegistered");
        loggedIn = CommandFile.getStringListPath("Command.Authentication.MessageLoggedIn");
        alreadyLoggedIn = CommandFile.getStringListPath("Command.Authentication.MessageAlreadyLoggedIn");
        incorrect = CommandFile.getStringListPath("Command.Authentication.MessagePasswordIncorrect");
        if(!NewSystem.loadedCommands.contains(this)) {
            NewSystem.loadedCommands.add(this);
            NewSystem.getInstance().getCommand("authentication").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if(args.length == 2) {
                    OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                    if(args[1].equalsIgnoreCase("reset")) {
                        resetPassword(t);
                        for(String msg : reset) {
                            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                        }
                    }else if(args[1].equalsIgnoreCase("get")) {
                        String password = getPassword(t);
                        for (String msg : get) {
                            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                    .replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Password}", password));
                        }
                    }else if(args[0].equalsIgnoreCase("register")) {
                        String password = getPasswordFromArray(args, 1);
                        register(p, password);
                    }else if(args[0].equalsIgnoreCase("login")) {
                        String password = getPasswordFromArray(args, 1);
                        login(p, password);
                    }else{
                        for(String value : usageWP) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                }else if(args.length > 2) {
                    if(args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("change") || args[1].equalsIgnoreCase("changepassword") || args[1].equalsIgnoreCase("changepw")) {
                        OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                        String password = getPasswordFromArray(args, 2);
                        setPassword(t, password);
                        for (String msg : set) {
                            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                    .replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Password}", password));
                        }
                    }else if(args[0].equalsIgnoreCase("register")) {
                        String password = getPasswordFromArray(args, 1);
                        register(p, password);
                    }else if(args[0].equalsIgnoreCase("login")) {
                        String password = getPasswordFromArray(args, 1);
                        login(p, password);
                    }else{
                        for(String value : usageWP) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                }else{
                    for(String value : usageWP) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }else{
                if(args.length >= 2) {
                    String password = getPasswordFromArray(args, 1);
                    if(args[0].equalsIgnoreCase("register")) {
                        register(p, password);
                    }else if(args[0].equalsIgnoreCase("login")) {
                        login(p, password);
                    }else if(args[0].equalsIgnoreCase("change") || args[0].equalsIgnoreCase("changepassword") || args[0].equalsIgnoreCase("changepw")) {
                        changePassword(p, password);
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
            }
        }else{
            if(args.length == 2) {
                OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                if(args[1].equalsIgnoreCase("reset")) {
                    resetPassword(t);
                    for(String msg : reset) {
                        sender.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                    }
                }else if(args[1].equalsIgnoreCase("get")) {
                    String password = getPassword(t);
                    for (String msg : get) {
                        sender.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                .replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Password}", password));
                    }
                }else{
                    for(String value : usageWP) {
                        sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }else if(args.length > 2) {
                if(args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("change") || args[1].equalsIgnoreCase("changepassword") || args[1].equalsIgnoreCase("changepw")) {
                    OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                    String password = getPasswordFromArray(args, 2);
                    setPassword(t, password);
                    for (String msg : set) {
                        sender.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                .replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Password}", password));
                    }
                }else{
                    for(String value : usageWP) {
                        sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }else{
                for(String value : usageWP) {
                    sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }
        return false;
    }

    private static String getPasswordFromArray(String[] args, int start) {
        String password = "";
        for(int i = start; i < args.length; i++) {
            if(i == args.length-1) {
                password += args[i];
            }else{
                password += args[i] + " ";
            }
        }
        return password;
    }

    private static void setPassword(OfflinePlayer t, String password) {
        SavingsFile.setPath("Authentication." + t.getUniqueId() + ".Password", password);
    }

    private static void changePassword(Player p, String password) {
        if (loggedInPlayer.contains(p)) {
            SavingsFile.setPath("Authentication." + p.getUniqueId() + ".Password", password);
            for (String msg : changed) {
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(p, false)).replace("{DisplayName}", NewSystem.getName(p, true)).replace("{Password}", password));
            }
        } else {
            for(String value : haveToLogin) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    private static void resetPassword(OfflinePlayer p) {
        if(isRegistered(p)) {
            SavingsFile.setPath("Authentication." + p.getUniqueId(), null);
            setPasswordRequired(p);
        }
    }

    public static void setPasswordRequired(OfflinePlayer p) {
        if(p.isOnline()) {
            Player pOnline = p.getPlayer();
            loggedInPlayer.remove(pOnline);
            if(!isRegistered(pOnline)) {
                for(String value : haveToRegister) {
                    pOnline.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }else{
                for(String value : haveToLogin) {
                    pOnline.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
            if (CommandFile.getBooleanPath("Command.Authentication.BlindPlayer")) {
                PotionEffect potionEffect = new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1);
                pOnline.addPotionEffect(potionEffect, true);
            }
        }
    }

    private static String getPassword(OfflinePlayer p) {
        String password = "Nicht gesetzt!";
        if(isRegistered(p)) {
            password = SavingsFile.getStringPath("Authentication." + p.getUniqueId() + ".Password");
        }
        return password;
    }

    public static boolean isRegistered(OfflinePlayer p) {
        return SavingsFile.isPathSet("Authentication." + p.getUniqueId() + ".Password");
    }

    public static ArrayList<Player> loggedInPlayer = new ArrayList<>();

    private static void register(Player p, String password) {
        if(!isRegistered(p)) {
            SavingsFile.setPath("Authentication." + p.getUniqueId() + ".Password", password);
            for(String msg : registered) {
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Password}", password));
            }
            loggedInPlayer.add(p);
            if(p.hasPotionEffect(PotionEffectType.BLINDNESS)) {
                p.removePotionEffect(PotionEffectType.BLINDNESS);
            }
        }else{
            for(String value : alreadyRegistered) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    private static void login(Player p, String inputPassword) {
        if(isRegistered(p)) {
            String password = SavingsFile.getStringPath("Authentication." + p.getUniqueId() + ".Password");
            if(inputPassword.equals(password)) {
                if(!loggedInPlayer.contains(p)) {
                    for(String value : loggedIn) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                    loggedInPlayer.add(p);
                    if(p.hasPotionEffect(PotionEffectType.BLINDNESS)) {
                        p.removePotionEffect(PotionEffectType.BLINDNESS);
                    }
                }else{
                    for(String value : alreadyLoggedIn) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }else{
                for(String value : incorrect) {
                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }else{
            for(String value : haveToRegister) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        List<String> tabCompletions = new ArrayList<>();

        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if (args.length == 1) {
                    String[] completions = {"register", "login"};
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
                }else if(args.length == 2) {
                    String[] completions = {"reset", "set", "get"};
                    for(String completion : completions) {
                        if(completion.contains(args[1])) {
                            tabCompletions.add(completion);
                        }
                    }
                }
            }else{
                if (args.length == 1) {
                    String[] completions = {"register", "login", "change"};
                    for(String completion : completions) {
                        if(completion.contains(args[0])) {
                            tabCompletions.add(completion);
                        }
                    }
                }
            }
        }else{
            if (args.length == 1) {
                for(Player all : Bukkit.getOnlinePlayers()) {
                    if(all.getName().contains(args[0])) {
                        tabCompletions.add(all.getName());
                    }
                }
            }else if(args.length == 2) {
                String[] completions = {"reset", "set", "get"};
                for(String completion : completions) {
                    if(completion.contains(args[1])) {
                        tabCompletions.add(completion);
                    }
                }
            }
        }

        return tabCompletions;
    }
}
