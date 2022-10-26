package me.newdavis.spigot.listener;

import me.newdavis.spigot.command.ItemEditCmd;
import me.newdavis.spigot.file.ListenerFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class DeathDropListener implements Listener {

    private static final List<ItemStack> items = new ArrayList<>();

    public void init() {
        if(ListenerFile.isPathSet("Listener.DeathDrop.Items")) {
            for (String item : ListenerFile.getConfigurationSection("Listener.DeathDrop.Items")) {
                ItemStack itemStack = new ItemStack(Material.GRASS);
                ItemMeta itemMeta = itemStack.getItemMeta();

                Material material = null;
                if(ListenerFile.isPathSet("Listener.DeathDrop.Items." + item + ".Type")) {
                    String type = ListenerFile.getStringPath("Listener.DeathDrop.Items." + item + ".Type");
                    try {
                        material = Material.getMaterial(Integer.parseInt(type));
                    } catch (NumberFormatException ignored) {
                        material = Material.getMaterial(type);
                    }
                    if(material != null) {
                        itemStack.setType(material);
                    }
                }

                if(material != null) {
                    if (ListenerFile.isPathSet("Listener.DeathDrop.Items." + item + ".SubID")) {
                        short subID = ListenerFile.getShortPath("Listener.DeathDrop.Items." + item + ".SubID");
                        if (subID != 0) {
                            itemStack.setDurability(subID);
                        }
                    } else if (ListenerFile.isPathSet("Listener.DeathDrop.Items." + item + ".Damage")) {
                        short subID = ListenerFile.getShortPath("Listener.DeathDrop.Items." + item + ".Damage");
                        if (subID != 0) {
                            itemStack.setDurability(subID);
                        }
                    }

                    if (ListenerFile.isPathSet("Listener.DeathDrop.Items." + item + ".Amount")) {
                        int amount = ListenerFile.getIntegerPath("Listener.DeathDrop.Items." + item + ".Amount");
                        if (amount != 0) {
                            itemStack.setAmount(amount);
                        }
                    }

                    if (ListenerFile.isPathSet("Listener.DeathDrop.Items." + item + ".DisplayName")) {
                        String displayName = ListenerFile.getStringPath("Listener.DeathDrop.Items." + item + ".DisplayName");
                        if (!displayName.equalsIgnoreCase("")) {
                            itemMeta.setDisplayName(displayName);
                        }
                    }

                    if (ListenerFile.isPathSet("Listener.DeathDrop.Items." + item + ".Lore")) {
                        List<String> lore = ListenerFile.getStringListPath("Listener.DeathDrop.Items." + item + ".Lore");
                        if (!lore.isEmpty()) {
                            itemMeta.setLore(lore);
                        }
                    }

                    if (ListenerFile.isPathSet("Listener.DeathDrop.Items." + item + ".Enchantments")) {
                        for (String enchantment : ListenerFile.getConfigurationSection("Listener.DeathDrop.Items." + item + ".Enchantments")) {
                            Enchantment enchant = ItemEditCmd.getEnchantmentByName(enchantment);
                            int level = ListenerFile.getIntegerPath("Listener.DeathDrop.Items." + item + ".Enchantments." + enchantment);
                            itemMeta.addEnchant(enchant, level, true);
                        }
                    }

                    if (ListenerFile.isPathSet("Listener.DeathDrop.Items." + item + ".Unbreakable")) {
                        if (ListenerFile.getBooleanPath("Listener.DeathDrop.Items." + item + ".Unbreakable")) {
                            itemMeta.spigot().setUnbreakable(true);
                        }
                    }

                    if (ListenerFile.isPathSet("Listener.DeathDrop.Items." + item + ".HideUnbreakable")) {
                        if (ListenerFile.getBooleanPath("Listener.DeathDrop.Items." + item + ".HideUnbreakable")) {
                            itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                        }
                    }

                    if (ListenerFile.isPathSet("Listener.DeathDrop.Items." + item + ".HideEnchantments")) {
                        if (ListenerFile.getBooleanPath("Listener.DeathDrop.Items." + item + ".HideEnchantments")) {
                            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        }
                    }

                    itemStack.setItemMeta(itemMeta);
                    items.add(itemStack);
                }
            }
        }
        NewSystem.getInstance().getServer().getPluginManager().registerEvents(this, NewSystem.getInstance());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();

        if(ListenerFile.getBooleanPath("Listener.DeathDrop.Enabled")) {
            if(ListenerFile.getBooleanPath("Listener.DeathDrop.ClearDrops")) {
                e.getDrops().clear();
            }
            if (ListenerFile.isPathSet("Listener.DeathDrop.Items")) {
                for (ItemStack item : items) {
                    p.getWorld().dropItemNaturally(p.getLocation(), item);
                }
            }
        }
    }

}
