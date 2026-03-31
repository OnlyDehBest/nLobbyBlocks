package dev.onlynelchilling.nlobbyblocks.util;

import dev.onlynelchilling.nlobbyblocks.NLobbyBlocks;
import org.bukkit.command.CommandSender;

public final class MessagesUtils {

    private MessagesUtils() {}

    public static void send(CommandSender sender, String path) {
        NLobbyBlocks.getInstance().getMessagesProvider().send(sender, path);
    }
}
