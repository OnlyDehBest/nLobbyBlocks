package dev.onlynelchilling.nlobbyblocks.config;

import org.bukkit.command.CommandSender;

public interface MessageService {

    void send(CommandSender sender, String path);

    void reload();
}

