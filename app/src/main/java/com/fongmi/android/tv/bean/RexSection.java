package com.fongmi.android.tv.bean;

import androidx.annotation.Nullable;

import com.fongmi.android.tv.api.rex.RexTmdbQuery;

/**
 * Rex 栏目定义（纯模型，不持资源）：一个栏目 = 一个数据源查询。
 *
 * <p>精选大卡行取每个栏目的一张代表卡；推荐流按栏目顺序混合拼接去重。
 * {@link #isShelf()} 为 true 的栏目还会在首页以经典书架样式同步展示（「动漫」等新板块）。
 */
public class RexSection {

    public enum Source {
        /** trending / popular / top_rated / now_playing / on_the_air。 */
        TMDB_LIST,
        /** 流派 / 平台 / 公司 discover（参数由 RexTmdbQuery 构造）。 */
        TMDB_DISCOVER,
        /** rexnow regional-tv feed：{date}/{region}_{series|variety}.json。 */
        REXNOW_REGIONAL,
        /** rexnow douban feed：douban/latest_{trending|latest}.json。 */
        REXNOW_DOUBAN,
        /** rexnow anime feed：anime/latest.json（按星期分组）。 */
        REXNOW_ANIME,
        /** 豆瓣云桥 /v1/catalog/{collectionId}。 */
        BRIDGE_CATALOG
    }

    /** 推荐流与书架每栏目最多显示条数（rex 观感一行约 20 张）。 */
    public static final int DEFAULT_LIMIT = 20;

    private String key = "";
    private int labelRes;
    private Source source = Source.TMDB_LIST;
    private RexTmdbQuery.Query query;
    private String region = "";
    private String category = "";
    private String feedCategory = "";
    private String collectionId = "";
    private int limit = DEFAULT_LIMIT;
    private boolean shelf;

    public RexSection() {
    }

    public static RexSection tmdb(String key, int labelRes, RexTmdbQuery.Query query) {
        RexSection section = new RexSection();
        section.key = key;
        section.labelRes = labelRes;
        section.query = query;
        section.source = query.path.startsWith("discover/") ? Source.TMDB_DISCOVER : Source.TMDB_LIST;
        return section;
    }

    public static RexSection regional(String key, int labelRes, String region, String category) {
        RexSection section = new RexSection();
        section.key = key;
        section.labelRes = labelRes;
        section.source = Source.REXNOW_REGIONAL;
        section.region = region;
        section.category = category;
        return section;
    }

    public static RexSection doubanFeed(String key, int labelRes, String feedCategory) {
        RexSection section = new RexSection();
        section.key = key;
        section.labelRes = labelRes;
        section.source = Source.REXNOW_DOUBAN;
        section.feedCategory = feedCategory;
        return section;
    }

    public static RexSection anime(String key, int labelRes) {
        RexSection section = new RexSection();
        section.key = key;
        section.labelRes = labelRes;
        section.source = Source.REXNOW_ANIME;
        return section;
    }

    public static RexSection bridge(String key, int labelRes, String collectionId) {
        RexSection section = new RexSection();
        section.key = key;
        section.labelRes = labelRes;
        section.source = Source.BRIDGE_CATALOG;
        section.collectionId = collectionId;
        return section;
    }

    /** 云桥不可用（无 sk / 401 / 失败）时回退的 rexnow 豆瓣 feed 类别。 */
    public RexSection doubanFallback(String feedCategory) {
        this.feedCategory = feedCategory;
        return this;
    }

    /** 同时以经典书架样式出现在首页。 */
    public RexSection shelf() {
        this.shelf = true;
        return this;
    }

    public RexSection limit(int limit) {
        this.limit = Math.max(1, limit);
        return this;
    }

    public String getKey() {
        return key;
    }

    public int getLabelRes() {
        return labelRes;
    }

    public Source getSource() {
        return source;
    }

    @Nullable
    public RexTmdbQuery.Query getQuery() {
        return query;
    }

    public String getRegion() {
        return region;
    }

    public String getCategory() {
        return category;
    }

    public String getFeedCategory() {
        return feedCategory;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public int getLimit() {
        return limit;
    }

    public boolean isShelf() {
        return shelf;
    }
}
