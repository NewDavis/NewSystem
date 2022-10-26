package me.newdavis.spigot.util;

import me.newdavis.spigot.command.ItemEditCmd;
import me.newdavis.spigot.file.KitFile;
import me.newdavis.spigot.file.OtherFile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;

import java.util.*;

public class CustomRecipe {

    public static String addRecipes() {
        String recipesEnabled = "Following Recipes are enabled: ";
        for(String recipeName : OtherFile.getConfigurationSection("Other.CustomRecipes")) {
            if(!recipeName.equalsIgnoreCase("Enabled")) {
                boolean ignoreSorting = shapedRecipe(recipeName);
                ItemStack result = getResult(recipeName);
                HashMap<String, MaterialData> materials = getMaterials(recipeName);

                if (!ignoreSorting) {
                    String shapeString = "";
                    ShapedRecipe recipe = new ShapedRecipe(result);
                    String[] shapeArray = new String[]{"", "", ""};
                    for (String slot : materials.keySet()) {
                        if (shapeString.split("").length == 3) {
                            if (shapeArray[0].equalsIgnoreCase("")) {
                                shapeArray[0] = shapeString;
                            } else if (shapeArray[1].equalsIgnoreCase("")) {
                                shapeArray[1] = shapeString;
                            }
                            shapeString = slot;
                        }else {
                            shapeString += slot;
                            if (slot.equalsIgnoreCase(getListOfSet(materials.keySet()).get(getListOfSet(materials.keySet()).size() - 1))) {
                                shapeArray[2] = shapeString;
                            }
                        }
                    }
                    recipe.shape(shapeArray);
                    for (String slot : materials.keySet()) {
                        if(materials.get(slot).getItemType() != Material.AIR) {
                            recipe.setIngredient(slot.toCharArray()[0], materials.get(slot));
                        }
                    }
                    Bukkit.addRecipe(recipe);
                    recipesEnabled += recipeName + " ";
                } else {
                    ShapelessRecipe recipe = new ShapelessRecipe(result);
                    for (String slot : materials.keySet()) {
                        if(materials.get(slot).getItemType() != Material.AIR) {
                            recipe.addIngredient(materials.get(slot));
                        }
                    }
                    Bukkit.addRecipe(recipe);
                    recipesEnabled += recipeName + " ";
                }
            }
        }
        return recipesEnabled;
    }

    private static List<String> getListOfSet(Set<String> set) {
        List<String> list = new ArrayList<>();
        for(String key : set) {
            list.add(key);
        }
        return list;
    }

    private static boolean shapedRecipe(String recipe) {
        boolean ignoreSorting = false;
        if(OtherFile.isPathSet("Other.CustomRecipes." + recipe + ".IgnoreSorting")) {
            ignoreSorting = OtherFile.getBooleanPath("Other.CustomRecipes." + recipe + ".IgnoreSorting");
        }
        return ignoreSorting;
    }

    private static ItemStack getResult(String recipe) {
        ItemStack resultItem = new ItemStack(Material.AIR);

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

        if (OtherFile.isPathSet("Other.CustomRecipes." + recipe + ".Result.Type")) {
            try {
                typeID = Integer.parseInt(OtherFile.getStringPath("Other.CustomRecipes." + recipe + ".Result.Type"));
            } catch (NumberFormatException exception) {
                type = OtherFile.getStringPath("Other.CustomRecipes." + recipe + ".Result.Type");
            }
        }
        if (OtherFile.isPathSet("Other.CustomRecipes." + recipe + ".Result.Damage")) {
            damage = OtherFile.getShortPath("Other.CustomRecipes." + recipe + ".Result.Damage");
        } else if (OtherFile.isPathSet("Other.CustomRecipes." + recipe + ".Result.SubID")) {
            damage = OtherFile.getShortPath("Other.CustomRecipes." + recipe + ".Result.SubID");
        }
        if (OtherFile.isPathSet("Other.CustomRecipes." + recipe + ".Result.DisplayName")) {
            displayName = OtherFile.getStringPath("Other.CustomRecipes." + recipe + ".Result.DisplayName");
        }
        if (OtherFile.isPathSet("Other.CustomRecipes." + recipe + ".Result.Lore")) {
            lore = OtherFile.getStringListPath("Other.CustomRecipes." + recipe + ".Result.Lore");
        }
        if (OtherFile.isPathSet("Other.CustomRecipes." + recipe + ".Result.Amount")) {
            amount = OtherFile.getIntegerPath("Other.CustomRecipes." + recipe + ".Result.Amount");
        }
        if (OtherFile.isPathSet("Other.CustomRecipes." + recipe + ".Result.Enchantments")) {
            List<String> enchantmentList = OtherFile.getConfigurationSection("Other.CustomRecipes." + recipe + ".Result.Enchantments");
            for (String enchantment : enchantmentList) {
                Enchantment enchant = ItemEditCmd.getEnchantmentByName(enchantment);
                int level = OtherFile.getIntegerPath("Other.CustomRecipes." + recipe + ".Result.Enchantments." + enchantment);
                enchantments.put(enchant, level);
            }
        }
        if (OtherFile.isPathSet("Other.CustomRecipes." + recipe + ".Result.Unbreakable")) {
            unbreakable = OtherFile.getBooleanPath("Other.CustomRecipes." + recipe + ".Result.Unbreakable");
        }
        if (OtherFile.isPathSet("Other.CustomRecipes." + recipe + ".Result.HideEnchants")) {
            hideEnchants = OtherFile.getBooleanPath("Other.CustomRecipes." + recipe + ".Result.HideEnchants");
        }
        if (OtherFile.isPathSet("Other.CustomRecipes." + recipe + ".Result.HideUnbreakable")) {
            hideUnbreakable = OtherFile.getBooleanPath("Other.CustomRecipes." + recipe + ".Result.HideUnbreakable");
        }
        //Book
        if (OtherFile.isPathSet("Other.CustomRecipes." + recipe + ".Result.BookPages")) {
            writtenBookPages = OtherFile.getStringListPath("Other.CustomRecipes." + recipe + ".Result.BookPages");
        }
        if (OtherFile.isPathSet("Other.CustomRecipes." + recipe + ".Result.Author")) {
            author = OtherFile.getStringPath("Other.CustomRecipes." + recipe + ".Result.Author");
        }
        //Leather Armor
        if (OtherFile.isPathSet("Other.CustomRecipes." + recipe + ".Result.Color")) {
            color = OtherFile.getStringPath("Other.CustomRecipes." + recipe + ".Result.Color");
        }
        //Skull
        if (OtherFile.isPathSet("Other.CustomRecipes." + recipe + ".Result.SkullOwner")) {
            skullOwner = OtherFile.getStringPath("Other.CustomRecipes." + recipe + ".Result.SkullOwner");
        }

        if (type.equalsIgnoreCase("WRITTEN_BOOK") || typeID == 387) {
            resultItem = ItemBuilder.createWrittenBook(amount, displayName, lore, enchantments, author, writtenBookPages);
        } else if (type.equalsIgnoreCase("LEATHER_BOOTS") || typeID == 301 || type.equalsIgnoreCase("LEATHER_LEGGINGS") || typeID == 300
                || type.equalsIgnoreCase("LEATHER_CHESTPLATE") || typeID == 299 || type.equalsIgnoreCase("LEATHER_HELMET") || typeID == 298) {
            resultItem = ItemBuilder.createLeatherArmor(type, damage, amount, displayName, lore, enchantments, unbreakable, hideEnchants, hideUnbreakable, color);
        } else if (type.equalsIgnoreCase("SKULL_ITEM") || type.equalsIgnoreCase("SKULL") || typeID == 397) {
            resultItem = ItemBuilder.createSkull("SKULL_ITEM", amount, displayName, lore, enchantments, unbreakable, hideEnchants, hideUnbreakable, skullOwner);
        } else {
            ItemStack item;
            if (!type.equalsIgnoreCase("AIR")) {
                item = ItemBuilder.createItem(type, damage, amount, displayName, lore, enchantments, unbreakable, hideEnchants, hideUnbreakable);
            } else {
                item = ItemBuilder.createItem(typeID, damage, amount, displayName, lore, enchantments, unbreakable, hideEnchants, hideUnbreakable);
            }
            resultItem = item;
        }
        return resultItem;
    }

    public static HashMap<String, MaterialData> getMaterials(String recipe) {
        HashMap<String, MaterialData> materials = new HashMap<>();

        for(String itemSlot : OtherFile.getConfigurationSection("Other.CustomRecipes." + recipe + ".Material")) {
            //Material
            MaterialData data = new MaterialData(Material.AIR);
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

            if (OtherFile.isPathSet("Other.CustomRecipes." + recipe + ".Material." + itemSlot + ".Type")) {
                try {
                    typeID = Integer.parseInt(OtherFile.getStringPath("Other.CustomRecipes." + recipe + ".Material." + itemSlot + ".Type"));
                } catch (NumberFormatException exception) {
                    type = OtherFile.getStringPath("Other.CustomRecipes." + recipe + ".Material." + itemSlot + ".Type");
                }
            }
            if (OtherFile.isPathSet("Other.CustomRecipes." + recipe + ".Material." + itemSlot + ".Damage")) {
                damage = OtherFile.getShortPath("Other.CustomRecipes." + recipe + ".Material." + itemSlot + ".Damage");
            } else if (OtherFile.isPathSet("Other.CustomRecipes." + recipe + ".Material." + itemSlot + ".SubID")) {
                damage = OtherFile.getShortPath("Other.CustomRecipes." + recipe + ".Material." + itemSlot + ".SubID");
            }
            if (OtherFile.isPathSet("Other.CustomRecipes." + recipe + ".Material." + itemSlot + ".DisplayName")) {
                displayName = OtherFile.getStringPath("Other.CustomRecipes." + recipe + ".Material." + itemSlot + ".DisplayName");
            }
            if (OtherFile.isPathSet("Other.CustomRecipes." + recipe + ".Material." + itemSlot + ".Lore")) {
                lore = OtherFile.getStringListPath("Other.CustomRecipes." + recipe + ".Material." + itemSlot + ".Lore");
            }
            if (OtherFile.isPathSet("Other.CustomRecipes." + recipe + ".Material." + itemSlot + ".Amount")) {
                amount = OtherFile.getIntegerPath("Other.CustomRecipes." + recipe + ".Material." + itemSlot + ".Amount");
            }
            if (OtherFile.isPathSet("Other.CustomRecipes." + recipe + ".Material." + itemSlot + ".Enchantments")) {
                List<String> enchantmentList = OtherFile.getConfigurationSection("Other.CustomRecipes." + recipe + ".Material." + itemSlot + ".Enchantments");
                for (String enchantment : enchantmentList) {
                    Enchantment enchant = ItemEditCmd.getEnchantmentByName(enchantment);
                    int level = OtherFile.getIntegerPath("Other.CustomRecipes." + recipe + ".Material." + itemSlot + ".Enchantments." + enchantment);
                    enchantments.put(enchant, level);
                }
            }
            if (OtherFile.isPathSet("Other.CustomRecipes." + recipe + ".Material." + itemSlot + ".Unbreakable")) {
                unbreakable = OtherFile.getBooleanPath("Other.CustomRecipes." + recipe + ".Material." + itemSlot + ".Unbreakable");
            }
            if (OtherFile.isPathSet("Other.CustomRecipes." + recipe + ".Material." + itemSlot + ".HideEnchants")) {
                hideEnchants = OtherFile.getBooleanPath("Other.CustomRecipes." + recipe + ".Material." + itemSlot + ".HideEnchants");
            }
            if (OtherFile.isPathSet("Other.CustomRecipes." + recipe + ".Material." + itemSlot + ".HideUnbreakable")) {
                hideUnbreakable = OtherFile.getBooleanPath("Other.CustomRecipes." + recipe + ".Material." + itemSlot + ".HideUnbreakable");
            }
            //Book
            if (OtherFile.isPathSet("Other.CustomRecipes." + recipe + ".Material." + itemSlot + ".BookPages")) {
                writtenBookPages = OtherFile.getStringListPath("Other.CustomRecipes." + recipe + ".Material." + itemSlot + ".BookPages");
            }
            if (OtherFile.isPathSet("Other.CustomRecipes." + recipe + ".Material." + itemSlot + ".Author")) {
                author = OtherFile.getStringPath("Other.CustomRecipes." + recipe + ".Material." + itemSlot + ".Author");
            }
            //Leather Armor
            if (OtherFile.isPathSet("Other.CustomRecipes." + recipe + ".Material." + itemSlot + ".Color")) {
                color = OtherFile.getStringPath("Other.CustomRecipes." + recipe + ".Material." + itemSlot + ".Color");
            }
            //Skull
            if (OtherFile.isPathSet("Other.CustomRecipes." + recipe + ".Material." + itemSlot + ".SkullOwner")) {
                skullOwner = OtherFile.getStringPath("Other.CustomRecipes." + recipe + ".Material." + itemSlot + ".SkullOwner");
            }

            if (type.equalsIgnoreCase("WRITTEN_BOOK") || typeID == 387) {
                 data = ItemBuilder.createWrittenBook(amount, displayName, lore, enchantments, author, writtenBookPages).getData();
            } else if (type.equalsIgnoreCase("LEATHER_BOOTS") || typeID == 301 || type.equalsIgnoreCase("LEATHER_LEGGINGS") || typeID == 300
                    || type.equalsIgnoreCase("LEATHER_CHESTPLATE") || typeID == 299 || type.equalsIgnoreCase("LEATHER_HELMET") || typeID == 298) {
                data = ItemBuilder.createLeatherArmor(type, damage, amount, displayName, lore, enchantments, unbreakable, hideEnchants, hideUnbreakable, color).getData();
            } else if (type.equalsIgnoreCase("SKULL_ITEM") || type.equalsIgnoreCase("SKULL") || typeID == 397) {
                data = ItemBuilder.createSkull("SKULL_ITEM", amount, displayName, lore, enchantments, unbreakable, hideEnchants, hideUnbreakable, skullOwner).getData();
            } else {
                if (!type.equalsIgnoreCase("AIR")) {
                    data = ItemBuilder.createItem(type, damage, amount, displayName, lore, enchantments, unbreakable, hideEnchants, hideUnbreakable).getData();
                } else {
                    data = ItemBuilder.createItem(typeID, damage, amount, displayName, lore, enchantments, unbreakable, hideEnchants, hideUnbreakable).getData();
                }
            }
            materials.put(itemSlot, data);
        }
        return materials;
    }

}
