package com.fongmi.android.tv.api.rex;

import com.google.gson.JsonArray;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RexGenreDictTest {

    private static JsonArray ids(int... values) {
        JsonArray array = new JsonArray();
        for (int value : values) array.add(value);
        return array;
    }

    @Test
    public void mapsKnownIds() {
        assertEquals("科幻", RexGenreDict.nameOf(878));
        assertEquals("科幻奇幻", RexGenreDict.nameOf(10765));
        assertEquals("真人秀", RexGenreDict.nameOf(10764));
        assertEquals("", RexGenreDict.nameOf(99999));
    }

    @Test
    public void joinTakesFirstTwoIdsThenMaps() {
        assertEquals("科幻、剧情", RexGenreDict.join(ids(878, 18, 9999), 2));
        // 未收录 id 占位后丢弃：前两个 id 是 9999 与 18
        assertEquals("剧情", RexGenreDict.join(ids(9999, 18, 878), 2));
    }

    @Test
    public void joinHandlesNullAndEmpty() {
        assertEquals("", RexGenreDict.join(null, 2));
        assertEquals("", RexGenreDict.join(new JsonArray(), 2));
        assertEquals("", RexGenreDict.join(ids(878), 0));
    }

    @Test
    public void normalizeTranslatesEnglishNames() {
        assertEquals("动作、惊悚", RexGenreDict.normalize("Action, Thriller, Crime"));
        assertEquals("动画、剧情", RexGenreDict.normalize("Animation, Drama"));
        assertEquals("科幻奇幻、剧情", RexGenreDict.normalize("Sci-Fi & Fantasy, Drama"));
    }

    @Test
    public void normalizePassesChineseThrough() {
        assertEquals("剧情", RexGenreDict.normalize("剧情"));
        assertEquals("真人秀", RexGenreDict.normalize("真人秀"));
    }

    @Test
    public void normalizeKeepsUnknownNames() {
        assertEquals("Film-Noir", RexGenreDict.normalize("Film-Noir"));
    }
}
