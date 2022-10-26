package me.newdavis.spigot.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.newdavis.spigot.api.ReflectionAPI;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ItemBuilder {

    public static HashMap<String, Material> serverMaterials = new HashMap<>();
    public static HashMap<String, Inventory> serverInventories = new HashMap<>();

    private ItemStack item;
    private ItemMeta meta;

    public ItemBuilder(Material material) {
        item = new ItemStack(material, 1);
        meta = item.getItemMeta();
    }

    public ItemBuilder(Material material, short subID) {
        item = new ItemStack(material, 1, subID);
        meta = item.getItemMeta();
    }

    public ItemBuilder setName(String name) {
        meta.setDisplayName(name);
        return this;
    }

    public ItemBuilder setLore(String[] lore) {
        meta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        meta.setLore(lore);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder setDurability(short durability) {
        item.setDurability(durability);
        return this;
    }

    public ItemBuilder setUnbreakable() {
        meta.spigot().setUnbreakable(true);
        return this;
    }

    public ItemBuilder hideUnbreakble() {
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        return this;
    }

    public ItemBuilder hideEnchantments() {
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemBuilder hideDurability() {
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        return this;
    }

    public ItemBuilder hidePotionEffects() {
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getSkull(String owner, String name, String[] lore, int amount) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        meta.setOwner(owner);
        meta.setDisplayName(name);
        if(lore[0].isEmpty()) {
            meta.setLore(Arrays.asList(lore));
        }
        item.setAmount(amount);

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getColoredArmor(String name, String[] lore, int amount, Color color) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();

        meta.setColor(color);
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setAmount(amount);

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getWrittenBook(String displayName, String title, List<String> text, List<String> lore, String author) {
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) item.getItemMeta();

        meta.setDisplayName(displayName);
        meta.setAuthor(author);
        meta.setTitle(title);
        for(String page : text) {
            meta.addPage(page);
        }
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getCustomSkull(String url, String displayName, List<String> lore) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
        if(url.isEmpty())return head;

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        if(!displayName.isEmpty()) {
            headMeta.setDisplayName(displayName);
        }
        if(!lore.isEmpty()) {
            headMeta.setLore(lore);
        }
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", url));
        Field profileField;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
    }

    private static Class<?> materialClass;
    static {
        try {
            materialClass = Class.forName("org.bukkit.Material");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<?> itemStackClass;
    static {
        try {
            itemStackClass = Class.forName("org.bukkit.inventory.ItemStack");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /*private static Class<?> inventoryTypeEnum;
    static {
        try {
            itemStackClass = Class.forName("org.bukkit.event.inventory.InventoryType");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setServerInventories() {
        Class<?> clazz = inventoryTypeEnum;
        for(Object inventoryType : clazz.getEnumConstants()) {
            if(inventoryType.toString().equalsIgnoreCase("ANVIL") || inventoryType.toString().equalsIgnoreCase("ENCHANTING") || inventoryType.toString().equalsIgnoreCase("WORKBENCH")) {
                try {
                    Class<?> bukkitClazz = Class.forName("org.bukkit");
                    Method createInventory = bukkitClazz.getMethod("createInventory", InventoryHolder.class, InventoryType.class, String.class);
                    createInventory.invoke(null, null, inventoryType, "");
                } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                         IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }*/

    public static void setServerMaterials() {
        Class<?> clazz = materialClass;
        for (Object mat : clazz.getEnumConstants()) {
            serverMaterials.put(mat.toString(), (Material) mat);
        }
    }

    public static ItemStack[] getArmor(Player p) {
        if(ReflectionAPI.VERSION_ID != 8) {
            try {
                Class<?> clazz = p.getClass();
                Object converted = clazz.cast(p);
                Method getInventory = converted.getClass().getMethod("getInventory");
                Object inventory = getInventory.invoke(converted);
                Method getHelmet = inventory.getClass().getMethod("getHelmet");
                Object helmet = getHelmet.invoke(inventory);
                Method getChestplate = inventory.getClass().getMethod("getChestplate");
                Object chestplate = getChestplate.invoke(inventory);
                Method getLeggings = inventory.getClass().getMethod("getLeggings");
                Object leggings = getLeggings.invoke(inventory);
                Method getBoots = inventory.getClass().getMethod("getBoots");
                Object boots = getBoots.invoke(inventory);

                ItemStack[] items = new ItemStack[4];
                items[0] = (ItemStack) helmet;
                items[1] = (ItemStack) chestplate;
                items[2] = (ItemStack) leggings;
                items[3] = (ItemStack) boots;
                return items;
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return p.getInventory().getArmorContents();
    }

    public static ItemStack[] getInventory(Player p) {
        if(ReflectionAPI.VERSION_ID != 8) {
            try {
                Class<?> clazz = p.getClass();
                Object converted = clazz.cast(p);
                Method getInventory = converted.getClass().getMethod("getInventory");
                Object inventory = getInventory.invoke(converted);
                Method getItem = inventory.getClass().getMethod("getItem", int.class);

                ItemStack[] items = new ItemStack[p.getInventory().getSize()];
                for (int i = 0; i < p.getInventory().getSize(); i++) {
                    items[i] = (ItemStack) getItem.invoke(inventory, i);
                }
                return items;
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return p.getInventory().getContents();
    }

    public static ItemStack[] getInventory(Inventory inv) {
        if(ReflectionAPI.VERSION_ID != 8) {
            try {
                Class<?> clazz = inv.getClass();
                Object converted = clazz.cast(inv);
                Method getInventory = converted.getClass().getMethod("getContents");
                Object inventory = getInventory.invoke(converted);
                Method getItem = inventory.getClass().getMethod("getItem", int.class);

                ItemStack[] items = new ItemStack[inv.getSize()];
                for (int i = 0; i < inv.getSize(); i++) {
                    items[i] = (ItemStack) getItem.invoke(inventory, i);
                }
                return items;
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return inv.getContents();
    }

    public static Material getMaterial(String type) {
        if(ReflectionAPI.VERSION_ID != 8) {
            try {
                Class<?> clazz = materialClass;
                Method method = clazz.getMethod("getMaterial", String.class);
                String material = String.valueOf(method.invoke(clazz, type));
                return serverMaterials.get(material);
            } catch (NoSuchMethodException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return Material.getMaterial(type);
    }

    public static Material getMaterial(int type) {
        if(ReflectionAPI.VERSION_ID <= 12) {
            try {
                Class<?> clazz = materialClass;
                Method method = clazz.getMethod("getMaterial", int.class);
                return (Material) method.invoke(clazz, type);
            } catch (NoSuchMethodException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static ItemStack getItemInHand(Player p) {
        if(ReflectionAPI.VERSION_ID > 8) {
            try {
                Class<?> clazz = p.getClass();
                Method method = clazz.getMethod("getItemInHand");
                return (ItemStack) method.invoke(p);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return p.getItemInHand();
    }

    public static Material getMaterialOfItemStack(ItemStack item) {
        if(ReflectionAPI.VERSION_ID != 8) {
            try {
                Class<?> clazz = itemStackClass;
                Method method = clazz.getMethod("getType");
                String mat = method.invoke(item).toString();
                if (serverMaterials.containsKey(mat) || serverMaterials.containsValue(mat)) {
                    return getMaterial(mat);
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
        return item.getType();
    }

    public static String getNameOfMaterial(Material material) {
        if(ReflectionAPI.VERSION_ID != 8) {
            if (serverMaterials.containsValue(material)) {
                for (String mat : serverMaterials.keySet()) {
                    if (serverMaterials.get(mat) == material) {
                        return mat;
                    }
                }
            }
        }else{
            return material.name();
        }
        return null;
    }

    //ITEM CREATOR

    public static ItemStack createItem(String material, short damage, int amount, String displayName, List<String> lore, HashMap<Enchantment, Integer> enchantments, boolean unbreakable, boolean hideEnchants, boolean hideUnbreakable) {
        Material mat = ItemBuilder.getMaterial(material);
        if(mat != null) {
            ItemStack item = new ItemStack(mat, amount, damage);
            ItemMeta meta = item.getItemMeta();

            if(!displayName.equalsIgnoreCase("")) {
                meta.setDisplayName(displayName);
            }
            if(!lore.isEmpty()) {
                meta.setLore(lore);
            }
            //Enchantments adden
            Collection<Enchantment> enchantmentsKeys = enchantments.keySet();
            for(Enchantment enchantment : enchantmentsKeys) {
                meta.addEnchant(enchantment, enchantments.get(enchantment), true);
            }

            if (unbreakable) {
                if(ReflectionAPI.VERSION_ID >= 16) {
                    item.setDurability((short)-1);
                }else {
                    meta.spigot().setUnbreakable(true);
                }
            }
            if (hideEnchants) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            if (hideUnbreakable) {
                meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            }

            item.setItemMeta(meta);
            return item;
        }
        return new ItemStack(Material.AIR);
    }

    public static ItemStack createItem(int material, short damage, int amount, String displayName, List<String> lore, HashMap<Enchantment, Integer> enchantments, boolean unbreakable, boolean hideEnchants, boolean hideUnbreakable) {
        Material mat = ItemBuilder.getMaterial(material);
        if(mat != null) {
            ItemStack item = new ItemStack(mat, amount, damage);
            ItemMeta meta = item.getItemMeta();

            if(!displayName.equalsIgnoreCase("")) {
                meta.setDisplayName(displayName);
            }
            if(!lore.isEmpty()) {
                meta.setLore(lore);
            }
            //Enchantments adden
            Collection<Enchantment> enchantmentsKeys = enchantments.keySet();
            for(Enchantment enchantment : enchantmentsKeys) {
                meta.addEnchant(enchantment, enchantments.get(enchantment), true);
            }

            if (unbreakable) {
                if(ReflectionAPI.VERSION_ID >= 16) {
                    item.setDurability((short)-1);
                }else {
                    meta.spigot().setUnbreakable(true);
                }
            }
            if (hideEnchants) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            if (hideUnbreakable) {
                meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            }

            item.setItemMeta(meta);
            return item;
        }
        return new ItemStack(Material.AIR);
    }

    public static ItemStack createLeatherArmor(String material, short damage, int amount, String displayName, List<String> lore, HashMap<Enchantment, Integer> enchantments, boolean unbreakable, boolean hideEnchants, boolean hideUnbreakable, String color) {
        Material mat = ItemBuilder.getMaterial(material);
        if(mat != null) {
            ItemStack item = new ItemStack(mat, amount, damage);
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();

            if(!color.equalsIgnoreCase("")) {
                Color c = getColorFromString(color);
                meta.setColor(c);
            }
            meta.setDisplayName(displayName);
            meta.setLore(lore);
            //Enchantments adden
            Collection<Enchantment> enchantmentsKeys = enchantments.keySet();
            for(Enchantment enchantment : enchantmentsKeys) {
                meta.addEnchant(enchantment, enchantments.get(enchantment), true);
            }

            if (unbreakable) {
                if(ReflectionAPI.VERSION_ID >= 16) {
                    item.setDurability((short)-1);
                }else {
                    meta.spigot().setUnbreakable(true);
                }
            }
            if (hideEnchants) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            if (hideUnbreakable) {
                meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            }

            item.setItemMeta(meta);
            return item;
        }
        return new ItemStack(Material.AIR);
    }

    public static ItemStack createSkull(String material, int amount, String displayName, List<String> lore, HashMap<Enchantment, Integer> enchantments, boolean unbreakable, boolean hideEnchants, boolean hideUnbreakable, String skullOwner) {
        Material mat = ItemBuilder.getMaterial(material);
        if(mat != null) {
            ItemStack item = new ItemStack(mat, amount, (short) 3);
            SkullMeta meta = (SkullMeta) item.getItemMeta();

            meta.setOwner(skullOwner);
            meta.setDisplayName(displayName);
            meta.setLore(lore);
            //Enchantments adden
            Collection<Enchantment> enchantmentsKeys = enchantments.keySet();
            for(Enchantment enchantment : enchantmentsKeys) {
                meta.addEnchant(enchantment, enchantments.get(enchantment), true);
            }

            if (unbreakable) {
                if(ReflectionAPI.VERSION_ID >= 16) {
                    item.setDurability((short)-1);
                }else {
                    meta.spigot().setUnbreakable(true);
                }
            }
            if (hideEnchants) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            if (hideUnbreakable) {
                meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            }

            item.setItemMeta(meta);
            return item;
        }
        return new ItemStack(Material.AIR);
    }

    public static ItemStack createWrittenBook(int amount, String displayName, List<String> lore, HashMap<Enchantment, Integer> enchantments, String author, List<String> pages) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK, amount);
        BookMeta meta = (BookMeta) book.getItemMeta();

        //Enchantments adden
        Collection<Enchantment> enchantmentsKeys = enchantments.keySet();
        for(Enchantment enchantment : enchantmentsKeys) {
            meta.addEnchant(enchantment, enchantments.get(enchantment), true);
        }
        //Pages adden
        for(String page : pages) {
            meta.addPage(page);
        }
        meta.setDisplayName(displayName);
        meta.setTitle(displayName);
        meta.setLore(lore);
        meta.setAuthor(author);

        book.setItemMeta(meta);
        return book;
    }

    public static Color getColorFromString(String color) {
        Color c = Color.WHITE;
        if (color.equalsIgnoreCase("AQUA") || color.equalsIgnoreCase("LIGHT_BLUE")) {
            c = Color.AQUA;
        }else if(color.equalsIgnoreCase("BLACK")) {
            c = Color.BLACK;
        }else if(color.equalsIgnoreCase("BLUE")) {
            c = Color.BLUE;
        }else if(color.equalsIgnoreCase("FUCHSIA") || color.equalsIgnoreCase("MAGENTA")) {
            c = Color.FUCHSIA;
        }else if(color.equalsIgnoreCase("GRAY") || color.equalsIgnoreCase("GREY")) {
            c = Color.GRAY;
        }else if(color.equalsIgnoreCase("GREEN")) {
            c = Color.GREEN;
        }else if(color.equalsIgnoreCase("LIME") || color.equalsIgnoreCase("LIGHT_GREEN")) {
            c = Color.LIME;
        }else if(color.equalsIgnoreCase("MAROON") || color.equalsIgnoreCase("DARK_RED")) {
            c = Color.MAROON;
        }else if(color.equalsIgnoreCase("NAVY") || color.equalsIgnoreCase("DARK_BLUE")) {
            c = Color.NAVY;
        }else if(color.equalsIgnoreCase("OLIVE") || color.equalsIgnoreCase("CACTUS_GREEN")) {
            c = Color.OLIVE;
        }else if(color.equalsIgnoreCase("ORANGE")) {
            c = Color.ORANGE;
        }else if(color.equalsIgnoreCase("PURPLE")) {
            c = Color.PURPLE;
        }else if(color.equalsIgnoreCase("RED")) {
            c = Color.RED;
        }else if(color.equalsIgnoreCase("SILVER") || color.equalsIgnoreCase("LIGHT_GREY") || color.equalsIgnoreCase("LIGHT_GRAY")) {
            c = Color.SILVER;
        }else if(color.equalsIgnoreCase("TEAL") || color.equalsIgnoreCase("CYAN")) {
            c = Color.TEAL;
        }else if(color.equalsIgnoreCase("WHITE")) {
            c = Color.WHITE;
        }else if(color.equalsIgnoreCase("YELLOW")) {
            c = Color.YELLOW;
        }
        return c;
    }
}
