package me.newdavis.spigot.command;
//Plugin by NewDavis

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

import java.util.*;

public class ReportCmd implements CommandExecutor, TabCompleter {

    private static List<String> usage;
    private static List<String> usageWithPerm;
    private static List<String> msgSelfReport;
    private static String perm;
    private static List<String> msgPCreated;
    private static List<String> msgSCreated;
    private static List<String> msgPStatusChanged;
    private static List<String> msgSEdit;
    private static List<String> msgSClosed;
    private static List<String> msgReportAccepted;
    private static List<String> msgAlreadyCreated;
    private static List<String> msgReportClosed;
    private static List<String> msgReportDoNotExist;
    private static List<String> msgAlreadyInAnotherTicket;
    private static String noSupporter;
    private static String statusCreated;
    private static String statusEdit;
    private static String statusClosed;
    private static String hoverMessageAccept;
    private static String hoverMessageClose;
    private static List<String> listMessage;
    private static String hoverFormat;
    private static long delayForAutoDeleteInMinutes;

    public void init() {
        usage = CommandFile.getStringListPath("Command.Report.Usage");
        usageWithPerm = CommandFile.getStringListPath("Command.Report.UsageWithPermission");
        msgSelfReport = CommandFile.getStringListPath("Command.Report.MessageSelfReport");
        perm = CommandFile.getStringPath("Command.Report.Permission");
        msgPCreated = CommandFile.getStringListPath("Command.Report.Message");
        msgSCreated = CommandFile.getStringListPath("Command.Report.MessageSupporterCreated");
        msgPStatusChanged = CommandFile.getStringListPath("Command.Report.changeStatus");
        msgSEdit = CommandFile.getStringListPath("Command.Report.MessageSupporterEdit");
        msgSClosed = CommandFile.getStringListPath("Command.Report.MessageSupporterClosed");
        msgReportAccepted = CommandFile.getStringListPath("Command.Report.MessageReportAccepted");
        msgAlreadyCreated = CommandFile.getStringListPath("Command.Report.MessageReportAlreadyCreated");
        msgReportClosed = CommandFile.getStringListPath("Command.Report.MessageReportClosed");
        msgReportDoNotExist = CommandFile.getStringListPath("Command.Report.MessageReportDoNotExist");
        msgAlreadyInAnotherTicket = CommandFile.getStringListPath("Command.Report.MessageSupporterAlreadyInReport");
        noSupporter = CommandFile.getStringPath("Command.Report.NoSupporter");
        statusCreated = CommandFile.getStringPath("Command.Report.StatusCreated");
        statusEdit = CommandFile.getStringPath("Command.Report.StatusEdit");
        statusClosed = CommandFile.getStringPath("Command.Report.StatusClosed");
        hoverMessageAccept = CommandFile.getStringPath("Command.Report.HoverMessageAcceptReport").replace("{Prefix}", SettingsFile.getPrefix());
        hoverMessageClose = CommandFile.getStringPath("Command.Report.HoverMessageCloseReport").replace("{Prefix}", SettingsFile.getPrefix());
        listMessage = CommandFile.getStringListPath("Command.Report.ListMessage");
        hoverFormat = CommandFile.getStringPath("Command.Report.ReportFormat");
        delayForAutoDeleteInMinutes = CommandFile.getLongPath("Command.Report.DelayForAutoDeleteInMinutes");
        NewSystem.getInstance().getCommand("report").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(!NewSystem.hasPermission(p, perm)) {
                if(args.length > 1) {
                    Player t = Bukkit.getPlayer(args[0]);
                    if(t != null) {
                        if(t != p) {
                            String reason = getReason(args);
                            createReport(p, t, reason);
                        }else{
                            for(String value : msgSelfReport) {
                                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        }
                    }else{
                        p.sendMessage(SettingsFile.getOffline());
                    }
                }else{
                    for(String value : usage) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }else{
                if(args.length == 1) {
                    if(args[0].equalsIgnoreCase("list")) {
                        sendList(p);
                    }else{
                        for(String value : usageWithPerm) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                }else if(args.length == 2) {
                    OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
                    if (args[1].equalsIgnoreCase("accept") || args[1].equalsIgnoreCase("annehmen")) {
                        acceptReport(p, t);
                    } else if (args[1].equalsIgnoreCase("close") || args[1].equalsIgnoreCase("schließen")) {
                        closeReport(p, t);
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

    public static String getReason(String[] args) {
        String reason = "";
        for(int i = 1; i < args.length; i++) {
            if(i == 1) {
                reason = args[i];
            }else{
                reason = reason + " " + args[i];
            }
        }
        return reason;
    }

    public static boolean alreadyCreatedReport(Player p) {
        List<String> reports = SavingsFile.getStringListPath("Report.Reports");
        if(reports.contains(p.getUniqueId().toString())) {
            return true;
        }
        return false;
    }

    public static boolean hasPlayerCreateReport(OfflinePlayer p) {
        List<String> reports = SavingsFile.getStringListPath("Report.Reports");
        if(reports.contains(p.getUniqueId().toString())) {
            return true;
        }
        return false;
    }

    public static boolean isPlayerReported(OfflinePlayer p) {
        List<String> reports = SavingsFile.getStringListPath("Report.Reports");
        for(String uuid : reports) {
            if (SavingsFile.getStringPath("Report." + uuid + ".To").equalsIgnoreCase(p.getUniqueId().toString())) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getPlayerReportOf(OfflinePlayer reported) {
        if(isPlayerReported(reported)) {
            List<String> reports = SavingsFile.getStringListPath("Report.Reports");
            List<String> playerWhoReported = new ArrayList<>();
            for (String uuid : reports) {
                if (SavingsFile.getStringPath("Report." + uuid + ".To").equalsIgnoreCase(reported.getUniqueId().toString())) {
                    playerWhoReported.add(uuid);
                }
            }
            return playerWhoReported;
        }
        return new ArrayList<>();
    }

    public static void createReport(Player p, Player t, String reason) {
        List<String> reports = SavingsFile.getStringListPath("Report.Reports");
        if(!alreadyCreatedReport(p)) {
            reports.add(p.getUniqueId().toString());
            SavingsFile.setPath("Report." + p.getUniqueId() + ".To", t.getUniqueId().toString());
            SavingsFile.setPath("Report." + p.getUniqueId() + ".Reason", reason);
            SavingsFile.setPath("Report." + p.getUniqueId() + ".Status", statusCreated);
            SavingsFile.setPath("Report.Reports", reports);

            for(String msg : msgPCreated) {
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()));
            }

            for(Player all : Bukkit.getOnlinePlayers()) {
                for (String msg : msgSCreated) {
                    if (NewSystem.hasPermission(all, perm)) {
                        if (msg.contains("{Accept-Report}")) {
                            TextComponent text = new TextComponent(hoverMessageAccept);

                            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessageAccept).create()));
                            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report " + p.getName() + " accept"));

                            all.spigot().sendMessage(text);
                        } else {
                            String prefixPlayer = NewSystem.getName(p);
                            String prefixTarget = NewSystem.getName(t);
                            all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                    .replace("{ReportOf}", prefixPlayer)
                                    .replace("{Player}", prefixTarget)
                                    .replace("{Reason}", reason)
                                    .replace("{Status}", statusCreated));
                        }
                    }
                }
            }
            reportTimer(p);
        }else{
            for(String value : msgAlreadyCreated) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static String getStatus(OfflinePlayer p) {
        String statusOfReport = SavingsFile.getStringPath("Report." + p.getUniqueId() + ".Status");

        if(statusOfReport.equalsIgnoreCase(statusCreated)) {
            return statusCreated;
        }else if(statusOfReport.equalsIgnoreCase(statusEdit)) {
            return statusEdit;
        }else if(statusOfReport.equalsIgnoreCase(statusClosed)) {
            return statusClosed;
        }
        return "";
    }

    public static HashMap<Player, OfflinePlayer> supporters = new HashMap<>();

    public static void acceptReport(Player p, OfflinePlayer t) {
        boolean vanish = false;
        if(CommandFile.getBooleanPath("Command.Vanish.Enabled")) {
            vanish = CommandFile.getBooleanPath("Command.Report.EnableVanishOnAccept");
        }
        if(hasPlayerCreateReport(t)) {
            if(getStatus(t).equalsIgnoreCase(statusCreated)) {
                if(!supporters.containsKey(p)) {
                    supporters.put(p, t);
                    SavingsFile.setPath("Report." + t.getUniqueId() + ".Status", statusEdit);
                    SavingsFile.setPath("Report." + t.getUniqueId() + ".Supporter", p.getUniqueId().toString());
                    String uuidReportTo = SavingsFile.getStringPath("Report." + t.getUniqueId() + ".To");
                    Player reportToPlayer = Bukkit.getPlayer(UUID.fromString(uuidReportTo));
                    if (reportToPlayer != null) {
                        String reason = SavingsFile.getStringPath("Report." + t.getUniqueId() + ".Reason");

                        for (String msg : msgPStatusChanged) {
                            if(t.isOnline()) {
                                t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Status}", statusEdit));
                            }
                        }

                        if (vanish) {
                            if (!VanishCmd.playerIsVanished(p)) {
                                VanishCmd.toggleVanish(p, p);
                            }
                        }

                        String reportOfPrefix = NewSystem.getName(t);
                        p.teleport(reportToPlayer);
                        for(String key : msgReportAccepted) {
                            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", reportOfPrefix));
                        }

                        for (Player all : Bukkit.getOnlinePlayers()) {
                            for (String msg : msgSEdit) {
                                if (NewSystem.hasPermission(all, perm)) {
                                    if (msg.contains("{Close-Report}")) {
                                        TextComponent text = new TextComponent(hoverMessageClose);

                                        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessageClose).create()));
                                        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report " + t.getName() + " close"));

                                        all.spigot().sendMessage(text);
                                    } else {
                                        String reportOf = NewSystem.getName(t);
                                        String reportTo = NewSystem.getName(reportToPlayer);
                                        all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                                .replace("{ReportOf}", reportOf)
                                                .replace("{Player}", reportTo)
                                                .replace("{Reason}", reason)
                                                .replace("{Supporter}", NewSystem.getName(p))
                                                .replace("{Status}", statusEdit));
                                    }
                                }
                            }
                        }
                    } else {
                        p.sendMessage(SettingsFile.getError().replace("{Error}", "Report"));
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
            for(String value : msgReportDoNotExist) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void closeReport(Player p, OfflinePlayer t) {
        if(hasPlayerCreateReport(t)) {
            supporters.remove(p);
            String reason = SavingsFile.getStringPath("Report." + t.getUniqueId() + ".Reason");
            String uuidReportTo = SavingsFile.getStringPath("Report." + t.getUniqueId() + ".To");
            OfflinePlayer reportToPlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuidReportTo));

            List<String> reports = SavingsFile.getStringListPath("Report.Reports");
            reports.remove(t.getUniqueId().toString());
            SavingsFile.setPath("Report.Reports", reports);
            SavingsFile.setPath("Report." + t.getUniqueId(), null);

            for(String msg : msgPStatusChanged) {
                if(t.isOnline()) {
                    t.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Status}", statusClosed));
                }
            }

            String prefix = NewSystem.getName(t);
            for(String key : msgReportClosed) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", prefix));
            }

            for (Player all : Bukkit.getOnlinePlayers()) {
                for (String msg : msgSClosed) {
                    if (NewSystem.hasPermission(all, perm)) {
                        String reportOf = NewSystem.getName(t);
                        String reportTo = NewSystem.getName(reportToPlayer);
                        all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                .replace("{ReportOf}", reportOf)
                                .replace("{Player}", reportTo)
                                .replace("{Reason}", reason)
                                .replace("{Supporter}", NewSystem.getName(p))
                                .replace("{Status}", statusClosed));
                    }
                }
            }
        }else{
            for(String value : msgReportDoNotExist) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void sendList(Player p) {
        List<String> reports = SavingsFile.getStringListPath("Report.Reports");
        for(String msg : listMessage) {
            if(msg.contains("{Reports}")) {
                for (String uuid : reports) {
                    OfflinePlayer reportOf = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                    String reportOfPrefix = NewSystem.getName(reportOf);

                    String reportToUUID = SavingsFile.getStringPath("Report." + uuid + ".To");
                    OfflinePlayer reportTo = Bukkit.getOfflinePlayer(UUID.fromString(reportToUUID));
                    String reportToPrefix = NewSystem.getName(reportTo);

                    String reason = SavingsFile.getStringPath("Report." + uuid + ".Reason");
                    String status = SavingsFile.getStringPath("Report." + uuid + ".Status");

                    TextComponent text = new TextComponent(hoverFormat.replace("{Prefix}", SettingsFile.getPrefix())
                            .replace("{ReportOf}", reportOfPrefix)
                            .replace("{ReportTo}", reportToPrefix)
                            .replace("{Reason}", reason)
                            .replace("{Status}", status));

                    if(status.equalsIgnoreCase(CommandFile.getStringPath("Command.Report.StatusCreated"))) {
                        String hoverMessage = hoverMessageAccept.replace("{Prefix}", SettingsFile.getPrefix());
                        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
                        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report " + reportOf.getName() + " accept"));
                    }else if(status.equalsIgnoreCase(CommandFile.getStringPath("Command.Report.StatusEdit"))) {
                        String hoverMessage = hoverMessageClose.replace("{Prefix}", SettingsFile.getPrefix());
                        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
                        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report " + reportOf.getName() + " close"));
                    }

                    p.spigot().sendMessage(text);
                }
            }else{
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Report-Count}", String.valueOf(reports.size())));
            }
        }
    }

    public static void reportTimer(OfflinePlayer reportOf) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(NewSystem.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (SavingsFile.isPathSet("Report." + reportOf.getUniqueId() + ".Status")) {
                    if (SavingsFile.getStringPath("Report." + reportOf.getUniqueId() + ".Status").equalsIgnoreCase(CommandFile.getStringPath("Command.Report.StatusCreated"))) {
                        String reportedUUID = SavingsFile.getStringPath("Report." + reportOf.getUniqueId() + ".To");
                        OfflinePlayer reported = Bukkit.getOfflinePlayer(UUID.fromString(reportedUUID));
                        deleteReport(reported);
                    }
                }
            }
        }, 20*60*delayForAutoDeleteInMinutes);
    }

    public static void deleteReport(OfflinePlayer reported) {
        List<String> reportOfPlayers = getPlayerReportOf(reported);
        for (String uuid : reportOfPlayers) {
            OfflinePlayer reportOf = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
            String reason = SavingsFile.getStringPath("Report." + uuid + ".Reason");

            String uuidReportTo = SavingsFile.getStringPath("Report." + uuid + ".To");
            OfflinePlayer reportTo = Bukkit.getOfflinePlayer(UUID.fromString(uuidReportTo));

            String uuidSupporter = SavingsFile.getStringPath("Report." + uuid + ".Supporter");
            OfflinePlayer supporter = null;
            if (!uuidSupporter.equals("")) {
                supporter = Bukkit.getOfflinePlayer(uuidSupporter);
                if (supporter.isOnline()) {
                    return;
                }
            }

            List<String> reports = SavingsFile.getStringListPath("Report.Reports");
            reports.remove(reportOf.getUniqueId().toString());
            SavingsFile.setPath("Report.Reports", reports);
            SavingsFile.setPath("Report." + reportOf.getUniqueId(), null);

            for (String msg : msgReportClosed) {
                if (reportOf.isOnline()) {
                    reportOf.getPlayer().sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Status}", statusClosed));
                }
            }

            for (Player all : Bukkit.getOnlinePlayers()) {
                for (String msg : msgSClosed) {
                    if (NewSystem.hasPermission(all, perm)) {
                        String reportOfPrefix = NewSystem.getName(reportOf);
                        String reportToPrefix = NewSystem.getName(reportTo);
                        String reportSupPrefix;
                        if (supporter != null) {
                            reportSupPrefix = NewSystem.getName(supporter);
                        } else {
                            reportSupPrefix = noSupporter;
                        }
                        all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                .replace("{ReportOf}", reportOfPrefix)
                                .replace("{Player}", reportToPrefix)
                                .replace("{Reason}", reason)
                                .replace("{Supporter}", reportSupPrefix)
                                .replace("{Status}", statusClosed));
                    }
                }
            }
        }
    }

    public static void QuitEvent(Player p) {
        List<String> reports = SavingsFile.getStringListPath("Report.Reports");
        if(supporters.containsKey(p)) {
            Player t = Bukkit.getPlayer(supporters.get(p).getUniqueId());

            String reason = SavingsFile.getStringPath("Report." + t.getUniqueId() + ".Reason");

            String uuidReportTo = SavingsFile.getStringPath("Report." + t.getUniqueId() + ".To");
            Player reportTo = Bukkit.getPlayer(UUID.fromString(uuidReportTo));

            SavingsFile.setPath("Report." + t.getUniqueId() + ".Status", statusCreated);
            SavingsFile.setPath("Report.Reports", reports);

            for(Player all : Bukkit.getOnlinePlayers()) {
                for (String msg : msgSCreated) {
                    if (NewSystem.hasPermission(all, perm)) {
                        if (msg.contains("{Accept-Report}")) {
                            TextComponent text = new TextComponent(hoverMessageAccept);

                            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessageAccept).create()));
                            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report " + t.getName() + " accept"));

                            all.spigot().sendMessage(text);
                        } else {
                            String prefixPlayer = NewSystem.getName(t);
                            String prefixTarget = NewSystem.getName(reportTo);
                            all.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                                    .replace("{ReportOf}", prefixPlayer)
                                    .replace("{Player}", prefixTarget)
                                    .replace("{Reason}", reason)
                                    .replace("{Status}", statusCreated));
                        }
                    }
                }
            }
            reportTimer(t);
        }else{
            deleteReport(p);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        List<String> tabCompletions = new ArrayList<>();

        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if(args.length == 1) {
                    String[] completions = {"list"};
                    for(String completion : completions) {
                        if(completion.contains(args[0])) {
                            tabCompletions.add(completion);
                        }
                    }

                    for(String report : SavingsFile.getStringListPath("Report.Reports")) {
                        if(report.contains(args[0])) {
                            tabCompletions.add(report);
                        }
                    }
                }else if(args.length == 2) {
                    if(!args[0].equalsIgnoreCase("list")) {
                        String[] completions = {"accept", "close"};
                        for(String completion : completions) {
                            if(completion.contains(args[1])) {
                                tabCompletions.add(completion);
                            }
                        }
                    }
                }
            }else{
                if(args.length == 1) {
                    for(Player all : Bukkit.getOnlinePlayers()) {
                        if(all.getName().contains(args[0])) {
                            tabCompletions.add(all.getName());
                        }
                    }
                }
            }
        }

        return tabCompletions;
    }
}
