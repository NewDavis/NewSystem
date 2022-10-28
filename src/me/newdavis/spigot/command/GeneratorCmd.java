package me.newdavis.spigot.command;

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.api.GeneratorAPI;
import me.newdavis.spigot.plugin.NewSystem;
import me.newdavis.spigot.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GeneratorCmd implements CommandExecutor, TabCompleter {

    private static List<String> usage;
    private static String perm;
    private static List<String> created;
    private static List<String> alreadyExist;
    private static List<String> deleted;
    private static List<String> doesNotExist;
    private static List<String> renamed;
    private static List<String> setLocation;
    private static List<String> setDrop;
    private static List<String> materialDoesNotExist;
    private static List<String> setDropAmount;
    private static List<String> setDropSpeed;
    private static List<String> noNumber;
    private static List<String> start;
    private static List<String> alreadyStarted;
    private static List<String> stop;
    private static List<String> notStarted;
    private static String activated;
    private static String deactivated;
    private static List<String> information;
    private static String format;
    private static List<String> list;
    private static List<String> noGeneratorsCreated;

    public void init() {
        usage = CommandFile.getStringListPath("Command.Generator.Usage");
        perm = CommandFile.getStringPath("Command.Generator.Permission");
        created = CommandFile.getStringListPath("Command.Generator.MessageCreated");
        alreadyExist = CommandFile.getStringListPath("Command.Generator.MessageAlreadyExist");
        deleted = CommandFile.getStringListPath("Command.Generator.MessageDeleted");
        doesNotExist = CommandFile.getStringListPath("Command.Generator.MessageDoesNotExist");
        renamed = CommandFile.getStringListPath("Command.Generator.MessageRenamed");
        setLocation = CommandFile.getStringListPath("Command.Generator.MessageSetLocation");
        setDrop = CommandFile.getStringListPath("Command.Generator.MessageSetDrop");
        materialDoesNotExist = CommandFile.getStringListPath("Command.Generator.MessageMaterialDoesNotExist");
        setDropAmount = CommandFile.getStringListPath("Command.Generator.MessageSetDropAmount");
        setDropSpeed = CommandFile.getStringListPath("Command.Generator.MessageSetDropSpeed");
        noNumber = CommandFile.getStringListPath("Command.Generator.MessageNoNumber");
        start = CommandFile.getStringListPath("Command.Generator.MessageStart");
        alreadyStarted = CommandFile.getStringListPath("Command.Generator.MessageAlreadyStarted");
        stop = CommandFile.getStringListPath("Command.Generator.MessageStop");
        notStarted = CommandFile.getStringListPath("Command.Generator.MessageNotStarted");
        activated = CommandFile.getStringPath("Command.Generator.Activated");
        deactivated = CommandFile.getStringPath("Command.Generator.Deactivated");
        information = CommandFile.getStringListPath("Command.Generator.MessageInformation");
        format = CommandFile.getStringPath("Command.Generator.ListFormat");
        list = CommandFile.getStringListPath("Command.Generator.MessageList");
        noGeneratorsCreated = CommandFile.getStringListPath("Command.Generator.NoGeneratorsCreated");
        NewSystem.getInstance().getCommand("generator").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                if(args.length == 1) {
                    if(args[0].equalsIgnoreCase("list")) {
                        String gensString = "";
                        int count = GeneratorAPI.getAllGenerator().size();
                        if(count != 0) {
                            for (String gen : GeneratorAPI.getAllGenerator()) {
                                gensString += format.replace("{Generator}", gen);
                            }
                            for (String key : list) {
                                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Count}", String.valueOf(count)).replace("{GeneratorList}", gensString));
                            }
                        }else{
                            for (String key : noGeneratorsCreated) {
                                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        }
                    } else {
                        for (String key : usage) {
                            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                }else if (args.length == 2) {
                    String generator = args[1];
                    if (!(args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("c"))) {
                        generator = GeneratorAPI.getGeneratorName(args[1]);
                        if (!GeneratorAPI.generatorExist(generator)) {
                            for (String key : doesNotExist) {
                                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                            return true;
                        }
                    }
                    if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("c")) {
                        if (GeneratorAPI.createGenerator(generator, p.getLocation(), new ItemStack(Material.AIR), 1, 20)) {
                            for (String key : created) {
                                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Generator}", generator));
                            }
                        } else {
                            for (String key : alreadyExist) {
                                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("d")) {
                        if (GeneratorAPI.deleteGenerator(generator)) {
                            for (String key : deleted) {
                                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Generator}", generator));
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("information") || args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("i")) {
                        String statusText = (GeneratorAPI.isGeneratorActive(generator) ? activated : deactivated);
                        Location location = GeneratorAPI.getLocation(generator);
                        String locationText = "§7X: §a" + location.getX() + " §7Y: §a" + location.getY() + " §7Z: §a" + location.getZ() + " §7World: §a" + location.getWorld().getName();
                        ItemStack drop = GeneratorAPI.getDrop(generator);
                        String itemText = (drop.hasItemMeta() && drop.getItemMeta().hasDisplayName() ? drop.getItemMeta().getDisplayName() : drop.getType().name());
                        String dropAmountText = String.valueOf(GeneratorAPI.getDropAmount(generator));
                        String dropSpeedText = String.valueOf(GeneratorAPI.getDropSpeed(generator));
                        for (String key : information) {
                            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix())
                                    .replace("{Generator}", generator)
                                    .replace("{Status}", statusText)
                                    .replace("{Location}", locationText)
                                    .replace("{Item}", itemText)
                                    .replace("{DropAmount}", dropAmountText)
                                    .replace("{DropSpeed}", dropSpeedText));
                        }
                    } else if (args[0].equalsIgnoreCase("setLocation") || args[0].equalsIgnoreCase("sl")) {
                        if (GeneratorAPI.setLocation(generator, p.getLocation())) {
                            for (String key : setLocation) {
                                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Generator}", generator));
                            }
                        }
                    } else if(args[0].equalsIgnoreCase("setDrop") || args[0].equalsIgnoreCase("sd")) {
                        ItemStack item = ItemBuilder.getItemInHand(p);
                        if (ItemBuilder.getMaterialOfItemStack(item) != Material.AIR) {
                            GeneratorAPI.setDrop(generator, item);
                            String itemName = (item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : ItemBuilder.getNameOfMaterial(ItemBuilder.getMaterialOfItemStack(item)));
                            for (String key : setDrop) {
                                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Generator}", generator).replace("{Item}", itemName));
                            }
                        } else {
                            for (String key : materialDoesNotExist) {
                                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("start")) {
                        if (GeneratorAPI.generatorExist(generator)) {
                            if (!GeneratorAPI.isGeneratorActive(generator)) {
                                GeneratorAPI.start(generator);
                                for (String key : start) {
                                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Generator}", generator));
                                }
                            } else {
                                for (String key : alreadyStarted) {
                                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Generator}", generator));
                                }
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("stop")) {
                        if (GeneratorAPI.generatorExist(generator)) {
                            if (GeneratorAPI.isGeneratorActive(generator)) {
                                GeneratorAPI.stop(generator);
                                for (String key : stop) {
                                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Generator}", generator));
                                }
                            } else {
                                for (String key : notStarted) {
                                    p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Generator}", generator));
                                }
                            }
                        }
                    } else {
                        for (String key : usage) {
                            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                }else if(args.length == 3) {
                    String generator = GeneratorAPI.getGeneratorName(args[1]);
                    if (!GeneratorAPI.generatorExist(generator)) {
                        for (String key : doesNotExist) {
                            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                        return true;
                    }

                    if(args[0].equalsIgnoreCase("rename")) {
                        String newName = args[2];
                        GeneratorAPI.changeName(generator, newName);
                        for (String key : renamed) {
                            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{OldGenerator}", generator).replace("{NewGenerator}", newName));
                        }
                    }else if(args[0].equalsIgnoreCase("setDrop") || args[0].equalsIgnoreCase("sd")) {
                        Material material = Material.AIR;
                        for(String mat : ItemBuilder.serverMaterials.keySet()) {
                            if(ItemBuilder.getNameOfMaterial(ItemBuilder.getMaterial(mat)).equalsIgnoreCase(args[2])) {
                                material = ItemBuilder.getMaterial(mat);
                            }
                        }

                        if(material != Material.AIR) {
                            GeneratorAPI.setDrop(generator, new ItemStack(material));
                            for (String key : setDrop) {
                                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Generator}", generator).replace("{Item}", material.name()));
                            }
                        }else{
                            for (String key : materialDoesNotExist) {
                                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                        }
                    }else if(args[0].equalsIgnoreCase("setDropAmount") || args[0].equalsIgnoreCase("sda")) {
                        int amount;
                        try {
                            amount = Integer.parseInt(args[2]);
                        }catch (NumberFormatException ignored) {
                            for (String key : noNumber) {
                                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                            return true;
                        }
                        GeneratorAPI.setDropAmount(generator, amount);
                        for (String key : setDropAmount) {
                            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Generator}", generator).replace("{DropAmount}", String.valueOf(amount)));
                        }
                    }else if(args[0].equalsIgnoreCase("setDropSpeed") || args[0].equalsIgnoreCase("sds")) {
                        long speed;
                        try {
                            speed = Long.parseLong(args[2]);
                        }catch (NumberFormatException ignored) {
                            for (String key : noNumber) {
                                p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                            }
                            return true;
                        }
                        GeneratorAPI.setDropSpeed(generator, speed);
                        for (String key : setDropSpeed) {
                            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Generator}", generator).replace("{DropSpeed}", String.valueOf(speed)));
                        }
                    } else {
                        for (String key : usage) {
                            p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                } else {
                    for (String key : usage) {
                        p.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }else{
                p.sendMessage(SettingsFile.getNoPerm());
            }
        }else{
            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("list")) {
                    String gensString = "";
                    int count = GeneratorAPI.getAllGenerator().size();
                    if(count != 0) {
                        for (String gen : GeneratorAPI.getAllGenerator()) {
                            gensString += format.replace("{Generator}", gen);
                        }
                        for (String key : list) {
                            sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Count}", String.valueOf(count)).replace("{GeneratorList}", gensString));
                        }
                    }else{
                        for (String key : noGeneratorsCreated) {
                            sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                } else {
                    for (String key : usage) {
                        sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }else if (args.length == 2) {
                String generator = args[1];
                if (!(args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("c"))) {
                    generator = GeneratorAPI.getGeneratorName(args[1]);
                    if (!GeneratorAPI.generatorExist(generator)) {
                        for (String key : doesNotExist) {
                            sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("c")) {
                    if (GeneratorAPI.createGenerator(generator, new Location(Bukkit.getWorlds().get(0), 0D, 0D, 0D), new ItemStack(Material.AIR), 1, 20)) {
                        for (String key : created) {
                            sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Generator}", generator));
                        }
                    } else {
                        for (String key : alreadyExist) {
                            sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("d")) {
                    if (GeneratorAPI.deleteGenerator(generator)) {
                        for (String key : deleted) {
                            sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Generator}", generator));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("information") || args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("i")) {
                    String statusText = (GeneratorAPI.isGeneratorActive(generator) ? activated : deactivated);
                    Location location = GeneratorAPI.getLocation(generator);
                    String locationText = "§7X: §a" + location.getX() + " §7Y: §a" + location.getY() + " §7Z: §a" + location.getZ() + " §7World: §a" + location.getWorld().getName();
                    ItemStack drop = GeneratorAPI.getDrop(generator);
                    String itemText = (drop.hasItemMeta() && drop.getItemMeta().hasDisplayName() ? drop.getItemMeta().getDisplayName() : drop.getType().name());
                    String dropAmountText = String.valueOf(GeneratorAPI.getDropAmount(generator));
                    String dropSpeedText = String.valueOf(GeneratorAPI.getDropSpeed(generator));
                    for (String key : information) {
                        sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix())
                                .replace("{Generator}", generator)
                                .replace("{Status}", statusText)
                                .replace("{Location}", locationText)
                                .replace("{Item}", itemText)
                                .replace("{DropAmount}", dropAmountText)
                                .replace("{DropSpeed}", dropSpeedText));
                    }
                } else if (args[0].equalsIgnoreCase("setLocation") || args[0].equalsIgnoreCase("sl")) {
                    sender.sendMessage(SettingsFile.getOnlyPlayerCanExecute());
                } else if(args[0].equalsIgnoreCase("setDrop") || args[0].equalsIgnoreCase("sd")) {
                    sender.sendMessage(SettingsFile.getOnlyPlayerCanExecute());
                } else if (args[0].equalsIgnoreCase("start")) {
                    if (GeneratorAPI.generatorExist(generator)) {
                        if (!GeneratorAPI.isGeneratorActive(generator)) {
                            GeneratorAPI.start(generator);
                            for (String key : start) {
                                sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Generator}", generator));
                            }
                        } else {
                            for (String key : alreadyStarted) {
                                sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Generator}", generator));
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("stop")) {
                    if (GeneratorAPI.generatorExist(generator)) {
                        if (GeneratorAPI.isGeneratorActive(generator)) {
                            GeneratorAPI.stop(generator);
                            for (String key : stop) {
                                sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Generator}", generator));
                            }
                        } else {
                            for (String key : notStarted) {
                                sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Generator}", generator));
                            }
                        }
                    }
                } else {
                    for (String key : usage) {
                        sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            }else if(args.length == 3) {
                String generator = GeneratorAPI.getGeneratorName(args[1]);
                if (!GeneratorAPI.generatorExist(generator)) {
                    for (String key : doesNotExist) {
                        sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                    return true;
                }

                if(args[0].equalsIgnoreCase("rename")) {
                    String newName = args[2];
                    GeneratorAPI.changeName(generator, newName);
                    for (String key : renamed) {
                        sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{OldGenerator}", generator).replace("{NewGenerator}", newName));
                    }
                }else if(args[0].equalsIgnoreCase("setDrop") || args[0].equalsIgnoreCase("sd")) {
                    Material material = Material.AIR;
                    Bukkit.broadcastMessage(ItemBuilder.getNameOfMaterial(ItemBuilder.getMaterial(args[2])));
                    for(String mat : ItemBuilder.serverMaterials.keySet()) {
                        Bukkit.broadcastMessage(mat);
                        if(ItemBuilder.getNameOfMaterial(ItemBuilder.getMaterial(mat)).equalsIgnoreCase(args[2])) {
                            material = ItemBuilder.getMaterial(mat);
                        }
                    }
                    if(material != Material.AIR) {
                        GeneratorAPI.setDrop(generator, new ItemStack(material));
                        for (String key : setDrop) {
                            sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Generator}", generator).replace("{Item}", material.name()));
                        }
                    }else{
                        for (String key : materialDoesNotExist) {
                            sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                    }
                }else if(args[0].equalsIgnoreCase("setDropAmount") || args[0].equalsIgnoreCase("sda")) {
                    int amount;
                    try {
                        amount = Integer.parseInt(args[2]);
                    }catch (NumberFormatException ignored) {
                        for (String key : noNumber) {
                            sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                        return true;
                    }
                    GeneratorAPI.setDropAmount(generator, amount);
                    for (String key : setDropAmount) {
                        sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Generator}", generator).replace("{DropAmount}", String.valueOf(amount)));
                    }
                }else if(args[0].equalsIgnoreCase("setDropSpeed") || args[0].equalsIgnoreCase("sds")) {
                    long speed;
                    try {
                        speed = Long.parseLong(args[2]);
                    }catch (NumberFormatException ignored) {
                        for (String key : noNumber) {
                            sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                        }
                        return true;
                    }
                    GeneratorAPI.setDropSpeed(generator, speed);
                    for (String key : setDropSpeed) {
                        sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Generator}", generator).replace("{DropSpeed}", String.valueOf(speed)));
                    }
                } else {
                    for (String key : usage) {
                        sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
                    }
                }
            } else {
                for (String key : usage) {
                    sender.sendMessage(key.replace("{Prefix}", SettingsFile.getPrefix()));
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
            if (NewSystem.hasPermission(p, perm)) {
                if (args.length == 1) {
                    String[] completions = {"create", "delete", "rename", "list", "info", "setLocation", "setDrop", "setDropAmount", "setDropSpeed", "start", "stop"};
                    for(String completion : completions) {
                        if(completion.contains(args[0])) {
                            tabCompletions.add(completion);
                        }
                    }
                }else if(args.length == 2) {
                    for(String generator : GeneratorAPI.getAllGenerator()) {
                        if(generator.contains(args[1])) {
                            tabCompletions.add(generator);
                        }
                    }
                }
            }
        }else{
            if (args.length == 1) {
                String[] completions = {"create", "delete", "rename", "list", "info", "setLocation", "setDrop", "setDropAmount", "setDropSpeed", "start", "stop"};
                for(String completion : completions) {
                    if(completion.contains(args[0])) {
                        tabCompletions.add(completion);
                    }
                }
            }else if(args.length == 2) {
                for(String generator : GeneratorAPI.getAllGenerator()) {
                    if(generator.contains(args[1])) {
                        tabCompletions.add(generator);
                    }
                }
            }
        }
        return tabCompletions;
    }
}
