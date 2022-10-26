package me.newdavis.spigot.listener;

import me.newdavis.spigot.command.MaintenanceCmd;
import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.file.ListenerFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerPingListener implements Listener {

    private static String motd;

    public void init() {
        motd = ListenerFile.getStringPath("Listener.ServerPing.MOTD").replace("{Prefix}", SettingsFile.getPrefix().replace("||", "\n"));
        NewSystem.getInstance().getServer().getPluginManager().registerEvents(this, NewSystem.getInstance());
    }

    @EventHandler
    public void onServerPing(ServerListPingEvent e) {
        if(CommandFile.getBooleanPath("Command.Maintenance.Enabled")
                && CommandFile.getBooleanPath("Command.Maintenance.ChangeMOTD")
                && MaintenanceCmd.status) {
            e.setMotd(MaintenanceCmd.maintenanceMOTD);
            return;
        }

        e.setMotd(motd);
    }

}
