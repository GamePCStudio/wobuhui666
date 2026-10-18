package com.github.catvod.net.ech;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EchSettingsTest {
    @Test
    public void echIsEnabledWhenThePreferenceKeyIsAbsent() {
        // Host tests install no Android context, so Prefers must fall back to the caller default.
        assertTrue(EchSettings.isEnabled());
    }
}
