package me.newdavis.spigot.command;

import me.newdavis.spigot.file.SettingsFile;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import me.newdavis.spigot.file.CommandFile;
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
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SupportCmd implements CommandExecutor, TabCompleter {

    private static List<String> usage;
    private static List<String> usageWithPerm;
    private static String perm;
    private static String permSpy;
    private static String spyActivate;
    private static String spyDeactivate;
    private static List<String> spyToggle;
    private static List<String> msgP;
    private static List<String> msgPChangeStatus;
    private static List<String> msgSCreated;
    private static List<String> msgSEdit;
    private static List<String> msgSClosed;
    private static List<String> msgAccepted;
    private static List<String> msgTicketDoNotExist;
    private static List<String> msgAlreadyInAnotherTicket;
    private static List<String> msgSupportClosed;
    private static List<String> msgAlreadyCreated;
    private static String hoverMessageAccept;
    private static String hoverMessageClose;
    private static String statusCreated;
    private static String statusEdit;
    private static String statusClosed;
    private static List<String> listMessage;
    private static String listFormat;
    private static long delayForAutoDeleteInMinutes;
    private static String noSupporter;
    private static String formatSupporterToPlayer;
    private static String formatPlayerToSupporter;

    public SupportCmd() {
        usage = CommandFile.getStringListPath("Command.Support.Usage");
        usageWithPerm = CommandFile.getStringListPath("Command.Support.UsageWithPermission");
        perm = CommandFile.getStringPath("Command.Support.Permission.Use");
        permSpy = CommandFile.getStringPath("Command.Support.Permission.Spy");
        spyActivate = CommandFile.getStringPath("Command.Support.SpyActivate");
        spyDeactivate = CommandFile.getStringPath("Command.Support.SpyDeactivate");
        spyToggle = CommandFile.getStringListPath("Command.Support.MessageToggleSpy");
        msgP = CommandFile.getStringListPath("Command.Support.Message");
        msgPChangeStatus = CommandFile.getStringListPath("Command.Support.changeStatus");
        msgSCreated = CommandFile.getStringListPath("Command.Support.MessageSupporterCreated");
        msgSEdit = CommandFile.getStringListPath("Command.Support.MessageSupporterEdit");
        msgSClosed = CommandFile.getStringListPath("Command.Support.MessageSupporterClosed");
        msgAccepted = CommandFile.getStringListPath("Command.Support.MessageSupportAccepted");
        msgTicketDoNotExist = CommandFile.getStringListPath("Command.Support.MessageTicketDoNotExist");
        msgAlreadyInAnotherTicket = CommandFile.getStringListPath("Command.Support.MessageSupporterAlreadyInTicket");
        msgSupportClosed = CommandFile.getStringListPath("Command.Support.MessageSupportClosed");
        msgAlreadyCreated = CommandFile.getStringListPath("Command.Support.MessageSupportAlreadyCreated");
        hoverMessageAccept = CommandFile.getStringPath("Command.Support.HoverMessageAcceptSupport").replace("{Prefix}", SettingsFile.getPrefix());
        hoverMessageClose = CommandFile.getStringPath("Command.Support.HoverMessageCloseSupport").replace("{Prefix}", SettingsFile.getPrefix());
        statusCreated = CommandFile.getStringPath("Command.Support.StatusCreated");
        statusEdit = CommandFile.getStringPath("Command.Support.StatusEdit");
        statusClosed = CommandFile.getStringPath("Command.Support.StatusClosed");
        listMessage = CommandFile.getStringListPath("Command.Support.ListMessage");
        listFormat = CommandFile.getStringPath("Command.Support.SupportFormat");
        delayForAutoDeleteInMinutes = CommandFile.getLongPath("Command.Support.DelayForAutoDeleteInMinutes");
        noSupporter = CommandFile.getStringPath("Command.Support.NoSupporter");
        formatSupporterToPlayer = CommandFile.getStringPath("Command.Support.SupportChatFormatToPlayer").replace("{Prefix}", SettingsFile.getPrefix());
        formatPlayerToSupporter = CommandFile.getStringPath("Command.Support.SupportChatFormatToSupporter").replace("{Prefix}", SettingsFile.getPrefix());
        if(!NewSystem.loadedCommands.contains(this)) {
            NewSystem.loadedCommands.add(this);
            NewSystem.getInstance().getCommand("support").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(!NewSystem.hasPermission(p, perm)) {
                if(args.length == 0) {
                    createSupport(p);
                }else{
                    for(String value : usage) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }else{
                if(args.length == 1) {
                    if(args[0].equalsIgnoreCase("list")) {
                        sendList(p);
                    }else if(args[0].equalsIgnoreCase("spy")) {
                        if(NewSystem.hasPermission(p, permSpy)) {
                            toggleSpy(p);
                        }else{
                            p.sendMessage(SettingsFile.getNoPerm());
                        }
                    }else{
                        for(String value : usageWithPerm) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                }else if(args.length == 2) {
                    OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                    if (args[1].equalsIgnoreCase("accept") || args[1].equalsIgnoreCase("annehmen")) {
                        acceptSupport(p, t);
                    } else if (args[1].equalsIgnoreCase("close") || args[1].equalsIgnoreCase("schließen")) {
                        closeSupport(p, t);
                    }else{
                        for(String value : usageWithPerm) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                }else{
                    for(String value : usageWithPerm) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }
        }else{
            sender.sendMessage(SettingsFile.getOnlyPlayerCanExecute());
        }
        return false;
    }

    public static boolean alreadyCreatedSupport(Player p) {
        List<String> supportTickets = SavingsFile.getStringListPath("Support.Supports");
        if(supportTickets.contains(p.getUniqueId().toString())) {
            return true;
        }
        return false;
    }

    public static boolean hasPlayerCreateSupportTicket(OfflinePlayer p) {
        List<String> supportTickets = SavingsFile.getStringListPath("Support.Supports");
        if(supportTickets.contains(p.getUniqueId().toString())) {
            return true;
        }
        return false;
    }

    public static List<String> getSupportOfPlayers() {
        List<String> supportTickets = SavingsFile.getStringListPath("Support.Supports");
        if(!supportTickets.isEmpty()) {
            return supportTickets;
        }
        return new ArrayList<>();
    }

    public static boolean playerIsInSupportTicket(Player p) {
        if(getSupportOfPlayers().contains(p.getUniqueId().toString())) {
            return true;
        }else{
            for(String uuid : getSupportOfPlayers()) {
                if(SavingsFile.isPathSet("Support." + uuid + ".Supporter")) {
                    if(SavingsFile.getStringPath("Support." + uuid + ".Supporter").equalsIgnoreCase(p.getUniqueId().toString())) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public static ArrayList<Player> spy = new ArrayList<>();

    public static void toggleSpy(Player p) {
        if(spy.contains(p)) {
            for(String key : spyToggle) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Status}", spyDeactivate));
            }
            spy.remove(p);
        }else{
            for(String key : spyToggle) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Status}", spyActivate));
            }
            spy.add(p);
        }
    }

    public static void createSupport(Player p) {
        List<String> supportTickets = SavingsFile.getStringListPath("Support.Supports");
        if(!alreadyCreatedSupport(p)) {
            supportTickets.add(p.getUniqueId().toString());
            SavingsFile.setPath("Support." + p.getUniqueId() + ".Supporter", "");
            SavingsFile.setPath("Support." + p.getUniqueId() + ".Status", statusCreated);
            SavingsFile.setPath("Support.Supports", supportTickets);

            for(String msg : msgP) {
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
            }

            for(Player all : Bukkit.getOnlinePlayers()) {
                for (String msg : msgSCreated) {
                    if (NewSystem.hasPermission(all, perm)) {
                        if (msg.contains("{Accept-Support}")) {
                            TextComponent text = new TextComponent(hoverMessageAccept);

                            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessageAccept).create()));
                            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/support " + p.getName() + " accept"));

                            all.spigot().sendMessage(text);
                        } else {
                            String prefixPlayer = NewSystem.getName(p, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"));
                            all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                    .replace("{SupportTicketOf}", prefixPlayer)
                                    .replace("{Status}", statusCreated));
                        }
                    }
                }
            }
            supportTimer(p);
        }else{
            for(String value : msgAlreadyCreated) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static String getStatus(OfflinePlayer p) {
        String statusOfSupportTicket = SavingsFile.getStringPath("Support." + p.getUniqueId() + ".Status");

        if(statusOfSupportTicket.equalsIgnoreCase(statusCreated)) {
            return statusCreated;
        }else if(statusOfSupportTicket.equalsIgnoreCase(statusEdit)) {
            return statusEdit;
        }else if(statusOfSupportTicket.equalsIgnoreCase(statusClosed)) {
            return statusClosed;
        }
        return "";
    }

    public static HashMap<Player, OfflinePlayer> supporters = new HashMap<>();

    public static void acceptSupport(Player p, OfflinePlayer t) {
        if(hasPlayerCreateSupportTicket(t)) {
            if(getStatus(t).equalsIgnoreCase(statusCreated)) {
                if(!supporters.containsKey(p)) {
                    supporters.put(p, t);
                    SavingsFile.setPath("Support." + t.getUniqueId() + ".Status", statusEdit);
                    SavingsFile.setPath("Support." + t.getUniqueId() + ".Supporter", p.getUniqueId().toString());

                    for(String msg : msgPChangeStatus) {
                        if(t.isOnline()) {
                            t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Status}", statusEdit));
                        }
                    }

                    for(String key : msgAccepted) {
                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t, false)).replace("{DisplayName}", NewSystem.getName(t, true)));
                    }

                    for (Player all : Bukkit.getOnlinePlayers()) {
                        for (String msg : msgSEdit) {
                            if (NewSystem.hasPermission(all, perm)) {
                                if (msg.contains("{Close-Support}")) {
                                    TextComponent text = new TextComponent(hoverMessageClose);

                                    text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessageClose).create()));
                                    text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/support " + t.getName() + " close"));

                                    all.spigot().sendMessage(text);
                                } else {
                                    String supportOf = NewSystem.getName(t, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"));
                                    all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                            .replace("{SupportTicketOf}", supportOf)
                                            .replace("{Supporter}", NewSystem.getName(p, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName")))
                                            .replace("{Status}", statusEdit));
                                }
                            }
                        }
                    }
                }else{
                    for(String value : msgAlreadyInAnotherTicket) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }else{
                for(String value : msgAlreadyCreated) {
                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }else{
            for(String value : msgTicketDoNotExist) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void closeSupport(Player p, OfflinePlayer t) {
        if(hasPlayerCreateSupportTicket(t)) {
            if(!SavingsFile.getStringPath("Support." + t.getUniqueId() + ".Supporter").equalsIgnoreCase("")) {
                Player supporter = Bukkit.getPlayer(UUID.fromString(SavingsFile.getStringPath("Support." + t.getUniqueId() + ".Supporter")));
                supporters.remove(supporter);
            }

            List<String> supportTickets = SavingsFile.getStringListPath("Support.Supports");
            supportTickets.remove(t.getUniqueId().toString());
            SavingsFile.setPath("Support.Supports", supportTickets);
            SavingsFile.setPath("Support." + t.getUniqueId(), null);

            for(String msg : msgPChangeStatus) {
                if(t.isOnline()) {
                    t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Status}", statusClosed));
                }
            }

            String prefix = NewSystem.getName(t, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"));
            if(p != null) {
                for (String key : msgSupportClosed) {
                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", prefix));
                }
            }

            for (Player all : Bukkit.getOnlinePlayers()) {
                for (String msg : msgSClosed) {
                    if (NewSystem.hasPermission(all, perm)) {
                        String supportOf = NewSystem.getName(t, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"));
                        all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                .replace("{SupportTicketOf}", supportOf)
                                .replace("{Supporter}", (p == null ? noSupporter : NewSystem.getName(p, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"))))
                                .replace("{Status}", statusClosed));
                    }
                }
            }
        }else{
            if(p != null) {
                for (String value : msgTicketDoNotExist) {
                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }
    }

    public static void sendList(Player p) {
        List<String> supportTickets = SavingsFile.getStringListPath("Support.Supports");
        for(String msg : listMessage) {
            if(msg.contains("{SupportTickets}")) {
                for (String uuid : supportTickets) {
                    OfflinePlayer supportTicketOf = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                    String supportTicketOfPrefix = NewSystem.getName(supportTicketOf, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"));
                    String status = SavingsFile.getStringPath("Support." + uuid + ".Status");

                    TextComponent text = new TextComponent(listFormat.replace("{Prefix}", SettingsFile.getPrefix())
                            .replace("{SupportTicketOf}", supportTicketOfPrefix)
                            .replace("{Status}", status));

                    if(status.equalsIgnoreCase(statusCreated)) {
                        String hoverMessage = hoverMessageAccept.replace("{Prefix}", SettingsFile.getPrefix());
                        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
                        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/support " + supportTicketOf.getName() + " accept"));
                    }else if(status.equalsIgnoreCase(statusEdit)) {
                        String hoverMessage = hoverMessageClose.replace("{Prefix}", SettingsFile.getPrefix());
                        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
                        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/support " + supportTicketOf.getName() + " close"));
                    }

                    p.spigot().sendMessage(text);
                }
            }else{
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Support-Count}", String.valueOf(supportTickets.size())));
            }
        }
    }

    public static void supportTimer(Player supportTicketOf) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(NewSystem.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (SavingsFile.isPathSet("Support." + supportTicketOf.getUniqueId() + ".Status")) {
                    if (SavingsFile.getStringPath("Support." + supportTicketOf.getUniqueId() + ".Status").equalsIgnoreCase(CommandFile.getStringPath("Command.Support.StatusCreated"))) {
                        deleteSupport(supportTicketOf);
                    }
                }
            }
        }, 20*60*delayForAutoDeleteInMinutes);
    }

    public static void deleteSupport(OfflinePlayer supportTicketOf) {
        Player supporter = null;
        if(!SavingsFile.getStringPath("Support." + supportTicketOf.getUniqueId() + ".Supporter").equalsIgnoreCase("")) {
            String uuidSupporter = SavingsFile.getStringPath("Support." + supportTicketOf.getUniqueId() + ".Supporter");
            supporter = Bukkit.getPlayer(UUID.fromString(uuidSupporter));
        }

        closeSupport(supporter, supportTicketOf);
    }

    public static void QuitEvent(Player p) {
        if(supporters.containsKey(p)) {
            String uuidSupportTicketOf = "";
            for(String uuid : getSupportOfPlayers()) {
                if(SavingsFile.isPathSet("Support." + uuid + ".Supporter")) {
                    if(SavingsFile.getStringPath("Support." + uuid + ".Supporter").equalsIgnoreCase(p.getUniqueId().toString())) {
                        uuidSupportTicketOf = uuid;
                    }
                }
            }

            if(!uuidSupportTicketOf.equalsIgnoreCase("")) {
                OfflinePlayer t = Bukkit.getOfflinePlayer(UUID.fromString(uuidSupportTicketOf));
                deleteSupport(t);
            }
        }else{
            if(getSupportOfPlayers().contains(p.getUniqueId().toString())) {
                deleteSupport(p);
            }
        }
    }

    public static void ChatEvent(Player p, String message) {
        if(supporters.containsKey(p)) {
            String uuidSupportTicketOf = "";
            for(String uuid : getSupportOfPlayers()) {
                if(SavingsFile.isPathSet("Support." + uuid + ".Supporter")) {
                    if(SavingsFile.getStringPath("Support." + uuid + ".Supporter").equalsIgnoreCase(p.getUniqueId().toString())) {
                        uuidSupportTicketOf = uuid;
                    }
                }
            }

            if(!uuidSupportTicketOf.equals("")) {
                Player supportTicketOf = Bukkit.getPlayer(UUID.fromString(uuidSupportTicketOf));

                p.sendMessage(formatSupporterToPlayer.replace("{Supporter}", NewSystem.getName(p, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"))).replace("{Player}", NewSystem.getName(supportTicketOf, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"))).replace("{Message}", message));
                supportTicketOf.sendMessage(formatSupporterToPlayer.replace("{Supporter}", NewSystem.getName(p, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"))).replace("{Player}", NewSystem.getName(supportTicketOf, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"))).replace("{Message}", message));

                for(Player spyPlayer : spy) {
                    if (spyPlayer != supportTicketOf && spyPlayer != p) {
                        spyPlayer.sendMessage(formatPlayerToSupporter.replace("{Supporter}", NewSystem.getName(p, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"))).replace("{Player}", NewSystem.getName(supportTicketOf, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"))).replace("{Message}", message));
                    }
                }
            }
        }else{
            if(getSupportOfPlayers().contains(p.getUniqueId().toString())) {
                String uuidSupporter = SavingsFile.getStringPath("Support." + p.getUniqueId() + ".Supporter");
                Player supporter = Bukkit.getPlayer(UUID.fromString(uuidSupporter));

                p.sendMessage(formatPlayerToSupporter.replace("{Supporter}", NewSystem.getName(supporter, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"))).replace("{Player}", NewSystem.getName(p, false)).replace("{DisplayName}", NewSystem.getName(p, true)).replace("{Message}", message));
                supporter.sendMessage(formatPlayerToSupporter.replace("{Supporter}", NewSystem.getName(supporter, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"))).replace("{Player}", NewSystem.getName(p, false)).replace("{DisplayName}", NewSystem.getName(p, true)).replace("{Message}", message));

                for(Player spyPlayer : spy) {
                    if (spyPlayer != p && spyPlayer != supporter) {
                        spyPlayer.sendMessage(formatPlayerToSupporter.replace("{Supporter}", NewSystem.getName(supporter, SettingsFile.getPlayerReplace().equalsIgnoreCase("DisplayName"))).replace("{Player}", NewSystem.getName(p, false)).replace("{DisplayName}", NewSystem.getName(p, true)).replace("{Message}", message));
                    }
                }
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        List<String> tabCompletions = new ArrayList<>();

        if(sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 1) {
                if(NewSystem.hasPermission(p, perm)) {
                    String[] completions = {"list"};
                    for(String completion : completions) {
                        if(completion.contains(args[0])) {
                            tabCompletions.add(completion);
                        }
                    }

                    for(Player all : Bukkit.getOnlinePlayers()) {
                        if(all.getName().contains(args[0])) {
                            if (hasPlayerCreateSupportTicket(all)) {
                                tabCompletions.add(all.getName());
                            }
                        }
                    }
                }
                if(NewSystem.hasPermission(p, permSpy)) {
                    String[] completions = {"spy"};
                    for(String completion : completions) {
                        if(completion.contains(args[0])) {
                            tabCompletions.add(completion);
                        }
                    }
                }
            }else if(args.length == 2) {
                if(!(args[0].equalsIgnoreCase("spy") || args[0].equalsIgnoreCase("list"))) {
                    String[] completions = {"accept", "close"};
                    for(String completion : completions) {
                        if(completion.contains(args[1])) {
                            tabCompletions.add(completion);
                        }
                    }
                }
            }
        }

        return tabCompletions;
    }

}
