package dev.onlynelchilling.nlobbyblocks.manager;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface BlockService {

    void registerBlock(Location location);

    boolean isActiveBlock(Location location);

    void clearAll();

    void reloadConfig();

    void addPlayer(Player player);

    void removePlayer(Player player);
}

