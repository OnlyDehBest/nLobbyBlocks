package dev.onlynelchilling.nlobbyblocks;

import co.aikar.commands.PaperCommandManager;
import dev.onlynelchilling.nlobbyblocks.command.NLBCommand;
import dev.onlynelchilling.nlobbyblocks.config.ConfigManager;
import dev.onlynelchilling.nlobbyblocks.config.MessagesProvider;
import dev.onlynelchilling.nlobbyblocks.listener.BlockBreakListener;
import dev.onlynelchilling.nlobbyblocks.listener.BlockPlaceListener;
import dev.onlynelchilling.nlobbyblocks.listener.ItemProtectionListener;
import dev.onlynelchilling.nlobbyblocks.listener.PlayerJoinListener;
import dev.onlynelchilling.nlobbyblocks.manager.BlockManager;
import dev.onlynelchilling.nlobbyblocks.manager.ItemManager;
import dev.onlynelchilling.nlobbyblocks.manager.RegionManager;
import dev.onlynelchilling.nlobbyblocks.util.EffectUtil;
import org.bukkit.plugin.java.JavaPlugin;

public class NLobbyBlocks extends JavaPlugin {

    private static NLobbyBlocks instance;

    private ConfigManager configManager;
    private MessagesProvider messagesProvider;
    private RegionManager regionManager;
    private ItemManager itemManager;
    private EffectUtil effectUtil;
    private BlockManager blockManager;

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this);
        messagesProvider = new MessagesProvider(this);
        regionManager = new RegionManager(this);
        itemManager = new ItemManager(this);
        effectUtil = new EffectUtil(this);
        blockManager = new BlockManager(this, effectUtil);

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemProtectionListener(this), this);

        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.enableUnstableAPI("help");
        commandManager.registerCommand(new NLBCommand(this));

        getLogger().info("nLobbyBlocks enabled.");
    }

    @Override
    public void onDisable() {
        if (getBlockManager() != null) {
            getBlockManager().clearAll();
        }
        getLogger().info("nLobbyBlocks disabled.");
    }

    public static NLobbyBlocks getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessagesProvider getMessagesProvider() {
        return messagesProvider;
    }

    public RegionManager getRegionManager() {
        return regionManager;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public EffectUtil getEffectUtil() {
        return effectUtil;
    }

    public BlockManager getBlockManager() {
        return blockManager;
    }
}
