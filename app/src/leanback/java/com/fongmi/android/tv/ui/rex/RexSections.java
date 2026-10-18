package com.fongmi.android.tv.ui.rex;

import com.fongmi.android.tv.R;
import com.fongmi.android.tv.api.rex.RexTmdbQuery;
import com.fongmi.android.tv.bean.RexSection;

import java.util.ArrayList;
import java.util.List;

/**
 * Rex 栏目清单（首页与推荐页共用，顺序即精选大卡行与推荐流的展示顺序）。
 *
 * <p>数据来源：TMDB（走应用反代）+ rexnow 公开 feed + 豆瓣云桥；
 * {@code shelf()} 标记的栏目同时以经典书架样式出现在首页（「动漫」等新板块）。
 */
public final class RexSections {

    private RexSections() {
    }

    public static List<RexSection> home() {
        List<RexSection> sections = new ArrayList<>();
        sections.add(RexSection.tmdb("trending_day", R.string.rex_section_trending_day, RexTmdbQuery.trending("day")));
        sections.add(RexSection.tmdb("now_playing", R.string.rex_section_now_playing, RexTmdbQuery.nowPlaying()));
        sections.add(RexSection.tmdb("popular_movie", R.string.rex_section_popular_movie, RexTmdbQuery.popular(RexTmdbQuery.MOVIE)));
        sections.add(RexSection.tmdb("popular_tv", R.string.rex_section_popular_tv, RexTmdbQuery.popular(RexTmdbQuery.TV)));
        sections.add(RexSection.tmdb("top_movie", R.string.rex_section_top_movie, RexTmdbQuery.topRated(RexTmdbQuery.MOVIE)));
        sections.add(RexSection.tmdb("top_tv", R.string.rex_section_top_tv, RexTmdbQuery.topRated(RexTmdbQuery.TV)));
        sections.add(RexSection.bridge("douban_movie", R.string.rex_section_douban_movie, "movie_hot_gaia").doubanFallback("trending"));
        sections.add(RexSection.bridge("douban_tv", R.string.rex_section_douban_tv, "tv_hot").doubanFallback("trending"));
        sections.add(RexSection.regional("cn_series", R.string.rex_section_cn_series, "chinese", "series").shelf());
        sections.add(RexSection.regional("kr_series", R.string.rex_section_kr_series, "korean", "series").shelf());
        sections.add(RexSection.regional("cn_variety", R.string.rex_section_cn_variety, "chinese", "variety").shelf());
        sections.add(RexSection.anime("anime_daily", R.string.rex_section_anime).shelf());
        return sections;
    }

    /** 按 key 查栏目（栏目列表承接页用）。 */
    public static RexSection byKey(String key) {
        if (key == null) return null;
        for (RexSection section : home()) if (section.getKey().equals(key)) return section;
        return null;
    }
}
