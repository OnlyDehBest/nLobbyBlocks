package dev.onlynelchilling.nlobbyblocks.manager;

import dev.onlynelchilling.nlobbyblocks.NLobbyBlocks;
import dev.onlynelchilling.nlobbyblocks.util.EffectUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BlockManager {

    private static final double CRACK_RANGE_SQ = 32.0 * 32.0;
    private static final BlockData AIR_DATA = Material.AIR.createBlockData();

    private final NLobbyBlocks plugin;
    private final EffectUtil effectUtil;
    private final Map<String, BlockEntry> activeBlocks = new ConcurrentHashMap<>();

    private volatile BukkitTask timerTask;
    private int breakTime;

    private static final class BlockEntry {

        final String key;
        final Location location;
        final int entityId;
        int elapsed;

        BlockEntry(String key, Location location, int entityId) {
            this.key = key;
            this.location = location;
            this.entityId = entityId;
        }
    }

    public BlockManager(NLobbyBlocks plugin, EffectUtil effectUtil) {
        this.plugin = plugin;
        this.effectUtil = effectUtil;
        this.breakTime = plugin.getConfigManager().getBreakTime();
    }

    public void reloadConfig() {
        this.breakTime = plugin.getConfigManager().getBreakTime();
    }

    public void registerBlock(Location location) {
        String key = toKey(location);
        int entityId = blockEntityId(location.getBlock());
        activeBlocks.put(key, new BlockEntry(key, location, entityId));

        if (timerTask == null) {
            startTimer();
        }
    }

    public boolean isActiveBlock(Location location) {
        return activeBlocks.containsKey(toKey(location));
    }

    public void clearAll() {
        stopTimer();

        for (BlockEntry entry : activeBlocks.values()) {
            entry.location.getBlock().setBlockData(AIR_DATA, false);
        }

        activeBlocks.clear();
    }

    private void startTimer() {
        timerTask = new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        }.runTaskTimerAsynchronously(plugin, 20L, 20L);
    }

    private void stopTimer() {
        BukkitTask task = timerTask;
        if (task != null) {
            task.cancel();
            timerTask = null;
        }
    }

    private void tick() {
        if (activeBlocks.isEmpty()) {
            stopTimer();
            return;
        }

        List<BlockEntry> toBreak = null;

        for (BlockEntry entry : activeBlocks.values()) {
            entry.elapsed++;

            if (entry.elapsed >= breakTime) {
                if (toBreak == null) toBreak = new ArrayList<>(4);
                toBreak.add(entry);
            } else {
                sendBlockCrack(entry);
            }
        }

        if (toBreak != null) {
            for (BlockEntry entry : toBreak) {
                activeBlocks.remove(entry.key);
                resetCrackAnimation(entry);
                effectUtil.playBreak(entry.location);
                entry.location.getBlock().setBlockData(AIR_DATA, false);
            }
        }

        if (activeBlocks.isEmpty()) {
            stopTimer();
        }
    }

    private void sendBlockCrack(BlockEntry entry) {
        float progress = (float) entry.elapsed / breakTime;
        List<? extends Player> players = entry.location.getWorld().getPlayers();

        for (int i = 0, size = players.size(); i < size; i++) {
            Player player = players.get(i);
            if (player.getLocation().distanceSquared(entry.location) <= CRACK_RANGE_SQ) {
                player.sendBlockDamage(entry.location, progress, entry.entityId);
            }
        }
    }

    private void resetCrackAnimation(BlockEntry entry) {
        List<? extends Player> players = entry.location.getWorld().getPlayers();

        for (int i = 0, size = players.size(); i < size; i++) {
            Player player = players.get(i);
            if (player.getLocation().distanceSquared(entry.location) <= CRACK_RANGE_SQ) {
                player.sendBlockDamage(entry.location, 0f, entry.entityId);
            }
        }
    }

    private static String toKey(Location loc) {
        return loc.getWorld().getName()
                + ":" + loc.getBlockX()
                + ":" + loc.getBlockY()
                + ":" + loc.getBlockZ();
    }

    private static int blockEntityId(Block block) {
        return (block.getX() & 4095) << 20
                | (block.getZ() & 4095) << 8
                | block.getY() & 255;
    }
}
