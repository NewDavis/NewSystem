package me.newdavis.spigot.plugin.newsystem;

import me.newdavis.spigot.plugin.newsystem.inventory.command.ChangeValueCommand;
import me.newdavis.spigot.plugin.newsystem.inventory.listener.ChangeValueListener;
import me.newdavis.spigot.plugin.newsystem.inventory.listener.ListenerChoosedInventory;
import me.newdavis.spigot.plugin.newsystem.inventory.kit.ChangeValueKit;
import me.newdavis.spigot.plugin.newsystem.inventory.kit.KitChoosedInventory;
import me.newdavis.spigot.plugin.newsystem.inventory.other.ChangeValueOther;
import me.newdavis.spigot.plugin.newsystem.inventory.settings.ChangeValueSettings;
import me.newdavis.spigot.plugin.newsystem.inventory.tablist.ChangeValueTabList;
import me.newdavis.spigot.plugin.newsystem.inventory.command.CommandChoosedInventory;
import me.newdavis.spigot.plugin.newsystem.inventory.other.OtherChoosedInventory;
import me.newdavis.spigot.plugin.newsystem.inventory.settings.SettingsFileInventory;
import me.newdavis.spigot.plugin.newsystem.inventory.tablist.TabListChoosedInventory;
import org.bukkit.entity.Player;

public class Other {

    public static void deleteOther(Player p, String current) {
        if(current.equalsIgnoreCase("Command")) {
            ListenerChoosedInventory.listener.remove(p);
            ListenerChoosedInventory.page.remove(p);
            ChangeValueListener.pathHM.remove(p);
            ChangeValueListener.indexHM.remove(p);

            KitChoosedInventory.kit.remove(p);
            KitChoosedInventory.page.remove(p);
            ChangeValueKit.pathHM.remove(p);
            ChangeValueKit.indexHM.remove(p);

            OtherChoosedInventory.other.remove(p);
            OtherChoosedInventory.page.remove(p);
            ChangeValueOther.pathHM.remove(p);
            ChangeValueOther.indexHM.remove(p);

            TabListChoosedInventory.tablist.remove(p);
            TabListChoosedInventory.page.remove(p);
            ChangeValueTabList.pathHM.remove(p);

            SettingsFileInventory.page.remove(p);
            ChangeValueSettings.pathHM.remove(p);
        }else if(current.equalsIgnoreCase("Listener")) {
            CommandChoosedInventory.command.remove(p);
            CommandChoosedInventory.page.remove(p);
            ChangeValueCommand.pathHM.remove(p);
            ChangeValueCommand.indexHM.remove(p);

            KitChoosedInventory.kit.remove(p);
            KitChoosedInventory.page.remove(p);
            ChangeValueKit.pathHM.remove(p);
            ChangeValueKit.indexHM.remove(p);

            OtherChoosedInventory.other.remove(p);
            OtherChoosedInventory.page.remove(p);
            ChangeValueOther.pathHM.remove(p);
            ChangeValueOther.indexHM.remove(p);

            TabListChoosedInventory.tablist.remove(p);
            TabListChoosedInventory.page.remove(p);
            ChangeValueTabList.pathHM.remove(p);

            SettingsFileInventory.page.remove(p);
            ChangeValueSettings.pathHM.remove(p);
        }else if(current.equalsIgnoreCase("Kit")) {
            CommandChoosedInventory.command.remove(p);
            CommandChoosedInventory.page.remove(p);
            ChangeValueCommand.pathHM.remove(p);
            ChangeValueCommand.indexHM.remove(p);

            ListenerChoosedInventory.listener.remove(p);
            ListenerChoosedInventory.page.remove(p);
            ChangeValueListener.pathHM.remove(p);
            ChangeValueListener.indexHM.remove(p);

            OtherChoosedInventory.other.remove(p);
            OtherChoosedInventory.page.remove(p);
            ChangeValueOther.pathHM.remove(p);
            ChangeValueOther.indexHM.remove(p);

            TabListChoosedInventory.tablist.remove(p);
            TabListChoosedInventory.page.remove(p);
            ChangeValueTabList.pathHM.remove(p);

            SettingsFileInventory.page.remove(p);
            ChangeValueSettings.pathHM.remove(p);
        }else if(current.equalsIgnoreCase("Other")) {
            CommandChoosedInventory.command.remove(p);
            CommandChoosedInventory.page.remove(p);
            ChangeValueCommand.pathHM.remove(p);
            ChangeValueCommand.indexHM.remove(p);

            KitChoosedInventory.kit.remove(p);
            KitChoosedInventory.page.remove(p);
            ChangeValueKit.pathHM.remove(p);
            ChangeValueKit.indexHM.remove(p);

            ListenerChoosedInventory.listener.remove(p);
            ListenerChoosedInventory.page.remove(p);
            ChangeValueListener.pathHM.remove(p);
            ChangeValueListener.indexHM.remove(p);

            TabListChoosedInventory.tablist.remove(p);
            TabListChoosedInventory.page.remove(p);
            ChangeValueTabList.pathHM.remove(p);

            SettingsFileInventory.page.remove(p);
            ChangeValueSettings.pathHM.remove(p);
        }else if(current.equalsIgnoreCase("TabList")) {
            CommandChoosedInventory.command.remove(p);
            CommandChoosedInventory.page.remove(p);
            ChangeValueCommand.pathHM.remove(p);
            ChangeValueCommand.indexHM.remove(p);

            KitChoosedInventory.kit.remove(p);
            KitChoosedInventory.page.remove(p);
            ChangeValueKit.pathHM.remove(p);
            ChangeValueKit.indexHM.remove(p);

            OtherChoosedInventory.other.remove(p);
            OtherChoosedInventory.page.remove(p);
            ChangeValueOther.pathHM.remove(p);
            ChangeValueOther.indexHM.remove(p);

            ListenerChoosedInventory.listener.remove(p);
            ListenerChoosedInventory.page.remove(p);
            ChangeValueListener.pathHM.remove(p);
            ChangeValueListener.indexHM.remove(p);

            SettingsFileInventory.page.remove(p);
            ChangeValueSettings.pathHM.remove(p);
        }else if(current.equalsIgnoreCase("Settings")) {
            CommandChoosedInventory.command.remove(p);
            CommandChoosedInventory.page.remove(p);
            ChangeValueCommand.pathHM.remove(p);
            ChangeValueCommand.indexHM.remove(p);

            KitChoosedInventory.kit.remove(p);
            KitChoosedInventory.page.remove(p);
            ChangeValueKit.pathHM.remove(p);
            ChangeValueKit.indexHM.remove(p);

            OtherChoosedInventory.other.remove(p);
            OtherChoosedInventory.page.remove(p);
            ChangeValueOther.pathHM.remove(p);
            ChangeValueOther.indexHM.remove(p);

            ListenerChoosedInventory.listener.remove(p);
            ListenerChoosedInventory.page.remove(p);
            ChangeValueListener.pathHM.remove(p);
            ChangeValueListener.indexHM.remove(p);

            TabListChoosedInventory.tablist.remove(p);
            TabListChoosedInventory.page.remove(p);
            ChangeValueTabList.pathHM.remove(p);
        }
    }

}
