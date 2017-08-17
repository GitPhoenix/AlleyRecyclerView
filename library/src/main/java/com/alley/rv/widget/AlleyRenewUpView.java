package com.alley.rv.widget;


import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * 上拉加载视图基类
 *
 * @author Phoenix
 * @date 2017/7/24 11:08
 */
public abstract class AlleyRenewUpView extends FrameLayout {
    /**
     * 正在进行上拉加载中
     */
    public static final int RENEW_UP_NOW = 1;
    /**
     * 上拉加载结束
     */
    public static final int RENEW_UP_END = 2;
    /**
     * 没有数据了，不在进行加载
     */
    public static final int RENEW_UP_NEVER = 3;

    public AlleyRenewUpView(@NonNull Context context) {
        super(context);
    }

    public AlleyRenewUpView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AlleyRenewUpView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置上拉加载状态
     *
     * @param currState
     */
    public abstract void setRenewState(int currState);

    /**
     * 返回当前状态值
     *
     * @return
     */
    public abstract int getRenewState();
}
