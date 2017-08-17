package com.alley.rv.helper;


import android.view.View;

/**
 * item动画必须实现的接口
 *
 * @author Phoenix
 * @date 2017/4/13 16:02
 */
public interface AlleyAnimationListener {

    /**
     * 设置item动画
     *
     * @param targetView
     */
    void setAnimator(View targetView);
}
