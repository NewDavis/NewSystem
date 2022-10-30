package me.newdavis.spigot.listener;
//Plugin by NewDavis

import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class NoHungerListener implements Listener {

    public NoHungerListener() {
        if(!NewSystem.loadedListeners.contains(this.getClass())) {
            NewSystem.loadedListeners.add(this.getClass());
            NewSystem.getInstance().getServer().getPluginManager().registerEvents(this, NewSystem.getInstance());
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent e) {
        if(e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            p.setFoodLevel(20);
            e.setCancelled(true);
        }
    }

}
