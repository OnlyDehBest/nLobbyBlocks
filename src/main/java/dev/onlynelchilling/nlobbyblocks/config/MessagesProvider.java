package dev.onlynelchilling.nlobbyblocks.config;

import dev.onlynelchilling.nlobbyblocks.NLobbyBlocks;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;

public class MessagesProvider {

    private final NLobbyBlocks plugin;
    private YamlConfiguration messages;
    private final HashMap<String, String> cache = new HashMap<>();

    public MessagesProvider(NLobbyBlocks plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        cache.clear();
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(file);
        var stream = plugin.getResource("messages.yml");
        if (stream != null) {
            messages.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(stream)));
        }
    }

    public void reload() {
        load();
    }

    private String getRaw(String path) {
        return cache.computeIfAbsent(path, k -> {
            String prefix = messages.getString("prefix", "");
            String value = messages.getString(k, "§c[Missing: " + k + "]");
            return value.replace("{prefix}", prefix);
        });
    }

    public void send(CommandSender sender, String path) {
        sender.sendMessage(LegacyComponentSerializer.legacySection().deserialize(getRaw(path)));
    }
}
