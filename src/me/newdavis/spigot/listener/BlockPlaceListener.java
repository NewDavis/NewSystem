package me.newdavis.spigot.listener;

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.file.ListenerFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.List;

public class BlockPlaceListener implements Listener {

    private static String perm;
    private static List<String> message;

    public void init() {
        perm = ListenerFile.getStringPath("Listener.BlockPlace.Permission");
        message = ListenerFile.getStringListPath("Listener.BlockPlace.Message");
        NewSystem.getInstance().getServer().getPluginManager().registerEvents(this, NewSystem.getInstance());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();

        if(!CommandFile.getBooleanPath("Command.Build.Enabled")) {
            if (ListenerFile.getBooleanPath("Listener.BlockPlace.Enabled")) {
                if (!NewSystem.hasPermission(p, perm)) {
                    e.setCancelled(true);
                    for(String value : message) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }
        }
    }

}
