package com.fongmi.android.tv.ui.presenter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.leanback.widget.Presenter;

import com.fongmi.android.tv.bean.Vod;
import com.fongmi.android.tv.databinding.AdapterRexPosterBinding;
import com.fongmi.android.tv.ui.custom.JetStreamAnimator;
import com.fongmi.android.tv.ui.rex.RexCaptions;
import com.fongmi.android.tv.ui.theme.JetStreamAmbient;
import com.fongmi.android.tv.utils.ImgUtil;

/** 混合推荐流小卡：海报 + 「年份 · 类型」常显副题，点击进入详情。 */
public class RexPosterPresenter extends Presenter {

    private final VodPresenter.OnClickListener listener;

    public RexPosterPresenter(VodPresenter.OnClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return new Holder(AdapterRexPosterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, Object item) {
        Holder holder = (Holder) viewHolder;
        Vod vod = (Vod) item;
        holder.binding.name.setText(vod.getName());
        String meta = RexCaptions.subtitle(vod);
        holder.binding.meta.setText(meta);
        holder.binding.meta.setVisibility(TextUtils.isEmpty(meta) ? View.GONE : View.VISIBLE);
        ImgUtil.load(vod.getName(), vod.getPic(), holder.binding.image);
        holder.view.setOnClickListener(view -> listener.onItemClick(vod, holder.binding.image));
        holder.view.setOnLongClickListener(view -> listener.onLongClick(vod));
        holder.view.setOnFocusChangeListener((view, focused) -> {
            JetStreamAnimator.animateFocus(view, focused, JetStreamAnimator.FOCUS_SCALE_CARD, 0);
            if (focused) JetStreamAmbient.push(vod.getPic());
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

    private static class Holder extends ViewHolder {

        private final AdapterRexPosterBinding binding;

        Holder(AdapterRexPosterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
