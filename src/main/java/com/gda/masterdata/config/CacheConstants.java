package com.gda.masterdata.config;

public final class CacheConstants {
    private CacheConstants() {}

    // Префикс проекта, чтобы не пересекаться с другими приложениями в одном Redis
    public static final String APP_PREFIX = "masterdata";

    // "Теги" (имена кэшей)
    public static final String MODULES = "portal:modules";
    public static final String GROUPS = "portal:groups";
    public static final String PERMISSIONS = "portal:permissions";
    public static final String DICTIONARIES = "common:dictionaries";
}
