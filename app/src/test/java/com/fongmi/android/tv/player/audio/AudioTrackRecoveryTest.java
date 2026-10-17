package com.fongmi.android.tv.player.audio;

import androidx.media3.common.C;
import androidx.media3.common.Format;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.TrackGroup;
import androidx.media3.common.Tracks;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AudioTrackRecoveryTest {

    private static final int AUDIO = C.TRACK_TYPE_AUDIO;

    private static Tracks tracks(boolean withAudio, int audioSupport, boolean audioSelected) {
        List<Tracks.Group> groups = new ArrayList<>();
        TrackGroup video = new TrackGroup(new Format.Builder().setSampleMimeType(MimeTypes.VIDEO_H264).setWidth(640).setHeight(360).build());
        groups.add(new Tracks.Group(video, false, new int[]{C.FORMAT_HANDLED}, new boolean[]{true}));
        if (withAudio) {
            TrackGroup audio = new TrackGroup(new Format.Builder().setSampleMimeType(MimeTypes.AUDIO_E_AC3).setChannelCount(6).setSampleRate(48000).build());
            groups.add(new Tracks.Group(audio, false, new int[]{audioSupport}, new boolean[]{audioSelected}));
        }
        return new Tracks(groups);
    }

    @Test
    public void silentBecauseMediaHasNoAudioTrackIsNotAFault() {
        Tracks tracks = tracks(false, C.FORMAT_HANDLED, false);
        assertFalse(AudioTrackRecovery.hasUnrenderedAudio(tracks));
        assertEquals(AudioTrackRecovery.Action.NONE, AudioTrackRecovery.decide(tracks, false, true, true, 0));
    }

    @Test
    public void playingAudioIsLeftAlone() {
        Tracks tracks = tracks(true, C.FORMAT_HANDLED, true);
        assertFalse(AudioTrackRecovery.hasUnrenderedAudio(tracks));
        assertEquals(AudioTrackRecovery.Action.NONE, AudioTrackRecovery.decide(tracks, false, true, true, 0));
    }

    @Test
    public void userDisabledAudioIsRespected() {
        Tracks tracks = tracks(true, C.FORMAT_UNSUPPORTED_TYPE, false);
        assertEquals(AudioTrackRecovery.Action.NONE, AudioTrackRecovery.decide(tracks, true, true, true, 0));
    }

    @Test
    public void unselectedButSupportedAudioIsReselected() {
        Tracks tracks = tracks(true, C.FORMAT_HANDLED, false);
        assertTrue(AudioTrackRecovery.hasUnrenderedAudio(tracks));
        assertEquals(AudioTrackRecovery.Action.RESELECT_TRACK, AudioTrackRecovery.decide(tracks, false, true, true, 0));
    }

    @Test
    public void stillUnselectedAfterReselectSwitchesToTheSoftwareEngine() {
        Tracks tracks = tracks(true, C.FORMAT_UNSUPPORTED_TYPE, false);
        assertEquals(AudioTrackRecovery.Action.SWITCH_ENGINE, AudioTrackRecovery.decide(tracks, false, true, true, 1));
    }

    @Test
    public void undecodableAudioSwitchesToTheSoftwareEngine() {
        Tracks tracks = tracks(true, C.FORMAT_UNSUPPORTED_TYPE, false);
        assertEquals(AudioTrackRecovery.Action.SWITCH_ENGINE, AudioTrackRecovery.decide(tracks, false, true, true, 0));
    }

    @Test
    public void withoutTheSoftwareEngineTheUserIsToldInstead() {
        Tracks tracks = tracks(true, C.FORMAT_UNSUPPORTED_TYPE, false);
        assertEquals(AudioTrackRecovery.Action.NOTIFY_ONLY, AudioTrackRecovery.decide(tracks, false, true, false, 0));
    }

    @Test
    public void softwareEnginePlaybackIsNotFlippedBackToExo() {
        Tracks tracks = tracks(true, C.FORMAT_UNSUPPORTED_TYPE, false);
        assertEquals(AudioTrackRecovery.Action.NOTIFY_ONLY, AudioTrackRecovery.decide(tracks, false, false, false, 0));
    }

    @Test
    public void recoveryStopsAfterItsAttemptsAreUsedUp() {
        Tracks tracks = tracks(true, C.FORMAT_UNSUPPORTED_TYPE, false);
        assertEquals(AudioTrackRecovery.Action.NONE,
                AudioTrackRecovery.decide(tracks, false, true, true, AudioTrackRecovery.MAX_ATTEMPTS));
    }
}
