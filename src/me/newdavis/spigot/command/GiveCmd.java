package me.newdavis.spigot.command;

import me.newdavis.spigot.api.ReflectionAPI;
import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import me.newdavis.spigot.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GiveCmd implements CommandExecutor, TabCompleter {

    private static List<String> usage;
    private static String perm;
    private static String permOther;
    private static List<String> msg = CommandFile.getStringListPath("Command.Give.Message");
    private static List<String> msgP = CommandFile.getStringListPath("Command.Give.MessagePlayer");

    public GiveCmd() {
        usage = CommandFile.getStringListPath("Command.Give.Usage");
        perm = CommandFile.getStringPath("Command.Give.Permission.Use");
        permOther = CommandFile.getStringPath("Command.Give.Permission.Other");
        msg = CommandFile.getStringListPath("Command.Give.Message");
        msgP = CommandFile.getStringListPath("Command.Give.MessagePlayer");
        if(!NewSystem.loadedCommands.contains(this)) {
            NewSystem.loadedCommands.add(this);
            NewSystem.getInstance().getCommand("give").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if(args.length == 1) {
                    short damage = 0;
                    String material = Material.AIR.name();

                    try {
                        if(args[0].contains(":")) {
                            damage = Short.parseShort(args[0].split(":")[1]);
                            material = args[0].split(":")[0];
                        }else {
                            material = args[0];
                        }
                    } catch (NumberFormatException ignored) {
                    }

                    getItem(p, p, 1, material.toUpperCase(), damage);
                }else if(args.length == 2) {
                    if (NewSystem.hasPermission(p, permOther)) {
                        Player t = Bukkit.getPlayer(args[1]);
                        if (t != null) {
                            short damage = 0;
                            String material = Material.AIR.name();

                            try {
                                if(args[0].contains(":")) {
                                    damage = Short.parseShort(args[0].split(":")[1]);
                                    material = args[0].split(":")[0];
                                }else {
                                    material = args[0];
                                }
                            } catch (NumberFormatException ignored) {
                            }

                            getItem(p, t, 1, material.toUpperCase(), damage);
                        } else {
                            int amount = 1;
                            String material = Material.AIR.name();
                            short damage = 0;

                            try {
                                if(args[0].contains(":")) {
                                    damage = Short.parseShort(args[0].split(":")[1]);
                                    material = args[0].split(":")[0];
                                }else {
                                    amount = Integer.parseInt(args[1]);
                                    material = args[0];
                                }
                            } catch (NumberFormatException ignored) {
                            }

                            getItem(p, p, amount, material.toUpperCase(), damage);
                        }
                    } else {
                        p.sendMessage(SettingsFile.getNoPerm());
                    }
                }else if(args.length == 3) {
                    if (NewSystem.hasPermission(p, permOther)) {
                        Player t = Bukkit.getPlayer(args[2]);
                        if (t != null) {
                            int amount = 1;
                            String material = Material.AIR.name();
                            short damage = 0;

                            try {
                                if(args[0].contains(":")) {
                                    damage = Short.parseShort(args[0].split(":")[1]);
                                    amount = Integer.parseInt(args[1]);
                                    material = args[0].split(":")[0];
                                }else {
                                    amount = Integer.parseInt(args[1]);
                                    material = args[0];
                                }
                            } catch (NumberFormatException ignored) {
                            }

                            getItem(p, t, amount, material.toUpperCase(), damage);
                        } else {
                            p.sendMessage(SettingsFile.getOffline());
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
                p.sendMessage(SettingsFile.getNoPerm());
            }
        }else{
            if(args.length == 2) {
                Player t = Bukkit.getPlayer(args[1]);
                if (t != null) {
                    short damage = 0;
                    String material = Material.AIR.name();

                    try {
                        if (args[0].contains(":")) {
                            damage = Short.parseShort(args[0].split(":")[1]);
                            material = args[0].split(":")[0];
                        } else {
                            material = args[0];
                        }
                    } catch (NumberFormatException ignored) {
                    }

                    getItem(sender, t, 1, material.toUpperCase(), damage);
                } else {
                    sender.sendMessage(SettingsFile.getOnlyPlayerCanExecute());
                }
            }else if(args.length == 3) {
                Player t = Bukkit.getPlayer(args[2]);
                if (t != null) {
                    int amount = 1;
                    String material = Material.AIR.name();
                    short damage = 0;

                    try {
                        if (args[0].contains(":")) {
                            damage = Short.parseShort(args[0].split(":")[1]);
                            amount = Integer.parseInt(args[1]);
                            material = args[0].split(":")[0];
                        } else {
                            amount = Integer.parseInt(args[1]);
                            material = args[0];
                        }
                    } catch (NumberFormatException ignored) {
                    }

                    getItem(sender, t, amount, material.toUpperCase(), damage);
                } else {
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

    private static void getItem(Player p, Player t, int amount, String item, short damage) {
        Material material = Material.AIR;
        if (!isNumber(item)) {
            for (String mat : ItemBuilder.serverMaterials.keySet()) {
                if (mat.equalsIgnoreCase(item) || ItemBuilder.getMaterial(item) != null && mat.equalsIgnoreCase(ItemBuilder.getNameOfMaterial(ItemBuilder.getMaterial(item)))) {
                    material = ItemBuilder.getMaterial(mat);
                    break;
                }
            }
        } else {
            if (ReflectionAPI.VERSION_ID <= 12) {
                try {
                    int matID = Integer.parseInt(item);
                    if (ItemBuilder.getMaterial(matID) != Material.AIR) {
                        material = ItemBuilder.getMaterial(matID);
                    }
                } catch (NumberFormatException ignored) {
                    p.sendMessage(SettingsFile.getError().replace("{Error}", "This Item does not exist"));
                    return;
                }
            } else {
                p.sendMessage(SettingsFile.getError().replace("{Error}", "This Item does not exist"));
                return;
            }
        }

        if(material == Material.AIR) {
            p.sendMessage(SettingsFile.getError().replace("{Error}", "This Item does not exist"));
            return;
        }

        ItemStack itemStack = new ItemStack(material, amount, damage);
        t.getInventory().addItem(itemStack);
        String subID = String.valueOf(damage);
        for (String key : msgP) {
            t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Item}", ItemBuilder.getNameOfMaterial(material)).replace("{SubID}", subID).replace("{Amount}", String.valueOf(amount)));
        }

        if (p != t) {
            for (String key : msg) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{SubID}", subID).replace("{Item}", ItemBuilder.getNameOfMaterial(material)).replace("{Amount}", String.valueOf(amount)));
            }
            for (String key : msgP) {
                t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Item}", ItemBuilder.getNameOfMaterial(material)).replace("{SubID}", subID).replace("{Amount}", String.valueOf(amount)).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
            }
        }
    }

    private static void getItem(CommandSender p, Player t, int amount, String item, short damage) {
        Material material = Material.AIR;
        if (!isNumber(item)) {
            for (String mat : ItemBuilder.serverMaterials.keySet()) {
                if (mat.equalsIgnoreCase(item) || ItemBuilder.getMaterial(item) != null && mat.equalsIgnoreCase(ItemBuilder.getNameOfMaterial(ItemBuilder.getMaterial(item)))) {
                    material = ItemBuilder.getMaterial(mat);
                    break;
                }
            }
        } else {
            if (ReflectionAPI.VERSION_ID == 8) {
                try {
                    int matID = Integer.parseInt(item);
                    if (ItemBuilder.getMaterial(matID) != null) {
                        material = ItemBuilder.getMaterial(matID);
                    }
                } catch (NumberFormatException ignored) {
                    p.sendMessage(SettingsFile.getError().replace("{Error}", "This Item does not exist"));
                    return;
                }
            } else {
                p.sendMessage(SettingsFile.getError().replace("{Error}", "This Item does not exist"));
                return;
            }
        }

        if(material == Material.AIR) {
            return;
        }

        ItemStack itemStack = new ItemStack(material, amount, damage);
        String subID = String.valueOf(damage);
        t.getInventory().addItem(itemStack);
        for (String key : msgP) {
            t.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Item}", ItemBuilder.getNameOfMaterial(material)).replace("{SubID}", subID).replace("{Amount}", String.valueOf(amount)));
        }

        for (String key : msg) {
            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{SubID}", subID).replace("{Item}", ItemBuilder.getNameOfMaterial(material)).replace("{Amount}", String.valueOf(amount)));
        }
    }

    private static boolean isNumber(String s) {
        try{
            Integer.parseInt(s);
            return true;
        }catch (NumberFormatException ignored) {
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        List<String> tabCompletions = new ArrayList<>();

        if(sender instanceof Player) {
            Player p = (Player) sender;
            if (NewSystem.hasPermission(p, perm)) {
                if (args.length == 1) {
                    for(String mat : ItemBuilder.serverMaterials.keySet()) {
                        if(mat.contains(args[0])) {
                            tabCompletions.add(mat);
                        }
                    }
                }else if(args.length == 2 || args.length == 3) {
                    if (NewSystem.hasPermission(p, permOther)) {
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
                for(String mat : ItemBuilder.serverMaterials.keySet()) {
                    if(mat.contains(args[0])) {
                        tabCompletions.add(mat);
                    }
                }
            }else if(args.length == 2 || args.length == 3) {
                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (all.getName().contains(args[1])) {
                        tabCompletions.add(all.getName());
                    }
                }
            }
        }
        return tabCompletions;
    }
}
