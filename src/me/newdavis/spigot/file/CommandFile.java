package me.newdavis.spigot.file;

import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CommandFile {

    public static File file = new File("plugins/NewSystem/Command.yml");
    public static YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

    public static void loadConfig() {
        configurationSection.clear();
        string.clear();
        booleanSavings.clear();
        integer.clear();
        longSavings.clear();
        doubleSavings.clear();
        stringList.clear();

        if(file.exists()) {
            try {
                yaml.load(file);
                checkPaths();
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }else{
            saveConfig();
        }
    }

    public static void saveConfig() {
        checkPaths();
    }

    public static void checkPaths() {
        //Gamemode
        boolean pathsChanged = false;
        if (!isPathSet("Command.GameMode.Enabled")) {
            yaml.set("Command.GameMode.Enabled", true);
            yaml.set("Command.GameMode.Aliases", "gm,egamemode,egm");
            yaml.set("Command.GameMode.Usage", Collections.singletonList("{Prefix} §8/§eSpielmodus §8<§aSpielmodus§8> <§aSpieler§8>"));
            yaml.set("Command.GameMode.Permission.Use", "system.gamemode");
            yaml.set("Command.GameMode.Permission.GameMode", "system.gamemode.{GameMode}");
            yaml.set("Command.GameMode.Permission.GameModeOther", "system.gamemode.{GameMode}.other");
            yaml.set("Command.GameMode.Survival", "Überleben");
            yaml.set("Command.GameMode.Creative", "Kreativ");
            yaml.set("Command.GameMode.Adventure", "Abenteuer");
            yaml.set("Command.GameMode.Spectator", "Zuschauer");
            yaml.set("Command.GameMode.MessagePlayer",Collections.singletonList("{Prefix} Dein §eSpielmodus §7wurde zu §a{GameMode} §7geändert!"));
            yaml.set("Command.GameMode.Message", Collections.singletonList("{Prefix} Der §eSpielmodus §7von §a{Player} §7wurde zu §a{GameMode} §7geändert!"));
            pathsChanged = true;
        }

        //Fly
        if (!isPathSet("Command.Fly.Enabled")) {
            yaml.set("Command.Fly.Enabled", true);
            yaml.set("Command.Fly.Aliases", "efly");
            yaml.set("Command.Fly.Usage", Collections.singletonList("{Prefix} §8/§bFly §8<§7Spieler§8>"));
            yaml.set("Command.Fly.Permission.Use", "system.fly");
            yaml.set("Command.Fly.Permission.Other", "system.fly.other");
            yaml.set("Command.Fly.Activated", "§aaktiviert");
            yaml.set("Command.Fly.Deactivated", "§cdeaktiviert");
            yaml.set("Command.Fly.MessagePlayer", Collections.singletonList("{Prefix} Dein §bFlugmodus §7wurde {FlyMode}§7!"));
            yaml.set("Command.Fly.Message", Collections.singletonList("{Prefix} Der §bFlugmodus §7von §a{Player} §7wurde {FlyMode}§7!"));
            pathsChanged = true;
        }

        //Speed
        if (!isPathSet("Command.Speed.Enabled")) {
            yaml.set("Command.Speed.Enabled", true);
            yaml.set("Command.Speed.Aliases", "espeed");
            yaml.set("Command.Speed.Usage", Collections.singletonList("{Prefix} §8/§3Speed §8<§7Fly§8/§7Walk§8/§7Speed§8> <§7Speed§8/§7Spieler§8> <§7Spieler§8>"));
            yaml.set("Command.Speed.Permission.Use", "system.speed");
            yaml.set("Command.Speed.Permission.Fly", "system.speed.Fly");
            yaml.set("Command.Speed.Permission.Walk", "system.speed.Walk");
            yaml.set("Command.Speed.Permission.Other", "system.speed.other");
            yaml.set("Command.Speed.MessagePlayer", Collections.singletonList("{Prefix} Deine Geschwindigkeit wurde auf §b{Speed} §7gesetzt!"));
            yaml.set("Command.Speed.Message", Collections.singletonList("{Prefix} Du hast die Geschwindigkeit von {Player} §7auf §b{Speed} §7gesetzt!"));
            pathsChanged = true;
        }

        //Heal
        if (!isPathSet("Command.Heal.Enabled")) {
            yaml.set("Command.Heal.Enabled", true);
            yaml.set("Command.Heal.Aliases", "eheal");
            yaml.set("Command.Heal.Usage", Collections.singletonList("{Prefix} §8/§aHeal §8<§aSpieler§8>"));
            yaml.set("Command.Heal.Permission.Use", "system.heal");
            yaml.set("Command.Heal.Permission.Other", "system.heal.other");
            yaml.set("Command.Heal.MessagePlayer", Collections.singletonList("{Prefix} Du wurdest §ageheilt§7!"));
            yaml.set("Command.Heal.Message", Collections.singletonList("{Prefix} Du hast §a{Player} §ageheilt§7."));
            pathsChanged = true;
        }

        //Feed
        if (!isPathSet("Command.Feed.Enabled")) {
            yaml.set("Command.Feed.Enabled", true);
            yaml.set("Command.Feed.Aliases", "efeed");
            yaml.set("Command.Feed.Usage", Collections.singletonList("{Prefix} §8/§eFeed §8<§aSpieler§8>"));
            yaml.set("Command.Feed.Permission.Use", "system.feed");
            yaml.set("Command.Feed.Permission.Other", "system.feed.other");
            yaml.set("Command.Feed.MessagePlayer", Collections.singletonList("{Prefix} Dein §eHunger §7wurde gestillt!"));
            yaml.set("Command.Feed.Message", Collections.singletonList("{Prefix} Der §eHunger §7von §a{Player} §7wurde gestillt!"));
            pathsChanged = true;
        }

        //Back
        if (!isPathSet("Command.Back.Enabled")) {
            yaml.set("Command.Back.Enabled", true);
            yaml.set("Command.Back.Aliases", "eback");
            yaml.set("Command.Back.Usage", Collections.singletonList("{Prefix} §8/§fBack"));
            yaml.set("Command.Back.Permission.Use", "system.back");
            yaml.set("Command.Back.Permission.NoDelay", "system.back.nodelay");
            yaml.set("Command.Back.Permission.Teleport", "system.back.teleport");
            yaml.set("Command.Back.MessageCoordinations", Arrays.asList("{Prefix} §7Deine Koordinaten von deinem letzten Todes-Punkt:", "{Prefix} §7X: §f{X} §7X: §f{Y} §7X: §f{Z} §7Welt: §f{World}"));
            yaml.set("Command.Back.CommandCooldown", 1000*5);
            yaml.set("Command.Back.CooldownFormat", "{Hours}h {Minutes}m {Seconds}s");
            yaml.set("Command.Back.CooldownMessage", Collections.singletonList("{Prefix} §cBitte warte noch {Format}!"));
            yaml.set("Command.Back.TeleportCooldownInSeconds", 3);
            yaml.set("Command.Back.TeleportCooldownMessage", Collections.singletonList("{Prefix} §7Du wirst in §a{Seconds}s §7zu deinem Todes-Punkt teleportiert!"));
            yaml.set("Command.Back.TeleportCooldownCountIsOne", "einer");
            yaml.set("Command.Back.TeleportedMessage", Collections.singletonList("{Prefix} §7Du wurdest zu deinem Todes-Punkt teleportiert!"));
            yaml.set("Command.Back.NoDeathPointFound", Collections.singletonList("{Prefix} §cEs wurde kein Todes-Punkt von Dir gefunden!"));
            yaml.set("Command.Back.MessageMovedWhileTeleportation", Arrays.asList("{Prefix} §cTeleport Vorgang wurde abgebrochen!", "Du hast dich während der Teleportation bewegt!"));
            yaml.set("Command.Back.MessageAlreadyInTeleport", Collections.singletonList("{Prefix} §cDu wirst bereits teleportiert!"));
            pathsChanged = true;
        }

        //Enderchest
        if (!isPathSet("Command.EnderChest.Enabled")) {
            yaml.set("Command.EnderChest.Enabled", true);
            yaml.set("Command.EnderChest.Aliases", "ec");
            yaml.set("Command.EnderChest.Usage", Collections.singletonList("{Prefix} §8/§5Enderchest §8<§aSpieler§8>"));
            yaml.set("Command.EnderChest.Permission.Use", "system.enderchest");
            yaml.set("Command.EnderChest.Permission.Other", "system.enderchest.other");
            yaml.set("Command.EnderChest.MessagePlayer", Collections.singletonList("{Prefix} Deine §5Endertruhe §7wird geöffnet."));
            yaml.set("Command.EnderChest.Message", Collections.singletonList("{Prefix} Die §5Endertruhe §7von §a{Player} §7wird geöffnet."));
            pathsChanged = true;
        }

        //Invsee
        if (!isPathSet("Command.InvSee.Enabled")) {
            yaml.set("Command.InvSee.Enabled", true);
            yaml.set("Command.InvSee.Aliases", "einvsee");
            yaml.set("Command.InvSee.Usage", Collections.singletonList("{Prefix} §8/§aInvsee §8<§aSpieler§8>"));
            yaml.set("Command.InvSee.Permission.Use", "system.invsee");
            yaml.set("Command.InvSee.Permission.Edit", "system.invsee.edit");
            yaml.set("Command.InvSee.Message", Collections.singletonList("{Prefix} Das §aInventar §7von §a{Player} §7wird geöffnet."));
            pathsChanged = true;
        }

        //CraftingTable
        if (!isPathSet("Command.CraftingTable.Enabled")) {
            yaml.set("Command.CraftingTable.Enabled", true);
            yaml.set("Command.CraftingTable.Aliases", "craft");
            yaml.set("Command.CraftingTable.Usage", Collections.singletonList("{Prefix} §8/§eCraftingTable"));
            yaml.set("Command.CraftingTable.Message", Collections.singletonList("{Prefix} §7öffne Werkbank..."));
            yaml.set("Command.CraftingTable.Permission", "system.craftingtable");
            pathsChanged = true;
        }

        //Anvil
        if (!isPathSet("Command.Anvil.Enabled")) {
            yaml.set("Command.Anvil.Enabled", true);
            yaml.set("Command.Anvil.Aliases", "");
            yaml.set("Command.Anvil.Usage", Collections.singletonList("{Prefix} §8/§fAnvil"));
            yaml.set("Command.Anvil.Message", Collections.singletonList("{Prefix} §7öffne Amboss..."));
            yaml.set("Command.Anvil.Permission", "system.anvil");
            pathsChanged = true;
        }

        //EnchantingTable
        if (!isPathSet("Command.EnchantingTable.Enabled")) {
            yaml.set("Command.EnchantingTable.Enabled", true);
            yaml.set("Command.EnchantingTable.Aliases", "enchanttable");
            yaml.set("Command.EnchantingTable.Usage", Collections.singletonList("{Prefix} §8/§5EnchantingTable"));
            yaml.set("Command.EnchantingTable.Message", Collections.singletonList("{Prefix} §7öffne Verzauberungstisch..."));
            yaml.set("Command.EnchantingTable.Permission", "system.enchantingtable");
            pathsChanged = true;
        }

        //Kit
        if (!isPathSet("Command.Kit.Enabled")) {
            yaml.set("Command.Kit.Enabled", true);
            yaml.set("Command.Kit.Aliases", "ekit");
            yaml.set("Command.Kit.Usage", Collections.singletonList("{Prefix} §8/§aKit §8<§7Kit§8/§7list§8> §8<§7Spieler§8>"));
            yaml.set("Command.Kit.Permission.Use", "system.kit");
            yaml.set("Command.Kit.Permission.Kit", "system.kit.{Kit}");
            yaml.set("Command.Kit.Permission.NoDelay", "system.kit.{Kit}.noDelay");
            yaml.set("Command.Kit.Permission.Other", "system.kit.other");
            yaml.set("Command.Kit.MessagePlayer", Collections.singletonList("{Prefix} §7Du hast das Kit §a{Kit} §7erhalten."));
            yaml.set("Command.Kit.Message", Collections.singletonList("{Prefix} §7Du hast §a{Player} §7das Kit §a{Kit} §7gegeben."));
            yaml.set("Command.Kit.ShowOnlyKitsWithPermission", true);
            yaml.set("Command.Kit.KitListFormat", "§a{Kit}§8, ");
            yaml.set("Command.Kit.MessageKitList", Arrays.asList("{Prefix} §7Alle Verfügbaren Kits.", "§8» {Kits}"));
            yaml.set("Command.Kit.DelayFormat", "{Hours}h {Minutes}m {Seconds}s");
            yaml.set("Command.Kit.MessageKitDelay", Collections.singletonList("{Prefix} §cBitte warte noch {Delay}!"));
            pathsChanged = true;
        }

        //Give
        if (!isPathSet("Command.Give.Enabled")) {
            yaml.set("Command.Give.Enabled", true);
            yaml.set("Command.Give.Aliases", "egive");
            yaml.set("Command.Give.Usage", Collections.singletonList("{Prefix} §8/§6Give §8<§7Item§8/§7ItemID§8> <§7Anzahl§8/§7Spieler§8> <§7Spieler§8>"));
            yaml.set("Command.Give.Permission.Use", "system.give");
            yaml.set("Command.Give.Permission.Other", "system.give.other");
            yaml.set("Command.Give.MessagePlayer", Collections.singletonList("{Prefix} Du hast den Gegenstand §7{Amount}x§b{Item} §7mit der SubID §b{SubID} §7erhalten!"));
            yaml.set("Command.Give.Message", Collections.singletonList("{Prefix} Du hast {Player} §7den Gegenstand §7{Amount}x§b{Item} §7mit der SubID §b{SubID} §7gegeben!"));
            pathsChanged = true;
        }

        //GiveAll
        if (!isPathSet("Command.GiveAll.Enabled")) {
            yaml.set("Command.GiveAll.Enabled", true);
            yaml.set("Command.GiveAll.Aliases", "egiveall");
            yaml.set("Command.GiveAll.Usage", Collections.singletonList("{Prefix} §8/§eGiveAll §8<§7Anzahl§8>"));
            yaml.set("Command.GiveAll.Permission", "system.giveall");
            yaml.set("Command.GiveAll.MessagePlayer", Collections.singletonList("{Prefix} §7Alle Spieler haben §a{Amount}x {Item} §7erhalten."));
            yaml.set("Command.GiveAll.Message", Collections.singletonList("{Prefix} §7Du hast allen Spielern §a{Amount}x {Item} §7gegeben."));
            yaml.set("Command.GiveAll.MessageItemIsAir", Collections.singletonList("{Prefix} §cBitte halte einen Gegenstand in der Hand."));
            pathsChanged = true;
        }

        //SpawnMob
        if (!isPathSet("Command.SpawnMob.Enabled")) {
            yaml.set("Command.SpawnMob.Enabled", true);
            yaml.set("Command.SpawnMob.Aliases", "espawnmob");
            yaml.set("Command.SpawnMob.Usage", Collections.singletonList("{Prefix} §8/§aSpawnMob §8<§7Type§8> <§7Amount§8>"));
            yaml.set("Command.SpawnMob.Permission.Use", "system.spawnmob.use");
            yaml.set("Command.SpawnMob.Permission.Type", "system.spawnmob.{Type}");
            yaml.set("Command.SpawnMob.Message", Collections.singletonList("{Prefix} §7Du hast das Mob §a{Type} §7{Amount}§ax §7gespawned!"));
            yaml.set("Command.SpawnMob.MessageTypeNotFound", Collections.singletonList("{Prefix} §cDieser Mob wurde nicht gefunden!"));
            pathsChanged = true;
        }

        //GiveAll
        if (!isPathSet("Command.Raffle.Enabled")) {
            yaml.set("Command.Raffle.Enabled", true);
            yaml.set("Command.Raffle.Aliases", "verlosung");
            yaml.set("Command.Raffle.Usage", Collections.singletonList("{Prefix} §8/§dVerlosung §8<§7Anzahl§8>"));
            yaml.set("Command.Raffle.Permission", "system.raffle");
            yaml.set("Command.Raffle.MessageStarted", Collections.singletonList("{Prefix} §7Es wird der Gegenstand §a{Amount}x §7{Item} §7verlost!"));
            yaml.set("Command.Raffle.MessageChoose", Collections.singletonList("{Prefix} §7Es wird ein Zufälliger Spieler ausgewählt!"));
            yaml.set("Command.Raffle.MessageSelected", Collections.singletonList("{Prefix} §7Der Gewinner der §dVerlosung §7ist: {Player}"));
            yaml.set("Command.Raffle.MessageItemIsAir", Collections.singletonList("{Prefix} §cBitte halte einen Gegenstand in der Hand."));
            pathsChanged = true;
        }

        //Clear
        if (!isPathSet("Command.Clear.Enabled")) {
            yaml.set("Command.Clear.Enabled", true);
            yaml.set("Command.Clear.Aliases", "eclear");
            yaml.set("Command.Clear.Usage", Collections.singletonList("{Prefix} §8/§bClear §8<§7Spieler§8>"));
            yaml.set("Command.Clear.Permission.Use", "system.clear");
            yaml.set("Command.Clear.Permission.Other", "system.clear.other");
            yaml.set("Command.Clear.MessagePlayer", Collections.singletonList("{Prefix} Dein Inventar wurde in Müll geschmissen!"));
            yaml.set("Command.Clear.Message", Collections.singletonList("{Prefix} Das Inventar von {Player} §7wurde geleert!"));
            pathsChanged = true;
        }

        //Dupe
        if (!isPathSet("Command.Dupe.Enabled")) {
            yaml.set("Command.Dupe.Enabled", true);
            yaml.set("Command.Dupe.Aliases", "edupe");
            yaml.set("Command.Dupe.Usage", "{Prefix} §8/§eDupe");
            yaml.set("Command.Dupe.Permission.Use", "system.dupe");
            yaml.set("Command.Dupe.Permission.NoDelay", "system.dupe.noDelay");
            yaml.set("Command.Dupe.DelayInTicks", 1000*60*60);
            yaml.set("Command.Dupe.Message", Collections.singletonList("{Prefix} Du hast den Gegenstand in deiner Hand dupliziert."));
            yaml.set("Command.Dupe.DelayFormat", "{Hours}h {Minutes}m {Seconds}s");
            yaml.set("Command.Dupe.MessageDelay", Collections.singletonList("{Prefix} §cBitte warte noch {Delay}!"));
            yaml.set("Command.Dupe.MessageInventoryIsFull", Collections.singletonList("{Prefix} §cDein Inventar ist voll!"));
            yaml.set("Command.Dupe.MessageItemCanNotDuped", Collections.singletonList("{Prefix} §cDieser Gegenstand kann nicht dupliziert werden!"));
            yaml.set("Command.Dupe.MessageItemIsAir", Collections.singletonList("{Prefix} §cDu kannst die Luft nicht duplizieren!"));
            yaml.set("Command.Dupe.BlockedMaterials", Collections.singletonList("COMMAND"));
            pathsChanged = true;
        }

        //ItemEdit
        if (!isPathSet("Command.ItemEdit.Enabled")) {
            yaml.set("Command.ItemEdit.Enabled", true);
            yaml.set("Command.ItemEdit.Aliases", "eitemedit");
            yaml.set("Command.ItemEdit.Usage", Collections.singletonList("{Prefix} §8/§bItemEdit §8<§7Rename§8/§7Lore§8/§7Enchant§8/§7Sign§8>"));
            yaml.set("Command.ItemEdit.Permission.Use", "system.itemedit");
            yaml.set("Command.ItemEdit.Permission.Rename", "system.itemedit.rename");
            yaml.set("Command.ItemEdit.Permission.Lore", "system.itemedit.lore");
            yaml.set("Command.ItemEdit.Permission.Enchant", "system.itemedit.enchant");
            yaml.set("Command.ItemEdit.Permission.Sign", "system.itemedit.sign");
            yaml.set("Command.ItemEdit.MessageRenamed", Collections.singletonList("{Prefix} Dein Gegenstand wurde zu §a{Name} umbenannt."));
            yaml.set("Command.ItemEdit.LoreSplitBy", ";");
            yaml.set("Command.ItemEdit.MessageLoreSet", Collections.singletonList("{Prefix} Zu deinem Gegenstand wurde die Lore §a{Lore} §7hinzugefügt."));
            yaml.set("Command.ItemEdit.MessageEnchanted", Collections.singletonList("{Prefix} Dein Gegenstand wurde mit §5{Enchantment} §7Level §5{Level} §7enchanted."));
            yaml.set("Command.ItemEdit.MessageSigned", Collections.singletonList("{Prefix} Dein Gegenstand wurde mit §a{Sign} §7Signiert."));
            yaml.set("Command.ItemEdit.SignFormat", Arrays.asList("{Prefix} §7Dieser Gegenstand wurde Signiert!", "", "§8» §7Signiert von §a{Player}", "§8» §7Signiert am §a{Date}", "", "§8» §7{Message}"));
            yaml.set("Command.ItemEdit.MessageItemIsAir", Collections.singletonList("{Prefix} §cBitte halte einen Gegenstand in der Hand."));
            pathsChanged = true;
        }

        //Repair
        if (!isPathSet("Command.Repair.Enabled")) {
            yaml.set("Command.Repair.Enabled", true);
            yaml.set("Command.Repair.Aliases", "erepair");
            yaml.set("Command.Repair.Usage", Collections.singletonList("{Prefix} §8/§aRepair §8<§7Spieler§8/§7all§8/§7Armor§8> <§7all§8/§7Armor§8>"));
            yaml.set("Command.Repair.Permission.Use", "system.repair");
            yaml.set("Command.Repair.Permission.NoDelay", "system.repair.noDelay");
            yaml.set("Command.Repair.Permission.All", "system.repair.all");
            yaml.set("Command.Repair.Permission.Armor", "system.repair.armor");
            yaml.set("Command.Repair.Permission.Other", "system.repair.other");
            yaml.set("Command.Repair.MessagePlayer", Collections.singletonList("{Prefix} §7Dein Gegenstand in der Hand wurde repariert."));
            yaml.set("Command.Repair.Message", Collections.singletonList("{Prefix} §7Der Gegenstand in der Hand von §a{Player} §7wurde repariert."));
            yaml.set("Command.Repair.MessageAllPlayer", Collections.singletonList("{Prefix} §7Alles in deinem Inventar wurde repariert."));
            yaml.set("Command.Repair.MessageAll", Collections.singletonList("{Prefix} §7Alles im Inventar von §a{Player} §7wurde repariert."));
            yaml.set("Command.Repair.MessageArmorPlayer", Collections.singletonList("{Prefix} §7Deine komplette Rüstung wurde repariert."));
            yaml.set("Command.Repair.MessageArmor", Collections.singletonList("{Prefix} §7Die Rüstung von {Player} §7wurde repariert."));
            yaml.set("Command.Repair.MessageAlreadyRepairedPlayer", Collections.singletonList("{Prefix} §cDer Gegenstand in deiner Hand ist bereits repariert!"));
            yaml.set("Command.Repair.MessageAlreadyRepaired", Collections.singletonList("{Prefix} §cDer Gegenstand in der Hand von {Player} §cist repariert!"));
            yaml.set("Command.Repair.MessageAlreadyRepairedAllPlayer", Collections.singletonList("{Prefix} §cAlle Gegenstände in deinem Inventar sind repariert!"));
            yaml.set("Command.Repair.MessageAlreadyRepairedAll", Collections.singletonList("{Prefix} §cAlle Gegenstände im Inventar von §a{Player} §csind repariert!"));
            yaml.set("Command.Repair.MessageAlreadyRepairedArmorPlayer", Collections.singletonList("{Prefix} §cDeine Rüstung ist bereits repariert worden!"));
            yaml.set("Command.Repair.MessageAlreadyRepairedArmor", Collections.singletonList("{Prefix} §cDie Rüstung von {Player} §cist bereits repariert!"));
            yaml.set("Command.Repair.ItemCanNotRepaired", Collections.singletonList("{Prefix} §cDer Gegenstand kann nicht repariert werden!"));
            yaml.set("Command.Repair.CoolDownInTicks", 1000 * 60 * 5);
            yaml.set("Command.Repair.MessageCoolDown", Collections.singletonList("{Prefix} §cBitte warte noch {Minutes}m {Seconds}s!"));
            pathsChanged = true;
        }

        //Skull
        if (!isPathSet("Command.Skull.Enabled")) {
            yaml.set("Command.Skull.Enabled", true);
            yaml.set("Command.Skull.Aliases", "eskull");
            yaml.set("Command.Skull.Usage", Collections.singletonList("{Prefix} §8/§eSkull §8<§aSpieler§8>"));
            yaml.set("Command.Skull.Permission.Use", "system.skull");
            yaml.set("Command.Skull.Permission.Other", "system.skull.other");
            yaml.set("Command.Skull.Name", "§8» §7Kopf von §a{SkullOwner}");
            yaml.set("Command.Skull.MessagePlayer", Collections.singletonList("{Prefix} Du hast den Kopf von §a{SkullOwner} §7erhalten."));
            yaml.set("Command.Skull.Message", Collections.singletonList("{Prefix} Du hast §a{Player} §7den Kopf von §a{SkullOwner}§7 gegeben!"));
            pathsChanged = true;
        }

        //Hat
        if (!isPathSet("Command.Hat.Enabled")) {
            yaml.set("Command.Hat.Enabled", true);
            yaml.set("Command.Hat.Aliases", "ehat");
            yaml.set("Command.Hat.Usage", Collections.singletonList("{Prefix} §8/§7Hat"));
            yaml.set("Command.Hat.Permission", "system.hat");
            yaml.set("Command.Hat.ItemIsAir", Collections.singletonList("{Prefix} §cBitte halte einen Gegenstand in der Hand."));
            yaml.set("Command.Hat.Message", Collections.singletonList("{Prefix} §7Du hast den Gegenstand §e{Item} §7auf deinem Kopf."));
            pathsChanged = true;
        }

        //Backpack
        if (!isPathSet("Command.Backpack.Enabled")) {
            yaml.set("Command.Backpack.Enabled", true);
            yaml.set("Command.Backpack.Aliases", "bp");
            yaml.set("Command.Backpack.Usage", Collections.singletonList("{Prefix} §8/§6Backpack §8<§7Spieler§8>"));
            yaml.set("Command.Backpack.Permission.Use", "system.backpack");
            yaml.set("Command.Backpack.Permission.Other", "system.backpack.other");
            yaml.set("Command.Backpack.Size", 9*3);
            yaml.set("Command.Backpack.Title", "§6Rucksack");
            yaml.set("Command.Backpack.TitleOther", "§6Rucksack §8(§7{Player}§8)");
            yaml.set("Command.Backpack.Message", Collections.singletonList("{Prefix} §7Dein Rucksack wird geöffnet."));
            yaml.set("Command.Backpack.MessageOther", Collections.singletonList("{Prefix} §7Der Rucksack von {Player} §7wird geöffnet"));
            yaml.set("Command.Backpack.MessageSaved", Collections.singletonList("{Prefix} §7Der Inhalt deines Rucksackes wurde §agespeichert§7."));
            yaml.set("Command.Backpack.MessageSavedOther", Collections.singletonList("{Prefix} §7Der Inhalt vom Rucksack von {Player} §7wurde §agespeichert§7."));
            pathsChanged = true;
        }

        //Garbage
        if (!isPathSet("Command.Garbage.Enabled")) {
            yaml.set("Command.Garbage.Enabled", true);
            yaml.set("Command.Garbage.Aliases", "muell,müll");
            yaml.set("Command.Garbage.Usage", Collections.singletonList("{Prefix} §8/§aGarbage"));
            yaml.set("Command.Garbage.Permission", "system.garbage");
            yaml.set("Command.Garbage.Size", 9*3);
            yaml.set("Command.Garbage.Title", "{Prefix} Müll");
            yaml.set("Command.Garbage.MessageClose", Collections.singletonList("{Prefix} Nam Nam Nam..."));
            pathsChanged = true;
        }

        //Poll
        if (!isPathSet("Command.Poll.Enabled")) {
            yaml.set("Command.Poll.Enabled", true);
            yaml.set("Command.Poll.Aliases", "epoll");
            yaml.set("Command.Poll.Usage", Collections.singletonList("{Prefix} §8/§bUmfrage §8<§7Frage§8>"));
            yaml.set("Command.Poll.Permission", "system.umfrage");
            yaml.set("Command.Poll.Yes", "§aJa");
            yaml.set("Command.Poll.No", "§cNein");
            yaml.set("Command.Poll.MessageStarted", Collections.singletonList("{Prefix} §7Die Umfrage wurde erstellt."));
            yaml.set("Command.Poll.Message", Arrays.asList("§8§m----------§8(§b§lUmfrage§8)§8§m----------", "", "§8» §7Frage: {Question}", "", "§8» /§aJa §7für §aJa§7.{Embed-Yes}", "§8» /§cNein §7für §cNein§7.{Embed-No}", "", "§8§m----------§8(§b§lUmfrage§8)§8§m----------"));
            yaml.set("Command.Poll.MessageDelay", Arrays.asList("§8§m----------§8(§b§lUmfrage§8)§8§m----------", "", "§8» §7Frage: {Question}", "", "§8» /§aJa §7für §aJa§7. §8(§a{Count-Yes}§8){Embed-Yes}", "§8» /§cNein §7für §cNein§7. §8(§a{Count-No}§8){Embed-No}", "", "§8§m----------§8(§b§lUmfrage§8)§8§m----------"));
            yaml.set("Command.Poll.HoverMessage", "{Prefix} §7Klicke, um für {Poll} §7abzustimmen.");
            yaml.set("Command.Poll.MessagePollEndsSoon", Collections.singletonList("{Prefix} §7Die Umfrage endet in §a{Seconds} §7Sekunde§8(§7n§8)§7."));
            yaml.set("Command.Poll.MessagePollEnds", Arrays.asList("§8§m----------§8(§b§lUmfrage§8)§8§m----------", "", "§8» §7Frage: {Question}", "", "§8» {Winner} §7hat mit §a{Votes} §7Stimme§8(§7n§8) §7gewonnen.", "", "§8§m----------§8(§b§lUmfrage§8)§8§m----------"));
            yaml.set("Command.Poll.MessageVotesIsOne", "einer");
            yaml.set("Command.Poll.MessagePollEndsWithNoWinner", Arrays.asList("§8§m----------§8(§b§lUmfrage§8)§8§m----------", "", "§8» §7Frage: {Question}", "", "§8» §cUnentschieden, keiner hat gewonnen.", "", "§8§m----------§8(§b§lUmfrage§8)§8§m----------"));
            yaml.set("Command.Poll.MessageVoted", Collections.singletonList("{Prefix} §7Du hast für {Vote} §7abgestimmt!"));
            yaml.set("Command.Poll.MessageAlreadyVoted", Collections.singletonList("{Prefix} §cDu hast bereits abgestimmt!"));
            yaml.set("Command.Poll.MessagePollNotActive", Collections.singletonList("{Prefix} §cEs ist derzeitig keine Umfrage gestartet!"));
            yaml.set("Command.Poll.MessagePollActive", Collections.singletonList("{Prefix} §cEs ist derzeitig eine Umfrage gestartet!"));
            pathsChanged = true;
        }

        //Peace
        if (!isPathSet("Command.Peace.Enabled")) {
            yaml.set("Command.Peace.Enabled", true);
            yaml.set("Command.Peace.Aliases", "frieden,epeace,efrieden");
            yaml.set("Command.Peace.Usage", Collections.singletonList("{Prefix} §8/§6Peace §8<§7accept§8/§7decline§8/§7requests§8/§7list§8/§7Spieler§8> <§7Spieler§8>"));
            yaml.set("Command.Peace.Permission", "system.peace");
            yaml.set("Command.Peace.PeaceListFormat", "§7{Player}§8, ");
            yaml.set("Command.Peace.MessagePeaceList", Arrays.asList("{Prefix} §7Du hast mit §a{Count} §7Spielern §6Frieden §7geschlossen.", "{Prefix} {Peace}"));
            yaml.set("Command.Peace.MessageNoPeace", Collections.singletonList("{Prefix} §cDu hast mit keinem Spieler Frieden geschlossen!"));
            yaml.set("Command.Peace.RequestListFormat", "§7{Player}§8, ");
            yaml.set("Command.Peace.MessageRequestList", Arrays.asList("{Prefix} §7Du hast §a{Count} §7offene §6Friedens Angebote§7.", "{Prefix} {Requests}"));
            yaml.set("Command.Peace.MessageNoRequests", Collections.singletonList("{Prefix} §cDu hast keine offenen Friedens Angebote!"));
            yaml.set("Command.Peace.MessageRequest", Collections.singletonList("{Prefix} §7Du hast ein §6Friedens Angebot §7an {Player} §7gesendet."));
            yaml.set("Command.Peace.MessageCanNotSendRequestSelf", Collections.singletonList("{Prefix} §cDu kannst Dir selbst kein Friedens Angebot senden!"));
            yaml.set("Command.Peace.Request.Hover.Accept.Hover", "{Prefix} §7Klicke zum §aakzeptieren§7.");
            yaml.set("Command.Peace.Request.Hover.Accept.Text", "{Prefix} §7Klicke, um das §6Friedens Angebot §aanzunehmen§7.");
            yaml.set("Command.Peace.Request.Hover.Decline.Hover", "{Prefix} §7Klicke zum §cablehnen§7.");
            yaml.set("Command.Peace.Request.Hover.Decline.Text", "{Prefix} §7Klicke, um das §6Friedens Angebot §cabzulehnen§7.");
            yaml.set("Command.Peace.Request.Hover.EnableClick", true);
            yaml.set("Command.Peace.MessageRequestPlayer", Arrays.asList("{Prefix} §8§m----------§8(§6§lFrieden§8)§8§m----------", "", "{Prefix} {Player} §7hat dir ein §6Friedens Angebot §7gesendet.", "", "{Accept}", "{Decline}", "", "", "{Prefix} §8§m----------§8(§6§lFrieden§8)§8§m----------"));
            yaml.set("Command.Peace.MessageRequestAlreadySend", Collections.singletonList("{Prefix} §cDu hast {Player} §cbereits ein Friedens Angebot gesendet!"));
            yaml.set("Command.Peace.MessageAccept", Collections.singletonList("{Prefix} §7Du hast das §6Friedens Angebot §7von {Player} §aakzeptiert§7!"));
            yaml.set("Command.Peace.MessageAcceptPlayer", Collections.singletonList("{Prefix} §7Dein §6Friedens Angebot §7an {Player} §7wurde §aangenommen§7!"));
            yaml.set("Command.Peace.MessageDecline", Collections.singletonList("{Prefix} §7Du hast das §6Friedens Angebot §7von {Player} §cabgelehnt§7!"));
            yaml.set("Command.Peace.MessageDeclinePlayer", Collections.singletonList("{Prefix} §7Dein §6Friedens Angebot §7an {Player} §7wurde §cabgelehnt§7!"));
            yaml.set("Command.Peace.MessageNoRequestOfPlayer", Collections.singletonList("{Prefix} §cDu hast kein Friedens Angebot von {Player} §cerhalten!"));
            yaml.set("Command.Peace.MessagePeaceDisband", Collections.singletonList("{Prefix} §cDu hast den Frieden mit {Player} §caufgelöst!"));
            yaml.set("Command.Peace.MessagePeaceDisbandPlayer", Collections.singletonList("{Prefix} §cDer Frieden zwischen Dir und {Player} §cwurde aufgelöst!"));
            yaml.set("Command.Peace.MessageNoPeaceWithPlayer", Collections.singletonList("{Prefix} §cDu hast kein Frieden mit {Player} §cgeschlossen!"));
            yaml.set("Command.Peace.MessageTryToDamage", Collections.singletonList("{Prefix} §cDu hast mit {Player} §CFrieden geschlossen!"));
            pathsChanged = true;
        }

        //God
        if (!isPathSet("Command.God.Enabled")) {
            yaml.set("Command.God.Enabled", true);
            yaml.set("Command.God.Aliases", "egod");
            yaml.set("Command.God.Usage", Collections.singletonList("{Prefix} §8/§eGod §8<§7Spieler§8>"));
            yaml.set("Command.God.Permission.Use", "system.god");
            yaml.set("Command.God.Permission.Other", "system.god.other");
            yaml.set("Command.God.Activated", "§aaktiviert");
            yaml.set("Command.God.Deactivated", "§cdeaktiviert");
            yaml.set("Command.God.MessagePlayer", Collections.singletonList("{Prefix} Dein §eGott§7-§eModus §7wurde {Status}§7!"));
            yaml.set("Command.God.Message", Collections.singletonList("{Prefix} Du hast den §eGott§7-§eModus §7von {Player} {Status}§7!"));
            pathsChanged = true;
        }

        //Freeze
        if (!isPathSet("Command.Freeze.Enabled")) {
            yaml.set("Command.Freeze.Enabled", true);
            yaml.set("Command.Freeze.Aliases", "efreeze");
            yaml.set("Command.Freeze.Usage", Collections.singletonList("{Prefix} §8/§fFreeze §8<§7Spieler§8>"));
            yaml.set("Command.Freeze.Permission", "system.freeze");
            yaml.set("Command.Freeze.MessageFreezed", Collections.singletonList("{Prefix} §7Du hast {Player} §7eingefroren!"));
            yaml.set("Command.Freeze.MessageFreezedPlayer", Collections.singletonList("{Prefix} §7Du wurdest eingefroren!"));
            yaml.set("Command.Freeze.MessageUnfreezed", Collections.singletonList("{Prefix} §7Du hast {Player} §7enteist!"));
            yaml.set("Command.Freeze.MessageUnfreezedPlayer", Collections.singletonList("{Prefix} §7Du bist enteist!"));
            pathsChanged = true;
        }

        //Vanish
        if (!isPathSet("Command.Vanish.Enabled")) {
            yaml.set("Command.Vanish.Enabled", true);
            yaml.set("Command.Vanish.Aliases", "v,evanish");
            yaml.set("Command.Vanish.Usage", Collections.singletonList("{Prefix} §8/§dVanish §8<§7Spieler§8/§7List§8>"));
            yaml.set("Command.Vanish.Permission.Use", "system.vanish");
            yaml.set("Command.Vanish.Permission.Other", "system.vanish.other");
            yaml.set("Command.Vanish.Permission.List", "system.vanish.list");
            yaml.set("Command.Vanish.Activated", "§aaktiviert");
            yaml.set("Command.Vanish.Deactivated", "§cdeaktiviert");
            yaml.set("Command.Vanish.MessagePlayer", Collections.singletonList("{Prefix} §7Dein §dVanish §7wurde {Vanish}§7!"));
            yaml.set("Command.Vanish.Message", Collections.singletonList("{Prefix} §7Der §dVanish §7von §a{Player} §7wurde {Vanish}§7!"));
            yaml.set("Command.Vanish.MessageStillInVanish", Collections.singletonList("{Prefix} §7Dein §dVanish §7ist derzeitig §aaktiviert§7!"));
            yaml.set("Command.Vanish.List.VanishedPlayers", "{Player} §8┃");
            yaml.set("Command.Vanish.List.MessageNoVanish", "§cEs ist kein Spieler im Vanish!");
            yaml.set("Command.Vanish.List.Message", Arrays.asList("{Prefix} Es sind §a{Vanish-Count} §7Spieler im §dVanish§7.", "{Prefix} {Vanished-Player}"));
            pathsChanged = true;
        }

        //PrivateMessage
        if (!isPathSet("Command.PrivateMessage.Enabled")) {
            yaml.set("Command.PrivateMessage.Enabled", true);
            yaml.set("Command.PrivateMessage.Aliases", "msg,emsg,pm,reply,r,er,erply");
            yaml.set("Command.PrivateMessage.Usage", Collections.singletonList("{Prefix} §8/§fPrivateMessage §8<§7Spieler§8/§7Nachricht§8> <§7Nachricht§8>"));
            yaml.set("Command.PrivateMessage.Permission", "system.privatemessage");
            yaml.set("Command.PrivateMessage.EnabledColoredMessage", true);
            yaml.set("Command.PrivateMessage.CanNotSendSelf", Collections.singletonList("{Prefix} §cDu kannst dir selbst keine Private Nachricht senden!"));
            yaml.set("Command.PrivateMessage.Message", Collections.singletonList("{Prefix} §aDu §8» §a{Player} §8● §7{Message}"));
            yaml.set("Command.PrivateMessage.MessagePlayer", Collections.singletonList("{Prefix} §a{Player} §8» §aDir §8● §7{Message}"));
            pathsChanged = true;
        }

        //Build
        if (!isPathSet("Command.Build.Enabled")) {
            yaml.set("Command.Build.Enabled", true);
            yaml.set("Command.Build.Aliases", "ebuild");
            yaml.set("Command.Build.Usage", Collections.singletonList("{Prefix} §8/§eBuild §8<§aSpieler§8>"));
            yaml.set("Command.Build.Permission.Use", "system.build");
            yaml.set("Command.Build.Permission.Other", "system.build.other");
            yaml.set("Command.Build.Activated", "§aaktiviert");
            yaml.set("Command.Build.Deactivated", "§cdeaktiviert");
            yaml.set("Command.Build.MessagePlayer", Collections.singletonList("{Prefix} Dein §eBaumodus §7wurde {BuildMode}§7!"));
            yaml.set("Command.Build.Message", Collections.singletonList("{Prefix} Der §eBaumodus §7von §a{Player} §7wurde {BuildMode}§7!"));
            yaml.set("Command.Build.DenyMessage", Collections.singletonList("{Prefix} §cAktiviere den Baumodus um etwas zu platzieren/zerstören!"));
            pathsChanged = true;
        }

        //Hologram
        if (!isPathSet("Command.Hologram.Enabled")) {
            yaml.set("Command.Hologram.Enabled", true);
            yaml.set("Command.Hologram.Aliases", "holo,ehologram,eholo");
            yaml.set("Command.Hologram.Usage", Collections.singletonList("{Prefix} §8/§3Hologramm §8<§7reload§8/§7create§8/§7delete§8/§7rename§8/§7move§8/§7list§8/§7addLine§8/§7removeLine§8/§7setLine§8/§7switchLine§8/§7setTitle§8> <§7name§8> <§7line§8/§7text§8>"));
            yaml.set("Command.Hologram.Permission.Use", "system.hologram");
            yaml.set("Command.Hologram.Permission.Title", "system.hologram.title");
            yaml.set("Command.Hologram.Permission.Line", "system.hologram.line");
            yaml.set("Command.Hologram.Permission.Manage", "system.hologram.manage");
            yaml.set("Command.Hologram.Message.HologramNotExist", Collections.singletonList("{Prefix} §cDas Hologramm existiert nicht!"));
            yaml.set("Command.Hologram.Message.HologramLineNotExist", Collections.singletonList("{Prefix} §cDas Hologramm besitzt diese Line nicht!"));
            yaml.set("Command.Hologram.Message.NoHologramsCreated", Collections.singletonList("{Prefix} §cEs gibt keine erstellten Hologramme!"));
            yaml.set("Command.Hologram.Message.Reloaded", Collections.singletonList("{Prefix} §7Du hast §c§nalle§7 Hologramme neugeladen!"));
            yaml.set("Command.Hologram.Message.Renamed", Collections.singletonList("{Prefix} §7Du hast den Namen vom §3Hologramm §7{HologramName} §7zu {NewHologramName} §7geändert!"));
            yaml.set("Command.Hologram.Message.Created", Arrays.asList("{Prefix} §7Du hast ein §3Hologramm §aerstellt§7!", "{Prefix} §7Name des §3Hologrammes §7ist {HologramName}"));
            yaml.set("Command.Hologram.Message.Deleted", Collections.singletonList("{Prefix} §7Du hast das §3Hologramm §7{HologramName} §cgelöscht§7!"));
            yaml.set("Command.Hologram.Message.Moved", Collections.singletonList("{Prefix} §7Du hast das §3Hologramm §7{HologramName} §7zu {Player} verschoben!"));
            yaml.set("Command.Hologram.Message.LineAdded", Collections.singletonList("{Prefix} §7Du hast dem §3Hologramm §7{HologramName} §7eine neue §3Line §ahinzugefügt§7!"));
            yaml.set("Command.Hologram.Message.LineRemoved", Collections.singletonList("{Prefix} §7Du hast dem §3Hologramm §7{HologramName} §7die §3Line §7{Line} §centfernt§7!"));
            yaml.set("Command.Hologram.Message.LineSet", Collections.singletonList("{Prefix} §7Du hast die §3Line §7{Line} §7zu {Text} §7geändert!"));
            yaml.set("Command.Hologram.Message.LineSwitched", Collections.singletonList("{Prefix} §7Du hast die §3Line §7{Line} mit {SwitchedLine} gewechselt!"));
            yaml.set("Command.Hologram.Message.TitleSet", Collections.singletonList("{Prefix} §7Du hast den Titel vom §3Hologramm §7{HologramName} §7geändert!"));
            yaml.set("Command.Hologram.DefaultTitle", "{Prefix} §cKein Titel gefunden! Nutze /holo setTitle {HologramName} <Title>");
            yaml.set("Command.Hologram.EnableTeleportToHologram", true);
            yaml.set("Command.Hologram.HoverHologram", true);
            yaml.set("Command.Hologram.Message.HoverMessage", "{Prefix} §3Hologramm§7: {HologramName}\n{Prefix} §7Titel: {Title}\n{Prefix} §7World: §3{World}\n{Prefix} §7Location-X: §3{Loc-X}\n{Prefix} §7Location-Y: §3{Loc-Y}\n{Prefix} §7Location-Z: §3{Loc-Z}\n{Prefix} §7Lines: §3{LineCount}");
            yaml.set("Command.Hologram.Message.HologramFormat", "§3{HologramName}§8, §3");
            yaml.set("Command.Hologram.Message.HologramList", Arrays.asList("{Prefix} §7Es gibt §3{HologramCount} §7erstellte§8/§7s §3Hologramm§8/§3e§7!", "{Prefix} {Holograms}"));
            pathsChanged = true;
        }

        //Currency
        if (!isPathSet("Command.Currency.Enabled.Currency")) {
            yaml.set("Command.Currency.Enabled.Currency", true);
            yaml.set("Command.Currency.Aliases", "bal,eco,ecurrency,economy");
            yaml.set("Command.Currency.Enabled.Pay", true);
            yaml.set("Command.Currency.Usage", Collections.singletonList("{Prefix} §8/§eCurrency §8<§7Spieler§8/§7Top§8>"));
            yaml.set("Command.Currency.UsagePay", Collections.singletonList("{Prefix} §8/§6Pay §8<§7Spieler§8/§7*§8> <§7Betrag§8>"));
            yaml.set("Command.Currency.UsageWithPermission", Collections.singletonList("{Prefix} §8/§eCurrency §8<§7Spieler§8/§7Top§8> <§7reset§8/§7add§8/§7remove§8/§7set§8/§7multiply§8/§7divide§8> <§7number§8>"));
            yaml.set("Command.Currency.Permission.Use", "system.currency");
            yaml.set("Command.Currency.Permission.Other", "system.currency.other");
            yaml.set("Command.Currency.Permission.Pay", "system.currency.pay");
            yaml.set("Command.Currency.Permission.PayEveryone", "system.currency.pay.*");
            yaml.set("Command.Currency.Permission.TopList", "system.currency.toplist");
            yaml.set("Command.Currency.DefaultAmount", 1000D);
            yaml.set("Command.Currency.Prefix", "€");
            yaml.set("Command.Currency.MessageNotEnoughMoney", Collections.singletonList("{Prefix} §cDu hast nicht genügend Geld!"));
            yaml.set("Command.Currency.MessagePaySelf", Collections.singletonList("{Prefix} §cDu kannst dir nicht selber Geld schicken!"));
            yaml.set("Command.Currency.MessageNumberNegative", Collections.singletonList("{Prefix} §cDie angegebene Zahl ist negativ, bitte verwende positive Zahlen!"));
            yaml.set("Command.Currency.MessagePay", Collections.singletonList("{Prefix} §7Du hast {Player} §a+{Amount}{CurrencyPrefix} §7gesendet!"));
            yaml.set("Command.Currency.MessagePayPlayer", Collections.singletonList("{Prefix} §7Du hast §a+{Amount}{CurrencyPrefix} §7von {Player} §7erhalten!"));
            yaml.set("Command.Currency.MessagePayEveryone", Collections.singletonList("{Prefix} §7Du hast §c§nallen Spielern§r §a+{Amount}{CurrencyPrefix} §7gesendet!"));
            yaml.set("Command.Currency.MessageShowMoneyPlayer", Collections.singletonList("{Prefix} §7Du hast §a+{Amount}{CurrencyPrefix}§7!"));
            yaml.set("Command.Currency.MessageShowMoney", Collections.singletonList("{Prefix} {Player} §7hat §a+{Amount}{CurrencyPrefix}§7!"));
            yaml.set("Command.Currency.MessageAddPlayer", Collections.singletonList("{Prefix} §7Du hast §a+{Amount}{CurrencyPrefix} §7erhalten!"));
            yaml.set("Command.Currency.MessageAdd", Collections.singletonList("{Prefix} §7Du hast {Player} §a+{Amount}{CurrencyPrefix} §7gutgeschrieben!"));
            yaml.set("Command.Currency.MessageRemovePlayer", Collections.singletonList("{Prefix} §7Dir wurden §c-{Amount}{CurrencyPrefix} §7abgezogen!"));
            yaml.set("Command.Currency.MessageRemove", Collections.singletonList("{Prefix} §7Du hast {Player} §c-{Amount}{CurrencyPrefix} §7abgezogen!"));
            yaml.set("Command.Currency.MessageSetPlayer", Collections.singletonList("{Prefix} §7Dein gesamtes Geld wurde auf §a+{Amount}{CurrencyPrefix} §7gesetzt!"));
            yaml.set("Command.Currency.MessageSet", Collections.singletonList("{Prefix} §7Du hast das gesamte Geld von {Player} auf §a+{Amount}{CurrencyPrefix} §7gesetzt!"));
            yaml.set("Command.Currency.MessageResetPlayer", Collections.singletonList("{Prefix} §cDein gesamtes Geld wurde zurückgesetzt!"));
            yaml.set("Command.Currency.MessageReset", Collections.singletonList("{Prefix} §7Du hast das Geld von {Player} §7zurückgesetzt!"));
            yaml.set("Command.Currency.MessageMultiplyPlayer", Collections.singletonList("{Prefix} §7Dein gesamtes Geld wurde mit §b{Faktor} §7multipliziert!"));
            yaml.set("Command.Currency.MessageMultiply", Collections.singletonList("{Prefix} §7Du hast das gesamte Geld von {Player} §7mit §b{Faktor} §7multipliziert!"));
            yaml.set("Command.Currency.MessageDividePlayer", Collections.singletonList("{Prefix} §7Dein gesamtes Geld wurde mit §b{Dividend} §7dividiert"));
            yaml.set("Command.Currency.MessageDivide", Collections.singletonList("{Prefix} §7Du hast das gesamte Geld von {Player} §7mit §b{Dividend} §7dividiert!"));
            yaml.set("Command.Currency.EnableTopList", true);
            yaml.set("Command.Currency.TopListSize", 10);
            yaml.set("Command.Currency.FirstPlacing", "§e§l1§f.");
            yaml.set("Command.Currency.SecondPlacing", "§f§l2§f.");
            yaml.set("Command.Currency.ThirdPlacing", "§6§l3§f.");
            yaml.set("Command.Currency.OtherPlacing", "§7{Placing}§f.");
            yaml.set("Command.Currency.NoPlayer", "§c-");
            yaml.set("Command.Currency.NoMoney", 0D);
            yaml.set("Command.Currency.TopListFormat", "§8» {Placing} §7Platz §8┃ §7{Player} besitzt §b{Amount}§7{CurrencyPrefix}");
            yaml.set("Command.Currency.TopListMessage", Arrays.asList("{Prefix} §eTop Balance", "", "{TopListFormat}", "", "{Prefix} Insgesamt sind §b{AllMoneyCount}§7{CurrencyPrefix} im Umlauf!"));
            pathsChanged = true;
        }

        //PlayTime
        if (!isPathSet("Command.PlayTime.Enabled")) {
            yaml.set("Command.PlayTime.Enabled", true);
            yaml.set("Command.PlayTime.Aliases", "pt,eplaytime,ept");
            yaml.set("Command.PlayTime.Usage", Collections.singletonList("{Prefix} §8/§6Playtime §8<§7Spieler§8>"));
            yaml.set("Command.PlayTime.Permission.Use", "system.playtime");
            yaml.set("Command.PlayTime.PlayTimeFormat", "{Days}d {Hours}h {Minutes}m");
            yaml.set("Command.PlayTime.MessagePlayer", Collections.singletonList("{Prefix} §7Deine Spielzeit: §a{PlayTime}"));
            yaml.set("Command.PlayTime.Message", Collections.singletonList("{Prefix} §7Die Spielzeit von §a{Player}§7: §a{PlayTime}"));
            pathsChanged = true;
        }

        //ClearLag
        if(!isPathSet("Command.ClearLag.Enabled")) {
            yaml.set("Command.ClearLag.Enabled", true);
            yaml.set("Command.ClearLag.Aliases", "cl");
            yaml.set("Command.ClearLag.Usage", Collections.singletonList("{Prefix} §8/§cClearLag §8<§7Welt§8>"));
            yaml.set("Command.ClearLag.Permission", "system.clearlag");
            yaml.set("Command.ClearLag.Message", Collections.singletonList("{Prefix} §7Du hast §a{Amount} §7bodenliegende Gegenstände entfernt!"));
            yaml.set("Command.ClearLag.Auto.Enabled", true);
            yaml.set("Command.ClearLag.Auto.DelayInMinutes", 10);
            yaml.set("Command.ClearLag.Auto.Message.Enabled", true);
            yaml.set("Command.ClearLag.Auto.Message.MinutesLeft.1", Collections.singletonList("{Prefix} §7In einer Minute werden alle bodenliegenden Gegenstände entfernt!"));
            yaml.set("Command.ClearLag.Auto.Message.Deleted", Collections.singletonList("{Prefix} §7Es wurden §a{Amount} §7bodenliegende Gegenstände entfernt!"));
            pathsChanged = true;
        }

        //TPA
        if (!isPathSet("Command.TeleportAsk.Enabled")) {
            yaml.set("Command.TeleportAsk.Enabled", true);
            yaml.set("Command.TeleportAsk.Aliases", "eteleportask,etpa,tpa");
            yaml.set("Command.TeleportAsk.Usage", Collections.singletonList("{Prefix} §8/§9Tpa §8<§7Spieler§8>"));
            yaml.set("Command.TeleportAsk.Permission", "system.tpa");
            yaml.set("Command.TeleportAsk.Message", Collections.singletonList("{Prefix} §7Deine Anfrage wurde versendet!"));
            yaml.set("Command.TeleportAsk.MessagePlayer", Collections.singletonList("{Prefix} §7Du hast eine Anfrage von §a{Player} §7erhalten, er möchte sich zu dir teleportieren."));
            pathsChanged = true;
        }

        //TPAHere
        if (!isPathSet("Command.TeleportAskHere.Enabled")) {
            yaml.set("Command.TeleportAskHere.Enabled", true);
            yaml.set("Command.TeleportAskHere.Aliases", "etpahere,tpahere");
            yaml.set("Command.TeleportAskHere.Usage", Collections.singletonList("{Prefix} §8/§9TpaHere §8<§7Spieler§8>"));
            yaml.set("Command.TeleportAskHere.Permission", "system.tpahere");
            yaml.set("Command.TeleportAskHere.Message", Collections.singletonList("{Prefix} §7Deine Anfrage wurde versendet!"));
            yaml.set("Command.TeleportAskHere.MessagePlayer", Collections.singletonList("{Prefix} §7Du hast eine Anfrage von §a{Player} §7erhalten, er möchte dich zu ihm teleportieren."));
            pathsChanged = true;
        }

        //TPAccept
        if (!isPathSet("Command.TeleportAccept.Enabled")) {
            yaml.set("Command.TeleportAccept.Enabled", true);
            yaml.set("Command.TeleportAccept.Aliases", "etpaccept,tpaccept");
            yaml.set("Command.TeleportAccept.Usage", Collections.singletonList("{Prefix} §8/§9TpAccept"));
            yaml.set("Command.TeleportAccept.Permission.Use", "system.tpaccept");
            yaml.set("Command.TeleportAccept.Permission.NoDelay", "system.tpaccept.nodelay");
            yaml.set("Command.TeleportAccept.MessageTeleportSendYourSelf", Collections.singletonList("{Prefix} §cDu kannst dich nicht selbst teleportieren!"));
            yaml.set("Command.TeleportAccept.DelayInSeconds", 3);
            yaml.set("Command.TeleportAccept.CountIsOne", "einer");
            yaml.set("Command.TeleportAccept.MessageAccepted", Collections.singletonList("{Prefix} §7Deine Anfrage wurde §aangenommen§7!"));
            yaml.set("Command.TeleportAccept.MessageAcceptedPlayer", Collections.singletonList("{Prefix} §7Du hast die Anfrage §aangenommen§7!"));
            yaml.set("Command.TeleportAccept.MessageTeleportWithDelay", Collections.singletonList("{Prefix} §7Du wirst in §a{Seconds} Sek §7teleportiert."));
            yaml.set("Command.TeleportAccept.MessageTeleport", Collections.singletonList("{Prefix} §7Du wirst teleportiert."));
            yaml.set("Command.TeleportAccept.MessageNoRequests", Collections.singletonList("{Prefix} §cDu hast keine Anfrage erhalten!"));
            yaml.set("Command.TeleportAccept.MessageMovedWhileTeleportation", Arrays.asList("{Prefix} §cTeleport Vorgang wurde abgebrochen!", "Du hast dich während der Teleportation bewegt!"));
            yaml.set("Command.TeleportAccept.MessageAlreadyInTeleport", Collections.singletonList("{Prefix} §cDu wirst bereits teleportiert!"));
            pathsChanged = true;
        }

        //TP
        if (!isPathSet("Command.Teleport.Enabled")) {
            yaml.set("Command.Teleport.Enabled", true);
            yaml.set("Command.Teleport.Aliases", "eteleport,etp,tp");
            yaml.set("Command.Teleport.Usage", Collections.singletonList("{Prefix} §8/§9Tp §8<§7Spieler§8/§7X§8> <§7Y§8> <§7Z§8> <§7World§8>"));
            yaml.set("Command.Teleport.Permission", "system.tp");
            yaml.set("Command.Teleport.MessageLocationPlayer", Collections.singletonList("{Prefix} §7Du wurdest zu der Position §a{X}x {Y}y {Z}z §7teleportiert."));
            yaml.set("Command.Teleport.MessageLocation", Collections.singletonList("{Prefix} {Player} §7wurde zu der Position §a{X}x {Y}y {Z}z §7teleportiert."));
            yaml.set("Command.Teleport.MessagePlayer", Collections.singletonList("{Prefix} §7Du wurdest zu §a{TeleportTo} §7teleportiert."));
            yaml.set("Command.Teleport.Message", Collections.singletonList("{Prefix} {Player} §7wurde zu {TeleportTo} §7teleportiert."));
            pathsChanged = true;
        }

        //TPHere
        if (!isPathSet("Command.TeleportHere.Enabled")) {
            yaml.set("Command.TeleportHere.Enabled", true);
            yaml.set("Command.TeleportHere.Aliases", "etphere,tphere");
            yaml.set("Command.TeleportHere.Usage", Collections.singletonList("{Prefix} §8/§9TpHere §8<§7Spieler§8>"));
            yaml.set("Command.TeleportHere.Permission", "system.tphere");
            yaml.set("Command.TeleportHere.MessagePlayer", Collections.singletonList("{Prefix} §7Du wurdest zu §a{Player} §7teleportiert."));
            yaml.set("Command.TeleportHere.Message", Collections.singletonList("{Prefix} {Player} §7wurde zu §adir §7teleportiert."));
            pathsChanged = true;
        }

        //TPAll
        if (!isPathSet("Command.TeleportAll.Enabled")) {
            yaml.set("Command.TeleportAll.Enabled", true);
            yaml.set("Command.TeleportAll.Aliases", "tpall,eteleportall,etpall");
            yaml.set("Command.TeleportAll.Usage", Collections.singletonList("{Prefix} §8/§9TpAll §8<§7Spieler§8>"));
            yaml.set("Command.TeleportAll.Permission", "system.tpall");
            yaml.set("Command.TeleportAll.MessagePlayer", Collections.singletonList("{Prefix} §7Alle Spieler wurden zu dir teleportiert."));
            yaml.set("Command.TeleportAll.MessagePlayerTeleportTo", Collections.singletonList("{Prefix} §7Alle Spieler werden zu §a{TeleportTo} §7teleportiert."));
            yaml.set("Command.TeleportAll.MessageAll", Collections.singletonList("{Prefix} §7Du wurdest zu {Player} §7teleportiert."));
            pathsChanged = true;
        }

        //TPAAll
        if (!isPathSet("Command.TeleportAskAll.Enabled")) {
            yaml.set("Command.TeleportAskAll.Enabled", true);
            yaml.set("Command.TeleportAskAll.Aliases", "etpaall,eteleportaskall,tpaall");
            yaml.set("Command.TeleportAskAll.Usage", Collections.singletonList("{Prefix} §8/§9TpAAll"));
            yaml.set("Command.TeleportAskAll.Permission", "system.tpaall");
            yaml.set("Command.TeleportAskAll.MessagePlayer", Collections.singletonList("{Prefix} §7Deine Anfrage wurde an alle versendet!"));
            yaml.set("Command.TeleportAskAll.Message", Collections.singletonList("{Prefix} §7Du hast eine Anfrage von §a{Player} §7erhalten, er möchte dich zu ihm teleportieren."));
            pathsChanged = true;
        }

        //Home
        if (!isPathSet("Command.Home.Enabled")) {
            yaml.set("Command.Home.Enabled", true);
            yaml.set("Command.Home.Aliases", "");
            yaml.set("Command.Home.Usage", Collections.singletonList("{Prefix} §8/§6Home §8<§7create§8/§7delete§8/§7list§8/§7home§8> <§7home§8>"));
            yaml.set("Command.Home.Permission.Use", "system.home");
            yaml.set("Command.Home.Permission.MaxHomes", "system.home.{MaxHomes}");
            yaml.set("Command.Home.Permission.NoDelay", "system.home.nodelay");
            yaml.set("Command.Home.TeleportDelayInSeconds", 3);
            yaml.set("Command.Home.MessageHomeCreated", Collections.singletonList("{Prefix} §7Du hast den Home §6{Home} §7erstellt!"));
            yaml.set("Command.Home.MessageHomeAlreadyCreated", Collections.singletonList("{Prefix} §cDieser Home existiert bereits!"));
            yaml.set("Command.Home.MessageHomeCanNotCreateMoreHomes", Collections.singletonList("{Prefix} §cDu kannst keine weiteren Homes erstellen!"));
            yaml.set("Command.Home.MessageHomeDeleted", Collections.singletonList("{Prefix} §7Du hast den Home §6{Home} §7gelöscht!"));
            yaml.set("Command.Home.MessageHomeNotExist", Collections.singletonList("{Prefix} §cDieser Home existiert nicht!"));
            yaml.set("Command.Home.MessageHomeMovedWhileTeleport", Collections.singletonList("{Prefix} §cTeleportierung wurde abgebrochen, da du dich bewegt hast!"));
            yaml.set("Command.Home.MessageHomeAlreadyInTeleport", Collections.singletonList("{Prefix} §cDu wirst bereits teleportiert!"));
            yaml.set("Command.Home.MessageHomeTeleport", Collections.singletonList("{Prefix} §7Du wirst zu deinem Home §6{Home} §7teleportiert!"));
            yaml.set("Command.Home.MessageHomeTeleportDelay", Collections.singletonList("{Prefix} §7Du wirst in §a{Seconds}§7sek zu deinem Home §6{Home} §7teleportiert!"));
            yaml.set("Command.Home.HomeTeleportCountIsOne", Collections.singletonList("einer"));
            yaml.set("Command.Home.MessageNoHomes", Collections.singletonList("{Prefix} §cEs wurden keine Home-Punkte von Dir gefunden!"));
            yaml.set("Command.Home.MessageHomesFormat", "§7{Home}§8,§7 ");
            yaml.set("Command.Home.MessageHomeList", Arrays.asList("{Prefix} §7Du besitzt §a{Count} §7Home Punkte.", "{Prefix} {Homes}"));
            pathsChanged = true;
        }

        //Spawn
        if (!isPathSet("Command.Spawn.Enabled")) {
            yaml.set("Command.Spawn.Enabled", true);
            yaml.set("Command.Spawn.Aliases", "espawn");
            yaml.set("Command.Spawn.Usage", Collections.singletonList("{Prefix} §8/§bSpawn §8<§7Spieler§8/§7Set§8/§7Delete§8>"));
            yaml.set("Command.Spawn.Permission.Use", "system.spawn");
            yaml.set("Command.Spawn.Permission.NoDelay", "system.spawn.nodelay");
            yaml.set("Command.Spawn.Permission.Edit", "system.spawn.edit");
            yaml.set("Command.Spawn.Permission.Other", "system.spawn.other");
            yaml.set("Command.Spawn.TeleportWhileJoin", true);
            yaml.set("Command.Spawn.TeleportWhileRespawn", true);
            yaml.set("Command.Spawn.MessagePlayer", Collections.singletonList("{Prefix} §7Du wurdest zum §bSpawn §7teleportiert."));
            yaml.set("Command.Spawn.Message", Collections.singletonList("{Prefix} §a{Player} §7wird zum §bSpawn §7teleportiert."));
            yaml.set("Command.Spawn.CountIsOne", "einer");
            yaml.set("Command.Spawn.DelayedMessage", Collections.singletonList("{Prefix} §7Du wirst in §a{Seconds} §7Sekunde§8(§7n§8) §7teleportiert."));
            yaml.set("Command.Spawn.TeleportDelayInSeconds", 5);
            yaml.set("Command.Spawn.MessageMovedWhileTeleportation", Collections.singletonList("{Prefix} §cDer Teleportvorgang wurde abgebrochen!"));
            yaml.set("Command.Spawn.MessageSpawnSet", Collections.singletonList("{Prefix} §7Der §bSpawn §7wurde gesetzt§7."));
            yaml.set("Command.Spawn.MessageSpawnDelete", Collections.singletonList("{Prefix} §7Der §bSpawn §7wurde gelöscht§7."));
            yaml.set("Command.Spawn.MessageSpawnAlreadySet", Collections.singletonList("{Prefix} §cDer Spawn wurde bereits gesetzt!"));
            yaml.set("Command.Spawn.MessageSpawnNotSet", Collections.singletonList("{Prefix} §cDer Spawn wurde noch nicht gesetzt!"));
            yaml.set("Command.Spawn.MessageAlreadyInTeleport", Collections.singletonList("{Prefix} §cDu wirst bereits teleportiert!"));
            pathsChanged = true;
        }

        //Warp
        if (!isPathSet("Command.Warp.Enabled")) {
            yaml.set("Command.Warp.Enabled", true);
            yaml.set("Command.Warp.Aliases", "ewarp");
            yaml.set("Command.Warp.Usage", Collections.singletonList("{Prefix} §8/§aWarp §8<§7Warp§8/§7List§8> <§7set§8/§7remove§8/§7Spieler§8>"));
            yaml.set("Command.Warp.Permission.Use", "system.warp");
            yaml.set("Command.Warp.Permission.Teleport", "system.warp.{Warp}");
            yaml.set("Command.Warp.Permission.NoDelay", "system.warp.nodelay");
            yaml.set("Command.Warp.Permission.Edit", "system.warp.edit");
            yaml.set("Command.Warp.Permission.Other", "system.warp.other");
            yaml.set("Command.Warp.MessagePlayer", Collections.singletonList("{Prefix} §7Du wurdest zum §aWarp §8(§7{Warp}§8) §7teleportiert."));
            yaml.set("Command.Warp.Message", Collections.singletonList("{Prefix} {Player} §7wird zum §aWarp §8(§7{Warp}§8) §7teleportiert."));
            yaml.set("Command.Warp.CountIsOne", "einer");
            yaml.set("Command.Warp.DelayedMessage", Collections.singletonList("{Prefix} §7Du wirst in §a{Seconds} §7Sekunde§8(§7n§8) §7teleportiert."));
            yaml.set("Command.Warp.TeleportDelayInSeconds", 3);
            yaml.set("Command.Warp.MessageMovedWhileTeleportation", Collections.singletonList("{Prefix} §cDer Teleportvorgang wurde abgebrochen!"));
            yaml.set("Command.Warp.MessageWarpSet", Collections.singletonList("{Prefix} §7Der §aWarp §8(§7{Warp}§8) §7wurde gesetzt§7."));
            yaml.set("Command.Warp.MessageWarpDelete", Collections.singletonList("{Prefix} §7Der §aWarp §8(§7{Warp}§8) §7wurde gelöscht§7."));
            yaml.set("Command.Warp.MessageWarpAlreadySet", Collections.singletonList("{Prefix} §cDer Warp wurde bereits gesetzt!"));
            yaml.set("Command.Warp.MessageWarpNotSet", Collections.singletonList("{Prefix} §cDer Warp existiert nicht!"));
            yaml.set("Command.Warp.MessageAlreadyInTeleport", Collections.singletonList("{Prefix} §cDu wirst bereits teleportiert!"));
            yaml.set("Command.Warp.WarpListFormat", Arrays.asList("{Prefix} §7Alle verfügbaren Warp-Punkte:", "§8» §7{Warps}"));
            yaml.set("Command.Warp.MessageNoWarps", "{Prefix} §cEs gibt keine Warp-Punkte!");
            pathsChanged = true;
        }

        //List
        if (!isPathSet("Command.List.Enabled")) {
            yaml.set("Command.List.Enabled", true);
            yaml.set("Command.List.Aliases", "elist");
            yaml.set("Command.List.Usage", Collections.singletonList("{Prefix} §8/§alist §8<§7Role§8>"));
            yaml.set("Command.List.Permission.Use", "system.list");
            yaml.set("Command.List.Permission.All", "system.list.all");
            yaml.set("Command.List.Permission.Role", "system.list.{Role}");
            yaml.set("Command.List.EmptyLineBetweenRoles", true);
            yaml.set("Command.List.RoleNotExist", Collections.singletonList("{Prefix} §cDiese Rolle existiert nicht!"));
            yaml.set("Command.List.NoPlayerInRole", "§cIn dieser Rolle ist kein Spieler online!");
            yaml.set("Command.List.ListFormat", "{Player}§8, §7");
            yaml.set("Command.List.AllListFormat", Arrays.asList("{Prefix} Davon sind §a{Count} §7Spieler aus {Role-Suffix}{Role} §aonline§7:", "{Prefix} {PlayerInRole}"));
            yaml.set("Command.List.MessageAll", Arrays.asList("{Prefix} Es sind §a{Count} §7Spieler §aonline§7!", "{AllList}"));
            yaml.set("Command.List.MessageRole", Arrays.asList("{Prefix} Es sind §a{Count} §7Spieler der Rolle {Role-Suffix}{Role} §aonline§7!", "{Prefix} {PlayerInRole}"));
            pathsChanged = true;
        }

        //Authentication
        if (!isPathSet("Command.Authentication.Enabled")) {
            yaml.set("Command.Authentication.Enabled", true);
            yaml.set("Command.Authentication.Aliases", "auth");
            yaml.set("Command.Authentication.Usage", Collections.singletonList("{Prefix} §8/§bAuth §8<§7register§8/§7login§8/§7change§8> <§7Passwort§8>"));
            yaml.set("Command.Authentication.UsageWithPerms", Collections.singletonList("{Prefix} §8/§bAuth §8<§7Spieler§8/§7register§8/§7login§8> <§7reset§8/§7set§8/§7get§8> <§7Neues Passwort§8>"));
            yaml.set("Command.Authentication.Permission", "system.auth");
            yaml.set("Command.Authentication.CancelMove", true);
            yaml.set("Command.Authentication.BlindPlayer", true);
            yaml.set("Command.Authentication.MessageHaveToRegister", Arrays.asList("{Prefix} §7Bitte registriere dich, um Sicherheit für deinen Account zu gewähren", "{Prefix} §7Zum Registrieren nutze §8/§bAuth §7register §8<§7Passwort§8>"));
            yaml.set("Command.Authentication.MessageRegistered", Arrays.asList("{Prefix} §7Du hast dich §aerfolgreich §7registriert!", "{Prefix} §cBitte merke dir dein Passwort gut!"));
            yaml.set("Command.Authentication.MessageAlreadyRegistered", Collections.singletonList("{Prefix} §cDu hast dich bereits registriert!"));
            yaml.set("Command.Authentication.MessageHaveToLogin", Arrays.asList("{Prefix} §7Bitte logge dich ein um dich zu authentifizieren!", "{Prefix} §7Zum Einloggen nutze §8/§bAuth §7login §8<§7Passwort§8>", "{Prefix} §cSolltest du Probleme beim einloggen haben, wende dich bitte an den Support!"));
            yaml.set("Command.Authentication.MessageLoggedIn", Collections.singletonList("{Prefix} Du hast dich §aerfolgreich §7eingeloggt!"));
            yaml.set("Command.Authentication.MessageAlreadyLoggedIn", Collections.singletonList("{Prefix} §cDu hast dich bereits eingeloggt!"));
            yaml.set("Command.Authentication.MessagePasswordIncorrect", Collections.singletonList("{Prefix} §cDas angegebene Passwort stimmt nicht mit dem aus dem System überein!"));
            yaml.set("Command.Authentication.MessageChanged", Arrays.asList("{Prefix} Du hast dein Passwort geändert!", "{Prefix} §cBitte merke dir dein Passwort gut!"));
            yaml.set("Command.Authentication.MessageReseted", Collections.singletonList("{Prefix} §cDu hast das Passwort von {Player} §cgelöscht!"));
            yaml.set("Command.Authentication.MessageSet", Collections.singletonList("{Prefix} Du hast das Passwort von {Player} §7zu §a{Password} §7gesetzt!"));
            yaml.set("Command.Authentication.MessageGet", Collections.singletonList("{Prefix} Das Passwort von {Player} §7lautet: §a{Password}"));
            pathsChanged = true;
        }

        //Ban
        if (!isPathSet("Command.Ban.Enabled")) {
            yaml.set("Command.Ban.Enabled", true);
            yaml.set("Command.Ban.Aliases", "eban");
            yaml.set("Command.Ban.Usage", Collections.singletonList("{Prefix} §8/§cBan §8<§7Spieler§8/§7List§8> <§7Dauer §8(§71m§8)§8/§7Grund§8> <§7Grund§8>"));
            yaml.set("Command.Ban.Permission.Use", "system.ban");
            yaml.set("Command.Ban.Permission.IPAddress", "system.ban.ip");
            yaml.set("Command.Ban.Permission.Temporary", "system.ban.tempban");
            yaml.set("Command.Ban.Permission.Permanent", "system.ban.permanentban");
            yaml.set("Command.Ban.Permission.CanNotBan", "system.ban.noban");
            yaml.set("Command.Ban.Seconds", "§cSekunde§8(§cn§8)");
            yaml.set("Command.Ban.Minutes", "§cMinute§8(§cn§8)");
            yaml.set("Command.Ban.Hours", "§cStunde§8(§cn§8)");
            yaml.set("Command.Ban.Days", "§cTag§8(§ce§8)");
            yaml.set("Command.Ban.Weeks", "§cWoche§8(§cn§8)");
            yaml.set("Command.Ban.Months", "§cMonat§8(§ce§8)");
            yaml.set("Command.Ban.Years", "§cJahr§8(§ce§8)");
            yaml.set("Command.Ban.PlayerCanNotGetBanned", Collections.singletonList("{Prefix} §cDieser Spieler kann nicht gebannt werden."));
            yaml.set("Command.Ban.MessagePlayerAlreadyBanned", Collections.singletonList("{Prefix} §cDieser Spieler wurde bereits bestraft."));
            yaml.set("Command.Ban.MessageBannedTemporary", Arrays.asList("{Prefix} §8§m----------§8(§c§lBan§8)§8§m----------", "", "§8» §cGebannt §8┃ §c{Player}", "§8» §cGebannt von §8┃ §c{Banned-Of}", "§8» §cGrund §8┃ §c{Reason}", "§8» §cDauer §8┃ §c{Durate}", "§8» §cGebannt am §8┃ §c{Date-Of-Ban}", "§8» §cEntbannung am §8┃ §c{Date-Of-Ban-Ends}", "", "{Prefix} §8§m----------§8(§c§lBan§8)§8§m----------"));
            yaml.set("Command.Ban.MessageBannedPermanent", Arrays.asList("{Prefix} §8§m----------§8(§c§lBan§8)§8§m----------", "", "§8» §cGebannt §8┃ §c{Player}", "§8» §cGebannt von §8┃ §c{Banned-Of}", "§8» §cGrund §8┃ §c{Reason}", "§8» §cDurate §8┃ §cPermanent!", "§8» §cGebannt am §8┃ §c{Date-Of-Ban}", "", "{Prefix} §8§m----------§8(§c§lBan§8)§8§m----------"));
            yaml.set("Command.Ban.MessageBannedTemporaryPlayer", Collections.singletonList("{Prefix} §7Du hast {Player} §7für §c{Durate} §7gebannt!"));
            yaml.set("Command.Ban.MessageBannedPermanentPlayer", Collections.singletonList("{Prefix} §7Du hast {Player} §cPermanent §7gebannt!"));
            yaml.set("Command.Ban.KickMessageTemporary", "\n{Prefix} §cDu wurdest temporär gebannt!\n\n§8» §cGrund §8┃ §c{Reason}\n§8» §cDauer §8┃ §c{Durate}\n§8» §cDu wurdest am §l{Date-Of-Ban}§c gebannt!\n§8» §cDein Ban endet am §l{Date-Of-Ban-Ends}§c!\n\n§8» §cEntbannungsantrag §8┃ §cLink");
            yaml.set("Command.Ban.KickMessagePermanent", "\n{Prefix} §cDu wurdest permanent gebannt!\n\n§8» §cGrund §8┃ §c{Reason}\n§8» §cDauer §8┃ §cPermanent!\n§8» §cDu wurdest am §l{Date-Of-Ban}§c gebannt!\n\n§8» §cEntbannungsantrag §8┃ §cLink");
            yaml.set("Command.Ban.ListMessage", Arrays.asList("{Prefix} §7Es sind §c{Ban-Count} §7Spieler gebannt!", "§8» §c{Banned-Player}"));
            yaml.set("Command.Ban.NoPlayerBannedMessage", "{Prefix} §cEs sind keine Spieler gebannt!");
            yaml.set("Command.Ban.HoverMessageTemporary", "{Prefix} §cBestrafungsinformationen\n\n§8» §cGebannt von §8┃ §c{Banned-Of}\n§8» §cGrund §8┃ §c{Reason}\n§8» §cDauer §8┃ §c{Durate}\n§8» §cGebannt am §8┃ §c{Date-Of-Ban}\n§8» §cEntbannung §8┃ §c{Date-Of-Ban-Ends}");
            yaml.set("Command.Ban.HoverMessagePermanent", "{Prefix} §cBestrafungsinformationen\n\n§8» §cGebannt von §8┃ §c{Banned-Of}\n§8» §cGrund §8┃ §c{Reason}\n§8» §cDauer §8┃ §cPermanent!\n§8» §cGebannt am §8┃ §c{Date-Of-Ban}");
            yaml.set("Command.Ban.PlayerListConsoleTemporary", "{Prefix} §c{Banned-Player} §8(§7Von: §c{Banned-Of}§8/§7Grund: §c{Reason}§8/§7Dauer: §c{Durate}§8/§7Gebannt am: §c{Date-Of-Ban}§8/§7Ban endet am: §c{Date-Of-Ban-Ends}§8)");
            yaml.set("Command.Ban.PlayerListConsolePermanent", "{Prefix} §c{Banned-Player} §8(§7Von: §c{Banned-Of}§8/§7Grund: §c{Reason}§8/§7Gebannt am: §c{Date-Of-Ban}§8)");
            pathsChanged = true;
        }

        //BanIP
        if (!isPathSet("Command.BanIP.Enabled")) {
            yaml.set("Command.BanIP.Enabled", true);
            yaml.set("Command.BanIP.Aliases", "ebanip");
            yaml.set("Command.BanIP.Usage", Collections.singletonList("{Prefix} §8/§cBanIP §8<§7Spieler§8/§7IP§8/§7List§8> <§7Dauer §8(§71m§8)§8/§7Grund§8> <§7Grund§8>"));
            yaml.set("Command.BanIP.Permission.Use", "system.banip");
            yaml.set("Command.BanIP.Permission.Temporary", "system.banip.tempban");
            yaml.set("Command.BanIP.Permission.Permanent", "system.banip.permanentban");
            yaml.set("Command.BanIP.Seconds", "§cSekunde§8(§cn§8)");
            yaml.set("Command.BanIP.Minutes", "§cMinute§8(§cn§8)");
            yaml.set("Command.BanIP.Hours", "§cStunde§8(§cn§8)");
            yaml.set("Command.BanIP.Days", "§cTag§8(§ce§8)");
            yaml.set("Command.BanIP.Weeks", "§cWoche§8(§cn§8)");
            yaml.set("Command.BanIP.Months", "§cMonat§8(§ce§8)");
            yaml.set("Command.BanIP.Years", "§cJahr§8(§ce§8)");
            yaml.set("Command.BanIP.Blacklist", Collections.singletonList("IP's that could not get banned."));
            yaml.set("Command.MuteIP.MessageIPCanNotGetBanned", Collections.singletonList("{Prefix} §cDiese IP kann nicht gebannt werden."));
            yaml.set("Command.BanIP.MessageIPAlreadyBanned", Collections.singletonList("{Prefix} §cDiese IP wurde bereits bestraft."));
            yaml.set("Command.BanIP.MessageIPBannedTemporary", Arrays.asList("{Prefix} §8§m----------§8(§c§lBan§8)§8§m----------", "", "§8» §cGebannt §8┃ §c{IP}", "§8» §cGebannt von §8┃ §c{Banned-Of}", "§8» §cGrund §8┃ §c{Reason}", "§8» §cDauer §8┃ §c{Durate}", "§8» §cGebannt am §8┃ §c{Date-Of-Ban}", "§8» §cEntbannung am §8┃ §c{Date-Of-Ban-Ends}", "", "{Prefix} §8§m----------§8(§c§lBan§8)§8§m----------"));
            yaml.set("Command.BanIP.MessageIPBannedPermanent", Arrays.asList("{Prefix} §8§m----------§8(§c§lBan§8)§8§m----------", "", "§8» §cGebannt §8┃ §c{IP}", "§8» §cGebannt von §8┃ §c{Banned-Of}", "§8» §cGrund §8┃ §c{Reason}", "§8» §cDurate §8┃ §cPermanent!", "§8» §cGebannt am §8┃ §c{Date-Of-Ban}", "", "{Prefix} §8§m----------§8(§c§lBan§8)§8§m----------"));
            yaml.set("Command.BanIP.MessageIPBannedTemporaryPlayer", Collections.singletonList("{Prefix} §7Du hast die IP §c{IP} §7für §c{Durate} §7gebannt!"));
            yaml.set("Command.BanIP.MessageIPBannedPermanentPlayer", Collections.singletonList("{Prefix} §7Du hast die IP §c{IP} §cPermanent §7gebannt!"));
            yaml.set("Command.BanIP.KickMessageTemporary", "\n{Prefix} §cDu wurdest temporär gebannt!\n\n§8» §cGrund §8┃ §c{Reason}\n§8» §cDauer §8┃ §c{Durate}\n§8» §cDu wurdest am §l{Date-Of-Ban}§c gebannt!\n§8» §cDein Ban endet am §l{Date-Of-Ban-Ends}§c!\n\n§8» §cEntbannungsantrag §8┃ §cLink");
            yaml.set("Command.BanIP.KickMessagePermanent", "\n{Prefix} §cDu wurdest permanent gebannt!\n\n§8» §cGrund §8┃ §c{Reason}\n§8» §cDauer §8┃ §cPermanent!\n§8» §cDu wurdest am §l{Date-Of-Ban}§c gebannt!\n\n§8» §cEntbannungsantrag §8┃ §cLink");
            yaml.set("Command.BanIP.ListMessage", Arrays.asList("{Prefix} §7Es sind §c{Ban-Count} §7Spieler gebannt!", "§8» §c{Banned-IPs}"));
            yaml.set("Command.BanIP.NoIPBannedMessage", "{Prefix} §cEs sind keine IPs gebannt!");
            yaml.set("Command.BanIP.HoverMessageTemporary", "{Prefix} §cBestrafungsinformationen\n\n§8» §cGebannt von §8┃ §c{Banned-Of}\n§8» §cGrund §8┃ §c{Reason}\n§8» §cDauer §8┃ §c{Durate}\n§8» §cGebannt am §8┃ §c{Date-Of-Ban}\n§8» §cEntbannung §8┃ §c{Date-Of-Ban-Ends}");
            yaml.set("Command.BanIP.HoverMessagePermanent", "{Prefix} §cBestrafungsinformationen\n\n§8» §cGebannt von §8┃ §c{Banned-Of}\n§8» §cGrund §8┃ §c{Reason}\n§8» §cDauer §8┃ §cPermanent!\n§8» §cGebannt am §8┃ §c{Date-Of-Ban}");
            yaml.set("Command.BanIP.IPListConsoleTemporary", "{Prefix} §c{Banned-IP} §8(§7Von: §c{Banned-Of}§8/§7Grund: §c{Reason}§8/§7Dauer: §c{Durate}§8/§7Gebannt am: §c{Date-Of-Ban}§8/§7Ban endet am: §c{Date-Of-Ban-Ends}§8)");
            yaml.set("Command.BanIP.IPListConsolePermanent", "{Prefix} §c{Banned-IP} §8(§7Von: §c{Banned-Of}§8/§7Grund: §c{Reason}§8/§7Gebannt am: §c{Date-Of-Ban}§8)");
            pathsChanged = true;
        }

        //Unban
        if (!isPathSet("Command.UnBan.Enabled")) {
            yaml.set("Command.UnBan.Enabled", true);
            yaml.set("Command.UnBan.Aliases", "eunban");
            yaml.set("Command.UnBan.Usage", Collections.singletonList("{Prefix} §8/§cUnBan §8<§7Spieler§8/§7IP§8>"));
            yaml.set("Command.UnBan.Permission.Use", "system.unban");
            yaml.set("Command.UnBan.Permission.IP", "system.unban.ip");
            yaml.set("Command.UnBan.Message", Arrays.asList("{Prefix} §8§m----------§8(§c§lBan§8)§8§m----------", "", "{Prefix} §c{Player} §chat §c{Banned-Player} §centbannt!", "", "{Prefix} §8§m----------§8(§c§lBan§8)§8§m----------"));
            yaml.set("Command.UnBan.MessagePlayer", Collections.singletonList("{Prefix} §cDu hast {Player} §centbannt!"));
            yaml.set("Command.UnBan.MessagePlayerNotBanned", Collections.singletonList("{Prefix} §cDieser Spieler ist nicht gebannt!"));
            yaml.set("Command.UnBan.MessageIPNotBanned", Collections.singletonList("{Prefix} §cDiese IP ist nicht gebannt!"));
            pathsChanged = true;
        }

        //Mute
        if (!isPathSet("Command.Mute.Enabled")) {
            yaml.set("Command.Mute.Enabled", true);
            yaml.set("Command.Mute.Aliases", "emute");
            yaml.set("Command.Mute.Usage", Collections.singletonList("{Prefix} §8/§cMute §8<§7Spieler§8/§7List§8> <§7Dauer §8(§71m§8)§8/§7Grund§8> <§7Grund§8>"));
            yaml.set("Command.Mute.Permission.Use", "system.mute");
            yaml.set("Command.Mute.Permission.Temporary", "system.mute.tempmute");
            yaml.set("Command.Mute.Permission.Permanent", "system.mute.permanentmute");
            yaml.set("Command.Mute.Permission.CanNotMute", "system.mute.nomute");
            yaml.set("Command.Mute.Seconds", "§cSekunde§8(§cn§8)");
            yaml.set("Command.Mute.Minutes", "§cMinute§8(§cn§8)");
            yaml.set("Command.Mute.Hours", "§cStunde§8(§cn§8)");
            yaml.set("Command.Mute.Days", "§cTag§8(§ce§8)");
            yaml.set("Command.Mute.Weeks", "§cWoche§8(§cn§8)");
            yaml.set("Command.Mute.Months", "§cMonat§8(§ce§8)");
            yaml.set("Command.Mute.Years", "§cJahr§8(§ce§8)");
            yaml.set("Command.Mute.PlayerCanNotGetMuted", Collections.singletonList("{Prefix} §cDieser Spieler kann nicht gemuted werden."));
            yaml.set("Command.Mute.MessagePlayerAlreadyMuted", Collections.singletonList("{Prefix} §cDieser Spieler wurde bereits bestraft."));
            yaml.set("Command.Mute.MessageMutedTemporary", Arrays.asList("{Prefix} §8§m----------§8(§c§lMute§8)§8§m----------", "", "§8» §cGemuted §8┃ §c{Player}", "§8» §cGemuted von §8┃ §c{Muted-Of}", "§8» §cGrund §8┃ §c{Reason}", "§8» §cDauer §8┃ §c{Durate}", "§8» §cGemuted am §8┃ §c{Date-Of-Mute}", "§8» §cEntbannung am §8┃ §c{Date-Of-Mute-Ends}", "", "{Prefix} §8§m----------§8(§c§lMute§8)§8§m----------"));
            yaml.set("Command.Mute.MessageMutedPermanent", Arrays.asList("{Prefix} §8§m----------§8(§c§lMute§8)§8§m----------", "", "§8» §cGemuted §8┃ §c{Player}", "§8» §cGemuted von §8┃ §c{Muted-Of}", "§8» §cGrund §8┃ §c{Reason}", "§8» §cDauer §8┃ §cPermanent!", "§8» §cGemuted am §8┃ §c{Date-Of-Mute}", "", "{Prefix} §8§m----------§8(§c§lMute§8)§8§m----------"));
            yaml.set("Command.Mute.MessageMutedTemporaryPlayer", Collections.singletonList("{Prefix} §7Du hast §c{Player} §7für §c{Durate} §7gemuted!"));
            yaml.set("Command.Mute.MessageMutedPermanentPlayer", Collections.singletonList("{Prefix} §7Du hast §c{Player} §cPermanent §7gemuted!"));
            yaml.set("Command.Mute.ChatMessageTemporary", Arrays.asList("{Prefix} §cDu wurdest temporär gemuted!", "", "§8» §cGrund §8┃ §c{Reason}", "§8» §cDauer §8┃ §c{Durate}", "§8» §cDu wurdest am §l{Date-Of-Mute}§c gemuted!", "§8» §cDein Mute endet am §l{Date-Of-Mute-Ends}§c!", "", "§8» §cEntbannungsantrag §8┃ §cLink"));
            yaml.set("Command.Mute.ChatMessagePermanent", Arrays.asList("{Prefix} §cDu wurdest permanent gemuted!", "", "§8» §cGrund §8┃ §c{Reason}", "§8» §cDauer §8┃ §cPermanent", "§8» §cDu wurdest am §l{Date-Of-Mute}§c gemuted!", "", "§8» §cEntbannungsantrag §8┃ §cLink"));
            yaml.set("Command.Mute.ListMessage", Arrays.asList("{Prefix} §7Es sind §c{Mute-Count} §7Spieler gemuted!", "§8» §c{Muted-Player}"));
            yaml.set("Command.Mute.NoPlayerMutedMessage", "{Prefix} §cEs sind keine Spieler gemuted!");
            yaml.set("Command.Mute.HoverMessageTemporary", "{Prefix} §cBestrafungsinformationen\n\n§8» §cGemuted von §8┃ §c{Muted-Of}\n§8» §cGrund §8┃ §c{Reason}\n§8» §cDauer §8┃ §c{Durate}\n§8» §cgemuted am §8┃ §c{Date-Of-Mute}\n§8» §cEntbannung §8┃ §c{Date-Of-Mute-Ends}");
            yaml.set("Command.Mute.HoverMessagePermanent", "{Prefix} §cBestrafungsinformationen\n\n§8» §cGemuted von §8┃ §c{Muted-Of}\n§8» §cGrund §8┃ §c{Reason}\n§8» §cDauer §8┃ §cPermanent!\n§8» §cgemuted am §8┃ §c{Date-Of-Mute}");
            yaml.set("Command.Mute.PlayerListConsoleTemporary", "{Prefix} §c{Muted-Player} §8(§7Von: §c{Muted-Of}§8/§7Grund: §c{Reason}§8/§7Dauer: §c{Durate}§8/§7Gemuted am: §c{Date-Of-Mute}§8/§7Mute endet am: §c{Date-Of-Mute-Ends}§8)");
            yaml.set("Command.Mute.PlayerListConsolePermanent", "{Prefix} §c{Muted-Player} §8(§7Von: §c{Muted-Of}§8/§7Grund: §c{Reason}§8/§7Gemuted am: §c{Date-Of-Mute}§8)");
            pathsChanged = true;
        }

        //MuteIP
        if (!isPathSet("Command.MuteIP.Enabled")) {
            yaml.set("Command.MuteIP.Enabled", true);
            yaml.set("Command.MuteIP.Aliases", "emuteip");
            yaml.set("Command.MuteIP.Usage", Collections.singletonList("{Prefix} §8/§cMuteIP §8<§7Spieler§8/§7IP§8/§7List§8> <§7Dauer §8(§71m§8)§8/§7Grund§8> <§7Grund§8>"));
            yaml.set("Command.MuteIP.Permission.Use", "system.muteip");
            yaml.set("Command.MuteIP.Permission.Temporary", "system.muteip.tempmute");
            yaml.set("Command.MuteIP.Permission.Permanent", "system.muteip.permanentmute");
            yaml.set("Command.MuteIP.Seconds", "§cSekunde§8(§cn§8)");
            yaml.set("Command.MuteIP.Minutes", "§cMinute§8(§cn§8)");
            yaml.set("Command.MuteIP.Hours", "§cStunde§8(§cn§8)");
            yaml.set("Command.MuteIP.Days", "§cTag§8(§ce§8)");
            yaml.set("Command.MuteIP.Weeks", "§cWoche§8(§cn§8)");
            yaml.set("Command.MuteIP.Months", "§cMonat§8(§ce§8)");
            yaml.set("Command.MuteIP.Years", "§cJahr§8(§ce§8)");
            yaml.set("Command.BanIP.Blacklist", Collections.singletonList("IP's that could not get banned."));
            yaml.set("Command.MuteIP.MessageIPCanNotGetBanned", Collections.singletonList("{Prefix} §cDiese IP kann nicht gemuted werden."));
            yaml.set("Command.MuteIP.MessageIPAlreadyMuted", Collections.singletonList("{Prefix} §cDiese IP wurde bereits bestraft."));
            yaml.set("Command.MuteIP.MessageIPMutedTemporary", Arrays.asList("{Prefix} §8§m----------§8(§c§lMute§8)§8§m----------", "", "§8» §cGemuted §8┃ §c{IP}", "§8» §cGemuted von §8┃ §c{Muted-Of}", "§8» §cGrund §8┃ §c{Reason}", "§8» §cDauer §8┃ §c{Durate}", "§8» §cGemuted am §8┃ §c{Date-Of-Mute}", "§8» §cEntbannung am §8┃ §c{Date-Of-Mute-Ends}", "", "{Prefix} §8§m----------§8(§c§lMute§8)§8§m----------"));
            yaml.set("Command.MuteIP.MessageIPMutedPermanent", Arrays.asList("{Prefix} §8§m----------§8(§c§lMute§8)§8§m----------", "", "§8» §cGemuted §8┃ §c{IP}", "§8» §cGemuted von §8┃ §c{Muted-Of}", "§8» §cGrund §8┃ §c{Reason}", "§8» §cDauer §8┃ §cPermanent!", "§8» §cGemuted am §8┃ §c{Date-Of-Mute}", "", "{Prefix} §8§m----------§8(§c§lMute§8)§8§m----------"));
            yaml.set("Command.MuteIP.MessageIPMutedTemporaryPlayer", Collections.singletonList("{Prefix} §7Du hast die IP §c{IP} §7für §c{Durate} §7gemuted!"));
            yaml.set("Command.MuteIP.MessageIPMutedPermanentPlayer", Collections.singletonList("{Prefix} §7Du hast die IP §c{IP} §cPermanent §7gemuted!"));
            yaml.set("Command.MuteIP.ChatMessageTemporary", Arrays.asList("{Prefix} §cDu wurdest temporär gemuted!", "", "§8» §cGrund §8┃ §c{Reason}", "§8» §cDauer §8┃ §c{Durate}", "§8» §cDu wurdest am §l{Date-Of-Mute}§c gemuted!", "§8» §cDein Mute endet am §l{Date-Of-Mute-Ends}§c!", "", "§8» §cEntbannungsantrag §8┃ §cLink"));
            yaml.set("Command.MuteIP.ChatMessagePermanent", Arrays.asList("{Prefix} §cDu wurdest permanent gemuted!", "", "§8» §cGrund §8┃ §c{Reason}", "§8» §cDauer §8┃ §cPermanent", "§8» §cDu wurdest am §l{Date-Of-Mute}§c gemuted!", "", "§8» §cEntbannungsantrag §8┃ §cLink"));
            yaml.set("Command.MuteIP.ListMessage", Arrays.asList("{Prefix} §7Es sind §c{Mute-Count} §7Spieler gemuted!", "§8» §c{Muted-IPs}"));
            yaml.set("Command.MuteIP.NoIPMutedMessage", "{Prefix} §cEs sind keine IPs gemuted!");
            yaml.set("Command.MuteIP.HoverMessageTemporary", "{Prefix} §cBestrafungsinformationen\n\n§8» §cGemuted von §8┃ §c{Muted-Of}\n§8» §cGrund §8┃ §c{Reason}\n§8» §cDauer §8┃ §c{Durate}\n§8» §cgemuted am §8┃ §c{Date-Of-Mute}\n§8» §cEntbannung §8┃ §c{Date-Of-Mute-Ends}");
            yaml.set("Command.MuteIP.HoverMessagePermanent", "{Prefix} §cBestrafungsinformationen\n\n§8» §cGemuted von §8┃ §c{Muted-Of}\n§8» §cGrund §8┃ §c{Reason}\n§8» §cDauer §8┃ §cPermanent!\n§8» §cgemuted am §8┃ §c{Date-Of-Mute}");
            yaml.set("Command.MuteIP.IPListConsoleTemporary", "{Prefix} §c{Muted-IP} §8(§7Von: §c{Muted-Of}§8/§7Grund: §c{Reason}§8/§7Dauer: §c{Durate}§8/§7Gemuted am: §c{Date-Of-Mute}§8/§7Mute endet am: §c{Date-Of-Mute-Ends}§8)");
            yaml.set("Command.MuteIP.IPListConsolePermanent", "{Prefix} §c{Muted-IP} §8(§7Von: §c{Muted-Of}§8/§7Grund: §c{Reason}§8/§7Gemuted am: §c{Date-Of-Mute}§8)");
            pathsChanged = true;
        }

        //UnMute
        if (!isPathSet("Command.UnMute.Enabled")) {
            yaml.set("Command.UnMute.Enabled", true);
            yaml.set("Command.UnMute.Aliases", "eunmute");
            yaml.set("Command.UnMute.Usage", Collections.singletonList("{Prefix} §8/§cUnMute §8<§7Spieler§8>"));
            yaml.set("Command.UnMute.Permission.Use", "system.unMute");
            yaml.set("Command.UnMute.Permission.IP", "system.unMute.ip");
            yaml.set("Command.UnMute.Message", Arrays.asList("{Prefix} §8§m----------§8(§c§lMute§8)§8§m----------", "", "{Prefix} {Player} §chat {Muted-Player} §centmuted!", "", "{Prefix} §8§m----------§8(§c§lMute§8)§8§m----------"));
            yaml.set("Command.UnMute.MessagePlayer", Collections.singletonList("{Prefix} §cDu hast {Player} §centmuted!"));
            yaml.set("Command.UnMute.MessagePlayerNotMuted", Collections.singletonList("{Prefix} §cDieser Spieler ist nicht gemuted!"));
            yaml.set("Command.UnMute.MessageIPNotMuted", Collections.singletonList("{Prefix} §cDiese IP ist nicht gemuted!"));
            pathsChanged = true;
        }

        //Kick
        if (!isPathSet("Command.Kick.Enabled")) {
            yaml.set("Command.Kick.Enabled", true);
            yaml.set("Command.Kick.Aliases", "ekick");
            yaml.set("Command.Kick.Usage", Collections.singletonList("{Prefix} §8/§cKick §8<§7Spieler§8> <§7Grund§8>"));
            yaml.set("Command.Kick.Permission.Use", "system.kick");
            yaml.set("Command.Kick.Permission.CanNotGetKicked", "system.nokick");
            yaml.set("Command.Kick.PlayerCanNotGetKicked", Collections.singletonList("{Prefix} §cDieser Spieler kann nicht gekickt werden!"));
            yaml.set("Command.Kick.KickMessage", "\n{Prefix} §cDu wurdest gekickt!\n\n§8» §cGrund §8┃ §c{Reason}");
            yaml.set("Command.Kick.MessagePlayer", Collections.singletonList("{Prefix} §cDu hast {Player} §cgekickt!"));
            yaml.set("Command.Kick.Message", Arrays.asList("{Prefix} §8§m----------§8(§c§lMute§8)§8§m----------", "", "§8» §cGekickt §8┃ §c{Player}", "§8» §cGekickt von §8┃ §c{Kick-Of}", "§8» §cGrund §8┃ §c{Reason}", "", "{Prefix} §8§m----------§8(§c§lMute§8)§8§m----------"));
            pathsChanged = true;
        }

        //Warn
        if (!isPathSet("Command.Warn.Enabled")) {
            yaml.set("Command.Warn.Enabled", true);
            yaml.set("Command.Warn.Aliases", "ewarn");
            yaml.set("Command.Warn.Usage", Collections.singletonList("{Prefix} §8/§cWarn §8<§7Spieler§8> §8<§7Grund§8>"));
            yaml.set("Command.Warn.Permission", "system.warn");
            yaml.set("Command.Warn.Message", Collections.singletonList("{Prefix} Du hast den Spieler {Player} §7wegen §c{Reason} §7verwarnt!"));
            pathsChanged = true;
        }

        //ShowIP
        if (!isPathSet("Command.ShowIP.Enabled")) {
            yaml.set("Command.ShowIP.Enabled", true);
            yaml.set("Command.ShowIP.Aliases", "eshowip");
            yaml.set("Command.ShowIP.Usage", Collections.singletonList("{Prefix} §8/§cShowIP §8<§7Spieler§8>"));
            yaml.set("Command.ShowIP.Permission", "system.showip");
            yaml.set("Command.ShowIP.SuggestIP", true);
            yaml.set("Command.ShowIP.Message", Collections.singletonList("{Prefix} Die IP von {Player} §7lautet: §c{IP}"));
            pathsChanged = true;
        }

        //History
        if (!isPathSet("Command.History.Enabled")) {
            yaml.set("Command.History.Enabled", true);
            yaml.set("Command.History.Aliases", "ehistory");
            yaml.set("Command.History.Usage", Collections.singletonList("{Prefix} §8/§cHistory §8<§7Spieler§8/§7IP§8>"));
            yaml.set("Command.History.Permission", "system.history");
            yaml.set("Command.History.Message", Arrays.asList("{Prefix} §c{Player} §chat §c{Punishment-Count} Bestrafung§8(§cen§8)§c!", "", "{Prefix} §7Alle §cVerwarnungen§7:", "{Player-Warns}", "", "{Prefix} §7Alle §cBan§8-§cBestrafungen§7:", "{Player-Bans}", "", "{Prefix} §7Alle §cMute§8-§cBestrafungen§7:", "{Player-Mutes}", "", "{Prefix} §7Alle §cKick§8-§cBestrafungen§7:", "{Player-Kicks}", "", "{Prefix} §7Aktive Bestrafungen:", "{Active-Punishments}"));
            yaml.set("Command.History.MessageIP", Arrays.asList("{Prefix} §c{Player} §chat §c{Punishment-Count} Bestrafung§8(§cen§8)§c!", "", "{Prefix} §7Alle §cBan§8-§cBestrafungen§7:", "{Player-Bans}", "", "{Prefix} §7Alle §cMute§8-§cBestrafungen§7:", "{Player-Mutes}", "", "{Prefix} §7Aktive Bestrafungen:", "{Active-Punishments}"));
            yaml.set("Command.History.MessagePlayerWarns", "{Prefix} §c{Date-Of-Warn} §8(§c§l{Reason}§8)");
            yaml.set("Command.History.HoverMessageWarn", "{Prefix} §cVerwarnungsinformationen\n\n§8» §cGewarnt von §8┃ §c{Warned-Of}\n§8» §cGrund §8┃ §c{Reason}\n§8» §cGewarnt am §8┃ §c{Date-Of-Warn}");
            yaml.set("Command.History.MessagePlayerBans", "{Prefix} §c{Date-Of-Ban} §8(§c§l{Reason}§8)");
            yaml.set("Command.History.HoverMessageBanTemporary", "{Prefix} §cBestrafungsinformationen\n\n§8» §cGebannt von §8┃ §c{Banned-Of}\n§8» §cGrund §8┃ §c{Reason}\n§8» §cDauer §8┃ §c{Durate}\n§8» §cGebannt am §8┃ §c{Date-Of-Ban}\n§8» §cEntbannung §8┃ §c{Date-Of-Ban-Ends}\n§8» §cEntbannt von §8┃ §c{Unban-Of}");
            yaml.set("Command.History.HoverMessageBanPermanent", "{Prefix} §cBestrafungsinformationen\n\n§8» §cGebannt von §8┃ §c{Banned-Of}\n§8» §cGrund §8┃ §c{Reason}\n§8» §cDauer §8┃ §cPermanent\n§8» §cGebannt am §8┃ §c{Date-Of-Ban}\n§8» §cEntbannt von §8┃ §c{Unban-Of}");
            yaml.set("Command.History.MessagePlayerMutes", "{Prefix} §c{Date-Of-Mute} §8(§c§l{Reason}§8)");
            yaml.set("Command.History.HoverMessageMuteTemporary", "{Prefix} §cBestrafungsinformationen\n\n§8» §cGemuted von §8┃ §c{Muted-Of}\n§8» §cGrund §8┃ §c{Reason}\n§8» §cDauer §8┃ §c{Durate}\n§8» §cGemuted am §8┃ §c{Date-Of-Mute}\n§8» §cEntbannung §8┃ §c{Date-Of-Mute-Ends}\n§8» §cEntmuted von §8┃ §c{UnMute-Of}");
            yaml.set("Command.History.HoverMessageMutePermanent", "{Prefix} §cBestrafungsinformationen\n\n§8» §cGemuted von §8┃ §c{Muted-Of}\n§8» §cGrund §8┃ §c{Reason}\n§8» §cDauer §8┃ §cPermanent\n§8» §cGemuted am §8┃ §c{Date-Of-Mute}\n§8» §cEntmuted von §8┃ §c{UnMute-Of}");
            yaml.set("Command.History.MessagePlayerKicks", "{Prefix} §c{Date-Of-Kick} §8(§c§l{Reason}§8)");
            yaml.set("Command.History.HoverMessageKick", "{Prefix} §cBestrafungsinformationen\n\n§8» §cGekickt von §8┃ §c{Kicked-Of}\n§8» §cGrund §8┃ §c{Reason}\n§8» §cGekickt am §8┃ §c{Date-Of-Kick}");
            yaml.set("Command.History.MessageNoOneUnBanned", "§c-");
            yaml.set("Command.History.MessageNoPunishments", Collections.singletonList("{Prefix} §c{Player} §7ist noch kein mal bestraft worden!"));
            yaml.set("Command.History.MessageNoPunishmentsIP", Collections.singletonList("{Prefix} §7Die §cIP {IP} §7ist noch kein mal bestraft worden!"));
            yaml.set("Command.History.MessageNoPunishmentsOfThis", "{Prefix} §cKeine Strafen in dieser Kategorie.");
            yaml.set("Command.History.MessageWarnConsole", "{Prefix} §7Von: §c{Warned-Of} §7Grund: §c{Reason} §7Gewarnt am: §c{Date-Of-Warn}");
            yaml.set("Command.History.MessageBanTemporaryConsole", "{Prefix} §7Von: §c{Banned-Of} §7Grund: §c{Reason} §7Dauer: §c{Durate} §7Gebannt am: §c{Date-Of-Ban} §7Ban endet am: §c{Date-Of-Ban-Ends}");
            yaml.set("Command.History.MessageBanPermanentConsole", "{Prefix} §7Von: §c{Banned-Of} §7Grund: §c{Reason} §7Gebannt am: §c{Date-Of-Ban}");
            yaml.set("Command.History.MessageMuteTemporaryConsole", "{Prefix} §7Von: §c{Muted-Of} §7Grund: §c{Reason} §7Dauer: §c{Durate} §7Gemuted am: §c{Date-Of-Mute} §7Mute endet am: §c{Date-Of-Mute-Ends}");
            yaml.set("Command.History.MessageMutePermanentConsole", "{Prefix} §7Von: §c{Muted-Of} §7Grund: §c{Reason} §7Gemuted am: §c{Date-Of-Mute}");
            yaml.set("Command.History.MessageKickConsole", "{Prefix} §7Von: §c{Kicked-Of} §7Grund: §c{Reason} §7Gekickt am: §c{Date-Of-Kick}");
            pathsChanged = true;
        }

        //Maintenance
        if (!isPathSet("Command.Maintenance.Enabled")) {
            yaml.set("Command.Maintenance.Enabled", true);
            yaml.set("Command.Maintenance.Aliases", "emaintenance,wartung");
            yaml.set("Command.Maintenance.Usage", Collections.singletonList("{Prefix} §8/§cWartung §8<§7add§8/§7remove§8/§7list§8/§7on§8/§7off§8> <§7Spieler§8/§7role§8>"));
            yaml.set("Command.Maintenance.Permission.Use", "system.wartung");
            yaml.set("Command.Maintenance.Permission.Player", "system.wartung.player");
            yaml.set("Command.Maintenance.Permission.Status", "system.wartung.status");
            yaml.set("Command.Maintenance.Permission.Role", "system.wartung.role");
            yaml.set("Command.Maintenance.MessageAddPlayer", Collections.singletonList("{Prefix} Der Spieler §a{Player} §7wurde hinzugefügt."));
            yaml.set("Command.Maintenance.MessagePlayerAlreadyAdded", Collections.singletonList("{Prefix} §cDieser Spieler ist bereits hinzugefügt!"));
            yaml.set("Command.Maintenance.MessageRemovePlayer", Collections.singletonList("{Prefix} Der Spieler §a{Player} §7wurde entfernt."));
            yaml.set("Command.Maintenance.MessagePlayerAlreadyRemoved", Collections.singletonList("{Prefix} §cDieser Spieler steht nicht auf der Liste!"));
            yaml.set("Command.Maintenance.MessageListPlayer", Arrays.asList("{Prefix} Es sind §a{Count} §7Spieler hinzugefügt.", "{AddedPlayer}"));
            yaml.set("Command.Maintenance.MessageNoPlayersAdded", "{Prefix} §cEs sind keine Spieler hinzugefügt");
            yaml.set("Command.Maintenance.ListPlayerFormat", "§8» §7{Player}");
            yaml.set("Command.Maintenance.ListPlayerHover", "§8» §7{Player}\n§8» §7hinzugefügt von §8┃ §a{AddedOf}\n§8» §7hinzugefügt am §8┃ §a{AddedDate}\n\n§8(§alinksklick§8) §cKlicke, um den Spieler zu entfernen.");
            yaml.set("Command.Maintenance.ListPlayerConsole", "§8» §7{Player} §7hinzugefügt von: §a{AddedOf} §7hinzugefügt am: §a{AddedDate}");
            yaml.set("Command.Maintenance.MessageAddRole", Collections.singletonList("{Prefix} Die Rolle §a{Suffix}{Role} §7wurde hinzugefügt."));
            yaml.set("Command.Maintenance.MessageRoleAlreadyAdded", Collections.singletonList("{Prefix} §cDiese Rolle ist bereits hinzugefügt!"));
            yaml.set("Command.Maintenance.MessageRemoveRole", Collections.singletonList("{Prefix} Die Rolle §a{Suffix}{Role} §7wurde entfernt."));
            yaml.set("Command.Maintenance.MessageRoleAlreadyRemoved", Collections.singletonList("{Prefix} §cDiese Rolle steht nicht auf der Liste!"));
            yaml.set("Command.Maintenance.MessageListRoles", Arrays.asList("{Prefix} Es sind §a{Count} §7Rollen hinzugefügt.", "{AddedRoles}"));
            yaml.set("Command.Maintenance.MessageNoRolesAdded", "{Prefix} §cEs sind keine Rollen hinzugefügt");
            yaml.set("Command.Maintenance.ListRoleFormat", "§8» §7{Suffix}{Role}");
            yaml.set("Command.Maintenance.ListRoleHover", "§8» §7Rolle: {Suffix}{Role}\n§8» §7hinzugefügt von §8┃ §a{AddedOf}\n§8» §7hinzugefügt am §8┃ §a{AddedDate}\n\n§8(§alinksklick§8) §cKlicke, um die Rolle zu entfernen.");
            yaml.set("Command.Maintenance.ListRoleConsole", "§8» §7Rolle: {Suffix}{Role} §7hinzugefügt von: §a{AddedOf} §7hinzugefügt am: §a{AddedDate}");
            yaml.set("Command.Maintenance.MessageEnableMaintenance", Collections.singletonList("{Prefix} §cDie Wartungsarbeiten wurden aktiviert!"));
            yaml.set("Command.Maintenance.MessageDisableMaintenance", Collections.singletonList("{Prefix} §cDie Wartungsarbeiten wurden deaktiviert!"));
            yaml.set("Command.Maintenance.MessageMaintenanceAlreadyEnabled", Collections.singletonList("{Prefix} §cDie Wartungsarbeiten sind bereits aktiviert!"));
            yaml.set("Command.Maintenance.MessageMaintenanceAlreadyDisabled", Collections.singletonList("{Prefix} §cDie Wartungsarbeiten sind nicht aktiviert!"));
            yaml.set("Command.Maintenance.MessageNotAdded", "\n{Prefix} §cDu wurdest gekickt!\n\n§8» §cGrund §8┃ §cWartungsarbeiten\n§8» §cRelease §8┃ §cUnbekannt!\n\n§8» §9Discord §8┃ §9https://discord.gg/seMwEpjUkD \n");
            yaml.set("Command.Maintenance.MessageIfPlayerTryJoin", true);
            yaml.set("Command.Maintenance.MessagePlayerTryJoin", Collections.singletonList("{Prefix} {Player} §7versuchte, den Server zu betreten."));
            yaml.set("Command.Maintenance.ChangeMOTD", true);
            yaml.set("Command.Maintenance.MOTD", "{Prefix} §7Netzwerk §8(§a1.8§8)\n§8» §7Dieser Server ist im §c§l§nWARTUNGSMODUS§7.");
            pathsChanged = true;
        }

        //CommandSpy
        if (!isPathSet("Command.CommandSpy.Enabled")) {
            yaml.set("Command.CommandSpy.Enabled", true);
            yaml.set("Command.CommandSpy.Aliases", "cmdspy,ecommandspy,ecmdspy");
            yaml.set("Command.CommandSpy.Usage", Collections.singletonList("{Prefix} §8/§cCommandSpy §8<§aSpieler§8>"));
            yaml.set("Command.CommandSpy.Permission.Use", "system.cmdspy");
            yaml.set("Command.CommandSpy.Permission.Other", "system.cmdspy.other");
            yaml.set("Command.CommandSpy.Activated", "§aaktiviert");
            yaml.set("Command.CommandSpy.Deactivated", "§cdeaktiviert");
            yaml.set("Command.CommandSpy.MessagePlayer", Collections.singletonList("{Prefix} Dein §eCmdSpy §7wurde {CommandSpyMode}§7!"));
            yaml.set("Command.CommandSpy.Message", Collections.singletonList("{Prefix} Der §eCmdSpy §7von §a{Player} §7wurde {CommandSpyMode}§7!"));
            yaml.set("Command.CommandSpy.ShowConsoleExecutingCommands", true);
            yaml.set("Command.CommandSpy.Format", Collections.singletonList("{Prefix} §8[§cCMDSPY§8] §a{Player} §8» §a{Command}"));
            pathsChanged = true;
        }

        //GlobalMute
        if (!isPathSet("Command.GlobalMute.Enabled")) {
            yaml.set("Command.GlobalMute.Enabled", true);
            yaml.set("Command.GlobalMute.Aliases", "eglobalmute");
            yaml.set("Command.GlobalMute.Usage", Collections.singletonList("{Prefix} §8/§cGlobalMute"));
            yaml.set("Command.GlobalMute.Permission.Use", "system.globalmute.use");
            yaml.set("Command.GlobalMute.Permission.ByPass", "system.globalmute.ignore");
            yaml.set("Command.GlobalMute.Status.Activated", "§aaktiviert");
            yaml.set("Command.GlobalMute.Status.Deactivated", "§cdeaktiviert");
            yaml.set("Command.GlobalMute.Message", Collections.singletonList("{Prefix} Du hast den GlobalMute {Status}§7!"));
            yaml.set("Command.GlobalMute.GlobalMessage", true);
            yaml.set("Command.GlobalMute.MessageGlobal", Collections.singletonList("{Prefix} Der GlobalMute wurde {Status}§7!"));
            yaml.set("Command.GlobalMute.MessageMute", Collections.singletonList("{Prefix} §cDerzeitig ist der GlobalMute aktiviert!"));
            pathsChanged = true;
        }

        //Clearchat
        if (!isPathSet("Command.ClearChat.Enabled")) {
            yaml.set("Command.ClearChat.Enabled", true);
            yaml.set("Command.ClearChat.Aliases", "cc,ecc,eclearchat");
            yaml.set("Command.ClearChat.Usage", Collections.singletonList("{Prefix} §8/§cClearChat"));
            yaml.set("Command.ClearChat.Permission", "system.clearchat");
            yaml.set("Command.ClearChat.EmptyLines", 200);
            yaml.set("Command.ClearChat.Message", Collections.singletonList("{Prefix} §7Der §aChat §7wurde von §a{Player} §7geleert!"));
            pathsChanged = true;
        }

        //Broadcast
        if (!isPathSet("Command.Broadcast.Enabled")) {
            yaml.set("Command.Broadcast.Enabled", true);
            yaml.set("Command.Broadcast.Aliases", "bc,ebc,ebroadcast");
            yaml.set("Command.Broadcast.Usage", Collections.singletonList("{Prefix} §8/§cBroadcast §8<§cNachricht§8>"));
            yaml.set("Command.Broadcast.Permission", "system.broadcast");
            yaml.set("Command.Broadcast.Message", Arrays.asList("§8§m---------------------§8(§c§lBroadcast§8)§8§m---------------------", "", "§8» §7{Message}", "", "§8§m---------------------§8(§c§lBroadcast§8)§8§m---------------------"));
            pathsChanged = true;
        }

        //Sudo
        if (!isPathSet("Command.Sudo.Enabled")) {
            yaml.set("Command.Sudo.Enabled", true);
            yaml.set("Command.Sudo.Aliases", "esudo");
            yaml.set("Command.Sudo.Usage", Collections.singletonList("{Prefix} §8/§cSudo §8<§7Spieler§8> §8<§7/Befehl§8/§7Nachricht§8>"));
            yaml.set("Command.Sudo.Permission.Use", "system.sudo");
            yaml.set("Command.Sudo.Permission.Command", "system.sudo.command");
            yaml.set("Command.Sudo.Permission.Message", "system.sudo.message");
            yaml.set("Command.Sudo.Message", Arrays.asList("{Prefix} Die Handlung wird nun von §a{Player} §7ausgeführt.", "{Prefix} §7Handlung: {Action}"));
            pathsChanged = true;
        }

        //TeamChat
        if (!isPathSet("Command.TeamChat.Enabled")) {
            yaml.set("Command.TeamChat.Enabled", true);
            yaml.set("Command.TeamChat.Aliases", "tc,eteamchat");
            yaml.set("Command.TeamChat.Usage", Collections.singletonList("{Prefix} §8/§cTeamchat §8<§7Nachricht§8>"));
            yaml.set("Command.TeamChat.Permission", "system.teamchat");
            yaml.set("Command.TeamChat.ColorCodes", true);
            yaml.set("Command.TeamChat.Format", Collections.singletonList("{Prefix} §8[§cTC§8] §a{Player} §8» §7{Message}"));
            pathsChanged = true;
        }

        //BuildChat
        if (!isPathSet("Command.BuildChat.Enabled")) {
            yaml.set("Command.BuildChat.Enabled", true);
            yaml.set("Command.BuildChat.Aliases", "buildchat,ebuildchat");
            yaml.set("Command.BuildChat.Usage", Collections.singletonList("{Prefix} §8/§eBuildchat §8<§7Nachricht§8>"));
            yaml.set("Command.BuildChat.Permission", "system.buildchat");
            yaml.set("Command.BuildChat.ColorCodes", true);
            yaml.set("Command.BuildChat.Format", Collections.singletonList("{Prefix} §8[§eBC§8] §a{Player} §8» §7{Message}"));
            pathsChanged = true;
        }

        //AdminChat
        if (!isPathSet("Command.AdminChat.Enabled")) {
            yaml.set("Command.AdminChat.Enabled", true);
            yaml.set("Command.AdminChat.Aliases", "ac,eadminchat");
            yaml.set("Command.AdminChat.Usage", Collections.singletonList("{Prefix} §8/§4Adminchat §8<§7Nachricht§8>"));
            yaml.set("Command.AdminChat.Permission", "system.adminchat");
            yaml.set("Command.AdminChat.ColorCodes", true);
            yaml.set("Command.AdminChat.Format", Collections.singletonList("{Prefix} §8[§4AC§8] §a{Player} §8» §7{Message}"));
            pathsChanged = true;
        }

        //Support
        if (!isPathSet("Command.Support.Enabled")) {
            yaml.set("Command.Support.Enabled", true);
            yaml.set("Command.Support.Aliases", "esupport");
            yaml.set("Command.Support.Usage", Collections.singletonList("{Prefix} §8/§9Support"));
            yaml.set("Command.Support.UsageWithPermission", Collections.singletonList("{Prefix} §8/§9Support §8<§7Spieler§8/§7List§8/§7Spy§8> <§7accept§8/§7close§8>"));
            yaml.set("Command.Support.Permission.Use", "system.support");
            yaml.set("Command.Support.Permission.Spy", "system.support.spy");
            yaml.set("Command.Support.SpyActivate", "§aaktiviert");
            yaml.set("Command.Support.SpyDeactivate", "§cdeaktiviert");
            yaml.set("Command.Support.MessageToggleSpy", Collections.singletonList("{Prefix} §7Du hast §9Spy §7in §9Support §7Tickets {Status}§7!"));
            yaml.set("Command.Support.DelayForAutoDeleteInMinutes", 5);
            yaml.set("Command.Support.Message", Arrays.asList("{Prefix} §7Du hast ein §9Support §7Ticket erstellt.", "{Prefix} Bitte habe etwas Geduld, es wird sich in kürze jemand um Dich kümmern."));
            yaml.set("Command.Support.MessageSupporterCreated", Arrays.asList("{Prefix} §8§m----------§8(§9§lSupport§8)§8§m----------", "", "{Prefix} §9Support §7Ticket von §8┃ {SupportTicketOf}", "", "{Prefix} §9Status §8┃ {Status}", "", "{Accept-Support}", "", "{Prefix} §8§m----------§8(§9§lSupport§8)§8§m----------"));
            yaml.set("Command.Support.MessageSupporterEdit", Arrays.asList("{Prefix} §8§m----------§8(§9§lSupport§8)§8§m----------", "", "{Prefix} §9Support §7Ticket von §8┃ {SupportTicketOf}", "", "{Prefix} §9Supporter §8┃ {Supporter}", "{Prefix} §9Status §8┃ {Status}", "", "{Close-Support}", "", "{Prefix} §8§m----------§8(§9§lSupport§8)§8§m----------"));
            yaml.set("Command.Support.MessageSupporterClosed", Arrays.asList("{Prefix} §8§m----------§8(§9§lSupport§8)§8§m----------", "", "{Prefix} §9Support §7Ticket von §8┃ {SupportTicketOf}", "", "{Prefix} §9Supporter §8┃ {Supporter}", "{Prefix} §9Status §8┃ {Status}", "", "{Prefix} §8§m----------§8(§9§lSupport§8)§8§m----------"));
            yaml.set("Command.Support.NoSupporter", "§c-");
            yaml.set("Command.Support.SupportChatFormatToPlayer", "§8» §9Support §8┃§7 {Supporter} §8» {Player} §8» §7{Message}");
            yaml.set("Command.Support.SupportChatFormatToSupporter", "§8» §9Support §8┃§7 {Player} §8» {Supporter} §8» §7{Message}");
            yaml.set("Command.Support.MessageSupporterAlreadyInTicket", Collections.singletonList("{Prefix} §cDu bearbeitest zur Zeit bereits ein Support Ticket!"));
            yaml.set("Command.Support.StatusCreated", "§eerstellt!");
            yaml.set("Command.Support.StatusEdit", "§eIn bearbeitung!");
            yaml.set("Command.Support.StatusClosed", "§aErledigt!");
            yaml.set("Command.Support.changeStatus", Arrays.asList("{Prefix} §7Der Status deines §9Support §7Tickets wurde geändert!", "{Prefix} Neuer Status: {Status}"));
            yaml.set("Command.Support.MessageSupportAccepted", Collections.singletonList("{Prefix} §7Du hast das §9Support §7Ticket von §a{Player} §7angenommen."));
            yaml.set("Command.Support.MessageSupportClosed", Collections.singletonList("{Prefix} §7Du hast das §9Support §7Ticket von §a{Player} §7gelöscht."));
            yaml.set("Command.Support.ListMessage", Arrays.asList("{Prefix} §7Es gibt §9{Support-Count} §7offene §9Support §7Tickets!", "{SupportTickets}"));
            yaml.set("Command.Support.SupportFormat", "§8» §9Support Ticket §7von §8» {SupportTicketOf} §8┃ §7Status §8» {Status}");
            yaml.set("Command.Support.HoverMessageAcceptSupport", "{Prefix} §7Klicke, um das §9Support §7Ticket §aanzunehmen§7.");
            yaml.set("Command.Support.HoverMessageCloseSupport", "{Prefix} §7Klicke, um das §9Support §7Ticket §czu schließen§7.");
            yaml.set("Command.Support.MessageSupportAlreadyAccepted", Collections.singletonList("{Prefix} §cDieses Support Ticket wird bereits bearbeitet!"));
            yaml.set("Command.Support.MessageSupportAlreadyCreated", Collections.singletonList("{Prefix} §cDu hast bereits ein Support Ticket erstellt!"));
            yaml.set("Command.Support.MessageTicketDoNotExist", Collections.singletonList("{Prefix} §cDieser Spieler hat kein Support Ticket erstellt!"));
            pathsChanged = true;
        }

        //SupportMessage
        if (!isPathSet("Command.SupportMessage.Enabled")) {
            yaml.set("Command.SupportMessage.Enabled", true);
            yaml.set("Command.SupportMessage.Aliases", "supmsg,esupmsg,esupportmessage");
            yaml.set("Command.SupportMessage.Usage", Collections.singletonList("{Prefix} §8/§9SupMSG §8<§7list§8/§7template§8/§7customText§8/§7clear§8/§7send§8> <§7Spieler§8>"));
            yaml.set("Command.SupportMessage.Permission", "system.supportmessage");
            yaml.set("Command.SupportMessage.HoverTemplate", true);
            yaml.set("Command.SupportMessage.HoverMessage", "§7{Template}");
            yaml.set("Command.SupportMessage.MessageCantSendToYourSelf", Collections.singletonList("{Prefix} §cDu kannst dir selbst keine Support-Nachricht senden!"));
            yaml.set("Command.SupportMessage.Templates.$Test", "§8» §9SupMSG §8┃ §7This is a test template!");
            yaml.set("Command.SupportMessage.MessageCurrentMessage", Arrays.asList("{Prefix} Deine bisherige Support-Nachricht:", "{SupportMessage}"));
            yaml.set("Command.SupportMessage.MessageDeleted", Collections.singletonList("{Prefix} §cDu hast deine Support-Nachricht gelöscht!"));
            yaml.set("Command.SupportMessage.MessageNoTemplateExist", "{Prefix} §cEs gibt keine erstellten Vorlagen!");
            yaml.set("Command.SupportMessage.TemplateFormat", "{Template}§8, §7");
            yaml.set("Command.SupportMessage.MessageList", Arrays.asList("{Prefix} Hier siehst du alle erstellten Vorlagen:", "{Prefix} {Templates}"));
            yaml.set("Command.SupportMessage.Message", Arrays.asList("{Prefix} §7Du hast eine §9Support§7-§9Nachricht §7erhalten!", "{SupportMessage}"));
            yaml.set("Command.SupportMessage.MessagePlayer", Arrays.asList("{Prefix} §7Du hast eine §9Support§7-§9Nachricht §7an {Player} §7versendet!", "{SupportMessage}"));
            pathsChanged = true;
        }

        //Report
        if (!isPathSet("Command.Report.Enabled")) {
            yaml.set("Command.Report.Enabled", true);
            yaml.set("Command.Report.Aliases", "ereport");
            yaml.set("Command.Report.Usage", Collections.singletonList("{Prefix} §8/§cReport §8<§7Spieler§8> <§7Verbrochene Regel§8>"));
            yaml.set("Command.Report.UsageWithPermission", Collections.singletonList("{Prefix} §8/§cReport §8<§7Spieler§8/§7List§8> <§7accept§8/§7close§8>"));
            yaml.set("Command.Report.Permission", "system.report");
            yaml.set("Command.Report.EnableVanishOnAccept", true);
            yaml.set("Command.Report.DelayForAutoDeleteInMinutes", 5);
            yaml.set("Command.Report.Message", Arrays.asList("{Prefix} §7Deine §cReport-Meldung §7wurde erstellt. Vielen Dank!", "{Prefix} Bitte habe etwas geduld."));
            yaml.set("Command.Report.MessageSupporterCreated", Arrays.asList("{Prefix} §8§m----------§8(§c§lReport§8)§8§m----------", "", "{Prefix} §cReport von §8┃ {ReportOf}", "{Prefix} §cReported wurde §8┃ {Player}", "{Prefix} §cReport Grund §8┃ §c{Reason}", "", "{Prefix} §cStatus §8┃ {Status}", "", "{Accept-Report}", "", "{Prefix} §8§m----------§8(§c§lReport§8)§8§m----------"));
            yaml.set("Command.Report.MessageSupporterEdit", Arrays.asList("{Prefix} §8§m----------§8(§c§lReport§8)§8§m----------", "", "{Prefix} §cReport von §8┃ {ReportOf}", "{Prefix} §cReported wurde §8┃ {Player}", "{Prefix} §cReport Grund §8┃ §c{Reason}", "", "{Prefix} §cSupporter §8┃ {Supporter}", "{Prefix} §cStatus §8┃ {Status}", "", "{Close-Report}", "", "{Prefix} §8§m----------§8(§c§lReport§8)§8§m----------"));
            yaml.set("Command.Report.MessageSupporterClosed", Arrays.asList("{Prefix} §8§m----------§8(§c§lReport§8)§8§m----------", "", "{Prefix} §cReport von §8┃ {ReportOf}", "{Prefix} §cReported wurde §8┃ {Player}", "{Prefix} §cReport Grund §8┃ §c{Reason}", "", "{Prefix} §cSupporter §8┃ {Supporter}", "{Prefix} §cStatus §8┃ {Status}", "", "{Prefix} §8§m----------§8(§c§lReport§8)§8§m----------"));
            yaml.set("Command.Report.NoSupporter", "§c-");
            yaml.set("Command.Report.MessageSupporterAlreadyInReport", Collections.singletonList("{Prefix} §cDu bearbeitest zur Zeit bereits eine Report-Meldung!"));
            yaml.set("Command.Report.StatusCreated", "§eerstellt!");
            yaml.set("Command.Report.StatusEdit", "§eIn bearbeitung!");
            yaml.set("Command.Report.StatusClosed", "§aErledigt!");
            yaml.set("Command.Report.changeStatus", Arrays.asList("{Prefix} §7Der Status deiner §cReport-Meldung §7wurde geändert!", "{Prefix} Neuer Status: {Status}"));
            yaml.set("Command.Report.MessageReportAccepted", Collections.singletonList("{Prefix} §7Du hast die §cReport-Meldung §7von §a{Player} §7angenommen."));
            yaml.set("Command.Report.MessageReportClosed", Collections.singletonList("{Prefix} §7Du hast die §cReport-Meldung §7von §a{Player} §7gelöscht."));
            yaml.set("Command.Report.ListMessage", Arrays.asList("{Prefix} §7Es gibt {Report-Count} §cReport-Meldungen§7!", "{Reports}"));
            yaml.set("Command.Report.ReportFormat", "§8┃ §cVerdächtig §8» {ReportTo} §8┃ §cGrund §8» §c{Reason} §8┃ §cStatus §8» {Status}");
            yaml.set("Command.Report.HoverMessageAcceptReport", "{Prefix} §7Klicke, um den §cReport §aanzunehmen§7.");
            yaml.set("Command.Report.HoverMessageCloseReport", "{Prefix} §7Klicke, um den §cReport §czu schließen§7.");
            yaml.set("Command.Report.MessageReportAlreadyAccepted", Collections.singletonList("{Prefix} §cDiese Report-Meldung wird bereits bearbeitet!"));
            yaml.set("Command.Report.MessageReportAlreadyCreated", Collections.singletonList("{Prefix} §cDu hast bereits eine Report-Meldung erstellt!"));
            yaml.set("Command.Report.MessageReportDoNotExist", Collections.singletonList("{Prefix} §cDieser Spieler hat keine Report-Meldung erstellt!"));
            yaml.set("Command.Report.MessageSelfReport", Collections.singletonList("{Prefix} §cDu kannst dich nicht selbst melden!"));
            pathsChanged = true;
        }

        //Stats
        if (!isPathSet("Command.Stats.Enabled")) {
            yaml.set("Command.Stats.Enabled", true);
            yaml.set("Command.Stats.Aliases", "estats");
            yaml.set("Command.Stats.Usage", Collections.singletonList("{Prefix} §8/§cStats §8<§7Spieler§8>"));
            yaml.set("Command.Stats.Permission.Use", "system.stats");
            yaml.set("Command.Stats.Permission.Other", "system.stats.other");
            yaml.set("Command.Stats.ShowSelf", "§aDir");
            yaml.set("Command.Stats.Message", Arrays.asList("{Prefix} §8§m----------§8(§c§lSTATS§8)§8§m----------", "", "{Prefix} §7Die Stats von {Player}§7.", "", "{Prefix} §7Kills §8● §a{Kills}", "{Prefix} §7Tode §8● §c{Deaths}", "{Prefix} §7K/D §8● §c{K/D}", "", "{Prefix} §7Spielzeit §8● §a{PlayTime}", "", "{Prefix} §8§m----------§8(§c§lSTATS§8)§8§m----------"));
            pathsChanged = true;
        }

        //Role
        if (!isPathSet("Command.Role.Enabled")) {
            yaml.set("Command.Role.Enabled", true);
            yaml.set("Command.Role.Aliases", "erole");
            yaml.set("Command.Role.Usage", Collections.singletonList("{Prefix} §8/§cRole §8<§7Spieler§8/§7List§8> <§7Role§8/§7Zeit §8(§7z.B. 1s§8)§8> <§7Role§8>"));
            yaml.set("Command.Role.Permission.Use", "system.role");
            yaml.set("Command.Role.Permission.Role", "system.role.{Role}");
            yaml.set("Command.Role.Format", "{Role-Suffix}{Role} §8┃§7 ");
            yaml.set("Command.Role.Seconds", "§cSekunde§8(§cn§8)");
            yaml.set("Command.Role.Minutes", "§cMinute§8(§cn§8)");
            yaml.set("Command.Role.Hours", "§cStunde§8(§cn§8)");
            yaml.set("Command.Role.Days", "§cTag§8(§ce§8)");
            yaml.set("Command.Role.Weeks", "§cWoche§8(§cn§8)");
            yaml.set("Command.Role.Months", "§cMonat§8(§ce§8)");
            yaml.set("Command.Role.Years", "§cJahr§8(§ce§8)");
            yaml.set("Command.Role.MessageTemporary", Arrays.asList("", "{Prefix} Du hast die temporäre Rolle {Role-Suffix}{Role} §7erhalten.", "{Prefix} Diese Rolle besitzt du §c{Durate}§7!", ""));
            yaml.set("Command.Role.MessageTemporaryExpired", Arrays.asList("", "{Prefix} §cDeine temporäre Rolle ist abgelaufen!", "{Prefix} Deine temporäre Rolle war {Latest-Role-Suffix}{Latest-Role}§7!", "{Prefix} Deine neue Rolle ist {Role-Suffix}{Role}§7!", ""));
            yaml.set("Command.Role.MessagePlayerTemporary", Arrays.asList("", "{Prefix} Du hast die temporäre Rolle von §a{Player} §7zu {Role-Suffix}{Role} §7geändert.", "{Prefix} Diese Rolle gilt §c{Durate}§7!", ""));
            yaml.set("Command.Role.MessageAlreadyHaveTemporary", Collections.singletonList("{Prefix} §cDieser Spieler besitzt bereits eine Temporäre Rolle!"));
            yaml.set("Command.Role.ListMessage", Arrays.asList("{Prefix} Verfügbare Rollen:", "§8» {Format}"));
            yaml.set("Command.Role.MessagePlayer", Arrays.asList("", "{Prefix} Du hast die Rolle {Role-Suffix}{Role} §7erhalten.", ""));
            yaml.set("Command.Role.Message", Arrays.asList("", "{Prefix} Du hast §a{Player} §7die Rolle {Role-Suffix}{Role} §7gegeben.", ""));
            pathsChanged = true;
        }

        //Voucher
        if (!isPathSet("Command.Voucher.Enabled")) {
            yaml.set("Command.Voucher.Enabled", true);
            yaml.set("Command.Voucher.Aliases", "");
            yaml.set("Command.Voucher.Usage", Collections.singletonList("{Prefix} §8/§fVoucher §8<§7voucher§8/§7list§8> <§7Anzahl§8/§7Spieler§8> <§7Spieler§8>"));
            yaml.set("Command.Voucher.Permission", "system.voucher");
            yaml.set("Command.Voucher.Message", Collections.singletonList("{Prefix} §7Du hast {Player} §7{Voucher} §7gegeben."));
            yaml.set("Command.Voucher.MessagePlayer", Collections.singletonList("{Prefix} §7Du hast den §fGutschein §7{Voucher} §7{Amount}x §7erhalten."));
            yaml.set("Command.Voucher.MessageVoucherNotExist", Collections.singletonList("{Prefix} §cDieser Gutschein existiert nicht!"));
            yaml.set("Command.Voucher.ListFormat", "§7{Voucher}§8, ");
            yaml.set("Command.Voucher.MessageNoVouchers", "{Prefix} §cEs sind keine Gutscheine erstellt!");
            yaml.set("Command.Voucher.MessageList", Arrays.asList("{Prefix} §7Es gibt §a{Count} §7erstellte §fGutscheine§7.", "{Prefix} {Vouchers}"));
            yaml.set("Command.Voucher.MessageRedeemedRole", Arrays.asList("{Prefix} §8§m----------§8(§fGUTSCHEIN§8)§8§m----------", "", "{Prefix} Du hast die Rolle {Role-Suffix}{Role} §7erhalten.", "", "{Prefix} §8§m----------§8(§fGUTSCHEIN§8)§8§m----------"));
            yaml.set("Command.Voucher.MessageRedeemedPermission", Arrays.asList("{Prefix} §8§m----------§8(§fGUTSCHEIN§8)§8§m----------", "", "{Prefix} Du hast die Permission §c{Permission} §7erhalten.", "", "{Prefix} §8§m----------§8(§fGUTSCHEIN§8)§8§m----------"));
            yaml.set("Command.Voucher.MessageRedeemedCurrency", Arrays.asList("{Prefix} §8§m----------§8(§fGUTSCHEIN§8)§8§m----------", "", "{Prefix} Du hast §e{Currency}{CurrencyPrefix} §7erhalten.", "", "{Prefix} §8§m----------§8(§fGUTSCHEIN§8)§8§m----------"));
            yaml.set("Command.Voucher.DefaultVoucherType", "PAPER");
            yaml.set("Command.Voucher.Vouchers.Voucher.Type", "BOOK");
            yaml.set("Command.Voucher.Vouchers.Voucher.DisplayName", "§fGUTSCHEIN");
            yaml.set("Command.Voucher.Vouchers.Voucher.Lore", Collections.singletonList("{Prefix} §7Rechtsklicke zum einlösen von diesem §aGutschein§7."));
            yaml.set("Command.Voucher.Vouchers.Voucher.Role", "Administrator");
            yaml.set("Command.Voucher.Vouchers.Voucher.Permission", "system.*");
            yaml.set("Command.Voucher.Vouchers.Voucher.Currency", 9999.99);
            pathsChanged = true;
        }

        //Generator
        if (!isPathSet("Command.Generator.Enabled")) {
            yaml.set("Command.Generator.Enabled", true);
            yaml.set("Command.Generator.Aliases", "gen");
            yaml.set("Command.Generator.Usage", Collections.singletonList("{Prefix} §8/§6Generator §8<§7create§8/§7delete§8/§7rename§8/§7list§8/§7info§8/§7setLocation§8/§7setDrop§8/§7setDropAmount§8/§7setDropSpeed§8/§7start§8/§7stop§8>"));
            yaml.set("Command.Generator.Permission", "system.generator");
            yaml.set("Command.Generator.MessageCreated", Collections.singletonList("{Prefix} Du hast den Generator §6{Generator} §aerstellt§7."));
            yaml.set("Command.Generator.MessageAlreadyExist", Collections.singletonList("{Prefix} §cEin Generator mit diesem Namen wurde bereits erstellt!"));
            yaml.set("Command.Generator.MessageDeleted", Collections.singletonList("{Prefix} Du hast den Generator §6{Generator} §cgelöscht§7."));
            yaml.set("Command.Generator.MessageDoesNotExist", Collections.singletonList("{Prefix} §cEs wurde kein Generator mit diesem Namen gefunden!"));
            yaml.set("Command.Generator.MessageRenamed", Collections.singletonList("{Prefix} Du hast den Generator §6{OldGenerator} §7in §6{NewGenerator} §7umbenannt."));
            yaml.set("Command.Generator.MessageSetLocation", Collections.singletonList("{Prefix} Du hast die Position von dem Generator §6{Generator} §7zu deiner Position geändert."));
            yaml.set("Command.Generator.MessageSetDrop", Collections.singletonList("{Prefix} Du hast den Drop von dem Generator §6{Generator} §7zu {Item} §7geändert."));
            yaml.set("Command.Generator.MessageMaterialDoesNotExist", Collections.singletonList("{Prefix} §cDas angegebene Argument ist kein gültiges Material!"));
            yaml.set("Command.Generator.MessageSetDropAmount", Collections.singletonList("{Prefix} Du hast die Anzahl pro Drop von dem Generator §6{Generator} §7zu §a{DropAmount} §7geändert."));
            yaml.set("Command.Generator.MessageSetDropSpeed", Collections.singletonList("{Prefix} Du hast die Drop Geschwindigkeit von dem Generator §6{Generator} §7zu §a{DropSpeed} §7Ticks geändert."));
            yaml.set("Command.Generator.MessageNoNumber", Collections.singletonList("{Prefix} §cDas angegebene Argument ist keine Zahl, bitte verwende nur ganze Zahlen!"));
            yaml.set("Command.Generator.MessageStart", Collections.singletonList("{Prefix} Der Generator §6{Generator} §7wurde §aaktiviert§7."));
            yaml.set("Command.Generator.MessageAlreadyStarted", Collections.singletonList("{Prefix} §cDer Generator {Generator} ist bereits aktiviert!"));
            yaml.set("Command.Generator.MessageStop", Collections.singletonList("{Prefix} Der Generator §6{Generator} §7wurde §cdeaktiviert§7."));
            yaml.set("Command.Generator.MessageNotStarted", Collections.singletonList("{Prefix} §cDer Generator {Generator} ist nicht aktiviert!"));
            yaml.set("Command.Generator.Activated", "§aaktiviert");
            yaml.set("Command.Generator.Deactivated", "§cdeaktiviert");
            yaml.set("Command.Generator.MessageInformation", Arrays.asList("{Prefix} §7Informationen über den Generator §6{Generator}§7.",
                    "{Prefix} §7Aktiviert: {Status}",
                    "{Prefix} §7Position: §a{Location}",
                    "{Prefix} §7Drop: §a{Item}",
                    "{Prefix} §7DropAmount: §a{DropAmount}",
                    "{Prefix} §7DropSpeed: §a{DropSpeed}"));
            yaml.set("Command.Generator.ListFormat", "§7{Generator}§8, ");
            yaml.set("Command.Generator.MessageList", Arrays.asList("{Prefix} §7Es gibt §a{Count} §7erstellte §6Generatoren§7.", "{Prefix} {GeneratorList}"));
            yaml.set("Command.Generator.NoGeneratorsCreated", Collections.singletonList("{Prefix} §cEs gibt keine erstellten Generatoren!"));
            yaml.set("Command.Generator.Title", "§6§lGEN §8» §6{Generator} §8┃ §7x§6{Amount} §7{Item}");
            yaml.set("Command.Generator.StartAllAtServerStart", true);
            pathsChanged = true;
        }

        //Day
        if (!isPathSet("Command.Day.Enabled")) {
            yaml.set("Command.Day.Enabled", true);
            yaml.set("Command.Day.Aliases", "eday");
            yaml.set("Command.Day.Usage", Collections.singletonList("{Prefix} §8/§eDay"));
            yaml.set("Command.Day.Permission", "system.day");
            yaml.set("Command.Day.Message", Collections.singletonList("{Prefix} Du hast die Zeit zu §eTag §7gewechselt!"));
            yaml.set("Command.Day.GlobalMessageEnabled", true);
            yaml.set("Command.Day.GlobalMessage", Collections.singletonList("{Prefix} Es werde §eTag§7!"));
            pathsChanged = true;
        }

        //Night
        if (!isPathSet("Command.Night.Enabled")) {
            yaml.set("Command.Night.Enabled", true);
            yaml.set("Command.Night.Aliases", "enight");
            yaml.set("Command.Night.Usage", Collections.singletonList("{Prefix} §8/§9Night"));
            yaml.set("Command.Night.Permission", "system.night");
            yaml.set("Command.Night.Message", Collections.singletonList("{Prefix} Du hast die Zeit zu §9Nacht §7gewechselt!"));
            yaml.set("Command.Night.GlobalMessageEnabled", true);
            yaml.set("Command.Night.GlobalMessage", Collections.singletonList("{Prefix} Es werde §9Nacht§7!"));
            pathsChanged = true;
        }

        //Ping
        if (!isPathSet("Command.Ping.Enabled")) {
            yaml.set("Command.Ping.Enabled", true);
            yaml.set("Command.Ping.Aliases", "eping");
            yaml.set("Command.Ping.Usage", Collections.singletonList("{Prefix} §8/§ePing"));
            yaml.set("Command.Ping.Permission", "system.ping");
            yaml.set("Command.Ping.ColorCode.a.Condition",  "15 > {Ping}");
            yaml.set("Command.Ping.ColorCode.a.Comment",  "§asehr gut");
            yaml.set("Command.Ping.ColorCode.2.Condition",  "14 < {Ping} && {Ping} > 25");
            yaml.set("Command.Ping.ColorCode.2.Comment",  "§2gut");
            yaml.set("Command.Ping.ColorCode.6.Condition",  "24 < {Ping} && {Ping} > 50");
            yaml.set("Command.Ping.ColorCode.6.Comment",  "§6okay");
            yaml.set("Command.Ping.ColorCode.c.Condition",  "49 < {Ping} && {Ping} > 75");
            yaml.set("Command.Ping.ColorCode.c.Comment",  "§cschlecht");
            yaml.set("Command.Ping.ColorCode.4.Condition",  "74 < {Ping}");
            yaml.set("Command.Ping.ColorCode.4.Comment",  "§4sehr schlecht");
            yaml.set("Command.Ping.Message", Collections.singletonList("{Prefix} §7Dein Ping: {Ping} §8({Comment}§8)"));
            pathsChanged = true;
        }

        //Custom Commands
        if (!isPathSet("Command.CustomCommands.Enabled")) {
            yaml.set("Command.CustomCommands.Enabled", true);
            yaml.set("Command.CustomCommands.Discord.Usage", Collections.singletonList("{Prefix} §8/§9Discord"));
            yaml.set("Command.CustomCommands.Discord.Message", Collections.singletonList("{Prefix} Unser Discord Server:§9 discord.gg/seMwEpjUkD"));
            yaml.set("Command.CustomCommands.Discord.Aliases", "dc");
            yaml.set("Command.CustomCommands.TeamSpeak.Usage", Collections.singletonList("{Prefix} §8/§9TeamSpeak"));
            yaml.set("Command.CustomCommands.TeamSpeak.Message", Collections.singletonList("{Prefix} Unser TeamSpeak Server: §9NewJump.de"));
            yaml.set("Command.CustomCommands.TeamSpeak.Aliases", "ts");
            yaml.set("Command.CustomCommands.Nix.Usage", Collections.singletonList("{Prefix} §8/§7Nix"));
            yaml.set("Command.CustomCommands.Nix.Permission", "system.customcommands.nix");
            yaml.set("Command.CustomCommands.Nix.Message", Collections.singletonList("{Prefix} Blöd, hier ist nichts. §c:/"));
            yaml.set("Command.CustomCommands.Nix.Aliases", "enix");
            yaml.set("Command.CustomCommands.Toll.Usage", Collections.singletonList("{Prefix} §8/§7Toll"));
            yaml.set("Command.CustomCommands.Toll.Permission", "system.customcommands.toll");
            yaml.set("Command.CustomCommands.Toll.Message", Collections.singletonList("{Prefix} Das ist aber sehr toll! §a^-^"));
            yaml.set("Command.CustomCommands.Toll.Aliases", "etoll");
            yaml.set("Command.CustomCommands.Schade.Usage", Collections.singletonList("{Prefix} §8/§7Schade"));
            yaml.set("Command.CustomCommands.Schade.Permission", "system.customcommands.schade");
            yaml.set("Command.CustomCommands.Schade.Message", Collections.singletonList("{Prefix} Das ist echt schade! §c:`c"));
            yaml.set("Command.CustomCommands.Schade.Aliases", "eschade");
            pathsChanged = true;
        }

        if(!pathsChanged) {
            pathsChanged = checkForNewPaths();
        }

        if(pathsChanged) {
            try {
                yaml.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean checkForNewPaths() {
        boolean updated = false;

        //GlobalMute
        if(yaml.contains("Command.GlobalMute.Permission.Ignore")) {
            yaml.set("Command.GlobalMute.Permission.ByPass", yaml.getString("Command.GlobalMute.Permission.Ignore"));
            yaml.set("Command.GlobalMute.Permission.Ignore", null);
            updated = true;
        }

        //CommandSpy
        if(!yaml.contains("Command.CommandSpy.ShowConsoleExecutingCommands")) {
            yaml.set("Command.CommandSpy.ShowConsoleExecutingCommands", true);
        }

        return updated;
    }

    public static HashMap<String, String[]> getCommandAliases() {
        HashMap<String, String[]> aliases = new HashMap<>();

        for(String cmd : getConfigurationSection("Command")) {
            String[] cmdAliases = CommandFile.getStringPath("Command." + cmd + ".Aliases").split(",");
            if(!cmdAliases[0].equals("")) {
                aliases.put(cmd, cmdAliases);
            }
        }

        aliases.put("CustomCommands", new String[]{""});

        return aliases;
    }

    public static HashMap<String, String[]> getCustomCommandAliases() {
        HashMap<String, String[]> aliases = new HashMap<>();

        for(String customCommand : getConfigurationSection("Command.CustomCommands")) {
            if(!customCommand.equalsIgnoreCase("Enabled")) {
                String[] customCommandAliases = CommandFile.getStringPath("Command.CustomCommands." + customCommand + ".Aliases").split(",");
                if(!customCommandAliases[0].equals("")) {
                    aliases.put(customCommand, customCommandAliases);
                }
            }
        }

        return aliases;
    }

    private static HashMap<String, List<String>> configurationSection = new HashMap<>();
    public static List<String> getConfigurationSection(String path) {
        if(configurationSection.containsKey(path)) {
            return configurationSection.get(path);
        }

        List<String> list = new ArrayList<>();
        if(isPathSet(path)) {
            Set<String> keys = yaml.getConfigurationSection(path).getKeys(false);
            for(String key : keys) {
                list.add(key);
            }

            configurationSection.put(path, list);
        }
        return list;
    }

    public static void setPath(String path, Object wert) {
        yaml.set(path, wert);
        saveConfig();
    }

    public static HashMap<String, String> string = new HashMap<>();
    public static String getStringPath(String path) {
        if(string.containsKey(path)) {
            return string.get(path);
        }

        if(file.exists() && isPathSet(path)) {
            string.put(path, NewSystem.replace(yaml.getString(path)));
            return string.get(path);
        }else{
            return "";
        }
    }

    private static HashMap<String, Boolean> booleanSavings = new HashMap<>();
    public static boolean getBooleanPath(String path) {
        if(booleanSavings.containsKey(path)) {
            return booleanSavings.get(path);
        }

        if(file.exists() && isPathSet(path)) {
            booleanSavings.put(path, yaml.getBoolean(path));
            return booleanSavings.get(path);
        }
        return false;
    }

    private static HashMap<String, Integer> integer = new HashMap<>();
    public static Integer getIntegerPath(String path) {
        if(integer.containsKey(path)) {
            return integer.get(path);
        }

        if(file.exists() && isPathSet(path)) {
            integer.put(path, yaml.getInt(path));
            return integer.get(path);
        }
        return 0;
    }

    private static HashMap<String, Long> longSavings = new HashMap<>();
    public static Long getLongPath(String path) {
        if(longSavings.containsKey(path)) {
            return longSavings.get(path);
        }

        if(file.exists() && isPathSet(path)) {
            longSavings.put(path, yaml.getLong(path));
            return longSavings.get(path);
        }
        return 0L;
    }

    private static HashMap<String, Double> doubleSavings = new HashMap<>();
    public static Double getDoublePath(String path) {
        if(doubleSavings.containsKey(path)) {
            return doubleSavings.get(path);
        }

        if(file.exists() && isPathSet(path)) {
            doubleSavings.put(path, yaml.getDouble(path));
            return doubleSavings.get(path);
        }
        return 0D;
    }

    private static HashMap<String, List<String>> stringList = new HashMap<>();
    public static List<String> getStringListPath(String path) {
        if(stringList.containsKey(path)) {
            return stringList.get(path);
        }

        if(file.exists() && isPathSet(path)) {
                List<String> list = yaml.getStringList(path);
                list.replaceAll(NewSystem::replace);
                stringList.put(path, list);
                return list;
        }
        return new ArrayList<>();
    }

    public static boolean isPathSet(String path) {
        if (file.exists()) {
            return yaml.contains(path);
        } else {
            try {
                if (file.createNewFile()) {
                    saveConfig();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
