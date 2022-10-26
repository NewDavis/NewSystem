package me.newdavis.spigot.api;

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.OtherFile;
import me.newdavis.spigot.file.SavingsFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import me.newdavis.spigot.sql.MySQL;
import me.newdavis.spigot.sql.SQLTables;
import me.newdavis.spigot.util.ScoreboardManager;
import me.newdavis.spigot.util.placeholder.PlaceholderManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;

public class CurrencyAPI {

    private static MySQL mySQL = NewSystem.getMySQL();
    private static boolean mySQLEnabled  = SettingsFile.getMySQLEnabled();

    public static String getCurrencyPrefix() {
        if(CommandFile.getBooleanPath("Command.Currency.Enabled.Currency")) {
            return CommandFile.yaml.getString("Command.Currency.Prefix");
        }
        return "";
    }

    public static double getDefaultAmount() {
        if(CommandFile.getBooleanPath("Command.Currency.Enabled.Currency")) {
            return CommandFile.getDoublePath("Command.Currency.DefaultAmount");
        }
        return 0D;
    }

    public static double getCurrencyOfPlayer(OfflinePlayer p) {
        if (CommandFile.getBooleanPath("Command.Currency.Enabled.Currency")) {
            double currency = getDefaultAmount();
            if (mySQLEnabled) {
                if (mySQL.hasNext("SELECT AMOUNT FROM " + SQLTables.CURRENCY.getTableName() + " WHERE UUID='" + p.getUniqueId().toString() + "'")) {
                    return mySQL.getDouble("SELECT AMOUNT FROM " + SQLTables.CURRENCY.getTableName() + " WHERE UUID='" + p.getUniqueId().toString() + "'");
                } else {
                    mySQL.executeUpdate("INSERT INTO " + SQLTables.CURRENCY.getTableName() + " (UUID,AMOUNT) VALUES ('" + p.getUniqueId().toString() + "','" + currency + "')");
                }
            } else {
                if (SavingsFile.isPathSet("Currency." + p.getUniqueId())) {
                    currency = SavingsFile.getDoublePath("Currency." + p.getUniqueId());
                } else {
                    SavingsFile.setPath("Currency." + p.getUniqueId(), currency);
                }
            }

            DecimalFormat df = new DecimalFormat("##.##");
            String decimal = df.format(currency);
            return Double.parseDouble(decimal);
        }
        return 0D;
    }

    public static String getCurrencyOfPlayerString(OfflinePlayer p) {
        double amount = getCurrencyOfPlayer(p);
        DecimalFormat df = new DecimalFormat("##.##");
        return df.format(amount);
    }

    public static String getCurrencyString(double amount) {
        DecimalFormat df = new DecimalFormat("##.##");
        return df.format(amount);
    }

    public static void addCurrencyToPlayer(OfflinePlayer p, double currency) {
        if(CommandFile.getBooleanPath("Command.Currency.Enabled.Currency")) {
            if (!(currency < 0)) {
                double current = getCurrencyOfPlayer(p);
                double after = current + currency;
                if (mySQLEnabled) {
                    mySQL.executeUpdate("UPDATE " + SQLTables.CURRENCY.getTableName() + " SET AMOUNT='" + after + "' WHERE UUID='" + p.getUniqueId().toString() + "';");
                } else {
                    SavingsFile.setPath("Currency." + p.getUniqueId(), after);
                }

                //Update Placeholder
                if(p.isOnline()) {
                    PlaceholderManager phManager = new PlaceholderManager(p);
                    phManager.updatePlaceholder("{Currency}");
                }

                if (OtherFile.getBooleanPath("Other.ScoreBoard.Enabled")) {
                    if (p.isOnline()) {
                        new ScoreboardManager(p.getPlayer()).updateScoreBoard();
                    }
                }
            }
        }
    }

    public static boolean removeCurrencyOfPlayer(OfflinePlayer p, double currency) {
        if(CommandFile.getBooleanPath("Command.Currency.Enabled.Currency")) {
            if (!(currency < 0)) {
                double current = getCurrencyOfPlayer(p);
                if ((current - currency) >= 0) {
                    double after = current - currency;
                    if (mySQLEnabled) {
                        mySQL.executeUpdate("UPDATE " + SQLTables.CURRENCY.getTableName() + " SET AMOUNT='" + after + "' WHERE UUID='" + p.getUniqueId().toString() + "';");
                    } else {
                        SavingsFile.setPath("Currency." + p.getUniqueId(), after);
                    }

                    //Update Placeholder
                    if(p.isOnline()) {
                        PlaceholderManager phManager = new PlaceholderManager(p);
                        phManager.updatePlaceholder("{Currency}");
                    }

                    if (OtherFile.getBooleanPath("Other.ScoreBoard.Enabled")) {
                        if (p.isOnline()) {
                            new ScoreboardManager(p.getPlayer()).updateScoreBoard();
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean payCurrencyToPlayer(Player p, Player t, double amount) {
        if(CommandFile.getBooleanPath("Command.Currency.Enabled.Currency") && CommandFile.getBooleanPath("Command.Currency.Enabled.Pay")) {
            double currencyOfPlayer = getCurrencyOfPlayer(p);
            if (!(amount < 0)) {
                if (currencyOfPlayer >= amount) {
                    if (removeCurrencyOfPlayer(p, amount)) {
                        addCurrencyToPlayer(t, amount);

                        //Update Placeholder
                        PlaceholderManager phManager = new PlaceholderManager(t);
                        phManager.updatePlaceholder("{Currency}");
                        phManager = new PlaceholderManager(p);
                        phManager.updatePlaceholder("{Currency}");

                        if (OtherFile.getBooleanPath("Other.ScoreBoard.Enabled")) {
                            new ScoreboardManager(p).updateScoreBoard();
                            new ScoreboardManager(t).updateScoreBoard();
                        }

                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean payCurrencyToEveryone(Player p, double amount) {
        if(CommandFile.getBooleanPath("Command.Currency.Enabled.Currency")) {
            int onlinePlayers = Bukkit.getOnlinePlayers().size() - 1;
            double currencyOfPlayer = getCurrencyOfPlayer(p);
            if (!(amount < 0)) {
                if (currencyOfPlayer >= (amount * onlinePlayers)) {
                    if (removeCurrencyOfPlayer(p, (amount * onlinePlayers))) {
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            if (all != p) {
                                addCurrencyToPlayer(all, amount);
                            }
                        }

                        //Update Placeholder
                        PlaceholderManager phManager = new PlaceholderManager(p);
                        phManager.updatePlaceholder("{Currency}");

                        if (OtherFile.getBooleanPath("Other.ScoreBoard.Enabled")) {
                            ScoreboardManager.updateEveryScoreboard();
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void setCurrencyOfPlayer(OfflinePlayer p, double currency) {
        if(CommandFile.getBooleanPath("Command.Currency.Enabled.Currency")) {
            if (!(currency < 0)) {
                if (mySQLEnabled) {
                    mySQL.executeUpdate("UPDATE " + SQLTables.CURRENCY.getTableName() + " SET AMOUNT='" + currency + "' WHERE UUID='" + p.getUniqueId().toString() + "';");
                } else {
                    SavingsFile.setPath("Currency." + p.getUniqueId(), currency);
                }

                //Update Placeholder
                if(p.isOnline()) {
                    PlaceholderManager phManager = new PlaceholderManager(p);
                    phManager.updatePlaceholder("{Currency}");
                }

                if (OtherFile.getBooleanPath("Other.ScoreBoard.Enabled")) {
                    if (p.isOnline()) {
                        new ScoreboardManager(p.getPlayer()).updateScoreBoard();
                    }
                }
            }
        }
    }

    public static void resetCurrencyOfPlayer(OfflinePlayer p) {
        if(CommandFile.getBooleanPath("Command.Currency.Enabled.Currency")) {
            setCurrencyOfPlayer(p, getDefaultAmount());

            //Update Placeholder
            if(p.isOnline()) {
                PlaceholderManager phManager = new PlaceholderManager(p);
                phManager.updatePlaceholder("{Currency}");
            }

            if (OtherFile.getBooleanPath("Other.ScoreBoard.Enabled")) {
                if (p.isOnline()) {
                    new ScoreboardManager(p.getPlayer()).updateScoreBoard();
                }
            }
        }
    }

    public static void multipleCurrencyOfPlayer(OfflinePlayer p, double multiplier) {
        if(CommandFile.getBooleanPath("Command.Currency.Enabled.Currency")) {
            if (!(multiplier < 0)) {
                double current = getCurrencyOfPlayer(p);
                double after = current * multiplier;
                if (mySQLEnabled) {
                    mySQL.executeUpdate("UPDATE " + SQLTables.CURRENCY.getTableName() + " SET AMOUNT='" + after + "' WHERE UUID='" + p.getUniqueId().toString() + "';");
                } else {
                    SavingsFile.setPath("Currency." + p.getUniqueId(), after);
                }

                //Update Placeholder
                if(p.isOnline()) {
                    PlaceholderManager phManager = new PlaceholderManager(p);
                    phManager.updatePlaceholder("{Currency}");
                }

                if (OtherFile.getBooleanPath("Other.ScoreBoard.Enabled")) {
                    if (p.isOnline()) {
                        new ScoreboardManager(p.getPlayer()).updateScoreBoard();
                    }
                }
            }
        }
    }

    public static void divideCurrencyOfPlayer(OfflinePlayer p, double dividend) {
        if(CommandFile.getBooleanPath("Command.Currency.Enabled.Currency")) {
            if (!(dividend < 0)) {
                double current = getCurrencyOfPlayer(p);
                double after = current / dividend;
                if (mySQLEnabled) {
                    mySQL.executeUpdate("UPDATE " + SQLTables.CURRENCY.getTableName() + " SET AMOUNT='" + after + "' WHERE UUID='" + p.getUniqueId().toString() + "';");
                } else {
                    SavingsFile.setPath("Currency." + p.getUniqueId(), after);
                }

                //Update Placeholder
                if(p.isOnline()) {
                    PlaceholderManager phManager = new PlaceholderManager(p);
                    phManager.updatePlaceholder("{Currency}");
                }

                if (OtherFile.getBooleanPath("Other.ScoreBoard.Enabled")) {
                    if (p.isOnline()) {
                        new ScoreboardManager(p.getPlayer()).updateScoreBoard();
                    }
                }
            }
        }
    }

    private static long nextUpdate = 0;
    private static final List<OfflinePlayer> topList = new ArrayList<>();
    private static final HashMap<OfflinePlayer, Double> currencyOfOfflinePlayers = new HashMap<>();

    public static List<OfflinePlayer> getTopCurrency() {
        long now = System.currentTimeMillis();
        if(nextUpdate - now <= 0) {
            currencyOfOfflinePlayers.clear();
            topList.clear();
            HashMap<OfflinePlayer, Double> currencyOfPlayers = new HashMap<>();
            Collection<String> keys;
            if(mySQLEnabled) {
                keys = mySQL.getStringList("UUID", SQLTables.CURRENCY.getTableName());
            }else{
                keys = SavingsFile.yaml.getConfigurationSection("Currency").getKeys(false);
            }

            for (String key : keys) {
                OfflinePlayer t = Bukkit.getOfflinePlayer(UUID.fromString(key));
                if(t != null) {
                    Double currency = getCurrencyOfPlayer(t);
                    currencyOfOfflinePlayers.put(t, currency);
                    currencyOfPlayers.put(t, currency);
                }
            }

            int topListSize = CommandFile.getIntegerPath("Command.Currency.TopListSize");
            for (int i = 0; i < topListSize; i++) {
                OfflinePlayer highestPlayer = null;
                double highestAmount = 0;
                for (OfflinePlayer t : currencyOfPlayers.keySet()) {
                    if ((currencyOfPlayers.containsKey(t) ? currencyOfPlayers.get(t) : 0) > highestAmount) {
                        highestAmount = currencyOfPlayers.get(t);
                        highestPlayer = t;
                    }
                }
                topList.add(highestPlayer);
                currencyOfPlayers.remove(highestPlayer);
            }
            nextUpdate = now + 1000*5;
        }
        return topList;
    }

    public static String getAllMoneyCount() {
        double amount = 0;

        for(OfflinePlayer t : topList) {
            if(t != null) {
                amount += currencyOfOfflinePlayers.get(t);
            }
        }

        DecimalFormat df = new DecimalFormat("##.##");
        return df.format(amount);
    }

}
