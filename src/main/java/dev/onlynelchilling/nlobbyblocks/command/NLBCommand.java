package dev.onlynelchilling.nlobbyblocks.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.onlynelchilling.nlobbyblocks.NLobbyBlocks;
import dev.onlynelchilling.nlobbyblocks.util.MessagesUtils;
import org.bukkit.command.CommandSender;

@CommandAlias("nlb|nlobbyblocks")
@CommandPermission("nlobbyblocks.admin")
public class NLBCommand extends BaseCommand {

    private final NLobbyBlocks plugin;

    public NLBCommand(NLobbyBlocks plugin) {
        this.plugin = plugin;
    }

    @Subcommand("reload")
    @Description("Reload configuration and messages")
    public void onReload(CommandSender sender) {
        plugin.getConfigManager().reload();
        plugin.getRegionManager().load();
        plugin.getItemManager().invalidateCache();
        plugin.getBlockManager().reloadConfig();
        plugin.getEffectUtil().reload();
        plugin.getMessagesProvider().reload();
        MessagesUtils.send(sender, "command-reload");
    }
}
