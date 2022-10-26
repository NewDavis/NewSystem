package me.newdavis.spigot.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionAPI {

    public static final int VERSION_ID = Integer.parseInt(getServerVersion().split("_")[1]);

    public int getPlayerPing(Player p) {
        try {
            int ping = 0;
            int versionId = Integer.parseInt(getServerVersion().split("_")[1]);
            if(versionId >= 12) {
                Class<?> player = Class.forName("org.bukkit.entity.Player");
                Object converted = player.cast(p);
                Method handle = converted.getClass().getMethod("getPing");
                Object entityPlayer = handle.invoke(converted);
                try {
                    ping = Integer.parseInt(entityPlayer.toString());
                }catch (NumberFormatException ignored) {}
            }else{
                Class<?> craftPlayer = getCraftBukkitClass("entity.CraftPlayer");
                Object converted = craftPlayer.cast(p);
                Method handle = converted.getClass().getMethod("getHandle");
                Object entityPlayer = handle.invoke(converted);
                Field pingField = entityPlayer.getClass().getField("ping");
                ping = pingField.getInt(entityPlayer);
            }
            return ping;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getPlayerIP(Player p) {
        try {
            Class<?> craftPlayer = getCraftBukkitClass("entity.CraftPlayer");
            Object converted = craftPlayer.cast(p);
            Method getAddress = converted.getClass().getMethod("getAddress");
            Object entityPlayer = getAddress.invoke(converted);
            return entityPlayer.toString().replace("/", "").split(":")[0].replace(".", "-");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void sendPacket(Player p, Object packet) {
        try {
            Class<?> player = p.getClass();
            Object handle = player.getMethod("getHandle").invoke(p);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getServerVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().substring(23);
    }

    public Class<?> getNMSClass(String nmsClass) {
        try {
            return Class.forName("net.minecraft.server." + getServerVersion() + "." + nmsClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Class<?> getCraftBukkitClass(String bukkitClass) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + getServerVersion() + "." + bukkitClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
