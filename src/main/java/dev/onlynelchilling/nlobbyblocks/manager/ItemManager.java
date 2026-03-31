package dev.onlynelchilling.nlobbyblocks.manager;

import dev.onlynelchilling.nlobbyblocks.NLobbyBlocks;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ItemManager {

    public static final String NBT_KEY = "nlb_lobby_block";

    private final NLobbyBlocks plugin;
    private final NamespacedKey key;
    private ItemStack cachedItem;
    private Material blockMaterial;

    public ItemManager(NLobbyBlocks plugin) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, NBT_KEY);
        this.blockMaterial = plugin.getConfigManager().getBlockMaterial();
    }

    public void invalidateCache() {
        cachedItem = null;
        this.blockMaterial = plugin.getConfigManager().getBlockMaterial();
    }

    public ItemStack createLobbyBlock() {
        if (cachedItem != null) return cachedItem.clone();
        Material material = plugin.getConfigManager().getBlockMaterial();
        int count = plugin.getConfigManager().getBlockCount();
        ItemStack item = ItemStack.of(material, count);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        String name = plugin.getConfigManager().getBlockName();
        if (name != null && !name.isEmpty()) {
            meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(name));
        }
        meta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        cachedItem = item;
        return item.clone();
    }

    public boolean isLobbyBlock(ItemStack item) {
        if (item == null) return false;

        if (item.getType() != blockMaterial) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer().has(key);
    }

    public void giveLobbyBlock(Player player) {
        int count = plugin.getConfigManager().getBlockCount();

        int heldSlot = player.getInventory().getHeldItemSlot();
        ItemStack heldItem = player.getInventory().getItem(heldSlot);
        if (heldItem != null && isLobbyBlock(heldItem)) {
            heldItem.setAmount(count);
            player.getInventory().setItem(heldSlot, heldItem);
            return;
        }

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            if (i == heldSlot) continue;
            ItemStack item = player.getInventory().getItem(i);
            if (item == null) continue;
            if (isLobbyBlock(item)) {
                item.setAmount(count);
                player.getInventory().setItem(i, item);
                return;
            }
        }

        int slot = plugin.getConfigManager().getHotbarSlot();
        ItemStack current = player.getInventory().getItem(slot);
        if (current != null && current.getType() != Material.AIR) {
            player.getInventory().addItem(createLobbyBlock());
        } else {
            player.getInventory().setItem(slot, createLobbyBlock());
        }
    }
}
