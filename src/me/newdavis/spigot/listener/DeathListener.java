package me.newdavis.spigot.listener;
//Plugin by NewDavis

import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.file.ListenerFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DeathListener implements Listener {

    private static boolean enabled;
    private static List<String> deathMessageList;
    private static boolean forceRespawn;

    public void init() {
        enabled = ListenerFile.getBooleanPath("Listener.Death.Enabled");
        deathMessageList = ListenerFile.getStringListPath("Listener.Death.Message");
        forceRespawn = ListenerFile.getBooleanPath("Listener.Death.ForceRespawn");
        NewSystem.getInstance().getServer().getPluginManager().registerEvents(this, NewSystem.getInstance());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();

        if(enabled) {
            e.setDeathMessage("");
            if(deathMessageList.isEmpty()) {
                return;
            }
            for (String msg : deathMessageList) {
                Bukkit.broadcastMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(p)));
            }

            if(forceRespawn) {
                p.spigot().respawn();
            }
        }
    }
}
