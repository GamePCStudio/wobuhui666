package com.fongmi.android.tv.ui.presenter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.leanback.widget.Presenter;

import com.fongmi.android.tv.bean.RexSection;
import com.fongmi.android.tv.bean.Vod;
import com.fongmi.android.tv.databinding.AdapterRexSpotlightBinding;
import com.fongmi.android.tv.ui.custom.JetStreamAnimator;
import com.fongmi.android.tv.ui.rex.RexFeedResult;
import com.fongmi.android.tv.ui.theme.JetStreamAmbient;
import com.fongmi.android.tv.utils.ImgUtil;

/** 精选大卡：每个栏目一张（栏目名 + 片名叠加），点击进入该栏目的列表页。 */
public class RexSpotlightPresenter extends Presenter {

    public interface Listener {

        void onSectionClick(RexSection section, View card);

        boolean onSectionLongClick(RexSection section);
    }

    private final Listener listener;

    public RexSpotlightPresenter(Listener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return new Holder(AdapterRexSpotlightBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, Object item) {
        Holder holder = (Holder) viewHolder;
        RexFeedResult result = (RexFeedResult) item;
        Vod vod = result.getItems().isEmpty() ? null : result.getItems().get(0);
        if (vod == null) return;
        holder.binding.section.setText(result.getSection().getLabelRes());
        holder.binding.name.setText(vod.getName());
        ImgUtil.load(vod.getName(), landscapeOf(vod), holder.binding.image);
        holder.view.setOnClickListener(view -> listener.onSectionClick(result.getSection(), holder.binding.image));
        holder.view.setOnLongClickListener(view -> listener.onSectionLongClick(result.getSection()));
        holder.view.setOnFocusChangeListener((view, focused) -> {
            JetStreamAnimator.animateFocus(view, focused, JetStreamAnimator.FOCUS_SCALE_CARD, 0);
            if (focused) JetStreamAmbient.push(landscapeOf(vod));
        });
    }

    @Override
    public void onUnbindViewHolder(@NonNull ViewHolder viewHolder) {
        Holder holder = (Holder) viewHolder;
        holder.view.setOnClickListener(null);
        holder.view.setOnLongClickListener(null);
        holder.view.setOnFocusChangeListener(null);
        JetStreamAnimator.reset(holder.view);
        ImgUtil.clear(holder.binding.image);
    }

    /** 优先横图（backdrop），缺失回退海报。 */
    static String landscapeOf(Vod vod) {
        String backdrop = vod.getBackdrop();
        return TextUtils.isEmpty(backdrop) ? vod.getPic() : backdrop;
    }

    private static class Holder extends ViewHolder {

        private final AdapterRexSpotlightBinding binding;

        Holder(AdapterRexSpotlightBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
