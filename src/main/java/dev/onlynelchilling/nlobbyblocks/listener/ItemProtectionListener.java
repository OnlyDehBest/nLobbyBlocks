package dev.onlynelchilling.nlobbyblocks.listener;

import dev.onlynelchilling.nlobbyblocks.NLobbyBlocks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class ItemProtectionListener implements Listener {

    private final NLobbyBlocks plugin;

    public ItemProtectionListener(NLobbyBlocks plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        if (!plugin.getConfigManager().isPreventDrop()) return;
        if (plugin.getItemManager().isLobbyBlock(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSwapHand(PlayerSwapHandItemsEvent event) {
        if (!plugin.getConfigManager().isPreventOffHandSwap()) return;
        if (plugin.getItemManager().isLobbyBlock(event.getMainHandItem())
                || plugin.getItemManager().isLobbyBlock(event.getOffHandItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!plugin.getConfigManager().isPreventMove()) return;
        if (plugin.getItemManager().isLobbyBlock(event.getCurrentItem())
                || plugin.getItemManager().isLobbyBlock(event.getCursor())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!plugin.getConfigManager().isPreventMove()) return;
        if (plugin.getItemManager().isLobbyBlock(event.getOldCursor())) {
            event.setCancelled(true);
        }
    }
}


