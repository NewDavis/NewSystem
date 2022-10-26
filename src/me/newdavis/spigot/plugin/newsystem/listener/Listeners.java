package me.newdavis.spigot.plugin.newsystem.listener;

import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import me.newdavis.spigot.plugin.newsystem.inventory.command.ChangeValueCommand;
import me.newdavis.spigot.plugin.newsystem.inventory.command.CommandChoosedInventory;
import me.newdavis.spigot.plugin.newsystem.inventory.command.CommandFileInventory;
import me.newdavis.spigot.plugin.newsystem.inventory.listener.ChangeValueListener;
import me.newdavis.spigot.plugin.newsystem.inventory.listener.ListenerChoosedInventory;
import me.newdavis.spigot.plugin.newsystem.inventory.listener.ListenerFileInventory;
import me.newdavis.spigot.plugin.newsystem.inventory.kit.ChangeValueKit;
import me.newdavis.spigot.plugin.newsystem.inventory.kit.KitChoosedInventory;
import me.newdavis.spigot.plugin.newsystem.inventory.kit.KitFileInventory;
import me.newdavis.spigot.plugin.newsystem.inventory.other.ChangeValueOther;
import me.newdavis.spigot.plugin.newsystem.inventory.other.OtherFileInventory;
import me.newdavis.spigot.plugin.newsystem.inventory.settings.ChangeValueSettings;
import me.newdavis.spigot.plugin.newsystem.inventory.settings.SettingChoosedInventory;
import me.newdavis.spigot.plugin.newsystem.inventory.settings.SettingsFileInventory;
import me.newdavis.spigot.plugin.newsystem.inventory.tablist.ChangeValueTabList;
import me.newdavis.spigot.plugin.newsystem.inventory.ChooseFileInventory;
import me.newdavis.spigot.plugin.newsystem.inventory.other.OtherChoosedInventory;
import me.newdavis.spigot.plugin.newsystem.inventory.tablist.TabListChoosedInventory;
import me.newdavis.spigot.plugin.newsystem.inventory.tablist.TabListFileInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Listeners implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory inventory = e.getClickedInventory();

        if (inventory != null && inventory.getType() != null && inventory.getType() != InventoryType.CHEST) {
            return;
        }
        int clickedSlot = e.getSlot();
        ItemStack clickedItem = e.getCurrentItem();

        String inventoryTitle = "";
        if(e.getView().getTitle() != null) {
            inventoryTitle = e.getView().getTitle();
        }else{
            return;
        }
        String titleChooseFile = new ChooseFileInventory().TITLE;

        String titleCommandFile = new CommandFileInventory().TITLE;
        String titleCommandChoosed = new CommandChoosedInventory().TITLE;
        String titleListenerFile = new ListenerFileInventory().TITLE;
        String titleListenerChoosed = new ListenerChoosedInventory().TITLE;
        String titleKitFile = new KitFileInventory().TITLE;
        String titleKitChoosed = new KitChoosedInventory().TITLE;
        String titleOtherFile = new OtherFileInventory().TITLE;
        String titleOtherChoosed = new OtherChoosedInventory().TITLE;
        String titleTabListFile = new TabListFileInventory().TITLE;
        String titleTabListChoosed = new TabListChoosedInventory().TITLE;
        String titleSettingsFile = new SettingsFileInventory().TITLE;
        String titleSettingChoosed = new SettingChoosedInventory().TITLE;

        if (inventoryTitle.equalsIgnoreCase(titleChooseFile)) {
            e.setCancelled(true);
            if (checkItem(clickedItem)) {
                String[] displayName = clickedItem.getItemMeta().getDisplayName().split(" ");
                String file;
                if (displayName.length > 1) {
                    file = displayName[1].replace("§f", "").replace(".yml", "");
                    if (file.equalsIgnoreCase("Command")) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(NewSystem.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                new CommandFileInventory().openInventoryPage(p, 1);
                            }
                        }, 2);
                    } else if (file.equalsIgnoreCase("Listener")) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(NewSystem.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                new ListenerFileInventory().openInventoryPage(p, 1);
                            }
                        }, 2);
                    } else if (file.equalsIgnoreCase("Other")) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(NewSystem.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                new OtherFileInventory().openInventoryPage(p, 1);
                            }
                        }, 2);
                    } else if (file.equalsIgnoreCase("Kit")) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(NewSystem.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                new KitFileInventory().openInventoryPage(p, 1);
                            }
                        }, 2);
                    } else if (file.equalsIgnoreCase("TabList")) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(NewSystem.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                new TabListFileInventory().openInventoryPage(p, 1);
                            }
                        }, 2);
                    } else if (file.equalsIgnoreCase("Settings")) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(NewSystem.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                new SettingsFileInventory().openInventoryPage(p, 1);
                            }
                        }, 2);
                    }
                }
            }
        } else if (inventoryTitle.equalsIgnoreCase(titleCommandChoosed)) {
            e.setCancelled(true);
            if (clickedSlot == 45) {
                new CommandFileInventory().openInventoryPage(p, 1);
            } else if (clickedSlot == 48) {
                int page = CommandChoosedInventory.page.get(p);
                if (page != 1) {
                    String command = CommandChoosedInventory.command.get(p);
                    new CommandChoosedInventory().openInventoryPage(p, command, page - 1);
                } else {
                    p.sendMessage(SettingsFile.getPrefix() + " §cThis page does not exist!");
                }
            } else if (clickedSlot == 49) {
                return;
            } else if (clickedSlot == 50) {
                int page = CommandChoosedInventory.page.get(p);
                String command = CommandChoosedInventory.command.get(p);
                new CommandChoosedInventory().openInventoryPage(p, command, page + 1);
            } else {
                if (checkItem(clickedItem)) {
                    p.closeInventory();
                    String path = clickedItem.getItemMeta().getDisplayName().replace("§7", "");
                    ChangeValueCommand.pathHM.put(p, path);
                    if (!ChangeValueCommand.isList(p)) {
                        p.sendMessage(SettingsFile.getPrefix() + " §7Please insert the new value.");
                        p.sendMessage(SettingsFile.getPrefix() + " §7To delete the value, write §cdelete§7!");
                        p.sendMessage(SettingsFile.getPrefix() + " §7To cancel this process, write §ccancel§7!");
                    } else {
                        p.sendMessage(SettingsFile.getPrefix() + " §7Please insert an index.");
                        p.sendMessage(SettingsFile.getPrefix() + " §7To cancel this process, write §ccancel§7!");
                    }
                }
            }
        } else if (inventoryTitle.equalsIgnoreCase(titleCommandFile)) {
            e.setCancelled(true);
            if (clickedSlot == 45) {
                new ChooseFileInventory().openInventory(p);
            } else if (clickedSlot == 48) {
                int page = CommandFileInventory.page.get(p);
                if (page != 1) {
                    new CommandFileInventory().openInventoryPage(p, page - 1);
                } else {
                    p.sendMessage(SettingsFile.getPrefix() + " §cDiese Seite existiert nicht!");
                }
            } else if (clickedSlot == 49) {
                return;
            } else if (clickedSlot == 50) {
                int page = CommandFileInventory.page.get(p);
                new CommandFileInventory().openInventoryPage(p, page + 1);
            } else {
                if (checkItem(clickedItem)) {
                    String command = clickedItem.getItemMeta().getDisplayName().replace("§7Command: ", "");
                    new CommandChoosedInventory().openInventoryPage(p, command, 1);
                }
            }
        } else if (inventoryTitle.equalsIgnoreCase(titleListenerChoosed)) {
            e.setCancelled(true);
            if (clickedSlot == 45) {
                new ListenerFileInventory().openInventoryPage(p, 1);
            } else if (clickedSlot == 48) {
                int page = ListenerChoosedInventory.page.get(p);
                if (page != 1) {
                    String listener = ListenerChoosedInventory.listener.get(p);
                    new ListenerChoosedInventory().openInventoryPage(p, listener, page - 1);
                } else {
                    p.sendMessage(SettingsFile.getPrefix() + " §cDiese Seite existiert nicht!");
                }
            } else if (clickedSlot == 49) {
                return;
            } else if (clickedSlot == 50) {
                int page = ListenerChoosedInventory.page.get(p);
                String listener = ListenerChoosedInventory.listener.get(p);
                new ListenerChoosedInventory().openInventoryPage(p, listener, page + 1);
            } else {
                if (checkItem(clickedItem)) {
                    p.closeInventory();
                    String path = clickedItem.getItemMeta().getDisplayName().replace("§7", "");
                    ChangeValueListener.pathHM.put(p, path);
                    if (!ChangeValueListener.isList(p)) {
                        p.sendMessage(SettingsFile.getPrefix() + " §7Please insert the new value.");
                        p.sendMessage(SettingsFile.getPrefix() + " §7To delete the value, write §cdelete§7!");
                        p.sendMessage(SettingsFile.getPrefix() + " §7To cancel this process, write §ccancel§7!");
                    } else {
                        p.sendMessage(SettingsFile.getPrefix() + " §7Please insert an index.");
                        p.sendMessage(SettingsFile.getPrefix() + " §7To cancel this process, write §ccancel§7!");
                    }
                }
            }
        } else if (inventoryTitle.equalsIgnoreCase(titleListenerFile)) {
            e.setCancelled(true);
            if (clickedSlot == 45) {
                new ChooseFileInventory().openInventory(p);
            } else if (clickedSlot == 48) {
                int page = ListenerFileInventory.page.get(p);
                if (page != 1) {
                    new ListenerFileInventory().openInventoryPage(p, page - 1);
                } else {
                    p.sendMessage(SettingsFile.getPrefix() + " §cDiese Seite existiert nicht!");
                }
            } else if (clickedSlot == 49) {
                return;
            } else if (clickedSlot == 50) {
                int page = ListenerFileInventory.page.get(p);
                new ListenerFileInventory().openInventoryPage(p, page + 1);
            } else {
                if (checkItem(clickedItem)) {
                    String listener = clickedItem.getItemMeta().getDisplayName().replace("§7Listener: ", "");
                    new ListenerChoosedInventory().openInventoryPage(p, listener, 1);
                }
            }
        } else if (inventoryTitle.equalsIgnoreCase(titleKitChoosed)) {
            e.setCancelled(true);
            if (clickedSlot == 45) {
                new KitFileInventory().openInventoryPage(p, 1);
            } else if (clickedSlot == 48) {
                int page = KitChoosedInventory.page.get(p);
                if (page != 1) {
                    String kit = KitChoosedInventory.kit.get(p);
                    new KitChoosedInventory().openInventoryPage(p, kit, page - 1);
                } else {
                    p.sendMessage(SettingsFile.getPrefix() + " §cDiese Seite existiert nicht!");
                }
            } else if (clickedSlot == 49) {
                return;
            } else if (clickedSlot == 50) {
                int page = KitChoosedInventory.page.get(p);
                String kit = KitChoosedInventory.kit.get(p);
                new KitChoosedInventory().openInventoryPage(p, kit, page + 1);
            } else {
                if (checkItem(clickedItem)) {
                    p.closeInventory();
                    String path = clickedItem.getItemMeta().getDisplayName().replace("§7", "");
                    ChangeValueKit.pathHM.put(p, path);
                    if (!ChangeValueKit.isList(p)) {
                        p.sendMessage(SettingsFile.getPrefix() + " §7Please insert the new value.");
                        p.sendMessage(SettingsFile.getPrefix() + " §7To delete the value, write §cdelete§7!");
                        p.sendMessage(SettingsFile.getPrefix() + " §7To cancel this process, write §ccancel§7!");
                    } else {
                        p.sendMessage(SettingsFile.getPrefix() + " §7Please insert an index.");
                        p.sendMessage(SettingsFile.getPrefix() + " §7To cancel this process, write §ccancel§7!");
                    }
                }
            }
        } else if (inventoryTitle.equalsIgnoreCase(titleKitFile)) {
            e.setCancelled(true);
            if (clickedSlot == 45) {
                new ChooseFileInventory().openInventory(p);
            } else if (clickedSlot == 48) {
                int page = KitFileInventory.page.get(p);
                if (page != 1) {
                    new KitFileInventory().openInventoryPage(p, page - 1);
                } else {
                    p.sendMessage(SettingsFile.getPrefix() + " §cDiese Seite existiert nicht!");
                }
            } else if (clickedSlot == 49) {
                return;
            } else if (clickedSlot == 50) {
                int page = KitFileInventory.page.get(p);
                new KitFileInventory().openInventoryPage(p, page + 1);
            } else {
                if (checkItem(clickedItem)) {
                    String kit = clickedItem.getItemMeta().getDisplayName().replace("§7Kit: ", "");
                    new KitChoosedInventory().openInventoryPage(p, kit, 1);
                }
            }
        } else if (inventoryTitle.equalsIgnoreCase(titleOtherChoosed)) {
            e.setCancelled(true);
            if (clickedSlot == 45) {
                new OtherFileInventory().openInventoryPage(p, 1);
            } else if (clickedSlot == 48) {
                int page = OtherChoosedInventory.page.get(p);
                if (page != 1) {
                    String other = OtherChoosedInventory.other.get(p);
                    new OtherChoosedInventory().openInventoryPage(p, other, page - 1);
                } else {
                    p.sendMessage(SettingsFile.getPrefix() + " §cDiese Seite existiert nicht!");
                }
            } else if (clickedSlot == 49) {
                return;
            } else if (clickedSlot == 50) {
                int page = OtherChoosedInventory.page.get(p);
                String other = OtherChoosedInventory.other.get(p);
                new OtherChoosedInventory().openInventoryPage(p, other, page + 1);
            } else {
                if (checkItem(clickedItem)) {
                    p.closeInventory();
                    String path = clickedItem.getItemMeta().getDisplayName().replace("§7", "");
                    ChangeValueOther.pathHM.put(p, path);
                    if (!ChangeValueOther.isList(p)) {
                        p.sendMessage(SettingsFile.getPrefix() + " §7Please insert the new value.");
                        p.sendMessage(SettingsFile.getPrefix() + " §7To delete the value, write §cdelete§7!");
                        p.sendMessage(SettingsFile.getPrefix() + " §7To cancel this process, write §ccancel§7!");
                    } else {
                        p.sendMessage(SettingsFile.getPrefix() + " §7Please insert an index.");
                        p.sendMessage(SettingsFile.getPrefix() + " §7To cancel this process, write §ccancel§7!");
                    }
                }
            }
        } else if (inventoryTitle.equalsIgnoreCase(titleOtherFile)) {
            e.setCancelled(true);
            if (clickedSlot == 45) {
                new ChooseFileInventory().openInventory(p);
            } else if (clickedSlot == 48) {
                int page = OtherFileInventory.page.get(p);
                if (page != 1) {
                    new OtherFileInventory().openInventoryPage(p, page - 1);
                } else {
                    p.sendMessage(SettingsFile.getPrefix() + " §cDiese Seite existiert nicht!");
                }
            } else if (clickedSlot == 49) {
                return;
            } else if (clickedSlot == 50) {
                int page = OtherFileInventory.page.get(p);
                new OtherFileInventory().openInventoryPage(p, page + 1);
            } else {
                if (checkItem(clickedItem)) {
                    String other = clickedItem.getItemMeta().getDisplayName().replace("§7Other: ", "");
                    new OtherChoosedInventory().openInventoryPage(p, other, 1);
                }
            }
        } else if (inventoryTitle.equalsIgnoreCase(titleTabListChoosed)) {
            e.setCancelled(true);
            if (clickedSlot == 45) {
                new TabListFileInventory().openInventoryPage(p, 1);
            } else if (clickedSlot == 48) {
                int page = TabListChoosedInventory.page.get(p);
                if (page != 1) {
                    String tablist = TabListChoosedInventory.tablist.get(p);
                    new TabListChoosedInventory().openInventoryPage(p, tablist, page - 1);
                } else {
                    p.sendMessage(SettingsFile.getPrefix() + " §cDiese Seite existiert nicht!");
                }
            } else if (clickedSlot == 49) {
                return;
            } else if (clickedSlot == 50) {
                int page = TabListChoosedInventory.page.get(p);
                String tablist = TabListChoosedInventory.tablist.get(p);
                new TabListChoosedInventory().openInventoryPage(p, tablist, page + 1);
            } else {
                if (checkItem(clickedItem)) {
                    p.closeInventory();
                    String path = clickedItem.getItemMeta().getDisplayName().replace("§7", "");
                    ChangeValueTabList.pathHM.put(p, path);
                    p.sendMessage(SettingsFile.getPrefix() + " §7Please insert the new value.");
                    p.sendMessage(SettingsFile.getPrefix() + " §7To delete the value, write §cdelete§7!");
                    p.sendMessage(SettingsFile.getPrefix() + " §7To cancel this process, write §ccancel§7!");
                }
            }
        } else if (inventoryTitle.equalsIgnoreCase(titleTabListFile)) {
            e.setCancelled(true);
            if (clickedSlot == 45) {
                new ChooseFileInventory().openInventory(p);
            } else if (clickedSlot == 48) {
                int page = TabListFileInventory.page.get(p);
                if (page != 1) {
                    new TabListFileInventory().openInventoryPage(p, page - 1);
                } else {
                    p.sendMessage(SettingsFile.getPrefix() + " §cDiese Seite existiert nicht!");
                }
            } else if (clickedSlot == 49) {
                return;
            } else if (clickedSlot == 50) {
                int page = TabListFileInventory.page.get(p);
                new TabListFileInventory().openInventoryPage(p, page + 1);
            } else {
                if (checkItem(clickedItem)) {
                    String tablist = clickedItem.getItemMeta().getDisplayName().replace("TabList: ", "").replace("§7", "");
                    if (clickedItem.getType() == Material.BOOK) {
                        new TabListChoosedInventory().openInventoryPage(p, tablist, 1);
                    } else {
                        p.closeInventory();
                        ChangeValueTabList.pathHM.put(p, tablist);
                        p.sendMessage(SettingsFile.getPrefix() + " §7Please insert the new value.");
                        p.sendMessage(SettingsFile.getPrefix() + " §7To delete the value, write §cdelete§7!");
                        p.sendMessage(SettingsFile.getPrefix() + " §7To cancel this process, write §ccancel§7!");
                    }
                }
            }
        } else if (inventoryTitle.equalsIgnoreCase(titleSettingChoosed)) {
            e.setCancelled(true);
            if (clickedSlot == 45) {
                new SettingsFileInventory().openInventoryPage(p, 1);
            } else if (clickedSlot == 48) {
                int page = SettingChoosedInventory.page.get(p);
                if (page != 1) {
                    String setting = SettingChoosedInventory.setting.get(p);
                    new SettingChoosedInventory().openInventoryPage(p, setting, page - 1);
                } else {
                    p.sendMessage(SettingsFile.getPrefix() + " §cDiese Seite existiert nicht!");
                }
            } else if (clickedSlot == 49) {
                return;
            } else if (clickedSlot == 50) {
                int page = SettingChoosedInventory.page.get(p);
                String setting = SettingChoosedInventory.setting.get(p);
                new SettingChoosedInventory().openInventoryPage(p, setting, page + 1);
            } else {
                if (checkItem(clickedItem)) {
                    p.closeInventory();
                    String path = clickedItem.getItemMeta().getDisplayName().replace("§7", "");
                    ChangeValueSettings.pathHM.put(p, SettingChoosedInventory.setting.get(p) + "." + path);
                    p.sendMessage(SettingsFile.getPrefix() + " §7Please insert the new value.");
                    p.sendMessage(SettingsFile.getPrefix() + " §7To delete the value, write §cdelete§7!");
                    p.sendMessage(SettingsFile.getPrefix() + " §7To cancel this process, write §ccancel§7!");
                }
            }
        } else if (inventoryTitle.equalsIgnoreCase(titleSettingsFile)) {
            e.setCancelled(true);
            if (clickedSlot == 45) {
                new ChooseFileInventory().openInventory(p);
            } else if (clickedSlot == 48) {
                int page = SettingsFileInventory.page.get(p);
                if (page != 1) {
                    new SettingsFileInventory().openInventoryPage(p, page - 1);
                } else {
                    p.sendMessage(SettingsFile.getPrefix() + " §cDiese Seite existiert nicht!");
                }
            } else if (clickedSlot == 49) {
                return;
            } else if (clickedSlot == 50) {
                int page = SettingsFileInventory.page.get(p);
                new SettingsFileInventory().openInventoryPage(p, page + 1);
            } else {
                if (checkItem(clickedItem)) {
                    String setting = clickedItem.getItemMeta().getDisplayName().replace("Setting: ", "").replace("§7", "");
                    if (clickedItem.getType() == Material.BOOK) {
                        new SettingChoosedInventory().openInventoryPage(p, setting, 1);
                        ChangeValueSettings.pathHM.put(p, setting);
                    } else {
                        p.closeInventory();
                        ChangeValueSettings.pathHM.put(p, setting);
                        p.sendMessage(SettingsFile.getPrefix() + " §7Please insert the new value.");
                        p.sendMessage(SettingsFile.getPrefix() + " §7To delete the value, write §cdelete§7!");
                        p.sendMessage(SettingsFile.getPrefix() + " §7To cancel this process, write §ccancel§7!");
                    }
                }
            }
        }
    }

    public boolean checkItem(ItemStack item) {
        if (item != null && item.getType() != Material.AIR) {
            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                return true;
            }
        }
        return false;
    }
}
