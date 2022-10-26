package me.newdavis.spigot.api;

import me.newdavis.spigot.file.CommandFile;
import me.newdavis.spigot.file.SavingsFile;
import me.newdavis.spigot.file.SettingsFile;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class HologramAPI {

    //Reload
    public static void reloadHologram(String hologramName) {
        removeHologramByName(hologramName);
        Location location = getLocationOfHologram(hologramName);
        String hologramText = getCustomNameOfHologram(hologramName);
        ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setCustomName(hologramText);
        armorStand.setCustomNameVisible(true);

        List<Integer> lines = getHologramLines(hologramName);
        for(int line : lines) {
            removeHologramLineByLine(hologramName, line);
        }
        for (int i = 1; i <= lines.size(); i++) {
            String asText = SavingsFile.getStringPath("Hologram." + hologramName + ".Line." + i);
            if(!asText.equals("")) {
                ArmorStand armorStandLine = location.getWorld().spawn(getLocationOfHologram(hologramName).subtract(0, (0.3 * i), 0), ArmorStand.class);
                armorStandLine.setVisible(false);
                armorStandLine.setGravity(false);
                armorStandLine.setCustomName(asText);
                armorStandLine.setCustomNameVisible(true);
            }
        }
    }

    //Hologram
    public static boolean createHologram(String hologramName, Location location) {
        if(!hologramExist(hologramName)) {
            location.subtract(0, 1, 0);
            SavingsFile.setPath("Hologram." + hologramName + ".Location.World", location.getWorld().getName());
            SavingsFile.setPath("Hologram." + hologramName + ".Location.X", location.getX());
            SavingsFile.setPath("Hologram." + hologramName + ".Location.Y", location.getY());
            SavingsFile.setPath("Hologram." + hologramName + ".Location.Z", location.getZ());
            SavingsFile.setPath("Hologram." + hologramName + ".Location.Yaw", location.getYaw());
            SavingsFile.setPath("Hologram." + hologramName + ".Location.Pitch", location.getPitch());
            SavingsFile.setPath("Hologram." + hologramName + ".Title", getDefaultTitle().replace("{HologramName}", hologramName));
            String hologramText = SavingsFile.getStringPath("Hologram." + hologramName + ".Title");
            ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class);
            armorStand.setVisible(false);
            armorStand.setGravity(false);
            armorStand.setCustomName(hologramText);
            armorStand.setCustomNameVisible(true);
            return true;
        }else{
            reloadHologram(hologramName);
        }
        return false;
    }

    public static boolean removeHologramByName(String hologramName) {
        if(hologramExist(hologramName)) {
            ArmorStand armorStand = getArmorStandByName(hologramName);
            if(armorStand != null) {
                armorStand.remove();
                return true;
            }
        }
        return false;
    }

    public static boolean deleteHologramByName(String hologramName) {
        if(hologramExist(hologramName)) {
            removeHologramByName(hologramName);
            List<Integer> lines = getHologramLines(hologramName);
            for(int line : lines) {
                removeHologramLineByLine(hologramName, line);
            }

            SavingsFile.setPath("Hologram." + hologramName, null);
            return true;
        }
        return false;
    }

    public static void renameHologramByName(String hologramName, String newHologramName) {
        if(hologramExist(hologramName)) {
            if (!hologramExist(newHologramName)) {
                String title = getCustomNameOfHologram(hologramName);
                Location location = getLocationOfHologram(hologramName);
                List<Integer> lines = getHologramLines(hologramName);
                List<String> linesText = getHologramLinesText(hologramName);

                deleteHologramByName(hologramName);

                SavingsFile.setPath("Hologram." + newHologramName + ".Title", title);
                setHologramLocation(newHologramName, location.add(0, 1, 0));
                for(int line : lines) {
                    SavingsFile.setPath("Hologram." + newHologramName + ".Line." + line, linesText.get(line - 1));
                }
                reloadHologram(newHologramName);
            }
        }
    }

    public static ArmorStand getArmorStandByName(String hologramName) {
        if(hologramExist(hologramName)) {
            Location hologramLocation = getLocationOfHologram(hologramName);
            String hologramText = getCustomNameOfHologram(hologramName);
            for(Entity entity : hologramLocation.getWorld().getNearbyEntities(hologramLocation, 2, 2, 2)) {
                if(entity.getType() == EntityType.ARMOR_STAND) {
                    if(entity.getCustomName().equalsIgnoreCase(hologramText)) {
                        return (ArmorStand) entity;
                    }
                }
            }
        }
        return null;
    }

    public static Location getLocationOfHologram(String hologramName) {
        Location location = null;
        if(hologramExist(hologramName)) {
            World world = Bukkit.getWorld(SavingsFile.getStringPath("Hologram." + hologramName + ".Location.World"));
            double x = SavingsFile.getDoublePath("Hologram." + hologramName + ".Location.X");
            double y = SavingsFile.getDoublePath("Hologram." + hologramName + ".Location.Y");
            double z = SavingsFile.getDoublePath("Hologram." + hologramName + ".Location.Z");
            double yaw = SavingsFile.getDoublePath("Hologram." + hologramName + ".Location.Yaw");
            double pitch = SavingsFile.getDoublePath("Hologram." + hologramName + ".Location.Pitch");
            location = new Location(world, x, y, z, (float) yaw, (float) pitch);
        }
        return location;
    }

    public static List<String> getHolograms() {
        List<String> holograms = new ArrayList<>();
        for (String key : SavingsFile.getConfigurationSection("Hologram")) {
            if(hologramExist(key)) {
                holograms.add(key);
            }
        }
        return holograms;
    }

    public static void setHologramTitle(String hologramName, String text) {
        if(hologramExist(hologramName)) {
            ArmorStand armorStand = getArmorStandByName(hologramName);
            if(armorStand != null) {
                armorStand.setCustomName(text);
                armorStand.setCustomNameVisible(true);
                SavingsFile.setPath("Hologram." + hologramName + ".Title", text);
            }
            reloadHologram(hologramName);
        }
    }

    public static void setHologramLocation(String hologramName, Location location) {
        if(hologramExist(hologramName)) {
            location.subtract(0, 1, 0);
            SavingsFile.setPath("Hologram." + hologramName + ".Location.World", location.getWorld().getName());
            SavingsFile.setPath("Hologram." + hologramName + ".Location.X", location.getX());
            SavingsFile.setPath("Hologram." + hologramName + ".Location.Y", location.getY());
            SavingsFile.setPath("Hologram." + hologramName + ".Location.Z", location.getZ());
            SavingsFile.setPath("Hologram." + hologramName + ".Location.Yaw", location.getYaw());
            SavingsFile.setPath("Hologram." + hologramName + ".Location.Pitch", location.getPitch());
        }
    }

    public static String getCustomNameOfHologram(String hologramName) {
        String customName = "";
        if(hologramExist(hologramName)) {
            customName = SavingsFile.getStringPath("Hologram." + hologramName + ".Title");
        }
        return customName;
    }

    public static boolean hologramExist(String hologramName) {
        return SavingsFile.isPathSet("Hologram." + hologramName + ".Title");
    }

    public static String getDefaultTitle() {
        String title = CommandFile.getStringPath("Command.Hologram.DefaultTitle").replace("{Prefix}", SettingsFile.getPrefix());
        return title;
    }

    //Hologram Line
    public static void addHologramLine(String hologramName, String text) {
        if(hologramExist(hologramName)) {
            List<Integer> lines = getHologramLines(hologramName);
            for(int i = 1; i <= lines.size(); i++) {
                removeHologramLineByLine(hologramName, i);
            }

            SavingsFile.setPath("Hologram." + hologramName + ".Line." + (lines.size() + 1), text);
            reloadHologram(hologramName);
        }
    }

    public static void removeHologramLineByLine(String hologramName, int line) {
        if(hologramLineExist(hologramName, line)) {
            ArmorStand armorStand = getArmorStandLineByLine(hologramName, line);
            if (armorStand != null) {
                armorStand.remove();
            }

        }
    }

    public static void deleteHologramLineByLine(String hologramName, int line) {
        if(hologramLineExist(hologramName, line)) {
            List<Integer> lines = getHologramLines(hologramName);
            for(int lineF : lines) {
                getArmorStandLineByLine(hologramName, lineF).remove();
            }

            for(int lineF : lines) {
                if(lineF > line) {
                    String text = getCustomNameOfHologramLine(hologramName, lineF);
                    SavingsFile.setPath("Hologram." + hologramName + ".Line." + (lineF - 1), text);
                }
            }
            SavingsFile.setPath("Hologram." + hologramName + ".Line." + lines.size(), null);

            reloadHologram(hologramName);
        }
    }

    public static void switchHologramLine(String hologramName, int line, int switchLine) {
        if(hologramLineExist(hologramName, line)) {
            if(hologramLineExist(hologramName, switchLine)) {
                List<Integer> lines = getHologramLines(hologramName);
                if (!(switchLine > lines.size())) {
                    for (int lineF : lines) {
                        getArmorStandLineByLine(hologramName, lineF).remove();
                    }

                    String textLine = getCustomNameOfHologramLine(hologramName, line);
                    String textPriority = getCustomNameOfHologramLine(hologramName, switchLine);

                    SavingsFile.setPath("Hologram." + hologramName + ".Line." + line, textPriority);
                    SavingsFile.setPath("Hologram." + hologramName + ".Line." + switchLine, textLine);

                    reloadHologram(hologramName);
                }
            }
        }
    }

    public static ArmorStand getArmorStandLineByLine(String hologramName, int line) {
        if(hologramLineExist(hologramName, line)) {
            Location hologramLocation = getLocationOfHologramLine(hologramName, line);
            String hologramText = getCustomNameOfHologramLine(hologramName, line);
            for(Entity entity : hologramLocation.getWorld().getNearbyEntities(hologramLocation, 2, 2, 2)) {
                if(entity.getType() ==  EntityType.ARMOR_STAND) {
                    if(entity.getCustomName().equals(hologramText)) {
                        if(entity.getLocation().getX() == hologramLocation.getX() && entity.getLocation().getY() == hologramLocation.getY() && entity.getLocation().getZ() == hologramLocation.getZ()) {
                            return (ArmorStand) entity;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static Location getLocationOfHologramLine(String hologramName, int line) {
        Location location = null;
        if(hologramLineExist(hologramName, line)) {
            location = getLocationOfHologram(hologramName).subtract(0, (0.3 * line), 0);
        }
        return location;
    }

    public static List<Integer> getHologramLines(String hologramName) {
        List<Integer> lines = new ArrayList<>();
        if(SavingsFile.isPathSet("Hologram." + hologramName + ".Line")) {
            for(String line : SavingsFile.getConfigurationSection("Hologram." + hologramName + ".Line")) {
                int lineNumber = 0;
                try {
                    lineNumber = Integer.parseInt(line);
                }catch(NumberFormatException e) {
                    e.printStackTrace();
                }
                if(lineNumber != 0) {
                    lines.add(lineNumber);
                }
            }
        }
        return lines;
    }

    public static List<String> getHologramLinesText(String hologramName) {
        List<String> lines = new ArrayList<>();
        if(SavingsFile.isPathSet("Hologram." + hologramName + ".Line")) {
            for(String line : SavingsFile.getConfigurationSection("Hologram." + hologramName + ".Line")) {
                String text = SavingsFile.getStringPath("Hologram." + hologramName + ".Line." + line);
                lines.add(text);
            }
        }
        return lines;
    }

    public static void setHologramLineText(String hologramName, int line, String text) {
        if(hologramLineExist(hologramName, line)) {
            ArmorStand armorStand = getArmorStandLineByLine(hologramName, line);
            if(armorStand != null) {
                armorStand.setCustomName(text);
                armorStand.setCustomNameVisible(true);
                SavingsFile.setPath("Hologram." + hologramName + ".Line." + line, text);
            }
            reloadHologram(hologramName);
        }
    }

    public static String getCustomNameOfHologramLine(String hologramName, int line) {
        String customName = "";
        if(hologramLineExist(hologramName, line)) {
            customName = SavingsFile.getStringPath("Hologram." + hologramName + ".Line." + line);
        }
        return customName;
    }

    public static boolean hologramLineExist(String hologramName, int line) {
        return SavingsFile.isPathSet("Hologram." + hologramName + ".Line." + line);
    }

    //Other
    public static void moveHologram(String hologramName, Location location) {
        removeHologramByName(hologramName);
        for(int line : getHologramLines(hologramName)) {
            removeHologramLineByLine(hologramName, line);
        }

        setHologramLocation(hologramName, location);

        reloadHologram(hologramName);
    }
}
