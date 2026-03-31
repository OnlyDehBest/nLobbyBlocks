package dev.onlynelchilling.nlobbyblocks.manager;

import dev.onlynelchilling.nlobbyblocks.NLobbyBlocks;
import dev.onlynelchilling.nlobbyblocks.util.EffectUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BlockManager {

    private final NLobbyBlocks plugin;
    private final EffectUtil effectUtil;
    private final Map<String, BlockEntry> activeBlocks = new ConcurrentHashMap<>();
    private int breakTime;
    private BukkitTask timerTask;
    private static final double CRACK_RANGE_SQ = 32.0 * 32.0;

    private static final class BlockEntry {
        final Location location;
        final int entityId;
        int elapsed;

        BlockEntry(Location location, int entityId) {
            this.location = location;
            this.entityId = entityId;
        }
    }

    public BlockManager(NLobbyBlocks plugin, EffectUtil effectUtil) {
        this.plugin = plugin;
        this.effectUtil = effectUtil;
        this.breakTime = plugin.getConfigManager().getBreakTime();
        startTimer();
    }

    public void reloadConfig() {
        this.breakTime = plugin.getConfigManager().getBreakTime();
    }

    private static int blockEntityId(Block block) {
        return (block.getX() & 4095) << 20 | (block.getZ() & 4095) << 8 | block.getY() & 255;
    }

    private static String toKey(Location loc) {
        return loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
    }

    private void startTimer() {
        timerTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (activeBlocks.isEmpty()) return;
                tick();
            }
        }.runTaskTimerAsynchronously(plugin, 20L, 20L);
    }

    private void tick() {
        List<BlockEntry> toBreak = null;

        for (BlockEntry entry : activeBlocks.values()) {
            entry.elapsed++;
            if (entry.elapsed >= breakTime) {
                if (toBreak == null) toBreak = new ArrayList<>();
                toBreak.add(entry);
            } else {
                sendBlockCrack(entry.location, (float) entry.elapsed / breakTime, entry.entityId);
            }
        }

        if (toBreak != null) {
            List<BlockEntry> finalToBreak = toBreak;
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                for (BlockEntry entry : finalToBreak) {
                    activeBlocks.remove(toKey(entry.location));
                    breakBlock(entry.location);
                }
            });
        }
    }

    private void sendBlockCrack(Location location, float progress, int entityId) {
        if (location.getWorld() == null) return;
        for (Player player : location.getWorld().getPlayers()) {
            if (player.getLocation().distanceSquared(location) <= CRACK_RANGE_SQ) {
                player.sendBlockDamage(location, progress, entityId);
            }
        }
    }

    private void breakBlock(Location location) {
        if (location.getWorld() == null) return;
        Block block = location.getBlock();
        if (block.getType() == Material.AIR) return;

        int entityId = blockEntityId(block);
        for (Player player : location.getWorld().getPlayers()) {
            if (player.getLocation().distanceSquared(location) <= CRACK_RANGE_SQ) {
                player.sendBlockDamage(location, 0f, entityId);
            }
        }

        effectUtil.spawnBreakParticles(location, block.getType());
        effectUtil.playBreak(location);
        block.setType(Material.AIR);
    }

    public void registerBlock(Location location) {
        activeBlocks.put(toKey(location), new BlockEntry(location, blockEntityId(location.getBlock())));
    }

    public boolean isActiveBlock(Location location) {
        if (location.getWorld() == null) return false;
        return activeBlocks.containsKey(toKey(location));
    }

    public void clearAll() {
        if (timerTask != null && !timerTask.isCancelled()) timerTask.cancel();
        for (BlockEntry entry : new ArrayList<>(activeBlocks.values())) {
            Location loc = entry.location;
            if (loc.getWorld() != null && loc.getBlock().getType() != Material.AIR) {
                loc.getBlock().setType(Material.AIR);
            }
        }
        activeBlocks.clear();
    }
}
