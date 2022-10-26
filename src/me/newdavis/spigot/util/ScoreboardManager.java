package me.newdavis.spigot.util;

import me.newdavis.spigot.util.placeholder.PlaceholderManager;
import me.newdavis.spigot.file.OtherFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.List;

public class ScoreboardManager {

    public static List<String> scoreboardTitle = OtherFile.getStringListPath("Other.ScoreBoard.Title");
    public static List<String> scoreboardScores = OtherFile.yaml.getStringList("Other.ScoreBoard.Scores");
    public static int speed = OtherFile.getIntegerPath("Other.ScoreBoard.UpdateSpeed");
    private static final HashMap<Player, Integer> ANIMATED_TITLE = new HashMap<>();

    private final Player p;
    public Scoreboard sb;
    private final Objective obj;

    public ScoreboardManager(Player p) {
        this.p = p;
        if(NewSystem.playerScoreboard.containsKey(p)) {
            sb = NewSystem.playerScoreboard.get(p);
            obj = (sb.getObjective(p.getName()) != null ? sb.getObjective(p.getName()) : sb.registerNewObjective(p.getName(), "dummy"));
        }else{
            sb = Bukkit.getScoreboardManager().getNewScoreboard();
            NewSystem.playerScoreboard.put(p, sb);
            obj = sb.registerNewObjective(p.getName(), "dummy");
        }
    }

    public ScoreboardManager setDisplaySlot() {
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        return this;
    }

    public void setScoreBoardScores() {
        int count = 15;
        for (String score : scoreboardScores) {
            Team team = (sb.getTeam("Score" + count) != null ? sb.getTeam("Score" + count) : sb.registerNewTeam("Score" + count));
            String entry = createEntryByInt(count);
            String lastColor = "";
            String score2 = new PlaceholderManager(p).replacePlaceholderInString(new String[]{score}, false)[0];
            if (score2.split("").length > 16) {
                String prefix = "";
                String suffix = "";
                for (int i = 0; i < score2.length(); i++) {
                    if (i > 15) {
                        if (suffix.split("").length <= 12) {
                            suffix += score2.split("")[i];
                        }
                    } else {
                        prefix += score2.split("")[i];
                    }
                }
                if(score.contains("rgb")) {
                    lastColor = String.valueOf(NewSystem.getChatColor(score));
                }else{
                    lastColor = getLastColor(prefix);
                }

                team.setPrefix(NewSystem.replace(prefix));
                team.setSuffix(NewSystem.replace(lastColor + suffix));
            } else {
                team.setPrefix(new PlaceholderManager(p).replacePlaceholderInString(new String[]{score}, true)[0]);
            }

            team.addEntry(entry);
            obj.getScore(entry).setScore(count);
            count--;
        }
    }

    private String createEntryByInt(int color) {
        if (color < 10 && color >= 0) {
            return "§" + color + "§r";
        }
        switch (color) {
            case 15:
                return "§a" + "§r";
            case 14:
                return "§b" + "§r";
            case 13:
                return "§c" + "§r";
            case 12:
                return "§d" + "§r";
            case 11:
                return "§e" + "§r";
            case 10:
                return "§f" + "§r";
        }
        return "§r";
    }

    public void setScoreBoardTitle() {
        int index = 0;
        ANIMATED_TITLE.put(p, index);
        obj.setDisplayName(scoreboardTitle.get(index));
    }

    public void updateScoreBoardTitle() {
        if(p.getScoreboard() != sb) {
            setScoreBoardTitle();
            setScoreBoardScores();
            setScoreBoard();
            return;
        }

        int index = ANIMATED_TITLE.getOrDefault(p, 0);
        obj.setDisplayName(scoreboardTitle.get(index));
        if(index != scoreboardTitle.size()-1) {
            ANIMATED_TITLE.put(p, index + 1);
        }else{
            ANIMATED_TITLE.put(p, 0);
        }
    }

    private String getLastColor(String prefix) {
        String lastColor = "";
        if(prefix.contains(" ")) {
            String[] last = prefix.split(" ");
            for(int i = last.length-1; i >= 0; i--) {
                String[] word = last[i].split("");
                for(int x = word.length-1; x >= 0; x--) {
                    if(word[x].equals("§") && (!(word[x+1].equals("l") || (word[x+1].equals("n")) || (word[x+1].equals("m")) || (word[x+1].equals("r")) || (word[x+1].equals("o")) || (word[x+1].equals("k"))))) {
                        lastColor = word[x] + word[x+1] + (word[x+2].equals("§") ? word[x+2] + (word.length >= 4 ? word[x+3] : "") : "");
                        break;
                    }
                }
                if(!lastColor.equals("")) {
                    break;
                }
            }
        }else{
            String[] last = prefix.split("");
            for(int i = 0; i < last.length; i++) {
                if(last[i].equals("§") && (!(last[i+1].equals("l") || (last[i+1].equals("n")) || (last[i+1].equals("m")) || (last[i+1].equals("r")) || (last[i+1].equals("o")) || (last[i+1].equals("k"))))) {
                    lastColor = last[i] + last[i+1] + (last[i+2].equals("§") ? last[i+2] + last[i+3] : "");
                    break;
                }
            }
        }
        return lastColor;
    }

    public void updateScoreBoard() {
        if(p.getScoreboard() != sb) {
            setScoreBoardTitle();
            setScoreBoardScores();
            setScoreBoard();
            return;
        }

        int count = 15;
        for(String score : scoreboardScores) {
            if(sb.getTeam("Score" + count) != null) {
                Team team = sb.getTeam("Score" + count);
                String prefix = "";
                String suffix = "";
                String lastColor = "";
                String score2 = new PlaceholderManager(p).replacePlaceholderInString(new String[]{score}, false)[0];
                if(score2.split("").length > 16) {
                    for(int i = 0; i < score2.length(); i++) {
                        if(i > 15) {
                            if (suffix.split("").length <= 12) {
                                suffix += score2.split("")[i];
                            }
                        }else{
                            prefix += score2.split("")[i];
                        }
                    }

                    if(score.contains("rgb")) {
                        lastColor = String.valueOf(NewSystem.getChatColor(score));
                    }else{
                        lastColor = getLastColor(prefix);
                    }

                    prefix = NewSystem.replace(prefix);
                    suffix = NewSystem.replace(lastColor + suffix);
                }else{
                    prefix = new PlaceholderManager(p).replacePlaceholderInString(new String[]{score}, true)[0];
                }

                if (!team.getPrefix().equals(prefix) || !team.getSuffix().equals(lastColor + suffix)) {
                    team.setPrefix(prefix);
                    team.setSuffix(lastColor + suffix);
                }
            }
            count--;
        }
    }

    public void setScoreBoard() {
        setScoreBoardScores();
        setScoreBoardTitle();
        p.setScoreboard(sb);
    }

    public static void updateEveryScoreboard() {
        for(Player all : Bukkit.getOnlinePlayers()) {
            new ScoreboardManager(all).updateScoreBoard();
        }
    }

    public static void startTimer() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(NewSystem.getInstance(), new Runnable() {
            @Override
            public void run() {
                for(Player all : Bukkit.getOnlinePlayers()) {
                    new ScoreboardManager(all).updateScoreBoardTitle();
                }
            }
        }, 0, speed);
    }

}
