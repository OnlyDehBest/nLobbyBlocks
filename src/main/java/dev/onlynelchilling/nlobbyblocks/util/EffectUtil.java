package dev.onlynelchilling.nlobbyblocks.util;

import dev.onlynelchilling.nlobbyblocks.NLobbyBlocks;
import dev.onlynelchilling.nlobbyblocks.config.ConfigManager;
import org.bukkit.Location;
import org.bukkit.Sound;

public class EffectUtil {

    private final NLobbyBlocks plugin;

    private boolean placeSoundEnabled;
    private Sound placeSound;
    private float placeSoundVolume;
    private float placeSoundPitch;

    private boolean breakSoundEnabled;
    private Sound breakSound;
    private float breakSoundVolume;
    private float breakSoundPitch;

    public EffectUtil(NLobbyBlocks plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        ConfigManager cm = plugin.getConfigManager();

        placeSoundEnabled = cm.isSoundEnabled("place");
        placeSound        = cm.getSound("place");
        placeSoundVolume  = cm.getSoundVolume("place");
        placeSoundPitch   = cm.getSoundPitch("place");

        breakSoundEnabled = cm.isSoundEnabled("break");
        breakSound        = cm.getSound("break");
        breakSoundVolume  = cm.getSoundVolume("break");
        breakSoundPitch   = cm.getSoundPitch("break");
    }

    public void playPlace(Location location) {
        if (!placeSoundEnabled || location.getWorld() == null) return;
        location.getWorld().playSound(location, placeSound, placeSoundVolume, placeSoundPitch);
    }

    public void playBreak(Location location) {
        if (!breakSoundEnabled || location.getWorld() == null) return;
        location.getWorld().playSound(location, breakSound, breakSoundVolume, breakSoundPitch);
    }
}
