package me.newdavis.spigot.util;

import me.newdavis.manager.NewPermManager;
import me.newdavis.spigot.api.CurrencyAPI;
import me.newdavis.spigot.command.PlayTimeCmd;
import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.OtherFile;
import me.newdavis.spigot.file.SavingsFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayTimeReward {

    private static List<Integer> playTimes = new ArrayList<>();
    private Player p;
    private int minutes;

    public PlayTimeReward(Player p) {
        this.p = p;
    }

    public PlayTimeReward() {}

    public void init() {
        for(String playTimeReward : OtherFile.getConfigurationSection("Other.PlayTimeReward")) {
            if(!playTimeReward.equalsIgnoreCase("Enabled")) {
                int minutes = OtherFile.getIntegerPath("Other.PlayTimeReward." + playTimeReward + ".Minutes");
                playTimes.add(minutes);
            }
        }
    }

    public boolean alreadyCollected(int minutes) {
        return SavingsFile.getStringListPath("PlayTimeReward.Collected." + minutes).contains(p.getUniqueId().toString());
    }

    public void collect() {
        for(int minutes : playTimes) {
            if (PlayTimeCmd.getMinutesPlayed(p) >= minutes) {
                if (!alreadyCollected(minutes)) {
                    this.minutes = minutes;
                    List<String> collected = SavingsFile.getStringListPath("PlayTimeReward.Collected." + minutes);
                    collected.add(p.getUniqueId().toString());
                    SavingsFile.setPath("PlayTimeReward.Collected." + minutes, collected);
                    sendMessage();
                    addMoney();
                    setRole();
                    addPermissions();
                    break;
                }
            }
        }
    }

    public void sendMessage() {
        if(OtherFile.isPathSet("Other.PlayTimeReward." + minutes + ".Message")) {
            for (String msg : OtherFile.getStringListPath("Other.PlayTimeReward." + minutes + ".Message")) {
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                        .replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix())
                        .replace("{Currency}", CurrencyAPI.getCurrencyOfPlayerString(p)));
            }
        }
    }

    public void addMoney() {
        if(CommandFile.getBooleanPath("Command.Currency.Enabled.Currency")) {
            if(OtherFile.isPathSet("Other.PlayTimeReward." + minutes + ".Currency")) {
                double amount = OtherFile.getDoublePath("Other.PlayTimeReward." + minutes + ".Currency");
                CurrencyAPI.addCurrencyToPlayer(p, amount);
            }
        }else{
            p.sendMessage(SettingsFile.getError().replace("{Error}", "Currency is required for PlayTimeReward Currency"));
        }
    }

    public void setRole() {
        if(NewSystem.newPerm) {
            if(OtherFile.isPathSet("Other.PlayTimeReward." + minutes + ".Role")) {
                String role = OtherFile.getStringPath("Other.PlayTimeReward." + minutes + ".Role");
                NewPermManager.setPlayerRole(p, role);
            }
        }else{
            p.sendMessage(SettingsFile.getError().replace("{Error}", "NewPerm is required for PlayTimeReward Role"));
        }
    }

    public void addPermissions() {
        if(NewSystem.newPerm) {
            if(OtherFile.isPathSet("Other.PlayTimeReward." + minutes + ".Permission")) {
                List<String> perms = OtherFile.getStringListPath("Other.PlayTimeReward." + minutes + ".Permission");
                NewPermManager.addPlayerPermission(p, perms);
            }
        }else{
            p.sendMessage(SettingsFile.getError().replace("{Error}", "NewPerm is required for PlayTimeReward Permissions"));
        }
    }

}
