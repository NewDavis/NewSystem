package me.newdavis.spigot.command;

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.*;

import java.util.*;

public class SpawnMobCmd implements CommandExecutor, TabCompleter {

    private static List<String> usage;
    private static String perm;
    private static String permMob;
    private static List<String> message;

    public SpawnMobCmd() {
        usage = CommandFile.getStringListPath("Command.SpawnMob.Usage");
        perm = CommandFile.getStringPath("Command.SpawnMob.Permission.Use");
        permMob = CommandFile.getStringPath("Command.SpawnMob.Permission.Type");
        message = CommandFile.getStringListPath("Command.SpawnMob.Message");
        if(!NewSystem.loadedCommands.contains(this)) {
            NewSystem.loadedCommands.add(this);
            NewSystem.getInstance().getCommand("spawnmob").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(NewSystem.hasPermission(p, perm)) {
                String type = "";
                int amount = 1;
                if(args.length == 1) {
                    type = args[0];
                    spawnMob(p, type, amount);
                }else if(args.length == 2) {
                    type = args[0];
                    try {
                        amount = Integer.parseInt(args[1]);
                    }catch (NumberFormatException ignored) {
                        p.sendMessage(SettingsFile.getError().replace("{Error}", "Please use a number for amount"));
                        return true;
                    }
                    spawnMob(p, type, amount);
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

    private static List<EntityType> mobEntityTypes = new ArrayList<>();

    public static void setMobEntityTypes() {
        World world = Bukkit.getWorlds().get(0);
        Location loc = new Location(world, 0, 0, 0);

        for(EntityType entityType : EntityType.values()) {
            if(entityType.isSpawnable()) {
                try {
                    Entity entity = world.spawnEntity(loc, entityType);
                    if (entity != null) {
                        if((!entity.isDead()) && entityType.isAlive()) {
                            mobEntityTypes.add(entityType);
                        }
                        entity.remove();
                    }
                }catch (IllegalArgumentException | NullPointerException ignored) {}
            }
        }
    }


    private void spawnMob(Player p, String type, int amount) {
        EntityType entity = EntityType.fromName(type);
        if(entity == null || !mobEntityTypes.contains(entity)) {
            p.sendMessage(SettingsFile.getError().replace("{Error}", "This Mob does not exist"));
            return;
        }

        if(NewSystem.hasPermission(p, permMob.replace("{Type}", type))) {
            for (int i = 0; i < amount; i++) {
                p.getWorld().spawnEntity(p.getLocation(), entity);
            }

            for (String msg : message) {
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                        .replace("{Type}", entity.getName())
                        .replace("{Amount}", String.valueOf(amount)));
            }
        }else{
            p.sendMessage(SettingsFile.getNoPerm());
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        List<String> tabCompletions = new ArrayList<>();

        if(sender instanceof Player) {
            Player p = (Player) sender;
            if (NewSystem.hasPermission(p, perm)) {
                for (EntityType entity : mobEntityTypes) {
                    if (NewSystem.hasPermission(p, permMob.replace("{Type}", entity.getName()))) {
                        if (entity.getName().contains(args[0])) {
                            tabCompletions.add(entity.getName());
                        }
                    }
                }
            }
        }else {
            for (EntityType entity : mobEntityTypes) {
                if (entity.getName().contains(args[0])) {
                    tabCompletions.add(entity.getName());
                }
            }
        }

        return tabCompletions;
    }
}
