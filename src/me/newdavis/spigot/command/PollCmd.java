package me.newdavis.spigot.command;
//Plugin by NewDavis

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import me.newdavis.spigot.file.SavingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PollCmd implements CommandExecutor {

    private static List<String> usage;
    private static String perm;
    private static List<String> pollActive;
    private static List<String> messageList;
    private static List<String> messageDelayList;
    private static List<String> messageEndsSoon;
    private static List<String> messageStarted;
    private static List<String> messageNoWinner;
    private static List<String> messagePollEnds;
    private static String oneVote;
    private static String hoverMessagePath;
    private static String yes;
    private static String no;
    private static String pollHoverMessage;
    private static List<String> alreadyVoted;
    private static List<String> pollNotActive;
    private static List<String> voted;
    private static String voteYes;
    private static String voteNo;

    public PollCmd() {
        usage = CommandFile.getStringListPath("Command.Poll.Usage");
        perm = CommandFile.getStringPath("Command.Poll.Permission");
        pollActive = CommandFile.getStringListPath("Command.Poll.MessagePollActive");
        messageList = CommandFile.getStringListPath("Command.Poll.Message");
        messageDelayList = CommandFile.getStringListPath("Command.Poll.MessageDelay");
        messageEndsSoon = CommandFile.getStringListPath("Command.Poll.MessagePollEndsSoon");
        messageStarted = CommandFile.getStringListPath("Command.Poll.MessageStarted");
        messageNoWinner = CommandFile.getStringListPath("Command.Poll.MessagePollEndsWithNoWinner");
        messagePollEnds = CommandFile.getStringListPath("Command.Poll.MessagePollEnds");
        oneVote = CommandFile.getStringPath("Command.Poll.MessageVotesIsOne");
        hoverMessagePath = CommandFile.getStringPath("Command.Poll.HoverMessage");
        yes = CommandFile.getStringPath("Command.Poll.Yes");
        no = CommandFile.getStringPath("Command.Poll.No");
        pollHoverMessage = CommandFile.getStringPath("Command.Poll.HoverMessage");
        alreadyVoted = CommandFile.getStringListPath("Command.Poll.MessageAlreadyVoted");
        pollNotActive = CommandFile.getStringListPath("Command.Poll.MessagePollNotActive");
        voted = CommandFile.getStringListPath("Command.Poll.MessageVoted");
        voteYes = CommandFile.getStringPath("Command.Poll.Yes");
        voteNo = CommandFile.getStringPath("Command.Poll.No");
        if(!NewSystem.loadedCommands.contains(this)) {
            NewSystem.loadedCommands.add(this);
            NewSystem.getInstance().getCommand("poll").setExecutor(this);
            NewSystem.getInstance().getCommand("ja").setExecutor(this);
            NewSystem.getInstance().getCommand("nein").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(cmd.getName().equalsIgnoreCase("ja")) {
                PollCmd.voteYes(p);
            }else if(cmd.getName().equalsIgnoreCase("nein")) {
                PollCmd.voteNo(p);
            }else if(cmd.getName().equalsIgnoreCase("poll")) {
                if (NewSystem.hasPermission(p, perm)) {
                    if (args.length > 0) {
                        startPoll(p, ChatColor.translateAlternateColorCodes('&', String.join(" ", args)));
                    } else {
                        for(String value : usage) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                }else{
                    p.sendMessage(SettingsFile.getNoPerm());
                }
            }
        }else{
            sender.sendMessage(SettingsFile.getOnlyPlayerCanExecute());
        }
        return false;
    }

    public static void startPoll(Player p, String question) {
        if(!SavingsFile.getBooleanPath("Poll.Enabled")) {
            SavingsFile.setPath("Poll.Votes", new ArrayList<>());
            SavingsFile.setPath("Poll.Enabled", true);
            SavingsFile.setPath("Poll.Vote.Yes", 0);
            SavingsFile.setPath("Poll.Vote.No", 0);

            for(String key : messageStarted) {
                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
            }
            for(String msg : messageList) {
                if(msg.contains("{Embed-Yes}") || msg.contains("{Embed-No}")) {
                    TextComponent text = new TextComponent(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Question}", question));

                    String hoverMessage;
                    if(msg.contains("{Embed-Yes}")) {
                        hoverMessage = hoverMessagePath.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Poll}", yes);
                        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ja"));
                        text.setText(text.getText().replace("{Embed-Yes}", ""));
                    }else{
                        hoverMessage = hoverMessagePath.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Poll}", no);
                        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nein"));
                        text.setText(text.getText().replace("{Embed-No}", ""));
                    }

                    text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));

                    Bukkit.spigot().broadcast(text);
                }else{
                    Bukkit.broadcastMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Question}", question));
                }
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(NewSystem.getInstance(), new Runnable() {
                @Override
                public void run() {

                    int yesVotes = SavingsFile.getIntegerPath("Poll.Vote.Yes");
                    int noVotes = SavingsFile.getIntegerPath("Poll.Vote.No");
                    int seconds = 30;

                    for(String key : messageEndsSoon) {
                        Bukkit.broadcastMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Seconds}", String.valueOf(seconds)));
                    }
                    for(String msg : messageDelayList) {
                        if(msg.contains("{Embed-Yes}") || msg.contains("{Embed-No}")) {
                            TextComponent text = new TextComponent(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Question}", question).replace("{Count-Yes}", String.valueOf(yesVotes)).replace("{Count-No}", String.valueOf(noVotes)));

                            String hoverMessage = "";
                            if(msg.contains("{Embed-Yes}")) {
                                hoverMessage = pollHoverMessage.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Poll}", CommandFile.getStringPath("Command.Poll.Yes"));
                                text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ja"));
                                text.setText(text.getText().replace("{Embed-Yes}", ""));
                            }else{
                                hoverMessage = pollHoverMessage.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Poll}", CommandFile.getStringPath("Command.Poll.No"));
                                text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nein"));
                                text.setText(text.getText().replace("{Embed-No}", ""));
                            }

                            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));

                            Bukkit.spigot().broadcast(text);
                        }else{
                            Bukkit.broadcastMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Question}", question).replace("{Count-Yes}", String.valueOf(yesVotes)).replace("{Count-No}", String.valueOf(noVotes)));
                        }
                    }

                    Bukkit.getScheduler().scheduleSyncDelayedTask(NewSystem.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            int seconds = 3;
                            for(String key : messageEndsSoon) {
                                Bukkit.broadcastMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Seconds}", String.valueOf(seconds)));
                            }
                            Bukkit.getScheduler().scheduleSyncDelayedTask(NewSystem.getInstance(), new Runnable() {
                                @Override
                                public void run() {
                                    int seconds = 2;
                                    for(String key : messageEndsSoon) {
                                        Bukkit.broadcastMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Seconds}", String.valueOf(seconds)));
                                    }
                                    Bukkit.getScheduler().scheduleSyncDelayedTask(NewSystem.getInstance(), new Runnable() {
                                        @Override
                                        public void run() {
                                            int seconds = 1;
                                            for(String key : messageEndsSoon) {
                                                Bukkit.broadcastMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Seconds}", String.valueOf(seconds)));
                                            }
                                            Bukkit.getScheduler().scheduleSyncDelayedTask(NewSystem.getInstance(), new Runnable() {
                                                @Override
                                                public void run() {
                                                    int yesVotes = SavingsFile.getIntegerPath("Poll.Vote.Yes");
                                                    int noVotes = SavingsFile.getIntegerPath("Poll.Vote.No");
                                                    String winner = "";

                                                    if(yesVotes == noVotes) {
                                                        for(String key : messageNoWinner) {
                                                            Bukkit.broadcastMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Question}", question));
                                                        }
                                                    }else if(yesVotes > noVotes) {
                                                        winner = yes;
                                                        for(String key : messagePollEnds) {
                                                            Bukkit.broadcastMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Question}", question).replace("{Winner}", winner).replace("{Votes}", yesVotes == 1 ? oneVote : String.valueOf(yesVotes)));
                                                        }
                                                    }else {
                                                        winner = no;
                                                        for(String key : messagePollEnds) {
                                                            Bukkit.broadcastMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Question}", question).replace("{Winner}", winner).replace("{Votes}", noVotes == 1 ? oneVote : String.valueOf(noVotes)));
                                                        }
                                                    }
                                                    SavingsFile.setPath("Poll.Enabled", false);
                                                    SavingsFile.setPath("Poll.Votes", new ArrayList<>());
                                                    SavingsFile.setPath("Poll.Vote.Yes", 0);
                                                    SavingsFile.setPath("Poll.Vote.No", 0);
                                                }
                                            }, 20);
                                        }
                                    }, 20);
                                }
                            }, 20);
                        }
                    }, 20*27);
                }
            }, 20*30);
        }else{
            for(String value : pollActive) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void voteYes(Player p) {
        if(SavingsFile.getBooleanPath("Poll.Enabled")) {
            if(!SavingsFile.getStringListPath("Poll.Votes").contains(p.getUniqueId().toString())) {
                SavingsFile.setPath("Poll.Vote.Yes", SavingsFile.getIntegerPath("Poll.Vote.Yes") + 1);
                List<String> voteList = SavingsFile.getStringListPath("Poll.Votes");
                voteList.add(p.getUniqueId().toString());
                SavingsFile.setPath("Poll.Votes", voteList);
                for(String key : voted) {
                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Vote}", voteYes));
                }
            }else{
                for(String value : alreadyVoted) {
                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }else{
            for(String value : pollNotActive) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }

    public static void voteNo(Player p) {
        if(SavingsFile.getBooleanPath("Poll.Enabled")) {
            if(!SavingsFile.getStringListPath("Poll.Votes").contains(p.getUniqueId().toString())) {
                SavingsFile.setPath("Poll.Vote.No", SavingsFile.getIntegerPath("Poll.Vote.No") + 1);
                List<String> voteList = SavingsFile.getStringListPath("Poll.Votes");
                voteList.add(p.getUniqueId().toString());
                SavingsFile.setPath("Poll.Votes", voteList);
                for(String key : voted) {
                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Vote}", voteNo));
                }
            }else{
                for(String value : alreadyVoted) {
                    p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                }
            }
        }else{
            for(String value : pollNotActive) {
                p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
            }
        }
    }
}
