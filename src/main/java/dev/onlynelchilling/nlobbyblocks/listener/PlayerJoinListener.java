package dev.onlynelchilling.nlobbyblocks.listener;

import dev.onlynelchilling.nlobbyblocks.NLobbyBlocks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinListener implements Listener {

    private final NLobbyBlocks plugin;

    public PlayerJoinListener(NLobbyBlocks plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getBlockManager().addPlayer(player);

        if (!plugin.getConfigManager().isBlocksOnJoin()) return;

        if (plugin.getConfigManager().isJoinGiveDelayEnabled()) {
            long delay = Math.max(1, plugin.getConfigManager().getJoinGiveDelay());
            player.getScheduler().runDelayed(plugin, task -> {
                if (player.isOnline()) {
                    plugin.getItemManager().giveLobbyBlock(player);
                }
            }, null, delay);
        } else {
            plugin.getItemManager().giveLobbyBlock(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getBlockManager().removePlayer(event.getPlayer());
    }
}
