package me.newdavis.spigot.command;

import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.api.CurrencyAPI;
import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SavingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CurrencyCmd implements CommandExecutor, TabCompleter {

    private static String perm;
    private static String permOther;
    private static List<String> usageWP;
    private static List<String> usage;
    private static List<String> msgNegative;
    private static List<String> msgShowMoney;
    private static List<String> msgShowMoneyPlayer;
    private static List<String> msgAdd;
    private static List<String> msgAddPlayer;
    private static List<String> msgRemove;
    private static List<String> msgRemovePlayer;
    private static List<String> msgSet;
    private static List<String> msgSetPlayer;
    private static List<String> msgReset;
    private static List<String> msgResetPlayer;
    private static List<String> msgMultiply;
    private static List<String> msgMultiplyPlayer;
    private static List<String> msgDivide;
    private static List<String> msgDividePlayer;

    //TopList
    private static String permTopList;
    private static boolean topListEnabled;
    private static int topListSize;
    private static String first;
    private static String second;
    private static String third;
    private static String otherPlacing;
    private static double noMoney;
    private static String noPlayer;
    private static String format;
    private static List<String> topListMessage;

    public void init() {
        perm = CommandFile.getStringPath("Command.Currency.Permission.Pay");
        permOther = CommandFile.getStringPath("Command.Currency.Permission.Other");
        usageWP = CommandFile.getStringListPath("Command.Currency.UsageWithPermission");
        usage = CommandFile.getStringListPath("Command.Currency.Usage");
        msgNegative = CommandFile.getStringListPath("Command.Currency.MessageNumberNegative");
        msgShowMoney = CommandFile.getStringListPath("Command.Currency.MessageShowMoney");
        msgShowMoneyPlayer = CommandFile.getStringListPath("Command.Currency.MessageShowMoneyPlayer");
        msgAdd = CommandFile.getStringListPath("Command.Currency.MessageAdd");
        msgAddPlayer = CommandFile.getStringListPath("Command.Currency.MessageAddPlayer");
        msgRemove = CommandFile.getStringListPath("Command.Currency.MessageRemove");
        msgRemovePlayer = CommandFile.getStringListPath("Command.Currency.MessageRemovePlayer");
        msgSet = CommandFile.getStringListPath("Command.Currency.MessageSet");
        msgSetPlayer = CommandFile.getStringListPath("Command.Currency.MessageSetPlayer");
        msgReset = CommandFile.getStringListPath("Command.Currency.MessageReset");
        msgResetPlayer = CommandFile.getStringListPath("Command.Currency.MessageResetPlayer");
        msgMultiply = CommandFile.getStringListPath("Command.Currency.MessageMultiply");
        msgMultiplyPlayer = CommandFile.getStringListPath("Command.Currency.MessageMultiplyPlayer");
        msgDivide = CommandFile.getStringListPath("Command.Currency.MessageDivide");
        msgDividePlayer = CommandFile.getStringListPath("Command.Currency.MessageDividePlayer");
        permTopList = CommandFile.getStringPath("Command.Currency.Permission.TopList");
        topListEnabled = CommandFile.getBooleanPath("Command.Currency.EnableTopList");
        topListSize = CommandFile.getIntegerPath("Command.Currency.TopListSize");
        first = CommandFile.getStringPath("Command.Currency.FirstPlacing");
        second = CommandFile.getStringPath("Command.Currency.SecondPlacing");
        third = CommandFile.getStringPath("Command.Currency.ThirdPlacing");
        otherPlacing = CommandFile.getStringPath("Command.Currency.OtherPlacing");
        noMoney = CommandFile.getDoublePath("Command.Currency.NoMoney");
        noPlayer = CommandFile.getStringPath("Command.Currency.NoPlayer");
        format = CommandFile.getStringPath("Command.Currency.TopListFormat").replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix());
        topListMessage = CommandFile.getStringListPath("Command.Currency.TopListMessage");
        NewSystem.getInstance().getCommand("currency").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            List<String> usage = (NewSystem.hasPermission(p, perm) ? usageWP : CurrencyCmd.usage);
            if(args.length == 0) {
                for(String msg : msgShowMoneyPlayer) {
                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(CurrencyAPI.getCurrencyOfPlayer(p))));
                }
            }else if(args.length == 1) {
                if(args[0].equalsIgnoreCase("top")) {
                    if(NewSystem.hasPermission(p, permTopList)) {
                        if(topListEnabled) {
                            sendTopList(p);
                        }else{
                            for(String value : usage) {
                                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        }
                    }else{
                        p.sendMessage(SettingsFile.getNoPerm());
                    }
                }else{
                    if(NewSystem.hasPermission(p, permOther)) {
                        OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                        if(p != t) {
                            if(SavingsFile.isPathSet("Currency." + t.getUniqueId())) {
                                for(String msg : msgShowMoney) {
                                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(CurrencyAPI.getCurrencyOfPlayer(t))).replace("{Player}", NewSystem.getName(t)));
                                }
                            }else{
                                for(String value : usage) {
                                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                            }
                        }else{
                            for(String msg : msgShowMoneyPlayer) {
                                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(CurrencyAPI.getCurrencyOfPlayer(p))));
                            }
                        }
                    }else{
                        for(String value : usage) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                }
            }else if(args.length == 2) {
                if(NewSystem.hasPermission(p, perm)) {
                    OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                    if(t != p) {
                        if(NewSystem.hasPermission(p, permOther)) {
                            if (args[1].equalsIgnoreCase("reset")) {
                                CurrencyAPI.resetCurrencyOfPlayer(t);
                                if (t.isOnline()) {
                                    for(String msg : msgResetPlayer) {
                                        t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                                    }
                                    for(String msg : msgShowMoneyPlayer) {
                                        t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(CurrencyAPI.getCurrencyOfPlayer(t))));
                                    }
                                }
                                for(String msg : msgReset) {
                                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)));
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
                        if (args[1].equalsIgnoreCase("reset")) {
                            CurrencyAPI.resetCurrencyOfPlayer(p);
                            for(String msg : msgResetPlayer) {
                                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                            for(String msg : msgShowMoneyPlayer) {
                                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(CurrencyAPI.getCurrencyOfPlayer(t))));
                            }
                        }else{
                            for(String value : usage) {
                                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        }
                    }
                }else{
                    for(String value : usage) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }else if(args.length == 3) {
                if(NewSystem.hasPermission(p, perm)) {
                    OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                    double amount = getDoubleOfString(p, args[2]);
                    if(!(amount < 0)) {
                        if (args[1].equalsIgnoreCase("add")) {
                            if (t != p) {
                                if (NewSystem.hasPermission(p, permOther)) {
                                    CurrencyAPI.addCurrencyToPlayer(t, amount);
                                    if(t.isOnline()) {
                                        for(String msg : msgAddPlayer) {
                                            t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(amount)));
                                        }
                                        for(String msg : msgShowMoneyPlayer) {
                                            t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(CurrencyAPI.getCurrencyOfPlayer(t))));
                                        }
                                    }
                                    for(String msg : msgAdd) {
                                        p.sendMessage(msg.replace("{Player}", NewSystem.getName(t)).replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(amount)));
                                    }
                                } else {
                                    p.sendMessage(SettingsFile.getNoPerm());
                                }
                            } else {
                                for(String msg : msgAddPlayer) {
                                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(amount)));
                                }
                                CurrencyAPI.addCurrencyToPlayer(p, amount);
                                for(String msg : msgShowMoneyPlayer) {
                                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(CurrencyAPI.getCurrencyOfPlayer(p))));
                                }
                            }
                        } else if (args[1].equalsIgnoreCase("remove")) {
                            if (t != p) {
                                if (NewSystem.hasPermission(p, permOther)) {
                                    CurrencyAPI.removeCurrencyOfPlayer(t, amount);
                                    if(t.isOnline()) {
                                        for(String msg : msgRemovePlayer) {
                                            t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(amount)));
                                        }
                                        for(String msg : msgShowMoneyPlayer) {
                                            t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(CurrencyAPI.getCurrencyOfPlayer(t))));
                                        }
                                    }
                                    for(String msg : msgRemove) {
                                        p.sendMessage(msg.replace("{Player}", NewSystem.getName(t)).replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(amount)));
                                    }
                                } else {
                                    p.sendMessage(SettingsFile.getNoPerm());
                                }
                            } else {
                                for(String msg : msgRemovePlayer) {
                                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(amount)));
                                }
                                CurrencyAPI.removeCurrencyOfPlayer(t, amount);
                                for(String msg : msgShowMoneyPlayer) {
                                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(CurrencyAPI.getCurrencyOfPlayer(p))));
                                }
                            }
                        } else if (args[1].equalsIgnoreCase("set")) {
                            if (t != p) {
                                if (NewSystem.hasPermission(p, permOther)) {
                                    CurrencyAPI.setCurrencyOfPlayer(t, amount);
                                    if(t.isOnline()) {
                                        for(String msg : msgSetPlayer) {
                                            t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(amount)));
                                        }
                                        for(String msg : msgShowMoneyPlayer) {
                                            t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(CurrencyAPI.getCurrencyOfPlayer(t))));
                                        }
                                    }
                                    for(String msg : msgSet) {
                                        p.sendMessage(msg.replace("{Player}", NewSystem.getName(t)).replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(amount)));
                                    }
                                } else {
                                    p.sendMessage(SettingsFile.getNoPerm());
                                }
                            } else {
                                for(String msg : msgSetPlayer) {
                                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(amount)));
                                }
                                CurrencyAPI.setCurrencyOfPlayer(t, amount);
                                for(String msg : msgShowMoneyPlayer) {
                                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(CurrencyAPI.getCurrencyOfPlayer(t))));
                                }
                            }
                        } else if (args[1].equalsIgnoreCase("multiply")) {
                            if (t != p) {
                                if (NewSystem.hasPermission(p, permOther)) {
                                    CurrencyAPI.multipleCurrencyOfPlayer(t, amount);
                                    if(t.isOnline()) {
                                        for(String msg : msgMultiplyPlayer) {
                                            t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Faktor}", CurrencyAPI.getCurrencyString(amount)));
                                        }
                                        for(String msg : msgShowMoneyPlayer) {
                                            t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(CurrencyAPI.getCurrencyOfPlayer(t))));
                                        }
                                    }
                                    for(String msg : msgMultiply) {
                                        p.sendMessage(msg.replace("{Player}", NewSystem.getName(t)).replace("{Prefix}", SettingsFile.getPrefix()).replace("{Faktor}", CurrencyAPI.getCurrencyString(amount)));
                                    }
                                } else {
                                    p.sendMessage(SettingsFile.getNoPerm());
                                }
                            } else {
                                for(String msg : msgMultiplyPlayer) {
                                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Faktor}", CurrencyAPI.getCurrencyString(amount)));
                                }
                                CurrencyAPI.multipleCurrencyOfPlayer(t, amount);
                                for(String msg : msgShowMoneyPlayer) {
                                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(CurrencyAPI.getCurrencyOfPlayer(t))));
                                }
                            }
                        } else if (args[1].equalsIgnoreCase("divide")) {
                            if (t != p) {
                                if (NewSystem.hasPermission(p, permOther)) {
                                    CurrencyAPI.divideCurrencyOfPlayer(t, amount);
                                    if(t.isOnline()) {
                                        for(String msg : msgDividePlayer) {
                                            t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Dividend}", CurrencyAPI.getCurrencyString(amount)));
                                        }
                                        for(String msg : msgShowMoneyPlayer) {
                                            t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(CurrencyAPI.getCurrencyOfPlayer(t))));
                                        }
                                    }
                                    for(String msg : msgDivide) {
                                        p.sendMessage(msg.replace("{Player}", NewSystem.getName(t)).replace("{Prefix}", SettingsFile.getPrefix()).replace("{Dividend}", CurrencyAPI.getCurrencyString(amount)));
                                    }
                                } else {
                                    p.sendMessage(SettingsFile.getNoPerm());
                                }
                            } else {
                                for(String msg : msgDividePlayer) {
                                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Dividend}", CurrencyAPI.getCurrencyString(amount)));
                                }
                                CurrencyAPI.divideCurrencyOfPlayer(t, amount);
                                for(String msg : msgShowMoneyPlayer) {
                                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(CurrencyAPI.getCurrencyOfPlayer(t))));
                                }
                            }
                        }
                    }else{
                        for(String value : msgNegative) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
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
        }else {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("top")) {
                    if (topListEnabled) {
                        sendTopList(sender);
                    } else {
                        for (String value : usageWP) {
                            sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                } else {
                    OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                    if (SavingsFile.isPathSet("Currency." + t.getUniqueId())) {
                        for (String msg : msgShowMoney) {
                            sender.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(CurrencyAPI.getCurrencyOfPlayer(t))).replace("{Player}", NewSystem.getName(t)));
                        }
                    } else {
                        for (String value : usageWP) {
                            sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                }
            } else if (args.length == 2) {
                OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                if (args[1].equalsIgnoreCase("reset")) {
                    CurrencyAPI.resetCurrencyOfPlayer(t);
                    if (t.isOnline()) {
                        for (String msg : msgResetPlayer) {
                            t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                        for (String msg : msgShowMoneyPlayer) {
                            t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(CurrencyAPI.getCurrencyOfPlayer(t))));
                        }
                    }
                    for (String msg : msgReset) {
                        sender.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t)));
                    }
                } else {
                    for (String value : usageWP) {
                        sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            } else if (args.length == 3) {
                OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                double amount = getDoubleOfString(sender, args[2]);
                if (!(amount < 0)) {
                    if (args[1].equalsIgnoreCase("add")) {
                        CurrencyAPI.addCurrencyToPlayer(t, amount);
                        if (t.isOnline()) {
                            for (String msg : msgAddPlayer) {
                                t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(amount)));
                            }
                            for (String msg : msgShowMoneyPlayer) {
                                t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(CurrencyAPI.getCurrencyOfPlayer(t))));
                            }
                        }
                        for (String msg : msgAdd) {
                            sender.sendMessage(msg.replace("{Player}", NewSystem.getName(t)).replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(amount)));
                        }
                    } else if (args[1].equalsIgnoreCase("remove")) {
                        CurrencyAPI.removeCurrencyOfPlayer(t, amount);
                        if (t.isOnline()) {
                            for (String msg : msgRemovePlayer) {
                                t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(amount)));
                            }
                            for (String msg : msgShowMoneyPlayer) {
                                t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(CurrencyAPI.getCurrencyOfPlayer(t))));
                            }
                        }
                        for (String msg : msgRemove) {
                            sender.sendMessage(msg.replace("{Player}", NewSystem.getName(t)).replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(amount)));
                        }
                    } else if (args[1].equalsIgnoreCase("set")) {
                        CurrencyAPI.setCurrencyOfPlayer(t, amount);
                        if (t.isOnline()) {
                            for (String msg : msgSetPlayer) {
                                t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(amount)));
                            }
                            for (String msg : msgShowMoneyPlayer) {
                                t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(CurrencyAPI.getCurrencyOfPlayer(t))));
                            }
                        }
                        for (String msg : msgSet) {
                            sender.sendMessage(msg.replace("{Player}", NewSystem.getName(t)).replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(amount)));
                        }
                    } else if (args[1].equalsIgnoreCase("multiply")) {
                        CurrencyAPI.multipleCurrencyOfPlayer(t, amount);
                        if (t.isOnline()) {
                            for (String msg : msgMultiplyPlayer) {
                                t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Faktor}", CurrencyAPI.getCurrencyString(amount)));
                            }
                            for (String msg : msgShowMoneyPlayer) {
                                t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(CurrencyAPI.getCurrencyOfPlayer(t))));
                            }
                        }
                        for (String msg : msgMultiply) {
                            sender.sendMessage(msg.replace("{Player}", NewSystem.getName(t)).replace("{Prefix}", SettingsFile.getPrefix()).replace("{Faktor}", CurrencyAPI.getCurrencyString(amount)));
                        }
                    } else if (args[1].equalsIgnoreCase("divide")) {
                        CurrencyAPI.divideCurrencyOfPlayer(t, amount);
                        if (t.isOnline()) {
                            for (String msg : msgDividePlayer) {
                                t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Dividend}", CurrencyAPI.getCurrencyString(amount)));
                            }
                            for (String msg : msgShowMoneyPlayer) {
                                t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(CurrencyAPI.getCurrencyOfPlayer(t))));
                            }
                        }
                        for (String msg : msgDivide) {
                            sender.sendMessage(msg.replace("{Player}", NewSystem.getName(t)).replace("{Prefix}", SettingsFile.getPrefix()).replace("{Dividend}", CurrencyAPI.getCurrencyString(amount)));
                        }
                    }
                } else {
                    for (String value : msgNegative) {
                        sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            } else {
                for (String value : usageWP) {
                    sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }
        return false;
    }

    private static void sendTopList(Player p) {
        List<OfflinePlayer> topList = CurrencyAPI.getTopCurrency();

        for(String msg : topListMessage) {
            if(msg.equalsIgnoreCase("{TopListFormat}")) {
                for(int i = 0; i < topListSize; i++) {
                    if(topList.get(i) != null) {
                        OfflinePlayer t = topList.get(i);
                        String amount = CurrencyAPI.getCurrencyOfPlayerString(t);
                        if (i == 0) {
                            p.sendMessage(format.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Placing}", first).replace("{Player}", NewSystem.getName(t)).replace("{Amount}", amount));
                        }else if (i == 1) {
                            p.sendMessage(format.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Placing}", second).replace("{Player}", NewSystem.getName(t)).replace("{Amount}", amount));
                        }else if (i == 2) {
                            p.sendMessage(format.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Placing}", third).replace("{Player}", NewSystem.getName(t)).replace("{Amount}", amount));
                        }else {
                            p.sendMessage(format.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Placing}", otherPlacing.replace("{Placing}", String.valueOf(i+1))).replace("{Player}", NewSystem.getName(t)).replace("{Amount}", amount));
                        }
                    }else{
                        if (i == 0) {
                            p.sendMessage(format.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Placing}", first).replace("{Player}", noPlayer).replace("{Amount}", String.valueOf(noMoney)));
                        }else if (i == 1) {
                            p.sendMessage(format.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Placing}", second).replace("{Player}", noPlayer).replace("{Amount}", String.valueOf(noMoney)));
                        }else if (i == 2) {
                            p.sendMessage(format.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Placing}", third).replace("{Player}", noPlayer).replace("{Amount}", String.valueOf(noMoney)));
                        }else {
                            p.sendMessage(format.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Placing}", otherPlacing.replace("{Placing}", String.valueOf(i+1))).replace("{Player}", noPlayer).replace("{Amount}", String.valueOf(noMoney)));
                        }
                    }
                }
            }else{
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{AllMoneyCount}", String.valueOf(CurrencyAPI.getAllMoneyCount())).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()));
            }
        }
    }

    private static void sendTopList(CommandSender p) {
        List<OfflinePlayer> topList = CurrencyAPI.getTopCurrency();

        for(String msg : topListMessage) {
            if(msg.equalsIgnoreCase("{TopListFormat}")) {
                for(int i = 0; i < topListSize; i++) {
                    if(topList.get(i) != null) {
                        OfflinePlayer t = topList.get(i);
                        String amount = CurrencyAPI.getCurrencyOfPlayerString(t);
                        if (i == 0) {
                            p.sendMessage(format.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Placing}", first).replace("{Player}", NewSystem.getName(t)).replace("{Amount}", amount));
                        }else if (i == 1) {
                            p.sendMessage(format.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Placing}", second).replace("{Player}", NewSystem.getName(t)).replace("{Amount}", amount));
                        }else if (i == 2) {
                            p.sendMessage(format.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Placing}", third).replace("{Player}", NewSystem.getName(t)).replace("{Amount}", amount));
                        }else {
                            p.sendMessage(format.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Placing}", otherPlacing.replace("{Placing}", String.valueOf(i+1))).replace("{Player}", NewSystem.getName(t)).replace("{Amount}", amount));
                        }
                    }else{
                        if (i == 0) {
                            p.sendMessage(format.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Placing}", first).replace("{Player}", noPlayer).replace("{Amount}", String.valueOf(noMoney)));
                        }else if (i == 1) {
                            p.sendMessage(format.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Placing}", second).replace("{Player}", noPlayer).replace("{Amount}", String.valueOf(noMoney)));
                        }else if (i == 2) {
                            p.sendMessage(format.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Placing}", third).replace("{Player}", noPlayer).replace("{Amount}", String.valueOf(noMoney)));
                        }else {
                            p.sendMessage(format.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Placing}", otherPlacing.replace("{Placing}", String.valueOf(i+1))).replace("{Player}", noPlayer).replace("{Amount}", String.valueOf(noMoney)));
                        }
                    }
                }
            }else{
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{AllMoneyCount}", String.valueOf(CurrencyAPI.getAllMoneyCount())).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()));
            }
        }
    }

    private double getDoubleOfString(Player p, String s) {
        try {
            return Double.parseDouble(s);
        }catch (NumberFormatException e) {
            p.sendMessage(SettingsFile.getError().replace("{Error}", "Please use a number for amount"));
        }
        return 0D;
    }

    private double getDoubleOfString(CommandSender p, String s) {
        try {
            return Double.parseDouble(s);
        }catch (NumberFormatException e) {
            p.sendMessage(SettingsFile.getError().replace("{Error}", "Please use a number for amount"));
        }
        return 0D;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        List<String> tabCompletions = new ArrayList<>();

        if(sender instanceof Player) {
            Player p = (Player) sender;
            if (NewSystem.hasPermission(p, perm)) {
                if (args.length == 1) {
                    if(NewSystem.hasPermission(p, permTopList)) {
                        String[] completions = {"top"};
                        for(String completion : completions) {
                            if(completion.contains(args[0])) {
                                tabCompletions.add(completion);
                            }
                        }
                    }
                    if(NewSystem.hasPermission(p, permOther)) {
                        for(Player all : Bukkit.getOnlinePlayers()) {
                            if(all.getName().contains(args[0])) {
                                tabCompletions.add(all.getName());
                            }
                        }
                    }else{
                        tabCompletions.add(p.getName());
                    }
                }else if(args.length == 2) {
                    if(args[0].equalsIgnoreCase("top")) {
                        String[] completions = {"reset", "add", "remove", "set", "multiply", "divide"};
                        for(String completion : completions) {
                            if(completion.contains(args[1])) {
                                tabCompletions.add(completion);
                            }
                        }
                    }
                }
            }
        }else {
            if (args.length == 1) {
                String[] completions = {"top"};
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
                if (args[0].equalsIgnoreCase("top")) {
                    String[] completions = {"reset", "add", "remove", "set", "multiply", "divide"};
                    for(String completion : completions) {
                        if(completion.contains(args[1])) {
                            tabCompletions.add(completion);
                        }
                    }
                }
            }
        }
        return tabCompletions;
    }
}
