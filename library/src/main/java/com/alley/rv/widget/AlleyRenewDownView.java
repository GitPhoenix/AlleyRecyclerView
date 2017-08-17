package com.alley.rv.widget;


import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * 下拉刷新视图基类
 *
 * @author Phoenix
 * @date 2017/7/24 10:57
 */
public abstract class AlleyRenewDownView extends FrameLayout {
    /**
     * 正在显示【下拉刷新】
     */
    public static final int RENEW_DOWN_GOING = 6;
    /**
     * 释放手势，即将进行刷新
     */
    public static final int RENEW_DOWN_RELEASE = 7;
    /**
     * 正在刷新
     */
    public static final int RENEW_DOWN_NOW = 8;
    /**
     * 下拉刷新结束
     */
    public static final int RENEW_DOWN_END = 9;

    public AlleyRenewDownView(@NonNull Context context) {
        super(context);
    }

    public AlleyRenewDownView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AlleyRenewDownView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 下滑监听
     *
     * @param faction
     */
    public abstract void onFling(float faction);

    /**
     * 手势离开
     *
     * @return
     */
    public abstract boolean onUp();

    /**
     * 设置下拉刷新状态
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
