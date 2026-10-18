package com.fongmi.android.tv.ui.custom

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import com.fongmi.android.tv.ui.rex.RexTheme
import com.google.android.material.textview.MaterialTextView

/**
 * Rex 精选大卡上的叠加文案：恒白字 + 阴影（不随主题切换），
 * 由 JetStreamVodScrimView 的底部渐变保证可读性。
 * 颜色统一取自 RexTheme（浅色模式立项时只改那里）。
 */
class JetStreamRexCardTitleTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialTextView(context, attrs, defStyleAttr) {

    init {
        applyJetStreamTypeface()
        setTextColor(RexTheme.spotlightTitleColor())
        setTypeface(typeface, Typeface.BOLD)
        setShadowLayer(6f, 0f, 1.5f, RexTheme.spotlightShadowColor())
    }
}

/** 精选大卡第一行（栏目名）：比片名小一号，略作压暗。 */
class JetStreamRexCardLabelTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialTextView(context, attrs, defStyleAttr) {

    init {
        applyJetStreamTypeface()
        setTextColor(RexTheme.spotlightLabelColor())
        setTypeface(typeface, Typeface.BOLD)
        setShadowLayer(6f, 0f, 1.5f, RexTheme.spotlightShadowColor())
    }
}

/** 精选大卡左侧装饰引号字形。 */
class JetStreamRexQuoteTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialTextView(context, attrs, defStyleAttr) {

    init {
        applyJetStreamTypeface()
        setTextColor(RexTheme.spotlightQuoteColor())
        setTypeface(typeface, Typeface.BOLD)
        setShadowLayer(6f, 0f, 1.5f, RexTheme.spotlightShadowColor())
    }
}
