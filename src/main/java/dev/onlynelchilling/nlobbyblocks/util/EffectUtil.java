package dev.onlynelchilling.nlobbyblocks.util;

import dev.onlynelchilling.nlobbyblocks.NLobbyBlocks;
import dev.onlynelchilling.nlobbyblocks.config.ConfigManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

public class EffectUtil {

    private final NLobbyBlocks plugin;

    private boolean placeSoundEnabled;
    private Sound placeSound;
    private float placeSoundVolume, placeSoundPitch;

    private boolean breakSoundEnabled;
    private Sound breakSound;
    private float breakSoundVolume, breakSoundPitch;

    private boolean placeParticleEnabled;
    private Particle placeParticle;
    private int placeParticleCount;
    private double placeParticleOX, placeParticleOY, placeParticleOZ, placeParticleSpeed;

    private boolean breakParticleEnabled;
    private int breakParticleCount;
    private double breakParticleOX, breakParticleOY, breakParticleOZ;

    public EffectUtil(NLobbyBlocks plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        ConfigManager cm = plugin.getConfigManager();

        placeSoundEnabled  = cm.isSoundEnabled("place");
        placeSound         = cm.getSound("place");
        placeSoundVolume   = cm.getSoundVolume("place");
        placeSoundPitch    = cm.getSoundPitch("place");

        breakSoundEnabled  = cm.isSoundEnabled("break");
        breakSound         = cm.getSound("break");
        breakSoundVolume   = cm.getSoundVolume("break");
        breakSoundPitch    = cm.getSoundPitch("break");

        placeParticleEnabled = cm.isAnimationEnabled("place");
        placeParticle        = cm.getParticle("place");
        placeParticleCount   = cm.getParticleCount("place");
        placeParticleOX      = cm.getParticleOffsetX("place");
        placeParticleOY      = cm.getParticleOffsetY("place");
        placeParticleOZ      = cm.getParticleOffsetZ("place");
        placeParticleSpeed   = cm.getParticleSpeed("place");

        breakParticleEnabled = cm.isAnimationEnabled("break");
        breakParticleCount   = cm.getParticleCount("break");
        breakParticleOX      = cm.getParticleOffsetX("break");
        breakParticleOY      = cm.getParticleOffsetY("break");
        breakParticleOZ      = cm.getParticleOffsetZ("break");
    }

    public void playPlace(Location location) {
        if (!placeSoundEnabled || location.getWorld() == null) return;
        location.getWorld().playSound(location, placeSound, placeSoundVolume, placeSoundPitch);
    }

    public void playBreak(Location location) {
        if (!breakSoundEnabled || location.getWorld() == null) return;
        location.getWorld().playSound(location, breakSound, breakSoundVolume, breakSoundPitch);
    }

    public void spawnPlaceParticles(Location location) {
        if (!placeParticleEnabled || location.getWorld() == null) return;
        try {
            placeParticle.builder()
                    .location(new Location(location.getWorld(),
                            location.getX() + 0.5, location.getY() + 0.5, location.getZ() + 0.5))
                    .count(placeParticleCount)
                    .offset(placeParticleOX, placeParticleOY, placeParticleOZ)
                    .extra(placeParticleSpeed)
                    .receivers(16, true)
                    .spawn();
        } catch (Exception ignored) {}
    }

    public void spawnBreakParticles(Location location, Material blockMaterial) {
        if (!breakParticleEnabled || location.getWorld() == null) return;
        try {
            Particle.BLOCK.builder()
                    .location(new Location(location.getWorld(),
                            location.getX() + 0.5, location.getY() + 0.5, location.getZ() + 0.5))
                    .count(breakParticleCount)
                    .offset(breakParticleOX, breakParticleOY, breakParticleOZ)
                    .data(blockMaterial.createBlockData())
                    .receivers(16, true)
                    .spawn();
        } catch (Exception ignored) {}
    }
}
