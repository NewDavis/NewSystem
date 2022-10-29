package me.newdavis.spigot.command;

import me.newdavis.manager.NewPermManager;
import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListCmd implements CommandExecutor {

    private static List<String> usage;
    private static String perm;
    private static String permAll;
    private static String permRole;
    private static boolean emptyLine;
    private static List<String> roleNotExist;
    private static String listFormat;
    private static String noPlayerOnline;
    private static List<String> allListFormat;
    private static List<String> messageAll;
    private static List<String> messageRole;

    public void init() {
        usage = CommandFile.getStringListPath("Command.List.Usage");
        perm = CommandFile.getStringPath("Command.List.Permission.Use");
        permAll = CommandFile.getStringPath("Command.List.Permission.All");
        permRole = CommandFile.getStringPath("Command.List.Permission.Role");
        emptyLine = CommandFile.getBooleanPath("Command.List.EmptyLineBetweenRoles");
        roleNotExist = CommandFile.getStringListPath("Command.List.RoleNotExist");
        listFormat = CommandFile.getStringPath("Command.List.ListFormat");
        noPlayerOnline = CommandFile.getStringPath("Command.List.NoPlayerInRole").replace("{Prefix}", SettingsFile.getPrefix());
        allListFormat = CommandFile.getStringListPath("Command.List.AllListFormat");
        messageAll = CommandFile.getStringListPath("Command.List.MessageAll");
        messageRole = CommandFile.getStringListPath("Command.List.MessageRole");
        NewSystem.getInstance().getCommand("list").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (NewSystem.hasPermission(p, perm)) {
                if (args.length == 0) {
                    if (NewSystem.hasPermission(p, permAll)) {
                        HashMap<String, List<Player>> roleList = new HashMap<>();
                        for (String role : NewPermManager.getRoleList()) {
                            List<Player> players = new ArrayList<>();
                            for (Player t : Bukkit.getOnlinePlayers()) {
                                if (role.equalsIgnoreCase(NewPermManager.getPlayerRole(t))) {
                                    players.add(t);
                                }
                            }
                            roleList.put(role, players);
                        }

                        HashMap<String, List<String>> messages = new HashMap<>();
                        for (String role : roleList.keySet()) {
                            List<String> messagesListInput = new ArrayList<>();
                            for (String format : allListFormat) {
                                if (messagesListInput.isEmpty()) {
                                    int count = roleList.get(role).size();
                                    messagesListInput.add(format.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Count}", String.valueOf(count)).replace("{Role-Suffix}", NewPermManager.getRoleSuffix(role)).replace("{Role}", role));
                                } else {
                                    String playersInRole = "";
                                    for (Player pList : roleList.get(role)) {
                                        playersInRole = playersInRole + listFormat.replace("{Player}", NewSystem.getName(pList, false)).replace("{DisplayName}", NewSystem.getName(pList, true));
                                    }
                                    if (playersInRole.isEmpty()) {
                                        playersInRole = noPlayerOnline;
                                    }
                                    messagesListInput.add(format.replace("{Prefix}", SettingsFile.getPrefix()).replace("{PlayerInRole}", playersInRole));
                                }
                            }
                            messages.put(role, messagesListInput);
                        }

                        for (int i = 0; i < messageAll.size(); i++) {
                            if (i == 0) {
                                int count = Bukkit.getOnlinePlayers().size();
                                p.sendMessage(messageAll.get(i).replace("{Prefix}", SettingsFile.getPrefix()).replace("{Count}", String.valueOf(count)));
                            } else {
                                for (String role : messages.keySet()) {
                                    if (emptyLine) {
                                        p.sendMessage("");
                                    }
                                    for (int x = 0; x < messages.get(role).size(); x++) {
                                        if (x == 0) {
                                            p.sendMessage(messageAll.get(i).replace("{AllList}", messages.get(role).get(x)));
                                        } else {
                                            p.sendMessage(messages.get(role).get(x));
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        p.sendMessage(SettingsFile.getNoPerm());
                    }
                } else if (args.length == 1) {
                    String role = args[0];
                    if (NewSystem.hasPermission(p, permRole.replace("{Role}", role))) {
                        if (NewPermManager.getRoleList().contains(args[0])) {
                            List<Player> playerInRole = new ArrayList<>();
                            for (Player t : Bukkit.getOnlinePlayers()) {
                                if (role.equalsIgnoreCase(NewPermManager.getPlayerRole(t))) {
                                    playerInRole.add(t);
                                }
                            }

                            List<String> messages = new ArrayList<>();
                            for (String format : messageRole) {
                                if (messages.isEmpty()) {
                                    int count = playerInRole.size();
                                    messages.add(format.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Count}", String.valueOf(count)).replace("{Role-Suffix}", NewPermManager.getRoleSuffix(role)).replace("{Role}", role));
                                } else {
                                    String playersInRole = "";
                                    for (Player pList : playerInRole) {
                                        playersInRole = playersInRole + listFormat.replace("{Player}", NewSystem.getName(pList, false)).replace("{DisplayName}", NewSystem.getName(pList, true));
                                    }
                                    if (playersInRole.isEmpty()) {
                                        playersInRole = noPlayerOnline;
                                    }
                                    messages.add(format.replace("{Prefix}", SettingsFile.getPrefix()).replace("{PlayerInRole}", playersInRole));
                                }
                            }

                            for (String msg : messages) {
                                p.sendMessage(msg);
                            }
                        } else {
                            for (String msg : roleNotExist) {
                                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        }
                    } else {
                        p.sendMessage(SettingsFile.getNoPerm());
                    }
                } else {
                    for (String msg : usage) {
                        p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            } else {
                p.sendMessage(SettingsFile.getNoPerm());
            }
        } else {
            if (args.length == 0) {
                HashMap<String, List<Player>> roleList = new HashMap<>();
                for (String role : NewPermManager.getRoleList()) {
                    List<Player> players = new ArrayList<>();
                    for (Player t : Bukkit.getOnlinePlayers()) {
                        if (role.equalsIgnoreCase(NewPermManager.getPlayerRole(t))) {
                            players.add(t);
                        }
                    }
                    roleList.put(role, players);
                }

                HashMap<String, List<String>> messages = new HashMap<>();
                for (String role : roleList.keySet()) {
                    List<String> messagesListInput = new ArrayList<>();
                    for (String format : allListFormat) {
                        if (messagesListInput.isEmpty()) {
                            int count = roleList.get(role).size();
                            messagesListInput.add(format.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Count}", String.valueOf(count)).replace("{Role-Suffix}", NewPermManager.getRoleSuffix(role)).replace("{Role}", role));
                        } else {
                            String playersInRole = "";
                            for (Player pList : roleList.get(role)) {
                                playersInRole = playersInRole + listFormat.replace("{Player}", NewSystem.getName(pList, false)).replace("{DisplayName}", NewSystem.getName(pList, true));
                            }
                            if (playersInRole.isEmpty()) {
                                playersInRole = noPlayerOnline;
                            }
                            messagesListInput.add(format.replace("{Prefix}", SettingsFile.getPrefix()).replace("{PlayerInRole}", playersInRole));
                        }
                    }
                    messages.put(role, messagesListInput);
                }

                for (int i = 0; i < messageAll.size(); i++) {
                    if (i == 0) {
                        int count = Bukkit.getOnlinePlayers().size();
                        sender.sendMessage(messageAll.get(i).replace("{Prefix}", SettingsFile.getPrefix()).replace("{Count}", String.valueOf(count)));
                    } else {
                        for (String role : messages.keySet()) {
                            if (emptyLine) {
                                sender.sendMessage("");
                            }
                            for (int x = 0; x < messages.get(role).size(); x++) {
                                if (x == 0) {
                                    sender.sendMessage(messageAll.get(i).replace("{AllList}", messages.get(role).get(x)));
                                } else {
                                    sender.sendMessage(messages.get(role).get(x));
                                }
                            }
                        }
                    }
                }
            } else if (args.length == 1) {
                String role = args[0];
                if (NewPermManager.getRoleList().contains(args[0])) {
                    List<Player> playerInRole = new ArrayList<>();
                    for (Player t : Bukkit.getOnlinePlayers()) {
                        if (role.equalsIgnoreCase(NewPermManager.getPlayerRole(t))) {
                            playerInRole.add(t);
                        }
                    }

                    List<String> messages = new ArrayList<>();
                    for (String format : messageRole) {
                        if (messages.isEmpty()) {
                            int count = playerInRole.size();
                            messages.add(format.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Count}", String.valueOf(count)).replace("{Role-Suffix}", NewPermManager.getRoleSuffix(role)).replace("{Role}", role));
                        } else {
                            String playersInRole = "";
                            for (Player pList : playerInRole) {
                                playersInRole = playersInRole + listFormat.replace("{Player}", NewSystem.getName(pList, false)).replace("{DisplayName}", NewSystem.getName(pList, true));
                            }
                            if (playersInRole.isEmpty()) {
                                playersInRole = noPlayerOnline;
                            }
                            messages.add(format.replace("{Prefix}", SettingsFile.getPrefix()).replace("{PlayerInRole}", playersInRole));
                        }
                    }

                    for (String msg : messages) {
                        sender.sendMessage(msg);
                    }
                } else {
                    for (String msg : roleNotExist) {
                        sender.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            } else {
                for (String msg : usage) {
                    sender.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }
        return false;
    }
}
