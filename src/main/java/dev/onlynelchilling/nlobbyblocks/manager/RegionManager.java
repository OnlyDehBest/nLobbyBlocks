package dev.onlynelchilling.nlobbyblocks.manager;

import dev.onlynelchilling.nlobbyblocks.NLobbyBlocks;
import dev.onlynelchilling.nlobbyblocks.model.Region;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class RegionManager {

    private final NLobbyBlocks plugin;
    private final List<Region> regions = new ArrayList<>();

    public RegionManager(NLobbyBlocks plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        regions.clear();

        ConfigurationSection section = plugin.getConfigManager().getRegionsSection();
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            ConfigurationSection r = section.getConfigurationSection(key);
            if (r == null) continue;

            String world = r.getString("world", "world");
            int minX = r.getInt("min.x", -100);
            int minY = r.getInt("min.y", 0);
            int minZ = r.getInt("min.z", -100);
            int maxX = r.getInt("max.x", 100);
            int maxY = r.getInt("max.y", 320);
            int maxZ = r.getInt("max.z", 100);

            regions.add(new Region(world, minX, minY, minZ, maxX, maxY, maxZ));
        }
    }

    public boolean isAllowed(Location location) {
        if (location.getWorld() == null) return false;
        if (regions.isEmpty()) return true;

        for (Region region : regions) {
            if (region.contains(location)) return true;
        }

        return false;
    }
}
