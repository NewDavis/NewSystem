package me.newdavis.spigot.command;

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.api.CurrencyAPI;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class PayCmd implements CommandExecutor {

    private static List<String> usage;
    private static String perm;
    private static String permEveryone;
    private static List<String> noMoney;
    private static List<String> paySelf;
    private static List<String> pay;
    private static List<String> payPlayer;
    private static List<String> payEveryone;

    public void init() {
        usage = CommandFile.getStringListPath("Command.Currency.UsagePay");
        perm = CommandFile.getStringPath("Command.Currency.Permission.Pay");
        permEveryone = CommandFile.getStringPath("Command.Currency.Permission.PayEveryone");
        noMoney = CommandFile.getStringListPath("Command.Currency.MessageNotEnoughMoney");
        paySelf = CommandFile.getStringListPath("Command.Currency.MessagePaySelf");
        pay = CommandFile.getStringListPath("Command.Currency.MessagePay");
        payPlayer = CommandFile.getStringListPath("Command.Currency.MessagePayPlayer");
        payEveryone = CommandFile.getStringListPath("Command.Currency.MessagePayEveryone");
        NewSystem.getInstance().getCommand("pay").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if(args.length == 2) {
                    double amount = Double.parseDouble(args[1]);
                    if(args[0].equalsIgnoreCase("*")) {
                        if(NewSystem.hasPermission(p, permEveryone)) {
                            if(CurrencyAPI.payCurrencyToEveryone(p, amount)) {
                                for(String msg : payEveryone) {
                                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(amount)).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()));
                                }
                                for (Player all : Bukkit.getOnlinePlayers()) {
                                    if(all != p) {
                                        for(String msg : payPlayer) {
                                            all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(amount)).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Player}", NewSystem.getName(p, false)).replace("{DisplayName}", NewSystem.getName(p, true)));
                                        }
                                    }
                                }
                            }else{
                                for(String value : noMoney) {
                                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                            }
                        }else{
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    }else{
                        Player t = Bukkit.getPlayer(args[0]);
                        if(t != null) {
                            if(p != t) {
                                if (CurrencyAPI.payCurrencyToPlayer(p, t, amount)) {
                                    for(String msg : pay) {
                                        p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(amount)).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                                    }
                                    for(String msg : payPlayer) {
                                        t.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Amount}", CurrencyAPI.getCurrencyString(amount)).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()).replace("{Player}", NewSystem.getName(p, false)).replace("{DisplayName}", NewSystem.getName(p, true)));
                                    }
                                } else {
                                    for(String value : noMoney) {
                                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                                    }
                                }
                            }else{
                                for(String value : paySelf) {
                                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                            }
                        }else{
                            p.sendMessage(SettingsFile.getOffline());
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
            sender.sendMessage(SettingsFile.getOnlyPlayerCanExecute());
        }
        return false;
    }
}
