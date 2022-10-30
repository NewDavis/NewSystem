package me.newdavis.spigot.listener;

import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.file.ListenerFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;
import java.util.Set;

public class BlockCommandListener implements Listener {

    private static String perm;
    private static List<String> blockMessage;
    private static List<String> commands;
    private static boolean enabledNewSystemCommands;

    public BlockCommandListener() {
        perm = ListenerFile.getStringPath("Listener.BlockCommand.Permission");
        blockMessage = ListenerFile.getStringListPath("Listener.BlockCommand.Message");
        enabledNewSystemCommands = ListenerFile.getBooleanPath("Listener.BlockCommand.EnabledNewSystemCommands");
        commands = ListenerFile.getStringListPath("Listener.BlockCommand.EnabledCommands");
        if(!NewSystem.loadedListeners.contains(this.getClass())) {
            NewSystem.loadedListeners.add(this.getClass());
            NewSystem.getInstance().getServer().getPluginManager().registerEvents(this, NewSystem.getInstance());
        }
    }

    @EventHandler
    public void onCmd(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        String[] cmd = e.getMessage().split(" ");
        if(!haveAccessToCommand(p, cmd[0].replace("/", ""))) {
            for(String msg : blockMessage) {
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Command}", cmd[0]));
            }
        }
    }

    public static boolean haveAccessToCommand(Player p, String cmd) {
        if(ListenerFile.getBooleanPath("Listener.BlockCommand.Enabled")) {
            if (!NewSystem.hasPermission(p, perm)) {
                boolean haveAccess = false;
                if (enabledNewSystemCommands) {
                    Set<String> newSystemCommands = NewSystem.status.keySet();
                    for (String cmdKey : newSystemCommands) {
                        if (cmdKey.contains("CMD")) {
                            String[] splitKey = cmdKey.split(" ");
                            if (cmd.equalsIgnoreCase(splitKey[0])) {
                                haveAccess = true;
                            }
                        }
                    }
                    if(!haveAccess) {
                        for (String cmdKey : newSystemCommands) {
                            if (cmdKey.contains("CMD")) {
                                String[] splitKey = cmdKey.split(" ");
                                if (OtherListeners.commandAliases.containsKey(splitKey[0])) {
                                    for (String alias : OtherListeners.commandAliases.get(splitKey[0])) {
                                        if (cmd.equalsIgnoreCase(alias)) {
                                            haveAccess = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(!haveAccess) {
                        for (String customCommand : OtherListeners.customCommandAliases.keySet()) {
                            if (cmd.equalsIgnoreCase(customCommand)) {
                                haveAccess = true;
                                break;
                            }
                            for (String customCommandAliases : OtherListeners.customCommandAliases.get(customCommand)) {
                                if (cmd.equalsIgnoreCase(customCommandAliases)) {
                                    haveAccess = true;
                                    break;
                                }
                            }
                        }
                    }
                }
                if(!haveAccess) {
                    for (String cmdKey : commands) {
                        if (cmd.equalsIgnoreCase(cmdKey)) {
                            haveAccess = true;
                            break;
                        }
                    }
                }
                return haveAccess;
            }
        }
        return true;
    }
}
