package com.fongmi.android.tv.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.net.Uri;

import androidx.media3.common.MediaItem;
import androidx.media3.extractor.DefaultExtractorsFactory;
import androidx.media3.extractor.Extractor;
import androidx.media3.extractor.ExtractorsFactory;
import androidx.media3.extractor.mkv.MatroskaExtractor;
import androidx.media3.extractor.mmt.TlvExtractor;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.fongmi.android.tv.player.subtitle.AdvancedSubtitleController;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Guards the extractor-selection fixes: the subtitle wrapper must forward the URI/header overload,
 * and the default order must try Matroska before MMT/TLV (matroska-before-mmt-tlv.patch).
 */
@RunWith(AndroidJUnit4.class)
public final class ExtractorSelectionTest {

    private static final class RecordingFactory implements ExtractorsFactory {
        Uri lastUri;
        boolean noArgCalled;

        @Override public Extractor[] createExtractors() {
            noArgCalled = true;
            return new Extractor[0];
        }

        @Override public Extractor[] createExtractors(Uri uri, Map<String, List<String>> responseHeaders) {
            lastUri = uri;
            return new Extractor[0];
        }
    }

    @Test public void subtitleWrapperForwardsUriOverloadToBaseFactory() {
        AdvancedSubtitleController controller = new AdvancedSubtitleController();
        RecordingFactory original = new RecordingFactory();
        ExtractorsFactory wrapped = controller.extractors(original, MediaItem.fromUri("file:///tmp/probe.mkv"));
        Uri uri = Uri.parse("file:///sdcard/Download/probe.mkv");
        wrapped.createExtractors(uri, Collections.emptyMap());
        assertEquals(uri, original.lastUri);
        wrapped.createExtractors();
        assertTrue(original.noArgCalled);
    }

    @Test public void uriHintSelectsMatroskaFirstForMkv() {
        Extractor[] extractors = new DefaultExtractorsFactory()
                .createExtractors(Uri.parse("file:///probe.mkv"), Collections.emptyMap());
        assertTrue(extractors.length > 0);
        assertTrue(extractors[0] instanceof MatroskaExtractor);
    }

    @Test public void defaultOrderTriesMatroskaBeforeMmtTlv() {
        Extractor[] extractors = new DefaultExtractorsFactory().createExtractors();
        int matroska = -1;
        int tlv = -1;
        for (int i = 0; i < extractors.length; i++) {
            if (extractors[i] instanceof MatroskaExtractor) matroska = i;
            if (extractors[i] instanceof TlvExtractor) tlv = i;
        }
        assertTrue(matroska >= 0);
        assertTrue(tlv >= 0);
        assertTrue("Matroska extractor must be tried before MMT/TLV", matroska < tlv);
    }
}
