package me.newdavis.spigot.command;

import me.newdavis.manager.NewPermManager;
import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.util.ItemBuilder;
import me.newdavis.spigot.util.placeholder.PlaceholderManager;
import me.newdavis.spigot.api.CurrencyAPI;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class VoucherCmd implements CommandExecutor, TabCompleter {

    private static List<String> usage;
    private static String perm;
    private static List<String> message;
    private static List<String> messagePlayer;
    private static List<String> messageVoucherNotExist;
    private static String listFormat;
    private static String noVouchers;
    private static List<String> listMessage;
    private static List<String> redeemedRole;
    private static List<String> redeemedPermission;
    private static List<String> redeemedCurrency;

    public void init() {
        usage = CommandFile.getStringListPath("Command.Voucher.Usage");
        perm = CommandFile.getStringPath("Command.Voucher.Permission");
        message = CommandFile.getStringListPath("Command.Voucher.Message");
        messagePlayer = CommandFile.getStringListPath("Command.Voucher.MessagePlayer");
        messageVoucherNotExist = CommandFile.getStringListPath("Command.Voucher.MessageVoucherNotExist");
        listFormat = CommandFile.getStringPath("Command.Voucher.ListFormat");
        noVouchers = CommandFile.getStringPath("Command.Voucher.MessageNoVouchers");
        listMessage = CommandFile.getStringListPath("Command.Voucher.MessageList");
        redeemedRole = CommandFile.getStringListPath("Command.Voucher.MessageRedeemedRole");
        redeemedPermission = CommandFile.getStringListPath("Command.Voucher.MessageRedeemedPermission");
        redeemedCurrency = CommandFile.getStringListPath("Command.Voucher.MessageRedeemedCurrency");
        NewSystem.getInstance().getCommand("voucher").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("list")) {
                        sendList(p);
                    } else {
                        for(String value : usage) {
                            p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                } else if (args.length == 2) {
                    String voucher = args[0];
                    if (voucherExist(voucher)) {
                        Player t = Bukkit.getPlayer(args[1]);
                        if (t != null) {
                            ItemStack voucherItem = getVoucher(voucher, 1);
                            t.getInventory().addItem(voucherItem);

                            for (String msg : message) {
                                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t))
                                        .replace("{Voucher}", voucher).replace("{Amount}", String.valueOf(1)));
                            }

                            for (String msg : messagePlayer) {
                                t.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Voucher}", voucher)
                                        .replace("{Amount}", String.valueOf(1)));
                            }
                        } else {
                            p.sendMessage(SettingsFile.getOffline());
                        }
                    }else{
                        for (String msg : messageVoucherNotExist) {
                            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Voucher}", voucher));
                        }
                    }
                } else if (args.length == 3) {
                    String voucher = args[0];
                    if (voucherExist(voucher)) {
                        int amount = 1;
                        try{
                            amount = Integer.parseInt(args[1]);
                        }catch (NumberFormatException ignored) {
                            p.sendMessage(SettingsFile.getError().replace("{Error}", "Please use a number for amount"));
                        }
                        Player t = Bukkit.getPlayer(args[2]);
                        if (t != null) {
                            ItemStack voucherItem = getVoucher(voucher, amount);
                            t.getInventory().addItem(voucherItem);

                            for (String msg : message) {
                                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(t))
                                        .replace("{Voucher}", voucher).replace("{Amount}", String.valueOf(amount)));
                            }

                            for (String msg : messagePlayer) {
                                t.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Voucher}", voucher)
                                        .replace("{Amount}", String.valueOf(amount)));
                            }
                        } else {
                            p.sendMessage(SettingsFile.getOffline());
                        }
                    } else {
                        for (String msg : messageVoucherNotExist) {
                            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Voucher}", voucher));
                        }
                    }
                }else{
                    for(String value : usage) {
                        p.sendMessage(value.replace("{Prefix}", SettingsFile.getPrefix()));
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

    private void sendList(Player p) {
        List<String> vouchers = getVouchers();
        String vouchersString = listFormat;

        for(String msg : listMessage) {
            if(msg.contains("{Vouchers}")) {
                if (!vouchers.isEmpty()) {
                    for (int i = 0; i < vouchers.size(); i++) {
                        if(i == (vouchers.size()-1)) {
                            vouchersString = vouchersString.replace("{Voucher}", vouchers.get(i));
                        }else {
                            vouchersString = vouchersString.replace("{Voucher}", vouchers.get(i)) + listFormat;
                        }
                    }
                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Vouchers}", vouchersString));
                }else{
                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Vouchers}", noVouchers));
                }
            }else{
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Count}", String.valueOf(vouchers.size())));
            }
        }
    }

    private List<String> getVouchers() {
        List<String> vouchers = new ArrayList<>();
        for(String voucher : CommandFile.getConfigurationSection("Command.Voucher.Vouchers")) {
            if(!vouchers.contains(voucher)) {
                vouchers.add(voucher);
            }
        }
        return vouchers;
    }

    private boolean voucherExist(String voucher) {
        return getVouchers().contains(voucher);
    }

    private boolean voucherForRole(String voucher) {
        return CommandFile.isPathSet("Command.Voucher.Vouchers." + voucher + ".Role");
    }

    private String voucherRole(String voucher) {
        return CommandFile.getStringPath("Command.Voucher.Vouchers." + voucher + ".Role");
    }

    private boolean voucherForPermission(String voucher) {
        return CommandFile.isPathSet("Command.Voucher.Vouchers." + voucher + ".Permission");
    }

    private String voucherPermission(String voucher) {
        return CommandFile.getStringPath("Command.Voucher.Vouchers." + voucher + ".Permission");
    }

    private boolean voucherForCurrency(String voucher) {
        return CommandFile.isPathSet("Command.Voucher.Vouchers." + voucher + ".Currency");
    }

    private Double voucherCurrency(String voucher) {
        return CommandFile.getDoublePath("Command.Voucher.Vouchers." + voucher + ".Currency");
    }

    public static boolean voucherHasType(String voucher) {
        return CommandFile.isPathSet("Command.Voucher.Vouchers." + voucher + ".Type");
    }

    public static Material voucherType(String voucher) {
        String type = CommandFile.getStringPath("Command.Voucher.Vouchers." + voucher + ".Type");
        Material material;
        try{
            material = ItemBuilder.getMaterial(Integer.parseInt(type));
        }catch(NumberFormatException ignored) {
            material = ItemBuilder.getMaterial(type);
        }
        return material;
    }

    public static Material defaultVoucherType() {
        String type = CommandFile.getStringPath("Command.Voucher.DefaultVoucherType");
        Material material;
        try{
            material = ItemBuilder.getMaterial(Integer.parseInt(type));
        }catch(NumberFormatException ignored) {
            material = ItemBuilder.getMaterial(type);
        }
        return material;
    }

    public static boolean voucherHasDisplayName(String voucher) {
        return CommandFile.isPathSet("Command.Voucher.Vouchers." + voucher + ".DisplayName");
    }

    public static String voucherDisplayName(String voucher) {
        return CommandFile.getStringPath("Command.Voucher.Vouchers." + voucher + ".DisplayName");
    }

    public static boolean voucherHasLore(String voucher) {
        return CommandFile.isPathSet("Command.Voucher.Vouchers." + voucher + ".Lore");
    }

    public static List<String> voucherLore(String voucher) {
        List<String> lore = CommandFile.getStringListPath("Command.Voucher.Vouchers." + voucher + ".Lore");
        for(int i = 0; i < lore.size(); i++) {
            lore.set(i, lore.get(i).replace("{Prefix}", SettingsFile.getPrefix()));
        }
        return lore;
    }

    private String getVoucherFromLore(ItemStack item) {
        if(item.hasItemMeta() && item.getItemMeta().hasLore()) {
            List<String> lore = item.getItemMeta().getLore();
            String voucher = lore.get(lore.size()-1).replace("§8Voucher: ", "");
            return voucher;
        }
        return "";
    }
    
    private ItemStack getVoucher(String voucher, int amount) {
        ItemStack voucherItem = new ItemStack(defaultVoucherType());
        voucherItem.setAmount(amount);
        ItemMeta voucherItemMeta = voucherItem.getItemMeta();

        Material material = (voucherHasType(voucher) ? voucherType(voucher) : defaultVoucherType());
        String displayName = (voucherHasDisplayName(voucher) ? voucherDisplayName(voucher) : material.name());
        List<String> lore = (voucherHasLore(voucher) ? voucherLore(voucher) : new ArrayList<>());
        lore.add("§8Voucher: " + voucher);

        if(material != null) {
            voucherItem.setType(material);
        }
        voucherItemMeta.setDisplayName(displayName);
        voucherItemMeta.setLore(lore);
        voucherItem.setItemMeta(voucherItemMeta);

        return voucherItem;
    }

    public static void redeemVoucher(Player p) {
        VoucherCmd voucherCmd = new VoucherCmd();
        ItemStack itemInHand = ItemBuilder.getItemInHand(p);
        String voucher = voucherCmd.getVoucherFromLore(itemInHand);

        if(!voucher.equalsIgnoreCase("")) {
            if(voucherCmd.voucherExist(voucher)) {
                if (voucherCmd.voucherForRole(voucher)) {
                    if (NewSystem.newPerm) {
                        String role = voucherCmd.voucherRole(voucher);
                        String roleSuffix = new PlaceholderManager(p).getPlaceholder("{RoleSuffix}").getValue();
                        NewPermManager.setPlayerRole(p, role);
                        for (String msg : redeemedRole) {
                            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(p))
                                    .replace("{Role}", role).replace("{Role-Suffix}", roleSuffix));
                        }
                    } else {
                        p.sendMessage(SettingsFile.getError().replace("{Error}", "NewPerm is required for Role"));
                    }
                }

                if (voucherCmd.voucherForPermission(voucher)) {
                    if (NewSystem.newPerm) {
                        String permission = voucherCmd.voucherPermission(voucher);
                        NewPermManager.addPlayerPermission(p, permission);
                        for (String msg : redeemedPermission) {
                            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(p))
                                    .replace("{Permission}", permission));
                        }
                    } else {
                        p.sendMessage(SettingsFile.getError().replace("{Error}", "NewPerm is required for Permission"));
                    }
                }

                if (voucherCmd.voucherForCurrency(voucher)) {
                    if (CommandFile.getBooleanPath("Command.Currency.Enabled.Currency")) {
                        double amount = voucherCmd.voucherCurrency(voucher);
                        CurrencyAPI.addCurrencyToPlayer(p, amount);
                        for (String msg : redeemedCurrency) {
                            p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Player}", NewSystem.getName(p))
                                    .replace("{Currency}", CurrencyAPI.getCurrencyString(amount)).replace("{CurrencyPrefix}", CurrencyAPI.getCurrencyPrefix()));
                        }
                    } else {
                        p.sendMessage(SettingsFile.getError().replace("{Error}", "Currency is required"));
                    }
                }

                if (itemInHand.getAmount() > 1) {
                    itemInHand.setAmount(itemInHand.getAmount() - 1);
                } else {
                    p.getInventory().setItemInHand(new ItemStack(Material.AIR));
                }
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        List<String> tabCompletions = new ArrayList<>();

        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if (args.length == 1) {
                    String[] completions = {"list"};
                    for(String completion : completions) {
                        if(completion.contains(args[0])) {
                            tabCompletions.add(completion);
                        }
                    }

                    for(String voucher : getVouchers()) {
                        if(voucher.contains(args[0])) {
                            tabCompletions.add(voucher);
                        }
                    }
                } else if (args.length == 2) {
                    if(!args[0].equalsIgnoreCase("list")) {
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            if (all.getName().contains(args[1])) {
                                tabCompletions.add(all.getName());
                            }
                        }
                    }
                }else if (args.length == 3) {
                    if(!args[0].equalsIgnoreCase("list")) {
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            if (all.getName().contains(args[2])) {
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
