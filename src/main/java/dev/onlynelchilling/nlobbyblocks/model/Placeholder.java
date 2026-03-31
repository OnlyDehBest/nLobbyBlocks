package dev.onlynelchilling.nlobbyblocks.model;

public record Placeholder(String key, String value) {

    public static Placeholder of(String key, String value) {
        return new Placeholder(key, value);
    }
}

