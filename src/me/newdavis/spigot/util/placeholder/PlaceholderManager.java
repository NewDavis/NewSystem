package me.newdavis.spigot.util.placeholder;

import me.newdavis.manager.NewPermManager;
import me.newdavis.spigot.api.CurrencyAPI;
import me.newdavis.spigot.api.ReflectionAPI;
import me.newdavis.spigot.command.PlayTimeCmd;
import me.newdavis.spigot.command.VanishCmd;
import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SavingsFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlaceholderManager {

    public static HashMap<OfflinePlayer, List<Placeholder>> placeholders = new HashMap<>();
    public static List<Placeholder> globalPlaceholder = new ArrayList<>();

    private OfflinePlayer p = null;

    public PlaceholderManager(OfflinePlayer p) {
        this.p = p;

        if(!placeholders.containsKey(p)) {
            init();
        }
    }

    public PlaceholderManager() {
        if(globalPlaceholder.isEmpty()) {
            init();
        }
    }

    public void init() {
        if(p != null) {
            String playtime = (CommandFile.getBooleanPath("Command.PlayTime.Enabled") ? PlayTimeCmd.getPlayTime(p.getUniqueId().toString()) : "");
            String role = "";
            String prefix = NewSystem.getName(p, true);
            String rolePrefix = "";
            String roleSuffix = "";
            String suffix = "";
            if (NewSystem.newPerm) {
                role = NewPermManager.getPlayerRole(p);
                suffix = NewPermManager.getPlayerSuffix(p);
                rolePrefix = NewPermManager.getRolePrefix(role);
                roleSuffix = NewPermManager.getRoleSuffix(role);
            }

            String currency = String.valueOf(0D);
            if (CommandFile.getBooleanPath("Command.Currency.Enabled.Currency")) {
                currency = CurrencyAPI.getCurrencyString(CurrencyAPI.getCurrencyOfPlayer(p));
            }

            String vanishMode = "";
            if (CommandFile.getBooleanPath("Command.Vanish.Enabled")) {
                vanishMode = (VanishCmd.playerIsVanished(p) ? CommandFile.getStringPath("Command.Vanish.Activated") : CommandFile.getStringPath("Command.Vanish.Deactivated"));
            }

            addPlaceholder("{PlayerPrefix}", prefix);
            addPlaceholder("{PlayerSuffix}", suffix);
            addPlaceholder("{Role}", role);
            addPlaceholder("{RolePrefix}", rolePrefix);
            addPlaceholder("{RoleSuffix}", roleSuffix);
            addPlaceholder("{Currency}", currency);
            addPlaceholder("{Vanish}", vanishMode);
            addPlaceholder("{PlayTime}", playtime);
        }else{
            int vanishedPlayer = 0;
            if (CommandFile.getBooleanPath("Command.Vanish.Enabled")) {
                for (String uuid : SavingsFile.getStringListPath("Vanish.Vanished")) {
                    Player vanished = Bukkit.getPlayer(uuid);
                    if (vanished != null) {
                        vanishedPlayer++;
                    }
                }
            }

            String currencyPrefix = "";
            if (CommandFile.getBooleanPath("Command.Currency.Enabled.Currency")) {
                currencyPrefix = CurrencyAPI.getCurrencyPrefix();
            }

            addPlaceholder("{VanishCount}", String.valueOf(vanishedPlayer));
            addPlaceholder("{CurrencyPrefix}", currencyPrefix);
        }
    }

    public void updatePlaceholder(String placeholder) {
        switch (placeholder) {
            case "{PlayerPrefix}":
                String prefix = NewSystem.getName(p, true);
                getPlaceholder("{PlayerPrefix}").updateValue(prefix);
                break;
            case "{PlayerSuffix}":
                String suffix = "";
                if(NewSystem.newPerm) {
                    suffix = NewPermManager.getRoleSuffix(getPlaceholder("{Role}").getValue());
                }
                getPlaceholder("{PlayerSuffix}").updateValue(suffix);
                break;
            case "{Role}":
                String role = "";
                if(NewSystem.newPerm) {
                    role = NewPermManager.getPlayerRole(p);
                }
                getPlaceholder("{Role}").updateValue(role);
                break;
            case "{RolePrefix}":
                getPlaceholder("{RolePrefix}").updateValue(NewPermManager.getRolePrefix(getPlaceholder("{Role}").getValue()));
                break;
            case "{RoleSuffix}":
                getPlaceholder("{RoleSuffix}").updateValue(NewPermManager.getRoleSuffix(getPlaceholder("{Role}").getValue()));
                break;
            case "{Currency}":
                String currency = String.valueOf(0D);
                if(CommandFile.getBooleanPath("Command.Currency.Enabled.Currency")) {
                    currency = CurrencyAPI.getCurrencyString(CurrencyAPI.getCurrencyOfPlayer(p));
                }
                getPlaceholder("{Currency}").updateValue(currency);
                break;
            case "{CurrencyPrefix}":
                String currencyPrefix = "";
                if(CommandFile.getBooleanPath("Command.Currency.Enabled.Currency")) {
                    currencyPrefix = CurrencyAPI.getCurrencyPrefix();
                }
                getPlaceholder("{CurrencyPrefix}").updateValue(currencyPrefix);
                break;
            case "{Vanish}":
                String vanishMode = "";
                if(CommandFile.getBooleanPath("Command.Vanish.Enabled")) {
                    vanishMode = (VanishCmd.playerIsVanished(p) ? CommandFile.getStringPath("Command.Vanish.Activated") : CommandFile.getStringPath("Command.Vanish.Deactivated"));
                }
                getPlaceholder("{Vanish}").updateValue(vanishMode);
                break;
            case "{VanishCount}":
                int vanishedPlayer = 0;
                if (CommandFile.getBooleanPath("Command.Vanish.Enabled")) {
                    for (String uuid : SavingsFile.getStringListPath("Vanish.Vanished")) {
                        Player vanished = Bukkit.getPlayer(UUID.fromString(uuid));
                        if (vanished != null) {
                            vanishedPlayer++;
                        }
                    }
                }

                getPlaceholder("{VanishCount}").updateValue(String.valueOf(vanishedPlayer));
                break;
            case "{PlayTime}":
                String playtime = PlayTimeCmd.getPlayTime(p.getUniqueId().toString());
                getPlaceholder("{PlayTime}").updateValue(playtime);
                break;
        }
    }

    public void addPlaceholder(String identifier, String value) {
        if(getPlaceholder(identifier) == null) {
            if(p != null) {
                List<Placeholder> current = new ArrayList<>();
                if (placeholders.containsKey(p)) {
                    current = placeholders.get(p);
                    current.add(new Placeholder(identifier, value));
                } else {
                    current.add(new Placeholder(identifier, value));
                }
                placeholders.put(p, current);
            }else{
                globalPlaceholder.add(new Placeholder(identifier, value));
            }
        }
    }

    public Placeholder getPlaceholder(String identifier) {
        if(placeholders.containsKey(p)) {
            for (Placeholder placeholder : placeholders.get(p)) {
                if (placeholder.getIdentifier().equalsIgnoreCase(identifier)) {
                    return placeholder;
                }
            }
        }else{
            for(Placeholder placeholder : globalPlaceholder) {
                if(placeholder.getIdentifier().equalsIgnoreCase(identifier)) {
                    return placeholder;
                }
            }
        }
        return null;
    }

    public String[] replacePlaceholderInString(String[] messages, boolean replaceRGB) {
        int players = Bukkit.getOnlinePlayers().size() - Integer.parseInt(new PlaceholderManager().getPlaceholder("{VanishCount}").getValue());
        int maxPlayers = Bukkit.getMaxPlayers();
        String time = SettingsFile.TimeFormat(System.currentTimeMillis());
        String date = SettingsFile.DateFormat(System.currentTimeMillis());

        int ping = -1;
        if(p.isOnline()) {
            ping = new ReflectionAPI().getPlayerPing(p.getPlayer());
        }

        for (int i = 0; i < messages.length; i++) {
            messages[i] = messages[i]
                    .replace("{Prefix}", SettingsFile.getPrefix())
                    .replace("{PlayerPrefix}", getPlaceholder("{PlayerPrefix}").getValue())
                    .replace("{PlayerSuffix}", getPlaceholder("{PlayerSuffix}").getValue())
                    .replace("{RolePrefix}", getPlaceholder("{RolePrefix}").getValue())
                    .replace("{RoleSuffix}", getPlaceholder("{RoleSuffix}").getValue())
                    .replace("{Name}", p.getName())
                    .replace("{DisplayName}", NewSystem.getName(p, true))
                    .replace("{Role}", getPlaceholder("{Role}").getValue())
                    .replace("{Currency}", getPlaceholder("{Currency}").getValue())
                    .replace("{CurrencyPrefix}", new PlaceholderManager().getPlaceholder("{CurrencyPrefix}").getValue())
                    .replace("{Vanish}", getPlaceholder("{Vanish}").getValue())
                    .replace("{VanishCount}", new PlaceholderManager().getPlaceholder("{VanishCount}").getValue())
                    .replace("{Time}", time)
                    .replace("{Date}", date)
                    .replace("{Online}", String.valueOf(players))
                    .replace("{MaxPlayers}", String.valueOf(maxPlayers))
                    .replace("{Ping}", String.valueOf(ping))
                    .replace("{PlayTime}", getPlaceholder("{PlayTime}").getValue())
                    .replace("||", "\n");
            messages[i] = replaceRGB ? NewSystem.replace(messages[i]) : messages[i];
        }
        return messages;
    }

}
