package dev.onlynelchilling.nlobbyblocks.config;

import dev.onlynelchilling.nlobbyblocks.NLobbyBlocks;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.function.Supplier;

public class ConfigManager {

    private final NLobbyBlocks plugin;
    private final HashMap<String, Object> cache = new HashMap<>();
    private final ConfigFile configFile;

    public ConfigManager(NLobbyBlocks plugin) {
        this.plugin = plugin;
        this.configFile = new ConfigFile(plugin, "config");
    }

    public void reload() {
        configFile.reload();
        cache.clear();
    }

    private YamlConfiguration cfg() {
        return configFile.getConfig();
    }

    @SuppressWarnings("unchecked")
    private <T> T cached(String key, Supplier<T> loader) {
        return (T) cache.computeIfAbsent(key, k -> loader.get());
    }


    public boolean isBlocksOnJoin() {
        return cached("blocks-on-join", () -> cfg().getBoolean("blocks-on-join"));
    }

    public boolean isPreventDrop() {
        return cached("prevent-drop", () -> cfg().getBoolean("prevent-drop"));
    }

    public boolean isPreventMove() {
        return cached("prevent-move", () -> cfg().getBoolean("prevent-move"));
    }

    public boolean isPreventOffHandSwap() {
        return cached("prevent-off-hand-swap", () -> cfg().getBoolean("prevent-off-hand-swap"));
    }

    public Material getBlockMaterial() {
        return cached("block-type", () -> {
            String name = cfg().getString("block-type", "RED_SANDSTONE");
            try {
                Material m = Material.valueOf(name.toUpperCase());
                if (m.isBlock()) return m;
            } catch (IllegalArgumentException ignored) {}
            return Material.RED_SANDSTONE;
        });
    }

    public int getBlockCount() {
        return cached("block-count", () -> {
            int count = cfg().getInt("block-count", 64);
            if (count < 1) return 1;
            return Math.min(count, 64);
        });
    }

    public int getHotbarSlot() {
        return cached("hotbar-slot", () -> Math.max(0, Math.min(8, cfg().getInt("hotbar-slot"))));
    }

    public boolean isJoinGiveDelayEnabled() {
        return cached("join-give-delay.enable", () -> cfg().getBoolean("join-give-delay.enable"));
    }

    public long getJoinGiveDelay() {
        return cached("join-give-delay.delay", () -> (long) cfg().getInt("join-give-delay.delay"));
    }

    public int getBreakTime() {
        return cached("break-after", () -> cfg().getInt("break-after"));
    }

    public boolean isPreventBlockInteraction() {
        return cached("environment.prevent-block-interaction", () -> cfg().getBoolean("environment.prevent-block-interaction"));
    }

    public ConfigurationSection getRegionsSection() {
        return cfg().getConfigurationSection("regions");
    }

    public boolean isSoundEnabled(String type) {
        return cached("sounds." + type + ".enabled", () -> cfg().getBoolean("sounds." + type + ".enabled"));
    }

    public Sound getSound(String type) {
        return cached("sounds." + type + ".sound", () ->
                Sound.valueOf(cfg().getString("sounds." + type + ".sound", "BLOCK_NOTE_BLOCK_PLING").toUpperCase()));
    }

    public float getSoundVolume(String type) {
        return cached("sounds." + type + ".volume", () -> (float) cfg().getDouble("sounds." + type + ".volume", 1.0));
    }

    public float getSoundPitch(String type) {
        return cached("sounds." + type + ".pitch", () -> (float) cfg().getDouble("sounds." + type + ".pitch", 1.0));
    }

    public boolean isAnimationEnabled(String type) {
        return cached("animations." + type + ".enabled", () -> cfg().getBoolean("animations." + type + ".enabled"));
    }

    public Particle getParticle(String type) {
        return cached("animations." + type + ".particle", () ->
                Particle.valueOf(cfg().getString("animations." + type + ".particle", "POOF").toUpperCase()));
    }

    public int getParticleCount(String type) {
        return cached("animations." + type + ".count", () -> cfg().getInt("animations." + type + ".count", 10));
    }

    public double getParticleOffsetX(String type) {
        return cached("animations." + type + ".offset-x", () -> cfg().getDouble("animations." + type + ".offset-x", 0.3));
    }

    public double getParticleOffsetY(String type) {
        return cached("animations." + type + ".offset-y", () -> cfg().getDouble("animations." + type + ".offset-y", 0.3));
    }

    public double getParticleOffsetZ(String type) {
        return cached("animations." + type + ".offset-z", () -> cfg().getDouble("animations." + type + ".offset-z", 0.3));
    }

    public double getParticleSpeed(String type) {
        return cached("animations." + type + ".speed", () -> cfg().getDouble("animations." + type + ".speed", 0.05));
    }
}
