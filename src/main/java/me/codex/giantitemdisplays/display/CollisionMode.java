package me.codex.giantitemdisplays.display;

import java.util.Locale;

public enum CollisionMode {
    NONE,
    INTERACTION,
    BARRIER;

    public static CollisionMode fromString(String value) {
        if (value == null) {
            return INTERACTION;
        }
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "none" -> NONE;
            case "barrier" -> BARRIER;
            default -> INTERACTION;
        };
    }

    public static boolean isValid(String value) {
        if (value == null) {
            return false;
        }
        String normalized = value.toLowerCase(Locale.ROOT);
        return normalized.equals("none") || normalized.equals("interaction") || normalized.equals("barrier");
    }

    public String configName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
