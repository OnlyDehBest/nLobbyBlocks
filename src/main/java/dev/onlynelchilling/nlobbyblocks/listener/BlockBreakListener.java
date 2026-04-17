package dev.onlynelchilling.nlobbyblocks.listener;

import dev.onlynelchilling.nlobbyblocks.NLobbyBlocks;
import dev.onlynelchilling.nlobbyblocks.manager.BlockService;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockBreakListener implements Listener {

    private final NLobbyBlocks plugin;

    public BlockBreakListener(NLobbyBlocks plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        BlockService mgr = plugin.getBlockManager();
        if (!mgr.hasActiveBlocks()) return;
        if (mgr.isActiveBlock(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        BlockService mgr = plugin.getBlockManager();
        if (!mgr.hasActiveBlocks()) return;
        event.blockList().removeIf(mgr::isActiveBlock);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        BlockService mgr = plugin.getBlockManager();
        if (!mgr.hasActiveBlocks()) return;
        event.blockList().removeIf(mgr::isActiveBlock);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockInteract(PlayerInteractEvent event) {
        if (!plugin.getConfigManager().isPreventBlockInteraction()) return;

        Block clicked = event.getClickedBlock();
        if (clicked == null) return;

        BlockService mgr = plugin.getBlockManager();
        if (!mgr.hasActiveBlocks()) return;

        if (mgr.isActiveBlock(clicked)) {
            event.setCancelled(true);
        }
    }
}
