package dev.onlynelchilling.nlobbyblocks.manager;

import dev.onlynelchilling.nlobbyblocks.NLobbyBlocks;
import dev.onlynelchilling.nlobbyblocks.util.EffectService;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class BlockManager implements BlockService {

    private static final double CRACK_RANGE_SQ = 32.0 * 32.0;
    private static final BlockData AIR_DATA = Material.AIR.createBlockData();

    private final NLobbyBlocks plugin;
    private final EffectService effectUtil;
    private final Map<Long, BlockEntry> activeBlocks = new ConcurrentHashMap<>();
    private final Set<Player> cachedPlayers = ConcurrentHashMap.newKeySet();

    private volatile ScheduledTask timerTask;
    private int breakTime;

    private static final class BlockEntry {

        final long key;
        final Location location;
        final Block block;
        final World world;
        final double x, y, z;
        final int entityId;
        int elapsed;

        BlockEntry(long key, Location location, Block block, int entityId) {
            this.key = key;
            this.location = location;
            this.block = block;
            this.world = location.getWorld();
            this.x = location.getX();
            this.y = location.getY();
            this.z = location.getZ();
            this.entityId = entityId;
        }
    }

    public BlockManager(NLobbyBlocks plugin, EffectService effectUtil) {
        this.plugin = plugin;
        this.effectUtil = effectUtil;
        this.breakTime = plugin.getConfigManager().getBreakTime();
    }

    public void reloadConfig() {
        this.breakTime = plugin.getConfigManager().getBreakTime();
    }

    public void addPlayer(Player player) {
        cachedPlayers.add(player);
    }

    public void removePlayer(Player player) {
        cachedPlayers.remove(player);
    }

    public void registerBlock(Location location) {
        Block block = location.getBlock();
        long key = toKey(location);
        int entityId = blockEntityId(block);
        activeBlocks.put(key, new BlockEntry(key, location, block, entityId));

        if (timerTask == null) {
            startTimer();
        }
    }

    public boolean isActiveBlock(Location location) {
        if (activeBlocks.isEmpty()) return false;
        return activeBlocks.containsKey(toKey(location));
    }

    public boolean isActiveBlock(Block block) {
        if (activeBlocks.isEmpty()) return false;
        return activeBlocks.containsKey(toKey(block));
    }

    public boolean hasActiveBlocks() {
        return !activeBlocks.isEmpty();
    }

    public void clearAll() {
        stopTimer();

        for (BlockEntry entry : activeBlocks.values()) {
            if (Bukkit.isOwnedByCurrentRegion(entry.location)) {
                entry.block.setBlockData(AIR_DATA, false);
            } else {
                try {
                    plugin.getServer().getRegionScheduler().execute(plugin, entry.location, () ->
                            entry.block.setBlockData(AIR_DATA, false)
                    );
                } catch (Exception ignored) {
                }
            }
        }

        activeBlocks.clear();
    }

    private void startTimer() {
        timerTask = plugin.getServer().getAsyncScheduler().runAtFixedRate(
                plugin, task -> tick(), 1, 1, TimeUnit.SECONDS
        );
    }

    private void stopTimer() {
        ScheduledTask task = timerTask;
        if (task != null && !task.isCancelled()) {
            task.cancel();
            timerTask = null;
        }
    }

    private void tick() {
        if (activeBlocks.isEmpty()) {
            stopTimer();
            return;
        }

        int playerCount = cachedPlayers.size();
        Player[] players = playerCount == 0 ? null : new Player[playerCount];
        World[] worlds = playerCount == 0 ? null : new World[playerCount];
        double[] px = playerCount == 0 ? null : new double[playerCount];
        double[] py = playerCount == 0 ? null : new double[playerCount];
        double[] pz = playerCount == 0 ? null : new double[playerCount];

        if (players != null) {
            int i = 0;
            for (Player p : cachedPlayers) {
                if (i >= playerCount) break;
                Location l = p.getLocation();
                players[i] = p;
                worlds[i] = l.getWorld();
                px[i] = l.getX();
                py[i] = l.getY();
                pz[i] = l.getZ();
                i++;
            }
        }

        Iterator<BlockEntry> it = activeBlocks.values().iterator();
        while (it.hasNext()) {
            BlockEntry entry = it.next();
            entry.elapsed++;

            if (entry.elapsed >= breakTime) {
                it.remove();
                plugin.getServer().getRegionScheduler().execute(plugin, entry.location, () -> {
                    entry.block.setBlockData(AIR_DATA, false);
                    effectUtil.playBreak(entry.location);
                });
            } else if (players != null) {
                sendBlockCrack(entry, players, worlds, px, py, pz, playerCount);
            }
        }

        if (activeBlocks.isEmpty()) {
            stopTimer();
        }
    }

    private void sendBlockCrack(BlockEntry entry, Player[] players, World[] worlds,
                                double[] px, double[] py, double[] pz, int count) {
        float progress = (float) entry.elapsed / breakTime;
        World blockWorld = entry.world;
        double bx = entry.x, by = entry.y, bz = entry.z;

        for (int i = 0; i < count; i++) {
            if (worlds[i] != blockWorld) continue;
            double dx = px[i] - bx;
            double dy = py[i] - by;
            double dz = pz[i] - bz;
            if (dx * dx + dy * dy + dz * dz <= CRACK_RANGE_SQ) {
                players[i].sendBlockDamage(entry.location, progress, entry.entityId);
            }
        }
    }

    private static long toKey(Location loc) {
        return packKey(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    private static long toKey(Block block) {
        return packKey(block.getX(), block.getY(), block.getZ());
    }

    private static long packKey(int x, int y, int z) {
        return ((long) x & 0x3FFFFFFL) << 38
                | ((long) z & 0x3FFFFFFL) << 12
                | ((long) y & 0xFFFL);
    }

    private static int blockEntityId(Block block) {
        return (block.getX() & 4095) << 20
                | (block.getZ() & 4095) << 8
                | block.getY() & 255;
    }
}
