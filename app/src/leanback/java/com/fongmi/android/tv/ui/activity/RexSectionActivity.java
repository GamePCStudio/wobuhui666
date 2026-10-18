package com.fongmi.android.tv.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.ListRow;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.fongmi.android.tv.BuildConfig;
import com.fongmi.android.tv.api.rex.DoubanBridgeClient;
import com.fongmi.android.tv.api.rex.RexNowClient;
import com.fongmi.android.tv.api.rex.RexTmdbClient;
import com.fongmi.android.tv.bean.DiscoverMediaKey;
import com.fongmi.android.tv.bean.RexSection;
import com.fongmi.android.tv.bean.Style;
import com.fongmi.android.tv.bean.Vod;
import com.fongmi.android.tv.databinding.ActivityDiscoverResultBinding;
import com.fongmi.android.tv.setting.BrowseExperienceSettings;
import com.fongmi.android.tv.setting.Setting;
import com.fongmi.android.tv.ui.base.BaseActivity;
import com.fongmi.android.tv.ui.custom.CustomRowPresenter;
import com.fongmi.android.tv.ui.custom.CustomScroller;
import com.fongmi.android.tv.ui.custom.CustomSelector;
import com.fongmi.android.tv.ui.presenter.VodPresenter;
import com.fongmi.android.tv.ui.rex.RexSections;
import com.fongmi.android.tv.utils.ResUtil;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** 栏目列表承接页：精选大卡点击进入；TMDB 源可翻页续拉，feed/云桥源直接展示已载入集合。 */
public class RexSectionActivity extends BaseActivity implements VodPresenter.OnClickListener, CustomScroller.Callback {

    private static final int COLUMN = 6;
    private final Object tag = new Object();
    private ActivityDiscoverResultBinding mBinding;
    private ArrayObjectAdapter mAdapter;
    private CustomScroller scroller;
    private RexSection section;
    private int page;
    private int totalPages = 1;

    public static void start(Activity activity, RexSection section) {
        Intent intent = new Intent(activity, RexSectionActivity.class);
        intent.putExtra("section_key", section.getKey());
        activity.startActivity(intent);
    }

    @Override
    protected ViewBinding getBinding() {
        return mBinding = ActivityDiscoverResultBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        section = RexSections.byKey(getIntent().getStringExtra("section_key"));
        if (section == null) {
            finish();
            return;
        }
        mBinding.title.setText(section.getLabelRes());
        setRecyclerView();
        load(1);
    }

    private void setRecyclerView() {
        CustomSelector selector = new CustomSelector();
        selector.addPresenter(ListRow.class, new CustomRowPresenter(16, FocusHighlight.ZOOM_FACTOR_NONE), VodPresenter.class);
        mBinding.recycler.setAdapter(new ItemBridgeAdapter(mAdapter = new ArrayObjectAdapter(selector)));
        mBinding.recycler.setItemAnimator(null);
        mBinding.recycler.setVerticalSpacing(ResUtil.dp2px(16));
        mBinding.recycler.addOnScrollListener(scroller = new CustomScroller(this));
    }

    private void load(int targetPage) {
        page = targetPage;
        if (targetPage == 1 && mAdapter.size() == 0) mBinding.progressLayout.showProgress();
        switch (section.getSource()) {
            case TMDB_LIST, TMDB_DISCOVER -> RexTmdbClient.fetchPage(section.getQuery(), targetPage, BuildConfig.TMDB_API_KEY, tag, new RexTmdbClient.PageListener() {
                @Override
                public void onSuccess(List<Vod> items, int target, int pages) {
                    if (isInactive()) return;
                    totalPages = pages;
                    complete(items);
                }

                @Override
                public void onError(Exception e) {
                    if (isInactive()) return;
                    complete(List.of());
                }
            });
            case REXNOW_ANIME -> RexNowClient.fetchAnime(tag, new RexNowClient.WeeklyListener() {
                @Override
                public void onSuccess(Map<String, List<Vod>> weekly) {
                    if (isInactive()) return;
                    List<Vod> all = new ArrayList<>();
                    for (List<Vod> group : weekly.values()) all.addAll(group);
                    complete(RexNowClient.mergeDistinct(List.of(), all, 0));
                }

                @Override
                public void onError(Exception e) {
                    if (isInactive()) return;
                    complete(List.of());
                }
            });
            case BRIDGE_CATALOG -> loadBridge();
            case REXNOW_REGIONAL -> RexNowClient.fetchRegional(section.getRegion(), section.getCategory(), tag, feedListener());
            case REXNOW_DOUBAN -> RexNowClient.fetchDouban(section.getFeedCategory(), tag, feedListener());
        }
    }

    private RexNowClient.ItemsListener feedListener() {
        return new RexNowClient.ItemsListener() {
            @Override
            public void onSuccess(List<Vod> items) {
                if (isInactive()) return;
                complete(items);
            }

            @Override
            public void onError(Exception e) {
                if (isInactive()) return;
                complete(List.of());
            }
        };
    }

    /** 云桥优先；无 sk / 401 / 失败回退 rexnow 豆瓣 feed（与首页加载器同策略）。 */
    private void loadBridge() {
        String sk = Setting.getBridgeSk();
        if (sk == null || sk.trim().isEmpty()) {
            RexNowClient.fetchDouban(feedCategory(), tag, feedListener());
            return;
        }
        DoubanBridgeClient.fetchCatalog(Setting.getBridgeBase(), sk, null, section.getCollectionId(), 0, tag, new DoubanBridgeClient.Listener() {
            @Override
            public void onSuccess(List<Vod> items) {
                if (isInactive()) return;
                complete(items);
            }

            @Override
            public void onError(Exception e) {
                if (isInactive()) return;
                RexNowClient.fetchDouban(feedCategory(), tag, feedListener());
            }

            @Override
            public void onUnauthorized() {
                if (isInactive()) return;
                RexNowClient.fetchDouban(feedCategory(), tag, feedListener());
            }
        });
    }

    private String feedCategory() {
        return section.getFeedCategory().isEmpty() ? "trending" : section.getFeedCategory();
    }

    private void complete(List<Vod> items) {
        scroller.endLoading(newResult(items));
        if (page == 1) mBinding.progressLayout.showContent(true, items.size());
        add(items);
    }

    private com.fongmi.android.tv.bean.Result newResult(List<Vod> items) {
        com.fongmi.android.tv.bean.Result result = new com.fongmi.android.tv.bean.Result();
        result.setList(items);
        return result;
    }

    private void add(List<Vod> items) {
        if (items.isEmpty()) return;
        int width = (ResUtil.getScreenWidth() - ResUtil.dp2px(112 + 16 * (COLUMN - 1))) / COLUMN;
        VodPresenter presenter = new VodPresenter(this, Style.rect(), new int[]{width, Math.round(width / 0.75f)});
        for (List<Vod> part : Lists.partition(items, COLUMN)) {
            ArrayObjectAdapter row = new ArrayObjectAdapter(presenter);
            row.addAll(0, part);
            mAdapter.add(new ListRow(row));
        }
        if (page == 1) requestFocus();
    }

    private void requestFocus() {
        mBinding.recycler.postDelayed(() -> {
            if (isInactive()) return;
            RecyclerView.ViewHolder holder = mBinding.recycler.findViewHolderForAdapterPosition(0);
            View target = holder == null ? null : findFocusable(holder.itemView);
            if (target != null && target.requestFocus()) return;
            mBinding.recycler.requestFocus();
        }, 80);
    }

    private View findFocusable(View view) {
        if (view == null || !view.isShown() || !view.isEnabled()) return null;
        if (view.isFocusable()) return view;
        if (!(view instanceof android.view.ViewGroup group)) return null;
        for (int i = 0; i < group.getChildCount(); i++) {
            View result = findFocusable(group.getChildAt(i));
            if (result != null) return result;
        }
        return null;
    }

    @Override
    public void onItemClick(Vod item) {
        openDetail(item, null);
    }

    @Override
    public void onItemClick(Vod item, View poster) {
        openDetail(item, poster);
    }

    private void openDetail(Vod item, View poster) {
        if (BrowseExperienceSettings.isDetailSourcesEnabled()) {
            DiscoverDetailActivity.start(this, item, poster);
            return;
        }
        DiscoverMediaKey key = DiscoverMediaKey.parse(item.getId());
        if (key == null) {
            CollectActivity.start(this, item.getName());
            return;
        }
        DiscoverDetailActivity.start(this, key, item.getName(), item.getPic(), item.getBackdrop(), item.getContent(), item.getYear(), item.getRemarks(), poster);
    }

    @Override
    public boolean onLongClick(Vod item) {
        return false;
    }

    @Override
    public boolean onLoadMore(String ignored) {
        if (isInactive() || page >= totalPages) return false;
        load(page + 1);
        return true;
    }

    @Override
    protected void onDestroy() {
        RexTmdbClient.cancel(tag);
        RexNowClient.cancel(tag);
        DoubanBridgeClient.cancel(tag);
        if (scroller != null) mBinding.recycler.removeOnScrollListener(scroller);
        super.onDestroy();
    }

    private boolean isInactive() {
        return isFinishing() || isDestroyed();
    }
}
