package me.newdavis.spigot.sql;

public enum SQLTables {

    CURRENCY("currency"), BAN("ban"), BANNED_PLAYERS("bannedplayers"),
    BANIP("banip"), BANNED_IPS("bannedips"), IP("ip_storage"),
    MAINTENANCE("maintenance"), MAINTENANCE_PLAYER("maintenance_player"),
    MAINTENANCE_ROLE("maintenance_role"), MUTE("mute"), MUTED_PLAYERS("mutedplayers"),
    MUTEIP("muteip"), MUTED_IPS("mutedips"), PLAYTIME("playtime"), PLAYTIMEREWARDS("playtimereward"),
    ROLE("role"), STATS("stats"), WARN("warn"), KICK("kick");

    private String table;

    SQLTables(String table) {
        this.table = table;
    }

    public String getTableName() {
        return table;
    }

}
