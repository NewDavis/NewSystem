package me.newdavis.spigot.listener;

import me.newdavis.spigot.command.VanishCmd;
import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.file.ListenerFile;
import me.newdavis.spigot.file.SavingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class JoinListener implements Listener {

    private static List<String> joinMsg;
    private static List<String> firstJoinMsg;

    public void init() {
        joinMsg = ListenerFile.getStringListPath("Listener.Join.Message");
        firstJoinMsg = ListenerFile.getStringListPath("Listener.Join.FirstJoin.Message");
        NewSystem.getInstance().getServer().getPluginManager().registerEvents(this, NewSystem.getInstance());
    }

    private static Integer getJoinCount() {
        List<String> keys = SavingsFile.getConfigurationSection("JoinListener");
        return keys.size()+1;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        e.setJoinMessage("");

        boolean join = false;
        if(SavingsFile.isPathSet("JoinListener." + p.getUniqueId())) {
            join = SavingsFile.getBooleanPath("JoinListener." + p.getUniqueId());
        }

        if(CommandFile.getBooleanPath("Command.Vanish.Enabled") && VanishCmd.playerIsVanished(p)) {
            return;
        }

        if(join) {
            for(String msg : joinMsg) {
                Bukkit.broadcastMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(p)));
            }
        }else{
            if(ListenerFile.getBooleanPath("Listener.Join.FirstJoin.Enabled")) {
                int joinCount = getJoinCount();

                for(String msg : firstJoinMsg) {
                    Bukkit.broadcastMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(p)).replace("{Count}", String.valueOf(joinCount)));
                }
                SavingsFile.setPath("JoinListener." + p.getUniqueId(), true);
            }else{
                for(String msg : joinMsg) {
                    Bukkit.broadcastMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(p)));
                }
            }
        }
    }

}
