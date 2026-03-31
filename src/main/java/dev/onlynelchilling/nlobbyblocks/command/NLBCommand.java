package dev.onlynelchilling.nlobbyblocks.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import dev.onlynelchilling.nlobbyblocks.NLobbyBlocks;
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
        plugin.getMessagesProvider().reload();
        plugin.getRegionManager().load();
        plugin.getItemManager().invalidateCache();
        plugin.getBlockManager().reloadConfig();
        plugin.getEffectUtil().reload();

        plugin.getMessagesProvider().send(sender, "command-reload");
    }
}
