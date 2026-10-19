package com.fongmi.android.tv.ui.rex;

import com.fongmi.android.tv.bean.Vod;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RexCaptionsTest {

    private static Vod vod(String year, String genre, String type) {
        Vod item = new Vod();
        item.setId("tmdb:tv:1");
        item.setYear(year);
        item.setGenreText(genre);
        item.setTypeName(type);
        return item;
    }

    @Test
    public void joinsYearAndGenre() {
        assertEquals("2026 · 剧情、悬疑", RexCaptions.subtitle(vod("2026", "剧情、悬疑", "tv")));
    }

    @Test
    public void fallsBackToTypeLabelWhenGenreMissing() {
        assertEquals("2026 · 电影", RexCaptions.subtitle(vod("2026", "", "movie")));
        assertEquals("2026 · 剧集", RexCaptions.subtitle(vod("2026", "", "tv")));
    }

    @Test
    public void keepsRemainingPartWhenOtherMissing() {
        assertEquals("2026", RexCaptions.subtitle(vod("2026", "", "")));
        assertEquals("剧情", RexCaptions.subtitle(vod("", "剧情", "tv")));
        assertEquals("", RexCaptions.subtitle(null));
    }
}
