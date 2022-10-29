package me.newdavis.spigot.command;

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import net.md_5.bungee.api.chat.*;
import me.newdavis.spigot.file.SavingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PeaceCmd implements CommandExecutor, TabCompleter {

    private static List<String> usage;
    private static String perm;
    private static List<String> canNotSendSelf;
    private static List<String> messageRequest;
    private static List<String> messageRequestPlayer;
    private static List<String> messageRequestAlreadySend;
    private static String hoverAcceptHover;
    private static String hoverAcceptText;
    private static String hoverDeclineHover;
    private static String hoverDeclineText;
    private static boolean enableClick;
    private static List<String> messageDisband;
    private static List<String> messageDisbandPlayer;
    private static List<String> messageNoPeace;
    private static List<String> messageAccept;
    private static List<String> messageAcceptPlayer;
    private static List<String> messageDecline;
    private static List<String> messageDeclinePlayer;
    private static List<String> messageNoRequest;
    private static List<String> peaceListMessage;
    private static String peaceListFormat;
    private static List<String> noPeace;
    private static List<String> requestListMessage;
    private static String requestListFormat;
    private static List<String> noRequests;

    public void init() {
        usage = CommandFile.getStringListPath("Command.Peace.Usage");
        perm = CommandFile.getStringPath("Command.Peace.Permission");
        canNotSendSelf = CommandFile.getStringListPath("Command.Peace.MessageCanNotSendRequestSelf");
        messageRequest = CommandFile.getStringListPath("Command.Peace.MessageRequest");
        messageRequestPlayer = CommandFile.getStringListPath("Command.Peace.MessageRequestPlayer");
        messageRequestAlreadySend = CommandFile.getStringListPath("Command.Peace.MessageRequestAlreadySend");
        hoverAcceptHover = CommandFile.getStringPath("Command.Peace.Request.Hover.Accept.Hover").replace("{Prefix}", SettingsFile.getPrefix());
        hoverAcceptText = CommandFile.getStringPath("Command.Peace.Request.Hover.Accept.Text").replace("{Prefix}", SettingsFile.getPrefix());
        hoverDeclineHover = CommandFile.getStringPath("Command.Peace.Request.Hover.Decline.Hover").replace("{Prefix}", SettingsFile.getPrefix());
        hoverDeclineText = CommandFile.getStringPath("Command.Peace.Request.Hover.Decline.Text").replace("{Prefix}", SettingsFile.getPrefix());
        enableClick = CommandFile.getBooleanPath("Command.Peace.Request.Hover.EnableClick");
        messageDisband = CommandFile.getStringListPath("Command.Peace.MessagePeaceDisband");
        messageDisbandPlayer = CommandFile.getStringListPath("Command.Peace.MessagePeaceDisbandPlayer");
        messageNoPeace = CommandFile.getStringListPath("Command.Peace.MessageNoPeaceWithPlayer");
        messageAccept = CommandFile.getStringListPath("Command.Peace.MessageAccept");
        messageAcceptPlayer = CommandFile.getStringListPath("Command.Peace.MessageAcceptPlayer");
        messageDecline = CommandFile.getStringListPath("Command.Peace.MessageDecline");
        messageDeclinePlayer = CommandFile.getStringListPath("Command.Peace.MessageDeclinePlayer");
        messageNoRequest = CommandFile.getStringListPath("Command.Peace.MessageNoRequestOfPlayer");
        peaceListMessage = CommandFile.getStringListPath("Command.Peace.MessagePeaceList");
        peaceListFormat = CommandFile.getStringPath("Command.Peace.PeaceListFormat");
        noPeace = CommandFile.getStringListPath("Command.Peace.MessageNoPeace");
        requestListMessage = CommandFile.getStringListPath("Command.Peace.MessageRequestList");
        requestListFormat = CommandFile.getStringPath("Command.Peace.RequestListFormat");
        noRequests = CommandFile.getStringListPath("Command.Peace.MessageNoRequests");
        NewSystem.getInstance().getCommand("peace").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if(args.length == 1) {
                    if (args[0].equalsIgnoreCase("list")) {
                        sendPeaceList(p);
                    } else if (args[0].equalsIgnoreCase("requests")) {
                        sendRequestList(p);
                    } else {
                        Player t = Bukkit.getPlayer(args[0]);
                        if(t != null) {
                            if(p != t) {
                                sendRequest(p, t);
                            }else{
                                for(String msg : canNotSendSelf) {
                                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                                }
                            }
                        }else{
                            p.sendMessage(SettingsFile.getOffline());
                        }
                    }
                }else if(args.length == 2) {
                    if(args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("annehmen")) {
                        Player t = Bukkit.getPlayer(args[1]);
                        if(t != null) {
                            acceptRequest(p, t);
                        }else{
                            p.sendMessage(SettingsFile.getOffline());
                        }
                    }else if(args[0].equalsIgnoreCase("decline") || args[0].equalsIgnoreCase("ablehnen")) {
                        Player t = Bukkit.getPlayer(args[1]);
                        if(t != null) {
                            declineRequest(p, t);
                        }else{
                            p.sendMessage(SettingsFile.getOffline());
                        }
                    }else{
                        for(String msg : usage) {
                            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
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

    private List<String> getPeace(OfflinePlayer p) {
        List<String> peace = new ArrayList<>();
        if(SavingsFile.isPathSet("Peace." + p.getUniqueId() + ".PeaceList")) {
            peace = SavingsFile.getStringListPath("Peace." + p.getUniqueId() + ".PeaceList");
        }
        return peace;
    }

    public boolean inPeace(Player p, OfflinePlayer t) {
        return getPeace(p).contains(t.getUniqueId().toString());
    }

    private List<String> getRequests(OfflinePlayer p) {
        List<String> requests = new ArrayList<>();
        if(SavingsFile.isPathSet("Peace." + p.getUniqueId() + ".PeaceRequestList")) {
            requests = SavingsFile.getStringListPath("Peace." + p.getUniqueId() + ".PeaceRequestList");
        }
        return requests;
    }

    private boolean gotRequest(Player p, OfflinePlayer t) {
        if(!inPeace(p, t)) {
            return getRequests(p).contains(t.getUniqueId().toString());
        }
        return false;
    }

    private void sendRequest(Player p, Player t) {
        List<String> requests = getRequests(t);
        if (!requests.contains(p.getUniqueId().toString())) {
            if(!inPeace(p, t)) {
                requests.add(p.getUniqueId().toString());
                SavingsFile.setPath("Peace." + t.getUniqueId() + ".PeaceRequestList", requests);

                for(String msg : messageRequest) {
                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                }

                if(!enableClick) {
                    for (String msg : messageRequestPlayer) {
                        t.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(p, false)).replace("{DisplayName}", NewSystem.getName(p, true)));
                    }
                }else{
                    for(String msg : messageRequestPlayer) {
                        if(msg.contains("{Accept}")) {
                            TextComponent text = new TextComponent(hoverAcceptText);
                            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverAcceptHover).create()));
                            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/peace accept " + p.getName()));
                            t.spigot().sendMessage(text);
                        }else if(msg.contains("{Decline}")) {
                            TextComponent text = new TextComponent(hoverDeclineText);
                            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverDeclineHover).create()));
                            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/peace decline " + p.getName()));
                            t.spigot().sendMessage(text);
                        }else{
                            t.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(p, false)).replace("{DisplayName}", NewSystem.getName(p, true)));
                        }
                    }
                }
            }else{
                disbandPeace(p, t);
            }
        }else{
            for (String msg : messageRequestAlreadySend) {
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
            }
        }
    }

    private void disbandPeace(Player p, OfflinePlayer t) {
        List<String> peaceP = getPeace(p);
        List<String> peaceT = getPeace(t);
        if(inPeace(p, t)) {
            peaceP.remove(t.getUniqueId().toString());
            peaceT.remove(p.getUniqueId().toString());
            SavingsFile.setPath("Peace." + p.getUniqueId() + ".PeaceList", peaceP);
            SavingsFile.setPath("Peace." + t.getUniqueId() + ".PeaceList", peaceT);

            for(String msg : messageDisband) {
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
            }

            if(t.isOnline()) {
                for (String msg : messageDisbandPlayer) {
                    t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(p, false)).replace("{DisplayName}", NewSystem.getName(p, true)));
                }
            }
        }else{
            for(String msg : messageNoPeace) {
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
            }
        }
    }

    private void acceptRequest(Player p, OfflinePlayer t) {
        List<String> peaceP = getPeace(p);
        List<String> peaceT = getPeace(t);
        List<String> requests = getRequests(p);
        if(gotRequest(p, t)) {
            requests.remove(t.getUniqueId().toString());
            SavingsFile.setPath("Peace." + p.getUniqueId() + ".PeaceRequestList", requests);
            peaceP.add(t.getUniqueId().toString());
            SavingsFile.setPath("Peace." + p.getUniqueId() + ".PeaceList", peaceP);
            peaceT.add(p.getUniqueId().toString());
            SavingsFile.setPath("Peace." + t.getUniqueId() + ".PeaceList", peaceT);

            for(String msg : messageAccept) {
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
            }

            if(t.isOnline()) {
                for (String msg : messageAcceptPlayer) {
                    t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(p, false)).replace("{DisplayName}", NewSystem.getName(p, true)));
                }
            }
        }else{
            for(String msg : messageNoRequest) {
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
            }
        }
    }

    private void declineRequest(Player p, OfflinePlayer t) {
        List<String> requests = getRequests(p);
        if(gotRequest(p, t)) {
            requests.remove(t.getUniqueId().toString());
            SavingsFile.setPath("Peace." + p.getUniqueId() + ".PeaceRequestList", requests);

            for(String msg : messageDecline) {
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
            }

            if(t.isOnline()) {
                for (String msg : messageDeclinePlayer) {
                    t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(p, false)).replace("{DisplayName}", NewSystem.getName(p, true)));
                }
            }
        }else{
            for(String msg : messageNoRequest) {
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
            }
        }
    }

    private void sendPeaceList(Player p) {
        String peaceListString = peaceListFormat;
        List<String> peace = getPeace(p);
        for(String msg : peaceListMessage) {
            if(msg.contains("{Peace}")) {
                if(!peace.isEmpty()) {
                    for (int i = 0; i < peace.size(); i++) {
                        OfflinePlayer t = Bukkit.getOfflinePlayer(UUID.fromString(peace.get(i)));
                        if (i == (peace.size() - 1)) {
                            peaceListString = peaceListString.replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true));
                        } else {
                            peaceListString = peaceListString.replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)) + peaceListFormat;
                        }
                    }
                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Peace}", peaceListString));
                }else{
                    for(String msg2 : noPeace) {
                        p.sendMessage(msg2.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }else{
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Count}", String.valueOf(peace.size())));
            }
        }
    }

    private void sendRequestList(Player p) {
        String requestsListString = requestListFormat;
        List<String> requests = getRequests(p);
        for(String msg : requestListMessage) {
            if(msg.contains("{Requests}")) {
                if(!requests.isEmpty()) {
                    for (int i = 0; i < requests.size(); i++) {
                        OfflinePlayer t = Bukkit.getOfflinePlayer(UUID.fromString(requests.get(i)));
                        if (i == (requests.size() - 1)) {
                            requestsListString = requestsListString.replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true));
                        } else {
                            requestsListString = requestsListString.replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)) + requestListFormat;
                        }
                    }
                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Requests}", requestsListString));
                }else{
                    for(String msg2 : noRequests) {
                        p.sendMessage(msg2.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }else{
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Count}", String.valueOf(requests.size())));
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        List<String> tabCompletions = new ArrayList<>();

        if(sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 1) {
                String[] completions = {"list", "accept", "decline", "requests"};
                for(String completion : completions) {
                    if(completion.contains(args[0])) {
                        tabCompletions.add(completion);
                    }
                }

                for(Player all : Bukkit.getOnlinePlayers()) {
                    if(all.getName().contains(args[0])) {
                        tabCompletions.add(all.getName());
                    }
                }
            } else if (args.length == 2) {
                if (!(args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("requests"))) {
                    for(Player all : Bukkit.getOnlinePlayers()) {
                        if(all.getName().contains(args[1])) {
                            if (getRequests(p).contains(all.getName())) {
                                tabCompletions.add(all.getName());
                            }
                        }
                    }
                }
            }
        }

        return tabCompletions;
    }
}
