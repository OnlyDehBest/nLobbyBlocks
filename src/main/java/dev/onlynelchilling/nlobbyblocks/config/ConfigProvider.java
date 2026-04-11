package dev.onlynelchilling.nlobbyblocks.config;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;

public interface ConfigProvider {

    void reload();

    boolean isBlocksOnJoin();

    boolean isPreventDrop();

    boolean isPreventMove();

    boolean isPreventOffHandSwap();

    Material getBlockMaterial();

    int getBlockCount();

    String getBlockName();

    int getHotbarSlot();

    boolean isJoinGiveDelayEnabled();

    long getJoinGiveDelay();

    int getBreakTime();

    boolean isPreventBlockInteraction();

    ConfigurationSection getRegionsSection();

    boolean isSoundEnabled(String type);

    Sound getSound(String type);

    float getSoundVolume(String type);

    float getSoundPitch(String type);
}

