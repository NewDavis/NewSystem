package me.newdavis.spigot.api;

import me.newdavis.spigot.file.OtherFile;
import me.newdavis.spigot.plugin.NewSystem;
import me.newdavis.spigot.util.placeholder.PlaceholderManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TabListAPI {

    public static int animationID = 1;
    public static final int UPDATE_SPEED = OtherFile.getIntegerPath("Other.TabList.UpdateSpeed");

    private static final String HEADER = OtherFile.getStringPath("Other.TabList." + animationID + ".Header");
    private static final String FOOTER = OtherFile.getStringPath("Other.TabList." + animationID + ".Footer");
    public static void setTabList() {
        for(Player all : Bukkit.getOnlinePlayers()) {
            PlaceholderManager placeholder = new PlaceholderManager(all);
            String[] s = placeholder.replacePlaceholderInString(new String[]{HEADER, FOOTER}, true);

            if(ReflectionAPI.VERSION_ID < 14) {
                init(s[0], s[1]);
                update(s[0], s[1]);
            }
            setTabList(all, s[0], s[1]);
        }
    }

    public static int getAnimationAmount() {
        int amount = 0;
        while(OtherFile.isPathSet("Other.TabList." + amount++)) {
            amount++;
        }
        return amount;
    }

    public static void TabListUpdater() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(NewSystem.getInstance(), new Runnable() {
            @Override
            public void run() {
                setTabList();
                animationID++;
                if(animationID > getAnimationAmount()) {
                    animationID = 1;
                }
            }
        }, 0, UPDATE_SPEED);
    }

    private static Object tabListPacket = null;
    private static Method a;
    private static Object headerComponent;
    private static Object footerComponent;
    private static Field footer;
    private static Field header;

    private static void init(String headerS, String footerS) {
        try {
            ReflectionAPI ref = new ReflectionAPI();
            tabListPacket = ref.getNMSClass("PacketPlayOutPlayerListHeaderFooter").newInstance();
            Class<?> iChatBaseComponent = ref.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0];
            a = iChatBaseComponent.getMethod("a", String.class);
            if (ReflectionAPI.VERSION_ID >= 12) {
                headerComponent = a.invoke(null, "{\"text\": \"" + headerS + '"' + "}");
                footerComponent = a.invoke(null, "{\"text\": \"" + footerS + '"' + "}");
                header = tabListPacket.getClass().getField("header");
                footer = tabListPacket.getClass().getField("footer");
            } else {
                headerComponent = a.invoke(null, "{'text': '" + headerS + "'}");
                footerComponent = a.invoke(null, "{'text': '" + footerS + "'}");
                header = tabListPacket.getClass().getDeclaredField("a");
                footer = tabListPacket.getClass().getDeclaredField("b");
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | NoSuchFieldException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static void update(String headerS, String footerS) {
        try {
            if (ReflectionAPI.VERSION_ID >= 12) {
                headerComponent = a.invoke(null, "{\"text\": \"" + headerS + '"' + "}");
                footerComponent = a.invoke(null, "{\"text\": \"" + footerS + '"' + "}");
            } else {
                headerComponent = a.invoke(null, "{'text': '" + headerS + "'}");
                footerComponent = a.invoke(null, "{'text': '" + footerS + "'}");
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setTabList(Player p, String headerS, String footerS) {
        try {
            if(ReflectionAPI.VERSION_ID >= 14) {
                Method method = p.getClass().getMethod("setPlayerListHeaderFooter", String.class, String.class);
                method.invoke(p, headerS, footerS);
            }else{
                //header
                header.setAccessible(true);
                header.set(tabListPacket, headerComponent);
                header.setAccessible(false);

                //footer
                footer.setAccessible(true);
                footer.set(tabListPacket, footerComponent);
                footer.setAccessible(false);

                ReflectionAPI ref = new ReflectionAPI();
                ref.sendPacket(p, tabListPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
