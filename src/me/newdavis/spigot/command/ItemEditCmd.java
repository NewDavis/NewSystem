package me.newdavis.spigot.command;
//Plugin by NewDavis

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import me.newdavis.spigot.util.ItemBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemEditCmd implements CommandExecutor, TabCompleter {

    private static List<String> usage;
    private static String perm;
    private static String permRename;
    private static String permLore;
    private static String permEnchant;
    private static String permSign;
    private static List<String> msgRenamed;
    private static String splitBy;
    private static List<String> msgLoreSet;
    private static List<String> msgEnchanted;
    private static List<String> msgSigned;
    private static List<String> msgAir;
    private static List<String> format;

    public void init() {
        usage = CommandFile.getStringListPath("Command.ItemEdit.Usage");
        perm = CommandFile.getStringPath("Command.ItemEdit.Permission.Use");
        permRename = CommandFile.getStringPath("Command.ItemEdit.Permission.Rename");
        permLore = CommandFile.getStringPath("Command.ItemEdit.Permission.Lore");
        permEnchant = CommandFile.getStringPath("Command.ItemEdit.Permission.Enchant");
        permSign = CommandFile.getStringPath("Command.ItemEdit.Permission.Sign");
        msgRenamed = CommandFile.getStringListPath("Command.ItemEdit.MessageRenamed");
        splitBy = CommandFile.getStringPath("Command.ItemEdit.LoreSplitBy");
        msgLoreSet = CommandFile.getStringListPath("Command.ItemEdit.MessageLoreSet");
        msgEnchanted = CommandFile.getStringListPath("Command.ItemEdit.MessageEnchanted");
        msgSigned = CommandFile.getStringListPath("Command.ItemEdit.MessageSigned");
        msgAir = CommandFile.getStringListPath("Command.ItemEdit.MessageItemIsAir");
        format = CommandFile.getStringListPath("Command.ItemEdit.SignFormat");
        NewSystem.getInstance().getCommand("itemedit").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if(args.length == 0 || args.length == 1) {
                    for(String value : usage) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }else{
                    if(args[0].equalsIgnoreCase("Rename")) {
                        if(NewSystem.hasPermission(p, permRename)) {
                            ItemStack item = ItemBuilder.getItemInHand(p);
                            if(!ItemBuilder.getNameOfMaterial(ItemBuilder.getMaterialOfItemStack(item)).equalsIgnoreCase("AIR")) {
                                String message = "";
                                for (int i = 1; i < args.length; i++) {
                                    if (i == 1) {
                                        message = args[i];
                                    } else {
                                        message = message + " " + args[i];
                                    }
                                }
                                renameItem(p, message.replace("&", "§"));
                                for(String key : msgRenamed) {
                                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Name}", message.replace("&", "§")));
                                }
                            }else{
                                for(String value : msgAir) {
                                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                            }
                        }else{
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    }else if(args[0].equalsIgnoreCase("Lore")) {
                        if(NewSystem.hasPermission(p, permLore)) {
                            ItemStack item = ItemBuilder.getItemInHand(p);
                            if(!ItemBuilder.getNameOfMaterial(ItemBuilder.getMaterialOfItemStack(item)).equalsIgnoreCase("AIR")) {
                                List<String> lore = new ArrayList<>();
                                String[] lSplitted = args[1].split(splitBy);
                                for(String l : lSplitted) {
                                    lore.add(l);
                                }

                                loreItem(p, lore);
                                for(String key : msgLoreSet) {
                                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Lore}", String.valueOf(lore).replace("[", "").replace("]", "")));
                                }
                            }else{
                                for(String value : msgAir) {
                                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                            }
                        }else{
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    }else if(args[0].equalsIgnoreCase("Enchant")) {
                        if(NewSystem.hasPermission(p, permEnchant)) {
                            ItemStack item = ItemBuilder.getItemInHand(p);
                            if(!ItemBuilder.getNameOfMaterial(ItemBuilder.getMaterialOfItemStack(item)).equalsIgnoreCase("AIR")) {
                                if(args.length == 2) {
                                    Enchantment enchantment = Enchantment.getByName(args[1]);
                                    if(enchantment == null) {
                                        enchantment = getEnchantmentByName(args[1]);
                                    }
                                    String enchantmentString = args[1];
                                    enchantItem(p, enchantmentString, enchantment, 1);
                                    for(String key : msgEnchanted) {
                                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Enchantment}", enchantmentString).replace("{Level}", "1"));
                                    }
                                }else if(args.length == 3) {
                                    Enchantment enchantment = Enchantment.getByName(args[1]);
                                    if(enchantment == null) {
                                        enchantment = getEnchantmentByName(args[1]);
                                    }
                                    String enchantmentString = args[1];
                                    int level = Integer.parseInt(args[2]);
                                    enchantItem(p, enchantmentString, enchantment, level);
                                    for(String key : msgEnchanted) {
                                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Enchantment}", enchantmentString).replace("{Level}", String.valueOf(level)));
                                    }
                                }else{
                                    for(String value : usage) {
                                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                                    }
                                }
                            }else{
                                for(String value : msgAir) {
                                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                            }
                        }else{
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    }else if(args[0].equalsIgnoreCase("Sign")) {
                        if(NewSystem.hasPermission(p, permSign)) {
                            ItemStack item = ItemBuilder.getItemInHand(p);
                            if(!ItemBuilder.getNameOfMaterial(ItemBuilder.getMaterialOfItemStack(item)).equalsIgnoreCase("AIR")) {
                                String message = "";
                                for (int i = 1; i < args.length; i++) {
                                    if (i == 1) {
                                        message = args[i];
                                    } else {
                                        message = message + " " + args[i];
                                    }
                                }
                                signItem(p, message.replace("&", "§"));
                                for(String key : msgSigned) {
                                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Sign}", message.replace("&", "§")));
                                }
                            }else{
                                for(String value : msgAir) {
                                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                            }
                        }else{
                            p.sendMessage(SettingsFile.getNoPerm());
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

    public static Enchantment getEnchantmentByName(String enchantment) {
        if(enchantment.equalsIgnoreCase("protection") || enchantment.equalsIgnoreCase("schutz") || enchantment.equalsIgnoreCase("protection_enviromental")) {
            return Enchantment.PROTECTION_ENVIRONMENTAL;
        }else if(enchantment.equalsIgnoreCase("fire_protection") || enchantment.equalsIgnoreCase("feuerschutz") || enchantment.equalsIgnoreCase("protection_fire")) {
            return Enchantment.PROTECTION_FIRE;
        }else if(enchantment.equalsIgnoreCase("feather_fall") || enchantment.equalsIgnoreCase("federfall") || enchantment.equalsIgnoreCase("protection_fall")) {
            return Enchantment.PROTECTION_FALL;
        }else if(enchantment.equalsIgnoreCase("blast_protection") || enchantment.equalsIgnoreCase("explosionsschutz") || enchantment.equalsIgnoreCase("protection_explosions")) {
            return Enchantment.PROTECTION_EXPLOSIONS;
        }else if(enchantment.equalsIgnoreCase("projectile_protection") || enchantment.equalsIgnoreCase("projektilschutz") || enchantment.equalsIgnoreCase("protection_projectile")) {
            return Enchantment.PROTECTION_PROJECTILE;
        }else if(enchantment.equalsIgnoreCase("respiration") || enchantment.equalsIgnoreCase("atmung") || enchantment.equalsIgnoreCase("oxygen")) {
            return Enchantment.OXYGEN;
        }else if(enchantment.equalsIgnoreCase("aqua_affinity") || enchantment.equalsIgnoreCase("wasseraffinität") || enchantment.equalsIgnoreCase("water_worker")) {
            return Enchantment.WATER_WORKER;
        }else if(enchantment.equalsIgnoreCase("thorns") || enchantment.equalsIgnoreCase("dornen")) {
            return Enchantment.THORNS;
        }else if(enchantment.equalsIgnoreCase("depth_strider") || enchantment.equalsIgnoreCase("wasserläufer")) {
            return Enchantment.DEPTH_STRIDER;
        }else if(enchantment.equalsIgnoreCase("sharpness") || enchantment.equalsIgnoreCase("schärfe") || enchantment.equalsIgnoreCase("damage_all")) {
            return Enchantment.DAMAGE_ALL;
        }else if(enchantment.equalsIgnoreCase("smite") || enchantment.equalsIgnoreCase("bann") || enchantment.equalsIgnoreCase("damage_undead")) {
            return Enchantment.DAMAGE_UNDEAD;
        }else if(enchantment.equalsIgnoreCase("bane_of_arthropods") || enchantment.equalsIgnoreCase("nemesis_der_gliederfüßler") || enchantment.equalsIgnoreCase("damage_ARTHROPODS")) {
            return Enchantment.DAMAGE_ARTHROPODS;
        }else if(enchantment.equalsIgnoreCase("knockback") || enchantment.equalsIgnoreCase("rückstoß")) {
            return Enchantment.KNOCKBACK;
        }else if(enchantment.equalsIgnoreCase("fire_aspect") || enchantment.equalsIgnoreCase("verbrennung")) {
            return Enchantment.FIRE_ASPECT;
        }else if(enchantment.equalsIgnoreCase("looting") || enchantment.equalsIgnoreCase("plünderung")) {
            return Enchantment.LOOT_BONUS_MOBS;
        }else if(enchantment.equalsIgnoreCase("power") || enchantment.equalsIgnoreCase("stärke") || enchantment.equalsIgnoreCase("arrow_damage")) {
            return Enchantment.ARROW_DAMAGE;
        }else if(enchantment.equalsIgnoreCase("punch") || enchantment.equalsIgnoreCase("schlag") || enchantment.equalsIgnoreCase("arrow_knockback")) {
            return Enchantment.ARROW_KNOCKBACK;
        }else if(enchantment.equalsIgnoreCase("flame") || enchantment.equalsIgnoreCase("flamme") || enchantment.equalsIgnoreCase("arrow_fire")) {
            return Enchantment.ARROW_FIRE;
        }else if(enchantment.equalsIgnoreCase("infinity") || enchantment.equalsIgnoreCase("unendlichkeit") || enchantment.equalsIgnoreCase("arrow_infinite")) {
            return Enchantment.ARROW_INFINITE;
        }else if(enchantment.equalsIgnoreCase("efficiency") || enchantment.equalsIgnoreCase("effizienz") || enchantment.equalsIgnoreCase("dig_speed")) {
            return Enchantment.DIG_SPEED;
        }else if(enchantment.equalsIgnoreCase("silk_touch") || enchantment.equalsIgnoreCase("behutsamkeit")) {
            return Enchantment.SILK_TOUCH;
        }else if(enchantment.equalsIgnoreCase("unbreaking") || enchantment.equalsIgnoreCase("haltbarkeit") || enchantment.equalsIgnoreCase("durability")) {
            return Enchantment.DURABILITY;
        }else if(enchantment.equalsIgnoreCase("fortune") || enchantment.equalsIgnoreCase("glück") || enchantment.equalsIgnoreCase("loot_bonus_blocks")) {
            return Enchantment.LOOT_BONUS_BLOCKS;
        }else if(enchantment.equalsIgnoreCase("luck_of_the_sea") || enchantment.equalsIgnoreCase("glück_des_meeres") || enchantment.equalsIgnoreCase("luck")) {
            return Enchantment.LUCK;
        }else if(enchantment.equalsIgnoreCase("lure") || enchantment.equalsIgnoreCase("köder")) {
            return Enchantment.LURE;
        }
        if(Enchantment.getByName(enchantment.toUpperCase()) != null) {
            return Enchantment.getByName(enchantment.toUpperCase());
        }
        return Enchantment.DURABILITY;
    }

    public static void renameItem(Player p, String name) {
        ItemStack item = ItemBuilder.getItemInHand(p);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
    }

    public static void loreItem(Player p, List<String> lore) {
        ItemStack item = ItemBuilder.getItemInHand(p);
        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public static void enchantItem(Player p, String enchantmentString, Enchantment enchantment, int level) {
        ItemStack item = ItemBuilder.getItemInHand(p);
        item.addUnsafeEnchantment(enchantment, level);
    }

    public static void signItem(Player p, String message) {
        ItemStack item = ItemBuilder.getItemInHand(p);
        ItemMeta meta = item.getItemMeta();

        List<String> lore = new ArrayList<>();

        for(String msg : format) {
            lore.add(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", p.getDisplayName()).replace("{Date}", getDate(System.currentTimeMillis())).replace("{Message}", message));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public static String getDate(long millis) {
        long newMillis = millis + (1000*60*60*2);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return sdf.format(newMillis);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        List<String> tabCompletions = new ArrayList<>();

        if(sender instanceof Player) {
            Player p = (Player) sender;
            if (NewSystem.hasPermission(p, perm)) {
                if (args.length == 1) {
                    String[] completions = {"rename", "lore", "sign", "enchant"};
                    for(String completion : completions) {
                        if(completion.contains(args[0])) {
                            tabCompletions.add(completion);
                        }
                    }
                }else if(args.length == 2) {
                    if(args[0].equalsIgnoreCase("enchant")) {
                        for(Enchantment all : Enchantment.values()) {
                            if(all.getName().contains(args[1])) {
                                tabCompletions.add(all.getName());
                            }
                        }
                    }
                }
            }
        }
        return tabCompletions;
    }
}
