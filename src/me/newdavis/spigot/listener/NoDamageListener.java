package me.newdavis.spigot.listener;
//Plugin by NewDavis

import me.newdavis.spigot.file.ListenerFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class NoDamageListener implements Listener {

    private static boolean everyDamage;
    private static boolean fallDamage;

    public void init() {
        everyDamage = ListenerFile.getBooleanPath("Listener.NoDamage.Every.Enabled");
        fallDamage = ListenerFile.getBooleanPath("Listener.NoDamage.Fall.Enabled");
        NewSystem.getInstance().getServer().getPluginManager().registerEvents(this, NewSystem.getInstance());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player) {
            if(everyDamage) {
                e.setCancelled(true);
            }else if(fallDamage) {
                if(e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    e.setCancelled(true);
                }
            }
        }
    }
}
