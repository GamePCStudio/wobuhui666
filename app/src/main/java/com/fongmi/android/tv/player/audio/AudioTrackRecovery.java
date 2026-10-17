package com.fongmi.android.tv.player.audio;

import androidx.media3.common.C;
import androidx.media3.common.Tracks;

/**
 * Decides what to do when the media carries an audio track that the current engine cannot render.
 *
 * <p>Exo only sees the platform MediaCodec decoders and the device's passthrough capabilities. When
 * the selected audio track is a format the box cannot decode (E-AC3 and other bitstream formats are
 * common), Media3 disables the audio renderer: video plays, no error is raised and the user hears
 * nothing at all. MPV decodes such formats with its bundled FFmpeg, so playback can be recovered
 * instead of staying silent.
 *
 * <p>The decision uses only reported track state and two environment flags, so it is unit tested
 * without a device or a codec.
 */
public final class AudioTrackRecovery {

    /** Recovery step to apply to the current media item. */
    public enum Action {
        NONE,
        RESELECT_TRACK,
        SWITCH_ENGINE,
        NOTIFY_ONLY
    }

    /** Recovery steps that may be applied to a single media item before giving up. */
    public static final int MAX_ATTEMPTS = 3;

    private AudioTrackRecovery() {
    }

    /**
     * Whether the media advertises audio that nothing is currently rendering. A track the user
     * disabled on purpose is not a fault, so callers must rule that out first.
     */
    public static boolean hasUnrenderedAudio(Tracks tracks) {
        if (tracks == null || !tracks.containsType(C.TRACK_TYPE_AUDIO)) return false;
        return !tracks.isTypeSelected(C.TRACK_TYPE_AUDIO);
    }

    /**
     * @param tracks track information reported by the player
     * @param audioDisabledByUser whether the user disabled the audio track type in the selection
     *     parameters, which means silence is intended
     * @param engineIsExo whether the current engine is Exo, the only engine whose renderer list is
     *     limited to platform decoders
     * @param softwareEngineUsable whether the software engine (MPV) can be created on this device
     * @param attempts recovery steps already applied to the current media item
     */
    public static Action decide(Tracks tracks, boolean audioDisabledByUser, boolean engineIsExo,
            boolean softwareEngineUsable, int attempts) {
        if (audioDisabledByUser || attempts >= MAX_ATTEMPTS) return Action.NONE;
        if (!hasUnrenderedAudio(tracks)) return Action.NONE;
        // Only Exo builds its audio renderers from platform decoders, and the software engine is the
        // last resort, so nothing here applies once the software engine is the one playing: its own
        // track reporting must not trigger another switch or a misleading notice.
        if (!engineIsExo) return Action.NONE;
        // A playable track exists but a saved or forced selection kept it out of the renderer.
        if (attempts == 0 && tracks.isTypeSupported(C.TRACK_TYPE_AUDIO)) return Action.RESELECT_TRACK;
        if (softwareEngineUsable) return Action.SWITCH_ENGINE;
        return Action.NOTIFY_ONLY;
    }
}
