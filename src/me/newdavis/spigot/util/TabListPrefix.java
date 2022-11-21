package me.newdavis.spigot.util;
//Plugin by NewDavis

import me.newdavis.spigot.api.ReflectionAPI;
import me.newdavis.spigot.command.VanishCmd;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import me.newdavis.spigot.util.placeholder.PlaceholderManager;
import me.newdavis.spigot.file.TabListFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

public class TabListPrefix {

    private final Player p;
    private final Scoreboard sb;

    public TabListPrefix(Player p) {
        this.p = p;
        if(NewSystem.playerScoreboard.containsKey(p)) {
            sb = NewSystem.playerScoreboard.get(p);
        }else{
            sb = Bukkit.getScoreboardManager().getNewScoreboard();
            NewSystem.playerScoreboard.put(p, sb);
        }
    }

    public void createTabList() {
        Collection<String> roles = getRoles();

        for(String role : roles) {
            String prefix = TabListFile.getPrefix(role);
            if(prefix.length() > 16) {
                prefix = prefix.substring(0, 16);
            }
            String suffix = TabListFile.getSuffix(role);
            if(suffix.length() > 16) {
                suffix = suffix.substring(0, 16);
            }
            String priority = TabListFile.getPriority(role);
            if(sb.getTeam(priority + role) == null) {
                sb.registerNewTeam(priority + role).setPrefix(prefix);
                sb.getTeam(priority + role).setSuffix(suffix);
                setColorToTeam(sb.getTeam(priority + role));
            }else{
                Team team = sb.getTeam(priority + role);
                team.setPrefix(prefix);
                team.setSuffix(suffix);
                setColorToTeam(team);
            }

            if(!TabListFile.getVanishSuffix(role).equalsIgnoreCase("")) {
                priority = TabListFile.getVanishPriority(role);
                suffix = TabListFile.getVanishSuffix(role);
                if(suffix.length() > 16) {
                    suffix = suffix.substring(0, 16);
                }
                if(sb.getTeam(priority + role) == null) {
                    sb.registerNewTeam(priority + role).setPrefix(prefix);
                    sb.getTeam(priority + role).setSuffix(suffix);
                    setColorToTeam(sb.getTeam(priority + role));
                }else{
                    Team team = sb.getTeam(priority + role);
                    team.setPrefix(prefix);
                    team.setSuffix(suffix);
                    setColorToTeam(team);
                }
            }
        }
    }

    private static void setColorToTeam(Team team) {
        ReflectionAPI ref = new ReflectionAPI();
        int versionId = Integer.parseInt(ref.getServerVersion().split("_")[1]);

        if(versionId >= 16) {
            ChatColor chatColor = getColorByColorCodes(getColorCodesFromPrefix(team.getPrefix()));
            try {
                Class.forName("org.bukkit.scoreboard.Team").getMethod("setColor", ChatColor.class).invoke(team, chatColor);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                     ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getColorCodesFromPrefix(String prefix) {
        return (prefix.split("")[prefix.length()-2] + prefix.split("")[prefix.length()-1]).replace(" ", "");
    }

    private static ChatColor getColorByColorCodes(String colorCode) {
        switch (colorCode) {
            case "§0":
                return ChatColor.BLACK;
            case "§1":
                return ChatColor.DARK_BLUE;
            case "§2":
                return ChatColor.DARK_GREEN;
            case "§3":
                return ChatColor.DARK_AQUA;
            case "§4":
                return ChatColor.DARK_RED;
            case "§5":
                return ChatColor.DARK_PURPLE;
            case "§6":
                return ChatColor.GOLD;
            case "§7":
                return ChatColor.GRAY;
            case "§8":
                return ChatColor.DARK_GRAY;
            case "§9":
                return ChatColor.BLUE;
            case "§a":
                return ChatColor.GREEN;
            case "§b":
                return ChatColor.AQUA;
            case "§c":
                return ChatColor.RED;
            case "§d":
                return ChatColor.LIGHT_PURPLE;
            case "§e":
                return ChatColor.YELLOW;
            case "§f":
                return ChatColor.WHITE;
            case "§r":
                return ChatColor.RESET;
            case "§n":
                return ChatColor.UNDERLINE;
            case "§k":
                return ChatColor.MAGIC;
            case "§m":
                return ChatColor.STRIKETHROUGH;
            case "§l":
                return ChatColor.BOLD;
            case "§o":
                return ChatColor.ITALIC;
            case "0":
                return ChatColor.BLACK;
            case "1":
                return ChatColor.DARK_BLUE;
            case "2":
                return ChatColor.DARK_GREEN;
            case "3":
                return ChatColor.DARK_AQUA;
            case "4":
                return ChatColor.DARK_RED;
            case "5":
                return ChatColor.DARK_PURPLE;
            case "6":
                return ChatColor.GOLD;
            case "7":
                return ChatColor.GRAY;
            case "8":
                return ChatColor.DARK_GRAY;
            case "9":
                return ChatColor.BLUE;
            case "a":
                return ChatColor.GREEN;
            case "b":
                return ChatColor.AQUA;
            case "c":
                return ChatColor.RED;
            case "d":
                return ChatColor.LIGHT_PURPLE;
            case "e":
                return ChatColor.YELLOW;
            case "f":
                return ChatColor.WHITE;
            case "r":
                return ChatColor.RESET;
            case "n":
                return ChatColor.UNDERLINE;
            case "k":
                return ChatColor.MAGIC;
            case "m":
                return ChatColor.STRIKETHROUGH;
            case "l":
                return ChatColor.BOLD;
            case "o":
                return ChatColor.ITALIC;
        }
        return ChatColor.WHITE;
    }

    public void setTabList() {
        for(Player all : Bukkit.getOnlinePlayers()) {
            boolean roleNotFound = false;

            String role;
            if (NewSystem.newPerm) {
                role = new PlaceholderManager(all).getPlaceholder("{Role}").getValue();
            } else {
                role = TabListFile.getRoleByPermission(all);
            }
            String priority = "";
            if (getRoles().contains(role)) {
                if (VanishCmd.playerIsVanished(all)) {
                    priority = TabListFile.getVanishPriority(role);
                } else {
                    priority = TabListFile.getPriority(role);
                }
            } else {
                roleNotFound = true;
            }

            if (sb.getTeam(priority + role) != null) {
                sb.getTeam(priority + role).addPlayer(all);
            } else {
                roleNotFound = true;
            }

            if(all == p && roleNotFound) {
                all.sendMessage((TabListFile.getStringPath("TabList.RoleNotFound")).replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }

        if(p.getScoreboard() != sb) {
            p.setScoreboard(sb);
        }
    }

    public static void setTabListForAll() {
        for(Player all : Bukkit.getOnlinePlayers()) {
            TabListPrefix tabList = new TabListPrefix(all);
            tabList.createTabList();
            tabList.setTabList();
        }
    }

    public static Collection<String> getRoles() {
        Collection<String> roles = new ArrayList<>();
        if(TabListFile.isPathSet("TabList")) {
            for (String role : TabListFile.getConfigurationSection("TabList")) {
                if (!(role.equalsIgnoreCase("RoleNotFound") || role.equalsIgnoreCase("Enabled"))) {
                    if (!roles.contains(role)) {
                        roles.add(role);
                    }
                }
            }
        }
        return roles;
    }

}
