package me.newdavis.spigot.listener;

import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.file.ListenerFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class QuitListener implements Listener {

    private static List<String> joinMsg = ListenerFile.getStringListPath("Listener.Quit.Message");

    public void init() {
        joinMsg = ListenerFile.getStringListPath("Listener.Quit.Message");
        NewSystem.getInstance().getServer().getPluginManager().registerEvents(this, NewSystem.getInstance());
    }

    @EventHandler
    public void onJoin(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        for (String s : joinMsg) {
            e.setQuitMessage(s.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(p)));
        }
    }

}