package me.newdavis.spigot.file;
//Plugin by NewDavis

import me.newdavis.spigot.command.ItemEditCmd;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

import me.newdavis.spigot.util.ItemBuilder;

public class KitFile {

    public static File file = new File("plugins/NewSystem/Kit.yml");
    public static YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

    public static void loadConfig() {
        configurationSection.clear();
        string.clear();
        booleanSavings.clear();
        integer.clear();
        shortSavings.clear();
        longSavings.clear();
        doubleSavings.clear();
        stringList.clear();

        if(file.exists()) {
            try {
                yaml.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }else{
            saveConfig();
        }
    }

    public static void saveConfig() {
        if(file.exists()) {
            try {
                yaml.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            yaml.set("Kit.NewSystem.DelayInTicks", 0);
            yaml.set("Kit.NewSystem.Items.Item1.Type", "WRITTEN_BOOK");
            yaml.set("Kit.NewSystem.Items.Item1.Amount", 1);
            yaml.set("Kit.NewSystem.Items.Item1.DisplayName", "§3N§bew§3S§bystem");
            yaml.set("Kit.NewSystem.Items.Item1.Lore", Collections.singletonList("§7Dieses Buch wurde von §aNewDavis §7erstellt! :o"));
            yaml.set("Kit.NewSystem.Items.Item1.Enchantments.DURABILITY", 1);
            yaml.set("Kit.NewSystem.Items.Item1.BookPages", Collections.singletonList("Vielen Dank, dass du NewSystem nutzt!\n~NewDavis"));
            yaml.set("Kit.NewSystem.Items.Item1.Author", "NewDavis");
            yaml.set("Kit.10min.DelayInTicks", -1);
            yaml.set("Kit.10min.Items.Item1.Type", "PAPER");
            yaml.set("Kit.10min.Items.Item1.Amount", 1);
            yaml.set("Kit.10min.Items.Item1.DisplayName", "§e§lZertifikat für 10min Spielzeit");
            yaml.set("Kit.10min.Items.Item1.Enchantments.DURABILITY", 1);

            try {
                yaml.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Collection<String> getKitsName() {
        Collection<String> kitNames = new ArrayList<>();
        if(isPathSet("Kit")) {
            Set<String> keys = yaml.getConfigurationSection("Kit").getKeys(false);
            for(String key : keys) {
                kitNames.add(key);
            }
        }
        return kitNames;
    }

    public static boolean getKit(Player p, Player t, String kit) {
        if(isPathSet("Kit." + kit + ".Items")) {
            if(canGetKit(p, kit)) {
                Collection<ItemStack> items = getKitItems(kit);
                for (ItemStack item : items) {
                    t.getInventory().addItem(item);
                }
                SavingsFile.setPath("Kit." + kit + "." + p.getUniqueId() + ".Abgeholt", System.currentTimeMillis());
                return true;
            }
        }
        return false;
    }

    public static boolean getKit(Player t, String kit) {
        if(isPathSet("Kit." + kit + ".Items")) {
            Collection<ItemStack> items = getKitItems(kit);
            for (ItemStack item : items) {
                t.getInventory().addItem(item);
            }
            return true;
        }
        return false;
    }

    public static Collection<ItemStack> getKitItems(String kit) {
        Collection<ItemStack> kitItems = new ArrayList<>();

        //Item
        String type = "AIR";
        int typeID = 0;
        short damage = 0;
        String displayName = "";
        int amount = 1;
        List<String> lore = new ArrayList<>();
        HashMap<Enchantment, Integer> enchantments = new HashMap<>();
        boolean unbreakable = false;
        boolean hideEnchants = false;
        boolean hideUnbreakable = false;
        //WrittenBook
        List<String> writtenBookPages = new ArrayList<>();
        String author = "";
        //Leather Armor
        String color = "";
        //Skull
        String skullOwner = "";

        if(isPathSet("Kit." + kit + ".Items")) {
            Set<String> keys = yaml.getConfigurationSection("Kit." + kit + ".Items").getKeys(false);
            for(String key : keys) {
                if(isPathSet("Kit." + kit + ".Items." + key + ".Type")) {
                    try{
                        typeID = Integer.parseInt(getStringPath("Kit." + kit + ".Items." + key + ".Type"));
                    }catch (NumberFormatException exception) {
                        type = getStringPath("Kit." + kit + ".Items." + key + ".Type");
                    }
                }
                if(isPathSet("Kit." + kit + ".Items." + key + ".Damage")) {
                    damage = getShortPath("Kit." + kit + ".Items." + key + ".Damage");
                }else if(isPathSet("Kit." + kit + ".Items." + key + ".SubID")) {
                    damage = getShortPath("Kit." + kit + ".Items." + key + ".SubID");
                }
                if(isPathSet("Kit." + kit + ".Items." + key + ".DisplayName")) {
                    displayName = getStringPath("Kit." + kit + ".Items." + key + ".DisplayName");
                }
                if(isPathSet("Kit." + kit + ".Items." + key + ".Lore")) {
                    lore = getStringListPath("Kit." + kit + ".Items." + key + ".Lore");
                }
                if(isPathSet("Kit." + kit + ".Items." + key + ".Amount")) {
                    amount = getIntegerPath("Kit." + kit + ".Items." + key + ".Amount");
                }
                if(isPathSet("Kit." + kit + ".Items." + key + ".Enchantments")) {
                    Set<String> enchantmentList = yaml.getConfigurationSection("Kit." + kit + ".Items." + key + ".Enchantments").getKeys(false);
                    for (String enchantment : enchantmentList) {
                        Enchantment enchant = ItemEditCmd.getEnchantmentByName(enchantment);
                        int level = getIntegerPath("Kit." + kit + ".Items." + key + ".Enchantments." + enchantment);
                        enchantments.put(enchant, level);
                    }
                }
                if(isPathSet("Kit." + kit + ".Items." + key + ".Unbreakable")) {
                    unbreakable = getBooleanPath("Kit." + kit + ".Items." + key + ".Unbreakable");
                }
                if(isPathSet("Kit." + kit + ".Items." + key + ".HideEnchants")) {
                    hideEnchants = getBooleanPath("Kit." + kit + ".Items." + key + ".HideEnchants");
                }
                if(isPathSet("Kit." + kit + ".Items." + key + ".HideUnbreakable")) {
                    hideUnbreakable = getBooleanPath("Kit." + kit + ".Items." + key + ".HideUnbreakable");
                }
                //Book
                if(isPathSet("Kit." + kit + ".Items." + key + ".BookPages")) {
                    writtenBookPages = getStringListPath("Kit." + kit + ".Items." + key + ".BookPages");
                }
                if(isPathSet("Kit." + kit + ".Items." + key + ".Author")) {
                    author = getStringPath("Kit." + kit + ".Items." + key + ".Author");
                }
                //Leather Armor
                if(isPathSet("Kit." + kit + ".Items." + key + ".Color")) {
                    color = getStringPath("Kit." + kit + ".Items." + key + ".Color");
                }
                //Skull
                if(isPathSet("Kit." + kit + ".Items." + key + ".SkullOwner")) {
                    skullOwner = getStringPath("Kit." + kit + ".Items." + key + ".SkullOwner");
                }

                if(type.equalsIgnoreCase("WRITTEN_BOOK") || typeID == 387) {
                    ItemStack item = ItemBuilder.createWrittenBook(amount, displayName, lore, enchantments, author, writtenBookPages);
                    kitItems.add(item);
                }else if(type.equalsIgnoreCase("LEATHER_BOOTS") || typeID == 301 || type.equalsIgnoreCase("LEATHER_LEGGINGS") || typeID == 300
                    || type.equalsIgnoreCase("LEATHER_CHESTPLATE") || typeID == 299 || type.equalsIgnoreCase("LEATHER_HELMET") || typeID == 298) {
                    ItemStack item = ItemBuilder.createLeatherArmor(type, damage, amount, displayName, lore, enchantments, unbreakable, hideEnchants, hideUnbreakable, color);
                    kitItems.add(item);
                }else if(type.equalsIgnoreCase("SKULL_ITEM") || type.equalsIgnoreCase("SKULL") || typeID == 397) {
                    ItemStack item = ItemBuilder.createSkull("SKULL_ITEM", amount, displayName, lore, enchantments, unbreakable, hideEnchants, hideUnbreakable, skullOwner);
                    kitItems.add(item);
                }else{
                    ItemStack item;
                    if(!type.equalsIgnoreCase("AIR")) {
                        item = ItemBuilder.createItem(type, damage, amount, displayName, lore, enchantments, unbreakable, hideEnchants, hideUnbreakable);
                    }else{
                        item = ItemBuilder.createItem(typeID, damage, amount, displayName, lore, enchantments, unbreakable, hideEnchants, hideUnbreakable);
                    }
                    kitItems.add(item);
                }
                enchantments.clear();
                lore.clear();
            }
        }
        return kitItems;
    }

    public static boolean canGetKit(Player p, String kit) {
        String noDelayPerm = CommandFile.getStringPath("Command.Kit.Permission.NoDelay");
        if(!NewSystem.hasPermission(p, noDelayPerm)) {
            if (SavingsFile.isPathSet("Kit." + kit + "." + p.getUniqueId() + ".Abgeholt")) {
                long ends = SavingsFile.getLongPath("Kit." + kit + "." + p.getUniqueId() + ".Abgeholt");
                long now = System.currentTimeMillis();
                int cooldown = 0;
                if (yaml.isSet("Kit." + kit + ".DelayInTicks")) {
                    cooldown = yaml.getInt("Kit." + kit + ".DelayInTicks");
                }
                int rest = (int) ((ends + cooldown) - now);

                return rest < 0;
            } else {
                return true;
            }
        }else{
            return true;
        }
    }

    private static HashMap<String, List<String>> configurationSection = new HashMap<>();
    public static List<String> getConfigurationSection(String path) {
        if(configurationSection.containsKey(path)) {
            return configurationSection.get(path);
        }

        List<String> list = new ArrayList<>();
        if(isPathSet(path)) {
            Set<String> keys = yaml.getConfigurationSection(path).getKeys(false);
            for(String key : keys) {
                list.add(key);
            }

            configurationSection.put(path, list);
        }
        return list;
    }

    public static void setPath(String path, Object wert) {
        yaml.set(path, wert);
        saveConfig();
    }

    private static HashMap<String, String> string = new HashMap<>();
    public static String getStringPath(String path) {
        if(string.containsKey(path)) {
            return string.get(path);
        }

        if(file.exists() && isPathSet(path)) {
            string.put(path, NewSystem.replace(yaml.getString(path)));
            return string.get(path);
        }else{
            return "";
        }
    }

    private static HashMap<String, Boolean> booleanSavings = new HashMap<>();
    public static boolean getBooleanPath(String path) {
        if(booleanSavings.containsKey(path)) {
            return booleanSavings.get(path);
        }

        if(file.exists() && isPathSet(path)) {
            booleanSavings.put(path, yaml.getBoolean(path));
            return booleanSavings.get(path);
        }
        return false;
    }

    private static HashMap<String, Integer> integer = new HashMap<>();
    public static Integer getIntegerPath(String path) {
        if(integer.containsKey(path)) {
            return integer.get(path);
        }

        if(file.exists() && isPathSet(path)) {
            integer.put(path, yaml.getInt(path));
            return integer.get(path);
        }
        return 0;
    }

    private static HashMap<String, Short> shortSavings = new HashMap<>();
    public static Short getShortPath(String path) {
        if(shortSavings.containsKey(path)) {
            return shortSavings.get(path);
        }

        if(file.exists() && isPathSet(path)) {
            shortSavings.put(path, (short) yaml.getInt(path));
            return shortSavings.get(path);
        }
        return 0;
    }

    private static HashMap<String, Long> longSavings = new HashMap<>();
    public static Long getLongPath(String path) {
        if(longSavings.containsKey(path)) {
            return longSavings.get(path);
        }

        if(file.exists() && isPathSet(path)) {
            longSavings.put(path, yaml.getLong(path));
            return longSavings.get(path);
        }
        return 0L;
    }

    private static HashMap<String, Double> doubleSavings = new HashMap<>();
    public static Double getDoublePath(String path) {
        if(doubleSavings.containsKey(path)) {
            return doubleSavings.get(path);
        }

        if(file.exists() && isPathSet(path)) {
            doubleSavings.put(path, yaml.getDouble(path));
            return doubleSavings.get(path);
        }
        return 0D;
    }

    private static HashMap<String, List<String>> stringList = new HashMap<>();
    public static List<String> getStringListPath(String path) {
        if(stringList.containsKey(path)) {
            return stringList.get(path);
        }

        if(file.exists() && isPathSet(path)) {
            List<String> list = yaml.getStringList(path);
            list.replaceAll(NewSystem::replace);
            stringList.put(path, list);
            return list;
        }
        return new ArrayList<>();
    }

    public static boolean isPathSet(String path) {
        if(file.exists()) {
            return yaml.contains(path);
        }else{
            try {
                if(file.createNewFile()) {
                    saveConfig();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
