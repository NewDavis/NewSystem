package me.newdavis.spigot.command;

import me.newdavis.spigot.api.ReflectionAPI;
import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class PingCmd implements CommandExecutor {

    private static String perm;
    private static List<String> usage;
    private static List<String> message;
    private static final HashMap<String, String> colorCodes = new HashMap<>();

    public void init() {
        perm = CommandFile.getStringPath("Command.Ping.Permission");
        usage = CommandFile.getStringListPath("Command.Ping.Usage");
        message = CommandFile.getStringListPath("Command.Ping.Message");
        colorCodes.clear();
        for(String colorCode : CommandFile.getConfigurationSection("Command.Ping.ColorCode")) {
            String condition = CommandFile.getStringPath("Command.Ping.ColorCode." + colorCode + ".Condition");
            colorCodes.put(condition, colorCode);
        }
        NewSystem.getInstance().getCommand("ping").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if(args.length == 0) {
                    sendPing(p);
                }else{
                    for(String msg : usage) {
                        p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }else{
                p.sendMessage(SettingsFile.getNoPerm());
            }
        }else{
            sender.sendMessage(SettingsFile.getOnlyPlayerCanExecute());
        }
        return false;
    }

    private void sendPing(Player p) {
        int ping = new ReflectionAPI().getPlayerPing(p);
        String colorPing = ping + "ms";
        String comment = "";

        for(String condition : colorCodes.keySet()) {
            if(condition.contains("&&")) {
                String c1 = condition.split("&&")[0].replace(" ", "");
                String c2 = condition.split("&&")[1].replace(" ", "");
                boolean c1B = false;
                boolean c2B = false;

                for(int i = 0; i < 2; i++) {
                    String c = (i == 0 ? c1 : c2);
                    if (c.contains(">")) {
                        String[] s = c.split(">");
                        int n1;
                        int n2;
                        try {
                            if (s[0].equalsIgnoreCase("{Ping}")) {
                                n1 = ping;
                                n2 = Integer.parseInt(s[1].replace(" ", ""));
                            } else {
                                n1 = ping;
                                n2 = Integer.parseInt(s[0].replace(" ", ""));
                            }
                        } catch (NumberFormatException ignored) {
                            p.sendMessage(SettingsFile.getError().replace("{Error}", "Condition configuration error"));
                            return;
                        }

                        if (n2 > n1) {
                            if (i == 0) {
                                c1B = true;
                            } else {
                                c2B = true;
                            }
                        }
                    } else {
                        String[] s = condition.split("<");
                        int n1;
                        int n2;
                        try {
                            if (s[0].replace(" ", "").equalsIgnoreCase("{Ping}")) {
                                n1 = ping;
                                n2 = Integer.parseInt(s[1].replace(" ", ""));
                            } else {
                                n1 = ping;
                                n2 = Integer.parseInt(s[0].replace(" ", ""));
                                ;
                            }
                        } catch (NumberFormatException ignored) {
                            p.sendMessage(SettingsFile.getError().replace("{Error}", "Condition configuration error"));
                            return;
                        }

                        if (n2 < n1) {
                            if (i == 0) {
                                c1B = true;
                            } else {
                                c2B = true;
                            }
                        }
                    }
                }

                if(c1B && c2B) {
                    colorPing = "§" + colorCodes.get(condition) + ping + "ms";
                    comment = CommandFile.getStringPath("Command.Ping.ColorCode." + colorCodes.get(condition) + ".Comment");
                    break;
                }
            }else{
                if(condition.contains(">")) {
                    String[] s = condition.split(">");
                    int n1;
                    int n2;
                    try {
                        if (s[0].equalsIgnoreCase("{Ping}")) {
                            n1 = ping;
                            n2 = Integer.parseInt(s[1].replace(" ", ""));
                        } else {
                            n1 = Integer.parseInt(s[0].replace(" ", ""));
                            n2 = ping;
                        }
                    } catch (NumberFormatException ignored) {
                        p.sendMessage(SettingsFile.getError().replace("{Error}", "Condition configuration error"));
                        return;
                    }

                    if(n1 > n2) {
                        colorPing = "§" + colorCodes.get(condition) + ping + "ms";
                        comment = CommandFile.getStringPath("Command.Ping.ColorCode." + colorCodes.get(condition) + ".Comment");
                        break;
                    }
                }else{
                    if(condition.contains("<")) {
                        String[] s = condition.split("<");
                        int n1;
                        int n2;
                        try {
                            if (s[0].equalsIgnoreCase("{Ping}")) {
                                n1 = ping;
                                n2 = Integer.parseInt(s[1].replace(" ", ""));
                            } else {
                                n1 = Integer.parseInt(s[0].replace(" ", ""));
                                n2 = ping;
                            }
                        } catch (NumberFormatException ignored) {
                            p.sendMessage(SettingsFile.getError().replace("{Error}", "Condition configuration error"));
                            return;
                        }

                        if(n1 < n2) {
                            colorPing = "§" + colorCodes.get(condition) + ping + "ms";
                            comment = CommandFile.getStringPath("Command.Ping.ColorCode." + colorCodes.get(condition) + ".Comment");
                            break;
                        }
                    }
                }
            }
        }

        for(String msg : message) {
            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Ping}", colorPing).replace("{Comment}", comment));
        }
    }
}
