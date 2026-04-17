package dev.onlynelchilling.nlobbyblocks.listener;

import dev.onlynelchilling.nlobbyblocks.NLobbyBlocks;
import dev.onlynelchilling.nlobbyblocks.manager.ItemService;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

public class ItemProtectionListener implements Listener {

    private final NLobbyBlocks plugin;

    public ItemProtectionListener(NLobbyBlocks plugin) {
        this.plugin = plugin;
    }

    private static boolean isEmpty(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        if (!plugin.getConfigManager().isPreventDrop()) return;

        ItemStack stack = event.getItemDrop().getItemStack();
        if (isEmpty(stack)) return;

        if (plugin.getItemManager().isLobbyBlock(stack)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSwapHand(PlayerSwapHandItemsEvent event) {
        if (!plugin.getConfigManager().isPreventOffHandSwap()) return;

        ItemService items = plugin.getItemManager();
        ItemStack main = event.getMainHandItem();
        ItemStack off = event.getOffHandItem();

        if ((!isEmpty(main) && items.isLobbyBlock(main))
                || (!isEmpty(off) && items.isLobbyBlock(off))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!plugin.getConfigManager().isPreventMove()) return;

        ItemService items = plugin.getItemManager();
        ItemStack current = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        if ((!isEmpty(current) && items.isLobbyBlock(current))
                || (!isEmpty(cursor) && items.isLobbyBlock(cursor))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!plugin.getConfigManager().isPreventMove()) return;

        ItemStack old = event.getOldCursor();
        if (isEmpty(old)) return;

        if (plugin.getItemManager().isLobbyBlock(old)) {
            event.setCancelled(true);
        }
    }
}
