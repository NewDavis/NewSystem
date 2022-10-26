package me.newdavis.spigot.util;

import me.newdavis.spigot.file.OtherFile;
import me.newdavis.spigot.file.SettingsFile;
import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Portal {

    public static HashMap<String, Location[]> portals = new HashMap<>();

    public Portal() {}

    public void init() {
        for(String portal : OtherFile.getConfigurationSection("Other.Portal")) {
            if((!portal.equalsIgnoreCase("Enabled")) && (!portal.equalsIgnoreCase("ServerNotFound"))) {
                World w1 = Bukkit.getWorld(OtherFile.getStringPath("Other.Portal." + portal + ".Location.From.World"));
                int x1 = OtherFile.getIntegerPath("Other.Portal." + portal + ".Location.From.X");
                int y1 = OtherFile.getIntegerPath("Other.Portal." + portal + ".Location.From.Y");
                int z1 = OtherFile.getIntegerPath("Other.Portal." + portal + ".Location.From.Z");
                Location from = new Location(w1, x1, y1, z1);

                World w2 = Bukkit.getWorld(OtherFile.getStringPath("Other.Portal." + portal + ".Location.To.World"));
                int x2 = OtherFile.getIntegerPath("Other.Portal." + portal + ".Location.To.X");
                int y2 = OtherFile.getIntegerPath("Other.Portal." + portal + ".Location.To.Y");
                int z2 = OtherFile.getIntegerPath("Other.Portal." + portal + ".Location.To.Z");
                Location to = new Location(w2, x2, y2, z2);

                if(w1.getName().equalsIgnoreCase(w2.getName())) {
                    Location[] locations = new Location[]{from, to};
                    portals.put(portal, locations);
                }
            }
        }
    }

    private Player p;
    private String portal;

    public boolean isInPortal(Player p) {
        this.p = p;

        for(String portal : portals.keySet()) {
            Location from = portals.get(portal)[0];
            Location to = portals.get(portal)[1];

            int highestX = Math.max(from.getBlockX(), to.getBlockX());
            int lowestX = Math.min(from.getBlockX(), to.getBlockX());
            int highestY = Math.max(from.getBlockY(), to.getBlockY());
            int lowestY = Math.min(from.getBlockY(), to.getBlockY());
            int highestZ = Math.max(from.getBlockZ(), to.getBlockZ());
            int lowestZ = Math.min(from.getBlockZ(), to.getBlockZ());

            int playerX = p.getLocation().getBlockX();
            int playerY = p.getLocation().getBlockY();
            int playerZ = p.getLocation().getBlockZ();

            if (playerX <= highestX && playerX >= lowestX) {
                if (playerY <= highestY && playerY >= lowestY) {
                    if (playerZ <= highestZ && playerZ >= lowestZ) {
                        this.portal = portal;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean hasPermission() {
        if(OtherFile.isPathSet("Other.Portal." + portal + ".Permission")) {
            String perm = OtherFile.getStringPath("Other.Portal." + portal + ".Permission");
            return NewSystem.hasPermission(p, perm);
        }
        return true;
    }

    public void sendMessage() {
        if(OtherFile.isPathSet("Other.Portal." + portal + ".Message")) {
            List<String> message = OtherFile.getStringListPath("Other.Portal." + portal + ".Message");
            for(String msg : message) {
                p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix())
                        .replace("{Server}", OtherFile.getStringPath("Other.Portal." + portal + ".SendToServer"))
                        .replace("{Portal}", portal));
            }
        }
    }

    public void executeCommand() {
        if(OtherFile.isPathSet("Other.Portal." + portal + ".ExecuteCommand")) {
            String command = OtherFile.getStringPath("Other.Portal." + portal + ".ExecuteCommand");
            Bukkit.dispatchCommand(p, command);
        }
    }

    public void teleportPlayer() {
        if(OtherFile.isPathSet("Other.Portal." + portal + ".Teleport")) {
            World world = Bukkit.getWorld(OtherFile.getStringPath("Other.Portal." + portal + ".Teleport.Location.World"));
            double x = OtherFile.getDoublePath("Other.Portal." + portal + ".Teleport.Location.X");
            double y = OtherFile.getDoublePath("Other.Portal." + portal + ".Teleport.Location.Y");
            double z = OtherFile.getDoublePath("Other.Portal." + portal + ".Teleport.Location.Z");
            float yaw = (float) (double) OtherFile.getDoublePath("Other.Portal." + portal + ".Teleport.Location.Yaw");
            float pitch = (float) (double) OtherFile.getDoublePath("Other.Portal." + portal + ".Teleport.Location.Pitch");

            Location location = new Location(world, x, y, z, yaw, pitch);
            p.teleport(location);
        }
    }

    public void sendToServer() {
        List<String> serverNotFound = OtherFile.getStringListPath("Other.Portal." + portal + ".ServerNotFound");
        if(OtherFile.isPathSet("Other.Portal." + portal + ".SendToServer")) {
            String server = OtherFile.getStringPath("Other.Portal." + portal + ".SendToServer");
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);

            try {
                out.writeUTF("Connect");
                out.writeUTF(server);
            } catch (IOException e) {
                for(String msg : serverNotFound) {
                    p.sendMessage(msg.replace("{Prefix}", SettingsFile.getPrefix()).replace("{Server}", server));
                }
            }
            p.sendPluginMessage(NewSystem.getInstance(), "BungeeCord", b.toByteArray());
        }
    }

}
