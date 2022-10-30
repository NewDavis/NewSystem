package me.newdavis.spigot.listener;
//Plugin by NewDavis

import me.newdavis.spigot.file.ListenerFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class DoubleJumpListener implements Listener {

    public static ArrayList<Player> flyMode = new ArrayList<>();
    public static ArrayList<Player> doubleJump = new ArrayList<>();

    private static double height;

    public DoubleJumpListener() {
        height = ListenerFile.getDoublePath("Listener.DoubleJump.Height");
        if(!NewSystem.loadedListeners.contains(this.getClass())) {
            NewSystem.loadedListeners.add(this.getClass());
            NewSystem.getInstance().getServer().getPluginManager().registerEvents(this, NewSystem.getInstance());
        }
    }

    @EventHandler
    public void onFlightToggle(PlayerToggleFlightEvent e) {
        Player p = e.getPlayer();
        if(!flyMode.contains(p)) {
            if(!doubleJump.contains(p)) {
                e.setCancelled(true);
                p.setAllowFlight(false);
                doubleJump.add(p);
                p.setVelocity(p.getLocation().getDirection().add(new Vector(0.0D, height, 0.0D)));
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if(doubleJump.contains(p) && (p.getLocation().getBlock().getType() != Material.AIR)) {
            p.setAllowFlight(true);
            doubleJump.remove(p);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.getPlayer().setAllowFlight(true);
    }

}
