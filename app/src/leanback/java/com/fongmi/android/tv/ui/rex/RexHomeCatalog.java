package com.fongmi.android.tv.ui.rex;

import com.fongmi.android.tv.bean.Vod;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Rex 两行的呈现策略（纯逻辑，可单测）：精选大卡选取与混合推荐流拼接。 */
public final class RexHomeCatalog {

    public static final int PER_SECTION_CAP = 20;
    public static final int MIXED_TOTAL_CAP = 100;

    private RexHomeCatalog() {
    }

    /** 精选大卡：每个栏目一张，优先有横图（backdrop 与 poster 不同）的条目。 */
    public static List<RexFeedResult> spotlights(List<RexFeedResult> results) {
        List<RexFeedResult> spotlights = new ArrayList<>();
        if (results == null) return spotlights;
        for (RexFeedResult result : results) {
            Vod chosen = pick(result.getItems());
            if (chosen != null) spotlights.add(new RexFeedResult(result.getSection(), List.of(chosen)));
        }
        return spotlights;
    }

    static Vod pick(List<Vod> items) {
        if (items == null || items.isEmpty()) return null;
        for (Vod item : items) if (hasLandscape(item)) return item;
        return items.get(0);
    }

    static boolean hasLandscape(Vod item) {
        return item != null && !item.getBackdrop().isEmpty() && !item.getBackdrop().equals(item.getPic());
    }

    /** 混合推荐流：按栏目顺序拼接、每栏目取前 N、按 id 去重、总量上限 {@link #MIXED_TOTAL_CAP}。 */
    public static List<Vod> mixed(List<RexFeedResult> results) {
        List<Vod> mixed = new ArrayList<>();
        if (results == null) return mixed;
        Set<String> seen = new LinkedHashSet<>();
        for (RexFeedResult result : results) {
            int cap = Math.max(1, Math.min(result.getSection().getLimit(), PER_SECTION_CAP));
            int taken = 0;
            for (Vod item : result.getItems()) {
                if (taken >= cap || mixed.size() >= MIXED_TOTAL_CAP) break;
                if (item == null || item.getId().isEmpty()) continue;
                if (seen.add(item.getId())) {
                    mixed.add(item);
                    taken++;
                }
            }
            if (mixed.size() >= MIXED_TOTAL_CAP) break;
        }
        return mixed;
    }
}
