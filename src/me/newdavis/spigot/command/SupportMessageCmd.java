package me.newdavis.spigot.command;

import me.newdavis.spigot.file.SettingsFile;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SupportMessageCmd implements CommandExecutor, TabCompleter {

    private static final HashMap<Player, String> savedTemplate = new HashMap<>();
    private static String savedTemplateConsole = "";

    private static List<String> usage;
    private static String perm;
    private static List<String> templates;
    private static boolean hover;
    private static String hoverMessage;
    private static String messageNoTemplatesExist;
    private static String templateFormat;
    private static List<String> messageDeleted;
    private static List<String> messageSendYourSelf;
    private static List<String> currentMessage;
    private static List<String> messageListTemplates;
    private static List<String> message;
    private static List<String> messagePlayer;

    public SupportMessageCmd() {
        usage = CommandFile.getStringListPath("Command.SupportMessage.Usage");
        perm = CommandFile.getStringPath("Command.SupportMessage.Permission");
        templates = CommandFile.getConfigurationSection("Command.SupportMessage.Templates");
        hover = CommandFile.getBooleanPath("Command.SupportMessage.HoverTemplate");
        hoverMessage = CommandFile.getStringPath("Command.SupportMessage.HoverMessage");
        messageNoTemplatesExist = CommandFile.getStringPath("Command.SupportMessage.MessageNoTemplateExist").replace("{Prefix}", SettingsFile.getPrefix());
        templateFormat = CommandFile.getStringPath("Command.SupportMessage.TemplateFormat");
        messageDeleted = CommandFile.getStringListPath("Command.SupportMessage.MessageDeleted");
        messageSendYourSelf = CommandFile.getStringListPath("Command.SupportMessage.MessageCantSendToYourSelf");
        currentMessage = CommandFile.getStringListPath("Command.SupportMessage.MessageCurrentMessage");
        messageListTemplates = CommandFile.getStringListPath("Command.SupportMessage.MessageList");
        message = CommandFile.getStringListPath("Command.SupportMessage.Message");
        messagePlayer = CommandFile.getStringListPath("Command.SupportMessage.MessagePlayer");
        if(!NewSystem.loadedCommands.contains(this)) {
            NewSystem.loadedCommands.add(this);
            NewSystem.getInstance().getCommand("supportmessage").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if(args.length == 0) {
                    for(String value : usage) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }else if(args.length == 1) {
                    if(args[0].equalsIgnoreCase("list")) {
                        if(!hover) {
                            String msg = messageListTemplates.get(0).replace("{Prefix}", SettingsFile.getPrefix());
                            String templateList = "";
                            if (!templates.isEmpty()) {
                                for (String template : templates) {
                                    templateList += templateFormat.replace("{Template}", template);
                                }
                            } else {
                                p.sendMessage(msg);
                                String msg2 = messageListTemplates.get(1).replace("{Prefix}", SettingsFile.getPrefix()).replace("{Templates}", messageNoTemplatesExist);
                                p.sendMessage(msg2);
                                return true;
                            }

                            String msg2 = messageListTemplates.get(1).replace("{Prefix}", SettingsFile.getPrefix()).replace("{Templates}", templateList);
                            p.sendMessage(msg);
                            p.sendMessage(msg2);
                        }else{
                            String msg = messageListTemplates.get(0).replace("{Prefix}", SettingsFile.getPrefix());
                            BaseComponent component = null;
                            if (!templates.isEmpty()) {
                                for (String template : templates) {
                                    String templateMessage = CommandFile.getStringPath("Command.SupportMessage.Templates." + template).replace("{Prefix}", SettingsFile.getPrefix());
                                    String[] format = templateFormat.split(" ");
                                    TextComponent textComponent = new TextComponent(format[1] + template);
                                    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage.replace("{Template}", templateMessage)).create()));
                                    if (component == null) {
                                        component = new TextComponent(messageListTemplates.get(1).replace("{Templates}", "").replace("{Prefix}", SettingsFile.getPrefix()));
                                    }
                                    component.addExtra(textComponent);
                                    component.addExtra(templateFormat.replace("{Template}", ""));
                                }
                            } else {
                                p.sendMessage(msg);
                                String msg2 = messageListTemplates.get(1).replace("{Prefix}", SettingsFile.getPrefix()).replace("{Templates}", messageNoTemplatesExist);
                                p.sendMessage(msg2);
                                return true;
                            }

                            p.sendMessage(msg);
                            p.spigot().sendMessage(component);
                        }
                    }else if(args[0].equalsIgnoreCase("clear")) {
                        savedTemplate.remove(p);
                        for(String value : messageDeleted) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }else{
                        String arg = args[0];
                        boolean isTemplate = false;
                        for(String template : templates) {
                            if(template.equalsIgnoreCase(arg)) {
                                arg = template;
                                isTemplate = true;
                            }
                        }

                        if(isTemplate) {
                            String template = CommandFile.getStringPath("Command.SupportMessage.Templates." + arg).replace("{Prefix}", SettingsFile.getPrefix());
                            savedTemplate.put(p, (savedTemplate.get(p) != null ? savedTemplate.get(p) : "") + template);
                        }else{
                            savedTemplate.put(p, (savedTemplate.get(p) != null ? savedTemplate.get(p) : "") + arg.replace("&", "§"));
                        }

                        for(String msg : currentMessage) {
                            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{SupportMessage}", savedTemplate.get(p)));
                        }
                    }
                }else if(args.length == 2) {
                    String arg = args[1];
                    if (args[0].equalsIgnoreCase("send")) {
                        Player t = Bukkit.getPlayer(arg);
                        if (t != null) {
                            if (p == t) {
                                for(String value : messageSendYourSelf) {
                                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                            } else {
                                String msg = savedTemplate.get(p);
                                for (String msgP : messagePlayer) {
                                    p.sendMessage(msgP.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{SupportMessage}", msg));
                                }
                                for (String msgT : message) {
                                    t.sendMessage(msgT.replace("{Prefix}", SettingsFile.getPrefix()).replace("{SupportMessage}", msg));
                                }
                                savedTemplate.remove(p);
                            }
                        }else{
                            p.sendMessage(SettingsFile.getOffline());
                        }
                    } else {
                        arg = args[0] + " " + args[1];
                        savedTemplate.put(p, (savedTemplate.get(p) != null ? savedTemplate.get(p) : "") + arg.replace("&", "§"));
                        for (String msg : currentMessage) {
                            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{SupportMessage}", savedTemplate.get(p)));
                        }
                    }
                }else{
                    String arg = "";
                    for(int i = 0; i < args.length; i++) {
                        arg += " " + args[i];
                    }

                    savedTemplate.put(p, (savedTemplate.get(p) != null ? savedTemplate.get(p) : "") + arg.replace("&", "§"));
                    for(String msg : currentMessage) {
                        p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{SupportMessage}", savedTemplate.get(p)));
                    }
                }
            }else{
                p.sendMessage(SettingsFile.getNoPerm());
            }
        }else{
            if(args.length == 0) {
                for(String value : usage) {
                    sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }else if(args.length == 1) {
                if(args[0].equalsIgnoreCase("list")) {
                    String msg = messageListTemplates.get(0).replace("{Prefix}", SettingsFile.getPrefix());
                    String templateList = "";
                    if (!templates.isEmpty()) {
                        for (String template : templates) {
                            templateList += templateFormat.replace("{Template}", template);
                        }
                    } else {
                        sender.sendMessage(msg);
                        String msg2 = messageListTemplates.get(1).replace("{Prefix}", SettingsFile.getPrefix()).replace("{Templates}", messageNoTemplatesExist);
                        sender.sendMessage(msg2);
                        return true;
                    }

                    String msg2 = messageListTemplates.get(1).replace("{Prefix}", SettingsFile.getPrefix()).replace("{Templates}", templateList);
                    sender.sendMessage(msg);
                    sender.sendMessage(msg2);
                }else if(args[0].equalsIgnoreCase("clear")) {
                    savedTemplateConsole = "";
                    for(String value : messageDeleted) {
                        sender.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }else{
                    String arg = args[0];
                    boolean isTemplate = false;
                    for(String template : templates) {
                        if(template.equalsIgnoreCase(arg)) {
                            arg = template;
                            isTemplate = true;
                        }
                    }

                    if(isTemplate) {
                        String template = CommandFile.getStringPath("Command.SupportMessage.Templates." + arg).replace("{Prefix}", SettingsFile.getPrefix());
                        savedTemplateConsole += template;
                    }else{
                        savedTemplateConsole += arg.replace("&", "§");
                    }

                    for(String msg : currentMessage) {
                        sender.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{SupportMessage}", savedTemplateConsole));
                    }
                }
            }else if(args.length == 2) {
                String arg = args[1];
                if (args[0].equalsIgnoreCase("send")) {
                    Player t = Bukkit.getPlayer(arg);
                    if (t != null) {
                        for (String msgP : messagePlayer) {
                            sender.sendMessage(msgP.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)).replace("{SupportMessage}", savedTemplateConsole));
                        }
                        for (String msgT : message) {
                            t.sendMessage(msgT.replace("{Prefix}", SettingsFile.getPrefix()).replace("{SupportMessage}", savedTemplateConsole));
                        }
                        savedTemplateConsole = "";
                    }else{
                        sender.sendMessage(SettingsFile.getOffline());
                    }
                } else {
                    arg = args[0] + " " + args[1];
                    savedTemplateConsole += arg.replace("&", "§");
                    for (String msg : currentMessage) {
                        sender.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{SupportMessage}", savedTemplateConsole));
                    }
                }
            }else{
                String arg = "";
                for(int i = 0; i < args.length; i++) {
                    arg += " " + args[i];
                }

                savedTemplateConsole += arg.replace("&", "§");
                for(String msg : currentMessage) {
                    sender.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{SupportMessage}", savedTemplateConsole));
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        List<String> tabCompletions = new ArrayList<>();

        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if (args.length == 1) {
                    String[] completions = {"list", "clear", "send"};
                    for(String completion : completions) {
                        if(completion.contains(args[0])) {
                            tabCompletions.add(completion);
                        }
                    }

                    for(String template : CommandFile.getConfigurationSection("Command.SupportMessage.Templates")) {
                        if(template.contains(args[0])) {
                            tabCompletions.add(template);
                        }
                    }
                } else if (args.length == 2) {
                    for(Player all : Bukkit.getOnlinePlayers()) {
                        if(all.getName().contains(args[1])) {
                            tabCompletions.add(all.getName());
                        }
                    }
                }
            }
        }else{
            if (args.length == 1) {
                String[] completions = {"list", "clear", "send"};
                for(String completion : completions) {
                    if(completion.contains(args[0])) {
                        tabCompletions.add(completion);
                    }
                }

                for(String template : CommandFile.getConfigurationSection("Command.SupportMessage.Templates")) {
                    if(template.contains(args[0])) {
                        tabCompletions.add(template);
                    }
                }
            } else if (args.length == 2) {
                for(Player all : Bukkit.getOnlinePlayers()) {
                    if(all.getName().contains(args[1])) {
                        tabCompletions.add(all.getName());
                    }
                }
            }
        }

        return tabCompletions;
    }
}
