package dev.onlynelchilling.nlobbyblocks.config;

import dev.onlynelchilling.nlobbyblocks.NLobbyBlocks;
import dev.onlynelchilling.nlobbyblocks.util.TextUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;

public class MessagesProvider {

    private final NLobbyBlocks plugin;
    private final HashMap<String, String> cache = new HashMap<>();
    private YamlConfiguration messages;

    public MessagesProvider(NLobbyBlocks plugin) {
        this.plugin = plugin;
        load();
    }

    public void reload() {
        load();
    }

    private void load() {
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

    public void send(CommandSender sender, String path) {
        sender.sendMessage(TextUtil.parse(getRaw(path)));
    }

    private String getRaw(String path) {
        return cache.computeIfAbsent(path, key -> {
            String prefix = messages.getString("prefix", "");
            String value = messages.getString(key, "§c[Missing: " + key + "]");
            return value.replace("{prefix}", prefix);
        });
    }
}
