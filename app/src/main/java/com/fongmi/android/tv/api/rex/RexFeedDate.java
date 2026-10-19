package com.fongmi.android.tv.api.rex;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * rexnow feed 的日期工具。
 *
 * <p>feed 目录与文件名里的日期按站点生成时间（东八区）计算，
 * 找不到当天文件时逐日回退，最多回溯 {@link #MAX_BACKTRACK_DAYS} 天。
 */
public final class RexFeedDate {

    public static final int MAX_BACKTRACK_DAYS = 7;
    private static final TimeZone ZONE = TimeZone.getTimeZone("Asia/Shanghai");
    private static final long DAY = 24L * 60L * 60L * 1000L;

    private RexFeedDate() {
    }

    /** yyyyMMdd（东八区），用于 regional-tv 目录与 enriched 文件名。 */
    public static String compact(long epochMillis) {
        return format(epochMillis, "yyyyMMdd");
    }

    /** yyyy-MM-dd（东八区），部分 feed 使用短横线日期。 */
    public static String dashed(long epochMillis) {
        return format(epochMillis, "yyyy-MM-dd");
    }

    /** [今天, 昨天, ...]，共 days 个 yyyyMMdd（东八区），最多回溯 7 天。 */
    public static List<String> recent(int days) {
        return recentFrom(System.currentTimeMillis(), days);
    }

    public static List<String> recentFrom(long epochMillis, int days) {
        List<String> values = new ArrayList<>();
        int count = Math.max(0, Math.min(days, MAX_BACKTRACK_DAYS));
        for (int i = 0; i < count; i++) values.add(compact(epochMillis - i * DAY));
        return values;
    }

    private static String format(long epochMillis, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.US);
        format.setTimeZone(ZONE);
        return format.format(new Date(epochMillis));
    }
}
