package me.newdavis.spigot.command;

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import net.md_5.bungee.api.chat.*;
import me.newdavis.spigot.api.HologramAPI;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HologramCmd implements CommandExecutor, TabCompleter {

    private static List<String> usage;
    private static String perm;
    private static String permTitle;
    private static String permLine;
    private static String permManage;
    private static List<String> hologramNotExist;
    private static List<String> hologramLineNotExist;
    private static List<String> reloaded;
    private static List<String> renamed;
    private static List<String> created;
    private static List<String> deleted;
    private static List<String> lineAdded;
    private static List<String> lineRemoved;
    private static List<String> lineSet;
    private static List<String> lineSwitched;
    private static List<String> titleSet;
    private static List<String> moved;
    private static boolean hover;
    private static boolean teleport;
    private static List<String> messages;
    private static List<String> noHologramsCreated;
    private static String hologramFormat;
    private static String hoverMessage;

    public HologramCmd() {
        usage = CommandFile.getStringListPath("Command.Hologram.Usage");
        perm = CommandFile.getStringPath("Command.Hologram.Permission.Use");
        permTitle = CommandFile.getStringPath("Command.Hologram.Permission.Title");
        permLine = CommandFile.getStringPath("Command.Hologram.Permission.Line");
        permManage = CommandFile.getStringPath("Command.Hologram.Permission.Manage");
        hologramNotExist = CommandFile.getStringListPath("Command.Hologram.Message.HologramNotExist");
        hologramLineNotExist = CommandFile.getStringListPath("Command.Hologram.Message.HologramLineNotExist");
        reloaded = CommandFile.getStringListPath("Command.Hologram.Message.Reloaded");
        renamed = CommandFile.getStringListPath("Command.Hologram.Message.Renamed");
        created = CommandFile.getStringListPath("Command.Hologram.Message.Created");
        deleted = CommandFile.getStringListPath("Command.Hologram.Message.Deleted");
        lineAdded = CommandFile.getStringListPath("Command.Hologram.Message.LineAdded");
        lineRemoved = CommandFile.getStringListPath("Command.Hologram.Message.LineRemoved");
        lineSet = CommandFile.getStringListPath("Command.Hologram.Message.LineSet");
        lineSwitched = CommandFile.getStringListPath("Command.Hologram.Message.LineSwitched");
        titleSet = CommandFile.getStringListPath("Command.Hologram.Message.TitleSet");
        moved = CommandFile.getStringListPath("Command.Hologram.Message.Moved");
        hover = CommandFile.getBooleanPath("Command.Hologram.HoverHologram");
        teleport = CommandFile.getBooleanPath("Command.Hologram.EnableTeleportToHologram");
        messages = CommandFile.getStringListPath("Command.Hologram.Message.HologramList");
        noHologramsCreated = CommandFile.getStringListPath("Command.Hologram.Message.NoHologramsCreated");
        hologramFormat = CommandFile.getStringPath("Command.Hologram.Message.HologramFormat");
        hoverMessage = CommandFile.getStringPath("Command.Hologram.Message.HoverMessage").replace("{Prefix}", SettingsFile.getPrefix());
        if(!NewSystem.loadedCommands.contains(this)) {
            NewSystem.loadedCommands.add(this);
            NewSystem.getInstance().getCommand("hologram").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (NewSystem.hasPermission(p, perm)) {
                if (args.length == 0) {
                    for(String value : usage) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("list")) {
                        if (NewSystem.hasPermission(p, permManage)) {
                            sendHologramList(p);
                        } else {
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                        return true;
                    } else if (args[0].equalsIgnoreCase("reload")) {
                        if (NewSystem.hasPermission(p, permManage)) {
                            for (String hologram : HologramAPI.getHolograms()) {
                                HologramAPI.reloadHologram(hologram);
                            }
                            for(String value : reloaded) {
                                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        } else {
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                        return true;
                    }else {
                        for (String value : usage) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                } else if (args.length == 2) {
                    String hologramName = args[1];
                    if (args[0].equalsIgnoreCase("create")) {
                        if (NewSystem.hasPermission(p, permManage)) {
                            HologramAPI.createHologram(hologramName, p.getLocation());
                            for (String msg : created) {
                                p.sendMessage(msg.replace("{HologramName}", hologramName).replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        } else {
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    } else if (args[0].equalsIgnoreCase("delete")) {
                        if (HologramAPI.hologramExist(hologramName)) {
                            if (NewSystem.hasPermission(p, permManage)) {
                                HologramAPI.deleteHologramByName(hologramName);
                                for (String msg : deleted) {
                                    p.sendMessage(msg.replace("{HologramName}", hologramName).replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                            } else {
                                p.sendMessage(SettingsFile.getNoPerm());
                            }
                        } else {
                            for(String value : hologramNotExist) {
                                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("move")) {
                        if (HologramAPI.hologramExist(hologramName)) {
                            if (NewSystem.hasPermission(p, permManage)) {
                                HologramAPI.moveHologram(hologramName, p.getLocation());
                                for (String msg : moved) {
                                    p.sendMessage(msg.replace("{HologramName}", hologramName).replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                            } else {
                                p.sendMessage(SettingsFile.getNoPerm());
                            }
                        } else {
                            for(String value : hologramNotExist) {
                                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        }
                    } else {
                        for(String value : usage) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                } else if (args.length == 3) {
                    String hologramName = args[1];
                    if (HologramAPI.hologramExist(hologramName)) {
                        if (args[0].equalsIgnoreCase("addline") || args[0].equalsIgnoreCase("al")) {
                            if (NewSystem.hasPermission(p, permLine)) {
                                String text = getTextOfStringArray(args, 2);
                                HologramAPI.addHologramLine(hologramName, text);
                                for (String msg : lineAdded) {
                                    p.sendMessage(msg.replace("{HologramName}", hologramName).replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                            }else{
                                p.sendMessage(SettingsFile.getNoPerm());
                            }
                        } else if (args[0].equalsIgnoreCase("removeline") || args[0].equalsIgnoreCase("rl")) {
                            if (NewSystem.hasPermission(p, permLine)) {
                                int line = getIntegerOfString(args[2]);
                                if (HologramAPI.hologramLineExist(hologramName, line)) {
                                    HologramAPI.deleteHologramLineByLine(hologramName, line);
                                    for (String msg : lineRemoved) {
                                        p.sendMessage(msg.replace("{HologramName}", hologramName).replace("{Line}", String.valueOf(line)).replace("{Prefix}", SettingsFile.getPrefix()));
                                    }
                                } else {
                                    for(String value : hologramNotExist) {
                                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                                    }
                                }
                            }else{
                                p.sendMessage(SettingsFile.getNoPerm());
                            }
                        } else if (args[0].equalsIgnoreCase("setTitle") || args[0].equalsIgnoreCase("st")) {
                            if (NewSystem.hasPermission(p, permTitle)) {
                                String text = getTextOfStringArray(args, 2);
                                HologramAPI.setHologramTitle(hologramName, text);
                                for (String msg : titleSet) {
                                    p.sendMessage(msg.replace("{HologramName}", hologramName).replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                            }else{
                                p.sendMessage(SettingsFile.getNoPerm());
                            }
                        } else if (args[0].equalsIgnoreCase("rename") || args[0].equalsIgnoreCase("r") || args[0].equalsIgnoreCase("renameHologram")) {
                            if (NewSystem.hasPermission(p, permManage)) {
                                String name = args[2];
                                HologramAPI.renameHologramByName(hologramName, name);
                                for (String msg : renamed) {
                                    p.sendMessage(msg.replace("{HologramName}", hologramName).replace("{NewHologramName}", name).replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                            }else{
                                p.sendMessage(SettingsFile.getNoPerm());
                            }
                        } else if (args[0].equalsIgnoreCase("move") || args[0].equalsIgnoreCase("movehere")) {
                            if (NewSystem.hasPermission(p, permManage)) {
                                Player t = Bukkit.getPlayer(args[2]);
                                if (t != null) {
                                    HologramAPI.moveHologram(hologramName, t.getLocation());
                                    for (String msg : moved) {
                                        p.sendMessage(msg.replace("{HologramName}", hologramName).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{Prefix}", SettingsFile.getPrefix()));
                                    }
                                } else {
                                    p.sendMessage(SettingsFile.getOffline());
                                }
                            }else{
                                p.sendMessage(SettingsFile.getNoPerm());
                            }
                        } else {
                            for(String value : usage) {
                                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        }
                    } else {
                        for(String value : hologramNotExist) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                } else if (args.length == 4) {
                    String hologramName = args[1];
                    if (HologramAPI.hologramExist(hologramName)) {
                        if (args[0].equalsIgnoreCase("addline") || args[0].equalsIgnoreCase("al")) {
                            if (NewSystem.hasPermission(p, permLine)) {
                                String text = getTextOfStringArray(args, 2);
                                HologramAPI.addHologramLine(hologramName, text);
                                for (String msg : lineAdded) {
                                    p.sendMessage(msg.replace("{HologramName}", hologramName).replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                            }else{
                                p.sendMessage(SettingsFile.getNoPerm());
                            }
                        } else if (args[0].equalsIgnoreCase("setTitle") || args[0].equalsIgnoreCase("st")) {
                            if (NewSystem.hasPermission(p, permLine)) {
                                String text = getTextOfStringArray(args, 2);
                                HologramAPI.setHologramTitle(hologramName, text);
                                for (String msg : titleSet) {
                                    p.sendMessage(msg.replace("{HologramName}", hologramName).replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                            }else{
                                p.sendMessage(SettingsFile.getNoPerm());
                            }
                        } else if (args[0].equalsIgnoreCase("setLine") || args[0].equalsIgnoreCase("sl")) {
                            if (NewSystem.hasPermission(p, permLine)) {
                                int line = getIntegerOfString(args[2]);
                                if (HologramAPI.hologramLineExist(hologramName, line)) {
                                    String text = getTextOfStringArray(args, 3);
                                    HologramAPI.setHologramLineText(hologramName, line, text);
                                    for (String msg : lineSet) {
                                        p.sendMessage(msg.replace("{Line}", String.valueOf(line)).replace("{Text}", text).replace("{Prefix}", SettingsFile.getPrefix()));
                                    }
                                } else {
                                    for(String value : hologramLineNotExist) {
                                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                                    }
                                }
                            }else{
                                p.sendMessage(SettingsFile.getNoPerm());
                            }
                        } else if (args[0].equalsIgnoreCase("switchLine") || args[0].equalsIgnoreCase("swl") || args[0].equalsIgnoreCase("switch")) {
                            if (NewSystem.hasPermission(p, permLine)) {
                                int line = getIntegerOfString(args[2]);
                                if (HologramAPI.hologramLineExist(hologramName, line)) {
                                    int lineSwitch = getIntegerOfString(args[3]);
                                    if (HologramAPI.hologramLineExist(hologramName, lineSwitch)) {
                                        HologramAPI.switchHologramLine(hologramName, line, lineSwitch);
                                        for (String msg : lineSwitched) {
                                            p.sendMessage(msg.replace("{Line}", String.valueOf(line)).replace("{SwitchedLine}", String.valueOf(lineSwitch)).replace("{Prefix}", SettingsFile.getPrefix()));
                                        }
                                    } else {
                                        for(String value : hologramLineNotExist) {
                                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                                        }
                                    }
                                } else {
                                    for(String value : hologramLineNotExist) {
                                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                                    }
                                }
                            }else{
                                p.sendMessage(SettingsFile.getNoPerm());
                            }
                        } else {
                            for(String value : usage) {
                                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        }
                    } else {
                        for(String value : hologramNotExist) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                } else {
                    String hologramName = args[1];
                    if (HologramAPI.hologramExist(hologramName)) {
                        if (args[0].equalsIgnoreCase("addline") || args[0].equalsIgnoreCase("al")) {
                            if (NewSystem.hasPermission(p, permLine)) {
                                String text = getTextOfStringArray(args, 2);
                                HologramAPI.addHologramLine(hologramName, text);
                                for (String msg : lineAdded) {
                                    p.sendMessage(msg.replace("{HologramName}", hologramName).replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                            }else{
                                p.sendMessage(SettingsFile.getNoPerm());
                            }
                        } else if (args[0].equalsIgnoreCase("setTitle") || args[0].equalsIgnoreCase("st")) {
                            if (NewSystem.hasPermission(p, permTitle)) {
                                String text = getTextOfStringArray(args, 2);
                                HologramAPI.setHologramTitle(hologramName, text);
                                for (String msg : titleSet) {
                                    p.sendMessage(msg.replace("{HologramName}", hologramName).replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                            }else{
                                p.sendMessage(SettingsFile.getNoPerm());
                            }
                        } else if (args[0].equalsIgnoreCase("setLine") || args[0].equalsIgnoreCase("sl")) {
                            if (NewSystem.hasPermission(p, permLine)) {
                                int line = getIntegerOfString(args[2]);
                                if (HologramAPI.hologramLineExist(hologramName, line)) {
                                    String text = getTextOfStringArray(args, 3);
                                    HologramAPI.setHologramLineText(hologramName, line, text);
                                    for (String msg : lineSet) {
                                        p.sendMessage(msg.replace("{Line}", String.valueOf(line)).replace("{Text}", text).replace("{Prefix}", SettingsFile.getPrefix()));
                                    }
                                } else {
                                    for(String value : hologramLineNotExist) {
                                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                                    }
                                }
                            }else{
                                p.sendMessage(SettingsFile.getNoPerm());
                            }
                        } else {
                            for(String value : usage) {
                                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        }
                    } else {
                        for(String value : hologramNotExist) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                }
            } else {
                p.sendMessage(SettingsFile.getNoPerm());
            }
        }else{
            sender.sendMessage(SettingsFile.getOnlyPlayerCanExecute());
        }
        return false;
    }

    private static int getIntegerOfString(String string) {
        int number = 0;

        try{
            number = Integer.parseInt(string);
        }catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return number;
    }

    private static String getTextOfStringArray(String[] array, int start) {
        String text = "";

        for(int i = start; i < array.length; i++) {
            if(i == start) {
                text = array[i];
            }else{
                text += " " + array[i];
            }
        }

        return text.replace("&", "§");
    }

    private static void sendHologramList(Player p) {
        List<String> holograms = HologramAPI.getHolograms();
        BaseComponent component = null;

        for(String msg : messages) {
            if(msg.contains("{HologramCount}")) {
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{HologramCount}", String.valueOf(holograms.size())));
            }else {
                if (!holograms.isEmpty()) {
                    for (String hologram : holograms) {
                        if(hover) {
                            String title = HologramAPI.getCustomNameOfHologram(hologram);
                            Location location = HologramAPI.getLocationOfHologram(hologram);
                            List<Integer> lines = HologramAPI.getHologramLines(hologram);
                            String[] format = hologramFormat.split(" ");
                            TextComponent textComponent = new TextComponent(format[1] + hologram);
                            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage
                                    .replace("{HologramName}", hologram)
                                    .replace("{Prefix}", SettingsFile.getPrefix())
                                    .replace("{Title}", title)
                                    .replace("{World}", location.getWorld().getName())
                                    .replace("{Loc-X}", String.valueOf((int) location.getX()))
                                    .replace("{Loc-Y}", String.valueOf((int) location.getY()))
                                    .replace("{Loc-Z}", String.valueOf((int) location.getZ()))
                                    .replace("{LineCount}", String.valueOf(lines.size()))).create()));
                            if (teleport) {
                                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + (int) location.getX() + " " + (int) location.getY() + " " + (int) location.getZ() + " " + location.getWorld().getName()));
                            }
                            if (component != null) {
                                component.addExtra(textComponent);
                                component.addExtra(hologramFormat.replace("{HologramName}", ""));
                            } else {
                                component = new TextComponent(messages.get(1).replace("{Holograms}", "").replace("{Prefix}", SettingsFile.getPrefix()));
                                component.addExtra(textComponent);
                                component.addExtra(hologramFormat.replace("{HologramName}", ""));
                            }
                        }else{
                            Location location = HologramAPI.getLocationOfHologram(hologram);
                            String[] format = hologramFormat.split(" ");
                            TextComponent textComponent = new TextComponent(format[1] + hologram);
                            if (teleport) {
                                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + (int) location.getX() + " " + (int) location.getY() + " " + (int) location.getZ() + " " + location.getWorld().getName()));
                            }
                            if (component != null) {
                                component.addExtra(textComponent);
                                component.addExtra(hologramFormat.replace("{HologramName}", ""));
                            } else {
                                component = new TextComponent(messages.get(1).replace("{Holograms}", "").replace("{Prefix}", SettingsFile.getPrefix()));
                                component.addExtra(textComponent);
                                component.addExtra(hologramFormat.replace("{HologramName}", ""));
                            }
                        }
                    }
                } else {
                    for(String value : noHologramsCreated) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                    return;
                }
            }
        }

        p.spigot().sendMessage(component);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        List<String> tabCompletions = new ArrayList<>();

        if(sender instanceof Player) {
            Player p = (Player) sender;
            if (NewSystem.hasPermission(p, perm)) {
                if (args.length == 1) {
                    String[] completions = {"list"};
                    for(String completion : completions) {
                        if(completion.contains(args[0])) {
                            tabCompletions.add(completion);
                        }
                    }
                    if(NewSystem.hasPermission(p, permManage)) {
                        completions = new String[]{"reload", "create", "delete", "rename", "move"};
                        for(String completion : completions) {
                            if(completion.contains(args[0])) {
                                tabCompletions.add(completion);
                            }
                        }
                    }
                    if(NewSystem.hasPermission(p, permLine)) {
                        completions = new String[]{"addline", "removeline", "setline", "switchline"};
                        for(String completion : completions) {
                            if(completion.contains(args[0])) {
                                tabCompletions.add(completion);
                            }
                        }
                    }
                    if(NewSystem.hasPermission(p, permTitle)) {
                        completions = new String[]{"settitle"};
                        for(String completion : completions) {
                            if(completion.contains(args[0])) {
                                tabCompletions.add(completion);
                            }
                        }
                    }
                }else if(args.length == 2) {
                    for(String holo : HologramAPI.getHolograms()) {
                        if(holo.contains(args[1])) {
                            tabCompletions.add(holo);
                        }
                    }
                }
            }
        }
        return tabCompletions;
    }
}
