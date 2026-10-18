package com.github.catvod.net.ech;

import com.github.catvod.utils.Prefers;

public final class EchSettings {

    private static final String KEY_ENABLED = "ech_enabled";

    private EchSettings() {
    }

    /** ECH is on by default; a stored preference always wins. */
    public static boolean isEnabled() {
        return Prefers.getBoolean(KEY_ENABLED, true);
    }

    public static void setEnabled(boolean enabled) {
        Prefers.put(KEY_ENABLED, enabled);
    }
}
