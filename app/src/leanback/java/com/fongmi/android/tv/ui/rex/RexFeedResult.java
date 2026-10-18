package com.fongmi.android.tv.ui.rex;

import com.fongmi.android.tv.bean.RexSection;
import com.fongmi.android.tv.bean.Vod;
import com.fongmi.android.tv.impl.Diffable;

import java.util.List;

/** 一个栏目的加载产物：栏目定义 + 条目（列表保持栏目顺序）。 */
public final class RexFeedResult implements Diffable<RexFeedResult> {

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

    @Override
    public boolean isSameItem(RexFeedResult other) {
        return section != null && other != null && section.getKey().equals(other.getSection().getKey());
    }

    @Override
    public boolean isSameContent(RexFeedResult other) {
        if (other == null || items.isEmpty() || other.getItems().isEmpty()) return false;
        return items.get(0).getId().equals(other.getItems().get(0).getId());
    }
}
