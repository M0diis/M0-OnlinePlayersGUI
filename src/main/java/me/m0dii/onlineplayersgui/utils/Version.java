package me.m0dii.onlineplayersgui.utils;

import org.bukkit.Bukkit;
import org.bukkit.Server;

@SuppressWarnings("java:S115")
public enum Version implements Comparable<Version> {
    // https://www.spigotmc.org/wiki/spigot-nms-and-minecraft-versions-1-16/
    // https://www.spigotmc.org/wiki/spigot-nms-and-minecraft-versions-1-21/
    v1_21_R4(28), // 1.21.5
    v1_21_R3(27), // 1.21.4 / 1.21.5
    v1_21_R2(26), // 1.21.2 / 1.21.3
    v1_21_R1(25), // 1.21 / 1.21.1
    v1_20_R4(24), // 1.20.6 / 1.20.5
    v1_20_R3(23), // 1.20.3 / 1.20.4
    v1_20_R2(22), // 1.20.2
    v1_20_R1(21), // 1.20 / 1.20.1
    v1_19_R3(20), // 1.19.4
    v1_19_R2(19), // 1.19.3
    v1_19_R1(18), // 1.19.1 / 1.19.2
    v1_18_R2(17), // 1.18.2
    v1_18_R1(16), // 1.18 / 1.18.1
    v1_17_R1(15),
    v1_16_R3(14),
    v1_16_R2(13),
    v1_16_R1(12),
    v1_15_R1(11),
    v1_14_R1(10),
    v1_13_R2(9),
    v1_13_R1(8),
    v1_12_R1(7),
    v1_11_R1(6),
    v1_10_R1(5),
    v1_9_R2(4),
    v1_9_R1(3),
    v1_8_R3(2),
    v1_8_R2(1),
    v1_8_R1(0),
    UNKNOWN(-1);

    private final int value;

    Version(int value) {
        this.value = value;
    }

    public static boolean serverIsNewerThan(Version version) {
        return getServerVersion(Bukkit.getServer()).isNewerThan(version);
    }

    /**
     * @param server to get the version from
     * @return the version of the server
     * @throws IllegalArgumentException if server is null
     */
    public static Version getServerVersion(Server server) {
        String packageName = server.getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);

        Messenger.debug("Package name: " + packageName + ", using server version: " + version);

        try {
            return valueOf(version.trim());
        }
        catch (final IllegalArgumentException e) {
            return Version.UNKNOWN;
        }
    }

    /**
     * @param server to check
     * @return true if the server is Paper or false of not
     * @throws IllegalArgumentException if server is null
     */
    public static boolean isPaper(Server server) {
        return server.getName().equalsIgnoreCase("Paper");
    }

    /**
     * Checks if the version is newer than the given version
     * <p>
     * If both versions are the same, the method will return false
     *
     * @param version to check against
     * @return true if the version is newer than the given one, otherwise false
     * @throws IllegalArgumentException if version is null
     * @throws IllegalArgumentException if this version or the given version, is the version UNKNOWN
     */
    public boolean isNewerThan(Version version) {
        if (checkUnknown(version)) {
            return true;
        }

        return value > version.value;
    }

    /**
     * Checks if the version is newer or the same than the given version
     *
     * @param version to check against
     * @return true if the version is newer or the same than the given one, otherwise false
     * @throws IllegalArgumentException if version is null
     * @throws IllegalArgumentException if this version or the given version, is the version UNKNOWN
     */
    public boolean isNewerOrSameThan(Version version) {
        if(checkUnknown(version)) {
            return true;
        }

        return value >= version.value;
    }

    /**
     * Checks if the version is older than the given version
     *
     * @param version to check against
     * @return true if the version is older than the given one, otherwise false
     * @throws IllegalArgumentException if version is null
     * @throws IllegalArgumentException if this version or the given version, is the version UNKNOWN
     */
    public boolean isOlderThan(Version version) {
        if (!checkUnknown(version)) {
            return true;
        }

        return value < version.value;
    }

    private static boolean NOTIFIED_UNKNOWN = false;

    private boolean checkUnknown(Version version) {
        if(NOTIFIED_UNKNOWN) {
            return true;
        }

        if (version == UNKNOWN) {
            Messenger.warn("Provided version is UNKNOWN. Some features may not work correctly.");
            Messenger.warn("Assuming using the latest version.");

            NOTIFIED_UNKNOWN = true;

            return true;
        }

        if (this == UNKNOWN) {
            Messenger.warn("Server version is UNKNOWN. Some features may not work correctly.");
            Messenger.warn("Assuming using the latest version.");

            NOTIFIED_UNKNOWN = true;

            return true;
        }

        return false;
    }

    /**
     * Checks if the version is older or the same than the given version
     *
     * @param version to check against
     * @return true if the version is older or the same than the given one, otherwise false
     * @throws IllegalArgumentException if version is null
     * @throws IllegalArgumentException if this version or the given version, is the version UNKNOWN
     */
    public boolean isOlderOrSameThan(Version version) {
        if (!checkUnknown(version)) {
            return true;
        }

        return value <= version.value;
    }

}