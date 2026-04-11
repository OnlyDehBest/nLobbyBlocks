package dev.onlynelchilling.nlobbyblocks;

import co.aikar.commands.PaperCommandManager;
import dev.onlynelchilling.nlobbyblocks.command.NLBCommand;
import dev.onlynelchilling.nlobbyblocks.config.ConfigManager;
import dev.onlynelchilling.nlobbyblocks.config.ConfigProvider;
import dev.onlynelchilling.nlobbyblocks.config.MessageService;
import dev.onlynelchilling.nlobbyblocks.config.MessagesProvider;
import dev.onlynelchilling.nlobbyblocks.listener.BlockBreakListener;
import dev.onlynelchilling.nlobbyblocks.listener.BlockPlaceListener;
import dev.onlynelchilling.nlobbyblocks.listener.ItemProtectionListener;
import dev.onlynelchilling.nlobbyblocks.listener.PlayerJoinListener;
import dev.onlynelchilling.nlobbyblocks.manager.BlockManager;
import dev.onlynelchilling.nlobbyblocks.manager.BlockService;
import dev.onlynelchilling.nlobbyblocks.manager.ItemManager;
import dev.onlynelchilling.nlobbyblocks.manager.ItemService;
import dev.onlynelchilling.nlobbyblocks.manager.RegionManager;
import dev.onlynelchilling.nlobbyblocks.manager.RegionService;
import dev.onlynelchilling.nlobbyblocks.util.EffectService;
import dev.onlynelchilling.nlobbyblocks.util.EffectUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class NLobbyBlocks extends JavaPlugin {

    private ConfigProvider configManager;
    private MessageService messagesProvider;
    private ItemService itemManager;
    private BlockService blockManager;
    private RegionService regionManager;
    private EffectService effectUtil;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        messagesProvider = new MessagesProvider(this);
        regionManager = new RegionManager(this);
        itemManager = new ItemManager(this);
        effectUtil = new EffectUtil(this);
        blockManager = new BlockManager(this, effectUtil);

        registerListeners();
        registerCommands();

        for (Player player : getServer().getOnlinePlayers()) {
            blockManager.addPlayer(player);
        }

        getLogger().info("nLobbyBlocks enabled.");
    }

    @Override
    public void onDisable() {
        if (blockManager != null) {
            blockManager.clearAll();
        }
        getLogger().info("nLobbyBlocks disabled.");
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new PlayerJoinListener(this), this);
        pm.registerEvents(new BlockPlaceListener(this), this);
        pm.registerEvents(new BlockBreakListener(this), this);
        pm.registerEvents(new ItemProtectionListener(this), this);
    }

    private void registerCommands() {
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.enableUnstableAPI("help");
        commandManager.registerCommand(new NLBCommand(this));
    }

    public ConfigProvider getConfigManager() {
        return configManager;
    }

    public MessageService getMessagesProvider() {
        return messagesProvider;
    }

    public ItemService getItemManager() {
        return itemManager;
    }

    public BlockService getBlockManager() {
        return blockManager;
    }

    public RegionService getRegionManager() {
        return regionManager;
    }

    public EffectService getEffectUtil() {
        return effectUtil;
    }
}
