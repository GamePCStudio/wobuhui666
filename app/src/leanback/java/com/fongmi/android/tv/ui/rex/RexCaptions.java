package com.fongmi.android.tv.ui.rex;

import com.fongmi.android.tv.bean.DiscoverMediaKey;
import com.fongmi.android.tv.bean.Vod;

/** 小卡副题：「年份 · 类型」（类型 = 中文流派，缺失回退 电影/剧集；两者皆缺只留年份）。 */
public final class RexCaptions {

    private RexCaptions() {
    }

    public static String subtitle(Vod item) {
        if (item == null) return "";
        String year = item.getYear();
        String genre = item.getGenreText();
        if (genre.isEmpty()) genre = typeLabel(item.getTypeName());
        if (year.isEmpty()) return genre;
        if (genre.isEmpty()) return year;
        return year + " · " + genre;
    }

    static String typeLabel(String mediaType) {
        if (DiscoverMediaKey.MOVIE.equals(mediaType)) return "电影";
        if (DiscoverMediaKey.TV.equals(mediaType)) return "剧集";
        return "";
    }
}
