package com.fongmi.android.tv.ui.rex;

import androidx.annotation.ColorInt;

/**
 * Rex 行的主题单一挂钩。
 *
 * <p>本轮恒深色呈现（与现有 UI 融合）：全局浅色模式立项后，只需要改这里
 * （{@link #isLight()} 接到全局开关、需要的 token 在这里分流）。
 * 叠加在剧照上的大字恒用白字 + 阴影，两套主题一致（由底部 scrim 保证可读性）。
 */
public final class RexTheme {

    private RexTheme() {
    }

    public static boolean isLight() {
        return false;
    }

    /** 精选大卡片名：恒定白。 */
    @ColorInt
    public static int spotlightTitleColor() {
        return 0xFFFFFFFF;
    }

    /** 精选大卡栏目名（小一号）：恒定白（95%）。 */
    @ColorInt
    public static int spotlightLabelColor() {
        return 0xF2FFFFFF;
    }

    /** 精选大卡装饰引号：恒定白（50%）。 */
    @ColorInt
    public static int spotlightQuoteColor() {
        return 0x80FFFFFF;
    }

    /** 叠加文案阴影色（黑 70%）。 */
    @ColorInt
    public static int spotlightShadowColor() {
        return 0xB3000000;
    }
}
