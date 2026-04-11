package dev.onlynelchilling.nlobbyblocks.manager;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ItemService {

    ItemStack createLobbyBlock();

    boolean isLobbyBlock(ItemStack item);

    void giveLobbyBlock(Player player);

    void invalidateCache();
}

