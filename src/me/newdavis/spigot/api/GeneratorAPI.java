package me.newdavis.spigot.api;

import me.newdavis.spigot.command.ItemEditCmd;
import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SavingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import me.newdavis.spigot.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;

import java.util.*;

public class GeneratorAPI {

    private static HashMap<String, Integer> genDrop = new HashMap<>();
    private static HashMap<String, ArmorStand> genHolo = new HashMap<>();

    public static boolean createGenerator(String generator, Location location, ItemStack drop, int dropAmount, long dropSpeed) {
        if(!generatorExist(generator)) {
            SavingsFile.setPath("Generator." + generator + ".Location.World", location.getWorld().getName());
            SavingsFile.setPath("Generator." + generator + ".Location.X", location.getBlockX() + 0.5D);
            SavingsFile.setPath("Generator." + generator + ".Location.Y", location.getBlockY() + 1D);
            SavingsFile.setPath("Generator." + generator + ".Location.Z", location.getBlockZ() + 0.5D);
            SavingsFile.setPath("Generator." + generator + ".Drop", drop);
            SavingsFile.setPath("Generator." + generator + ".DropAmount", dropAmount);
            SavingsFile.setPath("Generator." + generator + ".DropSpeed", dropSpeed);
            SavingsFile.saveConfig();
            return true;
        }
        return false;
    }

    public static boolean deleteGenerator(String generator) {
        if(generatorExist(generator)) {
            if(isGeneratorActive(generator)) {
                stop(generator);
            }
            SavingsFile.setPath("Generator." + generator, null);
            SavingsFile.saveConfig();
            return true;
        }
        return false;
    }

    public static boolean changeName(String generator, String wishName) {
        if(generatorExist(generator)) {
            if (!generatorExist(wishName)) {
                SavingsFile.setPath("Generator." + generator, null);
                Location location = getLocation(generator);
                SavingsFile.setPath("Generator." + wishName + ".Location.World", location.getWorld().getName());
                SavingsFile.setPath("Generator." + wishName + ".Location.X", location.getX());
                SavingsFile.setPath("Generator." + wishName + ".Location.Y", location.getY());
                SavingsFile.setPath("Generator." + wishName + ".Location.Z", location.getZ());
                ItemStack drop = getDrop(generator);
                SavingsFile.setPath("Generator." + wishName + ".Location.Drop", drop);
                int dropAmount = getDropAmount(generator);
                SavingsFile.setPath("Generator." + wishName + ".Location.DropAmount", dropAmount);
                long dropSpeed = getDropSpeed(generator);
                SavingsFile.setPath("Generator." + wishName + ".Location.DropSpeed", dropSpeed);
                SavingsFile.saveConfig();
                return true;
            }
        }
        return false;
    }

    public static boolean setLocation(String generator, Location location) {
        if(generatorExist(generator)) {
            SavingsFile.setPath("Generator." + generator + ".Location.World", location.getWorld().getName());
            SavingsFile.setPath("Generator." + generator + ".Location.X", location.getBlockX() + 0.5D);
            SavingsFile.setPath("Generator." + generator + ".Location.Y", location.getBlockY() + 1D);
            SavingsFile.setPath("Generator." + generator + ".Location.Z", location.getBlockZ() + 0.5D);
            SavingsFile.saveConfig();
            return true;
        }
        return false;
    }

    public static boolean setDrop(String generator, ItemStack itemStack) {
        if(generatorExist(generator)) {
            if (itemStack != null) {
                String type = ItemBuilder.getNameOfMaterial(ItemBuilder.getMaterialOfItemStack(itemStack));

                SavingsFile.setPath("Generator." + generator + ".Drop.Type", type);
                SavingsFile.setPath("Generator." + generator + ".Drop.SubID", itemStack.getData().getData());
                if(itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
                    SavingsFile.setPath("Generator." + generator + ".Drop.DisplayName", itemStack.getItemMeta().getDisplayName());
                }
                SavingsFile.setPath("Generator." + generator + ".Drop.Amount", itemStack.getAmount());
                if(itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()) {
                    SavingsFile.setPath("Generator." + generator + ".Drop.Lore", itemStack.getItemMeta().getLore());
                }
                for(Enchantment enchantment : itemStack.getEnchantments().keySet()) {
                    int level = itemStack.getEnchantmentLevel(enchantment);
                    SavingsFile.setPath("Generator." + generator + ".Drop.Enchantments." + enchantment.getName(), level);
                }
                if(ReflectionAPI.VERSION_ID >= 16) {
                    SavingsFile.setPath("Generator." + generator + ".Drop.Damage", -1);
                    SavingsFile.setPath("Generator." + generator + ".Drop.SubID", null);
                }else{
                    SavingsFile.setPath("Generator." + generator + ".Drop.Unbreakable", itemStack.getItemMeta().spigot().isUnbreakable());
                }
                SavingsFile.setPath("Generator." + generator + ".Drop.HideEnchants", itemStack.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS));
                SavingsFile.setPath("Generator." + generator + ".Drop.HideUnbreakable", itemStack.getItemMeta().hasItemFlag(ItemFlag.HIDE_UNBREAKABLE));

                if (type.equalsIgnoreCase("WRITTEN_BOOK")) {
                    BookMeta meta = (BookMeta) itemStack.getItemMeta();
                    if(meta.hasPages()) {
                        for (String bookPage : meta.getPages()) {
                            SavingsFile.setPath("Generator." + generator + ".Drop.BookPages", bookPage);
                        }
                    }
                    if(meta.hasAuthor()) {
                        SavingsFile.setPath("Generator." + generator + ".Drop.Author", meta.getAuthor());
                    }
                } else if (type.equalsIgnoreCase("LEATHER_BOOTS") || type.equalsIgnoreCase("LEATHER_LEGGINGS")
                        || type.equalsIgnoreCase("LEATHER_CHESTPLATE") || type.equalsIgnoreCase("LEATHER_HELMET")) {
                    LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
                    SavingsFile.setPath("Generator." + generator + ".Drop.Color", meta.getColor().toString());
                } else if (type.equalsIgnoreCase("SKULL_ITEM") || type.equalsIgnoreCase("SKULL")) {
                    SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
                    if(meta.hasOwner()) {
                        SavingsFile.setPath("Generator." + generator + ".Drop.SkullOwner", meta.getOwner());
                    }
                }
                SavingsFile.saveConfig();
                return true;
            }
        }
        return false;
    }

    public static boolean setDropAmount(String generator, Integer amount) {
        if(generatorExist(generator)) {
            SavingsFile.setPath("Generator." + generator + ".DropAmount", amount);
            SavingsFile.saveConfig();
            return true;
        }
        return false;
    }

    public static boolean setDropSpeed(String generator, Long speed) {
        if(generatorExist(generator)) {
            SavingsFile.setPath("Generator." + generator + ".DropSpeed", speed);
            SavingsFile.saveConfig();
            return true;
        }
        return false;
    }

    public static Location getLocation(String generator) {
        if(generatorExist(generator)) {
            World world = Bukkit.getWorld(SavingsFile.getStringPath("Generator." + generator + ".Location.World"));
            double x = SavingsFile.getDoublePath("Generator." + generator + ".Location.X");
            double y = SavingsFile.getDoublePath("Generator." + generator + ".Location.Y");
            double z = SavingsFile.getDoublePath("Generator." + generator + ".Location.Z");
            return new Location(world, x, y, z);
        }
        return new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
    }

    public static ItemStack getDrop(String generator) {
        if(generatorExist(generator)) {
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

            if(SavingsFile.isPathSet("Generator." + generator + ".Drop")) {
                if (SavingsFile.isPathSet("Generator." + generator + ".Drop.Type")) {
                    try {
                        typeID = Integer.parseInt(SavingsFile.getStringPath("Generator." + generator + ".Drop.Type"));
                    } catch (NumberFormatException exception) {
                        type = SavingsFile.getStringPath("Generator." + generator + ".Drop.Type");
                    }
                }
                if (SavingsFile.isPathSet("Generator." + generator + ".Drop.Damage")) {
                    damage = SavingsFile.getShortPath("Generator." + generator + ".Drop.Damage");
                } else if (SavingsFile.isPathSet("Generator." + generator + ".Drop.SubID")) {
                    damage = SavingsFile.getShortPath("Generator." + generator + ".Drop.SubID");
                }
                if (SavingsFile.isPathSet("Generator." + generator + ".Drop.DisplayName")) {
                    displayName = SavingsFile.getStringPath("Generator." + generator + ".Drop.DisplayName");
                }
                if (SavingsFile.isPathSet("Generator." + generator + ".Drop.Lore")) {
                    lore = SavingsFile.getStringListPath("Generator." + generator + ".Drop.Lore");
                }
                if (SavingsFile.isPathSet("Generator." + generator + ".Drop.Amount")) {
                    amount = SavingsFile.getIntegerPath("Generator." + generator + ".Drop.Amount");
                }
                if (SavingsFile.isPathSet("Generator." + generator + ".Drop.Enchantments")) {
                    List<String> enchantmentList = SavingsFile.getConfigurationSection("Generator." + generator + ".Drop.Enchantments");
                    for (String enchantment : enchantmentList) {
                        Enchantment enchant = ItemEditCmd.getEnchantmentByName(enchantment);
                        int level = SavingsFile.getIntegerPath("Generator." + generator + ".Drop.Enchantments." + enchantment);
                        enchantments.put(enchant, level);
                    }
                }
                if (SavingsFile.isPathSet("Generator." + generator + ".Drop.Unbreakable")) {
                    unbreakable = SavingsFile.getBooleanPath("Generator." + generator + ".Drop.Unbreakable");
                }
                if (SavingsFile.isPathSet("Generator." + generator + ".Drop.HideEnchants")) {
                    hideEnchants = SavingsFile.getBooleanPath("Generator." + generator + ".Drop.HideEnchants");
                }
                if (SavingsFile.isPathSet("Generator." + generator + ".Drop.HideUnbreakable")) {
                    hideUnbreakable = SavingsFile.getBooleanPath("Generator." + generator + ".Drop.HideUnbreakable");
                }
                //Book
                if (SavingsFile.isPathSet("Generator." + generator + ".Drop.BookPages")) {
                    writtenBookPages = SavingsFile.getStringListPath("Generator." + generator + ".Drop.BookPages");
                }
                if (SavingsFile.isPathSet("Generator." + generator + ".Drop.Author")) {
                    author = SavingsFile.getStringPath("Generator." + generator + ".Drop.Author");
                }
                //Leather Armor
                if (SavingsFile.isPathSet("Generator." + generator + ".Drop.Color")) {
                    color = SavingsFile.getStringPath("Generator." + generator + ".Drop.Color");
                }
                //Skull
                if (SavingsFile.isPathSet("Generator." + generator + ".Drop.SkullOwner")) {
                    skullOwner = SavingsFile.getStringPath("Generator." + generator + ".Drop.SkullOwner");
                }

                if (type.equalsIgnoreCase("WRITTEN_BOOK") || typeID == 387) {
                    return ItemBuilder.createWrittenBook(amount, displayName, lore, enchantments, author, writtenBookPages);
                } else if (type.equalsIgnoreCase("LEATHER_BOOTS") || typeID == 301 || type.equalsIgnoreCase("LEATHER_LEGGINGS") || typeID == 300
                        || type.equalsIgnoreCase("LEATHER_CHESTPLATE") || typeID == 299 || type.equalsIgnoreCase("LEATHER_HELMET") || typeID == 298) {
                    return ItemBuilder.createLeatherArmor(type, damage, amount, displayName, lore, enchantments, unbreakable, hideEnchants, hideUnbreakable, color);
                } else if (type.equalsIgnoreCase("SKULL_ITEM") || type.equalsIgnoreCase("SKULL") || typeID == 397) {
                    return ItemBuilder.createSkull("SKULL_ITEM", amount, displayName, lore, enchantments, unbreakable, hideEnchants, hideUnbreakable, skullOwner);
                } else {
                    if (!type.equalsIgnoreCase("AIR")) {
                        return ItemBuilder.createItem(type, damage, amount, displayName, lore, enchantments, unbreakable, hideEnchants, hideUnbreakable);
                    } else {
                        return ItemBuilder.createItem(typeID, damage, amount, displayName, lore, enchantments, unbreakable, hideEnchants, hideUnbreakable);
                    }
                }
            }
        }
        return new ItemStack(Material.AIR);
    }

    public static Integer getDropAmount(String generator) {
        if(generatorExist(generator)) {
            return SavingsFile.getIntegerPath("Generator." + generator + ".DropAmount");
        }
        return 1;
    }

    public static long getDropSpeed(String generator) {
        if(generatorExist(generator)) {
            return SavingsFile.getLongPath("Generator." + generator + ".DropSpeed");
        }
        return 20;
    }

    public static boolean isGeneratorActive(String generator) {
        return genDrop.containsKey(generator);
    }
    public static boolean generatorExist(String generator) {
        for(String gen : getAllGenerator()) {
            if(gen.equalsIgnoreCase(generator)) {
                return true;
            }
        }
        return false;
    }

    public static String getGeneratorName(String generator) {
        for(String gen : getAllGenerator()) {
            if(gen.equalsIgnoreCase(generator)) {
                return gen;
            }
        }
        return "";
    }

    public static Collection<String> getAllActiveGenerator() {
        return genDrop.keySet();
    }
    public static Collection<String> getAllGenerator() {
        return SavingsFile.getConfigurationSection("Generator");
    }

    public static void start(String generator) {
        if(generatorExist(generator) && !genDrop.containsKey(generator)) {
            spawnHologram(generator);
            genDrop.put(generator, Bukkit.getScheduler().scheduleSyncRepeatingTask(NewSystem.getInstance(), new Runnable() {
                @Override
                public void run() {
                    ItemStack dropItem = getDrop(generator);
                    if(ItemBuilder.getMaterialOfItemStack(dropItem) != Material.AIR) {
                        Location location = getLocation(generator);
                        int amount = getDropAmount(generator);
                        dropItem.setAmount(amount);
                        location.getWorld().dropItem(location, dropItem).setVelocity(new Vector(0D, 0D, 0D));
                    }
                }
            }, getDropSpeed(generator), getDropSpeed(generator)));
        }
    }

    public static void stop(String generator) {
        if(generatorExist(generator) && genDrop.containsKey(generator)) {
            Bukkit.getScheduler().cancelTask(genDrop.get(generator));
            genDrop.remove(generator);
            killHologram(generator);
        }
    }

    public static void spawnHologram(String generator) {
        if(!genHolo.containsKey(generator)) {
            Location location = getLocation(generator);
            ArmorStand entity = location.getWorld().spawn(location.subtract(0, 2.5, 0), ArmorStand.class);
            entity.setCustomNameVisible(true);
            ItemStack dropItem = getDrop(generator);
            int amount = getDropAmount(generator);
            String title = CommandFile.getStringPath("Command.Generator.Title")
                    .replace("{Item}", (dropItem.hasItemMeta() && dropItem.getItemMeta().hasDisplayName() ? dropItem.getItemMeta().getDisplayName() : ItemBuilder.getNameOfMaterial(ItemBuilder.getMaterialOfItemStack(dropItem))))
                    .replace("{Amount}", String.valueOf(amount))
                    .replace("{Generator}", generator);
            entity.setCustomName(title);
            entity.setGravity(false);
            entity.setVisible(false);
            genHolo.put(generator, entity);
        }
    }

    public static void killHologram(String generator) {
        if(genHolo.containsKey(generator)) {
            ArmorStand entity = genHolo.get(generator);
            entity.remove();
            genHolo.remove(generator);
        }
    }

    public static void stopAllGenerator() {
        for(String generator : getAllActiveGenerator()) {
            stop(generator);
        }
    }

}
