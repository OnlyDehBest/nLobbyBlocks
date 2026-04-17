package dev.onlynelchilling.nlobbyblocks.model;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Region {

    private final String worldName;
    private final int minX, minY, minZ;
    private final int maxX, maxY, maxZ;

    private volatile World cachedWorld;

    public Region(String worldName, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.worldName = worldName;
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
        this.maxZ = Math.max(z1, z2);
    }

    public boolean contains(Location location) {
        World locWorld = location.getWorld();
        if (locWorld == null) return false;

        World w = cachedWorld;
        if (w == null) {
            w = Bukkit.getWorld(worldName);
            cachedWorld = w;
        }

        if (w != null) {
            if (locWorld != w) return false;
        } else if (!locWorld.getName().equals(worldName)) {
            return false;
        }

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        return x >= minX && x <= maxX
                && y >= minY && y <= maxY
                && z >= minZ && z <= maxZ;
    }
}
