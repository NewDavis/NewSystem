package me.newdavis.spigot.listener;

import me.newdavis.spigot.command.VanishCmd;
import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.file.ListenerFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class QuitListener implements Listener {

    private static List<String> quitMsg = ListenerFile.getStringListPath("Listener.Quit.Message");

    public QuitListener() {
        quitMsg = ListenerFile.getStringListPath("Listener.Quit.Message");
        if(!NewSystem.loadedListeners.contains(this.getClass())) {
            NewSystem.loadedListeners.add(this.getClass());
            NewSystem.getInstance().getServer().getPluginManager().registerEvents(this, NewSystem.getInstance());
        }
    }

    @EventHandler
    public void onJoin(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        e.setQuitMessage("");

        if(CommandFile.getBooleanPath("Command.Vanish.Enabled") && VanishCmd.playerIsVanished(p)) {
            return;
        }

        for (String s : quitMsg) {
            Bukkit.broadcastMessage(s.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(p, false)).replace("{DisplayName}", NewSystem.getName(p, true)));
        }
    }

}