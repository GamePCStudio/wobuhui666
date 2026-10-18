package com.fongmi.android.tv.api.rex;

import androidx.annotation.Nullable;

import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 流派字典（移植自 rex-tmdb.js 的 genreTitleWith）。
 *
 * <p>两种用法：
 * <ul>
 *   <li>{@link #join(JsonArray, int)}：TMDB 列表接口的 genre_ids → 中文名（rex 语义：先取前 N 个
 *       id 再映射，未收录的 id 丢弃）；</li>
 *   <li>{@link #normalize(String)}：rexnow enriched feed 里 tmdb_info.genreTitle（多为英文逗号
 *       分隔的英文名）→ 中文名。</li>
 * </ul>
 *
 * <p>注：rex 原文对 10765 / 10768 保留英文（Sci-Fi &amp; Fantasy / War &amp; Politics），
 * 这里按中文界面习惯译为「科幻奇幻 / 战争政治」。
 */
public final class RexGenreDict {

    /** 卡片副题最多展示两个流派（与 rex slice(0, 2) 一致）。 */
    public static final int DEFAULT_MAX = 2;

    private static final Map<Integer, String> ID_NAMES = new LinkedHashMap<>();
    private static final Map<String, String> ALIASES = new HashMap<>();

    static {
        // 顺序与 rex-tmdb.js genreDict 一致：剧集段在前，电影独有项在后（重复 id 以首次为准）
        put(10759, "动作冒险");
        put(16, "动画");
        put(35, "喜剧");
        put(80, "犯罪");
        put(99, "纪录");
        put(18, "剧情");
        put(10751, "家庭");
        put(10762, "儿童");
        put(9648, "悬疑");
        put(10763, "新闻");
        put(10764, "真人秀");
        put(10765, "科幻奇幻");
        put(10766, "肥皂剧");
        put(10767, "脱口秀");
        put(10768, "战争政治");
        put(37, "西部");
        put(28, "动作");
        put(12, "冒险");
        put(14, "奇幻");
        put(36, "历史");
        put(27, "恐怖");
        put(10402, "音乐");
        put(10749, "爱情");
        put(878, "科幻");
        put(10770, "电视电影");
        put(53, "惊悚");
        put(10752, "战争");

        // 中文名自身也进别名表，feed 直接给中文时原样通过
        for (String name : ID_NAMES.values()) alias(name, name);
        // TMDB 英文流派名 → 中文（enriched feed 的 genreTitle 来源）
        alias("action", "动作");
        alias("action & adventure", "动作冒险");
        alias("adventure", "冒险");
        alias("animation", "动画");
        alias("comedy", "喜剧");
        alias("crime", "犯罪");
        alias("documentary", "纪录");
        alias("drama", "剧情");
        alias("family", "家庭");
        alias("fantasy", "奇幻");
        alias("history", "历史");
        alias("horror", "恐怖");
        alias("kids", "儿童");
        alias("music", "音乐");
        alias("mystery", "悬疑");
        alias("news", "新闻");
        alias("reality", "真人秀");
        alias("romance", "爱情");
        alias("sci-fi", "科幻");
        alias("science fiction", "科幻");
        alias("sci-fi & fantasy", "科幻奇幻");
        alias("soap", "肥皂剧");
        alias("talk", "脱口秀");
        alias("thriller", "惊悚");
        alias("tv movie", "电视电影");
        alias("war", "战争");
        alias("war & politics", "战争政治");
        alias("western", "西部");
    }

    private RexGenreDict() {
    }

    private static void put(int id, String name) {
        ID_NAMES.put(id, name);
    }

    private static void alias(String from, String to) {
        ALIASES.put(from.toLowerCase(Locale.ROOT), to);
    }

    /** 单个 TMDB genre id 的中文名，未收录返回空串。 */
    public static String nameOf(int id) {
        String name = ID_NAMES.get(id);
        return name == null ? "" : name;
    }

    /**
     * rex 语义的 genre_ids 展示：先取前 max 个 id，再映射为中文名（未收录丢弃），
     * 用「、」连接。max 小于等于 0 返回空串。
     */
    public static String join(@Nullable JsonArray genreIds, int max) {
        if (genreIds == null || max <= 0) return "";
        List<String> names = new ArrayList<>();
        int taken = 0;
        for (int i = 0; i < genreIds.size() && taken < max; i++) {
            taken++;
            long id;
            try {
                id = genreIds.get(i).getAsLong();
            } catch (RuntimeException e) {
                continue;
            }
            String name = nameOf((int) id);
            if (!name.isEmpty()) names.add(name);
        }
        return String.join("、", names);
    }

    /** genre_ids 展示，最多 {@link #DEFAULT_MAX} 个。 */
    public static String join(@Nullable JsonArray genreIds) {
        return join(genreIds, DEFAULT_MAX);
    }

    /**
     * rexnow enriched feed 的 genreTitle → 中文名，最多 {@link #DEFAULT_MAX} 个。
     * 支持英文/中文逗号、顿号、斜杠分隔；未收录的名字原样保留。
     */
    public static String normalize(@Nullable String genreTitle) {
        if (genreTitle == null || genreTitle.trim().isEmpty()) return "";
        List<String> names = new ArrayList<>();
        for (String part : genreTitle.split("[,，、/|]+")) {
            String raw = part.trim();
            if (raw.isEmpty()) continue;
            String name = ALIASES.get(raw.toLowerCase(Locale.ROOT));
            if (name == null) name = raw;
            if (!name.isEmpty() && !names.contains(name)) names.add(name);
            if (names.size() >= DEFAULT_MAX) break;
        }
        return String.join("、", names);
    }

    /** 字符串数组（feed 的 genres 字段，中文或英文名）→ 中文名，最多 max 个。 */
    public static String joinStrings(@Nullable JsonArray values, int max) {
        if (values == null || max <= 0) return "";
        List<String> names = new ArrayList<>();
        for (int i = 0; i < values.size() && names.size() < max; i++) {
            try {
                String name = normalize(values.get(i).getAsString());
                if (!name.isEmpty() && !names.contains(name)) names.add(name);
            } catch (RuntimeException ignored) {
            }
        }
        return String.join("、", names);
    }
}
