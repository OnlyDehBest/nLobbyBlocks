package dev.onlynelchilling.nlobbyblocks.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([0-9a-fA-F]{6})");

    private TextUtil() {
    }

    public static Component parse(String text) {
        if (text == null || text.isEmpty()) return Component.empty();

        String processed = convertHexCodes(text);
        processed = convertLegacyCodes(processed);

        return MINI_MESSAGE.deserialize(processed);
    }

    private static String convertHexCodes(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        if (!matcher.find()) return text;

        StringBuilder sb = new StringBuilder();
        matcher.reset();

        while (matcher.find()) {
            matcher.appendReplacement(sb, "<color:#" + matcher.group(1) + ">");
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String convertLegacyCodes(String text) {
        text = text.replace('&', '§');

        if (!text.contains("§")) return text;

        Component legacy = LEGACY_SERIALIZER.deserialize(text);
        return MINI_MESSAGE.serialize(legacy);
    }
}
