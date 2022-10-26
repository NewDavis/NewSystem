package me.newdavis.spigot.util;
//Plugin by NewDavis

import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.file.OtherFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AutoBroadcast {

    public static void startAutoBroadcast() {
        int repeat = OtherFile.getIntegerPath("Other.AutoBroadcast.MessageDelayInMinutes");
        final int[] currentMessage = {0};
        int maxMessages = OtherFile.getAutoBroadcastMessages().keySet().size();
        List<Collection<String>> messages = new ArrayList<>();
        for(Collection<String> message : OtherFile.getAutoBroadcastMessages().values()) {
            messages.add(message);
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask(NewSystem.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (String msg : messages.get(currentMessage[0])) {
                    for(Player all : Bukkit.getOnlinePlayers()) {
                        all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{FS}", "§7"));
                    }
                }
                currentMessage[0]++;
                if (currentMessage[0] == maxMessages) {
                    currentMessage[0] = 0;
                }
            }
        }, 20L*60L*(long)repeat, 20L*60L*(long)repeat);
    }

}
