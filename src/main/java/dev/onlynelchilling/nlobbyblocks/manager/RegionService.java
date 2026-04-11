package dev.onlynelchilling.nlobbyblocks.manager;

import org.bukkit.Location;

public interface RegionService {

    boolean isAllowed(Location location);

    void load();
}

