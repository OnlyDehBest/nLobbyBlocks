package dev.onlynelchilling.nlobbyblocks.listener;

import dev.onlynelchilling.nlobbyblocks.NLobbyBlocks;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;

public class BlockPlaceListener implements Listener {

    private final NLobbyBlocks plugin;

    public BlockPlaceListener(NLobbyBlocks plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!plugin.getItemManager().isLobbyBlock(event.getItemInHand())) return;

        Player player = event.getPlayer();
        Location location = event.getBlockPlaced().getLocation();

        if (!plugin.getRegionManager().isAllowed(location)) {
            event.setCancelled(true);
            plugin.getMessagesProvider().send(player, "place-denied-region");
            return;
        }

        plugin.getBlockManager().registerBlock(location);
        plugin.getEffectUtil().playPlace(location);

        var refill = plugin.getItemManager().createLobbyBlock();
        if (event.getHand() == EquipmentSlot.HAND) {
            player.getInventory().setItemInMainHand(refill);
        } else {
            player.getInventory().setItemInOffHand(refill);
        }
    }
}
