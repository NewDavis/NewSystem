package me.newdavis.spigot.listener;

import me.newdavis.spigot.file.ListenerFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class ColorSignListener implements Listener {

    private static String perm;

    public ColorSignListener() {
        perm = ListenerFile.getStringPath("Listener.ColorSign.Permission");
        if(!NewSystem.loadedListeners.contains(this.getClass())) {
            NewSystem.loadedListeners.add(this.getClass());
            NewSystem.getInstance().getServer().getPluginManager().registerEvents(this, NewSystem.getInstance());
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        Player p = e.getPlayer();
        String[] lines = e.getLines();
        if(lines[0].contains("&") || lines[1].contains("&") || lines[2].contains("&") || lines[3].contains("&")) {
            if(NewSystem.hasPermission(p, perm)) {
                e.setLine(0, ChatColor.translateAlternateColorCodes('&', lines[0]));
                e.setLine(1, ChatColor.translateAlternateColorCodes('&', lines[1]));
                e.setLine(2, ChatColor.translateAlternateColorCodes('&', lines[2]));
                e.setLine(3, ChatColor.translateAlternateColorCodes('&', lines[3]));
            }
        }
    }

}
