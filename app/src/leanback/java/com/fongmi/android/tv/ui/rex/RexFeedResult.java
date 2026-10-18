package com.fongmi.android.tv.ui.rex;

import com.fongmi.android.tv.bean.RexSection;
import com.fongmi.android.tv.bean.Vod;

import java.util.List;

/** 一个栏目的加载产物：栏目定义 + 条目（列表保持栏目顺序）。 */
public final class RexFeedResult {

    private final RexSection section;
    private final List<Vod> items;

    public RexFeedResult(RexSection section, List<Vod> items) {
        this.section = section;
        this.items = items;
    }

    public RexSection getSection() {
        return section;
    }

    public List<Vod> getItems() {
        return items;
    }
}
