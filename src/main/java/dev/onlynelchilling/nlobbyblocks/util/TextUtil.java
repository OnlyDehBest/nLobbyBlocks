package dev.onlynelchilling.nlobbyblocks.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextUtil {

    private static final MiniMessage MINI = MiniMessage.miniMessage();

    private static final Pattern HEX_PATTERN = Pattern.compile("[&§]#([0-9a-fA-F]{6})");
    private static final Pattern LEGACY_PATTERN = Pattern.compile("[&§]([0-9a-fk-orA-FK-OR])");

    private static final String RESET_FMT = "<!bold><!italic><!obfuscated><!strikethrough><!underlined>";

    private static final Map<Character, String> LEGACY_MAP = Map.ofEntries(
            Map.entry('0', RESET_FMT + "<black>"),
            Map.entry('1', RESET_FMT + "<dark_blue>"),
            Map.entry('2', RESET_FMT + "<dark_green>"),
            Map.entry('3', RESET_FMT + "<dark_aqua>"),
            Map.entry('4', RESET_FMT + "<dark_red>"),
            Map.entry('5', RESET_FMT + "<dark_purple>"),
            Map.entry('6', RESET_FMT + "<gold>"),
            Map.entry('7', RESET_FMT + "<gray>"),
            Map.entry('8', RESET_FMT + "<dark_gray>"),
            Map.entry('9', RESET_FMT + "<blue>"),
            Map.entry('a', RESET_FMT + "<green>"),
            Map.entry('b', RESET_FMT + "<aqua>"),
            Map.entry('c', RESET_FMT + "<red>"),
            Map.entry('d', RESET_FMT + "<light_purple>"),
            Map.entry('e', RESET_FMT + "<yellow>"),
            Map.entry('f', RESET_FMT + "<white>"),
            Map.entry('k', "<obfuscated>"),
            Map.entry('l', "<bold>"),
            Map.entry('m', "<strikethrough>"),
            Map.entry('n', "<underlined>"),
            Map.entry('o', "<italic>"),
            Map.entry('r', RESET_FMT + "<white>")
    );

    private TextUtil() {
    }

    public static Component parse(String text) {
        if (text == null || text.isEmpty()) return Component.empty();

        String processed = convertHexCodes(text);
        processed = convertLegacyCodes(processed);

        return MINI.deserialize(processed)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, false);
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
        Matcher matcher = LEGACY_PATTERN.matcher(text);
        if (!matcher.find()) return text;

        StringBuilder sb = new StringBuilder();
        matcher.reset();

        while (matcher.find()) {
            char code = Character.toLowerCase(matcher.group(1).charAt(0));
            String replacement = LEGACY_MAP.getOrDefault(code, "");
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }
}
