package me.newdavis.spigot.listener;

import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.file.ListenerFile;
import me.newdavis.spigot.file.SavingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import me.newdavis.spigot.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class FreeItemSignListener implements Listener {

    private static String perm;
    private static String title;
    private static String titleReplace;
    private static int defaultAmount;
    private static String rightClickSign;
    private static String colorItem;
    private static String colorAmount;
    private static String noItemInHand;
    private static boolean itemGivenMessageEnabled;
    private static String itemGivenMessage;
    private static boolean inventoryEnabled;
    private static String inventoryTitle;
    private static int inventoryRows;
    private static String[] itemSlots;

    public void init() {
        perm = ListenerFile.getStringPath("Listener.FreeItemSign.Permission");
        title = ListenerFile.getStringPath("Listener.FreeItemSign.Title");
        titleReplace = ListenerFile.getStringPath("Listener.FreeItemSign.TitleReplace");
        defaultAmount = ListenerFile.getIntegerPath("Listener.FreeItemSign.DefaultAmount");
        rightClickSign = ListenerFile.getStringPath("Listener.FreeItemSign.RightClickSign");
        colorItem = ListenerFile.getStringPath("Listener.FreeItemSign.Color.Item");
        colorAmount = ListenerFile.getStringPath("Listener.FreeItemSign.Color.Amount");
        noItemInHand = ListenerFile.getStringPath("Listener.FreeItemSign.NoItemInHand").replace("{Prefix}", SettingsFile.getPrefix());
        itemGivenMessageEnabled = ListenerFile.getBooleanPath("Listener.FreeItemSign.ItemGivenMessageEnabled");
        itemGivenMessage = ListenerFile.getStringPath("Listener.FreeItemSign.ItemGivenMessage").replace("{Prefix}", SettingsFile.getPrefix());
        inventoryEnabled = ListenerFile.getBooleanPath("Listener.FreeItemSign.Inventory.Enabled");
        inventoryTitle = ListenerFile.getStringPath("Listener.FreeItemSign.Inventory.Title").replace("{Prefix}", SettingsFile.getPrefix());
        inventoryRows = ListenerFile.getIntegerPath("Listener.FreeItemSign.Inventory.Rows");
        itemSlots = ListenerFile.getStringPath("Listener.FreeItemSign.Inventory.ItemSlots").split("\\|");
        NewSystem.getInstance().getServer().getPluginManager().registerEvents(this, NewSystem.getInstance());
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        Player p = e.getPlayer();
        if(NewSystem.hasPermission(p, perm)) {
            String titleLine = e.getLine(0);
            String itemLine = e.getLine(1);
            String amountLine = e.getLine(2);

            if(titleLine.equalsIgnoreCase(title)) {
                if(!itemLine.equals("")) {
                    if(!amountLine.equals("")) {
                        e.setLine(0, titleReplace);
                        Material material;
                        try{
                            material = ItemBuilder.getMaterial(Integer.parseInt(itemLine));
                        }catch (NumberFormatException exception) {
                            material = ItemBuilder.getMaterial(itemLine);
                        }
                        e.setLine(1, colorItem + material.name());
                        try{
                            defaultAmount = Integer.parseInt(amountLine);
                        }catch (NumberFormatException ignored){}
                        e.setLine(2, colorAmount + defaultAmount);
                        createSign(e.getBlock().getLocation(), new ItemStack(material, defaultAmount));
                    }else{
                        e.setLine(0, titleReplace);
                        Material material;
                        try{
                            material = ItemBuilder.getMaterial(Integer.parseInt(itemLine));
                        }catch (NumberFormatException exception) {
                            material = ItemBuilder.getMaterial(itemLine);
                        }
                        e.setLine(1, colorItem + material.name());
                        e.setLine(2, colorAmount + defaultAmount);

                        createSign(e.getBlock().getLocation(), new ItemStack(material));
                    }
                }else{
                    e.setLine(0, titleReplace);
                    e.setLine(1, rightClickSign);
                    e.setLine(2, colorAmount + defaultAmount);
                }
            }
        }
    }

    @EventHandler
    public void onRightClickSign(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getClickedBlock().getType() == Material.SIGN || e.getClickedBlock().getType() == Material.SIGN_POST || e.getClickedBlock().getType() == Material.WALL_SIGN) {
                Sign sign = (Sign) e.getClickedBlock().getState();
                String titleLine = sign.getLine(0);
                String itemLine = sign.getLine(1);
                String amountLine = sign.getLine(2).replace(colorAmount, "");
                try{
                    defaultAmount = Integer.parseInt(amountLine);
                }catch (NumberFormatException exception) {}

                if(titleLine.equalsIgnoreCase(titleReplace)) {
                    if (itemLine.equalsIgnoreCase(rightClickSign)) {
                        if(NewSystem.hasPermission(p, perm)) {
                            ItemStack item = p.getItemInHand();
                            if (item != null && item.getType() != Material.AIR) {
                                String itemName = (item.getItemMeta().getDisplayName() != null ? item.getItemMeta().getDisplayName() : item.getType().name());
                                sign.setLine(1, colorItem + itemName);
                                sign.update();
                                createSign(e.getClickedBlock().getLocation(), item);
                            } else {
                                p.sendMessage(noItemInHand);
                            }
                        }
                    }else{
                        ItemStack item = getItemStack(e.getClickedBlock().getLocation(), defaultAmount);
                        String itemName = (item.getItemMeta().getDisplayName() != null ? item.getItemMeta().getDisplayName() : item.getType().name());
                        if(inventoryEnabled) {
                            Inventory inventory = Bukkit.createInventory(null, 9*inventoryRows, inventoryTitle.replace("{Item-Name}", colorItem + itemName));

                            for(String itemSlot : itemSlots) {
                                inventory.setItem((Integer.parseInt(itemSlot)-1), item);
                            }

                            p.openInventory(inventory);
                        }else{
                            p.getInventory().addItem(item);
                            if(itemGivenMessageEnabled) {
                                p.sendMessage(itemGivenMessage.replace("{Item-Name}", colorItem + itemName).replace("{Amount}", "x" + defaultAmount));
                            }
                        }
                    }
                }
            }
        }
    }

    private static void createSign(Location location, ItemStack item) {
        SavingsFile.setPath("FreeItemSign.Sign." + location.getX() + "." + location.getY() + "." + location.getZ() + "." + location.getWorld().getName() + ".Location.X", location.getBlockX());
        SavingsFile.setPath("FreeItemSign.Sign." + location.getX() + "." + location.getY() + "." + location.getZ() + "." + location.getWorld().getName() + ".Location.Y", location.getBlockY());
        SavingsFile.setPath("FreeItemSign.Sign." + location.getX() + "." + location.getY() + "." + location.getZ() + "." + location.getWorld().getName() + ".Location.Z", location.getBlockZ());
        SavingsFile.setPath("FreeItemSign.Sign." + location.getX() + "." + location.getY() + "." + location.getZ() + "." + location.getWorld().getName() + ".Location.World", location.getWorld().getName());
        SavingsFile.setPath("FreeItemSign.Sign." + location.getX() + "." + location.getY() + "." + location.getZ() + "." + location.getWorld().getName() + ".Item", item);
    }

    private static ItemStack getItemStack(Location location, int amount) {
        ItemStack item = SavingsFile.getItemStack("FreeItemSign.Sign." + location.getX() + "." + location.getY() + "." + location.getZ() + "." + location.getWorld().getName() + ".Item");
        item.setAmount(amount);
        return item;
    }

}
