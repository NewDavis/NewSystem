package me.newdavis.spigot.command;
//Plugin by NewDavis

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.file.SavingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import me.newdavis.spigot.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RepairCmd implements CommandExecutor {

    private static List<String> usage;
    private static String perm;
    private static String permArmor;
    private static String permAll;
    private static String permOther;
    private static String noDelayPerm;
    private static int coolDownInTicks;
    private static List<String> alreadyRepairedPlayer;
    private static List<String> alreadyRepaired;
    private static List<String> canNotRepaired;
    private static List<String> coolDownMessage;
    private static List<String> msg;
    private static List<String> msgP;
    private static List<String> alreadyRepairedArmorPlayer;
    private static List<String> alreadyRepairedArmor;
    private static List<String> msgArmor;
    private static List<String> msgPArmor;
    private static List<String> alreadyRepairedAllPlayer;
    private static List<String> alreadyRepairedAll;
    private static List<String> msgAll;
    private static List<String> msgPAll;

    public void init() {
        usage = CommandFile.getStringListPath("Command.Repair.Usage");
        perm = CommandFile.getStringPath("Command.Repair.Permission.Use");
        permArmor = CommandFile.getStringPath("Command.Repair.Permission.Armor");
        permAll = CommandFile.getStringPath("Command.Repair.Permission.All");
        permOther = CommandFile.getStringPath("Command.Repair.Permission.Other");
        noDelayPerm = CommandFile.getStringPath("Command.Repair.Permission.NoDelay");
        coolDownInTicks = CommandFile.getIntegerPath("Command.Repair.CoolDownInTicks");
        alreadyRepairedPlayer = CommandFile.getStringListPath("Command.Repair.MessageAlreadyRepairedPlayer");
        alreadyRepaired = CommandFile.getStringListPath("Command.Repair.MessageAlreadyRepaired");
        canNotRepaired = CommandFile.getStringListPath("Command.Repair.ItemCanNotRepaired");
        coolDownMessage = CommandFile.getStringListPath("Command.Repair.MessageCoolDown");
        msg = CommandFile.getStringListPath("Command.Repair.Message");
        msgP = CommandFile.getStringListPath("Command.Repair.MessagePlayer");
        alreadyRepairedArmorPlayer = CommandFile.getStringListPath("Command.Repair.MessageAlreadyRepairedArmorPlayer");
        alreadyRepairedArmor = CommandFile.getStringListPath("Command.Repair.MessageAlreadyRepairedArmor");
        msgArmor = CommandFile.getStringListPath("Command.Repair.MessageArmor");
        msgPArmor = CommandFile.getStringListPath("Command.Repair.MessageArmorPlayer");
        alreadyRepairedAllPlayer = CommandFile.getStringListPath("Command.Repair.MessageAlreadyRepairedAllPlayer");
        alreadyRepairedAll = CommandFile.getStringListPath("Command.Repair.MessageAlreadyRepairedAll");
        msgAll = CommandFile.getStringListPath("Command.Repair.MessageAll");
        msgPAll = CommandFile.getStringListPath("Command.Repair.MessageAllPlayer");
        NewSystem.getInstance().getCommand("repair").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if(args.length == 0) {
                    repairItem(p, p);
                }else if(args.length == 1) {
                    if (args[0].equalsIgnoreCase("armor")) {
                        if(NewSystem.hasPermission(p, permArmor)) {
                            repairArmor(p, p);
                        }else{
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    }else if (args[0].equalsIgnoreCase("all")) {
                        if(NewSystem.hasPermission(p, permAll)) {
                            repairAll(p, p);
                        }else{
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    }else{
                        if(NewSystem.hasPermission(p, permOther)) {
                            Player t = Bukkit.getPlayer(args[0]);
                            if(t != null) {
                                repairItem(p, t);
                            }else{
                                p.sendMessage(SettingsFile.getOffline());
                            }
                        }else{
                            for(String value : usage) {
                                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        }
                    }
                }else if(args.length == 2) {
                    if(NewSystem.hasPermission(p, permOther)) {
                        Player t = Bukkit.getPlayer(args[0]);
                        if(t != null) {
                            if(args[1].equalsIgnoreCase("armor")) {
                                if(NewSystem.hasPermission(p, permArmor)) {
                                    repairArmor(p, t);
                                }else{
                                    p.sendMessage(SettingsFile.getNoPerm());
                                }
                            }else if(args[1].equalsIgnoreCase("all")) {
                                if(NewSystem.hasPermission(p, permAll)) {
                                    repairAll(p, t);
                                }else{
                                    p.sendMessage(SettingsFile.getNoPerm());
                                }
                            }else{
                                for(String value : usage) {
                                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                            }
                        }else{
                            p.sendMessage(SettingsFile.getOffline());
                        }
                    }else{
                        for(String value : usage) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
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

    public static boolean canRepair(Player p) {
        if(!NewSystem.hasPermission(p, noDelayPerm)) {
            List<String> repairCoolDownList = SavingsFile.getStringListPath("Repair.List");
            if (repairCoolDownList.contains(p.getUniqueId().toString())) {
                long coolDownEnds = SavingsFile.getLongPath("Repair." + p.getUniqueId() + ".End");
                long now = System.currentTimeMillis();

                if ((coolDownEnds + coolDownInTicks) - now <= 0) {
                    SavingsFile.setPath("Repair." + p.getUniqueId(), null);
                    repairCoolDownList.remove(p.getUniqueId().toString());
                    SavingsFile.setPath("Repair.List", repairCoolDownList);
                }else{
                    return false;
                }
            }
        }
        return true;
    }

    public static void repairItem(Player p, Player t) {
        if(canRepair(p)) {
            ItemStack item = ItemBuilder.getItemInHand(t);
            if(!ItemBuilder.getNameOfMaterial(ItemBuilder.getMaterialOfItemStack(item)).equalsIgnoreCase("AIR")) {
                if (p == t) {
                    if (item.getDurability() != 0) {
                        item.setDurability((short) 0);
                        for(String key : msgP) {
                            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    } else {
                        for(String value : alreadyRepairedPlayer) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                        return;
                    }
                } else {
                    if (item.getDurability() != 0) {
                        item.setDurability((short) 0);
                        for(String key : msg) {
                            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                        }
                        for(String key : msgP) {
                            t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    } else {
                        for(String key : alreadyRepaired) {
                            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                        }
                        return;
                    }
                }

                if (!NewSystem.hasPermission(p, noDelayPerm)) {
                    List<String> repairCoolDownList = SavingsFile.getStringListPath("Repair.List");
                    repairCoolDownList.add(p.getUniqueId().toString());
                    SavingsFile.setPath("Repair.List", repairCoolDownList);
                    SavingsFile.setPath("Repair." + p.getUniqueId() + ".End", System.currentTimeMillis());
                }
            }else{
                for(String value : canNotRepaired) {
                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }else{
            long coolDownEnds = SavingsFile.getLongPath("Repair." + p.getUniqueId() + ".End");
            long now = System.currentTimeMillis();
            int rest = (int) ((coolDownEnds + coolDownInTicks) - now);

            int days = rest/1000/60/60/24;
            rest = rest-(days*1000*60*60*24);
            int hours = (rest/1000/60/60);
            rest = rest-(hours*1000*60*60);
            int minutes = (rest/1000/60);
            rest = rest-(minutes*1000*60);
            int seconds = (rest/1000);

            for(String key : coolDownMessage) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix())
                        .replace("{Days}", String.valueOf(days))
                        .replace("{Hours}", String.valueOf(hours))
                        .replace("{Minutes}", String.valueOf(minutes))
                        .replace("{Seconds}", String.valueOf(seconds)));
            }
        }
    }

    public static void repairArmor(Player p, Player t) {
        if(canRepair(p)) {
            ItemStack[] armorItems = ItemBuilder.getArmor(t);;
            ArrayList<Integer> repairedItems = new ArrayList<>();
            for(ItemStack item : armorItems) {
                if(item != null) {
                    if (!ItemBuilder.getNameOfMaterial(ItemBuilder.getMaterialOfItemStack(item)).equalsIgnoreCase("AIR")) {
                        if (item.getDurability() != 0) {
                            item.setDurability((short) 0);
                        } else {
                            repairedItems.add(1);
                        }
                    }else{
                        repairedItems.add(1);
                    }
                }else{
                    repairedItems.add(1);
                }
            }
            if(repairedItems.size() == armorItems.length) {
                if(p == t) {
                    for(String key : alreadyRepairedArmorPlayer) {
                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                    return;
                }else{
                    for(String key : alreadyRepairedArmor) {
                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                    }
                    return;
                }
            }

            if(p == t) {
                for(String key : msgPArmor) {
                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }else{
                for(String key : msgArmor) {
                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                }
                for(String key : msgPArmor) {
                    t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }

            if(!NewSystem.hasPermission(p, noDelayPerm)) {
                List<String> repairCoolDownList = SavingsFile.getStringListPath("Repair.List");
                repairCoolDownList.add(p.getUniqueId().toString());
                SavingsFile.setPath("Repair.List", repairCoolDownList);
                SavingsFile.setPath("Repair." + p.getUniqueId() + ".End", System.currentTimeMillis());
            }
        }else{
            long coolDownEnds = SavingsFile.getLongPath("Repair." + p.getUniqueId() + ".End");
            long now = System.currentTimeMillis();
            int rest = (int) ((coolDownEnds + coolDownInTicks) - now);

            int days = rest/1000/60/60/24;
            rest = rest-(days*1000*60*60*24);
            int hours = (rest/1000/60/60);
            rest = rest-(hours*1000*60*60);
            int minutes = (rest/1000/60);
            rest = rest-(minutes*1000*60);
            int seconds = (rest/1000);

            for(String key : coolDownMessage) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix())
                        .replace("{Days}", String.valueOf(days))
                        .replace("{Hours}", String.valueOf(hours))
                        .replace("{Minutes}", String.valueOf(minutes))
                        .replace("{Seconds}", String.valueOf(seconds)));
            }
        }
    }

    public static void repairAll(Player p, Player t) {
        if(canRepair(p)) {
            ItemStack[] items = ItemBuilder.getInventory(t);
            ArrayList<Integer> repairedItems = new ArrayList<>();
            for(ItemStack item : items) {
                if(item != null) {
                    if (!ItemBuilder.getNameOfMaterial(ItemBuilder.getMaterialOfItemStack(item)).equalsIgnoreCase("AIR")) {
                        if (item.getDurability() != 0) {
                            item.setDurability((short) 0);
                        } else {
                            repairedItems.add(1);
                        }
                    }else{
                        repairedItems.add(1);
                    }
                }else{
                    repairedItems.add(1);
                }
            }
            if(repairedItems.size() == items.length) {
                if(p == t) {
                    for(String key : alreadyRepairedAllPlayer) {
                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix())
                                .replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                    }
                    return;
                }else{
                    for(String key : alreadyRepairedAll) {
                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                    return;
                }
            }

            if(p == t) {
                for(String key : msgPAll) {
                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }else{
                for(String key : msgAll) {
                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                }
                for(String key : msgPAll) {
                    t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }

            if(!NewSystem.hasPermission(p, noDelayPerm)) {
                List<String> repairCoolDownList = SavingsFile.getStringListPath("Repair.List");
                repairCoolDownList.add(p.getUniqueId().toString());
                SavingsFile.setPath("Repair.List", repairCoolDownList);
                SavingsFile.setPath("Repair." + p.getUniqueId() + ".End", System.currentTimeMillis());
            }
        }else{
            long coolDownEnds = SavingsFile.getLongPath("Repair." + p.getUniqueId() + ".End");
            long now = System.currentTimeMillis();
            int rest = (int) ((coolDownEnds + coolDownInTicks) - now);

            int days = rest/1000/60/60/24;
            rest = rest-(days*1000*60*60*24);
            int hours = (rest/1000/60/60);
            rest = rest-(hours*1000*60*60);
            int minutes = (rest/1000/60);
            rest = rest-(minutes*1000*60);
            int seconds = (rest/1000);

            for(String key : coolDownMessage) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix())
                        .replace("{Days}", String.valueOf(days))
                        .replace("{Hours}", String.valueOf(hours))
                        .replace("{Minutes}", String.valueOf(minutes))
                        .replace("{Seconds}", String.valueOf(seconds)));
            }
        }
    }
}
