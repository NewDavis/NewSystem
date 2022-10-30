package me.newdavis.spigot.api;

import me.newdavis.spigot.plugin.NewSystem;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameFetcher {

    private static final String NAME_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";

    private static final Pattern NAME_PATTERN = Pattern.compile(",\\s*\"name\"\\s*:\\s*\"(.*?)\"");

    public static String getName(UUID uuid) {
        if(NewSystem.playerCache.containsKey(uuid)) {
            return NewSystem.playerCache.get(uuid);
        }

        return getName(uuid.toString());
    }

    public static String getName(String uuid) {
        UUID uuid1 = UUID.fromString(uuid);
        if(NewSystem.playerCache.containsKey(uuid1)) {
            return NewSystem.playerCache.get(uuid1);
        }

        String output = callURL(NAME_URL + uuid);
        Matcher m = NAME_PATTERN.matcher(output);
        if (m.find()) {
            String name = m.group(1);
            NewSystem.playerCache.put(uuid1, name);

            return m.group(1);
        }
        return null;
    }

    private static String callURL(String urlStr) {
        StringBuilder sb = new StringBuilder();
        URLConnection conn;
        BufferedReader br = null;
        InputStreamReader in = null;
        try {
            conn = new URL(urlStr).openConnection();
            if (conn != null) {
                conn.setReadTimeout(60 * 1000);
            }
            if (conn != null && conn.getInputStream() != null) {
                in = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8);
                br = new BufferedReader(in);
                String line = br.readLine();
                while (line != null) {
                    sb.append(line).append("\n");
                    line = br.readLine();
                }
            }
        } catch (Throwable ignored) {} finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Throwable ignored) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (Throwable ignored) {
                }
            }
        }
        return sb.toString();
    }

}