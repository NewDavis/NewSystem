package me.newdavis.spigot.listener;
//Plugin by NewDavis

import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class WeatherChangeListener implements Listener {

    public void init() {
        NewSystem.getInstance().getServer().getPluginManager().registerEvents(this, NewSystem.getInstance());
    }

    @EventHandler
    public void onWeatherChange(org.bukkit.event.weather.WeatherChangeEvent e) {
        e.setCancelled(true);
    }

}
