package dev.onlynelchilling.nlobbyblocks.listener;

import dev.onlynelchilling.nlobbyblocks.NLobbyBlocks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final NLobbyBlocks plugin;

    public PlayerJoinListener(NLobbyBlocks plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!plugin.getConfigManager().isBlocksOnJoin()) return;

        Player player = event.getPlayer();

        if (plugin.getConfigManager().isJoinGiveDelayEnabled()) {
            long delay = plugin.getConfigManager().getJoinGiveDelay();
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline()) {
                    plugin.getItemManager().giveLobbyBlock(player);
                }
            }, delay);
        } else {
            plugin.getItemManager().giveLobbyBlock(player);
        }
    }
}
