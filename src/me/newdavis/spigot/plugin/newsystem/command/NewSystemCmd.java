package me.newdavis.spigot.plugin.newsystem.command;

import me.newdavis.spigot.api.HologramAPI;
import me.newdavis.spigot.api.ReflectionAPI;
import me.newdavis.spigot.file.*;
import me.newdavis.spigot.listener.OtherListeners;
import me.newdavis.spigot.plugin.NewSystem;
import me.newdavis.spigot.plugin.newsystem.inventory.ChooseFileInventory;
import me.newdavis.spigot.plugin.newsystem.inventory.command.ChangeValueCommand;
import me.newdavis.spigot.plugin.newsystem.inventory.command.CommandChoosedInventory;
import me.newdavis.spigot.plugin.newsystem.inventory.command.CommandFileInventory;
import me.newdavis.spigot.plugin.newsystem.inventory.listener.ChangeValueListener;
import me.newdavis.spigot.plugin.newsystem.inventory.listener.ListenerChoosedInventory;
import me.newdavis.spigot.plugin.newsystem.inventory.listener.ListenerFileInventory;
import me.newdavis.spigot.plugin.newsystem.inventory.kit.ChangeValueKit;
import me.newdavis.spigot.plugin.newsystem.inventory.kit.KitChoosedInventory;
import me.newdavis.spigot.plugin.newsystem.inventory.kit.KitFileInventory;
import me.newdavis.spigot.plugin.newsystem.inventory.other.ChangeValueOther;
import me.newdavis.spigot.plugin.newsystem.inventory.other.OtherChoosedInventory;
import me.newdavis.spigot.plugin.newsystem.inventory.other.OtherFileInventory;
import me.newdavis.spigot.plugin.newsystem.inventory.settings.ChangeValueSettings;
import me.newdavis.spigot.plugin.newsystem.inventory.settings.SettingChoosedInventory;
import me.newdavis.spigot.plugin.newsystem.inventory.settings.SettingsFileInventory;
import me.newdavis.spigot.plugin.newsystem.inventory.tablist.ChangeValueTabList;
import me.newdavis.spigot.plugin.newsystem.inventory.tablist.TabListChoosedInventory;
import me.newdavis.spigot.plugin.newsystem.inventory.tablist.TabListFileInventory;
import me.newdavis.spigot.util.*;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class NewSystemCmd implements CommandExecutor {

    private final String USAGE = SettingsFile.getPrefix() + " §8/§3NewSystem §8<§7reload§8/§7status§8/§7server§8/§7config§8/§7info§8> <§7list§8>";
    private final String USAGE_CONFIG = SettingsFile.getPrefix() + " §8/§3NewSystem §7config §8<§7file§8/§7list§8> <§7command§8/§7listener§8/§7kit§8/§7other§8/§7tablist§8/§7setting§8> <§7index§8/§7path§8> <§7path§8/§7value§8>";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, "*") || NewSystem.hasPermission(p, "system.*")) {
                if(args.length == 1) {
                    if(args[0].equalsIgnoreCase("info")) {
                        List<String> infoMessage = Arrays.asList(SettingsFile.getPrefix() + " §7Informations about NewSystem", SettingsFile.getPrefix() + " §7Plugin was created by §bNewDavis",
                                SettingsFile.getPrefix() + " §7Plugin version §b" + NewSystem.getInstance().PLUGIN_VERSION, SettingsFile.getPrefix() + " §7For Help join our Discord§b https://discord.gg/seMwEpjUkD");
                        for (String msg : infoMessage) {
                            p.sendMessage(msg);
                        }
                    }else if(args[0].equalsIgnoreCase("server") || args[0].equalsIgnoreCase("serverinfo")) {
                        String onlinePlayer = String.valueOf(Bukkit.getOnlinePlayers().size());
                        String maxPlayer = String.valueOf(Bukkit.getMaxPlayers());
                        Runtime runtime = Runtime.getRuntime();
                        String ramMax = (runtime.maxMemory() / 1048576) + "MB";
                        String ramFree = (runtime.freeMemory() / 1048576) + "MB";
                        String ramInUse = ((runtime.totalMemory() / 1048576) - (runtime.freeMemory() / 1048576)) + "MB";
                        String tps = getTPS();

                        List<String> infoMessage = Arrays.asList(SettingsFile.getPrefix() + " §7Informations about this Server", SettingsFile.getPrefix() + "",
                                SettingsFile.getPrefix() + " Max Ram: " + ramMax, SettingsFile.getPrefix() + " Ram In Use: " + ramInUse, SettingsFile.getPrefix() + " Free Ram: " + ramFree,
                                SettingsFile.getPrefix() + "", SettingsFile.getPrefix() + " §7TPS: " + tps, SettingsFile.getPrefix() + "",
                                SettingsFile.getPrefix() + " Online Player: " + onlinePlayer, SettingsFile.getPrefix() + " Max Player: " + maxPlayer);
                        for (String msg : infoMessage) {
                            p.sendMessage(msg);
                        }
                    }else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
                        reload(p);
                    }else if(args[0].equalsIgnoreCase("config")) {
                        new ChooseFileInventory().openInventory(p);
                    }else{
                        p.sendMessage(USAGE);
                    }
                }else if(args.length == 2) {
                    if(args[0].equalsIgnoreCase("status")) {
                        HashMap<String, Boolean> status = NewSystem.status;
                        if (args[1].equalsIgnoreCase("list")) {
                            String commands = "";
                            for(String cmd : status.keySet()) {
                                commands += (status.get(cmd) ? "§a" + cmd.replace(" CMD", "") : "§c" + cmd.replace(" CMD", "")) + "§8, §7";
                            }
                            p.sendMessage(commands);
                        }else{
                            ArrayList<String> split = new ArrayList<>();
                            String cmd = args[1];
                            boolean cmdStatus = false;
                            for (String key : status.keySet()) {
                                if(key.contains(" ")) {
                                    String[] keySplit = key.split(" ");
                                    if (keySplit[0].equalsIgnoreCase(cmd)) {
                                        split.add(keySplit[0]);
                                        split.add(keySplit[1]);
                                        cmd = split.get(0);
                                        cmdStatus = status.get(split.get(0) + " " + split.get(1));
                                    }
                                }else {
                                    if (key.equalsIgnoreCase(cmd)) {
                                        cmd = key;
                                        cmdStatus = status.get(cmd);
                                    }
                                }
                            }
                            if(split.isEmpty()) {
                                if (!status.containsKey(cmd)) {
                                    p.sendMessage(SettingsFile.getPrefix() + " §cThis isn't anything of NewSystem!");
                                }else {
                                    p.sendMessage(SettingsFile.getPrefix() + " §7The status of §8'§b" + cmd + "§8' §7is " + (cmdStatus ? "§aactivated§7!" : "§cdeactivated§7!"));
                                }
                            }else {
                                if (!status.containsKey(split.get(0) + " " + split.get(1))) {
                                    p.sendMessage(SettingsFile.getPrefix() + " §cThis isn't anything of NewSystem!");
                                } else if (cmd.equalsIgnoreCase("rang") || cmd.equalsIgnoreCase("role")) {
                                    p.sendMessage(SettingsFile.getPrefix() + " §7The status of §8'§b" + cmd + "§8' §7is " + (cmdStatus ? "§aactivated§7!" : "§cdeactivated§7!"));
                                    String Cmdusage = CommandFile.getStringPath("Command." + cmd + ".Usage").replace("{Prefix} ", "");
                                    p.sendMessage(SettingsFile.getPrefix() + " §7The Usage is " + Cmdusage);
                                    p.sendMessage(SettingsFile.getPrefix() + " §7The Command §8'§b" + cmd + "§8' §7needs this Plugin to run: §cNewPerm");
                                } else {
                                    p.sendMessage(SettingsFile.getPrefix() + " §7The status of §8'§b" + cmd + "§8' §7is " + (cmdStatus ? "§aactivated§7!" : "§cdeactivated§7!"));
                                    if (split.size() == 2) {
                                        if(CommandFile.isPathSet("Command." + cmd + ".Usage")) {
                                            String Cmdusage = CommandFile.getStringPath("Command." + cmd + ".Usage").replace("{Prefix} ", "");
                                            p.sendMessage(SettingsFile.getPrefix() + " §7The Usage: " + Cmdusage);
                                        }
                                    }
                                }
                            }
                        }
                    }else if(args[0].equalsIgnoreCase("config")) {
                        if(args[1].equalsIgnoreCase("Command") || args[1].equalsIgnoreCase("CommandFile") || args[1].equalsIgnoreCase("Cmd")) {
                            new CommandFileInventory().openInventoryPage(p, 1);
                        }else if(args[1].equalsIgnoreCase("Listener") || args[1].equalsIgnoreCase("ListenerFile")) {
                            new ListenerFileInventory().openInventoryPage(p, 1);
                        }else if(args[1].equalsIgnoreCase("Kit") || args[1].equalsIgnoreCase("KitFile")) {
                            new KitFileInventory().openInventoryPage(p, 1);
                        }else if(args[1].equalsIgnoreCase("Other") || args[1].equalsIgnoreCase("OtherFile")) {
                            new OtherFileInventory().openInventoryPage(p, 1);
                        }else if(args[1].equalsIgnoreCase("Settings") || args[1].equalsIgnoreCase("SettingsFile")) {
                            new SettingsFileInventory().openInventoryPage(p, 1);
                        }else if(args[1].equalsIgnoreCase("TabList") || args[1].equalsIgnoreCase("TabListFile") || args[1].equalsIgnoreCase("tl")) {
                            new TabListFileInventory().openInventoryPage(p, 1);
                        }else if(args[1].equalsIgnoreCase("list")) {
                            List<String> files = Arrays.asList("CommandFile", "ListenerFile", "KitFile", "OtherFile", "SettingsFile", "TabListFile");
                            String fileList = " ";
                            for(int i = 0; i < files.size(); i++) {
                                if(i == (files.size()-1)) {
                                    fileList += "§7" + files.get(i);
                                }else{
                                    fileList += "§7" + files.get(i) + "§8, ";
                                }
                            }

                            p.sendMessage(SettingsFile.getPrefix() + " §7There are currently §a" + files.size() + " §7editable Files.");
                            p.sendMessage(SettingsFile.getPrefix() + fileList);
                        }else{
                            p.sendMessage(USAGE_CONFIG);
                        }
                    }else{
                        p.sendMessage(USAGE);
                    }
                }else if(args.length == 3) {
                    if (args[0].equalsIgnoreCase("config")) {
                        if (args[1].equalsIgnoreCase("Command") || args[1].equalsIgnoreCase("CommandFile") || args[1].equalsIgnoreCase("Cmd")) {
                            String cmd = args[2];
                            new CommandChoosedInventory().openInventoryPage(p, cmd, 1);
                        } else if (args[1].equalsIgnoreCase("Listener") || args[1].equalsIgnoreCase("ListenerFile")) {
                            String listener = args[2];
                            new ListenerChoosedInventory().openInventoryPage(p, listener, 1);
                        } else if (args[1].equalsIgnoreCase("Kit") || args[1].equalsIgnoreCase("KitFile")) {
                            String kit = args[2];
                            new KitChoosedInventory().openInventoryPage(p, kit, 1);
                        } else if (args[1].equalsIgnoreCase("Other") || args[1].equalsIgnoreCase("OtherFile")) {
                            String other = args[2];
                            new OtherChoosedInventory().openInventoryPage(p, other, 1);
                        } else if (args[1].equalsIgnoreCase("Settings") || args[1].equalsIgnoreCase("SettingsFile")) {
                            String setting = args[2];
                            new SettingChoosedInventory().openInventoryPage(p, setting, 1);
                        } else if (args[1].equalsIgnoreCase("TabList") || args[1].equalsIgnoreCase("TabListFile") || args[1].equalsIgnoreCase("tl")) {
                            String tablist = args[2];
                            new TabListChoosedInventory().openInventoryPage(p, tablist, 1);
                        } else {
                            p.sendMessage(USAGE_CONFIG);
                        }
                    } else {
                        p.sendMessage(USAGE);
                    }
                }else if(args.length == 4) {
                    String path = args[2].replace("Command.", "").replace("Listener.", "")
                            .replace("Other.", "").replace("Kit.", "").replace("TabList.", "");
                    String value = getValueFromArray(args, 3);
                    if (args[1].equalsIgnoreCase("Settings") || args[1].equalsIgnoreCase("SettingsFile")) {
                        SettingChoosedInventory.page.put(p, 1);
                        ChangeValueSettings.pathHM.put(p, path);
                        if (value.equalsIgnoreCase("delete")) {
                            if(ChangeValueSettings.setPath(p, "")) {
                                p.sendMessage(SettingsFile.getPrefix() + " §7The value of the path got §cdeleted§7!");
                            }
                        } else {
                            if(ChangeValueSettings.setPath(p, value)) {
                                p.sendMessage(SettingsFile.getPrefix() + " §7The path was changed §asuccessfully§7.");
                            }
                        }
                    }
                }else if(args.length > 4) {
                    if(args[0].equalsIgnoreCase("config")) {
                        String path = args[3].replace("Command.", "").replace("Listener.", "")
                                .replace("Other.", "").replace("Kit.", "").replace("TabList.", "");
                        String value = getValueFromArray(args, 4);
                        String valueList = getValueFromArray(args, 4);
                        if(args.length > 5) {
                            valueList = getValueFromArray(args, 5);
                        }
                        if (args[1].equalsIgnoreCase("Command") || args[1].equalsIgnoreCase("CommandFile") || args[1].equalsIgnoreCase("Cmd")) {
                            String cmd = args[2];
                            CommandChoosedInventory.command.put(p, cmd);
                            CommandChoosedInventory.page.put(p, 1);
                            ChangeValueCommand.pathHM.put(p, path);
                            if(ChangeValueCommand.isList(p)) {
                                if(isInteger(args[4])) {
                                    int index = Integer.parseInt(args[4]);
                                    if (valueList.equalsIgnoreCase("delete")) {
                                        ChangeValueCommand.indexHM.put(p, index);
                                        if(ChangeValueCommand.removeIndex(p)) {
                                            p.sendMessage(SettingsFile.getPrefix() + " §7The index got §cremoved §7of the list.");
                                        }
                                    } else {
                                        ChangeValueCommand.indexHM.put(p, index);
                                        if(ChangeValueCommand.setList(p, valueList)) {
                                            p.sendMessage(SettingsFile.getPrefix() + " §7The index of the list was changed §asuccessfully§7!");
                                        }
                                    }
                                }else{
                                    p.sendMessage(SettingsFile.getPrefix() + " §cPlease use numbers!");
                                }
                            }else{
                                if(value.equalsIgnoreCase("delete")) {
                                    if(ChangeValueCommand.setPath(p, "")) {
                                        p.sendMessage(SettingsFile.getPrefix() + " §7The value of the path got §cdeleted§7!");
                                    }
                                }else{
                                    if(ChangeValueCommand.setPath(p, value)) {
                                        p.sendMessage(SettingsFile.getPrefix() + " §7The path was changed §asuccessfully§7.");
                                    }
                                }
                            }
                        } else if (args[1].equalsIgnoreCase("Listener") || args[1].equalsIgnoreCase("ListenerFile")) {
                            String listener = args[2];
                            ListenerChoosedInventory.listener.put(p, listener);
                            ListenerChoosedInventory.page.put(p, 1);
                            ChangeValueListener.pathHM.put(p, path);
                            if(ChangeValueListener.isList(p)) {
                                if(isInteger(args[4])) {
                                    int index = Integer.parseInt(args[4]);
                                    if (valueList.equalsIgnoreCase("delete")) {
                                        ChangeValueListener.indexHM.put(p, index);
                                        if(ChangeValueListener.removeIndex(p)) {
                                            p.sendMessage(SettingsFile.getPrefix() + " §7The index got §cremoved §7of the list.");
                                        }
                                    } else {
                                        ChangeValueListener.indexHM.put(p, index);
                                        if(ChangeValueListener.setList(p, valueList)) {
                                            p.sendMessage(SettingsFile.getPrefix() + " §7The index of the list was changed §asuccessfully§7!");
                                        }
                                    }
                                }else{
                                    p.sendMessage(SettingsFile.getPrefix() + " §cPlease use numbers!");
                                }
                            }else{
                                if(value.equalsIgnoreCase("delete")) {
                                    if(ChangeValueListener.setPath(p, "")) {
                                        p.sendMessage(SettingsFile.getPrefix() + " §7The value of the path got §cdeleted§7!");
                                    }
                                }else{
                                    if(ChangeValueListener.setPath(p, value)) {
                                        p.sendMessage(SettingsFile.getPrefix() + " §7The path was changed §asuccessfully§7.");
                                    }
                                }
                            }
                        } else if (args[1].equalsIgnoreCase("Kit") || args[1].equalsIgnoreCase("KitFile")) {
                            String kit = args[2];
                            KitChoosedInventory.kit.put(p, kit);
                            KitChoosedInventory.page.put(p, 1);
                            ChangeValueKit.pathHM.put(p, path);
                            if(ChangeValueKit.isList(p)) {
                                if(isInteger(args[4])) {
                                    int index = Integer.parseInt(args[4]);
                                    if (valueList.equalsIgnoreCase("delete")) {
                                        ChangeValueKit.indexHM.put(p, index);
                                        if(ChangeValueKit.removeIndex(p)) {
                                            p.sendMessage(SettingsFile.getPrefix() + " §7The index got §cremoved §7of the list.");
                                        }
                                    } else {
                                        ChangeValueKit.indexHM.put(p, index);
                                        if(ChangeValueKit.setList(p, valueList)) {
                                            p.sendMessage(SettingsFile.getPrefix() + " §7The index of the list was changed §asuccessfully§7!");
                                        }
                                    }
                                }else{
                                    p.sendMessage(SettingsFile.getPrefix() + " §cPlease use numbers!");
                                }
                            }else{
                                if(value.equalsIgnoreCase("delete")) {
                                    if(ChangeValueKit.setPath(p, "")) {
                                        p.sendMessage(SettingsFile.getPrefix() + " §7The value of the path got §cdeleted§7!");
                                    }
                                }else{
                                    if(ChangeValueKit.setPath(p, value)) {
                                        p.sendMessage(SettingsFile.getPrefix() + " §7The path was changed §asuccessfully§7.");
                                    }
                                }
                            }
                        } else if (args[1].equalsIgnoreCase("Other") || args[1].equalsIgnoreCase("OtherFile")) {
                            String other = args[2];
                            OtherChoosedInventory.other.put(p, other);
                            OtherChoosedInventory.page.put(p, 1);
                            ChangeValueOther.pathHM.put(p, path);
                            if(ChangeValueOther.isList(p)) {
                                if(isInteger(args[4])) {
                                    int index = Integer.parseInt(args[4]);
                                    if (valueList.equalsIgnoreCase("delete")) {
                                        ChangeValueOther.indexHM.put(p, index);
                                        if(ChangeValueOther.removeIndex(p)) {
                                            p.sendMessage(SettingsFile.getPrefix() + " §7The index got §cremoved §7of the list.");
                                        }
                                    } else {
                                        ChangeValueOther.indexHM.put(p, index);
                                        if(ChangeValueOther.setList(p, valueList)) {
                                            p.sendMessage(SettingsFile.getPrefix() + " §7The index of the list was changed §asuccessfully§7!");
                                        }
                                    }
                                }else{
                                    p.sendMessage(SettingsFile.getPrefix() + " §cPlease use numbers!");
                                }
                            }else{
                                if(value.equalsIgnoreCase("delete")) {
                                    if(ChangeValueOther.setPath(p, "")) {
                                        p.sendMessage(SettingsFile.getPrefix() + " §7The value of the path got §cdeleted§7!");
                                    }
                                }else{
                                    if(ChangeValueOther.setPath(p, value)) {
                                        p.sendMessage(SettingsFile.getPrefix() + " §7The path was changed §asuccessfully§7.");
                                    }
                                }
                            }
                        } else if (args[1].equalsIgnoreCase("Settings") || args[1].equalsIgnoreCase("SettingsFile")) {
                            String setting = args[2];
                            SettingChoosedInventory.setting.put(p, setting);
                            SettingChoosedInventory.page.put(p, 1);
                            ChangeValueSettings.pathHM.put(p, path);
                            if (value.equalsIgnoreCase("delete")) {
                                if(ChangeValueSettings.setPath(p, "")) {
                                    p.sendMessage(SettingsFile.getPrefix() + " §7The value of the path got §cdeleted§7!");
                                }
                            } else {
                                if(ChangeValueSettings.setPath(p, value)) {
                                    p.sendMessage(SettingsFile.getPrefix() + " §7The path was changed §asuccessfully§7.");
                                }
                            }
                        } else if (args[1].equalsIgnoreCase("TabList") || args[1].equalsIgnoreCase("TabListFile") || args[1].equalsIgnoreCase("tl")) {
                            String tablist = args[2];
                            TabListChoosedInventory.tablist.put(p, tablist);
                            TabListChoosedInventory.page.put(p, 1);
                            ChangeValueTabList.pathHM.put(p, path);
                            if (value.equalsIgnoreCase("delete")) {
                                if(ChangeValueTabList.setPath(p, "")) {
                                    p.sendMessage(SettingsFile.getPrefix() + " §7The value of the path got §cdeleted§7!");
                                }
                            } else {
                                if(ChangeValueTabList.setPath(p, value)) {
                                    p.sendMessage(SettingsFile.getPrefix() + " §7The path was changed §asuccessfully§7.");
                                }
                            }
                        } else {
                            p.sendMessage(USAGE_CONFIG);
                        }
                    }else{
                        p.sendMessage(USAGE);
                    }
                }else{
                    p.sendMessage(USAGE);
                }
            }else{
                p.sendMessage(SettingsFile.getNoPerm());
            }
        }else{
            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("info")) {
                    List<String> infoMessage = Arrays.asList(SettingsFile.getPrefix() + " §7Informations about NewSystem", SettingsFile.getPrefix() + " §7Plugin was created by §bNewDavis",
                            SettingsFile.getPrefix() + " §7Plugin version §b" + NewSystem.getInstance().PLUGIN_VERSION, SettingsFile.getPrefix() + " §7For Help join our Discord§b https://discord.gg/seMwEpjUkD");
                    for (String msg : infoMessage) {
                        sender.sendMessage(msg);
                    }
                }else if(args[0].equalsIgnoreCase("server") || args[0].equalsIgnoreCase("serverinfo")) {
                    String onlinePlayer = String.valueOf(Bukkit.getOnlinePlayers().size());
                    String maxPlayer = String.valueOf(Bukkit.getMaxPlayers());
                    Runtime runtime = Runtime.getRuntime();
                    String ramMax = (runtime.maxMemory() / 1048576) + "MB";
                    String ramFree = (runtime.freeMemory() / 1048576) + "MB";
                    String ramInUse = ((runtime.totalMemory() / 1048576) - (runtime.freeMemory() / 1048576)) + "MB";
                    String tps = getTPS();

                    List<String> infoMessage = Arrays.asList(SettingsFile.getPrefix() + " §7Informations about this Server", SettingsFile.getPrefix() + "",
                            SettingsFile.getPrefix() + " Max Ram: " + ramMax, SettingsFile.getPrefix() + " Ram In Use: " + ramInUse, SettingsFile.getPrefix() + " Free Ram: " + ramFree,
                            SettingsFile.getPrefix() + "", SettingsFile.getPrefix() + " §7TPS: " + tps, SettingsFile.getPrefix() + "",
                            SettingsFile.getPrefix() + " Online Player: " + onlinePlayer, SettingsFile.getPrefix() + " Max Player: " + maxPlayer);
                    for (String msg : infoMessage) {
                        sender.sendMessage(msg);
                    }
                }else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
                    reload(sender);
                }else{
                    sender.sendMessage(USAGE);
                }
            }else if(args.length == 2) {
                if (args[0].equalsIgnoreCase("status")) {
                    HashMap<String, Boolean> status = NewSystem.status;
                    if (args[1].equalsIgnoreCase("list")) {
                        String commands = "";
                        for (String cmd : status.keySet()) {
                            commands += (status.get(cmd) ? "§a" + cmd.replace(" CMD", "") : "§c" + cmd.replace(" CMD", "")) + "§8, §7";
                        }
                        sender.sendMessage(commands);
                    } else {
                        ArrayList<String> split = new ArrayList<>();
                        String cmd = args[1];
                        boolean cmdStatus = false;
                        for (String key : status.keySet()) {
                            if (key.contains(" ")) {
                                String[] keySplit = key.split(" ");
                                if (keySplit[0].equalsIgnoreCase(cmd)) {
                                    split.add(keySplit[0]);
                                    split.add(keySplit[1]);
                                    cmd = split.get(0);
                                    cmdStatus = status.get(split.get(0) + " " + split.get(1));
                                }
                            } else {
                                if (key.equalsIgnoreCase(cmd)) {
                                    cmd = key;
                                    cmdStatus = status.get(cmd);
                                }
                            }
                        }
                        if (split.isEmpty()) {
                            if (!status.containsKey(cmd)) {
                                sender.sendMessage(SettingsFile.getPrefix() + " §cThis isn't anything of NewSystem!");
                            } else {
                                sender.sendMessage(SettingsFile.getPrefix() + " §7The status of §8'§b" + cmd + "§8' §7is " + (cmdStatus ? "§aactivated§7!" : "§cdeactivated§7!"));
                            }
                        } else {
                            if (!status.containsKey(split.get(0) + " " + split.get(1))) {
                                sender.sendMessage(SettingsFile.getPrefix() + " §cThis isn't anything of NewSystem!");
                            } else if (cmd.equalsIgnoreCase("rang") || cmd.equalsIgnoreCase("role")) {
                                sender.sendMessage(SettingsFile.getPrefix() + " §7The status of §8'§b" + cmd + "§8' §7is " + (cmdStatus ? "§aactivated§7!" : "§cdeactivated§7!"));
                                String Cmdusage = CommandFile.getStringPath("Command." + cmd + ".Usage").replace("{Prefix} ", "");
                                sender.sendMessage(SettingsFile.getPrefix() + " §7The Usage is " + Cmdusage);
                                sender.sendMessage(SettingsFile.getPrefix() + " §7The Command §8'§b" + cmd + "§8' §7needs this Plugin to run: §cNewPerm");
                            } else {
                                sender.sendMessage(SettingsFile.getPrefix() + " §7The status of §8'§b" + cmd + "§8' §7is " + (cmdStatus ? "§aactivated§7!" : "§cdeactivated§7!"));
                                if (split.size() == 2) {
                                    if (CommandFile.isPathSet("Command." + cmd + ".Usage")) {
                                        String Cmdusage = CommandFile.getStringPath("Command." + cmd + ".Usage").replace("{Prefix} ", "");
                                        sender.sendMessage(SettingsFile.getPrefix() + " §7The Usage is " + Cmdusage);
                                    }
                                }
                            }
                        }
                    }
                }else{
                    sender.sendMessage(USAGE);
                }
            }else{
                sender.sendMessage(USAGE);
            }
        }
        return false;
    }

    public static boolean isInteger(String text) {
        try{
            Integer.parseInt(text);
        }catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static String getValueFromArray(String[] args, int start) {
        String value = "";
        for(int i = start; i < args.length; i++) {
            if(i == (args.length-1)) {
                value += args[i];
            }else{
                value += args[i] + " ";
            }
        }
        return value.replace("&", "§");
    }

    public static String getTPS() {
        String tps = "";
        double[] recentTps = TPS.getTPS();
        for(double tps2 : recentTps) {
            String colorCode = "§a";
            if(tps2 < 18 && tps2 > 15) {
                colorCode = "§6";
            }else if(tps2 < 14 && tps2 > 10) {
                colorCode = "§c";
            }else if(tps2 < 9){
                colorCode = "§4";
            }

            DecimalFormat format = new DecimalFormat("##");

            if(tps.equalsIgnoreCase("")) {
                tps = colorCode + format.format(tps2);
            }else{
                tps += " §8┃ " + colorCode + format.format(tps2);
            }
        }
        return tps;
    }

    public static void reload(Player p) {
        SettingsFile.loadConfig();
        p.sendMessage(SettingsFile.getPrefix() + " §7Settings File has been reloaded!");
        CommandFile.loadConfig();
        p.sendMessage(SettingsFile.getPrefix() + " §7Command File has been reloaded!");
        ListenerFile.loadConfig();
        p.sendMessage(SettingsFile.getPrefix() + " §7Event File has been reloaded!");
        OtherFile.loadConfig();
        p.sendMessage(SettingsFile.getPrefix() + " §7Other File has been reloaded!");
        SavingsFile.loadConfig();
        p.sendMessage(SettingsFile.getPrefix() + " §7Savings File has been reloaded!");
        if (CommandFile.getBooleanPath("Command.Kit.Enabled")) {
            KitFile.loadConfig();
            p.sendMessage(SettingsFile.getPrefix() + " §7Kit System has been reloaded!");
        } else {
            p.sendMessage(SettingsFile.getPrefix() + " §7Kit System is not activated!");
        }
        if (CommandFile.getBooleanPath("Command.Hologram.Enabled")) {
            for (String hologram : HologramAPI.getHolograms()) {
                HologramAPI.reloadHologram(hologram);
            }
            p.sendMessage(SettingsFile.getPrefix() + " §7Hologram System has been reloaded!");
        } else {
            p.sendMessage(SettingsFile.getPrefix() + " §7Hologram System is not activated!");
        }
        if (TabListFile.getBooleanPath("TabList.Enabled")) {
            TabListFile.loadConfig();
            TabListPrefix.setTabListForAll();
            p.sendMessage(SettingsFile.getPrefix() + " §7TabList Prefix System has been reloaded!");
        } else {
            p.sendMessage(SettingsFile.getPrefix() + " §7TabList Prefix System is not activated!");
        }
        if (OtherFile.getBooleanPath("Other.ScoreBoard.Enabled")) {
            ScoreboardManager.scoreboardTitle = OtherFile.getStringListPath("Other.ScoreBoard.Title");
            ScoreboardManager.scoreboardScores = OtherFile.getStringListPath("Other.ScoreBoard.Scores");
            ScoreboardManager.speed = OtherFile.getIntegerPath("Other.ScoreBoard.UpdateSpeed");
            ScoreboardManager.updateEveryScoreboard();
            p.sendMessage(SettingsFile.getPrefix() + " §7Scoreboard System has been reloaded!");
        } else {
            p.sendMessage(SettingsFile.getPrefix() + " §7Scoreboard System is not activated!");
        }
        if (OtherFile.getBooleanPath("Other.ChatFilter.Enabled")) {
            new ChatFilter().init();
            p.sendMessage(SettingsFile.getPrefix() + " §7ChatFilter System has been reloaded!");
        } else {
            p.sendMessage(SettingsFile.getPrefix() + " §7ChatFilter System is not activated!");
        }
        if (OtherFile.getBooleanPath("Other.Portal.Enabled")) {
            new Portal().init();
            p.sendMessage(SettingsFile.getPrefix() + " §7Portal System has been reloaded!");
        } else {
            p.sendMessage(SettingsFile.getPrefix() + " §7Portal System is not activated!");
        }
        OtherListeners.commandAliases = CommandFile.getCommandAliases();
        p.sendMessage(SettingsFile.getPrefix() + " §7Command Aliases has been reloaded!");
        if (CommandFile.getBooleanPath("Command.CustomCommands.Enabled")) {
            OtherListeners.customCommandAliases = CommandFile.getCustomCommandAliases();
            p.sendMessage(SettingsFile.getPrefix() + " §7Custom Command Aliases has been reloaded!");
        } else {
            p.sendMessage(SettingsFile.getPrefix() + " §7Custom Command Aliases is not activated!");
        }
        p.sendMessage(SettingsFile.getPrefix() + " §aThe NewSystem reload was completed successfully!");
    }

    public static void reload(CommandSender p) {
        SettingsFile.loadConfig();
        p.sendMessage(SettingsFile.getPrefix() + " §7Settings File has been reloaded!");
        CommandFile.loadConfig();
        p.sendMessage(SettingsFile.getPrefix() + " §7Command File has been reloaded!");
        ListenerFile.loadConfig();
        p.sendMessage(SettingsFile.getPrefix() + " §7Event File has been reloaded!");
        OtherFile.loadConfig();
        p.sendMessage(SettingsFile.getPrefix() + " §7Other File has been reloaded!");
        SavingsFile.loadConfig();
        p.sendMessage(SettingsFile.getPrefix() + " §7Savings File has been reloaded!");
        if (CommandFile.getBooleanPath("Command.Kit.Enabled")) {
            KitFile.loadConfig();
            p.sendMessage(SettingsFile.getPrefix() + " §7Kit System has been reloaded!");
        } else {
            p.sendMessage(SettingsFile.getPrefix() + " §7Kit System is not activated!");
        }
        if (CommandFile.getBooleanPath("Command.Hologram.Enabled")) {
            for (String hologram : HologramAPI.getHolograms()) {
                HologramAPI.reloadHologram(hologram);
            }
            p.sendMessage(SettingsFile.getPrefix() + " §7Hologram System has been reloaded!");
        } else {
            p.sendMessage(SettingsFile.getPrefix() + " §7Hologram System is not activated!");
        }
        if (TabListFile.getBooleanPath("TabList.Enabled")) {
            TabListFile.loadConfig();
            TabListPrefix.setTabListForAll();
            p.sendMessage(SettingsFile.getPrefix() + " §7TabList Prefix System has been reloaded!");
        } else {
            p.sendMessage(SettingsFile.getPrefix() + " §7TabList Prefix System is not activated!");
        }
        if (OtherFile.getBooleanPath("Other.ScoreBoard.Enabled")) {
            ScoreboardManager.scoreboardTitle = OtherFile.getStringListPath("Other.ScoreBoard.Title");
            ScoreboardManager.scoreboardScores = OtherFile.getStringListPath("Other.ScoreBoard.Scores");
            ScoreboardManager.speed = OtherFile.getIntegerPath("Other.ScoreBoard.UpdateSpeed");
            ScoreboardManager.updateEveryScoreboard();
            p.sendMessage(SettingsFile.getPrefix() + " §7Scoreboard System has been reloaded!");
        } else {
            p.sendMessage(SettingsFile.getPrefix() + " §7Scoreboard System is not activated!");
        }
        if (OtherFile.getBooleanPath("Other.ChatFilter.Enabled")) {
            new ChatFilter().init();
            p.sendMessage(SettingsFile.getPrefix() + " §7ChatFilter System has been reloaded!");
        } else {
            p.sendMessage(SettingsFile.getPrefix() + " §7ChatFilter System is not activated!");
        }
        if (OtherFile.getBooleanPath("Other.Portal.Enabled")) {
            new Portal().init();
            p.sendMessage(SettingsFile.getPrefix() + " §7Portal System has been reloaded!");
        } else {
            p.sendMessage(SettingsFile.getPrefix() + " §7Portal System is not activated!");
        }
        OtherListeners.commandAliases = CommandFile.getCommandAliases();
        p.sendMessage(SettingsFile.getPrefix() + " §7Command Aliases has been reloaded!");
        if (CommandFile.getBooleanPath("Command.CustomCommands.Enabled")) {
            OtherListeners.customCommandAliases = CommandFile.getCustomCommandAliases();
            p.sendMessage(SettingsFile.getPrefix() + " §7Custom Command Aliases has been reloaded!");
        } else {
            p.sendMessage(SettingsFile.getPrefix() + " §7Custom Command Aliases is not activated!");
        }
        p.sendMessage(SettingsFile.getPrefix() + " §aThe NewSystem reload was completed successfully!");
    }
}
